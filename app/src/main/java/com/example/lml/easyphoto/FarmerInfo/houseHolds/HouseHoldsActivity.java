package com.example.lml.easyphoto.FarmerInfo.houseHolds;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lml.easyphoto.FarmerInfo.FarmerInfoActivity;
import com.example.lml.easyphoto.FarmerInfo.FarmerInfoBean;
import com.example.lml.easyphoto.R;
import com.example.lml.easyphoto.dikuai.DKBean;
import com.example.lml.easyphoto.dikuai.DKService;
import com.example.lml.easyphoto.dikuai.DiKuaiActivity;
import com.example.lml.easyphoto.history.HistoryActivity;
import com.example.lml.easyphoto.main.MenuActivity;
import com.example.lml.easyphoto.okHttpUtils.DOkHttp;
import com.example.lml.easyphoto.sign.DrawActivity;
import com.example.lml.easyphoto.util.Configure;
import com.example.lml.easyphoto.util.LoadingDialog;
import com.example.lml.easyphoto.util.SharePreferencesTools;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
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

public class HouseHoldsActivity extends Activity implements View.OnClickListener {
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
    private String insuredName;
    private List<HouseHoldsBean> mList;
    private HouseHoldsAdapter adapter;
    private ListView lv_main;
    private HoldsService service;
    private DKService dKService;
    private boolean isAllChoose = false;
    private Button btn_allChoose;
    private Button btn_plot;
    private TextView tv_title;
    private LoadingDialog dialog;//提示框

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_holds);
        initView();
        initData();
    }

    private void initView() {
        tv_title = findViewById(R.id.holds_tv_title);
        lv_main = findViewById(R.id.holds_lv_list);
        btn_allChoose = findViewById(R.id.holds_btn_all);
        btn_plot = findViewById(R.id.holds_btn_plot);
        btn_allChoose.setOnClickListener(this);
        btn_plot.setOnClickListener(this);
    }

    private void initData() {
        gson = new Gson();
        dialog = new LoadingDialog(HouseHoldsActivity.this);
        dKService = new DKService(this);
        service = new HoldsService(this, dKService);
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
        insuredName = getIntent().getStringExtra("insuredName");
        certificateId = getIntent().getStringExtra("certificateId");
        tv_title.setText(insuredName+"\n"+certificateId);
        mList = service.getCustomerListNoPlot(provinceName, cityName, countyName, countryName, villageName, proposalNo, certificateId);
        adapter = new HouseHoldsAdapter(mList, this);
        lv_main.setAdapter(adapter);
        lv_main.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mList.get(position).isChoose()) {
                    mList.get(position).setChoose(false);
                } else {
                    mList.get(position).setChoose(true);
                }
                adapter.notifyDataSetChanged();
            }
        });
        if (mList.size()==0){
            Toast.makeText(this, "该用户已标绘完成", Toast.LENGTH_SHORT).show();
        }
    }

    public void goBack(View view) {
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.holds_btn_all:
                if (isAllChoose) {
                    for (int i = 0; i < mList.size(); i++) {
                        mList.get(i).setChoose(false);
                    }
                    adapter.notifyDataSetChanged();
                    isAllChoose = false;
                } else {
                    for (int i = 0; i < mList.size(); i++) {
                        mList.get(i).setChoose(true);
                    }
                    adapter.notifyDataSetChanged();
                    isAllChoose = true;
                }
                break;
            case R.id.holds_btn_plot:
                for (int i = 0; i < mList.size(); i++) {
                    if (mList.get(i).isChoose()) {
                        getMapServerPath();
                        /*Intent intent = new Intent(HouseHoldsActivity.this, DiKuaiActivity.class);
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
                        intent.putExtra("json", gson.toJson(mList));
                        intent.putExtra("updataFlag", "0");
                        intent.putExtra("customerIds", "");
                        startActivity(intent);*/
                        return;
                    }
                }
                Toast.makeText(this, "请选择需要标绘的地块", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    private void getMapServerPath() {
        LoadingDialog.Builder loadBuilder = new LoadingDialog.Builder(HouseHoldsActivity.this)
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
        DOkHttp.getInstance().uploadPost2ServerProgress(HouseHoldsActivity.this, Configure.getMapServicePath, "Bearer " + SharePreferencesTools.getValue(HouseHoldsActivity.this, "easyPhoto", "access_token", ""), requestBodys, new DOkHttp.MyCallBack() {
            @Override
            public void onFailure(Request request, IOException e) {
                Toast.makeText(HouseHoldsActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
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
                        Intent intent = new Intent(HouseHoldsActivity.this, DiKuaiActivity.class);
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
                        intent.putExtra("json", gson.toJson(mList));
                        intent.putExtra("updataFlag", "0");
                        intent.putExtra("customerIds", "");
                        intent.putExtra("serverPath", serverPath);
//                        intent.putExtra("leftTop", leftTop);
//                        intent.putExtra("leftLower", leftLower);
//                        intent.putExtra("rightTop", rightTop);
//                        intent.putExtra("rightLower", rightLower);
                        startActivity(intent);
                    } else {
                        Toast.makeText(HouseHoldsActivity.this, message, Toast.LENGTH_SHORT).show();
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

    public void goHome(View view) {
        Intent intent = new Intent(HouseHoldsActivity.this, MenuActivity.class);
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
}
