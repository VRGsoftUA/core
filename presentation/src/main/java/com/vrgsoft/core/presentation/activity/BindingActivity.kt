package com.vrgsoft.core.presentation.activity

import android.os.Bundle
import android.view.LayoutInflater
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.vrgsoft.core.presentation.fragment.BaseViewModel

abstract class BindingActivity<B : ViewDataBinding> : BaseActivity() {
    protected lateinit var binding: B
    abstract val viewModel: BaseViewModel

    @LayoutRes
    abstract fun getLayoutRes(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(LayoutInflater.from(this), getLayoutRes(), null, false)
        binding.lifecycleOwner = this

        lifecycle.addObserver(viewModel)

        setContentView(binding.root)
    }
}