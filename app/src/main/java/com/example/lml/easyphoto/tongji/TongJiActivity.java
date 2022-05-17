package com.example.lml.easyphoto.tongji;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.example.lml.easyphoto.R;
import com.example.lml.easyphoto.camera.CameraPhotoBean;
import com.example.lml.easyphoto.camera.CameraService;
import com.example.lml.easyphoto.camera.PhotoMain;
import com.example.lml.easyphoto.okHttpUtils.DOkHttp;
import com.example.lml.easyphoto.util.Configure;
import com.example.lml.easyphoto.util.SearchService;
import com.example.lml.easyphoto.util.SharePreferencesTools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

public class TongJiActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tv_city, tv_county, tv_country, tv_village, tv_crop;
    private TextView tv_allSelect;
    private Button btn_submit;
    private Button btn_delete;
    private ImageView tv_search;
    private LinearLayout lin_city, lin_county, lin_country, lin_village, lin_crop;
    private EditText et_userName;
    private ListView lv_main;
    private CameraService service;
    private String cityStr[];
    private String countyStr[];
    private String countryStr[];
    private String villageStr[];
    private String cropStr[] = new String[]{"全部", "玉米", "水稻", "大豆"};
    private String provinceName;
    private String cityName;
    private String countyName;
    private String countryName;
    private String villageName;
    private String cropName = "全部";
    private List<TongjiBean> mList;
    private List<TongjiBean> allList;
    private TongjiAdapter adapter;
    private LocationManager manager;
    private String provider;
    private ServiceFeatureTable featureTable;
    private String level = "1";
    List<CameraPhotoBean> subList = new ArrayList<>();
    List<CameraPhotoBean> noList = new ArrayList<CameraPhotoBean>();
    private ProgressDialog progressDialog1;
    private int size = 0;
    private int subNumber = 0;
    private int subFailNumber = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tong_ji);
        initView();
        initData();
    }

    private void initView() {
        featureTable = new ServiceFeatureTable(Configure.gisXZQYCX);
        tv_city = findViewById(R.id.tj_tv_city);
        tv_county = findViewById(R.id.tj_tv_county);
        tv_country = findViewById(R.id.tj_tv_town);
        tv_village = findViewById(R.id.tj_tv_village);
        tv_crop = findViewById(R.id.tj_tv_crop);
        lin_city = findViewById(R.id.tj_lin_city);
        lin_county = findViewById(R.id.tj_lin_county);
        lin_country = findViewById(R.id.tj_lin_town);
        lin_village = findViewById(R.id.tj_lin_village);
        lin_crop = findViewById(R.id.tj_lin_crop);
        tv_search = findViewById(R.id.tj_tv_search);
        tv_allSelect = findViewById(R.id.tj_tv_allChoose);
        btn_submit = findViewById(R.id.tj_btn_submit);
        btn_delete = findViewById(R.id.tj_btn_delete);
        et_userName = findViewById(R.id.tj_et_name);
        lv_main = findViewById(R.id.tj_lv_list);
        lin_city.setOnClickListener(this);
        lin_county.setOnClickListener(this);
        lin_country.setOnClickListener(this);
        lin_village.setOnClickListener(this);
        lin_crop.setOnClickListener(this);
        tv_search.setOnClickListener(this);
        tv_allSelect.setOnClickListener(this);
        btn_delete.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
        tv_crop.setText(cropName);
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providerlist = manager.getProviders(true);
        if (providerlist.contains(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
        } else if (providerlist.contains(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
        } else {
            Toast.makeText(this, "没有可用的位置提供器", Toast.LENGTH_LONG).show();
        }
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = null;
        if (provider != null) {
            location = manager.getLastKnownLocation(provider);
        }
        if (location != null) {
            Point pointGps = new Point(location.getLongitude(), location.getLatitude(), SpatialReferences.getWgs84());
            SearchService.getInstance().getMap4Server(featureTable, pointGps, new SearchService.MapCallBack() {
                @Override
                public void onFailure(Exception e) {
                }

                @Override
                public void onResponse(Map<String, Object> dataMap) {
                    provinceName = (String) dataMap.get("province");
                    cityName = (String) dataMap.get("city");
                    countyName = (String) dataMap.get("county");
                    countryName = (String) dataMap.get("town");
                    villageName = (String) dataMap.get("village");
                    if (cityName != null) {
                        tv_city.setText(cityName);
                    }
                    if (countyName != null) {
                        tv_county.setText(countyName);
                    }
                    if (countryName != null) {
                        tv_country.setText(countryName);
                    }
                    if (villageName != null) {
                        tv_village.setText(villageName);
                    }
                    getShowData();
                }
            });
        }

    }

    private void initData() {
        progressDialog1 = new ProgressDialog(TongJiActivity.this);
        progressDialog1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog1.setCancelable(false);
        service = new CameraService(this);
        provinceName = SharePreferencesTools.getValue(TongJiActivity.this, "easyPhoto", "provinceName", "");
        allList = new ArrayList<>();
        mList = new ArrayList<>();
        adapter = new TongjiAdapter(mList, TongJiActivity.this);
        lv_main.setAdapter(adapter);
        lv_main.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TongJiActivity.this, DkLookActivity.class);
                intent.putExtra("folderName", mList.get(position).getFoldName());
                intent.putExtra("crop", cropName);
                startActivity(intent);
            }
        });
        getShowData();
    }

    private void getShowData() {
        allList.clear();
        mList.clear();
        allList.addAll(service.getFilesNameListStr(provinceName, cityName, countyName, countryName, villageName, cropName, level));
        mList.addAll(allList);
        adapter.notifyDataSetChanged();
    }

    public void goBack(View view) {
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tj_lin_city:
                cityStr = service.getQuyuList(provinceName, "1");
                if (cityStr == null) {
                    Toast.makeText(this, "没有数据", Toast.LENGTH_SHORT).show();
                } else {
                    if (cityStr.length > 0) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(TongJiActivity.this);
                        builder.setItems(cityStr, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                cityName = cityStr[which];
                                tv_city.setText(cityName);
                                countyStr = null;
                                countryStr = null;
                                villageStr = null;
                                tv_county.setText("");
                                tv_country.setText("");
                                tv_village.setText("");
                                countyName = "";
                                countryName = "";
                                villageName = "";
                                level = "2";
                                getShowData();
                                dialog.dismiss();
                            }
                        });
                        builder.create().show();
                    }
                }
                break;
            case R.id.tj_lin_county:
                countyStr = service.getQuyuList(cityName, "2");
                if (countyStr == null) {
                    Toast.makeText(this, "没有数据", Toast.LENGTH_SHORT).show();
                } else {
                    if (countyStr.length > 0) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(TongJiActivity.this);
                        builder.setItems(countyStr, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                countyName = countyStr[which];
                                tv_county.setText(countyName);
                                countryStr = null;
                                villageStr = null;
                                tv_country.setText("");
                                tv_village.setText("");
                                countryName = "";
                                villageName = "";
                                level = "3";
                                getShowData();
                                dialog.dismiss();
                            }
                        });
                        builder.create().show();
                    }
                }
                break;
            case R.id.tj_lin_town:
                countryStr = service.getQuyuList(countyName, "3");
                if (countryStr == null) {
                    Toast.makeText(this, "没有数据", Toast.LENGTH_SHORT).show();
                } else {
                    if (countryStr.length > 0) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(TongJiActivity.this);
                        builder.setItems(countryStr, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                countryName = countryStr[which];
                                tv_country.setText(countryName);
                                villageStr = null;
                                tv_village.setText("");
                                villageName = "";
                                level = "4";
                                getShowData();
                                dialog.dismiss();
                            }
                        });
                        builder.create().show();
                    }
                }
                break;
            case R.id.tj_lin_village:
                villageStr = service.getQuyuList(countryName, "4");
                if (villageStr == null) {
                    Toast.makeText(this, "没有数据", Toast.LENGTH_SHORT).show();
                } else {
                    if (villageStr.length > 0) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(TongJiActivity.this);
                        builder.setItems(villageStr, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                villageName = villageStr[which];
                                tv_village.setText(villageName);
                                level = "5";
                                getShowData();
                                dialog.dismiss();
                            }
                        });
                        builder.create().show();
                    }
                }
                break;
            case R.id.tj_lin_crop:
                AlertDialog.Builder builder = new AlertDialog.Builder(TongJiActivity.this);
                builder.setItems(cropStr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cropName = cropStr[which];
                        tv_crop.setText(cropName);
                        getShowData();
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                break;
            case R.id.tj_tv_search:
                search(et_userName.getText().toString().trim());
                break;
            case R.id.tj_tv_allChoose:
                if (chooseFlag) {
                    chooseFlag = false;
                    tv_allSelect.setText("全选");
                    for (int i = 0; i < mList.size(); i++) {
                        if (mList.get(i).isChoose) {
                            mList.get(i).setChoose(false);
                        }
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    chooseFlag = true;
                    tv_allSelect.setText("取消");
                    for (int i = 0; i < mList.size(); i++) {
                        if (!mList.get(i).isChoose) {
                            mList.get(i).setChoose(true);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
                break;
            case R.id.tj_btn_delete:
                List<TongjiBean> deleteList = new ArrayList<>();
                for (int i = 0; i < mList.size(); i++) {
                    if (mList.get(i).isChoose()) {
                        deleteList.add(mList.get(i));
                    }
                }
                if (deleteList.size() > 0) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(TongJiActivity.this);
                    builder1.setTitle("提醒").setCancelable(true).setMessage("确定删除吗？").setNeutralButton("删除", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            List<CameraPhotoBean> beanList = new ArrayList<>();
                            for (int i = 0; i < deleteList.size(); i++) {
                                beanList.addAll(service.getPhotoByFilesName(deleteList.get(i).getFoldName()));
                            }
                            if (beanList.size() > 0) {
                                for (int i = 0; i < beanList.size(); i++) {
                                    service.deleteNoFile(beanList.get(i).getID());
                                    File file = new File(beanList.get(i).getFilePath());
                                    if (file.exists()) {
                                        file.delete();
                                    }
                                }
                                getShowData();
                            }
                            dialog.dismiss();
                        }
                    })
                            .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    builder1.create().show();
                }
                break;
            case R.id.tj_btn_submit:

                List<TongjiBean> subLists = new ArrayList<>();
                for (int i = 0; i < mList.size(); i++) {
                    if (mList.get(i).isChoose()) {
                        subLists.add(mList.get(i));
                    }
                }
                if (subLists.size() > 0) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(TongJiActivity.this);
                    builder1.setTitle("提醒").setCancelable(true).setMessage("确定提交吗？").setNeutralButton("提交", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            List<CameraPhotoBean> beanList = new ArrayList<>();
                            for (int i = 0; i < subLists.size(); i++) {
                                beanList.addAll(service.getPhotoByFilesName(subLists.get(i).getFoldName()));
                            }
                            if (beanList.size() > 0) {
                                for (int i = 0; i < beanList.size(); i++) {
                                    CameraPhotoBean bean = beanList.get(i);
                                    if (bean.getState().equals("0")) {
                                        File file = new File(bean.getFilePath());
                                        if (file.exists()) {
                                            subList.add(bean);
                                        } else {
                                            noList.add(bean);
                                            service.deleteNoFile(bean.getID());
                                        }
                                    }
                                }
                                if (subList.size() > 0) {
                                    size = subList.size();
                                    subNumber = 0;
                                    subFailNumber = 0;
                                    subFile(subList);
                                    progressDialog1.show();
                                } else {
                                    if (noList.size() > 0) {
                                        dialogShow("");
                                    } else {
                                        Toast.makeText(TongJiActivity.this, "没有未提交的照片", Toast.LENGTH_SHORT).show();
                                    }
                                    getShowData();
                                }
                            }
                            dialog.dismiss();
                        }
                    })
                            .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    builder1.create().show();
                }
                break;
            default:
                break;
        }
    }

    boolean chooseFlag = false;
    private void dialogShow(String tishi) {
        String message = "以下照片已经不存在已无法提交，图片名如下：\n";
        for (int i = 0; i < noList.size(); i++) {
            CameraPhotoBean photoBean = noList.get(i);
            message = message + photoBean.getFileName() + "  ";
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage(message);
        builder.setNeutralButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                if (!tishi.equals("")&&tishi.length()>0){
                    AlertDialog.Builder builders = new AlertDialog.Builder(TongJiActivity.this);
                    builders.setTitle("提示");
                    builders.setMessage(tishi);
                    builders.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builders.create().show();
                }

            }
        });
        builder.create().show();
    }
    private void search(String name) {
        mList.clear();
        adapter.notifyDataSetChanged();
        if (name.equals("")) {
            mList.addAll(allList);
            adapter.notifyDataSetChanged();
        } else {
            for (int i = 0; i < allList.size(); i++) {
                String infoBean = allList.get(i).getFoldName();
                if (infoBean.contains(name)) {
                    mList.add(allList.get(i));
                }
            }
            adapter.notifyDataSetChanged();
        }
    }
    private void subFile(List<CameraPhotoBean> mediaList) {
        //文件上传
        for (int i = 0; i < mediaList.size(); i++) {
            CameraPhotoBean bean = mediaList.get(i);
            File file = new File(bean.getFilePath());
            MultipartBody.Builder mb = new MultipartBody.Builder();
            mb.setType(MultipartBody.FORM);
            mb.addFormDataPart("files", bean.getFileName(),
                    RequestBody.create(null, file));
            mb.addFormDataPart("cityCode", (bean.getGisCode().equals(""))?"":bean.getGisCode().substring(0,4)+"00000000");
            mb.addFormDataPart("cityName", bean.getCity());
            mb.addFormDataPart("countryCode", (bean.getGisCode().equals(""))?"":bean.getGisCode().substring(0,9)+"000");
            mb.addFormDataPart("countryName", bean.getCountrysidename());
            mb.addFormDataPart("countyCode", (bean.getGisCode().equals(""))?"":bean.getGisCode().substring(0,6)+"000000");
            mb.addFormDataPart("countyName", bean.getTownname());
            mb.addFormDataPart("cropName", bean.getZhType());
            mb.addFormDataPart("degreeLoss", bean.getSscd());
            mb.addFormDataPart("isPreliminaryAssessment", bean.getChuPing());
            mb.addFormDataPart("lat", bean.getLat());
            mb.addFormDataPart("lon", bean.getLon());
            mb.addFormDataPart("pointRational", bean.getLonAndlat());
            mb.addFormDataPart("massifType", bean.getMassifType());
            mb.addFormDataPart("provinceCode", (bean.getGisCode().equals(""))?"":bean.getGisCode().substring(0,2)+"0000000000");
            mb.addFormDataPart("provinceName", bean.getProvince());
            mb.addFormDataPart("remark", bean.getRemark());
            mb.addFormDataPart("riskReason", bean.getRiskReason());
            mb.addFormDataPart("soilType", bean.getSoilType());
            mb.addFormDataPart("villageCode", bean.getGisCode());
            mb.addFormDataPart("villageName", bean.getVillagename());
            RequestBody requestBody = mb.build();
            DOkHttp.getInstance().uploadPost2ServerProgress(TongJiActivity.this, Configure.subPhotoInfo, "Bearer " + SharePreferencesTools.getValue(TongJiActivity.this, "easyPhoto", "access_token", ""), requestBody, new DOkHttp.MyCallBack() {
                @Override
                public void onFailure(Request request, IOException e) {
                    subFailNumber++;
                    if ((subNumber+subFailNumber) == size) {
                        if (progressDialog1.isShowing()) {
                            progressDialog1.dismiss();
                        }
                        if (noList.size() > 0) {
                            dialogShow("图片提交完成，成功："+subNumber+",失败："+subFailNumber);
                        }else {
                            Toast.makeText(TongJiActivity.this, "图片提交完成，成功：" + subNumber + ",失败：" + subFailNumber, Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onResponse(String json) {
                    try {
                        JSONObject object = new JSONObject(json);
                        int code = object.getInt("code");
                        if (code == 0) {
                            subNumber++;
                            service.updateCameraPhoto(bean.getID());
                            if ((subNumber+subFailNumber) == size) {
                                if (progressDialog1.isShowing()) {
                                    progressDialog1.dismiss();
                                }
                                if (noList.size() > 0) {
                                    dialogShow("图片提交完成，成功："+subNumber+",失败："+subFailNumber);
                                }else {
                                    Toast.makeText(TongJiActivity.this, "图片提交完成，成功：" + subNumber + ",失败：" + subFailNumber, Toast.LENGTH_SHORT).show();
                                }
                                getShowData();
                            }
                        }else {
                            String message = object.getString("message");
                            Toast.makeText(TongJiActivity.this, message, Toast.LENGTH_SHORT).show();
                            progressDialog1.dismiss();
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        getShowData();
    }
}
