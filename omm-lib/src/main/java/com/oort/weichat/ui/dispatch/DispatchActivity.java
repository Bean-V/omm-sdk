package com.oort.weichat.ui.dispatch;

import android.os.Bundle;

import com.oort.weichat.fragment.vs.ControlFragment;
import com.oort.weichat.ui.base.BaseActivity;

import timber.log.Timber;

public class DispatchActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 注意：这里没有 setContentView, 我们直接用 Fragment 填充
        getSupportActionBar().hide();
        Timber.tag(TAG).i("onCreate: DispatchActivity");
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(android.R.id.content, new ControlFragment()) // 全屏Fragment
                    .commit();
        }
    }

}
