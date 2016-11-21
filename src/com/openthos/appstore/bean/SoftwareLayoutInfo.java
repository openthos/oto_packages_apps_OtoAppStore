package com.openthos.appstore.bean;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.Serializable;
import org.json.JSONArray;

/**
 * Created by cyr on 16-11-17.
 */

public class SoftwareLayoutInfo implements Serializable {

    private int id;
    private String type;
    private AppLayoutInfo appLayoutInfo;

    public SoftwareLayoutInfo(JSONArray obj) throws JSONException {
        if (obj != null) {
            appLayoutInfo = new AppLayoutInfo();
            appLayoutInfo.setType("softerType");
            for (int i = 0 ; i < obj.length() ; i ++) {
                appLayoutInfo.getAppLayoutGridviewList().add(new
                              AppLayoutGridviewInfo(obj.getJSONObject(i)));
            }
        }
    }

    public int  getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public AppLayoutInfo getAppLayoutInfo() {
        return appLayoutInfo;
    }

    public void setAppLayoutInfo(AppLayoutInfo appLayoutInfo) {
        this.appLayoutInfo = appLayoutInfo;
    }
}
