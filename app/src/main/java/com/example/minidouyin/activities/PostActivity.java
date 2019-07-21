package com.example.minidouyin.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.minidouyin.R;
import com.example.minidouyin.utils.ResourceUtils;
import com.example.minidouyin.bean.PostResponse;
import com.example.minidouyin.network.IMiniDouyinService;
import com.example.minidouyin.network.RetrofitManager;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * @author: puppy
 * @Date: 2019/7/20
 * @Time: 14:28
 */

public class PostActivity extends AppCompatActivity {

    private VideoView postVideoView;
    private ImageView pauseImageView;
    private Button postVideoButton;
    private Button selectCoverButton;

    private static final int CHOOSE_IMAGE = 1;

    private String videoUriPath;
    private Uri imageUri;
    private Uri videoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        setTitle(R.string.preview);

        Intent intent = getIntent();
        videoUriPath =intent.getStringExtra("videoUri");
        videoUri = Uri.parse(videoUriPath);

        pauseImageView = findViewById(R.id.pauseImageView);
        postVideoButton = findViewById(R.id.postVideoButton);
        selectCoverButton = findViewById(R.id.selectCoverButton);
        postVideoView = findViewById(R.id.postVideoView);
        postVideoView.setVideoURI(videoUri);

        postVideoView.start();

        postVideoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(postVideoView.isPlaying()){
                    postVideoView.pause();
                    pauseImageView.setVisibility(View.VISIBLE);
                } else{
                    postVideoView.start();
                    pauseImageView.setVisibility(View.INVISIBLE);
                }
            }
        });

        postVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                postVideoView.start();
            }
        });

        selectCoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        postVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postVideo();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && null != data) {
            if (requestCode == CHOOSE_IMAGE) {
                imageUri = data.getData();
                postVideoView.start();
            }
        }
    }

    public void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), CHOOSE_IMAGE);
    }

    private MultipartBody.Part getMultipartFromUri(String name, Uri uri) {
        File file = new File(ResourceUtils.getRealPath(PostActivity.this, uri));
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        return MultipartBody.Part.createFormData(name, file.getName(), requestFile);
    }

    private void postVideo() {
        postVideoButton.setText(R.string.posting);
        postVideoButton.setEnabled(false);

        Retrofit retrofit = RetrofitManager.get("http://test.androidcamp.bytedance.com/");
        Call<PostResponse> call = retrofit.create(IMiniDouyinService.class).createVideo("123456","puppy",
                getMultipartFromUri("cover_image",imageUri),getMultipartFromUri("video",videoUri));
        call.enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                Toast.makeText(PostActivity.this,R.string.postSuccess,Toast.LENGTH_LONG).show();
                postVideoButton.setText(R.string.post);
                postVideoButton.setEnabled(true);
                startActivity(new Intent(PostActivity.this,HomeActivity.class));
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {

            }
        });
    }
}
