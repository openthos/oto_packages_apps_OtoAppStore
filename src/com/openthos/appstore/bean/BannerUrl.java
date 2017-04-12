package com.openthos.appstore.bean;

import org.json.JSONException;
import org.json.JSONObject;

public class BannerUrl{
    private int id;
    private String imgUrl;

    public BannerUrl(JSONObject obj) throws JSONException {
        id = obj.getInt("id");
        imgUrl = obj.getString("imgUrl");
    }

    public BannerUrl(int id,String imgUrl){
        this.id = id;
        this.imgUrl = imgUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
