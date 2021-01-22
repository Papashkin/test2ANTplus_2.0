package com.antsfamily.biketrainer.ant.channel

interface ChannelChangedListener {
    fun onChannelChanged(newInfo: ChannelInfo)
    fun onAllowStartScan(allowStartScan: Boolean)
}
