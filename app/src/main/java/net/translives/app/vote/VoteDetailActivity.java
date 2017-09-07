package net.translives.app.vote;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.translives.app.AppContext;
import net.translives.app.AppOperator;
import net.translives.app.R;
import net.translives.app.account.AccountHelper;
import net.translives.app.account.LoginActivity;
import net.translives.app.api.OSChinaApi;
import net.translives.app.base.BaseBackActivity;
import net.translives.app.bean.Attach;
import net.translives.app.bean.Topic;
import net.translives.app.bean.TopicActive;
import net.translives.app.bean.base.ResultBean;
import net.translives.app.bean.comment.Comment;
import net.translives.app.bean.comment.Reply;
import net.translives.app.behavior.CommentBar;
import net.translives.app.dialog.ShareDialog;
import net.translives.app.interf.OnKeyArrivedListenerAdapter;
import net.translives.app.topic.CommentAdapter;
import net.translives.app.topic.CommentView;
import net.translives.app.topic.TopicCommentPublishActivity;
import net.translives.app.ui.empty.EmptyLayout;
import net.translives.app.util.BitmapUtil;
import net.translives.app.util.DialogHelper;
import net.translives.app.util.HTMLUtil;
import net.translives.app.util.PicturesCompressor;
import net.translives.app.util.StringUtils;
import net.translives.app.util.TDevice;
import net.translives.app.util.TLog;
import net.translives.app.widget.OWebView;
import net.translives.app.widget.PortraitView;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import cz.msebera.android.httpclient.Header;

//import net.translives.app.bean.Report;
//import net.translives.app.detail.ReportDialog;

/**
 * Created by haibin
 * on 2016/12/15.
 */

public class VoteDetailActivity extends BaseBackActivity implements Runnable{
/*
    @Bind(R.id.lay_refreshLayout)
    RecyclerRefreshLayout mRefreshLayout;

    @Bind(R.id.lay_blog_detail_comment)
    RecyclerView mLayComments;
*/
    protected String mCommentHint;
    protected EmptyLayout mEmptyLayout;


    protected Topic mTopic;
    protected TopicActive mBean;

    @Bind(R.id.iv_user_avatar)
    PortraitView mAvatar;
    @Bind(R.id.tv_nick)
    TextView mTextAuthor;
    @Bind(R.id.tv_pub_date)
    TextView mTextPubDate;
    @Bind(R.id.tv_title)
    TextView mTitle;

    @Bind(R.id.webView)
    OWebView mWebView;

    @Bind(R.id.tv_topic_append)
    TextView mTopicAppend;

    private CommentAdapter mCommentAdapter;
    protected CommentBar mDelegation;

    private View.OnClickListener onReplyButtonClickListener;
    private View.OnClickListener onSubReplyButtonClickListener;

    @Bind(R.id.comment_container)
    CommentView mCommentView;

    protected long mCommentId;
    protected long mCommentAuthorId;
    protected boolean mInputDoubleEmpty = false;

    private boolean mIsLoadSuccess = false;

    interface UploadImageCallback {
        void onUploadImageDone();

        void onUploadImage(int index, Attach attach);
    }


