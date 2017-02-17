package com.openthos.appstore.bean;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by luojunhuan on 16-10-27.
 */
public class AppTypeListviewInfo {
    private long id;
    private String type;

    public AppTypeListviewInfo(JSONObject obj) throws JSONException {
        this.id = obj.getLong("id");
        this.type = obj.getString("type");
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}