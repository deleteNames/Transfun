package net.translives.app.util.parser;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;

import net.translives.app.util.HTMLUtil;

/**
 * 只显示文本，不需要富文本点击,如消息、私信列表
 * Created by haibin
 * on 2017/3/22.
 */

public class StringParser extends RichTextParser {
    private static StringParser mInstance = new StringParser();

    public static StringParser getInstance() {
        return mInstance;
    }

    @Override
    public Spannable parse(Context context, String content) {
        if (TextUtils.isEmpty(content))
            return null;
        content = HTMLUtil.rollbackReplaceTag(content);
        Spannable spannable = parseOnlyAtUser(context, content);
        spannable = parseOnlyTag(context, spannable);
        spannable = parseOnlyLink(context, spannable);
        return spannable;
    }
}
