package com.liudengjian.imageviewer.adapter;

import android.content.DialogInterface;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.liudengjian.imageviewer.ImageViewerBuild;

/**
 * 创建自定义的页面覆盖在ViewPager上面
 */

public abstract class ImageViewerAdapter {
    //查看器创建类
    protected ImageViewerBuild build = null;

    /**
     * 创建自定义的页面覆盖在viewpager上面
     *
     * @param parent
     * @param viewPager
     * @param dialogInterface
     * @return
     */
    public abstract View onCreateView(View parent, ViewPager viewPager, final DialogInterface dialogInterface);

    /**
     * 拖动图片关闭手势 拖动进度
     *
     * @param range
     */
    public void onPullRange(float range) {

    }

    /**
     * 拖动图片关闭手势 取消拖动
     */
    public void onPullCancel() {

    }

    /**
     * 设置查看器创建类
     */
    public void setImageViewerBuild(ImageViewerBuild build) {
        this.build = build;
    }

    /**
     * 打开图片动画开始
     */
    public void onOpenViewerStart() {

    }

    /**
     * 打开图片动画结束
     */
    public void onOpenViewerEnd() {

    }

    /**
     * 关闭图片动画开始
     */
    public void onCloseViewerStart() {

    }

    /**
     * 关闭图片动画结束
     */
    public void onCloseViewerEnd() {

    }

    public void onPageSelected(int pos) {

    }

    /**
     * 单击图片的回调
     *
     * @param v
     * @param pos 单击的图片索引
     * @return 如果拦截默认的单击关闭图片事件就返回true，反之false
     */
    public boolean onClick(View v, int pos) {
        if (onImageViewer!=null){
            return onImageViewer.onImageViewerClick(v,pos);
        }
       return false;
    }

    /**
     * 长按图片的监听事件
     *
     * @param v
     * @param pos 长按的图片索引
     */
    public void onLongClick(View v, int pos) {
        if (onLongImageViewer!=null){
            onLongImageViewer.onLongImageViewerClick(v,pos);
        }
    }

    //布局上的点击事件
    protected OnImageViewerClick onImageViewer = null;
    //布局上的长按事件
    protected  OnLongImageViewerClick onLongImageViewer = null;

    public  interface  OnImageViewerClick {
        boolean onImageViewerClick(View v, int pos);
    }

    public void setOnImageViewerClick(OnImageViewerClick onCheckLikeFlag) {
        if (onCheckLikeFlag!=null){
            this.onImageViewer = onCheckLikeFlag;
        }
    }

    public  interface  OnLongImageViewerClick {
        void onLongImageViewerClick(View v, int pos);
    }

    public void setOnLongImageViewerClick(OnLongImageViewerClick onLikeArticle) {
        if (onLikeArticle!=null){
            this.onLongImageViewer = onLikeArticle;
        }
    }
}
