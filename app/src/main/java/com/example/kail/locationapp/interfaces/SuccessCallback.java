package com.example.kail.locationapp.interfaces;

import java.io.IOException;

/**
 * Created by fan on 2018/4/14.
 */

public interface SuccessCallback {
    public abstract void error(int code, String s);

    public abstract void success(int code, Object Object);
}
