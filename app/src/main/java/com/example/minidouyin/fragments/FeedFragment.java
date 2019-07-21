package com.example.minidouyin.fragments;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.example.minidouyin.R;
import com.example.minidouyin.bean.Feed;
import com.example.minidouyin.bean.FeedLab;
import com.example.minidouyin.views.FullScreenVideoView;

/**
 * @author: jq_lu
 * @Date: 2019/7/20
 * @Time: 13:15
 */
public class FeedFragment extends Fragment {

    public static final String EXTRA_FEED_POSITION =
            "com.example.minidouyin.feed_position";
    private static final String TAG = "FeedFragment";

    private FullScreenVideoView mVideoView;
    private LottieAnimationView mLoading;
    private LottieAnimationView mHeart;
    private ImageView mPlayImage;
    private Feed mFeed;

//    private TextView mTextView;

    public static FeedFragment newInstance(int position) {
        Bundle args = new Bundle();
        args.putInt(EXTRA_FEED_POSITION, position);
        Log.d(TAG, "newInstance: put position " + position + " in args");
        FeedFragment fragment = new FeedFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int position = 0;
        if (getArguments() != null) {
            position = getArguments().getInt(EXTRA_FEED_POSITION);
        }
        Log.d(TAG, "onCreate: position = " + position);
        mFeed = FeedLab.get().getFeeds().get(position);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        mVideoView = view.findViewById(R.id.video_view);
        mPlayImage = view.findViewById(R.id.play_image);
        mLoading = view.findViewById(R.id.loading);
        mHeart = view.findViewById(R.id.heart);

        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

//                //TODO 循环播放 有问题
//                mVideoView.seekTo(0);
//                mVideoView.start();
                mediaPlayer.setLooping(true);
                mediaPlayer.start();

            }
        });

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mLoading.setVisibility(View.GONE);
            }
        });

        mVideoView.setOnTouchListener(new MyClickListener(new MyClickListener.ClickCallBack() {
            @Override
            public void oneClick() {
                if (mVideoView.isPlaying()) {
                    mVideoView.pause();
                    mPlayImage.setVisibility(View.VISIBLE);
                } else {
                    mVideoView.start();
                    mPlayImage.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void doubleClick() {
                mHeart.playAnimation();
            }
        }));

        mVideoView.setVideoPath(mFeed.getVideoUrl().replaceFirst("https", "http"));
        mVideoView.start();

        mPlayImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mVideoView.isPlaying()) {
                    mVideoView.pause();
                    mPlayImage.setVisibility(View.VISIBLE);
                } else {
                    mVideoView.start();
                    mPlayImage.setVisibility(View.INVISIBLE);
                }
            }
        });

        return view;
    }

    static class MyClickListener implements View.OnTouchListener {

        private int timeout = 400;
        private int clickCount = 0;
        private Handler handler;
        private ClickCallBack clickCallBack;

        public interface ClickCallBack {
            void oneClick();
            void doubleClick();
        }

        public MyClickListener(ClickCallBack clickCallBack) {
            this.clickCallBack = clickCallBack;
            handler = new Handler();
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                clickCount++;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (clickCount == 1) {
                            clickCallBack.oneClick();
                        } else {
                            clickCallBack.doubleClick();
                        }
                        handler.removeCallbacksAndMessages(null);
                        clickCount = 0;
                    }
                }, timeout);
            }
            return false;
        }
    }
}