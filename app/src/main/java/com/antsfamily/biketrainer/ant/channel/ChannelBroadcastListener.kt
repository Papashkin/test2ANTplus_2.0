package com.antsfamily.biketrainer.ant.channel

interface ChannelBroadcastListener {
    fun onBroadcastChanged(newInfo: ChannelInfo)
    fun onBackgroundScanStateChange(backgroundScanInProgress: Boolean, backgroundScanIsConfigured: Boolean)
    fun onChannelDeath()
}
