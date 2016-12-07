package com.openthos.appstore.bean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luojunhuan on 16-10-27.
 */
public class AppTypeInfo {
    private int result;
    private String message;
    private String name;
    private List<AppTypeListviewInfo> list;

    public AppTypeInfo(JSONObject obj) throws JSONException {
        this.result = obj.getInt("result");
        this.message = obj.getString("message");
        this.name = obj.getString("name");
        this.list = new ArrayList<>();
        JSONArray info1 = obj.getJSONArray("date");
        AppTypeListviewInfo info = null;
        for (int i = 0; i < info1.length(); i++) {
            info = new AppTypeListviewInfo(info1.getJSONObject(i));
            list.add(info);
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AppTypeListviewInfo> getList() {
        return list;
    }

    public void setList(List<AppTypeListviewInfo> list) {
        this.list = list;
    }
}