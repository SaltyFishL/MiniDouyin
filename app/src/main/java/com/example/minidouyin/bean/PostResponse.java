package com.example.minidouyin.bean;

import com.google.gson.annotations.SerializedName;

/**
 * @author: jq_lu
 * @Date: 2019/7/18
 * @Time: 19:21
 */
public class PostResponse {
    @SerializedName("success")
    private boolean mIsSuccess;
    @SerializedName("item")
    private Feed mFeed;

    public boolean isSuccess() {
        return mIsSuccess;
    }

    public Feed setFeed() {
        return mFeed;
    }
}
