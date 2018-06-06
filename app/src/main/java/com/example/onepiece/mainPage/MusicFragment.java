package com.example.onepiece.mainPage;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.onepiece.R;
import com.example.onepiece.db.MyDataBaseHelper;
import com.example.onepiece.model.SongList;
import com.example.onepiece.model.SongLists;
import com.example.onepiece.util.DebugMessage;
import com.example.onepiece.util.Utility;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Administrator on 2018/5/19 0019.
 */

public class MusicFragment extends Fragment {
    private static final String TAG = DebugMessage.TAG;

    private EditText mSearchEditText;
    private List<SongList> mSongLists;
    private MyAdapter mMyAdapter;
    private PowerMenu mPowerMenu;
    private Integer mSongList_item_selected;
    private MyDataBaseHelper mMyDataBaseHelper;
    private View mMyView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: execute");
        mMyView = inflater.inflate(R.layout.music_fragment, container, false);
        return mMyView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated: execute");
        init_SongLists();
        init_add_SongList_button();

        mSearchEditText = mMyView.findViewById(R.id.search_edit_text);

        mSearchEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    Intent intent = new Intent(getActivity(), SearchActivity.class);
                    mSearchEditText.clearFocus();
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        init_SongLists();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mMyDataBaseHelper != null) {
            mMyDataBaseHelper.close();
        }
    }

    class MyAdapter extends BaseAdapter {
        private List<SongList> contents;
        private int resource;
        private LayoutInflater inflater;
        private Context context;

        @Override
        public int getCount() {
            return contents.size();
        }

        private MyAdapter(List<SongList> contents, int resource, Context context) {
            this.contents = contents;
            this.resource = resource;
            this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public Object getItem(int i) {
            return contents.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = inflater.inflate(resource, null);
            }

            SongList songList = contents.get(i);
            TextView title = view.findViewById(R.id.songlist_title);
            title.setText(songList.getTitle());
            ImageView picture = view.findViewById(R.id.songlist_picture);
            Glide.with(MusicFragment.this).load(Uri.parse(songList.getUri())).into(picture);
            TextView numberOfSongs = view.findViewById(R.id.number_of_songs);
            numberOfSongs.setText(String.format(Locale.getDefault(), "%d首", songList.getNumberOfSongs()));

            RelativeLayout relativeLayout = view.findViewById(R.id.songlist_relative_layout);
            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextView temp = view.findViewById(R.id.songlist_title);
                    Intent intent = new Intent(getActivity(), PlaylistActivity.class);
                    intent.putExtra("title", temp.getText());
                    startActivity(intent);
                }
            });

            final ImageView more_button = view.findViewById(R.id.songlist_more);
            more_button.setTag(i);
            more_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mSongList_item_selected = (Integer)more_button.getTag();
                    getPopupMenu(getActivity());
                    mPowerMenu.showAsDropDown(view);
                }
            });
            return view;
        }
    }

    private void init_SongLists() {
        mMyDataBaseHelper = MyDataBaseHelper.get(getActivity(), "OnePiece", 1);
        mSongLists = SongLists.get(getContext()).getSongLists();
        if (mSongLists.size() == 0) {
            UUID uuid = UUID.randomUUID();
            String id = SongList.generateID(uuid);
            String title = "本地音乐";
            int numberOfSongs = init_localMusic(id);
            String createTime = Utility.getTimeNow();
            String uri = "android.resource://" + getActivity().getPackageName() + "/" + R.mipmap.umeng_socialize_share_music;
            SongList localMusic = new SongList(id, title, numberOfSongs, createTime, uri);
            mSongLists.add(localMusic);
            //将本地音乐歌单加入到歌单数据库中
            mMyDataBaseHelper.insertSongList(mMyDataBaseHelper.getWritableDatabase(), localMusic.getContentValues());
        }

        mMyAdapter = new MyAdapter(mSongLists, R.layout.music_songlists_item, getActivity());
        ListView mListView = mMyView.findViewById(R.id.playlist_view);
        mListView.setAdapter(mMyAdapter);
    }

    private int init_localMusic(String localMusicID) {
        //创建本地音乐歌单对应的数据库表`
        mMyDataBaseHelper.createSongList(mMyDataBaseHelper.getWritableDatabase(), localMusicID);
        Cursor cursor = getActivity().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Media._ID},
                null,
                null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        int numberOfSongs = cursor.getCount();
        for (int i = 0; i < numberOfSongs; i++) {
            cursor.moveToNext();
            String songID = cursor.getString(0);
            mMyDataBaseHelper.insertSong(mMyDataBaseHelper.getWritableDatabase(), localMusicID, songID);
        }
        cursor.close();
        return numberOfSongs;
    }

    private void getPopupMenu(Context context) {
        mPowerMenu = new PowerMenu.Builder(context)
                .addItem(new PowerMenuItem("编辑", false))
                .addItem(new PowerMenuItem("删除", false))
                .setAnimation(MenuAnimation.SHOWUP_BOTTOM_RIGHT)
                .setMenuRadius(10f)
                .setMenuShadow(10f)
                .setSelectedMenuColor(Color.WHITE)
                .setSelectedTextColor(Color.WHITE)
                .setOnMenuItemClickListener(new MyOnMenuItemClickListener())
                .build();
    }

    private class MyOnMenuItemClickListener implements OnMenuItemClickListener<PowerMenuItem> {
        @Override
        public void onItemClick(int position, PowerMenuItem item) {
            switch (item.getTitle()) {
                case "删除":
                    SongList delete_item = mSongLists.remove(mSongList_item_selected.intValue());
                    mMyDataBaseHelper.deleteSongList(mMyDataBaseHelper.getWritableDatabase(), delete_item.getTitle());
                    mMyDataBaseHelper.dropSongList(mMyDataBaseHelper.getWritableDatabase(), delete_item.getListID());
                    mMyAdapter.notifyDataSetChanged();
                    break;
                case "编辑":
                    Intent intent = new Intent(getActivity(), SongListEditActivity.class);
                    startActivityForResult(intent, Utility.REQUEST_UPDATE_INFO);
                    break;
            }
            mPowerMenu.dismiss();
        }
    }

    private AlertDialog getAddNewPlaylistDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("新建歌单");
        final LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.simple_alert_dialog, null);
        builder.setView(linearLayout);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                EditText new_title = linearLayout.findViewById(R.id.playlist_title_edit);
                UUID uuid = UUID.randomUUID();
                String id = SongList.generateID(uuid);
                String title = new_title.getText().toString();
                String uri = "android.resource://" + getActivity().getPackageName() + "/" + R.mipmap.umeng_socialize_share_music;
                int numberOfSongs = 0;
                String createTime = Utility.getTimeNow();
                SongList new_item = new SongList(id, title, numberOfSongs, createTime, uri);
                mSongLists.add(new_item);
                mMyAdapter.notifyDataSetChanged();
                //在歌单数据库中加入新歌单
                mMyDataBaseHelper.insertSongList(mMyDataBaseHelper.getWritableDatabase(), new_item.getContentValues());
                //创建歌单对应的数据库表
                mMyDataBaseHelper.createSongList(mMyDataBaseHelper.getWritableDatabase(), id);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getActivity(), "已取消", Toast.LENGTH_SHORT).show();
            }
        });
        return builder.create();
    }

    private void init_add_SongList_button() {
        ImageView madd_playlist_button = mMyView.findViewById(R.id.add_playlist);
        madd_playlist_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAddNewPlaylistDialog(getActivity()).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Utility.REQUEST_UPDATE_INFO:
                if (resultCode == RESULT_OK) {
                    SongList item = mSongLists.get(mSongList_item_selected);
                    ContentValues values = new ContentValues();

                    String title = data.getStringExtra("title");
                    Uri uri = data.getData();
                    if (title != null) {
                        item.setTitle(data.getStringExtra("title"));
                        values.put("title", title);
                    }
                    if (uri != null) {
                        item.setUri(data.getData().toString());
                        values.put("pictureUri", uri.toString());
                    }
                    if (values.size() != 0) {
                        mMyDataBaseHelper.updateSongList(mMyDataBaseHelper.getWritableDatabase(), values, item.getTitle());
                        mMyAdapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(getActivity(), "修改失败", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}