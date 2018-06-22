package com.example.onepiece.player;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.onepiece.R;
import com.example.onepiece.model.SongList;

import java.util.List;

public class PlayerActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "PlayerActivity";

    private SeekBar seekbar;
    private ImageView playpause;
    private ImageView nextmusic;
    private ImageView lastmusic;
    private ImageView circlestyle;
    private ImageView playinglist;
    private ImageView love;
    private ImageView add;
    private TextView currtime;
    private TextView totaltime;
    private TextView musictitle;
    private TextView singer;
    private TextView musiclyric;

    private List<String> LyricTime;
    private List<String> LyricContent;
    private List<String> MusicInfo;
    private List<String> PlayingList;
    private List<String> songList;

    private int TotalTime = 0;
    private int CurrTime = 0;
    private int PlayingMode = 0;
    private boolean playingORpause = true; //true:播放  false:暂停

    BCReceiver mBcReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_layout);
        playpause = (ImageView) findViewById(R.id.playpause);
        nextmusic = (ImageView) findViewById(R.id.nextmusic);
        lastmusic = (ImageView) findViewById(R.id.lastmusic);
        circlestyle = (ImageView) findViewById(R.id.circlestyle);
        playinglist = (ImageView) findViewById(R.id.playinglist);
        love = (ImageView) findViewById(R.id.love);
        add = (ImageView) findViewById(R.id.add);
        seekbar = (SeekBar)findViewById(R.id.seekbar);
        currtime = (TextView)findViewById(R.id.currtime);
        totaltime = (TextView)findViewById(R.id.totaltime);
        musictitle = (TextView)findViewById(R.id.musictitle);
        singer = (TextView)findViewById(R.id.singer);
        musictitle.setMovementMethod(ScrollingMovementMethod.getInstance());
        musiclyric = (TextView)findViewById(R.id.musiclyric);
        playpause.setOnClickListener(this);
        nextmusic.setOnClickListener(this);
        lastmusic.setOnClickListener(this);
        circlestyle.setOnClickListener(this);
        playinglist.setOnClickListener(this);
        love.setOnClickListener(this);
        add.setOnClickListener(this);

        //注册广播接受器
        mBcReceiver = new BCReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.broadcast.ToActivity");
        registerReceiver(mBcReceiver, intentFilter);

        Intent intent = getIntent();
        String playlistTitle = intent.getStringExtra("playlistTitle");
        int index = intent.getIntExtra("index", 0);

        //启动Service
        Intent serviceIntent = new Intent(this, MusicPlayerService.class);
        serviceIntent.putExtra("playlistTitle", playlistTitle);
        serviceIntent.putExtra("index", index);
        this.startService(serviceIntent);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playpause.setImageDrawable(getResources().getDrawable(R.drawable.pause));
                circlestyle.setImageDrawable(getResources().getDrawable(R.drawable.list_circle));
            }
        });

        //更新播放进度栏
        new Thread(){
            @Override
            public void run() {
                while (true){
                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() { //设置播放进度条
                            if (TotalTime != 0){
                                totaltime.setText(Time_Convert(TotalTime));
                                seekbar.setMax(TotalTime);
                            }
                            if (CurrTime < TotalTime){
                                seekbar.setProgress(CurrTime);
                                currtime.setText(Time_Convert(CurrTime));
                            }
                        }
                    });
                }
            }
        }.start();
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {  //监听进度条拖动
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    BCSender("control_seek", progress);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        new Thread(){
            @Override
            public void run() {
                Lyric_Displayer();
            }
        }.start();

    }

    //向Service发送控制命令
    private void BCSender(String name, String content){
        Intent msg = new Intent();
        msg.setAction("com.broadcast.ToService");
        msg.putExtra(name, content);
        sendBroadcast(msg);
    }
    private void BCSender(String name, int content){
        Intent msg = new Intent();
        msg.setAction("com.broadcast.ToService");
        msg.putExtra(name, content);
        sendBroadcast(msg);
    }
    //接受Service信息
    public class BCReceiver extends BroadcastReceiver {
        public BCReceiver(){}
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getStringArrayListExtra("LyricTime") != null){
                LyricTime = intent.getStringArrayListExtra("LyricTime");
            }
            if(intent.getStringArrayListExtra("LyricContent") != null){
                LyricContent = intent.getStringArrayListExtra("LyricContent");
            }
            if (intent.getStringArrayListExtra("MusicInfo") != null){
                MusicInfo = intent.getStringArrayListExtra("MusicInfo");
            }
            if (intent.getStringArrayListExtra("PlayingList") != null){
                PlayingList = intent.getStringArrayListExtra("PlayingList");
            }
            if(intent.getStringArrayListExtra("SongList") != null){
                songList = intent.getStringArrayListExtra("SongList");
            }
            if (intent.getIntExtra("CurrTime",0) != 0) {
                CurrTime = intent.getIntExtra("CurrTime", 0);
            }
            TotalTime = intent.getIntExtra("TotalTime",0);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.playpause:
                BCSender("control_ps", 0);
                if(playingORpause){
                    playpause.setImageDrawable(getResources().getDrawable(R.drawable.play));
                    playingORpause = false;
                }else{
                    playpause.setImageDrawable(getResources().getDrawable(R.drawable.pause));
                    playingORpause = true;
                }
                break;
            case R.id.nextmusic:
                BCSender("control_ps", 1);
                playingORpause = true;
                playpause.setImageDrawable(getResources().getDrawable(R.drawable.pause));
                break;
            case R.id.lastmusic:
                BCSender("control_ps", 2);
                playingORpause = true;
                playpause.setImageDrawable(getResources().getDrawable(R.drawable.pause));
                break;
            case R.id.circlestyle:
                if(PlayingMode == 0){
                    PlayingMode = 1;
                    BCSender("control_pm", 1);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            circlestyle.setImageDrawable(getResources().getDrawable(R.drawable.random));
                        }
                    });
                }else{
                    PlayingMode = 0;
                    BCSender("control_pm",0);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            circlestyle.setImageDrawable(getResources().getDrawable(R.drawable.list_circle));
                        }
                    });
                }
                break;
            case R.id.playinglist:
                PlayingListPopupwindowEnter();
                break;
            case R.id.love:
                BCSender("AddToSongList","我的喜欢");
                break;
            case R.id.add:
                MusicListPopupwindowEnter();
                break;
            default:
                break;
        }
    }
    //Convert Millisecond to Minute:Second in String
    private String Time_Convert(int t){
        String res = null;
        String minute = String.valueOf(t / 60000);
        String second = String.valueOf((t / 1000) % 60);
        if(minute.length() == 1){minute = '0'+ minute;}
        if(second.length() == 1){second = '0'+ second;}
        res = minute + ":" + second;
        return res;
    }
    //歌词更新
    private void Lyric_Displayer(){
        final int PastSize = 4;     //当前歌词前有四条
        final int PrepareSize = 4;  //当前歌词后有四条
        while(true){
            if(LyricTime == null || LyricContent == null || MusicInfo == null){
                continue;
            }
            String now = Time_Convert(CurrTime);
            if(LyricTime.contains(now)){
                int position = LyricTime.indexOf(now);
                String lyricContent = "";
                for(int i = position - PastSize; i <= position + PrepareSize; i++){
                    if(i == position){
                        lyricContent += "<font color='#363636'><big>" + LyricContent.get(i) + "</big></font><br>";
                    }else if(i >= 0 && i < LyricTime.size()){
                        if(i + 1 == position || i - 1 == position){
                            lyricContent += "<font color='#696969'>" + LyricContent.get(i) + "</font><br>";
                        }else if(i + 2 == position || i - 2 == position){
                            lyricContent += "<font color='#828282'>" + LyricContent.get(i) + "</font><br>";
                        }else if(i + 3 == position || i - 3 == position){
                            lyricContent += "<font color='#9C9C9C'>" + LyricContent.get(i) + "</font><br>";
                        }else if(i + 4 == position || i - 4 == position){
                            lyricContent += "<font color='#B5B5B5'>" + LyricContent.get(i) + "</font><br>";
                        }
                    }else{
                        lyricContent += "<br>";
                    }
                }
                final String finalLyricContent = lyricContent;
                new Thread(){
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //歌曲基本信息更新
                                if (MusicInfo.get(0) != null){
                                    musictitle.setText(MusicInfo.get(0));   //歌名
                                }
                                if (MusicInfo.get(1) != null){
                                    singer.setText(MusicInfo.get(1));       //歌手
                                }
                                //歌词更新
                                musiclyric.setText(Html.fromHtml(finalLyricContent));
                            }
                        });
                    }
                }.start();
            }
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //弹出当前播放歌曲列表
    private void PlayingListPopupwindowEnter() {
        // 利用layoutInflater获得View
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.playing_list, null);

        // 下面是两种方法得到宽度和高度 getWindow().getDecorView().getWidth()
        PopupWindow window = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        window.setFocusable(true);

        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        window.setBackgroundDrawable(dw);

        // 设置popWindow的显示和消失动画
        window.setAnimationStyle(R.style.popupwindow_moving);
        // 在底部显示
        window.showAtLocation(PlayerActivity.this.findViewById(R.id.playinglist), Gravity.BOTTOM, 0, 0);


        ListView playingList = (ListView) view.findViewById(R.id.playingList);
        playingList.setAdapter(new ArrayAdapter<String>(this, R.layout.playing_list_item, PlayingList));
        playingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BCSender("JumpToUrl",PlayingList.get(i));
            }
        });
    }

    //弹出歌单列表
    private void MusicListPopupwindowEnter() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.playing_list, null);

        PopupWindow window = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setFocusable(true);

        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        window.setBackgroundDrawable(dw);

        window.setAnimationStyle(R.style.popupwindow_moving);
        window.showAtLocation(PlayerActivity.this.findViewById(R.id.playinglist), Gravity.BOTTOM, 0, 0);


        ListView playingList = (ListView) view.findViewById(R.id.playingList);
        playingList.setAdapter(new ArrayAdapter<String>(this, R.layout.playing_list_item, songList));
        playingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BCSender("AddToSongList", songList.get(i));
            }
        });
    }

}


