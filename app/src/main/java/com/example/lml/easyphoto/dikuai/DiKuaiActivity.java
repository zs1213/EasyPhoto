package com.example.lml.easyphoto.dikuai;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
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
import com.example.lml.easyphoto.FarmerInfo.FarmerInfoBean;
import com.example.lml.easyphoto.FarmerInfo.houseHolds.HoldsService;
import com.example.lml.easyphoto.FarmerInfo.houseHolds.HouseHoldsAdapter;
import com.example.lml.easyphoto.FarmerInfo.houseHolds.HouseHoldsBean;
import com.example.lml.easyphoto.R;
import com.example.lml.easyphoto.camera.TimeCycleUtils;
import com.example.lml.easyphoto.dikuai.finish.FinishActivity;
import com.example.lml.easyphoto.main.MenuActivity;
import com.example.lml.easyphoto.okHttpUtils.DOkHttp;
import com.example.lml.easyphoto.sign.DrawActivity;
import com.example.lml.easyphoto.skp.TimeCycleUtil;
import com.example.lml.easyphoto.util.Configure;
import com.example.lml.easyphoto.util.Consts;
import com.example.lml.easyphoto.util.LoadingDialog;
import com.example.lml.easyphoto.util.SharePreferencesTools;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import cn.hutool.json.JSONUtil;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

