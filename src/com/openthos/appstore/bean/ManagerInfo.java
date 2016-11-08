package com.openthos.appstore.bean;

/**
 * Created by luojunhuan on 16-11-1.
 */
public class ManagerInfo {
    private long id;
    private String iconUrl;
    private String appName;
    private String appVersion;
    private String appContent;

    public ManagerInfo(long id, String iconUrl, String appName,
                       String appVersion, String appContent) {
        this.id = id;
        this.iconUrl = iconUrl;
        this.appName = appName;
        this.appVersion = appVersion;
        this.appContent = appContent;
    }

    public ManagerInfo() {
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

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getAppContent() {
        return appContent;
    }

    public void setAppContent(String appContent) {
        this.appContent = appContent;
    }
}