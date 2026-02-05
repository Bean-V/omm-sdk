package com.oortcloud.oort_zhifayi;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.BaseBundle;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;

import com.oortcloud.basemodule.BaseActivity;
import com.oortcloud.basemodule.constant.Constant;

public class ActivityPost extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);


        findViewById(R.id.tv_know).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(3000);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if(ActivityPost.this.isDestroyed()){
                            return;
                        }
                        finish();
                    }
                });
            }
        }).start();
    }
    @Override
    protected void onResume() {
        super.onResume();
        MediaPlayer player = MediaPlayer.create(this, R.raw.postsuc);
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    // 播放完成后执行逻辑，例如停止播放或者释放资源
                    mediaPlayer.stop(); // 如果您希望停止播放
                    mediaPlayer.release(); // 如果您希望释放 MediaPlayer 对象
                }
            });
            player.start();
    }
}