package com.openthos.appstore.bean;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ljh on 16-12-7.
 */
public class PicturesInfo {
    private int id;
    private String picUrl;
    private String linkUrl;

    public PicturesInfo(JSONObject obj) throws JSONException {
        this.id = obj.getInt("id");
        this.picUrl = obj.getString("picUrl");
        this.linkUrl = obj.optString("linkUrl");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }
}
