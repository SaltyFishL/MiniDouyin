package com.example.minidouyin.network;

import com.example.minidouyin.bean.FeedResponse;
import com.example.minidouyin.bean.PostResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;


/**
 * @author: jq_lu
 * @Date: 2019/7/19
 * @Time: 0:18
 */
public interface IMiniDouyinService {
    String HOST = "http://test.androidcamp.bytedance.com/";

    @Multipart
    @POST("/mini_douyin/invoke/video")
    Call<PostResponse> createVideo(
            @Query("student_id") String mStudentId,
            @Query("user_name") String mUserName,
            @Part MultipartBody.Part mImage, @Part MultipartBody.Part mVideo
    );

    @GET("/mini_douyin/invoke/video")
    Call<FeedResponse> getFeeds();
}
