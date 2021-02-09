package com.antsfamily.biketrainer.ui.util

import android.content.res.TypedArray

fun TypedArray.use(block: TypedArray.() -> Unit) {
    try {
        block()
    } finally {
        this.recycle()
    }
}
