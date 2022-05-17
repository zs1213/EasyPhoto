package com.example.lml.easyphoto.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import com.example.lml.easyphoto.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 点击editText弹出对话框进行单选，默认选择第一项
 *
 * @author 王占廷
 */
public class GetSingleSelectItem implements OnTouchListener {
    private Context mContext;
    private EditText etSelect;
    private String title;
    private String[] items;

    public GetSingleSelectItem(Context mContext, EditText etSelect, String title, String[] items, boolean seled) {
        this.mContext = mContext;
        this.etSelect = etSelect;
        this.title = title;
        this.items = items;
        //默认选择第一项
        if (seled) {
            etSelect.setText(items[0]);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.getinsured_name, null);
            dialog.setTitle(title);
            dialog.setView(layout)
                    .setCancelable(false)
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog accAlert = dialog.create();
            LinearLayout liner = (LinearLayout) layout.findViewById(R.id.search_liner);
            liner.setVisibility(View.GONE);
            final ListView accList = (ListView) layout.findViewById(R.id.accList);

            final List<Map<String, String>> itemData = new ArrayList<Map<String, String>>();
            Map<String, String> mp = null;
            for (int i = 0; i < items.length; i++) {
                mp = new HashMap<String, String>();
                mp.put("name", items[i]);
                itemData.add(mp);
            }
            SimpleAdapter czadapter = new SimpleAdapter(mContext, itemData,
                    android.R.layout.simple_list_item_1, new String[]{"name"},
                    new int[]{android.R.id.text1});
            accList.setAdapter(czadapter);
            accList.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                        long arg3) {
                    Map<String, String> m = itemData.get(arg2);
                    etSelect.setText(m.get("name"));
                    accAlert.dismiss();
                }
            });
            accAlert.show();
        }
        return false;
    }
}