package com.vrgsoft.core.presentation.liveData

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer

class SingleLiveManager<T>(val defValue: T? = null) {
    val event = SingleLiveEvent<T>()

    fun call(data: T) {
        event.postValue(data)
    }

    fun call() {
        if (defValue != null) {
            event.postValue(defValue)
        }
    }

    fun observe(owner: LifecycleOwner, observer: ((item: T?) -> Unit)) {
        event.observe(owner, Observer {
            observer.invoke(it)
        })
    }
}