package com.oortcloud.basemodule.widget.video;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.android.iplayer.base.AbstractMediaPlayer;
import com.android.iplayer.controller.VideoController;
import com.android.iplayer.interfaces.IRenderView;
import com.android.iplayer.interfaces.IVideoController;
import com.android.iplayer.listener.OnPlayerEventListener;
import com.android.iplayer.manager.IWindowManager;
import com.android.iplayer.media.IMediaPlayer;
import com.android.iplayer.media.core.ExoPlayerFactory;
import com.android.iplayer.model.PlayerState;
import com.android.iplayer.widget.VideoPlayer;
import com.android.iplayer.widget.controls.ControWindowView;
import com.android.iplayer.widget.controls.ControlCompletionView;
import com.android.iplayer.widget.controls.ControlFunctionBarView;
import com.android.iplayer.widget.controls.ControlGestureView;
import com.android.iplayer.widget.controls.ControlLoadingView;
import com.android.iplayer.widget.controls.ControlStatusView;
import com.android.iplayer.widget.controls.ControlToolBarView;
import com.oortcloud.basemodule.R;
import com.oortcloud.basemodule.constant.AppConstant;
import com.oortcloud.basemodule.widget.video.base.BaseActivity;
import com.oortcloud.basemodule.widget.video.base.BasePresenter;
import com.oortcloud.basemodule.widget.video.base.ScreenUtils;

public class ActivityVideoPlayer extends BaseActivity {

    //private VideoPlayer mVideoPlayer;

    private int MEDIA_CORE=2;//多媒体解码器 0:系统默认 1:ijk 2:exo
    private int RENDER_CORE=0;//画面渲染器 0:TextureView 1:SurfaceView
    private VideoController mController;//控制器
    private String mUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        getSupportActionBar().hide();

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destroyPlayer();
                finish();

            }
        });

        mUrl = getIntent().getStringExtra(AppConstant.EXTRA_VIDEO_FILE_URI);

