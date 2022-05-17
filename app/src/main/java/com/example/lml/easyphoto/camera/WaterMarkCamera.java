package com.example.lml.easyphoto.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import com.example.lml.easyphoto.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;

/**
 * Created by VIC on 2016/5/7.
 */
public class WaterMarkCamera {

    // 静态变量
    public static int MAX_DIM = 1024;
    public static int QUALITY = 80;
    private static int kuan;
    private Context context;
    public WaterMarkCamera(Context context){
        this.context = context;
    }

    public long resizeImage(String zuowu,String mt,String zaihai,String sunshi,String beizhu,String origFile, String address,String lonLat) {
        String lon = lonLat.split(",")[0];
        String lat = lonLat.split(",")[1];
        lonLat = "经度：" + lon + ",  纬度：" + lat;
        String exifDateTime = "";
        int exifOrient = -1;
        String exifMODEL = "";
        String exifMAKE = "";
        ExifInterface exif = null;
        try {
            // 获取原始相机拍照文件的Exif信息
            exif = new ExifInterface(origFile);
            exifOrient = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
            exifDateTime = exif.getAttribute(ExifInterface.TAG_DATETIME);
            exifMODEL = exif.getAttribute(ExifInterface.TAG_MODEL);
            exifMAKE = exif.getAttribute(ExifInterface.TAG_MAKE);
        } catch (Exception ex) {
        }

        int sampleSize = 1;
        // 是否缩放标志
        boolean resizeFlag = true;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(origFile, opts);
        // 判读是否需要缩放
        if (opts.outWidth <= MAX_DIM && opts.outHeight <= MAX_DIM) {
            resizeFlag = false;
        } else {
            if (opts.outWidth > opts.outHeight) {
                sampleSize = opts.outWidth / MAX_DIM;
            } else {
                sampleSize = opts.outHeight / MAX_DIM;
            }
            if (sampleSize <= 0)
                sampleSize = 1;
        }
        opts.inJustDecodeBounds = false;
        opts.inSampleSize = sampleSize;
        Bitmap bitmapOrig = BitmapFactory.decodeFile(origFile, opts);
        int tmpWidth = bitmapOrig.getWidth();
        int tmpHeight = bitmapOrig.getHeight();
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        if (resizeFlag) {
            float scale = 0;
            if (tmpWidth > tmpHeight)
                scale = (float) MAX_DIM / tmpWidth;
            else
                scale = (float) MAX_DIM / tmpHeight;
            // resize the Bitmap
            matrix.postScale(scale, scale);
        }

        // 根据照片方向进行旋转
        if (exifOrient == ExifInterface.ORIENTATION_ROTATE_90)
            matrix.postRotate(90);
        if (exifOrient == ExifInterface.ORIENTATION_ROTATE_180)
            matrix.postRotate(180);
        if (exifOrient == ExifInterface.ORIENTATION_ROTATE_270)
            matrix.postRotate(-90);
        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmapOrig, 0, 0, tmpWidth, tmpHeight, matrix, true);

        resizedBitmap = createBitmapWater(resizedBitmap, zuowu,mt, zaihai, sunshi, beizhu,address,lonLat);

