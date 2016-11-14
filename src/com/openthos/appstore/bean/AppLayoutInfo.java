package com.openthos.appstore.bean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luojunhuan on 16-10-26.
 */
public class AppLayoutInfo implements Serializable {
    private long id;
    private String type;
    private List<AppLayoutGridviewInfo> appLayoutGridviewList;

    public AppLayoutInfo(long id, String type, List<AppLayoutGridviewInfo>
            appLayoutGridviewList) {
        this.id = id;
        this.type = type;
        this.appLayoutGridviewList = appLayoutGridviewList;
    }

    public AppLayoutInfo() {
        appLayoutGridviewList = new ArrayList<>();
    }

    public AppLayoutInfo(JSONObject obj) throws JSONException {
        this.id = obj.getLong("id");
        this.type = obj.getString("type");
        this.appLayoutGridviewList = new ArrayList<>();
        JSONArray info = obj.getJSONArray("info");
        AppLayoutGridviewInfo leftGridviewInfo = null;
        for (int i = 0; i < info.length(); i++) {
            leftGridviewInfo = new AppLayoutGridviewInfo(info.getJSONObject(i));
            this.appLayoutGridviewList.add(leftGridviewInfo);
        }
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

    public List<AppLayoutGridviewInfo> getAppLayoutGridviewList() {
        return appLayoutGridviewList;
    }

    public void setAppLayoutGridviewList(List<AppLayoutGridviewInfo>
                                                  appLayoutGridviewInfos) {
        this.appLayoutGridviewList = appLayoutGridviewInfos;
    }

    @Override
    public String toString() {
        return "AppLayoutInfo{" +
                "type='" + type + '\'' +
                ", appLayoutGridviewList=" + appLayoutGridviewList +
                '}';
    }
}
