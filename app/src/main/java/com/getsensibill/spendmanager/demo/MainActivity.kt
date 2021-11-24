package com.getsensibill.spendmanager.demo

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.getsensibill.core.*
import com.getsensibill.oauthclient.OAuthSettings
import com.getsensibill.oauthclient.OauthSession
import com.getsensibill.rest.network.DefaultEnvironment
import com.getsensibill.sensibillauth.SensibillAuth
import com.getsensibill.sensibillauth.SensibillAuthBuilder
import com.getsensibill.spendmanager.demo.databinding.ActivityMainBinding
import com.getsensibill.tokenprovider.TokenProvider
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    // TODO: Set your token and or username/password before running. Values provided by Sensibill
    val privateToken = PROVIDED_BY_SENSIBILL
    val username = PROVIDED_BY_SENSIBILL
    val password = PROVIDED_BY_SENSIBILL

    // TODO: Make sure to update the key, secret, credentials and environment based on what is provided from Sensibill
    val apiKey = PROVIDED_BY_SENSIBILL
    val apiSecret = PROVIDED_BY_SENSIBILL
    val credentialType = PROVIDED_BY_SENSIBILL
    val environment = DefaultEnvironment.BETA_STAGING

    // Does not need to be updated or set for demo use
    val redirectURL = "https://testclient.com/redirect"

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            progress.hide()

            loginWithToken.setOnClickListener {
                if(!checkCredentialsSet(privateToken)) {
                    showToast("Please set your token before running the demo. Check MainActivity.kt")
                } else {
                    login(withToken = true)
                }
            }
            loginWithUsername.setOnClickListener {
                if (!checkCredentialsSet(apiKey, apiSecret, credentialType, username, password)) {
                    showToast("Please set your username and password before running the demo. Check MainActivity.kt")
                } else {
                    login(withToken = false)
                }
            }

            /**
             * To stop the SDK, you can call the [SensibillSDK] release() function. This will handle
             * tear down for you, shuts down and releases all resources being used by the SDK
             */
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
        loading(true)
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
     *   [privateToken]) to the SDK when it requests it
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
        val (initializer, onInitialized) = if (withToken) {
            // Creates the builder for the SDK Initializer
            val builder = InitializationBuilder(this, environment)

            // You can use our convenience function to create a [TokenProvider] implementation using
            // a lambda.  If using an integration server for providing tokens, this token provider
            // is called when the SDK requires a (new) auth token.
            val tokenProvider = TokenProvider.fromLambda { _, _, _ -> privateToken }

            // Provide a Token Provider to SDK initializer.  This token provider will be called when the
            // SDK starts, as well as if the token expires while the SDK is in use.
            // The token provider _must_ be provided in the `Initializer`, however it will not be used
            // until `SensibillSDK.start` is called.
            builder.authTokenProvider(tokenProvider)

            builder.build() to { startSDK("userIdentifierFromToken") }

        } else {
            // creates an auth settings based on the key, secret, credential type and a possible redirect
            val oAuthSettings = OAuthSettings(apiKey, apiSecret, credentialType, redirectURL, false)

            // builds the SensibillAuth object using the auth settings and environment
            val sensibillAuth = SensibillAuthBuilder(this, environment, oAuthSettings).build()

            // Creates the builder for the SDK Initializer
            val builder = InitializationBuilder(this, environment)

            // If using username / password auth, use the `TokenProvider` provided by `SensibillAuth`
            val tokenProvider = sensibillAuth.getTokenProvider()

            // Provide a Token Provider to SDK initializer.  This token provider will be called when the
            // SDK starts, as well as if the token expires while the SDK is in use.
            // The token provider _must_ be provided in the `Initializer`, however it will not be used
            // until `SensibillSDK.start` is called.
            builder.authTokenProvider(tokenProvider)

            builder.build() to { authenticate(sensibillAuth, username, password) }
        }

        // Initialize the SDK
        SensibillSDK.initialize(initializer, object : SDKInitializeListener {
            override fun onInitialized() { onInitialized() }

            override fun onInitializationFailed() {
                showToast("Failed to initialize the SDK. Please make sure you have set the correct variables at the top of the MainActivity, and have an active internet connection.")
                loading(false)
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
                loading(false, error.shortDescription)
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
            loading(false)
            return
        }

        SensibillSDK.start(userIdentifier, object : SDKStartup {
            override fun onSDKStarted() {
                startLauncher()
            }

            override fun onSDKFailed(error: LoginError?, p1: String?) {
                showToast("SDK failed to start: $p1")
                loading(false)
            }
        })
    }

    /**
     * Will navigate to the launcher activity, where the user can test out different
     * integration methods.
     */
    private fun startLauncher() {
        loading(false)
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

    private fun loading(isLoading: Boolean, error: String? = null) = runOnUiThread {
        binding.apply {
            if (isLoading) progress.show() else progress.hide()
            loginWithToken.isEnabled = !isLoading
            loginWithUsername.isEnabled = !isLoading
            logout.isEnabled = !isLoading

            error?.let { Toast.makeText(this@MainActivity, it, Toast.LENGTH_LONG).show() }
        }
    }

    private fun checkCredentialsSet(vararg requiredCredentials: String): Boolean =
        requiredCredentials.all { it.isNotBlank() && it != PROVIDED_BY_SENSIBILL }

    companion object {
        private const val PROVIDED_BY_SENSIBILL = "TO BE PROVIDED BY SENSIBILL"
    }
}
