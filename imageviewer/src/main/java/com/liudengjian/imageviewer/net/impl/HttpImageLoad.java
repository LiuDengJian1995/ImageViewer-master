package com.liudengjian.imageviewer.net.impl;

import android.text.TextUtils;

import com.liudengjian.imageviewer.util.ImageViewerUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.Executor;


/**
 * 图片加载的工具过度类
 */
public class HttpImageLoad {

    private Platform mPlatform;

    private volatile static HttpImageLoad mInstance;
    private HashMap<String, Builder> map = new LinkedHashMap<>();

    private HttpImageLoad() {
        mPlatform = Platform.get();
    }

    private Executor getDelivery() {
        return mPlatform.defaultCallbackExecutor();
    }

    /**
     * 加载图片
     *
     * @param url
     * @param listener
     */
    public static void load(String url, ImageDownLoadListener listener) {
        if (TextUtils.isEmpty(url)) {
            listener.onError(new Exception("链接不能为null"));
            return;
        }
        if (mInstance == null) {
            mInstance = new HttpImageLoad();
        }
        Builder builder = null;
        if (mInstance.map.containsKey(url)) {
            builder = mInstance.map.get(url);
        } else if (checkImageExists(url)) {
            //没有发现正在下载，检验是否已经下载过了
            listener.onSuccess();
            return;
        }
        if (builder == null) {
            builder = new Builder(url);
            mInstance.map.put(url, builder);
        }
        builder.listener(listener);
        builder.start();
    }

    /**
     * 判断图片是否已经存在
     *
     * @param url
     * @return
     */
    public static boolean checkImageExists(String url) {
        String key = ImageViewerUtil.generate(url);
        String destUrl = ImageViewerUtil.getImageCachePath() + "/" + key;
        File file = new File(destUrl);
        if (file.exists()) {
            int size = ImageViewerUtil.getMaxSizeOfBitMap(destUrl);
            if (size > 0) {
                return true;
            } else {
                file.delete();
                return false;
            }
        }
        return false;
    }

    /**
     * 解绑监听器,实际下载还在后台进行
     *
     * @param url
     * @param listener
     */
    public static void cancel(String url, ImageDownLoadListener listener) {
        if (mInstance == null) {
            return;
        }
        if (mInstance.map.containsKey(url)) {
            Builder builder = mInstance.map.get(url);
            if (builder != null) {
                builder.removeListener(listener);
            }
        }
    }

    /**
     * 取消下载图片
     *
     * @param url
     * @param listener
     */
    public static void destroy(String url, ImageDownLoadListener listener) {
        if (mInstance == null) {
            return;
        }
        if (mInstance.map.containsKey(url)) {
            Builder builder = mInstance.map.get(url);
            if (builder != null) {
                mInstance.map.remove(url);
                builder.cancel();
                builder.removeListener(listener);
            }
        }
    }

    public static class Builder {
        protected String url;
        private List<ImageDownLoadListener> imageDownLoadListener = new ArrayList<>();
        private boolean isSuccess = false;
        private boolean isStarted = false;
        private float currentProgress = 0f;
        private long total = 0L;
        private State currentState = State.DOWNLOADING;
        private ImageLoadUtils imageUtils;

        private enum State {
            DOWNLOADING, DOWNLOADERROR, DOWNLOADFINISH
        }

        public Builder(String url) {
            this.url = url;
            imageUtils = new ImageLoadUtils();
        }

        public Builder listener(ImageDownLoadListener listener) {
            if (!imageDownLoadListener.contains(listener))
                imageDownLoadListener.add(listener);
            return this;
        }

        public void cancel() {
            if (null == imageUtils) {
                throw new NullPointerException(" ImageUtils没有初始化 ");
            }
            if (!isSuccess) {
                //切换到非UI线程，进行网络的取消工作
                ImageViewerUtil.cThreadPool.submit(new Runnable() {
                    @Override
                    public void run() {
                        imageUtils.cancel();
                    }
                });
                downloadCancel();
            }
        }

        private void execute() {
            isStarted = true;
            currentState = State.DOWNLOADING;
            imageUtils.setException(new ImageLoadUtils.CallbackException() {
                @Override
                public void exception(Exception e) {
                    downloadFail(e);
                }
            });
            imageUtils.setRefreshProgress(new ImageLoadUtils.RefreshProgress() {
                @Override
                public void progress(float progress, long total) {
                    refreshProgress(progress,total);
                }
            });
            imageUtils.setDownloadSuccess(new ImageLoadUtils.DownloadSuccess() {
                @Override
                public void success() {
                    downloadSuccess();
                }
            });
            imageUtils.display(url);

        }


        /**
         * 如果已经开启就不再执行网络加载操作
         */
        public void start() {
            checkState();
            if (!isStarted) {
                execute();
            }
        }

        private void checkState() {
            switch (currentState) {
                case DOWNLOADING:
                    refreshProgress(currentProgress, total);
                    break;
                case DOWNLOADFINISH:
                    downloadSuccess();
            }
        }

        private void downloadCancel() {
            for (ImageDownLoadListener listener : imageDownLoadListener)
                listener.onCancel();
        }

        private void refreshProgress(final float progress, final long total) {
            this.currentProgress = progress;
            this.total = total;
            mInstance.getDelivery().execute(new Runnable() {
                @Override
                public void run() {
                    for (ImageDownLoadListener listener : imageDownLoadListener)
                        listener.inProgress(progress, total);
                }
            });
        }

        private void downloadFail(final Exception e) {
            currentState = State.DOWNLOADERROR;
            String key = ImageViewerUtil.generate(url);
            String destUrl = ImageViewerUtil.getImageCachePath() + "/" + key;
            File file = new File(destUrl);
            if (file.exists()) file.delete();
            if (imageDownLoadListener.size() == 0) {
                //发现没有绑定任何监听，自动移除当前build
                mInstance.map.remove(url);
                return;
            }
            mInstance.mPlatform.execute(new Runnable() {
                @Override
                public void run() {
                    for (ImageDownLoadListener listener : imageDownLoadListener)
                        listener.onError(e);
                }
            });
        }

        private void downloadSuccess() {
            isSuccess = true;
            currentState = State.DOWNLOADFINISH;
            if (imageDownLoadListener.size() == 0) {
                //发现没有绑定任何监听，自动移除当前build
                mInstance.map.remove(url);
                return;
            }
            mInstance.mPlatform.execute(new Runnable() {
                @Override
                public void run() {
                    for (ImageDownLoadListener listener : imageDownLoadListener)
                        listener.onSuccess();
                }
            });
        }

        public void removeListener(ImageDownLoadListener listener) {
            imageDownLoadListener.remove(listener);
            if (imageDownLoadListener.size() == 0 && currentState == State.DOWNLOADFINISH) {
                mInstance.map.remove(url);
            }
        }
    }


    public interface ImageDownLoadListener {
        void inProgress(float progress, long total);

        void onError(Exception e);

        void onSuccess();

        void onCancel();
    }

}