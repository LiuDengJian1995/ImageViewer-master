package com.demo.imageviewerdemo.view;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.demo.imageviewerdemo.R;
import com.liudengjian.imageviewer.adapter.ImageViewerAdapter;
import com.liudengjian.imageviewer.net.view.TileBitmapDrawable;

import static com.liudengjian.imageviewer.util.ImageViewerUtil.dpToPx;



public class ItemImageViewerAdapter extends ImageViewerAdapter {
    private RelativeLayout relativeLayout;
    private RelativeLayout topPanel;
    private boolean isShow = true;

    public ItemImageViewerAdapter(){
    }


    @Override
    public View onCreateView(View parent, ViewPager viewPager, final DialogInterface dialogInterface) {
        if (relativeLayout == null) {
            RelativeLayout.LayoutParams match_parent = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            RelativeLayout.LayoutParams match_parent_56 = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, dpToPx(56));
            RelativeLayout.LayoutParams image_parent = new RelativeLayout.LayoutParams(dpToPx(56), dpToPx(56));
            image_parent.addRule(RelativeLayout.CENTER_VERTICAL);

            RelativeLayout.LayoutParams indicator_parent = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(80));
            indicator_parent.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

            relativeLayout = new RelativeLayout(viewPager.getContext());
            relativeLayout.setId(R.id.relative_layout);
            relativeLayout.setLayoutParams(match_parent);

            topPanel = new RelativeLayout(viewPager.getContext());
            topPanel.setId(R.id.top_panel);
            topPanel.setLayoutParams(match_parent_56);
            topPanel.setBackgroundColor(Color.parseColor("#64000000"));

            ImageView top_panel_cancel = new ImageView(viewPager.getContext());
            top_panel_cancel.setId(R.id.top_panel_cancel);
            top_panel_cancel.setLayoutParams(image_parent);
            top_panel_cancel.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            top_panel_cancel.setImageResource(R.drawable.ic_keyboard_backspace_black_24dp);
            top_panel_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogInterface.cancel();
                }
            });



            topPanel.addView(top_panel_cancel);

            relativeLayout.addView(topPanel);


        }


        topPanel.setTranslationY(-dpToPx(56));

        return relativeLayout;
    }

    @Override
    public void onPullRange(float range) {
        topPanel.setTranslationY(-dpToPx(56) * range * 4);

    }

    @Override
    public void onPullCancel() {
        showPanel();
    }

    @Override
    public void onOpenViewerStart() {
        showPanel();
    }

    @Override
    public void onOpenViewerEnd() {

    }

    @Override
    public void onCloseViewerStart() {
        hiddenPanel();
    }

    @Override
    public void onCloseViewerEnd() {
        TileBitmapDrawable.clearCache();
    }

    @Override
    public boolean onClick(View v, int pos) {
        if (onImageViewer != null) {
            return onImageViewer.onImageViewerClick(v, pos);
        } else {
            if (isShow) {
                showPanel();
            } else {
                hiddenPanel();
            }
            isShow = !isShow;
            return true;
        }
    }

    @Override
    public void onLongClick(View v, int pos) {
        if (onLongImageViewer != null) {
            onLongImageViewer.onLongImageViewerClick(v, pos);
        }
    }

    public void hiddenPanel() {
        topPanel.animate().translationY(-dpToPx(56)).setDuration(200).start();

    }

    public void showPanel() {
        topPanel.animate().translationY(0).setDuration(200).start();
    }


}
