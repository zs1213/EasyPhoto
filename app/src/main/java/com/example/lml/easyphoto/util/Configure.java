package com.example.lml.easyphoto.util;

/**
 * Created by znyguser on 2016/7/27.
 */
public class Configure {
    /* 测试环境 */
//    public static String ip = "http://192.168.0.162:8888/";
    /* 正式环境1 */
//    public static String ip = "http://39.102.34.133:8888/";
    /* 正式环境2 */
    public static String ip = "http://121.89.204.139:8888/";
    /* 学习环境 */
//    public static String ip = "http://111.26.161.196:18888/";
//    public static String gisXZQYCX ="http://39.105.197.211:6080/arcgis/rest/services/vector/2021MapLayers/MapServer/3";//行政区域查询
//    public static String gisXZQYUrl = "http://39.105.197.211:6080/arcgis/rest/services/vector/2021MapLayers/MapServer";//行政区域
//    public static String gisXZQYCX ="http://111.26.39.91:8002/arcgis/rest/services/JiLin/JinLin_xingzhengbianjie/MapServer/3";//行政区域查询
//    public static String gisXZQYUrl = "http://111.26.39.91:8002/arcgis/rest/services/JiLin/JinLin_xingzhengbianjie/MapServer";//行政区域
    public static String gisXZQYCX ="http://39.105.197.211:6080/arcgis/rest/services/vector/MapLayerslab2/MapServer/4";//行政区域查询
    public static String gisXZQYUrl = "http://39.105.197.211:6080/arcgis/rest/services/vector/MapLayerslab2/MapServer";//行政区域
    public static String getToken = ip+"admin/login/token";//获取token
    public static String getUserInfo = ip+"admin/current/user";//获取用户信息
    public static String getAppVersion = ip+"znyp/appversion/queryLatest";//获取版本信息
    public static String subDiKuaiInfo = ip+"znyp/massifInfo/save/massifInfos";//提交体块信息
    public static String subPhotoInfo = ip+"jqcb/pointStorage/uploadFile";//提交查勘照片信息
    public static String cityInfo = ip+"base/area/city/list";//获取市列表
    public static String countyInfo = ip+"base/area/county/list";//获取县列表
    public static String countryInfo = ip+"base/area/country/list";//获取村列表
    public static String villageInfo = ip+"base/area/village/list";//获取村列表
    public static String farmerInfo = ip+"znyp/customer/get/customers";//获取村所有户列表
    public static String houseHoldsInfo = ip+"znyp/customer/get/info";//获取分户清单列表
    public static String uploadFile = ip+"base/file/upload";//提交签名照片
    public static String getMapServicePath = ip+"zynx/api/mapConfig/query/mapPath";//获取村边界及四置坐标
    public static String massifSnapAdd = ip+"znyp/massifSnap/add";//增加快照
    public static String massifSnapDelete = ip+"znyp/massifSnap/deleteSnap";//删除快照
    public static String getCheckMapServicePath = ip+"znyp/lossAssessment/query/byCode";//获取查勘底图地址

}