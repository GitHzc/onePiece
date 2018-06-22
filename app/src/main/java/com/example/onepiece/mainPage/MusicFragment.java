package com.example.onepiece.mainPage;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaScannerConnection;
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
import com.example.onepiece.model.DownloadFile;
import com.example.onepiece.model.SongListBean;
import com.example.onepiece.model.SongList;
import com.example.onepiece.model.SongListOfUserBean;
import com.example.onepiece.model.SongLists;
import com.example.onepiece.model.User;
import com.example.onepiece.model.UserNameBean;
import com.example.onepiece.util.DebugMessage;
import com.example.onepiece.util.FileUtils;
import com.example.onepiece.util.HttpUtils;
import com.example.onepiece.util.Utility;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;

import static android.app.Activity.RESULT_OK;
import static com.example.onepiece.mainPage.SearchResultFragment.getSongIdByPath;
import static com.example.onepiece.mainPage.SearchResultFragment.mapId;

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
    private Map<String, List<SongListOfUserBean.ItemsBean>> mStringItemsBeanMap;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mMyView = inflater.inflate(R.layout.music_fragment, container, false);

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
        return mMyView;
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
                    String title = temp.getText().toString();
                    Intent intent = new Intent(getActivity(), PlaylistActivity.class);
                    intent.putExtra("title", title);
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
            mMyDataBaseHelper.createSongList(id);  //创建本地音乐歌单对应的数据库表
            String title = "本地音乐";
            int numberOfSongs = init_localMusic(id);
            String createTime = Utility.getTimeNow();
            String uri = "android.resource://" + getActivity().getPackageName() + "/" + R.mipmap.umeng_socialize_share_music;
            SongList localMusic = new SongList(id, title, numberOfSongs, createTime, uri);
            mSongLists.add(localMusic);
            //将本地音乐歌单加入到歌单数据库中
            mMyDataBaseHelper.insertSongList(localMusic.getContentValues());
        } else {
            String id = SongLists.get(getActivity()).getSongListIdByTitle("本地音乐");
            int numberOfSongs = init_localMusic(id);
            SongLists.get(getActivity()).getSongListByTitle("本地音乐").setNumberOfSongs(numberOfSongs);
        }

        // 只有本地音乐歌单且用户已经登录
        if (mSongLists.size() == 1 && User.get().isLogin()) {
            synchronizeSongLists(User.get().getUsername());
        }

        mMyAdapter = new MyAdapter(mSongLists, R.layout.music_songlists_item, getActivity());
        ListView mListView = mMyView.findViewById(R.id.playlist_view);
        mListView.setAdapter(mMyAdapter);
    }

    private int init_localMusic(String localMusicID) {
        Cursor cursor = getActivity().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Media._ID},
                null,
                null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        int numberOfSongs = cursor.getCount();
        for (int i = 0; i < numberOfSongs; i++) {
            cursor.moveToNext();
            String songID = cursor.getString(0);
            mMyDataBaseHelper.insertSong(localMusicID, songID);
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
                    mMyDataBaseHelper.deleteSongList(delete_item.getTitle());
                    mMyDataBaseHelper.dropSongList(delete_item.getListID());
                    mMyAdapter.notifyDataSetChanged();
                    synchronizeSongList(User.get().getUsername(), delete_item.getTitle(), false);
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
                mMyDataBaseHelper.insertSongList(new_item.getContentValues());
                //创建歌单对应的数据库表
                mMyDataBaseHelper.createSongList(id);

                synchronizeSongList(User.get().getUsername(), title, true);
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
        super.onActivityResult(requestCode, resultCode, data);
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
                        mMyDataBaseHelper.updateSongList(values, item.getTitle());
                        mMyAdapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(getActivity(), "修改失败", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void synchronizeSongList(String username, String songListName, boolean flag) {
        SongListBean songListBean = new SongListBean();
        songListBean.setUsername(username);
        songListBean.setSongListName(songListName);
        Retrofit retrofit = HttpUtils.getRetrofit();
        HttpUtils.MyApi api = retrofit.create(HttpUtils.MyApi.class);

        if (flag) {     //创建歌单
            api.createSongList(songListBean)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ResponseBody>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(ResponseBody responseBody) {
                            Toast.makeText(getActivity(), "歌单创建成功", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(Throwable e) {
                            String errorMessage = e.getMessage();
                            if (errorMessage.contains("406")) {
                                Toast.makeText(getActivity(), "歌单已存在", Toast.LENGTH_SHORT).show();
                            } else if (errorMessage.contains("404")) {
                                Toast.makeText(getActivity(), "找不到该用户", Toast.LENGTH_SHORT).show();
                            } else if (errorMessage.contains("400")) {
                                Toast.makeText(getActivity(), "请求错误", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onComplete() {
                        }
                    });
        } else { // 删除歌单
            api.deleteSongList(songListBean)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ResponseBody>() {
                        @Override
                        public void onSubscribe(Disposable d) {}

                        @Override
                        public void onNext(ResponseBody responseBody) {
                            Toast.makeText(getActivity(), "删除歌单成功", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(Throwable e) {
                            String errorMessage = e.getMessage();
                            if (errorMessage.contains("404")) {
                                Toast.makeText(getActivity(), "歌单不存在", Toast.LENGTH_SHORT).show();
                            } else if (errorMessage.contains("400")) {
                                Toast.makeText(getActivity(), "请求错误", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onComplete() {}
                    });
        }
    }

    private void synchronizeSongLists(String username) {
        Retrofit retrofit = HttpUtils.getRetrofit();
        HttpUtils.MyApi api = retrofit.create(HttpUtils.MyApi.class);
        UserNameBean userNameBean = new UserNameBean();
        userNameBean.setUsername(username);
        api.getSongListOfUser(userNameBean)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<SongListOfUserBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {}

                    @Override
                    public void onNext(List<SongListOfUserBean> songListOfUserBeans) {
                        mStringItemsBeanMap = new HashMap<>();
                        for (SongListOfUserBean songListOfUserBean : songListOfUserBeans) {
                            UUID uuid = UUID.randomUUID();
                            String id = SongList.generateID(uuid);
                            String title = songListOfUserBean.getPlaylistName();
                            String uri = "android.resource://" + getActivity().getPackageName() + "/" + R.mipmap.umeng_socialize_share_music;
                            int numberOfSongs = songListOfUserBean.getItems().size();
                            String createTime = Utility.getTimeNow();
                            SongList new_item = new SongList(id, title, numberOfSongs, createTime, uri);
                            mSongLists.add(new_item);
                            //在歌单数据库中加入新歌单
                            mMyDataBaseHelper.insertSongList(new_item.getContentValues());
                            //创建歌单对应的数据库表
                            mMyDataBaseHelper.createSongList(id);

                            mStringItemsBeanMap.put(title, songListOfUserBean.getItems());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        String errorMessage = e.getMessage();
                        if (errorMessage.contains("404")) {
                            Toast.makeText(getActivity(), "找不到该用户，请检查用户名", Toast.LENGTH_SHORT).show();
                        } else if (errorMessage.contains("400")) {
                            Toast.makeText(getActivity(), "请求错误", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onComplete() {
                        mMyAdapter.notifyDataSetChanged();
                        Set<String> keys = mStringItemsBeanMap.keySet();
                        for (String key : keys) {
                            List<SongListOfUserBean.ItemsBean> itemsBeans = mStringItemsBeanMap.get(key);
                            for (SongListOfUserBean.ItemsBean itemsBean : itemsBeans) {
                                String fileName1 = FileUtils.getMusicDirectory() + itemsBean.getSong().getTitle() + ".mp3";
                                int fileId1 = itemsBean.getSong().getId();
                                resourceDownload(key, fileName1, "audio", fileId1);
                                String fileName2 = FileUtils.getLyricDirectory() + itemsBean.getSong().getTitle() + ".lrc";
                                int fileId2 = itemsBean.getSong().getLyric();
                                resourceDownload(null, fileName2, "lyric", fileId2);
                            }
                        }
                    }
                });
    }


    private void resourceDownload(final String songList, final String fileName, final String requestType, final int fileId) {
        Retrofit retrofit = HttpUtils.getRetrofit();
        HttpUtils.MyApi api = retrofit.create(HttpUtils.MyApi.class);
        DownloadFile df = new DownloadFile();
        df.setFileId(fileId);
        df.setRequestType(requestType);
        api.downloadFile(df)
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {}

                    @Override
                    public void onNext(final ResponseBody responseBody) {
                        FileUtils.writeResponseBodyToFile(fileName, responseBody);
                        if (requestType.equals("audio")) {
                            MediaScannerConnection.scanFile(getActivity(), new String[]{fileName}, null, null);
                            String id = getSongIdByPath(getActivity(), fileName);
                            MyDataBaseHelper myDataBaseHelper = MyDataBaseHelper.get(getActivity(), "OnePiece", 1);
                            String songListId = SongLists.get(getActivity()).getSongListIdByTitle(songList);
                            myDataBaseHelper.insertSong(songListId, id);
                            mapId(getActivity(), id, fileId);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {}

                    @Override
                    public void onComplete() {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), "下载完成", Toast.LENGTH_SHORT).show();
                                }
                            });
                    }
                });
    }
}