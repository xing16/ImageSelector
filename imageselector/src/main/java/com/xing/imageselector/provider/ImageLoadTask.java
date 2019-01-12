package com.xing.imageselector.provider;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.xing.imageselector.entity.Folder;
import com.xing.imageselector.entity.Image;
import com.xing.imageselector.interfaces.OnImageLoadCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 从 sdcard 加载图片任务
 */
public class ImageLoadTask extends AsyncTask<Void, Void, ArrayList<Folder>> {

    private Context mContext;

    public ImageLoadTask(Context context) {
        this.mContext = context;
    }

    @Override
    protected ArrayList<Folder> doInBackground(Void... voids) {
        // 加载图片
        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver mContentResolver = mContext.getContentResolver();
        Cursor cursor = mContentResolver.query(mImageUri, new String[]{
                        MediaStore.Images.Media.DATA,
                        MediaStore.Images.Media.DISPLAY_NAME,
                        MediaStore.Images.Media.DATE_ADDED,
                        MediaStore.Images.Media._ID,
                        MediaStore.Images.Media.MIME_TYPE},
                null,
                null,
                MediaStore.Images.Media.DATE_ADDED);

        ArrayList<Image> images = new ArrayList<>();
        // 读取图片
        if (cursor != null) {
            while (cursor.moveToNext()) {
                // 获取图片的路径
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                //获取图片名称
                String name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                //获取图片时间
                long time = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
                //获取图片类型
                String mimeType = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE));
                //过滤未下载完成或者不存在的文件
                if (!"downloading".equals(getExtensionName(path)) && checkImgExists(path)) {
                    images.add(new Image(name, path, time, mimeType));
                }
            }
            cursor.close();
        }
        Collections.reverse(images);
        ArrayList<Folder> folderList = splitFolder(images);
        return folderList;
    }


    @Override
    protected void onPostExecute(ArrayList<Folder> folders) {
        super.onPostExecute(folders);
        if (onLoadImageCallback != null) {
            onLoadImageCallback.onSuccess(folders);
        }
    }

    /**
     * 检查图片是否存在。ContentResolver查询处理的数据有可能文件路径并不存在。
     *
     * @param filePath
     * @return
     */
    private static boolean checkImgExists(String filePath) {
        return new File(filePath).exists();
    }

    /**
     * 把图片按文件夹拆分，第一个文件夹保存所有的图片
     *
     * @param imageList
     * @return
     */
    private static ArrayList<Folder> splitFolder(ArrayList<Image> imageList) {
        ArrayList<Folder> folderList = new ArrayList<>();
        folderList.add(new Folder("全部图片", imageList));
        if (imageList != null && !imageList.isEmpty()) {
            int size = imageList.size();
            for (int i = 0; i < size; i++) {
                // 图片的路径
                String path = imageList.get(i).getPath();
                // 从该图片路径，获取该图片的父级文件夹名称
                String folderName = getFolderName(path);
                if (!TextUtils.isEmpty(folderName)) {
                    Folder folder = getFolder(folderName, folderList);
                    folder.addImage(imageList.get(i));
                }
            }
        }
        return folderList;
    }

    /**
     * Java文件操作 获取文件扩展名
     */
    public static String getExtensionName(String filename) {
        if (filename != null && filename.length() > 0) {
            int dot = filename.lastIndexOf('.');
            if (dot > -1 && dot < filename.length() - 1) {
                return filename.substring(dot + 1);
            }
        }
        return "";
    }

    /**
     * 根据图片路径，获取图片文件夹名称
     *
     * @param path
     * @return
     */
    private static String getFolderName(String path) {
        if (!TextUtils.isEmpty(path)) {
            String[] strings = path.split(File.separator);
            if (strings.length >= 2) {
                return strings[strings.length - 2];
            }
        }
        return "";
    }

    /**
     * 判断该图片的父级文件夹是否已经存在，不存在则创建并添加到文件夹集合中
     *
     * @param name
     * @param folders
     * @return
     */
    private static Folder getFolder(String name, List<Folder> folders) {
        if (!folders.isEmpty()) {
            int size = folders.size();
            for (int i = 0; i < size; i++) {
                Folder folder = folders.get(i);
                if (name.equals(folder.getName())) {
                    return folder;
                }
            }
        }
        Folder newFolder = new Folder(name);
        folders.add(newFolder);
        return newFolder;
    }

    OnImageLoadCallback onLoadImageCallback;

    public void setOnLoadImageCallback(OnImageLoadCallback callback) {
        this.onLoadImageCallback = callback;
    }
}
