package com.cyh.lib;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;

import java.lang.reflect.Field;

import static android.content.Context.WINDOW_SERVICE;

/**
 * 屏幕适配帮助类.
 * 使用：
 * 在Application的onCreate中引用：
 * //设计图标注的宽度
 * int designWidth = 750;
 * new RudenessScreenHelper(this, designWidth).activate();
 *
 * FAQ:
 * 1、若存在webview导致适配失效的问题.
 * 可以先继承WebView并重写setOverScrollMode(int mode)方法，在方法中调用super之后调用一遍RudenessScreenHelper.resetDensity(getContext(), designWidth)规避
 *
 * 2、若存在dialog中适配失效的问题.
 * 可以在dialog的oncreate中调用一遍RudenessScreenHelper.resetDensity(getContext(), designWidth)规避
 *
 * 3、旋转屏幕之后适配失效.
 * 可以在onConfigurationChanged中调用RudenessScreenHelper.resetDensity(getContext(), designWidth)规避
 *
 * 4、特定国产机型ROM中偶先fragment失效.
 * 可以在fragment的onCreateView中调用RudenessScreenHelper.resetDensity(getContext(), designWidth)规避
 *
 * Created by youhai.cai on 2018/1/23.
 */

public class ScreenAdaptationHelper {

    /**
     * 重新计算displayMetrics.xhdpi, 使单位pt重定义为设计稿的相对长度
     * @see #activate()
     *
     * @param context
     * @param designWidth 设计稿的宽度
     */
    public static void resetDensity(Context context, float designWidth){
        if(context == null)
            return;

        Point size = new Point();
        ((WindowManager)context.getSystemService(WINDOW_SERVICE)).getDefaultDisplay().getSize(size);

        Resources resources = context.getResources();

        resources.getDisplayMetrics().xdpi = size.x/designWidth*72f;

        DisplayMetrics metrics = getMetricsOnMiui(context.getResources());
        if(metrics != null)
            metrics.xdpi = size.x/designWidth*72f;
    }

    /**
     * 恢复displayMetrics为系统原生状态，单位pt恢复为长度单位磅
     * @see #inactivate()
     *
     * @param context context
     */
    public static void restoreDensity(Context context){
        context.getResources().getDisplayMetrics().setToDefaults();

        DisplayMetrics metrics = getMetricsOnMiui(context.getResources());
        if(metrics != null)
            metrics.setToDefaults();
    }

    //解决MIUI更改框架导致的MIUI7+Android5.1.1上出现的失效问题(以及极少数基于这部分miui去掉art然后置入xposed的手机)
    private static DisplayMetrics getMetricsOnMiui(Resources resources){
        if("MiuiResources".equals(resources.getClass().getSimpleName()) || "XResources".equals(resources.getClass().getSimpleName())){
            try {
                Field field = Resources.class.getDeclaredField("mTmpMetrics");
                field.setAccessible(true);
                return  (DisplayMetrics) field.get(resources);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    /**
     * 转换dp为px
     * @param context context
     * @param value 需要转换的dp值
     * @return px值
     */
    public static float dp2px(Context context, float value){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, context.getResources().getDisplayMetrics());
    }

    /**
     * 转换pt为px
     * @param context context
     * @param value 需要转换的pt值，若context.resources.displayMetrics经过resetDensity()的修改则得到修正的相对长度，否则得到原生的磅
     * @return px值
     */
    public static float pt2px(Context context, float value){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PT, value, context.getResources().getDisplayMetrics());
    }

    private Application.ActivityLifecycleCallbacks activityLifecycleCallbacks;
    private Application mApplication;
    private float designWidth = 720;

    /**
     *
     * @param application application
     * @param width 设计稿宽度
     */
    public ScreenAdaptationHelper(Application application, float width){
        mApplication = application;
        designWidth = width;

        activityLifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                //通常情况下application与activity得到的resource虽然不是一个实例，但是displayMetrics是同一个实例，只需调用一次即可
                //为了面对一些不可预计的情况以及向上兼容，分别调用一次较为保险
                resetDensity(mApplication, designWidth);
                resetDensity(activity, designWidth);
            }

            @Override
            public void onActivityStarted(Activity activity) {
                resetDensity(mApplication, designWidth);
                resetDensity(activity, designWidth);
            }

            @Override
            public void onActivityResumed(Activity activity) {
                resetDensity(mApplication, designWidth);
                resetDensity(activity, designWidth);
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        };
    }

    /**
     * 激活本方案
     */
    public void activate(){
        resetDensity(mApplication, designWidth);
        mApplication.registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
    }

    /**
     * 恢复系统原生方案
     */
    public void inactivate(){
        restoreDensity(mApplication);
        mApplication.unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks);
    }


}
