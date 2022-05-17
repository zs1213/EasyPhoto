package com.example.lml.easyphoto.arcgisMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import com.example.lml.easyphoto.R;

import java.io.File;

public class MapsActivity extends Activity {

    private String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private String path = absolutePath + "/easyPhoto/backgroundmap/";
    private String[] paths;
    private ImageView ivBack;
    private ListView lvMaps;
    private LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        findView();
    }

    private void findView(){
        File file = new File(path);
        if(file.exists()){
            File[] files = file.listFiles();
            paths = new String[files.length];
            for(int i = 0; i < files.length; i++){
                paths[i] = "easyPhoto" + files[i].getAbsolutePath().split("easyPhoto")[1];
            }
        }
        inflater = LayoutInflater.from(this);
        ivBack = (ImageView) findViewById(R.id.titlebar_left);
        lvMaps = (ListView) findViewById(R.id.lv_maps);
        lvMaps.setAdapter(new MapsAdapter());
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        lvMaps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("path",  absolutePath+"/"+paths[position]);
                setResult(101, intent);
                finish();
            }
        });
    }

    //listView适配类
    private class MapsAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            if(paths!=null && paths.length>0){
                return paths.length;
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return paths[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView == null){
                convertView = inflater.inflate(R.layout.item_maps, null);
                holder = new ViewHolder();
                convertView.setTag(holder);
            } else{
                holder = (ViewHolder)convertView.getTag();
            }
            holder.tvPath = (TextView) convertView.findViewById(R.id.tv_path);
            holder.tvPath.setText(paths[position]);
            return convertView;
        }
    }

    public class ViewHolder{
        public TextView tvPath;
    }
}
