package com.example.onepiece.mainPage;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.media.audiofx.PresetReverb;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onepiece.R;
import com.example.onepiece.player.MusicPlayerService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EqualizerActivity extends AppCompatActivity {
    private MediaPlayer mPlayer;
    private Visualizer mVisualizer;     //系统频谱
    private Equalizer mEqualizer;       //系统均衡器
    private BassBoost mBassBoost;       //系统重低音控制器
    private PresetReverb mPresetReverb; //系统的预设音场控制器
    private LinearLayout layout;
    private List<Short> reverbNames = new ArrayList<>();
    private List<String> reverbVals = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        setContentView(R.layout.activity_equalizer);

        Toolbar toolbar = findViewById(R.id.equalizer_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        layout = findViewById(R.id.equalizer_container);

        if (MusicPlayerService.getService() == null) {
            Toast.makeText(EqualizerActivity.this, "无正在播放音乐", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            mPlayer = MusicPlayerService.getService().getMediaPlayer();
            setUpVisualizer();
            setUpEqualizer();
            setUpBassBoost();
            setUpPresetReverb();
        }
    }

    //初始化频谱
    private void setUpVisualizer() {
        final MyVisualizerView myVisualizerView = new MyVisualizerView(this);
        myVisualizerView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                (int) (180f * getResources().getDisplayMetrics().density), 1.0f));
        layout.addView(myVisualizerView);
        mVisualizer = new Visualizer(mPlayer.getAudioSessionId());
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        mVisualizer.setDataCaptureListener(
                new Visualizer.OnDataCaptureListener() {
                    @Override
                    public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes, int i) {
                        myVisualizerView.updateVisualizer(bytes);
                    }

                    @Override
                    public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int i) {

                    }
                }, Visualizer.getMaxCaptureRate() / 2, true, false
        );
        mVisualizer.setEnabled(true);
    }

    private void setUpEqualizer() {
        mEqualizer = new Equalizer(0, mPlayer.getAudioSessionId());
        mEqualizer.setEnabled(true);
        TextView eqTitle = new TextView(this);
        eqTitle.setText("均衡器：");
        layout.addView(eqTitle);
        final short minEQLevel = mEqualizer.getBandLevelRange()[0];
        short maxEQLevel = mEqualizer.getBandLevelRange()[1];
        short brands = mEqualizer.getNumberOfBands();
        for (short i = 0; i < brands; i++) {
            TextView eqTextView = new TextView(this);
            eqTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            eqTextView.setGravity(Gravity.CENTER_HORIZONTAL);
            eqTextView.setText(String.format(Locale.getDefault(),"%dHZ",mEqualizer.getCenterFreq(i) / 1000));
            layout.addView(eqTextView);
            LinearLayout tmpLayout = new LinearLayout(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1.0f
            );
            tmpLayout.setLayoutParams(params);
            tmpLayout.setOrientation(LinearLayout.HORIZONTAL);
            TextView minDbTextView = new TextView(this);
            minDbTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            minDbTextView.setText(String.format(Locale.getDefault(), "%ddB", minEQLevel / 100));
            TextView maxDbTextView = new TextView(this);
            maxDbTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            maxDbTextView.setText(String.format(Locale.getDefault(), "%ddB", maxEQLevel / 100));
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            layoutParams.weight = 1;
            SeekBar bar = new SeekBar(this);
            bar.setLayoutParams(layoutParams);
            bar.setMax(maxEQLevel - minEQLevel);
            bar.setProgress(mEqualizer.getBandLevel(i));
            final short brand = 1;
            bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    mEqualizer.setBandLevel(brand, (short)(i + minEQLevel));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            tmpLayout.addView(minDbTextView);
            tmpLayout.addView(bar);
            tmpLayout.addView(maxDbTextView);
            layout.addView(tmpLayout);
        }
    }

    private void setUpBassBoost() {
        mBassBoost = new BassBoost(0, mPlayer.getAudioSessionId());
        mBassBoost.setEnabled(true);
        TextView bbTitle = new TextView(this);
        bbTitle.setText("重低音：");
        LinearLayout tmpLayout = new LinearLayout(EqualizerActivity.this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
        );
        tmpLayout.setLayoutParams(params);
        tmpLayout.setOrientation(LinearLayout.VERTICAL);
        tmpLayout.addView(bbTitle);
        SeekBar bar = new SeekBar(this);
        bar.setMax(1000);
        bar.setProgress(0);
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mBassBoost.setStrength((short)i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        tmpLayout.addView(bar);
        layout.addView(tmpLayout);
    }

    private void setUpPresetReverb() {
        mPresetReverb = new PresetReverb(0, mPlayer.getAudioSessionId());
        mPresetReverb.setEnabled(true);
        TextView prTitle = new TextView(this);
        prTitle.setText("音场：");
        LinearLayout tmpLayout = new LinearLayout(EqualizerActivity.this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
        );
        tmpLayout.setLayoutParams(params);
        tmpLayout.setOrientation(LinearLayout.VERTICAL);
        tmpLayout.addView(prTitle);
        for (short i = 0; i < mEqualizer.getNumberOfPresets(); i++) {
            reverbNames.add(i);
            reverbVals.add(mEqualizer.getPresetName(i));
        }
        Spinner sp = new Spinner(this);
        sp.setAdapter(new ArrayAdapter<String>(
                EqualizerActivity.this,
                android.R.layout.simple_spinner_item,
                reverbVals));
        sp.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mPresetReverb.setPreset(reverbNames.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        tmpLayout.addView(sp);
        layout.addView(tmpLayout);
    }

    protected  void onPause() {
        super.onPause();
        if (isFinishing() && mPlayer != null) {
            mVisualizer.release();
            mEqualizer.release();
            mPresetReverb.release();
            mBassBoost.release();
            mPlayer = null;
        }
    }
}
