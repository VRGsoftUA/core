package com.vrgsoft.coregateway.boundary

import androidx.paging.PagedList
import com.vrgsoft.networkmanager.NetworkManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

class BaseCallback<T>(
    private val networkManager: NetworkManager,
    private val getAllCount: (suspend () -> Int),
    private val fetch: (suspend (count: Int) -> Unit)
) : PagedList.BoundaryCallback<T>() {
    private var channel = Channel<Int>()

    private suspend fun loadMore() {
        if (networkManager.processing.value == true) {
            return
        }

        val count = getAllCount.invoke()
        fetch.invoke(count)
    }

    override fun onZeroItemsLoaded() {
        super.onZeroItemsLoaded()

        GlobalScope.launch {
            channel.send(1)
        }
    }

    override fun onItemAtEndLoaded(itemAtEnd: T) {
        super.onItemAtEndLoaded(itemAtEnd)

        GlobalScope.launch {
            channel.send(1)
        }
    }

    init {
        GlobalScope.launch {
            while (true) {
                channel.receive()
                if (networkManager.processing.value == true) {
                    continue
                }
                loadMore()
            }
        }
    }
}