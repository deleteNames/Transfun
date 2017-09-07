package net.translives.app.share;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.WindowManager;

import net.translives.app.BuildConfig;
import net.translives.app.R;
import net.translives.app.account.AccountHelper;
import net.translives.app.account.LoginActivity;
import net.translives.app.base.BaseBackActivity;


public class SharePublishActivity extends BaseBackActivity {

    public static void show(Context context) {
        // Check login before show
        if (!AccountHelper.isLogin()) {
            LoginActivity.show(context);
            return;
        }

        Intent intent = new Intent(context, SharePublishActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        // hide the software
        // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        return R.layout.activity_share_publish;
    }

    @SuppressWarnings({"MismatchedQueryAndUpdateOfCollection", "unchecked", "ResultOfMethodCallIgnored"})
    @Override
    protected void initWidget() {
        super.initWidget();
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle == null) bundle = new Bundle();

        SharePublishFragment fragment = new SharePublishFragment();
        // init the args bounds
        fragment.setArguments(bundle);
        FragmentTransaction trans = getSupportFragmentManager()
                .beginTransaction();
        trans.replace(R.id.activity_share_publish, fragment);
        trans.commit();

    }

}
