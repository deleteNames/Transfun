package net.translives.app.bean;

import android.text.TextUtils;

import net.translives.app.bean.comment.Comment;
import net.translives.app.bean.simple.About;
import net.translives.app.bean.simple.Author;
import net.translives.app.bean.comment.Image;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by huanghaibin_dev
 * on 2016/7/18.
 */
public class Tweet implements Serializable {
    private String cacheKey;

    private long id;
    private String title;
    private String body;
    private String pubDate;
    private String href;
    //private int type;
    private boolean favorite;
    private Topic topic;
    private Author author;
    private Image[] images;
    private Tweet.Comment[] comments;
    private Statistics statistics;

    public Topic getTopic(){
        return topic;
    }

    public void setTopic(Topic topic){
        this.topic = topic;
    }

    public Image[] getImages() {
        return images;
    }

    public void setImages(Image[] images) {
        this.images = images;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }
    /*
        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

    */

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public void setStatistics(Statistics statistics) {
        this.statistics = statistics;
    }

    public String getKey() {
        if (cacheKey == null)
            cacheKey = String.format("new,id:%s",  getId() == 0 ? getHref().hashCode() : getId());
        return cacheKey;
    }

    public static class Statistics implements Serializable {
        private int comment;
        private int view;

        public int getComment() {
            return comment;
        }

        public void setComment(int comment) {
            this.comment = comment;
        }

        public int getView() {
            return view;
        }

        public void setView(int view) {
            this.view = view;
        }
    }




    public Tweet.Comment[] getComments() {
        return comments;
    }

    public void setComments(Tweet.Comment[] comments) {
        this.comments = comments;
    }

    public static class Comment implements Serializable {
        private String authorFrom;
        private String replyTo;
        private String content;

        public String getAuthorFrom() {
            return authorFrom;
        }

        public void setAuthorFrom(String authorFrom) {
            this.authorFrom = authorFrom;
        }

        public String getReplyTo() {
            return replyTo;
        }

        public void setReplyTo(String replyTo) {
            this.replyTo = replyTo;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}