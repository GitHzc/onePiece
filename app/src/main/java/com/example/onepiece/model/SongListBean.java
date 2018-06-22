package com.example.onepiece.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2018/6/20 0020.
 */

public class SongListBean {
    private String username;
    @SerializedName("playlistname")
    private String songListName;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSongListName() {
        return songListName;
    }

    public void setSongListName(String songListName) {
        this.songListName = songListName;
    }
}
