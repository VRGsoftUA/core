package com.vrgsoft.coregateway.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

inline fun <reified T : Any> LiveData<T>.assert(calls: Int = 2, crossinline assert: ((it: T) -> Unit)) {
    val observer: Observer<T> = mock()
    val captor = argumentCaptor<T>()

    runBlocking {
        this@assert.observeForever(observer)

        delay(1000)

        captor.run {
            verify(observer, times(calls)).onChanged(capture())
            assert.invoke(captor.lastValue)
        }
    }
}