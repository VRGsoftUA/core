package com.vrgsoft.coregateway

import androidx.annotation.VisibleForTesting
import com.vrgsoft.coreremote.error.BaseError
import com.vrgsoft.coreremote.result.BaseResult
import com.vrgsoft.coreremote.result.ErrorResult
import com.vrgsoft.networkmanager.NetworkManager

abstract class BaseGateway(override val networkManager: NetworkManager) : IBaseGateway {

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    suspend fun <T> executeRemote(call: suspend (() -> BaseResult<T>)): BaseResult<T> {
        networkManager.startProcessing()

        val result = call.invoke()

        networkManager.stopProcessing()

        if (result is ErrorResult<T>) {
            val message = calculateMessage(result.error)

            networkManager.errors.call(Error(message))
        }

        return result
    }

    abstract fun calculateMessage(error: BaseError): String
}