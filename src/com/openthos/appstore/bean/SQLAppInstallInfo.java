package com.openthos.appstore.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by luojunhuan on 16-11-3.
 */
public class SQLAppInstallInfo {
    private long _id;
    private Drawable icon;
    private String versionCode;
    private String packageName;
    private String versionName;
    private String name;

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return _id;
    }

    public void setId(long _id) {
        this._id = _id;
    }

    @Override
    public String toString() {
        return "SQLAppInstallInfo{" +
                "_id=" + _id +
                ", icon=" + icon +
                ", versionCode=" + versionCode +
                ", packageName='" + packageName + '\'' +
                ", versionName='" + versionName + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}