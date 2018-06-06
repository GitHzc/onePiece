package com.example.onepiece.model;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import com.example.onepiece.db.MyDataBaseHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.example.onepiece.util.DebugMessage.TAG;

/**
 * Created by Administrator on 2018/5/27 0027.
 */

public class Playlist {
    private static String mTitle = new String("本地音乐");
    private static Playlist sPlaylist;
    private List<Song> mSongs;

    public static Playlist get(Context context, String title) {
        if (!mTitle.equals(title) || sPlaylist == null) {
            sPlaylist = new Playlist(context, title);
        }
        return sPlaylist;
    }

    private Playlist(Context context, String title) {
        mTitle = title;
        mSongs = new ArrayList<>();
        MyDataBaseHelper myDataBaseHelper = MyDataBaseHelper.get(context, "OnePiece", 1);
        String id = SongLists.get(context).getSongListIdByTitle(title);
        Cursor cursor = myDataBaseHelper.querySong(myDataBaseHelper.getWritableDatabase(), id);
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToNext();
            id = cursor.getString(cursor.getColumnIndex("id"));
            Cursor query = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    null,
                    MediaStore.Audio.Media._ID + " = ?",
                    new String[] {id},
                    null);

            for (int j = 0; j < query.getCount(); j++) {
                query.moveToNext();

                long song_id = query.getLong(query.getColumnIndex(MediaStore.Audio.Media._ID));
                String song_title = query.getString(query.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String artist = query.getString(query.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                long duration = query.getLong(query.getColumnIndex(MediaStore.Audio.Media.DURATION));
                String url = query.getString(query.getColumnIndex(MediaStore.Audio.Media.DATA));
                String album = query.getString(query.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                int isMusic = query.getInt(query.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));

                song_title = (song_title == null) ? "unknown" : song_title;
                artist = (artist == null) ? "unknown" : artist;
                album = (album == null) ? "unknown" : album;

                if (isMusic != 0) {
                    Song song = new Song(song_id, song_title, artist, duration, url, album);
                    mSongs.add(song);
                }
            }
            query.close();
        }
        cursor.close();
    }

    public List<Song> getSongs() {
        return mSongs;
    }

    public void addSong(Song song) {
        mSongs.add(song);
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Song getSong(long id) {
        for (Song song : mSongs) {
            if (song.getId() == id) {
                return song;
            }
        }
        return null;
    }

    public int getNumber() {
        return mSongs.size();
    }
}
