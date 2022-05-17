package com.example.lml.easyphoto.history;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lml.easyphoto.FarmerInfo.FarmerInfoActivity;
import com.example.lml.easyphoto.FarmerInfo.houseHolds.HoldsService;
import com.example.lml.easyphoto.FarmerInfo.houseHolds.HouseHoldsActivity;
import com.example.lml.easyphoto.FarmerInfo.houseHolds.HouseHoldsBean;
import com.example.lml.easyphoto.R;
import com.example.lml.easyphoto.camera.LoadLocalImageUtil;
import com.example.lml.easyphoto.dikuai.DKBean;
import com.example.lml.easyphoto.dikuai.DKService;
import com.example.lml.easyphoto.dikuai.DiKuaiActivity;
import com.example.lml.easyphoto.dikuai.finish.FinishActivity;
import com.example.lml.easyphoto.login.LoginActivity;
import com.example.lml.easyphoto.main.MenuActivity;
import com.example.lml.easyphoto.massifShow.MassifShowActivity;
import com.example.lml.easyphoto.okHttpUtils.DOkHttp;
import com.example.lml.easyphoto.sign.DrawActivity;
import com.example.lml.easyphoto.sign.SignBean;
import com.example.lml.easyphoto.sign.SignService;
import com.example.lml.easyphoto.util.Configure;
import com.example.lml.easyphoto.util.LoadingDialog;
import com.example.lml.easyphoto.util.NoDoubleClickUtils;
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

