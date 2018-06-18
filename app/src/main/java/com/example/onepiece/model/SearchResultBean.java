package com.example.onepiece.model;

/**
 * Created by Administrator on 2018/6/14 0014.
 */

public class SearchResultBean {

    /**
     * id : 109
     * title : k歌之王
     * artist : 陈奕迅
     * album : 1997-2007 跨世纪国语精选
     * audio : 109
     * lyric : 109
     */

    private int id;
    private String title;
    private String artist;
    private String album;
    private int audio;
    private int lyric;

    public static SearchResultBean objectFromData(String str) {

        return new com.google.gson.Gson().fromJson(str, SearchResultBean.class);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
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
