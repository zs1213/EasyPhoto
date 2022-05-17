package com.example.lml.easyphoto.dikuai;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.opengl.GLSurfaceView;

import com.example.lml.easyphoto.FarmerInfo.houseHolds.HoldsService;
import com.example.lml.easyphoto.FarmerInfo.houseHolds.HouseHoldsBean;
import com.example.lml.easyphoto.camera.CameraPhotoBean;
import com.example.lml.easyphoto.db.OpenHelperManager;
import com.example.lml.easyphoto.dikuai.finish.FinishBean;
import com.example.lml.easyphoto.history.HistoryBean;
import com.example.lml.easyphoto.sign.SignService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.convert.impl.BeanConverter;

public class DKService {
    private Context mContext;

    public DKService(Context context) {
        mContext = context;
    }

    //保存地块
    public void insertDKInfo(ContentValues value) {
        SQLiteDatabase db = OpenHelperManager.getClaimHelper(mContext).getWritableDatabase();
        db.insert("DK_INFO", null, value);
        OpenHelperManager.closeClaimHelper();
    }

    //保存地块点
    public void insertDKPoint(ContentValues value) {
        SQLiteDatabase db = OpenHelperManager.getClaimHelper(mContext).getWritableDatabase();
        db.insert("DK_POINT", null, value);
        OpenHelperManager.closeClaimHelper();
    }
    //获取FinishBean列表
    public List<FinishBean> getFinishList(String provinceName, String cityName, String countyName, String countryName, String villageName, String proposalNo, String certificateId){
        List<FinishBean> mlst = new ArrayList<>();
        StringBuffer s = new StringBuffer();
        s.append("select * from DK_INFO where province = '" + provinceName + "' and city = '" + cityName + "' and town = '" + countyName + "' and countryside = '" + countryName + "' and village = '" + villageName + "' and proposalNo = '" + proposalNo + "' and certificateId = '" + certificateId + "' GROUP BY customerIds");
        Cursor c = null;
        try {
            SQLiteDatabase db = OpenHelperManager.getClaimHelper(mContext).getReadableDatabase();
            c = db.rawQuery(s.toString(), null);
            if (c != null && c.getCount() > 0) {
                FinishBean tb = null;
                while (c.moveToNext()) {
                    tb = new FinishBean();
                    tb.setUserName(c.getString(c.getColumnIndex("userName")));
                    tb.setCustomerIds(c.getString(c.getColumnIndex("customerIds")));
                    tb.setMassifName(c.getString(c.getColumnIndex("dkName")));
                    tb.setInsureArea("");
                    tb.setChoose(false);
                    tb.setPlotArea(""+formatDouble2(getSumPlotByCustomerIds(provinceName, cityName, countyName, countryName, villageName, proposalNo, certificateId,tb.getCustomerIds())));
                    mlst.add(tb);
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
        return mlst;
    }
    private double formatDouble2(double d) {
        BigDecimal bigDecimal = new BigDecimal(d);
        double bg = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return bg;
    }
    /**获取HistoryBean列表
     *
     * @param flag -1 全部  0：未标绘完成 1：未签字 2：已签字 3：已提交
     * **/
    public List<HistoryBean> getHistoryList(String flag, HoldsService holdsService, SignService signService){
        List<HistoryBean> mlist = new ArrayList<>();
        List<HistoryBean> mlist0 = new ArrayList<>();
        List<HistoryBean> mlist1 = new ArrayList<>();
        List<HistoryBean> mlist2 = new ArrayList<>();
        List<HistoryBean> mlist3 = new ArrayList<>();
        StringBuffer s = new StringBuffer();
        s.append("select * from DK_INFO GROUP BY certificateId,proposalNo");
        Cursor c = null;
        try {
            SQLiteDatabase db = OpenHelperManager.getClaimHelper(mContext).getReadableDatabase();
            c = db.rawQuery(s.toString(), null);
            if (c != null && c.getCount() > 0) {
                HistoryBean tb = null;
                while (c.moveToNext()) {
                    tb = new HistoryBean();
                    tb.setUserName(c.getString(c.getColumnIndex("userName")));
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
                    tb.setCreatTime(c.getString(c.getColumnIndex("createTime")));
                    tb.setCertificateId(c.getString(c.getColumnIndex("certificateId")));
                    double insureArea = holdsService.getSumHolds(c.getString(c.getColumnIndex("province")), c.getString(c.getColumnIndex("city")), c.getString(c.getColumnIndex("town")), c.getString(c.getColumnIndex("countryside")), c.getString(c.getColumnIndex("village")), tb.getProposalNo(), tb.getCertificateId());
                    double plotArea = getSumPlot(c.getString(c.getColumnIndex("province")), c.getString(c.getColumnIndex("city")), c.getString(c.getColumnIndex("town")), c.getString(c.getColumnIndex("countryside")), c.getString(c.getColumnIndex("village")), tb.getProposalNo(), tb.getCertificateId());
                    if ((insureArea - (insureArea * 0.02)) <= plotArea && plotArea <= (insureArea + (insureArea * 0.02))) {
                        plotArea = insureArea;
                    }
                    tb.setInsureArea(""+insureArea);
                    tb.setPlotArea(""+plotArea);
                    //0：未标绘完成 1：未签字 2：已签字 3：已提交
                    if (c.getString(c.getColumnIndex("state")).equals("1")){
                        tb.setState("3");
                        mlist3.add(tb);
                    }else {
                        if (signService.hasSign(tb.getProposalNo(),tb.getCertificateId())){
                            tb.setState("2");
                            mlist2.add(tb);
                        }else {
                            if (insureArea == plotArea&&getUserDrawFinish(tb.getProposalNo(),tb.getCertificateId())){
                                tb.setState("1");
                                mlist1.add(tb);
                            }else {
                                tb.setState("0");
                                mlist0.add(tb);
                            }
                        }
                    }

                }
                switch (flag){
                    case "-1":
                        mlist.addAll(mlist0);
                        mlist.addAll(mlist1);
                        mlist.addAll(mlist2);
                        mlist.addAll(mlist3);
                        break;
                    case "0":
                        mlist.addAll(mlist0);
                        break;
                    case "1":
                        mlist.addAll(mlist1);
                        break;
                    case "2":
                        mlist.addAll(mlist2);
                        break;
                    case "3":
                        mlist.addAll(mlist3);
                        break;
                    default:
                        break;
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
        return mlist;
    }
    /**获取保存和提交数量
     *
     * **/
    public Map<String,String> getNumberMap(){
        int keep = 0;
        int sub = 0;
        Map<String,String> map = new HashMap<>();
        StringBuffer s = new StringBuffer();
        s.append("select * from DK_INFO GROUP BY certificateId,proposalNo");
        Cursor c = null;
        try {
            SQLiteDatabase db = OpenHelperManager.getClaimHelper(mContext).getReadableDatabase();
            c = db.rawQuery(s.toString(), null);
            if (c != null && c.getCount() > 0) {
                while (c.moveToNext()) {
                    //0：未标绘完成 1：未签字 2：已签字 3：已提交
                    if (c.getString(c.getColumnIndex("state")).equals("1")){
                        sub++;
                    }else {
                        keep++;
                    }
                }

            }
            map.put("sub",""+sub);
            map.put("keep",""+keep);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
            OpenHelperManager.closeClaimHelper();
        }
        return map;
    }
    public boolean getUserDrawFinish(String proposalNo,String certificateId){
        StringBuffer s = new StringBuffer();
        s.append("select *  from CUSTOMER where  proposal_no = '" + proposalNo + "' and certificate_id = '" + certificateId + "'  order by id asc");
        Cursor c = null;
        try {
            SQLiteDatabase db = OpenHelperManager.getClaimHelper(mContext).getReadableDatabase();
            c = db.rawQuery(s.toString(), null);
            if (c != null && c.getCount() > 0) {
                HouseHoldsBean tb = null;
                while (c.moveToNext()) {
                    tb = new HouseHoldsBean();
                    tb.setId(c.getString(c.getColumnIndex("id")));
                    if (!getIsPlot(tb.getId())){
                        return false;
                    }
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
        return true;
    }
    /***
     *获取用户标绘总和数据
     * @param
     * @return
     */
    public double getSumPlot(String provinceName, String cityName, String countyName, String countryName, String villageName, String proposalNo, String certificateId) {
        double result = 0.0;
        StringBuffer s = new StringBuffer();
        s.append("select SUM(changeArea) from DK_INFO where province = '" + provinceName + "' and city = '" + cityName + "' and town = '" + countyName + "' and countryside = '" + countryName + "' and village = '" + villageName + "' and proposalNo = '" + proposalNo + "' and certificateId = '" + certificateId + "'");
        Cursor c = null;
        try {
            SQLiteDatabase db = OpenHelperManager.getClaimHelper(mContext).getReadableDatabase();
            c = db.rawQuery(s.toString(), null);
            if (c != null && c.getCount() > 0) {
                HouseHoldsBean tb = null;
                while (c.moveToNext()) {
                    result = c.getDouble(0);
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
        return result;
    }
    /***
     *获取用户标绘总和数据byCustomerIds
     * @param
     * @return
     */
    public double getSumPlotByCustomerIds(String provinceName, String cityName, String countyName, String countryName, String villageName, String proposalNo, String certificateId,String customerIds) {
        double result = 0.0;
        StringBuffer s = new StringBuffer();
        s.append("select SUM(changeArea) from DK_INFO where province = '" + provinceName + "' and city = '" + cityName + "' and town = '" + countyName + "' and countryside = '" + countryName + "' and village = '" + villageName + "' and proposalNo = '" + proposalNo + "' and certificateId = '" + certificateId + "' and customerIds = '"+customerIds+"'");
        Cursor c = null;
        try {
            SQLiteDatabase db = OpenHelperManager.getClaimHelper(mContext).getReadableDatabase();
            c = db.rawQuery(s.toString(), null);
            if (c != null && c.getCount() > 0) {
                HouseHoldsBean tb = null;
                while (c.moveToNext()) {
                    result = c.getDouble(0);
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
        return result;
    }
    public DKBean getDKBeanLimit(String provinceName, String cityName, String countyName, String countryName, String villageName, String proposalNo, String certificateId,String customerIds) {
        StringBuffer s = new StringBuffer();
        s.append("select * from DK_INFO where province = '" + provinceName + "' and city = '" + cityName + "' and town = '" + countyName + "' and countryside = '" + countryName + "' and village = '" + villageName + "' and proposalNo = '" + proposalNo + "' and certificateId = '" + certificateId + "' and customerIds = '"+customerIds+"' ORDER BY changeArea DESC limit 0,1");
        Cursor c = null;
        try {
            SQLiteDatabase db = OpenHelperManager.getClaimHelper(mContext).getReadableDatabase();
            c = db.rawQuery(s.toString(), null);
            if (c != null && c.getCount() > 0) {
                DKBean tb = null;
                while (c.moveToNext()) {
                    tb = new DKBean();
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
    public DKBean getDKBeanLimit(String proposalNo, String certificateId) {
        StringBuffer s = new StringBuffer();
        s.append("select * from DK_INFO where  proposalNo = '" + proposalNo + "' and certificateId = '" + certificateId + "' limit 0,1");
        Cursor c = null;
        try {
            SQLiteDatabase db = OpenHelperManager.getClaimHelper(mContext).getReadableDatabase();
            c = db.rawQuery(s.toString(), null);
            if (c != null && c.getCount() > 0) {
                DKBean tb = null;
                while (c.moveToNext()) {
                    tb = new DKBean();
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
    /***
     *
     * @param state -1:全部  0:未提交   1:已提交
     * @return
     */
    public List<DKBean> getDKList(String state) {
        List<DKBean> list = new ArrayList<DKBean>();
        StringBuffer s = new StringBuffer();
        switch (state) {
            case "-1":
                s.append("select *  from DK_INFO order by ID desc");
                break;
            case "0":
                s.append("select *  from DK_INFO where state = '" + state + "' order by ID desc");
                break;
            case "1":
                s.append("select *  from DK_INFO where state = '" + state + "'order by ID desc");
                break;
        }

        Cursor c = null;
        try {
            SQLiteDatabase db = OpenHelperManager.getClaimHelper(mContext).getReadableDatabase();
            c = db.rawQuery(s.toString(), null);
            if (c != null && c.getCount() > 0) {
                DKBean tb = null;
                while (c.moveToNext()) {
                    tb = new DKBean();
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
                    tb.setmList(getDKPointList(tb.getTaskId()));
                    tb.setSelsect(false);
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
    public List<DKBean> getDKByUser(String certificateId,String proposalNo) {
        List<DKBean> list = new ArrayList<DKBean>();
        StringBuffer s = new StringBuffer();
        s.append("select *  from DK_INFO where certificateId = '" + certificateId + "' and proposalNo = '"+proposalNo+"'");
        Cursor c = null;
        try {
            SQLiteDatabase db = OpenHelperManager.getClaimHelper(mContext).getReadableDatabase();
            c = db.rawQuery(s.toString(), null);
            if (c != null && c.getCount() > 0) {
                DKBean tb = null;
                while (c.moveToNext()) {
                    tb = new DKBean();
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
                    tb.setmList(getDKPointList(tb.getTaskId()));
                    tb.setSelsect(false);
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
    /***
     *
     * @param
     * @return
     */
    public List<DKBean> getDKUploadList(String proposalNo,String certificateId) {
        List<DKBean> list = new ArrayList<DKBean>();
        StringBuffer s = new StringBuffer();
        s.append("select *  from DK_INFO where state = '0' and proposalNo = '"+proposalNo+"' and certificateId = '"+certificateId+"'");
        Cursor c = null;
        try {
            SQLiteDatabase db = OpenHelperManager.getClaimHelper(mContext).getReadableDatabase();
            c = db.rawQuery(s.toString(), null);
            if (c != null && c.getCount() > 0) {
                DKBean tb = null;
                while (c.moveToNext()) {
                    tb = new DKBean();
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
        s.append("select *  from DK_INFO where taskId = '" + taskId + "'");
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
    public DKBean getDKBean(String taskId) {
        StringBuffer s = new StringBuffer();
        s.append("select *  from DK_INFO where taskId = '" + taskId + "' ");
        Cursor c =  null;
        try {
            SQLiteDatabase db = OpenHelperManager.getClaimHelper(mContext).getReadableDatabase();
            c = db.rawQuery(s.toString(), null);
            if (c != null && c.getCount() > 0) {
                DKBean tb = null;
                while (c.moveToNext()) {
                    tb = new DKBean();
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
    public List<DKPointBean> getDKPointList(String taskId) {
        List<DKPointBean> list = new ArrayList<DKPointBean>();
        StringBuffer s = new StringBuffer();
        s.append("select *  from DK_POINT where taskId = '"+taskId+"' order by CAST(number AS UNSIGNED)");
        Cursor c = null;
        try {
            SQLiteDatabase db = OpenHelperManager.getClaimHelper(mContext).getReadableDatabase();
            c = db.rawQuery(s.toString(), null);
            if (c != null && c.getCount() > 0) {
                DKPointBean tb = null;
                while (c.moveToNext()) {
                    tb = new DKPointBean();
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
        s.append("select *  from DK_INFO where customerIds like '%" + customerId + "%'");
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
        db.update("DK_INFO", value, "proposalNo=? and certificateId=?", new String[]{value.get("proposalNo").toString(),value.get("certificateId").toString()});
        OpenHelperManager.closeClaimHelper();
    }
    //通过关联地块修改地块
    public void updataDKInfoByCustomerIds(ContentValues value,String customerIds) {
        SQLiteDatabase db = OpenHelperManager.getClaimHelper(mContext).getWritableDatabase();
        db.update("DK_INFO", value, "proposalNo=? and certificateId=? and customerIds=?", new String[]{value.get("proposalNo").toString(),value.get("certificateId").toString(),customerIds});
        OpenHelperManager.closeClaimHelper();
    }
    //修改地块changeArea面积
    public boolean updataDKInfoChangeArea(ContentValues value) {
        SQLiteDatabase db = OpenHelperManager.getClaimHelper(mContext).getWritableDatabase();
        int a = db.update("DK_INFO", value, "taskId=?", new String[]{value.get("taskId").toString()});
        OpenHelperManager.closeClaimHelper();
        if (a==1){
            return true;
        }
        return false;
    }
    //删除地块
    public void deleteDKInfo(String taskId) {
        SQLiteDatabase db = OpenHelperManager.getClaimHelper(mContext).getWritableDatabase();
        db.delete("DK_INFO","taskId=?", new String[]{taskId});
        db.delete("DK_POINT","taskId=?", new String[]{taskId});
        OpenHelperManager.closeClaimHelper();
    }
    //删除地块
    public void deleteDKInfoByCustomerIds(String customerIds) {
        StringBuffer s = new StringBuffer();
        s.append("select * from DK_INFO where customerIds = '"+customerIds+"'");
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
