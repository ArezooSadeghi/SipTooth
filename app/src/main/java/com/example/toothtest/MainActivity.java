package com.example.toothtest;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.toothtest.databinding.ActivityMainBinding;
import com.example.toothtest.databinding.FloatWindowBinding;
import com.example.toothtest.databinding.ToothBinding;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private WindowManager windowManager;
    private FloatWindowBinding floatWindowBinding;
    private ToothBinding toothBinding;
    private WindowManager.LayoutParams layoutParams;
    private float lastX, lastY;
    private static int TYPE;

    private static final int REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        handleEvents();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Settings.canDrawOverlays(this)) {
                        handleWindowManager();
                    } else {
                        Toast.makeText(MainActivity.this, "دسترسی داده نشد", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (floatWindowBinding != null && floatWindowBinding.getRoot().isShown()) {
            windowManager.removeView(floatWindowBinding.getRoot());
        }

        if (toothBinding != null && toothBinding.getRoot().isShown()) {
            windowManager.removeView(toothBinding.getRoot());
        }
    }

    private void handleEvents() {
        binding.ivEnableAssistiveTouch.setOnTouchListener(new View.OnTouchListener() {
            private float xCoordinate, yCoordinate;
            private long startClickTime;
            private static final int MAX_CLICK_DURATION = 200;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startClickTime = Calendar.getInstance().getTimeInMillis();
                        xCoordinate = view.getX() - event.getRawX();
                        yCoordinate = view.getY() - event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                        if (clickDuration < MAX_CLICK_DURATION) {
                            if (canDrawOverlay()) {
                                handleWindowManager();
                            } else {
                                getDrawOverlayPermission();
                            }
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        view.animate().x(event.getRawX() + xCoordinate).y(event.getRawY() + yCoordinate).setDuration(0).start();
                        lastX = (int) (event.getRawX() + xCoordinate);
                        lastY = (int) (event.getRawY() + yCoordinate);
                        break;
                }
                return true;
            }
        });
    }

    private boolean canDrawOverlay() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(this);
        }
        return false;
    }

    private void getDrawOverlayPermission() {
        Intent starter = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
        startActivityForResult(starter, REQUEST_CODE);
    }

    private void handleWindowManager() {
        binding.ivEnableAssistiveTouch.setVisibility(View.GONE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            TYPE = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            TYPE = WindowManager.LayoutParams.TYPE_PHONE;
        }

        floatWindowBinding = DataBindingUtil.inflate(
                LayoutInflater.from(this),
                R.layout.float_window,
                null,
                false);

        toothBinding = DataBindingUtil.inflate(
                LayoutInflater.from(this),
                R.layout.tooth,
                null,
                false);

        layoutParams = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                TYPE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        layoutParams.x = (int) lastX;
        layoutParams.y = (int) lastY;
        /*        layoutParams.gravity = Gravity.TOP | Gravity.RIGHT;*/

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);


        windowManager.addView(floatWindowBinding.getRoot(), layoutParams);

        floatWindowBinding.getRoot().setOnTouchListener(new View.OnTouchListener() {
            private float xCoordinate, yCoordinate;
            private long startClickTime;
            private static final int MAX_CLICK_DURATION = 200;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startClickTime = Calendar.getInstance().getTimeInMillis();
                        xCoordinate = view.getX() - event.getRawX();
                        yCoordinate = view.getY() - event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                        if (clickDuration < MAX_CLICK_DURATION) {
                            int[] locationClose = new int[2];
                            floatWindowBinding.ivClose.getLocationOnScreen(locationClose);
                            int[] locationTL = new int[2];
                            floatWindowBinding.tvTl.getLocationOnScreen(locationTL);
                            int[] locationCardView = new int[2];
                            floatWindowBinding.cardView.getLocationOnScreen(locationCardView);
                            if (!(event.getRawX() >= locationCardView[0]) || !(event.getRawX() <= locationCardView[0] + floatWindowBinding.cardView.getWidth()) || !(event.getRawY() >= locationCardView[1]) || !(event.getRawY() <= locationCardView[1] + floatWindowBinding.cardView.getHeight())) {
                                floatWindowBinding.getRoot().setVisibility(View.GONE);
                                binding.ivEnableAssistiveTouch.setVisibility(View.VISIBLE);
                            } else if (event.getRawX() >= locationClose[0] && event.getRawX() <= locationClose[0] + floatWindowBinding.ivClose.getWidth() && event.getRawY() >= locationClose[1] && event.getRawY() <= locationClose[1] + floatWindowBinding.ivClose.getHeight()) {
                                floatWindowBinding.getRoot().setVisibility(View.GONE);
                                binding.ivEnableAssistiveTouch.setVisibility(View.VISIBLE);
                            } else if (event.getRawX() >= locationTL[0] && event.getRawX() <= locationTL[0] + floatWindowBinding.tvTl.getWidth() && event.getRawY() >= locationTL[1] && event.getRawY() <= locationTL[1] + floatWindowBinding.tvTl.getHeight()) {
                                floatWindowBinding.getRoot().setVisibility(View.GONE);
                                if (toothBinding.getRoot().getVisibility() == View.GONE) {
                                    toothBinding.getRoot().setVisibility(View.VISIBLE);
                                } else {
                                    windowManager.addView(toothBinding.getRoot(), layoutParams);
                                }
                            }
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        /* view.animate().x(event.getRawX() + xCoordinate).y(event.getRawY() + yCoordinate).setDuration(0).start();*/
                        layoutParams.x = (int) (event.getRawX() + xCoordinate);
                        layoutParams.y = (int) (event.getRawY() + yCoordinate);
                        windowManager.updateViewLayout(floatWindowBinding.getRoot(), layoutParams);
                        break;
                }
                return true;
            }
        });

        toothBinding.getRoot().setOnTouchListener(new View.OnTouchListener() {
            private float xCoordinate, yCoordinate;
            private long startClickTime;
            private static final int MAX_CLICK_DURATION = 200;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startClickTime = Calendar.getInstance().getTimeInMillis();
                        xCoordinate = view.getX() - event.getRawX();
                        yCoordinate = view.getY() - event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                        if (clickDuration < MAX_CLICK_DURATION) {
                            int[] locationClose = new int[2];
                            toothBinding.ivClose.getLocationOnScreen(locationClose);
                            int[] locationToothContainer = new int[2];
                            toothBinding.toothContainer.getLocationOnScreen(locationToothContainer);
                            if (!(event.getRawX() >= locationToothContainer[0]) || !(event.getRawX() <= locationToothContainer[0] + toothBinding.toothContainer.getWidth()) || !(event.getRawY() >= locationToothContainer[1]) || !(event.getRawY() <= locationToothContainer[1] + toothBinding.toothContainer.getHeight())) {
                                toothBinding.getRoot().setVisibility(View.GONE);
                                floatWindowBinding.getRoot().setVisibility(View.VISIBLE);
                            } else if (event.getRawX() >= locationClose[0] && event.getRawX() <= locationClose[0] + toothBinding.ivClose.getWidth() && event.getRawY() >= locationClose[1] && event.getRawY() <= locationClose[1] + toothBinding.ivClose.getHeight()) {
                                toothBinding.getRoot().setVisibility(View.GONE);
                                floatWindowBinding.getRoot().setVisibility(View.VISIBLE);
                            }
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        view.animate().x(event.getRawX() + xCoordinate).y(event.getRawY() + yCoordinate).setDuration(0).start();
                        break;
                }
                return true;
            }
        });
    }
}