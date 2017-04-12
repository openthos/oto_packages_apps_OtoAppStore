package com.openthos.appstore.bean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BannerInfo {
    private int result;
    private String message;
    private List<BannerUrl> imageUrls;

    public BannerInfo(JSONObject obj) throws JSONException {
        result = obj.getInt("result");
        message = obj.getString("message");
        JSONArray array = obj.getJSONArray("data");
        imageUrls = new ArrayList<>();
        BannerUrl carouselFigureUrl = null;
        for (int i = 0; i < array.length(); i++) {
            carouselFigureUrl = new BannerUrl(array.getJSONObject(i));
            imageUrls.add(carouselFigureUrl);
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

    public List<BannerUrl> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<BannerUrl> imageUrls) {
        this.imageUrls = imageUrls;
    }
}
