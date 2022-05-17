package com.example.lml.easyphoto.FarmerInfo.houseHolds;

import android.content.Context;
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

public class HouseHoldsAdapter extends BaseAdapter {
    private List<HouseHoldsBean> mList;
    private Context context;
    private LayoutInflater inflater;

    public HouseHoldsAdapter(List<HouseHoldsBean> mList, Context context) {
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
            convertView = inflater.inflate(R.layout.item_holds, null);
            holder.iv_choose = convertView.findViewById(R.id.ih_iv_choose);
            holder.tv_massifName = convertView.findViewById(R.id.ih_tv_massifName);
//            holder.tv_insuredName = convertView.findViewById(R.id.ih_tv_name);
//            holder.tv_certificateId = convertView.findViewById(R.id.ih_tv_idcard);
            holder.tv_sumQuantity = convertView.findViewById(R.id.ih_tv_area);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        HouseHoldsBean bean = mList.get(position);
        boolean isChoose = bean.isChoose();
        if (isChoose) {
            LoadLocalImageUtil.getInstance().displayFromDrawable(R.drawable.znyg_checkok, holder.iv_choose);
        } else {
            LoadLocalImageUtil.getInstance().displayFromDrawable(R.drawable.znyg_check, holder.iv_choose);
        }
        holder.tv_massifName.setText(bean.getMassifName());
//        holder.tv_insuredName.setText(bean.getInsuredName());
//        holder.tv_certificateId.setText(bean.getCertificateId());
        holder.tv_sumQuantity.setText(bean.getInsureArea());
        return convertView;
    }

    class ViewHolder {
        ImageView iv_choose;
        TextView tv_massifName;
//        TextView tv_insuredName;
//        TextView tv_certificateId;
        TextView tv_sumQuantity;
    }
}
