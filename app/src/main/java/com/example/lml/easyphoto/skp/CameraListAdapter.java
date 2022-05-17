package com.example.lml.easyphoto.skp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lml.easyphoto.R;
import com.example.lml.easyphoto.camera.C;
import com.example.lml.easyphoto.camera.CameraFileUpload;
import com.example.lml.easyphoto.camera.CameraPhotoBean;
import com.example.lml.easyphoto.camera.CameraService;
import com.example.lml.easyphoto.okHttpUtils.DOkHttp;
import com.example.lml.easyphoto.util.SharePreferencesTools;
import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;

//import com.znyg.yipai.camera.YiPaiActivity;

/**
 * Created by 陈忠磊 on 2017/9/14.
 */

public class CameraListAdapter extends BaseExpandableListAdapter {
    private List<Map<String, Object>> mList;
    private Context mContext;
    private LayoutInflater inflater;
    private CameraService service;
    Tongzhi tongzhi;
    Choose choose;
    private ProgressDialog progressDialog, progressDialog1;

    public CameraListAdapter(List<Map<String, Object>> mList, Context mContext) {
        this.mList = mList;
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
        service = new CameraService(mContext);
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog1 = new ProgressDialog(mContext);
        progressDialog1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    public void setTongzhi(Tongzhi tongzhi) {
        this.tongzhi = tongzhi;
    }

    public void setChoose(Choose choose) {
        this.choose = choose;
    }

    @Override
    public int getGroupCount() {
        return mList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        List<CameraPhotoBean> photoBeans = (List<CameraPhotoBean>) mList.get(groupPosition).get("item");
        if (photoBeans.size() == 0) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupPosition;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder holder = null;
        if (convertView == null) {
            holder = new GroupViewHolder();
            convertView = inflater.inflate(R.layout.item_title, null);
            holder.tv_filename = convertView.findViewById(R.id.tv_fileName);
            holder.tv_numbei = convertView.findViewById(R.id.tv_number);
            holder.tv_NoNumber = convertView.findViewById(R.id.tv_Nonumber);
            holder.lin_sub = convertView.findViewById(R.id.lin_sub);
            convertView.setTag(holder);
        } else {
            holder = (GroupViewHolder) convertView.getTag();
        }
        holder.tv_filename.setText(mList.get(groupPosition).get("filesName").toString());
        List<CameraPhotoBean> photoBeans = (List<CameraPhotoBean>) mList.get(groupPosition).get("item");
        holder.tv_numbei.setText(photoBeans.size() + "个文件");
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder holder = null;
        if (convertView == null) {
            holder = new ChildViewHolder();
            convertView = inflater.inflate(R.layout.item_photo, null);
            holder.chidren_item = convertView.findViewById(R.id.item_gv_photo);
            convertView.setTag(holder);
        } else {
            holder = (ChildViewHolder) convertView.getTag();
        }
        final PhotoItemAdapter adapter = new PhotoItemAdapter((List<CameraPhotoBean>) mList.get(groupPosition).get("item"), mContext);
        holder.chidren_item.setAdapter(adapter);
        holder.chidren_item.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mContext, ImagePagerActivity.class);
                // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
                intent.putExtra("image_urls", new Gson().toJson((List<CameraPhotoBean>) mList.get(groupPosition).get("item")));
                intent.putExtra("image_index", position);
                mContext.startActivity(intent);
            }
        });
        holder.chidren_item.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                choose.choose();
                return false;
            }
        });
        return convertView;
    }

    class GroupViewHolder {
        TextView tv_filename;
        TextView tv_numbei;
        TextView tv_NoNumber;
        LinearLayout lin_sub;
    }

    class ChildViewHolder {
        GridView chidren_item;

    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    /*@Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.item_acl, null);
            holder.tv_name = (TextView) view.findViewById(R.id.item_acl_tv_name);
            holder.tv_number = (TextView) view.findViewById(R.id.item_acl_tv_number);
            holder.iv_subMit = (ImageView) view.findViewById(R.id.item_acl_tv_subMit);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.tv_name.setText(mList.get(i));
        final List<CameraPhotoBean> subList = service.getPhotoListByFilesNameAndState(mList.get(i), "0");
        final int number = subList.size();
        if (number == 0) {
            holder.tv_number.setText("已存储在云端");
            holder.tv_number.setTextColor(Color.parseColor("#000000"));
            LoadLocalImageUtil.getInstance().displayFromDrawable(R.drawable.u150, holder.iv_subMit);
        } else {
            holder.tv_number.setText("仍有" + number + "张照片未存云端");
            holder.tv_number.setTextColor(Color.parseColor("#ff0000"));
            LoadLocalImageUtil.getInstance().displayFromDrawable(R.drawable.u154, holder.iv_subMit);
        }
        holder.iv_subMit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (number != 0) {
                    final List<CameraPhotoBean> mBeen = new ArrayList<CameraPhotoBean>();
                    for (int j = 0; j < subList.size(); j++) {
                        File file = new File(subList.get(j).getFilePath());
                        if (file.exists()) {
                            mBeen.add(subList.get(j));
                        }
                    }
                    if (mBeen.size() > 0) {
                        String userName = SharePreferencesTools.getValue(mContext, "ep", "userName", "");
                        String passWord = SharePreferencesTools.getValue(mContext, "ep", "passWord", "");
                        if (userName.equals("")) {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                            LayoutInflater inflater = LayoutInflater.from(mContext);
                            View layout = inflater.inflate(R.layout.layout_login, null);
                            final EditText et_userName = (EditText) layout.findViewById(R.id.et_userName);
                            final EditText et_password = (EditText) layout.findViewById(R.id.et_password);
                            dialog.setTitle("登录");
                            dialog.setView(layout)
                                    .setCancelable(false)
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    }).setNeutralButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String userName = et_userName.getText().toString().trim();
                                    String password = et_password.getText().toString().trim();
                                    login(userName, password, mBeen);
                                    dialogInterface.cancel();
                                }
                            });
                            dialog.show();
                        } else {
                            subFile(mBeen);
                        }

                    } else {
                        Toast.makeText(mContext, "没有可提交的图片", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        return view;
    }*/


    private void subFile(List<CameraPhotoBean> mediaList) {
        if (mediaList != null && mediaList.size() >= 0) {
            new CameraFileUpload(mContext, new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (msg.what == C.SUCCESS) {
                        tongzhi.tongzhi(true);
                        Toast.makeText(mContext, "图片提交成功", Toast.LENGTH_SHORT).show();
                    } else {
                        tongzhi.tongzhi(false);
                        Toast.makeText(mContext, "图片提交失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }, mediaList).execute();
        }
    }

    private void login(String name, String password, final List<CameraPhotoBean> mBeen) {
        progressDialog1.setMessage("登录");
        progressDialog1.show();
        Map<String, Object> map = new HashMap();
        map.put("action", "login");
        map.put("userId", name);
        map.put("passWord", password);
        Request request = RequestUtil.getRequest(map, mContext);

        DOkHttp.getInstance().getData4Server(request, new DOkHttp.MyCallBack() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(String json) {
                progressDialog1.dismiss();
                LoginBean bean = new Gson().fromJson(json, LoginBean.class);
                if (bean.getCode().equals("0")) {
                    SharePreferencesTools.saveString(mContext, "ep", "userName", bean.getUserId());
                    SharePreferencesTools.saveString(mContext, "ep", "passWord", bean.getPassWord());
                    subFile(mBeen);
                } else {
                    Toast.makeText(mContext, bean.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    interface Tongzhi {
        public void tongzhi(boolean flag);
    }

    interface Choose {
        public void choose();
    }
}
