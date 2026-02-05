package sound.wave.oort;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.oortcloud.basemodule.utils.OperLogUtil;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OortAudioRecordRecActivity extends AppCompatActivity {
    private static final String TAG = "VoiceRecorder";
    private static final int SAMPLE_RATE = 16000;
    private static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(
            SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
    private static final int SILENCE_THRESHOLD = 800;
    private static final int AUTO_SUBMIT_INTERVAL = 1000; // 每秒提交一次
    private static final int SILENCE_DURATION_MS = 500;
    private static final int PERMISSION_REQUEST_CODE = 1001;
    private static final String SERVER_URL = "http://oort.oortcloudsmart.com:21310/oort/oortcloud-ai";//"http://192.168.50.188";
    private static final String API_KEY = "app-mvnZODFfu2vVi68uXK3tAu91";

    private Button startButton;
    private TextView resultText;
    private WavePlayerView wavePlayerView; // 自定义波形视图
    private AudioRecord audioRecord;
    private ExecutorService executorService;
    private ExecutorService executorService_recon;
    private Handler mainHandler;
    private boolean isRecording = false;

    // 录音文件管理
    private File mainAudioFile;
    private BufferedOutputStream mainOutputStream;

    private File combinAudioFile;
    private BufferedOutputStream combinOutputStream;
    private File currentSubmitFile;
    private long lastVoiceTime;
    private boolean isInSilentPeriod = false;
    private int submitCount = 0;
    // 录音状态变量      // 上次检测到语音的时间
    private long lastSubmitTime = 0;
    // 录音状态变量// 是否处于静音期
    private int paragraphCount = 0;          // // 主输出流
    private String currentText = "";
    private String lastFinishText = "";
    private String allText = "";  // 当前段落文本
    private LinearLayout resultContainer;

    private TextView timerTextView;
    private boolean ISCPU = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aduio_record_rec);

//        // 查找 Toolbar 实例
//        Toolbar toolbar = findViewById(R.id.toolbar);
//
//        // 设置 Toolbar 为 ActionBar
//        if (toolbar != null) {
//            setSupportActionBar(toolbar);

        // 获取 ActionBar 实例并配置
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                // 隐藏默认标题
                actionBar.hide();
                // 显示返回按钮
            }
        //Objects.requireNonNull(getSupportActionBar()).hide();

//
//            // 使用 Toolbar 设置自定义标题
//            toolbar.setTitle("文件");



        // 让内容延伸到状态栏（API 21+）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            );
            getWindow().setStatusBarColor(Color.TRANSPARENT); // 状态栏透明
        }

