package com.example.minidouyin.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.minidouyin.R;
import com.example.minidouyin.bean.Feed;
import com.example.minidouyin.bean.FeedLab;
import com.example.minidouyin.player.VideoPlayerIJK;
import com.example.minidouyin.player.VideoPlayerListener;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * @author: jq_lu
 * @Date: 2019/7/20
 * @Time: 13:15
 */
public class FeedFragment extends Fragment {

    public static final String EXTRA_FEED_POSITION =
            "com.example.minidouyin.feed_position";
    private static final String TAG = "FeedFragment";

    private VideoPlayerIJK ijkPlayer;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        mPlayImage = view.findViewById(R.id.play_image);
        ijkPlayer = view.findViewById(R.id.ijkplayer);

        //加载native库
        try {
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        } catch (Exception e) {
            e.printStackTrace();
        }

        ijkPlayer.setListener(new VideoPlayerListener());
        ijkPlayer.setVideoPath(mFeed.getVideoUrl().replaceFirst("https", "http"));

        ijkPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ijkPlayer.isPlaying()) {
                    ijkPlayer.pause();
                    mPlayImage.setVisibility(View.VISIBLE);
                } else {
                    ijkPlayer.start();
                    mPlayImage.setVisibility(View.INVISIBLE);
                }
            }
        });
        return view;
    }
}
