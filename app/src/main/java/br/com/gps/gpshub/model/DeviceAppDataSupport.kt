package br.com.gps.gpshub.model

import android.os.Parcel
import android.os.Parcelable
import org.litepal.crud.DataSupport


class DeviceAppDataSupport() : DataSupport(), Parcelable {

    var packageName: String? = null

    var appName: String? = null

    var icon: Int? = null

    var isFavouriteApp: Boolean = false

    var isLocalApp: Boolean = false

    var isLocked: Boolean = false

    var isSetUnlock: Boolean = false

//    @Nullable
//    var appInfo: ApplicationInfo? = null

    var isSysApp: Boolean = false

    var topTitle: String? = ""


    constructor(packageName: String) : this() {
        this.packageName = packageName
    }

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
    ) {
        appName = parcel.readString()
        topTitle = parcel.readString()
        icon = parcel.readInt()
        isFavouriteApp = parcel.readByte() != 0.toByte()
        isLocalApp = parcel.readByte() != 0.toByte()
        isLocked = parcel.readByte() != 0.toByte()
        isSysApp = parcel.readByte() != 0.toByte()
        isSetUnlock = parcel.readByte() != 0.toByte()
//        appInfo = parcel.readParcelable(ApplicationInfo::class.java.classLoader)
    }


    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(packageName)
        dest.writeString(appName)
        dest.writeByte(if (isLocked) 1.toByte() else 0.toByte())
        dest.writeByte(if (isFavouriteApp) 1.toByte() else 0.toByte())
        dest.writeByte(if (isLocalApp) 1.toByte() else 0.toByte())
        dest.writeByte(if (isSysApp) 1.toByte() else 0.toByte())
//        dest.writeParcelable(appInfo, flags)
        dest.writeString(topTitle)
        dest.writeByte(if (isSetUnlock) 1.toByte() else 0.toByte())
    }


    companion object CREATOR : Parcelable.Creator<DeviceAppDataSupport> {
        override fun createFromParcel(parcel: Parcel): DeviceAppDataSupport {
            return DeviceAppDataSupport(parcel)
        }

        override fun newArray(size: Int): Array<DeviceAppDataSupport?> {
            return arrayOfNulls(size)
        }
    }

}
