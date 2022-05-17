package com.example.lml.easyphoto.skp;

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
        degrees = String.valueOf(intPart);
        coord = mod * 60;
        mod = coord % 1;
        intPart = (int) coord;
        if (intPart < 0) {
            intPart *= -1;
        }

        minutes = String.valueOf(intPart);
        coord = mod * 60;
        intPart = (int) coord;
        if (intPart < 0) {
            intPart *= -1;
        }
        seconds = String.valueOf(intPart);
        output = degrees + "/1," + minutes + "/1," + seconds + "/1";

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
    public static void writeLatLonIntoJpeg(String picPath, double dLat, double dLon, String id) {
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
