package com.zfs.commons.entity;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * 描述: 可选中并且实现Parcelable
 * 时间: 2018/9/7 09:58
 * 作者: zengfansheng
 */
public class CheckableParcelable<T extends Parcelable> extends CheckableItem<T> implements Parcelable {
    private static final String KEY_DATA = "data";
    
    public CheckableParcelable() {}
    
    public CheckableParcelable(T data) {
        super(data);
    }

    public CheckableParcelable(T data, boolean isChecked) {
        super(data, isChecked);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.data, flags);
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_DATA, this.data);
        dest.writeBundle(bundle);
        dest.writeByte(this.isChecked ? (byte) 1 : (byte) 0);
    }

    protected CheckableParcelable(Parcel in) {        
        this.data = in.readBundle(getClass().getClassLoader()).getParcelable(KEY_DATA);
        this.isChecked = in.readByte() != 0;
    }

    public static final Creator<CheckableParcelable> CREATOR = new Creator<CheckableParcelable>() {
        @Override
        public CheckableParcelable createFromParcel(Parcel source) {
            return new CheckableParcelable(source);
        }

        @Override
        public CheckableParcelable[] newArray(int size) {
            return new CheckableParcelable[size];
        }
    };
}
