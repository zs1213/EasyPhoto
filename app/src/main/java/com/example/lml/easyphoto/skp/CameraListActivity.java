package com.example.lml.easyphoto.skp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.lml.easyphoto.R;
import com.example.lml.easyphoto.camera.CameraPhotoBean;
import com.example.lml.easyphoto.camera.CameraService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CameraListActivity extends Activity {
    private CameraListAdapter adapter;
    private List<Map<String, Object>> mList;
    private CameraService service;
    private ExpandableListView el_photo;
    private TextView tv_quxiao;
    private LinearLayout lin_caozuo;
    private LinearLayout lin_share;
//    private LinearLayout lin_delete;
    private MyBroadcastReciver_notify myBroadcastReciver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_list);
        findView();
        initView();
    }

    private void findView() {
        IntentFilter filter = new IntentFilter("com.znyg.yipai.notify");
        myBroadcastReciver = new MyBroadcastReciver_notify();
        registerReceiver(myBroadcastReciver, filter);
        service = new CameraService(CameraListActivity.this);
        el_photo = findViewById(R.id.el_photo);
        tv_quxiao = findViewById(R.id.tv_quxiao);
        lin_caozuo = findViewById(R.id.lin_caozuo);
        lin_share = findViewById(R.id.lin_share);
//        lin_delete = findViewById(R.id.lin_delete);
        tv_quxiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < mList.size(); i++) {
                    List<CameraPhotoBean> changeList = (List<CameraPhotoBean>) mList.get(i).get("item");
                    for (int j = 0; j < changeList.size(); j++) {
                        ((List<CameraPhotoBean>) mList.get(i).get("item")).get(j).setShow(false);
                    }
                }
                adapter.notifyDataSetChanged();
                tv_quxiao.setVisibility(View.INVISIBLE);
                lin_caozuo.setVisibility(View.GONE);
            }
        });
        mList = new ArrayList<>();
        adapter = new CameraListAdapter(mList, CameraListActivity.this);
        adapter.setTongzhi(new CameraListAdapter.Tongzhi() {
            @Override
            public void tongzhi(boolean flag) {
                initView();
            }
        });
        adapter.setChoose(new CameraListAdapter.Choose() {
            @Override
            public void choose() {
                for (int i = 0; i < mList.size(); i++) {
                    List<CameraPhotoBean> changeList = (List<CameraPhotoBean>) mList.get(i).get("item");
                    for (int j = 0; j < changeList.size(); j++) {
                        ((List<CameraPhotoBean>) mList.get(i).get("item")).get(j).setShow(true);
                    }
                }
                adapter.notifyDataSetChanged();
                tv_quxiao.setVisibility(View.VISIBLE);
                lin_caozuo.setVisibility(View.VISIBLE);
            }
        });
        el_photo.setAdapter(adapter);
        el_photo.setGroupIndicator(null);
        lin_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> shareList = new ArrayList<String>();
                for (int i = 0; i < mList.size(); i++) {
                    List<CameraPhotoBean> cameraPhotoBeans = (List<CameraPhotoBean>) mList.get(i).get("item");
                    for (int j = 0; j < cameraPhotoBeans.size(); j++) {
                        if (cameraPhotoBeans.get(j).isChoose()) {
                            shareList.add(cameraPhotoBeans.get(j).getFilePath());
                        }
                    }
                }
                if (shareList.size() > 0) {
                    Uri uri[] = new Uri[shareList.size()];
                    for (int j = 0; j < shareList.size(); j++) {
                        File out = new File(shareList.get(j));
                        uri[j] = getImageContentUri(CameraListActivity.this, out);
                    }
                    shareWeChat(uri);
                } else {
                    Toast.makeText(CameraListActivity.this, "请选择分享的图片", Toast.LENGTH_SHORT).show();
                }
            }
        });
        /*lin_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < mList.size(); i++) {
                    List<CameraPhotoBean> cameraPhotoBeans = (List<CameraPhotoBean>) mList.get(i).get("item");
                    for (int j = 0; j < cameraPhotoBeans.size(); j++) {
                        if (cameraPhotoBeans.get(j).isChoose()) {
                            service.deleteNoFile(cameraPhotoBeans.get(j).getID());
                        }
                    }
                }
                initView();
                lin_caozuo.setVisibility(View.GONE);
                tv_quxiao.setVisibility(View.INVISIBLE);
            }
        });*/
        /*lv_main.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                List<CameraPhotoBean> photoList = service.getPhotoListByFilesName(mList.get(i));
                List<CameraPhotoBean> showList = new ArrayList<CameraPhotoBean>();
                for (int j = 0; j < photoList.size(); j++) {
                    File file = new File(photoList.get(j).getFilePath());
                    if (file.exists()){
                        showList.add(photoList.get(j));
                    }
                }
                if (showList.size()>0){
                    Intent intent = new Intent(CameraListActivity.this, ImagePagerActivity.class);
                    // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
                    intent.putExtra("image_urls", new Gson().toJson(showList));
                    intent.putExtra("image_index", 0);
                    startActivity(intent);
                }else {
                    Toast.makeText(CameraListActivity.this, "数据库中文件信息不在此文件夹中", Toast.LENGTH_SHORT).show();
                }

            }
        });*/
    }

    private void initView() {
        mList.clear();
        adapter.notifyDataSetChanged();
        mList.addAll(service.getFilesNameList());
        adapter.notifyDataSetChanged();
        for (int i = 0; i < adapter.getGroupCount(); i++) {
            el_photo.expandGroup(i);
        }
    }

    public void goBack(View view) {
        finish();
    }

    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        Uri uri = null;

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
                Uri baseUri = Uri.parse("content://media/external/images/media");
                uri = Uri.withAppendedPath(baseUri, "" + id);
            }

            cursor.close();
        }

        if (uri == null) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATA, filePath);
            uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        }

        return uri;
    }

    /**
     * 分享图片到 微信
     */
    private void shareWeChat(Uri[] uri) {
        Intent shareIntent = new Intent();
        // 1 Finals 2016-11-2 调用系统分享
        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // 2 Finals 2016-11-2 添加图片数组
        ArrayList<Uri> imageUris = new ArrayList<Uri>();
        for (int i = 0; i < uri.length; i++) {
            imageUris.add(uri[i]);
        }
        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
        shareIntent.setType("image/*");

        // 3 Finals 2016-11-2 指定选择微信。
        ComponentName mComponentName = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI");
        shareIntent.setComponent(mComponentName);
        // 4 Finals 2016-11-2 开始分享。
        startActivity(Intent.createChooser(shareIntent, "分享图片"));
    }
    //广播刷新消息
    private class MyBroadcastReciver_notify extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("com.znyg.yipai.notify")) {
                initView();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myBroadcastReciver);
    }
}
