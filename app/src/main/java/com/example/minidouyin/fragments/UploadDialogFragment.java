package com.example.minidouyin.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.example.minidouyin.R;
import com.example.minidouyin.Utils.ResourceUtils;
import com.example.minidouyin.activities.RecordActivity;
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

import static android.app.Activity.RESULT_OK;

/**
 * @author: puppy
 * @Date: 2019/7/19
 * @Time: 21:15
 */

public class UploadDialogFragment extends DialogFragment {

    private Button upLoadButton_Record;
    private Button upLoadButton_Search;
    private Uri selectedImage;
    private Uri selectedVideo;

    private String[] totalPermissions = new String[] {Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO};
    private static final int REQUEST_RECORD = 1;
    private static final int CHOOSE_IMAGE = 1;
    private static final int CHOOSE_VIDEO = 2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.fragment_upload_dialog, container);

        upLoadButton_Record = view.findViewById(R.id.upLoadButton_Record);
        upLoadButton_Search = view.findViewById(R.id.upLoadButton_Search);

        initButtons();

        return view;
    }

    private void initButtons() {
        upLoadButton_Record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(),totalPermissions,REQUEST_RECORD);
                } else {
                    startActivity(new Intent(getActivity(), RecordActivity.class));
                }
            }
        });

        upLoadButton_Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_RECORD);
                } else {
                    String s = upLoadButton_Search.getText().toString();
                    if (getString(R.string.selectImage).equals(s)) {
                        chooseImage();
                    } else if (getString(R.string.selectVideo).equals(s)) {
                        chooseVideo();
                    } else if (getString(R.string.post).equals(s)){
                        postVideo();
                    }
                }
            }
        });
    }

    public void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), CHOOSE_IMAGE);
    }

    public void chooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Video"), CHOOSE_VIDEO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && null != data) {
            if (requestCode == CHOOSE_IMAGE) {
                selectedImage = data.getData();
                upLoadButton_Search.setText(R.string.selectVideo);
            } else if (requestCode == CHOOSE_VIDEO) {
                selectedVideo = data.getData();
                upLoadButton_Search.setText(R.string.post);
            }
        }
    }

    private MultipartBody.Part getMultipartFromUri(String name, Uri uri) {
        File file = new File(ResourceUtils.getRealPath(getActivity(), uri));
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        return MultipartBody.Part.createFormData(name, file.getName(), requestFile);
    }

    private void postVideo() {
        upLoadButton_Search.setText(R.string.posting);
        upLoadButton_Search.setEnabled(false);

        Retrofit retrofit = RetrofitManager.get("http://test.androidcamp.bytedance.com/");
        Call<PostResponse> call = retrofit.create(IMiniDouyinService.class).createVideo("123456","puppy",
                getMultipartFromUri("cover_image",selectedImage),getMultipartFromUri("video",selectedVideo));
        call.enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                Toast.makeText(getActivity(),R.string.postSuccess,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {

            }
        });
    }
}
