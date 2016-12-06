package com.openthos.appstore.bean;

import com.openthos.appstore.MainActivity;
import com.openthos.appstore.app.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by luojunhuan on 16-10-26.
 */
public class AppLayoutGridviewInfo implements Serializable {
    private long id;
    private String iconUrl;
    private String name;
    private String appPackageName;
    private String downloadUrl;
    private long fileSize;
    private String versionName;
    private int state;
    private String type;
    private long downFileSize;
    private String comment;

    public long getDownFileSize() {
        return downFileSize;
    }

    public void setDownFileSize(long downFileSize) {
        this.downFileSize = downFileSize;
    }

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
        this.fileSize = obj.getLong("size");
        this.iconUrl = obj.getString("icon");
        this.name = obj.getString("name");
        this.appPackageName = obj.getString("packagename");
        this.versionName = obj.getString("version");
        this.downloadUrl = obj.getString("download");
        this.comment = obj.optString("comment");
        if (MainActivity.mAppPackageInfo != null && MainActivity.mDownloadStateMap != null) {
            this.state = setState();
        }
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

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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
        Map<String, Integer> downloadStateMap = MainActivity.mDownloadStateMap;
        if (downloadStateMap.containsKey(id + "")) {
            return downloadStateMap.get(id + "");
        } else {
            return Constants.APP_NOT_INSTALL;
        }
    }

    public int getProgress() {
        if (fileSize == 0) {
            return 0;
        } else {
            return ((int) (100 * downFileSize / fileSize));
        }
    }
}
