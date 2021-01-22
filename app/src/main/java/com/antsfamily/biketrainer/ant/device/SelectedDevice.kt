package com.antsfamily.biketrainer.ant.device

import android.os.Parcel
import android.os.Parcelable
import com.dsi.ant.plugins.antplus.pccbase.MultiDeviceSearch.MultiDeviceSearchResult
import javax.inject.Inject

class SelectedDevice @Inject constructor(
    val device: MultiDeviceSearchResult,
    var isSelected: Boolean
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readParcelable(MultiDeviceSearchResult::class.java.classLoader)!!,
        parcel.readByte() != 0.toByte()
    )

    override fun describeContents(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(device, flags)
        parcel.writeByte(if (isSelected) 1 else 0)
    }

    companion object CREATOR : Parcelable.Creator<SelectedDevice> {
        override fun createFromParcel(parcel: Parcel): SelectedDevice {
            return SelectedDevice(parcel)
        }

        override fun newArray(size: Int): Array<SelectedDevice?> {
            return arrayOfNulls(size)
        }
    }
}
