package com.kunoff.lupal.plasickakun.objects;

import android.os.Parcel;
import android.os.Parcelable;

public class DefaultItem implements Parcelable {
    private int id;
    private String name;
    private boolean isEnabled;

    public DefaultItem() {}

    public DefaultItem(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeByte(this.isEnabled ? (byte) 1 : (byte) 0);
    }

    protected DefaultItem(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.isEnabled = in.readByte() != 0;
    }

    public static final Parcelable.Creator<DefaultItem> CREATOR = new Parcelable.Creator<DefaultItem>() {
        @Override
        public DefaultItem createFromParcel(Parcel source) {
            return new DefaultItem(source);
        }

        @Override
        public DefaultItem[] newArray(int size) {
            return new DefaultItem[size];
        }
    };
}
