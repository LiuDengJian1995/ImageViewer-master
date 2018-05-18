package com.liudengjian.imageviewer.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.util.Log;
import android.view.OrientationEventListener;

import java.lang.ref.WeakReference;

/**
 * 监听重力系统传感器的变化，为图片横竖屏而定制
 */

public class MySensorHelper {
    private static final String TAG = MySensorHelper.class.getSimpleName();
    private OrientationEventListener mLandOrientationListener;
    private OrientationEventListener mPortOrientationListener;
    private WeakReference<Context> mActivityWeakRef;
    private boolean isPortLock = false;
    private boolean isLandLock = false;
    private int ori;
    private Activity mActivity;
    //是否强行转屏
    private boolean isForceScreen = false;

    public MySensorHelper(final Activity activity, boolean isForceScreen) {
        this.isForceScreen = isForceScreen;
        // 判断Android当前的屏幕是横屏还是竖屏。横竖屏判断
        Configuration mConfiguration = activity.getResources().getConfiguration(); //获取设置的配置信息
        ori = mConfiguration.orientation; //获取屏幕方向
        mActivity = activity;
        this.mActivityWeakRef = new WeakReference(activity);
        this.mLandOrientationListener = new OrientationEventListener(activity, 3) {
            public void onOrientationChanged(int orientation) {
                Log.d(MySensorHelper.TAG, "mLandOrientationListener");
                if (orientation < 100 && orientation > 80 || orientation < 280 && orientation > 260) {
                    Log.e(MySensorHelper.TAG, "转到了横屏");
                    if (!MySensorHelper.this.isLandLock) {
                        Activity mActivity = (Activity) MySensorHelper.this.mActivityWeakRef.get();
                        if (mActivity != null) {
                            Log.e(MySensorHelper.TAG, "转到了横屏##################");
                            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
//                            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                            isLandLock = true;
                            isPortLock = false;
                        }
                    }
                }

            }
        };
        this.mPortOrientationListener = new OrientationEventListener(activity, 3) {
            public void onOrientationChanged(int orientation) {
                Log.w(MySensorHelper.TAG, "mPortOrientationListener");
                if (orientation < 10 || orientation > 350 || orientation < 190 && orientation > 170) {
                    Log.e(MySensorHelper.TAG, "转到了竖屏");
                    if (!MySensorHelper.this.isPortLock) {
                        Activity mActivity = (Activity) MySensorHelper.this.mActivityWeakRef.get();
                        if (mActivity != null) {
                            Log.e(MySensorHelper.TAG, "转到了竖屏!!!!!!!!!!!!!!!!!!!!!!");
                            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
//                            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                            isPortLock = true;
                            isLandLock = false;
                        }
                    }
                }

            }
        };
        //this.disable();
    }

    //禁用切换屏幕的开关
    public void disable() {
        Log.e(TAG, "disable");
        if (!isForceScreen) {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        } else {
            Configuration mConfiguration = mActivity.getResources().getConfiguration();
            if (ori == mConfiguration.ORIENTATION_LANDSCAPE) {
                //强制为横屏
                mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else if (ori == mConfiguration.ORIENTATION_PORTRAIT) {
                //强制为竖屏
                mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }

        mActivity = null;
        this.mPortOrientationListener.disable();
        this.mLandOrientationListener.disable();
    }

    //开启横竖屏切换的开关
    public void enable() {
        this.mPortOrientationListener.enable();
        this.mLandOrientationListener.enable();
    }

    //设置竖屏是否上锁，true锁定屏幕,fanle解锁
    public void setPortLock(boolean lockFlag) {
        this.isPortLock = lockFlag;
    }

    //设置横屏是否锁定，true锁定，false解锁
    public void setLandLock(boolean isLandLock) {
        this.isLandLock = isLandLock;
    }
}
