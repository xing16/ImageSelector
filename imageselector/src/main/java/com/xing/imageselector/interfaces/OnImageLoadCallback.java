package com.xing.imageselector.interfaces;

import com.xing.imageselector.entity.Folder;

import java.util.ArrayList;

public interface OnImageLoadCallback {
    void onSuccess(ArrayList<Folder> folders);
}
