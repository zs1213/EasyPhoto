package com.example.lml.easyphoto.main;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lml.easyphoto.addressSelect.AddressSelectBean;
import com.example.lml.easyphoto.dikuai.DKPointBean;
import com.example.lml.easyphoto.xzqyDb.XzqyOpenHelperManager;

import java.util.ArrayList;
import java.util.List;

public class XzqyService {
    private Context mContext;

    public XzqyService(Context context) {
        mContext = context;
    }
    public List<AddressSelectBean> getAddressList(String fXZDM, String Level) {
        List<AddressSelectBean> list = new ArrayList<AddressSelectBean>();
        StringBuffer s = new StringBuffer();
        switch (Level){
            case "1":
                s.append("SELECT DISTINCT province_code,province_name FROM base_area");
                break;
            case "2":
                s.append("SELECT DISTINCT city_code,city_name FROM base_area WHERE province_code = '"+fXZDM+"'");
                break;
            case "3":
                s.append("SELECT DISTINCT county_code,county_name FROM base_area WHERE city_code = '"+fXZDM+"'");
                break;
            case "4":
                s.append("SELECT DISTINCT country_code,country_name FROM base_area WHERE county_code = '"+fXZDM+"'");
                break;
            case "5":
                s.append("SELECT DISTINCT village_code,village_name FROM base_area WHERE country_code = '"+fXZDM+"'");
                break;
        }

        Cursor c = null;
        try {
            SQLiteDatabase db = XzqyOpenHelperManager.getClaimHelper(mContext).getReadableDatabase();
            c = db.rawQuery(s.toString(), null);
            if (c != null && c.getCount() > 0) {
                AddressSelectBean tb = null;
                while (c.moveToNext()) {
                    tb = new AddressSelectBean();
                    switch (Level){
                        case "1":
                            tb.setAreaCode(c.getString(c.getColumnIndex("province_code")));
                            tb.setAreaName(c.getString(c.getColumnIndex("province_name")));
                            break;
                        case "2":
                            tb.setAreaCode(c.getString(c.getColumnIndex("city_code")));
                            tb.setAreaName(c.getString(c.getColumnIndex("city_name")));
                            break;
                        case "3":
                            tb.setAreaCode(c.getString(c.getColumnIndex("county_code")));
                            tb.setAreaName(c.getString(c.getColumnIndex("county_name")));
                            break;
                        case "4":
                            tb.setAreaCode(c.getString(c.getColumnIndex("country_code")));
                            tb.setAreaName(c.getString(c.getColumnIndex("country_name")));
                            break;
                        case "5":
                            tb.setAreaCode(c.getString(c.getColumnIndex("village_code")));
                            tb.setAreaName(c.getString(c.getColumnIndex("village_name")));
                            break;
                    }
                    list.add(tb);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
            XzqyOpenHelperManager.closeClaimHelper();
        }
        return list;
    }
}
