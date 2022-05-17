package com.example.lml.easyphoto.camera;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import com.example.lml.easyphoto.db.OpenHelperManager;
import com.example.lml.easyphoto.tongji.TongjiBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by VIC on 2016/5/20.
 */
public class CameraService {

    private Context mContext;

    public CameraService(Context context) {
        mContext = context;
    }

    //保存图片
    public void insertPhoto(ContentValues value) {
        SQLiteDatabase db = OpenHelperManager.getClaimHelper(mContext).getWritableDatabase();
        db.insert("FREE_PHOTO", null, value);
        OpenHelperManager.closeClaimHelper();
    }


    public List<CameraPhotoBean> getPhotoList() {
        List<CameraPhotoBean> list = new ArrayList<CameraPhotoBean>();
        StringBuffer s = new StringBuffer();
        s.append("select *  from FREE_PHOTO order by ID");
        Cursor c = null;
        try {
            SQLiteDatabase db = OpenHelperManager.getClaimHelper(mContext).getReadableDatabase();
            c = db.rawQuery(s.toString(), null);
            if (c != null && c.getCount() > 0) {
                CameraPhotoBean tb = null;
                while (c.moveToNext()) {
                    tb = new CameraPhotoBean();
                    tb.setID(c.getString(c.getColumnIndex("ID")));
                    tb.setState(c.getString(c.getColumnIndex("state")));
                    tb.setFileName(c.getString(c.getColumnIndex("fileName")));
                    tb.setFilePath(c.getString(c.getColumnIndex("filePath")));
                    tb.setCreateTime(c.getString(c.getColumnIndex("createTime")));
                    tb.setProvince(c.getString(c.getColumnIndex("province")));
                    tb.setCity(c.getString(c.getColumnIndex("city")));
                    tb.setTownname(c.getString(c.getColumnIndex("townname")));
                    tb.setCountrysidename(c.getString(c.getColumnIndex("countrysidename")));
                    tb.setVillagename(c.getString(c.getColumnIndex("villagename")));
                    tb.setCorporateName(c.getString(c.getColumnIndex("corporateName")));
                    tb.setLon(c.getString(c.getColumnIndex("lon")));
                    tb.setLat(c.getString(c.getColumnIndex("lat")));
                    tb.setRemark(c.getString(c.getColumnIndex("remark")));
                    tb.setRiskReason(c.getString(c.getColumnIndex("riskReason")));
                    tb.setRiskCode(c.getString(c.getColumnIndex("riskCode")));
                    tb.setMassifType(c.getString(c.getColumnIndex("massifType")));
                    tb.setZhType(c.getString(c.getColumnIndex("zhType")));
                    tb.setFolderName(c.getString(c.getColumnIndex("folderName")));
                    tb.setIsTask(c.getString(c.getColumnIndex("isTask")));
                    tb.setLonAndlat(c.getString(c.getColumnIndex("lonAndlat")));
                    tb.setArea(c.getString(c.getColumnIndex("area")));
                    tb.setGisCode(c.getString(c.getColumnIndex("gisCode")));
                    tb.setServerPath(c.getString(c.getColumnIndex("serverPath")));
                    tb.setGisPath(c.getString(c.getColumnIndex("gisPath")));
                    tb.setCnpjcl(c.getString(c.getColumnIndex("cnpjcl")));
                    tb.setChuPing(c.getString(c.getColumnIndex("chuPing")));
                    tb.setSoilType(c.getString(c.getColumnIndex("soilType")));
                    tb.setSscd(c.getString(c.getColumnIndex("sscd")));
                    tb.setFolderName(c.getString(c.getColumnIndex("folderName")));
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

    public void updateCameraPhoto(String ID) {
        SQLiteDatabase db = OpenHelperManager.getClaimHelper(mContext).getReadableDatabase();
        String sql = "UPDATE FREE_PHOTO SET state = '1' WHERE ID = '" + ID + "'";
        if (StringUtil.notEmpty(ID)) {
            db.execSQL(sql);
        }
        OpenHelperManager.closeClaimHelper();
    }

    public void updateSetting(ContentValues values) {
        SQLiteDatabase db = OpenHelperManager.getClaimHelper(mContext).getReadableDatabase();
        db.update("FREE_PHOTO", values, "lon=? and lat=? ", new String[]{(String) values.get("lon"), (String) values.get("lat")});
        OpenHelperManager.closeClaimHelper();
    }

    public List<Map<String, Object>> getFilesNameList() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        StringBuffer s = new StringBuffer();
        s.append("SELECT DISTINCT(folderName) FROM FREE_PHOTO order by createTime desc");
        Cursor c = null;
        try {
            SQLiteDatabase db = OpenHelperManager.getClaimHelper(mContext).getReadableDatabase();
            c = db.rawQuery(s.toString(), null);
            if (c != null && c.getCount() > 0) {
                while (c.moveToNext()) {
                    String name = c.getString(c.getColumnIndex("folderName"));
                    Map<String, Object> map = new HashMap<>();
                    map.put("filesName", name);
                    map.put("item", getPhotoByFilesName(name));
                    list.add(map);
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

    public List<Map<String, String>> getGuaiDianList() {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        StringBuffer s = new StringBuffer();
        s.append("SELECT DISTINCT(lonAndlat),area FROM FREE_PHOTO order by createTime desc");
        Cursor c = null;
        try {
            SQLiteDatabase db = OpenHelperManager.getClaimHelper(mContext).getReadableDatabase();
            c = db.rawQuery(s.toString(), null);
            if (c != null && c.getCount() > 0) {
                while (c.moveToNext()) {
                    Map<String, String> map = new HashMap<>();
                    String name = c.getString(c.getColumnIndex("lonAndlat"));
                    if (name != null && !name.equals("") && !name.equals("null")) {
                        String area = c.getString(c.getColumnIndex("area"));
                        map.put("lonAndlat", name);
                        map.put("area", area);
                        list.add(map);
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

    public List<Map<String, String>> getGuaiDianListByFolderName(String folderName, String cropName) {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        StringBuffer s = new StringBuffer();
        switch (cropName) {
            case "全部":
                s.append("SELECT DISTINCT(lonAndlat),area FROM FREE_PHOTO where folderName = '" + folderName + "' order by createTime desc");
                break;
            default:
                s.append("SELECT DISTINCT(lonAndlat),area FROM FREE_PHOTO where folderName = '" + folderName + "' and zhType = '" + cropName + "' order by createTime desc");
                break;
        }
        Cursor c = null;
        try {
            SQLiteDatabase db = OpenHelperManager.getClaimHelper(mContext).getReadableDatabase();
            c = db.rawQuery(s.toString(), null);
            if (c != null && c.getCount() > 0) {
                while (c.moveToNext()) {
                    Map<String, String> map = new HashMap<>();
                    String name = c.getString(c.getColumnIndex("lonAndlat"));
                    if (name != null && !name.equals("") && !name.equals("null")) {
                        String area = c.getString(c.getColumnIndex("area"));
                        map.put("lonAndlat", name);
                        map.put("area", area);
                        list.add(map);
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

    public List<Map<String, String>> getDianListByFolderName(String folderName, String cropName) {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        StringBuffer s = new StringBuffer();
        switch (cropName) {
            case "全部":
                s.append("SELECT DISTINCT lon,lat FROM FREE_PHOTO where folderName = '" + folderName + "' and lonAndlat = '' order by createTime desc");
                break;
            default:
                s.append("SELECT DISTINCT lon,lat FROM FREE_PHOTO where folderName = '" + folderName + "' and zhType = '" + cropName + "' and lonAndlat = '' order by createTime desc");
                break;
        }
        Cursor c = null;
        try {
            SQLiteDatabase db = OpenHelperManager.getClaimHelper(mContext).getReadableDatabase();
            c = db.rawQuery(s.toString(), null);
            if (c != null && c.getCount() > 0) {
                while (c.moveToNext()) {
                    Map<String, String> map = new HashMap<>();
                    map.put("lon", c.getString(c.getColumnIndex("lon")));
                    map.put("lat", c.getString(c.getColumnIndex("lat")));
                    list.add(map);
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

    public List<CameraPhotoBean> getPhotoByFilesName(String filesName) {
        List<CameraPhotoBean> list = new ArrayList<CameraPhotoBean>();
        StringBuffer s = new StringBuffer();
        s.append("select *  from FREE_PHOTO where folderName = '" + filesName + "' order by createTime desc");
        Cursor c = null;
        try {
            SQLiteDatabase db = OpenHelperManager.getClaimHelper(mContext).getReadableDatabase();
            c = db.rawQuery(s.toString(), null);
            if (c != null && c.getCount() > 0) {
                CameraPhotoBean tb = null;
                while (c.moveToNext()) {
                    tb = new CameraPhotoBean();
                    tb.setID(c.getString(c.getColumnIndex("ID")));
                    tb.setState(c.getString(c.getColumnIndex("state")));
                    tb.setFileName(c.getString(c.getColumnIndex("fileName")));
                    tb.setFilePath(c.getString(c.getColumnIndex("filePath")));
                    tb.setCreateTime(c.getString(c.getColumnIndex("createTime")));
                    tb.setProvince(c.getString(c.getColumnIndex("province")));
                    tb.setCity(c.getString(c.getColumnIndex("city")));
                    tb.setTownname(c.getString(c.getColumnIndex("townname")));
                    tb.setCountrysidename(c.getString(c.getColumnIndex("countrysidename")));
                    tb.setVillagename(c.getString(c.getColumnIndex("villagename")));
                    tb.setCorporateName(c.getString(c.getColumnIndex("corporateName")));
                    tb.setLon(c.getString(c.getColumnIndex("lon")));
                    tb.setLat(c.getString(c.getColumnIndex("lat")));
                    tb.setRemark(c.getString(c.getColumnIndex("remark")));
                    tb.setRiskReason(c.getString(c.getColumnIndex("riskReason")));
                    tb.setRiskCode(c.getString(c.getColumnIndex("riskCode")));
                    tb.setMassifType(c.getString(c.getColumnIndex("massifType")));
                    tb.setZhType(c.getString(c.getColumnIndex("zhType")));
                    tb.setFolderName(c.getString(c.getColumnIndex("folderName")));
                    tb.setIsTask(c.getString(c.getColumnIndex("isTask")));
                    tb.setCnpjcl(c.getString(c.getColumnIndex("cnpjcl")));
                    tb.setChuPing(c.getString(c.getColumnIndex("chuPing")));
                    tb.setSoilType(c.getString(c.getColumnIndex("soilType")));
                    tb.setSscd(c.getString(c.getColumnIndex("sscd")));
                    tb.setLonAndlat(c.getString(c.getColumnIndex("lonAndlat")));
                    tb.setArea(c.getString(c.getColumnIndex("area")));
                    tb.setGisCode(c.getString(c.getColumnIndex("gisCode")));
                    tb.setServerPath(c.getString(c.getColumnIndex("serverPath")));
                    tb.setGisPath(c.getString(c.getColumnIndex("gisPath")));
                    tb.setFolderName(c.getString(c.getColumnIndex("folderName")));
                    tb.setChoose(false);
                    tb.setShow(false);
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

    public List<CameraPhotoBean> getPhotoByFilesName(String filesName, String zhType) {
        List<CameraPhotoBean> list = new ArrayList<CameraPhotoBean>();
        StringBuffer s = new StringBuffer();
        switch (zhType) {
            case "全部":
                s.append("select *  from FREE_PHOTO where folderName = '" + filesName + "' order by createTime desc");
                break;
            default:
                s.append("select *  from FREE_PHOTO where zhType = '" + zhType + "' and folderName = '" + filesName + "' order by createTime desc");
                break;
        }
        Cursor c = null;
        try {
            SQLiteDatabase db = OpenHelperManager.getClaimHelper(mContext).getReadableDatabase();
            c = db.rawQuery(s.toString(), null);
            if (c != null && c.getCount() > 0) {
                CameraPhotoBean tb = null;
                while (c.moveToNext()) {
                    tb = new CameraPhotoBean();
                    tb.setID(c.getString(c.getColumnIndex("ID")));
                    tb.setState(c.getString(c.getColumnIndex("state")));
                    tb.setFileName(c.getString(c.getColumnIndex("fileName")));
                    tb.setFilePath(c.getString(c.getColumnIndex("filePath")));
                    tb.setCreateTime(c.getString(c.getColumnIndex("createTime")));
                    tb.setProvince(c.getString(c.getColumnIndex("province")));
                    tb.setCity(c.getString(c.getColumnIndex("city")));
                    tb.setTownname(c.getString(c.getColumnIndex("townname")));
                    tb.setCountrysidename(c.getString(c.getColumnIndex("countrysidename")));
                    tb.setVillagename(c.getString(c.getColumnIndex("villagename")));
                    tb.setCorporateName(c.getString(c.getColumnIndex("corporateName")));
                    tb.setLon(c.getString(c.getColumnIndex("lon")));
                    tb.setLat(c.getString(c.getColumnIndex("lat")));
                    tb.setRemark(c.getString(c.getColumnIndex("remark")));
                    tb.setRiskReason(c.getString(c.getColumnIndex("riskReason")));
                    tb.setRiskCode(c.getString(c.getColumnIndex("riskCode")));
                    tb.setMassifType(c.getString(c.getColumnIndex("massifType")));
                    tb.setZhType(c.getString(c.getColumnIndex("zhType")));
                    tb.setFolderName(c.getString(c.getColumnIndex("folderName")));
                    tb.setIsTask(c.getString(c.getColumnIndex("isTask")));
                    tb.setCnpjcl(c.getString(c.getColumnIndex("cnpjcl")));
                    tb.setChuPing(c.getString(c.getColumnIndex("chuPing")));
                    tb.setSoilType(c.getString(c.getColumnIndex("soilType")));
                    tb.setSscd(c.getString(c.getColumnIndex("sscd")));
                    tb.setLonAndlat(c.getString(c.getColumnIndex("lonAndlat")));
                    tb.setArea(c.getString(c.getColumnIndex("area")));
                    tb.setGisCode(c.getString(c.getColumnIndex("gisCode")));
                    tb.setServerPath(c.getString(c.getColumnIndex("serverPath")));
                    tb.setGisPath(c.getString(c.getColumnIndex("gisPath")));
                    tb.setFolderName(c.getString(c.getColumnIndex("folderName")));
                    tb.setChoose(false);
                    tb.setShow(false);
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

    public String selectLastPhoto() {
        String result = "";
        StringBuffer s = new StringBuffer();
        s.append("SELECT * FROM FREE_PHOTO order by ID desc limit 0,1");
        Cursor c = null;
        try {
            SQLiteDatabase db = OpenHelperManager.getClaimHelper(mContext).getReadableDatabase();
            c = db.rawQuery(s.toString(), null);
            if (c != null && c.getCount() > 0) {
                while (c.moveToNext()) {
                    result = c.getString(c.getColumnIndex("filePath"));
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

    //删除已提交任务
    public void deleteNoFile(String id) {
        SQLiteDatabase db = OpenHelperManager.getClaimHelper(mContext).getWritableDatabase();
        db.delete("FREE_PHOTO", " ID=? ", new String[]{id});
        OpenHelperManager.closeClaimHelper();
    }
    public void deleteFile(String filePath) {
        SQLiteDatabase db = OpenHelperManager.getClaimHelper(mContext).getWritableDatabase();
        db.delete("FREE_PHOTO", " filePath=? ", new String[]{filePath});
        OpenHelperManager.closeClaimHelper();
    }
    public String[] getQuyuList(String name, String level) {
        String[] str = null;
        StringBuffer s = new StringBuffer();
        switch (level) {
            case "1":
                s.append("SELECT DISTINCT(city) FROM FREE_PHOTO where province = '" + name + "' order by createTime desc");
                break;
            case "2":
                s.append("SELECT DISTINCT(townname) FROM FREE_PHOTO where city = '" + name + "' order by createTime desc");
                break;
            case "3":
                s.append("SELECT DISTINCT(countrysidename) FROM FREE_PHOTO where townname = '" + name + "' order by createTime desc");
                break;
            case "4":
                s.append("SELECT DISTINCT(villagename) FROM FREE_PHOTO where countrysidename = '" + name + "' order by createTime desc");
                break;
        }
        Cursor c = null;
        try {
            SQLiteDatabase db = OpenHelperManager.getClaimHelper(mContext).getReadableDatabase();
            c = db.rawQuery(s.toString(), null);
            if (c != null && c.getCount() > 0) {
                str = new String[c.getCount()];
                int position = 0;
                while (c.moveToNext()) {
                    switch (level) {
                        case "1":
                            str[position] = c.getString(c.getColumnIndex("city"));
                            break;
                        case "2":
                            str[position] = c.getString(c.getColumnIndex("townname"));
                            break;
                        case "3":
                            str[position] = c.getString(c.getColumnIndex("countrysidename"));
                            break;
                        case "4":
                            str[position] = c.getString(c.getColumnIndex("villagename"));
                            break;
                    }
                    position++;
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
        return str;
    }

    public List<TongjiBean> getFilesNameListStr(String province,String city,String townname,String countrysidename, String villagename, String type,String level) {
        List<TongjiBean> list = new ArrayList<TongjiBean>();
        List<TongjiBean> list0 = new ArrayList<TongjiBean>();
        List<TongjiBean> list1 = new ArrayList<TongjiBean>();
        StringBuffer s = new StringBuffer();
        switch (type) {
            case "全部":
                switch (level){
                    case "5":
                        s.append("SELECT DISTINCT(folderName) FROM FREE_PHOTO where province = '"+province+"' and city = '"+city+"' and townname = '"+townname+"' and  countrysidename = '" + countrysidename + "' and villagename = '" + villagename + "' order by createTime desc");
                        break;
                    case "4":
                        s.append("SELECT DISTINCT(folderName) FROM FREE_PHOTO where province = '"+province+"' and city = '"+city+"' and townname = '"+townname+"' and  countrysidename = '" + countrysidename + "'  order by createTime desc");
                        break;
                    case "3":
                        s.append("SELECT DISTINCT(folderName) FROM FREE_PHOTO where province = '"+province+"' and city = '"+city+"' and townname = '"+townname+"' order by createTime desc");
                        break;
                    case "2":
                        s.append("SELECT DISTINCT(folderName) FROM FREE_PHOTO where province = '"+province+"' and city = '"+city+"' order by createTime desc");
                        break;
                    case "1":
                        s.append("SELECT DISTINCT(folderName) FROM FREE_PHOTO where province = '"+province+"' order by createTime desc");
                        break;
                }
                break;
            default:
                switch (level){
                    case "5":
                        s.append("SELECT DISTINCT(folderName) FROM FREE_PHOTO where  zhType = '" + type + "' and province = '"+province+"' and city = '"+city+"' and townname = '"+townname+"' and   countrysidename = '" + countrysidename + "' and villagename = '" + villagename + "' order by createTime desc");
                        break;
                    case "4":
                        s.append("SELECT DISTINCT(folderName) FROM FREE_PHOTO where  zhType = '" + type + "' and province = '"+province+"' and city = '"+city+"' and townname = '"+townname+"' and   countrysidename = '" + countrysidename + "' order by createTime desc");
                        break;
                    case "3":
                        s.append("SELECT DISTINCT(folderName) FROM FREE_PHOTO where  zhType = '" + type + "' and province = '"+province+"' and city = '"+city+"' and townname = '"+townname+"' order by createTime desc");
                        break;
                    case "2":
                        s.append("SELECT DISTINCT(folderName) FROM FREE_PHOTO where  zhType = '" + type + "' and province = '"+province+"' and city = '"+city+"' order by createTime desc");
                        break;
                    case "1":
                        s.append("SELECT DISTINCT(folderName) FROM FREE_PHOTO where  zhType = '" + type + "' and province = '"+province+"' order by createTime desc");
                        break;
                }
                break;
        }
        Cursor c = null;
        try {
            SQLiteDatabase db = OpenHelperManager.getClaimHelper(mContext).getReadableDatabase();
            c = db.rawQuery(s.toString(), null);
            if (c != null && c.getCount() > 0) {
                while (c.moveToNext()) {
                    TongjiBean bean = new TongjiBean();
                    String name = c.getString(c.getColumnIndex("folderName"));
                    bean.setFoldName(name);
                    bean.setChoose(false);
                    bean.setNoSubmit(isNoSubmit(name));
                    if (bean.isNoSubmit()){
                        list0.add(bean);
                    }else {
                        list1.add(bean);
                    }
                }
                list.addAll(list0);
                list.addAll(list1);
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
    public boolean isNoSubmit(String filesName) {
        boolean isNoSubmit = false;
        StringBuffer s = new StringBuffer();
        s.append("select *  from FREE_PHOTO where folderName = '" + filesName + "' order by createTime desc");
        Cursor c = null;
        try {
            SQLiteDatabase db = OpenHelperManager.getClaimHelper(mContext).getReadableDatabase();
            c = db.rawQuery(s.toString(), null);
            if (c != null && c.getCount() > 0) {
                while (c.moveToNext()) {
                    String state = c.getString(c.getColumnIndex("state"));
                    if(state.equals("0")){
                        isNoSubmit = true;
                        return isNoSubmit;
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
        return isNoSubmit;
    }
}
