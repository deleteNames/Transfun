package net.translives.app.notice;

import android.content.Context;

import net.translives.app.cache.SharedPreferencesHelper;

import java.io.Serializable;

/**
 * Created by JuQiu
 * on 16/8/19.
 * Note: like count always zero
 */
public class NoticeBean implements Serializable {
    private int notice;
    private int system;

    public int getNotice() {
        return notice;
    }

    void setNotice(int notice) {
        this.notice = notice;
    }

    public int getSystem() {
        return system;
    }

    void setSystem(int system) {
        this.system = system;
    }

    public int getAllCount() {
        return system + notice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NoticeBean that = (NoticeBean) o;

        return notice == that.notice
                && system == that.system;

    }


    void clear() {
        this.notice = 0;
        this.system = 0;
    }

    NoticeBean set(NoticeBean bean) {
        this.notice = bean.notice;
        this.system = bean.system;

        return this;
    }

    NoticeBean add(NoticeBean bean) {
        this.notice += bean.notice;
        this.system += bean.system;

        return this;
    }

    NoticeBean save(Context context) {
        SharedPreferencesHelper.save(context, this);
        return this;
    }

    static NoticeBean getInstance(Context context) {
        NoticeBean bean = SharedPreferencesHelper.load(context, NoticeBean.class);
        if (bean == null)
            bean = new NoticeBean();
        return bean;
    }
}
