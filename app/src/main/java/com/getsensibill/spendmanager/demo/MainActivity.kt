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
import com.getsensibill.web.ui.WebUiActivity
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

        if(apiKey.isBlank() || apiKey == PROVIDED_BY_SENSIBILL) {
            throw Exception("Please set your API key, secret and credential type. This is all provided by Sensibill.")
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            progress.hide()

            loginWithToken.setOnClickListener {
                if(privateToken.isBlank() || privateToken == PROVIDED_BY_SENSIBILL) {
                    showToast("Please set your token before running the demo. Check MainActivity.kt")
                } else {
                    login(withToken = true)
                }
            }
            loginWithUsername.setOnClickListener {
                if(username.isBlank() || username == PROVIDED_BY_SENSIBILL) {
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
     * * Launches Spend Manager
     *
     * If the SDK is already running, we can directly launch the Spend Manager
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
     * Initialize the SDK by creating a Sensibill Auth object, and passing it into the SDK builder.
     * Once the SDK is initialized, setup authentication, and start the SDK.
     */
    private fun initializeSDK(withToken: Boolean) {
        Timber.i("For the demo, we are just releasing the SDK on every run through.")
        SensibillSDK.release()

        // creates an auth settings based on the key, secret, credential type and a possible redirect
        val authSettings = OAuthSettings(apiKey, apiSecret, credentialType, redirectURL, false)

        // builds the SensibillAuth object using the auth settings and environment
        val sensibillAuth = SensibillAuthBuilder(this, environment, authSettings)
            .build()

        // Creates the builder for the SDK
        val builder = InitializationBuilder(this, environment)

        // Add Token Provider to SDK
        builder.authTokenProvider(sensibillAuth.getTokenProvider())

        // Initializes the SDK with the builder
        SensibillSDK.initialize(builder.build(), object : SDKInitializeListener {
            override fun onInitialized() {
                if (withToken) {
                    authenticate(privateToken)
                } else {
                    authenticate(sensibillAuth, username, password)
                }
            }

            override fun onInitializationFailed() {
                showToast("Failed to initialize the SDK. Please make sure you have set the correct variables at the top of the MainActivity, and have an active internet connection.")
                loading(false)
            }
        })
    }

    /**
     * You can use our convenience function to create a [TokenProvider] implementation using a
     * lambda, and set the token. This should only be set after the SDK was been initialized,
     * and the [TokenProvider] has been setup, as we are doing in the startSDK() function.
     */
    private fun authenticate(token: String) {
        TokenProvider.fromLambda { _, _, _ -> token }
        startSDK("userIdentifierFromToken")
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
                loading(false)
            }
        }
        auth.signIn(username, password, sessionListener)
    }

    /**
     * Starts the SDK.
     *
     * Once started we can start the Spend Manager. Important to remember that the SDK needs
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
     * Will navigate to the launcher activity, where the user can test test out different
     * integration methods.
     */
    private fun startLauncher() {
        loading(false)
        if (SensibillSDK.getState() != CoreState.STARTED) {
            showToast("Make sure to start the SDK before launching Spend Manager")
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

    private fun loading(isLoading: Boolean) = binding.apply {
        if(isLoading) progress.show() else progress.hide()
        loginWithToken.isEnabled = !isLoading
        loginWithUsername.isEnabled = !isLoading
        logout.isEnabled = !isLoading
    }

    companion object {
        private const val PROVIDED_BY_SENSIBILL = "TO BE PROVIDED BY SENSIBILL"
    }
}