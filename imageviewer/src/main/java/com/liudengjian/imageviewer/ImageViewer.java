package com.liudengjian.imageviewer;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import com.liudengjian.imageviewer.adapter.ImageViewerAdapter;
import com.liudengjian.imageviewer.config.ITConfig;
import com.liudengjian.imageviewer.listener.ProgressViewGet;
import com.liudengjian.imageviewer.listener.SourceImageViewGet;
import com.liudengjian.imageviewer.net.ImageLoad;
import com.liudengjian.imageviewer.util.ImageViewerUtil;
import com.liudengjian.imageviewer.util.MySensorHelper;
import com.liudengjian.imageviewer.view.DialogView;
import com.liudengjian.imageviewer.view.interfaces.MoreViewInterface;
import com.liudengjian.imageviewer.view.util.ScaleType;

import java.util.ArrayList;
import java.util.List;


/**
 * 图片查看类供外部调用
 * enableReadMode(boolean)	是否开启阅读模式，针对长图默认适宽显示
 * largeThumb()	预览图适宽显示，默认父容器宽度的1/2显示
 * readModeRule（float）	自定义长图的判断标准，默认视图高度的1.5倍
 * noThumbWhenCached()	当有缓存的时候不显示预览图，直接显示原图
 * noThumb()	不显示预览图
 */

public class ImageViewer implements DialogInterface.OnShowListener,
        DialogInterface.OnKeyListener, DialogInterface {
    private Dialog mDialog;
    private ImageViewerBuild build;
    private DialogView dialogView;
    private Context mContext;
    private boolean isShowBar = false; //是否显示状态栏
    private MySensorHelper helper;

    public static ImageViewer with(Context context) {
        return new ImageViewer(context);
    }

    private ImageViewer(Context context) {
        this.mContext = context;
        ImageViewerUtil.init(context);
        build = new ImageViewerBuild(context);
    }

    /**
     * 查看器开始的索引
     */
    public ImageViewer setNowIndex(int index) {
        build.clickIndex = index;
        build.nowIndex = index;
        return this;
    }

    /**
     * 图片地址集合
     */
    public ImageViewer setImageList(List<String> imageList) {
        build.imageList = imageList;
        return this;
    }

    /**
     * 图片地址
     */
    public ImageViewer setImageList(String image) {
        build.imageList = new ArrayList<>();
        build.imageList.add(image);
        return this;
    }

    /**
     * 设置是否显示状态栏
     */
    public ImageViewer setShowBar(boolean showBar) {
        isShowBar = showBar;
        return this;
    }

    /***
     * 设置是否重力系统传感器横竖屏显示
     * @param isForceScreen 退出查看器时是否强行转屏
     */
    public ImageViewer setShowScreen(boolean isForceScreen) {
        if (mContext instanceof Activity) {
            helper = new MySensorHelper(((Activity) mContext), isForceScreen);
            helper.enable();
        }
        return this;
    }

    /**
     * 多张图片的左右滑动动画
     */
    public ImageViewer setPageTransformer(ViewPager.PageTransformer pageViewerformer) {
        build.pageTransformer = pageViewerformer;
        return this;
    }

    /**
     * 设置更多布局，必须继承MoreViewInterface
     */
    public ImageViewer setMoreView(MoreViewInterface moreView) {
        build.setMoreView(moreView);
        build.isShowMore = true;
        return this;
    }

    /**
     * 是否显示滑动查看更多布局
     */
    public ImageViewer setShowMore(boolean isShowMore) {
        build.isShowMore = isShowMore;
        return this;
    }

    /**
     * 得到源图像(用于在退出图片查看器时缩放在图片控件上的动画)
     */
    public ImageViewer setSourceImageView(SourceImageViewGet sourceImageView) {
        build.sourceImageViewGet = sourceImageView;
        return this;
    }

    /**
     * //覆盖在图片上的布局(自定义布局需要继承ImageViewerAdapter)
     */
    public ImageViewer setAdapter(ImageViewerAdapter adapter) {
        build.imageViewerAdapter = adapter;
        return this;
    }

    /**
     * 单击图片的回调
     */
    public ImageViewer setAdapterClick(ImageViewerAdapter.OnImageViewerClick onImageViewerClick) {
        build.onImageViewerClick = onImageViewerClick;
        return this;
    }

    /**
     * 长按图片的监听事件
     */
    public ImageViewer setAdapterLongClick(ImageViewerAdapter.OnLongImageViewerClick onLongImageViewerClick) {
        build.onLongImageViewerClick = onLongImageViewerClick;
        return this;
    }

    /**
     * 设置图片加载器
     */
    public ImageViewer setImageLoad(ImageLoad imageLoad) {
        build.imageLoad = imageLoad;
        return this;
    }

    /**
     * 图片裁剪类型
     */
    public ImageViewer setScaleType(ScaleType scaleType) {
        build.scaleType = scaleType;
        return this;
    }

    public ImageViewer setConfig(ITConfig itConfig) {
        build.itConfig = itConfig;
        return this;
    }

    /**
     * 设置加载动画
     */
    public ImageViewer setProgressBar(ProgressViewGet progressViewGet) {
        build.progressViewGet = progressViewGet;
        return this;
    }

    private View createView() {
        dialogView = new DialogView(mContext, build);
        return dialogView;
    }

    /*private int getDialogStyle() {
        int dialogStyle;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            dialogStyle = android.R.style.Theme_Viewerlucent_NoTitleBar_Fullscreen;
        } else {
            dialogStyle = android.R.style.Theme_Viewerlucent_NoTitleBar;
        }
        return dialogStyle;
    }*/

    /**
     * 开始创建显示
     */
    public ImageViewer show() {
        build.checkParam();
        if (isShowBar) {
            mDialog = new Dialog(mContext) {
                @Override
                public void dismiss() {
                    if (helper != null) {
                        helper.disable();
                    }
                    super.dismiss();
                }

                @Override
                protected void onStart() {
                    if (getWindow() != null) {
                        getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));
                        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                    }
                }
            };
            mDialog.setContentView(createView());
        } else {
            AlertDialog dialog = new AlertDialog(mContext, R.style.MyDialogStyle) {
                @Override
                public void dismiss() {
                    if (helper != null) {
                        helper.disable();
                    }
                    super.dismiss();
                }
            };
            dialog.setView(createView());
            mDialog = dialog;
        }
        build.dialog = mDialog;
        mDialog.setOnShowListener(this);
        mDialog.setOnKeyListener(this);
        mDialog.show();
        return this;
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK &&
                event.getAction() == KeyEvent.ACTION_UP &&
                !event.isCanceled()) {
            dialogView.onDismiss(mDialog);
        }
        return true;
    }

    @Override
    public void onShow(DialogInterface dialog) {
        dialogView.onCreate(this);
    }

    @Override
    public void cancel() {
        dismiss();
    }

    @Override
    public void dismiss() {
        dialogView.onDismiss(mDialog);
    }


    public Dialog getDialog() {
        return mDialog;
    }

    public ImageViewerBuild getBuild() {
        return build;
    }

    public DialogView getDialogView() {
        return dialogView;
    }

    public Context getContext() {
        return mContext;
    }
}
