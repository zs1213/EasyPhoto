package com.example.lml.easyphoto.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lml.easyphoto.R;

import java.util.List;

public class AssureGrideAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private Context context;
	
	private List<AssureItem> items;
	
	public AssureGrideAdapter(Context context, List<AssureItem> items) {
		super();
		this.context = context;
		this.items = items;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		AssureItem item = items.get(position);
		int itemIcon = item.getItemIcon();
		String itemName = item.getItemName();
		String itemFlag = item.getItemFlag();
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.assure_item, null);
			holder = new ViewHolder();
			holder.itemIcon = (ImageView) convertView.findViewById(R.id.item_icon);
			holder.itemTitle = (TextView) convertView.findViewById(R.id.item_title);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.itemIcon.setImageResource(itemIcon);
        holder.itemTitle.setText(itemName);
        holder.itemTitle.setTag(itemFlag);
        convertView.setTag(holder);
		return convertView;
	}

	private class ViewHolder{
		public ImageView itemIcon;
		public TextView itemTitle;
	}
}
