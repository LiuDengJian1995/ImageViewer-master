package com.liudengjian.imageviewer.listener;

import android.widget.ImageView;

/**
 * 得到源图像(用于在退出图片查看器时缩放在图片控件上的动画)
 */

public interface SourceImageViewGet {
    ImageView getImageView(int pos);
}
