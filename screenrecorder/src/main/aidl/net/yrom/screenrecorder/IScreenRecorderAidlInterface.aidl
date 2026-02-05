// IScreenRecorderAidlInterface.aidl
package net.yrom.screenrecorder;

// Declare any non-default types here with import statements
import net.yrom.screenrecorder.model.DanmakuBean;
interface IScreenRecorderAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void sendDanmaku( in List<DanmakuBean> danmakuBean);

      void startScreenRecord(in Intent bundleData);
}
