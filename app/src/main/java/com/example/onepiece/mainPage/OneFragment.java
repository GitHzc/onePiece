package com.example.onepiece.mainPage;

import android.content.Intent;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.example.onepiece.R;
import com.example.onepiece.model.DownloadFile;
import com.example.onepiece.model.OneBean;
import com.example.onepiece.model.Playlist;
import com.example.onepiece.model.Song;
import com.example.onepiece.player.PlayerActivity;
import com.example.onepiece.util.DebugMessage;
import com.example.onepiece.util.FileUtils;
import com.example.onepiece.util.HttpUtils;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;

import static android.os.Environment.DIRECTORY_MUSIC;

/**
 * Created by Administrator on 2018/5/19 0019.
 */

public class OneFragment extends Fragment {
    @BindView(R.id.one_fragment_song_title)
    TextView mTitle;
    @BindView(R.id.one_fragment_artist)
    TextView mArtist;
    @BindView(R.id.one_fragment_picture)
    ImageView mPicture;
    @BindView(R.id.love_button)
    ToggleButton mToggleButton;
    private int mAudioId;
    private int mLyricId;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.one_fragment, container, false);
        ButterKnife.bind(this, view);
        mToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mToggleButton.setBackgroundResource(isChecked? R.drawable.love_red : R.drawable.love_gray);
            }
        });
        mPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fileName = FileUtils.getLyricDirectory() + mTitle.getText() + ".lrc";
                resourceDownload(fileName, "lyric", mLyricId);
                fileName = FileUtils.getMusicDirectory() + mTitle.getText() + ".mp3";
                resourceDownload(fileName, "audio", mAudioId);
            }
        });

        mPicture.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getOneContent();
            }
        });

        return view;
    }


    private void getOneContent() {
        if (!readCache()) {
            Retrofit retrofit = HttpUtils.getRetrofit();
            HttpUtils.MyApi api = retrofit.create(HttpUtils.MyApi.class);
            api.fetchOne()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<OneBean>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(OneBean oneBean) {
                            saveInCache(oneBean);
                            mTitle.setText(oneBean.getSong().getTitle());
                            mArtist.setText(oneBean.getSong().getArtist());
                            mAudioId = oneBean.getSong().getAudio();
                            mLyricId = oneBean.getSong().getLyric();
                            String pictureName = oneBean.getImage().substring(oneBean.getImage().lastIndexOf('/') + 1);
                            getOnePicture(pictureName);
                        }

                        @Override
                        public void onError(Throwable e) {
                        }

                        @Override
                        public void onComplete() {
                        }
                    });
        }
    }

    private void getOnePicture(final String picName) {
        Retrofit retrofit = HttpUtils.getRetrofit();
        HttpUtils.MyApi api = retrofit.create(HttpUtils.MyApi.class);
        api.getOnePicture(picName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    File picture;
                    @Override
                    public void onSubscribe(Disposable d) {}

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        String picCache = FileUtils.getOnePictureCacheDirectory(getActivity());
                        Date date = new Date();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                        String fileName = sdf.format(date);
                        String filePath = picCache + fileName;
                        FileUtils.writeResponseBodyToFile(filePath, responseBody);
                        picture = new File(filePath);
                    }

                    @Override
                    public void onError(Throwable e) {}

                    @Override
                    public void onComplete() {
                        int width = mPicture.getWidth();
                        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width, width);
                        lp.addRule(RelativeLayout.BELOW, R.id.the_one);
                        mPicture.setLayoutParams(lp);
                        Log.d(DebugMessage.TAG, "onComplete: " + picture.toString());
                        Glide.with(OneFragment.this).load(picture).into(mPicture);
                    }
                });
    }

    void resourceDownload(final String fileName, final String requestType, int fileId) {
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
                    public void onNext(ResponseBody responseBody) {
                        FileUtils.writeResponseBodyToFile(fileName, responseBody);
                    }

                    @Override
                    public void onError(Throwable e) {}

                    @Override
                    public void onComplete() {
                        List<Song> songs = Playlist.get(getActivity(), "每日一歌").getSongs();
                        if (requestType.equals("audio")) {
                            MediaScannerConnection.scanFile(getActivity(), new String[]{fileName}, null, null);
                            Song song = new Song(0, mTitle.getText().toString(), mArtist.getText().toString(), 0, fileName, null);
                            songs.add(song);
                            Intent intent = new Intent(getActivity(), PlayerActivity.class);
                            intent.putExtra("playlistTitle", "每日一歌");
                            intent.putExtra("index", 0);
                            startActivity(intent);
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), "下载完成", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
    }

    private boolean saveInCache(Serializable ser) {
        String onePath = FileUtils.getOneCacheDirectory(getActivity());
        FileUtils.createDir(onePath);
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String fileName = simpleDateFormat.format(date);
        return FileUtils.saveSerializableObject(ser, onePath + fileName);
    }

    private boolean readCache() {
        String oneBeanCache = FileUtils.getOneCacheDirectory(getActivity());
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String fileName = simpleDateFormat.format(date);
        OneBean oneBean = (OneBean)FileUtils.readSerializableObject(oneBeanCache + fileName);
        if (oneBean != null) {
            mTitle.setText(oneBean.getSong().getTitle());
            mArtist.setText(oneBean.getSong().getArtist());
            mAudioId = oneBean.getSong().getAudio();
            mLyricId = oneBean.getSong().getLyric();

            String picPath = FileUtils.getOnePictureCacheDirectory(getActivity()) + fileName;
            File picture = new File(picPath);
            int width = mPicture.getWidth();
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width, width);
            lp.addRule(RelativeLayout.BELOW, R.id.the_one);
            mPicture.setLayoutParams(lp);
            Glide.with(OneFragment.this)
                    .load(picture)
                    .into(mPicture);
            return true;
        }
        return false;
    }
}
