package com.example.minidouyin.bean;

import com.google.gson.annotations.SerializedName;

/**
 * @author: jq_lu
 * @Date: 2019/7/18
 * @Time: 19:19
 */
public class Feed {
    @SerializedName("student_id")
    private String mStudentId;
    @SerializedName("user_name")
    private String mUserName;
    @SerializedName("image_url")
    private String mImageUrl;
    @SerializedName("video_url")
    private String mVideoUrl;

    public String getStudentId() {
        return mStudentId;
    }

    public void setStudentId(String studentId) {
        mStudentId = studentId;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        mUserName = userName;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    public String getVideoUrl() {
        return mVideoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        mVideoUrl = videoUrl;
    }
}
