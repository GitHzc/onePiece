package com.example.onepiece.model;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.example.onepiece.db.MyDataBaseHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Administrator on 2018/6/2 0002.
 */

public class SongLists {
    private List<SongList> mSongLists;
    private static SongLists sSongLists;  //单例

    public static SongLists get(Context context) {
        if (sSongLists == null) {
            sSongLists = new SongLists(context);
        }
        return sSongLists;
    }

    private SongLists(Context context) {
        mSongLists = new ArrayList<>();
        MyDataBaseHelper myDataBaseHelper = MyDataBaseHelper.get(context, "OnePiece", 1);
        Cursor cursor = myDataBaseHelper.querySongList(myDataBaseHelper.getReadableDatabase());
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToNext();
            String id = cursor.getString(cursor.getColumnIndex("id"));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            int numberOfSongs = cursor.getInt(cursor.getColumnIndex("numberOfSongs"));
            String createTime = cursor.getString(cursor.getColumnIndex("createTime"));
            String uri = cursor.getString(cursor.getColumnIndex("pictureUri"));
            mSongLists.add(new SongList(id, title, numberOfSongs, createTime, uri));
        }
        cursor.close();
    }

    public List<SongList> getSongLists() {
        return mSongLists;
    }

    public String getSongListIdByTitle(String title) {
        for (SongList s  : mSongLists) {
            if (title.equals(s.getTitle())) {
                return s.getListID();
            }
        }
        return null;
    }

    public SongList getSongListByTitle(String title) {
        for (SongList s : mSongLists) {
           if (title.equals(s.getTitle())) {
               return s;
           }
        }
        return null;
    }
}
