package com.openthos.appstore.bean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljh on 16-11-30.
 */
public class AllDataInfo {
    private int result;
    private String message;
    private List<AppLayoutGridviewInfo> appList;

    public AllDataInfo(JSONObject obj) throws JSONException {
        result = obj.getInt("result");
        message = obj.getString("message");
        appList = new ArrayList<>();
        AppLayoutGridviewInfo appInfo;
        JSONArray array = obj.getJSONArray("date");
        for (int i = 0; i < array.length(); i++) {
            appInfo = new AppLayoutGridviewInfo(array.getJSONObject(i));
            appList.add(appInfo);
        }
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

    public List<AppLayoutGridviewInfo> getAppList() {
        return appList;
    }

    public void setAppList(List<AppLayoutGridviewInfo> appList) {
        this.appList = appList;
    }
}