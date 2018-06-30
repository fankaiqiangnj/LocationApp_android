package com.example.kail.locationapp;

import com.example.kail.locationapp.base.BaseApplication;
import com.google.gson.Gson;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class LocationAppApplication extends BaseApplication {
    private static LocationAppApplication appApplication = null;

    private ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(3, 6, 30, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());

    private Gson gson = new Gson();

    public static LocationAppApplication getInstance() {
        return appApplication;
    }

    public ThreadPoolExecutor getThreadPoolExecutor() {
        return threadPoolExecutor;
    }

    public Gson getGson() {
        return gson;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appApplication = this;
    }
}
