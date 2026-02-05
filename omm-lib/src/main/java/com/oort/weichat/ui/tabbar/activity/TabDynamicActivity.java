package com.oort.weichat.ui.tabbar.activity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.oort.weichat.R;
import com.oort.weichat.fragment.DynamicFragment;
import com.oort.weichat.fragment.dynamic.DynamicFragment_tab;
import com.oort.weichat.ui.base.BaseLoginActivity;

public class TabDynamicActivity extends BaseLoginActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_dynamic);


        // 隐藏 ActionBar
        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // 隐藏导航栏 (NavBar) 和设置状态栏颜色
        hideNavBarAndStatusBar();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new DynamicFragment_tab())
                .commit();
        }
    }
    private void hideNavBarAndStatusBar() {
        // 隐藏导航栏 (NavBar)
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);

        // 设置状态栏颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.main_color)); // 替换成你需要的颜色
        }
    }
}
