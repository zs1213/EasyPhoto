package com.example.lml.easyphoto.gismapSelect;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lml.easyphoto.FarmerInfo.FarmerInfoAdapter;
import com.example.lml.easyphoto.FarmerInfo.FarmerInfoBean;
import com.example.lml.easyphoto.FarmerInfo.houseHolds.HoldsService;
import com.example.lml.easyphoto.FarmerInfo.houseHolds.HouseHoldsActivity;
import com.example.lml.easyphoto.FarmerInfo.houseHolds.HouseHoldsBean;
import com.example.lml.easyphoto.R;
import com.example.lml.easyphoto.addressSelect.AddressSelectBean;
import com.example.lml.easyphoto.camera.PhotoMain;
import com.example.lml.easyphoto.dikuai.DKBean;
import com.example.lml.easyphoto.dikuai.DKService;
import com.example.lml.easyphoto.dikuai.DiKuaiActivity;
import com.example.lml.easyphoto.login.LoginActivity;
import com.example.lml.easyphoto.main.MenuActivity;
import com.example.lml.easyphoto.okHttpUtils.DOkHttp;
import com.example.lml.easyphoto.util.Configure;
import com.example.lml.easyphoto.util.LoadingDialog;
import com.example.lml.easyphoto.util.SharePreferencesTools;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

