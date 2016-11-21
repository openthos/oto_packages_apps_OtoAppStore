package com.openthos.appstore.bean;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

/**
 * Created by cyr on 16-11-17.
 */

public class SoftwareInfo {

    private int result;
    private String message;
    private SoftwareLayoutInfo softwareLayoutInfo;

    public SoftwareInfo(JSONObject obj) throws JSONException {
        result = obj.getInt("result");
        message = obj.getString("message");
        JSONArray data = obj.getJSONArray("date");
        softwareLayoutInfo = new SoftwareLayoutInfo(data);
    }

    public int  getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public SoftwareLayoutInfo getSoftwareLayoutInfo() {
        return softwareLayoutInfo;
    }

    public void setSoftwareLayoutInfo(SoftwareLayoutInfo softwareLayoutInfo) {
        this.softwareLayoutInfo = softwareLayoutInfo;
    }
}
