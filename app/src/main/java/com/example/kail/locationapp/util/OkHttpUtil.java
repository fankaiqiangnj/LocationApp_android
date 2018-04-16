package com.example.kail.locationapp.util;

import com.example.kail.locationapp.interfaces.SuccessCallback;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by kail on 2018/2/26.
 */

public class OkHttpUtil {
    private static final OkHttpClient mOkHttpClient = new OkHttpClient();
    static{

    }
    /**
     * 该不会开启异步线程。
     * @param request
     * @return
     * @throws IOException
     */
    public static Response execute(Request request) throws IOException{
        return mOkHttpClient.newCall(request).execute();
    }

    /**
     * 开启异步线程访问网络, 且不在意返回结果（实现空callback）
     * @param request
     */
    public static void enqueue(final Request request, final SuccessCallback successCallback, final int code, final Class clzz){
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                successCallback.error(code,"服务器请求失败");

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code()==200) {
                    Gson gson = new Gson();
                    successCallback.success(code, gson.fromJson(response.body().charStream(), clzz));
                }else {
                    successCallback.error(code,"服务器请求失败");
                }
            }
        });
    }
    public static String getStringFromServer(String url) throws IOException{
        Request request = new Request.Builder().url(url).build();
        Response response = execute(request);
        if (response.isSuccessful()) {
            String responseUrl = response.body().string();
            return responseUrl;
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }


    /**
     * 为HttpGet 的 url 方便的添加1个name value 参数。
     * @param url
     * @param name
     * @param value
     * @return
     */
    public static String attachHttpGetParam(String url, String name, String value){
        return url + "?" + name + "=" + value;
    }


    public static void post(String url, Map<String, String> paramMap, SuccessCallback successCallback, int code, Class clzz)
    {
        try
        {
            FormBody.Builder formBody = new FormBody.Builder();

            Iterator iterator = paramMap.keySet().iterator();
            while (iterator.hasNext())
            {
                String str = (String)iterator.next();
                formBody.add(str, paramMap.get(str));
            }
            Request request = new Request.Builder()//创建Request 对象。
                    .url(url)
                    .post(formBody.build())//传递请求体
                    .build();
            enqueue(request,successCallback, code, clzz);

        }
        catch (Exception ex)
        {
            successCallback.error(code,"请求失败");
            return;
        }
    }

}
