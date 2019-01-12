package com.xing.imageselector.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 图片
 */
public class Image implements Parcelable {
    /**
     * 图片文件名称
     */
    private String name;
    /**
     * 图片路径
     */
    private String path;
    /**
     * 图片文件创建日期
     */
    private long time;
    /**
     * 文件类型
     */
    private String mimeType;


    public Image(String name, String path, long time, String mimeType) {
        this.name = name;
        this.path = path;
        this.time = time;
        this.mimeType = mimeType;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Image image = (Image) o;
        if (name != null ? !name.equals(image.name) : image.name != null) return false;
        if (path != null ? !path.equals(image.path) : image.path != null) return false;
        return mimeType != null ? mimeType.equals(image.mimeType) : image.mimeType == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + (int) (time ^ (time >>> 32));
        result = 31 * result + (mimeType != null ? mimeType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Image{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", time=" + time +
                ", mimeType='" + mimeType + '\'' +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.path);
        dest.writeLong(this.time);
        dest.writeString(this.mimeType);
    }

    protected Image(Parcel in) {
        this.name = in.readString();
        this.path = in.readString();
        this.time = in.readLong();
        this.mimeType = in.readString();
    }

    public static final Creator<Image> CREATOR = new Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel source) {
            return new Image(source);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };
}