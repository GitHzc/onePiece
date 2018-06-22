package com.example.onepiece.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


/**
 * Created by Administrator on 2018/6/15 0015.
 */

public class DiscoveryBean implements Serializable {

    /**
     * id : 389
     * text : 好听！
     * create_datetime : 2018-06-06T11:26:51.714897Z
     * author : {"id":557,"username":"OPU000156","password":"123456","profile":"/media/media/profile/default.jpg"}
     * song : {"id":212,"title":"蒲公英的约定","artist":"周杰伦","album":"我很忙","audio":212,"lyric":212}
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

    public static class AuthorBean implements Serializable{
        /**
         * id : 557
         * username : OPU000156
         * password : 123456
         * profile : /media/media/profile/default.jpg
         */

        @SerializedName("id")
        private int id;
        @SerializedName("username")
        private String username;
        @SerializedName("password")
        private String password;
        @SerializedName("profile")
        private String profile;

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

        public String getProfile() {
            return profile;
        }

        public void setProfile(String profile) {
            this.profile = profile;
        }
    }

    public static class SongBean implements Serializable{
        /**
         * id : 212
         * title : 蒲公英的约定
         * artist : 周杰伦
         * album : 我很忙
         * audio : 212
         * lyric : 212
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

