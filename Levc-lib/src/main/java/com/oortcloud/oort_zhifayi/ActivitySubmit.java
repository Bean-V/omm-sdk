package com.oortcloud.oort_zhifayi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;

import com.oortcloud.basemodule.constant.Constant;

public class ActivitySubmit extends ActivityBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);
        findViewById(R.id.tv_know).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ActivitySubmit.this, ActivityTasks.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);

                //finish();
            }
        });


        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(3000);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if(!ActivitySubmit.this.isDestroyed()){


                            Intent intent = new Intent(ActivitySubmit.this, ActivityTasks.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);

// 结束当前Activity
                            //finish();
                            return;
                        }
                        finish();
                    }
                });
            }
        }).start();


        MediaPlayer player = MediaPlayer.create(this, R.raw.submitsuc);
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                // 播放完成后执行逻辑，例如停止播放或者释放资源
                mediaPlayer.stop(); // 如果您希望停止播放
                mediaPlayer.release(); // 如果您希望释放 MediaPlayer 对象
            }
        });
        if(ZFYConstant.IsDebbug) {
            player.start();
        }


    }
    @Override
    protected void onResume() {
        super.onResume();


    }
}