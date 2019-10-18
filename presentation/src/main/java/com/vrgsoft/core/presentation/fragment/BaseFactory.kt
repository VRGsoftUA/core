package com.vrgsoft.core.presentation.fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

abstract class BaseFactory<VM : ViewModel> : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return createViewModel() as T
    }

    abstract fun createViewModel(): VM
}