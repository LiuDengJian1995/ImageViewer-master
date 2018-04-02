package com.liudengjian.imageviewer.net.impl;

import android.content.ContentResolver;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.liudengjian.imageviewer.net.ImageLoad;
import com.liudengjian.imageviewer.util.ImageViewerUtil;
import com.liudengjian.imageviewer.net.view.TileBitmapDrawable;

import java.util.HashMap;
import java.util.WeakHashMap;
import java.util.regex.Pattern;


/**
 * 网络图片加载器
 * Created by 刘登建 on 2018/3/31
 */

public class DefaultImageLoad implements ImageLoad {
    private static final Pattern webPattern = Pattern.compile("http[s]*://[[[^/:]&&[a-zA-Z_0-9]]\\.]+(:\\d+)?(/[a-zA-Z_0-9]+)*(/[a-zA-Z_0-9]*([a-zA-Z_0-9]+\\.[a-zA-Z_0-9]+)*)?(\\?(&?[a-zA-Z_0-9]+=[%[a-zA-Z_0-9]-]*)*)*(#[[a-zA-Z_0-9]|-]+)?(.jpg|.png|.gif|.jpeg)?");
    private static final String ASSET_PATH_SEGMENT = "android_asset";

    /**图片加载器中用来回传下载好的图片集合*/
    private static final WeakHashMap<String, LoadCallback> loadCallbackMap = new WeakHashMap<>();

    /**图片加载集合*/
    private static final HashMap<String, HttpImageLoad.ImageDownLoadListener> imageDownLoadListenerMap = new HashMap<>();


    @Override
    public void loadImage(String url, LoadCallback callback, ImageView imageView, String unique) {
        addLoadCallback(unique, callback);
        Uri uri = Uri.parse(url);
        if (isLocalUri(uri.getScheme())) {
            if (isAssetUri(uri)) {
                //是asset资源文件

                return;
            } else {
                //是本地文件
                loadImageFromLocal(uri.getPath(), unique, imageView);
                return;
            }
        } else {
            if (isNetUri(url)) {
                loadImageFromNet(url, unique, imageView);
                return;
            }
            Log.e("MyImageLoad", "未知的图片URL的类型");
        }
    }

    @Override
    public boolean isCached(String url) {
        if (isLocalUri(Uri.parse(url).getScheme())) {
            //是本地图片不用预览图
            return true;
        }
        return HttpImageLoad.checkImageExists(url);
    }

    @Override
    public void cancel(String url, String unique) {
        removeLoadCallback(unique);

        HttpImageLoad.destroy(url, imageDownLoadListenerMap.remove(unique));
    }

    /**
     * 从本地加载图片
     */
    private void loadImageFromLocal(String url, final String unique, final ImageView imageView) {
        TileBitmapDrawable.attachTileBitmapDrawable(imageView, url, new TileBitmapDrawable.OnLoadListener() {
            @Override
            public void onLoadFinish(TileBitmapDrawable drawable) {
                onFinishLoad(unique, drawable);
            }

            @Override
            public void onError(Exception ex) {

            }
        });
    }

    /**
     * 从网络加载图片
     */
    private void loadImageFromNet(final String url, final String unique, final ImageView imageView) {
        HttpImageLoad.ImageDownLoadListener loadListener = new HttpImageLoad.ImageDownLoadListener() {
            @Override
            public void inProgress(float progress, long total) {
                onProgress(unique, progress);
            }

            @Override
            public void onError(Exception e) {

            }

            @Override
            public void onSuccess() {
                loadImageFromLocal(ImageViewerUtil.getCachedPath(url), unique, imageView);
            }

            @Override
            public void onCancel() {

            }

        };
        imageDownLoadListenerMap.put(unique, loadListener);
        HttpImageLoad.load(url, loadListener);
    }


    /**添加图片加载器中的回调*/
    public static void addLoadCallback(String unique, LoadCallback callback) {
        loadCallbackMap.put(unique, callback);
    }

    /**删除图片加载器中的回调*/
    public static void removeLoadCallback(String unique) {
        loadCallbackMap.remove(unique);
    }

    /**加载时的进度*/
    public static void onProgress(String unique, float progress) {
        LoadCallback loadCallback = loadCallbackMap.get(unique);
        if (loadCallback != null) {
            loadCallback.progress(progress);
        }
    }

    /**加载完成并在回调集合中删除*/
    public static void onFinishLoad(String unique, Drawable drawable) {
        LoadCallback loadCallback = loadCallbackMap.remove(unique);
        if (loadCallback != null) {
            loadCallback.loadFinish(drawable);
        }
    }

    /**是否本地文件*/
    private static boolean isLocalUri(String scheme) {
        return ContentResolver.SCHEME_FILE.equals(scheme)
                || ContentResolver.SCHEME_CONTENT.equals(scheme)
                || ContentResolver.SCHEME_ANDROID_RESOURCE.equals(scheme);
    }

    /**是否asset资源本地文件*/
    public static boolean isAssetUri(Uri uri) {
        return ContentResolver.SCHEME_FILE.equals(uri.getScheme()) && !uri.getPathSegments().isEmpty()
                && ASSET_PATH_SEGMENT.equals(uri.getPathSegments().get(0));
    }

    /**是否网络文件*/
    private static boolean isNetUri(String url) {
        return webPattern.matcher(url).find();
    }
}