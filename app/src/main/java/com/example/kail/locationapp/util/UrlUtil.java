package com.example.kail.locationapp.util;

import android.content.Context;
import android.text.TextUtils;

/**
 * Created by fan on 2018/4/14.
 */

public class UrlUtil {
    public static String ip = "90.15.12.80";
    public static String socketIp = "22.46.127.70";
    public static String socketPort = "1909";

    public static String http = "http://";
    public static String uri = ":8080/IdeaLocationProject/";

    public static String getSocketIp(Context context) {
        if (TextUtils.isEmpty((String) SPUtils.get(context, "socketIp", ""))) {
            return socketIp;
        } else {
            return (String) SPUtils.get(context, "socketIp", "");
        }
    }

    public static String getSocketPort(Context context) {
        if (TextUtils.isEmpty((String) SPUtils.get(context, "socketPort", ""))) {
            return socketPort;
        } else {
            return (String) SPUtils.get(context, "socketPort", "");
        }
    }

    public static String getUrl(String ip) {
        return UrlUtil.http + ip + UrlUtil.uri;
    }

    public static String getIp(Context context) {
        if (TextUtils.isEmpty((String) SPUtils.get(context, "serviceIP", ""))) {
            return ip;
        } else {
            return (String) SPUtils.get(context, "serviceIP", "");
        }
    }

}
