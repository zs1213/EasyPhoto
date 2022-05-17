package com.example.lml.easyphoto.tongji;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.ArcGISMapImageLayer;
import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.TextSymbol;
import com.example.lml.easyphoto.R;
import com.example.lml.easyphoto.camera.CameraPhotoBean;
import com.example.lml.easyphoto.camera.CameraService;
import com.example.lml.easyphoto.camera.PhotoMain;
import com.example.lml.easyphoto.dikuai.DKPointBean;
import com.example.lml.easyphoto.skp.ImagePagerActivity;
import com.example.lml.easyphoto.util.Configure;
import com.example.lml.easyphoto.util.SharePreferencesTools;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DkLookActivity extends AppCompatActivity {
    private MapView mMapView;
    private CameraService service;
    private String gisCode = "";
    private String serverPath = "";
    private String gisPath = "";
    private GraphicsOverlay mGraphicsOverlay;
    private ServiceFeatureTable featureTable;
    private ArcGISMap map;
    private ArcGISMapImageLayer mainArcGISVectorTiledLayer;
    private String theOnlineTiledLayers = Configure.gisXZQYUrl;
    private List<Map<String,String>> guaidianList;
    private List<Map<String,String>> dianList;
    private String folderName;
    private String cropName;
    private List<CameraPhotoBean> cameraPhotoBeans;
    private Button btn_photo;
    private Button btn_choose;
    private SimpleMarkerSymbol markerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.parseColor("#000000"), 10);
    private int chooseFlag = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dk_look);
        mMapView = findViewById(R.id.dklook_map);
        btn_photo = findViewById(R.id.dklook_btn);
        btn_choose = findViewById(R.id.dklook_btn_choose);
        service = new CameraService(this);
        folderName = getIntent().getStringExtra("folderName");
        cropName = getIntent().getStringExtra("crop");
        cameraPhotoBeans  =service.getPhotoByFilesName(folderName,cropName);
        gisCode = cameraPhotoBeans.get(0).getGisCode();
        serverPath = cameraPhotoBeans.get(0).getServerPath();
        gisPath = cameraPhotoBeans.get(0).getGisPath();
        if (serverPath.contains("http")){
            btn_choose.setVisibility(View.VISIBLE);
            ArcGISMapImageLayer webTiledLayer = new ArcGISMapImageLayer(gisPath);
            map = new ArcGISMap(new Basemap(webTiledLayer));
            mMapView.setMap(map);
            mGraphicsOverlay = new GraphicsOverlay();
            mainArcGISVectorTiledLayer = new ArcGISMapImageLayer(theOnlineTiledLayers);
            map.getOperationalLayers().add(new ArcGISMapImageLayer(serverPath));
            map.getOperationalLayers().add(mainArcGISVectorTiledLayer);
            mMapView.getGraphicsOverlays().add(mGraphicsOverlay);
            featureTable = new ServiceFeatureTable(Configure.gisXZQYCX);
            searchForState(gisCode);
        }else {
            btn_choose.setVisibility(View.GONE);
            ArcGISTiledLayer webTiledLayer = new ArcGISTiledLayer(serverPath);
            map = new ArcGISMap(new Basemap(webTiledLayer));
            mMapView.setMap(map);
            mGraphicsOverlay = new GraphicsOverlay();
            mMapView.getGraphicsOverlays().add(mGraphicsOverlay);
        }

        guaidianList = service.getGuaiDianListByFolderName(folderName,cropName);

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
            Point point = polygonDbGraphic.getGeometry().getExtent().getCenter();
            TextSymbol textSymbol = new TextSymbol(15, areaDb+"亩", Color.parseColor("#ffffff"), TextSymbol.HorizontalAlignment.CENTER, TextSymbol.VerticalAlignment.MIDDLE);
            Graphic areaGraphicDb = new Graphic(point, textSymbol);
            areaGraphicDb.setZIndex(999);
            mGraphicsOverlay.getGraphics().add(areaGraphicDb);
        }
        dianList = service.getDianListByFolderName(folderName,cropName);
        for (int i = 0; i < dianList.size(); i++) {
            Map<String,String> stringMap = dianList.get(i);
            String gislon = stringMap.get("lon");
            String gislat = stringMap.get("lat");
            Point wgsPoint = new Point(Double.parseDouble(gislon), Double.parseDouble(gislat));
            Point mapPoint = (Point) GeometryEngine.project(wgsPoint, SpatialReferences.getWgs84());
            Graphic graphic = new Graphic(mapPoint, markerSymbol);
            graphic.setZIndex(999);
            mGraphicsOverlay.getGraphics().add(graphic);
        }
        btn_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cameraPhotoBeans.size() > 0) {
                    Intent intent = new Intent(DkLookActivity.this, ImagePagerActivity.class);
                    // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
                    intent.putExtra("image_urls", new Gson().toJson(cameraPhotoBeans));
                    intent.putExtra("image_index", 0);
                    intent.putExtra("from", "dkLook");
                    startActivity(intent);
                }else {
                    Toast.makeText(DkLookActivity.this, "没有可预览照片", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (chooseFlag==0){
                    chooseFlag =1;
                    map.getBasemap().getBaseLayers().get(0).setVisible(false);
                    map.getOperationalLayers().get(0).setVisible(true);
                }else if (chooseFlag==1){
                    chooseFlag =2;
                    map.getBasemap().getBaseLayers().get(0).setVisible(true);
                    map.getOperationalLayers().get(0).setVisible(false);
                }else {
                    chooseFlag =0;
                    map.getBasemap().getBaseLayers().get(0).setVisible(true);
                    map.getOperationalLayers().get(0).setVisible(true);
                }
            }
        });
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
    public void goBack(View view) {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraPhotoBeans  =service.getPhotoByFilesName(folderName,cropName);
        if (cameraPhotoBeans.size()==0){
            finish();
        }
    }
}
