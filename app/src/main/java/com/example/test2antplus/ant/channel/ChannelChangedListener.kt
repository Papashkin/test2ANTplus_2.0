package com.example.test2antplus.ant.channel

interface ChannelChangedListener {
    fun onChannelChanged(newInfo: ChannelInfo)
    fun onAllowStartScan(allowStartScan: Boolean)
}