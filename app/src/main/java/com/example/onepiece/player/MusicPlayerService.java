package com.example.onepiece.player;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.example.onepiece.db.MyDataBaseHelper;
import com.example.onepiece.model.Playlist;
import com.example.onepiece.model.Song;
import com.example.onepiece.model.SongList;
import com.example.onepiece.model.SongLists;
import com.example.onepiece.util.DebugMessage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Random;

import static android.os.Environment.DIRECTORY_MUSIC;

public class MusicPlayerService extends Service{
    static private MusicPlayerService mMusicPlayerService;
    private MediaPlayer mMediaPlayer = null;
    private ArrayList<String> LyricTime;
    private ArrayList<String> LyricContent;
    private ArrayList<String> MusicInfo;
    private ArrayList<String> MusicList;
    private ArrayList<String> songList;
    private String mPlaylistTitle;
    private int mCurrentSongIndex;
    private int playingMode = 0;    //0:顺序播放 1:随机播放
    private final String mLyricPath = Environment.getExternalStoragePublicDirectory(DIRECTORY_MUSIC).getAbsolutePath()
            + File.separator + "lyric/";
    private String dataPath = "data.txt";
    public MusicPlayerService() {}
    BCReceiver mBcReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        //InitPlayingSetting();      //初始化默认播放路径
        mBcReceiver = new BCReceiver();  //创建广播接收器
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mMusicPlayerService = this;
        if(intent.getStringExtra("playlistTitle") != null){
            mPlaylistTitle = intent.getStringExtra("playlistTitle");
        }
        if(intent.getIntExtra("index", 0) != -1) {
            mCurrentSongIndex = intent.getIntExtra("index", -1);
        }

        //注册广播接收器
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.broadcast.ToService");
        registerReceiver(mBcReceiver, intentFilter);

        //初始化播放器
        InitMediaPlayer();
        //同步歌曲播放进度
        new Thread(){
            @Override
            public void run() {
                while (true){
                    try{
                        sleep(1000);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    if (getMusicDuration() != 0){
                        BCSender("CurrTime",getMusicCurrentPosition());
                        BCSender("TotalTime", getMusicDuration());
                    }
                }
            }
        }.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy(){
        //保存当前播放状态
        try{
            FileOutputStream fos = openFileOutput(dataPath, Context.MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
            osw.write(mPlaylistTitle);
            osw.write("\r\n");
            osw.write(mCurrentSongIndex);
            osw.flush();
            fos.flush();
            osw.close();
            fos.close();
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //初始化播放器
    public void InitMediaPlayer(){
        if (mMediaPlayer != null){
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);

        try{
            InitMusicList();             //初始化播放列表
            InitSongList();             //初始化歌单列表
            mMediaPlayer.setDataSource(MusicPlayerService.this, Uri.parse(MusicList.get(mCurrentSongIndex)));  //设置播放路径
            mMediaPlayer.prepareAsync();
            //加载完成监听
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });
            //播放完毕监听
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    nextmusic();
                }
            });
            String songTitle = Playlist.get(MusicPlayerService.this, mPlaylistTitle)
                    .getSongs()
                    .get(mCurrentSongIndex)
                    .getTitle();
            InitLyricDisplayer(mLyricPath + songTitle + ".lrc");                       //初始化歌词展示器
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    // 播放/暂停
    public void playpause() {
        if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
        }else if (mMediaPlayer != null && mMediaPlayer.isPlaying()){
            mMediaPlayer.pause();
        }
    }

    //下一曲
    public void nextmusic() {
        //更新歌曲歌词路径
        GetNextMusicURL(1);
        //重新初始化播放器
        InitMediaPlayer();
    }

    //上一曲
    public void lastmusic() {
        //更新歌曲歌词路径
        GetNextMusicURL(0);
        //重新初始化播放器
        InitMediaPlayer();
    }

    //跳转到目标URL播放
    public void jump(String url){
        //更新歌曲歌词路径
        if(MusicList.indexOf(url) != -1){
            mCurrentSongIndex = MusicList.indexOf(url);
        }
        //重新初始化播放器
        InitMediaPlayer();
    }

