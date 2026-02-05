package com.oortcloud.oort_zhifayi.new_version.chat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.audiofx.Visualizer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.oortcloud.oort_zhifayi.ActivityBase;
import com.oortcloud.oort_zhifayi.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ChatActivity extends ActivityBase {

    private RecyclerView recyclerView;
    private MemberAdapter adapter;
    private List<Member> memberList = new ArrayList<>();
    private Button btnTalk;
    private MediaRecorder mediaRecorder;
    private String outputFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // 初始化成员列表
        initMembers();

        // 设置RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3)); // 3列网格

        adapter = new MemberAdapter(memberList);
        recyclerView.setAdapter(adapter);

        // 初始化录音按钮
//        btnTalk = findViewById(R.id.btnTalk);
//        btnTalk.setOnTouchListener((v, event) -> {
//            switch (event.getAction()) {
//                case MotionEvent.ACTION_DOWN:
//                    startRecording();
//                    break;
//                case MotionEvent.ACTION_UP:
//                    stopRecording();
//                    break;
//            }
//            return true;
//        });

        // 模拟在线状态更新
        simulateOnlineStatus();
    }

    // 初始化成员数据
    private void initMembers() {
        memberList.add(new Member("白倩倩", true));
        memberList.add(new Member("梁佳", true));
        memberList.add(new Member("顾文林", false));
        memberList.add(new Member("万裕", true));
        memberList.add(new Member("李钰", false));
        memberList.add(new Member("马文夏", true));
    }

    // 模拟动态更新在线状态
    private void simulateOnlineStatus() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Random random = new Random();
                for (Member member : memberList) {
                    member.setOnline(random.nextBoolean());
                }
                adapter.updateMembers(memberList);
                // 每5秒更新一次
                new Handler().postDelayed(this, 5000);
            }
        }, 5000);
    }


    // 修改 MainActivity 的录音逻辑
    private Visualizer visualizer;

    private void startRecording() {
        if (checkAudioPermission()) {
            try {
                outputFile = getExternalCacheDir().getAbsolutePath() + "/recording.3gp";
                mediaRecorder = new MediaRecorder();
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                mediaRecorder.setOutputFile(outputFile);
                mediaRecorder.prepare();
                mediaRecorder.start();
                btnTalk.setText("录音中...");

                // 初始化 Visualizer（带兼容性处理）
                int audioSessionId;
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                    audioSessionId = mediaRecorder.getAudioSessionId();
//                } else {
//                    // 手动生成 audioSessionId
                    AudioTrack audioTrack = new AudioTrack(
                            AudioManager.STREAM_MUSIC,
                            44100,
                            AudioFormat.CHANNEL_OUT_MONO,
                            AudioFormat.ENCODING_PCM_16BIT,
                            AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT),
                            AudioTrack.MODE_STREAM
                    );
                    audioSessionId = audioTrack.getAudioSessionId();
                    audioTrack.release();
                //}

                // 初始化 Visualizer
                Visualizer visualizer = new Visualizer(audioSessionId);
                visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
                visualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
                    @Override
                    public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
                        // 更新波形视图
                    }

                    @Override
                    public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {}
                }, Visualizer.getMaxCaptureRate(), true, false);
                visualizer.setEnabled(true);

            } catch (IOException | IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    private void stopRecording_() {
        if (mediaRecorder != null) {
            // ... 停止录音 ...
            if (visualizer != null) {
                visualizer.setEnabled(false);
                visualizer.release();
            }
        }
    }
    // 开始录音
    private void startRecording_() {
        if (checkAudioPermission()) {
            try {
                outputFile = getExternalCacheDir().getAbsolutePath() + "/recording.3gp";
                mediaRecorder = new MediaRecorder();
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                mediaRecorder.setOutputFile(outputFile);
                mediaRecorder.prepare();
                mediaRecorder.start();
                btnTalk.setText("录音中...");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 停止录音并播放
    private void stopRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            btnTalk.setText("按住讲话");
            playRecording();
        }
    }

    // 播放录音
    private void playRecording() {
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(outputFile);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 检查录音权限
    private boolean checkAudioPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO}, 1);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "录音权限已授予", Toast.LENGTH_SHORT).show();
        }
    }
}
