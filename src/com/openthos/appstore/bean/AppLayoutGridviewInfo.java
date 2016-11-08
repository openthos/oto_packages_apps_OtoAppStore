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
        this.iconUrl = obj.getString("iconUrl");
        this.name = obj.getString("name");
        this.type = obj.getString("type");
        this.appPackageName = obj.getString("appPackageName");
        this.appUrl = obj.getString("appUrl");
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
}