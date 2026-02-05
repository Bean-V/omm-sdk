/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
*/
package com.plugins.mediacapture;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.system.Os;
import android.system.OsConstants;

import com.plugins.file.FileUtils;
import com.plugins.file.LocalFilesystemURL;
import com.plugins.mediacapture.PendingRequests.Request;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.LOG;
import org.apache.cordova.PermissionHelper;
import org.apache.cordova.PluginManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import sound.wave.oort.OortAudioRecordRecActivity;

public class Capture extends CordovaPlugin {

    private static final String VIDEO_3GPP = "video/3gpp";
    private static final String VIDEO_MP4 = "video/mp4";
    private static final String AUDIO_3GPP = "audio/3gpp";
    private static final String[] AUDIO_TYPES = new String[] {"audio/3gpp", "audio/aac", "audio/amr", "audio/wav"};
    private static final String IMAGE_JPEG = "image/jpeg";

    private static final int CAPTURE_AUDIO = 0;     // Constant for capture audio
    private static final int CAPTURE_IMAGE = 1;     // Constant for capture image
    private static final int CAPTURE_VIDEO = 2;     // Constant for capture video
    private static final String LOG_TAG = "Capture";

    private static final int CAPTURE_INTERNAL_ERR = 0;
//    private static final int CAPTURE_APPLICATION_BUSY = 1;
//    private static final int CAPTURE_INVALID_ARGUMENT = 2;
    private static final int CAPTURE_NO_MEDIA_FILES = 3;
    private static final int CAPTURE_PERMISSION_DENIED = 4;
    private static final int CAPTURE_NOT_SUPPORTED = 20;

    private boolean cameraPermissionInManifest;     // Whether or not the CAMERA permission is declared in AndroidManifest.xml

    private final PendingRequests pendingRequests = new PendingRequests();

    private String audioAbsolutePath;
    private String imageAbsolutePath;
    private String videoAbsolutePath;

    private String applicationId;

    @Override
    protected void pluginInitialize() {
        super.pluginInitialize();

        // CB-10670: The CAMERA permission does not need to be requested unless it is declared
        // in AndroidManifest.xml. This plugin does not declare it, but others may and so we must
        // check the package info to determine if the permission is present.

        cameraPermissionInManifest = false;
        try {
            PackageManager packageManager = this.cordova.getActivity().getPackageManager();
            String[] permissionsInPackage = packageManager.getPackageInfo(this.cordova.getActivity().getPackageName(), PackageManager.GET_PERMISSIONS).requestedPermissions;
            if (permissionsInPackage != null) {
                for (String permission : permissionsInPackage) {
                    if (permission.equals(Manifest.permission.CAMERA)) {
                        cameraPermissionInManifest = true;
                        break;
                    }
                }
            }
        } catch (NameNotFoundException e) {
            // We are requesting the info for our package, so this should
            // never be caught
            LOG.e(LOG_TAG, "Failed checking for CAMERA permission in manifest", e);
        }
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.applicationId = cordova.getContext().getPackageName();

        if (action.equals("getFormatData")) {
            JSONObject obj = getFormatData(args.getString(0), args.getString(1));
            callbackContext.success(obj);
            return true;
        }

        JSONArray options = args;

        if (action.equals("captureAudio")) {
            this.captureAudio(pendingRequests.createRequest(CAPTURE_AUDIO, options, callbackContext));
        } else if (action.equals("captureImage")) {
            this.captureImage(pendingRequests.createRequest(CAPTURE_IMAGE, options, callbackContext));
        } else if (action.equals("captureVideo")) {
            this.captureVideo(pendingRequests.createRequest(CAPTURE_VIDEO, options, callbackContext));
        } else {
            return false;
        }

        return true;
    }

    /**
     * Provides the media data file data depending on it's mime type
     *
     * @param filePath path to the file
     * @param mimeType of the file
     * @return a MediaFileData object
     */
    private JSONObject getFormatData(String filePath, String mimeType) throws JSONException {
        Uri fileUrl = filePath.startsWith("file:") ? Uri.parse(filePath) : Uri.fromFile(new File(filePath));
        JSONObject obj = new JSONObject();
        // setup defaults
        obj.put("height", 0);
        obj.put("width", 0);
        obj.put("bitrate", 0);
        obj.put("duration", 0);
        obj.put("codecs", "");

        // If the mimeType isn't set the rest will fail
        // so let's see if we can determine it.
        if (mimeType == null || mimeType.isEmpty() || "null".equals(mimeType)) {
            mimeType = FileHelper.getMimeType(fileUrl, cordova);
        }
        LOG.d(LOG_TAG, "Mime type = " + mimeType);

        if (mimeType.equals(IMAGE_JPEG) || filePath.endsWith(".jpg")) {
            obj = getImageData(fileUrl, obj);
        } else if (Arrays.asList(AUDIO_TYPES).contains(mimeType)) {
            obj = getAudioVideoData(filePath, obj, false);
        } else if (mimeType.equals(VIDEO_3GPP) || mimeType.equals(VIDEO_MP4)) {
            obj = getAudioVideoData(filePath, obj, true);
        }
        return obj;
    }

