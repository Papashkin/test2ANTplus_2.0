package com.antsfamily.biketrainer.ui.util

import androidx.annotation.StringRes
import com.antsfamily.biketrainer.R
import com.garmin.fit.Gender

@StringRes
fun Gender.resourceId(): Int = when (this) {
    Gender.MALE -> R.string.gender_male
    Gender.FEMALE -> R.string.gender_female
    Gender.INVALID -> R.string.gender_invalid
}
