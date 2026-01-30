package com.hunk.afhunk;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MenuService extends Service {
    private WindowManager windowManager;
    private View floatingIcon;
    private View menuView;
    private WindowManager.LayoutParams iconParams;
    private WindowManager.LayoutParams menuParams;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotification();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        initFloatingIcon();
        initMenuView();
    }

    private void createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "menu_service";
            NotificationChannel channel = new NotificationChannel(channelId, "Service", NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
            Notification notification = new Notification.Builder(this, channelId)
                    .setContentTitle("AF HUNK Menu Running")
                    .setSmallIcon(android.R.drawable.ic_menu_info_details)
                    .build();
            startForeground(1, notification);
        }
    }

    private void initFloatingIcon() {
        TextView icon = new TextView(this);
        icon.setText("AF");
        icon.setGravity(Gravity.CENTER);
        icon.setTextColor(Color.WHITE);
        icon.setTextSize(18);
        icon.setTypeface(Typeface.DEFAULT_BOLD);
        
        // 创建圆形背景
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor("#E61A1A1A")); // 90% 不透明黑
        gd.setCornerRadius(75);
        gd.setStroke(3, Color.CYAN);
        icon.setBackground(gd);

        int type = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) ?
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                WindowManager.LayoutParams.TYPE_PHONE;

        iconParams = new WindowManager.LayoutParams(
                150, 150, type,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        iconParams.gravity = Gravity.TOP | Gravity.LEFT;
        iconParams.x = 100;
        iconParams.y = 100;

        icon.setOnTouchListener(new View.OnTouchListener() {
            private int initialX, initialY;
            private float initialTouchX, initialTouchY;
            private long lastDownTime;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = iconParams.x;
                        initialY = iconParams.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        lastDownTime = System.currentTimeMillis();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        iconParams.x = initialX + (int) (event.getRawX() - initialTouchX);
                        iconParams.y = initialY + (int) (event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(floatingIcon, iconParams);
                        return true;
                    case MotionEvent.ACTION_UP:
                        if (System.currentTimeMillis() - lastDownTime < 200) {
                            floatingIcon.setVisibility(View.GONE);
                            menuView.setVisibility(View.VISIBLE);
                        }
                        return true;
                }
                return false;
            }
        });

        floatingIcon = icon;
        windowManager.addView(floatingIcon, iconParams);
    }

    private void initMenuView() {
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        
        // 背景圆角
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.parseColor("#F2121212")); // 深黑灰色
        bg.setCornerRadius(30);
        bg.setStroke(2, Color.parseColor("#333333"));
        mainLayout.setBackground(bg);
        mainLayout.setPadding(10, 10, 10, 10);

        // 标题栏
        TextView title = new TextView(this);
        title.setText("AF HUNK INTERNAL");
        title.setTextColor(Color.CYAN);
        title.setTextSize(18);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 20, 0, 20);
        mainLayout.addView(title);

        // 分割线
        View line = new View(this);
        line.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2));
        line.setBackgroundColor(Color.parseColor("#333333"));
        mainLayout.addView(line);

        ScrollView scrollView = new ScrollView(this);
        LinearLayout.LayoutParams scrollLp = new LinearLayout.LayoutParams(600, 700);
        scrollView.setLayoutParams(scrollLp);

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(20, 20, 20, 20);

        // 功能组
        addCategory(content, "COMBAT");
        addToggleButton(content, "Aimbot [Safe]");
        addToggleButton(content, "Auto Headshot");
        addToggleButton(content, "No Recoil");

        addCategory(content, "VISUALS");
        addToggleButton(content, "ESP Line");
        addToggleButton(content, "ESP Box");
        addToggleButton(content, "ESP Distance");

        addCategory(content, "MISC");
        addToggleButton(content, "Speed Hack");
        addToggleButton(content, "God Mode");
        addToggleButton(content, "Unlock Skins");
        
        scrollView.addView(content);
        mainLayout.addView(scrollView);

        // 底部隐藏按钮
        Button hideBtn = new Button(this);
        hideBtn.setText("HIDE MENU");
        hideBtn.setTextColor(Color.WHITE);
        hideBtn.setBackgroundColor(Color.TRANSPARENT);
        hideBtn.setOnClickListener(v -> {
            menuView.setVisibility(View.GONE);
            floatingIcon.setVisibility(View.VISIBLE);
        });
        mainLayout.addView(hideBtn);

        int type = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) ?
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                WindowManager.LayoutParams.TYPE_PHONE;

        menuParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT, 
                WindowManager.LayoutParams.WRAP_CONTENT, 
                type,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        
        menuView = mainLayout;
        menuView.setVisibility(View.GONE);
        windowManager.addView(menuView, menuParams);

        // 允许菜单拖动
        mainLayout.setOnTouchListener(new View.OnTouchListener() {
            private int initialX, initialY;
            private float initialTouchX, initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = menuParams.x;
                        initialY = menuParams.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        menuParams.x = initialX + (int) (event.getRawX() - initialTouchX);
                        menuParams.y = initialY + (int) (event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(menuView, menuParams);
                        return true;
                }
                return false;
            }
        });
    }

    private void addCategory(LinearLayout parent, String name) {
        TextView cat = new TextView(this);
        cat.setText("— " + name + " —");
        cat.setTextColor(Color.GRAY);
        cat.setTextSize(12);
        cat.setGravity(Gravity.CENTER);
        cat.setPadding(0, 15, 0, 15);
        parent.addView(cat);
    }

    private void addToggleButton(LinearLayout parent, String featureName) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(10, 10, 10, 10);

        TextView label = new TextView(this);
        label.setText(featureName);
        label.setTextColor(Color.WHITE);
        label.setTextSize(14);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        label.setLayoutParams(lp);

        TextView status = new TextView(this);
        status.setText("OFF");
        status.setTextColor(Color.RED);
        status.setPadding(20, 10, 20, 10);
        
        final boolean[] isOn = {false};
        row.setOnClickListener(v -> {
            isOn[0] = !isOn[0];
            if (isOn[0]) {
                status.setText("ON");
                status.setTextColor(Color.GREEN);
                Toast.makeText(this, featureName + " Enabled", Toast.LENGTH_SHORT).show();
            } else {
                status.setText("OFF");
                status.setTextColor(Color.RED);
                Toast.makeText(this, featureName + " Disabled", Toast.LENGTH_SHORT).show();
            }
        });

        row.addView(label);
        row.addView(status);
        parent.addView(row);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (floatingIcon != null) windowManager.removeView(floatingIcon);
        if (menuView != null) windowManager.removeView(menuView);
    }
}