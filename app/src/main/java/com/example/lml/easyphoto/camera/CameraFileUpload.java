package com.example.lml.easyphoto.camera;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.example.lml.easyphoto.R;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by 陈忠磊 on 2017/8/28 16:26
 * 文件上传
 */
public class CameraFileUpload extends AsyncTask<Void, String, Integer> {
    private ProgressDialog progressDialog;
    private Context mContext;
    private Handler openerHandler;
    private List<CameraPhotoBean> mediaList;
    private String url;
    private CameraService service;
    public CameraFileUpload(Context context, Handler openerHandler, List<CameraPhotoBean> mediaList){
        this.mContext = context;
        this.openerHandler = openerHandler;
        this.mediaList=mediaList;
        this.url = mContext.getString(R.string.aits_host) +
                mContext.getString(R.string.aits_common_file);
        service=new CameraService(mContext);
        Log.e("-------","--------------"+url);
     }
    @Override
    protected Integer doInBackground(Void... voids) {
        try {
            for (int i=0;i<mediaList.size();i++){
                /*CameraPhotoBean media=mediaList.get(i);

                publishProgress("正上传第"+(i+1)+"个文件......");

                OkHttpClient mOKHttpClient = new OkHttpClient();
                 File file=new File(media.getFilePath());
                String str = "attachment;filename=" + URLEncoder.encode(file.getName(), "UTF-8");
                JSONObject res = new JSONObject();
                res.put("ID", media.getID()==null?"":media.getID());
                res.put("TASK_ID", media.getID()==null?"":media.getID());
                res.put("FILE_TYPE", "11");
                res.put("PICNAME",media.getFileName()==null?"":media.getFileName());
                res.put("PICSIZE", "0");
                //res.put("PICDATE", media.getPicdate());
                res.put("MEDIA_TYPE", "1");
                res.put("state", media.getState()==null?"":media.getState());
                res.put("province", media.getProvince()==null?"":media.getProvince());
                res.put("city", media.getCity()==null?"":media.getCity());
                res.put("townname", media.getTownname()==null?"":media.getTownname());
                res.put("countrysidename", media.getCountrysidename()==null?"":media.getCountrysidename());
                res.put("villagename", media.getVillagename()==null?"":media.getVillagename());
                res.put("createTime", media.getCreateTime()==null?"":media.getCreateTime());
                res.put("corporateName", media.getCorporateName()==null?"":media.getCorporateName());
                res.put("remark", media.getRemark()==null?"":media.getRemark());
                res.put("lon", media.getLon()==null?"":media.getLon());
                res.put("lat", media.getLat()==null?"":media.getLat());
                res.put("riskReason", media.getRiskReason()==null?"":media.getRiskReason());
                res.put("riskCode", media.getRiskCode()==null?"":media.getRiskCode());
                res.put("massifType", media.getMassifType()==null?"":media.getMassifType());
                res.put("zhType", media.getZhType()==null?"":media.getZhType());
                res.put("isTask", media.getIsTask()==null?"":media.getIsTask());
                res.put("cnpjcl", media.getCnpjcl()==null?"":media.getCnpjcl());
                res.put("chuPing", media.getChuPing()==null?"":media.getChuPing());
                res.put("soilType", media.getSoilType()==null?"":media.getSoilType());
                res.put("sscd", media.getSscd()==null?"":media.getSscd());
                res.put("jobNumber", media.getJobNumber()==null?"":media.getJobNumber());
                *//**
                 * private String ID;
                 private String fileName;
                 private String filePath;
                 private String state;
                 private String createTime;
                 private String province;
                 private String city;
                 private String townname;
                 private String countrysidename;
                 private String villagename;
                 private String corporateName;
                 private String remark;
                 private String lon;
                 private String lat;
                 private String  jobNumber;
                 *//*

                //res.put("LON", StringUtil.doEmpty(media.getLon()));
                //res.put("LAT", StringUtil.doEmpty(media.getLat()));
                //res.put("REMARK", "");
                //res.put("CREATE_USER",media.getCreateUser());
                Log.i("MediaUpload", "上传信息：" + res.toString());
                MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");
                Request request = new Request.Builder()
                        .url(url)
                        .post(RequestBody.create(MEDIA_TYPE_MARKDOWN, file))
                        .header("User-Agent", "OkHttp Headers.java")
                        .addHeader("Content-Type", "text/html;charset=UTF-8")
                        .addHeader("parameter", URLEncoder.encode(res.toString(), "UTF-8"))
                        .addHeader("Content-Disposition", str)
                        .build();
                Response response = mOKHttpClient.newCall(request).execute();

                if (!response.isSuccessful()) {
                    Log.e("","--------------文件上传报文："+response.code());
                    publishProgress("文件上传失败");
                    return C.FAILURE;
                }

                GZIPInputStream gin= new GZIPInputStream(response.body().byteStream());
                InputStreamReader in = new InputStreamReader(gin, "UTF-8");
                BufferedReader bin = new BufferedReader(in);
                char[] b = new char[1024 * 8];
                int read = 0;
                StringBuffer s = new StringBuffer();
                while ((read = bin.read(b)) != -1) {
                    s.append(b, 0, read);
                }
                String result = s.toString();
                JSONObject responeJson = (JSONObject) new JSONTokener(result)
                        .nextValue();
                Log.e("","--------------文件上传报文："+responeJson.toString());
                String status = responeJson.getString("status");
                if (status.equals("200")||status.equals("300")) {
                    service.updateCameraPhoto(media.getID());
                }else{
                    publishProgress("文件上传失败");
                    return C.FAILURE;
                }*/


            }
            publishProgress("文件上传成功");
        }catch (Exception e) {
            e.printStackTrace();
            publishProgress("服务器异常，文件上传失败");
            return C.FAILURE;
        }finally {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (Exception ie) {
                ie.printStackTrace();
            }
        }
        return C.SUCCESS;
    }
    @Override
    protected void onProgressUpdate(String... values) {
        if (values != null && values.length > 0
                && StringUtil.notEmpty(values[0])) {
            progressDialog.setMessage(values[0]);
        }
    }
    @Override
    protected void onPostExecute(Integer result) {
        progressDialog.cancel();
        if (C.SUCCESS == result.intValue()) {
            // 上传成功退出
            openerHandler.sendEmptyMessage(C.SUCCESS);
        }else{
            openerHandler.sendEmptyMessage(C.FAILURE);
        }
    }
    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("正在准备上传文件...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

}
