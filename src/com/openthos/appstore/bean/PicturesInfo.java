package com.openthos.appstore.bean;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ljh on 16-12-7.
 */
public class PicturesInfo {
    private int id;

    public PicturesInfo(JSONObject obj) throws JSONException {
        this.id = obj.getInt("id");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
