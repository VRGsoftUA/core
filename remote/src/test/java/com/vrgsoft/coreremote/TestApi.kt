package com.vrgsoft.coreremote

import kotlinx.coroutines.Deferred
import retrofit2.Response

interface TestApi {
    fun call(): Deferred<Response<String>>
}