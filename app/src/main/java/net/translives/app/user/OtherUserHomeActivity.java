package net.translives.app.user;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.translives.app.R;
import net.translives.app.account.LoginActivity;
import net.translives.app.api.OSChinaApi;
import net.translives.app.bean.Follow;
import net.translives.app.account.AccountHelper;
import net.translives.app.AppOperator;
import net.translives.app.base.BaseActivity;
import net.translives.app.bean.User;
import net.translives.app.bean.base.ResultBean;
import net.translives.app.bean.simple.Author;
import net.translives.app.interf.AppBarStateChangeListener;
import net.translives.app.media.ImageGalleryActivity;

import net.translives.app.util.DialogHelper;
import net.translives.app.widget.PortraitView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cz.msebera.android.httpclient.Header;

/**
 * 别的用户的主页
 * Created by thanatos on 16/7/13.
 * Updated by jzz on 2017/02/09
 */
public class OtherUserHomeActivity extends BaseActivity implements View.OnClickListener {

    public static final String KEY_BUNDLE = "KEY_BUNDLE_IN_OTHER_USER_HOME";

    /* 谁格式化了我这里的代码我就打谁 */
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.iv_portrait)
    PortraitView mPortrait;
    @Bind(R.id.tv_nick)
    TextView mNick;

    //@Bind(R.id.tv_score)
    //TextView mScore;
    @Bind(R.id.tv_btn_follow)
    TextView mBtnFollow;
    //@Bind(R.id.tv_count_fans)
    //TextView mCountFans;

    @Bind(R.id.layout_appbar)
    AppBarLayout mLayoutAppBar;

    @Bind(R.id.iv_logo_portrait)
    PortraitView mLogoPortrait;
    @Bind(R.id.tv_logo_nick)
    TextView mLogoNick;

    @Bind(R.id.iv_gender)
    TextView mGenderImage;

    @Bind(R.id.tv_summary)
    TextView mSummary;


    @Bind(R.id.layout_tab)
    TabLayout mTabLayout;
    @Bind(R.id.view_pager)
    ViewPager mViewPager;
    //@Bind(R.id.view_divider)
    //View mDivider;


    private boolean mIsLoadSuccess = false;
    private User user;
    private MenuItem mFollowMenu;
    private List<Pair<String, Fragment>> fragments;

    public static void show(Context context, Author author) {
        if (author == null) return;
        User user = new User();
        user.setId(author.getId());
        user.setName(author.getName());
        user.setPortrait(author.getPortrait());
        show(context, user);
    }

    public static void show(Context context, long id) {
        if (id <= 0) return;
        User user = new User();
        user.setId((int) id);
        show(context, user);
    }

    public static void show(Context context, String nick) {
        if (TextUtils.isEmpty(nick)) return;
        User user = new User();
        user.setName(nick);
        show(context, user);
    }

    /**
     * @param context context
     * @param id      无效值,随便填,只是用来区别{{@link #show(Context, String)}}方法的
     * @param suffix  个性后缀
     */
    public static void show(Context context, long id, String suffix) {
        if (TextUtils.isEmpty(suffix)) return;
        User user = new User();
        user.setSuffix(suffix);
        show(context, user);
    }

    public static void show(Context context, User user) {
        if (user == null) return;
        Intent intent = new Intent(context, OtherUserHomeActivity.class);
        intent.putExtra(KEY_BUNDLE, user);
        context.startActivity(intent);
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        user = (User) bundle.getSerializable(KEY_BUNDLE);
        if (user == null || (user.getId() <= 0 && TextUtils.isEmpty(user.getName())
                && TextUtils.isEmpty(user.getSuffix()))) {
            Toast.makeText(this, "没有此用户", Toast.LENGTH_SHORT).show();
            return false;
        }
        return super.initBundle(bundle);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_other_user_home;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mToolbar.setTitle("");
        mToolbar.setSubtitle("");
        mToolbar.setNavigationIcon(R.mipmap.btn_back_normal);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mLayoutAppBar.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                if( state == State.EXPANDED ) {
                    mLogoNick.setVisibility(View.GONE);
                    mLogoPortrait.setVisibility(View.GONE);
                }else if(state == State.COLLAPSED){
                    mLogoNick.setVisibility(View.VISIBLE);
                    mLogoPortrait.setVisibility(View.VISIBLE);
                }else {
                    //中间状态
                }
            }
        });

        injectDataToView();
    }

    private boolean isLoadSuccess() {
        return mIsLoadSuccess && user != null && user.getId() > 0;
    }

    @SuppressWarnings("all")
    private void injectDataToViewPager() {
        if (!isLoadSuccess()) return;


        mTabLayout.addTab(mTabLayout.newTab().setCustomView(getTabView("发帖")));
        mTabLayout.addTab(mTabLayout.newTab().setCustomView(getTabView("回复")));
        mTabLayout.addTab(mTabLayout.newTab().setCustomView(getTabView("提问")));
        mTabLayout.addTab(mTabLayout.newTab().setCustomView(getTabView("回答")));
        mTabLayout.setVisibility(View.VISIBLE);

        int t = 0, b = 0, a = 0, d = 0;
        if (user.getStatistics() != null) {
            t = user.getStatistics().getTweet();
            b = user.getStatistics().getBlog();
            a = user.getStatistics().getAnswer();
            d = user.getStatistics().getDiscuss();
        }

        if (fragments == null) {
            fragments = new ArrayList<>();
            fragments.add(new Pair<>(
                    "发帖",
                    UserTopicFragment.instantiate(user.getId())));
            fragments.add(new Pair<>(
                    "回复",
                    UserCommentFragment.instantiate(user.getId())));
            fragments.add(new Pair<>(
                    "提问",
                    UserQuestionFragment.instantiate(user.getId())));
            fragments.add(new Pair<>(
                    "回答",
                    UserAnswerFragment.instantiate(user.getId())));

            mViewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
                @Override
                public Fragment getItem(int position) {
                    return fragments.get(position).second;
                }

                @Override
                public int getCount() {
                    return fragments.size();
                }

                @Override
                public CharSequence getPageTitle(int position) {
                    return fragments.get(position).first;
                }
            });

            mTabLayout.setupWithViewPager(mViewPager);
            // TabLayout will remove up all tabs after setted up view pager
            // so we set it again
            mTabLayout.getTabAt(0).setCustomView(getTabView("发帖"));
            mTabLayout.getTabAt(1).setCustomView(getTabView("回复"));
            mTabLayout.getTabAt(2).setCustomView(getTabView("提问"));
            mTabLayout.getTabAt(3).setCustomView(getTabView("回答"));
        } else { // when request user detail info successfully
            setupTabText(mTabLayout.getTabAt(0), t);
            setupTabText(mTabLayout.getTabAt(1), b);
            setupTabText(mTabLayout.getTabAt(2), a);
            setupTabText(mTabLayout.getTabAt(3), d);
        }
    }




    private void injectDataToView() {
        if (user == null
                || user.getId() == 0
                || user.getName() == null)
            return;

        mPortrait.setup(user);
        mPortrait.setOnClickListener(this);
        mLogoPortrait.setup(user);
        mLogoNick.setText(user.getName());

        mNick.setText(user.getName());
        String desc = user.getSuffix();
        mSummary.setText(TextUtils.isEmpty(desc) ? "这人很懒,什么都没写" : desc);
        /*
        if (user.getStatistics() != null) {
            mScore.setText(String.format("积分 %s", user.getStatistics().getScore()));
            mCountFans.setText(String.format("粉丝 %s", user.getStatistics().getFans()));
            mCountFollow.setText(String.format("关注 %s", user.getStatistics().getFollow()));
        } else {
            mScore.setText("积分 0");
            mCountFans.setText("粉丝 0");
            mCountFollow.setText("关注 0");
        }
        */
        mBtnFollow.setOnClickListener(this);
        switch (user.getRelation()) {
            case User.RELATION_TYPE_BOTH:
            case User.RELATION_TYPE_ONLY_FANS_HIM:
            case User.RELATION_TYPE_ONLY_FANS_ME:
            case User.RELATION_TYPE_NULL:
                mBtnFollow.setText("已关注");
                mBtnFollow.setTextColor(getResources().getColor(R.color.done_text_color_disabled));
                //mBtnFollow.setSelected(true);
                break;
            default:
                mBtnFollow.setText("+ 关注");;
                mBtnFollow.setTextColor(getResources().getColor(R.color.main_green));
        }

        mGenderImage.setVisibility(View.VISIBLE);
        mGenderImage.setText(user.getGenderText());

        /*
        if (user.getGender() == User.GENDER_MALE) {
            mGenderImage.setImageResource(R.mipmap.ic_male);
        } else if (user.getGender() == User.GENDER_FEMALE) {
            mGenderImage.setImageResource(R.mipmap.ic_female);
        } else {
            mGenderImage.setVisibility(View.GONE);
        }
        */

    }

    @Override
    protected void initData() {
        super.initData();

        final ProgressDialog dialog = DialogHelper.getProgressDialog(this, "正在获取用户数据...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                onBackPressed();
            }
        });
        OSChinaApi.getUserInfo(user.getId(), new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                dialog.show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (isFinishing() || isDestroyed())
                    return;
                Toast.makeText(OtherUserHomeActivity.this, "获取用户信息失败", Toast.LENGTH_SHORT).show();
            }

            @SuppressWarnings("RestrictedApi")
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                if (isFinishing() || isDestroyed())
                    return;

                ResultBean<User> result = AppOperator.createGson().fromJson(
                        responseString, new TypeToken<ResultBean<User>>() {
                        }.getType());
                if (result.isSuccess() && result.getResult() == null) return;
                user = result.getResult();
                if (user == null || user.getId() == 0) {
                    Toast.makeText(OtherUserHomeActivity.this, "该用户不存在", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                mIsLoadSuccess = true;
                // 再次初始化用户信息
                injectDataToView();
                injectDataToViewPager();
                // 成功后初始化菜单
                invalidateOptionsMenu();
            }
        });
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public void onClick(View v) {
        if (!isLoadSuccess())
            return;
        switch (v.getId()) {
            /*
            case R.id.tv_count_follow:
                UserFollowsActivity.show(this, user.getId());
                break;
            case R.id.tv_count_fans:
                UserFansActivity.show(this, user.getId());
                break;
            case R.id.view_solar_system:
                Bundle userBundle = new Bundle();
                userBundle.putSerializable("user_info", user);
                UIHelper.showSimpleBack(this, SimpleBackPage.MY_INFORMATION_DETAIL, userBundle);
                break;
                */
            case R.id.iv_portrait:
                String url;
                if (user == null || TextUtils.isEmpty(url = user.getPortrait())) return;
                ImageGalleryActivity.show(this, url);
                break;
            case R.id.tv_btn_follow:
                followReverse();
                break;
        }
    }


    public void followReverse() {
        if (!AccountHelper.isLogin()) {
            LoginActivity.show(OtherUserHomeActivity.this);
            return;
        }

        if(AccountHelper.getUserId() == user.getId()){
            Toast.makeText(OtherUserHomeActivity.this, "不能关注自己", Toast.LENGTH_SHORT).show();
            return;
        }
        //if (mFollowMenu == null) return;
        OSChinaApi.getFollowReverse(user.getId(),"user", new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString
                    , Throwable throwable) {
                Toast.makeText(OtherUserHomeActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                ResultBean<Follow> result = AppOperator.createGson().fromJson(
                        responseString, new TypeToken<ResultBean<Follow>>() {
                        }.getType());
                if (result.isSuccess()) {
                    boolean isFollow = result.getResult().isFollow();
                    if(isFollow){
                        mBtnFollow.setText("已关注");
                        mBtnFollow.setTextColor(getResources().getColor(R.color.done_text_color_disabled));
                        //mBtnFollow.setSelected(true);
                    }else{
                        mBtnFollow.setText("+ 关注");
                        mBtnFollow.setTextColor(getResources().getColor(R.color.main_green));
                        //mBtnFollow.setSelected(false);
                    }

                } else {
                    onFailure(statusCode, headers, responseString, null);
                }
            }
        });
    }

    @SuppressWarnings("all")
    private void setupTabText(TabLayout.Tab tab, int count) {
        View view = tab.getCustomView();
        if (view == null) return;
        TabViewHolder holder = (TabViewHolder) view.getTag();
       // holder.mViewCount.setText(formatNumeric(count));
    }

    private String formatNumeric(int count) {
        if (count > 1000) {
            int a = count / 100;
            int b = a % 10;
            int c = a / 10;
            String str;
            if (c <= 9 && b != 0) str = c + "." + b;
            else str = String.valueOf(c);
            return str + "k";
        } else {
            return String.valueOf(count);
        }
    }

    private View getTabView(String tag) {
        View view = LayoutInflater.from(this).inflate(R.layout.tab_item_other_user, mTabLayout, false);
        TabViewHolder holder = new TabViewHolder(view);
       // holder.mViewCount.setText(cs);
        holder.mViewTag.setText(tag);
        view.setTag(holder);
        return view;
    }

    private static class TabViewHolder {
        //private TextView mViewCount;
        private TextView mViewTag;

        public TabViewHolder(View view) {
            mViewTag = (TextView) view.findViewById(R.id.tv_tag);
        }
    }

}