//        tb.setNavigationIcon(com.sentaroh.android.upantool.R.mipmap.ic_fm_back);
//
//        tb.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//
//        });
        handler = new Handler();

        startButton = findViewById(R.id.btn_start);
        resultText = findViewById(R.id.tv_result);
        timerTextView = findViewById(R.id.timer_text_view);
        wavePlayerView = findViewById(R.id.wavePlayerView); // 初始化自定义波形视图
        resultContainer = findViewById(R.id.resultContainer);

        executorService = Executors.newSingleThreadExecutor();
        executorService_recon = Executors.newSingleThreadExecutor();;
        mainHandler = new Handler(Looper.getMainLooper());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            REQUIRED_PERMISSIONS = new String[]{
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.READ_MEDIA_AUDIO
            };
        } else {
            REQUIRED_PERMISSIONS = new String[]{
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
        }

        startButton.setOnClickListener(v -> {
            if (isRecording) {
                stopRecording();
            } else {
                if (checkAndRequestPermissions()) {
                    startRecording();
                } else {
                    //requestPermissions();

                    XXPermissions.with(this)
                            .permission(Permission.RECORD_AUDIO)
                            .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                            .request(new OnPermissionCallback() {

                                @Override
                                public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                                    if (!allGranted) {
                                        return;
                                    }


                                    startRecording();

                                }
                            });
                }
            }
        });
        // 首次启动时检查权限
        checkAndRequestPermissions();



        findViewById(R.id.tv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    private boolean checkAndRequestPermissions() {


        if (!XXPermissions.isGrantedPermissions(this, REQUIRED_PERMISSIONS)) {
            XXPermissions.with(this)
                    .permission(REQUIRED_PERMISSIONS)
                    .request(new OnPermissionCallback() {
                        @Override
                        public void onGranted(@NonNull List<String> permissions, boolean allGranted) {

                        }
                    });
        }

        return  XXPermissions.isGrantedPermissions(this, REQUIRED_PERMISSIONS);




    }



    private void scrollToBottom() {
        ScrollView scrollView = findViewById(R.id.scrollView);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                // 滚动到底部
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }


    private boolean isPlaying = false;

    private Handler handler;
    private long startTime;

    private boolean isTimerRunning = false;
    private int secondsElapsed = 0;
    // 计时器更新任务
    private final Runnable updateTimerRunnable = new Runnable() {
        @Override
        public void run() {
            if (isTimerRunning) {
                secondsElapsed++;

                // 直接计算时、分、秒
                int hours = secondsElapsed / 3600;
                int minutes = (secondsElapsed % 3600) / 60;
                int seconds = secondsElapsed % 60;

                // 格式化显示
                String time = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
                timerTextView.setText(time);
                handler.postDelayed(this, 1000);
            }
        }
    };

    // 检查权限
    private boolean checkPermissions() {
        int recordPerm = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        int storagePerm = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return recordPerm == PackageManager.PERMISSION_GRANTED && storagePerm == PackageManager.PERMISSION_GRANTED;
    }

    // 请求权限
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_CODE);
    }
    private boolean permissionsGranted = false;
    private String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE
    };
    // 检查是否拥有所有需要的权限
    private boolean hasAllPermissions() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    // 请求所需权限
    private void requestNeededPermissions() {
        // 先检查是否有需要解释的权限
        if (shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO) ||
                shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // 向用户解释为什么需要这些权限
            new AlertDialog.Builder(this)
                    .setTitle("权限请求")
                    .setMessage("需要录音和存储权限才能使用录音功能，以便保存和识别您的录音")
                    .setPositiveButton("确定", (dialog, which) -> {
                        // 解释后请求权限
                        ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSION_REQUEST_CODE);
                    })
                    .setNegativeButton("取消", null)
                    .show();
        } else {
            // 直接请求权限
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSION_REQUEST_CODE);
        }
    }

    // 权限请求回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (allGranted) {
                // 权限已授予，可以开始录音
                showToast("权限已授予，现在可以开始录音");
                // 这里可以添加自动开始录音的逻辑
            } else {
                // 权限被拒绝，提示用户
                showToast("您拒绝了必要的权限，无法使用录音功能");

                // 提供引导用户到设置页面开启权限的选项
                new AlertDialog.Builder(this)
                        .setTitle("权限被拒绝")
                        .setMessage("若要使用录音功能，需要在设置中开启相关权限")
                        .setPositiveButton("前往设置", (dialog, which) -> {
                            // 打开应用设置页面
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        }
    }

    // 开始录音
    private void startRecording() {
        try {
            // 创建主录音文件
            mainAudioFile = new File(getExternalCacheDir().getAbsolutePath() + "/"+UUID.randomUUID().toString()+".wav");
            mainOutputStream = new BufferedOutputStream(new FileOutputStream(mainAudioFile));


            combinAudioFile = new File(getExternalCacheDir().getAbsolutePath() + "/"+UUID.randomUUID().toString()+"_combine.wav");
            combinOutputStream = new BufferedOutputStream(new FileOutputStream(combinAudioFile));
            writeWavHeader(combinOutputStream,SAMPLE_RATE, 1, 16);

            writeWavHeader(SAMPLE_RATE, 1, 16);
            // 初始化提交文件
            currentSubmitFile = mainAudioFile;
            lastVoiceTime = System.currentTimeMillis();
            isInSilentPeriod = false;
            submitCount = 0;
            resultText.setText("正在录音...（3秒无语音后自动提交）");

            // 启动计时器
            startTime = System.currentTimeMillis();
            isTimerRunning = true;
            handler.postDelayed(updateTimerRunnable, 0);

            // 初始化AudioRecord
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            audioRecord = new AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    SAMPLE_RATE,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    BUFFER_SIZE
            );

            if (audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
                showToast("AudioRecord初始化失败");
                return;
            }

            audioRecord.startRecording();
            isRecording = true;
            startButton.setText("停止录音");

            // 启动录音线程（包含波形更新）
            executorService.execute(new RecordingRunnable());

        } catch (IOException e) {
            OperLogUtil.e(TAG, "录音初始化失败", e);
            showToast("录音初始化失败：" + e.getMessage());
        }
    }

    // 停止录音
    private void stopRecording() {
        isRecording = false;
        if (audioRecord != null) {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        }
        if (mainOutputStream != null) {
            try {
                long dataLength = mainAudioFile.length() - WAV_HEADER_SIZE;
                updateWavHeader(mainAudioFile, dataLength);
                mainOutputStream.close();
                currentSubmitFile = mainAudioFile;
                submitCurrentSegment(); // 提交最后一段
            } catch (IOException e) {
                OperLogUtil.e(TAG, "关闭文件失败", e);
            }
        }


        if (combinOutputStream != null) {
            try {
                long dataLength = combinAudioFile.length() - WAV_HEADER_SIZE;
                updateWavHeader(combinAudioFile, dataLength);
                combinOutputStream.close();

                //submitCurrentSegment(); // 提交最后一段
            } catch (IOException e) {
                OperLogUtil.e(TAG, "关闭文件失败", e);
            }
        }
        startButton.setText("开始录音");
        resultText.append("\n\n录音已停止");
        wavePlayerView.stopListening(); // 停止波形动画


        finishRecording();
    }

    boolean needreset = false;

    boolean requestDone = true;
    // 重置录音状态，开始新的录音段
    private synchronized void resetRecording() {
        try {

            lastFinishText = allText;
            // 关闭当前流
            if (mainOutputStream != null) {


                mainOutputStream.close();
            }

            // 创建新的主录音文件
            mainAudioFile = new File(getExternalCacheDir().getAbsolutePath() + "/"+ UUID.randomUUID().toString()+".wav");
            mainOutputStream = new BufferedOutputStream(new FileOutputStream(mainAudioFile));

            // 写入WAV文件头
            writeWavHeader(SAMPLE_RATE, 1, 16);
            needreset = false;

            Log.d(TAG, "开始新的录音段: " + mainAudioFile.getName());
        } catch (IOException e) {
            OperLogUtil.e(TAG, "重置录音失败", e);
        }
    }

    // 提交当前片段
    private void submitCurrentSegment() {
        if (currentSubmitFile == null || !currentSubmitFile.exists() || currentSubmitFile.length() < 50) return;

        requestDone = false;
        submitCount++;
        executorService_recon.execute(() -> {


            String result = "";
            if(ISCPU) {
                AudioUploader uploader = new AudioUploader();
                String filePath = "path/to/your/audio.wav"; // 替换为实际文件路径

                try {
                    // 上传文件
                    String fileId = uploader.uploadFile(currentSubmitFile.getPath());
                    System.out.println("文件上传成功，fileId: " + fileId);

                    if (fileId != null) {
                        // 调用识别API
                        result = uploader.recognizeAudio(fileId);
                        System.out.println("识别结果: " + result);
                    }
                } catch (IOException e) {
                    System.err.println("操作失败: " + e.getMessage());
                    e.printStackTrace();
                }
            }else {
                result = uploadAndRecognize(currentSubmitFile);
            }

            OperLogUtil.e(TAG, "submitCurrentSegment: " + result);
            currentText = result;
            allText = lastFinishText + currentText;
            requestDone = true;
            mainHandler.post(() -> {

                if (currentText != null) {
                    // 更新当前文本




                    // 更新UI（使用最后一个TextView）
                    mainHandler.post(() -> updateCurrentTextView());
                }
                if (currentText != null) {
                    resultText.append("\n\n第" + submitCount + "段识别结果：" + currentText);
                } else {
                    resultText.append("\n\n第" + submitCount + "段识别失败");
                }

                if(needreset){
                    resetRecording();
                }
            });
        });
    }

    private void updateCurrentTextView() {
        int childCount = resultContainer.getChildCount();
        if (childCount > 0) {
            // 获取最后一个TextView（当前正在更新的段落）
            View lastChild = resultContainer.getChildAt(childCount - 1);
//            if (lastChild instanceof TextView) {
//                TextView currentView = (TextView) lastChild;
//                currentView.setText("段落 " + paragraphCount + ": " + currentText);
//            }

            TextView currentView = (TextView) lastChild;

            currentView.setText(allText);
            scrollToBottom();
        } else {
            // 如果没有TextView，创建第一个
            TextView firstView = new TextView(this);
            firstView.setTextSize(14);
            firstView.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));
            firstView.setPadding(12, 12, 12, 12);
            firstView.setText(currentText);
            resultContainer.addView(firstView);
        }
    }
    // 创建新的提交文件
    private void createNewSubmitFile() {
        try {
            if (mainOutputStream != null) {
                mainOutputStream.flush();

                // 更新当前文件的WAV头
                long dataLength = mainAudioFile.length() - WAV_HEADER_SIZE;
                updateWavHeader(mainAudioFile, dataLength);
            }
            File newFile = new File(getExternalCacheDir().getAbsolutePath() + "/"+"submit_" + submitCount+mainAudioFile.getName());
            copyFile(mainAudioFile, newFile);
            currentSubmitFile = newFile;
            Log.d(TAG, "创建新提交文件：" + newFile.getName());
        } catch (Exception e) {
            OperLogUtil.e(TAG, "创建提交文件失败", e);
            showToast("创建提交文件失败：" + e.getMessage());
        }
    }

    // 复制文件
    private void copyFile(File source, File dest) {
        try (FileInputStream in = new FileInputStream(source);
             FileOutputStream out = new FileOutputStream(dest)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            OperLogUtil.e(TAG, "复制文件失败", e);
        }
    }


    private String uploadAndRecognize(File audioFile) {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.get("audio/m4a");

        // 构建Multipart请求体
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", audioFile.getName(),
                        RequestBody.create(audioFile, mediaType))
                .addFormDataPart("accessToken", "123")
                .addFormDataPart("ContentType", "audio/mp3")
                .build();

        // 构建请求
        Request request = new Request.Builder()
                .url(SERVER_URL + "/api/v1/audio_to_text")
                .header("Authorization", "Bearer " + API_KEY)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String result = response.body().string();
                JSONObject json = new JSONObject(result);
                JSONObject data = json.getJSONObject("data");
                String text = data.getString("text");
                if(text != null) {
                    return unescapeUnicode(text);
                }else{
                    return "";
                }
            } else {
                OperLogUtil.e(TAG, "API请求失败：" + response.code());
                return null;
            }
        } catch (Exception e) {
            OperLogUtil.e(TAG, "API调用失败", e);


            return null;
        }
    }
    // API调用（使用OkHttp）
    private String uploadAndRecognize_(File audioFile) {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.get("audio/m4a");

        // 构建Multipart请求体
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", audioFile.getName(),
                        RequestBody.create(audioFile, mediaType))
                .build();

        // 构建请求
        Request request = new Request.Builder()
                .url(SERVER_URL + "/v1/audio-to-text")
                .header("Authorization", "Bearer " + API_KEY)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String result = response.body().string();
                JSONObject json = new JSONObject(result);
                String text = json.getString("text");
                if(text != null) {
                    return unescapeUnicode(text);
                }else{
                    return "";
                }
            } else {
                OperLogUtil.e(TAG, "API请求失败：" + response.code());
                return null;
            }
        } catch (Exception e) {
            OperLogUtil.e(TAG, "API调用失败", e);
            return null;
        }
    }

    // Unicode转字符串（保持不变）
    private String unescapeUnicode(String str) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < str.length()) {
            char c = str.charAt(i);
            if (c == '\\' && i + 1 < str.length() && str.charAt(i + 1) == 'u') {
                try {
                    sb.append((char) Integer.parseInt(str.substring(i + 2, i + 6), 16));
                    i += 6;
                } catch (NumberFormatException e) {
                    sb.append(c);
                    i++;
                }
            } else {
                sb.append(c);
                i++;
            }
        }
        return sb.toString();
    }

    // 显示Toast
