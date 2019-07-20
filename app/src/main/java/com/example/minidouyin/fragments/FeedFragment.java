package com.example.minidouyin.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.minidouyin.R;
import com.example.minidouyin.bean.Feed;
import com.example.minidouyin.bean.FeedLab;

/**
 * @author: jq_lu
 * @Date: 2019/7/20
 * @Time: 13:15
 */
public class FeedFragment extends Fragment {

    public static final String EXTRA_FEED_POSITION =
            "com.example.minidouyin.feed_position";
    private static final String TAG = "FeedFragment";

    private Feed mFeed;
    private TextView mTextView;

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
        mFeed = FeedLab.get(getActivity()).getFeeds().get(position);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);


        //TODO textView试用
        mTextView = view.findViewById(R.id.test_text_view);
        mTextView.setText(mFeed.getUserName());
        return view;
    }
}
