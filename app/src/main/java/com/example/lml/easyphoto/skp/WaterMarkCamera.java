package com.example.lml.easyphoto.skp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import com.example.lml.easyphoto.R;
import com.example.lml.easyphoto.camera.TimestampTool;
import com.example.lml.easyphoto.util.SharePreferencesTools;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by VIC on 2016/5/7.
 */
public class WaterMarkCamera {

    // 静态变量
    public static int QUALITY = 100;
    private  Context context;

    public WaterMarkCamera(Context context) {
        this.context = context;
    }

    public long resizeImage(String origFile, String address, String lonLat) {
        String lon = lonLat.split(",")[0];
        String lat = lonLat.split(",")[1];
        lonLat = "经    度：" + lon + ",  纬    度：" + lat;
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

        Bitmap bitmapOrig = BitmapFactory.decodeFile(origFile);
        Matrix matrix = new Matrix();
        // 根据照片方向进行旋转
        if (exifOrient == ExifInterface.ORIENTATION_ROTATE_90){
            matrix.postRotate(90);
        }
        if (exifOrient == ExifInterface.ORIENTATION_ROTATE_180){
            matrix.postRotate(180);
        }
        if (exifOrient == ExifInterface.ORIENTATION_ROTATE_270){
            matrix.postRotate(-90);
        }
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmapOrig, 0, 0, bitmapOrig.getWidth(), bitmapOrig.getHeight(), matrix, true);
        resizedBitmap = createBitmapWater(resizedBitmap, address, lonLat);
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
            if (bitmapOrig != null && !bitmapOrig.isRecycled()) {
                // 回收并且置为null
                bitmapOrig.recycle();
                bitmapOrig = null;
            }
            if (resizedBitmap != null && !resizedBitmap.isRecycled()) {
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

    private Bitmap createBitmapWater(Bitmap src, String address, String lonLat) {
        String tag = "createBitmap";
        if (src == null) {
            return null;
        }
        int w = src.getWidth();
        int h = src.getHeight();
        // create the new blank bitmap
        Bitmap watermark = null;
        String flag_logo = SharePreferencesTools.getValue(context, "yipai", "logo_position", "true");
        int logoW = 0;
        if (w > h) {
            logoW = h / 5;
        } else {
            logoW = h / 5;
        }
        if (flag_logo.equals("false")) {
            boolean isSdCardExist = Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED);// 判断sdcard是否存在
            if (isSdCardExist) {
                String sdpath = Environment.getExternalStorageDirectory()
                        .getAbsolutePath();// 获取sdcard的根路径
                String filepath = sdpath + "/yipai/logo/logo.png";
                File file = new File(filepath);
                if (file.exists()) {
                    watermark = BitmapFactory.decodeFile(filepath);
                } else {
                    watermark = Bitmaptest.scaleImage(BitmapFactory.decodeResource(context.getResources(), R.drawable.znyg), logoW, logoW);
                }
            } else {
                watermark = Bitmaptest.scaleImage(BitmapFactory.decodeResource(context.getResources(), R.drawable.znyg), logoW, logoW);
            }
        } else {
            watermark = Bitmaptest.scaleImage(BitmapFactory.decodeResource(context.getResources(), R.drawable.znyg), logoW, logoW);
        }
        Bitmap newb = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
        Bitmap watermark2 = createWaterLocal(w, h, address, lonLat);
        int ht = watermark2.getHeight();
        Canvas cv = new Canvas(newb);
        cv.drawBitmap(src, 0, 0, null);// 在 0，0坐标开始画入src
        cv.drawBitmap(watermark2, 0, h - ht, null);//
        cv.save();// 保存
        cv.restore();// 存储
        if (watermark2 != null && !watermark2.isRecycled()) {
            // 回收并且置为null
            watermark2.recycle();
            watermark2 = null;
        }
        return createWaterMaskBitmap(context,newb, watermark,
                newb.getWidth() - watermark.getWidth(),
                0);
    }

    public Bitmap createWaterLocal(int w, int h, String address, String lonLat) {
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
            shuiyin += "时    间：" + TimestampTool.getCurrentDateTime() + "\n";
            number += 1;
            shuiyin += address + "\n";
            number += 1;
            shuiyin += lonLat;
            number += 1;
        StaticLayout sl = new StaticLayout(shuiyin, textPaint, w - 8, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        canvas.translate(0, h - (w / 27 * 3 / 2 * number));
        sl.draw(canvas);
        return newBitmap;
    }

    private static Bitmap createWaterMaskBitmap(Context context,Bitmap src, Bitmap watermark, int paddingLeft, int paddingTop) {
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
        String logo = "false"/*SPTools.getValue(context, "yipai", "logo", "true")*/;
        if (logo.equals("true")) {
            canvas.drawBitmap(watermark, paddingLeft, paddingTop, null);
        }
        // 保存
        canvas.save();
        // 存储
        canvas.restore();
        if (src != null && !src.isRecycled()) {
            // 回收并且置为null
            src.recycle();
            src = null;
        }
        if (watermark != null && !watermark.isRecycled()) {
            // 回收并且置为null
            watermark.recycle();
            watermark = null;
        }
        return newb;
    }

    /**
     * dip转pix
     *
     * @param context
     * @param dp
     * @return
     */
    public static int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
