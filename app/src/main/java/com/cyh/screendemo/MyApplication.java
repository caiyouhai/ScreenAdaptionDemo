package com.cyh.screendemo;

import android.app.Application;

import com.cyh.lib.ScreenAdaptationHelper;

/**
 * KK
 * Created by Caodongyao on 2017/8/4.
 */
public class MyApplication extends Application{

    public static final int SCREEN_WITH=750;

    @Override
    public void onCreate() {
        super.onCreate();

        new ScreenAdaptationHelper(this, SCREEN_WITH).activate();
    }
}
