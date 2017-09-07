package net.translives.app.bean;

import java.io.Serializable;

/**
 * Created by haibin
 * on 2016/10/18.
 */

public class Follow implements Serializable {
    public static final String TYPE_USER = "user";
    public static final String TYPE_QUESTION = "qa";

    private long id;
    private String type;
    private String title;
    private boolean follow;
    private String followDate;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFollowDate() {
        return followDate;
    }

    public void setFollowDate(String followDate) {
        this.followDate = followDate;
    }

    public boolean isFollow() {
        return follow;
    }

    public void setFollow(boolean follow) {
        this.follow = follow;
    }
}
