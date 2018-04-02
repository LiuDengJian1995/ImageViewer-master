package com.liudengjian.imageviewer.listener;

import android.content.Context;
import android.view.View;

/**
 * 得到图片进度
 */

public interface ProgressViewGet<T extends View> {

    /**创建加载进度控件*/
    T getProgress(Context context);

    /**加载的进度*/
    void onProgressChange(T view, float progress);
}
