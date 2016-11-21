package com.openthos.appstore.bean;

import com.openthos.appstore.MainActivity;
import com.openthos.appstore.app.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by luojunhuan on 16-10-26.
 */
public class AppLayoutGridviewInfo implements Serializable {
    private long id;
    private String iconUrl;
    private String name;
    private String appPackageName;
    private String downloadUrl;
    private long size;
    private String versionName;
    private int state;
    private String type;

    public AppLayoutGridviewInfo(long id, String iconUrl, String name,
                                 String type, int state) {
        this.id = id;
        this.iconUrl = iconUrl;
        this.name = name;
        this.type = type;
        this.state = state;
    }

    public AppLayoutGridviewInfo(JSONObject obj) throws JSONException {
        this.id = obj.getLong("id");
        this.size = obj.getLong("size");
        this.iconUrl = obj.getString("icon");
        this.name = obj.getString("name");
        this.appPackageName = obj.getString("packagename");
        this.versionName = obj.getString("version");
        this.downloadUrl = obj.getString("download");
        this.state = setState();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAppPackageName() {
        return appPackageName;
    }

    public void setAppPackageName(String appPackageName) {
        this.appPackageName = appPackageName;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private int setState() {
        for (SQLAppInstallInfo sqlAppInstallInfo : MainActivity.mAppPackageInfo) {
            if (appPackageName.equals(sqlAppInstallInfo.getPackageName())) {
                if (versionName.equals(sqlAppInstallInfo.getVersionName())) {
                    return Constants.APP_HAVE_INSTALLED;
                } else {
                    return Constants.APP_NEED_UPDATE;
                }
            }
        }
        return Constants.APP_NOT_INSTALL;
    }
}