public class GismapSelectActivity extends Activity implements View.OnClickListener {
    private TextView tv_province,tv_city, tv_county, tv_country, tv_village;
    private LinearLayout lin_province,lin_city, lin_county, lin_country, lin_village;
    private String provinceCode;
    private String provinceName;
    private String cityCode;
    private String cityName;
    private String countyCode;
    private String countyName;
    private String countryCode;
    private String countryName;
    private String villageCode;
    private String villageName;
    private List<AddressSelectBean> provinceList;
    private List<AddressSelectBean> cityList;
    private List<AddressSelectBean> countyList;
    private List<AddressSelectBean> countryList;
    private List<AddressSelectBean> villageList;
    private String provinceStr[];
    private String cityStr[];
    private String countyStr[];
    private String countryStr[];
    private String villageStr[];
    private boolean showFlag = false;
    private Gson gson;
    private LoadingDialog dialog;//?????????
    private Button btn_sure;
    private String provinceCodeSp;
    private String provinceNameSp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gismap_select);
        initView();
        initData();
    }


    private void initView() {
        tv_province = findViewById(R.id.gismap_tv_province);
        tv_city = findViewById(R.id.gismap_tv_city);
        tv_county = findViewById(R.id.gismap_tv_county);
        tv_country = findViewById(R.id.gismap_tv_town);
        tv_village = findViewById(R.id.gismap_tv_village);
        lin_province = findViewById(R.id.gismap_lin_province);
        lin_city = findViewById(R.id.gismap_lin_city);
        lin_county = findViewById(R.id.gismap_lin_county);
        lin_country = findViewById(R.id.gismap_lin_town);
        lin_village = findViewById(R.id.gismap_lin_village);
        btn_sure = findViewById(R.id.gismap_btn_sure);
        lin_province.setOnClickListener(this);
        lin_city.setOnClickListener(this);
        lin_county.setOnClickListener(this);
        lin_country.setOnClickListener(this);
        lin_village.setOnClickListener(this);
        btn_sure.setOnClickListener(this);
        provinceCodeSp = SharePreferencesTools.getValue(GismapSelectActivity.this, "easyPhoto", "province", "");
        provinceNameSp = SharePreferencesTools.getValue(GismapSelectActivity.this, "easyPhoto", "provinceName", "");

    }

    private void initData() {
        provinceCode = getIntent().getStringExtra("provinceCode");
        provinceName = getIntent().getStringExtra("provinceName");
        cityCode = getIntent().getStringExtra("cityCode");
        cityName = getIntent().getStringExtra("cityName");
        countyCode = getIntent().getStringExtra("countyCode");
        countyName = getIntent().getStringExtra("countyName");
        countryCode = getIntent().getStringExtra("countryCode");
        countryName = getIntent().getStringExtra("countryName");
        villageCode = getIntent().getStringExtra("villageCode");
        villageName = getIntent().getStringExtra("villageName");
        tv_province.setText(provinceName);
        tv_city.setText(cityName);
        tv_county.setText(countyName);
        tv_country.setText(countryName);
        tv_village.setText(villageName);
        gson = new Gson();
        dialog = new LoadingDialog(this);
        provinceList = new ArrayList<>();
        cityList = new ArrayList<>();
        countyList = new ArrayList<>();
        countryList = new ArrayList<>();
        villageList = new ArrayList<>();
        if (provinceCode.equals(provinceCodeSp)){
            provinceStr = new String[]{provinceName};
            AddressSelectBean bean = new AddressSelectBean();
            bean.setAreaCode(provinceCode);
            bean.setAreaName(provinceName);
            provinceList.add(bean);
        }else {
            provinceStr = new String[]{provinceName,provinceNameSp};
            AddressSelectBean bean = new AddressSelectBean();
            bean.setAreaCode(provinceCode);
            bean.setAreaName(provinceName);
            AddressSelectBean bean1 = new AddressSelectBean();
            bean1.setAreaCode(provinceCodeSp);
            bean1.setAreaName(provinceNameSp);
            provinceList.add(bean);
            provinceList.add(bean1);
        }
        if (cityCode.equals("")) {
            getXzquData(Configure.cityInfo + "?provinceCode=" + provinceCode, 2);
        }
    }

    public void goBack(View view) {
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.gismap_lin_province:
                AlertDialog.Builder builders = new AlertDialog.Builder(GismapSelectActivity.this);
                builders.setItems(provinceStr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AddressSelectBean bean = provinceList.get(which);
                        if (!bean.getAreaName().equals(tv_province.getText().toString())) {
                            provinceName = bean.getAreaName();
                            provinceCode = bean.getAreaCode();
                            tv_province.setText(provinceName);
                            cityList.clear();
                            countyList.clear();
                            countryList.clear();
                            villageList.clear();
                            cityStr = null;
                            countyStr = null;
                            countryStr = null;
                            villageStr = null;
                            tv_city.setText("");
                            tv_county.setText("");
                            tv_country.setText("");
                            tv_village.setText("");
                            cityCode = "";
                            countyCode = "";
                            countryCode = "";
                            villageCode = "";
                            cityName = "";
                            countyName = "";
                            countryName = "";
                            villageName = "";
                            getXzquData(Configure.cityInfo + "?provinceCode=" + provinceCode, 2);
                        }
                        dialog.dismiss();
                    }
                });
                builders.create().show();
                break;
            case R.id.gismap_lin_city:
                if (cityList.size() > 0 && cityStr != null && cityStr.length > 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(GismapSelectActivity.this);
                    builder.setItems(cityStr, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AddressSelectBean bean = cityList.get(which);
                            if (!bean.getAreaName().equals(tv_city.getText().toString())) {
                                cityName = bean.getAreaName();
                                cityCode = bean.getAreaCode();
                                tv_city.setText(cityName);
                                countyList.clear();
                                countryList.clear();
                                villageList.clear();
                                countyStr = null;
                                countryStr = null;
                                villageStr = null;
                                tv_county.setText("");
                                tv_country.setText("");
                                tv_village.setText("");
                                countyCode = "";
                                countryCode = "";
                                villageCode = "";
                                countyName = "";
                                countryName = "";
                                villageName = "";
                                getXzquData(Configure.countyInfo + "?cityCode=" + cityCode, 3);
                            }
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                } else {
                    getXzquDataClike(Configure.cityInfo + "?provinceCode=" + provinceCode, 2);
                }
                break;
            case R.id.gismap_lin_county:
                if (countyList.size() > 0 && countyStr != null && countyStr.length > 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(GismapSelectActivity.this);
                    builder.setItems(countyStr, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AddressSelectBean bean = countyList.get(which);
                            if (!bean.getAreaName().equals(tv_county.getText().toString())) {
                                countyName = bean.getAreaName();
                                countyCode = bean.getAreaCode();
                                tv_county.setText(countyName);
                                countryList.clear();
                                villageList.clear();
                                countryStr = null;
                                villageStr = null;
                                tv_country.setText("");
                                tv_village.setText("");
                                countryCode = "";
                                villageCode = "";
                                countryName = "";
                                villageName = "";
                                getXzquData(Configure.countryInfo + "?countyCode=" + countyCode, 4);
                            }
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                } else {
                    getXzquDataClike(Configure.countyInfo + "?cityCode=" + cityCode, 3);
                }
                break;
            case R.id.gismap_lin_town:
                if (countryList.size() > 0 && countryStr != null && countryStr.length > 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(GismapSelectActivity.this);
                    builder.setItems(countryStr, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AddressSelectBean bean = countryList.get(which);
                            if (!bean.getAreaName().equals(tv_country.getText().toString())) {
                                countryName = bean.getAreaName();
                                countryCode = bean.getAreaCode();
                                tv_country.setText(countryName);
                                villageList.clear();
                                villageStr = null;
                                tv_village.setText("");
                                villageCode = "";
                                villageName = "";
                                getXzquData(Configure.villageInfo + "?countryCode=" + countryCode, 5);
                            }
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                } else {
                    getXzquDataClike(Configure.countryInfo + "?countyCode=" + countyCode, 4);
                }
                break;
            case R.id.gismap_lin_village:
                if (villageList.size() > 0 && villageStr != null && villageStr.length > 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(GismapSelectActivity.this);
                    builder.setItems(villageStr, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AddressSelectBean bean = villageList.get(which);
                            if (!bean.getAreaName().equals(tv_village.getText().toString())) {
                                villageName = bean.getAreaName();
                                villageCode = bean.getAreaCode();
                                tv_village.setText(villageName);
                            }
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                } else {
                    getXzquDataClike(Configure.villageInfo + "?countryCode=" + countryCode, 5);
                }
                break;
            case R.id.gismap_btn_sure:
                if (villageCode==null||villageCode.equals("")){
                    Toast.makeText(this, "????????????", Toast.LENGTH_SHORT).show();
                    return;
                }
                getFarmerInfo();
                break;
            default:
                break;
        }
    }

    private void getXzquData(String url, int flag) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        LoadingDialog.Builder loadBuilder = new LoadingDialog.Builder(GismapSelectActivity.this)
                .setMessage("??????????????????????????????")
                .setCancelable(true)
                .setCancelOutside(true);
        dialog = loadBuilder.create();

        dialog.show();
        DOkHttp.getInstance().getDataFromServerGet(url, "Bearer " + SharePreferencesTools.getValue(GismapSelectActivity.this, "easyPhoto", "access_token", ""), new DOkHttp.MyCallBack() {
            @Override
            public void onFailure(Request request, IOException e) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                Toast.makeText(GismapSelectActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String json) {
                try {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    JSONObject object = new JSONObject(json);
                    int code = object.getInt("code");
                    String message = object.getString("message");
                    if (code == 0) {
                        JSONArray jsonArray = object.getJSONArray("data");
                        switch (flag) {
                            case 2:
                                cityList.clear();
                                countyList.clear();
                                countryList.clear();
                                villageList.clear();
                                cityStr = new String[jsonArray.length()];
                                countyStr = null;
                                countryStr = null;
                                villageStr = null;
                                break;
                            case 3:
                                countyList.clear();
                                countryList.clear();
                                villageList.clear();
                                countyStr = new String[jsonArray.length()];
                                countryStr = null;
                                villageStr = null;
                                break;
                            case 4:
                                countryList.clear();
                                villageList.clear();
                                countryStr = new String[jsonArray.length()];
                                villageStr = null;
                                break;
                            case 5:
                                villageList.clear();
                                villageStr = new String[jsonArray.length()];
                                break;
                            default:
                                break;
                        }
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject objectData = jsonArray.getJSONObject(i);
                            AddressSelectBean bean = new AddressSelectBean();
                            switch (flag) {
                                case 2:
                                    bean.setAreaName(objectData.getString("cityName"));
                                    bean.setAreaCode(objectData.getString("cityCode"));
                                    cityList.add(bean);
                                    cityStr[i] = bean.getAreaName();
                                    break;
                                case 3:
                                    bean.setAreaName(objectData.getString("countyName"));
                                    bean.setAreaCode(objectData.getString("countyCode"));
                                    countyList.add(bean);
                                    countyStr[i] = bean.getAreaName();
                                    break;
                                case 4:
                                    bean.setAreaName(objectData.getString("countryName"));
                                    bean.setAreaCode(objectData.getString("countryCode"));
                                    countryList.add(bean);
                                    countryStr[i] = bean.getAreaName();
                                    break;
                                case 5:
                                    bean.setAreaName(objectData.getString("villageName"));
                                    bean.setAreaCode(objectData.getString("villageCode"));
                                    villageList.add(bean);
                                    villageStr[i] = bean.getAreaName();
                                    break;
                                default:
                                    break;
                            }
                        }

                        switch (flag) {
                            case 2:
                                cityName = cityList.get(0).getAreaName();
                                cityCode = cityList.get(0).getAreaCode();
                                tv_city.setText(cityName);
                                countyList.clear();
                                countryList.clear();
                                villageList.clear();
                                countyStr = null;
                                countryStr = null;
                                villageStr = null;
                                tv_county.setText("");
                                tv_country.setText("");
                                tv_village.setText("");
                                countyCode = "";
                                countryCode = "";
                                villageCode = "";
                                countyName = "";
                                countryName = "";
                                villageName = "";
                                break;
                            case 3:
                                countyName = countyList.get(0).getAreaName();
                                countyCode = countyList.get(0).getAreaCode();
                                tv_county.setText(countyName);
                                countryList.clear();
                                villageList.clear();
                                countryStr = null;
                                villageStr = null;
                                tv_country.setText("");
                                tv_village.setText("");
                                countryCode = "";
                                villageCode = "";
                                countryName = "";
                                villageName = "";
                                break;
                            case 4:
                                countryName = countryList.get(0).getAreaName();
                                countryCode = countryList.get(0).getAreaCode();
                                tv_country.setText(countryName);
                                villageList.clear();
                                villageStr = null;
                                tv_village.setText("");
                                villageCode = "";
                                villageName = "";
                                break;
                            case 5:
                                villageName = villageList.get(0).getAreaName();
                                villageCode = villageList.get(0).getAreaCode();
                                tv_village.setText(villageName);
                                break;
                            default:
                                break;
                        }
                    } else {
                        Toast.makeText(GismapSelectActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getXzquDataClike(String url, int flag) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        LoadingDialog.Builder loadBuilder = new LoadingDialog.Builder(GismapSelectActivity.this)
                .setMessage("??????????????????????????????")
                .setCancelable(true)
                .setCancelOutside(true);
        dialog = loadBuilder.create();
        dialog.show();
        DOkHttp.getInstance().getDataFromServerGet(url, "Bearer " + SharePreferencesTools.getValue(GismapSelectActivity.this, "easyPhoto", "access_token", ""), new DOkHttp.MyCallBack() {
            @Override
            public void onFailure(Request request, IOException e) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                Toast.makeText(GismapSelectActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String json) {
                try {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    JSONObject object = new JSONObject(json);
                    int code = object.getInt("code");
                    String message = object.getString("message");
                    if (code == 0) {
                        JSONArray jsonArray = object.getJSONArray("data");
                        switch (flag) {
                            case 2:
                                cityList.clear();
                                countyList.clear();
                                countryList.clear();
                                villageList.clear();
                                cityStr = new String[jsonArray.length()];
                                countyStr = null;
                                countryStr = null;
                                villageStr = null;
                                break;
                            case 3:
                                countyList.clear();
                                countryList.clear();
                                villageList.clear();
                                countyStr = new String[jsonArray.length()];
                                countryStr = null;
                                villageStr = null;
                                break;
                            case 4:
                                countryList.clear();
                                villageList.clear();
                                countryStr = new String[jsonArray.length()];
                                villageStr = null;
                                break;
                            case 5:
                                villageList.clear();
                                villageStr = new String[jsonArray.length()];
                                break;
                            default:
                                break;
                        }
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject objectData = jsonArray.getJSONObject(i);
                            AddressSelectBean bean = new AddressSelectBean();
                            switch (flag) {
                                case 2:
                                    bean.setAreaName(objectData.getString("cityName"));
                                    bean.setAreaCode(objectData.getString("cityCode"));
                                    cityList.add(bean);
                                    cityStr[i] = bean.getAreaName();
                                    break;
                                case 3:
                                    bean.setAreaName(objectData.getString("countyName"));
                                    bean.setAreaCode(objectData.getString("countyCode"));
                                    countyList.add(bean);
                                    countyStr[i] = bean.getAreaName();
                                    break;
                                case 4:
                                    bean.setAreaName(objectData.getString("countryName"));
                                    bean.setAreaCode(objectData.getString("countryCode"));
                                    countryList.add(bean);
                                    countryStr[i] = bean.getAreaName();
                                    break;
                                case 5:
                                    bean.setAreaName(objectData.getString("villageName"));
                                    bean.setAreaCode(objectData.getString("villageCode"));
                                    villageList.add(bean);
                                    villageStr[i] = bean.getAreaName();
                                    break;
                                default:
                                    break;
                            }
                        }

                        switch (flag) {
                            case 2:
                                AlertDialog.Builder builder = new AlertDialog.Builder(GismapSelectActivity.this);
                                builder.setItems(cityStr, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        AddressSelectBean bean = cityList.get(which);
                                        if (!bean.getAreaName().equals(tv_city.getText().toString())) {
                                            cityName = bean.getAreaName();
                                            cityCode = bean.getAreaCode();
                                            tv_city.setText(cityName);
                                            countyList.clear();
                                            countryList.clear();
                                            villageList.clear();
                                            countyStr = null;
                                            countryStr = null;
                                            villageStr = null;
                                            tv_county.setText("");
                                            tv_country.setText("");
                                            tv_village.setText("");
                                            countyCode = "";
                                            countryCode = "";
                                            villageCode = "";
                                            countyName = "";
                                            countryName = "";
                                            villageName = "";
                                            getXzquData(Configure.countyInfo + "?cityCode=" + cityCode, 3);
                                        }
                                        dialog.dismiss();
                                    }
                                });
                                builder.create().show();
                                break;
                            case 3:
                                AlertDialog.Builder builder3 = new AlertDialog.Builder(GismapSelectActivity.this);
                                builder3.setItems(countyStr, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        AddressSelectBean bean = countyList.get(which);
                                        if (!bean.getAreaName().equals(tv_county.getText().toString())) {
                                            countyName = bean.getAreaName();
                                            countyCode = bean.getAreaCode();
                                            tv_county.setText(countyName);
                                            countryList.clear();
                                            villageList.clear();
                                            countryStr = null;
                                            villageStr = null;
                                            tv_country.setText("");
                                            tv_village.setText("");
                                            countryCode = "";
                                            villageCode = "";
                                            countryName = "";
                                            villageName = "";
                                            getXzquData(Configure.countryInfo + "?countyCode=" + countyCode, 4);
                                        }
                                        dialog.dismiss();
                                    }
                                });
                                builder3.create().show();
                                break;
                            case 4:
                                AlertDialog.Builder builder4 = new AlertDialog.Builder(GismapSelectActivity.this);
                                builder4.setItems(countryStr, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        AddressSelectBean bean = countryList.get(which);
                                        if (!bean.getAreaName().equals(tv_country.getText().toString())) {
                                            countryName = bean.getAreaName();
                                            countryCode = bean.getAreaCode();
                                            tv_country.setText(countryName);
                                            villageList.clear();
                                            villageStr = null;
                                            tv_village.setText("");
                                            villageCode = "";
                                            villageName = "";
                                            getXzquData(Configure.villageInfo + "?countryCode=" + countryCode, 5);
                                        }
                                        dialog.dismiss();
                                    }
                                });
                                builder4.create().show();
                                break;
                            case 5:
                                AlertDialog.Builder builder5 = new AlertDialog.Builder(GismapSelectActivity.this);
                                builder5.setItems(villageStr, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        AddressSelectBean bean = villageList.get(which);
                                        if (!bean.getAreaName().equals(tv_village.getText().toString())) {
                                            villageName = bean.getAreaName();
                                            villageCode = bean.getAreaCode();
                                            tv_village.setText(villageName);
                                        }
                                        dialog.dismiss();
                                    }
                                });
                                builder5.create().show();
                                break;
                            default:
                                break;
                        }
                    } else {
                        Toast.makeText(GismapSelectActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getFarmerInfo() {
        LoadingDialog.Builder loadBuilder = new LoadingDialog.Builder(GismapSelectActivity.this)
                .setMessage("??????????????????")
                .setCancelable(true)
                .setCancelOutside(true);
        dialog = loadBuilder.create();
        dialog.show();
        Map<String, String> map = new HashMap<>();
        map.put("countyCode", countyCode);
//        map.put("assessLevel", "??????");
//        map.put("dataSources", "1");
        RequestBody requestBodys = FormBody.create(MediaType.parse("application/json; charset=utf-8"), gson.toJson(map));
        DOkHttp.getInstance().uploadPost2ServerProgress(GismapSelectActivity.this, Configure.getCheckMapServicePath, "Bearer " + SharePreferencesTools.getValue(GismapSelectActivity.this, "easyPhoto", "access_token", ""), requestBodys, new DOkHttp.MyCallBack() {
            @Override
            public void onFailure(Request request, IOException e) {
                Toast.makeText(GismapSelectActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }

            @Override
            public void onResponse(String json) {
                try {
                    dialog.dismiss();
                    JSONObject object = new JSONObject(json);
                    int code = object.getInt("code");
                    String message = object.getString("message");
                    if (code == 0) {
                        JSONObject data = object.getJSONObject("data");
                        if(data.has("assessUrl")&&data.has("mapUrl")){
                            String serverPath = data.getString("assessUrl");
                            String mapUrl = data.getString("mapUrl");
                            if (serverPath.length()>0&&mapUrl.length()>0){
                                Intent intent = new Intent(GismapSelectActivity.this, PhotoMain.class);
                                intent.putExtra("provinceName", provinceName);
                                intent.putExtra("provinceCode", provinceCode);
                                intent.putExtra("cityName", cityName);
                                intent.putExtra("cityCode", cityCode);
                                intent.putExtra("countyName", countyName);
                                intent.putExtra("countyCode", countyCode);
                                intent.putExtra("countryName", countryName);
                                intent.putExtra("countryCode", countryCode);
                                intent.putExtra("villageName", villageName);
                                intent.putExtra("villageCode", villageCode);
                                intent.putExtra("serverPath", serverPath);
                                SharePreferencesTools.saveString(GismapSelectActivity.this, "easyPhoto", "gisCode", villageCode);
                                SharePreferencesTools.saveString(GismapSelectActivity.this, "easyPhoto", "serverPath", serverPath);
                                SharePreferencesTools.saveString(GismapSelectActivity.this, "easyPhoto", "gisPath", mapUrl);

                                SharePreferencesTools.saveString(GismapSelectActivity.this, "easyPhoto", "provinceNameCk", provinceName);
                                SharePreferencesTools.saveString(GismapSelectActivity.this, "easyPhoto", "provinceCodeCk", provinceCode);
                                SharePreferencesTools.saveString(GismapSelectActivity.this, "easyPhoto", "cityNameCk", cityName);
                                SharePreferencesTools.saveString(GismapSelectActivity.this, "easyPhoto", "cityCodeCk", cityCode);
                                SharePreferencesTools.saveString(GismapSelectActivity.this, "easyPhoto", "countyNameCk", countyName);
                                SharePreferencesTools.saveString(GismapSelectActivity.this, "easyPhoto", "countyCodeCk", countyCode);
                                SharePreferencesTools.saveString(GismapSelectActivity.this, "easyPhoto", "countryNameCk", countryName);
                                SharePreferencesTools.saveString(GismapSelectActivity.this, "easyPhoto", "countryCodeCk", countryCode);
                                SharePreferencesTools.saveString(GismapSelectActivity.this, "easyPhoto", "villageNameCk", villageName);
                                SharePreferencesTools.saveString(GismapSelectActivity.this, "easyPhoto", "villageCodeCk", villageCode);
                                startActivity(intent);
                                finish();
                            }else {
                                Toast.makeText(GismapSelectActivity.this, "???????????????????????????", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(GismapSelectActivity.this, "???????????????????????????", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(GismapSelectActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new DOkHttp.UIchangeListener() {
            @Override
            public void progressUpdate(long bytesWrite, long contentLength, boolean done) {
                int progress = (int) (bytesWrite * 100 / contentLength);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // ?????????
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Intent intent = new Intent(GismapSelectActivity.this, MenuActivity.class);
            startActivity(intent);
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
