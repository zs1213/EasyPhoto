package com.example.lml.easyphoto.sign;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.lml.easyphoto.FarmerInfo.FarmerInfoActivity;
import com.example.lml.easyphoto.R;
import com.example.lml.easyphoto.camera.LoadLocalImageUtil;
import com.example.lml.easyphoto.dikuai.DKBean;
import com.example.lml.easyphoto.dikuai.DKService;
import com.example.lml.easyphoto.history.HistoryActivity;
import com.example.lml.easyphoto.login.LoginActivity;
import com.example.lml.easyphoto.okHttpUtils.DOkHttp;
import com.example.lml.easyphoto.sign.signFinish.SignFinishActivity;
import com.example.lml.easyphoto.skp.CameraManager;
import com.example.lml.easyphoto.skp.CameraView;
import com.example.lml.easyphoto.skp.FileUtils;
import com.example.lml.easyphoto.skp.MyOrientationEventListener;
import com.example.lml.easyphoto.skp.TimeCycleUtil;
import com.example.lml.easyphoto.util.Configure;
import com.example.lml.easyphoto.util.LoadingDialog;
import com.example.lml.easyphoto.util.SharePreferencesTools;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DrawActivity extends Activity implements View.OnClickListener {
    private DrawView dv_main;
    private Button btn_reDraw;
    private Button btn_ok;
    private Button btn_counton;
    private ImageView iv_sign;
    private boolean isClick = true;
    private static final String PATH_IMAGES = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "easyPhoto/qianzi";
    private String proposalNo;
    private String certificateId;
    private String photo_Path;
    private String sign_Path;
    private String overPath;
    private String from;
    private TextureView mTextureView;
    private CameraView mCameraView;
    private CameraManager cameraManager;
    private Subscription takePhotoSubscription;
    int orientation = 0;
    private MyOrientationEventListener orientationEventListener;
    private SignService signService;
    private DKService dkService;
    boolean flag = false;
    boolean subFlag = false;
    private Gson gson;
    private LoadingDialog dialog;//提示框
    private Handler handler = new Handler(Looper.getMainLooper());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_draw);
        initView();
        initData();
    }

    private void initView() {
        dv_main = findViewById(R.id.dc_draw_main);
        btn_reDraw = findViewById(R.id.ad_btn_reDraw);
        btn_counton = findViewById(R.id.ad_btn_counton);
        btn_ok = findViewById(R.id.ad_btn_ok);
        mTextureView = findViewById(R.id.draw_mTextureView);
        mCameraView = findViewById(R.id.draw_mCameraView);
        iv_sign = findViewById(R.id.draw_iv_sign);

    }

    private void initData() {
        gson = new Gson();
        dialog = new LoadingDialog(DrawActivity.this);
        signService = new SignService(this);
        dkService = new DKService(this);
        proposalNo = getIntent().getStringExtra("proposalNo");
        certificateId = getIntent().getStringExtra("certificateId");
        from = getIntent().getStringExtra("from");
//        if (from.equals("finish")){
//            btn_counton.setText("继续标绘");
//        }else {
//            btn_counton.setText("保存");
//        }
        btn_ok.setOnClickListener(this);
        btn_reDraw.setOnClickListener(this);
        btn_counton.setOnClickListener(this);
        SignBean bean = signService.getSignBean(proposalNo, certificateId);
        if (bean!=null&&bean.getPath()!=null){
            iv_sign.setVisibility(View.VISIBLE);
            LoadLocalImageUtil.getInstance().displayFromSDCard(bean.getPath(),iv_sign);
        }else {
            iv_sign.setVisibility(View.GONE);
        }
        cameraManager = CameraManager.getInstance(getApplication());
        cameraManager.setCameraType(0);
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
        orientationEventListener = new MyOrientationEventListener(DrawActivity.this, new MyOrientationEventListener.Xuanzhuan() {
            @Override
            public void xuanzhaun(int zhuan) {
                if (zhuan - orientation != 0) {
                    orientation = zhuan;
                }
            }
        });
    }

    public void goBack(View view) {
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ad_btn_reDraw:
                iv_sign.setVisibility(View.GONE);
                dv_main.clear();
                sign_Path = "";
                overPath = "";
                if (signService.hasSign(proposalNo, certificateId)) {
                    SignBean signBean = signService.getSignBean(proposalNo, certificateId);
                    signService.deleteSign(signBean.getID());
                }
                break;
            case R.id.ad_btn_ok:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(DrawActivity.this);
                builder1.setTitle("提醒").setMessage("确定要提交吗？")
                        .setPositiveButton("提交", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogs, int which) {
                                subFlag = true;
                                if (flag) {
                                    submitData();
                                } else {
                                    try {
                                        sign_Path = dv_main.saveBitmap();
                                        takePhoto();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                dialogs.dismiss();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialoga, int which) {
                                dialoga.dismiss();
                            }
                        });
                builder1.create().show();
                break;
            case R.id.ad_btn_counton:
                subFlag = false;
                if (iv_sign.getVisibility()==View.VISIBLE){
                    SignBean beans = signService.getSignBean(proposalNo, certificateId);
                    Intent intent = new Intent(DrawActivity.this, SignFinishActivity.class);
                    intent.putExtra("proposalNo",proposalNo);
                    intent.putExtra("certificateId",certificateId);
                    intent.putExtra("overPath",beans.getPath());
                    startActivity(intent);
                }else {
                    try {
                        sign_Path = dv_main.saveBitmap();
                        takePhoto();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
                default:
                    break;
        }
    }

    public void takePhoto() {
        if (isClick) {
            isClick = false;
            cameraManager.takePhoto(callback);
        }
    }

    private boolean isPhotoTakingState;
    private Camera.PictureCallback callback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(final byte[] data, Camera camera) {
            takePhotoSubscription = Observable.create(new Observable.OnSubscribe<Boolean>() {
                @Override
                public void call(Subscriber<? super Boolean> subscriber) {
                    subscriber.onNext(isPhotoTakingState);
                    if (!subscriber.isUnsubscribed()) {
                        photo_Path = getFileNmae();
                        FileUtils.savePhoto(cameraManager.getSaveOptimalPicSize(), photo_Path, data, cameraManager.isCameraFrontFacing(), 0);
                        Bitmap firstBitmap = BitmapFactory.decodeFile(photo_Path);
                        Bitmap nextBitmap = BitmapFactory.decodeFile(sign_Path);
                        Bitmap resultBitmap = add2Bitmap(firstBitmap, nextBitmap);
                        try {
                            overPath = PATH_IMAGES + "/" + UUID.randomUUID().toString() + ".jpg";
                            File file = new File(overPath);
                            FileOutputStream out = new FileOutputStream(file);
                            resultBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                            if (signService.hasSign(proposalNo, certificateId)) {
                                SignBean signBean = signService.getSignBean(proposalNo, certificateId);
                                signService.deleteSign(signBean.getID());
                            }
                            ContentValues values = new ContentValues();
                            values.put("ID", UUID.randomUUID().toString());
                            values.put("certificateId", certificateId);
                            values.put("createTime", TimeCycleUtil.getTime());
                            values.put("path", overPath);
                            values.put("proposalNo", proposalNo);
                            signService.insertSign(values);
                            flag = true;
                            File file1 = new File(overPath);
                            if (subFlag) {
                                if (file1.exists()) {
                                    submitData();
                                }
                            } else {

                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        iv_sign.setVisibility(View.VISIBLE);
                                        LoadLocalImageUtil.getInstance().displayFromSDCard(overPath,iv_sign);
                                    }
                                });
                                Intent intent = new Intent(DrawActivity.this, SignFinishActivity.class);
                                intent.putExtra("proposalNo",proposalNo);
                                intent.putExtra("certificateId",certificateId);
                                intent.putExtra("overPath",overPath);
                                startActivity(intent);
                                if (from.equals("finish")) {

                                } else {
                                    Intent intentss = new Intent();
                                    intentss.setAction("com.znyg.znyp.sign");
                                    sendBroadcast(intentss);
                                }

                            }
                            out.flush();
                            out.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (firstBitmap != null && !firstBitmap.isRecycled()) {
                            firstBitmap.recycle();
                            firstBitmap = null;
                        }
                        if (nextBitmap != null && !nextBitmap.isRecycled()) {
                            nextBitmap.recycle();
                            nextBitmap = null;
                        }
                        if (resultBitmap != null && !resultBitmap.isRecycled()) {
                            resultBitmap.recycle();
                            resultBitmap = null;
                        }
                        //Toast.makeText(DrawActivity.this, "拍照成功", Toast.LENGTH_SHORT).show();
                        isClick = true;
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
                    cameraManager.restartPreview();
                }
            });
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        orientationEventListener.enable();
        if (mTextureView.isAvailable()) {
            cameraManager.openCamera(mTextureView.getSurfaceTexture(),
                    mTextureView.getWidth(), mTextureView.getHeight());
        } else {
            mTextureView.setSurfaceTextureListener(listener);
        }
        cameraManager.changeCameraFacing(mTextureView.getSurfaceTexture(),
                mTextureView.getWidth(), mTextureView.getHeight(), "");
    }

    /**
     * camera回调监听
     */
    private TextureView.SurfaceTextureListener listener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
            cameraManager.openCamera(texture, width, height);
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

    public void submitData() {
        SignBean bean = signService.getSignBean(proposalNo, certificateId);
        if (bean != null) {
            File file = new File(bean.getPath());
            if (file.exists()) {
                LoadingDialog.Builder loadBuilder = new LoadingDialog.Builder(DrawActivity.this)
                        .setMessage("正在上传")
                        .setCancelable(true)
                        .setCancelOutside(true);
                dialog = loadBuilder.create();
                dialog.show();
                MultipartBody.Builder mb = new MultipartBody.Builder();
                mb.setType(MultipartBody.FORM);
                String paths[] = bean.getPath().split("/");
                mb.addFormDataPart("file", paths[paths.length - 1],
                        RequestBody.create(null, file));
                RequestBody requestBody = mb.build();
                DOkHttp.getInstance().uploadPost2ServerProgress(DrawActivity.this, Configure.uploadFile, "Bearer " + SharePreferencesTools.getValue(DrawActivity.this, "easyPhoto", "access_token", ""), requestBody, new DOkHttp.MyCallBack() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        Toast.makeText(DrawActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }

                    @Override
                    public void onResponse(String json) {
                        try {
                            JSONObject object = new JSONObject(json);
                            int code = object.getInt("code");
                            String message = object.getString("message");
                            if (code == 0) {
                                JSONObject object1 = object.getJSONObject("data");
                                String filePathSign = object1.getString("filePath");
                                List<DKBean> updataList = dkService.getDKUploadList(proposalNo, certificateId);
                                if (updataList.size() > 0) {
                                    List<Map<String, String>> upList = new ArrayList<>();
                                    for (int i = 0; i < updataList.size(); i++) {
                                        DKBean dkBean = updataList.get(i);
                                        Map<String, String> map = new HashMap<>();
                                        map.put("certificateId", dkBean.getCertificateId());
                                        map.put("cityCode", dkBean.getCityCode());
                                        map.put("cityName", dkBean.getCity());
                                        map.put("countryCode", dkBean.getCountrysideCode());
                                        map.put("countryName", dkBean.getCountryside());
                                        map.put("countyCode", dkBean.getTownCode());
                                        map.put("countyName", dkBean.getTown());
                                        map.put("cropName", dkBean.getCrop());
                                        map.put("customerIds", dkBean.getCustomerIds());
                                        map.put("farmerName", dkBean.getUserName());
                                        map.put("markArea", dkBean.getDrawArea());
                                        map.put("massifName", dkBean.getDkName());
                                        map.put("isBusinessEntity", dkBean.getIsBusinessEntity());
                                        map.put("policyNumber", dkBean.getPolicyNumber());
                                        map.put("insureArea", dkBean.getChangeArea());
                                        String pointRational = "";
                                        for (int j = 0; j < dkBean.getmList().size(); j++) {
                                            if (j == 0) {
                                                pointRational += dkBean.getmList().get(j).getLon() + "," + dkBean.getmList().get(j).getLat();
                                            } else {
                                                pointRational += ";" + dkBean.getmList().get(j).getLon() + "," + dkBean.getmList().get(j).getLat();
                                            }
                                        }
                                        map.put("pointRational", pointRational);

                                        map.put("proposalNo", dkBean.getProposalNo());
                                        map.put("provinceCode", dkBean.getProvinceCode());
                                        map.put("provinceName", dkBean.getProvince());
                                        map.put("signatureImg", filePathSign);
                                        map.put("villageCode", dkBean.getVillageCode());
                                        map.put("villageName", dkBean.getVillage());
                                        upList.add(map);
                                    }
                                    RequestBody requestBodys = FormBody.create(MediaType.parse("application/json; charset=utf-8"), gson.toJson(upList));
                                    DOkHttp.getInstance().uploadPost2ServerProgress(DrawActivity.this, Configure.subDiKuaiInfo, "Bearer " + SharePreferencesTools.getValue(DrawActivity.this, "easyPhoto", "access_token", ""), requestBodys, new DOkHttp.MyCallBack() {
                                        @Override
                                        public void onFailure(Request request, IOException e) {
                                            Toast.makeText(DrawActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }

                                        @Override
                                        public void onResponse(String json) {
                                            try {
                                                dialog.dismiss();
                                                JSONObject object = new JSONObject(json);
                                                int code = object.getInt("code");
                                                String message = object.getString("message");
                                                if (code == 0) {
                                                    ContentValues contentValues = new ContentValues();
                                                    contentValues.put("proposalNo", proposalNo);
                                                    contentValues.put("certificateId", certificateId);
                                                    contentValues.put("state", "1");
                                                    dkService.updataDKInfo(contentValues);
                                                    if (from.equals("finish")) {
                                                        DKBean dkBean = dkService.getDKBeanLimit(proposalNo,certificateId);
                                                        Intent intent = new Intent(DrawActivity.this, FarmerInfoActivity.class);
                                                        intent.putExtra("province",dkBean.getProvinceCode());
                                                        intent.putExtra("provinceName",dkBean.getProvince());
                                                        intent.putExtra("city",dkBean.getCityCode());
                                                        intent.putExtra("cityName",dkBean.getCity());
                                                        intent.putExtra("county",dkBean.getTownCode());
                                                        intent.putExtra("countyName",dkBean.getTown());
                                                        intent.putExtra("country",dkBean.getCountrysideCode());
                                                        intent.putExtra("countryName",dkBean.getCountryside());
                                                        intent.putExtra("village",dkBean.getVillageCode());
                                                        intent.putExtra("villageName",dkBean.getVillage());
                                                        startActivity(intent);
                                                    } else {
                                                        Intent intentss = new Intent();
                                                        intentss.setAction("com.znyg.znyp.sign");
                                                        sendBroadcast(intentss);
                                                        finish();

                                                    }
                                                } else {
                                                    Toast.makeText(DrawActivity.this, message, Toast.LENGTH_SHORT).show();
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }, new DOkHttp.UIchangeListener() {
                                        @Override
                                        public void progressUpdate(long bytesWrite, long contentLength, boolean done) {
                                            int progress = (int) (bytesWrite * 100 / contentLength);
                                        }
                                    });
                                }
                            } else {
                                Toast.makeText(DrawActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new DOkHttp.UIchangeListener() {
                    @Override
                    public void progressUpdate(long bytesWrite, long contentLength, boolean done) {
                        int progress = (int) (bytesWrite * 100 / contentLength);
                    }
                });
            }
        }

    }

    //保存图片到硬盘
    public String getFileNmae() {
        String fileName = UUID.randomUUID().toString() + ".jpg";
        File file = new File(PATH_IMAGES);
        if (!file.exists()) {
            file.mkdirs();
        }
        return PATH_IMAGES + File.separator + fileName;
    }

    /**
     * 将两张位图拼接成一张(横向拼接)
     *
     * @param first
     * @param second
     * @return
     */
    private Bitmap add2Bitmap(Bitmap first, Bitmap second) {

        int width = Math.max(first.getWidth(), second.getWidth());
        int height = first.getHeight() + second.getHeight();
        ;
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        canvas.drawColor(Color.parseColor("#ffffff"));
        canvas.drawBitmap(first, 0, 0, null);
        canvas.drawBitmap(second, ((first.getWidth() - second.getWidth()) / 2), first.getHeight(), null);
        return result;
    }

    @Override
    protected void onPause() {
        orientationEventListener.disable();
        if (takePhotoSubscription != null) {
            takePhotoSubscription.unsubscribe();
        }
        cameraManager.closeCamera();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mCameraView.removeOnZoomListener();
        super.onDestroy();
    }
}
