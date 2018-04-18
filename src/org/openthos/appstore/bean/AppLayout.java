package org.openthos.appstore.bean;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AppLayout {
    private int result;
    private String message;
    private List<AppItemLayoutInfo> appItemLayoutInfos;

    public AppLayout(JSONObject obj, Context context) throws JSONException {
        result = obj.getInt("result");
        message = obj.getString("message");
        appItemLayoutInfos = new ArrayList<>();
        AppItemLayoutInfo itemLayoutInfo = null;
        JSONArray arr = obj.getJSONArray("data");
        for (int i = 0; i < arr.length(); i++) {
            itemLayoutInfo = new AppItemLayoutInfo(arr.getJSONObject(i), context);
            appItemLayoutInfos.add(itemLayoutInfo);
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

    public List<AppItemLayoutInfo> getAppItemLayoutInfos() {
        return appItemLayoutInfos;
    }

    public void setAppItemLayoutInfos(List<AppItemLayoutInfo> appItemLayoutInfos) {
        this.appItemLayoutInfos = appItemLayoutInfos;
    }
}
