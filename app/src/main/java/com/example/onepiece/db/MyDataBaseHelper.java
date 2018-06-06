package com.example.onepiece.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;
import java.util.Locale;

/**
 * Created by Administrator on 2018/5/18 0018.
 */

public class MyDataBaseHelper extends SQLiteOpenHelper {
    final String CREATE_SONGLISTS_SQL = "create table SongLists(" +
            "id primary key," +
            "title," +
            "numberOfSongs," +
            "createTime," +
            "pictureUri);";

    private static MyDataBaseHelper sMyDataBaseHelper; //单例

    public static MyDataBaseHelper get(Context context, String name, int version) {
        if (sMyDataBaseHelper == null) {
            sMyDataBaseHelper = new MyDataBaseHelper(context, name, version);
        }
        return sMyDataBaseHelper;
    }

    public MyDataBaseHelper(Context context, String name, int version) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_SONGLISTS_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    //创建歌单表
    public void createSongList(SQLiteDatabase db, String id) {
        db.execSQL("create table " + id + "(id primary key);");
    }

    //删除歌单
    public void dropSongList(SQLiteDatabase db, String id) {
        db.execSQL("drop table " + id);
    }

    //添加歌单
    public void insertSongList(SQLiteDatabase db, ContentValues contentValues) {
        db.insert("SongLists", null, contentValues);
    }

    public void deleteSongList(SQLiteDatabase db, String title) {
        db.delete("SongLists", "title = ?", new String[]{title});
    }

    //返回歌单列表全部歌单
    public Cursor querySongList(SQLiteDatabase db) {
        return db.rawQuery("select * from SongLists;", null);
    }

    public void updateSongList(SQLiteDatabase db, ContentValues contentValues, String title) {
        db.update("SongLists", contentValues, "title = ?", new String[]{title});
    }

    //返回歌单全部歌曲
    public Cursor querySong(SQLiteDatabase db, String songList) {
        return db.rawQuery(String.format(Locale.getDefault(), "select * from %s;", songList), null);
    }

    //删除歌单中歌曲
    public void deleteSong(SQLiteDatabase db, String songList, String id) {
        db.delete(songList, "id = ?", new String[]{id});
    }

    //歌单添加歌曲
    public boolean insertSong(SQLiteDatabase db, String songList, String id) {
        try {
            db.execSQL("insert into " + songList + "(id) values(?);", new String[]{id});
            return true;
        } catch (SQLiteConstraintException e) {
            return false;
        }
    }
}
