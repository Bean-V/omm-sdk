package com.oort.weichat.luo.camfilter.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.media.ExifInterface;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.util.AttributeSet;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;

import com.oort.weichat.luo.camfilter.CameraEngine;
import com.oort.weichat.luo.camfilter.LuoGPUCameraInputFilter;
import com.oort.weichat.luo.camfilter.LuoGPUImgBaseFilter;
import com.oort.weichat.luo.camfilter.SavePictureTask;
import com.oort.weichat.luo.camfilter.utils.OpenGlUtils;
import com.oort.weichat.util.CameraUtil;
import com.oort.weichat.video.MessagePhoto;
//import com.xiaojigou.luo.xjgarsdk.XJGArSdkApi;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class LuoGLCameraView extends LuoGLBaseView {

    private static final String TAG = "LuoGLCameraView";
    List<Long> timeCounter = new ArrayList<Long>();
    int start = 0;
    int fps = 0;
    private LuoGPUCameraInputFilter cameraInputFilter;
    private SurfaceTexture surfaceTexture;
    private AlbumOrientationEventListener mAlbumOrientationEventListener;
    private int mOrientation = 0;
    // 状态标记
    private boolean isSurfaceValid = false; // SurfaceTexture是否有效
    private boolean isPreviewing = false;   // 相机是否正在预览
    private boolean isTextureValid = false; // 纹理ID是否有效

    private SurfaceTexture.OnFrameAvailableListener onFrameAvailableListener
            = new SurfaceTexture.OnFrameAvailableListener() {
        @Override
        public void onFrameAvailable(SurfaceTexture surfaceTexture) {
            if (isSurfaceValid && isPreviewing && isTextureValid) {
                requestRender();
            }
        }
    };

    public LuoGLCameraView(Context context) {
        this(context, null);
    }

    public LuoGLCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.getHolder().addCallback(this);
        scaleType = ScaleType.CENTER_CROP;
//        XJGArSdkApi.XJGARSDKReleaseAllOpenglResources();

        mAlbumOrientationEventListener = new AlbumOrientationEventListener(context, SensorManager.SENSOR_DELAY_NORMAL);
        if (mAlbumOrientationEventListener.canDetectOrientation()) {
            mAlbumOrientationEventListener.enable();
        } else {
            Log.e(TAG, "不能获取Orientation");
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);
        resetRenderState(); // 重置所有状态

        // 初始化过滤器
        if (cameraInputFilter == null) {
            cameraInputFilter = new LuoGPUCameraInputFilter();
        }
        cameraInputFilter.init();
        checkGLError("cameraInputFilter.init");

        if (filter == null) {
            filter = new LuoGPUImgBaseFilter();
        }
        filter.init();
        checkGLError("filter.init");

        // 重新创建外部纹理（关键：确保与当前GL上下文关联）
        recreateTexture();
        if (isTextureValid && textureId != OpenGlUtils.NO_TEXTURE) {
            // 创建SurfaceTexture并关联纹理
            surfaceTexture = new SurfaceTexture(textureId);
            surfaceTexture.setOnFrameAvailableListener(onFrameAvailableListener);
            isSurfaceValid = true;
            Log.d(TAG, "SurfaceTexture创建成功，纹理ID: " + textureId);
        } else {
            Log.e(TAG, "纹理初始化失败，无法创建SurfaceTexture");
        }
    }

    // 重新创建外部纹理（确保有效性）
    private void recreateTexture() {
        // 释放旧纹理
        if (textureId != OpenGlUtils.NO_TEXTURE) {
            int[] textures = {textureId};
            GLES20.glDeleteTextures(1, textures, 0);
            checkGLError("删除旧纹理");
        }
        // 创建新的外部纹理（必须使用GL_TEXTURE_EXTERNAL_OES）
        textureId = OpenGlUtils.getExternalOESTextureID();
        isTextureValid = (textureId != OpenGlUtils.NO_TEXTURE);
        if (isTextureValid) {
            // 验证纹理目标是否正确（外部纹理必须绑定到GL_TEXTURE_EXTERNAL_OES）
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
            GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
            GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
            GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0); // 解除绑定
            checkGLError("配置新纹理");
            Log.d(TAG, "外部纹理创建成功，ID: " + textureId);
        } else {
            Log.e(TAG, "创建外部纹理失败");
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);
        checkGLError("onSurfaceChanged开始");

        // 停止预览并重新打开相机
        if (isPreviewing) {
            CameraEngine.stopPreview();
            isPreviewing = false;
        }
        openCamera();
        checkGLError("onSurfaceChanged结束");
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);
        // 三重状态检查：SurfaceTexture有效 + 相机预览中 + 纹理有效
        if (!isSurfaceValid || !isPreviewing || !isTextureValid || surfaceTexture == null) {
            Log.w(TAG, "跳过渲染：状态无效（surface=" + isSurfaceValid + ", preview=" + isPreviewing + ", texture=" + isTextureValid + "）");
            return;
        }

        try {
            // 检查纹理绑定状态
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
            int error = GLES20.glGetError();
            if (error != GLES20.GL_NO_ERROR) {
                Log.e(TAG, "更新前纹理绑定错误: ");// + GLUtils.getErrorString(error)
                GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
                return;
            }
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);

            // 更新纹理（原错误位置）
            surfaceTexture.updateTexImage();
            checkGLError("updateTexImage");

        } catch (RuntimeException e) {
            Log.e(TAG, "updateTexImage失败: " + e.getMessage());
            resetRenderState(); // 重置状态避免循环错误
            return;
        }

        // 后续渲染逻辑
        float[] mtx = new float[16];
        surfaceTexture.getTransformMatrix(mtx);
        cameraInputFilter.setTextureTransformMatrix(mtx);

        int resultTexture = cameraInputFilter.onDrawToTexture(textureId, gLCubeBuffer, gLTextureBuffer);
        checkGLError("cameraInputFilter.onDrawToTexture");

        GLES20.glViewport(0, 0, surfaceWidth, surfaceHeight);
