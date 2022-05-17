package com.example.lml.easyphoto.dikuai.finish;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lml.easyphoto.FarmerInfo.houseHolds.HouseHoldsBean;
import com.example.lml.easyphoto.R;
import com.example.lml.easyphoto.camera.LoadLocalImageUtil;

import java.util.List;

public class FinishMassifAdapter extends BaseAdapter {
    private List<FinishBean> mList;
    private Context context;
    private LayoutInflater inflater;

    public FinishMassifAdapter(List<FinishBean> mList, Context context) {
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
            convertView = inflater.inflate(R.layout.item_finish_massif, null);
            holder.tv_massifName = convertView.findViewById(R.id.ifm_tv_massifName);
            //holder.tv_insuredName = convertView.findViewById(R.id.ifm_tv_name);
            holder.tv_sumQuantity = convertView.findViewById(R.id.ifm_tv_area);
            holder.lin_massif = convertView.findViewById(R.id.ifm_lin_massif);
            holder.iv_select = convertView.findViewById(R.id.ifm_iv_choose);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        FinishBean bean = mList.get(position);
        boolean isChoose = bean.isChoose();
        if (isChoose) {
            holder.lin_massif.setBackgroundResource(R.drawable.yj_lanse_biankuang);
            LoadLocalImageUtil.getInstance().displayFromDrawable(R.drawable.znyg_checkok,holder.iv_select);
        } else {
            holder.lin_massif.setBackgroundResource(R.drawable.yj_baise);
            LoadLocalImageUtil.getInstance().displayFromDrawable(R.drawable.znyg_check,holder.iv_select);
        }
        holder.tv_massifName.setText(bean.getMassifName());
        //holder.tv_insuredName.setText(bean.getUserName());
        holder.tv_sumQuantity.setText(bean.getPlotArea()+"亩");
        return convertView;
    }

    class ViewHolder {
        TextView tv_massifName;
        //        TextView tv_insuredName;
        TextView tv_sumQuantity;
        ImageView iv_select;
        LinearLayout lin_massif;
    }
}
