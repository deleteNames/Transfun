package net.translives.app.user;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.translives.app.R;
import net.translives.app.Setting;
import net.translives.app.base.BaseFragment;
import net.translives.app.util.HTMLUtil;
import net.translives.app.util.TDevice;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutUsFragment extends BaseFragment {

    @Bind(R.id.tv_version_name)
    TextView mTvVersionName;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        ButterKnife.bind(this, view);
        initView(view);
        initData();
        return view;
    }

    @Override
    public void initView(View view) {
        //view.findViewById(R.id.tv_grade).setOnClickListener(this);
    }

    @Override
    public void initData() {
        mTvVersionName.setText(TDevice.getVersionName());
    }

    @Override
    @OnClick({R.id.label_web_value, R.id.label_mail_value})
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            //case R.id.tv_grade:
                //TDevice.openAppInMarket(getActivity());
            //    break;

            case R.id.label_web_value:
                String url = getResources().getString(R.string.label_web_value);
                Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                getContext().startActivity(it);
                break;
            case R.id.label_mail_value:
                TDevice.copyTextToBoard(HTMLUtil.delHTMLTag(getResources().getString(R.string.label_mail_value)));
                break;
            default:
                break;
        }
    }
}
