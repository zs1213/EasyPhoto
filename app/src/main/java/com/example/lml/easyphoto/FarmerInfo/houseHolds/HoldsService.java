package com.example.lml.easyphoto.FarmerInfo.houseHolds;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lml.easyphoto.db.OpenHelperManager;
import com.example.lml.easyphoto.dikuai.DKBean;
import com.example.lml.easyphoto.dikuai.DKPointBean;
import com.example.lml.easyphoto.dikuai.DKService;

import java.util.ArrayList;
import java.util.List;

public class HoldsService {
    private Context mContext;
    private DKService dkService;

    public HoldsService(Context context,DKService dkService) {
        mContext = context;
        this.dkService = dkService;
    }

    //保存分户清单数据
    public void insertCustomer(ContentValues value) {
        SQLiteDatabase db = OpenHelperManager.getClaimHelper(mContext).getWritableDatabase();
        db.insert("CUSTOMER", null, value);
        OpenHelperManager.closeClaimHelper();
    }

    /***
     *获取用户全部分户清单数据
     * @param
     * @return
     */
    public List<HouseHoldsBean> getCustomerList(String provinceName, String cityName, String countyName, String countryName, String villageName, String proposalNo, String certificateId) {
        List<HouseHoldsBean> list = new ArrayList<HouseHoldsBean>();
        StringBuffer s = new StringBuffer();
        s.append("select *  from CUSTOMER where province_name = '" + provinceName + "' and city_name = '" + cityName + "' and county_name = '" + countyName + "' and country_name = '" + countryName + "' and village_name = '" + villageName + "' and proposal_no = '" + proposalNo + "' and certificate_id = '" + certificateId + "'  order by id asc");

        Cursor c = null;
        try {
            SQLiteDatabase db = OpenHelperManager.getClaimHelper(mContext).getReadableDatabase();
            c = db.rawQuery(s.toString(), null);
            if (c != null && c.getCount() > 0) {
                HouseHoldsBean tb = null;
                while (c.moveToNext()) {
                    tb = new HouseHoldsBean();
                    tb.setId(c.getString(c.getColumnIndex("id")));
                    tb.setOrderNo(c.getInt(c.getColumnIndex("order_no")));
                    tb.setProvinceName(c.getString(c.getColumnIndex("province_name")));
                    tb.setCityName(c.getString(c.getColumnIndex("city_name")));
                    tb.setCountyName(c.getString(c.getColumnIndex("county_name")));
                    tb.setCountryName(c.getString(c.getColumnIndex("country_name")));
                    tb.setVillageName(c.getString(c.getColumnIndex("village_name")));
                    tb.setProposalNo(c.getString(c.getColumnIndex("proposal_no")));
                    tb.setInsuredMethod(c.getString(c.getColumnIndex("insured_method")));
                    tb.setVillageGroup(c.getString(c.getColumnIndex("village_group")));
                    tb.setPacketNo(c.getString(c.getColumnIndex("packet_no")));
                    tb.setInsuredName(c.getString(c.getColumnIndex("insured_name")));
                    tb.setCertificateType(c.getString(c.getColumnIndex("certificate_type")));
                    tb.setCertificateId(c.getString(c.getColumnIndex("certificate_id")));
                    tb.setPhone(c.getString(c.getColumnIndex("phone")));
                    tb.setMassifName(c.getString(c.getColumnIndex("massif_name")));
                    tb.setMassifType(c.getString(c.getColumnIndex("massif_type")));
                    tb.setCropName(c.getString(c.getColumnIndex("crop_name")));
                    tb.setInsureArea(c.getString(c.getColumnIndex("insure_area")));
                    tb.setBankCategories(c.getString(c.getColumnIndex("bank_categories")));
                    tb.setOpenBank(c.getString(c.getColumnIndex("open_bank")));
                    tb.setBankCardNo(c.getString(c.getColumnIndex("bank_card_no")));
                    tb.setSumQuantity(c.getString(c.getColumnIndex("sum_quantity")));
                    tb.setMassifNo(c.getString(c.getColumnIndex("massif_no")));
                    tb.setIsDestitute(c.getString(c.getColumnIndex("is_destitute")));
                    tb.setOfficeId(c.getString(c.getColumnIndex("office_id")));
                    tb.setCentralOffice(c.getString(c.getColumnIndex("central_office")));
                    tb.setSubmitStatus(c.getString(c.getColumnIndex("submit_status")));
                    tb.setCreateBy(c.getString(c.getColumnIndex("create_by")));
                    tb.setCreateTime(c.getString(c.getColumnIndex("create_time")));
                    tb.setUpdateTime(c.getString(c.getColumnIndex("update_time")));
                    tb.setYearNo(c.getInt(c.getColumnIndex("year_no")));
                    tb.setIsBusinessEntity(c.getString(c.getColumnIndex("isBusinessEntity")));
                    tb.setPolicyNumber(c.getString(c.getColumnIndex("policyNumber")));
                    tb.setChoose(false);
                    tb.setShow(false);
                    tb.setPlot(dkService.getIsPlot(tb.getId()));
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
     *获取全部用户分户清单数据
     * @param
     * @return
     */
    public List<HouseHoldsBean> getCustomerListNoPlot(String provinceName, String cityName, String countyName, String countryName, String villageName, String proposalNo, String certificateId) {
        List<HouseHoldsBean> list = new ArrayList<HouseHoldsBean>();
        StringBuffer s = new StringBuffer();
        s.append("select *  from CUSTOMER where province_name = '" + provinceName + "' and city_name = '" + cityName + "' and county_name = '" + countyName + "' and country_name = '" + countryName + "' and village_name = '" + villageName + "' and proposal_no = '" + proposalNo + "' and certificate_id = '" + certificateId + "'  order by massif_name ASC");
        Cursor c = null;
        try {
            SQLiteDatabase db = OpenHelperManager.getClaimHelper(mContext).getReadableDatabase();
            c = db.rawQuery(s.toString(), null);
            if (c != null && c.getCount() > 0) {
                HouseHoldsBean tb = null;
                while (c.moveToNext()) {
                    tb = new HouseHoldsBean();
                    tb.setId(c.getString(c.getColumnIndex("id")));
                    tb.setOrderNo(c.getInt(c.getColumnIndex("order_no")));
                    tb.setProvinceName(c.getString(c.getColumnIndex("province_name")));
                    tb.setCityName(c.getString(c.getColumnIndex("city_name")));
                    tb.setCountyName(c.getString(c.getColumnIndex("county_name")));
                    tb.setCountryName(c.getString(c.getColumnIndex("country_name")));
                    tb.setVillageName(c.getString(c.getColumnIndex("village_name")));
                    tb.setProposalNo(c.getString(c.getColumnIndex("proposal_no")));
                    tb.setInsuredMethod(c.getString(c.getColumnIndex("insured_method")));
                    tb.setVillageGroup(c.getString(c.getColumnIndex("village_group")));
                    tb.setPacketNo(c.getString(c.getColumnIndex("packet_no")));
                    tb.setInsuredName(c.getString(c.getColumnIndex("insured_name")));
                    tb.setCertificateType(c.getString(c.getColumnIndex("certificate_type")));
                    tb.setCertificateId(c.getString(c.getColumnIndex("certificate_id")));
                    tb.setPhone(c.getString(c.getColumnIndex("phone")));
                    tb.setMassifName(c.getString(c.getColumnIndex("massif_name")));
                    tb.setMassifType(c.getString(c.getColumnIndex("massif_type")));
                    tb.setCropName(c.getString(c.getColumnIndex("crop_name")));
                    tb.setInsureArea(c.getString(c.getColumnIndex("insure_area")));
                    tb.setBankCategories(c.getString(c.getColumnIndex("bank_categories")));
                    tb.setOpenBank(c.getString(c.getColumnIndex("open_bank")));
                    tb.setBankCardNo(c.getString(c.getColumnIndex("bank_card_no")));
                    tb.setSumQuantity(c.getString(c.getColumnIndex("sum_quantity")));
                    tb.setMassifNo(c.getString(c.getColumnIndex("massif_no")));
                    tb.setIsDestitute(c.getString(c.getColumnIndex("is_destitute")));
                    tb.setOfficeId(c.getString(c.getColumnIndex("office_id")));
                    tb.setCentralOffice(c.getString(c.getColumnIndex("central_office")));
                    tb.setSubmitStatus(c.getString(c.getColumnIndex("submit_status")));
                    tb.setCreateBy(c.getString(c.getColumnIndex("create_by")));
                    tb.setCreateTime(c.getString(c.getColumnIndex("create_time")));
                    tb.setUpdateTime(c.getString(c.getColumnIndex("update_time")));
                    tb.setYearNo(c.getInt(c.getColumnIndex("year_no")));
                    tb.setIsBusinessEntity(c.getString(c.getColumnIndex("isBusinessEntity")));
                    tb.setPolicyNumber(c.getString(c.getColumnIndex("policyNumber")));
                    tb.setChoose(false);
                    tb.setShow(false);
                    tb.setPlot(dkService.getIsPlot(tb.getId()));
                    if (!tb.isPlot()){
                        list.add(tb);
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
        return list;
    }
    /***
     *获取用户分户清单投保总和数据
     * @param
     * @return
     */
    public double getSumHolds(String provinceName, String cityName, String countyName, String countryName, String villageName, String proposalNo, String certificateId) {
        double result = 0.0;
        StringBuffer s = new StringBuffer();
        s.append("select SUM(insure_area) from CUSTOMER where province_name = '" + provinceName + "' and city_name = '" + cityName + "' and county_name = '" + countyName + "' and country_name = '" + countryName + "' and village_name = '" + villageName + "' and proposal_no = '" + proposalNo + "' and certificate_id = '" + certificateId + "'");
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
    public boolean hasData(String id) {
        StringBuffer s = new StringBuffer();
        s.append("select *  from CUSTOMER where id = '" + id + "'");
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
    public void updataCustomer(ContentValues value) {
        SQLiteDatabase db = OpenHelperManager.getClaimHelper(mContext).getWritableDatabase();
        db.update("CUSTOMER", value, "id=?", new String[]{value.get("id").toString()});
        OpenHelperManager.closeClaimHelper();
    }

    //删除
    public boolean deleteCustomer(String id) {
        SQLiteDatabase db = OpenHelperManager.getClaimHelper(mContext).getWritableDatabase();
        int a = db.delete("CUSTOMER", "id=?", new String[]{id});
        OpenHelperManager.closeClaimHelper();
        if (a > 0) {
            return true;
        } else {
            return false;
        }
    }
    //删除
    public boolean deleteCustomerByproNoAndCard(String proposal_no,String certificate_id) {
        SQLiteDatabase db = OpenHelperManager.getClaimHelper(mContext).getWritableDatabase();
        int a = db.delete("CUSTOMER", "proposal_no=? and certificate_id=?", new String[]{proposal_no,certificate_id});
        OpenHelperManager.closeClaimHelper();
        if (a > 0) {
            return true;
        } else {
            return false;
        }
    }
}
