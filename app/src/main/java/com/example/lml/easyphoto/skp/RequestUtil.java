package com.example.lml.easyphoto.skp;

import android.content.Context;

import com.google.gson.Gson;

import java.util.Map;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by czl on 2016/12/9 0009.
 */

public class RequestUtil {
    public static Request getRequest(Map<String,Object> map, Context context){
        Request request=new Request.Builder()
                .post(RequestBody.create(MediaType.parse("text/plain"), new Gson().toJson(map)))
                .tag(context)
                .url(Config.json_url)
                .build();
        return  request;
    }
}
