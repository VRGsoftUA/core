package com.vrgsoft.annotations

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class ActivityVMDiModule(
    val provideRouter: Boolean = false
)