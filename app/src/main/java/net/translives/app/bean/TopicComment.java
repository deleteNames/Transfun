package net.translives.app.bean;

import net.translives.app.bean.Topic;
import net.translives.app.bean.TopicActive;
import net.translives.app.bean.comment.Image;
import net.translives.app.bean.simple.Author;

import java.io.Serializable;


/**
 * Created by haibin
 * on 2016/10/26.
 */

public class TopicComment implements Serializable {

    private long id;
    private String content;
    private Image[] images;
    private String pubDate;
    private TopicActive active;
    private Author author;

    public TopicActive getActive(){
        return active;
    }

    public void setActive(TopicActive active){
        this.active = active;
    }

    public Image[] getImages() {
        return images;
    }

    public void setImages(Image[] images) {
        this.images = images;
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

}
