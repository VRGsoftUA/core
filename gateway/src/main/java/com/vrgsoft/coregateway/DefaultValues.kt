package com.vrgsoft.coregateway

fun Long?.orMinusOne(): Long {
    return this ?: -1L
}

fun Long?.orZero(): Long {
    return this ?: 0L
}

fun Int?.orMinusOne(): Int {
    return this ?: -1
}

fun Int?.orZero(): Int {
    return this ?: 0
}