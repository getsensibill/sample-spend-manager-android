package com.getsensibill.spendmanager.demo

import com.getsensibill.rest.network.DefaultEnvironment

object AuthConfig {
    const val PLACEHOLDER = "PLACEHOLDER"

    // TODO: Verify that the correct environment is selected.
    var environment = DefaultEnvironment.BETA_PROD

    // OPTION 1: LOGIN WITH USERNAME AND PASSWORD
    // Note: all values are provided by Sensibill, and must match the selected environment
    // TODO: Set your username/password, API key/secret, and credentials type before running.
    var username = PLACEHOLDER
    var password = PLACEHOLDER
    var apiKey = PLACEHOLDER
    var apiSecret = PLACEHOLDER
    var credentialType = PLACEHOLDER
    // Does not need to be updated or set for demo use
    var redirectURL = "https://testclient.com/redirect"

    // OPTION 2: LOGIN WITH USER ACCESS TOKEN
    // Note: the value is provided by your Integration Server,
    //       or with the static access token obtained by any other method
    //       (e.g. provided by Sensibill)
    //       The value must match the selected environment
    // TODO: Set the user access token before running.
    var userAccessToken = PLACEHOLDER
}