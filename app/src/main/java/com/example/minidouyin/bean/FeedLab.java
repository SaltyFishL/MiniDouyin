package com.example.minidouyin.bean;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: jq_lu
 * @Date: 2019/7/20
 * @Time: 14:16
 */
public class FeedLab {
    private List<Feed> mFeeds;

    private static FeedLab sFeedLab;
    private static final String TAG = "FeedLab";

    private Context mContext;

    private FeedLab(Context appContext) {
        mContext = appContext;
        mFeeds = new ArrayList<>();
    }

    public static FeedLab get(Context context) {
        if (sFeedLab == null) {
            sFeedLab = new FeedLab(context.getApplicationContext());
        }

        return sFeedLab;
    }

    public List<Feed> getFeeds() {
        return mFeeds;
    }

    public void setFeeds(List<Feed> feeds) {
        mFeeds = feeds;
    }
}
