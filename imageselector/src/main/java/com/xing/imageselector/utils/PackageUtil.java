package com.xing.imageselector.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class PackageUtil {
    private PackageUtil() {
        throw new UnsupportedOperationException("can't be initial");
    }

    /**
     * 获取应用的包名
     *
     * @param context
     * @return
     */
    public static String getPackageName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.packageName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
