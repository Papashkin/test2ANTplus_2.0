package com.antsfamily.biketrainer.ui.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet

@SuppressLint("Recycle")
fun Context.getStyledAttributes(
    attributeSet: AttributeSet?,
    styleArray: IntArray,
    block: TypedArray.() -> Unit
) = obtainStyledAttributes(attributeSet, styleArray, 0, 0).use(block)
