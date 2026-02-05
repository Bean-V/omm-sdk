package com.oort.weichat.ui.message;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.oort.weichat.bean.message.ChatMessage;
import com.oort.weichat.R;
import com.oort.weichat.ui.base.BaseActivity;
import com.oort.weichat.util.HtmlUtils;
import com.oort.weichat.util.StringUtils;
import com.oort.weichat.view.ChatContentView;

public class MessageRemindActivity extends BaseActivity implements View.OnClickListener {

    private TextView mTvContext;
    private ChatMessage chatMessage;
    private boolean isGroup;
    private String toUserId;

    public static void start(Context ctx, String body, boolean isGroup, String toUserId) {
        Intent intent = new Intent(ctx, MessageRemindActivity.class);
        intent.putExtra("body", body);
        intent.putExtra("isGroup", isGroup);
        intent.putExtra("toUserId", toUserId);
        ctx.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_remind);
        String body = getIntent().getStringExtra("body");
        isGroup = getIntent().getBooleanExtra("isGroup", false);
        toUserId = getIntent().getStringExtra("toUserId");
        chatMessage = new ChatMessage(body);
        initView();
    }

    private void initView() {
        mTvContext = (TextView) findViewById(R.id.tv_content_message);
        String content = StringUtils.replaceSpecialChar(chatMessage.getContent());
        CharSequence charSequence = HtmlUtils.transform200SpanString(content, true);
        mTvContext.setText(charSequence);
        mTvContext.setMovementMethod(ScrollingMovementMethod.getInstance());

        findViewById(R.id.iv_forward).setOnClickListener(this);
        findViewById(R.id.iv_enshrine).setOnClickListener(this);
        findViewById(R.id.iv_timing).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_forward) {
            if (chatMessage.getIsReadDel()) {
                // 为阅后即焚类型的消息，不可转发
                Toast.makeText(mContext, getString(R.string.cannot_forwarded), Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(mContext, InstantMessageActivity.class);
            intent.putExtra("fromUserId", toUserId);
            intent.putExtra("messageId", chatMessage.getPacketId());
            mContext.startActivity(intent);
            ((Activity) mContext).finish();
        } else if (id == R.id.iv_enshrine) {// 收藏
            if (chatMessage.getIsReadDel()) {
                // 为阅后即焚类型的消息，不可收藏
                Toast.makeText(mContext, getString(R.string.tip_cannot_collect_burn), Toast.LENGTH_SHORT).show();
                return;
            }
            if (!TextUtils.isEmpty(chatMessage.getSignature())) {
                Toast.makeText(mContext, R.string.secure_msg_not_support_collection, Toast.LENGTH_SHORT).show();
                return;
            }
            new ChatContentView(MessageRemindActivity.this)
                    .collectionEmotion(chatMessage, true, isGroup, toUserId);
        } else if (id == R.id.iv_timing) {//todo 定时器设置
        }
    }
}
