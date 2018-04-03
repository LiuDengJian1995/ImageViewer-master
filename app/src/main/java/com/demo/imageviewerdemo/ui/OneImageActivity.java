package com.demo.imageviewerdemo.ui;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.demo.imageviewerdemo.R;
import com.demo.imageviewerdemo.imageload.ItemImageLoad;
import com.demo.imageviewerdemo.imageload.ItemProgressBarGet;
import com.demo.imageviewerdemo.view.ItemImageViewerAdapter;
import com.demo.imageviewerdemo.view.ItemMoreView;
import com.liudengjian.imageviewer.ImageViewer;
import com.liudengjian.imageviewer.adapter.ImageViewerAdapter;
import com.liudengjian.imageviewer.adapter.impl.MyImageViewerAdapter;
import com.liudengjian.imageviewer.adapter.indicator.IndicatorType;
import com.liudengjian.imageviewer.listener.SourceImageViewGet;
import com.liudengjian.imageviewer.util.ImageViewerUtil;

/**
 * 单张图片
 */
public class OneImageActivity extends AppCompatActivity implements
        View.OnClickListener, CompoundButton.OnCheckedChangeListener,
        RadioGroup.OnCheckedChangeListener {
    private ImageView mImgShow = null;
    public static String netImage = "http://v1.qzone.cc/pic/201803/29/20/34/5abcdd472beae707.jpeg!600x600.jpg";

    private IndicatorType mIndicatorType = IndicatorType.NULL;
    private CheckBox mColseViewerAnim;
    private CheckBox mViewerMore;
    private CheckBox mViewerItemMore;
    private CheckBox mViewerAdapterLayout;
    private CheckBox mViewerAdapterItemLayout;
    private CheckBox mViewerAdapterClick;
    private CheckBox mViewerAdapterLongClick;
    private CheckBox mViewerLoad;
    private CheckBox mViewerLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_image);

        initView();

        Glide.with(this).load(netImage)
                .placeholder(R.drawable.place_holder)
                .into(mImgShow);


    }

    private void initView() {
        mImgShow = (ImageView) findViewById(R.id.image_show);
        mImgShow.setOnClickListener(this);

        //打开关闭查看器动画
        mColseViewerAnim = (CheckBox) findViewById(R.id.cb_colse_viewer_anim);

        //右滑查看更多（仿淘宝）
        mViewerMore = (CheckBox) findViewById(R.id.cb_viewer_more);
        mViewerMore.setOnCheckedChangeListener(this);
        mViewerItemMore = (CheckBox) findViewById(R.id.cb_viewer_item_more);
        mViewerItemMore.setOnCheckedChangeListener(this);

        //图片上的布局
        mViewerAdapterLayout = (CheckBox) findViewById(R.id.cb_viewer_adapter_layout);
        mViewerAdapterLayout.setOnCheckedChangeListener(this);
        mViewerAdapterItemLayout = (CheckBox) findViewById(R.id.cb_viewer_adapter_item_layout);
        mViewerAdapterItemLayout.setOnCheckedChangeListener(this);
        ((RadioGroup) findViewById(R.id.radio_indicator)).setOnCheckedChangeListener(this);

        //图片上的布局的点击长按事件
        mViewerAdapterClick = (CheckBox) findViewById(R.id.cb_viewer_adapter_click);
        mViewerAdapterLongClick = (CheckBox) findViewById(R.id.cb_viewer_adapter_longClick);

        //图片加载器
        mViewerLoad = (CheckBox) findViewById(R.id.cb_viewer_load);
        //图片加载时的动画
        mViewerLoading = (CheckBox) findViewById(R.id.cb_viewer_loading);
    }


    @Override
    public void onClick(View v) {
        ImageViewer imageViewer = ImageViewer.with(this)
                //设置图片地址
                .setImageList(netImage);
        if (mColseViewerAnim.isChecked()) {
            //退出图片查看器时缩放在图片控件上的动画，没有这个方法则从中心点放大缩放图片
            imageViewer.setSourceImageView(new SourceImageViewGet() {
                @Override
                public ImageView getImageView(int pos) {
                    //这里返回缩放图片时的控件
                    return mImgShow;
                }
            });
        }
        if (mViewerMore.isChecked()) {
            //右滑显示加载更多（仿淘宝）
            imageViewer.setShowMore(true);
        }
        if (mViewerItemMore.isChecked()) {
            //右滑加载更多(自定义布局继承MoreViewInterface)
            imageViewer.setMoreView(new ItemMoreView(this));
        }
        if (mViewerAdapterLayout.isChecked()) {
            //覆盖在图片上的布局(多张图片时默认底部圆形指示器布局)
            imageViewer.setAdapter(new MyImageViewerAdapter(mIndicatorType));
        }
        if (mViewerAdapterItemLayout.isChecked()) {
            //覆盖在图片上的布局(自定义布局需要继承ImageViewerAdapter)
            imageViewer.setAdapter(new ItemImageViewerAdapter());
        }
        if (mViewerAdapterClick.isChecked()) {
            //覆盖在图片上的布局自定义长按事件
            imageViewer.setAdapterClick(new ImageViewerAdapter.OnImageViewerClick() {
                @Override
                public boolean onImageViewerClick(View v, int pos) {
                    Toast.makeText(OneImageActivity.this, "点击图片", Toast.LENGTH_SHORT).show();
                    //返回false关闭图片查看器
                    return true;
                }
            });
        }
        if (mViewerAdapterLongClick.isChecked()) {
            //覆盖在图片上的布局自定义长按事件
            imageViewer.setAdapterLongClick(new ImageViewerAdapter.OnLongImageViewerClick() {
                @Override
                public void onLongImageViewerClick(View v, int pos) {
                    Toast.makeText(OneImageActivity.this, "长按图片", Toast.LENGTH_SHORT).show();
                }
            });
        }
        if (mViewerLoad.isChecked()) {
            //图片加载器（默认实现DefaultImageLoad、自定义需要继承ImageLoad）因为要下载到本地所以需要读写权限
            //imageViewer.setImageLoad(new DefaultImageLoad());
            //为看清加载进度可以 ImageViewerUtil.isSleep做加载停顿。PS：正式用的时候不要把isSleep改为true
            ImageViewerUtil.isSleep = true;
            //自定义一个不需要读写权限的加载器
            imageViewer.setImageLoad(new ItemImageLoad());
        }

        if (mViewerLoading.isChecked()) {
            //图片加载时的动画（默认实现DefaultProgressBarGet、自定义需要继承ProgressViewGet<动画控件>）
            //已经写好不知进度的动画LoadingView、CustomLoadingView，知道动画的RingLoadingView，具体按项目要求更改
            //为看清加载进度可以 ImageViewerUtil.isSleep做加载停顿。PS：正式用的时候不要把isSleep改为true
            ImageViewerUtil.isSleep = true;
            imageViewer.setProgressBar(new ItemProgressBarGet());
        }


        //开始创建显示
        imageViewer.show();

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            if (buttonView == mViewerMore) {
                mViewerItemMore.setChecked(false);
            } else if (buttonView == mViewerItemMore) {
                mViewerMore.setChecked(false);
            } else if (buttonView == mViewerAdapterItemLayout) {
                mViewerAdapterLayout.setChecked(false);
            } else if (buttonView == mViewerAdapterLayout) {
                mViewerAdapterItemLayout.setChecked(false);
            }
        }

    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

        //设置指示器的位置
        switch (checkedId) {
            case R.id.btn_indicator_null:
                mIndicatorType = IndicatorType.NULL;//无指示器
                break;
            case R.id.btn_indicator_number_bottom:
                mIndicatorType = IndicatorType.NUMBER_BOTTOM;//底部数字指示器
                break;
            case R.id.btn_indicator_number_top:
                mIndicatorType = IndicatorType.NUMBER_TOP;//顶部数字指示器
                break;
            case R.id.btn_indicator_round_bottom://底部圆形指示器
                mIndicatorType = IndicatorType.ROUND_BOTTOM;
                break;
            case R.id.btn_indicator_round_top://顶部圆形指示器
                mIndicatorType = IndicatorType.ROUND_TOP;
                break;
            default:
                break;
        }
    }
}
