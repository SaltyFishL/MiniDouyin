package com.example.minidouyin.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.airbnb.lottie.LottieAnimationView;
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
    private ImageView returnImageView;
    private ImageView chooseImageView;
    private ImageView postImageView;
    private LottieAnimationView uploadAnim;

    private static final int CHOOSE_IMAGE = 1;
    private boolean coverSelected = false;
    private boolean isUploading = false;

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
        returnImageView = findViewById(R.id.returnImageView);
        chooseImageView = findViewById(R.id.chooseImageView);
        postImageView = findViewById(R.id.postImageView);
        postVideoView = findViewById(R.id.postVideoView);
        uploadAnim = findViewById(R.id.uploading);
        postVideoView.setVideoURI(videoUri);

        postVideoView.start();

        postVideoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(postVideoView.isPlaying()){
                    postVideoView.pause();
                    if(!isUploading) {
                        pauseImageView.setVisibility(View.VISIBLE);
                    }
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

        returnImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PostActivity.this,HomeActivity.class));
            }
        });

        returnImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN: {
                        returnImageView.setAlpha(0.5f);
                        break;
                    } case MotionEvent.ACTION_UP: {
                        returnImageView.setAlpha(1f);
                        break;
                    }
                }
                return false;
            }
        });

        chooseImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        chooseImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN: {
                        chooseImageView.setAlpha(0.5f);
                        break;
                    } case MotionEvent.ACTION_UP: {
                        chooseImageView.setAlpha(1f);
                        break;
                    }
                }
                return false;
            }
        });

        postImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postVideo();
            }
        });

        postImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN: {
                        postImageView.setAlpha(0.5f);
                        break;
                    } case MotionEvent.ACTION_UP: {
                        postImageView.setAlpha(1f);
                        break;
                    }
                }
                return false;
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
        coverSelected = true;
        startActivityForResult(Intent.createChooser(intent, "Select Image"), CHOOSE_IMAGE);
    }

    private MultipartBody.Part getMultipartFromUri(String name, Uri uri) {
        File file = new File(ResourceUtils.getRealPath(PostActivity.this, uri));
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        return MultipartBody.Part.createFormData(name, file.getName(), requestFile);
    }

    private void postVideo() {
        Toast.makeText(PostActivity.this,R.string.posting,Toast.LENGTH_LONG).show();
        postImageView.setEnabled(false);
        chooseImageView.setEnabled(false);
        uploadAnim.setVisibility(View.VISIBLE);
        isUploading = true;

        if(coverSelected == false) {
            imageUri = autoCover();
        }

        Retrofit retrofit = RetrofitManager.get("http://test.androidcamp.bytedance.com/");
        Call<PostResponse> call = retrofit.create(IMiniDouyinService.class).createVideo("123456","puppy",
                getMultipartFromUri("cover_image",imageUri),getMultipartFromUri("video",videoUri));
        call.enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                Toast.makeText(PostActivity.this,R.string.postSuccess,Toast.LENGTH_LONG).show();
                postImageView.setEnabled(true);
                chooseImageView.setEnabled(true);
                uploadAnim.setVisibility(View.INVISIBLE);
                startActivity(new Intent(PostActivity.this,HomeActivity.class));
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {

            }
        });
    }

    private Uri autoCover() {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(this,videoUri);
        Bitmap bitmap = mmr.getFrameAtTime();
        Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(),bitmap , null,null));
        return uri;
    }

}
