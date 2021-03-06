package me.ticknick.weixzz.dao.timeline;



import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.google.gson.Gson;

import me.ticknick.weixzz.Constants;
import me.ticknick.weixzz.dao.HttpClientUtils;
import me.ticknick.weixzz.dao.UrlConstants;
import me.ticknick.weixzz.dao.WeiboParameters;
import me.ticknick.weixzz.database.table.CommentTimelineTable;
import me.ticknick.weixzz.model.CommentListModel;

import static me.ticknick.weixzz.BuildConfig.DEBUG;


/**
 * Created by Finderlo on 2016/8/19.
 * 评论时间线微博dao层
 */

public class CommentTlDao extends BaseTimelineDao<CommentListModel> {

    public static final String TYPE_COMMENT_BY_ME = " commentByMe ";
    public static final String TYPE_COMMENT_TO_ME = " commentToMe ";
    public static final String TYPE_COMMENT_ALL = " commentsAll ";

    public String mType = TYPE_COMMENT_ALL;

    public CommentTlDao(Context context, String commentType) {
        super(context);

        if (TYPE_COMMENT_BY_ME.equals(commentType)){
            mType = TYPE_COMMENT_BY_ME;
        }else if (TYPE_COMMENT_TO_ME.equals(commentType)){
            mType = TYPE_COMMENT_TO_ME;
        }else mType = TYPE_COMMENT_ALL;

    }

    @Override
    public void cache() {
        mDatabase.beginTransaction();
        mDatabase.delete(CommentTimelineTable.NAME, CommentTimelineTable.TYPE + " = ? ", new String[]{mType});
        ContentValues values = new ContentValues();
        values.put(CommentTimelineTable.TYPE, mType);
        values.put(CommentTimelineTable.JSON, new Gson().toJson(mListModel));
        mDatabase.insert(CommentTimelineTable.NAME, null, values);
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
    }

    @Override
    public CommentListModel load() {
        CommentListModel listModel;

        if (mType.equals(TYPE_COMMENT_TO_ME)) {
            listModel = getCommentToMeTimeLine(Constants.HOME_TIMELINE_PAGE_SIZE, ++mCurrentPage);
        } else if (mType.equals(TYPE_COMMENT_BY_ME)) {
            listModel = getCommentByMeTimeLine(Constants.HOME_TIMELINE_PAGE_SIZE, ++mCurrentPage);
        } else {
            listModel = getCommentTimeLine(Constants.HOME_TIMELINE_PAGE_SIZE, ++mCurrentPage);
        }

        return listModel;
    }

    @Override
    public void loadMore() {

    }

    private CommentListModel getCommentToMeTimeLine(int count, int page) {
        WeiboParameters param = new WeiboParameters();
        param.put("count", count);
        param.put("page", page);

        try {
            String json = HttpClientUtils.doGetRequstWithAceesToken(UrlConstants.COMMENTS_TO_ME_TIMELINE, param);
            return new Gson().fromJson(json, CommentListModel.class);
        } catch (Exception e) {
            if (DEBUG) {
                Log.d(TAG, "Can't get comment to me timeline, " + e.getClass().getSimpleName());
                Log.d(TAG, Log.getStackTraceString(e));
                e.printStackTrace();
            }
            return null;
        }
    }

    private CommentListModel getCommentByMeTimeLine(int count, int page) {
        WeiboParameters param = new WeiboParameters();
        param.put("count", count);
        param.put("page", page);

        try {
            String json = HttpClientUtils.doGetRequstWithAceesToken(UrlConstants.COMMENTS_BY_ME_TIMELINE, param);
            return new Gson().fromJson(json, CommentListModel.class);
        } catch (Exception e) {
            if (DEBUG) {
                Log.d(TAG, "Can't get comment by me timeline, " + e.getClass().getSimpleName());
                Log.d(TAG, Log.getStackTraceString(e));
                e.printStackTrace();
            }
            return null;
        }
    }

    private CommentListModel getCommentTimeLine(int count, int page) {
        WeiboParameters param = new WeiboParameters();
        param.put("count", count);
        param.put("page", page);

        try {
            String json = HttpClientUtils.doGetRequstWithAceesToken(UrlConstants.COMMENTS_TIMELINE, param);
            return new Gson().fromJson(json, CommentListModel.class);
        } catch (Exception e) {
            if (DEBUG) {
                Log.d(TAG, "Can't get comment home timeline, " + e.getClass().getSimpleName());
                Log.d(TAG, Log.getStackTraceString(e));
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public Cursor query() {
        return mDatabase.rawQuery(
                "select * from " + CommentTimelineTable.NAME + " where " + CommentTimelineTable.TYPE + " = '" + mType + "' ",
                null);
    }

    @Override
    protected Class<? extends CommentListModel> getListClass() {
        return CommentListModel.class;
    }
}
