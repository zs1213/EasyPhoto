package com.example.lml.easyphoto.dikuai.subList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lml.easyphoto.R;
import com.example.lml.easyphoto.camera.LoadLocalImageUtil;
import com.example.lml.easyphoto.customize.CustomizeDKBean;
import com.example.lml.easyphoto.dikuai.DKBean;

import java.util.List;

public class DKSubListAdapter extends BaseAdapter {
    private List<CustomizeDKBean> mList;
    private Context context;
    private LayoutInflater inflater;

    public DKSubListAdapter(List<CustomizeDKBean> mList, Context context) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.layout_dklist_item, null);
            holder.tv_userName = convertView.findViewById(R.id.item_tv_userName);
            holder.tv_dkName = convertView.findViewById(R.id.item_tv_dkName);
            holder.tv_area = convertView.findViewById(R.id.item_tv_area);
            holder.tv_crop = convertView.findViewById(R.id.item_tv_crop);
            holder.tv_remakes = convertView.findViewById(R.id.item_tv_remark);
            holder.lin_select = convertView.findViewById(R.id.item_lin_select);
            holder.iv_select = convertView.findViewById(R.id.item_iv_select);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        CustomizeDKBean dkBean = mList.get(position);
        if (dkBean.getState().equals("0")){
            holder.iv_select.setVisibility(View.VISIBLE);
            if (dkBean.isSelsect()) {
                LoadLocalImageUtil.getInstance().displayFromDrawable(R.drawable.znyg_checkok, holder.iv_select);
            } else {
                LoadLocalImageUtil.getInstance().displayFromDrawable(R.drawable.znyg_check, holder.iv_select);
            }
        }else {
            holder.iv_select.setVisibility(View.GONE);
        }

        holder.tv_userName.setText("用户名称：" + dkBean.getUserName());
        holder.tv_crop.setText("种植作物：" + dkBean.getCrop());
        holder.tv_dkName.setText("地块名称：" + dkBean.getDkName());
        holder.tv_area.setText("标绘面积：" + dkBean.getDrawArea());
        holder.tv_remakes.setText("备        注：" + dkBean.getRemarks());
        holder.lin_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dkBean.getState().equals("0")){
                    if (mList.get(position).isSelsect()) {
                        mList.get(position).setSelsect(false);
                    } else {
                        mList.get(position).setSelsect(true);
                    }
                    notifyDataSetChanged();
                }

            }
        });
        return convertView;
    }

    class ViewHolder {
        TextView tv_userName;
        TextView tv_dkName;
        TextView tv_area;
        TextView tv_crop;
        TextView tv_remakes;
        LinearLayout lin_select;
        ImageView iv_select;
    }
}
