package com.vrgsoft.retrofit.common

import android.accounts.AccountManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthCustomInterceptor(
    private val manager: AccountManager,
    private val packageName: String
) : Auth {

    override fun process(chain: Interceptor.Chain?): Response {
        val accounts = manager.getAccountsByType(packageName)
        val builder = chain!!.request()!!.newBuilder()

        if (accounts.isNotEmpty()) {
            val account = accounts[0]
            val token = manager.blockingGetAuthToken(account, "Bearer", false)

            if (token?.isNotEmpty() == true) {
                builder?.addHeader(HEADER_AUTHORIZATION, "Bearer $token")
            }
        }

        return chain.proceed(builder.build())
    }

    companion object {
        private const val HEADER_AUTHORIZATION = "Authorization"
    }
}