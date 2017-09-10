package net.translives.app.api;

import android.text.TextUtils;

import net.translives.app.util.TLog;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.File;
import java.io.FileNotFoundException;

import static net.translives.app.api.ApiHttpClient.post;

/**
 * OSChina Api v1 and v2
 */
public class OSChinaApi {

    public static final int CATALOG_ALL = 0;
    public static final int CATALOG_SOFTWARE = 1;
    public static final int CATALOG_QUESTION = 2;
    public static final int CATALOG_BLOG = 3;
    public static final int CATALOG_TRANSLATION = 4;
    public static final int CATALOG_EVENT = 5;
    public static final int CATALOG_NEWS = 6;
    public static final int CATALOG_TWEET = 100;

    public static final int COMMENT_QUESTION = 1;
    public static final int COMMENT_NEWS = 2;
    public static final int COMMENT_SHARE = 3;
    public static final int COMMENT_TOPIC = 4;

    public static final int COMMENT_HOT_ORDER = 2; //热门评论顺序
    public static final int COMMENT_NEW_ORDER = 1; //最新评论顺序

    public static final String LOGIN_WEIBO = "weibo";
    public static final String LOGIN_QQ = "qq";
    public static final String LOGIN_WECHAT = "wechat";

    public static final int REGISTER_INTENT = 1;
    public static final int RESET_PWD_INTENT = 2;

    public static final int REQUEST_COUNT = 0x50;//请求分页大小

    public static final int TYPE_USER_FLOWS = 1;//你关注的人
    public static final int TYPE_USER_FANS = 2;//关注你的人


    public static void checkUpdate(TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("appId", 1);
        params.put("catalog", 1);
        params.put("all", false);
        ApiHttpClient.get("api/check_upload", params, handler);
    }

