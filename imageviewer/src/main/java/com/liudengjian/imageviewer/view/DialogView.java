package com.liudengjian.imageviewer.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.liudengjian.imageviewer.ImageViewerBuild;
import com.liudengjian.imageviewer.listener.OnViewerformListener;
import com.liudengjian.imageviewer.view.listener.ViewPagerOnPageChangeListener;

import java.util.List;


/**
 * 重写对话框中的FrameLayout控件
 */
public class DialogView extends FrameLayout implements OnViewerformListener {

    private ImageViewerBuild build;

    /**
     * 创建一个可以拦截（设置是否可以滑动）的ViewPager
     */
    private InterceptViewPager viewPager;

    /**
     * ViewPager的适配器
     */
    private ImagePagerAdapter mAdapter;

    private boolean isOpened = false;

    public DialogView(Context context, ImageViewerBuild build) {
        super(context);
        this.build = build;
    }

    public void onCreate(DialogInterface dialogInterface) {
        viewPager = new InterceptViewPager(getContext());
        addView(viewPager, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mAdapter = new ImagePagerAdapter(build.imageList);
        viewPager.setAdapter(mAdapter);
        viewPager.setOffscreenPageLimit(1);
        viewPager.setCurrentItem(build.clickIndex);
        View maskView = build.imageViewerAdapter.onCreateView(this, viewPager, dialogInterface);
        if (maskView != null) {
            addView(maskView);
        }

        if (build.pageTransformer != null) {
            //true child倒叙
            //false child顺序
            viewPager.setPageTransformer(true, build.pageTransformer);
        }
        viewPager.setBackgroundColor(Color.BLACK);
        viewPager.getBackground().setAlpha(0);

        viewPager.addOnPageChangeListener(new ViewPagerOnPageChangeListener(build, build.getMoreView(), viewPager, this));
    }

    public void onDismiss(Dialog dialog) {
        ImageItemView itemView = mAdapter.getItemView(build.nowIndex);
        if (itemView != null) itemView.onDismiss();
        else dialog.dismiss();
    }


    @Override
    public void viewerformStart() {
        build.imageViewerAdapter.onOpenViewerStart();
        viewPager.setCanScroll(false);
    }

    @Override
    public void viewerformEnd() {
        isOpened = true;
        build.imageViewerAdapter.onOpenViewerEnd();
        viewPager.setCanScroll(true);
        mAdapter.loadWhenTransEnd();
    }


    /**
     * ViewPager的适配器
     */
    class ImagePagerAdapter extends PagerAdapter {

        /**
         * 控件图片地址的集合
         */
        private List<String> mData;

        /**
         * 控件的集合
         */
        private SparseArray<ImageItemView> itemViewSparseArray;

        public ImagePagerAdapter(@NonNull List<String> data) {
            mData = data;
            itemViewSparseArray = new SparseArray<>();
        }

        /**
         * 当要显示的图片可以进行缓存的时候，会调用这个方法进行显示图片的初始化，我们将要显示的ImageItemView加入到ViewGroup中，然后作为返回值返回即可
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (position < mData.size()) {
                ImageItemView view = itemViewSparseArray.get(position);
                if (view == null) {
                    view = new ImageItemView(container.getContext(), build, position, mData.get(position));
                    if (build.needTransOpen(position, false)) {
                        view.bindTransOpenListener(DialogView.this);
                    }
                    view.init(isOpened);
                    itemViewSparseArray.put(position, view);
                }
                container.addView(view);
                return view;
            } else {

                View hintView = build.getMoreView().onCreate();
                container.addView(hintView);
                return hintView;
            }
        }

        /**
         * 获取指定的控件
         */
        public ImageItemView getItemView(int pos) {
            return itemViewSparseArray.get(pos);
        }

        /**
         * PagerAdapter只缓存三张要显示的图片，如果滑动的图片超出了缓存的范围，就会调用这个方法，将图片销毁
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
            itemViewSparseArray.remove(position);
        }

        /**
         * 获取要滑动的控件的数量
         */
        @Override
        public int getCount() {
            if (mData == null){
                return 0;
            }else if(build.isShowMore){
                return mData.size() + 1;
            }else{
                return mData.size();
            }
        }

        /**
         * 来判断显示的是否是同一张图片，这里我们将两个参数相比较返回即可
         */
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        public void loadWhenTransEnd() {
            if (itemViewSparseArray != null) {
                for (int i = 0; i < itemViewSparseArray.size(); i++) {
                    ImageItemView itemView = itemViewSparseArray.valueAt(i);
                    if (itemView != null) itemView.loadImageWhenTransEnd();
                }
            }
        }
    }
}
