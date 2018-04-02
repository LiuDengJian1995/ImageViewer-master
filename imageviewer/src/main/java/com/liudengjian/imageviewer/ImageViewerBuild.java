package com.liudengjian.imageviewer;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.liudengjian.imageviewer.adapter.ImageViewerAdapter;
import com.liudengjian.imageviewer.adapter.impl.MyImageViewerAdapter;
import com.liudengjian.imageviewer.config.ITConfig;
import com.liudengjian.imageviewer.listener.ProgressViewGet;
import com.liudengjian.imageviewer.listener.SourceImageViewGet;
import com.liudengjian.imageviewer.net.impl.DefaultProgressBarGet;
import com.liudengjian.imageviewer.net.ImageLoad;
import com.liudengjian.imageviewer.net.impl.DefaultImageLoad;
import com.liudengjian.imageviewer.view.MoreView;
import com.liudengjian.imageviewer.view.interfaces.MoreViewInterface;
import com.liudengjian.imageviewer.view.util.ScaleType;

import java.util.List;

/**
 * 查看器创建类
 * Created by liuting on 18/3/14.
 */

public class ImageViewerBuild {

    /**
     * 点击的索引
     */
    public int clickIndex;

    /**
     * 当前的索引
     */
    public int nowIndex;

    /**
     * 图片地址的集合
     */
    public List<String> imageList;

    /**
     * 上下文
     */
    private Context mContext;

    /**
     * 得到源图像(用于在退出图片查看器时缩放在图片控件上的动画)
     */
    public SourceImageViewGet sourceImageViewGet;

    /**
     * 得到图片进度
     */
    public ProgressViewGet progressViewGet;

    /**
     * 图片查看器的配置文件
     */
    public ITConfig itConfig;

    /**
     * ViewPager的滑动动画
     */
    public ViewPager.PageTransformer pageTransformer;

    /**
     * 创建自定义的页面覆盖在ViewPager上面
     */
    public ImageViewerAdapter imageViewerAdapter;

    /**
     * 单击图片
     */
    public ImageViewerAdapter.OnImageViewerClick onImageViewerClick;

    /**
     * 长按图片的监听事件
     */
    public ImageViewerAdapter.OnLongImageViewerClick onLongImageViewerClick;

    /**
     * 图片加载器
     */
    public ImageLoad imageLoad;

    /**
     * 裁剪类型
     */
    public ScaleType scaleType = ScaleType.CENTER_CROP;

    /**
     * 对话框
     */
    public Dialog dialog;

    /**
     * 查看更多布局
     */
    private MoreViewInterface moreView;

    /**
     * 是否显示查看更多布局
     */
    public boolean isShowMore = false;


    public ImageViewerBuild(Context context) {
        this.mContext = context;
    }

    void checkParam() {
        if (itConfig == null)
            itConfig = new ITConfig();
        if (imageViewerAdapter == null) {
            if (imageList == null || imageList == null || imageList.size() <= 1) {
                imageViewerAdapter = new ImageViewerAdapter() {
                    @Override
                    public View onCreateView(View parent, ViewPager viewPager, DialogInterface dialogInterface) {
                        return null;
                    }
                };
            } else {
                imageViewerAdapter = new MyImageViewerAdapter();
            }
        }
        imageViewerAdapter.setOnImageViewerClick(onImageViewerClick);
        imageViewerAdapter.setOnLongImageViewerClick(onLongImageViewerClick);
        imageViewerAdapter.setImageViewerBuild(this);
        if (imageLoad == null) {
            imageLoad = new DefaultImageLoad();
        }
        if (progressViewGet == null) {
            progressViewGet = new DefaultProgressBarGet();
        }
//        if (sourceImageViewGet == null)
//            throw new NullPointerException("not set SourceImageViewGet");
        if (imageList == null)
            throw new NullPointerException("not set ImageList");
    }

    public MoreViewInterface getMoreView() {
        if (moreView == null) {
            moreView = new MoreView(mContext);
        }
        return moreView;
    }

    public void setMoreView(MoreViewInterface moreView) {
        if (moreView == null) {
            this.moreView = getMoreView();
        } else {
            this.moreView = moreView;
        }
    }

    public boolean needTransOpen(int pos, boolean change) {
        boolean need = pos == clickIndex;
        if (need && change) {
            clickIndex = -1;
        }
        return need;
    }

    public View inflateProgress(Context context, FrameLayout rootView) {
        if (progressViewGet != null) {
            View progress = progressViewGet.getProgress(context);
            if (progress == null) return null;
            int width = ViewGroup.LayoutParams.WRAP_CONTENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            if (progress.getLayoutParams() != null) {
                width = progress.getLayoutParams().width;
                height = progress.getLayoutParams().height;
            }
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(width, height);
            lp.gravity = Gravity.CENTER;
            rootView.addView(progress, lp);
            return progress;
        }
        return null;
    }


}
