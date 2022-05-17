/*
package com.example.lml.easyphoto.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.view.MotionEvent;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapOnTouchListener;
import com.esri.android.map.MapView;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Line;
import com.esri.core.geometry.MultiPath;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.TextSymbol;
import com.example.lml.easyphoto.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

*/
/**
 * Created by 陈忠磊 on 2017/8/21.
 *//*

public class CameraMapTouchListener extends MapOnTouchListener {
    LonAndLat lonAndLat;
    private Context context;
    private MapView map;
    private GraphicsLayer drawLayer;
    //放大镜声明开始
    private MagnifierView mag; // 放大镜
    private boolean redrawCache = true; // 是否绘制
    private boolean showmag = false;
    public int MSG_CHOOSEPOINT = 0x01;
    PictureMarkerSymbol mBlackMarkerSymbol;
    //放大镜声明结束
    private boolean flag = true;
    public CameraMapTouchListener(Context context, MapView map) {
        super(context, map);
        this.context = context;
        this.map = map;
        mBlackMarkerSymbol = new PictureMarkerSymbol(context.getResources().getDrawable(R.mipmap.arcgis_point_icon));
    }

    //设置修改图层
    public void setDrawLayer(GraphicsLayer drawLayer) {
        this.drawLayer = drawLayer;
    }

    public void setLonAndLat(LonAndLat lonAndLat) {
        this.lonAndLat = lonAndLat;
    }

    @Override
    public boolean onSingleTap(MotionEvent e) {
        if (flag){
            Point point = map.toMapPoint(new Point(e.getX(), e.getY()));
            drawPolylineOrPolygon(point);
        }

        return true;
    }

    @Override
    public void onLongPress(MotionEvent point) {
        super.onLongPress(point);
        if (flag) {
            magnify(point);
            this.showmag = true;
        }
    }

    private void drawPolylineOrPolygon(Point point) {
        if (drawLayer == null) {
            drawLayer = new GraphicsLayer();
            map.addLayer(drawLayer);
        }
            drawVertices(point);
        sendLonLat(point);
    }

    private void sendLonLat(Point point) {//传递经纬度
            DecimalFormat df = new DecimalFormat("#.000000");
            Point p = (Point) GeometryEngine.project(point, map.getSpatialReference(), SpatialReference.create(4326));
            lonAndLat.getLonLat(df.format(p.getX()), df.format(p.getY()));
    }

    //画点和点的坐标
    private void drawVertices(Point point) {
        drawLayer.removeAll();
        Graphic graphic = new Graphic(point, mBlackMarkerSymbol);
        int a = drawLayer.addGraphic(graphic);
        DecimalFormat df = new DecimalFormat("#.000000");
        Point p = (Point) GeometryEngine.project(point, map.getSpatialReference(), SpatialReference.create(4326));

        PictureMarkerSymbol markerSymbola = new PictureMarkerSymbol( createMapBitMap(df.format(p.getX()) + "-" + df.format(p.getY())));
        Graphic g = new Graphic(point, markerSymbola);
        int index2 = drawLayer.addGraphic(g);
    }
    */
/**
     * 文字转换BitMap
     * @param text
     * @return
     *//*

    public static Drawable createMapBitMap(String text) {
        String lonlat[] = text.split("-");
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(30);
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.LEFT);//位置


        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        Bitmap bmpGrayscale = Bitmap.createBitmap((int) paint.measureText(lonlat[0]), 80,
                Bitmap.Config.ARGB_8888);
        Canvas cv = new Canvas(bmpGrayscale);
        cv.drawColor(Color.parseColor("#00000000"));
        cv.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));

        for(int i=0;i<lonlat.length;i++){
            switch (i){
                case 0:
                    cv.drawText(lonlat[i+1]+"",10,70,paint);
                    break;
                case 1:
                    cv.drawText(lonlat[i-1]+"",0,30,paint);
                    break;
            }
        }


        cv.save();// 保存
        cv.restore();// 存储
        return new BitmapDrawable(bmpGrayscale);
    }

    public void setMotionTouch(boolean isMotionTouch) {
        this.flag = isMotionTouch;
    }

    */
/**
     * 放大操作
     *
     * @param paramMotionEvent 触屏事件
     *//*

    private void magnify(MotionEvent paramMotionEvent) {
        if (this.mag == null) {
            this.mag = new MagnifierView(this.context, this.map);
            map.addView(this.mag);
            this.mag.prepareDrawingCacheAt(paramMotionEvent.getX(),
                    paramMotionEvent.getY());
        } else {
            this.redrawCache = false;
            this.mag.prepareDrawingCacheAt(paramMotionEvent.getX(),
                    paramMotionEvent.getY());
        }
    }

    */
/**
     * 当拖动时是否显示放大镜
     *//*

    @Override
    public boolean onDragPointerMove(MotionEvent paramMotionEvent1,
                                     MotionEvent paramMotionEvent2) {
        if (this.showmag) {
            magnify(paramMotionEvent2);
            return true;
        }
        return super.onDragPointerMove(paramMotionEvent1, paramMotionEvent2);
    }

    */
/**
     * 触点松开事件
     *//*

    public boolean onDragPointerUp(MotionEvent paramMotionEvent1,
                                   MotionEvent paramMotionEvent2) {
        if (this.showmag) {
            if (this.mag != null)
                this.mag.hide();
            this.mag.postInvalidate();
            this.showmag = false;
            this.redrawCache = true;
            onSingleTap(paramMotionEvent2);
            return true;
        }
        return super.onDragPointerUp(paramMotionEvent1, paramMotionEvent2);
    }

    public interface LonAndLat {
        void getLonLat(String lon, String lat);
    }
}
*/
