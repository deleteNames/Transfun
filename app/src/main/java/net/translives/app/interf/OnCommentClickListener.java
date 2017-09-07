package net.translives.app.interf;

import android.view.View;

import net.translives.app.bean.comment.Comment;

/**
 * Created by JuQiu
 * on 16/6/21.
 */

public interface OnCommentClickListener {
    void onClick(View view, Comment comment);
}
