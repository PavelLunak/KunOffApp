package com.kunoff.lupal.plasickakun.objects;

import android.os.Parcel;
import android.os.Parcelable;

public class ItemMedia implements Parcelable {

    private int id;
    private String path;
    private String fileName;
    private boolean isEnabled;
    private boolean isPlay;
    private boolean isHelp;

    public ItemMedia() {}

    public ItemMedia(String path, String fileName) {
        this.path = path;
        this.fileName = fileName;
    }

    public ItemMedia(boolean isHelp) {
        this.isHelp = isHelp;
    }

    public ItemMedia(int id, String path, String fileName, boolean isEnabled, boolean isPlay) {
        this.id = id;
        this.path = path;
        this.fileName = fileName;
        this.isEnabled = isEnabled;
        this.isPlay = isPlay;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public boolean isPlay() {
        return isPlay;
    }

    public void setPlay(boolean play) {
        isPlay = play;
    }

    public boolean isHelp() {
        return isHelp;
    }

    public void setHelp(boolean help) {
        isHelp = help;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.path);
        dest.writeString(this.fileName);
        dest.writeByte(this.isEnabled ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isPlay ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isHelp ? (byte) 1 : (byte) 0);
    }

    protected ItemMedia(Parcel in) {
        this.id = in.readInt();
        this.path = in.readString();
        this.fileName = in.readString();
        this.isEnabled = in.readByte() != 0;
        this.isPlay = in.readByte() != 0;
        this.isHelp = in.readByte() != 0;
    }

    public static final Parcelable.Creator<ItemMedia> CREATOR = new Parcelable.Creator<ItemMedia>() {
        @Override
        public ItemMedia createFromParcel(Parcel source) {
            return new ItemMedia(source);
        }

        @Override
        public ItemMedia[] newArray(int size) {
            return new ItemMedia[size];
        }
    };
}
