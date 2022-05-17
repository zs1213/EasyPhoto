package com.example.lml.easyphoto.skp;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.lml.easyphoto.R;
import com.example.lml.easyphoto.camera.CameraPhotoBean;
import com.example.lml.easyphoto.camera.CameraService;
import com.example.lml.easyphoto.camera.LoadLocalImageUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class PhotoItemAdapter extends BaseAdapter {
    private List<CameraPhotoBean> mList;
    private Context context;
    private LayoutInflater inflater;
    private CameraService service;

    public PhotoItemAdapter(List<CameraPhotoBean> mList, Context context) {
        this.mList = mList;
        this.context = context;
        inflater = LayoutInflater.from(context);
        service = new CameraService(context);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
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
            convertView = inflater.inflate(R.layout.item_photo_item, null);
            holder.iv_photo = convertView.findViewById(R.id.item_iv_photo);
            holder.rl_choose = convertView.findViewById(R.id.item_rl_choose);
            holder.iv_choose = convertView.findViewById(R.id.item_iv_choose);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        CameraPhotoBean photoBean = mList.get(position);
        String fileName = photoBean.getFileName();
            LoadLocalImageUtil.getInstance().displayFromSDCard(photoBean.getFilePath(), holder.iv_photo);
        boolean isShow = photoBean.isShow();
        boolean isChoose = photoBean.isChoose();
        if (isShow) {
            holder.rl_choose.setVisibility(View.VISIBLE);
        } else {
            holder.rl_choose.setVisibility(View.GONE);
        }
        if (isChoose) {
            LoadLocalImageUtil.getInstance().displayFromDrawable(R.drawable.choose_ok, holder.iv_choose);
        } else {
            LoadLocalImageUtil.getInstance().displayFromDrawable(R.drawable.choose, holder.iv_choose);
        }
        holder.rl_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mList.get(position).setChoose(!mList.get(position).isChoose());
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    class ViewHolder {
        ImageView iv_photo;
        RelativeLayout rl_choose;
        ImageView iv_choose;
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
        context.startActivity(Intent.createChooser(shareIntent, "分享图片"));

    }
}
