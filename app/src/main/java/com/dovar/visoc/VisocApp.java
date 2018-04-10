package com.dovar.visoc;

import android.app.Application;

import com.dovar.common.utils.Utils;

/**
 * Created by heweizong on 2018/4/10.
 */

public class VisocApp extends Application {
    private static volatile VisocApp instance;


    public static VisocApp instance() {
        if (instance == null) {
            throw new RuntimeException("Application cannot be null!");
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        Utils.init(instance);
    }
}
