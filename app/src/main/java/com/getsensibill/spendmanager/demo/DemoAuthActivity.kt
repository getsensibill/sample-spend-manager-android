package com.getsensibill.spendmanager.demo

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.getsensibill.core.*
import com.getsensibill.rest.client.v1.oauth.OAuthSettings
import com.getsensibill.rest.client.v1.oauth.OauthSession
import com.getsensibill.sensibillauth.SensibillAuth
import com.getsensibill.sensibillauth.SensibillAuthBuilder
import com.getsensibill.spendmanager.demo.AuthConfig.PLACEHOLDER
import com.getsensibill.spendmanager.demo.AuthConfig.apiKey
import com.getsensibill.spendmanager.demo.AuthConfig.apiSecret
import com.getsensibill.spendmanager.demo.AuthConfig.credentialType
import com.getsensibill.spendmanager.demo.AuthConfig.environment
import com.getsensibill.spendmanager.demo.AuthConfig.password
import com.getsensibill.spendmanager.demo.AuthConfig.userAccessToken
import com.getsensibill.spendmanager.demo.AuthConfig.redirectURL
import com.getsensibill.spendmanager.demo.AuthConfig.username
import com.getsensibill.spendmanager.demo.databinding.ActivityDemoAuthBinding
import com.getsensibill.tokenprovider.TokenProvider
import timber.log.Timber

class DemoAuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDemoAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDemoAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            progress.hide()

            loginWithToken.setOnClickListener {
                if(!checkCredentialsSet(userAccessToken)) {
                    showToast("Please set your token before running the demo. Check DemoAuthActivity and AuthConfig")
                } else {
                    login(withToken = true)
                }
            }
            loginWithUsername.setOnClickListener {
                if (!checkCredentialsSet(apiKey, apiSecret, credentialType, username, password)) {
                    showToast("Please set your username and password before running the demo. Check DemoAuthActivity and AuthConfig")
                } else {
                    login(withToken = false)
                }
            }

            // To stop the SDK, you can call the [SensibillSDK] release() function. This will handle
            // tear down for you, shuts down and releases all resources being used by the SDK
            logout.setOnClickListener {
                SensibillSDK.release()
                showToast("SDK has been released.")
            }
        }
    }

    /**
     * If the SDK is not yet started, we will follow these steps:
     * * Initialize the SDK
     * * Applies token to the [TokenProvider]
     * * Start the SDK
     * * Navigates to the launcher activity
     *
     * If the SDK is already running, we can go directly to the launcher activity
     */
    private fun login(withToken: Boolean) {
        onLoadingStateChanged(true)
        if (SensibillSDK.getState() != CoreState.STARTED) {
            initializeSDK(withToken = withToken)
        } else {
            startLauncher()
        }
    }

    /**
     * Initialize the Sensibill SDK, with correct auth configuration based on [withToken].
     *
     * **If using a token directly:**
     * - Create an `Initializer` with the required configuration for the SDK
     * - Create and provide a `TokenProvider` which will provide a token (for this demo,
     *   [userAccessToken]) to the SDK when it requests it
     * - Initialize the SDK
     * - Upon initialization, start the SDK
     *
     * **If using username/password auth:**
     * - Create a `SensibillAuth`, which will be used for username/password authentication
     * - Create an `Initializer` with the required configuration for the SDK
     * - Provide the `Initializer` with the `TokenProvider` from `SensibillAuth`
     * - Initialize the SDK
     * - Upon initialization, authenticate with the Sensibill servers using the provided [username]
     *   and [password]
     * - Upon successful authentication, start the SDK
     */
    private fun initializeSDK(withToken: Boolean) {
        Timber.i("For the demo, we are just releasing the SDK on every run through.")
        SensibillSDK.release()

        // Pair<Initializer, () -> Unit>
        val (initializer, afterInitialized) = if (withToken) {

            // You can use our convenience function to create a [TokenProvider] implementation using
            // a lambda.  If using an integration server for providing tokens, this token provider
            // is called when the SDK requires a (new) auth token.
            // Provide a Token Provider to SDK initializer.  This token provider will be called when the
            // SDK starts, as well as if the token expires while the SDK is in use.
            // The token provider _must_ be provided in the `Initializer`, however it will not be used
            // until `SensibillSDK.start` is called.
            val tokenProvider = TokenProvider.fromLambda { _, _, listener ->
                listener.onTokenProvided(userAccessToken)
            }

            // Creates the builder for the SDK Initializer
            val builder = InitializationBuilder(this, environment, tokenProvider)

            builder.build() to { startSDK("userIdentifierFromToken") }

        } else {
            // creates an auth settings based on the key, secret, credential type and a possible redirect
            val oAuthSettings = OAuthSettings(apiKey, apiSecret, credentialType, redirectURL, false)

            // builds the SensibillAuth object using the auth settings and environment
            val sensibillAuth = SensibillAuthBuilder(this, environment, oAuthSettings).build()

            // If using username / password auth, use the `TokenProvider` provided by `SensibillAuth`
            // Provide a Token Provider to SDK initializer.  This token provider will be called when the
            // SDK starts, as well as if the token expires while the SDK is in use.
            // The token provider _must_ be provided in the `Initializer`, however it will not be used
            // until `SensibillSDK.start` is called.
            val tokenProvider = sensibillAuth.getTokenProvider()

            // Creates the builder for the SDK Initializer
            val builder = InitializationBuilder(this, environment, tokenProvider)

            builder.build() to { authenticate(sensibillAuth, username, password) }
        }

        // Initialize the SDK
        SensibillSDK.initialize(initializer, object : SDKInitializeListener {
            override fun onInitialized() { afterInitialized() }

            override fun onInitializationFailed() {
                showToast("Failed to initialize the SDK. Please make sure you have set the correct variables in AuthConfig, and have an active internet connection.")
                onLoadingStateChanged(false)
            }
        })
    }

    /**
     * Handles authentication using a username/password
     *
     * On success, it will call startSDK
     * On failure, will output to logs
     */
    private fun authenticate(auth: SensibillAuth, username: String, password: String) {
        val sessionListener = object : SensibillAuth.SessionListener {
            override fun onSuccess(session: OauthSession) {
                startSDK("userIdentifierFromSignIn")
            }
            override fun onFailure(error: SensibillAuth.AuthError) {
                Timber.e("Failed to authenticate: %s", error.shortDescription)
                onLoadingStateChanged(false, error.shortDescription)
            }
        }
        auth.signIn(username, password, sessionListener)
    }

    /**
     * Starts the SDK. Important to remember that the SDK needs
     * to have been initialized first.
     */
    private fun startSDK(userIdentifier: String) {
        if (SensibillSDK.getState() != CoreState.INITIALIZED) {
            showToast("SDK needs to be in a INITIALIZED state before trying to start it.")
            onLoadingStateChanged(false)
            return
        }

        SensibillSDK.start(userIdentifier, object : SDKStartup {
            override fun onSDKStarted() {
                startLauncher()
            }

            override fun onSDKFailed(error: LoginError?, p1: String?) {
                showToast("SDK failed to start: $p1")
                onLoadingStateChanged(false)
            }
        })
    }

    /**
     * Will navigate to the launcher activity, where the user can test out different
     * integration methods.
     */
    private fun startLauncher() {
        onLoadingStateChanged(false)
        if (SensibillSDK.getState() != CoreState.STARTED) {
            showToast("Make sure to start the SDK before going to the launcher.")
            return
        }

        // Creates the intent and launches it.
        val intent = Intent(this, LauncherActivity::class.java)
        startActivity(intent)
    }

    /**
     * Helper function to show toasts.
     */
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun onLoadingStateChanged(isLoading: Boolean, error: String? = null) = runOnUiThread {
        binding.apply {
            if (isLoading) progress.show() else progress.hide()
            loginWithToken.isEnabled = !isLoading
            loginWithUsername.isEnabled = !isLoading
            logout.isEnabled = !isLoading

            error?.let { showToast(error) }
        }
    }

    private fun checkCredentialsSet(vararg requiredCredentials: String): Boolean =
        requiredCredentials.all { it.isNotBlank() && it != PLACEHOLDER }
}
