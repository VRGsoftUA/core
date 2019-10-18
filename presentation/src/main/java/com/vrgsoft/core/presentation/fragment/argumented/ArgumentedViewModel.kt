package com.vrgsoft.core.presentation.fragment.argumented

import com.vrgsoft.core.presentation.fragment.BaseViewModel

interface ArgumentedViewModel<A : BaseArguments> : BaseViewModel {
    fun applyArguments(args: A)
}