package com.antsfamily.biketrainer.ant.channel

class ChannelInfo(deviceNumber: Int, isMaster: Boolean) {
    var deviceNumber: Int = 0
    var isMaster: Boolean = false

    var error: Boolean = false
    private var mErrorMessage: String? = ""

    init {
        this.deviceNumber = deviceNumber
        this.isMaster = isMaster
        error = false
        mErrorMessage = null
    }
}