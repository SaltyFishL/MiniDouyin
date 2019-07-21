package com.example.minidouyin.activities;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.minidouyin.R;
import com.example.minidouyin.bean.Feed;
import com.example.minidouyin.bean.FeedLab;
import com.example.minidouyin.db.TikTokDbHelper;
import com.example.minidouyin.fragments.FeedFragment;

import java.util.List;

/**
 * @author: jq_lu
 * @Date: 2019/7/20
 * @Time: 9:27
 */

public class FeedPagerActivity extends FragmentActivity {
    private ViewPager mViewPager;
    private List<Feed> mFeeds;
    private int mPosition;

    public static TikTokDbHelper sDbHelper;
    public static SQLiteDatabase sDatabase;

    private final String TAG = "FeedPagerActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPosition = getIntent().getIntExtra(FeedFragment.EXTRA_FEED_POSITION, 0);

        sDbHelper = new TikTokDbHelper(this);
        sDatabase = sDbHelper.getWritableDatabase();


        mViewPager = new ViewPager(this);
        mViewPager.setId(R.id.view_pager);
        Log.d(TAG, "onCreate: mPosition = " + mPosition);

//        Toast.makeText(this, "position = " + mPosition, Toast.LENGTH_SHORT).show();

        setContentView(mViewPager);

        mFeeds = FeedLab.get().getFeeds();

        FragmentManager fm = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return FeedFragment.newInstance(position);
            }

            @Override
            public int getCount() {
                return mFeeds.size();
            }
        });

        mViewPager.setCurrentItem(mPosition);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sDatabase.close();
        sDatabase = null;
        sDbHelper.close();
        sDbHelper = null;
    }
}