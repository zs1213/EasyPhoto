package com.example.lml.easyphoto.camera;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.data.TileCache;
import com.esri.arcgisruntime.geometry.AreaUnit;
import com.esri.arcgisruntime.geometry.AreaUnitId;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.GeodeticCurveType;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.ArcGISMapImageLayer;
import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
import com.esri.arcgisruntime.layers.WebTiledLayer;
import com.esri.arcgisruntime.location.LocationDataSource;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.IdentifyGraphicsOverlayResult;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.TextSymbol;
import com.example.lml.easyphoto.FarmerInfo.FarmerInfoActivity;
import com.example.lml.easyphoto.R;
import com.example.lml.easyphoto.arcgisMap.MapsActivity;
import com.example.lml.easyphoto.camera.offline.OfflineActivity;
import com.example.lml.easyphoto.dikuai.DKBean;
import com.example.lml.easyphoto.dikuai.DKPointBean;
import com.example.lml.easyphoto.dikuai.DiKuaiActivity;
import com.example.lml.easyphoto.dikuai.subList.DKSubListActivity;
import com.example.lml.easyphoto.gismapSelect.GismapSelectActivity;
import com.example.lml.easyphoto.okHttpUtils.DOkHttp;
import com.example.lml.easyphoto.skp.CameraListActivity;
import com.example.lml.easyphoto.skp.SkpActivity;
import com.example.lml.easyphoto.skp.TimeCycleUtil;
import com.example.lml.easyphoto.tongji.TongJiActivity;
import com.example.lml.easyphoto.util.Configure;
import com.example.lml.easyphoto.util.Consts;
import com.example.lml.easyphoto.util.GetSingleSelectItem;
import com.example.lml.easyphoto.util.LoadingDialog;
import com.example.lml.easyphoto.util.SearchService;
import com.example.lml.easyphoto.util.SharePreferencesTools;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import cn.hutool.json.JSONUtil;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;


