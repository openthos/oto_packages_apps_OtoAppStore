package com.openthos.appstore.bean;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.Serializable;
import org.json.JSONArray;

/**
 * Created by cyr on 16-11-15.
 */
public class GameLayoutInfo implements Serializable {

    private int mId;
    private String mType;
    private AppLayoutInfo mAppLayoutInfo;

    public GameLayoutInfo(JSONArray obj) throws JSONException {
        if (obj != null) {
            mAppLayoutInfo = new AppLayoutInfo();
            mAppLayoutInfo.setType("softerType");
            for (int i = 0 ; i< obj.length() ; i ++) {
                mAppLayoutInfo.getAppLayoutGridviewList().
                        add(new AppLayoutGridviewInfo(obj.getJSONObject(i)));
            }
        }
    }

    public int  getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public AppLayoutInfo getAppLayoutInfo() {
        return mAppLayoutInfo;
    }

    public void setAppLayoutInfo(AppLayoutInfo appLayoutInfo) {
        mAppLayoutInfo = appLayoutInfo;
    }
}
