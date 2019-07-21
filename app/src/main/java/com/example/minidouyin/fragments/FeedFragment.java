package com.example.minidouyin.fragments;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.example.minidouyin.R;
import com.example.minidouyin.activities.FeedPagerActivity;
import com.example.minidouyin.bean.Feed;
import com.example.minidouyin.bean.FeedLab;
import com.example.minidouyin.db.TikTokContract;
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
        mHeart.setRepeatCount(0);

        mVideoView.setVideoPath(mFeed.getVideoUrl().replaceFirst("https", "http"));

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mLoading.setVisibility(View.GONE);
                mPlayImage.setVisibility(View.VISIBLE);
                //TODO 判断数据库中有没有like
                if (isExistLike(mFeed)) {
                    mHeart.setVisibility(View.VISIBLE);
                    mHeart.setProgress(1f);
                }
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
                //TODO 处理doubleClick事件 添加onScroll事件
                if (mHeart.getVisibility() == View.INVISIBLE) {
                    mHeart.setVisibility(View.VISIBLE);
                    mHeart.playAnimation();
                    //TODO 添加进入数据库
                    saveLike2Database(mFeed);
                } else {
                    mHeart.setVisibility(View.INVISIBLE);
                    //TODO 删除数据库中的like
                    deleteLike(mFeed);
                }

//                这是修改之前的代码
//                mHeart.setVisibility(View.INVISIBLE);
//                mHeart.playAnimation();

            }

            @Override
            public void onScroll() {
                mVideoView.pause();
                mPlayImage.setVisibility(View.VISIBLE);
            }
        }));

        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.setLooping(false);
                mPlayImage.setVisibility(View.VISIBLE);
            }
        });


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

    private boolean saveLike2Database(Feed feed) {
        if (FeedPagerActivity.sDatabase == null || feed == null) {
            return false;
        }
        ContentValues values = new ContentValues();
        values.put(TikTokContract.FeedLike.COLUMN_ID, feed.getId());
        values.put(TikTokContract.FeedLike.COLUMN_IMG_URL, feed.getImageUrl());
        values.put(TikTokContract.FeedLike.COLUMN_STUDENT_ID, feed.getStudentId());
        values.put(TikTokContract.FeedLike.COLUMN_USER_NAME, feed.getUserName());
        values.put(TikTokContract.FeedLike.COLUMN_VIDEO_URL, feed.getVideoUrl());

        long rowId = FeedPagerActivity.sDatabase.insert(TikTokContract.FeedLike.TABLE_NAME,
                null, values);
        return rowId != -1;
    }

    private void deleteLike(Feed feed) {
        if (FeedPagerActivity.sDatabase == null) {
            return;
        }

        FeedPagerActivity.sDatabase.delete(
                TikTokContract.FeedLike.TABLE_NAME,
                TikTokContract.FeedLike.COLUMN_ID + "=?",
                new String[]{feed.getId()}
        );

    }

    private boolean isExistLike(Feed feed) {
        if (FeedPagerActivity.sDatabase == null) {
            return false;
        }

        boolean isExist = false;

        Cursor cursor = null;

        try {
            cursor = FeedPagerActivity.sDatabase.query(
                    TikTokContract.FeedLike.TABLE_NAME,
                    null,
                    TikTokContract.FeedLike.COLUMN_ID + "=?",
                    new String[]{feed.getId()},
                    null,
                    null,
                    null
            );
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && cursor.getCount() > 0) {
                isExist = true;
            }

            if (cursor != null) {
                cursor.close();
            }
        }
        return isExist;
    }

    static class MyClickListener implements View.OnTouchListener {

        private int timeout = 300;
        private int clickCount = 0;
        private Handler handler;
        private ClickCallBack clickCallBack;
        private float initX = 0;

        public interface ClickCallBack {
            void oneClick();
            void doubleClick();

            void onScroll();
        }

        public MyClickListener(ClickCallBack clickCallBack) {
            this.clickCallBack = clickCallBack;
            handler = new Handler();
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                clickCount++;
                if (clickCount == 1) {
                    initX = motionEvent.getRawX();
                }

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (clickCount == 1) {
                            //TODO 冲突解决了
                            if (Math.abs(motionEvent.getRawX() - initX) > 50) {
                                clickCallBack.onScroll();
                            } else {
                                clickCallBack.oneClick();
                            }
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