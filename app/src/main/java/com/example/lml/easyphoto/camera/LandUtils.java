
package com.example.lml.easyphoto.camera;


import android.util.Log;
import android.widget.Toast;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.layers.FeatureLayer;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class LandUtils {

/**
     * @param val
     * @return
     */

    public static String convertPlantType(String val) {
        String s = val;
        switch (val) {
            case "3101":
                s = "水稻种植保险";
                break;
            case "3103":
                s = "水稻制种种植保险";
                break;
            case "3104":
                s = "马铃薯种植保险";
                break;
            case "3105":
                s = "香蕉树风灾保险";
                break;
            case "3106":
                s = "甘蔗种植保险";
                break;
            case "3107":
                s = "小麦种植保险";
                break;
            case "3108":
                s = "油菜种植保险";
                break;
            case "3109":
                s = "芒果种植保险";
                break;
            case "3110":
                s = "橡胶树风灾指数保险";
                break;
            case "3111":
                s = "橡胶树风灾保险";
                break;
            case "3112":
                s = "高梁种植保险";
                break;
            case "3114":
                s = "玉米种植保险";
                break;
            case "3125":
                s = "烟叶种植保险";
                break;
            case "3126":
                s = "大豆种植保险";
                break;
            case "3140":
                s = "葵花籽种植保险";
                break;
            case "3141":
                s = "大棚及瓜菜种植保险";
                break;
            case "3157":
                s = "花生种植保险";
                break;
        }
        return s;
    }


/**
     * @return
     */

//    public static LandInfo findLand(FeatureLayer featureLayer, Point point) {
//        LandInfo land = null;
//        try {
//            QueryParameters parameters = new QueryParameters();
//            Point mapPoint = (Point) GeometryEngine.project(point, SpatialReference.create(32651));
//            parameters.setGeometry(mapPoint);
//            Future<FeatureResult> resultFuture = featureLayer.selectFeatures(parameters, FeatureLayer.SelectionMode.NEW, callback);
//            FeatureResult results = null;//最关键  得到结果
//            results = resultFuture.get();
//            if (results != null && results.featureCount() > 0) {
//                for (Object element : results) {//得到每个要素
//                    if (element instanceof Feature) {
//                        land = new LandInfo();
//                        Feature feature = (Feature) element;
//                        if (feature.getAttributes().get("Province").toString()!=null){
//                            land.setProvince(feature.getAttributes().get("Province").toString());
//                        }
//                        if (feature.getAttributes().get("City").toString()!=null){
//                            land.setCity(feature.getAttributes().get("City").toString());
//                        }
//                        if (feature.getAttributes().get("County").toString()!=null){
//                            land.setCounty(feature.getAttributes().get("County").toString());
//                        }
//                        if (feature.getAttributes().get("Country").toString()!=null){
//                            land.setCountry(feature.getAttributes().get("Country").toString());
//                        }
//                        if (feature.getAttributes().get("Village").toString()!=null){
//                            land.setVillage(feature.getAttributes().get("Village").toString());
//                        }
//                        if (feature.getAttributes().get("GroudName").toString()!=null){
//                            land.setGroudName(feature.getAttributes().get("GroudName").toString());
//                        }
//                        land.setName(feature.getAttributes().get("name").toString());
//                        land.setIdCard(feature.getAttributes().get("idcard").toString());
//                        land.setDkbm(feature.getAttributes().get("dkbm").toString());
//                        land.setAreas(Float.parseFloat(feature.getAttributes().get("areas").toString()));
//                        land.setPlantType(convertPlantType(feature.getAttributes().get("planttype").toString()));
//                    }
//                    break;
//                }
//            }
//        } catch (Exception ex) {
//        }
//        return land;
//    }

//public static void searchForState(ShapefileFeatureTable mServiceFeatureTable, final String searchString) {
//    // clear any previous selections
//    // create objects required to do a selection with a query
//    QueryParameters query = new QueryParameters();
//    // make search case insensitive
//    query.setWhereClause("xzdm LIKE '%220722204211%'");
//    // call select features
//    final ListenableFuture<FeatureQueryResult> future = mServiceFeatureTable.queryFeaturesAsync(query);
//    // add done loading listener to fire when the selection returns
//    future.addDoneListener(() -> {
//        try {
//            // call get on the future to get the result
//            FeatureQueryResult result = future.get();
//            // check there are some results
//            Iterator<Feature> resultIterator = result.iterator();
//            if (resultIterator.hasNext()) {
//                // get the extent of the first feature in the result to zoom to
//                Feature feature = resultIterator.next();
//                Envelope envelope = feature.getGeometry().getExtent();
////                mMapView.setViewpointGeometryAsync(envelope, 10);
//                // select the feature
//            } else {
//            }
//        } catch (Exception e) {
//            String error = "Feature search failed for: " + searchString + ". Error: " + e.getMessage();
//            Log.e("123456", error);
//        }
//    });
//}
    public static void  findVillage(FeatureLayer featureLayer, Point point) {

        QueryParameters query = new QueryParameters();
        query.setGeometry(point);// 设置空间几何对象
        FeatureTable mTable = featureLayer.getFeatureTable();//得到查询属性表
        final ListenableFuture<FeatureQueryResult> featureQueryResult
                = mTable.queryFeaturesAsync(query);
        featureQueryResult.addDoneListener(new Runnable() {
            @Override
            public void run() {
                try {
                    FeatureQueryResult featureResul = featureQueryResult.get();
                    for (Object element : featureResul) {
                        if (element instanceof Feature) {
                            Feature mFeatureGrafic = (Feature) element;
                            Map<String, Object> mQuerryString = mFeatureGrafic.getAttributes();
                            for(String key : mQuerryString.keySet()){
                                Log.i("==============="+key,String.valueOf(mQuerryString.get(key)));
                                VillageInfo village = new VillageInfo();
                                village.setProvince(String.valueOf(mQuerryString.get("province"))/*feature.getAttributes().get("province").toString()*/);
                                village.setCity(String.valueOf(mQuerryString.get("city"))/*feature.getAttributes().get("city").toString()*/);
                                village.setCounty(String.valueOf(mQuerryString.get("county"))/*feature.getAttributes().get("county").toString()*/);
                                village.setCountry(String.valueOf(mQuerryString.get("town"))/*feature.getAttributes().get("town").toString()*/);
                                village.setVillage(String.valueOf(mQuerryString.get("village"))/*feature.getAttributes().get("village").toString()*/);
                            }
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });






//        QueryParameters parameters = new QueryParameters();
//        Point mapPoint = (Point) GeometryEngine.project(point, SpatialReference.create(4326), SpatialReference.create(32651));
//        parameters.setGeometry(mapPoint);
//        CallbackListener<FeatureResult> callback = new CallbackListener<FeatureResult>() {
//            @Override
//            public void onError(Throwable e) {
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onCallback(FeatureResult featureIterator) {
//            }
//        };
//
//        Future<FeatureResult> resultFuture = featureLayer.selectFeatures(parameters, FeatureLayer.SelectionMode.NEW, callback);
//        FeatureResult results = null;//最关键  得到结果
//        try {
//            results = resultFuture.get();
//            if (results != null) {
//                int size = (int) results.featureCount();
//                int i = 0;
//                for (Object element : results) {//得到每个要素
//                    if (element instanceof Feature) {
//                        Feature feature = (Feature) element;
//                        village = new VillageInfo();
//                        village.setProvince(feature.getAttributes().get("province").toString());
//                        village.setCity(feature.getAttributes().get("city").toString());
//                        village.setCounty(feature.getAttributes().get("county").toString());
//                        village.setCountry(feature.getAttributes().get("town").toString());
//                        village.setVillage(feature.getAttributes().get("village").toString());
////                        AlertDialog.Builder dialog = new AlertDialog.Builder(PhotoMain.this);
////                        dialog.setTitle("提示");
////                        View view = LayoutInflater.from(PhotoMain.this).inflate(R.layout.xingzheng,null);
////                        final EditText et_sheng = (EditText) view.findViewById(R.id.xz_et_sheng);
////                        final EditText et_shi = (EditText) view.findViewById(R.id.xz_et_shi);
////                        final EditText et_xian = (EditText) view.findViewById(R.id.xz_et_xian);
////                        final EditText et_xiang = (EditText) view.findViewById(R.id.xz_et_xiang);
////                        final EditText et_cun = (EditText) view.findViewById(R.id.xz_et_cun);
////                        et_sheng.setText(province);
////                        et_shi.setText(city);
////                        et_xian.setText(county);
////                        et_xiang.setText(country);
////                        et_cun.setText(village);
////                        dialog.setView(view);
////                        address = province + city + county + country + village;
////                        dialog.setNeutralButton("确定", new DialogInterface.OnClickListener() {
////                            @Override
////                            public void onClick(DialogInterface dialogInterface, int i) {
////                                province = et_sheng.getText().toString();
////                                city = et_shi.getText().toString();
////                                county = et_xian.getText().toString();
////                                country = et_xiang.getText().toString();
////                                village = et_cun.getText().toString();
////                                address = province + city + county + country + village;
////                            }
////                        });
////                        dialog.setCancelable(false);
////                        dialog.create().show();
//                    }
//                    break;
//                }
//            }
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//
//        }
    }


/**
     * @param feature
     * @return
     */

    public static VillageInfo parseVillage(Feature feature) {
        VillageInfo vi = null;
        try {
            String province = "" + feature.getAttributes().get("province");
            String city = "" + feature.getAttributes().get("city");
            String county = "" + feature.getAttributes().get("county");
            String country = "" + feature.getAttributes().get("town");
            String village = "" + feature.getAttributes().get("village");
            vi = new VillageInfo(province, city, county, country, village);
        } catch (Exception ex) {

        }
        return vi;
    }
}

