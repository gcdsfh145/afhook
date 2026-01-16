package com.hunk.afhunk;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.internal.view.SupportMenu;

/* loaded from: classes.dex */
public class MenuService extends Service {
    private View floatingIcon;
    private WindowManager.LayoutParams iconParams;
    private View menuView;
    private WindowManager windowManager;

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        createNotification();
        this.windowManager = (WindowManager) getSystemService("window");
        initFloatingIcon();
        initMenuView();
    }

    private void createNotification() {
        if (Build.VERSION.SDK_INT >= 26) {
            ((NotificationManager) getSystemService(NotificationManager.class)).createNotificationChannel(new NotificationChannel("menu_service", "Service", 2));
            startForeground(1, new Notification.Builder(this, "menu_service").setContentTitle("Menu Service Running").setSmallIcon(android.R.drawable.ic_menu_info_details).build());
        }
    }

    private void initFloatingIcon() {
        Button button = new Button(this);
        button.setText("M");
        button.setBackgroundColor(Color.parseColor("#CC000000"));
        button.setTextColor(-1);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(120, 120, Build.VERSION.SDK_INT >= 26 ? 2038 : 2002, 8, -3);
        this.iconParams = layoutParams;
        layoutParams.gravity = 51;
        this.iconParams.x = 100;
        this.iconParams.y = 100;
        button.setOnTouchListener(new View.OnTouchListener() { // from class: com.hunk.afhunk.MenuService.1
            private float initialTouchX;
            private float initialTouchY;
            private int initialX;
            private int initialY;

            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if (action == 0) {
                    this.initialX = MenuService.this.iconParams.x;
                    this.initialY = MenuService.this.iconParams.y;
                    this.initialTouchX = motionEvent.getRawX();
                    this.initialTouchY = motionEvent.getRawY();
                    return true;
                }
                if (action == 1) {
                    if (Math.abs(motionEvent.getRawX() - this.initialTouchX) < 10.0f && Math.abs(motionEvent.getRawY() - this.initialTouchY) < 10.0f) {
                        MenuService.this.floatingIcon.setVisibility(8);
                        MenuService.this.menuView.setVisibility(0);
                    }
                    return true;
                }
                if (action != 2) {
                    return false;
                }
                MenuService.this.iconParams.x = this.initialX + ((int) (motionEvent.getRawX() - this.initialTouchX));
                MenuService.this.iconParams.y = this.initialY + ((int) (motionEvent.getRawY() - this.initialTouchY));
                MenuService.this.windowManager.updateViewLayout(MenuService.this.floatingIcon, MenuService.this.iconParams);
                return true;
            }
        });
        this.floatingIcon = button;
        this.windowManager.addView(button, this.iconParams);
    }

    private void initMenuView() {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(1);
        linearLayout.setBackgroundColor(Color.parseColor("#F2121212"));
        linearLayout.setPadding(30, 30, 30, 30);
        TextView textView = new TextView(this);
        textView.setText("AF HUNK INTERNAL");
        textView.setTextColor(-16711681);
        textView.setTextSize(20.0f);
        textView.setPadding(0, 0, 0, 20);
        textView.setGravity(17);
        linearLayout.addView(textView);
        ScrollView scrollView = new ScrollView(this);
        LinearLayout linearLayout2 = new LinearLayout(this);
        linearLayout2.setOrientation(1);
        addToggleButton(linearLayout2, "Unlimited Gold");
        addToggleButton(linearLayout2, "God Mode");
        addToggleButton(linearLayout2, "Wallhack");
        addToggleButton(linearLayout2, "Speed Hack");
        addToggleButton(linearLayout2, "No Recoil");
        scrollView.addView(linearLayout2);
        linearLayout.addView(scrollView);
        Button button = new Button(this);
        button.setText("HIDE MENU");
        button.setBackgroundColor(-12303292);
        button.setTextColor(-1);
        button.setOnClickListener(new View.OnClickListener() { // from class: com.hunk.afhunk.MenuService$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                MenuService.this.lambda$initMenuView$0(view);
            }
        });
        linearLayout.addView(button);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-2, -2, Build.VERSION.SDK_INT >= 26 ? 2038 : 2002, 32, -3);
        this.menuView = linearLayout;
        linearLayout.setVisibility(8);
        this.windowManager.addView(this.menuView, layoutParams);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$initMenuView$0(View view) {
        this.menuView.setVisibility(8);
        this.floatingIcon.setVisibility(0);
    }

    private void addToggleButton(LinearLayout linearLayout, final String str) {
        final Button button = new Button(this);
        final boolean[] zArr = {false};
        button.setText(str + ": [OFF]");
        button.setTextColor(SupportMenu.CATEGORY_MASK);
        button.setBackgroundColor(0);
        button.setAllCaps(false);
        button.setOnClickListener(new View.OnClickListener() { // from class: com.hunk.afhunk.MenuService$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                MenuService.this.lambda$addToggleButton$1(zArr, button, str, view);
            }
        });
        linearLayout.addView(button);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$addToggleButton$1(boolean[] zArr, Button button, String str, View view) {
        boolean z = zArr[0];
        zArr[0] = !z;
        if (!z) {
            button.setText(str + ": [ON]");
            button.setTextColor(-16711936);
            Toast.makeText(this, str + " Enabled", 0).show();
        } else {
            button.setText(str + ": [OFF]");
            button.setTextColor(SupportMenu.CATEGORY_MASK);
            Toast.makeText(this, str + " Disabled", 0).show();
        }
    }

    @Override // android.app.Service
    public void onDestroy() {
        super.onDestroy();
        View view = this.floatingIcon;
        if (view != null) {
            this.windowManager.removeView(view);
        }
        View view2 = this.menuView;
        if (view2 != null) {
            this.windowManager.removeView(view2);
        }
    }
}
