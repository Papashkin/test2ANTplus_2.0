package com.antsfamily.biketrainer.util

import java.math.BigDecimal

fun BigDecimal?.orZero() = this ?: BigDecimal.ZERO

@Suppress("UNCHECKED_CAST")
fun <T : Number> T?.orZero() = this ?: 0 as T
