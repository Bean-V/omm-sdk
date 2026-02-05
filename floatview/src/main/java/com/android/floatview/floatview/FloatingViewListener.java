package com.android.floatview.floatview;

/**
 * @Company: 奥尔特云（深圳）智慧科技有限公司
 * @Author: lukezhang
 * @Date: 2022/11/5 16:45
 */
public interface FloatingViewListener {

    void onClick(BaseFloatingView baseFloatingView);

    void onUp(BaseFloatingView baseFloatingView);

    void onDown(BaseFloatingView baseFloatingView);
}
