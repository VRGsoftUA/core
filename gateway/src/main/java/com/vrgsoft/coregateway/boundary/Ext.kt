package com.vrgsoft.coregateway.boundary

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.vrgsoft.networkmanager.NetworkManager

fun <Key, Value> DataSource.Factory<Key, Value>.toLiveData(
    networkManager: NetworkManager,
    pageSize: Int,
    getLocalCount: (suspend () -> Int),
    fetch: (suspend (count: Int) -> Unit)
): LiveData<PagedList<Value>> {
    return this.toLiveData(
        pageSize = pageSize,
        boundaryCallback = BaseCallback(
            networkManager = networkManager,
            getAllCount = getLocalCount,
            fetch = fetch
        )
    )
}