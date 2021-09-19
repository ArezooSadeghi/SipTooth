package com.example.toothtest.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.toothtest.R;
import com.example.toothtest.databinding.FloatWindowOneBinding;
import com.example.toothtest.databinding.FloatWindowTwoBinding;
import com.example.toothtest.databinding.FragmentToothBinding;

import java.util.Calendar;

public class ToothFragment extends Fragment {
    private FragmentToothBinding binding;
    private FloatWindowOneBinding floatWindowOneBinding;
    private FloatWindowTwoBinding floatWindowTwoBinding;
    private WindowManager windowManager;
    private static final int REQUEST_CODE = 0;

    public static ToothFragment newInstance() {
        ToothFragment fragment = new ToothFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_tooth,
                container,
                false);

        handleEvents();

        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && !Settings.canDrawOverlays(getContext())) {
            new CountDownTimer(15000, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                    if (Settings.canDrawOverlays(getContext())) {
                        this.cancel();
                        showWindow();
                    }
                }

                @Override
                public void onFinish() {
                    if (Settings.canDrawOverlays(getContext())) {
                        showWindow();
                    } else {
                        Toast.makeText(getContext(), "دسترسی داده نشد", Toast.LENGTH_SHORT).show();
                    }
                }
            }.start();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (floatWindowOneBinding != null && floatWindowOneBinding.getRoot().isShown()) {
            windowManager.removeView(floatWindowOneBinding.getRoot());
        }
        if (floatWindowTwoBinding != null && floatWindowTwoBinding.getRoot().isShown()) {
            windowManager.removeView(floatWindowTwoBinding.getRoot());
        }
    }

    private void handleEvents() {
        binding.ivAssistiveTouch.setOnTouchListener(new View.OnTouchListener() {
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
                            if (hasPermission()) {
                                showWindow();
                            } else {
                                getPermission();
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

    private boolean hasPermission() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(getContext());
    }

    private void getPermission() {
        Intent starter = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getContext().getPackageName()));
        startActivityForResult(starter, REQUEST_CODE);
    }

    private void showWindow() {
        binding.ivAssistiveTouch.setVisibility(View.GONE);

        floatWindowOneBinding = DataBindingUtil.inflate(
                LayoutInflater.from(getContext()),
                R.layout.float_window_one,
                null,
                false);

        floatWindowTwoBinding = DataBindingUtil.inflate(
                LayoutInflater.from(getContext()),
                R.layout.float_window_two,
                null,
                false);

        int w = WindowManager.LayoutParams.WRAP_CONTENT;
        int h = WindowManager.LayoutParams.WRAP_CONTENT;
        int xpos = (int) binding.ivAssistiveTouch.getX();
        int ypos = (int) binding.ivAssistiveTouch.getY();
        int _type;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            _type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            _type = WindowManager.LayoutParams.TYPE_PHONE;
        }

        int _flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        int _format = PixelFormat.TRANSLUCENT;

        WindowManager.LayoutParams windowParams = new WindowManager.LayoutParams(w, h, xpos, ypos, _type, _flags, _format);
        windowParams.gravity = Gravity.TOP | Gravity.LEFT;
        windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.addView(floatWindowOneBinding.getRoot(), windowParams);

        floatWindowOneBinding.getRoot().setOnTouchListener(new View.OnTouchListener() {
            private float xCoordinate, yCoordinate;
            private long startClickTime;
            private static final int MAX_CLICK_DURATION = 200;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startClickTime = Calendar.getInstance().getTimeInMillis();
                        xCoordinate = windowParams.x - event.getRawX();
                        yCoordinate = windowParams.y - event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                        if (clickDuration < MAX_CLICK_DURATION) {
                            int[] locationClose = new int[2];
                            floatWindowOneBinding.ivClose.getLocationOnScreen(locationClose);
                            int[] locationTL = new int[2];
                            floatWindowOneBinding.tvTl.getLocationOnScreen(locationTL);
                            int[] locationCardView = new int[2];
                            floatWindowOneBinding.cardView.getLocationOnScreen(locationCardView);
                            if (!(event.getRawX() >= locationCardView[0]) || !(event.getRawX() <= locationCardView[0] + floatWindowOneBinding.cardView.getWidth()) || !(event.getRawY() >= locationCardView[1]) || !(event.getRawY() <= locationCardView[1] + floatWindowOneBinding.cardView.getHeight())) {
                                floatWindowOneBinding.getRoot().setVisibility(View.GONE);
                                binding.ivAssistiveTouch.setVisibility(View.VISIBLE);
                            } else if (event.getRawX() >= locationClose[0] && event.getRawX() <= locationClose[0] + floatWindowOneBinding.ivClose.getWidth() && event.getRawY() >= locationClose[1] && event.getRawY() <= locationClose[1] + floatWindowOneBinding.ivClose.getHeight()) {
                                floatWindowOneBinding.getRoot().setVisibility(View.GONE);
                                binding.ivAssistiveTouch.setVisibility(View.VISIBLE);
                            } else if (event.getRawX() >= locationTL[0] && event.getRawX() <= locationTL[0] + floatWindowOneBinding.tvTl.getWidth() && event.getRawY() >= locationTL[1] && event.getRawY() <= locationTL[1] + floatWindowOneBinding.tvTl.getHeight()) {
                                floatWindowOneBinding.getRoot().setVisibility(View.GONE);
                                if (floatWindowTwoBinding.getRoot().getVisibility() == View.GONE) {
                                    floatWindowTwoBinding.getRoot().setVisibility(View.VISIBLE);
                                } else {
                                    windowManager.addView(floatWindowTwoBinding.getRoot(), windowParams);
                                }
                            }
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        windowParams.x = (int) (event.getRawX() + xCoordinate);
                        windowParams.y = (int) (event.getRawY() + yCoordinate);
                        windowManager.updateViewLayout(view, windowParams);
                        break;
                }
                return true;
            }
        });

        floatWindowTwoBinding.getRoot().setOnTouchListener(new View.OnTouchListener() {
            private float xCoordinate, yCoordinate;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        xCoordinate = windowParams.x - event.getRawX();
                        yCoordinate = windowParams.y - event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        windowParams.x = (int) (event.getRawX() + xCoordinate);
                        windowParams.y = (int) (event.getRawY() + yCoordinate);
                        windowManager.updateViewLayout(view, windowParams);
                        break;
                }
                return true;
            }
        });
    }

   /* @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                swipeScreen(); //if action recognized as swipe then swipe
                break;
            case MotionEvent.ACTION_MOVE:
                float x = event.getX();
                float y = event.getY();
                float xDelta = Math.abs(x - mLastX);
                float yDelta = Math.abs(y - mLastY);

                if (yDelta > xDelta) {
                    return true;
                }
                break;
        }

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        ButtonOnClick(); //if not a swipe, then button click
        return true;
    }*/
}
