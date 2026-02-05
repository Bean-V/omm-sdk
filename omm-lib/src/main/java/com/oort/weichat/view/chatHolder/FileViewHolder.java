package com.oort.weichat.view.chatHolder;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.oort.weichat.R;
import com.oort.weichat.bean.message.ChatMessage;
import com.oort.weichat.helper.ImageLoadHelper;
import com.oort.weichat.helper.UploadEngine;
import com.oort.weichat.ui.mucfile.DownManager;
import com.oort.weichat.ui.mucfile.MucFileDetails;
import com.oort.weichat.ui.mucfile.XfileUtils;
import com.oort.weichat.ui.mucfile.bean.MucFileBean;
import com.oort.weichat.util.EncryptMessageHelper;
import com.oort.weichat.util.FileUtil;
import com.oort.weichat.util.StringUtils;
import com.oort.weichat.util.ToastUtil;
import com.oort.weichat.view.FileProgressPar;
import com.oort.weichat.view.SelectionFrame;
import com.oort.weichat.xmpp.listener.ChatMessageListener;
import com.oortcloud.basemodule.dialog.inputpsw.dialog.PswInputDialog;

class FileViewHolder extends AChatHolderInterface {

    ImageView ivCardImage;
    TextView tvPersonName;
    FileProgressPar progressPar;
    ImageView ivUploadCancel;
    private ViewGroup mChatWrapView;
    private ViewGroup mChatCardView;
    private View encryptView;

    @Override
    public int itemLayoutId(boolean isMysend) {
        return isMysend ? R.layout.chat_from_item_card : R.layout.chat_to_item_card;
    }

