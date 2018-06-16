package com.example.onepiece.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2018/6/15 0015.
 */

public class DiscoveryBean {

    /**
     * id : 228
     * text : 最爱的歌没有之一
     * create_datetime : 2018-06-06T11:26:35.388986Z
     * author : {"id":528,"username":"OPU000127","password":"123456"}
     * song : {"id":173,"title":"裙下之臣(live)","artist":"陈奕迅","album":"duo 陈奕迅2010演唱会","audio":173,"lyric":173}
     */

    @SerializedName("id")
    private int id;
    @SerializedName("text")
    private String text;
    @SerializedName("create_datetime")
    private String createDatetime;
    @SerializedName("author")
    private AuthorBean author;
    @SerializedName("song")
    private SongBean song;

    public static DiscoveryBean objectFromData(String str) {

        return new Gson().fromJson(str, DiscoveryBean.class);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCreateDatetime() {
        return createDatetime;
    }

    public void setCreateDatetime(String createDatetime) {
        this.createDatetime = createDatetime;
    }

    public AuthorBean getAuthor() {
        return author;
    }

    public void setAuthor(AuthorBean author) {
        this.author = author;
    }

    public SongBean getSong() {
        return song;
    }

    public void setSong(SongBean song) {
        this.song = song;
    }

    public static class AuthorBean {
        /**
         * id : 528
         * username : OPU000127
         * password : 123456
         */

        @SerializedName("id")
        private int id;
        @SerializedName("username")
        private String username;
        @SerializedName("password")
        private String password;

        public static AuthorBean objectFromData(String str) {

            return new Gson().fromJson(str, AuthorBean.class);
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class SongBean {
        /**
         * id : 173
         * title : 裙下之臣(live)
         * artist : 陈奕迅
         * album : duo 陈奕迅2010演唱会
         * audio : 173
         * lyric : 173
         */

        @SerializedName("id")
        private int id;
        @SerializedName("title")
        private String title;
        @SerializedName("artist")
        private String artist;
        @SerializedName("album")
        private String album;
        @SerializedName("audio")
        private int audio;
        @SerializedName("lyric")
        private int lyric;

        public static SongBean objectFromData(String str) {

            return new Gson().fromJson(str, SongBean.class);
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
}
