package com.example.lml.easyphoto.dikuai;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lml.easyphoto.FarmerInfo.houseHolds.HouseHoldsBean;
import com.example.lml.easyphoto.db.OpenHelperManager;

import java.util.ArrayList;
import java.util.List;

public class MassifSnapService {
    private Context mContext;

    public MassifSnapService(Context context) {
        mContext = context;
    }

    public void insertMassifSnap(ContentValues value) {
        SQLiteDatabase db = OpenHelperManager.getClaimHelper(mContext).getWritableDatabase();
        db.insert("MASSIFSNAP", null, value);
        OpenHelperManager.closeClaimHelper();
    }

    /***
     * @param
     * @return
     */
    public List<MassifSnapBean> getMassifSnap(String provinceName, String cityName, String countyName, String countryName, String villageName,  String certificateId) {
        List<MassifSnapBean> list = new ArrayList<MassifSnapBean>();
        StringBuffer s = new StringBuffer();
        s.append("select *  from MASSIFSNAP where provinceName = '" + provinceName + "' and cityName = '" + cityName + "' and countyName = '" + countyName + "' and countryName = '" + countryName + "' and villageName = '" + villageName + "' and certificateId = '" + certificateId + "'");
        Cursor c = null;
        try {
            SQLiteDatabase db = OpenHelperManager.getClaimHelper(mContext).getReadableDatabase();
            c = db.rawQuery(s.toString(), null);
            if (c != null && c.getCount() > 0) {
                MassifSnapBean tb = null;
                while (c.moveToNext()) {
                    tb = new MassifSnapBean();
                    tb.setId(c.getString(c.getColumnIndex("ID")));
                    tb.setProvinceName(c.getString(c.getColumnIndex("provinceName")));
                    tb.setCityName(c.getString(c.getColumnIndex("cityName")));
                    tb.setCountyName(c.getString(c.getColumnIndex("countyName")));
                    tb.setCountryName(c.getString(c.getColumnIndex("countryName")));
                    tb.setVillageName(c.getString(c.getColumnIndex("villageName")));
                    tb.setCertificateId(c.getString(c.getColumnIndex("certificateId")));
                    list.add(tb);
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
        return list;
    }

    //删除
    public boolean deleteCustomer(String provinceName, String cityName, String countyName, String countryName, String villageName,  String certificateId) {
        SQLiteDatabase db = OpenHelperManager.getClaimHelper(mContext).getWritableDatabase();
        int a = db.delete("MASSIFSNAP", "provinceName=? and cityName=? and countyName=? and countryName=? and villageName=? and certificateId=?", new String[]{provinceName,cityName,countyName,countryName,villageName,certificateId});
        OpenHelperManager.closeClaimHelper();
        if (a > 0) {
            return true;
        } else {
            return false;
        }
    }
}
