package net.translives.app.bean;

import android.text.TextUtils;

import net.translives.app.bean.simple.About;
import net.translives.app.bean.simple.Author;
import net.translives.app.bean.comment.Image;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by fei on 2016/5/24.
 * desc:  question bean
 */
public class Question implements Serializable {
    private String cacheKey;

    public static final int TYPE_SHARE = 1;
    public static final int TYPE_QUESTION = 2;

    private long id;
    private int type;
    private String title;
    private String body;
    private String pubDate;
    private String href;
    //private int type;
    private boolean favorite;
    private boolean follow;
    private String summary;
    private Author author;
    private Image[] images;
    private HashMap<String, Object> extra;
    //private String[] tags;
    private Question.Statistics statistics;
    private ArrayList<About> abouts;

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
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

    public boolean isFollow() {
        return follow;
    }

    public void setFollow(boolean follow) {
        this.follow = follow;
    }

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

    public HashMap<String, Object> getExtra() {
        return extra;
    }

    public void setExtra(HashMap<String, Object> extra) {
        this.extra = extra;
    }
    /*
        public ArrayList<About> getAbouts() {
            return abouts;
        }

        public void setAbouts(ArrayList<About> abouts) {
            this.abouts = abouts;
        }


        public String[] getTags() {
            return tags;
        }

        public void setTags(String[] tags) {
            this.tags = tags;
        }
    */
    public Question.Statistics getStatistics() {
        return statistics;
    }

    public void setStatistics(Question.Statistics statistics) {
        this.statistics = statistics;
    }

    public String getKey() {
        if (cacheKey == null)
            cacheKey = String.format("question,id:%s",  getId() == 0 ? getHref().hashCode() : getId());
        return cacheKey;
    }

    public static class Statistics implements Serializable {
        private int comment;
        private int view;
        private int follow;

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

        public int getFollow() {
            return follow;
        }

        public void setFollow(int follow) {
            this.follow = follow;
        }

    }
}
