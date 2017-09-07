package net.translives.app.question;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.gson.reflect.TypeToken;

import net.translives.app.AppOperator;
import net.translives.app.OSCApplication;
import net.translives.app.R;
import net.translives.app.api.OSChinaApi;
import net.translives.app.base.adapter.BaseGeneralRecyclerAdapter;
import net.translives.app.base.adapter.BaseRecyclerAdapter;
import net.translives.app.base.fragments.BaseGeneralRecyclerFragment;
import net.translives.app.bean.Question;
import net.translives.app.bean.base.PageBean;
import net.translives.app.bean.base.ResultBean;
import net.translives.app.question.QuestionDetailActivity;
import net.translives.app.question.QuestionAdapter;
import net.translives.app.share.ShareDetailActivity;
import net.translives.app.ui.empty.EmptyLayout;
import net.translives.app.util.CacheManager;
import net.translives.app.widget.SimplexToast;

import org.json.JSONObject;

import java.lang.reflect.Type;

/**
 * 动弹列表
 * Created by huanghaibin_dev
 * Updated by thanatosx
 * on 2016/7/18.
 */
public class QuestionFragment extends BaseGeneralRecyclerFragment<Question> {
    //private OSCApplication.ReadState mReadState;

    public static final int CATALOG_NEW = 0X0001;
    public static final int CATALOG_HOT = 0X0002;
    public static final int CATALOG_TOPIC = 0X0003;

    public static final String BUNDLE_KEY_REQUEST_CATALOG = "BUNDLE_KEY_REQUEST_CATALOG";

    public int mReqCatalog;

    public static Fragment instantiate(int catalog) {
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_KEY_REQUEST_CATALOG, catalog);
        Fragment fragment = new QuestionFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initBundle(Bundle bundle) {
        super.initBundle(bundle);
        mReqCatalog = bundle.getInt(BUNDLE_KEY_REQUEST_CATALOG, CATALOG_NEW);
    }

    /**
     * fragment被销毁的时候重新调用，初始化保存的数据
     *
     * @param bundle onSaveInstanceState
     */
    @Override
    protected void onRestartInstance(Bundle bundle) {
        super.onRestartInstance(bundle);
        mReqCatalog = bundle.getInt(BUNDLE_KEY_REQUEST_CATALOG, CATALOG_NEW);
    }


    @Override
    public void initData() {
        super.initData();

       // mReadState = OSCApplication.getReadState("question_list");
    }

    @Override
    protected void requestData() {
        super.requestData();

        String pageToken = isRefreshing ? null : mBean.getNextPageToken();
        switch (mReqCatalog) {
            case CATALOG_NEW:
                OSChinaApi.getQuestionList("new",pageToken, mHandler);
                break;
            case CATALOG_HOT:
                OSChinaApi.getQuestionList("hot",pageToken, mHandler);
                break;
            case CATALOG_TOPIC:
                OSChinaApi.getQuestionList("hot",pageToken, mHandler);
                break;
        }
    }

    @Override
    public void onItemClick(int position, long itemId) {
        Question qa = mAdapter.getItem(position);
        if (qa == null) return;

        if(qa.getType() == 2){
            QuestionDetailActivity.show(getContext(), qa);
        }else{
            ShareDetailActivity.show(getContext(), qa.getId());
        }

        //mReadState.put(qa.getKey());
        //mAdapter.updateItem(position);
    }

    @Override
    protected BaseRecyclerAdapter<Question> getRecyclerAdapter() {
        int mode = BaseRecyclerAdapter.ONLY_FOOTER;
        return new QuestionAdapter(this,mode);
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<PageBean<Question>>>() {
        }.getType();
    }

    @Override
    protected Class<Question> getCacheClass() {
        return Question.class;
    }

    public boolean isNeedCache(){
        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(BUNDLE_KEY_REQUEST_CATALOG, mReqCatalog);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == 1) {
            mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
            //mUserId = AccountHelper.getUserId();
            onRefreshing();
        }
    }
}
