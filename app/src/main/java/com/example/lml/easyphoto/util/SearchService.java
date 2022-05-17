package com.example.lml.easyphoto.util;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.example.lml.easyphoto.R;
import com.example.lml.easyphoto.camera.PhotoMain;
import com.example.lml.easyphoto.okHttpUtils.DOkHttp;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import okhttp3.Request;

public class SearchService {
    private Handler mainHanlder;

    private SearchService() {
        mainHanlder = new Handler(Looper.getMainLooper());
    }

    private static class SearchServiceHolder {
        public static SearchService mInstance = new SearchService();

    }

    public static SearchService getInstance() {
        return SearchServiceHolder.mInstance;
    }

    public interface MapCallBack {
        void onFailure(Exception e);
        void onResponse(Map<String, Object> dataMap);
    }
    public void getMessage4Server(FeatureLayer featureLayer, Point clickPoint, MapView mMapView, final MapCallBack myCallBack) {

        int tolerance = 1;
        double mapTolerance = tolerance * mMapView.getUnitsPerDensityIndependentPixel();
        SpatialReference spatialReference = mMapView.getSpatialReference();
        Envelope envelope = new Envelope(clickPoint.getX() - mapTolerance, clickPoint.getY() - mapTolerance,
                clickPoint.getX() + mapTolerance, clickPoint.getY() + mapTolerance, spatialReference);
        QueryParameters query = new QueryParameters();
        query.setGeometry(envelope);
        query.setSpatialRelationship(QueryParameters.SpatialRelationship.WITHIN);
        final ListenableFuture<FeatureQueryResult> future = featureLayer.selectFeaturesAsync(query, FeatureLayer.SelectionMode.NEW);
        future.addDoneListener(new Runnable() {
            @Override
            public void run() {
                try {
                    FeatureQueryResult result = future.get();
                    //mFeatureLayer.getFeatureTable().deleteFeaturesAsync(result);
                    Iterator<Feature> iterator = result.iterator();
                    int counter = 0;
                    if (iterator.hasNext()){
                        while (iterator.hasNext()) {
                            counter++;
                            Feature feature = iterator.next();
                            Map<String, Object> attributes = feature.getAttributes();
                            mainHanlder.post(new Runnable() {
                                @Override
                                public void run() {
                                    myCallBack.onResponse(attributes);
                                }
                            });
                            return;

                        }
                    }else {
                        mainHanlder.post(new Runnable() {
                            @Override
                            public void run() {
                                myCallBack.onFailure(null);
                            }
                        });
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });}
    public void getMap4Server(ServiceFeatureTable featureTable, Point pointGps, final MapCallBack myCallBack) {
        QueryParameters query = new QueryParameters();
        query.setGeometry(pointGps);
        query.setReturnGeometry(true);
        final ListenableFuture<FeatureQueryResult> featureQueryResult = featureTable.queryFeaturesAsync(query,ServiceFeatureTable.QueryFeatureFields.LOAD_ALL);
        featureQueryResult.addDoneListener(new Runnable() {
            @Override
            public void run() {
                try {
                    FeatureQueryResult result = featureQueryResult.get();
                    Iterator<Feature> iterator = result.iterator();
                    if (iterator.hasNext()){
                        while (iterator.hasNext()) {
                            Feature feature = iterator.next();
                            Map<String, Object> attributes = feature.getAttributes();
                            mainHanlder.post(new Runnable() {
                                @Override
                                public void run() {
                                    myCallBack.onResponse(attributes);
                                }
                            });
                        }
                    }else {
                        mainHanlder.post(new Runnable() {
                            @Override
                            public void run() {
                                myCallBack.onFailure(null);
                            }
                        });
                    }

                } catch (Exception e1) {
                    e1.printStackTrace();
                    mainHanlder.post(new Runnable() {
                        @Override
                        public void run() {
                            myCallBack.onFailure(e1);
                        }
                    });
                }
            }
        });
    }
}
