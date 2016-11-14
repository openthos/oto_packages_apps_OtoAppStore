package com.openthos.appstore.bean;

import com.openthos.appstore.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luojunhuan on 16-10-26.
 */
public class HomeDataAppLayoutInfo implements Serializable {
    private long id;
    private String type;
    private AppLayoutInfo appLayoutSoftwareInfo;
    private AppLayoutInfo appLayoutGameInfo;

    public HomeDataAppLayoutInfo(JSONObject obj) throws JSONException {
        JSONArray software = obj.optJSONArray("software");
        if (software != null) {
            appLayoutSoftwareInfo = new AppLayoutInfo();
            appLayoutSoftwareInfo.setType("software");
            for (int i = 0; i < software.length(); i++) {
                appLayoutSoftwareInfo.getAppLayoutGridviewList().
                        add(new AppLayoutGridviewInfo(software.getJSONObject(i)));
            }
        }
        JSONArray game = obj.optJSONArray("game");
        if (game != null) {
            appLayoutGameInfo = new AppLayoutInfo();
            appLayoutGameInfo.setType("game");
            for (int i = 0; i < game.length(); i++) {
                appLayoutGameInfo.getAppLayoutGridviewList().
                        add(new AppLayoutGridviewInfo(game.getJSONObject(i)));
            }
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

    public AppLayoutInfo getAppLayoutSoftwareInfo() {
        return appLayoutSoftwareInfo;
    }

    public void setAppLayoutSoftwareInfo(AppLayoutInfo appLayoutSoftwareInfo) {
        this.appLayoutSoftwareInfo = appLayoutSoftwareInfo;
    }

    public AppLayoutInfo getAppLayoutGameInfo() {
        return appLayoutGameInfo;
    }

    public void setAppLayoutGameInfo(AppLayoutInfo appLayoutGameInfo) {
        this.appLayoutGameInfo = appLayoutGameInfo;
    }

    @Override
    public String toString() {
        return "HomeDataAppLayoutInfo{" +
                "appLayoutSoftwareInfo=" + appLayoutSoftwareInfo +
                ", appLayoutGameInfo=" + appLayoutGameInfo +
                '}';
    }
}