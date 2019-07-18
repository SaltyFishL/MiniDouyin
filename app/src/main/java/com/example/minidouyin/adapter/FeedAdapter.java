package com.example.minidouyin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.minidouyin.R;
import com.example.minidouyin.bean.Feed;

import java.util.List;

/**
 * @author: jq_lu
 * @Date: 2019/7/19
 * @Time: 0:34
 */
public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.VH> {

    private Context context;
    private List<Feed> feeds;

    public FeedAdapter(Context context, List<Feed> feeds) {
        this.context = context;
        this.feeds = feeds;
    }

    @NonNull
    @Override
    public FeedAdapter.VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //设置子布局
        View view = LayoutInflater.from(context).inflate(R.layout.item_feed, parent, false);
        //关联viewHolder
        VH vh = new VH(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull FeedAdapter.VH holder, int position) {
        Glide.with(context)
                .load(feeds.get(position).getImageUrl())
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .placeholder(R.mipmap.tiktok)
                .override(250, 350)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return feeds.size();
    }

    public class VH extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public VH(@NonNull View itemView) {
            super(itemView);
            //设置复用控件
            imageView = itemView.findViewById(R.id.feed_item_image);

        }
    }
}
