package com.example.minidouyin.db;

import android.provider.BaseColumns;

/**
 * @author: jq_lu
 * @Date: 2019/7/21
 * @Time: 16:22
 */
public final class TikTokContract {

    public static final String SQL_CREATE_LIKE_TABLE =
            "CREATE TABLE " + FeedLike.TABLE_NAME
                    + "(" + FeedLike.COLUMN_ID + " TEXT PRIMARY KEY, "
                    + FeedLike.COLUMN_STUDENT_ID + " TEXT, "
                    + FeedLike.COLUMN_USER_NAME + " TEXT, "
                    + FeedLike.COLUMN_IMG_URL + " TEXT, "
                    + FeedLike.COLUMN_VIDEO_URL + " TEXT)";

    public static class FeedLike implements BaseColumns {
        public static final String TABLE_NAME = "feed_like";

        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_STUDENT_ID = "student_id";
        public static final String COLUMN_USER_NAME = "user_name";
        public static final String COLUMN_IMG_URL = "img_url";
        public static final String COLUMN_VIDEO_URL = "video_url";
    }

    private TikTokContract() {
    }
}