//        int finalTexture = XJGArSdkApi.XJGARSDKRenderGLTexToGLTex(resultTexture, imageWidth, imageHeight);
        checkGLError("XJGARSDKRenderGLTexToGLTex");

        GLES20.glViewport(0, 0, surfaceWidth, surfaceHeight);
//        filter.onDrawFrame(finalTexture, filter.mGLCubeBuffer, filter.mGLTextureBuffer);
        checkGLError("filter.onDrawFrame");

        // FPS计算（保持不变）
        long timer = System.currentTimeMillis();
        timeCounter.add(timer);
        while (start < timeCounter.size() && timeCounter.get(start) < timer - 1000) {
            start++;
        }
        fps = timeCounter.size() - start;
        if (start > 100) {
            timeCounter = timeCounter.subList(start, timeCounter.size() - 1);
            start = 0;
        }
        Log.i(TAG, "fps: " + fps);

        // 帧率控制（保持不变）
        int targetFPS = 30;
        if (fps > targetFPS) {
            float targetFrameTime = 1000.f / targetFPS;
            float currentFrameTime = 1000.f / fps;
            float timeToSleep = targetFrameTime - currentFrameTime;
            if (timeToSleep > 1.0) {
                try {
                    Thread.sleep((long) timeToSleep);
                } catch (InterruptedException e) {
                    // 忽略中断
                }
            }
        }
    }

    public void openCamera() {
        if (CameraEngine.getCamera() == null) {
            CameraEngine.openCamera();
        }
        CameraEngine.CameraEngineInfo info = CameraEngine.getCameraInfo();
        if (info == null) {
            Log.e(TAG, "相机信息为空，无法启动预览");
            return;
        }

        // 调整图像尺寸（确保与相机预览尺寸匹配）
        if (info.orientation == 90 || info.orientation == 270) {
            imageWidth = info.previewHeight;
            imageHeight = info.previewWidth;
        } else {
            imageWidth = info.previewWidth;
            imageHeight = info.previewHeight;
        }
        Log.d(TAG, "相机预览尺寸: " + info.previewWidth + "x" + info.previewHeight + ", 渲染尺寸: " + imageWidth + "x" + imageHeight);

        cameraInputFilter.onInputSizeChanged(imageWidth, imageHeight);
        cameraInputFilter.initCameraFrameBuffer(imageWidth, imageHeight);
        filter.onInputSizeChanged(imageWidth, imageHeight);
        checkGLError("调整过滤器尺寸");

        adjustSize(270, false, false);

        // 绑定SurfaceTexture到相机并启动预览
        if (surfaceTexture != null && isSurfaceValid) {
            try {
                CameraEngine.startPreview(surfaceTexture);
                isPreviewing = true;
                Log.d(TAG, "相机预览已启动");
            } catch (Exception e) {
                Log.e(TAG, "启动相机预览失败: " + e.getMessage());
                isPreviewing = false;
            }
        } else {
            Log.e(TAG, "SurfaceTexture无效，无法启动预览");
            isPreviewing = false;
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
        resetRenderState();
        CameraEngine.releaseCamera();
        cameraInputFilter.destroyFramebuffers();
//        XJGArSdkApi.XJGARSDKReleaseAllOpenglResources();
        if (mAlbumOrientationEventListener != null) {
            mAlbumOrientationEventListener.disable();
        }
        Log.d(TAG, "surfaceDestroyed: 资源已释放");
    }

    // 重置所有渲染状态（关键：确保资源彻底释放）
    private void resetRenderState() {
        isSurfaceValid = false;
        isPreviewing = false;
        isTextureValid = false;

        if (surfaceTexture != null) {
            try {
                surfaceTexture.release();
            } catch (Exception e) {
                Log.e(TAG, "释放SurfaceTexture失败: " + e.getMessage());
            }
            surfaceTexture = null;
        }

        // 释放纹理
        if (textureId != OpenGlUtils.NO_TEXTURE) {
            int[] textures = {textureId};
            GLES20.glDeleteTextures(1, textures, 0);
            textureId = OpenGlUtils.NO_TEXTURE;
            checkGLError("重置时释放纹理");
        }
    }

    public void release() {
        resetRenderState();
        CameraEngine.releaseCamera();
    }

    @Override
    public void savePicture(final SavePictureTask savePictureTask) {
        CameraEngine.openCamera();
        CameraEngine.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                CameraEngine.stopPreview();
                isPreviewing = false; // 同步状态

                queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        CameraEngine.CameraEngineInfo info = CameraEngine.getCameraInfo();
                        bitmap = CameraUtil.restoreRotatedImage((info.orientation) % 360, bitmap);
                        bitmap = drawPhoto(bitmap, false);
                        if (info.isFront) {
                            bitmap = CameraUtil.turnCurrentLayer(bitmap, -1, 1);
                        }
                        GLES20.glViewport(0, 0, bitmap.getWidth(), bitmap.getHeight());
                        EventBus.getDefault().post(new MessagePhoto(bitmap));
                    }
                });

                // 重启预览并同步状态
                try {
                    CameraEngine.startPreview(surfaceTexture);
                    isPreviewing = true;
                } catch (Exception e) {
                    Log.e(TAG, "拍照后重启预览失败: " + e.getMessage());
                    isPreviewing = false;
                }
            }
        });
    }

    public int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            Log.i(TAG, "读取角度-" + orientation);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    private Bitmap drawPhoto(Bitmap bitmap, boolean isRotated) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap result = null;
//        if (isRotated)
//            result = XJGArSdkApi.XJGARSDKRenderImage(bitmap, true);
//        else
//            result = XJGArSdkApi.XJGARSDKRenderImage(bitmap, false);
        return result;
    }

    // 检查OpenGL错误并打印
    private void checkGLError(String operation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, "OpenGL错误 [" + operation + "]: ");
        }
    }

    private class AlbumOrientationEventListener extends OrientationEventListener {
        public AlbumOrientationEventListener(Context context) {
            super(context);
        }

        public AlbumOrientationEventListener(Context context, int rate) {
            super(context, rate);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
                return;
            }
            int newOrientation = ((orientation + 45) / 90 * 90) % 360;
            if (newOrientation != mOrientation) {
                mOrientation = newOrientation;
                Log.e(TAG, "onOrientationChanged: " + mOrientation);
            }
        }
    }
}
