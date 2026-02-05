package net.yrom.screenrecorder.rtmp;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;

import java.util.List;

/**
 * Created by lake on 16-3-16.
 * Modified by raomengyang on 17-3-12
 */
public class RESFlvData {

    // video size
//    public static final int VIDEO_WIDTH = 1280;
//    public static final int VIDEO_HEIGHT = 720;
    public static final int VIDEO_BITRATE = 500000; // 500Kbps
    public static final int FPS = 20;
    public static final int AAC_SAMPLE_RATE = 44100;
    public static final int AAC_BITRATE = 32 * 1024;

    public static final int FLV_RTMP_PACKET_TYPE_VIDEO = 9;
    public static final int FLV_RTMP_PACKET_TYPE_AUDIO = 8;
    public static final int FLV_RTMP_PACKET_TYPE_INFO = 18;
    public static final int NALU_TYPE_IDR = 5;

    public boolean droppable;

    public int dts;//解码时间戳

    public byte[] byteBuffer; //数据

    public int size; //字节长度

    public int flvTagType; //视频和音频的分类

    public int videoFrameType;

    public boolean isKeyframe() {
        return videoFrameType == NALU_TYPE_IDR;
    }

    public static int mRealSizeWidth;//手机屏幕真实宽度
    public static int mRealSizeHeight;//手机屏幕真实高度

    /**
     * 查找最接近屏幕宽高比的参数
     * @param cameraSizeList
     * @return
     */
    public static int bestVideoSize(List<Camera.Size> cameraSizeList) {
        if (cameraSizeList == null) {
            return -1;
        }
        //计算屏幕的实际分辨率的比值
        float realRatio = ((float) mRealSizeWidth / (float) mRealSizeHeight);
        int index = 0;//目标索引
        float outRatio = -1f;
        for (int i = 0; i < cameraSizeList.size(); i++) {
            if (cameraSizeList.get(i).height <= mRealSizeHeight) {//取的值高度不能高于屏
        //幕真实高度
                float ratio = (float) (cameraSizeList.get(i).width) / (float) (cameraSizeList.get(i).height);
                if (outRatio == -1f) {
                    outRatio = Math.abs(ratio / realRatio - 1);
                    index = i;
                } else {
                    if (outRatio > Math.abs(ratio / realRatio - 1)) {
                        //取绝对值小的值，即选择与屏幕分辨率最接近的值
                        index = i;
                        outRatio = Math.abs(ratio / realRatio - 1);
                    }else if (outRatio == Math.abs(ratio / realRatio - 1)) {
                        //如果有两组长宽比完全一样的，选择height比较大，小的可能会视频模糊
                        if (cameraSizeList.get(i).height >= cameraSizeList.get(index).height) {
                            index = i;
                            outRatio = Math.abs(ratio / realRatio - 1);
                        }
                    }
                }

            }
        }
        return index;
    }

    //计算屏幕真实分辨率
    public  static void getScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point outPoint = new Point();
        if (Build.VERSION.SDK_INT >= 19) {
            // 可能有虚拟按键的情况
            display.getRealSize(outPoint);
        } else {
            // 不可能有虚拟按键
            display.getSize(outPoint);
        }

        mRealSizeHeight = outPoint.y;
        mRealSizeWidth = outPoint.x;
    }

    public static void initCamera(Context context) {
        getScreenSize(context);

       Camera camera =  Camera.open();
        Camera.Parameters myParameters =camera.getParameters();
        //获取摄像头支持的分辨率
        List<Camera.Size> prviewSizeList = myParameters.getSupportedPreviewSizes();
        //获取手机支持的视频分辨率
        //List<Camera.Size> videoSizeList = myParameters.getSupportedVideoSizes();
        //获取最接近的分辨的索引
        int selectIndex = bestVideoSize(prviewSizeList);

        //设置相机预览尺寸
        //TODO 在你设置视频大小的地方也是用这里计算出来的值，以为我设置录制视频分辨率的地方不在，
        //这，不过也是取得这里的值，所以就不写了
        myParameters.setPreviewSize(prviewSizeList.get(selectIndex).width, prviewSizeList.get(selectIndex).height);

        if (camera != null){
            camera.stopPreview();
            camera.release();
            camera = null;

        }

    }
}
