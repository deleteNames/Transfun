package net.translives.app.banner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import net.translives.app.AppContext;
import net.translives.app.R;
import net.translives.app.banner.HeaderView;
import net.translives.app.bean.Banner;
import net.translives.app.event.EventDetailActivity;
import net.translives.app.event.EventListActivity;
import net.translives.app.news.NewsDetailActivity;
import net.translives.app.question.QuestionDetailActivity;
import net.translives.app.topic.TopicActiveDetailActivity;
import net.translives.app.topic.TopicActiveFollowActivity;
import net.translives.app.topic.TopicCategoryActivity;
import net.translives.app.util.URLUtils;
import net.translives.app.vote.VoteDetailActivity;
import net.translives.app.vote.VoteListActivity;
import net.translives.app.widget.ViewNewsBanner;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by haibin
 * on 2016/10/26.
 */

@SuppressLint("ViewConstructor")
public class TopicHeader extends HeaderView implements View.OnClickListener {

    private TextView mTitleTextView;
    public Context mContext;

    public TopicHeader(Context context, RequestManager loader,String bannerCache) {
        super(context, loader, bannerCache);
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        mContext = context;
        //mBannerView.setTransformer(new ScaleTransform());
        //mTitleTextView = (TextView) findViewById(R.id.tv_title);

        View mfollow = (View) findViewById(R.id.t_menu_follow);
        mfollow.setOnClickListener(this);
        View mvote = (View) findViewById(R.id.t_menu_vote);
        mvote.setOnClickListener(this);
        View mtopic = (View) findViewById(R.id.t_menu_topic);
        mtopic.setOnClickListener(this);
        View mactivity = (View) findViewById(R.id.t_menu_activity);
        mactivity.setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_topic_header;
    }

    @Override
    public void onViewSelected(int position) {
        super.onViewSelected(position);
        //if (mBanners.size() != 0)
        //    mTitleTextView.setText(mBanners.get(position % mBanners.size()).getName());
    }

    @Override
    void setBanners(List<Banner> banners) {
        super.setBanners(banners);
        //if (banners.size() > 0 && mCurrentItem == 0) {
        //    mTitleTextView.setText(banners.get(0).getName());
        //}
    }

    @Override
    public void onItemClick(int position) {
        Banner banner = mBanners.get(position);
        if (banner != null) {
            int type = banner.getType();
            long id = banner.getId();

            switch (type) {
                case Banner.BANNER_TYPE_TOPIC:
                    TopicActiveDetailActivity.show(getContext(), id);
                    break;
                case Banner.BANNER_TYPE_EVENT:
                    EventDetailActivity.show(getContext(), id);
                    break;
                case Banner.BANNER_TYPE_VOTE:
                    VoteDetailActivity.show(getContext(), id);
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

    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.t_menu_follow:
                TopicActiveFollowActivity.show(getContext());
                break;
            case R.id.t_menu_vote:
                VoteListActivity.show(getContext());
                break;
            case R.id.t_menu_topic:
                TopicCategoryActivity.show(getContext());
                break;
            case R.id.t_menu_activity:
                EventListActivity.show(getContext());
                break;
        }

    }
}
