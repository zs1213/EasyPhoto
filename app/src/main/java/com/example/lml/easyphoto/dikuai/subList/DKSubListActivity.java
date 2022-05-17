package com.example.lml.easyphoto.dikuai.subList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.lml.easyphoto.R;
import com.example.lml.easyphoto.customize.CustomizeActivity;
import com.example.lml.easyphoto.customize.CustomizeAddressActivity;
import com.example.lml.easyphoto.customize.CustomizeDKBean;
import com.example.lml.easyphoto.customize.CustomizeDKService;
import com.example.lml.easyphoto.customize.CustomizeInfoActivity;
import com.example.lml.easyphoto.dikuai.DKBean;
import com.example.lml.easyphoto.dikuai.DKService;
import com.example.lml.easyphoto.history.HistoryActivity;
import com.example.lml.easyphoto.login.LoginActivity;
import com.example.lml.easyphoto.okHttpUtils.DOkHttp;
import com.example.lml.easyphoto.sign.DrawActivity;
import com.example.lml.easyphoto.util.Configure;
import com.example.lml.easyphoto.util.LoadingDialog;
import com.example.lml.easyphoto.util.SharePreferencesTools;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

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

public class DKSubListActivity extends Activity implements View.OnClickListener {
    private CustomizeDKService dkService;
    private ListView lv_main;
    private List<CustomizeDKBean> mList;
    private DKSubListAdapter adapter;
    private Button btn_allSelect, btn_unSelect, btn_submit;
//    private int size = 0;
//    private int subNumber = 0;
    private LoadingDialog dialog;//提示框
    private Gson gson;
    private MyBroadcastRecivers myBroadcastRecivers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.znyg.znyp.refushDk");
        myBroadcastRecivers = new MyBroadcastRecivers();
        this.registerReceiver(myBroadcastRecivers, intentFilter);
        setContentView(R.layout.activity_dksub_list);
        initView();
        initData();
    }

    private void initView() {
        lv_main = findViewById(R.id.dklist_lv_main);
        btn_allSelect = findViewById(R.id.dklist_btn_allSelect);
        btn_unSelect = findViewById(R.id.dklist_btn_unSelect);
        btn_submit = findViewById(R.id.dklist_btn_submit);
        btn_allSelect.setOnClickListener(this);
        btn_unSelect.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
    }

    private void initData() {
        gson = new Gson();
        dialog = new LoadingDialog(this);
        dkService = new CustomizeDKService(this);
        mList = dkService.getDKList("-1");
        adapter = new DKSubListAdapter(mList, this);
        lv_main.setAdapter(adapter);
        lv_main.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getMapServerPath(mList.get(position));
            }
        });
    }

    public void goBack(View view) {
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dklist_btn_allSelect:
                for (int i = 0; i < mList.size(); i++) {
                    if (mList.get(i).getState().equals("0")){
                        mList.get(i).setSelsect(true);
                    }else {
                        mList.get(i).setSelsect(false);
                    }
                }
                adapter.notifyDataSetChanged();
                break;
            case R.id.dklist_btn_unSelect:
                for (int i = 0; i < mList.size(); i++) {
                    mList.get(i).setSelsect(false);
                }
                adapter.notifyDataSetChanged();
                break;
            case R.id.dklist_btn_submit:
                subMit();
                break;
            default:
                break;
        }
    }
    private void getMapServerPath(CustomizeDKBean dkBean) {
        LoadingDialog.Builder loadBuilder = new LoadingDialog.Builder(DKSubListActivity.this)
                .setMessage("正在请求数据")
                .setCancelable(true)
                .setCancelOutside(true);
        dialog = loadBuilder.create();
        dialog.show();
        Map<String, String> map = new HashMap<>();
        map.put("provinceName", dkBean.getProvince());
        map.put("cityName", dkBean.getCity());
        map.put("countyName", "");
        map.put("yearNum", "2021");
        RequestBody requestBodys = FormBody.create(MediaType.parse("application/json; charset=utf-8"), gson.toJson(map));
        DOkHttp.getInstance().uploadPost2ServerProgress(DKSubListActivity.this, Configure.getMapServicePath, "Bearer " + SharePreferencesTools.getValue(DKSubListActivity.this, "easyPhoto", "access_token", ""), requestBodys, new DOkHttp.MyCallBack() {
            @Override
            public void onFailure(Request request, IOException e) {
                Toast.makeText(DKSubListActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
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
                        Intent intent = new Intent(DKSubListActivity.this, CustomizeInfoActivity.class);
                        intent.putExtra("provinceName", dkBean.getProvince());
                        intent.putExtra("provinceCode", dkBean.getProvinceCode());
                        intent.putExtra("cityName", dkBean.getCity());
                        intent.putExtra("cityCode", dkBean.getCityCode());
                        intent.putExtra("countyName", dkBean.getTown());
                        intent.putExtra("countyCode", dkBean.getTownCode());
                        intent.putExtra("countryName", dkBean.getCountryside());
                        intent.putExtra("countryCode", dkBean.getCountrysideCode());
                        intent.putExtra("villageName", dkBean.getVillage());
                        intent.putExtra("villageCode", dkBean.getVillageCode());
                        intent.putExtra("serverPath", serverPath);
                        intent.putExtra("json", gson.toJson(dkBean));
//                        intent.putExtra("leftTop", leftTop);
//                        intent.putExtra("leftLower", leftLower);
//                        intent.putExtra("rightTop", rightTop);
//                        intent.putExtra("rightLower", rightLower);
                        startActivity(intent);
                    } else {
                        Toast.makeText(DKSubListActivity.this, message, Toast.LENGTH_SHORT).show();
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

    private void subMit() {
        List<CustomizeDKBean> subList = new ArrayList<>();
        for (int i = 0; i < mList.size(); i++) {
            CustomizeDKBean dkBean = mList.get(i);
            if (dkBean.getState().equals("0")&&dkBean.isSelsect()) {
                subList.add(dkBean);
            }
        }
        if (subList.size() > 0) {
//            size = subList.size();
//            subNumber = 0;
            LoadingDialog.Builder loadBuilder = new LoadingDialog.Builder(DKSubListActivity.this)
                    .setMessage("正在上传地块信息...")
                    .setCancelable(true)
                    .setCancelOutside(true);
            dialog = loadBuilder.create();
            dialog.show();
            List<Map<String, String>> upList = new ArrayList<>();
            for (int i = 0; i < subList.size(); i++) {
                CustomizeDKBean dkBean = subList.get(i);
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
                map.put("remark", dkBean.getRemarks());
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
                map.put("signatureImg", "");
                map.put("villageCode", dkBean.getVillageCode());
                map.put("villageName", dkBean.getVillage());
                upList.add(map);
            }
            RequestBody requestBodys = FormBody.create(MediaType.parse("application/json; charset=utf-8"), gson.toJson(upList));
            DOkHttp.getInstance().uploadPost2ServerProgress(DKSubListActivity.this, Configure.subDiKuaiInfo, "Bearer " + SharePreferencesTools.getValue(DKSubListActivity.this, "easyPhoto", "access_token", ""), requestBodys, new DOkHttp.MyCallBack() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        int a = 0;
                    }

                    @Override
                    public void onResponse(String json) {
                        try {
                            JSONObject object = new JSONObject(json);
                            int code = object.getInt("code");
                            if (code == 0) {
                                for (int i = 0; i < subList.size(); i++) {
                                ContentValues contentValues = new ContentValues();
                                contentValues.put("taskId", subList.get(i).getTaskId());
                                contentValues.put("state", "1");
                                dkService.updataDKInfoState(contentValues);
                                }
                                    dialog.dismiss();
                                    mList.clear();
                                    mList.addAll(dkService.getDKList("-1"));
                                    adapter.notifyDataSetChanged();
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
        } else {
            Toast.makeText(this, "请选择要提交的地块", Toast.LENGTH_SHORT).show();
        }
    }
    private class MyBroadcastRecivers extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String actions = intent.getAction();
            if (actions.equals("com.znyg.znyp.refushDk")) {
                mList.clear();
                mList.addAll(dkService.getDKList("-1"));
                adapter.notifyDataSetChanged();
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myBroadcastRecivers);
    }
}
