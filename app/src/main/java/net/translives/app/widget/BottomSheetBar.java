package net.translives.app.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import net.translives.app.R;
//import Emojicon;
//import InputHelper;
//import OnEmojiClickListener;
//import net.translives.app.emoji.EmojiView;
import net.translives.app.interf.OnPicturesPreviewerImageListener;
import net.translives.app.util.TDevice;
import net.translives.app.widget.richedit.RichEditText;

/**
 * 底部弹出评论框
 * Created by haibin
 * on 2016/11/10.
 * <p>
 * Changed by fei
 * on 2016/11/17
 *
 * @author Qiujuer
 */
@SuppressWarnings("unused")
public class BottomSheetBar{

    private View mRootView;
    private RichEditText mEditText;
    private ImageButton mPicView;
    //private ImageButton mAtView;
    //private ImageButton mFaceView;
    //private CheckBox mSyncToTweetView;
    private Context mContext;
    private Button mBtnCommit;
    private BottomCommentDialog mDialog;
    private FrameLayout mFrameLayout;
    private PicturesPreviewer mPictureView;


    private BottomSheetBar(Context context) {
        this.mContext = context;
    }

    @SuppressLint("InflateParams")
    public static BottomSheetBar delegation(Context context) {
        BottomSheetBar bar = new BottomSheetBar(context);
        bar.mRootView = LayoutInflater.from(context).inflate(R.layout.layout_bottom_sheet_comment_bar, null, false);
        bar.initView();
        return bar;
    }

    private void initView() {
        mFrameLayout = (FrameLayout) mRootView.findViewById(R.id.fl_pic);
        mEditText = (RichEditText) mRootView.findViewById(R.id.et_comment);
        mPicView = (ImageButton) mRootView.findViewById(R.id.ib_pic);
        mPicView.setVisibility(View.INVISIBLE);
        mPictureView = (PicturesPreviewer) mRootView.findViewById(R.id.recycler_images);

        // set hide action
        mPictureView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                TDevice.closeKeyboard(mEditText);
                return false;
            }
        });

        //mAtView = (ImageButton) mRootView.findViewById(R.id.ib_mention);
        //mAtView.setVisibility(View.INVISIBLE);
        //mFaceView = (ImageButton) mRootView.findViewById(R.id.ib_face);
        //mFaceView.setVisibility(View.GONE);
        //mSyncToTweetView = (CheckBox) mRootView.findViewById(R.id.cb_sync);
        //if (mFaceView.getVisibility() == View.GONE) {
        //    mSyncToTweetView.setText(R.string.send_tweet);
        //}
        mBtnCommit = (Button) mRootView.findViewById(R.id.btn_comment);
        mBtnCommit.setEnabled(false);

        mDialog = new BottomCommentDialog(mContext, R.style.dialog_bottom);
        mDialog.setContentView(mRootView);

        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                TDevice.closeKeyboard(mEditText);
                mFrameLayout.setVisibility(View.GONE);
            }
        });

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mBtnCommit.setEnabled(s.length() > 0);
            }
        });
    }

    public void showPic() {
        mPicView.setVisibility(View.VISIBLE);
        mPicView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mFrameLayout.setVisibility(View.VISIBLE);
                mPictureView.onLoadMoreClick(6,false);
                TDevice.closeKeyboard(mEditText);
            }
        });

        mPictureView.setOnImageSetListener(new OnPicturesPreviewerImageListener(){

            @Override
            public void onImageSet(String[] paths) {
                mFrameLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    public void setPicOnClickListener(View.OnClickListener listener){
        mPicView.setOnClickListener(listener);
    }

    public void hidePic() {
        mPicView.setVisibility(View.INVISIBLE);
        mPicView.setOnClickListener(null);
    }

    public void show(String hint) {
        mDialog.show();
        if (!"添加评论".equals(hint)) {
            mEditText.setHint(hint + " ");
        }
        mRootView.postDelayed(new Runnable() {
            @Override
            public void run() {
                TDevice.showSoftKeyboard(mEditText);
            }
        }, 50);
    }

    public void setEditText(String text) {
        mEditText.setText(text);
        mEditText.setSelection(text.length());
    }

    public void dismiss() {
        TDevice.closeKeyboard(mEditText);
        mDialog.dismiss();
    }


    public void setCommitListener(View.OnClickListener listener) {
        mBtnCommit.setOnClickListener(listener);
    }

    public void handleSelectFriendsResult(Intent data) {
        String names[] = data.getStringArrayExtra("names");
        if (names != null && names.length > 0) {
            String text = "";
            for (String n : names) {
                text += "@" + n + " ";
            }
            mEditText.getText().insert(mEditText.getSelectionEnd(), text);
        }
    }

    public RichEditText getEditText() {
        return mEditText;
    }

    public String getCommentText() {
        return mEditText.getText().toString().trim();
    }

    public Button getBtnCommit() {
        return mBtnCommit;
    }

    public PicturesPreviewer getPicturePreviewer() {
        return mPictureView;
    }
}