//    private void showToast(String msg) {
//        mainHandler.post(() -> Toast.makeText(OortAudioRecordRecActivity.this, msg, Toast.LENGTH_SHORT).show());
//    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    // 录音线程（包含波形更新）
    private class RecordingRunnable implements Runnable {
        @Override
        public void run() {
            short[] buffer = new short[BUFFER_SIZE / 2];

            try {
                wavePlayerView.startListening();
                lastVoiceTime = System.currentTimeMillis();
                lastSubmitTime = System.currentTimeMillis();

                while (isRecording) {
                    if(needreset){
                        continue;
                    }
                    int shortsRead = audioRecord.read(buffer, 0, buffer.length);
                    if (shortsRead > 0) {
                        // 写入主文件
                        byte[] byteBuffer = shortArrayToByteArray(buffer, shortsRead);
                        mainOutputStream.write(byteBuffer);
                        mainOutputStream.flush();
                        combinOutputStream.write(byteBuffer);
                        combinOutputStream.flush();

                        // 检测音量
                        double volume = calculateVolume(buffer, shortsRead);
                        boolean hasVoice = volume >= SILENCE_THRESHOLD;

                        if (hasVoice) {
                            lastVoiceTime = System.currentTimeMillis();
                            if (isInSilentPeriod) {
                                Log.d(TAG, "检测到语音恢复，开始新的录音段");
                                isInSilentPeriod = false;
                                // resetRecording(); // 重置录音状态，开始新段

                                if(requestDone){
                                    resetRecording();
                                }else {
                                    needreset = true;
                                }
                            }
                        } else {
                            // 检查是否进入静默期
                            long currentTime = System.currentTimeMillis();
                            if (!isInSilentPeriod && (currentTime - lastVoiceTime) > SILENCE_DURATION_MS) {
                                Log.d(TAG, "进入静默期，开始新的录音段");
                                isInSilentPeriod = true;
                                fixCurrentParagraph();
                                // resetRecording(); // 重置录音状态，开始新段
                                if(requestDone){
                                    resetRecording();
                                }else {
                                    needreset = true;
                                }
                            }
                        }

                        // 每秒自动提交当前片段
                        long currentTime = System.currentTimeMillis();
                        if (currentTime - lastSubmitTime >= AUTO_SUBMIT_INTERVAL && !isInSilentPeriod) {
                            createNewSubmitFile();

                            submitCurrentSegment();
                            lastSubmitTime = currentTime;
                        }
                    }
                }
            } catch (IOException e) {
                OperLogUtil.e(TAG, "录音线程错误", e);
            } finally {
                wavePlayerView.stopListening();
            }
        }

        // short转byte数组
        private byte[] shortArrayToByteArray(short[] shorts, int length) {
            byte[] bytes = new byte[length * 2];
            for (int i = 0; i < length; i++) {
                bytes[i * 2] = (byte) (shorts[i] & 0xFF);
                bytes[i * 2 + 1] = (byte) ((shorts[i] >> 8) & 0xFF);
            }
            return bytes;
        }

        // 计算音量
        private double calculateVolume(short[] buffer, int length) {
            long sum = 0;
            for (int i = 0; i < length; i++) {
                sum += buffer[i] * buffer[i];
            }
            return Math.sqrt(sum / (double) length);
        }
    }

    // 生成随机Boundary
    private String getBoundary() {
        return "Boundary-" + System.currentTimeMillis();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRecording();
        if (executorService != null) {
            executorService.shutdownNow();
        }
        if (executorService_recon != null) {
            executorService_recon.shutdownNow();
        }
        isTimerRunning = false;
        handler.removeCallbacks(updateTimerRunnable);
    }


    private void fixCurrentParagraph() {
        mainHandler.post(() -> {
            if (!allText.isEmpty()) {
//                // 创建新的TextView显示固定的段落
//                TextView paragraphView = new TextView(this);
//                paragraphView.setTextSize(14);
//                paragraphView.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));
//                paragraphView.setPadding(12, 12, 12, 12);
//                paragraphView.setText(allText);
//
//                // 添加到容器
//                resultContainer.addView(paragraphView);

                View lastChild = resultContainer.getChildAt(resultContainer.getChildCount() - 1);
//            if (lastChild instanceof TextView) {
//                TextView currentView = (TextView) lastChild;
//                currentView.setText("段落 " + paragraphCount + ": " + currentText);
//            }

                TextView currentView = (TextView) lastChild;
                if(currentView != null) {
                    currentView.setText(allText);
                }
                paragraphCount++;
                currentText = ""; // 清空当前文本
            }
        });
    }




    // 在录音完成的回调中（如stopRecording方法）
    private void finishRecording() {
        if (combinAudioFile == null || !combinAudioFile.exists()) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        // 创建返回的Intent
        Intent resultIntent = new Intent();

        // 将录音文件转换为Uri
        Uri audioUri = Uri.fromFile(combinAudioFile);
        resultIntent.setData(audioUri);

        // 添加额外信息（可选）
        resultIntent.putExtra("audio_duration", getAudioDuration(combinAudioFile));
        resultIntent.putExtra("audio_format", "wav");
        resultIntent.putExtra("audio_text", allText);

        // 设置结果并关闭Activity
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    // 获取音频文件时长（单位：毫秒）
    private long getAudioDuration(File audioFile) {
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(audioFile.getAbsolutePath());
            String durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            retriever.release();
            return durationStr != null ? Long.parseLong(durationStr) : 0;
        } catch (Exception e) {
            return 0;
        }
    }


    // WAV 文件头相关常量
    private static final int WAV_HEADER_SIZE = 44;

    // 写入 WAV 文件头
    private void writeWavHeader(int sampleRate, int channels, int bitsPerSample) {
        try {
            if (mainOutputStream == null) return;

            // 计算音频数据长度（初始为0，后续更新）
            int dataSize = 0;

            // WAV 文件头结构
            byte[] header = new byte[WAV_HEADER_SIZE];

            // ChunkID: "RIFF"
            header[0] = 'R';
            header[1] = 'I';
            header[2] = 'F';
            header[3] = 'F';

            // ChunkSize: 文件总长度 - 8
            int fileSize = 36 + dataSize;
            header[4] = (byte) (fileSize & 0xFF);
            header[5] = (byte) ((fileSize >> 8) & 0xFF);
            header[6] = (byte) ((fileSize >> 16) & 0xFF);
            header[7] = (byte) ((fileSize >> 24) & 0xFF);

            // Format: "WAVE"
            header[8] = 'W';
            header[9] = 'A';
            header[10] = 'V';
            header[11] = 'E';

            // Subchunk1ID: "fmt "
            header[12] = 'f';
            header[13] = 'm';
            header[14] = 't';
            header[15] = ' ';

            // Subchunk1Size: 16 for PCM
            header[16] = 16;
            header[17] = 0;
            header[18] = 0;
            header[19] = 0;

            // AudioFormat: 1 for PCM
            header[20] = 1;
            header[21] = 0;

            // NumChannels: 1 for mono
            header[22] = (byte) channels;
            header[23] = 0;

            // SampleRate
            header[24] = (byte) (sampleRate & 0xFF);
            header[25] = (byte) ((sampleRate >> 8) & 0xFF);
            header[26] = (byte) ((sampleRate >> 16) & 0xFF);
            header[27] = (byte) ((sampleRate >> 24) & 0xFF);

            // ByteRate = SampleRate * NumChannels * BitsPerSample/8
            int byteRate = sampleRate * channels * bitsPerSample / 8;
            header[28] = (byte) (byteRate & 0xFF);
            header[29] = (byte) ((byteRate >> 8) & 0xFF);
            header[30] = (byte) ((byteRate >> 16) & 0xFF);
            header[31] = (byte) ((byteRate >> 24) & 0xFF);

            // BlockAlign = NumChannels * BitsPerSample/8
            short blockAlign = (short) (channels * bitsPerSample / 8);
            header[32] = (byte) (blockAlign & 0xFF);
            header[33] = (byte) ((blockAlign >> 8) & 0xFF);

            // BitsPerSample
            header[34] = (byte) bitsPerSample;
            header[35] = 0;

            // Subchunk2ID: "data"
            header[36] = 'd';
            header[37] = 'a';
            header[38] = 't';
            header[39] = 'a';

            // Subchunk2Size: 音频数据长度
            header[40] = (byte) (dataSize & 0xFF);
            header[41] = (byte) ((dataSize >> 8) & 0xFF);
            header[42] = (byte) ((dataSize >> 16) & 0xFF);
            header[43] = (byte) ((dataSize >> 24) & 0xFF);

            // 写入文件头
            mainOutputStream.write(header);
            mainOutputStream.flush();
        } catch (IOException e) {
            OperLogUtil.e(TAG, "写入WAV文件头失败", e);
        }
    }


    private void writeWavHeader(OutputStream s, int sampleRate, int channels, int bitsPerSample) {
        try {
            if (s == null) return;

            // 计算音频数据长度（初始为0，后续更新）
            int dataSize = 0;

            // WAV 文件头结构
            byte[] header = new byte[WAV_HEADER_SIZE];

            // ChunkID: "RIFF"
            header[0] = 'R';
            header[1] = 'I';
            header[2] = 'F';
            header[3] = 'F';

            // ChunkSize: 文件总长度 - 8
            int fileSize = 36 + dataSize;
            header[4] = (byte) (fileSize & 0xFF);
            header[5] = (byte) ((fileSize >> 8) & 0xFF);
            header[6] = (byte) ((fileSize >> 16) & 0xFF);
            header[7] = (byte) ((fileSize >> 24) & 0xFF);

            // Format: "WAVE"
            header[8] = 'W';
            header[9] = 'A';
            header[10] = 'V';
            header[11] = 'E';

            // Subchunk1ID: "fmt "
            header[12] = 'f';
            header[13] = 'm';
            header[14] = 't';
            header[15] = ' ';

            // Subchunk1Size: 16 for PCM
            header[16] = 16;
            header[17] = 0;
            header[18] = 0;
            header[19] = 0;

            // AudioFormat: 1 for PCM
            header[20] = 1;
            header[21] = 0;

            // NumChannels: 1 for mono
            header[22] = (byte) channels;
            header[23] = 0;

            // SampleRate
            header[24] = (byte) (sampleRate & 0xFF);
            header[25] = (byte) ((sampleRate >> 8) & 0xFF);
            header[26] = (byte) ((sampleRate >> 16) & 0xFF);
            header[27] = (byte) ((sampleRate >> 24) & 0xFF);

            // ByteRate = SampleRate * NumChannels * BitsPerSample/8
            int byteRate = sampleRate * channels * bitsPerSample / 8;
            header[28] = (byte) (byteRate & 0xFF);
            header[29] = (byte) ((byteRate >> 8) & 0xFF);
            header[30] = (byte) ((byteRate >> 16) & 0xFF);
            header[31] = (byte) ((byteRate >> 24) & 0xFF);

            // BlockAlign = NumChannels * BitsPerSample/8
            short blockAlign = (short) (channels * bitsPerSample / 8);
            header[32] = (byte) (blockAlign & 0xFF);
            header[33] = (byte) ((blockAlign >> 8) & 0xFF);

            // BitsPerSample
            header[34] = (byte) bitsPerSample;
            header[35] = 0;

            // Subchunk2ID: "data"
            header[36] = 'd';
            header[37] = 'a';
            header[38] = 't';
            header[39] = 'a';

            // Subchunk2Size: 音频数据长度
            header[40] = (byte) (dataSize & 0xFF);
            header[41] = (byte) ((dataSize >> 8) & 0xFF);
            header[42] = (byte) ((dataSize >> 16) & 0xFF);
            header[43] = (byte) ((dataSize >> 24) & 0xFF);

            // 写入文件头
            s.write(header);
            s.flush();
        } catch (IOException e) {
            OperLogUtil.e(TAG, "写入WAV文件头失败", e);
        }
    }

    // 更新WAV文件头中的数据长度
    private void updateWavHeader(File file, long dataLength) {
        try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
            // 文件总长度 = 头部大小 + 数据长度
            long fileLength = WAV_HEADER_SIZE + dataLength;

            // 更新 ChunkSize (4-7字节)
            raf.seek(4);
            raf.write((int) (fileLength - 8) & 0xFF);
            raf.write(((int) (fileLength - 8) >> 8) & 0xFF);
            raf.write(((int) (fileLength - 8) >> 16) & 0xFF);
            raf.write(((int) (fileLength - 8) >> 24) & 0xFF);

            // 更新 Subchunk2Size (40-43字节)
            raf.seek(40);
            raf.write((int) dataLength & 0xFF);
            raf.write(((int) dataLength >> 8) & 0xFF);
            raf.write(((int) dataLength >> 16) & 0xFF);
            raf.write(((int) dataLength >> 24) & 0xFF);
        } catch (IOException e) {
            OperLogUtil.e(TAG, "更新WAV文件头失败", e);
        }
    }

    protected void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}