public class PhotoMain extends Activity implements View.OnClickListener {
    private MapView mMapView;
    private ImageView iv_take, iv_photo;
    private TextView tv_submit;
    private TextView tv_xuanze;
    private TextView tv_setting;
    private TextView tv_legend;
    private TextView btn_quandi;
    private TextView btn_choose;
    private TextView tv_tongji;
    private CameraService service;
    private String address = "";
    private String province = "";
    private String city = "";
    private String county = "";
    private String country = "";
    private String village = "";
    private String gisCode = "";
    private String serverPath = "";
    private String gisPath = "";
    private String lon = "";
    private String lat = "";
    private String oldlon = "";
    private String oldlat = "";
    private String fileName = Environment.getExternalStorageDirectory() + "/easyPhoto/backgroundmap";
    private List<CameraPhotoBean> photoBeanList = null;
    private List<CameraPhotoBean> showPhotoList = new ArrayList<CameraPhotoBean>();
    List<CameraPhotoBean> subList = null;
    private List<CameraPhotoBean> noList = new ArrayList<CameraPhotoBean>();
    private String[] crop = Consts.getStrNames(Consts.risk);
    private String[] zaiHai = Consts.getStrNames(Consts.disasterLevel);
    private String[] chuping = Consts.getStrNames(Consts.chuping);
    private String[] massifType = Consts.getStrNames(Consts.massifType);
    private String[] soilType = Consts.getStrNames(Consts.soilType);
    private String[] riskReson = Consts.getStrNames(Consts.riskReson);
    private String[] taskIf = {"???", "???"};
    private List<String> riskList;
    private List<String> sscd = null;
    private String zhongzhizuowu = "";
    private String mt = "";
    private String cp = "";
    private String st = "";
    private String shunshi = "";
    private String zaiHaiReson = "";
    private String taskCloseIf = "";
    private String pjcl = "0";
    private String zai = "";
    private String remark = "";
    private String foldeName = "";
    private GraphicsOverlay mGraphicsOverlay;
    private ServiceFeatureTable featureTable;
    private TextSymbol textSymbol;
    private SimpleMarkerSymbol markerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.parseColor("#0000FF"), 10);
    private Graphic graphic;
    private Callout mCallout;
    private ArcGISMap map;
    private int size = 0;
    private int subNumber = 0;
    private int subFailNumber = 0;
    private LoadingDialog dialog;//?????????
    private String provinceCodeGis;
    private String provinceNameGis;
    private String cityCodeGis;
    private String cityNameGis;
    private String countyCodeGis;
    private String countyNameGis;
    private String countryCodeGis;
    private String countryNameGis;
    private String villageCodeGis;
    private String villageNameGis;
    private ArcGISMapImageLayer mainArcGISVectorTiledLayer;
    private String theOnlineTiledLayers = Configure.gisXZQYUrl;
    private int typeFlag = 0;//0 ??????  1 ??????
    private int chooseFlag = 0;//0???????????? 1 ?????????  2 ??????
    private SimpleMarkerSymbol markerCentreSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.parseColor("#808080"), 10);
    private List<Point> pointList;
    private List<Graphic> graphicPointList;
    private List<Point> pointCentreList;
    private List<Graphic> graphicCentreList;
    private List<Graphic> graphicLineList;
    private List<Graphic> graphicAreaList;
    private List<Graphic> graphicDbList;
    private boolean canDrawTop = false;
    private boolean canScroll = false;
    private boolean longPressFlag = false;
    private boolean updataFlag = false;
    private int position = -1;
    private String type = "top";
    private SimpleLineSymbol simpleLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.DASH, Color.parseColor("#ffffff"), 3);
    private PointCollection poliCollection = new PointCollection(SpatialReferences.getWgs84());
    private Polygon polygon;
    private String polygonArea = "";
    private String polygonAreaKeep = "";
    private Graphic polygonGraphic;
    private Graphic areaGraphic;
    private String lonAndlat = "";
    private List<Map<String,String>> guaidianList;
    private LinearLayout btn_location, btn_delete, btn_back;
    //gps??????
    private LocationDisplay locationDisplay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_main);
        initView();
    }

    private void initView() {
        provinceCodeGis = getIntent().getStringExtra("provinceCode");
        provinceNameGis = getIntent().getStringExtra("provinceName");
        cityCodeGis = getIntent().getStringExtra("cityCode");
        cityNameGis = getIntent().getStringExtra("cityName");
        countyCodeGis = getIntent().getStringExtra("countyCode");
        countyNameGis = getIntent().getStringExtra("countyName");
        countryCodeGis = getIntent().getStringExtra("countryCode");
        countryNameGis = getIntent().getStringExtra("countryName");
        villageCodeGis = getIntent().getStringExtra("villageCode");
        villageNameGis = getIntent().getStringExtra("villageName");
        dialog = new LoadingDialog(this);
        progressDialog1 = new ProgressDialog(PhotoMain.this);
        progressDialog1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog1.setCancelable(false);
        service = new CameraService(PhotoMain.this);
        photoBeanList = service.getPhotoList();
        mMapView = findViewById(R.id.camera_map);
        iv_take = findViewById(R.id.camera_take);
        tv_xuanze = findViewById(R.id.camera_xuanze);
        iv_photo = findViewById(R.id.camera_photo);
        tv_setting = findViewById(R.id.camera_setting);
        tv_submit = findViewById(R.id.camera_submit);
        tv_legend = findViewById(R.id.camera_legend);
        btn_quandi = findViewById(R.id.camera_quandi);
        btn_choose = findViewById(R.id.camera_choose);
        tv_tongji = findViewById(R.id.camera_tongji);
        btn_location = findViewById(R.id.camera_btn_location);
        btn_back = findViewById(R.id.camera_btn_back);
        btn_delete = findViewById(R.id.camera_btn_delete);
        iv_take.setOnClickListener(this);
        iv_photo.setOnClickListener(this);
        tv_xuanze.setOnClickListener(this);
        tv_setting.setOnClickListener(this);
        tv_submit.setOnClickListener(this);
        tv_legend.setOnClickListener(this);
        btn_quandi.setOnClickListener(this);
        btn_choose.setOnClickListener(this);
        tv_tongji.setOnClickListener(this);
        btn_location.setOnClickListener(this);
        btn_back.setOnClickListener(this);
        btn_delete.setOnClickListener(this);
        mMapView.setAttributionTextVisible(false);
        gisCode = SharePreferencesTools.getValue(PhotoMain.this, "easyPhoto", "gisCode", "");
        serverPath = SharePreferencesTools.getValue(PhotoMain.this, "easyPhoto", "serverPath", "");
        gisPath = SharePreferencesTools.getValue(PhotoMain.this, "easyPhoto", "gisPath", "");
//        ArcGISRuntimeEnvironment.setLicense("runtimelite,1000,rud4449636536,none,NKMFA0PL4S0DRJE15166");
        ArcGISMapImageLayer webTiledLayer = new ArcGISMapImageLayer(gisPath);
        map = new ArcGISMap(new Basemap(webTiledLayer));
        mMapView.setMap(map);
        mGraphicsOverlay = new GraphicsOverlay();
        mainArcGISVectorTiledLayer = new ArcGISMapImageLayer(theOnlineTiledLayers);
        map.getOperationalLayers().add(new ArcGISMapImageLayer(serverPath));
        map.getOperationalLayers().add(mainArcGISVectorTiledLayer);
        mMapView.getGraphicsOverlays().add(mGraphicsOverlay);
        pointList = new ArrayList<>();
        graphicCentreList = new ArrayList<>();
        graphicPointList = new ArrayList<>();
        pointCentreList = new ArrayList<>();
        graphicLineList = new ArrayList<>();
        graphicAreaList = new ArrayList<>();
        graphicDbList = new ArrayList<>();
        mMapView.setOnTouchListener(new MyDefaultMapViewOnTouchListener(PhotoMain.this, mMapView));
        mCallout = mMapView.getCallout();
        featureTable = new ServiceFeatureTable(Configure.gisXZQYCX);
        reset();
        searchForState(gisCode);
        guaidianList = service.getGuaiDianList();
        for (int i = 0; i <guaidianList.size() ; i++) {
            Map<String,String> stringMap = guaidianList.get(i);
            String lonAndlat = stringMap.get("lonAndlat");
            String areaDb = stringMap.get("area");
            String [] pointStr = lonAndlat.split(";");
            List<DKPointBean> mlBeans = new ArrayList<>();
            for (int j = 0; j < pointStr.length; j++) {
                DKPointBean bean = new DKPointBean();
                String [] lonlatStr = pointStr[j].split(",");
                bean.setLon(lonlatStr[0]);
                bean.setLat(lonlatStr[1]);
                mlBeans.add(bean);
            }
            PointCollection poliCollectionDb = new PointCollection(SpatialReferences.getWgs84());
            for (int k = 0; k < mlBeans.size(); k++) {
                DKPointBean bean = mlBeans.get(k);
                Point wgsPoint = new Point(Double.parseDouble(bean.getLon()), Double.parseDouble(bean.getLat()));
                Point mapPoint = (Point) GeometryEngine.project(wgsPoint, SpatialReference.create(4326));
                poliCollectionDb.add(mapPoint);
            }
            Polygon polygonDb = new Polygon(poliCollectionDb);
            SimpleFillSymbol simpleFillSymbol= new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.parseColor("#000000"), null);
            Graphic polygonDbGraphic = new Graphic(polygonDb, simpleFillSymbol);
            polygonDbGraphic.setZIndex(-1);
            mGraphicsOverlay.getGraphics().add(polygonDbGraphic);
            graphicDbList.add(polygonDbGraphic);
            Point point = polygonDbGraphic.getGeometry().getExtent().getCenter();
            TextSymbol textSymbol = new TextSymbol(15, areaDb+"???", Color.parseColor("#ffffff"), TextSymbol.HorizontalAlignment.CENTER, TextSymbol.VerticalAlignment.MIDDLE);
            Graphic areaGraphicDb = new Graphic(point, textSymbol);
            areaGraphicDb.setZIndex(999);
            mGraphicsOverlay.getGraphics().add(areaGraphicDb);
            graphicAreaList.add(areaGraphicDb);
        }
        locationDisplay = mMapView.getLocationDisplay();
        locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.OFF);
        locationDisplay.startAsync();

    }
    private void addMarker(MotionEvent e) {
        reset();
        Point point = mMapView.screenToLocation(new android.graphics.Point(Math.round(e.getX()), Math.round(e.getY())));
        Point pointGps = (Point) GeometryEngine.project(point, SpatialReferences.getWgs84());
        graphic = new Graphic(point, markerSymbol);
        graphic.setZIndex(999);
        mGraphicsOverlay.getGraphics().add(graphic);
        oldlon = lon;
        oldlat = lat;
        lon = forMatLonLat(pointGps.getX());
        lat = forMatLonLat(pointGps.getY());
        TextView calloutContent = new TextView(getApplicationContext());
        calloutContent.setTextColor(Color.BLACK);
        calloutContent.setSingleLine();
        // format coordinates to 4 decimal places
        calloutContent.setText("??????: " + lon + ", ??????: " + lat);
        calloutContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallout.isShowing()) {
                    mCallout.dismiss();
                }
            }
        });
        mCallout.setLocation(point);
        mCallout.setContent(calloutContent);
        mCallout.show();
        mMapView.setViewpointCenterAsync(point);
        if (lon.equals("") || lat.equals("")) {
            address = "";
            return;
        }
        LoadingDialog.Builder loadBuilder = new LoadingDialog.Builder(PhotoMain.this)
                .setMessage("????????????????????????...")
                .setCancelable(true)
                .setCancelOutside(true);
        dialog = loadBuilder.create();
        dialog.show();
        SearchService.getInstance().getMap4Server(featureTable, pointGps, new SearchService.MapCallBack() {
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(PhotoMain.this, "???????????????????????????", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }

            @Override
            public void onResponse(Map<String, Object> dataMap) {
                dialog.dismiss();
                province = (String) dataMap.get("province");
                city = (String) dataMap.get("city");
                county = (String) dataMap.get("county");
                country = (String) dataMap.get("town");
                village = (String) dataMap.get("village");
                AlertDialog.Builder dialog = new AlertDialog.Builder(PhotoMain.this);
                dialog.setTitle("??????");
                View view = LayoutInflater.from(PhotoMain.this).inflate(R.layout.xingzheng, null);
                final EditText et_sheng = view.findViewById(R.id.xz_et_sheng);
                final EditText et_shi = view.findViewById(R.id.xz_et_shi);
                final EditText et_xian = view.findViewById(R.id.xz_et_xian);
                final EditText et_xiang = view.findViewById(R.id.xz_et_xiang);
                final EditText et_cun = view.findViewById(R.id.xz_et_cun);
                et_sheng.setText(province);
                et_shi.setText(city);
                et_xian.setText(county);
                et_xiang.setText(country);
                et_cun.setText(village);
                dialog.setView(view);
                address = province + city + county + country + village;
                dialog.setNeutralButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        province = et_sheng.getText().toString();
                        city = et_shi.getText().toString();
                        county = et_xian.getText().toString();
                        country = et_xiang.getText().toString();
                        village = et_cun.getText().toString();
                        address = province + city + county + country + village;
                    }
                });
                dialog.setCancelable(false);
                dialog.create().show();
            }
        });
    }

    private String forMatLonLat(double lonlat) {
        DecimalFormat df = new DecimalFormat("#######.000000");
        return df.format(lonlat);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.camera_take:
                if (address.equals("")) {
                    Toast.makeText(this, "??????????????????????????????", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (typeFlag==0){
                    if (lon.equals(oldlon)&&lat.equals(oldlat)){
                        Intent intent_skp = new Intent(PhotoMain.this, SkpActivity.class);
                        intent_skp.putExtra("flag", "burn");
                        intent_skp.putExtra("gps_lon", lon);
                        intent_skp.putExtra("gps_lat", lat);
                        intent_skp.putExtra("gps_address", address);
                        intent_skp.putExtra("zuowu", zhongzhizuowu);
                        intent_skp.putExtra("zaihai", zaiHaiReson);
                        intent_skp.putExtra("beizhu", remark);
                        intent_skp.putExtra("sunshi", shunshi);
                        intent_skp.putExtra("mt", mt);
                        foldeName = country + "/" + village + "/" +zhongzhizuowu + "/"+mt + "/" + TimeCycleUtil.getHour();
                        intent_skp.putExtra("foldeName", foldeName);
                        startActivityForResult(intent_skp, 0);
                    }else {
                        setting();
                    }

                }else {
                    if (pointList.size()>0){
                        lonAndlat = "";
                    }
                    if (lonAndlat.equals("")){
                        if (pointList.size()<3){
                            Toast.makeText(this, "?????????????????????????????????", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        setting();
                    }else {
                        Intent intent_skp = new Intent(PhotoMain.this, SkpActivity.class);
                        intent_skp.putExtra("flag", "burn");
                        intent_skp.putExtra("gps_lon", lon);
                        intent_skp.putExtra("gps_lat", lat);
                        intent_skp.putExtra("gps_address", address);
                        intent_skp.putExtra("zuowu", zhongzhizuowu);
                        intent_skp.putExtra("zaihai", zaiHaiReson);
                        intent_skp.putExtra("beizhu", remark);
                        intent_skp.putExtra("sunshi", shunshi);
                        intent_skp.putExtra("mt", mt);
                        foldeName = country + "/" + village + "/" +zhongzhizuowu + "/"+mt + "/" + TimeCycleUtil.getHour();
                        intent_skp.putExtra("foldeName", foldeName);
                        startActivityForResult(intent_skp, 0);
                    }

                }
                break;
            case R.id.camera_submit:
                subMit();
                break;
            case R.id.camera_tongji:
                Intent intent_tj = new Intent(PhotoMain.this, TongJiActivity.class);
                startActivity(intent_tj);
                break;
            case R.id.camera_photo:
                Intent intent = new Intent(PhotoMain.this, CameraListActivity.class);
                startActivity(intent);
                break;
            case R.id.camera_xuanze:
                Intent mapIntent = new Intent(PhotoMain.this, GismapSelectActivity.class);
                mapIntent.putExtra("provinceCode", provinceCodeGis);
                mapIntent.putExtra("provinceName", provinceNameGis);
                mapIntent.putExtra("cityCode", cityCodeGis);
                mapIntent.putExtra("cityName", cityNameGis);
                mapIntent.putExtra("countyCode", countyCodeGis);
                mapIntent.putExtra("countyName", countyNameGis);
                mapIntent.putExtra("countryCode", countryCodeGis);
                mapIntent.putExtra("countryName", countryNameGis);
                mapIntent.putExtra("villageCode", villageCodeGis);
                mapIntent.putExtra("villageName", villageNameGis);
                startActivity(mapIntent);
                break;
            case R.id.camera_setting:
                setting();
                break;
            case R.id.camera_quandi:
                lonAndlat = "";
                polygonAreaKeep = "";
                address = "";
                reset();
                if (typeFlag==0){
                    typeFlag =1;
                    btn_quandi.setTextColor(Color.parseColor("#506DF5"));
                }else {
                    typeFlag =0;
                    btn_quandi.setTextColor(Color.parseColor("#4A5175"));
                }
                break;
            case R.id.camera_choose:
                lonAndlat = "";
                polygonAreaKeep = "";
                address = "";
                reset();
                if (chooseFlag==0){
                    chooseFlag =1;
                    btn_choose.setText("?????????");
                    map.getBasemap().getBaseLayers().get(0).setVisible(false);
                    map.getOperationalLayers().get(0).setVisible(true);
                }else if (chooseFlag==1){
                    chooseFlag =2;
                    map.getBasemap().getBaseLayers().get(0).setVisible(true);
                    map.getOperationalLayers().get(0).setVisible(false);
                    btn_choose.setText("?????????");
                }else {
                    chooseFlag =0;
                    map.getBasemap().getBaseLayers().get(0).setVisible(true);
                    map.getOperationalLayers().get(0).setVisible(true);
                    btn_choose.setText("?????????");
                }
                break;
            case R.id.camera_legend:
                    DialogLegend dlgLegend = new DialogLegend();
                    dlgLegend.build(PhotoMain.this);
                    dlgLegend.show();
                break;
            case R.id.camera_btn_location:
                LocationDataSource.Location location = locationDisplay.getLocation();
                if (location != null) {
                    Point pointGps = location.getPosition();
                    if (pointGps!=null){
                        Point mapPoint = (Point) GeometryEngine.project(pointGps, SpatialReference.create(4326));
                        TextView calloutContent = new TextView(getApplicationContext());
                        calloutContent.setTextColor(Color.BLACK);
                        calloutContent.setSingleLine();
                        calloutContent.setText("??????: " + pointGps.getX() + ", ??????: " + pointGps.getY());
                        calloutContent.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (mCallout.isShowing()) {
                                    mCallout.dismiss();
                                }
                            }
                        });
                        mCallout.setLocation(mapPoint);
                        mCallout.setContent(calloutContent);
                        mCallout.show();
                        mMapView.setViewpointCenterAsync(mapPoint);
                        mMapView.setViewpointScaleAsync(1000);
                    }

                }

                break;
            case R.id.camera_btn_back:
                if (pointList.size() > 3) {
                    mGraphicsOverlay.getGraphics().remove(graphicPointList.get(graphicPointList.size() - 1));
                    mGraphicsOverlay.getGraphics().remove(graphicLineList.get(graphicLineList.size() - 1));
                    mGraphicsOverlay.getGraphics().remove(graphicLineList.get(graphicLineList.size() - 2));
                    mGraphicsOverlay.getGraphics().remove(graphicCentreList.get(pointCentreList.size() - 1));
                    mGraphicsOverlay.getGraphics().remove(graphicCentreList.get(pointCentreList.size() - 2));
                    pointList.remove(pointList.size() - 1);
                    graphicPointList.remove(graphicPointList.size() - 1);
                    graphicLineList.remove(graphicLineList.size() - 1);
                    graphicLineList.remove(graphicLineList.size() - 1);
                    graphicCentreList.remove(graphicCentreList.size() - 1);
                    graphicCentreList.remove(graphicCentreList.size() - 1);
                    pointCentreList.remove(pointCentreList.size() - 1);
                    pointCentreList.remove(pointCentreList.size() - 1);

                    PointCollection collection = new PointCollection(SpatialReferences.getWgs84());
                    collection.add(pointList.get(0));
                    collection.add(pointList.get(pointList.size() - 1));
                    Polyline polyline = new Polyline(collection);
                    Graphic graphic = new Graphic(polyline, simpleLineSymbol);
                    mGraphicsOverlay.getGraphics().add(graphic);
                    graphicLineList.add(graphic);

                    Point pointCentre1 = getCentrePoint(pointList.get(0), pointList.get(pointList.size() - 1));
                    Graphic graphicCentre = new Graphic(pointCentre1, markerCentreSymbol);
                    mGraphicsOverlay.getGraphics().add(graphicCentre);
                    graphicCentreList.add(graphicCentre);
                    pointCentreList.add(pointCentre1);
                    drawPolygon();
                } else if (pointList.size() == 3) {
                    mGraphicsOverlay.getGraphics().remove(graphicPointList.get(graphicPointList.size() - 1));
                    mGraphicsOverlay.getGraphics().remove(graphicLineList.get(graphicLineList.size() - 1));
                    mGraphicsOverlay.getGraphics().remove(graphicLineList.get(graphicLineList.size() - 2));
                    mGraphicsOverlay.getGraphics().remove(graphicCentreList.get(pointCentreList.size() - 1));
                    mGraphicsOverlay.getGraphics().remove(graphicCentreList.get(pointCentreList.size() - 2));
                    pointList.remove(pointList.size() - 1);
                    graphicPointList.remove(graphicPointList.size() - 1);
                    graphicLineList.remove(graphicLineList.size() - 1);
                    graphicLineList.remove(graphicLineList.size() - 1);
                    graphicCentreList.remove(graphicCentreList.size() - 1);
                    graphicCentreList.remove(graphicCentreList.size() - 1);
                    pointCentreList.remove(pointCentreList.size() - 1);
                    pointCentreList.remove(pointCentreList.size() - 1);
                    drawPolygon();
                } else if (pointList.size() == 2) {
                    mGraphicsOverlay.getGraphics().remove(graphicPointList.get(graphicPointList.size() - 1));
                    mGraphicsOverlay.getGraphics().remove(graphicLineList.get(graphicLineList.size() - 1));
                    mGraphicsOverlay.getGraphics().remove(graphicCentreList.get(pointCentreList.size() - 1));
                    pointList.remove(pointList.size() - 1);
                    graphicPointList.remove(graphicPointList.size() - 1);
                    graphicLineList.remove(graphicLineList.size() - 1);
                    graphicCentreList.remove(graphicCentreList.size() - 1);
                    pointCentreList.remove(pointCentreList.size() - 1);
                } else if (pointList.size() == 1) {
                    if (mCallout.isShowing()) {
                        mCallout.dismiss();
                    }
                    mGraphicsOverlay.getGraphics().remove(graphicPointList.get(graphicPointList.size() - 1));
                    pointList.remove(pointList.size() - 1);
                    graphicPointList.remove(graphicPointList.size() - 1);
                }
                break;
            case R.id.camera_btn_delete:
                if (mCallout.isShowing()) {
                    mCallout.dismiss();
                }
                for (int j = 0; j < graphicPointList.size(); j++) {
                    mGraphicsOverlay.getGraphics().remove(graphicPointList.get(j));
                }
                for (int j = 0; j < graphicCentreList.size(); j++) {
                    mGraphicsOverlay.getGraphics().remove(graphicCentreList.get(j));
                }
                for (int j = 0; j < graphicLineList.size(); j++) {
                    mGraphicsOverlay.getGraphics().remove(graphicLineList.get(j));
                }
                pointList.clear();
                graphicPointList.clear();
                pointCentreList.clear();
                graphicCentreList.clear();
                graphicLineList.clear();
                drawPolygon();
                break;
        }
    }

    private void subMit() {
        if (noList != null) {
            noList = null;
            noList = new ArrayList<CameraPhotoBean>();
        } else {
            noList = new ArrayList<CameraPhotoBean>();
        }
        subList = new ArrayList<CameraPhotoBean>();
        progressDialog1.setMessage("?????????");

        photoBeanList = service.getPhotoList();
        for (int i = 0; i < photoBeanList.size(); i++) {
            CameraPhotoBean bean = photoBeanList.get(i);
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
                Toast.makeText(this, "????????????????????????", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void dialogShow(String tishi) {
        String message = "???????????????????????????????????????????????????????????????\n";
        for (int i = 0; i < noList.size(); i++) {
            CameraPhotoBean photoBean = noList.get(i);
            message = message + photoBean.getFileName() + "  ";
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("??????");
        builder.setMessage(message);
        builder.setNeutralButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                AlertDialog.Builder builders = new AlertDialog.Builder(PhotoMain.this);
                builders.setTitle("??????");
                builders.setMessage(tishi+"???????????????");
                builders.setNeutralButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                        dialogInterface.dismiss();
                    }
                });
                builders.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builders.create().show();

            }
        });
        builder.create().show();
    }

    private String getPhotopath(String form, String Name) {
        String name = TimeCycleUtils.getPicData();
        String fileName = "/sdcard/" + Name + "/" + name + form;
        File file = new File("/sdcard/" + Name + "/");
        if (!file.exists()) {
            Log.e("TAG", "????????????????????????");
            file.mkdirs();// ?????????????????????????????????????????????
        }
        return fileName;
    }


    private void subFile(List<CameraPhotoBean> mediaList) {
        //????????????
        for (int i = 0; i < mediaList.size(); i++) {
            CameraPhotoBean bean = mediaList.get(i);
            File file = new File(bean.getFilePath());
            MultipartBody.Builder mb = new MultipartBody.Builder();
            mb.setType(MultipartBody.FORM);
            mb.addFormDataPart("files", bean.getFileName(),
                    RequestBody.create(null, file));
            mb.addFormDataPart("cityCode", bean.getGisCode().substring(0,4)+"00000000");
            mb.addFormDataPart("cityName", bean.getCity());
            mb.addFormDataPart("countryCode", bean.getGisCode().substring(0,9)+"000");
            mb.addFormDataPart("countryName", bean.getCountrysidename());
            mb.addFormDataPart("countyCode", bean.getGisCode().substring(0,6)+"000000");
            mb.addFormDataPart("countyName", bean.getTownname());
            mb.addFormDataPart("cropName", bean.getZhType());
            mb.addFormDataPart("degreeLoss", bean.getSscd());
            mb.addFormDataPart("isPreliminaryAssessment", bean.getChuPing());
            mb.addFormDataPart("lat", bean.getLat());
            mb.addFormDataPart("lon", bean.getLon());
            mb.addFormDataPart("pointRational", bean.getLonAndlat());
            mb.addFormDataPart("massifType", bean.getMassifType());
            mb.addFormDataPart("provinceCode", bean.getGisCode().substring(0,2)+"0000000000");
            mb.addFormDataPart("provinceName", bean.getProvince());
            mb.addFormDataPart("remark", bean.getRemark());
            mb.addFormDataPart("riskReason", bean.getRiskReason());
            mb.addFormDataPart("soilType", bean.getSoilType());
            mb.addFormDataPart("villageCode", bean.getGisCode());
            mb.addFormDataPart("villageName", bean.getVillagename());
            RequestBody requestBody = mb.build();
            DOkHttp.getInstance().uploadPost2ServerProgress(PhotoMain.this, Configure.subPhotoInfo, "Bearer " + SharePreferencesTools.getValue(PhotoMain.this, "easyPhoto", "access_token", ""), requestBody, new DOkHttp.MyCallBack() {
                @Override
                public void onFailure(Request request, IOException e) {
                    subFailNumber++;
                    if ((subNumber+subFailNumber) == size) {
                        if (progressDialog1.isShowing()) {
                            progressDialog1.dismiss();
                        }
                        photoBeanList = service.getPhotoList();
                        if (noList.size() > 0) {
                            dialogShow("??????????????????????????????"+subNumber+",?????????"+subFailNumber);
                        }else {
                            AlertDialog.Builder builders = new AlertDialog.Builder(PhotoMain.this);
                            builders.setTitle("??????");
                            builders.setMessage("??????????????????????????????"+subNumber+",?????????"+subFailNumber+"???????????????");
                            builders.setNeutralButton("??????", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                    dialogInterface.dismiss();
                                }
                            });
                            builders.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builders.create().show();
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
                                photoBeanList = service.getPhotoList();
                                if (noList.size() > 0) {
                                    dialogShow("??????????????????????????????"+subNumber+",?????????"+subFailNumber);
                                }else {
                                    AlertDialog.Builder builders = new AlertDialog.Builder(PhotoMain.this);
                                    builders.setTitle("??????");
                                    builders.setMessage("??????????????????????????????"+subNumber+",?????????"+subFailNumber+"???????????????");
                                    builders.setNeutralButton("??????", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            finish();
                                            dialogInterface.dismiss();
                                        }
                                    });
                                    builders.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    builders.create().show();
                                }

                            }
                        } else {
                            subFailNumber++;
                            String message = object.getString("message");
                            Toast.makeText(PhotoMain.this, message, Toast.LENGTH_SHORT).show();
                            progressDialog1.dismiss();
                            if ((subNumber+subFailNumber) == size) {
                                Toast.makeText(PhotoMain.this, "??????????????????????????????"+subNumber+",?????????"+subFailNumber, Toast.LENGTH_SHORT).show();
                                if (progressDialog1.isShowing()) {
                                    progressDialog1.dismiss();
                                }
                                photoBeanList = service.getPhotoList();
                                if (noList.size() > 0) {
                                    dialogShow("??????????????????????????????"+subNumber+",?????????"+subFailNumber);
                                }else {
                                    AlertDialog.Builder builders = new AlertDialog.Builder(PhotoMain.this);
                                    builders.setTitle("??????");
                                    builders.setMessage("??????????????????????????????"+subNumber+",?????????"+subFailNumber+"???????????????");
                                    builders.setNeutralButton("??????", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            finish();
                                            dialogInterface.dismiss();
                                        }
                                    });
                                    builders.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    builders.create().show();
                                }

                            }
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
    protected void onDestroy() {
        mMapView.dispose();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        mMapView.resume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mMapView.pause();
        super.onPause();
    }

    public void goBack(View view) {
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (resultCode == 0) {
                String resultList = data.getStringExtra("skpResult");
                List<String> photoList = new Gson().fromJson(resultList, new TypeToken<List<String>>() {
                }.getType());

                for (int i = 0; i < photoList.size(); i++) {
                    String fName[] = photoList.get(i).split("/");
                    CameraPhotoBean photoBean = new CameraPhotoBean();
                    photoBean.setFileName(fName[fName.length - 1]);
                    photoBean.setFilePath(photoList.get(i));
                    photoBean.setID(TimeCycleUtils.getTaskId());
                    photoBean.setState("0");
                    photoBean.setCreateTime(TimeCycleUtils.getTime());
                    photoBean.setProvince(province);
                    photoBean.setCity(city);
                    photoBean.setTownname(county);
                    photoBean.setCountrysidename(country);
                    photoBean.setVillagename(village);
                    photoBean.setCorporateName(getString(R.string.comName));
                    photoBean.setLon(lon);
                    photoBean.setLat(lat);
                    photoBean.setFolderName(foldeName);
                    photoBean.setRemark(remark);
                    photoBean.setRiskReason(zaiHaiReson);
                    photoBean.setRiskCode(zai);
                    photoBean.setMassifType(mt);
                    photoBean.setZhType(zhongzhizuowu);
                    photoBean.setIsTask(taskCloseIf);
                    photoBean.setCnpjcl(pjcl);
                    photoBean.setChuPing(cp);
                    photoBean.setSoilType(st);
                    photoBean.setSscd(shunshi);
                    photoBean.setGisCode(gisCode);
                    photoBean.setServerPath(serverPath);
                    photoBean.setGisPath(gisPath);
                    photoBean.setArea(polygonAreaKeep);
                    photoBean.setLonAndlat(lonAndlat);
                    photoBeanList.add(0, photoBean);
                    ContentValues values = new ContentValues();
                    values.put("ID", photoBean.getID());
                    values.put("fileName", photoBean.getFileName());
                    values.put("filePath", photoBean.getFilePath());
                    values.put("state", "0");
                    values.put("folderName", photoBean.getFolderName());
                    values.put("createTime", photoBean.getCreateTime());
                    values.put("province", photoBean.getProvince());
                    values.put("city", photoBean.getCity());
                    values.put("townname", photoBean.getTownname());
                    values.put("countrysidename", photoBean.getCountrysidename());
                    values.put("villagename", photoBean.getVillagename());
                    values.put("corporateName", photoBean.getCorporateName());
                    values.put("lon", photoBean.getLon());
                    values.put("lat", photoBean.getLat());
                    values.put("remark", remark);
                    values.put("riskReason", zaiHaiReson);
                    values.put("riskCode", zai);
                    values.put("massifType", mt);
                    values.put("zhType", zhongzhizuowu);
                    values.put("isTask", taskCloseIf);
                    values.put("cnpjcl", pjcl);
                    values.put("chuPing", cp);
                    values.put("soilType", st);
                    values.put("sscd", shunshi);
                    values.put("area", polygonAreaKeep);
                    values.put("lonAndlat", lonAndlat);
                    values.put("gisCode", gisCode);
                    values.put("serverPath", serverPath);
                    values.put("gisPath", gisPath);
                    service.insertPhoto(values);
                    LoadLocalImageUtil.getInstance().displayFromSDCard(photoList.get(i), iv_photo);
                }

            }
        }

        /*} else if (requestCode == 101 && resultCode == 101) {//??????????????????????????????????????????
            SharePreferencesTools.saveString(PhotoMain.this, "easyPhoto", "gisPath", data.getStringExtra("path"));
            Intent intent = new Intent(PhotoMain.this, PhotoMain.class);
            startActivity(intent);
            finish();
        }*/
    }

    //??????????????????
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

    private ProgressDialog progressDialog1;


    private void setting() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(PhotoMain.this);
        LayoutInflater inflater = LayoutInflater.from(PhotoMain.this);
        View layout = inflater.inflate(R.layout.layout_dialog, null);
        final EditText et_beizhu = layout.findViewById(R.id.edit_camera_beizhu);
        final EditText et_zai = layout.findViewById(R.id.dl_edt_zai);
        final EditText et_crop = layout.findViewById(R.id.dl_edt_crop);
        final EditText et_massifType = layout.findViewById(R.id.dl_edt_massifType);
        final EditText et_chuping = layout.findViewById(R.id.dl_edt_chuping);
        final EditText et_soilType = layout.findViewById(R.id.dl_edt_soilType);
        final EditText et_sscd = layout.findViewById(R.id.dl_edt_sscd);
        final EditText et_taskIf = layout.findViewById(R.id.dl_edt_taskIf);
        final EditText et_riskType = layout.findViewById(R.id.dl_edt_zaiType);
        final EditText et_pjcl = layout.findViewById(R.id.dl_edt_chanliang);
        et_pjcl.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

                //??????.???????????????????????????
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + 3);
                        et_pjcl.setText(s);
                        et_pjcl.setSelection(s.length());
                    }
                }

                //??????.???????????????,????????????????????????0
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    et_pjcl.setText(s);
                    et_pjcl.setSelection(2);
                }
                if (s.toString().trim().length() > 0&&!s.toString().trim().contains(".")) {
                    int number = Integer.parseInt(s.toString().trim());
                    if (number > 999999) {
                        s = s.toString().subSequence(0, 6);
                        et_pjcl.setText(s);
                        et_pjcl.setSelection(s.length());
                    }
                }
                //?????????????????????0???????????????????????????".",?????????????????????
                if (s.toString().startsWith("0")
                        && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        et_pjcl.setText(s.subSequence(0, 1));
                        et_pjcl.setSelection(1);
                        return;
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }

        });
        et_zai.setText(zaiHai[0]);
        sscd = new ArrayList<String>();
        sscd.add("0");
        sscd.add("5");
        sscd.add("10");
        sscd.add("15");
        sscd.add("20");
        sscd.add("25");
        sscd.add("30");
        sscd.add("35");
        sscd.add("40");
        sscd.add("45");
        sscd.add("50");
        sscd.add("55");
        sscd.add("60");
        sscd.add("65");
        sscd.add("70");
        sscd.add("75");
        sscd.add("80");
        sscd.add("85");
        sscd.add("90");
        sscd.add("95");
        sscd.add("100");
        et_taskIf.setText(taskIf[0]);
        et_riskType.setText(riskReson[0]);
        et_pjcl.setText("0");

        et_crop.setText(crop[0]);
        et_chuping.setText(chuping[0]);
        et_massifType.setText(massifType[0]);
        et_soilType.setText(soilType[0]);
        et_sscd.setText(sscd.get(0));
        et_beizhu.setText("");
        et_crop.setOnTouchListener(new GetSingleSelectItem(PhotoMain.this, et_crop, "????????????", crop, false));
        et_taskIf.setOnTouchListener(new GetSingleSelectItem(PhotoMain.this, et_taskIf, "?????????", taskIf, false));
        //et_riskType.setOnTouchListener(new GetSingleSelectItem(CameraActivity.this, et_riskType, "????????????", riskReson, false));
        et_riskType.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(PhotoMain.this);
                    dialog.setTitle("????????????");
                    dialog.setMultiChoiceItems(riskReson, null, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            if (isChecked) {
                                // ??????
                                riskList.add(riskReson[which]);
                            } else {
                                // ????????????
                                if (riskList.contains(riskReson[which])) {
                                    riskList.remove(riskReson[which]);
                                }
                            }
                        }
                    });
                    dialog.setPositiveButton("??????", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            //TODO ??????????????????
                            if (riskList != null && riskList.size() > 0) {
                                String str = "";
                                for (int i = 0; i < riskList.size(); i++) {
                                    if (i != riskList.size() - 1) {
                                        str += riskList.get(i) + ",";
                                    } else {
                                        str += riskList.get(i);
                                    }
                                }
                                et_riskType.setText(str);
                            }
                            // ???????????????
                            arg0.dismiss();
                        }
                    });
                    /*dialog.setNegativeButton("??????", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            // TODO ??????????????????
                            riskList.clear();
                            // ???????????????
                            arg0.dismiss();
                        }
                    });*/
                    dialog.create();
                    dialog.show();
                }
                return false;
            }
        });

        et_zai.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(PhotoMain.this);
                    LayoutInflater inflater = (LayoutInflater) PhotoMain.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.getinsured_name, null);
                    dialog.setTitle("????????????");
                    dialog.setView(layout)
                            .setCancelable(false)
                            .setNegativeButton("??????", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    final AlertDialog accAlert = dialog.create();
                    LinearLayout liner = (LinearLayout) layout.findViewById(R.id.search_liner);
                    liner.setVisibility(View.GONE);
                    final ListView accList = (ListView) layout.findViewById(R.id.accList);

                    final List<Map<String, String>> itemData = new ArrayList<Map<String, String>>();
                    Map<String, String> mp = null;
                    for (int i = 0; i < zaiHai.length; i++) {
                        mp = new HashMap<String, String>();
                        mp.put("name", zaiHai[i]);
                        itemData.add(mp);
                    }
                    SimpleAdapter czadapter = new SimpleAdapter(PhotoMain.this, itemData,
                            android.R.layout.simple_list_item_1, new String[]{"name"},
                            new int[]{android.R.id.text1});
                    accList.setAdapter(czadapter);
                    accList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                                long arg3) {
                            Map<String, String> m = itemData.get(arg2);
                            et_zai.setText(m.get("name"));
                            sscd.clear();
                            if (m.get("name").equals("?????????")) {
                                sscd.add("0");
                                sscd.add("5");
                                sscd.add("10");
                                sscd.add("15");
                                sscd.add("20");
                                sscd.add("25");
                                sscd.add("30");
                            } else if (m.get("name").equals("??????")) {
                                sscd.add("35");
                                sscd.add("40");
                            } else if (m.get("name").equals("??????")) {
                                sscd.add("45");
                                sscd.add("50");
                                sscd.add("55");
                                sscd.add("60");
                            } else if (m.get("name").equals("??????")) {
                                sscd.add("65");
                                sscd.add("70");
                                sscd.add("75");
                            } else if (m.get("name").equals("??????")) {
                                sscd.add("80");
                                sscd.add("85");
                                sscd.add("90");
                                sscd.add("95");
                                sscd.add("100");
                            }
                            et_sscd.setText(sscd.get(0));
                            accAlert.dismiss();
                        }
                    });
                    accAlert.show();
                }
                return false;
            }
        });
        et_massifType.setOnTouchListener(new GetSingleSelectItem(PhotoMain.this, et_massifType, "????????????", massifType, false));
        et_chuping.setOnTouchListener(new GetSingleSelectItem(PhotoMain.this, et_chuping, "????????????", chuping, false));
        et_soilType.setOnTouchListener(new GetSingleSelectItem(PhotoMain.this, et_soilType, "????????????", soilType, false));
        et_sscd.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(PhotoMain.this);
                    LayoutInflater inflater = (LayoutInflater) PhotoMain.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.getinsured_name, null);
                    dialog.setTitle("????????????");
                    dialog.setView(layout)
                            .setCancelable(false)
                            .setNegativeButton("??????", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    final AlertDialog accAlert = dialog.create();
                    LinearLayout liner = (LinearLayout) layout.findViewById(R.id.search_liner);
                    liner.setVisibility(View.GONE);
                    final ListView accList = (ListView) layout.findViewById(R.id.accList);

                    final List<Map<String, String>> itemData = new ArrayList<Map<String, String>>();
                    Map<String, String> mp = null;
                    for (int i = 0; i < sscd.size(); i++) {
                        mp = new HashMap<String, String>();
                        mp.put("name", sscd.get(i));
                        itemData.add(mp);
                    }
                    SimpleAdapter czadapter = new SimpleAdapter(PhotoMain.this, itemData,
                            android.R.layout.simple_list_item_1, new String[]{"name"},
                            new int[]{android.R.id.text1});
                    accList.setAdapter(czadapter);
                    accList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                                long arg3) {
                            Map<String, String> m = itemData.get(arg2);
                            et_sscd.setText(m.get("name"));
                            accAlert.dismiss();
                        }
                    });
                    accAlert.show();
                }
                return false;
            }
        });
        dialog.setTitle("??????");
        dialog.setView(layout)
                .setCancelable(false)
                .setNeutralButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                zai = et_zai.getText().toString();
                cp = et_chuping.getText().toString();
                zhongzhizuowu = et_crop.getText().toString();
                mt = et_massifType.getText().toString();
                st = et_soilType.getText().toString();
                shunshi = et_sscd.getText().toString();
                taskCloseIf = et_taskIf.getText().toString();
                zaiHaiReson = et_riskType.getText().toString();
                remark = et_beizhu.getText().toString();
                pjcl = et_pjcl.getText().toString();
                ContentValues values = new ContentValues();
                values.put("lon", lon);
                values.put("lat", lat);
                values.put("remark", remark);
                values.put("riskReason", zaiHaiReson);
                values.put("riskCode", zai);
                values.put("massifType", mt);
                values.put("zhType", zhongzhizuowu);
                values.put("isTask", taskCloseIf);
                values.put("cnpjcl", pjcl);
                values.put("chuPing", cp);
                values.put("soilType", st);
                values.put("sscd", shunshi);
                service.updateSetting(values);
                dialogInterface.cancel();
                if (typeFlag==1){
                    PointCollection poliCollectionDb = new PointCollection(SpatialReferences.getWgs84());
                    if (pointList.size()>0){
                        lonAndlat = "";
                    }
                    for (int j = 0; j < pointList.size(); j++) {
                        Point point = pointList.get(j);
                        Point wgs84Point = (Point) GeometryEngine.project(point, SpatialReferences.getWgs84());
                        if (j<pointList.size()-1){
                            lonAndlat+=wgs84Point.getX()+","+wgs84Point.getY()+";";
                        }else {
                            lonAndlat+=wgs84Point.getX()+","+wgs84Point.getY();
                        }
                        poliCollectionDb.add(point);
                    }

                    Polygon polygonDb = new Polygon(poliCollectionDb);
                    SimpleFillSymbol simpleFillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.parseColor("#000000"), null);
                    Graphic polygonDbGraphic = new Graphic(polygonDb, simpleFillSymbol);
                    polygonDbGraphic.setZIndex(-1);
                    mGraphicsOverlay.getGraphics().add(polygonDbGraphic);
                    graphicDbList.add(polygonDbGraphic);
                    Point point = polygonDbGraphic.getGeometry().getExtent().getCenter();
                    TextSymbol textSymbol = new TextSymbol(15,  polygonArea + "???", Color.parseColor("#ffffff"), TextSymbol.HorizontalAlignment.CENTER, TextSymbol.VerticalAlignment.MIDDLE);
                    Graphic areaGraphicDb = new Graphic(point, textSymbol);
                    areaGraphicDb.setZIndex(999);
                    mGraphicsOverlay.getGraphics().add(areaGraphicDb);
                    graphicAreaList.add(areaGraphicDb);
                    for (int j = 0; j < graphicPointList.size(); j++) {
                        mGraphicsOverlay.getGraphics().remove(graphicPointList.get(j));
                    }
                    for (int j = 0; j < graphicCentreList.size(); j++) {
                        mGraphicsOverlay.getGraphics().remove(graphicCentreList.get(j));
                    }
                    for (int j = 0; j < graphicLineList.size(); j++) {
                        mGraphicsOverlay.getGraphics().remove(graphicLineList.get(j));
                    }
                    pointList.clear();
                    graphicPointList.clear();
                    pointCentreList.clear();
                    graphicCentreList.clear();
                    graphicLineList.clear();
                    drawPolygon();
                    Intent intent_skp = new Intent(PhotoMain.this, SkpActivity.class);
                    intent_skp.putExtra("flag", "burn");
                    intent_skp.putExtra("gps_lon", lon);
                    intent_skp.putExtra("gps_lat", lat);
                    intent_skp.putExtra("gps_address", address);
                    intent_skp.putExtra("zuowu", zhongzhizuowu);
                    intent_skp.putExtra("zaihai", zaiHaiReson);
                    intent_skp.putExtra("beizhu", remark);
                    intent_skp.putExtra("sunshi", shunshi);
                    intent_skp.putExtra("mt", mt);
                    foldeName = country + "/" + village + "/" +zhongzhizuowu + "/"+mt + "/" + TimeCycleUtil.getHour();
                    intent_skp.putExtra("foldeName", foldeName);
                    startActivityForResult(intent_skp, 0);
                }else {
                    oldlon = lon;
                    oldlat = lat;
                    Intent intent_skp = new Intent(PhotoMain.this, SkpActivity.class);
                    intent_skp.putExtra("flag", "burn");
                    intent_skp.putExtra("gps_lon", lon);
                    intent_skp.putExtra("gps_lat", lat);
                    intent_skp.putExtra("gps_address", address);
                    intent_skp.putExtra("zuowu", zhongzhizuowu);
                    intent_skp.putExtra("zaihai", zaiHaiReson);
                    intent_skp.putExtra("beizhu", remark);
                    intent_skp.putExtra("sunshi", shunshi);
                    intent_skp.putExtra("mt", mt);
                    foldeName = country + "/" + village + "/" +zhongzhizuowu + "/"+mt + "/" + TimeCycleUtil.getHour();
                    intent_skp.putExtra("foldeName", foldeName);
                    startActivityForResult(intent_skp, 0);
                }
            }
        });
        dialog.show();
    }

    public void reset() {
        taskCloseIf = "";
        pjcl = "";
        zai = "";
        zhongzhizuowu = "";
        cp = "";
        mt = "";
        st = "";
        shunshi = "";
        zaiHaiReson = "";
        remark = "";
        riskList = new ArrayList<String>();
        for (int j = 0; j < graphicPointList.size(); j++) {
            mGraphicsOverlay.getGraphics().remove(graphicPointList.get(j));
        }
        for (int j = 0; j < graphicCentreList.size(); j++) {
            mGraphicsOverlay.getGraphics().remove(graphicCentreList.get(j));
        }
        for (int j = 0; j < graphicLineList.size(); j++) {
            mGraphicsOverlay.getGraphics().remove(graphicLineList.get(j));
        }
        pointList.clear();
        graphicPointList.clear();
        pointCentreList.clear();
        graphicCentreList.clear();
        graphicLineList.clear();
        drawPolygon();
    }

    private void searchForState(String searchString) {
        QueryParameters query = new QueryParameters();//220303200205
        query.setWhereClause("xzdm like '%" + searchString+"%'");
        final ListenableFuture<FeatureQueryResult> future = featureTable.queryFeaturesAsync(query);
        future.addDoneListener(() -> {
            try {
                FeatureQueryResult result = future.get();
                Iterator<Feature> resultIterator = result.iterator();
                if (resultIterator.hasNext()) {
                    Feature feature = resultIterator.next();
                    Envelope envelope = feature.getGeometry().getExtent();
                    mMapView.setViewpointGeometryAsync(envelope, 10);
                    // select the feature
//                    mainArcGISVectorTiledLayer.selectFeature(feature);
                } else {
                    Toast.makeText(this, "No states found with name: " + searchString, Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                String error = "Feature search failed for: " + searchString + ". Error: " + e.getMessage();
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });
    }
    public class MyDefaultMapViewOnTouchListener extends DefaultMapViewOnTouchListener {

        public MyDefaultMapViewOnTouchListener(Context context, MapView mapView) {
            super(context, mapView);
        }

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (typeFlag==1){
                    Map<String, Object> map = getSelectedIndex(event.getX(), event.getY());
                    int index = (int) map.get("index");
                    position = index;
                    if (index == -1) {
                        canDrawTop = true;
                    } else {
                        type = (String) map.get("type");
                        canDrawTop = false;
                        canScroll = false;
                        if (type.equals("centre")) {
                            if (pointList.size() == 2) {
                                //?????????????????????
                                mGraphicsOverlay.getGraphics().remove(graphicCentreList.get(position));
                                graphicCentreList.remove(position);
                                pointCentreList.remove(position);
                                //?????????
                                mGraphicsOverlay.getGraphics().remove(graphicLineList.get(position));
                                graphicLineList.remove(graphicLineList.get(position));
                                //????????????
                                Point point = mMapView.screenToLocation(new android.graphics.Point(Math.round(event.getX()), Math.round(event.getY())));
                                Graphic graphic = new Graphic(point, markerSymbol);
                                graphic.setZIndex(999);
                                mGraphicsOverlay.getGraphics().add(graphic);
                                graphicPointList.add(position + 1, graphic);
                                pointList.add(position + 1, point);
                                //??????1
                                PointCollection collection1 = new PointCollection(SpatialReferences.getWgs84());
                                collection1.add(pointList.get(position));
                                collection1.add(pointList.get(position + 1));
                                Polyline polyline1 = new Polyline(collection1);
                                Graphic graphics1 = new Graphic(polyline1, simpleLineSymbol);
                                mGraphicsOverlay.getGraphics().add(graphics1);
                                graphicLineList.add(position, graphics1);
                                //???????????????1
                                Point pointCentre1 = getCentrePoint(pointList.get(position), pointList.get(position + 1));
                                Graphic graphicCentre1 = new Graphic(pointCentre1, markerCentreSymbol);
                                mGraphicsOverlay.getGraphics().add(graphicCentre1);
                                graphicCentreList.add(position, graphicCentre1);
                                pointCentreList.add(position, pointCentre1);
                                //??????2
                                PointCollection collection2 = new PointCollection(SpatialReferences.getWgs84());
                                collection2.add(pointList.get(position + 1));
                                collection2.add(pointList.get(position + 2));
                                Polyline polyline2 = new Polyline(collection2);
                                Graphic graphics2 = new Graphic(polyline2, simpleLineSymbol);
                                mGraphicsOverlay.getGraphics().add(graphics2);
                                graphicLineList.add(position + 1, graphics2);
                                //???????????????2
                                Point pointCentre2 = getCentrePoint(pointList.get(position + 1), pointList.get(position + 2));
                                Graphic graphicCentre2 = new Graphic(pointCentre2, markerCentreSymbol);
                                mGraphicsOverlay.getGraphics().add(graphicCentre2);
                                graphicCentreList.add(position + 1, graphicCentre2);
                                pointCentreList.add(position + 1, pointCentre2);

                                //??????3
                                PointCollection collection3 = new PointCollection(SpatialReferences.getWgs84());
                                collection3.add(pointList.get(position + 2));
                                collection3.add(pointList.get(position));
                                Polyline polyline3 = new Polyline(collection3);
                                Graphic graphics3 = new Graphic(polyline3, simpleLineSymbol);
                                mGraphicsOverlay.getGraphics().add(graphics3);
                                graphicLineList.add(position + 2, graphics3);
                                //???????????????3
                                Point pointCentre3 = getCentrePoint(pointList.get(position + 2), pointList.get(position));
                                Graphic graphicCentre3 = new Graphic(pointCentre3, markerCentreSymbol);
                                mGraphicsOverlay.getGraphics().add(graphicCentre3);
                                graphicCentreList.add(position + 2, graphicCentre3);
                                pointCentreList.add(position + 2, pointCentre3);
                            } else if (pointList.size() > 2) {
                                //?????????????????????
                                mGraphicsOverlay.getGraphics().remove(graphicCentreList.get(position));
                                graphicCentreList.remove(position);
                                pointCentreList.remove(position);
                                //?????????
                                mGraphicsOverlay.getGraphics().remove(graphicLineList.get(position));
                                graphicLineList.remove(graphicLineList.get(position));
                                //????????????
                                Point point = mMapView.screenToLocation(new android.graphics.Point(Math.round(event.getX()), Math.round(event.getY())));
                                Graphic graphic = new Graphic(point, markerSymbol);
                                graphic.setZIndex(999);
                                mGraphicsOverlay.getGraphics().add(graphic);
                                graphicPointList.add(position + 1, graphic);
                                pointList.add(position + 1, point);

                                //??????1
                                PointCollection collection1 = new PointCollection(SpatialReferences.getWgs84());
                                collection1.add(pointList.get(position));
                                collection1.add(pointList.get(position + 1));
                                Polyline polyline1 = new Polyline(collection1);
                                Graphic graphics1 = new Graphic(polyline1, simpleLineSymbol);
                                mGraphicsOverlay.getGraphics().add(graphics1);
                                graphicLineList.add(position, graphics1);
                                //???????????????1
                                Point pointCentre1 = getCentrePoint(pointList.get(position), pointList.get(position + 1));
                                Graphic graphicCentre1 = new Graphic(pointCentre1, markerCentreSymbol);
                                mGraphicsOverlay.getGraphics().add(graphicCentre1);
                                graphicCentreList.add(position, graphicCentre1);
                                pointCentreList.add(position, pointCentre1);
                                //??????2
                                PointCollection collection2 = new PointCollection(SpatialReferences.getWgs84());
                                collection2.add(pointList.get(position + 1));
                                if (position + 2 == pointList.size()) {
                                    collection2.add(pointList.get(0));
                                } else {
                                    collection2.add(pointList.get(position + 2));
                                }
                                Polyline polyline2 = new Polyline(collection2);
                                Graphic graphics2 = new Graphic(polyline2, simpleLineSymbol);
                                mGraphicsOverlay.getGraphics().add(graphics2);
                                graphicLineList.add(position + 1, graphics2);
                                //???????????????2
                                Point pointCentre2;
                                if (position + 2 == pointList.size()) {
                                    pointCentre2 = getCentrePoint(pointList.get(position + 1), pointList.get(0));
                                } else {
                                    pointCentre2 = getCentrePoint(pointList.get(position + 1), pointList.get(position + 2));
                                }
                                Graphic graphicCentre2 = new Graphic(pointCentre2, markerCentreSymbol);
                                mGraphicsOverlay.getGraphics().add(graphicCentre2);
                                graphicCentreList.add(position + 1, graphicCentre2);
                                pointCentreList.add(position + 1, pointCentre2);

                            }
                        } else {
                        }
                        drawPolygon();

                    }}
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (typeFlag==1){
                    if (position != -1) {
                        switch (type) {
                            case "top":
                                Point point = mMapView.screenToLocation(new android.graphics.Point(Math.round(event.getX()), Math.round(event.getY())));
                                if (!JSONUtil.isNull(point)) {
                                    if (pointList.size() == 1) {
                                        mGraphicsOverlay.getGraphics().remove(graphicPointList.get(position));
                                        graphicPointList.remove(position);
                                        pointList.remove(position);

                                        Graphic graphic = new Graphic(point, markerSymbol);
                                        graphic.setZIndex(999);
                                        mGraphicsOverlay.getGraphics().add(graphic);
                                        graphicPointList.add(graphic);
                                        pointList.add(point);

                                    } else if (pointList.size() == 2) {
                                        //????????????
                                        mGraphicsOverlay.getGraphics().remove(graphicPointList.get(position));
                                        graphicPointList.remove(position);
                                        pointList.remove(position);
                                        //???????????????
                                        mGraphicsOverlay.getGraphics().remove(graphicCentreList.get(0));
                                        graphicCentreList.remove(0);
                                        pointCentreList.remove(0);
                                        //?????????
                                        mGraphicsOverlay.getGraphics().remove(graphicLineList.get(0));
                                        graphicLineList.remove(graphicLineList.get(0));
                                        //????????????
//                                        Point point = mMapView.screenToLocation(new android.graphics.Point(Math.round(event.getX()), Math.round(event.getY())));
                                        Graphic graphic = new Graphic(point, markerSymbol);
                                        graphic.setZIndex(999);
                                        mGraphicsOverlay.getGraphics().add(graphic);
                                        graphicPointList.add(position, graphic);
                                        pointList.add(position, point);
                                        //?????????
                                        PointCollection collection = new PointCollection(SpatialReferences.getWgs84());
                                        collection.add(pointList.get(0));
                                        collection.add(pointList.get(1));
                                        Polyline polyline = new Polyline(collection);
                                        Graphic graphics = new Graphic(polyline, simpleLineSymbol);
                                        mGraphicsOverlay.getGraphics().add(graphics);
                                        graphicLineList.add(graphics);
                                        //???????????????
                                        Point pointCentre = getCentrePoint(pointList.get(0), pointList.get(1));
                                        Graphic graphicCentre = new Graphic(pointCentre, markerCentreSymbol);
                                        mGraphicsOverlay.getGraphics().add(graphicCentre);
                                        graphicCentreList.add(graphicCentre);
                                        pointCentreList.add(pointCentre);
                                    } else if (pointList.size() > 2) {
                                        //????????????
                                        mGraphicsOverlay.getGraphics().remove(graphicPointList.get(position));
                                        graphicPointList.remove(position);
                                        pointList.remove(position);
                                        //???????????????1
                                        mGraphicsOverlay.getGraphics().remove(graphicCentreList.get(position));
                                        graphicCentreList.remove(position);
                                        pointCentreList.remove(position);
                                        //???????????????2
                                        if (position == 0) {
                                            mGraphicsOverlay.getGraphics().remove(graphicCentreList.get(graphicCentreList.size() - 1));
                                            graphicCentreList.remove(graphicCentreList.size() - 1);
                                            pointCentreList.remove(pointCentreList.size() - 1);
                                        } else {
                                            mGraphicsOverlay.getGraphics().remove(graphicCentreList.get(position - 1));
                                            graphicCentreList.remove(position - 1);
                                            pointCentreList.remove(position - 1);
                                        }
                                        //?????????1
                                        mGraphicsOverlay.getGraphics().remove(graphicLineList.get(position));
                                        graphicLineList.remove(graphicLineList.get(position));
                                        //????????????2
                                        if (position == 0) {
                                            mGraphicsOverlay.getGraphics().remove(graphicLineList.get(graphicLineList.size() - 1));
                                            graphicLineList.remove(graphicLineList.get(graphicLineList.size() - 1));
                                        } else {
                                            mGraphicsOverlay.getGraphics().remove(graphicLineList.get(position - 1));
                                            graphicLineList.remove(graphicLineList.get(position - 1));
                                        }
                                        //????????????
//                                        Point point = mMapView.screenToLocation(new android.graphics.Point(Math.round(event.getX()), Math.round(event.getY())));
//                                        Log.i("spatialReference", point.toJson());
                                        Graphic graphic = null;
                                        try {
                                            graphic = new Graphic(point, markerSymbol);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        graphic.setZIndex(999);
                                        mGraphicsOverlay.getGraphics().add(graphic);
                                        graphicPointList.add(position, graphic);
                                        pointList.add(position, point);

                                        if (position == 0) {
                                            //?????????1
                                            PointCollection collection = new PointCollection(SpatialReferences.getWgs84());
                                            collection.add(pointList.get(position));
                                            collection.add(pointList.get(position + 1));
                                            Polyline polyline = new Polyline(collection);
                                            Graphic graphics = new Graphic(polyline, simpleLineSymbol);
                                            mGraphicsOverlay.getGraphics().add(graphics);
                                            graphicLineList.add(position, graphics);
                                            //?????????2
                                            PointCollection collection2 = new PointCollection(SpatialReferences.getWgs84());
                                            collection2.add(pointList.get(position));
                                            collection2.add(pointList.get(pointList.size() - 1));
                                            Polyline polylines = new Polyline(collection2);
                                            Graphic graphicss = new Graphic(polylines, simpleLineSymbol);
                                            mGraphicsOverlay.getGraphics().add(graphicss);
                                            graphicLineList.add(graphicss);

                                            //???????????????1
                                            Point pointCentre = getCentrePoint(pointList.get(0), pointList.get(1));
                                            Graphic graphicCentre = new Graphic(pointCentre, markerCentreSymbol);
                                            mGraphicsOverlay.getGraphics().add(graphicCentre);
                                            graphicCentreList.add(position, graphicCentre);
                                            pointCentreList.add(position, pointCentre);

                                            //???????????????2
                                            Point pointCentre2 = getCentrePoint(pointList.get(0), pointList.get(pointList.size() - 1));
                                            Graphic graphicCentre2 = new Graphic(pointCentre2, markerCentreSymbol);
                                            mGraphicsOverlay.getGraphics().add(graphicCentre2);
                                            graphicCentreList.add(graphicCentre2);
                                            pointCentreList.add(pointCentre2);
                                        } else {
                                            //?????????1
                                            PointCollection collection = new PointCollection(SpatialReferences.getWgs84());
                                            collection.add(pointList.get(position));
                                            collection.add(pointList.get(position - 1));
                                            Polyline polyline = new Polyline(collection);
                                            Graphic graphics = new Graphic(polyline, simpleLineSymbol);
                                            mGraphicsOverlay.getGraphics().add(graphics);
                                            graphicLineList.add(position - 1, graphics);
                                            //?????????2
                                            PointCollection collection2 = new PointCollection(SpatialReferences.getWgs84());
                                            collection2.add(pointList.get(position));
                                            if (position == pointList.size() - 1) {
                                                collection2.add(pointList.get(0));
                                            } else {
                                                collection2.add(pointList.get(position + 1));
                                            }
                                            Polyline polylines = new Polyline(collection2);
                                            Graphic graphicss = new Graphic(polylines, simpleLineSymbol);
                                            mGraphicsOverlay.getGraphics().add(graphicss);
                                            graphicLineList.add(position, graphicss);

                                            //???????????????1
                                            Point pointCentre = getCentrePoint(pointList.get(position), pointList.get(position - 1));
                                            Graphic graphicCentre = new Graphic(pointCentre, markerCentreSymbol);
                                            mGraphicsOverlay.getGraphics().add(graphicCentre);
                                            graphicCentreList.add(position - 1, graphicCentre);
                                            pointCentreList.add(position - 1, pointCentre);

                                            //???????????????2
                                            Point pointCentre2;
                                            if (position == pointList.size() - 1) {
                                                pointCentre2 = getCentrePoint(pointList.get(position), pointList.get(0));
                                            } else {
                                                pointCentre2 = getCentrePoint(pointList.get(position), pointList.get(position + 1));
                                            }

                                            Graphic graphicCentre2 = new Graphic(pointCentre2, markerCentreSymbol);
                                            mGraphicsOverlay.getGraphics().add(graphicCentre2);
                                            graphicCentreList.add(position, graphicCentre2);
                                            pointCentreList.add(position, pointCentre2);
                                        }
                                    }
                                    drawPolygon();
                                }
                                break;
                            case "centre":

                                break;
                            default:
                                break;
                        }
                    }}
                    break;
                case MotionEvent.ACTION_UP:
                    break;
                default:
                    break;
            }

            return super.onTouch(view, event);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return super.onDown(e);
        }

        @Override
        public boolean onUp(MotionEvent e) {
            if (typeFlag==1){
            if (longPressFlag) {
                longPressFlag = false;
                onSingleTapConfirmed(e);
            }}
            return super.onUp(e);

        }

        @Override
        public void onShowPress(MotionEvent e) {
            super.onShowPress(e);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (canScroll) {
                super.onScroll(e1, e2, distanceX, distanceY);
            }
            return true/*super.onScroll(e1, e2, distanceX, distanceY)*/;
        }

        @Override
        public void onLongPress(MotionEvent e) {

//            if (pointList.size() > 0) {
//                longPressFlag = true;
//            } else {
//                getGraphicForUpdata(new android.graphics.Point(Math.round(e.getX()), Math.round(e.getY())));
//            }

            super.onLongPress(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            canScroll = true;
            return true/*super.onFling(e1, e2, velocityX, velocityY)*/;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            return super.onScale(detector);
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return super.onScaleBegin(detector);
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            super.onScaleEnd(detector);
        }

        @Override
        public boolean onRotate(MotionEvent event, double rotationAngle) {
            return super.onRotate(event, rotationAngle);
        }

        @Override
        public boolean onMultiPointerTap(MotionEvent event) {
            return super.onMultiPointerTap(event);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (graphic != null) {
                mGraphicsOverlay.getGraphics().remove(graphic);
                if (mCallout.isShowing()) {
                    mCallout.dismiss();
                }
                lonAndlat = "";
                polygonArea = "";
            }
            if (typeFlag==0){
                addMarker(e);
            }else {
                if (canDrawTop) {
                    Point point = mMapView.screenToLocation(new android.graphics.Point(Math.round(e.getX()), Math.round(e.getY())));
                    if(pointList.size()==0){
                        Point pointGpss = (Point) GeometryEngine.project(point, SpatialReferences.getWgs84());
                        lon = forMatLonLat(pointGpss.getX());
                        lat = forMatLonLat(pointGpss.getY());
                        TextView calloutContent = new TextView(getApplicationContext());
                        calloutContent.setTextColor(Color.BLACK);
                        calloutContent.setSingleLine();
                        // format coordinates to 4 decimal places
                        calloutContent.setText("??????: " + lon + ", ??????: " + lat);
                        calloutContent.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (mCallout.isShowing()) {
                                    mCallout.dismiss();
                                }
                            }
                        });
                        mCallout.setLocation(point);
                        mCallout.setContent(calloutContent);
                        mCallout.show();
                        mMapView.setViewpointCenterAsync(point);
                        if (lon.equals("") || lat.equals("")) {
                            address = "";
                        }
                        LoadingDialog.Builder loadBuilder = new LoadingDialog.Builder(PhotoMain.this)
                                .setMessage("????????????????????????...")
                                .setCancelable(true)
                                .setCancelOutside(true);
                        dialog = loadBuilder.create();
                        dialog.show();
                        SearchService.getInstance().getMap4Server(featureTable, pointGpss, new SearchService.MapCallBack() {
                            @Override
                            public void onFailure(Exception e) {
                                Toast.makeText(PhotoMain.this, "???????????????????????????", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }

                            @Override
                            public void onResponse(Map<String, Object> dataMap) {
                                dialog.dismiss();
                                province = (String) dataMap.get("province");
                                city = (String) dataMap.get("city");
                                county = (String) dataMap.get("county");
                                country = (String) dataMap.get("town");
                                village = (String) dataMap.get("village");
                                AlertDialog.Builder dialog = new AlertDialog.Builder(PhotoMain.this);
                                dialog.setTitle("??????");
                                View view = LayoutInflater.from(PhotoMain.this).inflate(R.layout.xingzheng, null);
                                final EditText et_sheng = view.findViewById(R.id.xz_et_sheng);
                                final EditText et_shi = view.findViewById(R.id.xz_et_shi);
                                final EditText et_xian = view.findViewById(R.id.xz_et_xian);
                                final EditText et_xiang = view.findViewById(R.id.xz_et_xiang);
                                final EditText et_cun = view.findViewById(R.id.xz_et_cun);
                                et_sheng.setText(province);
                                et_shi.setText(city);
                                et_xian.setText(county);
                                et_xiang.setText(country);
                                et_cun.setText(village);
                                dialog.setView(view);
                                address = province + city + county + country + village;
                                dialog.setNeutralButton("??????", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        province = et_sheng.getText().toString();
                                        city = et_shi.getText().toString();
                                        county = et_xian.getText().toString();
                                        country = et_xiang.getText().toString();
                                        village = et_cun.getText().toString();
                                        address = province + city + county + country + village;
                                        Graphic graphic = new Graphic(point, markerSymbol);
                                        graphic.setZIndex(999);
                                        mGraphicsOverlay.getGraphics().add(graphic);
                                        graphicPointList.add(graphic);
                                        pointList.add(point);
                                        drawLine();
                                    }
                                });
                                dialog.setCancelable(false);
                                dialog.create().show();
                            }
                        });
                    }else {
                        Graphic graphic = new Graphic(point, markerSymbol);
                        graphic.setZIndex(999);
                        mGraphicsOverlay.getGraphics().add(graphic);
                        graphicPointList.add(graphic);
                        pointList.add(point);
                        drawLine();
                    }

                }
            }

            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return super.onDoubleTapEvent(e);
        }

        @Override
        public boolean onDoubleTouchDrag(MotionEvent event) {
            return super.onDoubleTouchDrag(event);
        }

