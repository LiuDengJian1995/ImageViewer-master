package com.liudengjian.imageviewer.listener;

/**
 * 上下拉关闭
 */

public interface OnPullCloseListener {

    void onClose();

    void onPull(float range);

    void onCancel();
}
