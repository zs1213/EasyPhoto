package com.example.lml.easyphoto.dikuai.finish;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lml.easyphoto.FarmerInfo.houseHolds.HouseHoldsBean;
import com.example.lml.easyphoto.R;
import com.example.lml.easyphoto.camera.LoadLocalImageUtil;

import java.util.List;

public class FinishHoldsAdapter extends BaseAdapter {
    private List<HouseHoldsBean> mList;
    private Context context;
    private LayoutInflater inflater;

    public FinishHoldsAdapter(List<HouseHoldsBean> mList, Context context) {
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
            convertView = inflater.inflate(R.layout.item_finish_holds, null);
            holder.tv_massifName = convertView.findViewById(R.id.ifh_tv_massifName);
            holder.tv_sumQuantity = convertView.findViewById(R.id.ifh_tv_area);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        HouseHoldsBean bean = mList.get(position);
        holder.tv_massifName.setText(bean.getMassifName());
        holder.tv_sumQuantity.setText(bean.getInsureArea()+"亩");
        return convertView;
    }

    class ViewHolder {
        TextView tv_massifName;
        TextView tv_sumQuantity;
    }
}
