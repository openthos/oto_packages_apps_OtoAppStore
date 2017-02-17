package com.openthos.appstore.bean;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ljh on 16-12-7.
 */
public class DetailInfo {
    private String message;
    private DetailContentInfo detailContentInfo;

    public DetailInfo(JSONObject obj) throws JSONException {
        this.message = obj.getString("message");
        JSONObject data = obj.getJSONObject("data");
        if (data != null) {
            this.detailContentInfo = new DetailContentInfo(data);
        }
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DetailContentInfo getDetailContentInfo() {
        return detailContentInfo;
    }
}
