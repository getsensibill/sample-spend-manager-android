package com.getsensibill.spendmanager.demo

import com.getsensibill.rest.network.DefaultEnvironment

object AuthConfig {
    const val PROVIDED_BY_SENSIBILL = "TO BE PROVIDED BY SENSIBILL"

    // TODO: Set your token and or username/password before running. Values provided by Sensibill
    var privateToken = PROVIDED_BY_SENSIBILL
    var username = PROVIDED_BY_SENSIBILL
    var password = PROVIDED_BY_SENSIBILL

    // TODO: Make sure to update the key, secret, credentials and environment based on what is provided from Sensibill
    var apiKey = PROVIDED_BY_SENSIBILL
    var apiSecret = PROVIDED_BY_SENSIBILL
    var credentialType = PROVIDED_BY_SENSIBILL
    var environment = DefaultEnvironment.BETA_STAGING

    // Does not need to be updated or set for demo use
    var redirectURL = "https://testclient.com/redirect"
}