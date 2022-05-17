package com.example.lml.easyphoto.main;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.example.lml.easyphoto.FarmerInfo.FarmerInfoActivity;
import com.example.lml.easyphoto.R;
import com.example.lml.easyphoto.addressSelect.AddressPickerView;
import com.example.lml.easyphoto.addressSelect.AddressSelectBean;
import com.example.lml.easyphoto.banner.BannerFragment;
import com.example.lml.easyphoto.calculation.CalculationActivity;
import com.example.lml.easyphoto.camera.AitsApplication;
import com.example.lml.easyphoto.camera.PhotoMain;
import com.example.lml.easyphoto.camera.offline.OfflineActivity;
import com.example.lml.easyphoto.customize.CustomizeAddressActivity;
import com.example.lml.easyphoto.dikuai.DKService;
import com.example.lml.easyphoto.dikuai.DiKuaiActivity;
import com.example.lml.easyphoto.down.DownActivity;
import com.example.lml.easyphoto.gismapSelect.GismapSelectActivity;
import com.example.lml.easyphoto.history.HistoryActivity;
import com.example.lml.easyphoto.login.LoginActivity;
import com.example.lml.easyphoto.skp.Config;
import com.example.lml.easyphoto.util.Configure;
import com.example.lml.easyphoto.util.SearchService;
import com.example.lml.easyphoto.util.SharePreferencesTools;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MenuActivity extends Activity implements View.OnClickListener {
    //拍照开始
    private double lon, lat;
    private LocationManager manager;
    private String provider;
    private TextView tv_name;
    private TextView tv_jigou;
    private TextView tv_keepNumber;
    private TextView tv_subNumber;
    private TextView tv_location;
    private LinearLayout lin_plot;
    private LinearLayout lin_customize;
    private LinearLayout lin_history;
    private LinearLayout lin_data;
    private LinearLayout lin_result;
    private LinearLayout lin_offline;
    private DKService dkService;
    String officeName;
    String nickName;
    String province;
    String provinceName;
    String city;
    String cityName;
    String county;
    String countyName;
    String country;
    String countryName;
    String village;
    String villageName;
    private ServiceFeatureTable featureTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        initPage();
    }

    private void initPage() {
        featureTable = new ServiceFeatureTable(Configure.gisXZQYCX);
        officeName = SharePreferencesTools.getValue(MenuActivity.this, "easyPhoto", "officeName", "");
        province = SharePreferencesTools.getValue(MenuActivity.this, "easyPhoto", "province", "");
        provinceName = SharePreferencesTools.getValue(MenuActivity.this, "easyPhoto", "provinceName", "");
        city = SharePreferencesTools.getValue(MenuActivity.this, "easyPhoto", "city", "");
        cityName = SharePreferencesTools.getValue(MenuActivity.this, "easyPhoto", "cityName", "");
        county = SharePreferencesTools.getValue(MenuActivity.this, "easyPhoto", "county", "");
        countyName = SharePreferencesTools.getValue(MenuActivity.this, "easyPhoto", "countyName", "");
        country = SharePreferencesTools.getValue(MenuActivity.this, "easyPhoto", "country", "");
        countryName = SharePreferencesTools.getValue(MenuActivity.this, "easyPhoto", "countryName", "");
        village = SharePreferencesTools.getValue(MenuActivity.this, "easyPhoto", "village", "");
        villageName = SharePreferencesTools.getValue(MenuActivity.this, "easyPhoto", "villageName", "");
        nickName = SharePreferencesTools.getValue(MenuActivity.this, "easyPhoto", "nickName", "");
        tv_name = findViewById(R.id.menu_tv_name);
        tv_jigou = findViewById(R.id.menu_tv_jigou);
        tv_keepNumber = findViewById(R.id.menu_tv_keepNumber);
        tv_subNumber = findViewById(R.id.menu_tv_subNumber);
        tv_location = findViewById(R.id.menu_tv_location);
        lin_plot = findViewById(R.id.menu_lin_plot);
        lin_customize = findViewById(R.id.menu_lin_customize);
        lin_history = findViewById(R.id.menu_lin_history);
        lin_data = findViewById(R.id.menu_lin_data);
        lin_result = findViewById(R.id.menu_lin_result);
        lin_offline = findViewById(R.id.menu_lin_offline);
        lin_plot.setOnClickListener(this);
        lin_customize.setOnClickListener(this);
        lin_history.setOnClickListener(this);
        lin_data.setOnClickListener(this);
        lin_result.setOnClickListener(this);
        lin_offline.setOnClickListener(this);
        dkService = new DKService(this);
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providerlist = manager.getProviders(true);
        if (providerlist.contains(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
        } else if (providerlist.contains(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
        } else {
            Toast.makeText(this, "没有可用的位置提供器", Toast.LENGTH_LONG).show();
        }
        tv_name.setText(nickName);
        tv_jigou.setText(officeName);
        tv_location.setText(provinceName + cityName + countyName + countryName + villageName);
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
//                    double dos = (double) dataMap.get("xzdm");
//                    DecimalFormat df = new DecimalFormat("0");
//                    village = df.format(dos);
//                    String dos =  dataMap.get("xzdm").toString();
//                    DecimalFormat df = new DecimalFormat("0");
//                    village = dos;
                    village = (String) dataMap.get("xzdm");
                    country = village.substring(0, 9) + "000";
                    county = village.substring(0, 6) + "000000";
                    city = village.substring(0, 4) + "00000000";
                    province = village.substring(0, 2) + "0000000000";
                    tv_location.setText(provinceName + cityName + countyName + countryName + villageName);
                }
            });
        }

        // requestLocationUpdates()方法实时更新用户的位置信息,接收四个参数
        // ①第一个参数是位置提供器的类型
        // ②第二个参数是监听位置变化间隔的毫秒数
        // ③第三个参数是监听位置变化间隔的距离，达到设定距离时，触发第四个参数的监听器的onLocationChanged()方法，
        // 并把新的位置信息作为参数传入
        // ④第四个参数是LocationListener()监听器接口，所以应该传入的是它的实现类的对象
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10,
                listener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 关闭程序时将监听器移除
        if (manager != null) {
            manager.removeUpdates(listener);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 返回键
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            new AlertDialog.Builder(MenuActivity.this)
                    .setTitle("提示")
                    .setMessage("是否退出当前系统？")
                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            AitsApplication.getInstance().setAuthorised(false);
                            AitsApplication.getInstance().savePrefs();
                            finish();

                        }
                    }).setNegativeButton("否", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            }).create().show();

            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    LocationListener listener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onLocationChanged(Location location) {
            // 更新当前位置信息
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
                        village= (String) dataMap.get("xzdm");
//                        DecimalFormat df = new DecimalFormat("0");
//                        village = df.format(dos);
//                    DecimalFormat df = new DecimalFormat("0");
//                    village = df.format(dos);
//                        String dos = dataMap.get("xzdm").toString();
//                    DecimalFormat df = new DecimalFormat("0");
//                        village = dos;
                        country = village.substring(0, 9) + "000";
                        county = village.substring(0, 6) + "000000";
                        city = village.substring(0, 4) + "00000000";
                        province = village.substring(0, 2) + "0000000000";
                        tv_location.setText(provinceName + cityName + countyName + countryName + villageName);
                    }
                });
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_lin_plot:
                Intent intent_cb = new Intent(MenuActivity.this, FarmerInfoActivity.class);
                intent_cb.putExtra("province", province);
                intent_cb.putExtra("provinceName", provinceName);
                intent_cb.putExtra( "city",city);
                intent_cb.putExtra("cityName",cityName);
                intent_cb.putExtra("county",county);
                intent_cb.putExtra("countyName",countyName);
                intent_cb.putExtra("country",country);
                intent_cb.putExtra("countryName",countryName);
                intent_cb.putExtra("village",village);
                intent_cb.putExtra("villageName",villageName);
