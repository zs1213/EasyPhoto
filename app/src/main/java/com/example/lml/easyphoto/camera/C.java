package com.example.lml.easyphoto.camera;


import com.example.lml.easyphoto.R;

public class C {

    /**
     * 成功
     */
    public static final int SUCCESS = 0;

    /**
     * 失败
     */
    public static final int FAILURE = 1;
    /**
     * 服务器异常
     */
    public static final int SERVICE_FAILURE = 2;
    /**
     * 409
     */
    public static final int FAILURE_409 = 409;
    /**
     * 404
     */
    public static final int FAILURE_404 = 404;

    /**
     * 400  密码已修改 重新登录
     */
    public static final int FAILURE_400 = 400;

    /**
     * 300  密码已修改 重新登录
     */
    public static final int FAILURE_300 = 300;

    /**
     * 600  已在其他设备登录
     */
    public static final int FAILURE_600 = 600;

    /**
     * 网络
     */
    public static final int NETWORK = 10000;

    /**
     * 检查内存卡
     */
    public static final int CHECK_SDCARD = 13015;

    /**
     * 列表无更多数据
     */
    public static final int NODATA = 10001;

    public static final int RESTART = 10002;

    public static final int COUNTDOWN = 10003;

    public static final int TIMING = 10004;

    /**
     * 版本检测
     */
    public static final int NONEED_UPDATE = 20000;
    public static final int REQUIRE_UPDATE = 20001;
    public static final int FORCE_UPDATE = 20002;
    public static final int STAFF = 30001;
    /**
     * ******************拍照LOGO
     ****************************/
    //安邦
    //public static final int PHOTO_LOGO = R.mipmap.chinamobile_logo_anbang;
    //安华
    //public static final int PHOTO_LOGO = R.mipmap.chinamobile_logo_anhua;
    //平安
    //public static final int PHOTO_LOGO = R.mipmap.chinamobile_logo_pingan;
    //学习
    public static final int PHOTO_LOGO = R.mipmap.chinamobile_logo_xuexi;


    /**
     * ******************ArcGISActivity
     ****************************/
    //学习
    // public static final String ARCGISACTIVITY = "http://111.26.161.196:6080/arcgis/rest/services/StudyMapService/Plant/FeatureServer/0";
    //安华
    //public static final String ARCGISACTIVITY = "http://111.26.161.196:6080/arcgis/rest/services/AHMapService/Plant_AH/FeatureServer/0";
    //安邦
    public static final String ARCGISACTIVITY = "http://111.26.161.196:6080/arcgis/rest/services/ABMapService/Plant_AB/FeatureServer/0";
    //平安
    //public static final String ARCGISACTIVITY = "http://111.26.161.196:3401/arcgis/rest/services/VectorMapService/Plant_PA/FeatureServer/0";

    /**
     * ******************AccurateCheckActivity
     ****************************/
    //学习
    // public static final String ACCURATECHECKACTIVITY = "http://111.26.161.196:6080/arcgis/rest/services/StudyMapService/CheckPlant/FeatureServer/0";
    //安华
    //public static final String ACCURATECHECKACTIVITY = "http://111.26.161.196:6080/arcgis/rest/services/AHMapService/CheckPlant_AH/FeatureServer/0";
    //安邦
    public static final String ACCURATECHECKACTIVITY = "http://111.26.161.196:6080/arcgis/rest/services/ABMapService/CheckPlant_AB/FeatureServer/0";
    //平安
    //public static final String ACCURATECHECKACTIVITY = "http://111.26.161.196:3401/arcgis/rest/services/PAMapService/CheckPlant_PA/FeatureServer/0";

    /**
     * ******************AsyncQueryTask
     ****************************/
    //安邦精确承保地块查询
    public static final String FACTORYBIAODIURL = "http://111.26.161.196:6080/arcgis/rest/services/ABMapService/Plant_AB/MapServer/0";
    //安华精确承保地块查询
    //public static final String FACTORYBIAODIURL = "http://111.26.161.196:6080/arcgis/rest/services/AHMapService/Plant_AH/MapServer/0";
    //学习精确承保地块
    //public static final String FACTORYBIAODIURL = "http://111.26.161.196:6080/arcgis/rest/services/StudyMapService/Plant/MapServer/0";
    //平安精确承保地块
    //public static final String FACTORYBIAODIURL = "http://111.26.161.196:3401/arcgis/rest/services/VectorMapService/Plant_PA/MapServer/0";


    /**
     * ******************CheckAsyncQueryTask
     ****************************/
    //安邦精确验标地块查询
    public static final String CHECKASYNCQUERYTASK = "http://111.26.161.196:6080/arcgis/rest/services/ABMapService/CheckPlant_AB/MapServer/0";
    //安华精确验标地块查询
    //public static final String CHECKASYNCQUERYTASK = "http://111.26.161.196:6080/arcgis/rest/services/AHMapService/CheckPlant_AH/MapServer/0";
    //学习精确验标地块
    //public static final String CHECKASYNCQUERYTASK = "http://111.26.161.196:6080/arcgis/rest/services/StudyMapService/CheckPlant/MapServer/0";
    //平安精确验标地块
    //public static final String CHECKASYNCQUERYTASK = "http://111.26.161.196:3401/arcgis/rest/services/PAMapService/CheckPlant_PA/MapServer/0";

}
