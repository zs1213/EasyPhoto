package com.example.lml.easyphoto.customize;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lml.easyphoto.FarmerInfo.houseHolds.HoldsService;
import com.example.lml.easyphoto.FarmerInfo.houseHolds.HouseHoldsBean;
import com.example.lml.easyphoto.db.OpenHelperManager;
import com.example.lml.easyphoto.dikuai.finish.FinishBean;
import com.example.lml.easyphoto.history.HistoryBean;
import com.example.lml.easyphoto.sign.SignService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomizeDKService {
    private Context mContext;

    public CustomizeDKService(Context context) {
        mContext = context;
    }

    //保存地块
    public void insertDKInfo(ContentValues value) {
        SQLiteDatabase db = OpenHelperManager.getClaimHelper(mContext).getWritableDatabase();
        db.insert("CUSTOMIZE_INFO", null, value);
        OpenHelperManager.closeClaimHelper();
    }

    //保存地块点
    public void insertDKPoint(ContentValues value) {
        SQLiteDatabase db = OpenHelperManager.getClaimHelper(mContext).getWritableDatabase();
        db.insert("CUSTOMIZE_POINT", null, value);
        OpenHelperManager.closeClaimHelper();
    }
    private double formatDouble2(double d) {
        BigDecimal bigDecimal = new BigDecimal(d);
        double bg = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return bg;
    }
    /***
     *
     * @param state -1:全部  0:未提交   1:已提交
     * @return
     */
    public List<CustomizeDKBean> getDKList(String state) {
        List<CustomizeDKBean> list = new ArrayList<CustomizeDKBean>();
        List<CustomizeDKBean> list0 = new ArrayList<CustomizeDKBean>();
        List<CustomizeDKBean> list1 = new ArrayList<CustomizeDKBean>();
        StringBuffer s = new StringBuffer();
        switch (state) {
            case "-1":
                s.append("select *  from CUSTOMIZE_INFO order by ID desc");
                break;
            case "0":
                s.append("select *  from CUSTOMIZE_INFO where state = '" + state + "' order by ID desc");
                break;
            case "1":
                s.append("select *  from CUSTOMIZE_INFO where state = '" + state + "'order by ID desc");
                break;
        }

        Cursor c = null;
        try {
            SQLiteDatabase db = OpenHelperManager.getClaimHelper(mContext).getReadableDatabase();
            c = db.rawQuery(s.toString(), null);
            if (c != null && c.getCount() > 0) {
                CustomizeDKBean tb = null;
                while (c.moveToNext()) {
                    tb = new CustomizeDKBean();
                    tb.setID(c.getString(c.getColumnIndex("ID")));
                    tb.setTaskId(c.getString(c.getColumnIndex("taskId")));
                    tb.setUserName(c.getString(c.getColumnIndex("userName")));
                    tb.setCrop(c.getString(c.getColumnIndex("crop")));
                    tb.setDkName(c.getString(c.getColumnIndex("dkName")));
                    tb.setDrawArea(c.getString(c.getColumnIndex("drawArea")));
                    tb.setChangeArea(c.getString(c.getColumnIndex("changeArea")));
                    tb.setState(c.getString(c.getColumnIndex("state")));
                    tb.setCreateTime(c.getString(c.getColumnIndex("createTime")));
                    tb.setSubTime(c.getString(c.getColumnIndex("subTime")));
                    tb.setUserId(c.getString(c.getColumnIndex("userId")));
                    tb.setProvince(c.getString(c.getColumnIndex("province")));
                    tb.setCity(c.getString(c.getColumnIndex("city")));
                    tb.setTown(c.getString(c.getColumnIndex("town")));
                    tb.setCountryside(c.getString(c.getColumnIndex("countryside")));
                    tb.setVillage(c.getString(c.getColumnIndex("village")));
                    tb.setProvinceCode(c.getString(c.getColumnIndex("provinceCode")));
                    tb.setCityCode(c.getString(c.getColumnIndex("cityCode")));
                    tb.setTownCode(c.getString(c.getColumnIndex("townCode")));
                    tb.setCountrysideCode(c.getString(c.getColumnIndex("countrysideCode")));
                    tb.setVillageCode(c.getString(c.getColumnIndex("villageCode")));
                    tb.setProposalNo(c.getString(c.getColumnIndex("proposalNo")));
                    tb.setCertificateId(c.getString(c.getColumnIndex("certificateId")));
                    tb.setCustomerIds(c.getString(c.getColumnIndex("customerIds")));
                    tb.setIsBusinessEntity(c.getString(c.getColumnIndex("isBusinessEntity")));
                    tb.setPolicyNumber(c.getString(c.getColumnIndex("policyNumber")));
                    tb.setRemarks(c.getString(c.getColumnIndex("remarks")));
                    tb.setmList(getDKPointList(tb.getTaskId()));
                    tb.setSelsect(false);
                    if(state.equals("-1")){
                        if (tb.getState().equals("0")){
                            list0.add(tb);
                        }else {
                            list1.add(tb);
                        }
                    }else{
                        list.add(tb);
                    }
                }
                if (state.equals("-1")){
                    list.addAll(list0);
                    list.addAll(list1);
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
    /***
     *
     * @param
     * @return
     */
    public List<CustomizeDKBean> getDKUploadList(String proposalNo, String certificateId) {
        List<CustomizeDKBean> list = new ArrayList<CustomizeDKBean>();
        StringBuffer s = new StringBuffer();
        s.append("select *  from CUSTOMIZE_INFO where state = '0' and proposalNo = '"+proposalNo+"' and certificateId = '"+certificateId+"'");
        Cursor c = null;
        try {
            SQLiteDatabase db = OpenHelperManager.getClaimHelper(mContext).getReadableDatabase();
            c = db.rawQuery(s.toString(), null);
            if (c != null && c.getCount() > 0) {
                CustomizeDKBean tb = null;
                while (c.moveToNext()) {
                    tb = new CustomizeDKBean();
                    tb.setID(c.getString(c.getColumnIndex("ID")));
                    tb.setTaskId(c.getString(c.getColumnIndex("taskId")));
                    tb.setUserName(c.getString(c.getColumnIndex("userName")));
                    tb.setCrop(c.getString(c.getColumnIndex("crop")));
                    tb.setDkName(c.getString(c.getColumnIndex("dkName")));
                    tb.setDrawArea(c.getString(c.getColumnIndex("drawArea")));
                    tb.setChangeArea(c.getString(c.getColumnIndex("changeArea")));
                    tb.setState(c.getString(c.getColumnIndex("state")));
                    tb.setCreateTime(c.getString(c.getColumnIndex("createTime")));
                    tb.setSubTime(c.getString(c.getColumnIndex("subTime")));
                    tb.setUserId(c.getString(c.getColumnIndex("userId")));
                    tb.setProvince(c.getString(c.getColumnIndex("province")));
                    tb.setCity(c.getString(c.getColumnIndex("city")));
                    tb.setTown(c.getString(c.getColumnIndex("town")));
                    tb.setCountryside(c.getString(c.getColumnIndex("countryside")));
                    tb.setVillage(c.getString(c.getColumnIndex("village")));
                    tb.setProvinceCode(c.getString(c.getColumnIndex("provinceCode")));
                    tb.setCityCode(c.getString(c.getColumnIndex("cityCode")));
                    tb.setTownCode(c.getString(c.getColumnIndex("townCode")));
                    tb.setCountrysideCode(c.getString(c.getColumnIndex("countrysideCode")));
                    tb.setVillageCode(c.getString(c.getColumnIndex("villageCode")));
                    tb.setProposalNo(c.getString(c.getColumnIndex("proposalNo")));
                    tb.setCertificateId(c.getString(c.getColumnIndex("certificateId")));
                    tb.setCustomerIds(c.getString(c.getColumnIndex("customerIds")));
                    tb.setIsBusinessEntity(c.getString(c.getColumnIndex("isBusinessEntity")));
                    tb.setPolicyNumber(c.getString(c.getColumnIndex("policyNumber")));
                    tb.setRemarks(c.getString(c.getColumnIndex("remarks")));
                    tb.setmList(getDKPointList(tb.getTaskId()));
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
    public boolean getIsHave(String taskId){
        StringBuffer s = new StringBuffer();
        s.append("select *  from CUSTOMIZE_INFO where taskId = '" + taskId + "'");
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
    /***
     *很具taskid 获取DKBean
     */
    public CustomizeDKBean getDKBean(String taskId) {
        StringBuffer s = new StringBuffer();
        s.append("select *  from CUSTOMIZE_INFO where taskId = '" + taskId + "' ");
        Cursor c =  null;
        try {
            SQLiteDatabase db = OpenHelperManager.getClaimHelper(mContext).getReadableDatabase();
            c = db.rawQuery(s.toString(), null);
            if (c != null && c.getCount() > 0) {
                CustomizeDKBean tb = null;
                while (c.moveToNext()) {
                    tb = new CustomizeDKBean();
                    tb.setID(c.getString(c.getColumnIndex("ID")));
                    tb.setTaskId(c.getString(c.getColumnIndex("taskId")));
                    tb.setUserName(c.getString(c.getColumnIndex("userName")));
                    tb.setCrop(c.getString(c.getColumnIndex("crop")));
                    tb.setDkName(c.getString(c.getColumnIndex("dkName")));
                    tb.setDrawArea(c.getString(c.getColumnIndex("drawArea")));
                    tb.setChangeArea(c.getString(c.getColumnIndex("changeArea")));
                    tb.setState(c.getString(c.getColumnIndex("state")));
                    tb.setCreateTime(c.getString(c.getColumnIndex("createTime")));
                    tb.setSubTime(c.getString(c.getColumnIndex("subTime")));
                    tb.setUserId(c.getString(c.getColumnIndex("userId")));
                    tb.setProvince(c.getString(c.getColumnIndex("province")));
                    tb.setCity(c.getString(c.getColumnIndex("city")));
                    tb.setTown(c.getString(c.getColumnIndex("town")));
                    tb.setCountryside(c.getString(c.getColumnIndex("countryside")));
                    tb.setVillage(c.getString(c.getColumnIndex("village")));
                    tb.setProvinceCode(c.getString(c.getColumnIndex("provinceCode")));
                    tb.setCityCode(c.getString(c.getColumnIndex("cityCode")));
                    tb.setTownCode(c.getString(c.getColumnIndex("townCode")));
                    tb.setCountrysideCode(c.getString(c.getColumnIndex("countrysideCode")));
                    tb.setVillageCode(c.getString(c.getColumnIndex("villageCode")));
                    tb.setProposalNo(c.getString(c.getColumnIndex("proposalNo")));
                    tb.setCertificateId(c.getString(c.getColumnIndex("certificateId")));
                    tb.setCustomerIds(c.getString(c.getColumnIndex("customerIds")));
                    tb.setIsBusinessEntity(c.getString(c.getColumnIndex("isBusinessEntity")));
                    tb.setPolicyNumber(c.getString(c.getColumnIndex("policyNumber")));
                    tb.setRemarks(c.getString(c.getColumnIndex("remarks")));
                    tb.setSelsect(false);
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

    //查询地块点
    public List<CustomizeDKPointBean> getDKPointList(String taskId) {
        List<CustomizeDKPointBean> list = new ArrayList<CustomizeDKPointBean>();
        StringBuffer s = new StringBuffer();
        s.append("select *  from CUSTOMIZE_POINT where taskId = '"+taskId+"' order by CAST(number AS UNSIGNED)");
        Cursor c = null;
        try {
            SQLiteDatabase db = OpenHelperManager.getClaimHelper(mContext).getReadableDatabase();
            c = db.rawQuery(s.toString(), null);
            if (c != null && c.getCount() > 0) {
                CustomizeDKPointBean tb = null;
                while (c.moveToNext()) {
                    tb = new CustomizeDKPointBean();
                    tb.setID(c.getString(c.getColumnIndex("ID")));
                    tb.setTaskId(c.getString(c.getColumnIndex("taskId")));
                    tb.setLon(c.getString(c.getColumnIndex("lon")));
                    tb.setLat(c.getString(c.getColumnIndex("lat")));
                    tb.setType(c.getString(c.getColumnIndex("type")));
                    tb.setNumber(c.getString(c.getColumnIndex("number")));
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
    public boolean getIsPlot(String customerId){
        StringBuffer s = new StringBuffer();
        s.append("select *  from CUSTOMIZE_INFO where customerIds like '%" + customerId + "%'");
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
    //修改地块
    public void updataDKInfo(ContentValues value) {
        SQLiteDatabase db = OpenHelperManager.getClaimHelper(mContext).getWritableDatabase();
        db.update("CUSTOMIZE_INFO", value, "proposalNo=? and certificateId=?", new String[]{value.get("proposalNo").toString(),value.get("certificateId").toString()});
        OpenHelperManager.closeClaimHelper();
    }
    //通过关联地块修改地块
    public void updataDKInfoByCustomerIds(ContentValues value,String customerIds) {
        SQLiteDatabase db = OpenHelperManager.getClaimHelper(mContext).getWritableDatabase();
        db.update("CUSTOMIZE_INFO", value, "proposalNo=? and certificateId=? and customerIds=?", new String[]{value.get("proposalNo").toString(),value.get("certificateId").toString(),customerIds});
        OpenHelperManager.closeClaimHelper();
    }
    //修改地块changeArea面积
    public boolean updataDKInfoState(ContentValues value) {
        SQLiteDatabase db = OpenHelperManager.getClaimHelper(mContext).getWritableDatabase();
        int a = db.update("CUSTOMIZE_INFO", value, "taskId=?", new String[]{value.get("taskId").toString()});
        OpenHelperManager.closeClaimHelper();
        if (a==1){
            return true;
        }
        return false;
    }
    //删除地块
    public void deleteDKInfo(String taskId) {
        SQLiteDatabase db = OpenHelperManager.getClaimHelper(mContext).getWritableDatabase();
        db.delete("CUSTOMIZE_INFO","taskId=?", new String[]{taskId});
        db.delete("CUSTOMIZE_POINT","taskId=?", new String[]{taskId});
        OpenHelperManager.closeClaimHelper();
    }
    //删除地块
    public void deleteDKInfoByCustomerIds(String customerIds) {
        StringBuffer s = new StringBuffer();
        s.append("select * from CUSTOMIZE_INFO where customerIds = '"+customerIds+"'");
        Cursor c = null;
        try {
            SQLiteDatabase db = OpenHelperManager.getClaimHelper(mContext).getReadableDatabase();
            c = db.rawQuery(s.toString(), null);
            if (c != null && c.getCount() > 0) {
                while (c.moveToNext()) {
                    String taskId = c.getString(c.getColumnIndex("taskId"));
                    deleteDKInfo(taskId);
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
    }
}
