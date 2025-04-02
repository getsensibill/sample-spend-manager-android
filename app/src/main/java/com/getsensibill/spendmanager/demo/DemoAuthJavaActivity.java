package com.getsensibill.spendmanager.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.getsensibill.core.CoreState;
import com.getsensibill.core.InitializationBuilder;
import com.getsensibill.core.Initializer;
import com.getsensibill.core.LoginError;
import com.getsensibill.core.SDKInitializeListener;
import com.getsensibill.core.SDKStartup;
import com.getsensibill.core.SensibillSDK;
import com.getsensibill.rest.client.v1.oauth.OAuthSettings;
import com.getsensibill.rest.client.v1.oauth.OauthSession;
import com.getsensibill.sensibillauth.SensibillAuth;
import com.getsensibill.sensibillauth.SensibillAuthBuilder;
import com.getsensibill.spendmanager.demo.databinding.ActivityDemoAuthBinding;
import com.getsensibill.tokenprovider.TokenProvider;

import timber.log.Timber;

public class DemoAuthJavaActivity extends AppCompatActivity {

    private ActivityDemoAuthBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDemoAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupViews();
    }

    private void setupViews() {
        binding.progress.hide();

        binding.loginWithToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkCredentialsSet(AuthConfig.INSTANCE.getUserAccessToken())) {
                    showToast("Please set your token before running the demo. Check DemoAuthJavaActivity and AuthConfig");
                } else {
                    login(true);
                }
            }
        });

        binding.loginWithUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean credentialsSet = checkCredentialsSet(
                        AuthConfig.INSTANCE.getApiKey(),
                        AuthConfig.INSTANCE.getApiSecret(),
                        AuthConfig.INSTANCE.getCredentialType(),
                        AuthConfig.INSTANCE.getUsername(),
                        AuthConfig.INSTANCE.getPassword()
                );
                if (!credentialsSet) {
                    showToast("Please set your username and password before running the demo. Check DemoAuthJavaActivity and AuthConfig");
                } else {
                    login(false);
                }
            }
        });

        // To stop the SDK, you can call the [SensibillSDK] release() function. This will handle
        // tear down for you, shuts down and releases all resources being used by the SDK
        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SensibillSDK.getInstance().release();
                showToast("SDK has been released.");
            }
        });
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
    private void login(boolean withToken) {
        onLoadingStateChanged(true);
        if (SensibillSDK.getInstance().getState() != CoreState.STARTED) {
            if (withToken) {
                initializeSDKWithToken();
            } else {
                initializeSDKForUsernamePassword();
            }
        } else {
            startLauncher();
        }
    }

    /**
     * Initialize the Sensibill SDK, using username / password authentication.
     *
     * - Create a `SensibillAuth`, which will be used for username/password authentication
     * - Create an `Initializer` with the required configuration for the SDK
     * - Provide the `Initializer` with the `TokenProvider` from `SensibillAuth`
     * - Initialize the SDK
     * - Upon initialization, authenticate with the Sensibill servers using the provided [username]
     *   and [password]
     * - Upon successful authentication, start the SDK
     */
    private void initializeSDKForUsernamePassword() {
        Timber.i("For the demo, we are just releasing the SDK on every run through.");
        SensibillSDK.getInstance().release();

        // creates an auth settings based on the key, secret, credential type and a possible redirect
        OAuthSettings oAuthSettings = new OAuthSettings(
                AuthConfig.INSTANCE.getApiKey(),
                AuthConfig.INSTANCE.getApiSecret(),
                AuthConfig.INSTANCE.getCredentialType(),
                AuthConfig.INSTANCE.getRedirectURL(),
                false
        );

        // builds the SensibillAuth object using the auth settings and environment
        SensibillAuth sensibillAuth = new SensibillAuthBuilder(this, AuthConfig.INSTANCE.getEnvironment(), oAuthSettings)
                .build();

        // If using username / password auth, use the `TokenProvider` provided by `SensibillAuth`
        // Provide a Token Provider to SDK initializer.  This token provider will be called when the
        // SDK starts, as well as if the token expires while the SDK is in use.
        // The token provider _must_ be provided in the `Initializer`, however it will not be used
        // until `SensibillSDK.start` is called.
        TokenProvider tokenProvider = sensibillAuth.getTokenProvider();

        // Creates the builder for the SDK Initializer
        InitializationBuilder builder = new InitializationBuilder(this, AuthConfig.INSTANCE.getEnvironment(), tokenProvider);

        // Initialize the SDK
        SensibillSDK.getInstance().initialize(builder.build(), new SDKInitializeListener() {
            @Override
            public void onInitialized() {
                authenticate(sensibillAuth, AuthConfig.INSTANCE.getUsername(), AuthConfig.INSTANCE.getPassword());
            }

            @Override
            public void onInitializationFailed() {
                showToast("Failed to initialize the SDK. Please make sure you have set the correct variables in AuthConfig, and have an active internet connection.");
                onLoadingStateChanged(false);
            }
        });
    }

    /**
     * Initialize the Sensibill SDK, using a hardcoded token for authentication.
     *
     * - Create an `Initializer` with the required configuration for the SDK
     * - Create and provide a `TokenProvider` which will provide a token (for this demo,
     *   [privateToken]) to the SDK when it requests it
     * - Initialize the SDK
     * - Upon initialization, start the SDK
     */
    private void initializeSDKWithToken() {
        Timber.i("For the demo, we are just releasing the SDK on every run through.");
        SensibillSDK.getInstance().release();

        // Create a token provider that will just be used to provide the `AuthConfig` token.  If using an integration
        // server for providing tokens, this token provider is called when the SDK requires a (new) auth token.
        // Provide a Token Provider to SDK initializer.  This token provider will be called when the
        // SDK starts, as well as if the token expires while the SDK is in use.
        // The token provider _must_ be provided in the `Initializer`, however it will not be used
        // until `SensibillSDK.start` is called.
        final TokenProvider tokenProvider = new TokenProvider() {
            @Override
            public void provideTokenReplacement(@Nullable String s, @NonNull String s1, @NonNull OnTokenProviderListener onTokenProviderListener) {
                onTokenProviderListener.onTokenProvided(AuthConfig.INSTANCE.getUserAccessToken());
            }
        };

        // Creates the builder for the SDK Initializer
        Initializer initializer = new InitializationBuilder(this, AuthConfig.INSTANCE.getEnvironment(), tokenProvider).build();

        // Initialize the SDK
        SensibillSDK.getInstance().initialize(initializer, new SDKInitializeListener() {
            @Override
            public void onInitialized() {
                startSDK("userIdentifierFromToken");
            }

            @Override
            public void onInitializationFailed() {
                showToast("Failed to initialize the SDK. Please make sure you have set the correct variables in AuthConfig, and have an active internet connection.");
                onLoadingStateChanged(false);
            }
        });
    }

    /**
     * Handles authentication using a username/password
     *
     * On success, it will call startSDK
     * On failure, will output to logs
     */
    private void authenticate(@NonNull SensibillAuth auth,@NonNull String username,@NonNull String password) {
        SensibillAuth.SessionListener sessionListener = new SensibillAuth.SessionListener() {
            @Override
            public void onSuccess(@NonNull OauthSession oauthSession) {
                startSDK("userIdentifierFromSignIn");
            }

            @Override
            public void onFailure(@NonNull SensibillAuth.AuthError authError) {
                Timber.e("Failed to authenticate: %s", authError.getShortDescription());
                onLoadingStateChanged(false, authError.getShortDescription());
            }
        };
        auth.signIn(username, password, sessionListener);
    }

    /**
     * Starts the SDK. Important to remember that the SDK needs
     * to have been initialized first.
     */
    private void startSDK(@NonNull String userIdentifier) {
        if (SensibillSDK.getInstance().getState() != CoreState.INITIALIZED) {
            showToast("SDK needs to be in a INITIALIZED state before trying to start it.");
            onLoadingStateChanged(false);
            return;
        }

        SensibillSDK.getInstance().start(userIdentifier, new SDKStartup() {
            @Override
            public void onSDKStarted() {
                startLauncher();
            }

            @Override
            public void onSDKFailed(LoginError loginError, String s) {
                showToast("SDK failed to start: " + s);
                onLoadingStateChanged(false);
            }
        });
    }

    /**
     * Will navigate to the launcher activity, where the user can test out different
     * integration methods.
     */
    private void startLauncher() {
        onLoadingStateChanged(false);
        if (SensibillSDK.getInstance().getState() != CoreState.STARTED) {
            showToast("Make sure to start the SDK before going to the launcher.");
            return;
        }

        // Creates the intent and launches it.
        Intent intent = new Intent(this, LauncherActivity.class);
        startActivity(intent);
    }

    private void onLoadingStateChanged(boolean isLoading) { onLoadingStateChanged(isLoading, null); }
    private void onLoadingStateChanged(boolean isLoading, @Nullable String error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isLoading) {
                    binding.progress.show();
                } else {
                    binding.progress.hide();
                }

                binding.loginWithToken.setEnabled(!isLoading);
                binding.loginWithUsername.setEnabled(!isLoading);
                binding.logout.setEnabled(!isLoading);

                if (error != null) {
                    showToast(error);
                }
            }
        });
    }

    /** Helper function to show toasts. */
    private void showToast(@NonNull String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private boolean checkCredentialsSet(@NonNull String... requiredCredentials) {
        boolean allValid = true;
        for (String credential : requiredCredentials) {
            boolean isValid = !credential.equals(AuthConfig.PLACEHOLDER) && !credential.isEmpty();
            if (!isValid) {
                allValid = false;
                break;
            }
        }
        return allValid;
    }
}
