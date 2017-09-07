package net.translives.app.comment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.translives.app.AppContext;
import net.translives.app.R;
import net.translives.app.api.OSChinaApi;
import net.translives.app.account.AccountHelper;
import net.translives.app.account.LoginActivity;
import net.translives.app.AppOperator;
import net.translives.app.base.BaseBackActivity;
import net.translives.app.base.adapter.BaseRecyclerAdapter;
import net.translives.app.bean.base.PageBean;
import net.translives.app.bean.base.ResultBean;
import net.translives.app.bean.comment.Comment;
import net.translives.app.bean.simple.About;
import net.translives.app.behavior.CommentBar;
import net.translives.app.comment.CommentAdapter;
import net.translives.app.util.DialogHelper;

import net.translives.app.widget.RecyclerRefreshLayout;
import net.translives.app.interf.OnKeyArrivedListenerAdapter;
import net.translives.app.util.HTMLUtil;
import net.translives.app.util.TDevice;

import java.lang.reflect.Type;
import java.util.List;

import butterknife.Bind;
import cz.msebera.android.httpclient.Header;
//import pub.devrel.easypermissions.AfterPermissionGranted;
//import pub.devrel.easypermissions.EasyPermissions;

import static net.translives.app.R.id.tv_back_label;

/**
 * Created by  fei
 * on  16/11/17
 * desc:详情评论列表ui
 */
public class CommentsActivity extends BaseBackActivity {

    @Bind(R.id.lay_refreshLayout)
    RecyclerRefreshLayout mRefreshLayout;

    @Bind(R.id.lay_blog_detail_comment)
    RecyclerView mLayComments;

    @Bind(R.id.activity_comments)
    CoordinatorLayout mCoordinatorLayout;

    @Bind(tv_back_label)
    TextView mBack_label;
    @Bind(R.id.tv_title)
    TextView mTitle;

    private CommentAdapter mCommentAdapter;

    private CommentBar mDelegation;
    private boolean mInputDoubleEmpty = true;
    private boolean isAddCommented;

