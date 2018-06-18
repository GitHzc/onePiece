package com.example.onepiece.model;

import android.content.ContentValues;

import java.util.UUID;

/**
 * Created by Administrator on 2018/6/2 0002.
 */

public class SongList {
    String mListID;
    String mTitle;
    int mNumberOfSongs;
    String mCreateTime;
    String mUri;

    public SongList(UUID id, String title, int numberOfSongs, String createTime, String uri) {
        mListID = generateID(id);
        mTitle = title;
        mNumberOfSongs = numberOfSongs;
        mCreateTime = createTime;
        mUri = uri;
    }

    public SongList(String id, String title, int numberOfSongs, String createTime, String uri) {
        mListID = id;
        mTitle = title;
        mNumberOfSongs = numberOfSongs;
        mCreateTime = createTime;
        mUri = uri;
    }

    static public String generateID(UUID uuid) {
        return "list" + uuid.toString().replace('-', '_');
    }

    static public UUID idToUUID(String id) {
        return UUID.fromString(id.replace("list", "").replace('_', '-'));
    }

    public String getUri() {
        return mUri;
    }

    public void setUri(String uri) {
        mUri = uri;
    }

    public String getListID() {
        return mListID;
    }

    public void setListID(UUID listID) {
        mListID = generateID(listID);
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String titme) {
        mTitle = titme;
    }

    public int getNumberOfSongs() {
        return mNumberOfSongs;
    }

    public void setNumberOfSongs(int numberOfSongs) {
        mNumberOfSongs = numberOfSongs;
    }

    public String getCreateTime() {
        return mCreateTime;
    }

    public void setCreateTime(String createTime) {
        mCreateTime = createTime;
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put("id", mListID);
        values.put("title", mTitle);
        values.put("numberOfSongs", mNumberOfSongs);
        values.put("createTime", mCreateTime);
        values.put("PictureUri", mUri);

        return values;
    }
}
