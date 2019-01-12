package com.xing.imageselector.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * 文件夹
 */
public class Folder implements Parcelable {
    /**
     * 文件夹名称
     */
    private String name;
    /**
     * 文件夹下的所有图片文件
     */
    private ArrayList<Image> imageList = new ArrayList<>();
    /**
     * 文件夹路径
     */
    private String path;

    public Folder() {
    }

    public Folder(String name) {
        this.name = name;
    }

    public Folder(String name, ArrayList<Image> imageList) {
        this.name = name;
        this.imageList = imageList;
    }


    public void addImage(Image image) {
        imageList.add(image);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Image> getImageList() {
        return imageList;
    }

    public void setImageList(ArrayList<Image> imageList) {
        this.imageList = imageList;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeTypedList(this.imageList);
        dest.writeString(this.path);
    }

    protected Folder(Parcel in) {
        this.name = in.readString();
        this.imageList = in.createTypedArrayList(Image.CREATOR);
        this.path = in.readString();
    }

    public static final Creator<Folder> CREATOR = new Creator<Folder>() {
        @Override
        public Folder createFromParcel(Parcel source) {
            return new Folder(source);
        }

        @Override
        public Folder[] newArray(int size) {
            return new Folder[size];
        }
    };
}
