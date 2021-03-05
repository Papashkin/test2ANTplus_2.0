package com.antsfamily.biketrainer.util

import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult

fun RequestAccessResult.getErrorMessageOrNull(): String? = when (this) {
    RequestAccessResult.USER_CANCELLED -> "Request was canceled by user"
    RequestAccessResult.CHANNEL_NOT_AVAILABLE -> "Channel is not available"
    RequestAccessResult.DEVICE_ALREADY_IN_USE -> "This sensor is already in use"
    RequestAccessResult.SEARCH_TIMEOUT -> "Request failed: waiting time is over"
    RequestAccessResult.ALREADY_SUBSCRIBED -> "You are already connected with this sensor"
    RequestAccessResult.BAD_PARAMS -> "Request failed: bad request parameters"
    RequestAccessResult.ADAPTER_NOT_DETECTED -> "ANT Adapter Not Available. Built-in ANT hardware or external adapter required."
    RequestAccessResult.OTHER_FAILURE,
    RequestAccessResult.UNRECOGNIZED -> "Something went wrong :("
    else -> null
}
