package net.translives.app.question;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.translives.app.OSCApplication;
import net.translives.app.R;
import net.translives.app.base.adapter.BaseGeneralRecyclerAdapter;
import net.translives.app.base.adapter.BaseRecyclerAdapter;
import net.translives.app.bean.Question;
import net.translives.app.bean.comment.Image;
import net.translives.app.bean.simple.Author;
import net.translives.app.util.StringUtils;
import net.translives.app.util.TDevice;
import net.translives.app.widget.DetailPicturesLayout;

import butterknife.Bind;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 新版栏目问答
 * Created by haibin
 * on 2016/10/27.
 */

class QuestionAdapter extends BaseGeneralRecyclerAdapter<Question> implements BaseRecyclerAdapter.OnLoadingHeaderCallBack {
    //private OSCApplication.ReadState mReadState;

    QuestionAdapter(Callback callback, int mode) {
        super(callback, mode);
        //mReadState = OSCApplication.getReadState("question_list");
        setOnLoadingHeaderCallBack(this);
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderHolder(ViewGroup parent) {
        return new HeaderViewHolder(mHeaderView);
    }

    @Override
    public void onBindHeaderHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new QuestionViewHolder(mInflater.inflate(R.layout.item_list_question, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, Question item, int position) {
        QuestionViewHolder vh = (QuestionViewHolder) holder;

        Author author = item.getAuthor();

        mCallBack.getImgLoader()
                .load(author != null ? author.getPortrait() : "")
                .asBitmap().placeholder(R.mipmap.widget_default_face)
                .into(vh.iv_question);


        vh.tv_name.setText(author.getName());
        if(item.getType() == Question.TYPE_QUESTION){
            vh.tv_question_type.setText(R.string.publish_type_question);
            vh.tv_comment_count.setText(String.valueOf(item.getStatistics().getComment()) + " 个回答");

            vh.tv_follow_count.setVisibility(View.VISIBLE);
            vh.tv_follow_count.setText(String.valueOf(item.getStatistics().getFollow()) + " 个关注");
        }else{
            vh.tv_question_type.setText(R.string.publish_type_share);
            vh.tv_comment_count.setText(String.valueOf(item.getStatistics().getComment()) + " 个评论");
            vh.tv_follow_count.setVisibility(View.GONE);
        }


        vh.tv_question_title.setText(item.getTitle());

        if (!TextUtils.isEmpty(item.getBody())) {
            String content = item.getBody().replaceAll("[\n\\s]+", " ");
            vh.tv_question_content.setText(content);
        }else{
            vh.tv_question_content.setVisibility(View.GONE);
        }

        Image[] images = item.getImages();
        if(images != null && images.length> 0){
            vh.mLayoutFlow.setImage(images);
            vh.mLayoutFlow.setVisibility(View.VISIBLE);
        }else{
            vh.mLayoutFlow.setVisibility(View.GONE);
        }


        /*
        Resources resources = mContext.getResources();

        if (mReadState.already(item.getKey())) {
            vh.tv_question_title.setTextColor(TDevice.getColor(resources, R.color.text_desc_color));
            vh.tv_question_content.setTextColor(TDevice.getColor(resources, R.color.text_secondary_color));
        } else {
            vh.tv_question_title.setTextColor(TDevice.getColor(resources, R.color.text_title_color));
            vh.tv_question_content.setTextColor(TDevice.getColor(resources, R.color.text_desc_color));
        }
    */
        vh.tv_time.setText(StringUtils.formatSomeAgo(item.getPubDate()));
        //vh.tv_view.setText(String.valueOf(item.getStatistics().getView()));

    }

    private static class QuestionViewHolder extends RecyclerView.ViewHolder {
        TextView tv_question_type,tv_question_title, tv_question_content, tv_time, tv_comment_count,tv_name,tv_follow_count;
        CircleImageView iv_question;

        DetailPicturesLayout mLayoutFlow;

        QuestionViewHolder(View itemView) {
            super(itemView);
            tv_question_type = (TextView) itemView.findViewById(R.id.tv_question_type);
            tv_question_title = (TextView) itemView.findViewById(R.id.tv_question_title);
            tv_question_content = (TextView) itemView.findViewById(R.id.tv_question_content);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            tv_comment_count = (TextView) itemView.findViewById(R.id.tv_comment);
            tv_follow_count = (TextView) itemView.findViewById(R.id.tv_follow);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            iv_question = (CircleImageView) itemView.findViewById(R.id.iv_question);

            mLayoutFlow = (DetailPicturesLayout) itemView.findViewById(R.id.fl_image);
        }
    }
}
