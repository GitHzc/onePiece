package com.example.onepiece.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2018/6/16 0016.
 */

public class OneBean {

    /**
     * id : 3
     * image : /media/one/dr_20180608.jpg
     * create_date : 2018-06-16
     * song : {"id":109,"title":"k歌之王","artist":"陈奕迅","album":"1997-2007 跨世纪国语精选","audio":109,"lyric":109}
     */

    @SerializedName("id")
    private int id;
    @SerializedName("image")
    private String image;
    @SerializedName("create_date")
    private String createDate;
    @SerializedName("song")
    private SongBean song;

    public static OneBean objectFromData(String str) {

        return new Gson().fromJson(str, OneBean.class);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public SongBean getSong() {
        return song;
    }

    public void setSong(SongBean song) {
        this.song = song;
    }

    public static class SongBean {
        /**
         * id : 109
         * title : k歌之王
         * artist : 陈奕迅
         * album : 1997-2007 跨世纪国语精选
         * audio : 109
         * lyric : 109
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
