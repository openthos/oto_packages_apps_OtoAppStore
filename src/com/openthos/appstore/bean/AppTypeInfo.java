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
    private long id;
    private String name;
    private List<AppTypeListviewInfo> list;

    public AppTypeInfo(long id, String name, List<AppTypeListviewInfo> list) {
        this.id = id;
        this.name = name;
        this.list = list;
    }

    public AppTypeInfo(JSONObject obj) throws JSONException {
        this.id = obj.getLong("id");
        this.name = obj.getString("name");
        this.list = new ArrayList<>();
        JSONArray info1 = obj.getJSONArray("info");
        AppTypeListviewInfo info = null;
        for (int i = 0; i < info1.length(); i++) {
            info = new AppTypeListviewInfo(info1.getJSONObject(i));
            list.add(info);
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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