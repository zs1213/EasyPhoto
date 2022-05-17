package com.example.lml.easyphoto.skp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.example.lml.easyphoto.R;
import com.example.lml.easyphoto.camera.CameraPhotoBean;
import com.example.lml.easyphoto.camera.CameraService;
import com.example.lml.easyphoto.camera.LoadLocalImageUtil;
import com.example.lml.easyphoto.camera.TimeCycleUtils;
import com.example.lml.easyphoto.camera.TimestampTool;
import com.example.lml.easyphoto.camera.WaterMarkCamera;
import com.example.lml.easyphoto.util.SharePreferencesTools;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SkpActivity extends AppCompatActivity implements View.OnClickListener {
    /**
     * 获取相册
     */
    public static final int REQUEST_PHOTO = 1;
    /**
     * 获取视频
     */
    public static final int REQUEST_VIDEO = 2;
    /**
     * 最小录制时间
     */
    private static final int MIN_RECORD_TIME = 1 * 1000;
    /**
     * 最长录制时间
     */
    private static final int MAX_RECORD_TIME = 30 * 1000;
    /**
     * 刷新进度的间隔时间
     */
    private static final int PLUSH_PROGRESS = 100;

    /**
     * TextureView
     */
    private TextureView mTextureView;
    /**
     * 带手势识别
     */
    private CameraView mCameraView;
    /**
     * 录制按钮
     */
    private TextView mProgressbar;
    private ImageView iv_xc;
    /**
     *  顶部像机设置*/
//    private RelativeLayout rl_camera;
    /**
     * 关闭,选择,前后置
     */
    private ImageView iv_facing;
    /**
     * 闪光
     */
    private LinearLayout tv_flash;
    /**
     * camera manager
     */
    private CameraManager cameraManager;
    /**
     * player manager
     */
    private MediaPlayerManager playerManager;
    /**
     * true代表视频录制,否则拍照
     */
    private boolean isSupportRecord;
    /**
     * 视频录制地址
     */
    private String recorderPath;
    /**
     * 录制视频的时间,毫秒
     */
    private int recordSecond;
    /**
     * 获取照片订阅, 进度订阅
     */
    private Subscription takePhotoSubscription, progressSubscription;
    /**
     * 是否正在录制
     */
    private boolean isRecording;

    /**
     * 是否为点了拍摄状态(没有拍照预览的状态)
     */
    private boolean isPhotoTakingState;
    private String filesName = "";
    private String gps_lon = "0.0";
    private String gps_lat = "0.0";
    private String gps_address = "未获取到地址";
    private String fileNames = "";
    private String zuowu = "";
    private String zaihai = "";
    private String beizhu = "";
    private String sunshi = "";
    private String mt = "";
    private ImageView apz_iv_logo;
    private TextView apz_tv_shuiyin;
    MyOrientationEventListener orientationEventListener;
    int orientation = 0;
    String shuiyin = "";
    CameraService service;
    String path;
    private RelativeLayout apz_rl_juli;
    private DisplayImageOptions options;
    private String flag = "";
    private List<String> resultList = new ArrayList<>();
    private TextView tv_sure;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_skp);
        verifyStoragePermissions(this);
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.CAMERA);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        } else {
            initView();
            initDatas();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            initView();
            initDatas();
        }
    }

    private void initView() {
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .bitmapConfig(Bitmap.Config.ARGB_8888)   //设置图片的解码类型
                .displayer(new RoundedBitmapDisplayer(20))
                .build();
        service = new CameraService(SkpActivity.this);
        mTextureView = findViewById(R.id.mTextureView);
        mCameraView = findViewById(R.id.mCameraView);
        mProgressbar = findViewById(R.id.mProgressbar);
        apz_rl_juli = findViewById(R.id.apz_rl_juli);
        iv_xc = findViewById(R.id.iv_photo);
        iv_facing = findViewById(R.id.iv_facing);
        iv_facing.setOnClickListener(this);
        tv_flash = findViewById(R.id.tv_flash);
        tv_flash.setOnClickListener(this);
        iv_xc.setOnClickListener(this);
        apz_iv_logo = findViewById(R.id.apz_iv_logo);
        apz_tv_shuiyin = findViewById(R.id.apz_tv_shuiyin);
        tv_sure = findViewById(R.id.tv_sure);
        tv_sure.setOnClickListener(this);
        if (getIntent().getStringExtra("flag") != null) {
            flag = getIntent().getStringExtra("flag");
            filesName = getIntent().getStringExtra("foldeName");
            gps_lon = getIntent().getStringExtra("gps_lon");
            gps_lat = getIntent().getStringExtra("gps_lat");
            zuowu = getIntent().getStringExtra("zuowu");
            zaihai = getIntent().getStringExtra("zaihai");
            beizhu = getIntent().getStringExtra("beizhu");
            mt = getIntent().getStringExtra("mt");
            sunshi = getIntent().getStringExtra("sunshi");
            gps_address = getIntent().getStringExtra("gps_address");
        }
        setYuLan();
        path = service.selectLastPhoto();
        if (path != null && !path.equals("")) {
            LoadLocalImageUtil.getInstance().displayFromSDCardYj(path, iv_xc, options);
        }
    }


    private void setYuLan() {
        shuiyin = "";
        shuiyin += "作    物：" + zuowu + "\n";
        shuiyin += "类    型：" + mt + "\n";
        shuiyin += "灾    害：" + zaihai + "\n";
        shuiyin += "程    度：" + sunshi + "\n";
        shuiyin += "时    间：" + TimestampTool.getCurrentDateTime() + "\n";
        shuiyin += "地    址：" + gps_address + "\n";
        if (beizhu.length()>0){
            shuiyin += "经    度：" + gps_lon + ",纬    度：" + gps_lat + "\n";
            shuiyin += "备    注：" + beizhu;
        }else {
            shuiyin += "经    度：" + gps_lon + ",纬    度：" + gps_lat;
        }
        apz_tv_shuiyin.setText(shuiyin);
    }

    protected void initDatas() {
        apz_tv_shuiyin.setRotation(0);
        apz_tv_shuiyin.setTranslationY(0);
        apz_tv_shuiyin.setTranslationX(0);
        orientationEventListener = new MyOrientationEventListener(SkpActivity.this, new MyOrientationEventListener.Xuanzhuan() {
            @Override
            public void xuanzhaun(int zhuan) {
                if (zhuan - orientation != 0) {
                    int height = apz_rl_juli.getHeight();
                    switch (zhuan) {
                        case 0:
                            apz_tv_shuiyin.setRotation(0);
                            apz_tv_shuiyin.setTranslationY(0);
                            apz_tv_shuiyin.setTranslationX(0);
                            apz_iv_logo.setRotation(0);
                            apz_iv_logo.setTranslationY(0);
                            apz_iv_logo.setTranslationX(0);
                            break;
                        case 90:
                            break;
                        case 180:
                            break;
                        case 270:
                            apz_tv_shuiyin.setRotation(90);
                            float w = (float) apz_tv_shuiyin.getWidth() / 2;
                            float h = (float) apz_tv_shuiyin.getHeight() / 2;
                            apz_tv_shuiyin.setTranslationY(-(height - w - h));
                            apz_tv_shuiyin.setTranslationX(-(w - h));
                            apz_iv_logo.setRotation(90);
                            float w_g = (float) apz_iv_logo.getWidth() / 2;
                            float h_g = (float) apz_iv_logo.getHeight();
                            apz_iv_logo.setTranslationY(height - h_g);
                            apz_iv_logo.setTranslationX(0);
                            break;
                    }
                    orientation = zhuan;
                }
            }
        });
        cameraManager = CameraManager.getInstance(getApplication());
        playerManager = MediaPlayerManager.getInstance(getApplication());
        cameraManager.setCameraType(isSupportRecord ? 1 : 0);

        tv_flash.setVisibility(cameraManager.isSupportFlashCamera() ? View.VISIBLE : View.GONE);
        setCameraFlashState();
        iv_facing.setVisibility(cameraManager.isSupportFrontCamera() ? View.VISIBLE : View.GONE);
        iv_facing.setVisibility(cameraManager.isSupportFlashCamera()
                || cameraManager.isSupportFrontCamera() ? View.VISIBLE : View.GONE);
        tv_flash.setVisibility(cameraManager.isSupportFlashCamera()
                || cameraManager.isSupportFrontCamera() ? View.VISIBLE : View.GONE);

        final int max = MAX_RECORD_TIME / PLUSH_PROGRESS;
        mProgressbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraManager.takePhoto(callback);
            }
        });

        mCameraView.setOnViewTouchListener(new CameraView.OnViewTouchListener() {
            @Override
            public void handleFocus(float x, float y) {
                try {
                    cameraManager.handleFocusMetering(x, y);
                } catch (Exception e) {

                }

            }

            @Override
            public void handleZoom(boolean zoom) {
                cameraManager.handleZoom(zoom);
            }
        });
    }

    /**
     * 设置闪光状态
     */
    private void setCameraFlashState() {
        int flashState = cameraManager.getCameraFlash();
        switch (flashState) {
            case 0:
                tv_flash.setSelected(true);
                break;
            case 1:
                tv_flash.setSelected(true);
                break;
            case 2:
                tv_flash.setSelected(false);
                break;
        }
    }

    /**
     * 是否显示录制按钮
     *
     * @param isShow
     */
    private void setTakeButtonShow(boolean isShow) {
        if (isShow) {
            mProgressbar.setVisibility(View.VISIBLE);
            iv_facing.setVisibility(cameraManager.isSupportFlashCamera()
                    || cameraManager.isSupportFrontCamera() ? View.VISIBLE : View.GONE);
            tv_flash.setVisibility(cameraManager.isSupportFlashCamera()
                    || cameraManager.isSupportFrontCamera() ? View.VISIBLE : View.GONE);
        } else {
            mProgressbar.setVisibility(View.GONE);
            iv_facing.setVisibility(View.GONE);
            tv_flash.setVisibility(View.GONE);
        }
    }

    /**
     * 停止拍摄
     */
    private void stopRecorder(boolean play) {
        isRecording = false;
        cameraManager.stopMediaRecord();
        if (recordSecond < MIN_RECORD_TIME) {//小于最小录制时间作废
            if (recorderPath != null) {
                FileUtils.delteFiles(new File(recorderPath));
                recorderPath = null;
                recordSecond = 0;
            }
            setTakeButtonShow(true);
        } else if (play && mTextureView != null && mTextureView.isAvailable()) {
            setTakeButtonShow(false);
            mProgressbar.setVisibility(View.GONE);
            cameraManager.closeCamera();
            playerManager.playMedia(new Surface(mTextureView.getSurfaceTexture()), recorderPath);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (apz_tv_shuiyin!=null){
            setYuLan();
        }
        orientationEventListener.enable();
        if (mTextureView.isAvailable()) {
            if (recorderPath != null) {//优先播放视频
                setTakeButtonShow(false);
                playerManager.playMedia(new Surface(mTextureView.getSurfaceTexture()), recorderPath);
            } else {
                setTakeButtonShow(true);
                cameraManager.openCamera(mTextureView.getSurfaceTexture(),
                        mTextureView.getWidth(), mTextureView.getHeight());
            }
        } else {
            mTextureView.setSurfaceTextureListener(listener);
        }
    }

    @Override
    protected void onPause() {
        orientationEventListener.disable();
        if (progressSubscription != null) {
            progressSubscription.unsubscribe();
        }
        if (takePhotoSubscription != null) {
            takePhotoSubscription.unsubscribe();
        }
        if (isRecording) {
            stopRecorder(false);
        }
        cameraManager.closeCamera();
        playerManager.stopMedia();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mCameraView.removeOnZoomListener();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_flash:
                try {
                    cameraManager.changeCameraFlash(mTextureView.getSurfaceTexture(),
                            mTextureView.getWidth(), mTextureView.getHeight());
                    setCameraFlashState();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case R.id.iv_facing:
                cameraManager.changeCameraFacing(mTextureView.getSurfaceTexture(),
                        mTextureView.getWidth(), mTextureView.getHeight());
                break;
            case R.id.iv_photo:
                if (resultList.size() > 0) {
                    List<CameraPhotoBean> cameraPhotoBeans  = new ArrayList<>();
                    for (int i = 0; i < resultList.size(); i++) {
                        CameraPhotoBean c = new CameraPhotoBean();
                        c.setFilePath(resultList.get(i));
                        cameraPhotoBeans.add(c);
                    }
                    Intent intent = new Intent(SkpActivity.this, ImagePagerActivity.class);
                    // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
                    intent.putExtra("image_urls", new Gson().toJson(cameraPhotoBeans));
                    intent.putExtra("image_index", 0);
                    startActivity(intent);
                }else {
                    Toast.makeText(this, "没有可预览照片", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tv_sure:
                if (flag.equals("burn")) {
                    if (resultList.size() > 0) {
                        for (int i = 0; i < resultList.size(); i++) {
                            File file = new File(resultList.get(i));
                            if (!file.exists()) {
                                Toast.makeText(this, "图片正在保存,请稍后！", Toast.LENGTH_SHORT).show();
                                return ;
                            }
                        }
                        Intent i = new Intent();
                        i.putExtra("skpResult", new Gson().toJson(resultList));
                        setResult(0, i);
                        finish();
                    } else {
                        finish();
                    }
                } else {
                    finish();
                }
                break;
        }
    }


    /**
     * camera回调监听
     */
    private TextureView.SurfaceTextureListener listener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
            if (recorderPath != null) {
                setTakeButtonShow(false);
                playerManager.playMedia(new Surface(texture), recorderPath);
            } else {
                setTakeButtonShow(true);
                cameraManager.openCamera(texture, width, height);
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height) {
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture texture) {
        }
    };
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            LoadLocalImageUtil.getInstance().displayFromSDCardYj(msg.obj.toString(), iv_xc, options);
        }
    };
    private Camera.PictureCallback callback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(final byte[] data, Camera camera) {
            setTakeButtonShow(false);
            takePhotoSubscription = Observable.create(new Observable.OnSubscribe<Boolean>() {
                @Override
                public void call(Subscriber<? super Boolean> subscriber) {
                    if (!subscriber.isUnsubscribed()) {
                        fileNames = getPhotopath(".jpg", "easyPhoto/" + filesName + "");//参数分别为 格式，存放的文件夹名;
//                        File flagFile = new File(Environment.getExternalStorageDirectory() + "/easyPhoto/catch/");
//                        if (!flagFile.exists()) {
//                            flagFile.mkdirs();
//                        }
//                        deleteFolderFile(Environment.getExternalStorageDirectory() + "/easyPhoto/catch/");
//                        showNames = Environment.getExternalStorageDirectory() + "/easyPhoto/catch/" + TimeCycleUtils.getPicData() + ".jpg";
//                        FileUtils.savePhoto(cameraManager.getSaveOptimalPicSize(), showNames, data, cameraManager.isCameraFrontFacing(), orientation);

                        isPhotoTakingState = FileUtils.savePhoto(cameraManager.getSaveOptimalPicSize(), fileNames, data, cameraManager.isCameraFrontFacing(), orientation);
                        long nSize = 0;
                        nSize = new WaterMarkCamera(SkpActivity.this).resizeImage("作    物：" + zuowu,"类    型：" + mt,"灾    害：" + zaihai,"程    度：" + sunshi, beizhu,fileNames, "地    址：" + gps_address, gps_lon + "," + gps_lat);
                        ExifInterfaceUtils.writeLatLonIntoJpeg(fileNames, Double.parseDouble(gps_lat), Double.parseDouble(gps_lon), "qwerty");
                        File file = new File(fileNames);
                        boolean flag = file.exists();
                        if (flag) {
                            Message message = Message.obtain();
                            message.obj = fileNames;
                            handler.sendMessage(message);
                        }
                        resultList.add(fileNames);
                        subscriber.onNext(isPhotoTakingState);
                    }
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Boolean>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(Boolean aBoolean) {
                    isPhotoTakingState = false;
                    setTakeButtonShow(true);
                    cameraManager.restartPreview();
                }
            });
        }
    };

    private String getPhotopath(String form, String foldeName) {
        String name = TimeCycleUtils.getPicData();
        String fileName = Environment.getExternalStorageDirectory() + "/" + foldeName + "/" + name + form;
        File file = new File(Environment.getExternalStorageDirectory() + "/" + foldeName + "/");
        if (!file.exists()) {
            Log.e("TAG", "第一次创建文件夹");
            file.mkdirs();// 如果文件夹不存在，则创建文件夹
        }
        return fileName;
    }

    public void goBack(View v) {
        finish();
    }

    Uri uri;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 102) {
            if (resultCode == 0) {
                return;
            }
            String path = Util.getImageAbsolutePath(this, data.getData());
            File file = new File(path);
            uri = Uri.fromFile(file);
            cropImage(uri);
            setYuLan();
        } else if (requestCode == 103) {
        }
    }

    public void cropImage(Uri uri) {
        File ff = new File(Environment.getExternalStorageDirectory() + "/yipai/logo/");
        if (!ff.exists()) {
            ff.mkdirs();
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 100);
        intent.putExtra("outputY", 100);
        intent.putExtra("return-data", false);
        File pictureFile = new File(Environment.getExternalStorageDirectory() + "/yipai/logo/logo.png");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(pictureFile));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, 103);
    }


    public void deleteFolderFile(String filePath) {
        try {
            File file = new File(filePath);//获取SD卡指定路径
            File[] files = file.listFiles();//获取SD卡指定路径下的文件或者文件夹
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {//如果是文件直接删除
                    File photoFile = new File(files[i].getPath());
                    Log.d("photoPath -->> ", photoFile.getPath());
                    photoFile.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @Override
//    public boolean onKeyUp(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
//
//            //不执行父类点击事件
//            return true;
//        }
//        //继续执行父类其他点击事件
//        return super.onKeyUp(keyCode, event);
//    }
}