    public static void show(Context context, Topic topic, long id) {
        Intent intent = new Intent(context, VoteDetailActivity.class);
        Bundle bundle = new Bundle();
        TopicActive bean = new TopicActive();
        //bean.setType(News.TYPE_QUESTION);
        bean.setId(id);
        bundle.putSerializable("topic", topic);
        bundle.putSerializable("bean", bean);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public static void show(Context context, long id) {
        Intent intent = new Intent(context, VoteDetailActivity.class);
        Bundle bundle = new Bundle();
        TopicActive bean = new TopicActive();
        //bean.setType(News.TYPE_QUESTION);
        bean.setId(id);
        bundle.putSerializable("bean", bean);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_detail_topic;
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        super.initBundle(bundle);

        mBean = (TopicActive) bundle.getSerializable("bean");
        mTopic = (Topic) bundle.getSerializable("topic");

        return true;
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        if (!TDevice.hasWebView(this)) {
            finish();
            return;
        }


        mCommentHint = getString(R.string.pub_comment_hint);
        LinearLayout layComment = (LinearLayout) findViewById(R.id.ll_comment);
        mDelegation = CommentBar.delegation(this, layComment);
        mDelegation.setCommentHint(mCommentHint);
        mDelegation.getBottomSheet().getEditText().setHint(mCommentHint);

        mDelegation.hideFav();
        mDelegation.hideShare();

        mDelegation.getBottomSheet().showPic();

        /*
        mDelegation.setShareListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toShare(mBean.getTitle(), mBean.getBody(), mBean.getHref());
            }
        });
        */

        mDelegation.getBottomSheet().getEditText().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    handleKeyDel();
                }
                return false;
            }
        });
        mDelegation.getBottomSheet().setCommitListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AccountHelper.isLogin()) {
                    LoginActivity.show(VoteDetailActivity.this);
                    return;
                }

                mDelegation.getBottomSheet().dismiss();
                mDelegation.setCommitButtonEnable(false);

                notifyMsg(R.string.tweet_publishing);

                final String[] images = mDelegation.getBottomSheet().getPicturePreviewer().getPaths();
                if(images != null){
                    //AppOperator.runOnThread(new Runnable() {
                    //    @Override
                    //    public void run() {

                            final String cacheDir = String.format("%s/Pictures", getCacheDir().getAbsolutePath());
                            // change the model
                            String[] mCaheImages = saveImageToCache(cacheDir, images);
                            final List<Attach> uploaded_files = new ArrayList<Attach>();

                            uploadImages(0, mCaheImages,  new UploadImageCallback() {
                                        @Override
                                        public void onUploadImageDone() {

                                            String tempUrl = "";
                                            for(Attach attach : uploaded_files ){
                                                tempUrl += "<p><img src=\"" + attach.getHref() + "\" /></p>";
                                            }

                                            String comment_content = mDelegation.getBottomSheet().getCommentText() + tempUrl;

                                            addComment(mBean.getId(),
                                                    comment_content,
                                                    0,
                                                    mCommentId,
                                                    mCommentAuthorId);
                                        }

                                        @Override
                                        public void onUploadImage(int index , Attach attach) {
                                            uploaded_files.add(index,attach);
                                        }
                                    });
                   //     }
                   // });
                }else{

                    addComment(mBean.getId(),
                            mDelegation.getBottomSheet().getCommentText(),
                            0,
                            mCommentId,
                            mCommentAuthorId);
                }

            }
        });
        mDelegation.getBottomSheet().getEditText().setOnKeyArrivedListener(new OnKeyArrivedListenerAdapter(this));


