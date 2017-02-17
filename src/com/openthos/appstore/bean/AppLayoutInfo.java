package com.openthos.appstore.bean;

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

    public AppLayoutInfo(){
        appLayoutGridviewList = new ArrayList<>();
    }

    public AppLayoutInfo(List<AppLayoutGridviewInfo>
            appLayoutGridviewList) {
        this.appLayoutGridviewList = appLayoutGridviewList;
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

    @Override
    public String toString() {
        return "AppLayoutInfo{" +
                "type='" + type + '\'' +
                ", appLayoutGridviewList=" + appLayoutGridviewList +
                '}';
    }
}