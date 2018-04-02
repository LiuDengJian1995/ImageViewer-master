package com.liudengjian.imageviewer.net.impl;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.liudengjian.imageviewer.util.ImageViewerUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 图片三级缓存的工具类
 * Created by ldj on 2018/3/31.
 */
public class ImageLoadUtils {

    private List<String> urls = new ArrayList<>();

    //LruCache内部实现实际就是HashMap集合
    private LruCache<String, Bitmap> mMemoryCache;

    //线程池
    private static ExecutorService mPool;
    private Handler mHandler;

    public ImageLoadUtils() {
        if (mMemoryCache == null) {
            //申请的内存空间占到总内存的1/8，
            // (int)Runtime.getRuntime().maxMemory()/8
            mMemoryCache = new LruCache<String, Bitmap>((int) Runtime.getRuntime().maxMemory() / 8) {
                @Override

                protected int sizeOf(String key, Bitmap value) {
                    // 判断添加进入的bitmap的占用内存的大小
                    return value.getRowBytes() * value.getHeight();
                }

                @Override
                protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                    super.entryRemoved(evicted, key, oldValue, newValue);
                    Log.v("tag", "hard cache is full , push to soft cache");
                }
            };
        }
        if (mHandler == null) {
            mHandler = new Handler();
        }
        if (mPool == null) {
            synchronized (ExecutorService.class) {
                if (mPool == null) {
                    //为了下载图片更加的流畅，我们用了3个线程来下载图片
                    mPool = Executors.newFixedThreadPool(3);
                }
            }
        }
    }

    public void cancel() {
       /* if (mPool != null) {
            mPool.shutdown(); // 平缓关闭服务
        }*/

        for (String url : urls) {
            File file = getCacheFile(url);
            if (file.exists()) {
                file.delete();
            }
        }


    }

    //显示图片
    public void display(String picUrl) {

      /*  //1,从内存中获取图片
        Bitmap bm = getFromMemory(picUrl);
        if (bm != null) {
            return;
        }

        //2，从本地获取图片
        bm = getFromLocal(picUrl);
        if (bm != null) {
            //保存到内存
            addBitmapToMemoryCache(picUrl, bm);
            return;
        }
*/
        //3，从网络获取图片
        getFromNet(picUrl);

    }

    private void getFromNet(String picUrl) {

        // 线程池管理
        mPool.execute(new LoadImageTask(picUrl));

    }

    public class LoadImageTask implements Runnable {
        String url;

        public LoadImageTask(String url) {
            this.url = url;
            urls.add(url);
        }

        @Override
        public void run() {

            HttpURLConnection conn = null;

            try {
//                conn = (HttpURLConnection) new URL(url).openConnection();
//                conn.setConnectTimeout(5000);// 连接服务器超时时间
//                conn.setReadTimeout(5000);// 读取超时时间
//                conn.connect();// 连接服务器
                URL uri = new URL(url);
                conn = (HttpURLConnection) uri.openConnection();
                conn.setConnectTimeout(5 * 1000);// 连接服务器超时时间
//                conn.setReadTimeout(5000);// 读取超时时间
                conn.setDoInput(true);
                conn.setUseCaches(false); //设置不使用缓存
                if (conn.getResponseCode() == 200) {
                    // 获取输入流
                    InputStream is = conn.getInputStream();
                    //把输入转化为bitmap
//                    Bitmap bm = BitmapFactory.decodeStream(is);
                    // 存储到本地
                    int totalLength = conn.getContentLength();
                    save2Local(is, url, totalLength);


                    if (downloadSuccess != null) {
                        downloadSuccess.success();
                    }
                    urls.remove(url);

                }

            } catch (Exception e) {
                if (exception != null) {
                    exception.exception(e);
                } else {
//                    e.printStackTrace();
                }
                File file = getCacheFile(url);
                if (file.exists()) file.delete();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }
    }

    /**
     * 添加Bitmap到内存缓存
     *
     * @param key
     * @param bitmap
     */
    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getFromMemory(key) == null && bitmap != null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    /**
     * 把bitmap保存在本地
     *
     * @param is
     * @param url
     */
    private void save2Local(InputStream is, String url, int total) throws Exception {

        File file = getCacheFile(url);
        FileOutputStream fos = new FileOutputStream(file);
       /*  Bitmap bitmap = BitmapFactory.decodeStream(is);
        //原封不动的保存在内存卡上
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);*/


        int len = 0;
        long sum = 0;
        byte[] buf = new byte[1024 * 2];

        boolean isSleep = ImageViewerUtil.isSleep;
        while ((len = is.read(buf)) != -1) {
            sum += len;
            fos.write(buf, 0, len);
            final long finalSum = sum;
            if (refreshProgress != null) {
                refreshProgress.progress(finalSum * 1.0f / total, total);
            }
            if (isSleep){
                //为更清楚看到图片加载的进度条，此处休眠短暂
                Thread.sleep(50);
            }
        }
        fos.flush();
        fos.close();
//        out.flush();
//        out.close();

    }


    /**
     * 从本地获取图片
     *
     * @param picUrl
     * @return
     */
    private Bitmap getFromLocal(String picUrl) {

        File file = getCacheFile(picUrl);
        if (file.exists()) {
            Bitmap bm = BitmapFactory.decodeFile(file.getAbsolutePath());

            // 存储到内存
            mMemoryCache.put(picUrl, bm);
            return bm;
        }
        return null;
    }

    /**
     * 根据url从内存中获取图片
     *
     * @param picUrl
     * @return
     */
    private Bitmap getFromMemory(String picUrl) {

        Bitmap bitmap = mMemoryCache.get(picUrl);

        return bitmap;
    }

    /**
     * 根据图片url获取存储路径
     *
     * @param picUrl
     * @return
     */
    public File getCacheFile(String picUrl) {
        //文件名加密

        File dir = new File(ImageViewerUtil.getImageCachePath());
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String key = ImageViewerUtil.generate(picUrl);
        String destUrl = ImageViewerUtil.getImageCachePath() + "/" + key;
        File file = new File(destUrl);
        return file;
    }

    interface CallbackException {
        void exception(Exception e);
    }

    private CallbackException exception;

    public void setException(CallbackException exception) {
        this.exception = exception;
    }

    interface RefreshProgress {
        void progress(final float progress, final long total);
    }

    private RefreshProgress refreshProgress;

    public void setRefreshProgress(RefreshProgress refreshProgress) {
        this.refreshProgress = refreshProgress;
    }

    interface DownloadSuccess {
        void success();
    }

    private DownloadSuccess downloadSuccess;

    public void setDownloadSuccess(DownloadSuccess downloadSuccess) {
        this.downloadSuccess = downloadSuccess;
    }

}