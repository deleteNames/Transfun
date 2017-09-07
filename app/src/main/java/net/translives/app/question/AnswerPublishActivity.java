package net.translives.app.question;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Size;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;

import net.translives.app.BuildConfig;
import net.translives.app.R;
import net.translives.app.account.AccountHelper;
import net.translives.app.account.LoginActivity;
import net.translives.app.base.BaseBackActivity;
import net.translives.app.bean.Question;
import net.translives.app.util.CollectionUtil;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by JuQiu
 * on 16/8/22.
 */
public class AnswerPublishActivity extends BaseBackActivity {


    public static void show(Context context, Question bean) {
        // Check login before show
        if (!AccountHelper.isLogin()) {
            LoginActivity.show(context);
            return;
        }

        Intent intent = new Intent(context, AnswerPublishActivity.class);
        intent.putExtra("bean", bean);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        // hide the software
        // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        return R.layout.activity_answer_publish;
    }

    @SuppressWarnings({"MismatchedQueryAndUpdateOfCollection", "unchecked", "ResultOfMethodCallIgnored"})
    @Override
    protected void initWidget() {
        super.initWidget();
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        AnswerPublishFragment fragment = new AnswerPublishFragment();
        // init the args bounds
        fragment.setArguments(bundle);
        FragmentTransaction trans = getSupportFragmentManager()
                .beginTransaction();
        trans.replace(R.id.activity_answer_publish, fragment);
        trans.commit();
    }
}
