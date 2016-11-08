package com.openthos.appstore.bean;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by luojunhuan on 16-10-27.
 */
public class AppTypeListviewInfo {
    private long id;
    private String content;

    public AppTypeListviewInfo(long id, String content) {
        this.id = id;
        this.content = content;
    }

    public AppTypeListviewInfo(JSONObject obj) throws JSONException {
        this.id = obj.getLong("id");
        this.content = obj.getString("content");
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}