    /**
     * 反馈
     *
     * @param authorId
     * @param content
     * @param file
     * @param handler
     */
    public static void feedBack(long authorId, String content, File file, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("authorId", authorId);
        params.put("content", content);
        if (file != null && file.exists()) {
            try {
                params.put("file", file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ApiHttpClient.post("api/feedback", params, handler);
    }

    public static void validateRegister(String email, String pwd, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("email", email);
        params.put("pwd", pwd);
        post("api/auth/register", params, handler);
    }

    /**
     * @param catalog  open catalog
     * @param openInfo openInfo
     * @param handler  handler
     */
    public static void openLogin(String catalog, String openInfo, TextHttpResponseHandler handler) {
        if (TextUtils.isEmpty(openInfo)) return;

        TLog.log(openInfo);
        RequestParams params = new RequestParams();
        params.put("catalog", catalog);
        params.put("info", openInfo);

        ApiHttpClient.post("api/auth/snslogin", params, handler);
    }

    public static void getBanner(TextHttpResponseHandler handler) {
        ApiHttpClient.get("api/banner/list", handler);
    }

    public static void getNewList(String pageToken, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("page", pageToken);
        ApiHttpClient.get("api/new/list", params, handler);
    }

    public static void getTopicFeed(String pageToken, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("page", pageToken);
        ApiHttpClient.get("api/topic/feed", params, handler);
    }

    public static void getTopicCategory(String pageToken, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("page", pageToken);
        ApiHttpClient.get("api/topic/category", params, handler);
    }

    public static void getTopicList(long topic_id, String pageToken, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("topic_id", topic_id);
        params.put("page", pageToken);
        ApiHttpClient.get("api/topic/list", params, handler);
    }

    public static void getTopicDetail(long id, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("id", id);
        ApiHttpClient.get("api/topic/detail", params, handler);
    }


    /**
     * 新版获得各种类型详情统一接口和Model
     */
    public static void getNewDetail( long id, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("id", id);
        ApiHttpClient.get("api/new/detail", params, handler);
    }

    public static void getEventList(String pageToken, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("page", pageToken);
        ApiHttpClient.get("api/event/list", params, handler);
    }

    public static void getEventDetail( long id, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("id", id);
        ApiHttpClient.get("api/event/detail", params, handler);
    }

    /**
     * 请求评论列表
     *
     * @param sourceId  目标Id，该sourceId为资讯、博客、问答等文章的Id
     * @param type      问答类型
     * @param parts     请求的数据节点 parts="refer,reply"
     * @param handler   AsyncHttpResponseHandler
     */
    //@Deprecated
    public static void getComments(long sourceId, int type, String parts,int order, String pageToken, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("sourceId", sourceId);
        params.put("type", type);
        params.put("parts", parts);
        params.put("order", order);
        params.put("page", pageToken);
        ApiHttpClient.get("api/comment/list", params, handler);
    }

    /**
     * 请求评论详情
     *
     * @param id      评论Id
     * @param handler AsyncHttpResponseHandler
     */
    public static void getCommentDetail(long id, long aid, int type, TextHttpResponseHandler handler) {
        if (id <= 0) return;
        RequestParams params = new RequestParams();
        params.put("id", id);
        params.put("authorId", aid);
        params.put("type", type);
        ApiHttpClient.get("api/comment/detail", params, handler);
    }

    public static void getTopicComments(long sourceId, int order, String pageToken,TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("sourceId", sourceId);
        params.put("type", OSChinaApi.COMMENT_TOPIC);
        params.put("order", order);
        params.put("page", pageToken);
        ApiHttpClient.get("api/comment/list", params, handler);
    }

    /**
     * 发布评论
     */
    public static void pubNewsComment(long sid, String comment, long referId,long replyId, long commentAuthorId
            , TextHttpResponseHandler handler) {
        if (referId == 0 || referId == sid) {
            referId = 0;
            commentAuthorId = 0;
        }
        publishComment(sid, referId, replyId, commentAuthorId, OSChinaApi.COMMENT_NEWS, comment, handler);
    }

    public static void pubQuestionComment(long sid, String comment, long referId,long replyId, long commentAuthorId
            , TextHttpResponseHandler handler) {
        if (referId == 0 || referId == sid) {
            referId = 0;
            commentAuthorId = 0;
        }
        publishComment(sid, referId, replyId, commentAuthorId, OSChinaApi.COMMENT_QUESTION, comment, handler);
    }

    public static void pubTopicComment(long sid, String comment, long referId,long replyId, long commentAuthorId
            , TextHttpResponseHandler handler) {
        /*
        if (referId == 0 || referId == sid) {
            referId = 0;
            commentAuthorId = 0;
        }*/
        publishComment(sid, referId, replyId, commentAuthorId, OSChinaApi.COMMENT_TOPIC, comment, handler);
    }

    /**
     * 发布评论
     */
    public static void pubShareComment(long sid, String comment, long referId,long replyId, long commentAuthorId
            , TextHttpResponseHandler handler) {
        if (referId == 0 || referId == sid) {
            referId = 0;
            commentAuthorId = 0;
        }
        publishComment(sid, referId, replyId, commentAuthorId, OSChinaApi.COMMENT_SHARE, comment, handler);
    }


    /**
     * 发表评论
     *
     * @param sourceId   文章id
     * @param referId    引用的评论的id，问答评论详情
     * @param replyId    回复的评论的id
     * @param reAuthorId 引用、回复的发布者id
     * @param type       文章类型 1:软件推荐, 2:问答帖子, 3:博客, 4:翻译文章, 5:活动, 6:资讯
     * @param content    内容
     * @param handler    你懂得
     */
    public static void publishComment(long sourceId, long referId, long replyId, long reAuthorId,
                                      int type, String content, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("sourceId", sourceId);
        params.put("type", type);
        params.put("content", content);
        if (referId > 0)
            params.put("referId", referId);
        if (replyId > 0)
            params.put("replyId", replyId);
        if (reAuthorId > 0)
            params.put("reAuthorId", reAuthorId);
        ApiHttpClient.post("api/comment/create", params, handler);
    }

    public static void voteComment(int sourceType, long commentId, long commentAuthorId , int voteOpt, TextHttpResponseHandler handler) {
        if (commentId <= 0) return;
        RequestParams params = new RequestParams();
        params.put("sourceType", sourceType);
        params.put("commentId", commentId);
        params.put("commentAuthorId", commentAuthorId);
        params.put("voteOpt", voteOpt);
        ApiHttpClient.post("api/comment/vote", params, handler);
    }

    /**
     * 更改收藏状态
     *
     * @param id      id
     * @param type    type
     * @param handler handler
     */
    public static void getFavReverse(long id, String type, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("id", id);
        params.put("type", type);
        ApiHttpClient.post("api/user/favorite/reverse", params, handler);
    }

    public static void getUserFavList(long uid, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        if (uid > 0) params.put("id", uid);
        ApiHttpClient.get("api/user/favorite/list", params, handler);
    }

    public static void getFollowReverse(long id, String type, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("id", id);
        params.put("type", type);
        ApiHttpClient.post("api/user/follow/reverse", params, handler);
    }

    public static void getQuestionList(String order, String pageToken, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();

        params.put("order", order);
        params.put("page", pageToken);
        TLog.error(params.toString());
        ApiHttpClient.get("api/qa/list", params, handler);
    }

    /**
     * 请求动弹详情
     *
     * @param id      动弹id
     * @param handler 回调
     */
    public static void getQuestionDetail(long id, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("id", id);
        ApiHttpClient.get("api/qa/detail", params, handler);
    }

    public static void getAnswers(long sourceId, int type, int order, String pageToken, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("sourceId", sourceId);
        params.put("type", type);
        params.put("order", order);
        params.put("page", pageToken);
        ApiHttpClient.get("api/answer/list", params, handler);
    }

    public static void getAnswerDetail(long id, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("id", id);
        ApiHttpClient.get("api/answer/detail", params, handler);
    }


    /**
     * 问答的回答, 顶\踩
     *
     * @param sid     source id 问答的id
     * @param cid     回答的id
     * @param opt     操作类型 0:取消, 1:顶, 2:踩
     * @param handler
     */
    public static void answerVote(long sid, long cid, int opt, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("sourceId", sid);
        params.put("commentId", cid);
        params.put("voteOpt", opt);
        ApiHttpClient.post("api/answer/vote", params, handler);
    }


    public static void getUserInfo(long uid, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        ApiHttpClient.get("api/user",params, handler);
    }

    public static void getProfileInfo(TextHttpResponseHandler handler) {
        ApiHttpClient.get("api/user/profile/info", handler);
    }

    public static void updateUserIcon(File file, TextHttpResponseHandler handler) {
        if (file == null) return;
        RequestParams params = new RequestParams();
        try {
            params.put("portrait", file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ApiHttpClient.post("api/user/profile/upload", params, handler);

    }

    public static void uploadUser(String nickname, TextHttpResponseHandler handler){
        RequestParams params = new RequestParams();
        params.put("nickname", nickname);
        ApiHttpClient.post("api/user/profile/upload", params, handler);
    }

    public static void uploadNickName(String nickname, TextHttpResponseHandler handler){
        RequestParams params = new RequestParams();
        params.put("nickname", nickname);
        ApiHttpClient.post("api/user/profile/upload", params, handler);
    }

    public static void uploadGander(int gender, TextHttpResponseHandler handler){
        RequestParams params = new RequestParams();
        params.put("gender", gender);
        ApiHttpClient.post("api/user/profile/upload", params, handler);
    }

    public static void uploadSuffix(String suffix, TextHttpResponseHandler handler){
        RequestParams params = new RequestParams();
        params.put("suffix", suffix);
        ApiHttpClient.post("api/user/profile/upload", params, handler);
    }


    public static void getNoticeList( String pageToken, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("page", pageToken);
        ApiHttpClient.get("api/user/notice/list",params, handler);
    }

    /**
     * 获取当前的新消息数量
     *
     * @param handler TextHttpResponseHandler
     */
    public static void getNotice(TextHttpResponseHandler handler) {
        ApiHttpClient.get("api/user/notice/stat", handler);
    }

    /**
     * 清理消息
     *
     * @param handler TextHttpResponseHandler
     */
    public static void clearNotice(int flag, TextHttpResponseHandler handler) {
        ApiHttpClient.post("api/user/notice/clear",
                new RequestParams("clearFlag", flag),
                handler);
    }


    public static void getAttentList( String pageToken, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();

        // params.put("order", order);
        params.put("page", pageToken);
        TLog.error(params.toString());
        ApiHttpClient.get("api/attent/list", params, handler);
    }

    /**
     * 获取用户私信列表
     *
     * @param pageToken pageToken
     * @param handler   回调
     */
    public static void getUserMessageList(String pageToken, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("page", pageToken);
        ApiHttpClient.get("api/user/message/list", params, handler);
    }

    public static void getUserCollectionList(String pageToken,long userId,  AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("page", pageToken);
        params.put("user_id", userId);
        ApiHttpClient.get("api/user/favorite/list", params, handler);
    }

    public static void getUserFollowQuestionList(String pageToken, long userId, AsyncHttpResponseHandler handler) {
        if (userId <= 0) return;
        RequestParams params = new RequestParams();
        params.put("page", pageToken);
        params.put("user_id", userId);
        ApiHttpClient.get("api/user/follow/question", params, handler);
    }

    public static void getUserFollowUserList(String pageToken, long userId, AsyncHttpResponseHandler handler) {
        if (userId <= 0) return;
        RequestParams params = new RequestParams();
        params.put("page", pageToken);
        params.put("user_id", userId);
        ApiHttpClient.get("api/user/follow/user", params, handler);
    }

    /**
     * 请求用户问答列表
     *
     * @param pageToken 请求上下页数据令牌
     * @param handler   AsyncHttpResponseHandler
     */
    public static void getUserQuestionList(String pageToken, long userId, AsyncHttpResponseHandler handler) {
        if (userId <= 0) return;
        RequestParams params = new RequestParams();
        params.put("page", pageToken);
        params.put("user_id", userId);
        ApiHttpClient.get("api/user/question/list", params, handler);
    }

    public static void getUserTopicList(String pageToken, long userId, AsyncHttpResponseHandler handler) {
        if (userId <= 0) return;
        RequestParams params = new RequestParams();
        params.put("page", pageToken);
        params.put("user_id", userId);
        ApiHttpClient.get("api/user/topic/list", params, handler);
    }

    public static void getUserTopicCommentList(String pageToken, long userId, AsyncHttpResponseHandler handler) {
        if (userId <= 0) return;
        RequestParams params = new RequestParams();
        params.put("page", pageToken);
        params.put("user_id", userId);
        ApiHttpClient.get("api/user/topic/comments", params, handler);
    }

    public static void getUserAnswerList(String pageToken, long userId, AsyncHttpResponseHandler handler) {
        if (userId <= 0) return;
        RequestParams params = new RequestParams();
        params.put("page", pageToken);
        params.put("user_id", userId);
        ApiHttpClient.get("api/user/answer/list", params, handler);
    }

    /**
     * 评论我的列表
     *
     * @param pageToken pageToken
     * @param handler   回调
     */
    public static void getMsgCommentList(String pageToken, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("page", pageToken);
        ApiHttpClient.get("api/user/comment/list", params, handler);
    }


    /**
     * 发布动弹
     * 链接 http://doc.oschina.net/app_v2?t=105522
     *
     * @param content     内容
     * @param handler     回调
     */
    public static void pubQuestion(int type, String title,String content, AsyncHttpResponseHandler handler) {
        if (TextUtils.isEmpty(title))
            throw new NullPointerException("title is not null.");
        //if (TextUtils.isEmpty(content))
        //    throw new NullPointerException("content is not null.");
        RequestParams params = new RequestParams();
        params.put("type", type);
        params.put("title", title);
        params.put("content", content);

        ApiHttpClient.post("api/qa/create", params, handler);
    }

    public static void pubAnswer(long qid, String content, AsyncHttpResponseHandler handler) {

        RequestParams params = new RequestParams();
        params.put("qid", qid);
        params.put("content", content);

        ApiHttpClient.post("api/answer/create", params, handler);
    }


    /**
     * 上传图片接口
     * http://doc.oschina.net/app_v2?t=105508
     *
     * @param token     上传口令，单次口令最多上传9张图片。
     * @param imagePath 图片地址
     * @param handler   回调
     */
    public static void uploadImage(String token, String imagePath, AsyncHttpResponseHandler handler) {
        if (TextUtils.isEmpty(imagePath))
            throw new NullPointerException("imagePath is not null.");
        RequestParams params = new RequestParams();
        params.put("token", token);
        try {
            params.put("resource", new File(imagePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ApiHttpClient.post("api/image/upload", params, handler);
    }

    public static void updateAttach(File file, TextHttpResponseHandler handler) {
        if (file == null) return;
        RequestParams params = new RequestParams();
        try {
            params.put("file", file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ApiHttpClient.post("api/attach/upload", params, handler);

    }

    public static void pubTopic(long topic_id, int type, String title,String content, AsyncHttpResponseHandler handler) {

        RequestParams params = new RequestParams();
        params.put("topic_id", topic_id);
        params.put("type", type);
        params.put("title", title);
        params.put("content", content);

        ApiHttpClient.post("api/topic/create", params, handler);
    }
}
