package com.openthos.appstore.bean;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by luojunhuan on 16-11-8.
 */
public class HomeDataInfo {

    private int result ;
    private String message;
    private HomeDataAppLayoutInfo appLayoutInfo;

    public HomeDataInfo(JSONObject obj) throws JSONException {
        result = obj.getInt("result");
        message = obj.getString("message");
        JSONObject data = obj.getJSONObject("date");
        appLayoutInfo = new HomeDataAppLayoutInfo(data);
    }

    public int getResult() {
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

    public HomeDataAppLayoutInfo getAppLayoutInfo() {
        return appLayoutInfo;
    }

    public void setAppLayoutInfo(HomeDataAppLayoutInfo appLayoutInfo) {
        this.appLayoutInfo = appLayoutInfo;
    }

    @Override
    public String toString() {
        return "HomeDataInfo{" +
                "result=" + result +
                ", message='" + message + '\'' +
                ", appLayoutInfo=" + appLayoutInfo +
                '}';
    }
}