public class DiKuaiActivity extends AppCompatActivity implements View.OnClickListener {
    private MapView mMapView;
    private Callout mCallout;
    private GraphicsOverlay mGraphicsOverlay;
    private TextSymbol textSymbol;
    private SimpleMarkerSymbol markerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.parseColor("#FF0000"), 10);
    private SimpleMarkerSymbol markerCentreSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.parseColor("#00FF00"), 10);
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
    private Button btn_holds, btn_finish;
    private LinearLayout btn_location, btn_delete, btn_back, btn_keep;
    private DKService dkService;
    private MassifSnapService massifSnapService;
    private List<DKBean> mList;
    private List<HouseHoldsBean> holdsBeanList;
    private String proposalNo;//投保单号
    private String certificateId;//身份证号
    private String customerIds = "";//地块关联的分户清单号
    private String customerIdsForUpdata = "";//修改地块关联的分户清单号
    private String updataFlagFrom;//判断是否是点击修改进到改界面0：标绘 1：修改
    private String userName;//地块用户名称
    private String dkName = "";//地块名称
    private String cropName;//种植作物
    private String taskId;//dkID
    private String provinceCode = "";//省代码
    private String cityCode = "";//市代码
    private String zipCode = "";//县代码
    private String sideCode = "";//乡代码
    private String villCode = "";//村代码
    private String province = "";//省
    private String city = "";//市
    private String town = "";//县
    private String country = "";//乡
    private String village = "";//村
    private int flag = 0;
    private PointCollection poliCollection = new PointCollection(SpatialReferences.getWebMercator());
    private Polygon polygon;
    private String polygonArea = "";
    private Graphic polygonGraphic;
    private Graphic areaGraphic;
    //gps定位
    private LocationDisplay locationDisplay;
    //指南针
    private SensorManager sensorManager;
    private SensorEventListener sensorEventListener;
    private TextView miui;
    private TextView tv_title;
    private TextView tv_hasArea;
    private TextView tv_needArea;
    private TextView tv_lackArea;
    private TextView tv_error;
    private float val;
    private Gson gson;
    private LinearLayout lin_holds;
    private ListView lv_holds;
    private Button btn_cure;
    private HouseHoldsAdapter houseHoldsAdapter;
    private String serverPath;
    private double allArea = 0;
    private double hasArea = 0;
    private double lackArea = 0;
    private ServiceFeatureTable mServiceFeatureTable;
    private ArcGISMapImageLayer mainArcGISVectorTiledLayer;
    private String theOnlineTiledLayers = Configure.gisXZQYUrl;
    private boolean extentFlag = true;
    private LoadingDialog dialog;//提示框
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_di_kuai);
        initView();
    }

    private void initView() {
        mMapView = findViewById(R.id.mapView);
        miui = findViewById(R.id.dk_mi_znz);
        tv_title = findViewById(R.id.dk_tv_title);
        tv_needArea = findViewById(R.id.dk_tv_needArea);
        tv_hasArea = findViewById(R.id.dk_tv_hasArea);
        tv_lackArea = findViewById(R.id.dk_tv_lackArea);
        tv_error = findViewById(R.id.dk_tv_error);
        dialog = new LoadingDialog(this);
        gson = new Gson();
        dkService = new DKService(this);
        massifSnapService = new MassifSnapService(this);
        province = getIntent().getStringExtra("provinceName");
        provinceCode = getIntent().getStringExtra("provinceCode");
        cityCode = getIntent().getStringExtra("cityCode");
        city = getIntent().getStringExtra("cityName");
        zipCode = getIntent().getStringExtra("countyCode");
        town = getIntent().getStringExtra("countyName");
        sideCode = getIntent().getStringExtra("countryCode");
        country = getIntent().getStringExtra("countryName");
        villCode = getIntent().getStringExtra("villageCode");
        village = getIntent().getStringExtra("villageName");
        serverPath = getIntent().getStringExtra("serverPath");
        btn_location = findViewById(R.id.dk_btn_location);
        btn_back = findViewById(R.id.dk_btn_back);
        btn_delete = findViewById(R.id.dk_btn_delete);
        btn_holds = findViewById(R.id.dk_btn_hoolds);
        btn_keep = findViewById(R.id.dk_btn_keep);
        btn_finish = findViewById(R.id.dk_btn_finish);
        lin_holds = findViewById(R.id.dk_lin_holds);
        lv_holds = findViewById(R.id.dk_lv_holds);
        btn_cure = findViewById(R.id.dk_btn_cure);
        btn_location.setOnClickListener(this);
        btn_back.setOnClickListener(this);
        btn_delete.setOnClickListener(this);
        btn_holds.setOnClickListener(this);
        btn_keep.setOnClickListener(this);
        btn_finish.setOnClickListener(this);
        btn_cure.setOnClickListener(this);
        //service = new XzqyService(this);
        mMapView.setAttributionTextVisible(false);
        mCallout = mMapView.getCallout();
//        ArcGISRuntimeEnvironment.setLicense("runtimelite,1000,rud4449636536,none,NKMFA0PL4S0DRJE15166");
        WebTiledLayer webTiledLayer = new WebTiledLayer(serverPath + "/{level}/{col}/{row}");
        ArcGISMap map = new ArcGISMap(new Basemap(webTiledLayer));
        mMapView.setMap(map);
        mGraphicsOverlay = new GraphicsOverlay();
        mainArcGISVectorTiledLayer = new ArcGISMapImageLayer(theOnlineTiledLayers);
        mServiceFeatureTable = new ServiceFeatureTable(Configure.gisXZQYCX);
        mMapView.setMagnifierEnabled(true);
        mMapView.setCanMagnifierPanMap(true);
        mMapView.setMagnifierEnabled(true);
        mMapView.setCanMagnifierPanMap(true);
        map.getOperationalLayers().add(mainArcGISVectorTiledLayer);
        mMapView.getGraphicsOverlays().add(mGraphicsOverlay);
        mMapView.setOnTouchListener(new MyDefaultMapViewOnTouchListener(this, mMapView));
        searchForState(villCode);
        pointList = new ArrayList<>();
        graphicCentreList = new ArrayList<>();
        graphicPointList = new ArrayList<>();
        pointCentreList = new ArrayList<>();
        graphicLineList = new ArrayList<>();
        graphicAreaList = new ArrayList<>();
        graphicDbList = new ArrayList<>();
        locationDisplay = mMapView.getLocationDisplay();
        locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.OFF);
        locationDisplay.startAsync();
        holdsBeanList = gson.fromJson(getIntent().getStringExtra("json"), new TypeToken<List<HouseHoldsBean>>() {
        }.getType());
        updataFlagFrom = getIntent().getStringExtra("updataFlag");
        proposalNo = holdsBeanList.get(0).getProposalNo();
        certificateId = holdsBeanList.get(0).getCertificateId();
        userName = holdsBeanList.get(0).getInsuredName();
        cropName = holdsBeanList.get(0).getCropName();
        taskId = UUID.randomUUID().toString();
        List<HouseHoldsBean> selsecBeanList = new ArrayList<>();
        for (int i = 0; i < holdsBeanList.size(); i++) {
            if (holdsBeanList.get(i).isChoose()) {
                selsecBeanList.add(holdsBeanList.get(i));
                allArea += Double.parseDouble(holdsBeanList.get(i).getInsureArea());
            }
        }
        String selsecCustomerIds[] = new String[selsecBeanList.size()];
        for (int i = 0; i < selsecBeanList.size(); i++) {
            selsecCustomerIds[i] = selsecBeanList.get(i).getId();
            dkName = selsecBeanList.get(i).getMassifName();
        }
        Arrays.sort(selsecCustomerIds);
        for (int i = 0; i < selsecCustomerIds.length; i++) {
            if (customerIds.equals("")) {
                customerIds += selsecCustomerIds[i];
            } else {
                customerIds += "," + selsecCustomerIds[i];
            }
        }
        if (updataFlagFrom.equals("1")){
            customerIdsForUpdata = customerIds;
        }
        allArea = formatDouble2(allArea);
        hasArea = dkService.getSumPlotByCustomerIds(province, city, town, country, village, proposalNo, certificateId, customerIds);
        hasArea = formatDouble2(hasArea);
        lackArea = (allArea - hasArea)/allArea;
        tv_title.setText(holdsBeanList.get(0).getInsuredName() +"\n"+ certificateId);
        tv_needArea.setText(allArea+"");
        tv_hasArea.setText(hasArea+"");
        tv_lackArea.setText(formatDouble2((allArea - hasArea))+"");
        tv_error.setText(formatDouble2((lackArea*100))+"");
        if (lackArea>0.02||lackArea<-0.02){
            tv_error.setTextColor(Color.parseColor("#FF0000"));
        }else {
            tv_error.setTextColor(Color.parseColor("#00FF00"));
        }

        houseHoldsAdapter = new HouseHoldsAdapter(holdsBeanList, this);
        lv_holds.setAdapter(houseHoldsAdapter);
        mList = dkService.getDKList("-1");
        for (int i = 0; i < mList.size(); i++) {
            DKBean dkBean = mList.get(i);
            List<DKPointBean> dkPointList = dkBean.getmList();
            drawDbPolygon(dkBean.getCustomerIds(), dkBean.getTaskId(), dkPointList, dkBean.getUserName() + "_" + dkBean.getDkName() + "_" + dkBean.getChangeArea() + "亩");
        }
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                val = event.values[0];
                String text = "";
                String oldText = "";
                if (val <= 15 || val >= 345) {
                    text = "北";
                } else if (val > 15 && val <= 75) {
                    text = "东北";
                } else if (val > 75 && val <= 105) {
                    text = "东";
                } else if (val > 105 && val <= 165) {
                    text = "东南";
                } else if (val > 165 && val <= 195) {
                    text = "南";
                } else if (val > 195 && val <= 255) {
                    text = "西南";
                } else if (val > 255 && val <= 285) {
                    text = "西";
                } else if (val > 285 && val < 345) {
                    text = "西北";
                }
                if (!oldText.equals(text)) {
                    miui.setText(text);
                }
                oldText = text;
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };

        sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
        lv_holds.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (holdsBeanList.get(position).isChoose()) {
                    int chooseFlag = 0;
                    for (int i = 0; i < holdsBeanList.size(); i++) {
                        if (holdsBeanList.get(i).isChoose()){
                            chooseFlag++;
                        }
                    }

                    if (chooseFlag==1){
                        Toast.makeText(DiKuaiActivity.this, "至少有一个要被选中", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    holdsBeanList.get(position).setChoose(false);
                } else {
                    holdsBeanList.get(position).setChoose(true);
                }
                houseHoldsAdapter.notifyDataSetChanged();

                List<HouseHoldsBean> selsecBeanList = new ArrayList<>();
                allArea = 0;
                for (int i = 0; i < holdsBeanList.size(); i++) {
                    if (holdsBeanList.get(i).isChoose()) {
                        selsecBeanList.add(holdsBeanList.get(i));
                        allArea += Double.parseDouble(holdsBeanList.get(i).getInsureArea());
                    }
                }
                String selsecCustomerIds[] = new String[selsecBeanList.size()];
                for (int i = 0; i < selsecBeanList.size(); i++) {
                    selsecCustomerIds[i] = selsecBeanList.get(i).getId();
                    dkName = selsecBeanList.get(i).getMassifName();
                }
                Arrays.sort(selsecCustomerIds);
                customerIds = "";
                for (int i = 0; i < selsecCustomerIds.length; i++) {
                    if (customerIds.equals("")) {
                        customerIds += selsecCustomerIds[i];
                    } else {
                        customerIds += "," + selsecCustomerIds[i];
                    }
                }

                allArea = formatDouble2(allArea);
                if (updataFlagFrom.equals("1")){
                    hasArea = dkService.getSumPlotByCustomerIds(province, city, town, country, village, proposalNo, certificateId, customerIdsForUpdata);
                }else {
                    hasArea = dkService.getSumPlotByCustomerIds(province, city, town, country, village, proposalNo, certificateId, customerIds);
                }
                hasArea = formatDouble2(hasArea);
                lackArea = (allArea - hasArea)/allArea;
                tv_needArea.setText(allArea+"");
                tv_hasArea.setText(hasArea+"");
                tv_lackArea.setText(formatDouble2((allArea - hasArea))+"");
                tv_error.setText(formatDouble2((lackArea*100))+"");
                if (lackArea>0.02||lackArea<-0.02){
                    tv_error.setTextColor(Color.parseColor("#FF0000"));
                }else {
                    tv_error.setTextColor(Color.parseColor("#00FF00"));
                }

            }
        });

    }

    public void goHome(View view) {
        Intent intent = new Intent(DiKuaiActivity.this, MenuActivity.class);
//        intent.putExtra("province",provinceCode);
//        intent.putExtra("provinceName",province);
//        intent.putExtra("city",cityCode);
//        intent.putExtra("cityName",city);
//        intent.putExtra("county",zipCode);
//        intent.putExtra("countyName",town);
//        intent.putExtra("country",sideCode);
//        intent.putExtra("countryName",country);
//        intent.putExtra("village",villCode);
//        intent.putExtra("villageName",village);
        startActivity(intent);
    }


    public class MyDefaultMapViewOnTouchListener extends DefaultMapViewOnTouchListener {

        public MyDefaultMapViewOnTouchListener(Context context, MapView mapView) {
            super(context, mapView);
        }

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
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
                                //删除选中中心点
                                mGraphicsOverlay.getGraphics().remove(graphicCentreList.get(position));
                                graphicCentreList.remove(position);
                                pointCentreList.remove(position);
                                //删除线
                                mGraphicsOverlay.getGraphics().remove(graphicLineList.get(position));
                                graphicLineList.remove(graphicLineList.get(position));
                                //画新顶点
                                Point point = mMapView.screenToLocation(new android.graphics.Point(Math.round(event.getX()), Math.round(event.getY())));
                                Graphic graphic = new Graphic(point, markerSymbol);
                                graphic.setZIndex(999);
                                mGraphicsOverlay.getGraphics().add(graphic);
                                graphicPointList.add(position + 1, graphic);
                                pointList.add(position + 1, point);
                                //画线1
                                PointCollection collection1 = new PointCollection(SpatialReferences.getWebMercator());
                                collection1.add(pointList.get(position));
                                collection1.add(pointList.get(position + 1));
                                Polyline polyline1 = new Polyline(collection1);
                                Graphic graphics1 = new Graphic(polyline1, simpleLineSymbol);
                                mGraphicsOverlay.getGraphics().add(graphics1);
                                graphicLineList.add(position, graphics1);
                                //画新中心点1
                                Point pointCentre1 = getCentrePoint(pointList.get(position), pointList.get(position + 1));
                                Graphic graphicCentre1 = new Graphic(pointCentre1, markerCentreSymbol);
                                mGraphicsOverlay.getGraphics().add(graphicCentre1);
                                graphicCentreList.add(position, graphicCentre1);
                                pointCentreList.add(position, pointCentre1);
                                //画线2
                                PointCollection collection2 = new PointCollection(SpatialReferences.getWebMercator());
                                collection2.add(pointList.get(position + 1));
                                collection2.add(pointList.get(position + 2));
                                Polyline polyline2 = new Polyline(collection2);
                                Graphic graphics2 = new Graphic(polyline2, simpleLineSymbol);
                                mGraphicsOverlay.getGraphics().add(graphics2);
                                graphicLineList.add(position + 1, graphics2);
                                //画新中心点2
                                Point pointCentre2 = getCentrePoint(pointList.get(position + 1), pointList.get(position + 2));
                                Graphic graphicCentre2 = new Graphic(pointCentre2, markerCentreSymbol);
                                mGraphicsOverlay.getGraphics().add(graphicCentre2);
                                graphicCentreList.add(position + 1, graphicCentre2);
                                pointCentreList.add(position + 1, pointCentre2);

                                //画线3
                                PointCollection collection3 = new PointCollection(SpatialReferences.getWebMercator());
                                collection3.add(pointList.get(position + 2));
                                collection3.add(pointList.get(position));
                                Polyline polyline3 = new Polyline(collection3);
                                Graphic graphics3 = new Graphic(polyline3, simpleLineSymbol);
                                mGraphicsOverlay.getGraphics().add(graphics3);
                                graphicLineList.add(position + 2, graphics3);
                                //画新中心点3
                                Point pointCentre3 = getCentrePoint(pointList.get(position + 2), pointList.get(position));
                                Graphic graphicCentre3 = new Graphic(pointCentre3, markerCentreSymbol);
                                mGraphicsOverlay.getGraphics().add(graphicCentre3);
                                graphicCentreList.add(position + 2, graphicCentre3);
                                pointCentreList.add(position + 2, pointCentre3);
                            } else if (pointList.size() > 2) {
                                //删除选中中心点
                                mGraphicsOverlay.getGraphics().remove(graphicCentreList.get(position));
                                graphicCentreList.remove(position);
                                pointCentreList.remove(position);
                                //删除线
                                mGraphicsOverlay.getGraphics().remove(graphicLineList.get(position));
                                graphicLineList.remove(graphicLineList.get(position));
                                //画新顶点
                                Point point = mMapView.screenToLocation(new android.graphics.Point(Math.round(event.getX()), Math.round(event.getY())));
                                Graphic graphic = new Graphic(point, markerSymbol);
                                graphic.setZIndex(999);
                                mGraphicsOverlay.getGraphics().add(graphic);
                                graphicPointList.add(position + 1, graphic);
                                pointList.add(position + 1, point);

                                //画线1
                                PointCollection collection1 = new PointCollection(SpatialReferences.getWebMercator());
                                collection1.add(pointList.get(position));
                                collection1.add(pointList.get(position + 1));
                                Polyline polyline1 = new Polyline(collection1);
                                Graphic graphics1 = new Graphic(polyline1, simpleLineSymbol);
                                mGraphicsOverlay.getGraphics().add(graphics1);
                                graphicLineList.add(position, graphics1);
                                //画新中心点1
                                Point pointCentre1 = getCentrePoint(pointList.get(position), pointList.get(position + 1));
                                Graphic graphicCentre1 = new Graphic(pointCentre1, markerCentreSymbol);
                                mGraphicsOverlay.getGraphics().add(graphicCentre1);
                                graphicCentreList.add(position, graphicCentre1);
                                pointCentreList.add(position, pointCentre1);
                                //画线2
                                PointCollection collection2 = new PointCollection(SpatialReferences.getWebMercator());
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
                                //画新中心点2
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

                    }
                    break;
                case MotionEvent.ACTION_MOVE:
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
                                        //删除顶点
                                        mGraphicsOverlay.getGraphics().remove(graphicPointList.get(position));
                                        graphicPointList.remove(position);
                                        pointList.remove(position);
                                        //删除中心点
                                        mGraphicsOverlay.getGraphics().remove(graphicCentreList.get(0));
                                        graphicCentreList.remove(0);
                                        pointCentreList.remove(0);
                                        //删除线
                                        mGraphicsOverlay.getGraphics().remove(graphicLineList.get(0));
                                        graphicLineList.remove(graphicLineList.get(0));
                                        //画新顶点
