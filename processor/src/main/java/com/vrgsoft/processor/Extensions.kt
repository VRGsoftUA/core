package com.vrgsoft.processor

import javax.annotation.processing.Messager

const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"

fun Messager.errormessage(message: () -> String) {
    this.printMessage(javax.tools.Diagnostic.Kind.ERROR, message())
}

fun Messager.noteMessage(message: () -> String) {
    this.printMessage(javax.tools.Diagnostic.Kind.NOTE, message())
}

fun Messager.warningMessage(message: () -> String) {
    this.printMessage(javax.tools.Diagnostic.Kind.WARNING, message())
}