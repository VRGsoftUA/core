package com.vrgsoft.core.presentation.router

import com.vrgsoft.core.presentation.fragment.IBaseRouter

interface Routable {
    val router: IBaseRouter
    fun backPressed(): Boolean {
        return router.clickBack()
    }
}