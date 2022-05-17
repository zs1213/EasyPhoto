package com.example.lml.easyphoto.sign.signFinish;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.lml.easyphoto.FarmerInfo.FarmerInfoActivity;
import com.example.lml.easyphoto.R;
import com.example.lml.easyphoto.camera.LoadLocalImageUtil;
import com.example.lml.easyphoto.dikuai.DKBean;
import com.example.lml.easyphoto.dikuai.DKService;
import com.example.lml.easyphoto.history.HistoryActivity;
import com.example.lml.easyphoto.okHttpUtils.DOkHttp;
import com.example.lml.easyphoto.sign.DrawActivity;
import com.example.lml.easyphoto.sign.SignBean;
import com.example.lml.easyphoto.util.Configure;
import com.example.lml.easyphoto.util.LoadingDialog;
import com.example.lml.easyphoto.util.SharePreferencesTools;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

public class SignFinishActivity extends AppCompatActivity {
    private Button btn_sub;
    private Button btn_counton;
    private ImageView iv_sign;
    private String proposalNo;
    private String certificateId;
    private String overPath;
    private DKService dkService;
    private LoadingDialog dialog;//提示框

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_finish);
        initView();
        initData();
    }

    private void initView() {
        btn_sub = findViewById(R.id.asf_btn_sub);
        btn_counton = findViewById(R.id.asf_btn_counton);
        iv_sign = findViewById(R.id.asf_iv_sign);

        btn_counton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DKBean dkBean = dkService.getDKBeanLimit(proposalNo, certificateId);
                Intent intent = new Intent(SignFinishActivity.this, FarmerInfoActivity.class);
                intent.putExtra("province", dkBean.getProvinceCode());
                intent.putExtra("provinceName", dkBean.getProvince());
                intent.putExtra("city", dkBean.getCityCode());
                intent.putExtra("cityName", dkBean.getCity());
                intent.putExtra("county", dkBean.getTownCode());
                intent.putExtra("countyName", dkBean.getTown());
                intent.putExtra("country", dkBean.getCountrysideCode());
                intent.putExtra("countryName", dkBean.getCountryside());
                intent.putExtra("village", dkBean.getVillageCode());
                intent.putExtra("villageName", dkBean.getVillage());
                startActivity(intent);
            }
        });
        btn_sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(SignFinishActivity.this);
                builder1.setTitle("提醒").setMessage("确定要提交吗？")
                        .setPositiveButton("提交", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogs, int which) {
                                submitData();
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

            }
        });
    }

    private void initData() {
        proposalNo = getIntent().getStringExtra("proposalNo");
        certificateId = getIntent().getStringExtra("certificateId");
        overPath = getIntent().getStringExtra("overPath");
        LoadLocalImageUtil.getInstance().displayFromSDCard(overPath, iv_sign);
        dkService = new DKService(this);
        dialog = new LoadingDialog(SignFinishActivity.this);
    }

    public void submitData() {
        File file = new File(overPath);
        if (file.exists()) {
            LoadingDialog.Builder loadBuilder = new LoadingDialog.Builder(SignFinishActivity.this)
                    .setMessage("正在上传")
                    .setCancelable(true)
                    .setCancelOutside(true);
            dialog = loadBuilder.create();
            dialog.show();
            MultipartBody.Builder mb = new MultipartBody.Builder();
            mb.setType(MultipartBody.FORM);
            String paths[] = overPath.split("/");
            mb.addFormDataPart("file", paths[paths.length - 1],
                    RequestBody.create(null, file));
            RequestBody requestBody = mb.build();
            DOkHttp.getInstance().uploadPost2ServerProgress(SignFinishActivity.this, Configure.uploadFile, "Bearer " + SharePreferencesTools.getValue(SignFinishActivity.this, "easyPhoto", "access_token", ""), requestBody, new DOkHttp.MyCallBack() {
                @Override
                public void onFailure(Request request, IOException e) {
                    Toast.makeText(SignFinishActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
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
                                RequestBody requestBodys = FormBody.create(MediaType.parse("application/json; charset=utf-8"), new Gson().toJson(upList));
                                DOkHttp.getInstance().uploadPost2ServerProgress(SignFinishActivity.this, Configure.subDiKuaiInfo, "Bearer " + SharePreferencesTools.getValue(SignFinishActivity.this, "easyPhoto", "access_token", ""), requestBodys, new DOkHttp.MyCallBack() {
                                    @Override
                                    public void onFailure(Request request, IOException e) {
                                        Toast.makeText(SignFinishActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
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
                                                Intent intentss = new Intent();
                                                intentss.setAction("com.znyg.znyp.sign");
                                                sendBroadcast(intentss);
                                                DKBean dkBean = dkService.getDKBeanLimit(proposalNo, certificateId);
                                                Intent intent = new Intent(SignFinishActivity.this, FarmerInfoActivity.class);
                                                intent.putExtra("province", dkBean.getProvinceCode());
                                                intent.putExtra("provinceName", dkBean.getProvince());
                                                intent.putExtra("city", dkBean.getCityCode());
                                                intent.putExtra("cityName", dkBean.getCity());
                                                intent.putExtra("county", dkBean.getTownCode());
                                                intent.putExtra("countyName", dkBean.getTown());
                                                intent.putExtra("country", dkBean.getCountrysideCode());
                                                intent.putExtra("countryName", dkBean.getCountryside());
                                                intent.putExtra("village", dkBean.getVillageCode());
                                                intent.putExtra("villageName", dkBean.getVillage());
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(SignFinishActivity.this, message, Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(SignFinishActivity.this, message, Toast.LENGTH_SHORT).show();
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

    public void goBack(View view) {
        finish();
    }
}
