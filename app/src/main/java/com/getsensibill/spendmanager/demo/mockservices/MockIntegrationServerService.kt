package com.getsensibill.spendmanager.demo.mockservices

import com.getsensibill.spendmanager.demo.AuthConfig.privateToken

/**
 * A fake service to represent your service with your own integration server.
 * The integration server will provide Sensibill SDK access tokens.
 */
object MockIntegrationServerService {
    fun fetchNewAccessToken(
        userIdentifier: String,
        onSuccess: (newToken: String) -> Unit,
        onFailure: (error: String) -> Unit
    ) {
        onSuccess(privateToken)
    }
}
