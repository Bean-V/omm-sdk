package com.oortcloud.debug;

/**
 *
 * 用于webframe模块回调接口
 */

public interface IOortPluginCallback {


    /**
     * 回调
     * @param json   格式：{"code":"200", "obj":"", "msg":"成功啦"}      code=200-成功    其他-失败
     */
    void onOortFrameCallback(String json);


}