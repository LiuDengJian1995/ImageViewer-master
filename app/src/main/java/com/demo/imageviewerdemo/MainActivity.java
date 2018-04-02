package com.demo.imageviewerdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.demo.imageviewerdemo.ui.MultiImageActivity;
import com.demo.imageviewerdemo.ui.OneImageActivity;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionListener;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_one).setOnClickListener(this);
        findViewById(R.id.btn_multi).setOnClickListener(this);
        startLocationPermission();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_one: //一张图片
                startActivity(new Intent(this, OneImageActivity.class));
                break;
            case R.id.btn_multi: //多张图片
                startActivity(new Intent(this, MultiImageActivity.class));
                break;
            default:
                break;
        }
    }


    public void startLocationPermission(){
        AndPermission.with(this)
                .requestCode(103) // 添加读写权限
                .permission(
                        // 申请多个权限组方式：
                        Permission.LOCATION
                )
                .callback(listener)
                .start();
    }

    private PermissionListener listener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, List<String> grantedPermissions) {
            // 权限申请成功回调。

            // 这里的requestCode就是申请时设置的requestCode。
            // 和onActivityResult()的requestCode一样，用来区分多个不同的请求。
            if(requestCode == 103) {//读写权限
                // TODO ...
            }
        }

        @Override
        public void onFailed(int requestCode, List<String> deniedPermissions) {
            // 权限申请失败回调。
            if(requestCode == 101) {
                // TODO ...
                // 第一种：用AndPermission默认的提示语。
//                AndPermission.defaultSettingDialog(PermissionCheckerDelegate.this, 400).show();
//                Toasty.info(getContext(), "权限申请失败", Toast.LENGTH_LONG).show();
                // 第二种：用自定义的提示语。
                AndPermission.defaultSettingDialog(MainActivity.this, 400)
                        .setTitle("权限申请失败")
                        .setMessage("您拒绝了我们必要的一些权限，请在设置中授权！")
//                        .setMessage("您拒绝了我们必要的一些权限，已经没法愉快的玩耍了，请在设置中授权！")
                        .setPositiveButton("好，去设置")
                        .show();
            }
        }
    };

}
