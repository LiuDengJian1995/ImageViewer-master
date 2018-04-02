package com.liudengjian.imageviewer.net.impl;

import android.content.Context;
import android.view.ViewGroup;

import com.liudengjian.imageviewer.listener.ProgressViewGet;
import com.liudengjian.imageviewer.util.ImageViewerUtil;
import com.liudengjian.imageviewer.net.widget.RingLoadingView;


/**
 * 创建进度条
 */

public class DefaultProgressBarGet implements ProgressViewGet<RingLoadingView> {
    @Override
    public RingLoadingView getProgress(Context context) {
        RingLoadingView view = new RingLoadingView(context);
        view.setLayoutParams(new ViewGroup.LayoutParams(ImageViewerUtil.dpToPx(50), ImageViewerUtil.dpToPx(50)));
        return view;
    }

    @Override
    public void onProgressChange(RingLoadingView view, float progress) {
        view.setProgress(progress);
    }
}
