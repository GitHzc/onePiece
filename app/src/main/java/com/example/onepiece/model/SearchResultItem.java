package com.example.onepiece.model;

/**
 * Created by Administrator on 2018/6/12 0012.
 */

public class SearchResultItem {
    private String title;
    private String url;
    private int token;
    private int audio;
    private int lyric;

    public SearchResultItem(String title, String url, int token) {
        this.title = title;
        this.token = token;
        this.url = url;

    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getToken() {
        return token;
    }

    public void setToken(int token) {
        this.token = token;
    }

    public int getAudio() {
        return audio;
    }

    public void setAudio(int audio) {
        this.audio = audio;
    }

    public int getLyric() {
        return lyric;
    }

    public void setLyric(int lyric) {
        this.lyric = lyric;
    }
}
