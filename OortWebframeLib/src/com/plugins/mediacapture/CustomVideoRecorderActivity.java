package com.plugins.mediacapture;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.apache.cordova.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class CustomVideoRecorderActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    private static final String TAG = "CustomVideoRecorder";
    private static final int REQUEST_PERMISSIONS = 1001;

    // 传递参数常量
    public static final String EXTRA_DURATION_LIMIT = "duration_limit";
    public static final String EXTRA_VIDEO_WIDTH = "video_width";
    public static final String EXTRA_VIDEO_HEIGHT = "video_height";
    public static final String EXTRA_VIDEO_QUALITY = "video_quality";
    public static final String EXTRA_ALLOW_ROTATION = "allow_rotation";
    public static final String RESULT_VIDEO_PATH = "video_path";
    public static final String RESULT_VIDEO_DURATION = "video_duration";

    // 视图控件
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private ImageView recordButton;
    private TextView timerText;
    private ImageView switchCameraBtn;
    private ImageView cancelButton;

    // 录制核心变量
    private MediaRecorder mediaRecorder;
    private Camera camera;
    private boolean isRecording = false;
    private String videoPath;
    private int durationLimit;
    private int videoWidth;
    private int videoHeight;
    private int videoQuality;
    private boolean allowRotation;
    private int recordingTime = 0;
    private Timer timer;
    private int currentCameraFacing = Camera.CameraInfo.CAMERA_FACING_BACK;
    private Handler autoStopHandler = new Handler(Looper.getMainLooper());
    private Runnable autoStopRunnable;


    private VideoView videoView;
    private ImageView btnCancel, btnConfirm, btnPlayPause;
    private SeekBar seekBar;
    private Handler handler = new Handler();
    private boolean isUserSeeking = false;

    private RelativeLayout rlplayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 加载布局
        setContentView(R.layout.activity_custom_video_recorder);
        // 绑定视图控件
        bindViews();
        // 获取传递的参数
        getIntentParams();

        // 设置沉浸式全屏
        setImmersiveFullScreen();

        // 初始化SurfaceHolder
        initSurfaceHolder();
        // 检查权限并启动预览
        checkPermissionsAndStartPreview();
    }

    /**
     * 设置沉浸式全屏
     */
    private void setImmersiveFullScreen() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            final WindowInsetsController insetsController = getWindow().getInsetsController();
            if (insetsController != null) {
                insetsController.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
                insetsController.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        } else {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }

        // 根据设置锁定屏幕方向
        if (!allowRotation) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            // 调整SurfaceView的宽高比以适应竖屏
            surfaceView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            surfaceView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
        }
    }

    /**
     * 获取Intent传递的参数
     */
    private void getIntentParams() {
        durationLimit = getIntent().getIntExtra(EXTRA_DURATION_LIMIT, 60);
        videoWidth = getIntent().getIntExtra(EXTRA_VIDEO_WIDTH, 1280);
        videoHeight = getIntent().getIntExtra(EXTRA_VIDEO_HEIGHT, 720);
        videoQuality = getIntent().getIntExtra(EXTRA_VIDEO_QUALITY, 1);
        allowRotation = getIntent().getBooleanExtra(EXTRA_ALLOW_ROTATION, false);

        // 确保时长限制合理
        if (durationLimit <= 0) durationLimit = 60;
        if (durationLimit > 300) durationLimit = 300; // 最大5分钟

        // 对于竖屏模式，调整默认视频尺寸
        if (!allowRotation) {
            videoWidth = 720;
            videoHeight = 1280;
        }
    }

    /**
     * 绑定视图控件
     */
    private void bindViews() {
        surfaceView = findViewById(R.id.surfaceView);
        recordButton = findViewById(R.id.recordButton);
        timerText = findViewById(R.id.timerText);
        switchCameraBtn = findViewById(R.id.switchCameraBtn);
        cancelButton = findViewById(R.id.cancelButton);

        timerText.setVisibility(View.GONE);

        // 录制按钮点击事件
        recordButton.setOnClickListener(v -> {

            v.animate()
                    .scaleX(0.8f)
                    .scaleY(0.8f)
                    .setDuration(150)
                    .withEndAction(() -> v.animate().scaleX(1f).scaleY(1f).setDuration(150))
                    .start();
            if (isRecording) stopRecording();
            else startRecording();
        });

        // 摄像头切换按钮点击事件
        switchCameraBtn.setOnClickListener(v -> {
            if (!isRecording) switchCamera();
            else showToast("录制中无法切换摄像头");
        });

        // 取消按钮点击事件
        cancelButton.setOnClickListener(v -> {
            if (isRecording) {
                stopRecording();
                // 删除不完整的视频文件
                deleteVideoFile();
            }
            setResult(RESULT_CANCELED);
            finish();
        });


        ///////////


        rlplayer = findViewById(R.id.rl_player);
        videoView = findViewById(R.id.videoView);
        btnCancel = findViewById(R.id.btnCancel);
        btnConfirm = findViewById(R.id.btnConfirm);
        btnPlayPause = findViewById(R.id.btnPlayPause);
        seekBar = findViewById(R.id.seekBar);


        videoPath = getIntent().getStringExtra(CustomVideoRecorderActivity.RESULT_VIDEO_PATH);

        if (videoPath != null) {
            videoView.setVideoURI(Uri.parse(videoPath));

            // 视频准备完成自动开始播放
            videoView.setOnPreparedListener(mp -> {
                videoView.start();
                btnPlayPause.setBackgroundResource(R.drawable.ic_pause);
                seekBar.setMax(videoView.getDuration());
                updateSeekBar();
            });

            // 播放完成自动重置播放按钮
            videoView.setOnCompletionListener(mp -> btnPlayPause.setBackgroundResource(R.drawable.ic_play));
        }

        // 播放/暂停按钮
        btnPlayPause.setOnClickListener(v -> {
            if (videoView.isPlaying()) {
                videoView.pause();
                btnPlayPause.setBackgroundResource(R.drawable.ic_play);
            } else {
                videoView.start();
                btnPlayPause.setBackgroundResource(R.drawable.ic_pause);
            }
        });

        // SeekBar拖动事件
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) videoView.seekTo(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isUserSeeking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isUserSeeking = false;
            }
        });

        // 取消按钮
        btnCancel.setOnClickListener(v -> {
          rlplayer.setVisibility(View.GONE);
        });

        // 确认按钮
        btnConfirm.setOnClickListener(v -> {

            Intent resultIntent = new Intent();
            resultIntent.putExtra(CustomVideoRecorderActivity.RESULT_VIDEO_PATH, videoPath);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    private void updateSeekBar() {
        if (videoView != null && !isUserSeeking) {
            seekBar.setProgress(videoView.getCurrentPosition());
        }
        handler.postDelayed(this::updateSeekBar, 500);
    }

    /**
     * 初始化SurfaceHolder
     */
    private void initSurfaceHolder() {
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder.addCallback(this);
    }

    /**
     * 检查权限并启动预览
     */
    private void checkPermissionsAndStartPreview() {
        String[] requiredPermissions = {
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        boolean hasUnGranted = false;
        for (String perm : requiredPermissions) {
            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                hasUnGranted = true;
                break;
            }
        }

        if (hasUnGranted) {
            ActivityCompat.requestPermissions(this, requiredPermissions, REQUEST_PERMISSIONS);
        } else {
            startCameraPreview();
        }
    }

    /**
     * 权限申请结果回调
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (allGranted) {
                startCameraPreview();
            } else {
                showToast("需要相机、麦克风和存储权限才能录制");
                new Handler(Looper.getMainLooper()).postDelayed(this::finish, 2000);
            }
        }
    }

    /**
     * 启动摄像头预览
     */
    private void startCameraPreview() {
        try {
            releaseCamera();

            // 检查摄像头是否可用
            if (!checkCameraHardware()) {
                showToast("设备无可用摄像头");
                finish();
                return;
            }

            camera = Camera.open(currentCameraFacing);
            if (camera == null) {
                showToast("无法打开摄像头");
                finish();
                return;
            }

            Camera.Parameters params = camera.getParameters();

            // 设置预览尺寸（适配竖屏）
            Camera.Size optimalPreviewSize = getOptimalPreviewSize(
                    params.getSupportedPreviewSizes(),
                    surfaceView.getWidth(),
                    surfaceView.getHeight()
            );
            if (optimalPreviewSize != null) {
                params.setPreviewSize(optimalPreviewSize.width, optimalPreviewSize.height);
            }

            // 设置对焦模式
            if (params.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            } else if (params.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            }

            // 设置闪光灯模式（默认关闭）
            if (params.getSupportedFlashModes() != null &&
                    params.getSupportedFlashModes().contains(Camera.Parameters.FLASH_MODE_OFF)) {
                params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            }

            camera.setParameters(params);

            // 设置正确的预览方向（修复竖屏问题）
            camera.setDisplayOrientation(getCameraRotationAngle());
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();

            // 如果支持自动对焦，触发一次对焦
            if (params.getFocusMode().equals(Camera.Parameters.FOCUS_MODE_AUTO)) {
                camera.autoFocus(null);
            }

        } catch (Exception e) {
            Log.e(TAG, "预览启动失败：" + e.getMessage(), e);
            showToast("摄像头预览失败，请重试");
            releaseCamera();
            finish();
        }
    }

    /**
     * 检查设备是否有摄像头
     */
    private boolean checkCameraHardware() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    /**
     * 计算摄像头预览旋转角度（修复竖屏问题）
     */
    private int getCameraRotationAngle() {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(currentCameraFacing, cameraInfo);

        int screenRotation = getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;

        switch (screenRotation) {
            case android.view.Surface.ROTATION_0:
                degrees = 0;
                break;
            case android.view.Surface.ROTATION_90:
                degrees = 90;
                break;
            case android.view.Surface.ROTATION_180:
                degrees = 180;
                break;
            case android.view.Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (cameraInfo.orientation + degrees) % 360;
            result = (360 - result) % 360; // 补偿镜像
        } else {
            result = (cameraInfo.orientation - degrees + 360) % 360;
        }

        return result;
    }

    /**
     * 选择最优预览尺寸（适配竖屏）
     */
    private Camera.Size getOptimalPreviewSize(List<Camera.Size> supportedSizes, int targetWidth, int targetHeight) {
        if (supportedSizes == null) return null;

        // 对于竖屏应用，我们需要交换宽高来匹配设备方向
        boolean isPortrait = !allowRotation ||
                getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

        int actualTargetWidth = isPortrait ? targetHeight : targetWidth;
        int actualTargetHeight = isPortrait ? targetWidth : targetHeight;

        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) actualTargetWidth / actualTargetHeight;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        // 首先尝试找到匹配宽高比的尺寸
        for (Camera.Size size : supportedSizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;

            int diff = Math.abs(size.height - actualTargetHeight);
            if (diff < minDiff) {
                optimalSize = size;
                minDiff = diff;
            }
        }

        // 如果没有找到匹配宽高比的尺寸，找最接近的尺寸
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : supportedSizes) {
                int diff = Math.abs(size.height - actualTargetHeight);
                if (diff < minDiff) {
                    optimalSize = size;
                    minDiff = diff;
                }
            }
        }

        return optimalSize;
    }

    /**
     * 切换前后摄像头
     */
    private void switchCamera() {
        // 检查是否有前置摄像头
        int numberOfCameras = Camera.getNumberOfCameras();
        if (numberOfCameras < 2) {
            showToast("设备只有一个摄像头，无法切换");
            return;
        }

        // 切换摄像头方向
        currentCameraFacing = (currentCameraFacing == Camera.CameraInfo.CAMERA_FACING_BACK)
                ? Camera.CameraInfo.CAMERA_FACING_FRONT
                : Camera.CameraInfo.CAMERA_FACING_BACK;

        startCameraPreview();
    }

    /**
     * 开始录制
     */
    /**
     * 开始录制
     */
    private void startRecording() {
        if (isRecording) return;

        // 1. 确认权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            showToast("缺少录制权限");
            return;
        }

        // 2. 确认 Surface 已准备好
        if (surfaceHolder == null || surfaceHolder.getSurface() == null || !surfaceHolder.getSurface().isValid()) {
            showToast("Surface未准备好");
            return;
        }

        // 3. 确认 Camera 已打开
        if (camera == null) {
            showToast("摄像头未初始化");
            startCameraPreview();
            if (camera == null) return;
        }

        // 4. 检查存储空间
        if (!checkStorageSpace()) {
            showToast("存储空间不足，无法录制视频");
            return;
        }

        // 5. 准备 MediaRecorder
        if (!prepareMediaRecorder()) {
            showToast("录制初始化失败");
            return;
        }

        // 6. 开始录制
        try {
            mediaRecorder.start();
            isRecording = true;

            timerText.setVisibility(View.VISIBLE);
            recordButton.setBackgroundResource(R.drawable.btn_record_stop);
            startTimer();

            // 自动停止
            if (durationLimit > 0) {
                autoStopRunnable = this::stopRecording;
                autoStopHandler.postDelayed(autoStopRunnable, durationLimit * 1000L);
            }

          //  recordButton.setImageResource(R.drawable.ic_record_square);


            showToast("开始录制");
        } catch (RuntimeException e) {
            Log.e(TAG, "start failed", e);
            showToast("录制失败，请重试");
            releaseMediaRecorder();
            if (camera != null) camera.lock();
            isRecording = false;
        }
    }

    /**
     * 准备 MediaRecorder
     */
    private boolean prepareMediaRecorder() {
        try {
            releaseMediaRecorder();
            mediaRecorder = new MediaRecorder();

            // 确保 Camera 已打开
            if (camera == null) {
                Log.e(TAG, "Camera为空，无法录制");
                startCameraPreview();
                if (camera == null) return false;
            }

            // 解锁 Camera
            try {
                camera.unlock();
            } catch (RuntimeException e) {
                Log.e(TAG, "Camera unlock失败", e);
                return false;
            }

            mediaRecorder.setCamera(camera);

            // 设置音视频源
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

            // 使用 CamcorderProfile 获取设备安全参数
            if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_720P)) {
                CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_720P);
                mediaRecorder.setProfile(profile);

            }else if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_480P)) {
                CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
                mediaRecorder.setProfile(profile);
            }else{
                CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_1080P);
                mediaRecorder.setProfile(profile);
            }




            // 输出文件
            videoPath = createVideoFile();
            if (videoPath == null) {
                Log.e(TAG, "视频文件创建失败");
                return false;
            }
            mediaRecorder.setOutputFile(videoPath);

            // 设置预览显示
            if (surfaceHolder != null && surfaceHolder.getSurface() != null &&
                    surfaceHolder.getSurface().isValid()) {
                mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());
            } else {
                Log.e(TAG, "Surface不可用");
                return false;
            }

            // 设置旋转角度
            int rotationAngle = getCameraRotationAngle();
            mediaRecorder.setOrientationHint(rotationAngle);

            // 准备 MediaRecorder
            mediaRecorder.prepare();

            Log.d(TAG, "MediaRecorder准备成功，文件路径：" + videoPath);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "prepareMediaRecorder失败", e);
            deleteVideoFile();
            releaseMediaRecorder();
            if (camera != null) {
                try {
                    camera.lock();
                } catch (Exception ignored) {
                }
            }
            return false;
        }
    }

    /**
     * 检查存储空间是否充足
     */
    private boolean checkStorageSpace() {
        // 检查外部存储是否可用
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            return false;
        }

        // 检查可用空间（至少需要100MB）
        File storageDir = Environment.getExternalStorageDirectory();
        long availableSpace = storageDir.getFreeSpace();
        long requiredSpace = 100 * 1024 * 1024; // 100MB

        return availableSpace >= requiredSpace;
    }

    /**
     * 准备MediaRecorder
     */


    /**
     * 获取设备支持的最佳视频尺寸（适配竖屏）
     */
    private Camera.Size getBestSupportedVideoSize(Camera.Parameters params) {
        if (camera == null) {
            return null;
        }

        try {
            // Camera.Parameters params = camera.getParameters();
            List<Camera.Size> supportedSizes = params.getSupportedVideoSizes();

            if (supportedSizes == null || supportedSizes.isEmpty()) {
                supportedSizes = params.getSupportedPreviewSizes();
            }

            if (supportedSizes == null || supportedSizes.isEmpty()) {
                return null;
            }

            // 对于竖屏模式，我们需要选择适合竖屏的尺寸
            int targetWidth = videoWidth;
            int targetHeight = videoHeight;

            if (!allowRotation) {
                // 交换宽高来匹配竖屏比例
                targetWidth = videoHeight;
                targetHeight = videoWidth;
            }

            return getOptimalPreviewSize(supportedSizes, targetWidth, targetHeight);
        } catch (RuntimeException e) {
            Log.e(TAG, "getBestSupportedVideoSize: 获取视频尺寸失败", e);
            return null;
        }
    }

    /**
     * 创建视频文件
     */
    private String createVideoFile() {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault()).format(new Date());
            String fileName = "cdv_media_capture_video_" + timeStamp + ".mp4";

            // 使用应用私有目录存储视频
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_MOVIES);
            if (storageDir == null) {
                storageDir = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_MOVIES), "MediaCapture");
            }

            // 创建目录（如果不存在）
            if (!storageDir.exists() && !storageDir.mkdirs()) {
                Log.e(TAG, "无法创建存储目录");
                return null;
            }

            // 检查目录是否可写
            if (!storageDir.canWrite()) {
                Log.e(TAG, "存储目录不可写");
                return null;
            }

            File videoFile = new File(storageDir, fileName);
            return videoFile.getAbsolutePath();
        } catch (Exception e) {
            Log.e(TAG, "创建视频文件失败: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * 停止录制
     */
    private void stopRecording() {
        if (!isRecording) return;

        // 移除自动停止的回调
        if (autoStopRunnable != null) {
            autoStopHandler.removeCallbacks(autoStopRunnable);
        }

        try {
            mediaRecorder.stop();
            showToast("录制完成");
        } catch (Exception e) {
            Log.e(TAG, "停止录制失败: " + e.getMessage(), e);
            showToast("录制停止失败");
            deleteVideoFile(); // 删除损坏的文件
            videoPath = null;
        }

        releaseMediaRecorder();
        if (camera != null) {
            camera.lock();
        }

        isRecording = false;
        recordButton.setBackgroundResource(R.drawable.btn_record_start);
        timerText.setVisibility(View.GONE);
        stopTimer();

       // recordButton.setImageResource(R.drawable.ic_record_dot);


        // 返回录制结果
        if (videoPath != null) {
//            Intent resultIntent = new Intent();
//            resultIntent.putExtra(RESULT_VIDEO_PATH, videoPath);
//            resultIntent.putExtra(RESULT_VIDEO_DURATION, recordingTime);
//            setResult(RESULT_OK, resultIntent);

//            Intent intent = new Intent(this, PreviewActivity.class);
//            intent.putExtra(RESULT_VIDEO_PATH, videoPath);
//            intent.putExtra(RESULT_VIDEO_DURATION, recordingTime);
//            startActivity(intent);
            rlplayer.setVisibility(View.VISIBLE);
        } else {
            setResult(RESULT_CANCELED);
        }
        //finish();
    }

    /**
     * 删除视频文件
     */
    private void deleteVideoFile() {
        if (videoPath != null) {
            File videoFile = new File(videoPath);
            if (videoFile.exists() && !videoFile.delete()) {
                Log.w(TAG, "无法删除视频文件: " + videoPath);
            }
            videoPath = null;
        }
    }

    /**
     * 启动计时器
     */
    private void startTimer() {
        recordingTime = 0;
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    recordingTime++;
                    updateTimerText();
                    // 显示剩余录制时间提示
                    if (durationLimit > 0 && durationLimit - recordingTime <= 5) {
                    }
                });
            }
        }, 0, 1000);
    }

    /**
     * 停止计时器
     */
    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    /**
     * 更新计时器文本
     */
    private void updateTimerText() {
        int seconds = recordingTime % 60;
        int minutes = (recordingTime / 60) % 60;
        timerText.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
    }

    /**
     * 释放MediaRecorder资源
     */
    private void releaseMediaRecorder() {
        if (mediaRecorder != null) {
            try {
                mediaRecorder.reset();
                mediaRecorder.release();
            } catch (Exception e) {
                Log.e(TAG, "释放MediaRecorder失败: " + e.getMessage(), e);
            } finally {
                mediaRecorder = null;
            }
        }
    }

    /**
     * 释放Camera资源
     */
    private void releaseCamera() {
        if (camera != null) {
            try {
                camera.stopPreview();
                camera.release();
            } catch (Exception e) {
                Log.e(TAG, "释放Camera失败: " + e.getMessage(), e);
            } finally {
                camera = null;
            }
        }
    }

    /**
     * 显示Toast消息（封装）
     */
    private void showToast(String message) {
        runOnUiThread(() -> {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // 当Surface创建后，如果权限已授予则启动预览
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startCameraPreview();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (surfaceHolder.getSurface() == null) {
            return;
        }

        if (camera != null && !isRecording) {
            try {
                camera.stopPreview();

                // 设置Camera参数以适应竖屏
                Camera.Parameters params = camera.getParameters();

                // 获取最佳预览尺寸
                Camera.Size optimalSize = getOptimalPreviewSize(
                        params.getSupportedPreviewSizes(), width, height);

                if (optimalSize != null) {
                    params.setPreviewSize(optimalSize.width, optimalSize.height);
                }

                // 设置预览方向
                camera.setDisplayOrientation(getCameraRotationAngle());
                camera.setParameters(params);
                camera.setPreviewDisplay(surfaceHolder);
                camera.startPreview();

            } catch (Exception e) {
                Log.e(TAG, "重启预览失败: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // 当Surface销毁时释放资源
        releaseMediaRecorder();
        releaseCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 暂停时释放摄像头资源（如果不在录制状态）
        if (!isRecording) {
            releaseCamera();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 恢复时重新启动预览（如果不在录制状态且有权限）
        if (!isRecording && ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startCameraPreview();
        }
        // 重新设置沉浸式模式
        setImmersiveFullScreen();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 销毁时彻底释放所有资源
        releaseMediaRecorder();
        releaseCamera();
        stopTimer();
        autoStopHandler.removeCallbacksAndMessages(null);
        handler.removeCallbacksAndMessages(null);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isRecording) {
            stopRecording();
        } else {
            setResult(RESULT_CANCELED);
            finish();
        }
    }
}