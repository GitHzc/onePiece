package com.example.onepiece.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2018/6/20 0020.
 */

public class SongListOfUserBean {

    /**
     * playlist_name : list
     * items : [{"song":{"id":109,"title":"k歌之王","artist":"陈奕迅","album":"1997-2007 跨世纪国语精选","audio":109,"lyric":109}},{"song":{"id":116,"title":"你的背包","artist":"陈奕迅","album":"1997-2007 跨世纪国语精选","audio":116,"lyric":116}}]
     */

    @SerializedName("playlist_name")
    private String playlistName;
    @SerializedName("items")
    private List<ItemsBean> items;

    public static SongListOfUserBean objectFromData(String str) {

        return new Gson().fromJson(str, SongListOfUserBean.class);
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public List<ItemsBean> getItems() {
        return items;
    }

    public void setItems(List<ItemsBean> items) {
        this.items = items;
    }

    public static class ItemsBean implements Serializable{
        /**
         * song : {"id":109,"title":"k歌之王","artist":"陈奕迅","album":"1997-2007 跨世纪国语精选","audio":109,"lyric":109}
         */

        @SerializedName("song")
        private SongBean song;

        public static ItemsBean objectFromData(String str) {

            return new Gson().fromJson(str, ItemsBean.class);
        }

        public SongBean getSong() {
            return song;
        }

        public void setSong(SongBean song) {
            this.song = song;
        }

        public static class SongBean implements Parcelable {
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

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeInt(this.id);
                dest.writeString(this.title);
                dest.writeString(this.artist);
                dest.writeString(this.album);
                dest.writeInt(this.audio);
                dest.writeInt(this.lyric);
            }

            public SongBean() {
            }

            protected SongBean(Parcel in) {
                this.id = in.readInt();
                this.title = in.readString();
                this.artist = in.readString();
                this.album = in.readString();
                this.audio = in.readInt();
                this.lyric = in.readInt();
            }

            public static final Parcelable.Creator<SongBean> CREATOR = new Parcelable.Creator<SongBean>() {
                @Override
                public SongBean createFromParcel(Parcel source) {
                    return new SongBean(source);
                }

                @Override
                public SongBean[] newArray(int size) {
                    return new SongBean[size];
                }
            };
        }
    }
}
