package com.zhihu.matisse.listener;


import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

/**
 *  when original is enabled , callback immediately when user check or uncheck original.
 */
public interface OnCheckedListener {
    void onCheck(boolean isChecked);
}