    /**
     * Get the Image specific attributes
     *
     * @param fileUrl url pointing to the file
     * @param obj represents the Media File Data
     * @return a JSONObject that represents the Media File Data
     * @throws JSONException
     */
    private JSONObject getImageData(Uri fileUrl, JSONObject obj) throws JSONException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(fileUrl.getPath(), options);
        obj.put("height", options.outHeight);
        obj.put("width", options.outWidth);
        return obj;
    }

    /**
     * Get the Image specific attributes
     *
     * @param filePath path to the file
     * @param obj represents the Media File Data
     * @param video if true get video attributes as well
     * @return a JSONObject that represents the Media File Data
     * @throws JSONException
     */
    private JSONObject getAudioVideoData(String filePath, JSONObject obj, boolean video) throws JSONException {
        MediaPlayer player = new MediaPlayer();
        try {
            player.setDataSource(filePath);
            player.prepare();
            obj.put("duration", player.getDuration() / 1000);
            if (video) {
                obj.put("height", player.getVideoHeight());
                obj.put("width", player.getVideoWidth());
            }
        } catch (IOException e) {
            LOG.d(LOG_TAG, "Error: loading video file");
        }
        return obj;
    }

    private String getTempDirectoryPath() {
        File cache = new File(cordova.getActivity().getCacheDir(), "org.apache.cordova.mediacapture");

        // Create the cache directory if it doesn't exist
        cache.mkdirs();
        return cache.getAbsolutePath();
    }

    /**
     * Sets up an intent to capture audio.  Result handled by onActivityResult()
     */
    private void captureAudio_(Request req) {
        try {
            Intent intent = new Intent(android.provider.MediaStore.Audio.Media.RECORD_SOUND_ACTION);
            String timeStamp = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
            String fileName = "cdv_media_capture_audio_" + timeStamp + ".m4a";
            File audio = new File(getTempDirectoryPath(), fileName);
            Uri audioUri = FileProvider.getUriForFile(this.cordova.getActivity(),
                    this.applicationId + ".cordova.plugin.mediacapture.provider",
                    audio);
            this.audioAbsolutePath = audio.getAbsolutePath();
            LOG.d(LOG_TAG, "Recording an audio and saving to: " + this.audioAbsolutePath);
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, MediaRecorder.OutputFormat.M;
//            intent.putExtra(MediaStore.EXTR, MediaRecorder.AudioEncoder.AAC);
//            intent.putExtra(MediaStore.EXTRA_AUDIO_QUALITY, MediaRecorder.AudioEncodingQuality.HIGH);

            this.cordova.startActivityForResult((CordovaPlugin) this, intent, req.requestCode);
        } catch (ActivityNotFoundException ex) {
            pendingRequests.resolveWithFailure(req, createErrorObject(CAPTURE_NOT_SUPPORTED, "No Activity found to handle Audio Capture."));
        }
    }

    private void captureAudio(Request req) {
        try {

            String timeStamp = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
            String fileName = "cdv_media_capture_audio_" + timeStamp + ".m4a";
            File audio = new File(getTempDirectoryPath(), fileName);
            Uri audioUri = FileProvider.getUriForFile(this.cordova.getActivity(),
                    this.applicationId + ".cordova.plugin.mediacapture.provider",
                    audio);
            this.audioAbsolutePath = audio.getAbsolutePath();
            LOG.d(LOG_TAG, "Recording an audio and saving to: " + this.audioAbsolutePath);
            Intent intent = new Intent(this.cordova.getActivity(), OortAudioRecordRecActivity.class);
            intent.setAction(android.provider.MediaStore.Audio.Media.RECORD_SOUND_ACTION);
            this.cordova.startActivityForResult((CordovaPlugin) this, intent, req.requestCode);
        } catch (ActivityNotFoundException ex) {
            pendingRequests.resolveWithFailure(req, createErrorObject(CAPTURE_NOT_SUPPORTED, "No Activity found to handle Audio Capture."));
        }
    }

    /**
     * Checks for and requests the camera permission if necessary.
     *
     * Returns a boolean which if true, signals that the permission has been granted, or that the
     * permission isn't necessary and that the action may continue as normal.
     *
     * If the response is false, then the action should stop performing, as a permission prompt
     * will be presented to the user. The action based on the request's requestCode will be invoked
     * later.
     *
     * @param req
     * @return
     */
    private boolean requestCameraPermission(Request req) {
        boolean cameraPermissionGranted = true; // We will default to true, but if the manifest
        // declares the permission, then we need to check
        // for the grant
        if (cameraPermissionInManifest) {
            cameraPermissionGranted = PermissionHelper.hasPermission(this, Manifest.permission.CAMERA);
        }

        if (!cameraPermissionGranted) {
            PermissionHelper.requestPermissions(this, req.requestCode, new String[]{Manifest.permission.CAMERA});
            return false;
        }

        return true;
    }

    /**
     * Sets up an intent to capture images.  Result handled by onActivityResult()
     */
    private void captureImage(Request req) {
        if (!requestCameraPermission(req)) {
            return;
        }

        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        String fileName = "cdv_media_capture_image_" + timeStamp + ".jpg";
        File image = new File(getTempDirectoryPath(), fileName);

        Uri imageUri = FileProvider.getUriForFile(this.cordova.getActivity(),
                this.applicationId + ".cordova.plugin.mediacapture.provider",
                image);
        this.imageAbsolutePath = image.getAbsolutePath();
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageUri);
        LOG.d(LOG_TAG, "Taking a picture and saving to: " + this.imageAbsolutePath);

        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageUri);

        this.cordova.startActivityForResult((CordovaPlugin) this, intent, req.requestCode);
    }

    /**
     * Sets up an intent to capture video.  Result handled by onActivityResult()
     */
    private void captureVideo(Request request) {
        if (!requestCameraPermission(request)) {
            return;
        }

//        Intent intent = new Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
////        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
////        String fileName = "cdv_media_capture_video_" + timeStamp + ".mp4";
////        File movie = new File(getTempDirectoryPath(), fileName);
////
////        Uri videoUri = FileProvider.getUriForFile(this.cordova.getActivity(),
////                this.applicationId + ".cordova.plugin.mediacapture.provider",
////                movie);
////        this.videoAbsolutePath = movie.getAbsolutePath();
////        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, videoUri);
////        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
////        LOG.d(LOG_TAG, "Recording a video and saving to: " + this.videoAbsolutePath);
////        intent.putExtra("android.intent.extra.durationLimit", req.duration);
//////        intent.putExtra("android.intent.extra.videoQuality", req.quality);
//////        intent.putExtra("android.intent.extra.videoWidth", 1920);  // 宽度
//////        intent.putExtra("android.intent.extra.videoHeight", 1080);
////
////        // 设置720p分辨率（宽1280，高720）
////        intent.putExtra("android.intent.extra.videoWidth", 1280);
////        intent.putExtra("android.intent.extra.videoHeight", 720);
////// 配合高质量参数，保证码率适配
////        intent.putExtra("android.intent.extra.videoQuality", 1);
////        this.cordova.startActivityForResult((CordovaPlugin) this, intent, req.requestCode);

        Intent intent = new Intent(this.cordova.getActivity(), CustomVideoRecorderActivity.class);

        // 设置录制参数
        intent.putExtra(CustomVideoRecorderActivity.EXTRA_DURATION_LIMIT, request.duration);
        intent.putExtra(CustomVideoRecorderActivity.EXTRA_VIDEO_WIDTH, 720);  // 720p宽度
        intent.putExtra(CustomVideoRecorderActivity.EXTRA_VIDEO_HEIGHT, 1280);  // 720p高度
        intent.putExtra(CustomVideoRecorderActivity.EXTRA_VIDEO_QUALITY, 1);   // 高质量

        // 启动活动并等待结果
        this.cordova.startActivityForResult((CordovaPlugin) this, intent, request.requestCode);
    }

    /**
     * Called when the video view exits.
     *
     * @param requestCode The request code originally supplied to startActivityForResult(),
     *                    allowing you to identify who this result came from.
     * @param resultCode  The integer result code returned by the child activity through its setResult().
     * @param intent      An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
     * @throws JSONException
     */
    public void onActivityResult(int requestCode, int resultCode, final Intent intent) {
        final Request req = pendingRequests.get(requestCode);

        // Result received okay
        if (resultCode == Activity.RESULT_OK) {
            Runnable processActivityResult = new Runnable() {
                @Override
                public void run() {
                    switch (req.action) {
                        case CAPTURE_AUDIO:
                            onAudioActivityResult(req, intent);
                            break;
                        case CAPTURE_IMAGE:
                            onImageActivityResult(req);
                            break;
                        case CAPTURE_VIDEO:
                            onVideoActivityResult(req, resultCode,intent);
                            break;
                    }
                }
            };

            this.cordova.getThreadPool().execute(processActivityResult);
        }
        // If canceled
        else if (resultCode == Activity.RESULT_CANCELED) {
            // If we have partial results send them back to the user
            if (req.results.length() > 0) {
                pendingRequests.resolveWithSuccess(req);
            }
            // user canceled the action
            else {
                pendingRequests.resolveWithFailure(req, createErrorObject(CAPTURE_NO_MEDIA_FILES, "Canceled."));
            }
        }
        // If something else
        else {
            // If we have partial results send them back to the user
            if (req.results.length() > 0) {
                pendingRequests.resolveWithSuccess(req);
            }
            // something bad happened
            else {
                pendingRequests.resolveWithFailure(req, createErrorObject(CAPTURE_NO_MEDIA_FILES, "Did not complete!"));
            }
        }
    }

    public void onAudioActivityResult(Request req, Intent intent) {
        Uri uri = intent.getData();

        InputStream input = null;
        OutputStream output = null;
        try {
            if (uri == null) {
                throw new IOException("Unable to open input audio");
            }

            input = this.cordova.getActivity().getContentResolver().openInputStream(uri);

            if (input == null) {
                throw new IOException("Unable to open input audio");
            }

            output = new FileOutputStream(this.audioAbsolutePath);

            byte[] buffer = new byte[getPageSize()];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
        } catch (FileNotFoundException e) {
            pendingRequests.resolveWithFailure(req, createErrorObject(CAPTURE_INTERNAL_ERR, "Error: Unable to read input audio: File not found"));
        } catch (IOException e) {
            pendingRequests.resolveWithFailure(req, createErrorObject(CAPTURE_INTERNAL_ERR, "Error: Unable to read input audio"));
        } finally {
            try {
                if (output != null) output.close();
                if (input != null) input.close();
            } catch (IOException ex) {
                pendingRequests.resolveWithFailure(req, createErrorObject(CAPTURE_INTERNAL_ERR, "Error: Unable to copy input audio"));
            }
        }

        // create a file object from the audio absolute path
        JSONObject mediaFile = createMediaFileWithAbsolutePath(this.audioAbsolutePath);


                if (mediaFile == null) {
            pendingRequests.resolveWithFailure(req, createErrorObject(CAPTURE_INTERNAL_ERR, "Error: no mediaFile created from " + this.audioAbsolutePath));
            return;
        }

        if(intent.getStringExtra("audio_text") != null){

            try {
                mediaFile.put("text",intent.getStringExtra("audio_text"));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }

        req.results.put(mediaFile);

        if (req.results.length() >= req.limit) {
            // Send Uri back to JavaScript for listening to audio
            pendingRequests.resolveWithSuccess(req);
        } else {
            // still need to capture more audio clips
            captureAudio(req);
        }
    }

    public void onImageActivityResult(Request req) {
        // create a file object from the image absolute path
        JSONObject mediaFile = createMediaFileWithAbsolutePath(this.imageAbsolutePath);
        if (mediaFile == null) {
            pendingRequests.resolveWithFailure(req, createErrorObject(CAPTURE_INTERNAL_ERR, "Error: no mediaFile created from " + this.imageAbsolutePath));
            return;
        }

        req.results.put(mediaFile);

        if (req.results.length() >= req.limit) {
            // Send Uri back to JavaScript for viewing image
            pendingRequests.resolveWithSuccess(req);
        } else {
            // still need to capture more images
            captureImage(req);
        }
    }

    public void onVideoActivityResult(Request req) {
        // create a file object from the video absolute path
        JSONObject mediaFile = createMediaFileWithAbsolutePath(this.videoAbsolutePath);
        if (mediaFile == null) {
            pendingRequests.resolveWithFailure(req, createErrorObject(CAPTURE_INTERNAL_ERR, "Error: no mediaFile created from " + this.videoAbsolutePath));
            return;
        }

        req.results.put(mediaFile);

        if (req.results.length() >= req.limit) {
            // Send Uri back to JavaScript for viewing video
            pendingRequests.resolveWithSuccess(req);
        } else {
            // still need to capture more video clips
            captureVideo(req);
        }
    }


    //private static final String CAPTURE_INTERNAL_ERR = "capture_internal_error";
    private static final int CAPTURE_FILE_NOT_FOUND_ERR = 1;// 文件未找到错误
    private static final int CAPTURE_USER_CANCELED_ERR = 2; // 用户取消错误
    // 假设这是你的 Cordova 插件类中的方法
    public void onVideoActivityResult(Request req, int resultCode, Intent intent) {
        // 1. 首先校验基础参数（避免空指针）
        if (req == null) {
            LOG.e(LOG_TAG, "onVideoActivityResult: Request 对象为空");
            return;
        }
        if (this.cordova == null || this.cordova.getActivity() == null) {
            pendingRequests.resolveWithFailure(req, createErrorObject(
                    CAPTURE_INTERNAL_ERR,
                    "Error: Cordova 上下文已销毁"
            ));
            return;
        }


        String videoPath = intent.getStringExtra(CustomVideoRecorderActivity.RESULT_VIDEO_PATH);
        // 2. 根据自定义录像的结果码处理（区分成功/取消/失败）
        if (resultCode == android.app.Activity.RESULT_OK) {
            // 2.1 校验录像文件路径（自定义录像返回的路径可能为空）
            if (videoPath == null || videoPath.trim().isEmpty()) {
                pendingRequests.resolveWithFailure(req, createErrorObject(
                        CAPTURE_INTERNAL_ERR,
                        "Error: 录像文件路径为空"
                ));
                return;
            }

            // 2.2 校验文件是否真实存在（避免路径有效但文件损坏/未创建）
            File videoFile = new File(videoPath);
            if (!videoFile.exists() || videoFile.length() <= 0) {
                pendingRequests.resolveWithFailure(req, createErrorObject(
                        CAPTURE_FILE_NOT_FOUND_ERR,
                        "Error: 录像文件不存在或为空，路径：" + videoPath
                ));
                // 可选：清理无效的空文件
                if (videoFile.exists()) {
                    boolean deleted = videoFile.delete();
                    LOG.d(LOG_TAG, "清理空录像文件: " + videoPath + "，结果：" + deleted);
                }
                return;
            }

            // 2.3 更新插件的视频绝对路径（与原逻辑保持一致）
            this.videoAbsolutePath = videoPath;

            // 2.4 创建媒体文件 JSON 对象（复用原 createMediaFileWithAbsolutePath 方法）
            JSONObject mediaFile = createMediaFileWithAbsolutePath(this.videoAbsolutePath);
            if (mediaFile == null) {
                pendingRequests.resolveWithFailure(req, createErrorObject(
                        CAPTURE_INTERNAL_ERR,
                        "Error: 无法从路径创建媒体文件对象，路径：" + this.videoAbsolutePath
                ));
                return;
            }

            // 2.5 将结果添加到请求队列，判断是否达到数量限制
            try {
                // 确保 req.results 是 JSONArray（避免初始化异常）
                if (req.results == null) {
                    req.results = new JSONArray();
                }
                req.results.put(mediaFile);
                LOG.d(LOG_TAG, "已添加录像结果，当前数量：" + req.results.length() + "，限制数量：" + req.limit);

                // 2.6 达到限制则返回成功，否则继续录制
                if (req.results.length() >= req.limit) {
                    // 通知 JavaScript 端录制完成（返回所有结果）
                    pendingRequests.resolveWithSuccess(req);
                    // 可选：重置视频路径，避免后续混淆
                    this.videoAbsolutePath = null;
                } else {
                    // 未达限制，继续录制下一段
                    LOG.d(LOG_TAG, "未达录制限制，继续录制下一段");
                    captureVideo(req); // 复用原有的 captureVideo 方法（需确保已适配自定义录像）
                }
            } catch (Exception e) {
                // 捕获 JSON 操作异常（如 put 失败）
                pendingRequests.resolveWithFailure(req, createErrorObject(
                        CAPTURE_INTERNAL_ERR,
                        "Error: 处理录像结果时发生异常：" + e.getMessage()
                ));
                LOG.e(LOG_TAG, "JSON 操作异常", e);
            }

        } else if (resultCode == android.app.Activity.RESULT_CANCELED) {
            // 3. 用户取消录制（返回取消错误）
            pendingRequests.resolveWithFailure(req, createErrorObject(
                    CAPTURE_USER_CANCELED_ERR,
                    "Error: 用户取消了录像操作"
            ));
            this.videoAbsolutePath = null; // 重置路径

        } else {
            // 4. 录制失败（如权限不足、设备不支持等）
            pendingRequests.resolveWithFailure(req, createErrorObject(
                    CAPTURE_INTERNAL_ERR,
                    "Error: 录像失败，错误码：" + resultCode
            ));
            this.videoAbsolutePath = null; // 重置路径
        }
    }

    /**
     * Creates a JSONObject that represents a File from the absolute path
     *
     * @param path the absolute path saved in FileProvider of the audio/image/video
     * @return a JSONObject that represents a File
     * @throws IOException
     */
    private JSONObject createMediaFileWithAbsolutePath(String path) {
        File fp = new File(path);
        JSONObject obj = new JSONObject();

        Class webViewClass = webView.getClass();
        PluginManager pm = null;
        try {
            Method gpm = webViewClass.getMethod("getPluginManager");
            pm = (PluginManager) gpm.invoke(webView);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            // Do Nothing
        }
        if (pm == null) {
            try {
                Field pmf = webViewClass.getField("pluginManager");
                pm = (PluginManager)pmf.get(webView);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                // Do Nothing
            }
        }
        FileUtils filePlugin = (FileUtils) pm.getPlugin("File");
        LocalFilesystemURL url = filePlugin.filesystemURLforLocalPath(fp.getAbsolutePath());

        try {
            // File properties
            obj.put("name", fp.getName());
            obj.put("fullPath", Uri.fromFile(fp));
            if (url != null) {
                obj.put("localURL", url.toString());
            }
            // Because of an issue with MimeTypeMap.getMimeTypeFromExtension() all .3gpp files
            // are reported as video/3gpp. I'm doing this hacky check of the URI to see if it
            // is stored in the audio or video content store.
            if (fp.getAbsoluteFile().toString().endsWith(".3gp") || fp.getAbsoluteFile().toString().endsWith(".3gpp")) {
                Uri data = Uri.fromFile(fp);
                if (data.toString().contains("/audio/")) {
                    obj.put("type", AUDIO_3GPP);
                } else {
                    obj.put("type", VIDEO_3GPP);
                }
            } else {
                obj.put("type", FileHelper.getMimeType(Uri.fromFile(fp), cordova));
            }

            obj.put("lastModifiedDate", fp.lastModified());
            obj.put("size", fp.length());
        } catch (JSONException e) {
            // this will never happen
            e.printStackTrace();
        }
        return obj;
    }

    private JSONObject createErrorObject(int code, String message) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("code", code);
            obj.put("message", message);
        } catch (JSONException e) {
            // This will never happen
        }
        return obj;
    }

    private void executeRequest(Request req) {
        switch (req.action) {
            case CAPTURE_AUDIO:
                this.captureAudio(req);
                break;
            case CAPTURE_IMAGE:
                this.captureImage(req);
                break;
            case CAPTURE_VIDEO:
                this.captureVideo(req);
                break;
        }
    }

    public void onRequestPermissionResult(int requestCode, String[] permissions,
                                          int[] grantResults) throws JSONException {
        Request req = pendingRequests.get(requestCode);

        if (req != null) {
            boolean success = true;
            for(int r:grantResults) {
                if (r == PackageManager.PERMISSION_DENIED) {
                    success = false;
                    break;
                }
            }

            if (success) {
                executeRequest(req);
            } else {
                pendingRequests.resolveWithFailure(req, createErrorObject(CAPTURE_PERMISSION_DENIED, "Permission denied."));
            }
        }
    }

    public Bundle onSaveInstanceState() {
        return pendingRequests.toBundle();
    }

    public void onRestoreStateForActivityResult(Bundle state, CallbackContext callbackContext) {
        pendingRequests.setLastSavedState(state, callbackContext);
    }

    /**
     * Gets the ideal buffer size for processing streams of data.
     *
     * @return The page size of the device.
     */
    private int getPageSize() {
        // Get the page size of the device. Most devices will be 4096 (4kb)
        // Newer devices may be 16kb
        long ps = Os.sysconf(OsConstants._SC_PAGE_SIZE);

        // sysconf returns a long because it's a general purpose API
        // the expected value of a page size should not exceed an int,
        // but we guard it here to avoid integer overflow just in case
        if (ps > Integer.MAX_VALUE) {
            ps = Integer.MAX_VALUE;
        }

        return (int) ps;
    }
}




