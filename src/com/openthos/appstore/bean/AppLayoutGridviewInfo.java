package com.openthos.appstore.bean;

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
    private String type;
    private int state;
    private String appPackageName;
    private String appUrl;
    private String download;
    private long size;
    private String version;

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
        this.version = obj.getString("version");
        this.download = obj.getString("download");
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getAppPackageName() {
        return appPackageName;
    }

    public void setAppPackageName(String appPackageName) {
        this.appPackageName = appPackageName;
    }

    public String getAppUrl() {
        return appUrl;
    }

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }

    public String getDownload() {
        return download;
    }

    public void setDownload(String download) {
        this.download = download;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "AppLayoutGridviewInfo{" +
                "download='" + download + '\'' +
                ", size=" + size +
                ", version='" + version + '\'' +
                ", appPackageName='" + appPackageName + '\'' +
                ", name='" + name + '\'' +
                ", id=" + id +
                '}';
    }
}
