package com.demo.imageviewerdemo.view;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.liudengjian.imageviewer.ImageViewerBuild;
import com.liudengjian.imageviewer.R;
import com.liudengjian.imageviewer.view.DialogView;
import com.liudengjian.imageviewer.view.interfaces.MoreViewInterface;


/**
 * 右滑查看更多
 * Created by 刘登建 on 2018/3/30
 */

public class ItemMoreView implements MoreViewInterface {
    private ImageView imageView;
    private TextView textView;
    private Context context;
    private LinearLayout mLinearLayout = null;

    public ItemMoreView(Context context) {
        this.context = context;
    }


    @Override
    public ImageView getImageView() {
        return imageView;
    }

    @Override
    public TextView getTextView() {
        return textView;
    }

    @Override
    public String getTextEnd() {
        return "松开关闭查看器";
    }

    @Override
    public String getTextStart() {
        return "继续滑动将关闭查看器";
    }

    @Override
    public View onCreate() {
        if (mLinearLayout==null){
            mLinearLayout = new LinearLayout(context);
            mLinearLayout.setId(R.id.more_layout);
            mLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            LinearLayout mainLayout = new LinearLayout(context);
            mainLayout.setOrientation(LinearLayout.HORIZONTAL);
            mainLayout.setPadding(5, 0, 0, 0);
            mLinearLayout.addView(mainLayout, lp);

            lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.CENTER_VERTICAL;
            imageView = new ImageView(context);
            imageView.setId(R.id.more_image);
            imageView.setImageResource(R.drawable.ic_more_arrow);
            mainLayout.addView(imageView, lp);

            lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.CENTER_VERTICAL;
            lp.setMargins(5, 0, 0, 0);
            textView = new TextView(context);
            textView.setId(R.id.more_text);
            textView.setText(getTextStart());
            textView.setTextColor(Color.WHITE);
            textView.setMaxEms(1);
            textView.setEms(1);
            mainLayout.addView(textView, lp);
        }
        return mLinearLayout;
    }

    @Override
    public void setScrollEndClick(DialogView dialogView, ImageViewerBuild build) {
        dialogView.onDismiss(build.dialog);
        Toast.makeText(context,"滑动结束",Toast.LENGTH_LONG).show();
    }

}
