package com.demo.imageviewerdemo.imageload;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.liudengjian.imageviewer.net.ImageLoad;

import java.util.WeakHashMap;


/**
 * 网络图片加载器
 * Created by 刘登建 on 2018/3/31
 */

public class ItemImageLoad implements ImageLoad {

    /**
     * 图片加载器中用来回传下载好的图片集合
     */
    private static final WeakHashMap<String, LoadCallback> loadCallbackMap = new WeakHashMap<>();



    @Override
    public void loadImage(final String url, LoadCallback callback, final ImageView imageView, final String unique) {

        addLoadCallback(unique, callback);
        onProgress(unique, 10);

        Glide.with(imageView.getContext()).load(url)
//                .centerCrop()
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String s, Target<GlideDrawable> target, boolean b) {
                        Toast.makeText(imageView.getContext(),"加载失败",Toast.LENGTH_LONG).show();
                        onProgress(unique, -1);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(final GlideDrawable glideDrawable, String s, Target<GlideDrawable> target, boolean b, boolean b1) {
                        onProgress(unique, 100);
                        onFinishLoad(unique, glideDrawable);
                        return false;
                    }
                }).

                diskCacheStrategy(DiskCacheStrategy.NONE).
                dontAnimate().
                into(imageView);

    }

    @Override
    public boolean isCached(String url) {
        return false;
    }

    @Override
    public void cancel(String url, String unique) {
        removeLoadCallback(unique);

    }


    /**
     * 添加图片加载器中的回调
     */
    public static void addLoadCallback(String unique, LoadCallback callback) {
        loadCallbackMap.put(unique, callback);
    }

    /**
     * 删除图片加载器中的回调
     */
    public static void removeLoadCallback(String unique) {
        loadCallbackMap.remove(unique);
    }

    /**
     * 加载时的进度
     */
    public static void onProgress(String unique, float progress) {
        LoadCallback loadCallback = loadCallbackMap.get(unique);
        if (loadCallback != null) {
            loadCallback.progress(progress);
        }
    }

    /**
     * 加载完成并在回调集合中删除
     */
    public static void onFinishLoad(String unique, Drawable drawable) {
        LoadCallback loadCallback = loadCallbackMap.remove(unique);
        if (loadCallback != null) {
            loadCallback.loadFinish(drawable);
        }
    }

}