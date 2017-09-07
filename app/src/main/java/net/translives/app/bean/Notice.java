package net.translives.app.bean;

import net.translives.app.bean.comment.Image;
import net.translives.app.bean.simple.Author;

import java.io.Serializable;

public class Notice implements Serializable {
    public static final int TYPE_REPLY = 1;
    public static final int TYPE_POST = 2;
    public static final int TYPE_LIKE = 3;
    public static final int TYPE_COMMENT = 4;

    public static final int SOURCE_TYPE_ANSWER = 10;
    public static final int SOURCE_TYPE_QUESTION = 11;
    public static final int SOURCE_TYPE_SHARE = 12;
    public static final int SOURCE_TYPE_TOPIC = 13;
    public static final int SOURCE_TYPE_COMMENT = 14;

    private long id;
    private int type;
    private Source source;
    private String action;
    private String content;
    private Image[] images;
    private boolean read;
    private String pubDate;
    private Author author;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Image[] getImages() {
        return images;
    }

    public void setImages(Image[] images) {
        this.images = images;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public static class Source implements Serializable {
        private long id;
        private String title;
        private int type;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    @Override
    public String toString() {
        return "Notice{" + "id=" + id +
                ", type='" + type + '\'' +
                ", author='" + author + '\'' +
                ", content='" + content +'\'' +
                ", pubDate='" + pubDate + '\'' +
                ", source=" + source +
                '}';
    }
}
