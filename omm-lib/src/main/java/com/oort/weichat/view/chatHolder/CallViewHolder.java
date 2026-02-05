package com.oort.weichat.view.chatHolder;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.oort.weichat.bean.message.ChatMessage;
import com.oort.weichat.bean.message.XmppMessage;
import com.oort.weichat.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;


class CallViewHolder extends AChatHolderInterface {

    ImageView ivTextImage;
    TextView mTvContent;

    @Override
    public int itemLayoutId(boolean isMysend) {
        return isMysend ? R.layout.chat_from_item_call : R.layout.chat_to_item_call;
    }

    @Override
    public void initView(View view) {
        ivTextImage = view.findViewById(R.id.chat_text_img);
        mTvContent = view.findViewById(R.id.chat_text);
        mRootView = view.findViewById(R.id.chat_warp_view);
    }

    @Override
    public void fillData(ChatMessage message) {
        switch (message.getType()) {
            case XmppMessage.TYPE_NO_CONNECT_VOICE: {
                String content;
                if (message.getTimeLen() == 0) {
                    content = getString(R.string.sip_canceled);
                } else {
                    if (TextUtils.equals(mLoginUserId, message.getFromUserId())) {
                        content = getString(R.string.sip_refused);
                    } else {
                        content = getString(R.string.sip_remote_refused);
                    }
                }
                mTvContent.setText(content);
                ivTextImage.setImageResource(isMysend ? R.mipmap.end_of_voice_call_icon : R.mipmap.end_of_voice_call_icon01);
            }
            break;
            case XmppMessage.TYPE_END_CONNECT_VOICE: {
                // 结束
                long timeLen = message.getTimeLen();
                mTvContent.setText(getString(R.string.time_len) + getTimeLengthString(timeLen));
                ivTextImage.setImageResource(isMysend ? R.mipmap.end_of_voice_call_icon : R.mipmap.end_of_voice_call_icon01);
            }
            break;

            case XmppMessage.TYPE_NO_CONNECT_VIDEO: {
                String content;
                if (message.getTimeLen() == 0) {
                    content = getString(R.string.sip_canceled);
                } else {
                    if (TextUtils.equals(mLoginUserId, message.getFromUserId())) {
                        content = getString(R.string.sip_refused);
                    } else {
                        content = getString(R.string.sip_remote_refused);
                    }
                }
                mTvContent.setText(content);
                ivTextImage.setImageResource(isMysend ? R.mipmap.end_of_video_call_icon : R.mipmap.end_of_video_call_icon01);
            }
            break;
            case XmppMessage.TYPE_END_CONNECT_VIDEO:
            case XmppMessage.TYPE_END_CONNECT_SCREEN: {
                // 结束
                long timeLen = message.getTimeLen();
                mTvContent.setText(
                         getString(R.string.time_len) + getTimeLengthString(timeLen));
                ivTextImage.setImageResource(isMysend ? R.mipmap.end_of_video_call_icon : R.mipmap.end_of_video_call_icon01);
            }
            break;
            case XmppMessage.TYPE_NO_CONNECT_SCREEN: {
                String content;
                if (message.getTimeLen() == 0) {
                    content = getString(R.string.sip_canceled) + getString(R.string.screen_call);
                } else {
                    if (TextUtils.equals(mLoginUserId, message.getFromUserId())) {
                        content = getString(R.string.sip_refused);
                    } else {
                        content = getString(R.string.sip_remote_refused);
                    }
                }

                mTvContent.setText(content);
                ivTextImage.setImageResource(isMysend ? R.mipmap.end_of_video_call_icon : R.mipmap.end_of_video_call_icon01);
            }
            break;
            case XmppMessage.TYPE_IS_MU_CONNECT_VOICE: {
                mTvContent.setText(R.string.tip_invite_voice_meeting);
                ivTextImage.setImageResource(isMysend ? R.mipmap.end_of_voice_call_icon : R.mipmap.end_of_voice_call_icon01);
            }
            case XmppMessage.TYPE_IS_MU_CONNECT_SCREEN: {
                mTvContent.setText(R.string.tip_invite_screen_meeting);
                ivTextImage.setImageResource(isMysend ? R.mipmap.end_of_voice_call_icon : R.mipmap.end_of_voice_call_icon01);
            }
            break;
            case XmppMessage.TYPE_IS_MU_CONNECT_VIDEO: {
                mTvContent.setText(R.string.tip_invite_video_meeting);
                ivTextImage.setImageResource(isMysend ? R.mipmap.end_of_video_call_icon : R.mipmap.end_of_video_call_icon01);
            }
            break;
            case XmppMessage.TYPE_IS_MU_CONNECT_TALK: {
                mTvContent.setText(R.string.tip_invite_talk_meeting);
                ivTextImage.setImageResource(isMysend ? R.mipmap.end_of_video_call_icon : R.mipmap.end_of_video_call_icon01);
            }
            break;
            case XmppMessage.TYPE_IS_BUSY:
                if (TextUtils.equals(mdata.getObjectId(), String.valueOf(0))) {
                    ivTextImage.setImageResource(isMysend ? R.mipmap.end_of_voice_call_icon : R.mipmap.end_of_voice_call_icon01);
                } else {
                    ivTextImage.setImageResource(isMysend ? R.mipmap.end_of_video_call_icon : R.mipmap.end_of_video_call_icon01);
                }
                if (mdata.isMySend()) {
                    mTvContent.setText(R.string.busy_he);
                } else {
                    mTvContent.setText(R.string.busy_me);
                }
                break;
        }
    }

    @NonNull
    private String getTimeLengthString(long timeLen) {
        SimpleDateFormat sdf;
        if (timeLen < TimeUnit.HOURS.toSeconds(1)) {
            sdf = new SimpleDateFormat("mm:ss");
        } else {
            sdf = new SimpleDateFormat("HH:mm:ss");
        }
        return sdf.format(new Date(TimeUnit.SECONDS.toMillis(timeLen)));
    }

    @Override
    protected void onRootClick(View v) {

    }

    /**
     * 重写该方法，return true 表示自动发送已读
     *
     * @return
     */
    @Override
    public boolean enableSendRead() {
        return true;
    }
}
