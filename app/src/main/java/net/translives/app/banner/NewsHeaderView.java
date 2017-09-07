package net.translives.app.banner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;

import net.translives.app.R;
import net.translives.app.bean.Banner;
import net.translives.app.news.NewsDetailActivity;
import net.translives.app.question.QuestionDetailActivity;
import net.translives.app.util.URLUtils;
import net.translives.app.widget.ViewNewsBanner;

import java.util.List;

/**
 * Created by haibin
 * on 2016/10/26.
 */

@SuppressLint("ViewConstructor")
public class NewsHeaderView extends HeaderView {
    private TextView mTitleTextView;

    public NewsHeaderView(Context context, RequestManager loader,String bannerCache) {
        super(context, loader, bannerCache);
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        //mBannerView.setTransformer(new ScaleTransform());
        mTitleTextView = (TextView) findViewById(R.id.tv_title);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_news_banner;
    }

    @Override
    public void onViewSelected(int position) {
        super.onViewSelected(position);
        if (mBanners.size() != 0)
            mTitleTextView.setText(mBanners.get(position % mBanners.size()).getName());
    }

    @Override
    void setBanners(List<Banner> banners) {
        super.setBanners(banners);
        if (banners.size() > 0 && mCurrentItem == 0) {
            mTitleTextView.setText(banners.get(0).getName());
        }
    }

    @Override
    public void onItemClick(int position) {
        Banner banner = mBanners.get(position);
        if (banner != null) {
            int type = banner.getType();
            long id = banner.getId();

            switch (type) {
                case Banner.BANNER_TYPE_NEWS:
                    NewsDetailActivity.show(getContext(), id);
                    break;
                case Banner.BANNER_TYPE_QUESTION:
                    //问答
                    QuestionDetailActivity.show(getContext(), id);
                    break;
                default:
                    URLUtils.parseUrl(getContext(),banner.getHref());
                    break;
            }
        }
    }

    @Override
    protected View instantiateItem(int position) {
        ViewNewsBanner view = new ViewNewsBanner(getContext());
        if (mBanners.size() != 0) {
            int p = position % mBanners.size();
            if (p >= 0 && p < mBanners.size()) {
                view.initData(mImageLoader, mBanners.get(p));
            }
        }
        return view;
    }
}
