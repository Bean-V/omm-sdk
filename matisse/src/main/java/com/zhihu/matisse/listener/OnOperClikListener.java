package com.zhihu.matisse.listener;

import android.os.Handler;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public interface OnOperClikListener {

    public interface Callback {
        void callback();
    }

    void onOperClikListener(AppCompatActivity act, List filePaths, int type, Callback callback);

    void onShowBadgetener(TextView badgeTv);

    void onShowCopy(FrameLayout copy);
}
