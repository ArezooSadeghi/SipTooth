package com.example.toothtest;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.example.toothtest.databinding.AssistiveTouchBinding;
import com.example.toothtest.databinding.FloatWindowBinding;
import com.example.toothtest.databinding.ToothBinding;

import java.util.Calendar;

public class ToothService extends Service {
    private AssistiveTouchBinding assistiveTouchBinding;
    private FloatWindowBinding floatWindowBinding;
    private ToothBinding toothBinding;
    private WindowManager windowManager;
    private static int TYPE;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            TYPE = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            TYPE = WindowManager.LayoutParams.TYPE_PHONE;
        }

        toothBinding = DataBindingUtil.inflate(
                LayoutInflater.from(this),
                R.layout.tooth,
                null,
                false);

        floatWindowBinding = DataBindingUtil.inflate(
                LayoutInflater.from(this),
                R.layout.float_window,
                null,
                false);

        assistiveTouchBinding = DataBindingUtil.inflate(
                LayoutInflater.from(this),
                R.layout.assistive_touch,
                null,
                false);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                TYPE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        layoutParams.gravity = Gravity.TOP | Gravity.RIGHT;
        layoutParams.windowAnimations = android.R.style.Animation_Translucent;

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        windowManager.addView(assistiveTouchBinding.getRoot(), layoutParams);

        assistiveTouchBinding.getRoot().setOnTouchListener(new View.OnTouchListener() {
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
                            assistiveTouchBinding.getRoot().setVisibility(View.GONE);
                            if (floatWindowBinding.getRoot().getVisibility() == View.GONE) {
                                floatWindowBinding.getRoot().setVisibility(View.VISIBLE);
                            } else {
                                windowManager.addView(floatWindowBinding.getRoot(), layoutParams);
                            }
                        }
                    case MotionEvent.ACTION_MOVE:
                        view.animate().x(event.getRawX() + xCoordinate).y(event.getRawY() + yCoordinate).setDuration(0).start();
                        break;
                }
                return true;
            }
        });

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
                                assistiveTouchBinding.getRoot().setVisibility(View.VISIBLE);
                            } else if (event.getRawX() >= locationClose[0] && event.getRawX() <= locationClose[0] + floatWindowBinding.ivClose.getWidth() && event.getRawY() >= locationClose[1] && event.getRawY() <= locationClose[1] + floatWindowBinding.ivClose.getHeight()) {
                                floatWindowBinding.getRoot().setVisibility(View.GONE);
                                assistiveTouchBinding.getRoot().setVisibility(View.VISIBLE);
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
                        view.animate().x(event.getRawX() + xCoordinate).y(event.getRawY() + yCoordinate).setDuration(0).start();
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

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        windowManager.removeView(assistiveTouchBinding.getRoot());
        windowManager.removeView(floatWindowBinding.getRoot());
        windowManager.removeView(toothBinding.getRoot());
    }

    public static Intent start(Context context) {
        return new Intent(context, ToothService.class);
    }
}
