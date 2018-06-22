package com.example.onepiece.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2018/6/20 0020.
 */

public class SongBean {
    private String username;
    @SerializedName("pl_name")
    private String songList;
    @SerializedName("song_id")
    private int songId;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSongList() {
        return songList;
    }

    public void setSongList(String songList) {
        this.songList = songList;
    }

    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }
}
