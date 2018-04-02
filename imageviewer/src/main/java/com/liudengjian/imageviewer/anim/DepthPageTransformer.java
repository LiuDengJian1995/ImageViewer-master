package com.liudengjian.imageviewer.anim;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * ViewPagerd动画
 * Created by 刘登建 on 2018/3/30
 */

public class DepthPageTransformer implements ViewPager.PageTransformer {
    private static final float MIN_SCALE = 0.75f;

    @Override
    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();

        /**
         * position：这个position不是手指滑动的坐标位置，而是滑动页面相对于手机屏幕的位置，
         * 范围位：[-1,1],[-1,0)表示页面向左滑出屏幕，0表示处于中心（即当前显示）的页面，
         * (0,1]表示页面向右滑出屏幕
         */
        if (position < -1) { //表示已经滑出屏幕（左边）
            view.setAlpha(0);
        } else if (position <= 0) { // [-1,0]
            view.setAlpha(1);
            view.setTranslationX(0);
            view.setScaleX(1);
            view.setScaleY(1);

        } else if (position <= 1) { // (0,1]
            view.setAlpha(1 - position);

            view.setTranslationX(pageWidth * -position);

            float scaleFactor = MIN_SCALE
                    + (1 - MIN_SCALE) * (1 - Math.abs(position));
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);

        } else { //表示已经滑出屏幕（右边）
            view.setAlpha(0);
        }
    }
}