package com.example.onepiece.model;

/**
 * Created by Administrator on 2018/5/27 0027.
 */

public class Song {
    long mId;
    String mTitle;
    String mArtist;
    long mDuration;
    String mUrl;
    String album;

    public Song (long id, String title, String artist, long duration, String url, String album) {
        this.mId = id;
        this.mTitle = title;
        this.mArtist = artist;
        this.mDuration = duration;
        this.mUrl = url;
        this.album = album;
    }

    public long getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getArtist() {
        return mArtist;
    }

    public long getDuration() {
        return mDuration;
    }

    public String getAlbum() {
        return album;
    }

    public String getUrl() {
        return mUrl;
    }
}
