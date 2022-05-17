package com.example.lml.easyphoto.FarmerInfo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lml.easyphoto.FarmerInfo.houseHolds.HoldsService;
import com.example.lml.easyphoto.FarmerInfo.houseHolds.HouseHoldsActivity;
import com.example.lml.easyphoto.FarmerInfo.houseHolds.HouseHoldsBean;
import com.example.lml.easyphoto.R;
import com.example.lml.easyphoto.addressSelect.AddressSelectBean;
import com.example.lml.easyphoto.camera.AitsApplication;
import com.example.lml.easyphoto.dikuai.DKBean;
import com.example.lml.easyphoto.dikuai.DKService;
import com.example.lml.easyphoto.dikuai.subList.DKSubListActivity;
import com.example.lml.easyphoto.history.HistoryActivity;
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
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

public class FarmerInfoActivity extends Activity implements View.OnClickListener {
    private TextView tv_city, tv_county, tv_country, tv_village;
    private ImageView tv_search;
    private LinearLayout lin_city, lin_county, lin_country, lin_village;
    private EditText et_userName;
    private ListView lv_main;
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
    private List<AddressSelectBean> cityList;
    private List<AddressSelectBean> countyList;
    private List<AddressSelectBean> countryList;
    private List<AddressSelectBean> villageList;
    private String cityStr[];
    private String countyStr[];
    private String countryStr[];
    private String villageStr[];
    private boolean showFlag = false;
    private Gson gson;
    private List<FarmerInfoBean> mList;
    private List<FarmerInfoBean> allList;
    private FarmerInfoAdapter adapter;
    private HoldsService service;
    private DKService dkService;
    private LoadingDialog dialog;//提示框

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_info);
        initView();
        initData();
    }


    private void initView() {
        tv_city = findViewById(R.id.fi_tv_city);
        tv_county = findViewById(R.id.fi_tv_county);
        tv_country = findViewById(R.id.fi_tv_town);
        tv_village = findViewById(R.id.fi_tv_village);
        lin_city = findViewById(R.id.fi_lin_city);
        lin_county = findViewById(R.id.fi_lin_county);
        lin_country = findViewById(R.id.fi_lin_town);
        lin_village = findViewById(R.id.fi_lin_village);
        tv_search = findViewById(R.id.fi_tv_search);
        et_userName = findViewById(R.id.fi_et_name);
        lv_main = findViewById(R.id.fi_lv_list);
        lin_city.setOnClickListener(this);
        lin_county.setOnClickListener(this);
        lin_country.setOnClickListener(this);
        lin_village.setOnClickListener(this);
        tv_search.setOnClickListener(this);

        String city = SharePreferencesTools.getValue(FarmerInfoActivity.this, "easyPhoto", "city", "");
        String county = SharePreferencesTools.getValue(FarmerInfoActivity.this, "easyPhoto", "county", "");
        String country = SharePreferencesTools.getValue(FarmerInfoActivity.this, "easyPhoto", "country", "");
        String village = SharePreferencesTools.getValue(FarmerInfoActivity.this, "easyPhoto", "village", "");
        if (!city.equals("")) {
            lin_city.setEnabled(false);
        }
        if (!county.equals("")) {
            lin_county.setEnabled(false);
        }
        if (!country.equals("")) {
            lin_country.setEnabled(false);
        }
        if (!village.equals("")) {
            lin_village.setEnabled(false);
        }
    }

    private void initData() {
        provinceCode = getIntent().getStringExtra("province");
        provinceName = getIntent().getStringExtra("provinceName");
        cityCode = getIntent().getStringExtra("city");
        cityName = getIntent().getStringExtra("cityName");
        countyCode = getIntent().getStringExtra("county");
        countyName = getIntent().getStringExtra("countyName");
        countryCode = getIntent().getStringExtra("country");
        countryName = getIntent().getStringExtra("countryName");
        villageCode = getIntent().getStringExtra("village");
        villageName = getIntent().getStringExtra("villageName");
        tv_city.setText(cityName);
        tv_county.setText(countyName);
        tv_country.setText(countryName);
        tv_village.setText(villageName);
        dkService = new DKService(this);
        service = new HoldsService(this, dkService);
        mList = new ArrayList<>();
        allList = new ArrayList<>();
        gson = new Gson();
        adapter = new FarmerInfoAdapter(mList, this);
        lv_main.setAdapter(adapter);
        dialog = new LoadingDialog(this);
        cityList = new ArrayList<>();
        countyList = new ArrayList<>();
        countryList = new ArrayList<>();
        villageList = new ArrayList<>();
        if (cityCode.equals("")) {
            getXzquData(Configure.cityInfo + "?provinceCode=" + provinceCode, 2);
        }
        if (villageCode.length() > 0) {
            getFarmerInfo();
        }
        lv_main.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getHolds(mList.get(position));
            }
        });
    }

    public void goBack(View view) {
        Intent intent = new Intent(FarmerInfoActivity.this, MenuActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fi_lin_city:
                if (cityList.size() > 0 && cityStr != null && cityStr.length > 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(FarmerInfoActivity.this);
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
            case R.id.fi_lin_county:
                if (countyList.size() > 0 && countyStr != null && countyStr.length > 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(FarmerInfoActivity.this);
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
            case R.id.fi_lin_town:
                if (countryList.size() > 0 && countryStr != null && countryStr.length > 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(FarmerInfoActivity.this);
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
            case R.id.fi_lin_village:
                if (villageList.size() > 0 && villageStr != null && villageStr.length > 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(FarmerInfoActivity.this);
                    builder.setItems(villageStr, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AddressSelectBean bean = villageList.get(which);
                            if (!bean.getAreaName().equals(tv_village.getText().toString())) {
                                villageName = bean.getAreaName();
                                villageCode = bean.getAreaCode();
                                tv_village.setText(villageName);
                            }
                            getFarmerInfo();
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                } else {
                    getXzquDataClike(Configure.villageInfo + "?countryCode=" + countryCode, 5);
                }
                break;
            case R.id.fi_tv_search:
                search(et_userName.getText().toString().trim());
                break;
            default:
                break;
        }
    }

    private void getXzquData(String url, int flag) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        LoadingDialog.Builder loadBuilder = new LoadingDialog.Builder(FarmerInfoActivity.this)
                .setMessage("正在获取行政区域数据")
                .setCancelable(true)
                .setCancelOutside(true);
        dialog = loadBuilder.create();

        dialog.show();
        DOkHttp.getInstance().getDataFromServerGet(url, "Bearer " + SharePreferencesTools.getValue(FarmerInfoActivity.this, "easyPhoto", "access_token", ""), new DOkHttp.MyCallBack() {
            @Override
            public void onFailure(Request request, IOException e) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                Toast.makeText(FarmerInfoActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
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
                                getFarmerInfo();
                                break;
                            default:
                                break;
                        }
                    } else {
                        Toast.makeText(FarmerInfoActivity.this, message, Toast.LENGTH_SHORT).show();
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
        LoadingDialog.Builder loadBuilder = new LoadingDialog.Builder(FarmerInfoActivity.this)
                .setMessage("正在获取行政区域数据")
                .setCancelable(true)
                .setCancelOutside(true);
        dialog = loadBuilder.create();
        dialog.show();
        DOkHttp.getInstance().getDataFromServerGet(url, "Bearer " + SharePreferencesTools.getValue(FarmerInfoActivity.this, "easyPhoto", "access_token", ""), new DOkHttp.MyCallBack() {
            @Override
            public void onFailure(Request request, IOException e) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                Toast.makeText(FarmerInfoActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
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
                                AlertDialog.Builder builder = new AlertDialog.Builder(FarmerInfoActivity.this);
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
                                AlertDialog.Builder builder3 = new AlertDialog.Builder(FarmerInfoActivity.this);
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
                                AlertDialog.Builder builder4 = new AlertDialog.Builder(FarmerInfoActivity.this);
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
                                AlertDialog.Builder builder5 = new AlertDialog.Builder(FarmerInfoActivity.this);
                                builder5.setItems(villageStr, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        AddressSelectBean bean = villageList.get(which);
                                        if (!bean.getAreaName().equals(tv_village.getText().toString())) {
                                            villageName = bean.getAreaName();
                                            villageCode = bean.getAreaCode();
                                            tv_village.setText(villageName);
                                            getFarmerInfo();
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
                        Toast.makeText(FarmerInfoActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getFarmerInfo() {
        if (villageName.equals("")) {
            return;
        }
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        LoadingDialog.Builder loadBuilder = new LoadingDialog.Builder(FarmerInfoActivity.this)
                .setMessage("正在获取分户信息")
                .setCancelable(true)
                .setCancelOutside(true);
        dialog = loadBuilder.create();
        dialog.show();
        mList.clear();
        allList.clear();
        adapter.notifyDataSetChanged();
        MultipartBody.Builder mb = new MultipartBody.Builder();
        mb.setType(MultipartBody.FORM);
        mb.addFormDataPart("provinceName", provinceName);
        mb.addFormDataPart("cityName", cityName);
        mb.addFormDataPart("countyName", countyName);
        mb.addFormDataPart("countryName", countryName);
        mb.addFormDataPart("villageName", villageName);
        RequestBody requestBody = mb.build();
        DOkHttp.getInstance().uploadPost2ServerProgress(FarmerInfoActivity.this, Configure.farmerInfo, "Bearer " + SharePreferencesTools.getValue(FarmerInfoActivity.this, "easyPhoto", "access_token", ""), requestBody, new DOkHttp.MyCallBack() {
            @Override
            public void onFailure(Request request, IOException e) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                Toast.makeText(FarmerInfoActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
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
                        JSONArray array = object.getJSONArray("data");
                        mList.clear();
                        allList.clear();
                        adapter.notifyDataSetChanged();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject jsonObject = array.getJSONObject(i);
                            FarmerInfoBean infoBean = new FarmerInfoBean();
                            infoBean.setCertificateId(jsonObject.getString("certificateId"));
                            infoBean.setInsuredName(jsonObject.getString("insuredName"));
                            infoBean.setProposalNo(jsonObject.getString("proposalNo"));
                            infoBean.setSumQuantity(jsonObject.getString("sumQuantity"));
                            List<DKBean> list1 = dkService.getDKByUser(infoBean.getCertificateId(), infoBean.getProposalNo());
                            if (list1.size() == 0) {
                                mList.add(infoBean);
                                allList.add(infoBean);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        if (mList.size() == 0) {
                            Toast.makeText(FarmerInfoActivity.this, "该村暂无需要标绘的农户", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(FarmerInfoActivity.this, message, Toast.LENGTH_SHORT).show();
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

    private void search(String name) {
        mList.clear();
        adapter.notifyDataSetChanged();
        if (name.equals("")) {
            mList.addAll(allList);
            adapter.notifyDataSetChanged();
        } else {
            for (int i = 0; i < allList.size(); i++) {
                FarmerInfoBean infoBean = allList.get(i);
                if (infoBean.getInsuredName().contains(name)) {
                    mList.add(infoBean);
                }
            }
            adapter.notifyDataSetChanged();
        }
    }

    private void getHolds(FarmerInfoBean infoBean) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        LoadingDialog.Builder loadBuilder = new LoadingDialog.Builder(FarmerInfoActivity.this)
                .setMessage("正在获取数据")
                .setCancelable(true)
                .setCancelOutside(true);
        dialog = loadBuilder.create();
        dialog.show();
        MultipartBody.Builder mb = new MultipartBody.Builder();
        mb.setType(MultipartBody.FORM);
        mb.addFormDataPart("provinceName", provinceName);
        mb.addFormDataPart("cityName", cityName);
        mb.addFormDataPart("countyName", countyName);
        mb.addFormDataPart("countryName", countryName);
        mb.addFormDataPart("villageName", villageName);
        mb.addFormDataPart("proposalNo", infoBean.getProposalNo());
        mb.addFormDataPart("certificateId", infoBean.getCertificateId());
        RequestBody requestBody = mb.build();
        DOkHttp.getInstance().uploadPost2ServerProgress(FarmerInfoActivity.this, Configure.houseHoldsInfo, "Bearer " + SharePreferencesTools.getValue(FarmerInfoActivity.this, "easyPhoto", "access_token", ""), requestBody, new DOkHttp.MyCallBack() {
            @Override
            public void onFailure(Request request, IOException e) {
                Toast.makeText(FarmerInfoActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }

            @Override
            public void onResponse(String json) {
                try {

                    JSONObject object = new JSONObject(json);
                    int code = object.getInt("code");
                    String message = object.getString("message");
                    if (code == 0) {
                        JSONArray jsonArray = object.getJSONArray("data");
                        if (jsonArray.length() > 0) {
                            service.deleteCustomerByproNoAndCard(infoBean.getProposalNo(), infoBean.getCertificateId());
                        }
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            HouseHoldsBean houseHoldsBean = gson.fromJson(jsonObject.toString(), HouseHoldsBean.class);
                            ContentValues values = new ContentValues();
                            values.put("id", houseHoldsBean.getId());
                            values.put("order_no", houseHoldsBean.getOrderNo());
                            values.put("province_name", houseHoldsBean.getProvinceName());
                            values.put("city_name", houseHoldsBean.getCityName());
                            values.put("county_name", houseHoldsBean.getCountyName());
                            values.put("country_name", houseHoldsBean.getCountryName());
                            values.put("village_name", houseHoldsBean.getVillageName());
                            values.put("proposal_no", houseHoldsBean.getProposalNo());
                            values.put("insured_method", houseHoldsBean.getInsuredMethod());
                            values.put("village_group", houseHoldsBean.getVillageGroup());
                            values.put("packet_no", houseHoldsBean.getPacketNo());
                            values.put("insured_name", houseHoldsBean.getInsuredName());
                            values.put("certificate_type", houseHoldsBean.getCertificateType());
                            values.put("certificate_id", houseHoldsBean.getCertificateId());
                            values.put("phone", houseHoldsBean.getPhone());
                            values.put("massif_name", houseHoldsBean.getMassifName());
                            values.put("massif_type", houseHoldsBean.getMassifType());
                            values.put("crop_name", houseHoldsBean.getCropName());
                            values.put("insure_area", houseHoldsBean.getInsureArea());
                            values.put("bank_categories", houseHoldsBean.getBankCategories());
                            values.put("open_bank", houseHoldsBean.getOpenBank());
                            values.put("bank_card_no", houseHoldsBean.getBankCardNo());
                            values.put("sum_quantity", houseHoldsBean.getSumQuantity());
                            values.put("massif_no", houseHoldsBean.getMassifNo());
                            values.put("is_destitute", houseHoldsBean.getIsDestitute());
                            values.put("office_id", houseHoldsBean.getOfficeId());
                            values.put("central_office", houseHoldsBean.getCentralOffice());
                            values.put("submit_status", houseHoldsBean.getSubmitStatus());
                            values.put("create_by", houseHoldsBean.getCreateBy());
                            values.put("create_time", houseHoldsBean.getCreateTime());
                            values.put("update_time", houseHoldsBean.getUpdateTime());
                            values.put("year_no", houseHoldsBean.getYearNo());
                            values.put("isBusinessEntity", houseHoldsBean.getIsBusinessEntity());
                            values.put("policyNumber", houseHoldsBean.getPolicyNumber());
                            service.insertCustomer(values);
                        }
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        Intent intent = new Intent(FarmerInfoActivity.this, HouseHoldsActivity.class);
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
                        intent.putExtra("proposalNo", infoBean.getProposalNo());
                        intent.putExtra("certificateId", infoBean.getCertificateId());
                        intent.putExtra("insuredName", infoBean.getInsuredName());
                        startActivity(intent);
                    } else {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        Toast.makeText(FarmerInfoActivity.this, message, Toast.LENGTH_SHORT).show();
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
        // 返回键
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Intent intent = new Intent(FarmerInfoActivity.this, MenuActivity.class);
            startActivity(intent);
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mList.clear();
        allList.clear();
        adapter.notifyDataSetChanged();
        getFarmerInfo();
    }
}
