package com.vrgsoft.coreremote

import com.vrgsoft.coreremote.result.BaseResult
import com.vrgsoft.coreremote.result.SuccessResult

fun <T, M> BaseResult<T>.mapDataIfSuccess(mapper: ((item: T) -> M)): M? {
    if (this !is SuccessResult<T>) {
        return null
    }

    return mapper.invoke(this.data)
}

fun <T> BaseResult<T>.getDataIfSuccess(): T? {
    if (this !is SuccessResult<T>) {
        return null
    }

    return this.data
}