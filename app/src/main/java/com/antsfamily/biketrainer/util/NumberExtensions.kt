package com.antsfamily.biketrainer.util

import java.math.BigDecimal

fun BigDecimal?.orZero() = this ?: BigDecimal.ZERO

fun Number?.orZero() = this ?: 0
