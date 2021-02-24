package com.antsfamily.biketrainer.util

import java.math.BigDecimal

fun BigDecimal?.orZero(): BigDecimal = this ?: BigDecimal.ZERO

@Suppress("UNCHECKED_CAST")
fun <T : Number> T?.orZero() = this ?: 0 as T

fun Boolean?.orFalse() = this ?: false