/*
        mRefreshLayout.setColorSchemeResources(
                R.color.swiperefresh_color1, R.color.swiperefresh_color2,
                R.color.swiperefresh_color3, R.color.swiperefresh_color4);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mLayComments.setLayoutManager(manager);

        mCommentAdapter = new CommentAdapter(this, BaseRecyclerAdapter.VIEW_TYPE_FOOTER);

        mLayComments.setAdapter(mCommentAdapter);
*/

        mEmptyLayout = (EmptyLayout) findViewById(R.id.lay_error);
        mEmptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEmptyLayout.getErrorState() != EmptyLayout.NETWORK_LOADING) {
                    mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                    getDetail();
                }
            }
        });

        mEmptyLayout.post(new Runnable() {
            @Override
            public void run() {
                getDetail();
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();

    }


    protected void handleKeyDel() {
        if (mCommentId != mBean.getId()) {
            if (TextUtils.isEmpty(mDelegation.getBottomSheet().getCommentText())) {
                if (mInputDoubleEmpty) {
                    mCommentId = 0;
                    mCommentAuthorId = 0;
                    mDelegation.setCommentHint(mCommentHint);
                    mDelegation.getBottomSheet().getEditText().setHint(mCommentHint);
                    mDelegation.getBottomSheet().showPic();
                } else {
                    mInputDoubleEmpty = true;
                }
            } else {
                mInputDoubleEmpty = false;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_share, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_share:
                if (mBean == null || mBean.getId() <= 0 || TextUtils.isEmpty(mBean.getBody()))
                    break;

                String title = mBean.getTitle();
                if(TextUtils.isEmpty(title)){

                    title = HTMLUtil.delHTMLTag(mBean.getBody());
                    if (title.length() > 20)
                        title = StringUtils.getSubString(0, 20, title);
                }

                toShare(title, mBean.getBody(), mBean.getHref());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getDetail() {

        OSChinaApi.getTopicDetail(mBean.getId(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mEmptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

                    Type type = new TypeToken<ResultBean<TopicActive>>() {}.getType();
                    ResultBean<TopicActive> bean = AppOperator.createGson().fromJson(responseString, type);
                    if (bean.isSuccess()) {
                        mBean = bean.getResult();
                        //mDetailFragment.showGetDetailSuccess(mBean);
                        showGetDetailSuccess(mBean);
                    } else {
                        mEmptyLayout.setErrorType(EmptyLayout.NODATA);
                    }
            }
        });
    }

    private boolean isLoadSuccess() {
        return mIsLoadSuccess && mBean != null && mBean.getId() > 0;
    }

    public void showGetDetailSuccess(TopicActive bean) {

        mAvatar.setup(bean.getAuthor());
        mTextAuthor.setText(bean.getAuthor().getName());
        mTextPubDate.setText(StringUtils.formatSomeAgo(bean.getPubDate()));

        if(TextUtils.isEmpty(bean.getTitle())){
            mTitle.setVisibility(View.GONE);
        }else{
            mTitle.setText(bean.getTitle());
            mTitle.setVisibility(View.VISIBLE);
        }

        mWebView.loadDetailDataAsync(bean.getBody(), this);

        mCommentView.init(bean.getId(),OSChinaApi.COMMENT_TOPIC,OSChinaApi.COMMENT_NEW_ORDER);
        mCommentView.setReplyButtonClickListener(getOnReplyButtonClickListener());
        mCommentView.setSubReplyButtonClickListener(getOnSubReplyButtonClickListener());

        if (AccountHelper.isLogin() && AccountHelper.getUserId() == bean.getAuthor().getId()) {
            mTopicAppend.setVisibility(View.VISIBLE);
            mTopicAppend.setClickable(true);
            mTopicAppend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TopicCommentPublishActivity.show(VoteDetailActivity.this,mBean);
                }
            });

        }else{
            mTopicAppend.setVisibility(View.GONE);
        }

        mIsLoadSuccess = true;
    }

    private View.OnClickListener getOnReplyButtonClickListener() {
        if (onReplyButtonClickListener == null) {
            onReplyButtonClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Comment reply = (Comment) v.getTag();
                    mCommentId = reply.getId();
                    mCommentAuthorId = reply.getAuthor().getId();
                    mDelegation.getBottomSheet().show(String.format("%s %s", getResources().getString(R.string.reply_hint), reply.getAuthor().getName()));
                    mDelegation.getBottomSheet().setPicOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            AppContext.showToastShort("暂时不支持回复时添加图片");
                        }
                    });
                    mDelegation.getCommentText().setHint(String.format("%s %s", getResources().getString(R.string.reply_hint), reply.getAuthor().getName()));
                }
            };

        }


        return onReplyButtonClickListener;
    }

    private View.OnClickListener getOnSubReplyButtonClickListener() {
        if (onSubReplyButtonClickListener == null) {
            onSubReplyButtonClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Reply reply = (Reply) v.getTag();
                    mCommentId = reply.getId();
                    mCommentAuthorId = reply.getAuthor().getId();
                    mDelegation.getBottomSheet().show(String.format("%s %s", getResources().getString(R.string.reply_hint), reply.getAuthor().getName()));
                    mDelegation.getBottomSheet().setPicOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            AppContext.showToastShort("暂时不支持回复时添加图片");
                        }
                    });
                    mDelegation.getCommentText().setHint(String.format("%s %s", getResources().getString(R.string.reply_hint), reply.getAuthor().getName()));
                }
            };
        }


        return onSubReplyButtonClickListener;
    }

    @Override
    public void run() {
        hideEmptyLayout();
    }

    public void hideEmptyLayout() {
        mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
    }

    ProgressDialog msgDialog;
    public void notifyMsg(int resId, Object... values){

        if(msgDialog == null) {
            msgDialog = DialogHelper.getProgressDialog(this);
            msgDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            msgDialog.setCancelable(false);// 设置是否可以通过点击Back键取消
            msgDialog.setCanceledOnTouchOutside(false);
        }

        String message = getString(resId);
        msgDialog.setMessage(message);

        if(!msgDialog.isShowing()){
            msgDialog.show();
        }
    }

    public void setError(int resId, Object... values){
        msgDialog.dismiss();


        String message = getString(resId);
        /*AlertDialog.Builder alertDialog = DialogHelper.getMessageDialog(getActivity(),message);
        alertDialog.show();
        */
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        mDelegation.setCommitButtonEnable(true);
    }


    // Max upload 860KB
    private static final long MAX_UPLOAD_LENGTH = 860 * 1024;

    /**
     * 保存文件到缓存中
     *
     * @param cacheDir 缓存文件夹
     * @param paths    原始路径
     * @return 转存后的路径
     */
    private static String[] saveImageToCache(String cacheDir, String[] paths) {
        List<String> ret = new ArrayList<>();
        byte[] buffer = new byte[BitmapUtil.DEFAULT_BUFFER_SIZE];
        BitmapFactory.Options options = BitmapUtil.createOptions();
        for (final String path : paths) {
            String ext = null;
            try {
                int lastDotIndex = path.lastIndexOf(".");
                if (lastDotIndex != -1)
                    ext = path.substring(lastDotIndex + 1).toLowerCase();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (TextUtils.isEmpty(ext)) {
                ext = "jpg";
            }

            try {
                String tempFile = String.format("%s/IMG_%s.%s", cacheDir, SystemClock.currentThreadTimeMillis(), ext);
                if (PicturesCompressor.compressImage(path, tempFile, MAX_UPLOAD_LENGTH,
                        80, 1280, 1280 * 6, buffer, options, true)) {
                    TLog.log("OPERATOR doImage:" + tempFile + " " + new File(tempFile).length());
                    // verify the picture ext.
                    tempFile = PicturesCompressor.verifyPictureExt(tempFile);
                    ret.add(tempFile);
                    continue;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            TLog.error("OPERATOR compressImage error:" + path);
        }
        if (ret.size() > 0) {
            String[] images = new String[ret.size()];
            ret.toArray(images);
            return images;
        }
        return null;
    }


    /**
     * 上传图片
     *
     * @param index    上次图片的坐标
     * @param paths    上传的路径数组
     * @param runnable 完全上传完成时回调
     */
    private void uploadImages(final int index, final String[] paths, final UploadImageCallback runnable) {

        // checkShare done
        if (index < 0 || index >= paths.length) {
            runnable.onUploadImageDone();
            return;
        }

        final String path = paths[index];

        File file = new File(path);

        OSChinaApi.updateAttach(file, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                //notifyMsg(R.string.attach_uploading);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                TLog.error(String.format("Upload image onFailure, statusCode:[%s] responseString:%s throwable:%s",
                        statusCode, responseString, throwable.getMessage()));
                setError(R.string.attach_upload_failed);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<Attach>>() {
                    }.getType();
                    ResultBean resultBean = new Gson().fromJson(responseString, type);
                    if (resultBean.isSuccess()) {
                        Attach attach = (Attach) resultBean.getResult();
                        // call progress
                        runnable.onUploadImage(index, attach);

                        uploadImages(index + 1, paths, runnable);
                    } else {
                        File file = new File(path);
                        TLog.log(String.format("Upload name:[%s] size:[%s] error:%s",
                                file.getAbsolutePath(), file.length(), resultBean.getMessage()));
                        onFailure(statusCode, headers, responseString, null);
                    }
                } catch (Exception e) {
                    onFailure(statusCode, headers, responseString, null);
                }
            }

        });
    }

    public void addComment(long sourceId, String content, long referId, long replyId, long reAuthorId) {


        OSChinaApi.pubTopicComment(sourceId, content, referId, replyId, reAuthorId, new TextHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                showCommentError("评论失败");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<Comment>>() {
                    }.getType();

                    ResultBean<Comment> resultBean = AppOperator.createGson().fromJson(responseString, type);
                    if (resultBean.isSuccess()) {
                        Comment respComment = resultBean.getResult();
                        if (respComment != null) {
                            //mDetailFragment.showCommentSuccess(respComment);
                            showCommentSuccess(respComment);
                        }
                    } else {
                        showCommentError(resultBean.getMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                    showCommentError("评论失败");
                }
            }

            @Override
            public void onStart() {
                super.onStart();
                if (mDelegation == null) return;
                mDelegation.getBottomSheet().dismiss();
                mDelegation.setCommitButtonEnable(false);

            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (mDelegation == null) return;
                mDelegation.getBottomSheet().dismiss();
                mDelegation.setCommitButtonEnable(true);

                if (msgDialog != null && msgDialog.isShowing()) msgDialog.dismiss();
            }
        });
    }

    public void showCommentSuccess(Comment comment) {

        if (mDelegation == null)
            return;

        mDelegation.getBottomSheet().dismiss();
        mDelegation.setCommitButtonEnable(true);
        AppContext.showToastShort(R.string.pub_comment_success);
        mDelegation.getCommentText().setHint(mCommentHint);
        mDelegation.getBottomSheet().getEditText().setText("");
        mDelegation.getBottomSheet().getEditText().setHint(mCommentHint);
        mDelegation.getBottomSheet().dismiss();

        mCommentView.refresh();
    }

    public void showCommentError(String message) {
        //hideDialog();
        AppContext.showToastShort(R.string.pub_comment_failed);
        mDelegation.setCommitButtonEnable(true);
    }
