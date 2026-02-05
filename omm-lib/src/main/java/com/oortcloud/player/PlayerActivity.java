package com.oortcloud.player;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.oort.weichat.R;
import com.oortcloud.basemodule.utils.OperLogUtil;

import tcking.github.com.giraffeplayer.GiraffePlayer;
import tcking.github.com.giraffeplayer.GiraffePlayerActivity;
import tv.danmaku.ijk.media.player.IMediaPlayer;


public class PlayerActivity extends AppCompatActivity {
    GiraffePlayer player;
    String url = "";
    private String currentPlayUrl = ""; // 记录当前播放的URL，用于日志追踪

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OperLogUtil.msg("PlayerActivity - onCreate：Activity创建，开始初始化播放页面");
        setContentView(R.layout.activity_player);

        // 获取并校验Intent中的URL
        Intent intent = getIntent();
        if (intent == null) {
            OperLogUtil.msg("PlayerActivity - onCreate：错误！获取到的Intent为空，无法获取播放地址");
            Toast.makeText(this, "播放参数错误", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        url = intent.getStringExtra("URL");
        OperLogUtil.msg("PlayerActivity - onCreate：从Intent获取到的URL：" + (TextUtils.isEmpty(url) ? "空值" : url));

        // 校验URL有效性
        if (TextUtils.isEmpty(url)) {
            OperLogUtil.msg("PlayerActivity - onCreate：错误！URL为空，终止播放流程");
            Toast.makeText(this, "无效视频地址", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        currentPlayUrl = url;
        OperLogUtil.msg("PlayerActivity - onCreate：URL校验通过，准备初始化播放器，目标URL：" + currentPlayUrl);

        // 初始化播放器
        try {
            player = new GiraffePlayer(this);
            OperLogUtil.msg("PlayerActivity - onCreate：GiraffePlayer初始化成功");
        } catch (Exception e) {
            OperLogUtil.msg("PlayerActivity - onCreate：错误！初始化播放器失败，异常信息：" + e.getMessage()
                    + "，堆栈：" + android.util.Log.getStackTraceString(e));
            Toast.makeText(this, "播放器初始化失败", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // 设置播放完成回调
        player.onComplete(new Runnable() {
            @Override
            public void run() {
                OperLogUtil.msg("PlayerActivity - onComplete：视频播放完成，当前URL：" + currentPlayUrl);
                finish();
            }
        });

        // 设置播放信息回调（缓冲、带宽等）
        player.onInfo(new GiraffePlayer.OnInfoListener() {
            @Override
            public void onInfo(int what, int extra) {
                String infoDesc = getInfoDescription(what, extra);
                OperLogUtil.msg("PlayerActivity - onInfo：当前URL：" + currentPlayUrl + "，信息：" + infoDesc);

                // 更新UI显示
                if (what == IMediaPlayer.MEDIA_INFO_NETWORK_BANDWIDTH) {
                    ((TextView) findViewById(R.id.tv_speed)).setText(Formatter.formatFileSize(getApplicationContext(), extra) + "/s");
                } else if (what == IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                    findViewById(R.id.tv_speed).setVisibility(View.GONE);
                }
            }
        });

        // 设置播放错误回调
        player.onError(new GiraffePlayer.OnErrorListener() {
            @Override
            public void onError(int what, int extra) {
                String errorDesc = getErrorDescription(what, extra);
                OperLogUtil.msg("PlayerActivity - onError：当前URL：" + currentPlayUrl + "，错误详情：" + errorDesc
                        + "，堆栈：" + android.util.Log.getStackTraceString(new Throwable()));
                Toast.makeText(getApplicationContext(), "播放错误：" + errorDesc, Toast.LENGTH_SHORT).show();
            }
        });

        // 发起初始播放请求
        try {
            OperLogUtil.msg("PlayerActivity - onCreate：开始播放初始URL：" + currentPlayUrl);
            player.play(url);
            player.setTitle(" ");
            OperLogUtil.msg("PlayerActivity - onCreate：初始播放请求已提交");
        } catch (Exception e) {
            OperLogUtil.msg("PlayerActivity - onCreate：错误！发起初始播放失败，异常：" + e.getMessage()
                    + "，堆栈：" + android.util.Log.getStackTraceString(e));
            Toast.makeText(this, "播放请求失败", Toast.LENGTH_SHORT).show();
        }

        // 初始化所有按钮点击事件
        OperLogUtil.msg("PlayerActivity - onCreate：初始化按钮点击监听器");
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleButtonClick(v);
            }
        };

        // 绑定所有按钮点击事件
        findViewById(R.id.btn_play).setOnClickListener(clickListener);
        findViewById(R.id.btn_play_sample_1).setOnClickListener(clickListener);
        findViewById(R.id.btn_play_sample_2).setOnClickListener(clickListener);
        findViewById(R.id.btn_play_sample_3).setOnClickListener(clickListener);
        findViewById(R.id.btn_pause).setOnClickListener(clickListener);
        findViewById(R.id.btn_start).setOnClickListener(clickListener);
        findViewById(R.id.btn_toggle).setOnClickListener(clickListener);
        findViewById(R.id.btn_open).setOnClickListener(clickListener);
        findViewById(R.id.btn_forward).setOnClickListener(clickListener);
        findViewById(R.id.btn_back).setOnClickListener(clickListener);
        findViewById(R.id.btn_toggle_ratio).setOnClickListener(clickListener);

        OperLogUtil.msg("PlayerActivity - onCreate：初始化完成");
    }

    // 处理所有按钮点击事件
    private void handleButtonClick(View v) {
        int id = v.getId();
        String btnName = getButtonName(id);

        OperLogUtil.msg("PlayerActivity - handleButtonClick：点击按钮：" + btnName + "，当前播放URL：" + currentPlayUrl);

        try {
            if (id == R.id.btn_play) {
                handleCustomUrlPlay();
            } else if (id == R.id.btn_play_sample_1) {
                handleSamplePlay("http://devimages.apple.com/iphone/samples/bipbop/bipbopall.m3u8", "示例1（m3u8）");
            } else if (id == R.id.btn_play_sample_2) {
                handleSamplePlay("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4", "示例2（MP4）");
                player.setShowNavIcon(false);
            } else if (id == R.id.btn_play_sample_3) {
                String sample3Url = "https://r13---sn-o097znes.googlevideo.com/videoplayback?mt=1455852432&mv=m&ms=au&source=youtube&key=yt6&requiressl=yes&mm=31&mn=sn-o097znes&initcwndbps=16485000&id=o-AEGdeTbgSTzVGqwV2s8MjH5mlDPz3APWVwGfftr9GDqy&upn=D3A5w5WYU1k&lmt=1410665930307178&ip=2600:3c01::f03c:91ff:fe70:35ff&sparams=dur,id,initcwndbps,ip,ipbits,itag,lmt,mime,mm,mn,ms,mv,nh,pl,ratebypass,requiressl,source,upn,expire&fexp=9416126,9420452,9422596,9423341,9423661,9423662,9424038,9424862,9425077,9425730,9426472,9426698,9427379,9428544,9428649,9429218,9429237,9429435,9429589&pl=32&dur=106.370&sver=3&expire=1455874197&nh=IgpwcjAxLnNqYzA3KgkxMjcuMC4wLjE&ratebypass=yes&mime=video/mp4&itag=18&signature=22C4633FCD1259D5F6CD1E0B54AB649982895534.378BAAC5AFAAEA737246C5CE5B92212E40B765BD&ipbits=0";
                handleSamplePlay(sample3Url, "示例3（YouTube MP4）");
                player.setShowNavIcon(false);
            } else if (id == R.id.btn_open) {
                handleOpenNewPlayer();
            } else if (id == R.id.btn_start) {
                player.start();
                OperLogUtil.msg("PlayerActivity - handleButtonClick：" + btnName + "执行完成（恢复播放）");
            } else if (id == R.id.btn_pause) {
                player.pause();
                OperLogUtil.msg("PlayerActivity - handleButtonClick：" + btnName + "执行完成（暂停播放）");
            } else if (id == R.id.btn_toggle) {
                player.toggleFullScreen();
                OperLogUtil.msg("PlayerActivity - handleButtonClick：" + btnName + "执行完成（切换全屏状态）");
            } else if (id == R.id.btn_forward) {
                player.forward(0.2f);
                OperLogUtil.msg("PlayerActivity - handleButtonClick：" + btnName + "执行完成（快进0.2倍速）");
            } else if (id == R.id.btn_back) {
                player.forward(-0.2f);
                OperLogUtil.msg("PlayerActivity - handleButtonClick：" + btnName + "执行完成（后退0.2倍速）");
            } else if (id == R.id.btn_toggle_ratio) {
                player.toggleAspectRatio();
                OperLogUtil.msg("PlayerActivity - handleButtonClick：" + btnName + "执行完成（切换宽高比）");
            } else {
                OperLogUtil.msg("PlayerActivity - handleButtonClick：未知按钮，ID：" + id);
            }
        } catch (Exception e) {
            OperLogUtil.msg("PlayerActivity - handleButtonClick：" + btnName + "执行失败，异常：" + e.getMessage()
                    + "，堆栈：" + android.util.Log.getStackTraceString(e));
            Toast.makeText(this, btnName + "操作失败", Toast.LENGTH_SHORT).show();
        }
    }

    // 处理自定义URL播放
    private void handleCustomUrlPlay() {
        EditText etUrl = findViewById(R.id.et_url);
        String inputUrl = etUrl.getText().toString().trim();

        if (TextUtils.isEmpty(inputUrl)) {
            OperLogUtil.msg("PlayerActivity - handleCustomUrlPlay：输入URL为空，取消播放");
            Toast.makeText(this, "请输入有效URL", Toast.LENGTH_SHORT).show();
            return;
        }

        currentPlayUrl = inputUrl;
        OperLogUtil.msg("PlayerActivity - handleCustomUrlPlay：开始播放自定义URL：" + currentPlayUrl);
        player.play(inputUrl);
        player.setTitle(" ");
    }

    // 处理示例URL播放
    private void handleSamplePlay(String sampleUrl, String sampleName) {
        currentPlayUrl = sampleUrl;
        ((EditText) findViewById(R.id.et_url)).setText(sampleUrl);
        OperLogUtil.msg("PlayerActivity - handleSamplePlay：开始播放" + sampleName + "，URL：" + currentPlayUrl);
        player.play(sampleUrl);
        player.setTitle(" ");
    }

    // 处理打开新播放器
    private void handleOpenNewPlayer() {
        String inputUrl = ((EditText) findViewById(R.id.et_url)).getText().toString().trim();

        if (TextUtils.isEmpty(inputUrl)) {
            OperLogUtil.msg("PlayerActivity - handleOpenNewPlayer：输入URL为空，取消打开新播放器");
            Toast.makeText(this, "请输入有效URL", Toast.LENGTH_SHORT).show();
            return;
        }

        currentPlayUrl = inputUrl;
        OperLogUtil.msg("PlayerActivity - handleOpenNewPlayer：打开新播放器播放URL：" + currentPlayUrl);
        GiraffePlayerActivity.configPlayer(this).setTitle(inputUrl).play(inputUrl);
    }

    // 获取按钮名称（用于日志）
    private String getButtonName(int viewId) {
        if (viewId == R.id.btn_play) {
            return "自定义URL播放（btn_play）";
        } else if (viewId == R.id.btn_play_sample_1) {
            return "示例1播放（btn_play_sample_1）";
        } else if (viewId == R.id.btn_play_sample_2) {
            return "示例2播放（btn_play_sample_2）";
        } else if (viewId == R.id.btn_play_sample_3) {
            return "示例3播放（btn_play_sample_3）";
        } else if (viewId == R.id.btn_open) {
            return "打开新播放器（btn_open）";
        } else if (viewId == R.id.btn_start) {
            return "开始播放（btn_start）";
        } else if (viewId == R.id.btn_pause) {
            return "暂停播放（btn_pause）";
        } else if (viewId == R.id.btn_toggle) {
            return "切换全屏（btn_toggle）";
        } else if (viewId == R.id.btn_forward) {
            return "快进（btn_forward）";
        } else if (viewId == R.id.btn_back) {
            return "后退（btn_back）";
        } else if (viewId == R.id.btn_toggle_ratio) {
            return "切换宽高比（btn_toggle_ratio）";
        }
        return "未知按钮（ID：" + viewId + "）";
    }

    // 解析播放信息描述
    private String getInfoDescription(int what, int extra) {
        switch (what) {
            case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                return "开始缓冲（等待网络数据）";
            case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                return "缓冲结束（开始播放）";
            case IMediaPlayer.MEDIA_INFO_NETWORK_BANDWIDTH:
                return "网络带宽：" + Formatter.formatFileSize(getApplicationContext(), extra) + "/s";
            case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                return "视频开始渲染（画面显示）";
            case IMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START:
                return "音频开始渲染（声音播放）";
            case IMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED:
                return "视频旋转角度改变：" + extra;
            default:
                return "未知信息（what=" + what + ", extra=" + extra + "）";
        }
    }

    // 解析错误信息描述
    private String getErrorDescription(int what, int extra) {
        switch (what) {
            case IMediaPlayer.MEDIA_ERROR_UNKNOWN:
                return "未知错误";
            case IMediaPlayer.MEDIA_ERROR_IO:
                return "IO错误（网络异常/文件不可访问）";
            case IMediaPlayer.MEDIA_ERROR_MALFORMED:
                return "媒体格式错误（文件损坏/协议错误）";
            case IMediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                return "媒体格式不支持（无对应解码器）";
            case IMediaPlayer.MEDIA_ERROR_TIMED_OUT:
                return "播放超时（网络延迟/服务器无响应）";
            case IMediaPlayer.MEDIA_ERROR_SERVER_DIED:
                return "媒体服务器异常终止";
            default:
                return "错误类型（what=" + what + ", extra=" + extra + "）";
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        OperLogUtil.msg("PlayerActivity - onPause：Activity暂停，暂停播放器");
        if (player != null) {
            player.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        OperLogUtil.msg("PlayerActivity - onResume：Activity恢复，恢复播放器");
        if (player != null) {
            player.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OperLogUtil.msg("PlayerActivity - onDestroy：Activity销毁，释放播放器资源");
        if (player != null) {
            player.onDestroy();
            player = null; // 置空避免内存泄漏
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        OperLogUtil.msg("PlayerActivity - onConfigurationChanged：配置变更（如横竖屏切换），通知播放器");
        if (player != null) {
            player.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public void onBackPressed() {
        OperLogUtil.msg("PlayerActivity - onBackPressed：用户点击返回键");
        if (player != null && player.onBackPressed()) {
            OperLogUtil.msg("PlayerActivity - onBackPressed：播放器处理返回事件（如退出全屏）");
            return;
        }
        super.onBackPressed();
        OperLogUtil.msg("PlayerActivity - onBackPressed：执行默认返回操作");
    }
}
