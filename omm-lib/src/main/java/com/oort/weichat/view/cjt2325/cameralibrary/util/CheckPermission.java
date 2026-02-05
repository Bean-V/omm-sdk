package com.oort.weichat.view.cjt2325.cameralibrary.util;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.oortcloud.basemodule.CommonApplication;

public class CheckPermission {
    public static final int STATE_RECORDING = -1;       // 录音被占用
    public static final int STATE_NO_PERMISSION = -2;   // 无录音权限
    public static final int STATE_SUCCESS = 1;          // 录音正常
    private static AudioRecord audioRecord;

    /**
     * 检测是否具有录音权限
     * @return 状态码（STATE_RECORDING/STATE_NO_PERMISSION/STATE_SUCCESS）
     */
    public static int getRecordState() {
        // 1. 计算最小缓冲区大小
        int minBuffer = AudioRecord.getMinBufferSize(
                44100,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT
        );
        if (minBuffer <= 0) {
            Log.d("CheckAudioPermission", "最小缓冲区计算失败");
            return STATE_NO_PERMISSION;
        }

        // 2. 初始化AudioRecord（使用try-with-resources思想，确保资源最终释放）
        audioRecord = null;
        try {
            // 创建AudioRecord实例
            if (ActivityCompat.checkSelfPermission(CommonApplication.getAppContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return STATE_NO_PERMISSION;
            }
            audioRecord = new AudioRecord(
                    MediaRecorder.AudioSource.DEFAULT,
                    44100,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    minBuffer * 100  // 缓冲区大小
            );

            // 检查AudioRecord是否初始化成功
            if (audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
                Log.d("CheckAudioPermission", "AudioRecord初始化失败");
                return STATE_NO_PERMISSION;
            }

            // 3. 尝试开始录音
            audioRecord.startRecording();

            // 4. 检查录音状态
            if (audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
                Log.d("CheckAudioPermission", "录音机被占用");
                return STATE_RECORDING;
            }

            // 5. 检测是否能读取到录音数据
            short[] point = new short[minBuffer];
            int readSize = audioRecord.read(point, 0, point.length);
            if (readSize <= 0) {
                Log.d("CheckAudioPermission", "录音数据为空");
                return STATE_NO_PERMISSION;
            }

            // 所有检查通过
            return STATE_SUCCESS;

        } catch (IllegalStateException e) {
            // 捕获startRecording()可能抛出的异常（如无权限）
            Log.e("CheckAudioPermission", "录音状态异常: " + e.getMessage());
            return STATE_NO_PERMISSION;
        } catch (Exception e) {
            // 捕获其他可能的异常（如设备不支持）
            Log.e("CheckAudioPermission", "录音检查失败: " + e.getMessage());
            return STATE_NO_PERMISSION;
        } finally {
            // 6. 安全释放资源（核心修复：只在初始化成功时调用stop()）
            if (audioRecord != null) {
                try {
                    // 仅当状态为"已初始化"时，才尝试停止和释放
                    if (audioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
                        // 仅当正在录音时，才调用stop()
                        if (audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
                            audioRecord.stop();
                        }
                        audioRecord.release();
                    }
                } catch (Exception e) {
                    Log.e("CheckAudioPermission", "释放资源失败: " + e.getMessage());
                } finally {
                    audioRecord = null;  // 置空，避免后续错误引用
                }
            }
        }
    }

    /**
     * 关闭音频资源（安全封装）
     */
    public static void closeAudio() {
        if (audioRecord != null) {
            try {
                if (audioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
                    if (audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
                        audioRecord.stop();
                    }
                    audioRecord.release();
                }
            } catch (Exception e) {
                Log.e("CheckAudioPermission", "关闭音频资源失败: " + e.getMessage());
            } finally {
                audioRecord = null;
            }
        }
    }

    /**
     * 检查摄像头是否可用
     * @param cameraID 摄像头ID（0：后置，1：前置）
     * @return 是否可用
     */
    public synchronized static boolean isCameraUseable(int cameraID) {
        boolean canUse = true;
        Camera mCamera = null;
        try {
            mCamera = Camera.open(cameraID);
            // 针对部分机型（如魅族MX5）的兼容性处理
            Camera.Parameters mParameters = mCamera.getParameters();
            mCamera.setParameters(mParameters);
        } catch (Exception e) {
            Log.e("CheckCameraPermission", "摄像头不可用: " + e.getMessage());
            canUse = false;
        } finally {
            if (mCamera != null) {
                try {
                    mCamera.release();
                } catch (Exception e) {
                    Log.e("CheckCameraPermission", "释放摄像头失败: " + e.getMessage());
                }
            }
            mCamera = null;
        }
        return canUse;
    }
}
