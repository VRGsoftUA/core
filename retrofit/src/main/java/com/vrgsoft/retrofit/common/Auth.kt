package com.vrgsoft.retrofit.common

import okhttp3.Interceptor
import okhttp3.Response

interface Auth {
    fun process(chain: Interceptor.Chain?): Response
}