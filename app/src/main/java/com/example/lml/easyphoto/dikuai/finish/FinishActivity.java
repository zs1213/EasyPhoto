package com.example.lml.easyphoto.dikuai.finish;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lml.easyphoto.FarmerInfo.FarmerInfoActivity;
import com.example.lml.easyphoto.FarmerInfo.houseHolds.HoldsService;
import com.example.lml.easyphoto.FarmerInfo.houseHolds.HouseHoldsActivity;
import com.example.lml.easyphoto.FarmerInfo.houseHolds.HouseHoldsBean;
import com.example.lml.easyphoto.R;
import com.example.lml.easyphoto.addressSelect.AddressSelectBean;
import com.example.lml.easyphoto.camera.AitsApplication;
import com.example.lml.easyphoto.dikuai.DKBean;
import com.example.lml.easyphoto.dikuai.DKService;
import com.example.lml.easyphoto.dikuai.DiKuaiActivity;
import com.example.lml.easyphoto.dikuai.MassifSnapService;
import com.example.lml.easyphoto.history.HistoryActivity;
import com.example.lml.easyphoto.main.MenuActivity;
import com.example.lml.easyphoto.okHttpUtils.DOkHttp;
import com.example.lml.easyphoto.sign.DrawActivity;
import com.example.lml.easyphoto.sign.SignBean;
import com.example.lml.easyphoto.sign.SignService;
import com.example.lml.easyphoto.util.Configure;
import com.example.lml.easyphoto.util.LoadingDialog;
import com.example.lml.easyphoto.util.SharePreferencesTools;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

