package com.qa.myextrackapk;

import android.graphics.drawable.Drawable;

/**
 * Created by sev_user on 18-Mar-15.
 */
public class AppDetail {

    String appName;
    long appSize;
    Drawable appIcon;

    String path;
    String pkg;

    public AppDetail(String appName, long appSize, Drawable appIcon, String path, String pkg) {
        this.appName = appName;
        this.appSize = appSize;
        this.appIcon = appIcon;
        this.path = path;
        this.pkg = pkg;
    }
}
