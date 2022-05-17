package com.example.lml.easyphoto.tongji;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lml.easyphoto.FarmerInfo.FarmerInfoBean;
import com.example.lml.easyphoto.R;
import com.example.lml.easyphoto.camera.LoadLocalImageUtil;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TongjiAdapter extends BaseAdapter {
    private List<TongjiBean> mList;
    private Context context;
    private LayoutInflater inflater;

    public TongjiAdapter(List<TongjiBean> mList, Context context) {
        this.mList = mList;
        this.context = context;
        inflater = LayoutInflater.from(context);
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
            convertView = inflater.inflate(R.layout.item_tongji, null);
            holder.tv_name = convertView.findViewById(R.id.tongji_tv_name);
            holder.iv_choose = convertView.findViewById(R.id.tongji_iv_choose);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_name.setText(mList.get(position).getFoldName());
        boolean isChoose = mList.get(position).isChoose();
        boolean isNoSubmit = mList.get(position).isNoSubmit();
        if (isNoSubmit){
            holder.tv_name.setTextColor(Color.parseColor("#000000"));
        }else {
            holder.tv_name.setTextColor(Color.parseColor("#696969"));
        }
        if (isChoose) {
            LoadLocalImageUtil.getInstance().displayFromDrawable(R.drawable.znyg_checkok, holder.iv_choose);
        } else {
            LoadLocalImageUtil.getInstance().displayFromDrawable(R.drawable.znyg_check, holder.iv_choose);
        }
        holder.iv_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mList.get(position).setChoose(!mList.get(position).isChoose());
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    class ViewHolder {
        TextView tv_name;
        ImageView iv_choose;
    }
}
