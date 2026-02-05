package com.oort.weichat.ui.backup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.oort.weichat.bean.event.EventSentChatHistory;
import com.oort.weichat.R;
import com.oort.weichat.ui.base.BaseActivity;
import com.oort.weichat.ui.tool.ButtonColorChange;
import com.oort.weichat.util.EventBusHelper;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class BackupHistoryActivity extends BaseActivity {

    public static void start(Context ctx) {
        Intent intent = new Intent(ctx, BackupHistoryActivity.class);
        ctx.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_history);

        initActionBar();

        Button btnSelectChat = findViewById(R.id.btnSelectChat);
        btnSelectChat.setOnClickListener((v) -> {
            SelectChatActivity.start(this);
        });
        ButtonColorChange.colorChange(this, btnSelectChat);
        EventBusHelper.register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void helloEventBus(final EventSentChatHistory message) {
        finish();
    }

    private void initActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        findViewById(R.id.iv_title_left).setOnClickListener((v) -> {
            onBackPressed();
        });
        TextView tvTitle = findViewById(R.id.tv_title_center);
        tvTitle.setText(getString(R.string.backup_chat_history));
    }
}
