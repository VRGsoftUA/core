package com.vrgsoft.core.presentation.activity

import android.os.Bundle
import com.vrgsoft.core.R
import com.vrgsoft.core.presentation.activity.BaseActivity

abstract class ContainerActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)
    }

    fun getContainerId() = R.id.fragmentContainer
}