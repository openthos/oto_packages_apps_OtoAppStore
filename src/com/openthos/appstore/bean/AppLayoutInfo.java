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
    private List<AppLayoutGridviewInfo> appLayoutGridviewInfos;

    public AppLayoutInfo(long id, String type, List<AppLayoutGridviewInfo>
            appLayoutGridviewInfos) {
        this.id = id;
        this.type = type;
        this.appLayoutGridviewInfos = appLayoutGridviewInfos;
    }

    public AppLayoutInfo(JSONObject obj) throws JSONException {
        this.id = obj.getLong("id");
        this.type = obj.getString("type");
        this.appLayoutGridviewInfos = new ArrayList<>();
        JSONArray info = obj.getJSONArray("info");
        AppLayoutGridviewInfo leftGridviewInfo = null;
        for (int i = 0; i < info.length(); i++) {
            leftGridviewInfo = new AppLayoutGridviewInfo(info.getJSONObject(i));
            this.appLayoutGridviewInfos.add(leftGridviewInfo);
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

    public List<AppLayoutGridviewInfo> getAppLayoutGridviewInfos() {
        return appLayoutGridviewInfos;
    }

    public void setAppLayoutGridviewInfos(List<AppLayoutGridviewInfo>
                                                  appLayoutGridviewInfos) {
        this.appLayoutGridviewInfos = appLayoutGridviewInfos;
    }
}
