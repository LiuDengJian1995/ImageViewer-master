package com.liudengjian.imageviewer.config;

/**
 * 图片查看器的配置文件
 */
public class ITConfig {
    public static final float DEFAULT_READMODERULE = 2f;
    //阅读模式（长图的默认适宽显示）
    public boolean isReadMode = true;
    //默认小图显示thumb
    public boolean thumbLarge = false;
    //判定是否是长图的变量,默认是视图高度的1.5倍数
    public float readModeRule = DEFAULT_READMODERULE;
    //在有缓存的情况下是否显示缩略图
    public boolean noThumbWhenCached = false;
    //是否显示缩略图
    public boolean noThumb = false;


    public ITConfig enableReadMode(boolean enable) {
        isReadMode = enable;
        return this;
    }

    public ITConfig largeThumb() {
        thumbLarge = true;
        return this;
    }

    public ITConfig readModeRule(float rule) {
        this.readModeRule = rule;
        return this;
    }

    public ITConfig noThumbWhenCached() {
        noThumbWhenCached = true;
        return this;
    }

    public ITConfig noThumb() {
        noThumb = true;
        return this;
    }
}
