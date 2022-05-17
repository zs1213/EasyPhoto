package com.example.lml.easyphoto.sign;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lml.easyphoto.FarmerInfo.houseHolds.HouseHoldsBean;
import com.example.lml.easyphoto.db.OpenHelperManager;
import com.example.lml.easyphoto.dikuai.DKService;

import java.util.ArrayList;
import java.util.List;

public class SignService {
    private Context mContext;

    public SignService(Context context) {
        mContext = context;
    }

    public void insertSign(ContentValues value) {
        SQLiteDatabase db = OpenHelperManager.getClaimHelper(mContext).getWritableDatabase();
        db.insert("SIGN", null, value);
        OpenHelperManager.closeClaimHelper();
    }

    /***
     * @param
     * @return
     */
    public SignBean getSignBean( String proposalNo, String certificateId) {
        StringBuffer s = new StringBuffer();
        s.append("select *  from SIGN where proposalNo = '" + proposalNo + "' and certificateId = '" + certificateId + "'");
        Cursor c = null;
        try {
            SQLiteDatabase db = OpenHelperManager.getClaimHelper(mContext).getReadableDatabase();
            c = db.rawQuery(s.toString(), null);
            if (c != null && c.getCount() > 0) {
                SignBean tb = null;
                while (c.moveToNext()) {
                    tb = new SignBean();
                    tb.setID(c.getString(c.getColumnIndex("ID")));
                    tb.setProposalNo(c.getString(c.getColumnIndex("proposalNo")));
                    tb.setCertificateId(c.getString(c.getColumnIndex("certificateId")));
                    tb.setCreateTime(c.getString(c.getColumnIndex("createTime")));
                    tb.setPath(c.getString(c.getColumnIndex("path")));
                    return tb;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
            OpenHelperManager.closeClaimHelper();
        }
        return null;
    }

    public boolean hasSign( String proposalNo, String certificateId) {
        StringBuffer s = new StringBuffer();
        s.append("select *  from SIGN where proposalNo = '" + proposalNo + "' and certificateId = '" + certificateId + "'");
        Cursor c = null;
        try {
            SQLiteDatabase db = OpenHelperManager.getClaimHelper(mContext).getReadableDatabase();
            c = db.rawQuery(s.toString(), null);
            if (c != null && c.getCount() > 0) {
                while (c.moveToNext()) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
            OpenHelperManager.closeClaimHelper();
        }
        return false;
    }
    //修改
    public void updataSign(ContentValues value) {
        SQLiteDatabase db = OpenHelperManager.getClaimHelper(mContext).getWritableDatabase();
        db.update("SIGN", value, "ID=?", new String[]{value.get("id").toString()});
        OpenHelperManager.closeClaimHelper();
    }

    //删除
    public boolean deleteSign(String id) {
        SQLiteDatabase db = OpenHelperManager.getClaimHelper(mContext).getWritableDatabase();
        int a = db.delete("SIGN", "ID=?", new String[]{id});
        OpenHelperManager.closeClaimHelper();
        if (a > 0) {
            return true;
        } else {
            return false;
        }
    }
}