    private TextHttpResponseHandler mHandler = new TextHttpResponseHandler() {

        @Override
        public void onStart() {
            super.onStart();
            if (mDelegation == null) return;
            mDelegation.getBottomSheet().dismiss();
            mDelegation.setCommitButtonEnable(false);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            AppContext.showToastShort(getResources().getString(R.string.pub_comment_failed));
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            try {
                Type type = new TypeToken<ResultBean<Comment>>() {
                }.getType();

                ResultBean<Comment> resultBean = AppOperator.createGson().fromJson(responseString, type);
                if (resultBean.isSuccess()) {
                    isAddCommented = true;
                    Comment respComment = resultBean.getResult();
                    if (respComment != null) {
                        mDelegation.setCommentHint(getString(mSourceId));
                        mDelegation.getBottomSheet().getEditText().setHint(getString(mSourceId));
                        AppContext.showToastShort(getString(R.string.pub_comment_success));
                        mDelegation.getBottomSheet().getEditText().setText("");
                        mDelegation.getBottomSheet().getBtnCommit().setTag(null);
                        mDelegation.getBottomSheet().dismiss();
                        getData(true, null);
                    }
                } else {
                    AppContext.showToastShort(resultBean.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
                onFailure(statusCode, headers, responseString, e);
            }
        }

        @Override
        public void onFinish() {
            super.onFinish();
            if (mDelegation == null) return;
            mDelegation.getBottomSheet().dismiss();
            mDelegation.setCommitButtonEnable(true);
        }
    };

    private int mOrder;
    private int mSourceId;

    private long mId;
    private int mType;

    private PageBean<Comment> mPageBean;
    //private AlertDialog mShareCommentDialog;
    private Comment mComment;

    public static void show(Activity activity, long id, int type, int order) {
        Intent intent = new Intent(activity, CommentsActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("type", type);
        intent.putExtra("order", order);
        activity.startActivityForResult(intent, 1);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_comments;
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        mId = bundle.getLong("id");
        mType = bundle.getInt("type");
        mOrder = bundle.getInt("order");
        return super.initBundle(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        /*
        mShareCommentDialog = DialogHelper.getRecyclerViewDialog(this, new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, long itemId) {
                switch (position) {
                    case 0:
                        TDevice.copyTextToBoard(HTMLUtil.delHTMLTag(mComment.getContent()));
                        break;
                    case 1:
                        if (!AccountHelper.isLogin()) {
                            LoginActivity.show(CommentsActivity.this, 1);
                            return;
                        }
                        mDelegation.getBottomSheet().getBtnCommit().setTag(mComment);

                        mDelegation.getBottomSheet().show(String.format("%s %s",
                                getString(R.string.reply_hint), mComment.getAuthor().getName()));
                        break;
                }
                mShareCommentDialog.dismiss();
            }
        }).create();
        */
        mBack_label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mDelegation = CommentBar.delegation(this, mCoordinatorLayout);
        mSourceId = R.string.pub_comment_hint;
        if (mType == OSChinaApi.COMMENT_QUESTION) {
            mSourceId = R.string.answer_hint;
        }

        mDelegation.getBottomSheet().getEditText().setHint(mSourceId);
        mDelegation.hideFav();
        mDelegation.hideShare();

        mDelegation.getBottomSheet().getEditText().setOnKeyArrivedListener(new OnKeyArrivedListenerAdapter(this));

        mDelegation.getBottomSheet().getEditText().setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                EditText view = (EditText) v;
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    Button mBtnView = mDelegation.getBottomSheet().getBtnCommit();
                    Object o = mBtnView.getTag();
                    if (o == null) return false;
                    if (!TextUtils.isEmpty(view.getText().toString())) {
                        mInputDoubleEmpty = false;
                        return false;
                    }
                    if (TextUtils.isEmpty(view.getText().toString()) && !mInputDoubleEmpty) {
                        mInputDoubleEmpty = true;
                        return false;
                    }
                    mBtnView.setTag(null);
                    view.setHint(mSourceId);
                    return true;
                }
                return false;
            }
        });

        mDelegation.getBottomSheet().setCommitListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendComment(mType, mId, (Comment) v.getTag(), mDelegation.getBottomSheet().getCommentText());
            }
        });

        mRefreshLayout.setColorSchemeResources(
                R.color.swiperefresh_color1, R.color.swiperefresh_color2,
                R.color.swiperefresh_color3, R.color.swiperefresh_color4);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mLayComments.setLayoutManager(manager);

        mCommentAdapter = new CommentAdapter(this, getImageLoader(), BaseRecyclerAdapter.ONLY_FOOTER);
        mCommentAdapter.setSourceId(mId);
        mCommentAdapter.setCommentType(mType);
        mCommentAdapter.setDelegation(mDelegation);
        //mCommentAdapter.setOnItemLongClickListener(this);
        /*
        mCommentAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, long itemId) {
                mComment = mCommentAdapter.getItem(position);
                if (mType == OSChinaApi.COMMENT_QUESTION) {
                   // QuesAnswerDetailActivity.show(CommentsActivity.this, mComment, mId, mType);
                } else {
                    mShareCommentDialog.show();
                }
            }
        });
        */
        mLayComments.setAdapter(mCommentAdapter);
    }

    @Override
    protected void initData() {
        super.initData();

        mRefreshLayout.setSuperRefreshLayoutListener(new RecyclerRefreshLayout.SuperRefreshLayoutListener() {
            @Override
            public void onRefreshing() {
                getData(true, null);
            }

            @Override
            public void onLoadMore() {
                String token = null;
                if (mPageBean != null)
                    token = mPageBean.getNextPageToken();
                getData(false, token);
            }
        });

        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                //第一次请求初始化数据
                getData(true, null);

            }
        });

    }

    Type getCommentType() {
        return new TypeToken<ResultBean<PageBean<Comment>>>() {
        }.getType();
    }

    /**
     * 检查当前数据,并检查网络状况
     *
     * @return 返回当前登录用户, 未登录或者未通过检查返回0
     */
    private long requestCheck() {
        if (mId == 0) {
            AppContext.showToast(getResources().getString(R.string.state_loading_error));
            return 0;
        }
        if (!TDevice.hasInternet()) {
            AppContext.showToastShort(R.string.tip_no_internet);
            return 0;
        }
        if (!AccountHelper.isLogin()) {
            LoginActivity.show(this);
            return 0;
        }
        // 返回当前登录用户ID
        return AccountHelper.getUserId();
    }

    /**
     * handle send comment
     */
    private void sendComment(int type, long id, Comment comment, String content) {
        if (requestCheck() == 0)
            return;

        if (TextUtils.isEmpty(content)) {
            AppContext.showToastShort(R.string.tip_comment_content_empty);
            return;
        }

        long uid = comment == null ? 0 : comment.getAuthor().getId();
        long cid = comment == null ? 0 : comment.getId();

        switch (type) {
            case OSChinaApi.COMMENT_QUESTION:
                OSChinaApi.pubQuestionComment(id, content,0, cid, uid, mHandler);
                break;
            case OSChinaApi.COMMENT_NEWS:
                OSChinaApi.pubNewsComment(id, content,0, cid, uid, mHandler);
                break;
            case OSChinaApi.COMMENT_SHARE:
                OSChinaApi.pubShareComment(id, content,0, cid, uid, mHandler);
                break;
            default:
                break;
        }

    }

    private void getData(final boolean clearData, String token) {
        OSChinaApi.getComments(mId, mType, "refer,reply", mOrder, token, new TextHttpResponseHandler() {
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
                try {

                    ResultBean<PageBean<Comment>> resultBean = AppOperator.createGson().fromJson(responseString, getCommentType());

                    if (resultBean.isSuccess()) {
                        mPageBean = resultBean.getResult();
                        int titleHintId = R.string.comment_title_hint;
                        if (mType == OSChinaApi.COMMENT_QUESTION) {
                            titleHintId = R.string.answer_hint;
                        }
                        mTitle.setText(String.format("%d%s%s", mPageBean.getTotalResults(), getString(R.string.item_hint), getString(titleHintId)));
                        handleData(mPageBean.getItems(), clearData);
                    }

                    mCommentAdapter.setState(
                            mPageBean == null || mPageBean.getItems() == null || mPageBean.getItems().size() < 20 ?
                                    BaseRecyclerAdapter.STATE_NO_MORE : BaseRecyclerAdapter.STATE_LOAD_MORE, true);
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }
        });
    }

    private void handleData(List<Comment> comments, boolean clearData) {
        if (clearData)
            mCommentAdapter.clear();
        mCommentAdapter.addAll(comments);
    }

    @Override
    public void finish() {
        if (isAddCommented) {
            setResult(RESULT_OK, new Intent());
        }
        super.finish();
    }
}
