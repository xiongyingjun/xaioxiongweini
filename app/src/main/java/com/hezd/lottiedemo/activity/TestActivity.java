package com.hezd.lottiedemo.activity;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.chaquo.python.Python;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.GpsDirectory;
import com.hezd.lottiedemo.CalculateUtils;
import com.hezd.lottiedemo.R;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;



public class TestActivity extends AppCompatActivity {
    private TextView tvInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvInfo = (TextView) findViewById(R.id.tv_info);
    }

    @Override
    protected void onResume() {
        super.onResume();
        XXPermissions.with(this)
                .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                .request(new OnPermissionCallback() {

                    @Override
                    public void onGranted(List<String> permissions, boolean all) {
                        if (all) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    callPythonCode();
                                }
                            }).start();
                        }
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) {
                        if (never) {
//                            showToast("被永久拒绝授权，请手动授予存储权限");
                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
//                            XXPermissions.startPermissionActivity(mContext, permissions);
                        } else {
//                            showToast("获取存储权限失败");
                        }
                    }
                });
    }

    void callPythonCode() {
        String path1 = Environment.getExternalStorageDirectory().getPath() + "/aihangpy";
        String path2 = Environment.getExternalStorageDirectory().getPath() + "/aihangpy2";

        List<String> path = new ArrayList<>();
        path.add(path1 + "/DJI_0438.JPG");
        path.add(path1 + "/DJI_0439.JPG");
        path.add(path1 + "/DJI_0440.JPG");
        path.add(path1 + "/DJI_0441.JPG");
        path.add(path1 + "/DJI_0442.JPG");

        path.add(path2 + "/DJI_0444.JPG");
        path.add(path2 + "/DJI_0445.JPG");
        path.add(path2 + "/DJI_0451.JPG");
        path.add(path2 + "/DJI_0454.JPG");
        path.add(path2 + "/DJI_0455.JPG");
        try {
            //CalculateUtils.calculate(  将线线交跨需要提供十张图 按规定的顺序作为参数，返回的值为交叉点净距值，米为单位
            final double calculate = CalculateUtils.calculate(path);
            Log.i("calculate", calculate + "");

            tvInfo.post(new Runnable() {
                @Override
                public void run() {
                    tvInfo.setText("交叉点净距（m）" + calculate);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