public class HistoryActivity extends Activity implements View.OnClickListener {
    private List<HistoryBean> mlist;
    private DKService dkService;
    private SignService signService;
    private HoldsService holdsService;
    private TextView tv_all;
    private TextView tv_nodraw;
    private TextView tv_nosign;
    private TextView tv_nosubmit;
    private TextView tv_finish;
    private TextView tv_allSub;
    private LinearLayout lin_all;
    private LinearLayout lin_nodraw;
    private LinearLayout lin_nosign;
    private LinearLayout lin_nosubmit;
    private LinearLayout lin_finish;
    private ListView lv_main;
    private HistoryAdapter adapter;
    private LoadingDialog dialog;//提示框
    private int size = 0;//提示框开始
    private int number = 0;//提示框结束
    private Gson gson;
    private MyBroadcastRecivers myBroadcastRecivers;
    private String action = "-1";
    private String state = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.znyg.znyp.sign");
        myBroadcastRecivers = new MyBroadcastRecivers();
        this.registerReceiver(myBroadcastRecivers, intentFilter);
        initView();
        initData();
    }

    private void initView() {
        lv_main = findViewById(R.id.his_lv_main);
        tv_all = findViewById(R.id.his_tv_all);
        tv_nodraw = findViewById(R.id.his_tv_nodraw);
        tv_nosign = findViewById(R.id.his_tv_nosign);
        tv_nosubmit = findViewById(R.id.his_tv_nosubmit);
        tv_finish = findViewById(R.id.his_tv_finish);
        lin_all = findViewById(R.id.his_lin_all);
        lin_nodraw = findViewById(R.id.his_lin_nodraw);
        lin_nosign = findViewById(R.id.his_lin_nosign);
        lin_nosubmit = findViewById(R.id.his_lin_nosubmit);
        lin_finish = findViewById(R.id.his_lin_finish);
        tv_allSub = findViewById(R.id.his_tv_allSub);
        lin_all.setOnClickListener(this);
        lin_nodraw.setOnClickListener(this);
        lin_nosign.setOnClickListener(this);
        lin_nosubmit.setOnClickListener(this);
        lin_finish.setOnClickListener(this);
        tv_allSub.setOnClickListener(this);
    }

    private void initData() {
        gson = new Gson();
        dialog = new LoadingDialog(HistoryActivity.this);
        dkService = new DKService(this);
        signService = new SignService(this);
        holdsService = new HoldsService(this, dkService);
        mlist = new ArrayList<>();
        adapter = new HistoryAdapter(mlist, this);
        lv_main.setAdapter(adapter);
        state = getIntent().getStringExtra("state");
        if (state != null) {
            action = state;
        } else {
            action = "-1";
        }
        changeDate(action);
        lv_main.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!NoDoubleClickUtils.isDoubleClick()){
                    HistoryBean bean = mlist.get(position);
                    switch (bean.getState()) {
                        case "0":
                            Intent intent_finish = new Intent(HistoryActivity.this, FinishActivity.class);
                            intent_finish.putExtra("provinceName", bean.getProvince());
                            intent_finish.putExtra("provinceCode", bean.getProvinceCode());
                            intent_finish.putExtra("cityName", bean.getCity());
                            intent_finish.putExtra("cityCode", bean.getCityCode());
                            intent_finish.putExtra("countyName", bean.getTown());
                            intent_finish.putExtra("countyCode", bean.getTownCode());
                            intent_finish.putExtra("countryName", bean.getCountryside());
                            intent_finish.putExtra("countryCode", bean.getCountrysideCode());
                            intent_finish.putExtra("villageName", bean.getVillage());
                            intent_finish.putExtra("villageCode", bean.getVillageCode());
                            intent_finish.putExtra("proposalNo", bean.getProposalNo());
                            intent_finish.putExtra("certificateId", bean.getCertificateId());
                            startActivity(intent_finish);
                            break;
                        case "1":
                            Intent intent_1 = new Intent(HistoryActivity.this, FinishActivity.class);
                            intent_1.putExtra("provinceName", bean.getProvince());
                            intent_1.putExtra("provinceCode", bean.getProvinceCode());
                            intent_1.putExtra("cityName", bean.getCity());
                            intent_1.putExtra("cityCode", bean.getCityCode());
                            intent_1.putExtra("countyName", bean.getTown());
                            intent_1.putExtra("countyCode", bean.getTownCode());
                            intent_1.putExtra("countryName", bean.getCountryside());
                            intent_1.putExtra("countryCode", bean.getCountrysideCode());
                            intent_1.putExtra("villageName", bean.getVillage());
                            intent_1.putExtra("villageCode", bean.getVillageCode());
                            intent_1.putExtra("proposalNo", bean.getProposalNo());
                            intent_1.putExtra("certificateId", bean.getCertificateId());
                            startActivity(intent_1);
                            break;
                        case "2":
                            Intent intentDraw = new Intent(HistoryActivity.this, DrawActivity.class);
                            intentDraw.putExtra("proposalNo", bean.getProposalNo());
                            intentDraw.putExtra("certificateId", bean.getCertificateId());
                            intentDraw.putExtra("from", "history");
                            startActivity(intentDraw);
                            break;
                        case "3":
                            getMapServerPath(bean);
                            break;
                        default:
                            break;
                    }
                }
            }
        });
    }

    public void goBack(View view) {
        Intent intent = new Intent(HistoryActivity.this, MenuActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 返回键
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Intent intent = new Intent(HistoryActivity.this, MenuActivity.class);
            startActivity(intent);
            finish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.his_lin_all:
                changeDate("-1");
                break;
            case R.id.his_lin_nodraw:
                changeDate("0");
                break;
            case R.id.his_lin_nosign:
                changeDate("1");
                break;
            case R.id.his_lin_nosubmit:
                changeDate("2");
                break;
            case R.id.his_lin_finish:
                changeDate("3");
                break;
            case R.id.his_tv_allSub:
                if (mlist.size() > 0) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(HistoryActivity.this);
                    builder1.setTitle("提醒").setMessage("确定要全部提交吗？")
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
                break;
            default:
                break;
        }
    }

    private void changeDate(String flag) {
        action = flag;
        tv_all.setTextColor(Color.parseColor("#4A5175"));
        tv_nodraw.setTextColor(Color.parseColor("#4A5175"));
        tv_nosign.setTextColor(Color.parseColor("#4A5175"));
        tv_nosubmit.setTextColor(Color.parseColor("#4A5175"));
        tv_finish.setTextColor(Color.parseColor("#4A5175"));
        lin_all.setBackgroundResource(R.color.aits_00ffffff);
        lin_nodraw.setBackgroundResource(R.color.aits_00ffffff);
        lin_nosign.setBackgroundResource(R.color.aits_00ffffff);
        lin_nosubmit.setBackgroundResource(R.color.aits_00ffffff);
        lin_finish.setBackgroundResource(R.color.aits_00ffffff);
        mlist.clear();
        adapter.notifyDataSetChanged();
        mlist.addAll(dkService.getHistoryList(flag, holdsService, signService));
        tv_allSub.setVisibility(View.INVISIBLE);
        switch (flag) {
            case "-1":
                tv_all.setTextColor(Color.parseColor("#0de67d"));
                lin_all.setBackgroundResource(R.drawable.yj_baise);

                break;
            case "0":
                tv_nodraw.setTextColor(Color.parseColor("#0de67d"));
                lin_nodraw.setBackgroundResource(R.drawable.yj_baise);

                break;
            case "1":
                tv_nosign.setTextColor(Color.parseColor("#0de67d"));
                lin_nosign.setBackgroundResource(R.drawable.yj_baise);

                break;
            case "2":
                tv_nosubmit.setTextColor(Color.parseColor("#0de67d"));
                lin_nosubmit.setBackgroundResource(R.drawable.yj_baise);

                tv_allSub.setVisibility(View.VISIBLE);
                break;
            case "3":
                tv_finish.setTextColor(Color.parseColor("#0de67d"));
                lin_finish.setBackgroundResource(R.drawable.yj_baise);
                break;
            default:
                break;
        }
        adapter.notifyDataSetChanged();
    }

    public void submitData() {
        LoadingDialog.Builder loadBuilder = new LoadingDialog.Builder(HistoryActivity.this)
                .setMessage("正在上传")
                .setCancelable(true)
                .setCancelOutside(true);
        dialog = loadBuilder.create();
        dialog.show();
        size = mlist.size();
        for (int i = 0; i < mlist.size(); i++) {
            String proposalNo = mlist.get(i).getProposalNo();
            String certificateId = mlist.get(i).getCertificateId();
            SignBean bean = signService.getSignBean(proposalNo, certificateId);
            if (bean != null) {
                File file = new File(bean.getPath());
                if (file.exists()) {
                    MultipartBody.Builder mb = new MultipartBody.Builder();
                    mb.setType(MultipartBody.FORM);
                    String paths[] = bean.getPath().split("/");
                    mb.addFormDataPart("file", paths[paths.length - 1],
                            RequestBody.create(null, file));
                    RequestBody requestBody = mb.build();
                    DOkHttp.getInstance().uploadPost2ServerProgress(HistoryActivity.this, Configure.uploadFile, "Bearer " + SharePreferencesTools.getValue(HistoryActivity.this, "easyPhoto", "access_token", ""), requestBody, new DOkHttp.MyCallBack() {
                        @Override
                        public void onFailure(Request request, IOException e) {
                            Toast.makeText(HistoryActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
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
                                            map.put("isBusinessEntity", dkBean.getIsBusinessEntity());
                                            map.put("policyNumber", dkBean.getPolicyNumber());
                                            map.put("insureArea", dkBean.getChangeArea());
                                            map.put("villageCode", dkBean.getVillageCode());
                                            map.put("villageName", dkBean.getVillage());
                                            upList.add(map);
                                        }
                                        RequestBody requestBodys = FormBody.create(MediaType.parse("application/json; charset=utf-8"), gson.toJson(upList));
                                        DOkHttp.getInstance().uploadPost2ServerProgress(HistoryActivity.this, Configure.subDiKuaiInfo, "Bearer " + SharePreferencesTools.getValue(HistoryActivity.this, "easyPhoto", "access_token", ""), requestBodys, new DOkHttp.MyCallBack() {
                                            @Override
                                            public void onFailure(Request request, IOException e) {
                                                Toast.makeText(HistoryActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            }

                                            @Override
                                            public void onResponse(String json) {
                                                number++;
                                                if (number == size) {
                                                    dialog.dismiss();
                                                }
                                                try {
                                                    JSONObject object = new JSONObject(json);
                                                    int code = object.getInt("code");
                                                    String message = object.getString("message");
                                                    if (code == 0) {
                                                        ContentValues contentValues = new ContentValues();
                                                        contentValues.put("proposalNo", proposalNo);
                                                        contentValues.put("certificateId", certificateId);
                                                        contentValues.put("state", "1");
                                                        dkService.updataDKInfo(contentValues);
                                                        mlist.clear();
                                                        mlist.addAll(dkService.getHistoryList("2", holdsService, signService));
                                                        adapter.notifyDataSetChanged();
                                                    } else {
                                                        Toast.makeText(HistoryActivity.this, message, Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(HistoryActivity.this, message, Toast.LENGTH_SHORT).show();
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

    }

    @Override
    protected void onResume() {
        super.onResume();
        changeDate(action);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myBroadcastRecivers);
    }

    private class MyBroadcastRecivers extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String actions = intent.getAction();
            if (actions.equals("com.znyg.znyp.sign")) {
                state = intent.getStringExtra("state");
                if (state!=null){
                    action = state;
                    changeDate(state);
                }
            }
        }
    }

    private void getMapServerPath(HistoryBean bean) {
        LoadingDialog.Builder loadBuilder = new LoadingDialog.Builder(HistoryActivity.this)
                .setMessage("正在请求数据")
                .setCancelable(true)
                .setCancelOutside(true);
        dialog = loadBuilder.create();
        dialog.show();
        Map<String, String> map = new HashMap<>();
        map.put("provinceName", bean.getProvince());
        map.put("cityName", bean.getCity());
        map.put("countyName", "");
        map.put("yearNum", "2021");
        RequestBody requestBodys = FormBody.create(MediaType.parse("application/json; charset=utf-8"), gson.toJson(map));
        DOkHttp.getInstance().uploadPost2ServerProgress(HistoryActivity.this, Configure.getMapServicePath, "Bearer " + SharePreferencesTools.getValue(HistoryActivity.this, "easyPhoto", "access_token", ""), requestBodys, new DOkHttp.MyCallBack() {
            @Override
            public void onFailure(Request request, IOException e) {
                Toast.makeText(HistoryActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
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
                        JSONObject extra = object.getJSONObject("extra");
                        JSONObject data = extra.getJSONObject("data");
                        String serverPath = data.getString("cityPath");
                        String leftTop = data.getString("leftTop");
                        String leftLower = data.getString("leftLower");
                        String rightTop = data.getString("rightTop");
                        String rightLower = data.getString("rightLower");
                        Intent intent = new Intent(HistoryActivity.this, MassifShowActivity.class);
                        intent.putExtra("HistoryBean", gson.toJson(bean));
                        intent.putExtra("serverPath", serverPath);
                        startActivity(intent);
                    } else {
                        Toast.makeText(HistoryActivity.this, message, Toast.LENGTH_SHORT).show();
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
