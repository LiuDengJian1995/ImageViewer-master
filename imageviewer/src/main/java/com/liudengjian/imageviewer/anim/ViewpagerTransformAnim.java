package com.liudengjian.imageviewer.anim;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * ViewPagerd动画
 * Created by 刘登建 on 2018/3/30
 */

public class ViewpagerTransformAnim implements ViewPager.PageTransformer {
    @Override
    public void transformPage(View page, float position) {
        float alpha = 0.0f;
        if (0.0f <= position && position <= 1.0f) {
            alpha = 1.0f - position;
        } else if (-1.0f <= position && position < 0.0f) {
            alpha = position + 1.0f;
        }
        page.setAlpha(alpha);
    }
}