package com.demo.imageviewerdemo.imageload;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.liudengjian.imageviewer.listener.ProgressViewGet;
import com.liudengjian.imageviewer.net.widget.CustomLoadingView;


/**
 * 创建进度条
 */

public class ItemProgressBarGet implements ProgressViewGet<CustomLoadingView> {
    @Override
    public CustomLoadingView getProgress(Context context) {
        CustomLoadingView view = new CustomLoadingView(context);
//        view.setLayoutParams(new ViewGroup.LayoutParams(ImageViewerUtil.dpToPx(50), ImageViewerUtil.dpToPx(50)));
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.start();
        return view;
    }

    @Override
    public void onProgressChange(CustomLoadingView view, float progress) {
        if (progress == 100 || progress == 1) {
            view.stop();
        } else if (progress == -1) {
            view.stop();
            view.setVisibility(View.GONE);
        } else {
            view.start();
        }

    }
}
