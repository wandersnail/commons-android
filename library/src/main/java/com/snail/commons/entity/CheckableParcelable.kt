package com.snail.commons.entity

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable

/**
 * 描述: 可选中并且实现Parcelable
 * 时间: 2018/9/7 09:58
 * 作者: zengfansheng
 */
class CheckableParcelable<T : Parcelable> : CheckableItem<T>, Parcelable {

    constructor()

    constructor(data: T?) : super(data)

    constructor(data: T?, isChecked: Boolean) : super(data, isChecked)

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        val bundle = Bundle()
        bundle.putParcelable(KEY_DATA, this.data)
        dest.writeBundle(bundle)
        dest.writeByte(if (isChecked) 1.toByte() else 0.toByte())
    }

    @Suppress("unchecked_cast")
    protected constructor(source: Parcel) {
        this.data = source.readBundle(javaClass.classLoader)?.getParcelable<Parcelable>(KEY_DATA) as? T
        this.isChecked = source.readByte().toInt() != 0
    }

    companion object {
        private const val KEY_DATA = "items"

        @JvmField
        val CREATOR: Parcelable.Creator<CheckableParcelable<*>> = object : Parcelable.Creator<CheckableParcelable<*>> {
            override fun createFromParcel(source: Parcel): CheckableParcelable<*> {
                return CheckableParcelable<Parcelable>(source)
            }

            override fun newArray(size: Int): Array<CheckableParcelable<*>?> {
                return arrayOfNulls(size)
            }
        }
    }
}