public class FinishActivity extends Activity implements View.OnClickListener {
    private TextView tv_havePlot;
    private TextView tv_noPlot;
    private TextView tv_title;
    private DKService dkService;
    private Gson gson;
    private String provinceCode;
    private String provinceName;
    private String cityCode;
    private String cityName;
    private String countyCode;
    private String countyName;
    private String countryCode;
    private String countryName;
    private String villageCode;
    private String villageName;
    private String proposalNo;
    private String certificateId;
    private List<HouseHoldsBean> holdsList;
    private List<HouseHoldsBean> showHoldsList;
    private List<FinishBean> plotList;
    private HoldsService holdsService;
    private double holdsNumber = 0.0;
    private double havePoltNumber = 0.0;
    private double noPoltNumber = 0.0;
    private ListView lv_holds;
    private ListView lv_massif;
    private FinishHoldsAdapter finishHoldsAdapter;
    private FinishMassifAdapter finishMassifAdapter;
    private String upData[] = new String[]{"修改", "删除"};
    private Button btn_next;
    private Button btn_updata;
    private Button btn_delete;
    private String flag = "";
    private LoadingDialog dialog;//提示框
    private int choosePosition = 0;
    private MassifSnapService snapService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);
        initView();
        initData();
    }

    private void initView() {
        gson = new Gson();
        dialog = new LoadingDialog(FinishActivity.this);
        tv_havePlot = findViewById(R.id.finish_tv_havePlot);
        tv_noPlot = findViewById(R.id.finish_tv_noPlot);
        tv_title = findViewById(R.id.finish_tv_title);
        lv_holds = findViewById(R.id.finish_lv_holds);
        lv_massif = findViewById(R.id.finish_lv_massif);
        btn_next = findViewById(R.id.finish_btn_next);
        btn_updata = findViewById(R.id.finish_btn_updata);
        btn_delete = findViewById(R.id.finish_btn_delete);
        btn_next.setOnClickListener(this);
        btn_updata.setOnClickListener(this);
        btn_delete.setOnClickListener(this);
    }

    private void initData() {
        dkService = new DKService(this);
        holdsService = new HoldsService(this, dkService);
        snapService = new MassifSnapService(this);
        provinceCode = getIntent().getStringExtra("provinceCode");
        provinceName = getIntent().getStringExtra("provinceName");
        cityCode = getIntent().getStringExtra("cityCode");
        cityName = getIntent().getStringExtra("cityName");
        countyCode = getIntent().getStringExtra("countyCode");
        countyName = getIntent().getStringExtra("countyName");
        countryCode = getIntent().getStringExtra("countryCode");
        countryName = getIntent().getStringExtra("countryName");
        villageCode = getIntent().getStringExtra("villageCode");
        villageName = getIntent().getStringExtra("villageName");
        proposalNo = getIntent().getStringExtra("proposalNo");
        certificateId = getIntent().getStringExtra("certificateId");
        holdsList = holdsService.getCustomerList(provinceName, cityName, countyName, countryName, villageName, proposalNo, certificateId);
        plotList = dkService.getFinishList(provinceName, cityName, countyName, countryName, villageName, proposalNo, certificateId);
        holdsNumber = holdsService.getSumHolds(provinceName, cityName, countyName, countryName, villageName, proposalNo, certificateId);
        havePoltNumber = dkService.getSumPlot(provinceName, cityName, countyName, countryName, villageName, proposalNo, certificateId);
        if ((holdsNumber - (holdsNumber * 0.02)) <= havePoltNumber && havePoltNumber <= (holdsNumber + (holdsNumber * 0.02))) {
            havePoltNumber = holdsNumber;
            noPoltNumber = 0;
        } else {
            noPoltNumber = holdsNumber - havePoltNumber;
        }
        havePoltNumber = formatDouble2(havePoltNumber);
        noPoltNumber = formatDouble2(noPoltNumber);
        tv_havePlot.setText(havePoltNumber + "");
        tv_noPlot.setText(noPoltNumber + "");
        tv_title.setText(plotList.get(0).getUserName() + "\n" + certificateId);
        showHoldsList = new ArrayList<>();
        finishHoldsAdapter = new FinishHoldsAdapter(showHoldsList, this);
        plotList.get(0).setChoose(true);
        finishMassifAdapter = new FinishMassifAdapter(plotList, this);
        lv_holds.setAdapter(finishHoldsAdapter);
        lv_massif.setAdapter(finishMassifAdapter);
        for (int i = 0; i < holdsList.size(); i++) {
            HouseHoldsBean bean = holdsList.get(i);
            if (plotList.get(choosePosition).getCustomerIds().contains(bean.getId())) {
                showHoldsList.add(bean);
            }
        }
        finishHoldsAdapter.notifyDataSetChanged();
        lv_massif.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showHoldsList.clear();
                finishHoldsAdapter.notifyDataSetChanged();
                for (int i = 0; i < holdsList.size(); i++) {
                    HouseHoldsBean bean = holdsList.get(i);
                    if (plotList.get(position).getCustomerIds().contains(bean.getId())) {
                        showHoldsList.add(bean);
                    }
                }
                finishHoldsAdapter.notifyDataSetChanged();
                lv_holds.smoothScrollToPositionFromTop(0, 0);
                choosePosition = position;
                for (int i = 0; i < plotList.size(); i++) {
                    if (i == position) {
                        plotList.get(i).setChoose(true);
                    } else {
                        plotList.get(i).setChoose(false);
                    }
                }
                finishMassifAdapter.notifyDataSetChanged();
            }
        });
        if (noPoltNumber == 0 && isFinish()) {
            btn_next.setText("签字");
        } else {
            btn_next.setText("未标绘");
        }
    }

    public boolean isFinish() {
        for (int i = 0; i < holdsList.size(); i++) {
            if (!holdsList.get(i).isPlot()) {
                return false;
            }
        }
        return true;
    }

    public void goBack(View view) {
        goHistory();
    }

    public void goHistory() {
        String state = "-1";
        if (noPoltNumber == 0 && isFinish()) {
            SignBean bean = new SignService(FinishActivity.this).getSignBean(proposalNo, certificateId);
            if (bean != null) {
                state = "2";
            } else {
                state = "1";
            }
        }else {
            if (plotList.size()>0){
                state = "0";
            }else {
                state = "-1";
            }
        }
        Intent intent_dk = new Intent(FinishActivity.this, HistoryActivity.class);
        intent_dk.putExtra("state", state);
        startActivity(intent_dk);
        finish();
        Intent intentss = new Intent();
        intentss.setAction("com.znyg.znyp.sign");
        intentss.putExtra("state", state);
        sendBroadcast(intentss);
    }

    private double formatDouble2(double d) {
        BigDecimal bigDecimal = new BigDecimal(d);
        double bg = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return bg;
    }

    private void getMapServerPath(List<HouseHoldsBean> dataList, String customerIds) {
        LoadingDialog.Builder loadBuilder = new LoadingDialog.Builder(FinishActivity.this)
                .setMessage("正在请求数据")
                .setCancelable(true)
                .setCancelOutside(true);
        dialog = loadBuilder.create();
        dialog.show();
        Map<String, String> map = new HashMap<>();
        map.put("provinceName", provinceName);
        map.put("cityName", cityName);
        map.put("countyName", "");
        map.put("yearNum", "2021");
        RequestBody requestBodys = FormBody.create(MediaType.parse("application/json; charset=utf-8"), gson.toJson(map));
        DOkHttp.getInstance().uploadPost2ServerProgress(FinishActivity.this, Configure.getMapServicePath, "Bearer " + SharePreferencesTools.getValue(FinishActivity.this, "easyPhoto", "access_token", ""), requestBodys, new DOkHttp.MyCallBack() {
            @Override
            public void onFailure(Request request, IOException e) {
                Toast.makeText(FinishActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
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
                        Intent intent = new Intent(FinishActivity.this, DiKuaiActivity.class);
                        intent.putExtra("provinceName", provinceName);
                        intent.putExtra("provinceCode", provinceCode);
                        intent.putExtra("cityName", cityName);
                        intent.putExtra("cityCode", cityCode);
                        intent.putExtra("countyName", countyName);
                        intent.putExtra("countyCode", countyCode);
                        intent.putExtra("countryName", countryName);
                        intent.putExtra("countryCode", countryCode);
                        intent.putExtra("villageName", villageName);
                        intent.putExtra("villageCode", villageCode);
                        intent.putExtra("json", gson.toJson(dataList));
                        intent.putExtra("updataFlag", "1");
                        intent.putExtra("customerIds", customerIds);
                        intent.putExtra("serverPath", serverPath);
                        startActivity(intent);
                    } else {
                        Toast.makeText(FinishActivity.this, message, Toast.LENGTH_SHORT).show();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.finish_btn_updata:
                if (plotList.size() > 0) {
                    List<HouseHoldsBean> beans = new ArrayList<>();
                    for (int i = 0; i < holdsList.size(); i++) {
                        if (plotList.get(choosePosition).getCustomerIds().contains(holdsList.get(i).getId())) {
                            HouseHoldsBean bean = holdsList.get(i);
                            bean.setChoose(true);
                            beans.add(bean);
                        }
                    }
                    for (int i = 0; i < holdsList.size(); i++) {
                        if (!plotList.get(choosePosition).getCustomerIds().contains(holdsList.get(i).getId()) && !holdsList.get(i).isPlot()) {
                            HouseHoldsBean bean = holdsList.get(i);
                            bean.setChoose(false);
                            beans.add(bean);
                        }
                    }
                    getMapServerPath(beans, plotList.get(choosePosition).getCustomerIds());
                }

                break;
            case R.id.finish_btn_delete:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(FinishActivity.this);
                builder1.setTitle("提醒").setMessage("确定要删除选择的标绘地块么？")
                        .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogs, int which) {
                                dkService.deleteDKInfoByCustomerIds(plotList.get(choosePosition).getCustomerIds());
                                holdsList.clear();
                                plotList.clear();
                                showHoldsList.clear();
                                finishHoldsAdapter.notifyDataSetChanged();
                                finishMassifAdapter.notifyDataSetChanged();
                                holdsList.addAll(holdsService.getCustomerList(provinceName, cityName, countyName, countryName, villageName, proposalNo, certificateId));
                                plotList.addAll(dkService.getFinishList(provinceName, cityName, countyName, countryName, villageName, proposalNo, certificateId));
                                holdsNumber = holdsService.getSumHolds(provinceName, cityName, countyName, countryName, villageName, proposalNo, certificateId);
                                havePoltNumber = dkService.getSumPlot(provinceName, cityName, countyName, countryName, villageName, proposalNo, certificateId);
                                if ((holdsNumber - (holdsNumber * 0.02)) <= havePoltNumber && havePoltNumber <= (holdsNumber + (holdsNumber * 0.02))) {
                                    havePoltNumber = holdsNumber;
                                    noPoltNumber = 0;
                                } else {
                                    noPoltNumber = holdsNumber - havePoltNumber;
                                }
                                havePoltNumber = formatDouble2(havePoltNumber);
                                noPoltNumber = formatDouble2(noPoltNumber);
                                tv_havePlot.setText(havePoltNumber + "");
                                tv_noPlot.setText(noPoltNumber + "");
                                if (noPoltNumber == 0 && isFinish()) {
                                    btn_next.setText("签字");
                                } else {
                                    btn_next.setText("未标绘");
                                }
                                if (plotList.size() > 0) {
                                    plotList.get(0).setChoose(true);
                                }
                                choosePosition = 0;
                                for (int i = 0; i < holdsList.size(); i++) {
                                    HouseHoldsBean bean = holdsList.get(i);
                                    if (plotList.size() > 0) {
                                        if (plotList.get(choosePosition).getCustomerIds().contains(bean.getId())) {
                                            showHoldsList.add(bean);
                                        }
                                    }
                                }
                                finishHoldsAdapter.notifyDataSetChanged();
                                finishMassifAdapter.notifyDataSetChanged();
                                if (plotList.size() == 0) {
                                    deleteHaveDraw();
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
            case R.id.finish_btn_next:
                if (noPoltNumber == 0 && isFinish()) {
                    Intent intent = new Intent(FinishActivity.this, DrawActivity.class);
                    intent.putExtra("proposalNo", proposalNo);
                    intent.putExtra("certificateId", certificateId);
                    intent.putExtra("from", "finish");
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(FinishActivity.this, HouseHoldsActivity.class);
                    intent.putExtra("provinceName", provinceName);
                    intent.putExtra("provinceCode", provinceCode);
                    intent.putExtra("cityName", cityName);
                    intent.putExtra("cityCode", cityCode);
                    intent.putExtra("countyName", countyName);
                    intent.putExtra("countyCode", countyCode);
                    intent.putExtra("countryName", countryName);
                    intent.putExtra("countryCode", countryCode);
                    intent.putExtra("villageName", villageName);
                    intent.putExtra("villageCode", villageCode);
                    intent.putExtra("proposalNo", proposalNo);
                    intent.putExtra("certificateId", certificateId);
                    intent.putExtra("insuredName", holdsList.get(0).getInsuredName());
                    startActivity(intent);
                }
                break;
            default:
                break;
        }
    }

    public void goHome(View view) {
        Intent intent = new Intent(FinishActivity.this, MenuActivity.class);
//        intent.putExtra("province",provinceCode);
//        intent.putExtra("provinceName",provinceName);
//        intent.putExtra("city",cityCode);
//        intent.putExtra("cityName",cityName);
//        intent.putExtra("county",countyCode);
//        intent.putExtra("countyName",countyName);
//        intent.putExtra("country",countryCode);
//        intent.putExtra("countryName",countryName);
//        intent.putExtra("village",villageCode);
//        intent.putExtra("villageName",villageName);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        holdsList.clear();
        plotList.clear();
        choosePosition = 0;
        holdsList.addAll(holdsService.getCustomerList(provinceName, cityName, countyName, countryName, villageName, proposalNo, certificateId));
        plotList.addAll(dkService.getFinishList(provinceName, cityName, countyName, countryName, villageName, proposalNo, certificateId));
        holdsNumber = holdsService.getSumHolds(provinceName, cityName, countyName, countryName, villageName, proposalNo, certificateId);
        havePoltNumber = dkService.getSumPlot(provinceName, cityName, countyName, countryName, villageName, proposalNo, certificateId);
        if ((holdsNumber - (holdsNumber * 0.02)) <= havePoltNumber && havePoltNumber <= (holdsNumber + (holdsNumber * 0.02))) {
            havePoltNumber = holdsNumber;
            noPoltNumber = 0;
        } else {
            noPoltNumber = holdsNumber - havePoltNumber;
        }
        havePoltNumber = formatDouble2(havePoltNumber);
        noPoltNumber = formatDouble2(noPoltNumber);
        tv_havePlot.setText(havePoltNumber + "");
        tv_noPlot.setText(noPoltNumber + "");
        tv_title.setText(plotList.get(0).getUserName() + "\n" + certificateId);
        showHoldsList.clear();
        plotList.get(0).setChoose(true);
        for (int i = 0; i < holdsList.size(); i++) {
            HouseHoldsBean bean = holdsList.get(i);
            if (plotList.get(choosePosition).getCustomerIds().contains(bean.getId())) {
                showHoldsList.add(bean);
            }
        }
        finishHoldsAdapter.notifyDataSetChanged();
        finishMassifAdapter.notifyDataSetChanged();
        if (noPoltNumber == 0 && isFinish()) {
            btn_next.setText("签字");
        } else {
            btn_next.setText("未标绘");
        }
        super.onResume();
    }

    private void deleteHaveDraw() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        LoadingDialog.Builder loadBuilder = new LoadingDialog.Builder(FinishActivity.this)
                .setMessage("正在更新信息")
                .setCancelable(true)
                .setCancelOutside(true);
        dialog = loadBuilder.create();
        dialog.show();
        MultipartBody.Builder mb = new MultipartBody.Builder();
        mb.setType(MultipartBody.FORM);
        mb.addFormDataPart("provinceName", provinceName);
        mb.addFormDataPart("cityName", cityName);
        mb.addFormDataPart("countyName", countyName);
        mb.addFormDataPart("countryName", countryName);
        mb.addFormDataPart("villageName", villageName);
        mb.addFormDataPart("certificateId", certificateId);
        RequestBody requestBody = mb.build();
        DOkHttp.getInstance().uploadPost2ServerProgress(FinishActivity.this, Configure.massifSnapDelete, "Bearer " + SharePreferencesTools.getValue(FinishActivity.this, "easyPhoto", "access_token", ""), requestBody, new DOkHttp.MyCallBack() {
            @Override
            public void onFailure(Request request, IOException e) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                Toast.makeText(FinishActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String json) {
                try {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    JSONObject object = new JSONObject(json);
                    int code = object.getInt("code");
                    String message = object.getString("message");
                    if (code == 0) {
                        if (snapService.deleteCustomer(provinceName, cityName, countyName, countryName, villageName, certificateId)) {
                            Toast.makeText(FinishActivity.this, "数据更新成功", Toast.LENGTH_SHORT).show();
                            goHistory();
                        }
                    } else {
                        Toast.makeText(FinishActivity.this, message, Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 返回键
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            goHistory();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