//                intent_cb.putExtra("city", "");
//                intent_cb.putExtra("cityName", "");
//                intent_cb.putExtra("county", "");
//                intent_cb.putExtra("countyName", "");
//                intent_cb.putExtra("country", "");
//                intent_cb.putExtra("countryName", "");
//                intent_cb.putExtra("village", "");
//                intent_cb.putExtra("villageName", "");
                startActivity(intent_cb);
                break;
            case R.id.menu_lin_customize:
                Intent intent_customize = new Intent(MenuActivity.this, CustomizeAddressActivity.class);
                intent_customize.putExtra("provinceCode", province);
                intent_customize.putExtra("provinceName", provinceName);
                intent_customize.putExtra( "cityCode",city);
                intent_customize.putExtra("cityName",cityName);
                intent_customize.putExtra("countyCode",county);
                intent_customize.putExtra("countyName",countyName);
                intent_customize.putExtra("countryCode",country);
                intent_customize.putExtra("countryName",countryName);
                intent_customize.putExtra("villageCode",village);
                intent_customize.putExtra("villageName",villageName);
                startActivity(intent_customize);
                break;
            case R.id.menu_lin_history:
                Intent intent_dk = new Intent(MenuActivity.this, HistoryActivity.class);
                startActivity(intent_dk);
                break;
            case R.id.menu_lin_offline:
                String[] path = getFileName();
                String serverPaths = SharePreferencesTools.getValue(MenuActivity.this, "easyPhoto", "offlineServerPath", "");
                if (!serverPaths.equals("")) {
                    File file = new File(serverPaths);
                    if (file.exists()){
                        Intent intent = new Intent(MenuActivity.this, OfflineActivity.class);
                        startActivity(intent);
                    }else {
                        if (path!=null&&path.length>0){
                            AlertDialog.Builder builder = new AlertDialog.Builder(MenuActivity.this);
                            builder.setTitle("底图选择");
                            builder.setCancelable(true);
                            builder.setItems(path, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/easyPhoto/backgroundmap/"+path[which];
                                    SharePreferencesTools.saveString(MenuActivity.this, "easyPhoto", "offlineServerPath", filePath);
                                    Intent intent = new Intent(MenuActivity.this, OfflineActivity.class);
                                    startActivity(intent);
                                }
                            });
                            builder.create().show();
                        }else {
                            Toast.makeText(this, "没有底图", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    if (path!=null&&path.length>0){
                        AlertDialog.Builder builder = new AlertDialog.Builder(MenuActivity.this);
                        builder.setTitle("底图选择");
                        builder.setCancelable(true);
                        builder.setItems(path, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/easyPhoto/backgroundmap/"+path[which];
                                SharePreferencesTools.saveString(MenuActivity.this, "easyPhoto", "offlineServerPath", filePath);
                                Intent intent = new Intent(MenuActivity.this, OfflineActivity.class);
                                startActivity(intent);
                            }
                        });
                        builder.create().show();
                    }else {
                        Toast.makeText(this, "没有底图", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.menu_lin_data:
                String gisCode = SharePreferencesTools.getValue(MenuActivity.this, "easyPhoto", "gisCode", "");
                String serverPath = SharePreferencesTools.getValue(MenuActivity.this, "easyPhoto", "serverPath", "");
                if (!gisCode.equals("")&&!serverPath.equals("")){
                    String provinceNameCk = SharePreferencesTools.getValue(MenuActivity.this, "easyPhoto", "provinceNameCk", "");
                    String provinceCodeCk = SharePreferencesTools.getValue(MenuActivity.this, "easyPhoto", "provinceCodeCk", "");
                    String cityNameCk = SharePreferencesTools.getValue(MenuActivity.this, "easyPhoto", "cityNameCk", "");
                    String cityCodeCk = SharePreferencesTools.getValue(MenuActivity.this, "easyPhoto", "cityCodeCk", "");
                    String countyNameCk = SharePreferencesTools.getValue(MenuActivity.this, "easyPhoto", "countyNameCk", "");
                    String countyCodeCk = SharePreferencesTools.getValue(MenuActivity.this, "easyPhoto", "countyCodeCk", "");
                    String countryNameCk = SharePreferencesTools.getValue(MenuActivity.this, "easyPhoto", "countryNameCk", "");
                    String countryCodeCk = SharePreferencesTools.getValue(MenuActivity.this, "easyPhoto", "countryCodeCk", "");
                    String villageNameCk = SharePreferencesTools.getValue(MenuActivity.this, "easyPhoto", "villageNameCk", "");
                    String villageCodeCk = SharePreferencesTools.getValue(MenuActivity.this, "easyPhoto", "villageCodeCk", "");

                    Intent intent = new Intent(MenuActivity.this, PhotoMain.class);
                    intent.putExtra("provinceCode", provinceCodeCk);
                    intent.putExtra("provinceName", provinceNameCk);
                    intent.putExtra("cityCode",cityCodeCk);
                    intent.putExtra("cityName",cityNameCk);
                    intent.putExtra("countyCode",countyCodeCk);
                    intent.putExtra("countyName",countyNameCk);
                    intent.putExtra("countryCode",countryCodeCk);
                    intent.putExtra("countryName",countryNameCk);
                    intent.putExtra("villageCode",villageCodeCk);
                    intent.putExtra("villageName",villageNameCk);
                    startActivity(intent);
                }else {
                    Intent intent_data = new Intent(MenuActivity.this, GismapSelectActivity.class);
                    intent_data.putExtra("provinceCode", province);
                    intent_data.putExtra("provinceName", provinceName);
                    intent_data.putExtra( "cityCode",city);
                    intent_data.putExtra("cityName",cityName);
                    intent_data.putExtra("countyCode",county);
                    intent_data.putExtra("countyName",countyName);
                    intent_data.putExtra("countryCode",country);
                    intent_data.putExtra("countryName",countryName);
                    intent_data.putExtra("villageCode",village);
                    intent_data.putExtra("villageName",villageName);
                    startActivity(intent_data);
                }

                break;
            default:
                break;
        }
    }
    //获取底图路径
    private String[] getFileName() {
        String[] paths = null;
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/easyPhoto/backgroundmap/";
        File file = new File(path);
        if (file.exists()) {
            File[] files = file.listFiles();
            paths = new String[files.length];
            for (int i = 0; i < files.length; i++) {
                paths[i] = files[i].getName();
            }
        } else {
            file.mkdirs();
        }
        return paths;
    }
    @Override
    protected void onResume() {
        super.onResume();
        Map<String, String> map = dkService.getNumberMap();
        tv_keepNumber.setText(map.get("keep") + "户");
        tv_subNumber.setText(map.get("sub") + "户");
    }
}
