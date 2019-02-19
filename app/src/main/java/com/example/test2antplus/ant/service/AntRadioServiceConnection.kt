package com.example.test2antplus.ant.service

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import com.dsi.ant.AntService
import com.dsi.ant.channel.*
import com.example.test2antplus.ant.channel.ChannelBroadcastListener
import com.example.test2antplus.ant.channel.ChannelChangedListener
import com.example.test2antplus.ant.channel.ChannelController
import com.example.test2antplus.ant.channel.ChannelInfo
import javax.inject.Inject


class AntRadioServiceConnection @Inject constructor(private var context: Context): ServiceConnection {
    companion object {
        private const val TAG = "ChannelService"
        private val CHANNEL_LOCK = Object()
    }

    private var mAllowAcquireBackgroundScanChannel = false
    private var mBackgroundScanController : ChannelController? = null
    private var mListener: ChannelChangedListener? = null
    private var mBackgroundScanInProgress = false
    private var mBackgroundScanAcquired = false
    private var mAntRadioService: AntService? = null
    private var mAntChannelProvider: AntChannelProvider? = null

    private val broadcastListener = object : ChannelBroadcastListener {
        /**
         * [onBroadcastChanged] - Pass on the received channel info to activity for display
         */
        override fun onBroadcastChanged(newInfo: ChannelInfo) {
            mListener?.apply { this.onChannelChanged(newInfo) }
        }

        /**
         * [onBackgroundScanStateChange] allows starting background scan if no scan in progress
         */
        override fun onBackgroundScanStateChange(backgroundScanInProgress: Boolean, backgroundScanIsConfigured: Boolean) {
            if(mListener == null) return

            mBackgroundScanInProgress = backgroundScanInProgress
            mListener?.apply {
                this.onAllowStartScan(!mBackgroundScanInProgress && mBackgroundScanAcquired)
            }
        }

        /**
         * [onChannelDeath] kills background scan channel
         */
        override fun onChannelDeath() {
            closeBackgroundScanChannel()
            if(mListener == null) return
            mListener?.apply { this.onAllowStartScan(false) }
        }
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        mAntChannelProvider = null
        mAntRadioService = null
        mListener?.apply { this.onAllowStartScan(false) }
        mAllowAcquireBackgroundScanChannel = false
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        mAntRadioService = AntService(service)
        try {
            mAntChannelProvider = mAntRadioService!!.channelProvider

            val mChannelAvailable : Boolean = (getNumberOfChannelsAvailable() > 0)
            val legacyInterfaceInUse: Boolean = mAntChannelProvider!!.isLegacyInterfaceInUse

            // If there are channels OR legacy interface in use, allow acquire background scan.
            // If no channels available AND legacy interface is not in use, disallow acquire background scan
            mAllowAcquireBackgroundScanChannel = mChannelAvailable || legacyInterfaceInUse

            // Attempting to acquire a background scan channel when connected to ANT Radio Service
            if (mAllowAcquireBackgroundScanChannel) {
                acquireBackgroundScanningChannel()
                mListener?.apply {
                    this.onAllowStartScan(!mBackgroundScanInProgress && mBackgroundScanAcquired)
                }
            }
        } catch (e: RemoteException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        } catch (e: ChannelNotAvailableException) {
            // If channel is not available, do not allow to start scan
            mListener?.apply { this.onAllowStartScan(false) }
        }
    }

    /**
     * In order to get the number of channels that are capable of
     * background scanning, a [Capabilities] object must be created with
     * the background scanning feature set to true.
     *
     * By passing in Capabilities object this will return the number
     * of channels capable of background scanning.
     */
    private fun getNumberOfChannelsAvailable(): Int {
        if(null != mAntChannelProvider) {
            val capabilities = Capabilities()
            capabilities.supportBackgroundScanning(true)
            try {
                val numChannels = mAntChannelProvider!!.getNumChannelsAvailable(capabilities)
                Log.i(TAG, "Number of channels with background scanning capabilities: $numChannels")
                return numChannels
            } catch (e: RemoteException) {
                Log.i(TAG, "", e)
            }
        }
        return 0
    }

    @Throws(ChannelNotAvailableException::class)
    fun acquireBackgroundScanningChannel() {
        synchronized(CHANNEL_LOCK) {
            // We only want one channel; don't attempt if already acquired
            if (!mBackgroundScanAcquired) {

                // Acquire a channel, if no exception then set background scan
                // acquired to true
                val antChannel = acquireChannel()
                mBackgroundScanAcquired = true

                if (null != antChannel) {
                    mBackgroundScanController =
                            ChannelController(antChannel, broadcastListener)
                }
            }
        }
    }

    /**
     * In order to acquire a channel that is capable of background scanning, a Capabilities object must be created with the
     * background scanning feature set to true. Passing this Capabilities object to [acquireChannel] will return a channel
     * that is capable of being assigned (via Extended Assignment) as a background scanning channel.
     */
    @Throws(ChannelNotAvailableException::class)
    fun acquireChannel(): AntChannel? {
        var mAntChannel: AntChannel? = null
        if(null != mAntChannelProvider) {
            try {
                val capableOfBackgroundScan = Capabilities()
                capableOfBackgroundScan.supportBackgroundScanning(true)
                mAntChannel = mAntChannelProvider!!.acquireChannel(
                    context,
                    PredefinedNetwork.PUBLIC,
                    capableOfBackgroundScan
                )

                // Get background scan status
                mBackgroundScanInProgress = mAntChannel?.backgroundScanState!!.isInProgress
            } catch (e: RemoteException) { }
        }
        return mAntChannel
    }

    fun closeBackgroundScanChannel() {
        mBackgroundScanController?.apply {
            this.close()
            mBackgroundScanAcquired = false
        }
    }
}