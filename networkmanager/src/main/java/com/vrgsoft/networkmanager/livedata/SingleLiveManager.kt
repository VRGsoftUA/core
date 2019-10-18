package com.vrgsoft.networkmanager.livedata

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer

class SingleLiveManager<T : Any>(val defValue: T? = null) {
    private val event = SingleLiveEvent<T>()

    fun call(data: T) {
        event.postValue(data)
    }

    fun call() {
        checkNotNull(defValue) { "You must set default value for this call" }

        event.postValue(defValue)
    }

    fun observe(owner: LifecycleOwner, observer: ((item: T?) -> Unit)) {
        event.observe(owner, Observer {
            observer.invoke(it)
        })
    }
}