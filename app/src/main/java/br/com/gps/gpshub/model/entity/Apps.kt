package br.com.gps.gpshub.model.entity

import android.graphics.drawable.Drawable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "apps")
data class Apps @JvmOverloads constructor(
    @PrimaryKey @ColumnInfo var packageName: String,
    @ColumnInfo (name = "isFavouriteApp") var isFavouriteApp: Boolean = false,
    @ColumnInfo var isLocalApp: Boolean = false,
    @ColumnInfo var isLocked: Boolean = false,
    @ColumnInfo var isSysApp: Boolean = false
) {

    @ColumnInfo
    var appName: String? = null

    @ColumnInfo
    var icon: Int? = null

//    @ColumnInfo
//    var isFavouriteApp: Boolean = false

//    @ColumnInfo
//    var isLocalApp: Boolean = false
//
//    @ColumnInfo
//    var isLocked: Boolean = false

//    @ColumnInfo
//    var isSetUnlock: Boolean = false

//    @Nullable
//    var appInfo: ApplicationInfo? = null

//    @ColumnInfo
//    var isSysApp: Boolean = false

    @ColumnInfo
    var iconDrawable: String? = null

    @ColumnInfo
    var topTitle: String = ""


    val isDevice
        get() = !isLocalApp


    companion object {
        val appsComparator = Comparator<Apps> { a, b ->
            when {
                (a == null && b == null) -> 0
                (a == null) -> -1
                (a.isLocked && !b.isLocked) -> -1
                (!a.isLocked && b.isLocked) -> 1
                (a.isLocalApp && !b.isLocalApp) -> -1
                (!a.isLocalApp && b.isLocalApp) -> 1
                (!a.isLocalApp && a.isFavouriteApp && !b.isLocalApp && !b.isFavouriteApp) -> -1
                (!a.isLocalApp && !a.isFavouriteApp && !b.isLocalApp && b.isFavouriteApp) -> 1
                (a.appName != null && b.appName != null) -> (a.appName!!.compareTo(b.appName!!))

                else -> 0
            }
        }
    }

}
