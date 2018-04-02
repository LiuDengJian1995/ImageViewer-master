package com.liudengjian.imageviewer.adapter.indicator;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.liudengjian.imageviewer.ImageViewerBuild;

import static android.support.v4.view.ViewPager.OnPageChangeListener;

/**
 * 当图片数量大于一时控件的指示器
 */
public class NumberPageIndicator extends LinearLayout {

    private ViewPager mViewpager;
    private TextView Indicator;
    private int count = 0;
    private ImageViewerBuild build;

    private final OnPageChangeListener mInternalPageChangeListener = new OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {

            if (mViewpager.getAdapter() == null || mViewpager.getAdapter().getCount() <= 0) {
                return;
            }

            Indicator.setTextColor(Color.WHITE);
            Indicator.setText(position + 1 + "/" + count);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    public NumberPageIndicator(Context context) {
        super(context);
        init(context, null);
    }

    public NumberPageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public NumberPageIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public NumberPageIndicator(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        Indicator = new TextView(getContext());
        Indicator.setTextColor(Color.WHITE);
        Indicator.setText("");
        addView(Indicator);
    }


    public void setViewPager(ViewPager viewPager, ImageViewerBuild build) {
        this.build = build;
        mViewpager = viewPager;
        if (mViewpager != null && mViewpager.getAdapter() != null && build != null) {
            createIndicators();
            mViewpager.removeOnPageChangeListener(mInternalPageChangeListener);
            mViewpager.addOnPageChangeListener(mInternalPageChangeListener);
            mInternalPageChangeListener.onPageSelected(mViewpager.getCurrentItem());
        }
    }


    /**
     * @deprecated User ViewPager addOnPageChangeListener
     */
    @Deprecated
    public void setOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
        if (mViewpager == null) {
            throw new NullPointerException("can not find Viewpager , setViewPager first");
        }
        mViewpager.removeOnPageChangeListener(onPageChangeListener);
        mViewpager.addOnPageChangeListener(onPageChangeListener);
    }

    private void createIndicators() {
        int count = mViewpager.getAdapter().getCount();

        if (count <= 0) {
            return;
        }

        if (build != null && build.isShowMore) {
            count--;
        }

        int currentItem = mViewpager.getCurrentItem();

        this.count = count;
        Indicator.setTextColor(Color.WHITE);
        Indicator.setText(currentItem + 1 + "/" + count);
    }


}