package com.liudengjian.imageviewer.view.listener;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.os.Handler;
import android.support.v4.view.ViewPager;

import com.liudengjian.imageviewer.ImageViewerBuild;
import com.liudengjian.imageviewer.view.DialogView;
import com.liudengjian.imageviewer.view.InterceptViewPager;
import com.liudengjian.imageviewer.view.interfaces.MoreViewInterface;


/**
 * Created by 刘登建 on 2018/3/30
 */

/**
 * 三个方法的执行顺序为：用手指拖动翻页时，最先执行一遍onPageScrollStateChanged（1），
 * 然后不断执行onPageScrolled，放手指的时候，直接立即执行一次onPageScrollStateChanged（2），
 * 然后立即执行一次onPageSelected，然后再不断执行onPageScrollStateChanged，
 * 最后执行一次onPageScrollStateChanged（0）。
 */
public class ViewPagerOnPageChangeListener implements ViewPager.OnPageChangeListener {
    int currPosition = 0; // 当前滑动到了哪一页
    boolean canJump = false;
    boolean canLeft = true;
    boolean isObjAnmatitor = true;
    boolean isObjAnmatitor2 = false;
    private ImageViewerBuild build;
    private MoreViewInterface moreView;
    private InterceptViewPager viewPager;
    private DialogView dialogView;

    public ViewPagerOnPageChangeListener(ImageViewerBuild build, MoreViewInterface moreView,
                                         InterceptViewPager viewPager, DialogView dialogView) {
        this.build = build;
        this.moreView = moreView;
        this.viewPager = viewPager;
        this.dialogView = dialogView;
        this.currPosition = build.nowIndex;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        /**
         * 这个方法会在屏幕滚动过程中不断被调用。
         *有三个参数，第一个position，这个参数要特别注意一下。
         *当用手指滑动时，如果手指按在页面上不动，position和当前页面index是一致的；
         * 如果手指向左拖动（相应页面向右翻动），这时候position大部分时间和当前页面是一致的，只有翻页成功的情况下最后一次调用才会变为目标页面；
         * 如果手指向右拖动（相应页面向左翻动），这时候position大部分时间和目标页面是一致的，只有翻页不成功的情况下最后一次调用才会变为原页面。
         *当直接设置setCurrentItem翻页时，如果是相邻的情况（比如现在是第二个页面，跳到第一或者第三个页面），
         * 如果页面向右翻动，大部分时间是和当前页面是一致的，只有最后才变成目标页面；如果向左翻动，
         * position和目标页面是一致的。这和用手指拖动页面翻动是基本一致的。
         *如果不是相邻的情况，比如我从第一个页面跳到第三个页面，position先是0，然后逐步变成1，然后逐步变成2；
         * 我从第三个页面跳到第一个页面，position先是1，然后逐步变成0，并没有出现为2的情况。
         *positionOffset是当前页面滑动比例，如果页面向右翻动，
         * 这个值不断变大，最后在趋近1的情况后突变为0。如果页面向左翻动，这个值不断变小，最后变为0。
         *positionOffsetPixels是当前页面滑动像素，变化情况和positionOffset一致。
         */
        if (build.isShowMore){
            if (position == (build.imageList.size()-1)) {
                if (positionOffset > 0.35) {
                    canJump = true;
                    if (moreView!=null && moreView.getImageView() != null && moreView.getTextView() != null) {
                        if (isObjAnmatitor) {
                            isObjAnmatitor = false;
                            ObjectAnimator animator = ObjectAnimator.ofFloat(moreView.getImageView(), "rotation", 0f, 180f);
                            animator.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    moreView.getTextView().setText(moreView.getTextEnd());
                                    isObjAnmatitor2 = true;
                                }
                            });
                            animator.setDuration(500).start();
                        }
                    }
                } else if (positionOffset <= 0.35 && positionOffset > 0) {
                    canJump = false;
                    if (moreView!=null && moreView.getImageView() != null && moreView.getTextView() != null) {
                        if (isObjAnmatitor2) {
                            isObjAnmatitor2 = false;
                            ObjectAnimator animator = ObjectAnimator.ofFloat(moreView.getImageView(), "rotation", 180f, 360f);
                            animator.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    moreView.getTextView().setText(moreView.getTextStart());
                                    isObjAnmatitor = true;
                                }
                            });
                            animator.setDuration(500).start();
                        }
                    }
                }
                canLeft = false;
            } else {
                canLeft = true;
            }
        }

    }

    @Override
    public void onPageSelected(int position) {
        /**
         * 这个方法有一个参数position，代表哪个页面被选中。
         * 当用手指滑动翻页的时候，如果翻动成功了（滑动的距离够长），手指抬起来就会立即执行这个方法，position就是当前滑动到的页面。
         * 如果直接setCurrentItem翻页，那position就和setCurrentItem的参数一致，这种情况在onPageScrolled执行方法前就会立即执行。
         */
        currPosition = position;
        build.nowIndex = position;
        build.imageViewerAdapter.onPageSelected(position);
    }
    @Override
    public void onPageScrollStateChanged(int state) {
        /**
         * onPageScrollStateChanged(int state)：这个方法在手指操作屏幕的时候发生变化。有三个值：0（END）,1(PRESS) , 2(UP) 。
         *当用手指滑动翻页时，手指按下去的时候会触发这个方法，state值为1，手指抬起时，如果发生了滑动（即使很小），
         * 这个值会变为2，然后最后变为0 。总共执行这个方法三次。
         * 一种特殊情况是手指按下去以后一点滑动也没有发生，这个时候只会调用这个方法两次，state值分别是1,0 。
         *当setCurrentItem翻页时，会执行这个方法两次，state值分别为2 , 0 。
         */
        if (state == 0) {
            viewPager.getBackground().setAlpha(0);
        } else {
            viewPager.getBackground().setAlpha(255);
        }
        if (currPosition == (build.imageList.size()-1) && !canLeft && build.isShowMore) {
            if (state == ViewPager.SCROLL_STATE_SETTLING) {
                if (canJump) {
                    build.getMoreView().setScrollEndClick(dialogView,build);
                }
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        // 在handler里调用setCurrentItem才有效
                        viewPager.setCurrentItem(build.imageList.size()-1);
                    }
                });
            }
        }
    }
}