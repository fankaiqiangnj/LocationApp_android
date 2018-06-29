package com.example.kail.locationapp.base;

import android.content.Context;
import android.os.StrictMode;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.squareup.leakcanary.LeakCanary;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.GINGERBREAD;

/**
 * Created by Nate on 2015/9/29.
 */
public class BaseApplication extends MultiDexApplication {

    public static BaseApplication mInstance = null;
    // Volley request queue

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this) ;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        enabledStrictMode();
        mInstance = this;
        LeakCanary.install(this);
    }

    /**
     * 开发阶段开启严苛模式
     */
    private void enabledStrictMode() {
        if (SDK_INT >= GINGERBREAD) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder() //
                    .detectAll() //
                    .penaltyLog() //
//                    .penaltyDeath() //
                    .build());
        }
    }

    /**
     * @return BaseApplication
     */
    public static BaseApplication getInstance() {
        return mInstance;
    }



}
