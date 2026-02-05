package com.oortcloud.oort_zhifayi.new_version;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.oortcloud.oort_zhifayi.R;
import com.oortcloud.oort_zhifayi.new_version.home.GroupFragment;
import com.oortcloud.oort_zhifayi.new_version.home.HomeFragment;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {


    private BottomSheetBehavior<FrameLayout> bottomSheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_new);

        // 绑定按钮
        LinearLayout recordAudioButton = findViewById(R.id.ll_aduio);
        LinearLayout recordVideoButton = findViewById(R.id.ll_video);
        LinearLayout takePhotoButton = findViewById(R.id.ll_photo);
        LinearLayout logoutButton = findViewById(R.id.ll_live_picture);

        // 设置按钮点击事件
        recordAudioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "开始录音", Toast.LENGTH_SHORT).show();
                // 这里可以添加录音逻辑
            }
        });

        recordVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "开始录像", Toast.LENGTH_SHORT).show();
                // 这里可以添加录像逻辑
            }
        });

        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "拍照并上传", Toast.LENGTH_SHORT).show();
                // 这里可以添加拍照逻辑
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "退出登录", Toast.LENGTH_SHORT).show();
                // 这里可以添加退出登录逻辑
            }
        });

        ImageButton menuButton = findViewById(R.id.menuButton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu(v);
            }
        });

        Button showBottomSheetButton = findViewById(R.id.showBottomSheetButton);
        showBottomSheetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheet();
            }
        });




        FrameLayout topLayer = findViewById(R.id.top_layer);
        bottomSheetBehavior = BottomSheetBehavior.from(topLayer);

        // 设置初始状态为折叠状态
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        // 加载上层 Fragment
        loadFragment(new GroupFragment(), R.id.fragment_container);
    }

    // 加载 Fragment 的通用方法
    private void loadFragment(Fragment fragment, int containerId) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(containerId, fragment);
        transaction.commit();
    }

    private void showMenu(View view) {

        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.main_menu, popupMenu.getMenu());
        // 反射强制显示图标
        try {
            Field mPopupField = popupMenu.getClass().getDeclaredField("mPopup");
            mPopupField.setAccessible(true);
            Object menuPopupHelper = mPopupField.get(popupMenu);
            Class<?> classPopupHelper = Class.forName("com.android.internal.view.menu.MenuPopupHelper");
            Method setForceShowIcon = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
            setForceShowIcon.invoke(menuPopupHelper, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 设置菜单项点击事件
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.menu_contact) {
                    Toast.makeText(MainActivity.this, "通讯录", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemId == R.id.menu_map) {
                    Toast.makeText(MainActivity.this, "地图", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemId == R.id.menu_settings) {
                    Toast.makeText(MainActivity.this, "设置", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });

        popupMenu.show();
    }

    private void showBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }
}