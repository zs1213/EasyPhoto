package com.example.lml.easyphoto.okHttpUtils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DOkHttp {

    private Handler mainHanlder;
    public OkHttpClient mOkHttpClient;

    public Gson gson;


    private static class OkHttpUtilHolder {
        public static DOkHttp mInstance = new DOkHttp();

    }

    private DOkHttp() {
        mOkHttpClient = new OkHttpClient().newBuilder().connectTimeout(60000, TimeUnit.MILLISECONDS).readTimeout(60000, TimeUnit.MILLISECONDS).build();
        // mOkHttpClient.networkInterceptors().add(new StethoInterceptor());
        gson = new Gson();

        //更新UI线程
        mainHanlder = new Handler(Looper.getMainLooper());

    }


    public Gson getGson() {
        return gson;
    }

    public static DOkHttp getInstance() {
        return OkHttpUtilHolder.mInstance;
    }


    public interface MyCallBack {
        void onFailure(Request request, IOException e);
        void onResponse(String json);
    }

    public interface MyCallBack_Progress {
        void onFailure(Request request, IOException e);

        void onResponse(Response response);


    }


    public MyCallBack myCallBack;

    public void addGetResult(MyCallBack myCallBack) {
        this.myCallBack = myCallBack;
    }

    public static void setCookie(CookieManager cookieManager) {
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
    }

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static final String CHARSET_NAME = "UTF-8";

    /*------------------------------------------------Daemon------------------------------------------------------------------------*/

    public void getData4Server(Request request, final MyCallBack myCallBack) {

        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String json = response.body().string();

                //LogUtils.e(json);

                mainHanlder.post(new Runnable() {
                    @Override
                    public void run() {
                        myCallBack.onResponse(json);
                    }
                });
            }

        });
    }


    public static String sessionId = "";


    /**
     * 异步回调方式 post请求  自定义回调接口  结果运行在UI线程
     * json 也可以
     *
     * @param request
     * @throws Exception
     */
    public void postData2Server(Request request, final MyCallBack myCallBack) {
        try {
            getInstance().mOkHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    mainHanlder.post(new Runnable() {
                        @Override
                        public void run() {
                            myCallBack.onFailure(request,e);
                        }
                    });

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        final String json = response.body().string();

                        mainHanlder.post(new Runnable() {
                            @Override
                            public void run() {
                                myCallBack.onResponse(json);
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public interface UIchangeListener {
        void progressUpdate(long bytesWrite, long contentLength, boolean done);
    }

    /**
     * 上传文件
     * 也可以和数据post一起提交 监听progress
     *
     * @param requestBody
     * @param uIchangeListener
     */
    public void uploadPost2ServerProgress(Context context, String url, String token, RequestBody requestBody,
                                          MyCallBack myCallBack, final UIchangeListener uIchangeListener) {

        ProgressRequestBody progressRequestBody = ProgressHelper.addProgressRequestListener(requestBody, new UIProgressRequestListener() {
            @Override
            public void onUIRequestProgress(long bytesWrite, long contentLength, boolean done) {
                uIchangeListener.progressUpdate(bytesWrite, contentLength, done);
            }
        });

        Request request = new Request.Builder()
                .tag(context)
                .post(progressRequestBody)
                .url(url).addHeader("Authorization", token)
                .build();

        postData2Server(request, myCallBack);

    }
    /*
    /**
     * get
     *
     * @param
     * @param
     */
    public void getDataFromServerGet(String url, String token,
                                     final MyCallBack myCallBack) {


        Request request = new Request.Builder().url(url).addHeader("Authorization", token).build();
        try {
            getInstance().mOkHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        final String json = response.body().string();

                        mainHanlder.post(new Runnable() {
                            @Override
                            public void run() {
                                myCallBack.onResponse(json);
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 下载监听
     *
     * @param request
     * @param myCallBack
     * @param uIchangeListener
     */
    public void download4ServerListener(Request request, final MyCallBack_Progress myCallBack,
                                        final UIchangeListener uIchangeListener) {

        ProgressHelper.addProgressResponseListener(getInstance().mOkHttpClient, new UIProgressResponseListener() {
            @Override
            public void onUIResponseProgress(long bytesRead, long contentLength, boolean done) {
                uIchangeListener.progressUpdate(bytesRead, contentLength, done);
            }
        }).newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                myCallBack.onResponse(response);
            }

        });


    }


}