//                                        Point point = mMapView.screenToLocation(new android.graphics.Point(Math.round(event.getX()), Math.round(event.getY())));
                                        Graphic graphic = new Graphic(point, markerSymbol);
                                        graphic.setZIndex(999);
                                        mGraphicsOverlay.getGraphics().add(graphic);
                                        graphicPointList.add(position, graphic);
                                        pointList.add(position, point);
                                        //画新线
                                        PointCollection collection = new PointCollection(SpatialReferences.getWebMercator());
                                        collection.add(pointList.get(0));
                                        collection.add(pointList.get(1));
                                        Polyline polyline = new Polyline(collection);
                                        Graphic graphics = new Graphic(polyline, simpleLineSymbol);
                                        mGraphicsOverlay.getGraphics().add(graphics);
                                        graphicLineList.add(graphics);
                                        //画新中心点
                                        Point pointCentre = getCentrePoint(pointList.get(0), pointList.get(1));
                                        Graphic graphicCentre = new Graphic(pointCentre, markerCentreSymbol);
                                        mGraphicsOverlay.getGraphics().add(graphicCentre);
                                        graphicCentreList.add(graphicCentre);
                                        pointCentreList.add(pointCentre);
                                    } else if (pointList.size() > 2) {
                                        //删除顶点
                                        mGraphicsOverlay.getGraphics().remove(graphicPointList.get(position));
                                        graphicPointList.remove(position);
                                        pointList.remove(position);
                                        //删除中心点1
                                        mGraphicsOverlay.getGraphics().remove(graphicCentreList.get(position));
                                        graphicCentreList.remove(position);
                                        pointCentreList.remove(position);
                                        //删除中心点2
                                        if (position == 0) {
                                            mGraphicsOverlay.getGraphics().remove(graphicCentreList.get(graphicCentreList.size() - 1));
                                            graphicCentreList.remove(graphicCentreList.size() - 1);
                                            pointCentreList.remove(pointCentreList.size() - 1);
                                        } else {
                                            mGraphicsOverlay.getGraphics().remove(graphicCentreList.get(position - 1));
                                            graphicCentreList.remove(position - 1);
                                            pointCentreList.remove(position - 1);
                                        }
                                        //删除线1
                                        mGraphicsOverlay.getGraphics().remove(graphicLineList.get(position));
                                        graphicLineList.remove(graphicLineList.get(position));
                                        //删除中线2
                                        if (position == 0) {
                                            mGraphicsOverlay.getGraphics().remove(graphicLineList.get(graphicLineList.size() - 1));
                                            graphicLineList.remove(graphicLineList.get(graphicLineList.size() - 1));
                                        } else {
                                            mGraphicsOverlay.getGraphics().remove(graphicLineList.get(position - 1));
                                            graphicLineList.remove(graphicLineList.get(position - 1));
                                        }
                                        //画新顶点
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
                                            //画新线1
                                            PointCollection collection = new PointCollection(SpatialReferences.getWebMercator());
                                            collection.add(pointList.get(position));
                                            collection.add(pointList.get(position + 1));
                                            Polyline polyline = new Polyline(collection);
                                            Graphic graphics = new Graphic(polyline, simpleLineSymbol);
                                            mGraphicsOverlay.getGraphics().add(graphics);
                                            graphicLineList.add(position, graphics);
                                            //画新线2
                                            PointCollection collection2 = new PointCollection(SpatialReferences.getWebMercator());
                                            collection2.add(pointList.get(position));
                                            collection2.add(pointList.get(pointList.size() - 1));
                                            Polyline polylines = new Polyline(collection2);
                                            Graphic graphicss = new Graphic(polylines, simpleLineSymbol);
                                            mGraphicsOverlay.getGraphics().add(graphicss);
                                            graphicLineList.add(graphicss);

                                            //画新中心点1
                                            Point pointCentre = getCentrePoint(pointList.get(0), pointList.get(1));
                                            Graphic graphicCentre = new Graphic(pointCentre, markerCentreSymbol);
                                            mGraphicsOverlay.getGraphics().add(graphicCentre);
                                            graphicCentreList.add(position, graphicCentre);
                                            pointCentreList.add(position, pointCentre);

                                            //画新中心点2
                                            Point pointCentre2 = getCentrePoint(pointList.get(0), pointList.get(pointList.size() - 1));
                                            Graphic graphicCentre2 = new Graphic(pointCentre2, markerCentreSymbol);
                                            mGraphicsOverlay.getGraphics().add(graphicCentre2);
                                            graphicCentreList.add(graphicCentre2);
                                            pointCentreList.add(pointCentre2);
                                        } else {
                                            //画新线1
                                            PointCollection collection = new PointCollection(SpatialReferences.getWebMercator());
                                            collection.add(pointList.get(position));
                                            collection.add(pointList.get(position - 1));
                                            Polyline polyline = new Polyline(collection);
                                            Graphic graphics = new Graphic(polyline, simpleLineSymbol);
                                            mGraphicsOverlay.getGraphics().add(graphics);
                                            graphicLineList.add(position - 1, graphics);
                                            //画新线2
                                            PointCollection collection2 = new PointCollection(SpatialReferences.getWebMercator());
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

                                            //画新中心点1
                                            Point pointCentre = getCentrePoint(pointList.get(position), pointList.get(position - 1));
                                            Graphic graphicCentre = new Graphic(pointCentre, markerCentreSymbol);
                                            mGraphicsOverlay.getGraphics().add(graphicCentre);
                                            graphicCentreList.add(position - 1, graphicCentre);
                                            pointCentreList.add(position - 1, pointCentre);

                                            //画新中心点2
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
                    }
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
            if (longPressFlag) {
                longPressFlag = false;
                onSingleTapConfirmed(e);
            }
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

            if (pointList.size() > 0) {
                longPressFlag = true;
            } else {
                getGraphicForUpdata(new android.graphics.Point(Math.round(e.getX()), Math.round(e.getY())));
            }

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
            if (canDrawTop) {
                Point point = mMapView.screenToLocation(new android.graphics.Point(Math.round(e.getX()), Math.round(e.getY())));
                Graphic graphic = new Graphic(point, markerSymbol);
                graphic.setZIndex(999);
                mGraphicsOverlay.getGraphics().add(graphic);
                graphicPointList.add(graphic);
                pointList.add(point);
                drawLine();
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

    public void drawLine() {
        if (pointList.size() == 2) {
            //画线
            PointCollection collection = new PointCollection(SpatialReferences.getWebMercator());
            collection.add(pointList.get(0));
            collection.add(pointList.get(1));
            Polyline polyline = new Polyline(collection);

            Graphic graphic = new Graphic(polyline, simpleLineSymbol);
            mGraphicsOverlay.getGraphics().add(graphic);
            graphicLineList.add(graphic);
            //画中心点
            Point pointCentre = getCentrePoint(pointList.get(0), pointList.get(1));
            Graphic graphicCentre = new Graphic(pointCentre, markerCentreSymbol);
            mGraphicsOverlay.getGraphics().add(graphicCentre);
            graphicCentreList.add(graphicCentre);
            pointCentreList.add(pointCentre);


        } else if (pointList.size() == 3) {
            PointCollection collection = new PointCollection(SpatialReferences.getWebMercator());
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
            //画最后两点线段中心点
            Point pointCentre1 = getCentrePoint(pointList.get(1), pointList.get(2));
            Graphic graphicCentre = new Graphic(pointCentre1, markerCentreSymbol);
            mGraphicsOverlay.getGraphics().add(graphicCentre);
            graphicCentreList.add(graphicCentre);
            pointCentreList.add(pointCentre1);
            //画顶点和最后点线段中心点
            Point pointCentre2 = getCentrePoint(pointList.get(0), pointList.get(2));
            Graphic graphicCentre2 = new Graphic(pointCentre2, markerCentreSymbol);
            mGraphicsOverlay.getGraphics().add(graphicCentre2);
            graphicCentreList.add(graphicCentre2);
            pointCentreList.add(pointCentre2);


        } else if (pointList.size() > 3) {
            PointCollection collection = new PointCollection(SpatialReferences.getWebMercator());
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
            //删除 顶点和最后点线段中心点
            mGraphicsOverlay.getGraphics().remove(graphicCentreList.get(graphicCentreList.size() - 1));
            graphicCentreList.remove(graphicCentreList.size() - 1);
            pointCentreList.remove(pointCentreList.size() - 1);
            //画 最后两点线段中心点
            Point pointCentre1 = getCentrePoint(pointList.get(pointList.size() - 2), pointList.get(pointList.size() - 1));
            Graphic graphicCentre = new Graphic(pointCentre1, markerCentreSymbol);
            mGraphicsOverlay.getGraphics().add(graphicCentre);
            graphicCentreList.add(graphicCentre);
            pointCentreList.add(pointCentre1);
            //画顶点和最后点线段中心点
            Point pointCentre2 = getCentrePoint(pointList.get(0), pointList.get(pointList.size() - 1));
            Graphic graphicCentre2 = new Graphic(pointCentre2, markerCentreSymbol);
            mGraphicsOverlay.getGraphics().add(graphicCentre2);
            graphicCentreList.add(graphicCentre2);
            pointCentreList.add(pointCentre2);
        }
        drawPolygon();
    }

    public void drawDbPolygon(String itemCustomerIds, String taskId, List<DKPointBean> dkPointList, String areaDb) {
        PointCollection poliCollectionDb = new PointCollection(SpatialReferences.getWgs84());
        for (int i = 0; i < dkPointList.size(); i++) {
            DKPointBean bean = dkPointList.get(i);
            Point wgsPoint = new Point(Double.parseDouble(bean.getLon()), Double.parseDouble(bean.getLat()));
            Point mapPoint = (Point) GeometryEngine.project(wgsPoint, SpatialReference.create(4326));
            poliCollectionDb.add(mapPoint);
        }
        Polygon polygonDb = new Polygon(poliCollectionDb);
        SimpleFillSymbol simpleFillSymbol;
        if (customerIdsForUpdata.equals(itemCustomerIds) && updataFlagFrom.equals("1")) {
            simpleFillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.parseColor("#50FF0000"), null);
        } else {
            simpleFillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.parseColor("#5000ff00"), null);
        }
        Graphic polygonDbGraphic = new Graphic(polygonDb, simpleFillSymbol);
        polygonDbGraphic.getAttributes().put("taskId", taskId);
        polygonDbGraphic.setZIndex(-1);
        mGraphicsOverlay.getGraphics().add(polygonDbGraphic);
        graphicDbList.add(polygonDbGraphic);
        Point point = polygonDbGraphic.getGeometry().getExtent().getCenter();
        TextSymbol textSymbol = new TextSymbol(15, areaDb, Color.parseColor("#ffffff"), TextSymbol.HorizontalAlignment.CENTER, TextSymbol.VerticalAlignment.MIDDLE);
        Graphic areaGraphicDb = new Graphic(point, textSymbol);
        areaGraphicDb.getAttributes().put("taskId", taskId);
        areaGraphicDb.setZIndex(999);
        mGraphicsOverlay.getGraphics().add(areaGraphicDb);
        graphicAreaList.add(areaGraphicDb);
        /*if (customerIds.equals(itemCustomerIds) && updataFlagFrom.equals("1")&&extentFlag) {
            mMapView.setViewpointGeometryAsync(polygonDbGraphic.getGeometry().getExtent());
            extentFlag = false;
        }*/

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
            SimpleFillSymbol simpleFillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.parseColor("#603BB06C"), null);
            polygonGraphic = new Graphic(polygon, simpleFillSymbol);
            polygonGraphic.setZIndex(-1);
            mGraphicsOverlay.getGraphics().add(polygonGraphic);

            Point point = polygonGraphic.getGeometry().getExtent().getCenter();
            if (areaGraphic != null) {
                mGraphicsOverlay.getGraphics().remove(areaGraphic);
                areaGraphic = null;
            }
            polygonArea = calculateArea(GeometryEngine.areaGeodetic(polygon, new AreaUnit(AreaUnitId.SQUARE_METERS), GeodeticCurveType.GEODESIC));
            textSymbol = new TextSymbol(15, polygonArea + "亩", Color.parseColor("#ffffff"), TextSymbol.HorizontalAlignment.CENTER, TextSymbol.VerticalAlignment.MIDDLE);
            areaGraphic = new Graphic(point, textSymbol);
            areaGraphic.setZIndex(999);
            mGraphicsOverlay.getGraphics().add(areaGraphic);
            double overArea = hasArea+Double.parseDouble(polygonArea);
            lackArea = (allArea - overArea)/allArea;
            tv_needArea.setText(allArea+"");
            tv_hasArea.setText(formatDouble2(overArea)+"");
            tv_lackArea.setText(formatDouble2((allArea - overArea))+"");
            tv_error.setText(formatDouble2((lackArea*100))+"");
            if (lackArea>0.02||lackArea<-0.02){
                tv_error.setTextColor(Color.parseColor("#FF0000"));
            }else {
                tv_error.setTextColor(Color.parseColor("#00FF00"));
            }

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
            lackArea = (allArea - hasArea)/allArea;
            tv_needArea.setText(""+allArea+"");
            tv_hasArea.setText(""+hasArea+"");
            tv_lackArea.setText(""+formatDouble2((allArea - hasArea))+"");
            tv_error.setText(""+formatDouble2((lackArea*100))+"");
            if (lackArea>0.02||lackArea<-0.02){
                tv_error.setTextColor(Color.parseColor("#FF0000"));
            }else {
                tv_error.setTextColor(Color.parseColor("#00FF00"));
            }
        }
    }

    /**
     * 获取p1与p2的中心点
     *
     * @param p1 起点
     * @param p2 终点
     * @return
     */
    public Point getCentrePoint(Point p1, Point p2) {
        Point point = new Point((p1.getX() + p2.getX()) / 2, (p1.getY() + p2.getY()) / 2, p1.getSpatialReference());
        return point;
    }

    //计算面积
    private String calculateArea(double area) {
        DecimalFormat df = new DecimalFormat("#######.00");
        double area2 = Math.abs(area / 666.667d);
        return df.format(area2);
    }

    /**
     * 获取选中的点
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

    @Override
    protected void onDestroy() {
        mMapView.dispose();
        super.onDestroy();
        sensorManager.unregisterListener(sensorEventListener);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dk_btn_back:
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

                    PointCollection collection = new PointCollection(SpatialReferences.getWebMercator());
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
                    mGraphicsOverlay.getGraphics().remove(graphicPointList.get(graphicPointList.size() - 1));
                    pointList.remove(pointList.size() - 1);
                    graphicPointList.remove(graphicPointList.size() - 1);
                }
                break;
            case R.id.dk_btn_delete:
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
                tv_needArea.setText(""+allArea+"");
                tv_hasArea.setText(""+hasArea+"");
                tv_lackArea.setText(""+formatDouble2((allArea - hasArea))+"");
                tv_error.setText(""+formatDouble2((lackArea*100))+"");
                if (lackArea>0.02||lackArea<-0.02){
                    tv_error.setTextColor(Color.parseColor("#FF0000"));
                }else {
                    tv_error.setTextColor(Color.parseColor("#00FF00"));
                }
                break;
            case R.id.dk_btn_keep:
                if (pointList.size() > 2) {
                    keep();
                    double holdsNumber = 0;
                    for (int i = 0; i < holdsBeanList.size(); i++) {
                        if (holdsBeanList.get(i).isChoose()) {
                            holdsNumber += Double.parseDouble(holdsBeanList.get(i).getInsureArea());
                        }
                    }
                    holdsNumber = formatDouble2(holdsNumber);
                    double havePoltNumber = dkService.getSumPlotByCustomerIds(province, city, town, country, village, proposalNo, certificateId, customerIds);
                    if ((holdsNumber - (holdsNumber * 0.02)) <= havePoltNumber && havePoltNumber <= (holdsNumber + (holdsNumber * 0.02))) {
                        DKBean dkBean = dkService.getDKBeanLimit(province, city, town, country, village, proposalNo, certificateId, customerIds);
                        double drawArea = Double.parseDouble(dkBean.getChangeArea());
                        double changeArea = formatDouble2(holdsNumber - (havePoltNumber - drawArea));
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("changeArea", changeArea + "");
                        contentValues.put("taskId", dkBean.getTaskId());
                        boolean success = dkService.updataDKInfoChangeArea(contentValues);
                    }
                } else {
                    Toast.makeText(this, "请画地块并闭合地块", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.dk_btn_finish:
                /*if (pointList.size() < 3 && pointList.size() > 0) {
                    Toast.makeText(this, "请闭合地块", Toast.LENGTH_SHORT).show();
                    return;
                } else if (pointList.size() > 2) {
                    keep();
                }*/
                if (pointList.size() > 0) {
                    Toast.makeText(this, "请完成地块绘制", Toast.LENGTH_SHORT).show();
                    return;
                } 
                double holdsNumber = 0;
                for (int i = 0; i < holdsBeanList.size(); i++) {
                    if (holdsBeanList.get(i).isChoose()) {
                        holdsNumber += Double.parseDouble(holdsBeanList.get(i).getInsureArea());
                    }
                }
                holdsNumber = formatDouble2(holdsNumber);
                double havePoltNumber = dkService.getSumPlotByCustomerIds(province, city, town, country, village, proposalNo, certificateId, customerIds);
                if ((holdsNumber - (holdsNumber * 0.02)) <= havePoltNumber && havePoltNumber <= (holdsNumber + (holdsNumber * 0.02))) {
                    DKBean dkBean = dkService.getDKBeanLimit(province, city, town, country, village, proposalNo, certificateId, customerIds);
                    double drawArea = Double.parseDouble(dkBean.getChangeArea());
                    double changeArea = formatDouble2(holdsNumber - (havePoltNumber - drawArea));
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("changeArea", changeArea + "");
                    contentValues.put("taskId", dkBean.getTaskId());
                    boolean success = dkService.updataDKInfoChangeArea(contentValues);
                    if (success) {
                        Intent intent_finish = new Intent(DiKuaiActivity.this, FinishActivity.class);
                        intent_finish.putExtra("provinceName", province);
                        intent_finish.putExtra("provinceCode", provinceCode);
                        intent_finish.putExtra("cityName", city);
                        intent_finish.putExtra("cityCode", cityCode);
                        intent_finish.putExtra("countyName", town);
                        intent_finish.putExtra("countyCode", zipCode);
                        intent_finish.putExtra("countryName", country);
                        intent_finish.putExtra("countryCode", sideCode);
                        intent_finish.putExtra("villageName", village);
                        intent_finish.putExtra("villageCode", villCode);
                        intent_finish.putExtra("proposalNo", proposalNo);
                        intent_finish.putExtra("certificateId", certificateId);
                        startActivity(intent_finish);
                        finish();
                    }

                } else {
                    Toast.makeText(this, "标绘面积与投保面积不符", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.dk_btn_location:
                LocationDataSource.Location location = locationDisplay.getLocation();
                if (location != null) {
                    Point pointGps = location.getPosition();
                    if (pointGps!=null){
                        Point mapPoint = (Point) GeometryEngine.project(pointGps, SpatialReference.create(4326));
                        TextView calloutContent = new TextView(getApplicationContext());
                        calloutContent.setTextColor(Color.BLACK);
                        calloutContent.setSingleLine();
                        calloutContent.setText("经度: " + pointGps.getX() + ", 纬度: " + pointGps.getY());
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
            case R.id.dk_btn_hoolds:
                lin_holds.setVisibility(View.VISIBLE);
                /*AlertDialog.Builder dialog = new AlertDialog.Builder(DiKuaiActivity.this);
                LayoutInflater inflater = LayoutInflater.from(DiKuaiActivity.this);
                View layout = inflater.inflate(R.layout.layout_lonlat, null);
                final EditText et_lon = layout.findViewById(R.id.lonlat_et_lon);
                final EditText et_lat = layout.findViewById(R.id.lonlat_et_lat);
                dialog.setTitle("请填写经纬度");
                dialog.setView(layout)
                        .setCancelable(false)
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        }).setNeutralButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (isDoubleOrFloat(et_lon.getText().toString().trim()) && isDoubleOrFloat(et_lat.getText().toString().trim())) {
                            if (mCallout.isShowing()) {
                                mCallout.dismiss();
                            }
                            Point wgsPoint = new Point(Double.parseDouble(et_lon.getText().toString().trim()), Double.parseDouble(et_lat.getText().toString().trim()));
                            Point mapPoint = (Point) GeometryEngine.project(wgsPoint, SpatialReference.create(4326));
                            TextView calloutContent = new TextView(getApplicationContext());
                            calloutContent.setTextColor(Color.BLACK);
                            calloutContent.setSingleLine();
                            // format coordinates to 4 decimal places
                            calloutContent.setText("经度: " + et_lon.getText().toString().trim() + ", 纬度: " + et_lat.getText().toString().trim());
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
                        } else {
                            Toast.makeText(DiKuaiActivity.this, "请输入正确的经纬度", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                dialog.show();*/
                break;
            case R.id.dk_btn_cure:
                lin_holds.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    public static boolean isDoubleOrFloat(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
        return pattern.matcher(str).matches();
    }

    private void keep() {
        if (customerIds.equals("")) {
            Toast.makeText(this, "请选择分户清单", Toast.LENGTH_SHORT).show();
            return;
        }
        if (updataFlagFrom.equals("1")){
            ContentValues contentValues = new ContentValues();
            contentValues.put("proposalNo",proposalNo);
            contentValues.put("certificateId",certificateId);
            contentValues.put("customerIds",customerIds);
            dkService.updataDKInfoByCustomerIds(contentValues,customerIdsForUpdata);
            mList = dkService.getDKList("-1");
            customerIdsForUpdata = customerIds;
        }
//        String taskId = UUID.randomUUID().toString();
        ContentValues values = new ContentValues();
        values.put("ID", TimeCycleUtils.getTaskId());
        values.put("taskId", taskId);
        values.put("proposalNo", proposalNo);
        values.put("certificateId", certificateId);
        values.put("customerIds", customerIds);
        values.put("userName", userName);
        values.put("crop", cropName);
        values.put("dkName", dkName);
        values.put("drawArea", polygonArea);
        values.put("changeArea", polygonArea);
        values.put("state", "0");
        values.put("createTime", TimeCycleUtils.getTime());
        values.put("subTime", "");
        values.put("userId", SharePreferencesTools.getValue(DiKuaiActivity.this, "easyPhoto", "userId", ""));
        values.put("province", province);
        values.put("city", city);
        values.put("town", town);
        values.put("countryside", country);
        values.put("village", village);
        values.put("provinceCode", provinceCode);
        values.put("cityCode", cityCode);
        values.put("townCode", zipCode);
        values.put("countrysideCode", sideCode);
        values.put("villageCode", villCode);
        values.put("isBusinessEntity", holdsBeanList.get(0).getIsBusinessEntity());
        values.put("policyNumber", holdsBeanList.get(0).getPolicyNumber());
        if (dkService.getIsHave(taskId)) {
            dkService.deleteDKInfo(taskId);
            for (int j = 0; j < graphicAreaList.size(); j++) {
                String areaTaskId = graphicAreaList.get(j).getAttributes().get("taskId").toString();
                if (areaTaskId.equals(taskId)) {
                    mGraphicsOverlay.getGraphics().remove(graphicAreaList.get(j));
                    graphicAreaList.remove(j);
                    break;
                }
            }
            for (int j = 0; j < graphicDbList.size(); j++) {
                String areaTaskId = graphicDbList.get(j).getAttributes().get("taskId").toString();
                if (areaTaskId.equals(taskId)) {
                    mGraphicsOverlay.getGraphics().remove(graphicDbList.get(j));
                    graphicDbList.remove(j);
                    break;
                }
            }
        }
        dkService.insertDKInfo(values);
        PointCollection poliCollectionDb = new PointCollection(SpatialReferences.getWebMercator());
        for (int j = 0; j < pointList.size(); j++) {
            Point point = pointList.get(j);
            Point wgs84Point = (Point) GeometryEngine.project(point, SpatialReferences.getWgs84());
            ContentValues contentValues = new ContentValues();
            contentValues.put("ID", TimeCycleUtils.getTaskId());
            contentValues.put("taskId", taskId);
            contentValues.put("lon", wgs84Point.getX());
            contentValues.put("lat", wgs84Point.getY());
            contentValues.put("type", "");
            contentValues.put("number", "" + j);
            dkService.insertDKPoint(contentValues);
            poliCollectionDb.add(point);
        }

        Polygon polygonDb = new Polygon(poliCollectionDb);
        SimpleFillSymbol simpleFillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.parseColor("#50FF4081"), null);
        Graphic polygonDbGraphic = new Graphic(polygonDb, simpleFillSymbol);
        polygonDbGraphic.getAttributes().put("taskId", taskId);
        polygonDbGraphic.setZIndex(-1);
        mGraphicsOverlay.getGraphics().add(polygonDbGraphic);
        graphicDbList.add(polygonDbGraphic);
        Point point = polygonDbGraphic.getGeometry().getExtent().getCenter();
        TextSymbol textSymbol = new TextSymbol(15, userName + "_" + dkName + "_" + polygonArea + "亩", Color.parseColor("#ffffff"), TextSymbol.HorizontalAlignment.CENTER, TextSymbol.VerticalAlignment.MIDDLE);
        Graphic areaGraphicDb = new Graphic(point, textSymbol);
        areaGraphicDb.getAttributes().put("taskId", taskId);
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
        mList = dkService.getDKList("-1");
        taskId = UUID.randomUUID().toString();
//        updataFlagFrom = "0";
        allArea = formatDouble2(allArea);
        hasArea = dkService.getSumPlotByCustomerIds(province, city, town, country, village, proposalNo, certificateId, customerIds);
        hasArea = formatDouble2(hasArea);
        lackArea = (allArea - hasArea)/allArea;
        tv_needArea.setText(""+allArea+"");
        tv_hasArea.setText(""+hasArea+"");
        tv_lackArea.setText(""+formatDouble2((allArea - hasArea))+"");
        tv_error.setText(""+formatDouble2((lackArea*100))+"");
        if (lackArea>0.02||lackArea<-0.02){
            tv_error.setTextColor(Color.parseColor("#FF0000"));
        }else {
            tv_error.setTextColor(Color.parseColor("#00FF00"));
        }
        List<MassifSnapBean> massifSnapBeanList = massifSnapService.getMassifSnap(province,city,town,country,village,certificateId);
        if (massifSnapBeanList.size()==0){
            upHaveDraw();
        }
    }


    private Bitmap getAreaBitMap(String areaText) {
        TextPaint textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(15);
        Bitmap txtBitmap = Bitmap.createBitmap((int) textPaint.measureText(areaText), 40, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(txtBitmap);
        canvas.drawBitmap(txtBitmap, 0, 2, null);
        StaticLayout sl = new StaticLayout(areaText, textPaint, txtBitmap.getWidth(), Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
        sl.draw(canvas);
        return txtBitmap;
    }

    public String formatDouble(double d) {
        return String.format("%.2f", d);
    }

    private void getGraphicForUpdata(android.graphics.Point screenPoint) {
        // 识别GraphicsOverlay中的Graphic
        final ListenableFuture<IdentifyGraphicsOverlayResult> identifyGraphic = mMapView.identifyGraphicsOverlayAsync(mGraphicsOverlay, screenPoint, 10.0, false, 10);
        identifyGraphic.addDoneListener(new Runnable() {
            @Override
            public void run() {
                try {
                    IdentifyGraphicsOverlayResult grOverlayResult = identifyGraphic.get();
                    // 获取识别的GraphicOverlay返回的图形列表
                    List<Graphic> graphicList = grOverlayResult.getGraphics();
                    // 获取结果列表的大小
                    int identifyResultSize = graphicList.size();
                    if (!graphicList.isEmpty() && identifyResultSize > 0) {
                        longPressFlag = false;
                        for (int i = 0; i < identifyResultSize; i++) {
                            Graphic mGraphic = graphicList.get(i);
                            String taskIds = mGraphic.getAttributes().get("taskId").toString();
                            if (mGraphic.getAttributes() != null && taskIds != null && taskIds.length() > 0 && pointList.size() == 0) {
                                longPressFlag = false;
                                updataFlag = true;
                                DKBean dkBeanU = dkService.getDKBean(taskIds);
                                if (customerIdsForUpdata.equals(dkBeanU.getCustomerIds()) && updataFlagFrom.equals("1")) {
                                    AlertDialog.Builder dialog = new AlertDialog.Builder(DiKuaiActivity.this);
                                    dialog.setTitle("提示").setMessage("请选择修改方式").setNegativeButton("删除", new DialogInterface.OnClickListener() {
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
                                            tv_needArea.setText(""+allArea+"");
                                            tv_hasArea.setText(""+hasArea+"");
                                            tv_lackArea.setText(""+formatDouble2((allArea - hasArea))+"");
                                            tv_error.setText(""+formatDouble2((lackArea*100))+"");
                                            if (lackArea>0.02||lackArea<-0.02){
                                                tv_error.setTextColor(Color.parseColor("#FF0000"));
                                            }else {
                                                tv_error.setTextColor(Color.parseColor("#00FF00"));
                                            }
                                            if (dkService.getDKByUser(certificateId,proposalNo).size()==0){
                                                deleteHaveDraw();
                                            }
                                        }
                                    }).setNeutralButton("调整面积", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //mGraphicsOverlay.getGraphics().remove(mGraphic);
                                            /*if (updataFlagFrom.equals("1")){
                                                ContentValues contentValues = new ContentValues();
                                                contentValues.put("proposalNo",proposalNo);
                                                contentValues.put("certificateId",certificateId);
                                                contentValues.put("customerIds",customerIds);
                                                dkService.updataDKInfoByCustomerIds(contentValues,customerIdsForUpdata);
                                                mList = dkService.getDKList("-1");
                                                customerIdsForUpdata = customerIds;
                                            }*/
                                            List<DKPointBean> dkPointBeanList = dkService.getDKPointList(taskIds);
                                            for (int i = 0; i < dkPointBeanList.size(); i++) {
                                                DKPointBean bean = dkPointBeanList.get(i);
                                                Point wgsPoint = new Point(Double.parseDouble(bean.getLon()), Double.parseDouble(bean.getLat()), SpatialReferences.getWgs84());
                                                Point mapPoint = (Point) GeometryEngine.project(wgsPoint, SpatialReferences.getWebMercator());
                                                Graphic graphic = new Graphic(mapPoint, markerSymbol);
                                                graphic.setZIndex(999);
                                                mGraphicsOverlay.getGraphics().add(graphic);
                                                graphicPointList.add(graphic);
                                                pointList.add(mapPoint);
                                            }
                                            for (int i = 0; i < pointList.size(); i++) {
                                                //画线
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
                                                //画中心点
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
                                            DKBean bean = dkService.getDKBean(taskIds);
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
                                            tv_needArea.setText(""+allArea+"");
                                            tv_hasArea.setText(""+hasArea+"");
                                            tv_lackArea.setText(""+formatDouble2((allArea - hasArea))+"");
                                            tv_error.setText(""+formatDouble2((lackArea*100))+"");
                                            if (lackArea>0.02||lackArea<-0.02){
                                                tv_error.setTextColor(Color.parseColor("#FF0000"));
                                            }else {
                                                tv_error.setTextColor(Color.parseColor("#00FF00"));
                                            }
                                            drawPolygon();
                                        }
                                    }).setPositiveButton("取消", new DialogInterface.OnClickListener() {
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
    }

    private void searchForState(String searchString) {
        QueryParameters query = new QueryParameters();//220303200205
        query.setWhereClause("xzdm like '%" + searchString+"%'");
        final ListenableFuture<FeatureQueryResult> future = mServiceFeatureTable.queryFeaturesAsync(query);
        future.addDoneListener(() -> {
            try {
                FeatureQueryResult result = future.get();
                Iterator<Feature> resultIterator = result.iterator();
                if (resultIterator.hasNext()) {
                    Feature feature = resultIterator.next();
                    Envelope envelope = feature.getGeometry().getExtent();
//                    if (!updataFlagFrom.equals("1")){
                        mMapView.setViewpointGeometryAsync(envelope, 10);
//                    }
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

    private double formatDouble2(double d) {
        BigDecimal bigDecimal = new BigDecimal(d);
        double bg = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return bg;
    }
    private void upHaveDraw() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        LoadingDialog.Builder loadBuilder = new LoadingDialog.Builder(DiKuaiActivity.this)
                .setMessage("正在更新信息")
                .setCancelable(true)
                .setCancelOutside(true);
        dialog = loadBuilder.create();
        dialog.show();
        MultipartBody.Builder mb = new MultipartBody.Builder();
        mb.setType(MultipartBody.FORM);
        mb.addFormDataPart("provinceName", province);
        mb.addFormDataPart("cityName", city);
        mb.addFormDataPart("countyName", town);
        mb.addFormDataPart("countryName", country);
        mb.addFormDataPart("villageName", village);
        mb.addFormDataPart("certificateId", certificateId);
        RequestBody requestBody = mb.build();
        DOkHttp.getInstance().uploadPost2ServerProgress(DiKuaiActivity.this, Configure.massifSnapAdd, "Bearer " + SharePreferencesTools.getValue(DiKuaiActivity.this, "easyPhoto", "access_token", ""), requestBody, new DOkHttp.MyCallBack() {
            @Override
            public void onFailure(Request request, IOException e) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                Toast.makeText(DiKuaiActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
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
                        ContentValues values = new ContentValues();
                        values.put("ID", TimeCycleUtil.getTaskId());
                        values.put("provinceName", province);
                        values.put("cityName", city);
                        values.put("countyName", town);
                        values.put("countryName", country);
                        values.put("villageName", village);
                        values.put("certificateId", certificateId);
                        massifSnapService.insertMassifSnap(values);
                        Toast.makeText(DiKuaiActivity.this, "更新数据成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(DiKuaiActivity.this, message, Toast.LENGTH_SHORT).show();
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
    private void deleteHaveDraw() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        LoadingDialog.Builder loadBuilder = new LoadingDialog.Builder(DiKuaiActivity.this)
                .setMessage("正在更新信息")
                .setCancelable(true)
                .setCancelOutside(true);
        dialog = loadBuilder.create();
        dialog.show();
        MultipartBody.Builder mb = new MultipartBody.Builder();
        mb.setType(MultipartBody.FORM);
        mb.addFormDataPart("provinceName", province);
        mb.addFormDataPart("cityName", city);
        mb.addFormDataPart("countyName", town);
        mb.addFormDataPart("countryName", country);
        mb.addFormDataPart("villageName", village);
        mb.addFormDataPart("certificateId", certificateId);
        RequestBody requestBody = mb.build();
        DOkHttp.getInstance().uploadPost2ServerProgress(DiKuaiActivity.this, Configure.massifSnapDelete, "Bearer " + SharePreferencesTools.getValue(DiKuaiActivity.this, "easyPhoto", "access_token", ""), requestBody, new DOkHttp.MyCallBack() {
            @Override
            public void onFailure(Request request, IOException e) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                Toast.makeText(DiKuaiActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
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
                        if (massifSnapService.deleteCustomer(province,city,town,country,village,certificateId)){
                            Toast.makeText(DiKuaiActivity.this, "数据更新成功", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(DiKuaiActivity.this, message, Toast.LENGTH_SHORT).show();
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
