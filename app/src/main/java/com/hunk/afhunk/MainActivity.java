package com.hunk.afhunk;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final String TAG = "AFHunk_Main";

    // 静态块：防止 VMP 库加载失败导致整个类不可用
    static {
        try {
            // 这里可以尝试手动加载崩溃报告中提到的库，但加上 try-catch
            // System.loadLibrary("tdiegd");
        } catch (Throwable t) {
            Log.e(TAG, "Native library load failed: " + t.getMessage());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setGravity(Gravity.CENTER);

            Button btn = new Button(this);
            btn.setText("开启功能菜单");
            btn.setPadding(50, 20, 50, 20);
            
            layout.addView(btn);
            setContentView(layout);

            btn.setOnClickListener(v -> {
                if (checkOverlayPermission()) {
                    startMenuService();
                }
            });
        } catch (Throwable t) {
            Log.e(TAG, "UI Layout error: " + t.getMessage());
            // 如果布局都报错，尝试静默启动
            startMenuService();
        }
    }

    private boolean checkOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                try {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, 100);
                } catch (Exception e) {
                    Toast.makeText(this, "无法打开权限设置", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        }
        return true;
    }

    private void startMenuService() {
        try {
            Intent intent = new Intent(this, MenuService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }
            finish();
        } catch (Throwable t) {
            Log.e(TAG, "Service start failed: " + t.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (checkOverlayPermission()) {
                startMenuService();
            }
        }
    }
}