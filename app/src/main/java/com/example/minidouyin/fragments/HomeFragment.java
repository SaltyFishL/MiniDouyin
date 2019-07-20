package com.example.minidouyin.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.minidouyin.R;
import com.example.minidouyin.activities.FeedPagerActivity;
import com.example.minidouyin.adapter.FeedAdapter;
import com.example.minidouyin.bean.Feed;
import com.example.minidouyin.bean.FeedLab;
import com.example.minidouyin.bean.FeedResponse;
import com.example.minidouyin.network.IMiniDouyinService;
import com.example.minidouyin.network.RetrofitManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * @author: jq_lu
 * @Date: 2019/7/18
 * @Time: 19:34
 */
public class HomeFragment extends Fragment implements FeedAdapter.OnItemClicked {

    private static final String TAG = "HomeFragment";
    private static final int SPAN_COUNT = 2;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mRefresh;
    private List<Feed> mFeeds = new ArrayList<>();
    private FeedAdapter mFeedAdapter;
    private UploadDialogFragment mUploadDialogFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        mUploadDialogFragment = new UploadDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated: ");
        super.onActivityCreated(savedInstanceState);

        mRefresh = getActivity().findViewById(R.id.refresh);
        mRefresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);
        mRefresh.setProgressBackgroundColorSchemeResource(R.color.design_default_color_secondary);
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchFeeds();
            }
        });

        FloatingActionButton floatingActionButton = getActivity().findViewById(R.id.floating_action_bar);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO 添加视频
                FragmentManager fm = getFragmentManager();
                if (fm != null) {
                    mUploadDialogFragment.show(fm, "UploadDialog");
                }
            }
        });

        mRecyclerView = getActivity().findViewById(R.id.recycler_view);
        //设置网格布局管理器 TODO 换成瀑布?
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), SPAN_COUNT);
        //设置排列方式 纵向排列
        gridLayoutManager.setOrientation(RecyclerView.VERTICAL);
        //引用网格布局
        mRecyclerView.setLayoutManager(gridLayoutManager);
        fetchFeeds();

    }

    private void fetchFeeds() {
        mRefresh.setRefreshing(true);
        Retrofit retrofit = RetrofitManager.get(IMiniDouyinService.HOST);
        Call<FeedResponse> feedResponseCall = retrofit.create(IMiniDouyinService.class).getFeeds();

        feedResponseCall.enqueue(new Callback<FeedResponse>() {
            @Override
            public void onResponse(Call<FeedResponse> call, Response<FeedResponse> response) {
                mFeeds = response.body().getFeeds();
                FeedLab.get(getActivity()).setFeeds(mFeeds);

                mFeedAdapter = new FeedAdapter(getActivity(), mFeeds);
                mRecyclerView.setAdapter(mFeedAdapter);
                mFeedAdapter.setOnItemClicked(HomeFragment.this);

                mFeedAdapter.notifyDataSetChanged();
                mRefresh.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<FeedResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "拉取失败", Toast.LENGTH_SHORT).show();
                mRefresh.setRefreshing(false);
            }
        });
    }


    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getActivity(), FeedPagerActivity.class);
        intent.putExtra(FeedFragment.EXTRA_FEED_POSITION, position);
        startActivity(intent);
    }
}
