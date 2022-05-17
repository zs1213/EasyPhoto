/*
package com.example.lml.easyphoto.camera;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.lml.easyphoto.R;
import com.example.lml.easyphoto.http.HttpRequest;
import com.example.lml.easyphoto.http.OKHttpUtils;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class LoginRequest extends HttpRequest {

	private Handler handler;
	private Context mContext;
	private String url;
	private ProgressDialog progressDialog;
	private String usercode;
	private String password;
	private static String TAG = "LoginRequest";

	public LoginRequest(Context mContext, Handler handler, String usercode, String password) {

		super();
		this.mContext = mContext;
		this.handler = handler;
		this.usercode = usercode;
		this.password = password;
		this.url = mContext.getString(R.string.aits_host) +
				mContext.getString(R.string.aits_common);
	}

	public void excute(String requestString) {
		try {
//			MediaType JSON = MediaType.parse("application/json; charset=utf-8");
			MediaType JSON = MediaType.parse("text/plain; charset=utf-8");
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			GZIPOutputStream out = new GZIPOutputStream(bos);
			out.write(requestString.getBytes("UTF-8"));
			out.finish();
			out.close();
			Log.d("test", "url:" + url);
			Log.e(TAG,"LoginRequest请求:"+requestString.toString());
			RequestBody body = RequestBody.create(JSON, bos.toByteArray());
			Request request = new Request.Builder().url(url).post(body).build();
			OKHttpUtils.enqueue(request, callback);
			createProgressDialog();
		} catch (Exception e) {
			Message msg = new Message();
			msg.what = C.FAILURE;
			handler.sendMessage(msg);
			e.printStackTrace();
		}
	}

	void createProgressDialog(){
	}

	void finishProgressDialog(){
	}

	private Callback callback = new Callback() {

		@Override
		public void onResponse(Response response) throws IOException {
			Message msg = new Message();
			Log.d("test", "等待返回结果");
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

				Log.e(TAG, "LoginRequest返回的result:" + result);
//			{"errorMessage":"业务员权限","body":{"js":1,"yhxm":"admin"},"sessionId":"C5468438D1478A53D0C618D986FED543","status":200}

				JSONObject responeJson = (JSONObject) new JSONTokener(result)
						.nextValue();
				int status = responeJson.getInt("status");
				if (status == 200) {
					JSONObject body = responeJson.getJSONObject("body");
					msg.what = C.SUCCESS;
					handler.sendMessage(msg);

					finishProgressDialog();
				} else if (status == 500) {
					msg.what = 500;
					msg.getData().putString("msg", responeJson.getString("errorMessage"));
					handler.sendMessage(msg);
				}
			} catch (Exception e) {
				e.printStackTrace();
				msg.what = C.FAILURE;
				// msg.obj = res;
				handler.sendMessage(msg);
			} finally {
				try {
					TimeUnit.SECONDS.sleep(0);
				} catch (InterruptedException ie) {
					ie.printStackTrace();
				}
			}
		}

		@Override
		public void onFailure(Request arg0, IOException arg1) {
			finishProgressDialog();
			Message msg = new Message();
			msg.what = C.FAILURE;
			// msg.obj = res;
			handler.sendMessage(msg);
		}
	};
}


*/
