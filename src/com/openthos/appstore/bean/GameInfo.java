package com.openthos.appstore.bean;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

/**
 * Created by cyr on 16-11-15.
 */

public class GameInfo {

    private int mResult;
    private String mMessage;
    private GameLayoutInfo mGameLayoutInfo;

    public GameInfo(JSONObject obj) throws JSONException {
        mResult = obj.getInt("result");
        mMessage = obj.getString("message");
        JSONArray data = obj.getJSONArray("date");
        mGameLayoutInfo = new GameLayoutInfo(data);
    }

    public int  getResult() {
        return mResult;
    }

    public void setResult(int result) {
        mResult = result;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public GameLayoutInfo getGameLayoutInfo() {
        return mGameLayoutInfo;
    }

    public void setGameLayoutInfo(GameLayoutInfo gameLayoutInfo) {
        mGameLayoutInfo = gameLayoutInfo;
    }
}
