package net.translives.app.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import net.translives.app.interf.OnPicturesPreviewerImageListener;
import net.translives.app.media.SelectImageActivity;
import net.translives.app.media.SelectOptions;
import net.translives.app.question.SelectImageAdapter;

public class PicturesPreviewer extends RecyclerView implements SelectImageAdapter.Callback {
    private SelectImageAdapter mImageAdapter;
    private ItemTouchHelper mItemTouchHelper;
    private RequestManager mCurImageLoader;

    public PicturesPreviewer(Context context) {
        super(context);
        init();
    }

    public PicturesPreviewer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PicturesPreviewer(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mImageAdapter = new SelectImageAdapter(this);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        this.setLayoutManager(layoutManager);
        this.setAdapter(mImageAdapter);
        this.setOverScrollMode(View.OVER_SCROLL_NEVER);

        ItemTouchHelper.Callback callback = new PicturesPreviewerItemTouchCallback(mImageAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(this);
    }

    OnPicturesPreviewerImageListener mOnPicturesPreviewerImage;
    public void setOnImageSetListener(OnPicturesPreviewerImageListener listener) {
        mOnPicturesPreviewerImage = listener;
    }

    public void set(String[] paths) {
        mImageAdapter.clear();
        for (String path : paths) {
            mImageAdapter.add(path);
        }
        mImageAdapter.notifyDataSetChanged();

        if(mOnPicturesPreviewerImage != null){
            mOnPicturesPreviewerImage.onImageSet(paths);
        }
    }

    @Override
    public void onLoadMoreClick(int selectCount ,boolean hasCam) {
        SelectImageActivity.show(getContext(), new SelectOptions.Builder()
                .setHasCam(hasCam)
                .setSelectCount(selectCount)
                .setSelectedImages(mImageAdapter.getPaths())
                .setCallback(new SelectOptions.Callback() {
                    @Override
                    public void doSelected(String[] images) {
                        set(images);
                    }
                }).build());
    }

    @Override
    public RequestManager getImgLoader() {
        if (mCurImageLoader == null) {
            mCurImageLoader = Glide.with(getContext());
        }
        return mCurImageLoader;
    }

    @Override
    public void onStartDrag(ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    public String[] getPaths() {
        return mImageAdapter.getPaths();
    }
}
