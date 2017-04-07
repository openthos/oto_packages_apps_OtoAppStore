package com.openthos.appstore.bean;

import com.openthos.appstore.app.Constants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by root on 3/14/17.
 */

public class AppItemInfo {
    private String taskId;
    private long fileSize;
    private String packageName;
    private String appName;
    private String versionName;
    private int versionCode;
    private String downloadUrl;
    private String iconUrl;
    private String describle;
    private String type;
    private String company;
    private float star;
    private int state;
    private int progress;

    public AppItemInfo(JSONObject obj) throws JSONException {
        taskId = obj.getString("taskId");
        fileSize = obj.getLong("fileSize");
        packageName = obj.getString("packageName");
        appName = obj.getString("appName");
        versionName = obj.getString("versionName");
        versionCode = obj.getInt("versionCode");
        downloadUrl = obj.getString("downloadUrl");
        iconUrl = obj.getString("iconUrl");
        describle = obj.getString("describle");
        type = obj.getString("type");
        company = obj.getString("company");
        star = (float) obj.getDouble("star");
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getDescrible() {
        return describle;
    }

    public void setDescrible(String describle) {
        this.describle = describle;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public float getStar() {
        return star;
    }

    public void setStar(float star) {
        this.star = star;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        if (state != Constants.APP_NOT_EXIST) {
            this.state = state;
        }
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    @Override
    public String toString() {
        return "NetDataInfo{" +
                "taskId=" + taskId +
                ", fileSize=" + fileSize +
                ", packageName='" + packageName + '\'' +
                ", appName='" + appName + '\'' +
                ", versionName='" + versionName + '\'' +
                ", versionCode=" + versionCode +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                ", describle='" + describle + '\'' +
                ", type='" + type + '\'' +
                ", company='" + company + '\'' +
                '}';
    }
}
