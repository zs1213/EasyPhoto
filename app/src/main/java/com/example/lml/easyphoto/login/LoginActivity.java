package com.example.lml.easyphoto.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.lml.easyphoto.R;
import com.example.lml.easyphoto.camera.LoadLocalImageUtil;
import com.example.lml.easyphoto.main.MenuActivity;
import com.example.lml.easyphoto.okHttpUtils.DOkHttp;
import com.example.lml.easyphoto.okHttpUtils.DownloadUtil;
import com.example.lml.easyphoto.util.Configure;
import com.example.lml.easyphoto.util.LoadingDialog;
import com.example.lml.easyphoto.util.PhoneInfoUtil;
import com.example.lml.easyphoto.util.SharePreferencesTools;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

public class LoginActivity extends Activity {
    private Button btn_login;
    private EditText et_userName;
    private EditText et_passward;
    private RelativeLayout rl_checked;
    private ImageView iv_checked;
    private String checked = "0";
    private String userName = "";
    private String passward = "";
    private File apkfile;
    private LoadingDialog dialog;//提示框
    private ProgressDialog progressDialog;//进度条
    private String version;//版本号
    private NetChangeReceiver receiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        verifyStoragePermissions();
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public void verifyStoragePermissions() {
        int permission = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            permission = checkSelfPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (permission != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(PERMISSIONS_STORAGE,
                        REQUEST_EXTERNAL_STORAGE);
            }
        } else {
            initView();
            initData();
        }
    }

    private void initView() {
        btn_login = findViewById(R.id.btn_login);
        et_userName = findViewById(R.id.login_edt_username);
        et_passward = findViewById(R.id.login_edt_password);
        rl_checked = findViewById(R.id.login_rl_checked);
        iv_checked = findViewById(R.id.iv_checked);
    }

    private void initData() {
        receiver = new NetChangeReceiver ();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(receiver, intentFilter);
        dialog = new LoadingDialog(LoginActivity.this);
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        version = PhoneInfoUtil.getVersionName(this);
        checked = SharePreferencesTools.getValue(LoginActivity.this, "easyPhoto", "checked", "0");
        userName = SharePreferencesTools.getValue(LoginActivity.this, "easyPhoto", "userName", "");
        passward = SharePreferencesTools.getValue(LoginActivity.this, "easyPhoto", "passward", "");
        if (checked.equals("0")) {
            LoadLocalImageUtil.getInstance().displayFromDrawable(R.mipmap.radio, iv_checked);
//            et_userName.setText("admin");
//            et_passward.setText("admin123");
        } else {
            LoadLocalImageUtil.getInstance().displayFromDrawable(R.mipmap.radio_active, iv_checked);
            et_userName.setText(userName);
            et_passward.setText(passward);
        }
        rl_checked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checked.equals("0")) {
                    checked = "1";
                    LoadLocalImageUtil.getInstance().displayFromDrawable(R.mipmap.radio_active, iv_checked);
                } else {
                    checked = "0";
                    LoadLocalImageUtil.getInstance().displayFromDrawable(R.mipmap.radio, iv_checked);
                }
                SharePreferencesTools.saveString(LoginActivity.this, "easyPhoto", "checked", checked);
            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadingDialog.Builder loadBuilder = new LoadingDialog.Builder(LoginActivity.this)
                        .setMessage("正在登陆")
                        .setCancelable(true)
                        .setCancelOutside(true);
                dialog = loadBuilder.create();
                dialog.show();
                MultipartBody.Builder mb = new MultipartBody.Builder();
                mb.setType(MultipartBody.FORM);
                mb.addFormDataPart("username", et_userName.getText().toString());
                mb.addFormDataPart("password", et_passward.getText().toString());
                RequestBody requestBody = mb.build();
                DOkHttp.getInstance().uploadPost2ServerProgress(LoginActivity.this, Configure.getToken, "", requestBody, new DOkHttp.MyCallBack() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        dialog.dismiss();
                        Toast.makeText(LoginActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String json) {
                        try {
                            JSONObject object = new JSONObject(json);
                            int code = object.getInt("code");
                            String message = object.getString("message");
                            if (code == 0) {
                                final JSONObject jsonObject = object.getJSONObject("data");
                                final String access_token = jsonObject.getString("access_token");
                                SharePreferencesTools.saveString(LoginActivity.this,"easyPhoto","access_token",access_token);
                                //版本控制
                                MultipartBody.Builder mb = new MultipartBody.Builder();
                                mb.setType(MultipartBody.FORM);
                                mb.addFormDataPart("", "");
                                RequestBody requestBody = mb.build();
                                DOkHttp.getInstance().uploadPost2ServerProgress(LoginActivity.this, Configure.getAppVersion, "Bearer " + access_token, requestBody, new DOkHttp.MyCallBack() {
                                    @Override
                                    public void onFailure(Request request, IOException e) {
                                        dialog.dismiss();
                                        Toast.makeText(LoginActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onResponse(String json) {
                                        try {
                                            JSONObject object = new JSONObject(json);
                                            int code = object.getInt("code");
                                            String message = object.getString("message");
                                            if (code == 0) {
                                                JSONObject objectData = object.getJSONObject("data");
                                                String appVersion = objectData.getString("appVersion");
                                                String downloadPath = objectData.getString("downloadPath");
                                                if (version.equals(appVersion)) {
                                                    DOkHttp.getInstance().getDataFromServerGet(Configure.getUserInfo, "Bearer " + access_token, new DOkHttp.MyCallBack() {
                                                        @Override
                                                        public void onFailure(Request request, IOException e) {
                                                            dialog.dismiss();
                                                            Toast.makeText(LoginActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                                                        }

                                                        @Override
                                                        public void onResponse(String json) {
                                                            dialog.dismiss();
                                                            try {
                                                                JSONObject uJsonObject = new JSONObject(json);
                                                                int code = uJsonObject.getInt("code");
                                                                String message = uJsonObject.getString("message");
                                                                if (code == 0) {
                                                                    JSONObject data = uJsonObject.getJSONObject("data");
                                                                    String userId = data.getString("userId");
                                                                    String officeName = data.getString("officeName");
                                                                    String nickName = data.getString("nickName");

                                                                    String province = data.getString("province");
                                                                    String provinceName = data.getString("provinceName");
                                                                    String city = data.getString("city");
                                                                    String cityName = data.getString("cityName");
                                                                    String county = data.getString("county");
                                                                    String countyName = data.getString("countyName");
                                                                    String country = data.getString("country");
                                                                    String countryName = data.getString("countryName");
                                                                    String village = data.getString("village");
                                                                    String villageName = data.getString("villageName");
                                                                    if (!SharePreferencesTools.getValue(LoginActivity.this, "easyPhoto", "userId", "").equals(userId)){
                                                                          SharePreferencesTools.saveString(LoginActivity.this, "easyPhoto", "gisCode", "");
                                                                          SharePreferencesTools.saveString(LoginActivity.this, "easyPhoto", "serverPath", "");
                                                                    }
                                                                    SharePreferencesTools.saveString(LoginActivity.this, "easyPhoto", "userId", userId);
                                                                    SharePreferencesTools.saveString(LoginActivity.this, "easyPhoto", "userName", et_userName.getText().toString());
                                                                    SharePreferencesTools.saveString(LoginActivity.this, "easyPhoto", "passward", et_passward.getText().toString());
                                                                    SharePreferencesTools.saveString(LoginActivity.this, "easyPhoto", "officeName",officeName);
                                                                    SharePreferencesTools.saveString(LoginActivity.this, "easyPhoto", "province",province);
                                                                    SharePreferencesTools.saveString(LoginActivity.this, "easyPhoto", "provinceName",provinceName);
                                                                    SharePreferencesTools.saveString(LoginActivity.this, "easyPhoto", "city",city);
                                                                    SharePreferencesTools.saveString(LoginActivity.this, "easyPhoto", "cityName",cityName);
                                                                    SharePreferencesTools.saveString(LoginActivity.this, "easyPhoto", "county",county);
                                                                    SharePreferencesTools.saveString(LoginActivity.this, "easyPhoto", "countyName",countyName);
                                                                    SharePreferencesTools.saveString(LoginActivity.this, "easyPhoto", "country",country);
                                                                    SharePreferencesTools.saveString(LoginActivity.this, "easyPhoto", "countryName",countryName);
                                                                    SharePreferencesTools.saveString(LoginActivity.this, "easyPhoto", "village",village);
                                                                    SharePreferencesTools.saveString(LoginActivity.this, "easyPhoto", "villageName",villageName);
                                                                    SharePreferencesTools.saveString(LoginActivity.this, "easyPhoto", "nickName",nickName);
                                                                    Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                                                                    startActivity(intent);
                                                                    finish();
                                                                } else {
                                                                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                                                                }
                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    dialog.dismiss();
                                                    Toast.makeText(LoginActivity.this, "检测到新版本，正在升级！", Toast.LENGTH_SHORT).show();
                                                    dowmload(downloadPath);
                                                }

                                            } else {
                                                dialog.dismiss();
                                                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new DOkHttp.UIchangeListener() {
                                    @Override
                                    public void progressUpdate(long bytesWrite, long contentLength, boolean done) {
                                        int progress = (int) (bytesWrite * 100 / contentLength);
                                    }
                                });

                            } else {
                                dialog.dismiss();
                                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new DOkHttp.UIchangeListener() {
                    @Override
                    public void progressUpdate(long bytesWrite, long contentLength, boolean done) {
                        int progress = (int) (bytesWrite * 100 / contentLength);
                    }
                });
            }
        });
    }

    /**
     * 下载新版本
     *
     * @param url
     */
    private void dowmload(String url) {
        progressDialog.setMessage("新版本下载中");
        progressDialog.setMax(100);
        progressDialog.show();
        String dir = Environment.getExternalStorageDirectory() + "/easyPhoto";
        File files = new File(dir);
        if (files != null) {
            files.mkdirs();
        }
        DownloadUtil.getInstance().download(url, dir, new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess(String path) {
                progressDialog.dismiss();
                apkfile = new File(path);
                progressDialog.dismiss();
                installApk();
            }

            @Override
            public void onDownloading(int progress) {
                progressDialog.setProgress(progress);
            }

            @Override
            public void onDownloadFailed() {
                progressDialog.dismiss();
            }
        });
    }

    /**
     * 安装APK
     */

    protected void installApk() {
        int permission = ActivityCompat.checkSelfPermission(LoginActivity.this,
                Manifest.permission.REQUEST_INSTALL_PACKAGES);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES},
                    2);
        } else {
            anzhuang();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            initView();
            initData();
        } else if (requestCode == 2) {
            anzhuang();
        }
    }

    /**
     * 安装
     */
    private void anzhuang() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(LoginActivity.this, "com.example.lml.easyphoto.fileprovider", apkfile);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(apkfile), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    public class NetChangeReceiver extends BroadcastReceiver {

        @SuppressLint("UnsafeProtectedBroadcastReceiver")
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isAvailable()) {
                    //有网处理
                } else {
                    Toast.makeText(LoginActivity.this, "网络连接中断", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                //ignore
            }
        }
    }
}
