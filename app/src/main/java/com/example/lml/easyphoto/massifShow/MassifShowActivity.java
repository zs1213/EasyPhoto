package com.example.lml.easyphoto.massifShow;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.ArcGISMapImageLayer;
import com.esri.arcgisruntime.layers.WebTiledLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.IdentifyGraphicsOverlayResult;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.TextSymbol;
import com.example.lml.easyphoto.FarmerInfo.houseHolds.HoldsService;
import com.example.lml.easyphoto.FarmerInfo.houseHolds.HouseHoldsBean;
import com.example.lml.easyphoto.R;
import com.example.lml.easyphoto.camera.LoadLocalImageUtil;
import com.example.lml.easyphoto.dikuai.DKBean;
import com.example.lml.easyphoto.dikuai.DKPointBean;
import com.example.lml.easyphoto.dikuai.DKService;
import com.example.lml.easyphoto.dikuai.DiKuaiActivity;
import com.example.lml.easyphoto.dikuai.finish.FinishHoldsAdapter;
import com.example.lml.easyphoto.history.HistoryBean;
import com.example.lml.easyphoto.sign.SignBean;
import com.example.lml.easyphoto.sign.SignService;
import com.example.lml.easyphoto.util.Configure;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MassifShowActivity extends Activity {
    private TextView tv_title;
    private MapView mMapView;
    private Button btn_holds;
    private Button btn_sign;
    private String serverPath;
    private HistoryBean historyBean;
    private Gson gson;
    private GraphicsOverlay mGraphicsOverlay;
    private LocationDisplay locationDisplay;
    private ServiceFeatureTable mServiceFeatureTable;
    private ArcGISMapImageLayer mainArcGISVectorTiledLayer;
//    private String theOnlineTiledLayers = "http://111.26.39.91:8002/arcgis/rest/services/JiLin/JinLin_xingzhengbianjie/MapServer";
    private String theOnlineTiledLayers = Configure.gisXZQYUrl;
    private DKService dkService;
    private HoldsService holdsService;
    private List<DKBean> mList;
    private List<HouseHoldsBean> holdsList;
    private SignService signService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_massif_show);
        initView();
        initData();
    }

    private void initView() {
        tv_title = findViewById(R.id.ams_tv_title);
        mMapView = findViewById(R.id.ams_map_mapView);
        btn_holds = findViewById(R.id.ams_btn_hoolds);
        btn_sign = findViewById(R.id.ams_btn_sign);
    }

    private void initData() {
        gson = new Gson();
        dkService = new DKService(this);
        holdsService = new HoldsService(this, dkService);
        signService = new SignService(this);
        serverPath = getIntent().getStringExtra("serverPath");
        historyBean = gson.fromJson(getIntent().getStringExtra("HistoryBean"), HistoryBean.class);
        mMapView.setAttributionTextVisible(false);
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
        locationDisplay = mMapView.getLocationDisplay();
        locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.OFF);
        locationDisplay.startAsync();
        searchForState(historyBean.getVillageCode());
        mMapView.setOnTouchListener(new DefaultMapViewOnTouchListener(this, mMapView) {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                getGraphicForUpdata(new android.graphics.Point(Math.round(e.getX()), Math.round(e.getY())));
                return super.onSingleTapUp(e);
            }
        });
        mList = dkService.getDKByUser(historyBean.getCertificateId(), historyBean.getProposalNo());
        for (int i = 0; i < mList.size(); i++) {
            DKBean dkBean = mList.get(i);
            List<DKPointBean> dkPointList = dkBean.getmList();
            drawDbPolygon(dkBean.getTaskId(), dkPointList, dkBean.getUserName() + "_" + dkBean.getDkName() + "_" + dkBean.getChangeArea() + "亩");
        }
        tv_title.setText(historyBean.getUserName() + "\n" + historyBean.getCertificateId());
        holdsList = holdsService.getCustomerList(historyBean.getProvince(), historyBean.getCity(), historyBean.getTown(), historyBean.getCountryside(), historyBean.getVillage(), historyBean.getProposalNo(), historyBean.getCertificateId());
        btn_holds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MassifShowActivity.this);
                View view = LayoutInflater.from(MassifShowActivity.this).inflate(R.layout.layout_holds, null);
                ListView listView = view.findViewById(R.id.show_lv_holds);
                FinishHoldsAdapter holdsAdapter = new FinishHoldsAdapter(holdsList, MassifShowActivity.this);
                listView.setAdapter(holdsAdapter);
                dialog.setView(view);
                dialog.setCancelable(true);
                dialog.create().show();
            }
        });
        btn_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignBean bean = signService.getSignBean(historyBean.getProposalNo(), historyBean.getCertificateId());
                if (bean!=null){
                    AlertDialog.Builder dialog = new AlertDialog.Builder(MassifShowActivity.this);
                    ImageView imageView = new ImageView(MassifShowActivity.this);
                    imageView.setBackgroundColor(Color.parseColor("#ffffff"));
                    imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    LoadLocalImageUtil.getInstance().displayFromSDCard(bean.getPath(),imageView);
                    dialog.setView(imageView);
                    dialog.setCancelable(true);
                    dialog.create().show();
                }else {
                    switch (historyBean.getState()){
                        case "0":
                            Toast.makeText(MassifShowActivity.this, "未标绘完成", Toast.LENGTH_SHORT).show();
                            break;
                        case "1":
                            Toast.makeText(MassifShowActivity.this, "未签字", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            break;
                    }
                }

            }
        });
    }

    public void drawDbPolygon(String taskId, List<DKPointBean> dkPointList, String areaDb) {
        PointCollection poliCollectionDb = new PointCollection(SpatialReferences.getWgs84());
        for (int i = 0; i < dkPointList.size(); i++) {
            DKPointBean bean = dkPointList.get(i);
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
        Point point = polygonDbGraphic.getGeometry().getExtent().getCenter();
        TextSymbol textSymbol = new TextSymbol(15, areaDb, Color.parseColor("#ffffff"), TextSymbol.HorizontalAlignment.CENTER, TextSymbol.VerticalAlignment.MIDDLE);
        Graphic areaGraphicDb = new Graphic(point, textSymbol);
        areaGraphicDb.getAttributes().put("taskId", taskId);
        areaGraphicDb.setZIndex(999);
        mGraphicsOverlay.getGraphics().add(areaGraphicDb);
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
                        for (int i = 0; i < identifyResultSize; i++) {
                            Graphic mGraphic = graphicList.get(i);
                            String taskIds = mGraphic.getAttributes().get("taskId").toString();
                            if (mGraphic.getAttributes() != null && taskIds != null && taskIds.length() > 0) {
                                DKBean dkBeanU = dkService.getDKBean(taskIds);
                                AlertDialog.Builder dialog = new AlertDialog.Builder(MassifShowActivity.this);
                                View view = LayoutInflater.from(MassifShowActivity.this).inflate(R.layout.layout_holds, null);
                                ListView listView = view.findViewById(R.id.show_lv_holds);
                                List<HouseHoldsBean> adapterList = new ArrayList<>();
                                for (int j = 0; j < holdsList.size(); j++) {
                                    HouseHoldsBean bean = holdsList.get(j);
                                    if (dkBeanU.getCustomerIds().contains(bean.getId())) {
                                        adapterList.add(bean);
                                    }
                                }
                                FinishHoldsAdapter holdsAdapter = new FinishHoldsAdapter(adapterList, MassifShowActivity.this);
                                listView.setAdapter(holdsAdapter);
                                dialog.setView(view);
                                dialog.setCancelable(true);
                                dialog.create().show();
                                break;
                            }
                        }
                    }
                } catch (InterruptedException | ExecutionException ie) {
                    ie.printStackTrace();
                }

            }
        });
    }

    private void searchForState(String searchString) {
        QueryParameters query = new QueryParameters();
        query.setWhereClause("xzdm like '%" + searchString+"%'");
        final ListenableFuture<FeatureQueryResult> future = mServiceFeatureTable.queryFeaturesAsync(query);
        future.addDoneListener(() -> {
            try {
                FeatureQueryResult result = future.get();
                Iterator<Feature> resultIterator = result.iterator();
                if (resultIterator.hasNext()) {
                    Feature feature = resultIterator.next();
                    Envelope envelope = feature.getGeometry().getExtent();
                    mMapView.setViewpointGeometryAsync(envelope, 10);
                } else {
                    Toast.makeText(this, "No states found with name: " + searchString, Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                String error = "Feature search failed for: " + searchString + ". Error: " + e.getMessage();
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void goBack(View view) {
        finish();
    }
}
