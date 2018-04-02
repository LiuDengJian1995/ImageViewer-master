package com.liudengjian.imageviewer.view.interfaces;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.liudengjian.imageviewer.ImageViewerBuild;
import com.liudengjian.imageviewer.view.DialogView;

/**
 * 右滑查看更多
 * Created by 刘登建 on 2018/3/30
 */

public interface MoreViewInterface {
    /**查看更多布局中的图片*/
    ImageView getImageView();

    /**查看更多布局中的文本*/
    TextView getTextView();

    /**滑动开始的文本*/
    String getTextEnd();

    /**滑动结束的文本*/
    String getTextStart();

    /**添加查看更多的布局*/
    View onCreate();

    /**滑动查看更多的结束的事件*/
    void setScrollEndClick(DialogView dialogView, ImageViewerBuild build);
}