        File resizedFile = new File(origFile);
        FileOutputStream out;
        try {
            out = new FileOutputStream(resizedFile);
            if (resizedBitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY, out)) {
                out.flush();
                out.close();
            }
            // 修改Resize后图片文件exif信息
            exif = new ExifInterface(origFile);
            exif.setAttribute(ExifInterface.TAG_DATETIME, exifDateTime);
            exif.setAttribute(ExifInterface.TAG_MODEL, exifMODEL);
            exif.setAttribute(ExifInterface.TAG_MAKE, exifMAKE);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(bitmapOrig != null && !bitmapOrig.isRecycled()){
                // 回收并且置为null
                bitmapOrig.recycle();
                bitmapOrig = null;
            }
            if(resizedBitmap != null && !resizedBitmap.isRecycled()){
                // 回收并且置为null
                resizedBitmap.recycle();
                resizedBitmap = null;
            }
            try {
                exif.saveAttributes();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return resizedFile.length() / 1024;
    }
    private Bitmap createBitmapWater(Bitmap src,String zuowu,String mt,String zaihai,String sunshi,String beizhu,String address,String lonLat) {
        String tag = "createBitmap";
        if (src == null) {
            return null;
        }
        int w = src.getWidth();
        int h = src.getHeight();
        // create the new blank bitmap
        Bitmap watermark = BitmapFactory.decodeResource(context.getResources(),C.PHOTO_LOGO);
//        kuan = watermark.getWidth();
        kuan = 0;
        Bitmap newb = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
        Bitmap watermark2 = createWaterLocal(w,h, zuowu, mt,zaihai, sunshi, beizhu,address,lonLat);
        int ht = watermark2.getHeight();
        Canvas cv = new Canvas(newb);
        cv.drawBitmap(src, 0, 0, null);// 在 0，0坐标开始画入src
        cv.drawBitmap(watermark2, 0, h-ht, null);// 在src的右下角画入水印
        cv.save();// 保存
        cv.restore();// 存储
        if(watermark2 != null && !watermark2.isRecycled()){
            // 回收并且置为null
            watermark2.recycle();
            watermark2 = null;
        }
        return createWaterMaskBitmap( newb, watermark,
                0,
                newb.getHeight()-watermark.getHeight());
    }
    public Bitmap createWaterLocal(int w,int h,String zuowu,String mt,String zaihai,String sunshi,String beizhu,String address,String lonLat) {
//        kuan = 0;
        int number = 0;
        Bitmap newBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        TextPaint textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        if (w > h) {
            textPaint.setTextSize(h / 27);
        } else {
            textPaint.setTextSize(w / 27);
        }
        textPaint.setColor(context.getResources().getColor(R.color.white));
        if (address.contains("委会")) {
            address = address.replace("委会", "");
        }

        String shuiyin = "";
        shuiyin += zuowu + "\n";
        number += 1;
        shuiyin += mt + "\n";
        number += 1;
        shuiyin += zaihai + "\n";
        number += 1;
        shuiyin += sunshi + "\n";
        number += 1;

        shuiyin += "时    间：" + TimestampTool.getCurrentDateTime() + "\n";
        number += 1;
        shuiyin += address + "\n";
        number += 1;
        if (beizhu.length()>0){
            shuiyin += lonLat+ "\n";
            number += 1;
            shuiyin += beizhu;
            number += 1;
        }else {
            shuiyin += lonLat;
            number += 1;
        }

        StaticLayout sl = new StaticLayout(shuiyin, textPaint, w - 8, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        canvas.translate(0, h - (w / 27 * 3 / 2 * number));
        sl.draw(canvas);
        return newBitmap;
    }

    private static Bitmap createWaterMaskBitmap(Bitmap src, Bitmap watermark,int paddingLeft, int paddingTop) {
        if (src == null) {
            return null;
        }
        int width = src.getWidth();
        int height = src.getHeight();

        //创建一个bitmap
        Bitmap newb = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
        //将该图片作为画布
        Canvas canvas = new Canvas(newb);
        //在画布 0，0坐标上开始绘制原始图片
        canvas.drawBitmap(src, 0, 0, null);
        //在画布上绘制水印图片
//        canvas.drawBitmap(watermark, paddingLeft, paddingTop, null);
        // 保存
        canvas.save();
        // 存储
        canvas.restore();
        if(src != null && !src.isRecycled()){
            // 回收并且置为null
            src.recycle();
            src = null;
        }
        if(watermark != null && !watermark.isRecycled()){
            // 回收并且置为null
            watermark.recycle();
            watermark = null;
        }
        return newb;
    }
    /**
     * dip转pix
     * @param context
     * @param dp
     * @return
     */
    public static int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