/*
    private void getCommentData(final boolean clearData, String token) {
        OSChinaApi.getComments(mBean.getId(), OSChinaApi.COMMENT_TOPIC, "refer,reply", 1, token, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mCommentAdapter.setState(BaseRecyclerAdapter.STATE_LOAD_ERROR, true);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                mRefreshLayout.onComplete();
            }

            @SuppressLint("DefaultLocale")
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                //try {
                    Type type = new TypeToken<ResultBean<PageBean<Comment>>>() {}.getType();
                    ResultBean<PageBean<Comment>> resultBean = AppOperator.createGson().fromJson(responseString, type);

                    if (resultBean.isSuccess()) {
                        mPageBean = resultBean.getResult();
                        handleCommentData(mPageBean.getItems(), clearData);
                    }

                    mCommentAdapter.setState(
                            mPageBean == null || mPageBean.getItems() == null || mPageBean.getItems().size() < 20 ?
                                    BaseRecyclerAdapter.STATE_NO_MORE : BaseRecyclerAdapter.STATE_LOAD_MORE, true);
                //} catch (Exception e) {
                //    e.printStackTrace();
                //    onFailure(statusCode, headers, responseString, e);
               // }
            }
        });
    }

    private void handleCommentData(List<Comment> comments, boolean clearData) {
        if (clearData)
            mCommentAdapter.clear();
        mCommentAdapter.addAll(comments);
    }
    */

    protected ShareDialog mAlertDialog;

    @SuppressWarnings("LoopStatementThatDoesntLoop")
    protected boolean toShare(String title, String content, String url) {
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(url)) {
            TLog.error("title:"+title+"  url:"+url);
            return false;
        }

        String imageUrl = null;
        //匹配内容中是否有图片，有就返回第一张图片的url
        //"<\\s*img\\s+([^>]*)\\s*/>"
        String regex = "<img .*src=\"([^\"]+)\"";

        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            imageUrl = matcher.group(1);
            break;
        }

        content = content.trim();
        if (content.length() > 55) {
            content = HTMLUtil.delHTMLTag(content);
            if (content.length() > 55)
                content = StringUtils.getSubString(0, 55, content);
        } else {
            content = HTMLUtil.delHTMLTag(content);
        }
        if (TextUtils.isEmpty(content))
            content = "";


        // 分享
        if (mAlertDialog == null) {
            mAlertDialog = new ShareDialog(VoteDetailActivity.this)
                    .title(title)
                    .content(content)
                    .imageUrl(imageUrl)//如果没有图片，即url为null，直接加入app默认分享icon
                    .url(url).with();
        }
        mAlertDialog.show();

        return true;
    }
}
