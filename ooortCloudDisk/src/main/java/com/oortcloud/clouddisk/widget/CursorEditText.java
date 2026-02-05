package com.oortcloud.clouddisk.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;

/**
 * @filename:
 * @author: zzj/@date: 2021/1/23 15:39
 * @version： v1.0
 * @function： 光标指定在尾部
 */
public class CursorEditText extends AppCompatEditText {

    public CursorEditText(Context context) {
        super(context);
    }

    public CursorEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CursorEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        if (selStart == selEnd){//防止不能多选
            if(getText()==null){//判空，防止出现空指针
                setSelection(0);
            }else {
                setSelection(getText().length()); // 保证光标始终在最后面
            }

        }
    }
}