    @Override
    public void initView(View view) {
        ivCardImage = view.findViewById(R.id.iv_card_head);
        tvPersonName = view.findViewById(R.id.person_name);
        progressPar = view.findViewById(R.id.chat_card_light);
        ivUploadCancel = view.findViewById(R.id.chat_upload_cancel_iv);
        mChatWrapView = view.findViewById(R.id.chat_warp_view);
        mChatCardView = view.findViewById(R.id.chat_card_view);
        TextView tvType = view.findViewById(R.id.person_title);
        tvType.setText(getString(R.string.chat_file));
        mRootView = mChatWrapView;
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
            showFileContent(message);
        }
    }
    
    /**
     * 显示文件内容
     */
    private void showFileContent(ChatMessage message) {
        // 显示文件容器
        if (mChatCardView != null) {
            mChatCardView.setVisibility(View.VISIBLE);
        }
        
        // 隐藏密聊提示
        if (encryptView != null) {
            encryptView.setVisibility(View.GONE);
        }
        
        // 文件消息处理
        String filePath = FileUtil.isExist(message.getFilePath()) ? message.getFilePath() : message.getContent();
        if (TextUtils.isEmpty(filePath)) return;
        
        // 设置图标
        if (message.getTimeLen() > 0) { // 有文件类型
            if (message.getTimeLen() == 1) {
                ImageLoadHelper.showImageWithSize(
                        mContext,
                        filePath,
                        100, 100,
                        ivCardImage
                );
            } else {
                XfileUtils.setFileInco(message.getTimeLen(), ivCardImage);
            }
        } else {// 没有文件类型，取出后缀
            int pointIndex = filePath.lastIndexOf(".");
            if (pointIndex != -1) {
                String type = filePath.substring(pointIndex + 1).toLowerCase();
                if (type.equals("png") || type.equals("jpg") || type.equals("gif")) {
                    ImageLoadHelper.showImageWithSize(
                            mContext,
                            filePath,
                            100, 100,
                            ivCardImage
                    );
                    message.setTimeLen(1);
                } else {
                    fillFileIcon(type, ivCardImage, message);
                }
            }
        }

        // 设置文件名称
        String fileName = TextUtils.isEmpty(message.getFilePath()) ? message.getContent() : message.getFilePath();
        int start = fileName.lastIndexOf("/");
        String name = fileName.substring(start + 1).toLowerCase();
        tvPersonName.setText(name);
        message.setObjectId(name);

        // 设置进度条显示 不是我发的，或者进度到了100，或者上传了，都不显示 ——zq
        boolean hide = !isMysend || message.getUploadSchedule() == 100 || message.isUpload();
        progressPar.visibleMode(!hide);
        if (isMysend) {
            // 没有上传或者 进度小于100
            boolean show = !message.isUpload() && message.getUploadSchedule() < 100;
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

        // 消息发送失败有可能与文件上传失败一起出现(上传时杀死app)，发送失败按钮 > 取消上传按钮显示，如两个一起显示会重叠
        if (mdata.getMessageState() == ChatMessageListener.MESSAGE_SEND_FAILED) {
            ivUploadCancel.setVisibility(View.GONE);
            progressPar.update(0);
        }
    }
    
    /**
     * 显示密聊提示
     */
    private void showEncryptTip(EncryptMessageHelper.EncryptMessageStatus encryptStatus, ChatMessage message, String content) {
        // 隐藏文件容器
        if (mChatCardView != null) {
            mChatCardView.setVisibility(View.GONE);
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
                android.widget.FrameLayout.LayoutParams.WRAP_CONTENT
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
                        showFileContent(message);
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
                                            showFileContent(message);
                                        } else {
                                            Toast.makeText(mContext, "密码错误，无法查看文件", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onRootClick(View v) {
        String content = StringUtils.replaceSpecialChar(mdata.getContent());
        
        // 检查密聊状态
        EncryptMessageHelper.EncryptMessageStatus encryptStatus = checkEncryptMessage(mdata, content);
        
//        if (encryptStatus.isEncryptedMessage) {
//            // 密聊消息，不执行普通点击逻辑
//            return;
//        }
        
        if (TextUtils.isEmpty(mdata.getContent())
                && TextUtils.isEmpty(mdata.getFilePath())) {
            // 容错
            ToastUtil.showToast(mContext, getString(R.string.alert_not_have_file));
            return;
        }

        sendReadMessage(mdata);
        ivUnRead.setVisibility(View.GONE);

        MucFileBean data = new MucFileBean();
        String url = mdata.getContent();
        String filePath = mdata.getFilePath();
        if (TextUtils.isEmpty(filePath)) {
            filePath = url;
        }

        long size = mdata.getFileSize();
        // 取出文件名称
        int start = filePath.lastIndexOf("/");
        String name = filePath.substring(start + 1).toLowerCase();
        data.setNickname(name);
        data.setUrl(url);
        data.setName(name);
        data.setSize(size);
        data.setState(DownManager.STATE_UNDOWNLOAD);
        data.setType(Math.toIntExact(mdata.getTimeLen()));
        Intent intent = new Intent(mContext, MucFileDetails.class);
        intent.putExtra("data", data);
        mContext.startActivity(intent);
    }

    private void fillFileIcon(String type, ImageView v, ChatMessage chat) {
        if (type.equals("mp3")) {
            v.setImageResource(R.drawable.ic_muc_flie_type_y);
            chat.setTimeLen(2);
        } else if (type.equals("mp4") || type.equals("avi")) {
            v.setImageResource(R.drawable.ic_muc_flie_type_v);
            chat.setTimeLen(3);
        } else if (type.equals("xls")) {
            v.setImageResource(R.drawable.ic_muc_flie_type_x);
            chat.setTimeLen(5);
        } else if (type.equals("doc")) {
            v.setImageResource(R.drawable.ic_muc_flie_type_w);
            chat.setTimeLen(6);
        } else if (type.equals("ppt")) {
            v.setImageResource(R.drawable.ic_muc_flie_type_p);
            chat.setTimeLen(4);
        } else if (type.equals("pdf")) {
            v.setImageResource(R.drawable.ic_muc_flie_type_f);
            chat.setTimeLen(10);
        } else if (type.equals("apk")) {
            v.setImageResource(R.drawable.ic_muc_flie_type_a);
            chat.setTimeLen(11);
        } else if (type.equals("txt")) {
            v.setImageResource(R.drawable.ic_muc_flie_type_t);
            chat.setTimeLen(8);
        } else if (type.equals("rar") || type.equals("zip")) {
            v.setImageResource(R.drawable.ic_muc_flie_type_z);
            chat.setTimeLen(7);
        } else {
            v.setImageResource(R.drawable.ic_muc_flie_type_what);
            chat.setTimeLen(9);
        }
    }


    @Override
    public boolean enableUnRead() {
        return true;
    }
}
