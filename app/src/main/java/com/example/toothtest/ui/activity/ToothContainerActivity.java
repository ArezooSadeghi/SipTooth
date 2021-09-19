package com.example.toothtest.ui.activity;

import androidx.fragment.app.Fragment;

import com.example.toothtest.ui.fragment.ToothFragment;

public class ToothContainerActivity extends SingleFragmentActivity {

    @Override
    public Fragment createFragment() {
        return ToothFragment.newInstance();
    }
}