package net.translives.app.bean.comment;

import android.text.TextUtils;

import net.translives.app.util.CollectionUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by haibin
 * on 2016/10/26.
 */

public class Image implements Serializable {
    private String href;
    private String thumb;
    private int w;
    private int h;
    private String type;
    private String name;

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static Image create(String href) {
        Image image = new Image();
        image.href = href;
        return image;
    }

    public static String[] getImagePath(Image[] images) {
        if (images == null || images.length == 0)
            return null;

        List<String> paths = new ArrayList<>();
        for (Image image : images) {
            if (check(image))
                paths.add(image.href);
        }

        return CollectionUtil.toArray(paths, String.class);
    }


    public static boolean check(Image image) {
        return image != null && !TextUtils.isEmpty(image.getHref());
    }
}
