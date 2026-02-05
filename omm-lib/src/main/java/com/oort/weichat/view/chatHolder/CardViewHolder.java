package com.oort.weichat.view.chatHolder;

import static android.view.View.GONE;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.oort.weichat.bean.message.ChatMessage;
import com.oort.weichat.helper.AvatarHelper;
import com.oort.weichat.ui.other.BasicInfoActivity;
import com.oort.weichat.R;
import com.oort.weichat.util.EncryptMessageHelper;
import com.oort.weichat.util.StringUtils;
import com.oort.weichat.util.secure.SimpleEncryptUtil;

class CardViewHolder extends AChatHolderInterface {

    ImageView ivCardImage;
    TextView tvPersonName;
    TextView tvPersonSex;
    ImageView ivUnRead;
    ViewGroup mChatCardView;
    ViewGroup mChatWrapView;
    View encryptView;

    private boolean isDecrypted = false; // 标记是否已解密
    @Override
    public int itemLayoutId(boolean isMysend) {
        return isMysend ? R.layout.chat_from_item_card : R.layout.chat_to_item_card;
    }

    @Override
    public void initView(View view) {
        ivCardImage = view.findViewById(R.id.iv_card_head);
        tvPersonName = view.findViewById(R.id.person_name);
        tvPersonSex = view.findViewById(R.id.person_sex);
        ivUnRead = view.findViewById(R.id.unread_img_view);
        mRootView = mChatCardView = view.findViewById(R.id.chat_card_view);
        mChatWrapView = view.findViewById(R.id.chat_warp_view);

    }

    @Override
    public void fillData(ChatMessage message) {
        String content = StringUtils.replaceSpecialChar(message.getContent());

        // 使用公共方法检查密聊消息
        EncryptMessageHelper.EncryptMessageStatus encryptStatus = checkEncryptMessage(message, content);

        Log.e("zq", "--CardViewHolder----------" +encryptStatus.isEncryptedMessage);
        if (encryptStatus.isEncryptedMessage) {
            // 密聊消息：显示密聊提示布局
            showEncryptTip(encryptStatus, message, content);
        } else {
            // 普通消息：显示名片内容
            showCardContent(message, content);
        }
    }

    /**
     * 显示名片内容
     */
    private void showCardContent(ChatMessage message, String content) {
        // 显示名片容器
        if (mChatCardView != null) {
            mChatCardView.setVisibility(View.VISIBLE);
        }
        
        // 隐藏密聊提示
        if (encryptView != null) {
            encryptView.setVisibility(GONE);
        }
        
        // 解析名片信息
        try {
            String[] cardInfoArray = content.split("\\|");
            if (cardInfoArray.length >= 2) {
                String nickName = cardInfoArray[0];
                String userId = cardInfoArray[1];

                AvatarHelper.getInstance().displayAvatar(nickName, userId, ivCardImage, true);
                tvPersonName.setText(nickName);
                message.setObjectId(userId);
            } else {
                AvatarHelper.getInstance().displayAvatar(content, message.getObjectId(), ivCardImage, true);
                tvPersonName.setText(content);
            }
        } catch (Exception e) {
            AvatarHelper.getInstance().displayAvatar(content, message.getObjectId(), ivCardImage, true);
            tvPersonName.setText(content);
        }

        if (!isMysend) {
            ivUnRead.setVisibility(message.isSendRead() ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * 显示密聊提示
     */
    private void showEncryptTip(EncryptMessageHelper.EncryptMessageStatus encryptStatus, ChatMessage message, String content) {
        // 隐藏名片容器
        if (mChatCardView != null) {
            mChatCardView.setVisibility(GONE);
        }
        // 清理之前的密聊提示视图
        if (encryptView != null && mChatWrapView != null) {
            mChatWrapView.removeView(encryptView);
            encryptView = null;
        }
        
        // 在容器中加载密聊提示布局
        if (mChatWrapView != null) {
            // 动态加载密聊布局
            encryptView = View.inflate(mContext,
                isMysend ? R.layout.chat_from_ml_item_text : R.layout.chat_to_ml_item_text, null);

            // 添加到容器视图
            android.widget.FrameLayout.LayoutParams params = new android.widget.FrameLayout.LayoutParams(
                android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                android.widget.FrameLayout.LayoutParams.MATCH_PARENT
            );
            mChatWrapView.addView(encryptView, params);

            // 设置密聊提示文本
            TextView encryptText = encryptView.findViewById(R.id.chat_text);
            encryptText.setText(encryptStatus.displayText);
            encryptText.setTextColor(encryptStatus.textColor);

            // 设置点击事件
            encryptView.setOnClickListener(v -> {
                if (encryptStatus.isEncryptedMessage && encryptStatus.isSimpleEncryptEnabled) {
                    // 解密消息
                    String decryptedContent = SimpleEncryptUtil.decrypt(content, message.getFromUserId());
                    if (decryptedContent != null && !decryptedContent.equals(content)) {
                        isDecrypted = true;
                        // 解密成功后显示名片内容
                        showCardContent(message, decryptedContent);
                    }
                } else {
                    // 密聊未开启
                    handleEncryptNotEnabledClick();
                }
            });
        }
    }

    @Override
    protected void onRootClick(View v) {
        // 检查是否为密聊消息且未解密
        String content = StringUtils.replaceSpecialChar(mdata.getContent());
        EncryptMessageHelper.EncryptMessageStatus encryptStatus = checkEncryptMessage(mdata, content);
        
        if (encryptStatus.isEncryptedMessage && !isDecrypted) {
            // 密聊消息且未解密，不处理点击事件
            return;
        }
        
        sendReadMessage(mdata);
        ivUnRead.setVisibility(GONE);
        BasicInfoActivity.start(mContext, mdata.getObjectId(), BasicInfoActivity.FROM_ADD_TYPE_CARD);
    }

    /**
     * 重写该方法，return true 表示显示红点
     *
     */
    @Override
    public boolean enableUnRead() {
        return true;
    }
}
