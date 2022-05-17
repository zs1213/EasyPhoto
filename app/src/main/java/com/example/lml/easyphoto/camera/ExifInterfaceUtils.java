package com.example.lml.easyphoto.camera;

import android.media.ExifInterface;

import java.io.File;

/**
 * Created by 陈忠磊 on 2017/8/23.
 */

public class ExifInterfaceUtils {
    /**
     * 浮点型经纬度值转成度分秒格式
     *
     * @param coord
     * @return
     */
    public static String decimalToDMS(double coord) {
        String output, degrees, minutes, seconds;

        double mod = coord % 1;
        int intPart = (int) coord;

        // set degrees to the value of intPart
        // e.g. degrees := "-79"

        degrees = String.valueOf(intPart);

        // next times the MOD1 of degrees by 60 so we can find the integer part
        // for minutes.
        // get the MOD1 of the new coord to find the numbers after the decimal
        // point.
        // e.g. coord := 0.982195 * 60 == 58.9317
        // mod := 58.9317 % 1 == 0.9317
        //
        // next get the value of the integer part of the coord.
        // e.g. intPart := 58

        coord = mod * 60;
        mod = coord % 1;
        intPart = (int) coord;
        if (intPart < 0) {
            // Convert number to positive if it's negative.
            intPart *= -1;
        }

        // set minutes to the value of intPart.
        // e.g. minutes = "58"
        minutes = String.valueOf(intPart);

        // do the same again for minutes
        // e.g. coord := 0.9317 * 60 == 55.902
        // e.g. intPart := 55
        coord = mod * 60;
        intPart = (int) coord;
        if (intPart < 0) {
            // Convert number to positive if it's negative.
            intPart *= -1;
        }

        // set seconds to the value of intPart.
        // e.g. seconds = "55"
        seconds = String.valueOf(intPart);

        // I used this format for android but you can change it
        // to return in whatever format you like
        // e.g. output = "-79/1,58/1,56/1"
        output = degrees + "/1," + minutes + "/1," + seconds + "/1";

        // Standard output of D°M′S″
        // output = degrees + "°" + minutes + "'" + seconds + "\"";

        return output;
    }

    /**
     * 将经纬度信息写入JPEG图片文件里
     *
     * @param picPath
     *            JPEG图片文件路径
     * @param dLat
     *            纬度
     * @param dLon
     *            经度
     */
    public static void writeLatLonIntoJpeg(String picPath, double dLat, double dLon,String id) {
        File file = new File(picPath);
        if (file.exists()) {
            try {
                ExifInterface exif = new ExifInterface(picPath);
                String tagLat = exif
                        .getAttribute(ExifInterface.TAG_GPS_LATITUDE);
                String tagLon = exif
                        .getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
                if (tagLat == null && tagLon == null) // 无经纬度信息
                {
                    exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE,
                            decimalToDMS(dLat));
                    exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF,
                            dLat > 0 ? "N" : "S"); // 区分南北半球
                    exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE,
                            decimalToDMS(dLon));
                    exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF,
                            dLon > 0 ? "E" : "W"); // 区分东经西经
                    exif.setAttribute(ExifInterface.TAG_DATETIME,
                            id); // 区分东经西经

                    exif.saveAttributes();
                }
            } catch (Exception e) {

            }
        }
    }
}
