package com.openthos.appstore.bean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljh on 16-11-30.
 */
public class DataInfo {
    private String message;
    private List<AppLayoutGridviewInfo> appList;

    public DataInfo(JSONObject obj) throws JSONException {
        message = obj.getString("message");
        appList = new ArrayList<>();
        AppLayoutGridviewInfo appInfo;
        JSONArray array = obj.getJSONArray("date");
        for (int i = 0; i < array.length(); i++) {
            appInfo = new AppLayoutGridviewInfo(array.getJSONObject(i));
            appList.add(appInfo);
        }
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<AppLayoutGridviewInfo> getAppList() {
        return appList;
    }
}