package me.ticknick.weixzz.dao.statusdetail;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import com.google.gson.Gson;

import me.ticknick.weixzz.Constants;
import me.ticknick.weixzz.dao.HttpClientUtils;
import me.ticknick.weixzz.dao.UrlConstants;
import me.ticknick.weixzz.dao.WeiboParameters;
import me.ticknick.weixzz.database.table.RepostStatusTable;
import me.ticknick.weixzz.model.RepostListModel;

/**
 * Created by Finderlo on 2016/8/24.
 */

public class RepostStatusSdDAO extends BaseStatusDetailDao<RepostListModel> {

    public long mStatusId;

    public RepostStatusSdDAO(Context context, long statusId) {
        super(context);
        mStatusId = statusId;
    }

    @Override
    public void cache() {
        mDatabase.beginTransaction();
        mDatabase.delete(RepostStatusTable.NAME,
                RepostStatusTable.STATUS_ID+" = ?",
                new String[]{String.valueOf(mStatusId)});
        ContentValues values = new ContentValues();
        values.put(RepostStatusTable.STATUS_ID,mStatusId);
        values.put(RepostStatusTable.JSON,new Gson().toJson(mModelList));
        mDatabase.insert(RepostStatusTable.NAME,null,values);
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
    }

    @Override
    public RepostListModel load() {
        return getRepostByStatusId(mStatusId, Constants.HOME_TIMELINE_PAGE_SIZE,++mCurrentPage
        );
    }

    @Override
    public Cursor query() {
        return mDatabase.rawQuery("select * from " + RepostStatusTable.NAME + " where "
                + RepostStatusTable.STATUS_ID + " =  '" + mStatusId + "'",
                null);
    }

    @Override
    protected Class<? extends RepostListModel> getListClass() {
        return RepostListModel.class;
    }

    public RepostListModel getRepostByStatusId(long statusId, int count , int page){
        WeiboParameters param = new WeiboParameters();
        param.put("count",50);
//        param.put("page",page);
        param.put("id",statusId);

        try {
            String json = HttpClientUtils.doGetRequstWithAceesToken(UrlConstants.REPOST_TIMELINE,param);
            Log.d(TAG, "getRepostByStatusId: json:"+json);
            return new Gson().fromJson(json,RepostListModel.class);
        } catch (Exception e) {
            Log.e(TAG, "getCommentByStatusId: get repost timeline fail"+e.getClass().getSimpleName());
            e.printStackTrace();
            return null;
        }
    }
}