//        mVideoPlayer = (VideoPlayer) findViewById(R.id.video_player);
//        mVideoPlayer.getLayoutParams().height= getResources().getDisplayMetrics().widthPixels * 9 /16;//固定播放器高度，或高度设置为:match_parent
//        //使用SDK自带控制器+各UI交互组件
//        VideoController controller = mVideoPlayer.initController();//初始化SDK默认控制器
//        //将UI交互组件绑定到控制器(需集成：implementation 'com.github.hty527.iPlayer:widget:lastversion')
//        WidgetFactory.bindDefaultControls(controller);
//        controller.setTitle("测试地址播放");//视频标题(仅横屏状态可见)
//        //设置播放源
//        mVideoPlayer.setDataSource(mUrl);
//        //异步开始准备播放
//        mVideoPlayer.prepareAsync();
        initPlayer();
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    private void initPlayer() {
        mVideoPlayer = (VideoPlayer) findViewById(R.id.video_player);
        //findViewById(R.id.player_container).getLayoutParams().height= getResources().getDisplayMetrics().widthPixels * 9 /16;//给播放器固定一个高度
        /**
         * 如果适用自定义解码器则必须实现setOnPlayerActionListener并返回一个多媒体解码器
         */
        //自定义解码器\自定义画面渲染器，在开始播放前设置监听并返回生效
        mVideoPlayer.setOnPlayerActionListener(new OnPlayerEventListener() {

            //自定义解码器
            @Override
            public AbstractMediaPlayer createMediaPlayer() {
                if(1==MEDIA_CORE){
                    //return IjkPlayerFactory.create().createPlayer(ActivityVideoPlayer.this);//IJK解码器,需引用implementation 'com.github.hty527.iPlayer:ijk:2.0.3.1'
                    return null;
                }else if(2==MEDIA_CORE){
                    return ExoPlayerFactory.create().createPlayer(ActivityVideoPlayer.this);//EXO解码器,需引用implementation 'com.github.hty527.iPlayer:ijk:2.0.3.1'
                    //return null;
                }else{
                    return null;//返回null时,SDK内部会自动使用系统MediaPlayer解码器,自定义解码器请参考Demo中的JkMediaPlayer或ExoMediaPlayer类
                }
            }

            //自定义画面渲染器
            @Override
            public IRenderView createRenderView() {
                if(1==RENDER_CORE){
                   // return new CoustomSurfaceView(VideoPlayerActivity.this);//不推荐使用SurfaceView,SurfaceView在横竖屏切换时会有短暂黑屏及镜像失效
                    return null;
                }else{
                    return null; //返回null时,SDK内部会自动使用自定义的MediaTextureView渲染器,自定义渲染器请参考Demo中CoustomSurfaceView类
                }
            }

            @Override
            public void onPlayerState(PlayerState state, String message) {
                if(state==PlayerState.STATE_COMPLETION||state==PlayerState.STATE_RESET||state==PlayerState.STATE_STOP){
                   // if(null!=mSdkDefaultFuncation) mSdkDefaultFuncation.onReset();//播放完成后重置功能设置
                    if(null!=mMenuDialog) mMenuDialog.onReset();
                }
            }

            @Override
            public void onMute(boolean isMute) {
                //if(null!=mSdkDefaultFuncation) mSdkDefaultFuncation.updateMute(isMute,false);
            }
        });
        mVideoPlayer.setLoop(false);//是否循环播放
        mVideoPlayer.setZoomModel(IMediaPlayer.MODE_ZOOM_CROPPING);//设置视频画面渲染模式为：全屏裁剪缩放模式
        mVideoPlayer.setProgressCallBackSpaceTime(300);//设置进度条回调间隔时间(毫秒)
        mVideoPlayer.setSpeed(1.0f);//设置播放倍速(默认正常即1.0f，区间：0.5f-2.0f)
        mVideoPlayer.setMirror(false);//是否镜像显示
        mVideoPlayer.setAutoChangeOrientation(true);//是否开启重力旋转。当系统"自动旋转"开启+正在播放生效
        //mVideoPlayer.setLandscapeWindowTranslucent(true);//全屏模式下是否启用沉浸样式，默认关闭。辅以setZoomModel为IMediaPlayer.MODE_ZOOM_CROPPING效果最佳
        //mVideoPlayer.setVolume(1.0f,1.0f);//设置左右声道，0.0f(小)-1.0f(大)
        //mVideoPlayer.setPlayCompletionRestoreDirection(true);//播放器在横屏状态下播放完成是否自动还原到竖屏状态,默认自动还原到竖屏
        //mVideoPlayer.setMobileNetwork(true);//移动网络下是否允许播放网络视频,需要声明权限：<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
        //mVideoPlayer.setInterceptTAudioFocus(true);//是否监听音频焦点状态，设置为true后SDK在监听焦点丢失时自动暂停播放

        /**播放器控制器及UI交互组件**/
        mController = mVideoPlayer.initController();//给播放器设置一个控制器
        mController.setTitle("测试播放地址");//视频标题(默认视图控制器横屏可见)
        //mController.setCanTouchInPortrait(false);//竖屏状态下是否开启手势交互,内部默认允许
        //mController.showLocker(true);//横屏状态下是否启用屏幕锁功能,默认开启

        //一键绑定UI交互组件
        //WidgetFactory.bindDefaultControls(mController);

        //自定义各UI交互组件
        ControlToolBarView toolBarView=new ControlToolBarView(this);//标题栏，返回按钮、视频标题、功能按钮、系统时间、电池电量等组件
        toolBarView.setTarget(IVideoController.TARGET_CONTROL_TOOL);
        toolBarView.showBack(false);//是否显示返回按钮,仅限竖屏情况下，横屏模式下强制显示
        toolBarView.showMenus(false,true,true);//是否显示投屏\悬浮窗\功能等按钮，仅限竖屏情况下，横屏模式下强制不显示
        //监听标题栏的功能事件
        toolBarView.setOnToolBarActionListener(new ControlToolBarView.OnToolBarActionListener() {
            @Override
            public void onBack() {//仅当设置showBack(true)后并且竖屏情况下才会有回调到此
                //Logger.d(TAG,"onBack");
                onBackPressed();
            }

            @Override
            public void onTv() {
                //Logger.d(TAG,"onTv");
            }

            @Override
            public void onWindow() {
                //Logger.d(TAG,"onWindow");
                startGlobalWindow(null);
            }

            @Override
            public void onMenu() {
                //Logger.d(TAG,"onMenu");
                showMenuDialog();
            }
        });
        ControlFunctionBarView functionBarView=new ControlFunctionBarView(this);//底部时间、seek、静音、全屏功能栏
        functionBarView.showSoundMute(true,false);//启用静音功能交互\默认不静音
        ControlGestureView gestureView=new ControlGestureView(this);//手势控制屏幕亮度、系统音量、快进、快退UI交互
        ControlCompletionView completionView=new ControlCompletionView(this);//播放完成、重试
        ControlStatusView statusView=new ControlStatusView(this);//移动网络播放提示、播放失败、试看完成
        ControlLoadingView loadingView=new ControlLoadingView(this);//加载中、开始播放
        ControWindowView windowView=new ControWindowView(this);//悬浮窗窗口播放器的窗口样式
        mController.addControllerWidget(toolBarView,functionBarView,gestureView,completionView,statusView,loadingView,windowView);

        //此演示界面的其它UI交互设置
        //initSetting();

        /**开始准备播放**/
        mVideoPlayer.setDataSource(mUrl);//播放地址设置
        mVideoPlayer.prepareAsync();//开始异步准备播放
    }

    /**
     * 更多设置功能拓展
     */


    /**
     * 重新播放
     */
    private void reStartPlay() {
        if(null!=mVideoPlayer){
            mVideoPlayer.onReset();
            mVideoPlayer.setProgressCallBackSpaceTime(300);
            mVideoPlayer.getController().setTitle("测试播放地址");//视频标题(默认视图控制器横屏可见)
            mVideoPlayer.setDataSource(mUrl);//播放地址设置 URL4惊奇队长
            mVideoPlayer.prepareAsync();//开始异步准备播放
        }
    }


    public static void start(Context cox, String url){

        Intent in = new Intent(cox, ActivityVideoPlayer.class);
        in.putExtra(AppConstant.EXTRA_VIDEO_FILE_URI,url);
        cox.startActivity(in);
    }


    protected void startGlobalWindow(Object customParams) {
        if(null!=mVideoPlayer){
            /**
             * 如需关心点击播放器事件,则需要在自己的Application中注册IWindowManager中的监听器setWindowActionListener
             * 如果点击悬浮窗口播放器打开自己的Activity时有自定义参数，可调用IWindowManager中的coustomParams传入自定义参数，方便在收到悬浮窗点击事件后的Activity跳转
             */
//            //示例代码：
//            IWindowManager.getInstance().setOnWindowActionListener(new OnWindowActionListener() {
//                @Override
//                public void onMovie(float x, float y) {
//
//                }
//
//                @Override
//                public void onClick(BasePlayer basePlayer, Object coustomParams) {
//                    Logger.d(TAG,"onClick-->coustomParams:"+coustomParams);
//                    Intent intent=new Intent(context, WindowGlobalPlayerActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    intent.putExtra("is_global","1");//是否接收并继续播放悬浮窗口的视频
//                    intent.putExtra("extra", (String) coustomParams);//示例GlobalWindowPlayerActivity中传入的是字符串,所以这里可以强转
//                    context.startActivity(intent);
//                }
//
//                @Override
//                public void onClose() {
//                    Logger.d(TAG,"onClose-->");
//                    IWindowManager.getInstance().quitGlobaWindow();//关闭悬浮窗播放器窗口
//                }
//            });
            boolean globalWindow = mVideoPlayer.startGlobalWindow(ScreenUtils.getInstance().dpToPxInt(3), Color.parseColor("#99000000"));
            //Logger.d(TAG,"startGoableWindow-->globalWindow:"+globalWindow);
            if(globalWindow){
                IWindowManager.getInstance().setCustomParams(customParams);
                forbidCycle();
                //跳转后设置自定义参数，将在onClick中回调
                finish();//开启悬浮窗必须关闭Activity
            }
        }
    }
}