//        @Override
//        public void setPinchToZoomGestureDetector(PinchToZoomGestureDetector pinchToZoomGestureDetector) {
//            super.setPinchToZoomGestureDetector(pinchToZoomGestureDetector);
//        }
//
//        @Override
//        public PinchToZoomGestureDetector getPinchToZoomGestureDetector() {
//            return super.getPinchToZoomGestureDetector();
//        }
    }
    /**
     * ??????????????????
     *
     * @return
     */
    private Map<String, Object> getSelectedIndex(double x, double y) {
        Map<String, Object> map = new HashMap<>();
        final int TOLERANCE = 30; // Tolerance in pixels
        if (pointList == null || pointList.size() == 0) {
            map.put("index", -1);
            return map;
        }
        int index = -1;
        double distSQ_Small = Double.MAX_VALUE;
        for (int i = 0; i < pointList.size(); i++) {
            SpatialReference spatialReference = pointList.get(i).getSpatialReference();
            android.graphics.Point p = mMapView.locationToScreen(pointList.get(i));
            double diffx = p.x - x;
            double diffy = p.y - y;
            double distSQ = diffx * diffx + diffy * diffy;
            if (distSQ < distSQ_Small) {
                index = i;
                distSQ_Small = distSQ;
            }
        }
        if (distSQ_Small < (TOLERANCE * TOLERANCE)) {
            map.put("index", index);
            map.put("type", "top");
            return map;
        }


        int indexs = -1;
        double distSQ_Smalls = Double.MAX_VALUE;
        for (int i = 0; i < pointCentreList.size(); i++) {
            SpatialReference spatialReference = pointCentreList.get(i).getSpatialReference();
            android.graphics.Point p = mMapView.locationToScreen(pointCentreList.get(i));
            double diffx = p.x - x;
            double diffy = p.y - y;
            double distSQ = diffx * diffx + diffy * diffy;
            if (distSQ < distSQ_Smalls) {
                indexs = i;
                distSQ_Smalls = distSQ;
            }
        }
        if (distSQ_Smalls < (TOLERANCE * TOLERANCE)) {
            map.put("index", indexs);
            map.put("type", "centre");
            return map;
        }
        map.put("index", -1);
        return map;
    }
    /**
     * ??????p1???p2????????????
     *
     * @param p1 ??????
     * @param p2 ??????
     * @return
     */
    public Point getCentrePoint(Point p1, Point p2) {
        Point point = new Point((p1.getX() + p2.getX()) / 2, (p1.getY() + p2.getY()) / 2, p1.getSpatialReference());
        return point;
    }

    //????????????
    private String calculateArea(double area) {
        DecimalFormat df = new DecimalFormat("#######.00");
        double area2 = Math.abs(area / 666.667d);
        return df.format(area2);
    }
    public void drawPolygon() {
        if (polygon != null) {
            mGraphicsOverlay.getGraphics().remove(polygonGraphic);
            poliCollection.clear();
            polygonArea = "";
        }
        if (pointList.size() >= 3) {
            for (int i = 0; i < pointList.size(); i++) {
                poliCollection.add(pointList.get(i));
            }
            polygon = new Polygon(poliCollection);
            SimpleFillSymbol simpleFillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.parseColor("#000000"), null);
            polygonGraphic = new Graphic(polygon, simpleFillSymbol);
            polygonGraphic.setZIndex(-1);
            mGraphicsOverlay.getGraphics().add(polygonGraphic);

            Point point = polygonGraphic.getGeometry().getExtent().getCenter();
            if (areaGraphic != null) {
                mGraphicsOverlay.getGraphics().remove(areaGraphic);
                areaGraphic = null;
            }
            //polygonArea = calculateArea(GeometryEngine.area(polygon));
            polygonArea = calculateArea(GeometryEngine.areaGeodetic(polygon,new AreaUnit(AreaUnitId.SQUARE_METERS), GeodeticCurveType.GEODESIC));
            String str = polygonArea.substring(0,1);
            if (str.equals(".")) {
                polygonArea = "0" + polygonArea;
            }
            polygonAreaKeep = polygonArea;
            textSymbol = new TextSymbol(15, polygonArea + "???", Color.parseColor("#ffffff"), TextSymbol.HorizontalAlignment.CENTER, TextSymbol.VerticalAlignment.MIDDLE);
            areaGraphic = new Graphic(point, textSymbol);
            areaGraphic.setZIndex(999);
            mGraphicsOverlay.getGraphics().add(areaGraphic);

        } else {
            if (polygon != null) {
                mGraphicsOverlay.getGraphics().remove(polygonGraphic);
                poliCollection.clear();
                polygon = null;
            }
            if (areaGraphic != null) {
                mGraphicsOverlay.getGraphics().remove(areaGraphic);
                areaGraphic = null;
            }
            polygonArea = "";
            polygonGraphic = null;
        }
    }
    /*private void getGraphicForUpdata(android.graphics.Point screenPoint) {
        // ??????GraphicsOverlay??????Graphic
        final ListenableFuture<IdentifyGraphicsOverlayResult> identifyGraphic = mMapView.identifyGraphicsOverlayAsync(mGraphicsOverlay, screenPoint, 10.0, false, 10);
        identifyGraphic.addDoneListener(new Runnable() {
            @Override
            public void run() {
                try {
                    IdentifyGraphicsOverlayResult grOverlayResult = identifyGraphic.get();
                    // ???????????????GraphicOverlay?????????????????????
                    List<Graphic> graphicList = grOverlayResult.getGraphics();
                    // ???????????????????????????
                    int identifyResultSize = graphicList.size();
                    if (!graphicList.isEmpty() && identifyResultSize > 0) {
                        longPressFlag = false;
                        for (int i = 0; i < identifyResultSize; i++) {
                            Graphic mGraphic = graphicList.get(i);
                            String taskIds = mGraphic.getAttributes().get("taskId").toString();
                            if (mGraphic.getAttributes() != null && taskIds != null && taskIds.length() > 0 && pointList.size() == 0) {
                                longPressFlag = false;
                                updataFlag = true;
                                CustomizeDKBean dkBeanU = dkService.getDKBean(taskIds);
                                if (customerIdsForUpdata.equals(dkBeanU.getCustomerIds()) && updataFlagFrom.equals("1")) {
                                    AlertDialog.Builder dialog = new AlertDialog.Builder(DiKuaiActivity.this);
                                    dialog.setTitle("??????").setMessage("?????????????????????").setNegativeButton("??????", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
//                                        mGraphicsOverlay.getGraphics().remove(mGraphic);
                                            for (int j = 0; j < graphicAreaList.size(); j++) {
                                                String areaTaskId = graphicAreaList.get(j).getAttributes().get("taskId").toString();
                                                if (areaTaskId.equals(taskIds)) {
                                                    mGraphicsOverlay.getGraphics().remove(graphicAreaList.get(j));
                                                    graphicAreaList.remove(j);
                                                    break;
                                                }
                                            }
                                            for (int j = 0; j < graphicDbList.size(); j++) {
                                                String areaTaskId = graphicDbList.get(j).getAttributes().get("taskId").toString();
                                                if (areaTaskId.equals(taskIds)) {
                                                    mGraphicsOverlay.getGraphics().remove(graphicDbList.get(j));
                                                    graphicDbList.remove(j);
                                                    break;
                                                }
                                            }
                                            dkService.deleteDKInfo(taskIds);
                                            mList = dkService.getDKList("-1");
                                            if (updataFlagFrom.equals("1")){
                                                ContentValues contentValues = new ContentValues();
                                                contentValues.put("proposalNo",proposalNo);
                                                contentValues.put("certificateId",certificateId);
                                                contentValues.put("customerIds",customerIds);
                                                dkService.updataDKInfoByCustomerIds(contentValues,customerIdsForUpdata);
                                                mList = dkService.getDKList("-1");
                                                customerIdsForUpdata = customerIds;
                                            }
                                            allArea = 0;
                                            for (int i = 0; i < holdsBeanList.size(); i++) {
                                                if (holdsBeanList.get(i).isChoose()) {
                                                    allArea += Double.parseDouble(holdsBeanList.get(i).getInsureArea());
                                                }
                                            }
                                            allArea = formatDouble2(allArea);
                                            hasArea = dkService.getSumPlotByCustomerIds(province, city, town, country, village, proposalNo, certificateId, customerIds);
                                            hasArea = formatDouble2(hasArea);
                                            lackArea = (allArea - hasArea)/allArea;
                                            tv_needArea.setText("????????????"+allArea+"???");
                                            tv_hasArea.setText("????????????"+hasArea+"???");
                                            tv_lackArea.setText("???    ??????"+formatDouble2((allArea - hasArea))+"???");
                                            tv_error.setText("????????????"+formatDouble2((lackArea*100))+"%");
                                            if (lackArea>0.02||lackArea<-0.02){
                                                tv_error.setTextColor(Color.parseColor("#FF0000"));
                                            }else {
                                                tv_error.setTextColor(Color.parseColor("#00FF00"));
                                            }
                                            if (dkService.getDKByUser(certificateId,proposalNo).size()==0){
                                                deleteHaveDraw();
                                            }
                                        }
                                    }).setNeutralButton("????????????", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //mGraphicsOverlay.getGraphics().remove(mGraphic);
                                            *//*if (updataFlagFrom.equals("1")){
                                                ContentValues contentValues = new ContentValues();
                                                contentValues.put("proposalNo",proposalNo);
                                                contentValues.put("certificateId",certificateId);
                                                contentValues.put("customerIds",customerIds);
                                                dkService.updataDKInfoByCustomerIds(contentValues,customerIdsForUpdata);
                                                mList = dkService.getDKList("-1");
                                                customerIdsForUpdata = customerIds;
                                            }*//*
                                            List<CustomizeDKPointBean> dkPointBeanList = dkService.getDKPointList(taskIds);
                                            for (int i = 0; i < dkPointBeanList.size(); i++) {
                                                CustomizeDKPointBean bean = dkPointBeanList.get(i);
                                                Point wgsPoint = new Point(Double.parseDouble(bean.getLon()), Double.parseDouble(bean.getLat()), SpatialReferences.getWgs84());
                                                Point mapPoint = (Point) GeometryEngine.project(wgsPoint, SpatialReferences.getWebMercator());
                                                Graphic graphic = new Graphic(mapPoint, markerSymbol);
                                                graphic.setZIndex(999);
                                                mGraphicsOverlay.getGraphics().add(graphic);
                                                graphicPointList.add(graphic);
                                                pointList.add(mapPoint);
                                            }
                                            for (int i = 0; i < pointList.size(); i++) {
                                                //??????
                                                PointCollection collection = new PointCollection(SpatialReferences.getWebMercator());
                                                collection.add(pointList.get(i));
                                                if (i == pointList.size() - 1) {
                                                    collection.add(pointList.get(0));
                                                } else {
                                                    collection.add(pointList.get(i + 1));
                                                }
                                                Polyline polyline = new Polyline(collection);

                                                Graphic graphic = new Graphic(polyline, simpleLineSymbol);
                                                mGraphicsOverlay.getGraphics().add(graphic);
                                                graphicLineList.add(graphic);
                                                //????????????
                                                Point pointCentre = null;
                                                if (i == pointList.size() - 1) {
                                                    pointCentre = getCentrePoint(pointList.get(i), pointList.get(0));
                                                } else {
                                                    pointCentre = getCentrePoint(pointList.get(i), pointList.get(i + 1));
                                                }
                                                Graphic graphicCentre = new Graphic(pointCentre, markerCentreSymbol);
                                                mGraphicsOverlay.getGraphics().add(graphicCentre);
                                                graphicCentreList.add(graphicCentre);
                                                pointCentreList.add(pointCentre);
                                            }
                                            CustomizeDKBean bean = dkService.getDKBean(taskIds);
                                            taskId = taskIds;
                                            proposalNo = bean.getProposalNo();
                                            certificateId = bean.getCertificateId();
//                                            customerIds = bean.getCustomerIds();
                                            userName = bean.getUserName();
                                            cropName = bean.getCrop();
//                                            dkName = bean.getDkName();
                                            allArea = 0;
                                            for (int i = 0; i < holdsBeanList.size(); i++) {
                                                if (holdsBeanList.get(i).isChoose()){
                                                    allArea += Double.parseDouble(holdsBeanList.get(i).getInsureArea());
                                                }
                                            }
                                            allArea = formatDouble2(allArea);
                                            hasArea = dkService.getSumPlotByCustomerIds(province, city, town, country, village, proposalNo, certificateId, customerIdsForUpdata);
                                            hasArea = formatDouble2(hasArea-Double.parseDouble(bean.getChangeArea()));
                                            lackArea = (allArea - hasArea)/allArea;
                                            tv_needArea.setText("????????????"+allArea+"???");
                                            tv_hasArea.setText("????????????"+hasArea+"???");
                                            tv_lackArea.setText("???    ??????"+formatDouble2((allArea - hasArea))+"???");
                                            tv_error.setText("????????????"+formatDouble2((lackArea*100))+"%");
                                            if (lackArea>0.02||lackArea<-0.02){
                                                tv_error.setTextColor(Color.parseColor("#FF0000"));
                                            }else {
                                                tv_error.setTextColor(Color.parseColor("#00FF00"));
                                            }
                                            drawPolygon();
                                        }
                                    }).setPositiveButton("??????", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                                    dialog.setCancelable(true);
                                    dialog.create().show();
                                }

                                break;
                            }
                        }
                        if (!updataFlag) {
                            longPressFlag = true;
                        }
                    } else {
                        longPressFlag = true;
                    }
                } catch (InterruptedException | ExecutionException ie) {
                    ie.printStackTrace();
                }

            }
        });
    }*/
    public void drawLine() {
        if (pointList.size() == 2) {
            //??????
            PointCollection collection = new PointCollection(SpatialReferences.getWgs84());
            collection.add(pointList.get(0));
            collection.add(pointList.get(1));
            Polyline polyline = new Polyline(collection);

            Graphic graphic = new Graphic(polyline, simpleLineSymbol);
            mGraphicsOverlay.getGraphics().add(graphic);
            graphicLineList.add(graphic);
            //????????????
            Point pointCentre = getCentrePoint(pointList.get(0), pointList.get(1));
            Graphic graphicCentre = new Graphic(pointCentre, markerCentreSymbol);
            mGraphicsOverlay.getGraphics().add(graphicCentre);
            graphicCentreList.add(graphicCentre);
            pointCentreList.add(pointCentre);


        } else if (pointList.size() == 3) {
            PointCollection collection = new PointCollection(SpatialReferences.getWgs84());
            collection.add(pointList.get(pointList.size() - 2));
            collection.add(pointList.get(pointList.size() - 1));
            Polyline polyline = new Polyline(collection);
            Graphic graphic = new Graphic(polyline, simpleLineSymbol);
            mGraphicsOverlay.getGraphics().add(graphic);
            graphicLineList.add(graphic);
            collection.remove(0);
            collection.add(pointList.get(0));
            Polyline polylines = new Polyline(collection);
            Graphic graphicLast = new Graphic(polylines, simpleLineSymbol);
            mGraphicsOverlay.getGraphics().add(graphicLast);
            graphicLineList.add(graphicLast);
            //??????????????????????????????
            Point pointCentre1 = getCentrePoint(pointList.get(1), pointList.get(2));
            Graphic graphicCentre = new Graphic(pointCentre1, markerCentreSymbol);
            mGraphicsOverlay.getGraphics().add(graphicCentre);
            graphicCentreList.add(graphicCentre);
            pointCentreList.add(pointCentre1);
            //????????????????????????????????????
            Point pointCentre2 = getCentrePoint(pointList.get(0), pointList.get(2));
            Graphic graphicCentre2 = new Graphic(pointCentre2, markerCentreSymbol);
            mGraphicsOverlay.getGraphics().add(graphicCentre2);
            graphicCentreList.add(graphicCentre2);
            pointCentreList.add(pointCentre2);


        } else if (pointList.size() > 3) {
            PointCollection collection = new PointCollection(SpatialReferences.getWgs84());
            collection.add(pointList.get(pointList.size() - 2));
            collection.add(pointList.get(pointList.size() - 1));
            Polyline polyline = new Polyline(collection);
            Graphic graphic = new Graphic(polyline, simpleLineSymbol);
            mGraphicsOverlay.getGraphics().remove(graphicLineList.get(graphicLineList.size() - 1));
            graphicLineList.remove(graphicLineList.size() - 1);
            mGraphicsOverlay.getGraphics().add(graphic);
            graphicLineList.add(graphic);

            collection.remove(0);
            collection.add(pointList.get(0));
            Polyline polylines = new Polyline(collection);
            Graphic graphicLast = new Graphic(polylines, simpleLineSymbol);
            mGraphicsOverlay.getGraphics().add(graphicLast);
            graphicLineList.add(graphicLast);
            //?????? ?????????????????????????????????
            mGraphicsOverlay.getGraphics().remove(graphicCentreList.get(graphicCentreList.size() - 1));
            graphicCentreList.remove(graphicCentreList.size() - 1);
            pointCentreList.remove(pointCentreList.size() - 1);
            //??? ???????????????????????????
            Point pointCentre1 = getCentrePoint(pointList.get(pointList.size() - 2), pointList.get(pointList.size() - 1));
            Graphic graphicCentre = new Graphic(pointCentre1, markerCentreSymbol);
            mGraphicsOverlay.getGraphics().add(graphicCentre);
            graphicCentreList.add(graphicCentre);
            pointCentreList.add(pointCentre1);
            //????????????????????????????????????
            Point pointCentre2 = getCentrePoint(pointList.get(0), pointList.get(pointList.size() - 1));
            Graphic graphicCentre2 = new Graphic(pointCentre2, markerCentreSymbol);
            mGraphicsOverlay.getGraphics().add(graphicCentre2);
            graphicCentreList.add(graphicCentre2);
            pointCentreList.add(pointCentre2);
        }
        drawPolygon();
    }
}
