package com.tap.gallary.models

import android.os.Parcelable
import android.os.Parcel

/**
 * Created by Ankit on 03-11-2016.
 */
class Image() : Parcelable {
    var id: Long = 0
    var name: String? = null
    var path: String? = null
    var isSelected = false

    constructor(parcel: Parcel) : this() {
        id = parcel.readLong()
        name = parcel.readString()
        path = parcel.readString()
        isSelected = parcel.readByte() != 0.toByte()
    }

    constructor(id: Long, name: String?, path: String?, isSelected: Boolean) : this() {
        this.id = id
        this.name = name
        this.path = path
        this.isSelected = isSelected
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, p1: Int) {
        dest.writeLong(id)
        dest.writeString(name)
        dest.writeString(path)
    }

    companion object CREATOR : Parcelable.Creator<Image> {
        override fun createFromParcel(parcel: Parcel): Image {
            return Image(parcel)
        }

        override fun newArray(size: Int): Array<Image?> {
            return arrayOfNulls(size)
        }
    }


}