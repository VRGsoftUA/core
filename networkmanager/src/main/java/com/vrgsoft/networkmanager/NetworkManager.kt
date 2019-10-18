package com.vrgsoft.networkmanager

import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.vrgsoft.networkmanager.livedata.SingleLiveManager

class NetworkManager {
    val errors = SingleLiveManager<Error>()

    val processing = MutableLiveData<Boolean>().apply {
        postValue(false)
    }

    fun startProcessing() {
        processing.postValue(true)
    }

    fun stopProcessing() {
        processing.postValue(false)
    }

    fun applyDefaultObserver(fragment: Fragment) {
        errors.observe(fragment) {
            Toast.makeText(fragment.requireActivity(), it?.message ?: return@observe, Toast.LENGTH_SHORT).show()
        }
    }
}