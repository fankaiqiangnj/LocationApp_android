package com.example.kail.locationapp;

import android.database.sqlite.SQLiteDatabase;

import com.example.kail.locationapp.base.BaseApplication;
import com.example.kail.locationapp.gen.DaoMaster;
import com.example.kail.locationapp.gen.DaoSession;
import com.google.gson.Gson;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class LocationAppApplication extends BaseApplication {
    private static LocationAppApplication appApplication = null;
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    boolean socketIsCanClient = false;
    private DaoMaster.DevOpenHelper mHelper;
    private SQLiteDatabase db;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;

    private ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(3, 6, 30, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(),new ThreadPoolExecutor.DiscardOldestPolicy());

    public boolean isSocketIsCanClient() {
        return socketIsCanClient;
    }

    public void setSocketIsCanClient(boolean socketIsCanClient) {
        this.socketIsCanClient = socketIsCanClient;
    }

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

    public double getmCurrentLat(){
        return mCurrentLat;
    }

    public double getmCurrentLon() {
        return mCurrentLon;
    }

    public void setmCurrentLat(double mCurrentLat) {
        this.mCurrentLat = mCurrentLat;
    }

    public void setmCurrentLon(double mCurrentLon) {
        this.mCurrentLon = mCurrentLon;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appApplication = this;
        setDatabase();
    }
    /**
     * 设置greenDao
     */
    private void setDatabase() {
        // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        mHelper = new DaoMaster.DevOpenHelper(this, "location-db", null);
        db = mHelper.getWritableDatabase();
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
    }
    public DaoSession getDaoSession() {
        return mDaoSession;
    }
    public SQLiteDatabase getDb() {
        return db;
    }
}




