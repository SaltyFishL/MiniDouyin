package com.example.minidouyin.activities;

import android.util.Log;

import androidx.fragment.app.Fragment;

import com.example.minidouyin.fragments.HomeFragment;

public class HomeActivity extends AbstractSingleFragmentActivity {

    private static final String TAG = "HomeActivity";

    @Override
    protected Fragment createFragment() {
        Log.d(TAG, "createFragment: ");
        return new HomeFragment();
    }

}
