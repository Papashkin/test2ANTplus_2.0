package com.example.test2antplus.ant.channel

interface ChannelBroadcastListener {
    fun onBroadcastChanged(newInfo: ChannelInfo)
    fun onBackgroundScanStateChange(backgroundScanInProgress: Boolean, backgroundScanIsConfigured: Boolean)
    fun onChannelDeath()
}