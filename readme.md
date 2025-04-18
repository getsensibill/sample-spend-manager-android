# SPEND MANAGER DEMO

## SETUP THE PROJECT

1. Open `build.gradle` in the root folder and replace the credentials for getsensibill Maven:

    ```properties
    ...
    maven {
        url 'https://maven.pkg.github.com/getsensibill/sdk-android'
        credentials {
            username 'TO BE PROVIDED BY SENSIBILL'
            password 'TO BE PROVIDED BY SENSIBILL'
    ```

2. Optionally, adjust build parameters in `app/build.gradle` file (e.g. min/target SDK, JVM version) to best match your real app.

## CONFIGURE THE AUTHENTICATION METHOD

App demonstrates 2 authentication methods:

* **Using Username and Password**.
     This simple method allows you to login with preconfigured username and password, provided by Sensibill.

* **Using User Access Token**.
     This method allows you to demo with the User Access Token provided by your Integration Server,
     or with the static access token obtained by any other method (e.g. provided by Sensibill).
     See [Authentication](https://docs.getsensibill.com/docs/V1/ZG9jOjExMDcyMQ-authentication) page of API documentation for more information on this method

To configure a desired method:

1. Open `AuthConfig.kt` (in folder `./app/src/main/java/com/getsensibill/spendmanager/demo`).

2. Make sure the value selected for the `environment` variable corresponds the environment for which the username or access token you are about to provide were created.

    ```kotlin
    // TODO: Verify that the correct environment is selected.
    var environment = ...
    ```

3. Provide authentication parameters:
   * When using the authentication method with username and password, provide username/password, API key/secret, and the credentials type.
       All values are provided by Sensibill.

      ```kotlin
        // TODO: Set your username/password, API key/secret, and credentials type before running.
        var username = PLACEHOLDER
        var password = PLACEHOLDER
        var apiKey = PLACEHOLDER
        var apiSecret = PLACEHOLDER
        var credentialType = PLACEHOLDER
      ```

   * When using the User Access Token authentication method, provide the access token.

      ```kotlin
        // TODO: Set the user access token before running.
        var userAccessToken = PLACEHOLDER
      ```

See ***Get Started*** guide in the SDK documentation for more details on SDK startup configuration and authentication options.
