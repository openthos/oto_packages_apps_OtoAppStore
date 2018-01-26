package com.openthos.appstore.bean;

import com.openthos.appstore.app.Constants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by root on 3/14/17.
 */

public class AppItemInfo {
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
    private String filePath;
    private boolean isOnDownloading;
    private String fileName;
    private long downFileSize = 0L;
    private int downloadState;
    private long speed;
    private String userID;
    private String url;
    private boolean isSuccess;

    public AppItemInfo(JSONObject obj) throws JSONException {
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

    public AppItemInfo() {
    }

    public String getTaskId() {
        return packageName;
    }

    public void setTaskId(String taskId) {
        this.packageName = taskId;
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
        if (fileSize == 0) {
            return 0;
        } else {
            return ((int) (100 * downFileSize / fileSize));
        }
    }

    public int getSqlProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    @Override
    public String toString() {
        return "NetDataInfo{" +
                "taskId=" + packageName +
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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean isOnDownloading() {
        return isOnDownloading;
    }

    public void setOnDownloading(boolean onDownloading) {
        isOnDownloading = onDownloading;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getDownFileSize() {
        return downFileSize;
    }

    public void setDownFileSize(long downFileSize) {
        this.downFileSize = downFileSize;
    }

    public int getDownloadState() {
        return downloadState;
    }

    public void setDownloadState(int downloadState) {
        this.downloadState = downloadState;
    }

    public long getSpeed() {
        return speed;
    }

    public void setSpeed(long speed) {
        this.speed = speed;
    }

    public boolean isSuccess() {
        return fileSize == downFileSize;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
