package com.oort.weichat.view.chatHolder;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.oort.weichat.AppConstant;
import com.oort.weichat.bean.message.ChatMessage;
import com.oort.weichat.db.dao.ChatMessageDao;
import com.oort.weichat.downloader.DownloadListener;
import com.oort.weichat.downloader.DownloadProgressListener;
import com.oort.weichat.downloader.Downloader;
import com.oort.weichat.downloader.FailReason;
import com.oort.weichat.helper.AvatarHelper;
import com.oort.weichat.helper.UploadEngine;
import com.oort.weichat.util.EncryptMessageHelper;
import com.oort.weichat.util.StringUtils;
import com.oort.weichat.video.ChatVideoPreviewActivity;
import com.oort.weichat.xmpp.listener.ChatMessageListener;
import com.oort.weichat.R;
import com.oort.weichat.util.FileUtil;
import com.oort.weichat.util.HttpUtil;
import com.oort.weichat.view.SelectionFrame;
import com.oort.weichat.view.XuanProgressPar;
import com.oortcloud.basemodule.dialog.inputpsw.dialog.PswInputDialog;

public class VideoViewHolder extends AChatHolderInterface implements DownloadListener, DownloadProgressListener {

    // JVCideoPlayerStandardforchat mVideo;
    ImageView mVideo;
    ImageView ivStart;
    XuanProgressPar progressPar;
    TextView tvInvalid;
    ImageView ivUploadCancel;
    private ViewGroup mChatWrapView;
    private ViewGroup mChatVideoView;
    private View encryptView;
    @Override
    public int itemLayoutId(boolean isMysend) {
        return isMysend ? R.layout.chat_from_item_video : R.layout.chat_to_item_video;
    }

    @Override
    public void initView(View view) {
        mVideo = view.findViewById(R.id.chat_jcvideo);
        ivStart = view.findViewById(R.id.iv_start);
        progressPar = view.findViewById(R.id.img_progress);
        tvInvalid = view.findViewById(R.id.tv_invalid);
        ivUploadCancel = view.findViewById(R.id.chat_upload_cancel_iv);
        mRootView = view.findViewById(R.id.chat_warp_view);
        mChatWrapView = view.findViewById(R.id.chat_warp_view);;
        mChatVideoView = view.findViewById(R.id.chat_video_warp_view);
    }


