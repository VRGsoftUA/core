package com.vrgsoft.retrofit.common

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val auth: Auth? = null
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain?): Response {
        return auth?.process(chain) ?: chain!!.request()!!.newBuilder()?.let {
            chain.proceed(it.build())
        }!!
    }
}