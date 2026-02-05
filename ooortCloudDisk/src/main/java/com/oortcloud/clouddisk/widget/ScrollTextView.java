package com.oortcloud.clouddisk.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * @filename:
 * @author: zzj/@date: 2020/12/17 10:21
 * @version： v1.0
 * @function：
 */

@SuppressLint("AppCompatCustomView")
public class ScrollTextView extends TextView {
    public ScrollTextView(Context context) {
        super(context);
    }
    public ScrollTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public ScrollTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    //返回textview是否处在选中的状态
    //而只有选中的textview才能够实现跑马灯效果
    @Override
    public boolean isFocused() {
        return true;
    }

}
