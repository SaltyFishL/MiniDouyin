package com.example.minidouyin.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author: jq_lu
 * @Date: 2019/7/18
 * @Time: 19:20
 */
public class FeedResponse {
    @SerializedName("success")
    private boolean mIsSuccess;
    @SerializedName("feeds")
    private List<Feed> mFeeds;

    public boolean isSuccess() {
        return mIsSuccess;
    }

    public List<Feed> getFeeds() {
        return mFeeds;
    }
}
