package com.example.kail.locationapp.base.network;

/**
 * Created by fan on 2018/4/14.
 */

public interface NetWorkCallback {
    public abstract void error(int code, String s);

    public abstract void success(int code, Object Object);
}
