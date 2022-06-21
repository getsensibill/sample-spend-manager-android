package com.getsensibill.spendmanager.demo

import com.getsensibill.rest.network.DefaultEnvironment
import com.getsensibill.rest.network.SBEnvironment

object AuthConfig {
    // ========== Auth Configurations - used for demo app authentication ==========
    const val PROVIDED_BY_SENSIBILL = "TO BE PROVIDED BY SENSIBILL"

    // TODO: Set your token and or username/password before running. Values provided by Sensibill
    var privateToken = PROVIDED_BY_SENSIBILL
    var username = PROVIDED_BY_SENSIBILL
    var password = PROVIDED_BY_SENSIBILL
    var tokenOwnersUserIdentifier = PROVIDED_BY_SENSIBILL

    // TODO: Make sure to update the key, secret, credentials and environment based on what is provided from Sensibill
    var apiKey = PROVIDED_BY_SENSIBILL
    var apiSecret = PROVIDED_BY_SENSIBILL
    var credentialType = PROVIDED_BY_SENSIBILL
    var environment = DefaultEnvironment.BETA_PROD

    // Does not need to be updated or set for demo use
    var redirectURL = "https://testclient.com/redirect"

    // ========== Separate Examples ==========
    /** A sample fully custom environment */
    val sampleCustomEnvironment: SBEnvironment = object : SBEnvironment {
        // Provide URL information
        override fun getHostName(): String = "my.custom.env"
        override fun getV1BaseUrl(): String = determineBaseUrl("1")
        override fun getV2BaseUrl(): String = determineBaseUrl("2")

        // Add a list of certificate pins if certificate pinning is required
        override fun getCertificatePins(): MutableList<String> = mutableListOf()

        // In any realistic case this should always be true
        override fun hasHttps(): Boolean = true

        private fun determineBaseUrl(apiVersion: String) =
            "${if (hasHttps()) "https" else "http"}://$hostName/api/v$apiVersion/"
    }
}
