# ImageViewer-master
一个仿微信、仿淘宝的图片查看器，参考了[ImageTrans](https://github.com/wufenglincheng/ImageTrans)，支持拖动图片手势返回 ，支持右滑查看详情

##### 不会做动态图、具体demo 请下载：[ImageViewer.apk](browse/ImageViewer.apk)</br>

## 使用步骤

#### Step 1.依赖imageviewer
Gradle
```groovy
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

```groovy
dependencies{
   compile 'com.github.LiuDengJian1995:ImageViewer-master:1.0.33'  //最新版本
}
```
或者引用本地lib
```groovy
compile project(':imageviewer')
```


#### Step 2.添加权限到你的 AndroidManifest.xml
```xml
<!-- 网络权限 -->
<uses-permission android:name="android.permission.INTERNET" /> 

<!--获取手机读写权限-->
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

## 使用方法
```
 ImageViewer.with(context)
                      //设置图片地址
                     .setImageList(imageList)
                      //查看器开始查看的位置
                     .setNowIndex(clickPos) 
                     //退出图片查看器时缩放在图片控件上的动画，没有这个方法则从中心点放大缩放图片，需要实现getImageView方法返回缩放图片时的ImageView控件
                     .setSourceImageView(new SourceImageViewGet())  
                     //右滑显示加载更多（仿淘宝）true显示false不显示 PS：已默认实现布局
                     .setShowMore(true)
                     //右滑加载更多(自定义布局继承MoreViewInterface)
                     .setMoreView(new ItemMoreView(context))
                     //覆盖在图片上的布局(不设置时多张图片时默认底部圆形指示器布局)(自定义布局需要继承ImageViewerAdapter)
                      .setAdapter(new MyImageViewerAdapter())
                      //覆盖在图片上的布局自定义长按事件,需要实现onImageViewerClick方法返回false关闭图片查看器
                      .setAdapterClick(new ImageViewerAdapter.OnImageViewerClick())
                      //覆盖在图片上的布局自定义长按事件,需要实现onLongImageViewerClick方法
                      .setAdapterLongClick(new ImageViewerAdapter.OnLongImageViewerClick())
                       //图片加载器（默认实现DefaultImageLoad、自定义需要继承ImageLoad）因为要下载到本地所以需要读写权限
                       //PS：可以自定义一个不需要读写权限的加载器
                        //为看清加载进度可以 ImageViewerUtil.isSleep做加载停顿。PS：正式用的时候不要把isSleep改为true
                      .setImageLoad(new DefaultImageLoad());
                      //图片加载时的动画（默认实现DefaultProgressBarGet、自定义需要继承ProgressViewGet<动画控件>）
                     //已经写好不知进度（循环）的动画LoadingView、CustomLoadingView，知道动画的RingLoadingView，具体按项目要求更改
                     .setProgressBar(new ProgressViewGet())
		     //设置是否重力系统传感器横竖屏显示,传入boolean类型表示退出查看器时是否强行转屏
		     //设置横竖屏显示建议配置ITConfig里的enableReadMode方法为false
		     //PS：在对应的activity配置文件加 android:configChanges="orientation|keyboardHidden|screenSize"
		     .setShowScreen(true)
		     //设置是否显示状态栏
		     .setShowBar(true)
                     //图片查看器的配置文件
                     setConfig(new ITConfig())
                    //开始创建显示
                     .show();
```


|ImageViewer方法|说明|
|:---|:---|
|setImageList(List<String>)|设置图片地址 |
|setImageList(String)|设置图片地址 |
|setNowIndex(int)| 查看器开始查看的位置 |
|setSourceImageView（SourceImageViewGet）|退出图片查看器时缩放在图片控件上的动画，没有这个方法则从中心点放大缩放图片|
|setShowMore(boolean)|右滑显示加载更多（仿淘宝）true显示false不显示 PS：已默认实现布局 |
|setMoreView(MoreViewInterface)|自定义右滑加载更多布局 |
|setAdapter(ImageViewerAdapter)|覆盖在图片上的布局(不设置时多张图片时默认底部圆形指示器布局) |
|setAdapterClick(OnImageViewerClick)|覆盖在图片上的布局自定义长按事件,需要实现onImageViewerClick方法返回false关闭图片查看器 |
|setAdapterLongClick(OnLongImageViewerClick)|覆盖在图片上的布局自定义长按事件,需要实现onImageViewerClick方法返回false关闭图片查看器 |
|setImageLoad(ImageLoad)|图片加载器、默认实现DefaultImageLoad |
|setProgressBar(ProgressViewGet<动画控件>)|图片加载时的动画、默认实现DefaultProgressBarGet |
|setShowScreen(boolean isForceScreen)|设置是否重力系统传感器横竖屏显示，isForceScreen 退出查看器时是否强行转屏 |
|设置横竖屏需要在对应的activity配置文件加| android:configChanges="orientation，keyboardHidden，screenSize"|
|setShowBar(boolean)|设置是否显示状态栏 true显示false不显示  |
	



|MyImageViewerAdapter（指示器类型）|说明|
|:---|:---|
|IndicatorType.NULL|无指示器 |
|IndicatorType.NUMBER_BOTTOM|底部数字指示器 |
|IndicatorType.NUMBER_TOP| 顶部数字指示器 |
|IndicatorType.ROUND_BOTTOM|底部圆形指示器|
|IndicatorType.ROUND_TOP|顶部圆形指示器 |



|ITConfig方法|说明|
|:---|:---|
|enableReadMode(boolean)|是否开启阅读模式，针对长图默认适宽显示 |
|largeThumb()| 预览图适宽显示，默认父容器宽度的1/2显示 |
|readModeRule（float）|自定义长图的判断标准，默认视图高度的1.5倍|
|noThumbWhenCached()|当有缓存的时候不显示预览图，直接显示原图 |
|noThumb()|不显示预览图 |


### 关于作者
    刘登建<br>
    贵州遵义<br>
	QQ：1044946823<br>

## 致谢
[ImageTrans](https://github.com/wufenglincheng/ImageTrans) 

## 更新说明

#### v1.0.33
 * 添加查看器的横竖屏切换
 * 添加查看器的状态栏显示

