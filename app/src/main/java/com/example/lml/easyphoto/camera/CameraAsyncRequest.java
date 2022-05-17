/*
package com.example.lml.easyphoto.camera;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.lml.easyphoto.R;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import okhttp3.MediaType;
import okhttp3.RequestBody;

*/
/**
 * Created by VIC on 2016/5/25.
 *//*

public class CameraAsyncRequest extends HttpRequest {
    private Handler handler;
    private Context mContext;
    private String url;
    private ProgressDialog progressDialog;
    JSONObject requestJson;
    public CameraAsyncRequest(Context mContext, Handler handler, JSONObject requestJson) {
        super();
        this.mContext = mContext;
        this.handler = handler;
        this.url = mContext.getString(R.string.aits_host) +
                mContext.getString(R.string.aits_common);
        this.requestJson = requestJson;
    }

    void createProgressDialog() {
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("正在上传信息，请稍候......");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void excute() {
            try {
                MediaType JSON = MediaType.parse("text/plain; charset=utf-8");
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                GZIPOutputStream out = new GZIPOutputStream(bos);
                out.write(requestJson.toString().getBytes("UTF-8"));
                out.finish();
                out.close();
                RequestBody body = RequestBody.create(JSON, bos.toByteArray());
                Request request = new Request.Builder().url(url).post(body).build();
                OKHttpUtils.enqueue(request, callback);
            } catch (Exception e) {
                Message msg = new Message();
                msg.what = C.FAILURE;
                msg.getData().putString("msg", "服务器异常，请稍后重试！");
                handler.sendMessage(msg);
                updateProgressDialogMessage("服务器异常，请稍后");
                if (progressDialog != null) {
                    progressDialog.cancel();
                }
                e.printStackTrace();
            }

    }

    private void updateProgressDialogMessage(String message){
        //更新进度条信息
        Message progressDialog = new Message();
        progressDialog.what = 1;
        progressDialog.getData().putString("msg",message);
    }
    private Callback callback = new Callback() {

        @Override
        public void onResponse(Response response) {
            Message msg = new Message();
            try {
                GZIPInputStream gin = new GZIPInputStream(response.body().byteStream());
                InputStreamReader in = new InputStreamReader(gin, "UTF-8");
                BufferedReader bin = new BufferedReader(in);
                char[] b = new char[1024 * 8];
                int read = 0;
                StringBuffer s = new StringBuffer();
                while ((read = bin.read(b)) != -1) {
                    s.append(b, 0, read);
                }
                String result = s.toString();

                bin.close();
                in.close();
                gin.close();

                JSONObject responeJson = (JSONObject) new JSONTokener(result)
                        .nextValue();
                String status = responeJson.getString("status");
                if (status.equals("200")){
                    msg.what = C.SUCCESS;
                    msg.obj = responeJson;
                    if (progressDialog != null) {
                        progressDialog.cancel();
                    }
                    handler.sendMessage(msg);
                }

            } catch (Exception e) {
                e.printStackTrace();
                msg.what = C.FAILURE;
                msg.getData().putString("msg", "服务器异常，请稍后重试！");
                updateProgressDialogMessage("服务器异常，请稍后");
                handler.sendMessage(msg);
                if (progressDialog != null) {
                    progressDialog.cancel();
                }
            }

        }

        @Override
        public void onFailure(Request arg0, IOException arg1) {
            Message msg = new Message();
            msg.what = C.SERVICE_FAILURE;
            handler.sendMessage(msg);
            if (progressDialog != null) {
                progressDialog.cancel();
            }
        }
    };
}
*/
