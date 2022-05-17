package com.example.lml.easyphoto.history;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lml.easyphoto.FarmerInfo.houseHolds.HouseHoldsBean;
import com.example.lml.easyphoto.R;
import com.example.lml.easyphoto.camera.LoadLocalImageUtil;
import com.example.lml.easyphoto.massifShow.MassifShowActivity;
import com.example.lml.easyphoto.okHttpUtils.DOkHttp;
import com.example.lml.easyphoto.util.Configure;
import com.example.lml.easyphoto.util.LoadingDialog;
import com.example.lml.easyphoto.util.NoDoubleClickUtils;
import com.example.lml.easyphoto.util.SharePreferencesTools;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HistoryAdapter extends BaseAdapter {
    private List<HistoryBean> mList;
    private Context context;
    private LayoutInflater inflater;
    private LoadingDialog dialog;//提示框
    private Gson gson;
    public HistoryAdapter(List<HistoryBean> mList, Context context) {
        this.mList = mList;
        this.context = context;
        inflater = LayoutInflater.from(context);
        dialog = new LoadingDialog(context);
        gson = new Gson();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_history, null);
            holder.tv_name = convertView.findViewById(R.id.hs_tv_name);
            holder.tv_area = convertView.findViewById(R.id.hs_tv_area);
            holder.tv_proNo = convertView.findViewById(R.id.hs_tv_proNo);
            holder.tv_createTime= convertView.findViewById(R.id.hs_tv_createTime);
            holder.tv_state = convertView.findViewById(R.id.hs_tv_state);
            holder.iv_right = convertView.findViewById(R.id.hs_iv_right);
            holder. tv_massif= convertView.findViewById(R.id.hs_tv_massif);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        HistoryBean bean = mList.get(position);
        holder.tv_name.setText(bean.getUserName());
        holder.tv_area.setText(bean.getInsureArea());
        holder.tv_proNo.setText(bean.getProposalNo());
        String str0[] = bean.getCreatTime().split(" ");
        String str[] = str0[0].split("-");
        holder.tv_createTime.setText(str[1]+"月"+str[2]+"日");
        holder.iv_right.setVisibility(View.VISIBLE);
        switch (bean.getState()){
            case "0":
                holder.tv_state.setText("未标绘完成");
                holder.tv_state.setTextColor(Color.parseColor("#4A5175"));
                break;
            case "1":
                holder.tv_state.setText("未签字");
                holder.tv_state.setTextColor(Color.parseColor("#F59A50"));
                break;
            case "2":
                holder.tv_state.setText("已签字");
                holder.tv_state.setTextColor(Color.parseColor("#506DF5"));
                break;
            case "3":
                holder.iv_right.setVisibility(View.INVISIBLE);
                holder.tv_state.setText("已提交");
                holder.tv_state.setTextColor(Color.parseColor("#3BB06C"));
                break;
            default:
                break;
        }
        holder.tv_massif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NoDoubleClickUtils.isDoubleClick()){
                    getMapServerPath(bean);
                }else {
                    Toast.makeText(context, "请不要多次点击", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return convertView;
    }

    class ViewHolder {
        TextView tv_name;
        TextView tv_area;
        TextView tv_proNo;
        TextView tv_createTime;
        TextView tv_state;
        TextView tv_massif;
        ImageView iv_right;
    }
    private void getMapServerPath(HistoryBean bean) {
        LoadingDialog.Builder loadBuilder = new LoadingDialog.Builder(context)
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
        DOkHttp.getInstance().uploadPost2ServerProgress(context, Configure.getMapServicePath, "Bearer " + SharePreferencesTools.getValue(context, "easyPhoto", "access_token", ""), requestBodys, new DOkHttp.MyCallBack() {
            @Override
            public void onFailure(Request request, IOException e) {
                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
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
                        Intent intent = new Intent(context, MassifShowActivity.class);
                        intent.putExtra("HistoryBean", gson.toJson(bean));
                        intent.putExtra("serverPath", serverPath);
                        context.startActivity(intent);
                    } else {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
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
