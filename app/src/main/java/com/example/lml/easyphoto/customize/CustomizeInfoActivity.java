package com.example.lml.easyphoto.customize;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.lml.easyphoto.R;
import com.example.lml.easyphoto.camera.TimeCycleUtils;
import com.example.lml.easyphoto.dikuai.subList.DKSubListActivity;
import com.example.lml.easyphoto.main.MenuActivity;
import com.example.lml.easyphoto.util.Configure;
import com.example.lml.easyphoto.util.Consts;
import com.example.lml.easyphoto.util.GetSingleSelectItem;
import com.example.lml.easyphoto.util.SharePreferencesTools;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import cn.hutool.json.JSONUtil;

public class CustomizeInfoActivity extends AppCompatActivity implements View.OnClickListener{
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
    private Button btn_finish;
    private LinearLayout btn_location, btn_delete, btn_back;
    private CustomizeDKService dkService;
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
    private PointCollection poliCollection = new PointCollection(SpatialReferences.getWebMercator());
    private Polygon polygon;
    private String polygonArea = "";
    private Graphic polygonGraphic;
    private Graphic areaGraphic;
    //gps定位
    private LocationDisplay locationDisplay;
    private String serverPath;
    private ServiceFeatureTable mServiceFeatureTable;
    private ArcGISMapImageLayer mainArcGISVectorTiledLayer;
    private String theOnlineTiledLayers = Configure.gisXZQYUrl;
    private String[] crop = Consts.getStrNames(Consts.risk);
    private String[] hzs = new String[]{"否", "是"};
    String taskIdOld = "";
    private Button btn_esc;
    private Gson gson;
    private  CustomizeDKBean mDkBean;
    private int drawFlag = 0;//0:刚进来 不能画图 1：点击修改可以画图
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize_info);
        initView();
    }
    private void initView() {
        gson = new Gson();
        dkService = new CustomizeDKService(this);
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
        mMapView = findViewById(R.id.customize_info_mapView);
        btn_location = findViewById(R.id.customize_info_btn_location);
        btn_back = findViewById(R.id.customize_info_btn_back);
        btn_delete = findViewById(R.id.customize_info_btn_delete);
        btn_finish = findViewById(R.id.customize_info_btn_finish);
        btn_esc = findViewById(R.id.customize_info_btn_esc);
        btn_location.setOnClickListener(this);
        btn_back.setOnClickListener(this);
        btn_delete.setOnClickListener(this);
        btn_finish.setOnClickListener(this);
        btn_esc.setOnClickListener(this);
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
        mDkBean = gson.fromJson(getIntent().getStringExtra("json"),CustomizeDKBean.class);
        searchForState(villCode);

    }

    public void goHome(View view) {
        Intent intent = new Intent(CustomizeInfoActivity.this, MenuActivity.class);
        startActivity(intent);
    }


    public class MyDefaultMapViewOnTouchListener extends DefaultMapViewOnTouchListener {

        public MyDefaultMapViewOnTouchListener(Context context, MapView mapView) {
            super(context, mapView);
        }

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            if (drawFlag==1){
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
            }

            return super.onTouch(view, event);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return super.onDown(e);
        }

        @Override
        public boolean onUp(MotionEvent e) {
            if (drawFlag ==1){
                if (longPressFlag) {
                    longPressFlag = false;
                    onSingleTapConfirmed(e);
                }
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
            if (drawFlag==1){
                if (canDrawTop) {
                    Point point = mMapView.screenToLocation(new android.graphics.Point(Math.round(e.getX()), Math.round(e.getY())));
                    Graphic graphic = new Graphic(point, markerSymbol);
                    graphic.setZIndex(999);
                    mGraphicsOverlay.getGraphics().add(graphic);
                    graphicPointList.add(graphic);
                    pointList.add(point);
                    drawLine();
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

        /*@Override
        public void setPinchToZoomGestureDetector(PinchToZoomGestureDetector pinchToZoomGestureDetector) {
            super.setPinchToZoomGestureDetector(pinchToZoomGestureDetector);
        }

        @Override
        public PinchToZoomGestureDetector getPinchToZoomGestureDetector() {
            return super.getPinchToZoomGestureDetector();
        }*/
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

    public void drawDbPolygon(String taskId, List<CustomizeDKPointBean> dkPointList, String areaDb) {
        PointCollection poliCollectionDb = new PointCollection(SpatialReferences.getWgs84());
        for (int i = 0; i < dkPointList.size(); i++) {
            CustomizeDKPointBean bean = dkPointList.get(i);
            Point wgsPoint = new Point(Double.parseDouble(bean.getLon()), Double.parseDouble(bean.getLat()));
            Point mapPoint = (Point) GeometryEngine.project(wgsPoint, SpatialReference.create(4326));
            poliCollectionDb.add(mapPoint);
        }
        Polygon polygonDb = new Polygon(poliCollectionDb);
        SimpleFillSymbol simpleFillSymbol;
        simpleFillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.parseColor("#5000ff00"), null);
        Graphic polygonDbGraphic = new Graphic(polygonDb, simpleFillSymbol);
        polygonDbGraphic.getAttributes().put("taskId", taskId);
        polygonDbGraphic.setZIndex(-1);
        mGraphicsOverlay.getGraphics().add(polygonDbGraphic);
        graphicDbList.add(polygonDbGraphic);
        mMapView.setViewpointGeometryAsync(polygonDbGraphic.getGeometry(), 10);
        Point point = polygonDbGraphic.getGeometry().getExtent().getCenter();
        TextSymbol textSymbol = new TextSymbol(15, areaDb, Color.parseColor("#ffffff"), TextSymbol.HorizontalAlignment.CENTER, TextSymbol.VerticalAlignment.MIDDLE);
        Graphic areaGraphicDb = new Graphic(point, textSymbol);
        areaGraphicDb.getAttributes().put("taskId", taskId);
        areaGraphicDb.setZIndex(999);
        mGraphicsOverlay.getGraphics().add(areaGraphicDb);
        graphicAreaList.add(areaGraphicDb);

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
            case R.id.customize_info_btn_back:
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
            case R.id.customize_info_btn_delete:
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
            case R.id.customize_info_btn_finish:
                if (pointList.size() > 2) {
                    keep();
                } else {
                    finish();
                }
                break;
            case R.id.customize_info_btn_esc:
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
                taskIdOld = "";
                btn_esc.setVisibility(View.GONE);
                drawFlag = 0;
                break;
            case R.id.customize_info_btn_location:
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
            default:
                break;
        }
    }

    public static boolean isDoubleOrFloat(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
        return pattern.matcher(str).matches();
    }

    private void keep() {

        LayoutInflater inflater = LayoutInflater.from(CustomizeInfoActivity.this);
        View layout = inflater.inflate(R.layout.layout_keep, null);
        final EditText et_crop = layout.findViewById(R.id.keep_et_crop);
        final EditText et_userName = layout.findViewById(R.id.keep_et_userName);
        final EditText et_dkName = layout.findViewById(R.id.keep_et_dkName);
        final EditText et_cardNumber = layout.findViewById(R.id.keep_et_cardNumber);
        final EditText et_hezuoshe = layout.findViewById(R.id.keep_et_hezuoshe);
        final EditText et_remarks = layout.findViewById(R.id.keep_et_remarks);
        if (!taskIdOld.equals("")){
            CustomizeDKBean customizeDKBean = dkService.getDKBean(taskIdOld);
            et_crop.setText(customizeDKBean.getCrop());
            et_userName.setText(customizeDKBean.getUserName());
            et_dkName.setText(customizeDKBean.getDkName());
            et_cardNumber.setText(customizeDKBean.getCertificateId());
            et_hezuoshe.setText(customizeDKBean.getIsBusinessEntity());
            et_remarks.setText(customizeDKBean.getRemarks());
        }else {
            et_hezuoshe.setText(hzs[0]);
        }
        et_crop.setOnTouchListener(new GetSingleSelectItem(CustomizeInfoActivity.this, et_crop, "种植作物", crop, false));
        et_hezuoshe.setOnTouchListener(new GetSingleSelectItem(CustomizeInfoActivity.this, et_hezuoshe, "是否合作社", hzs, false));
        AlertDialog dialog = new AlertDialog.Builder(CustomizeInfoActivity.this)
                .setTitle("设置")
                .setView(layout)
                .setCancelable(false)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).setPositiveButton("确定", null).create();


        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog1) {
                Button btnPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                btnPositive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (et_dkName.getText().toString().trim().equals("") || et_crop.getText().toString().trim().equals("")) {
                            Toast.makeText(CustomizeInfoActivity.this, "地块名称和种植作物是必填项请填选", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String taskId ="";
                        if (taskIdOld.equals("")){
                            taskId = UUID.randomUUID().toString();
                        }else {
                            taskId = taskIdOld;
                        }
                        ContentValues values = new ContentValues();
                        values.put("ID", TimeCycleUtils.getTaskId());
                        values.put("taskId", taskId);
                        values.put("proposalNo", "");
                        values.put("certificateId", et_cardNumber.getText().toString());
                        values.put("customerIds", "");
                        values.put("userName", et_userName.getText().toString());
                        values.put("crop", et_crop.getText().toString().trim());
                        values.put("dkName", et_dkName.getText().toString().trim());
                        values.put("drawArea", polygonArea);
                        values.put("changeArea", "0");
                        values.put("state", "0");
                        values.put("createTime", TimeCycleUtils.getTime());
                        values.put("subTime", "");
                        values.put("userId", SharePreferencesTools.getValue(CustomizeInfoActivity.this, "easyPhoto", "userId", ""));
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
                        values.put("isBusinessEntity", et_hezuoshe.getText().toString().trim());
                        values.put("remarks", et_remarks.getText().toString().trim());
                        values.put("policyNumber", "");
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
                        TextSymbol textSymbol = new TextSymbol(15, et_dkName.getText().toString().trim() + "_" + et_crop.getText().toString().trim() + "_" + polygonArea + "亩", Color.parseColor("#ffffff"), TextSymbol.HorizontalAlignment.CENTER, TextSymbol.VerticalAlignment.MIDDLE);
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
                        taskIdOld = "";
                        btn_esc.setVisibility(View.GONE);
                        dialog.dismiss();
                        Intent intentss = new Intent();
                        intentss.setAction("com.znyg.znyp.refushDk");
                        sendBroadcast(intentss);
                        finish();
                    }
                });
            }
        });

        dialog.show();

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
                                CustomizeDKBean dkBeanU = dkService.getDKBean(taskIds);
                                if ("0".equals(dkBeanU.getState())) {
                                    AlertDialog.Builder dialog = new AlertDialog.Builder(CustomizeInfoActivity.this);
                                    dialog.setTitle("提示").setMessage("请选择修改方式").setNegativeButton("删除", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
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
                                            Intent intentss = new Intent();
                                            intentss.setAction("com.znyg.znyp.refushDk");
                                            sendBroadcast(intentss);
                                            finish();
                                        }
                                    }).setNeutralButton("调整面积", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            drawFlag = 1;
                                            btn_esc.setVisibility(View.VISIBLE);
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
                                            taskIdOld = taskIds;
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
        query.setWhereClause("xzdm like '%" + searchString + "%'");
        final ListenableFuture<FeatureQueryResult> future = mServiceFeatureTable.queryFeaturesAsync(query);
        future.addDoneListener(() -> {
            try {
                FeatureQueryResult result = future.get();
                Iterator<Feature> resultIterator = result.iterator();
                if (resultIterator.hasNext()) {
                    Feature feature = resultIterator.next();
                    Envelope envelope = feature.getGeometry().getExtent();
                    mMapView.setViewpointGeometryAsync(envelope, 10);
                    drawDbPolygon(mDkBean.getTaskId(), mDkBean.getmList(), mDkBean.getDkName() + "_" + mDkBean.getCrop() + "_" + mDkBean.getDrawArea() + "亩");
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
}
