package com.example.onepiece.mainPage;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onepiece.R;
import com.example.onepiece.db.MyDataBaseHelper;
import com.example.onepiece.model.Playlist;
import com.example.onepiece.model.Song;
import com.example.onepiece.model.SongList;
import com.example.onepiece.model.SongLists;
import com.example.onepiece.player.PlayerActivity;
import com.example.onepiece.util.DebugMessage;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import java.util.List;
import java.util.Locale;

/**
 * Created by Administrator on 2018/5/28 0028.
 */

public class PlaylistFragment extends Fragment {
    private static final String TAG = DebugMessage.TAG;
    private SongAdapter mAdapter;
    private TextView mPlaylist_title;
    private int mSong_selected;
    private PowerMenu mActivePowerMenu;
    List<Song> songs;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.playlist, container, false);
        RecyclerView mRecyclerView = view.findViewById(R.id.playlist_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        Intent intent = getActivity().getIntent();
        String title = intent.getStringExtra("title");
        mPlaylist_title = view.findViewById(R.id.toolbar_playlist_title);
        mPlaylist_title.setText(title);
        
        Playlist playlist = Playlist.get(getActivity(), title);
        songs = playlist.getSongs();
        mAdapter = new SongAdapter(songs);
        mRecyclerView.setAdapter(mAdapter);


        Toolbar toolbar = view.findViewById(R.id.playlist_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        return view;
    }

    public class SongHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView mTitle;
        public TextView mArtist;
        public TextView mSongNum;
        public ImageView mPlaylist_item_more;
        public PowerMenu mPowerMenu;

        public SongHolder(View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.song_title);
            mArtist = itemView.findViewById(R.id.song_artist);
            mSongNum = itemView.findViewById(R.id.song_num);
            mPlaylist_item_more = itemView.findViewById(R.id.playlist_item_more);
            mPlaylist_item_more.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mSong_selected = (int)view.getTag();
            mPowerMenu.showAtCenter(view);
            mActivePowerMenu = mPowerMenu;
        }
    }

    public class SongAdapter extends RecyclerView.Adapter<SongHolder> {
        private List<Song> mSongs;
        public SongAdapter(List<Song> songs) {
            mSongs = songs;
        }

        @Override
        public SongHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.playlist_recycler_list_item, parent, false);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int index = (int)view.findViewById(R.id.playlist_item_more).getTag();
                    String title = mPlaylist_title.getText().toString();
                    Intent intent = new Intent(getActivity(), PlayerActivity.class);
                    intent.putExtra("playlistTitle", title);
                    intent.putExtra("index", index);
                    startActivity(intent);
                }
            });

            return new SongHolder(view);
        }

        @Override
        public void onBindViewHolder(SongHolder holder, int position) {
            Song song = mSongs.get(position);
            holder.mTitle.setText(song.getTitle());
            holder.mArtist.setText(song.getArtist());
            holder.mSongNum.setText(String.format(Locale.getDefault(),"%d",position + 1));
            holder.mPlaylist_item_more.setTag(position);
            if (holder.mPowerMenu != null) {
                holder.mPowerMenu.dismiss();
            }
            holder.mPowerMenu = getPopupMenu(getContext());
            holder.mPowerMenu.addItem(new PowerMenuItem("专辑:" + song.getAlbum(), false));
        }

        @Override
        public int getItemCount() {
            return mSongs.size();
        }
    }


    // more按钮的弹出菜单
    private PowerMenu getPopupMenu(Context context) {
        return new PowerMenu.Builder(context)
                .addItem(new PowerMenuItem("删除", false))
                .addItem(new PowerMenuItem("添加到歌单", false))
                .setWidth(900)
                .setAnimation(MenuAnimation.SHOWUP_BOTTOM_RIGHT)
                .setMenuRadius(10f)
                .setMenuShadow(10f)
                .setSelectedMenuColor(Color.WHITE)
                .setSelectedTextColor(Color.WHITE)
                .setOnMenuItemClickListener(new OnMenuItemClickListener<PowerMenuItem>() {
                    @Override
                    public void onItemClick(int position, PowerMenuItem item) {
                        switch (item.getTitle()) {
                            case "删除":
                                String songList_title = mPlaylist_title.getText().toString();
                                MyDataBaseHelper myDataBaseHelper = MyDataBaseHelper.get(getContext(), "OnePiece", 1);
                                SongList songList = SongLists.get(getContext()).getSongListByTitle(songList_title);
                                myDataBaseHelper.deleteSong(myDataBaseHelper.getWritableDatabase(),
                                        songList.getListID(),
                                        String.valueOf(songs.get(mSong_selected).getId()));
                                mActivePowerMenu.dismiss();
                                songs.remove(mSong_selected);
                                songList.setNumberOfSongs(songs.size());
                                ContentValues contentValues = new ContentValues();
                                contentValues.put("numberOfSongs", songList.getNumberOfSongs());
                                myDataBaseHelper.updateSongList(myDataBaseHelper.getWritableDatabase(), contentValues, songList_title);
                                mAdapter.notifyDataSetChanged();
                                myDataBaseHelper.close();
                                break;
                            case "添加到歌单":
                                mActivePowerMenu.dismiss();
                                mActivePowerMenu = getPopupMenu2(getContext());
                                mActivePowerMenu.showAtCenter(mPlaylist_title);
                                break;
                        }
                    }
                })
                .build();
    }

    // “添加到歌单”的弹出菜单
    private PowerMenu getPopupMenu2(Context context) {
        PowerMenu powerMenu = new PowerMenu.Builder(context)
                .setWidth(900)
                .setAnimation(MenuAnimation.SHOWUP_BOTTOM_RIGHT)
                .setMenuRadius(10f)
                .setMenuShadow(10f)
                .setSelectedMenuColor(Color.WHITE)
                .setSelectedTextColor(Color.WHITE)
                .setHeaderView(R.layout.customer_powermenu_header)
                .setOnMenuItemClickListener(new OnMenuItemClickListener<PowerMenuItem>() {
                    @Override
                    public void onItemClick(int position, PowerMenuItem item) {
                        MyDataBaseHelper myDataBaseHelper = MyDataBaseHelper.get(getContext(), "OnePiece", 1);
                        boolean res = myDataBaseHelper.insertSong(myDataBaseHelper.getWritableDatabase(),
                                SongLists.get(getContext()).getSongListIdByTitle(item.getTitle()),
                                String.valueOf(songs.get(mSong_selected).getId()));
                        mActivePowerMenu.dismiss();
                        if (res) {
                            SongList songList = SongLists.get(getContext()).getSongListByTitle(item.getTitle());
                            int numberOfSongs = songList.getNumberOfSongs() + 1;
                            songList.setNumberOfSongs(numberOfSongs);
                            ContentValues contentValues = new ContentValues();
                            contentValues.put("numberOfSongs", numberOfSongs);
                            myDataBaseHelper.updateSongList(myDataBaseHelper.getWritableDatabase(), contentValues, item.getTitle());
                            myDataBaseHelper.close();
                            Toast.makeText(getContext(), "已添加", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), songs.get(mSong_selected).getTitle() + "已在歌单中!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .build();

        List<SongList> songLists = SongLists.get(getContext()).getSongLists();
        for (int i = 0; i < songLists.size(); i++) {
            powerMenu.addItem(new PowerMenuItem(songLists.get(i).getTitle(), false));
        }

        return powerMenu;
    }

}
