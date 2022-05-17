package com.example.lml.easyphoto.skp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.lml.easyphoto.R;
import com.example.lml.easyphoto.addressSelect.AddressSelectBean;
import com.example.lml.easyphoto.camera.CameraPhotoBean;
import com.example.lml.easyphoto.camera.CameraService;
import com.example.lml.easyphoto.camera.LoadLocalImageUtil;
import com.example.lml.easyphoto.tongji.TongJiActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;

/**
 * 图片查看器
 */
public class ImagePagerActivity extends FragmentActivity {
    private static final String STATE_POSITION = "STATE_POSITION";
    public static final String EXTRA_IMAGE_INDEX = "image_index";
    public static final String EXTRA_IMAGE_URLS = "image_urls";
    private ProgressDialog progressDialog;
    private Gson gson;
    private List<AddressSelectBean> beanList;
    private HackyViewPager mPager;
    private int pagerPosition;
    private TextView indicator;
    private TextView tv_share;
    private ImageView iv_choose;
    private List<Boolean> flagList = new ArrayList<Boolean>();
    private List<CameraPhotoBean> diao;
    private String comefrom = "";
    private TextView tv_delete;
    private CameraService service;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_detail_pager);
        gson = new Gson();
        service = new CameraService(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pagerPosition = getIntent().getIntExtra(EXTRA_IMAGE_INDEX, 0);
        String json = getIntent().getStringExtra(EXTRA_IMAGE_URLS);
        comefrom = getIntent().getStringExtra("from");
        diao = new Gson().fromJson(json, new TypeToken<List<CameraPhotoBean>>() {
        }.getType());
        final List<String> urls = new ArrayList<String>();
        for (int i = 0; i < diao.size(); i++) {
            urls.add(diao.get(i).getFilePath());
            flagList.add(false);
        }
        mPager =  findViewById(R.id.pager);
        ImagePagerAdapter mAdapter = new ImagePagerAdapter(urls);
        mPager.setAdapter(mAdapter);
        indicator =  findViewById(R.id.indicator);
        tv_share =  findViewById(R.id.share);
        tv_delete =  findViewById(R.id.delete);
        iv_choose =  findViewById(R.id.iv_choose);
        tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> shareList = new ArrayList<String>();
                for (int i = 0; i < flagList.size(); i++) {
                    if (flagList.get(i)) {
                        shareList.add(urls.get(i));
                    }
                }
                if (shareList.size() > 0) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(ImagePagerActivity.this);
                    builder1.setTitle("提醒").setCancelable(true).setMessage("确定删除吗？").setNeutralButton("删除", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            for (int j = 0; j < shareList.size(); j++) {
                                File out = new File(shareList.get(j));
                                service.deleteFile(shareList.get(j));
                                File file = new File(shareList.get(j));
                                if (file.exists()) {
                                    file.delete();
                                }
                            }
                            dialog.dismiss();
                            finish();
                        }
                    })
                            .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    builder1.create().show();

                } else {
                    Toast.makeText(ImagePagerActivity.this, "请选择删除的图片", Toast.LENGTH_SHORT).show();
                }
            }
        });
        iv_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flagList.get(pagerPosition)) {
                    flagList.set(pagerPosition, false);
                    LoadLocalImageUtil.getInstance().displayFromDrawable(R.mipmap.radio, iv_choose);
                } else {
                    Toast.makeText(ImagePagerActivity.this, "可选择多张", Toast.LENGTH_SHORT).show();
                    flagList.set(pagerPosition, true);
                    LoadLocalImageUtil.getInstance().displayFromDrawable(R.mipmap.radio_active, iv_choose);
                }

            }
        });
        tv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> shareList = new ArrayList<String>();
                for (int i = 0; i < flagList.size(); i++) {
                    if (flagList.get(i)) {
                        shareList.add(urls.get(i));
                    }
                }
                if (shareList.size() > 0) {
                    Uri uri[] = new Uri[shareList.size()];
                    for (int j = 0; j < shareList.size(); j++) {
                        File out = new File(shareList.get(j));
                        uri[j] = getImageContentUri(ImagePagerActivity.this, out);
                    }
                    shareWeChat(uri);
                } else {
                    Toast.makeText(ImagePagerActivity.this, "请选择分享的图片", Toast.LENGTH_SHORT).show();
                }

            }
        });
        CharSequence text = getString(R.string.viewpager_indicator, 1, urls.size());
        indicator.setText(text);
        // 更新下标
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageSelected(int arg0) {
                pagerPosition = arg0 % urls.size();
                CharSequence text = getString(R.string.viewpager_indicator, arg0 % urls.size() + 1, urls.size());
                indicator.setText(text);
                if (flagList.get(pagerPosition)) {
                    LoadLocalImageUtil.getInstance().displayFromDrawable(R.mipmap.radio_active, iv_choose);
                } else {
                    LoadLocalImageUtil.getInstance().displayFromDrawable(R.mipmap.radio, iv_choose);
                }
            }

        });
        if (savedInstanceState != null) {
            pagerPosition = savedInstanceState.getInt(STATE_POSITION);
        }
        if (urls.size() < 3) {
            mPager.setCurrentItem(pagerPosition);
        } else {
            mPager.setCurrentItem(urls.size() * 100);
        }
        if (comefrom!=null&&comefrom.equals("dkLook")){
            tv_delete.setVisibility(View.VISIBLE);
        }else {
            tv_delete.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_POSITION, mPager.getCurrentItem());
    }

    private class ImagePagerAdapter extends PagerAdapter {

        public List<String> fileList;
        public List<PhotoView> mList;

        public ImagePagerAdapter(final List<String> fileList) {
            this.fileList = fileList;
            mList = new ArrayList<PhotoView>();
            for (int i = 0; i < fileList.size(); i++) {
                PhotoView imageView = new PhotoView(ImagePagerActivity.this);
                final String url = fileList.get(i);
                if (fileList.get(i).contains("http")) {
                    LoadLocalImageUtil.getInstance().displayFromHttp(url, imageView);
                } else {
                    LoadLocalImageUtil.getInstance().displayFromSDCard(url, imageView);
                }
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                mList.add(imageView);
            }
        }

        @Override
        public int getCount() {
            int count = 0;
            if (mList.size() < 3) {
                count = mList.size();
            } else {
                count = Integer.MAX_VALUE;
            }
            return count;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup view, int position, Object object) {
            //view.removeView(mList.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            position %= mList.size();
            if (position < 0) {
                position = mList.size() + position;
            }
            ImageView view = mList.get(position);
            //如果View已经在之前添加到了一个父组件，则必须先remove，否则会抛出IllegalStateException。

            ViewParent viewParent = view.getParent();
            if (viewParent != null) {
                ViewGroup parent = (ViewGroup) viewParent;
                parent.removeView(view);
            }
            container.addView(view);
            return view;
        }
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
     * 删除照片
     */
    public void deletePhoto() {

    }







}
