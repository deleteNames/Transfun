package net.translives.app.bean;

import net.translives.app.bean.simple.Author;

import java.io.Serializable;

/**
 * Created by haibin
 * on 2016/10/18.
 */

public class Collection implements Serializable {
    public static final String TYPE_NEWS = "news";
    public static final String TYPE_QUESTION = "question";
    public static final String TYPE_SHARE = "share";
    public static final String TYPE_ANSWER = "answer";

    private long id;
    private String type;
    private String title;
    private String href;
    private Author author;
    private String favDate;
    private int favCount;
    private String content;
    private int commentCount;
    private boolean favorite;

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

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getFavDate() {
        return favDate;
    }

    public void setFavDate(String favDate) {
        this.favDate = favDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }
/*



    public int getFavCount() {
        return favCount;
    }

    public void setFavCount(int favCount) {
        this.favCount = favCount;
    }



    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }
*/
    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
