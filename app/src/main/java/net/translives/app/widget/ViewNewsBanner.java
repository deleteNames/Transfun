package net.translives.app.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.RequestManager;

import net.translives.app.R;
import net.translives.app.bean.Banner;
import net.translives.app.news.NewsDetailActivity;

/**
 * Created by huanghaibin
 * on 16-5-23.
 */
public class ViewNewsBanner extends RelativeLayout implements View.OnClickListener {
    private Banner banner;
    private ImageView iv_banner;

    public ViewNewsBanner(Context context) {
        super(context, null);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_news_banner, this, true);
        iv_banner = (ImageView) findViewById(R.id.iv_banner);
        setOnClickListener(this);
    }

    public void initData(RequestManager manager, Banner banner) {
        this.banner = banner;
        manager.load(banner.getImg()).into(iv_banner);
    }

    @Override
    public void onClick(View v) {
        if (banner != null) {
            //int type = banner.getType();
            long id = banner.getId();
            //UIHelper.showDetail(getContext(), type, id, banner.getHref());
            NewsDetailActivity.show(getContext(), id);
        }
    }


    public String getTitle() {
        return banner.getName();
    }
}
