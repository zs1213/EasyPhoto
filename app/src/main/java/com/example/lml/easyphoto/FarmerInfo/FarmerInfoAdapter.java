package com.example.lml.easyphoto.FarmerInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.lml.easyphoto.R;

import java.util.List;

public class FarmerInfoAdapter extends BaseAdapter {
    private List<FarmerInfoBean> mList;
    private Context context;
    private LayoutInflater inflater;

    public FarmerInfoAdapter(List<FarmerInfoBean> mList, Context context) {
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
            convertView = inflater.inflate(R.layout.item_farmerinfo, null);
            holder.tv_proposalNo = convertView.findViewById(R.id.ifi_tv_proposalNo);
            holder.tv_insuredName = convertView.findViewById(R.id.ifi_tv_insuredName);
            holder.tv_certificateId = convertView.findViewById(R.id.ifi_tv_certificateId);
            holder.tv_sumQuantity = convertView.findViewById(R.id.ifi_tv_sumQuantity);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        FarmerInfoBean bean = mList.get(position);
        holder.tv_proposalNo.setText(bean.getProposalNo());
        holder.tv_insuredName.setText(bean.getInsuredName());
        holder.tv_certificateId.setText(bean.getCertificateId());
        holder.tv_sumQuantity.setText(bean.getSumQuantity());
        return convertView;
    }

    class ViewHolder {
        TextView tv_proposalNo;
        TextView tv_insuredName;
        TextView tv_certificateId;
        TextView tv_sumQuantity;
    }
}
