package org.openthos.appstore.bean;

import org.json.JSONException;
import org.json.JSONObject;

public class CommentInfo {
    private long id;
    private String content;
    private int star;
    private String commentPerson;
    private String time;

    public CommentInfo(long id, String content, int star, String commentPerson, String time) {
        this.id = id;
        this.content = content;
        this.star = star;
        this.commentPerson = commentPerson;
        this.time = time;
    }

    public CommentInfo(JSONObject obj) throws JSONException {
        id = obj.getLong("id");
        content = obj.getString("content");
        star = obj.getInt("star");
        commentPerson = obj.getString("commentPerson");
        time = obj.getString("time");
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStar() {
        return star;
    }

    public String getTime() {
        return time;
    }
}