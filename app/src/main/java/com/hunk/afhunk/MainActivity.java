package com.hunk.afhunk;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

/* loaded from: classes.dex */
public class MainActivity extends Activity {
    private static final String TAG = "AFHunk_Main";

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        try {
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(1);
            linearLayout.setGravity(17);
            Button button = new Button(this);
            button.setText("开启功能菜单");
            button.setPadding(50, 20, 50, 20);
            linearLayout.addView(button);
            setContentView(linearLayout);
            button.setOnClickListener(new View.OnClickListener() { // from class: com.hunk.afhunk.MainActivity$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    MainActivity.this.lambda$onCreate$0(view);
                }
            });
        } catch (Throwable th) {
            Log.e(TAG, "UI Layout error: " + th.getMessage());
            startMenuService();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$0(View view) {
        if (checkOverlayPermission()) {
            startMenuService();
        }
    }

    private boolean checkOverlayPermission() {
        if (Settings.canDrawOverlays(this)) {
            return true;
        }
        try {
            startActivityForResult(new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse("package:" + getPackageName())), 100);
        } catch (Exception unused) {
            Toast.makeText(this, "无法打开权限设置", 0).show();
        }
        return false;
    }

    private void startMenuService() {
        try {
            Intent intent = new Intent(this, (Class<?>) MenuService.class);
            if (Build.VERSION.SDK_INT >= 26) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }
            finish();
        } catch (Throwable th) {
            Log.e(TAG, "Service start failed: " + th.getMessage());
        }
    }

    @Override // android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 100 && checkOverlayPermission()) {
            startMenuService();
        }
    }
}
