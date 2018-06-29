package com.example.kail.locationapp.base.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.kail.locationapp.base.BaseApplication;

import java.util.UUID;

/**
 * Created by kail on 2017/10/11.
 */

public class BaseUtil {

    /**
     *
     * 通用吐司
     */
    private static Toast mToast;

    @SuppressLint("ShowToast")
    public static void showToast(String msg) {
        if (mToast == null) {
            Context mContext = BaseApplication.getInstance().getApplicationContext();
            mToast = Toast.makeText(mContext, msg, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(msg);
        }
        mToast.show();
    }

    public String getPhoneSign() {
        StringBuilder deviceId = new StringBuilder();
        // 渠道标志
        deviceId.append("a");
        try {
            //IMEI（imei）
            Context mContext = BaseApplication.getInstance().getApplicationContext();
            TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            String imei = tm.getDeviceId();
            if (!TextUtils.isEmpty(imei)) {
                deviceId.append("imei");
                deviceId.append(imei);
                return deviceId.toString();
            }
            //序列号（sn）
            String sn = tm.getSimSerialNumber();
            if(!TextUtils.isEmpty(sn)){
                deviceId.append("sn");
                deviceId.append(sn);
                return deviceId.toString();
            }
            //如果上面都没有， 则生成一个id：随机码
            String uuid = getUUID(mContext);
            if(!TextUtils.isEmpty(uuid)){
                deviceId.append("id");
                deviceId.append(uuid);
                return deviceId.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Context mContext = BaseApplication.getInstance().getApplicationContext();
            deviceId.append("id").append(getUUID(mContext));
        }
        return deviceId.toString();
    }
    /**
     * 得到全局唯一UUID
     */
    private String uuid;
    public String getUUID(Context context){
        SharedPreferences mShare = context.getSharedPreferences("uuid",Context.MODE_PRIVATE);
        if(mShare != null){
            uuid = mShare.getString("uuid", "");
        }
        if(TextUtils.isEmpty(uuid)){
            uuid = UUID.randomUUID().toString();
            mShare.edit().putString("uuid",uuid).commit();
        }
        return uuid;
    }



}