    //监听Activity控制命令
    public class BCReceiver extends BroadcastReceiver {
        public BCReceiver(){}
        @Override
        public void onReceive(Context context, Intent intent) {
            //循环模式
            if(intent.getIntExtra("control_pm", -1) != -1){
                playingMode = intent.getIntExtra("control_pm", -1);
            }
            //播放控制
            int playingStatus = intent.getIntExtra("control_ps",-1);
            switch (playingStatus) {
                case 0:
                    playpause();
                    break;
                case 1:
                    nextmusic();
                    break;
                case 2:
                    lastmusic();
                    break;
            }
            //播放进度控制
            int seekTime = intent.getIntExtra("control_seek", -1);
            if(seekTime != -1){
                seekTo(seekTime);
            }
            //歌曲切换
            String url = intent.getStringExtra("JumpToUrl");
            if(url != null){
                jump(url);
            }
            //添加到歌单
            String songListName = intent.getStringExtra("AddToSongList");
            if(songListName != null){
                MyDataBaseHelper db = MyDataBaseHelper.get(MusicPlayerService.this, "OnePiece", 1);
                db.insertSong(songListName, String.valueOf(Playlist.get(MusicPlayerService.this, mPlaylistTitle).getSongs().get(mCurrentSongIndex).getId()));
            }
        }
    }
    //发送Activity需要的信息
    private void BCSender(String name, String content){
        Intent msg = new Intent();
        msg.setAction("com.broadcast.ToActivity");
        msg.putExtra(name, content);
        sendBroadcast(msg);
    }
    private void BCSender(String name, int content){
        Intent msg = new Intent();
        msg.setAction("com.broadcast.ToActivity");
        msg.putExtra(name, content);
        sendBroadcast(msg);
    }
    private void BCSender(String name, ArrayList<String> content){
        Intent msg = new Intent();
        msg.setAction("com.broadcast.ToActivity");
        msg.putStringArrayListExtra(name, content);
        sendBroadcast(msg);
    }

    //获取歌曲长度
    public int getMusicDuration() {
        int rtn = 0;
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            rtn = mMediaPlayer.getDuration();
        }
        return rtn;
    }
    //获取当前播放进度
    public int getMusicCurrentPosition() {
        int rtn = 0;
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            rtn = mMediaPlayer.getCurrentPosition();
        }
        return rtn;
    }

    //跳转时长（毫秒）
    public void seekTo(int position) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(position);
        }
    }

    //初始化单曲歌词列表
    public void InitLyricDisplayer(String lyricFilePath){
        LyricTime = new ArrayList<String>();
        LyricContent = new ArrayList<String>();
        MusicInfo = new ArrayList<String>(Arrays.asList("","",""));
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(lyricFilePath)), "GBK"));
            String line = null;
            while ((line = br.readLine()) != null) {
                if(line.length() > 9 && line.charAt(0) == '[' && line.charAt(3) == ':' && line.charAt(6) == '.' & line.charAt(9) == ']'){
                    String minute = line.substring(1, 3);
                    String second = line.substring(4,6);
                    String time = line.substring(1,6);
                    String content = "";
                    if (line.length() > 10 && !LyricTime.contains(time)){
                        content = line.substring(10);
                        LyricTime.add(time);
                        LyricContent.add(content);
                    }
                }else if(line.length() > 3 && (line.substring(1,3).equals("ti") || line.substring(1,3).equals("ar") || line.substring(1,3).equals("al"))){
                    String info = line.substring(1,3);
                    String content = "";
                    if (line.length() > 4){
                        content = line.substring(4, line.length() - 1);
                    }
                    switch (info){
                        case "ti":
                            MusicInfo.set(0, content);
                            break;
                        case "ar":
                            MusicInfo.set(1, content);
                            break;
                        case "al":
                            MusicInfo.set(2, content);
                            break;
                    }

                }else{
                    continue;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        BCSender("LyricTime", LyricTime);
        BCSender("LyricContent", LyricContent);
        BCSender("MusicInfo", MusicInfo);
    }

    //初始化播放列表
    public void InitMusicList(){
        MusicList = new ArrayList<String>();
        try{
            List<Song> songs = Playlist.get(MusicPlayerService.this, mPlaylistTitle).getSongs();
            for (Song song : songs) {
                MusicList.add(song.getUrl());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        BCSender("PlayingList", MusicList);
    }

    //初始歌单列表
    public void InitSongList(){
        songList = new ArrayList<String>();
        try{
            //TODO
            List<com.example.onepiece.model.SongList> lists = SongLists.get(MusicPlayerService.this).getSongLists();
            for(SongList list : lists){
                songList.add(list.getTitle());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        BCSender("SongList", songList);
    }

    //根据当前播放歌曲在歌曲列表中取出下一首的URL
    public void GetNextMusicURL(int mode) {
        if(mode == 1 && playingMode == 0){          //顺序播放下一首
            mCurrentSongIndex = (mCurrentSongIndex + 1) % MusicList.size();
        }else if (mode == 2 && playingMode == 0){   //顺序播放上一首
            mCurrentSongIndex = (mCurrentSongIndex + MusicList.size() - 1) % MusicList.size();
        }else if (playingMode == 1){                //随机取出下一首
            Random rand = new Random();
            int choice = mCurrentSongIndex;
            while(choice == mCurrentSongIndex){
                choice = rand.nextInt(MusicList.size());
            }
            mCurrentSongIndex = choice;
        }
    }

    public static MusicPlayerService getService() {
        return mMusicPlayerService;

    }

    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    //读取初始化播放状态
    public void InitPlayingSetting(){
        try{
            FileInputStream fis = openFileInput(dataPath);
            InputStreamReader isr = new InputStreamReader(fis, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String line;
            for (int i = 0; i < 2; i++){
                line = br.readLine();
                if(line != null && i == 0){
                    mPlaylistTitle = line;
                }else if(line != null && i == 1){
                    mCurrentSongIndex = Integer.parseInt(line);
                }
            }
            isr.close();
            fis.close();
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