    @Override
    public void fillData(ChatMessage message) {
        String content = StringUtils.replaceSpecialChar(message.getContent());

        // 检查密聊状态
        EncryptMessageHelper.EncryptMessageStatus encryptStatus = checkEncryptMessage(message, content);

        if (encryptStatus.isEncryptedMessage) {
            // 密聊消息：显示密聊提示
            showEncryptTip(encryptStatus, message, content);
        } else {
            // 普通消息：显示文件内容
            showVideoContent(message);
        }
    }
    /**
     * 显示密聊提示
     */
    private void showEncryptTip(EncryptMessageHelper.EncryptMessageStatus encryptStatus, ChatMessage message, String content) {
        // 隐藏文件容器
        if (mChatVideoView != null) {
            mChatVideoView.setVisibility(View.GONE);
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
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
            );
            mChatWrapView.addView(encryptView, params);

            // 设置密聊提示文本
            TextView encryptText = encryptView.findViewById(R.id.chat_text);
            encryptText.setText(encryptStatus.displayText);
            encryptText.setTextColor(encryptStatus.textColor);

            // 设置点击事件
            encryptView.setOnClickListener(v -> {
                if (encryptStatus.isEncryptedMessage && encryptStatus.isSimpleEncryptEnabled) {
                    if (isMysend) {
                        // 自己发送的图片：直接显示
                        showVideoContent(message);
                    } else {


                        PswInputDialog pswInputDialog = new PswInputDialog(mContext);
                        //pswInputDialog.setTitle("请输入管理员密码");
                        //showPswDialog()一定要在最前面执行
                        pswInputDialog.showPswDialog();

                        //隐藏忘记密码的入口
                        pswInputDialog.hideForgetPswClickListener();

                        //设置忘记密码的点击事件
                        pswInputDialog.setOnForgetPswClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(mContext, mContext.getText(R.string.forget_password), Toast.LENGTH_SHORT).show();
                            }
                        });

                        //设置密码长度
                        pswInputDialog.setPswCount(6);
                        //设置密码输入完成监听
                        pswInputDialog.setListener(new PswInputDialog.OnPopWindowClickListener() {
                            @Override
                            public void onPopWindowClickListener(String password, boolean complete) {
                                if (complete) {
                                    String inputPassword = password;
                                    if (TextUtils.isEmpty(inputPassword)) {
                                        Toast.makeText(mContext, "请输入密码", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    try {
                                        // 密码验证逻辑（根据实际加密逻辑调整，例如校验与对方用户ID关联的密码）
                                        // 示例：通过工具类验证密码有效性
                                        boolean isPasswordValid = inputPassword.equals(message.getSignature());
                                        if (isPasswordValid) {
                                            // 密码正确，显示图片
                                            showVideoContent(message);
                                        } else {
                                            Toast.makeText(mContext, "密码错误，无法查看视频", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (Exception e) {
                                        Toast.makeText(mContext, "验证失败，请重试", Toast.LENGTH_SHORT).show();
                                    }
                                }
//                            Toast.makeText(MainActivity.this, "你输入的密码是：" + psw, Toast.LENGTH_SHORT).show();
                            }
                        });


                    }
                } else {
                    // 密聊未开启
                    handleEncryptNotEnabledClick();
                }
            });
        }
    }

    private void showVideoContent(ChatMessage message){
        tvInvalid.setVisibility(View.GONE);
        // 显示图片容器
        if (mChatVideoView != null) {
            mChatVideoView.setVisibility(View.VISIBLE);
        }

        // 隐藏密聊提示
        if (encryptView != null) {
            encryptView.setVisibility(View.GONE);
        }

        String filePath = message.getFilePath();
        boolean isExist = FileUtil.isExist(filePath);

        if (!isExist) {
            AvatarHelper.getInstance().asyncDisplayOnlineVideoThumb(message.getContent(), mVideo);
        } else {
            AvatarHelper.getInstance().displayVideoThumb(filePath, mVideo);
            ivStart.setImageResource(fm.jiecao.jcvideoplayer_lib.R.drawable.jc_click_play_selector);
        }

        if (isMysend) { // 判断是否上传
            // 没有上传或者 进度小于100
            boolean show = !message.isUpload() && message.getUploadSchedule() < 100
                    && message.getMessageState() == ChatMessageListener.MESSAGE_SEND_ING;
            changeVisible(progressPar, show);
            changeVisible(ivStart, !show);

            if (show) {
                if (ivUploadCancel != null) {
                    ivUploadCancel.setVisibility(View.VISIBLE);
                }
            } else {
                if (ivUploadCancel != null) {
                    ivUploadCancel.setVisibility(View.GONE);
                }
            }
        }

        progressPar.update(message.getUploadSchedule());
        mSendingBar.setVisibility(View.GONE);

        if (ivUploadCancel != null) {
            ivUploadCancel.setOnClickListener(v -> {
                SelectionFrame selectionFrame = new SelectionFrame(mContext);
                selectionFrame.setSomething(getString(R.string.cancel_upload), getString(R.string.sure_cancel_upload), new SelectionFrame.OnSelectionFrameClickListener() {
                    @Override
                    public void cancelClick() {

                    }

                    @Override
                    public void confirmClick() {
                        // 用户可能在弹窗弹起后停留很久，所以点击确认的时候还需要判断一下
                        if (!mdata.isUpload()) {
                            UploadEngine.cancel(mdata.getPacketId());
                        }
                    }
                });
                selectionFrame.show();
            });
        }
    }

    @Override
    protected void onRootClick(View v) {
        if (tvInvalid.getVisibility() == View.VISIBLE) {
            return;
        }

        String filePath = mdata.getFilePath();
        if (!FileUtil.isExist(filePath)) {
            filePath = mdata.getContent();
            // 本地不存在，传网络路径进去播放，下载。。。
            if (HttpUtil.isConnectedGprs(mContext)) {
                SelectionFrame selectionFrame = new SelectionFrame(mContext);
                String finalFilePath = filePath;
                selectionFrame.setSomething(null, getString(fm.jiecao.jcvideoplayer_lib.R.string.tips_not_wifi), new SelectionFrame.OnSelectionFrameClickListener() {
                    @Override
                    public void cancelClick() {

                    }

                    @Override
                    public void confirmClick() {
                        Downloader.getInstance().addDownload(finalFilePath, mSendingBar, VideoViewHolder.this, VideoViewHolder.this);
                    }
                });
                selectionFrame.show();
            } else {
                Downloader.getInstance().addDownload(filePath, mSendingBar, VideoViewHolder.this, VideoViewHolder.this);
            }
        } else {
            startPlay(filePath);
        }
    }

    private void startPlay(String filePath) {
        Intent intent = new Intent(mContext, ChatVideoPreviewActivity.class);
        intent.putExtra(AppConstant.EXTRA_VIDEO_FILE_PATH, filePath);
        if (mdata.getIsReadDel()) {
            intent.putExtra("DEL_PACKEDID", mdata.getPacketId());
        }

        ivUnRead.setVisibility(View.GONE);
        mContext.startActivity(intent);
    }

    @Override
    public void onStarted(String uri, View view) {
        changeVisible(progressPar, true);
        changeVisible(ivStart, false);
    }

    @Override
    public void onFailed(String uri, FailReason failReason, View view) {
        changeVisible(progressPar, false);
        ivStart.setImageResource(fm.jiecao.jcvideoplayer_lib.R.drawable.jc_click_error_selector);
        tvInvalid.setVisibility(View.VISIBLE);
        ivStart.setVisibility(View.VISIBLE);
    }

    @Override
    public void onComplete(String uri, String filePath, View view) {
        mdata.setFilePath(filePath);
        changeVisible(progressPar, false);
        changeVisible(ivStart, true);
        ivStart.setImageResource(fm.jiecao.jcvideoplayer_lib.R.drawable.jc_click_play_selector);

        // 更新数据库
        ChatMessageDao.getInstance().updateMessageDownloadState(mLoginUserId, mToUserId, mdata.get_id(), true, filePath);
        AvatarHelper.getInstance().displayVideoThumb(filePath, mVideo);
        startPlay(filePath);
    }

    @Override
    public void onCancelled(String uri, View view) {
        changeVisible(progressPar, false);
        changeVisible(ivStart, true);
    }

    @Override
    public void onProgressUpdate(String imageUri, View view, int current, int total) {
        int pro = (int) (current / (float) total * 100);
        progressPar.update(pro);
    }

    @Override
    public boolean enableUnRead() {
        return true;
    }

    @Override
    public boolean enableFire() {
        return true;
    }

}
