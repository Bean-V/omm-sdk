package com.oort.weichat.view.chatHolder;

import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.oort.weichat.bean.message.ChatMessage;
import com.oort.weichat.R;
import com.oort.weichat.util.Constants;
import com.oort.weichat.util.EncryptMessageHelper;
import com.oort.weichat.util.HtmlUtils;
import com.oort.weichat.util.PreferenceUtils;
import com.oort.weichat.util.StringUtils;
import com.oort.weichat.util.link.HttpTextView;
import com.oort.weichat.util.secure.SimpleEncryptUtil;

public class TextViewHolder extends AChatHolderInterface {

    public HttpTextView mTvContent;
    public TextView tvFireTime;
    private MotionEvent event;
    private boolean isDecrypted = false; // 标记消息是否已被解密

    @Override
    public int itemLayoutId(boolean isMysend) {
        return isMysend ? R.layout.chat_from_item_text : R.layout.chat_to_item_text;
    }

    @Override
    public void initView(View view) {
        mTvContent = view.findViewById(R.id.chat_text);
        mRootView = view.findViewById(R.id.chat_warp_view);
        if (!isMysend) {
            tvFireTime = view.findViewById(R.id.tv_fire_time);
        }
    }

    @Override
    public void fillData(ChatMessage message) {
        // 重置解密状态
        isDecrypted = false;
        
        // 修改字体功能
        int size = PreferenceUtils.getInt(mContext, Constants.FONT_SIZE) + 16;
        mTvContent.setTextSize(size);
        mTvContent.setTextColor(isMysend ?  mContext.getResources().getColor(R.color.white) : mContext.getResources().getColor(R.color.black));

        String content = StringUtils.replaceSpecialChar(message.getContent());
        
        // 使用公共方法检查密聊消息
        EncryptMessageHelper.EncryptMessageStatus encryptStatus = checkEncryptMessage(message, content);
        
        if (encryptStatus.isEncryptedMessage) {
            // 显示密聊提示
            mTvContent.setText(encryptStatus.displayText);
            mTvContent.setTextColor(encryptStatus.textColor);
            mTvContent.setClickable(encryptStatus.isClickable);
        } else if (message.getIsReadDel() && !isMysend) {// 阅后即焚
            if (!message.isGroup() && !message.isSendRead()) {
                mTvContent.setText(R.string.tip_click_to_read);
//                mTvContent.setText(R.string.tip_click_to_Chat);
                mTvContent.setTextColor(mContext.getResources().getColor(R.color.redpacket_bg));
            } else {
                // 已经查看了，当适配器再次刷新的时候，不需要重新赋值
                CharSequence charSequence = HtmlUtils.transform200SpanString(content, true);
                mTvContent.setText(charSequence);
            }
        } else {
            CharSequence charSequence = HtmlUtils.transform200SpanString(content, true);
            mTvContent.setText(charSequence);
        }
        mTvContent.setUrlText(mTvContent.getText());

        // 保存变量供点击事件使用
        final EncryptMessageHelper.EncryptMessageStatus finalEncryptStatus = encryptStatus;
        final String finalContent = content;

        mTvContent.setOnClickListener(v -> {
            // 检查是否为密聊消息需要解密查看
            if (finalEncryptStatus.isEncryptedMessage && finalEncryptStatus.isSimpleEncryptEnabled && !isDecrypted) {
                handleEncryptMessageClick(message, finalContent, mTvContent);
            } else if (finalEncryptStatus.isEncryptedMessage && !finalEncryptStatus.isSimpleEncryptEnabled) {
                // 检测到加密消息但密聊未开启，提示用户开启密聊
                handleEncryptNotEnabledClick();
            } else {
                mHolderListener.onItemClick(mRootView, TextViewHolder.this, mdata);
            }
        });
        mTvContent.setOnLongClickListener(v -> {
            mHolderListener.onItemLongClick(v, event, TextViewHolder.this, mdata);
            return true;
        });

        mTvContent.setOnTouchListener((v, e) -> {
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    event = e;
            }
            return false;
        });
    }

    @Override
    protected void onRootClick(View v) {

    }

    @Override
    public boolean enableFire() {
        return true;
    }

    @Override
    public boolean enableSendRead() {
        return true;
    }

    public void showFireTime(boolean show) {
        if (tvFireTime != null) {
            tvFireTime.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }


}
