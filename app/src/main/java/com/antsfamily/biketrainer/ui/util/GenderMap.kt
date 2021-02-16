package com.antsfamily.biketrainer.ui.util

import androidx.annotation.DrawableRes
import com.antsfamily.biketrainer.R
import com.garmin.fit.Gender

@DrawableRes
fun Gender.iconId(): Int = when (this) {
    Gender.FEMALE -> R.drawable.ic_profile_woman
    Gender.MALE -> R.drawable.ic_profile_man
    Gender.INVALID -> -1
}
