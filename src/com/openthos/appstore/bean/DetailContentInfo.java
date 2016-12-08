package com.openthos.appstore.bean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljh on 16-12-7.
 */
public class DetailContentInfo {
    private long id;
    private String iconUrl;
    private String downloadUrl;
    private String packageName;
    private String versionName;
    private int versionCode;
    private String name;
    private String company;
    private String type;
    private int star;
    private String content;
    private String promulgator;
    private long fileSize;
    private List<CommentInfo> commentInfoList;
    private List<PicturesInfo> picInfoList;

    public DetailContentInfo(JSONObject obj) throws JSONException {
        this.id = obj.getLong("id");
        this.iconUrl = obj.getString("iconUrl");
        this.downloadUrl = obj.getString("downloadUrl");
        this.packageName = obj.getString("packageName");
        this.versionName = obj.getString("versionName");
        this.versionCode = obj.optInt("versionCode");
        this.name = obj.getString("name");
        this.company = obj.getString("company");
        this.type = obj.getString("type");
        this.star = obj.getInt("star");
        this.content = obj.getString("content");
        this.promulgator = obj.getString("promulgator");
        this.fileSize = obj.getLong("size");
        commentInfoList = new ArrayList<>();
        JSONArray comment = obj.getJSONArray("comment");
        if (comment != null) {
            for (int i = 0; i < comment.length(); i++) {
                CommentInfo commentInfo = new CommentInfo(comment.getJSONObject(i));
                commentInfoList.add(commentInfo);
            }
        }

        picInfoList = new ArrayList<>();
        JSONArray pictures = obj.getJSONArray("pictures");
        if (pictures != null) {
            for (int i = 0; i < pictures.length(); i++) {
                picInfoList.add(new PicturesInfo(pictures.getJSONObject(i)));
            }
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPromulgator() {
        return promulgator;
    }

    public void setPromulgator(String promulgator) {
        this.promulgator = promulgator;
    }

    public String getFileSize() {
        if (fileSize < 1024) {
            return fileSize + "b";
        } else if (fileSize < 1024 * 1024) {
            return (fileSize * 100 / 1024) / 100.0 + "Kb";
        } else {
            return (fileSize * 100 / (1024 * 1024)) / 100.0 + "Mb";
        }
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public List<CommentInfo> getCommentInfoList() {
        return commentInfoList;
    }

    public void setCommentInfoList(List<CommentInfo> commentInfoList) {
        this.commentInfoList = commentInfoList;
    }

    public List<PicturesInfo> getPicInfoList() {
        return picInfoList;
    }

    public void setPicInfoList(List<PicturesInfo> picInfoList) {
        this.picInfoList = picInfoList;
    }
}
