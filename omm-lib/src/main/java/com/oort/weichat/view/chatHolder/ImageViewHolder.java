package com.oort.weichat.view.chatHolder;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.oort.weichat.AppConstant;
import com.oort.weichat.MyApplication;
import com.oort.weichat.R;
import com.oort.weichat.bean.message.ChatMessage;
import com.oort.weichat.bean.message.XmppMessage;
import com.oort.weichat.db.dao.ChatMessageDao;
import com.oort.weichat.downloader.DownloadListener;
import com.oort.weichat.downloader.Downloader;
import com.oort.weichat.downloader.FailReason;
import com.oort.weichat.helper.ImageLoadHelper;
import com.oort.weichat.ui.message.ChatOverviewActivity;
import com.oort.weichat.ui.tool.SingleImagePreviewActivity;
import com.oort.weichat.util.EncryptMessageHelper;
import com.oort.weichat.util.FileUtil;
import com.oort.weichat.util.StringUtils;
import com.oort.weichat.view.ChatImageView2;
import com.oort.weichat.view.XuanProgressPar;
import com.oortcloud.basemodule.dialog.inputpsw.dialog.PswInputDialog;

import java.util.ArrayList;
import java.util.List;

public class ImageViewHolder extends AChatHolderInterface {
    private static final int IMAGE_MIN_SIZE = 90;
    private static final int IMAGE_MAX_SIZE = 120;
    private ChatImageView2 mImageView;
    private XuanProgressPar progressPar;
    private ViewGroup mChatWrapView;
    private ViewGroup mChatImageView;
    private View encryptView;
    private int width, height;

    @Override
    public int itemLayoutId(boolean isMysend) {
        return isMysend ? R.layout.chat_from_item_image : R.layout.chat_to_item_image;
    }

    @Override
    public void initView(View view) {
        mImageView = view.findViewById(R.id.chat_image);
        progressPar = view.findViewById(R.id.img_progress);
        mChatWrapView = view.findViewById(R.id.chat_warp_view);
        mChatImageView = view.findViewById(R.id.chat_image_view);
        mRootView = mChatImageView;
    }

    @Override
    public void fillData(ChatMessage message) {
        String content = StringUtils.replaceSpecialChar(message.getContent());
        // 使用公共方法检查密聊消息
        EncryptMessageHelper.EncryptMessageStatus encryptStatus = checkEncryptMessage(message, content);

        Log.e("zq", "--ImageViewHolder---content-------" +content);
        Log.e("zq", "--ImageViewHolder---isEncryptedMessage-------" +encryptStatus.isEncryptedMessage);
        Log.e("zq", "--ImageViewHolder---isSimpleEncryptEnabled-------" +encryptStatus.isSimpleEncryptEnabled);
        if (encryptStatus.isEncryptedMessage) {
            // 密聊消息：显示密聊提示布局
            showEncryptTip(encryptStatus, message, content);
        } else {
            // 普通消息：显示图片内容
            showImageContent(message);
        }
    }

    /**
     * 显示图片内容
     */
    private void showImageContent(ChatMessage message) {
        // 显示图片容器
        if (mChatImageView != null) {
            mChatImageView.setVisibility(View.VISIBLE);
        }
        
        // 隐藏密聊提示
        if (encryptView != null) {
            encryptView.setVisibility(View.GONE);
        }
        
        // 修改image布局大小，解决因图片异步加载且布局设置的warp_content导致setSelection不能滑动到最底部的问题
        changeImageLayoutSize(message);

        // 加载图片
        String filePath = message.getFilePath();
        if (FileUtil.isExist(filePath)) { // 本地存在
            fillImage(filePath);
        } else {
            if (TextUtils.isEmpty(message.getContent())) {// 理论上不可能
                mImageView.setImageResource(R.drawable.fez);
            } else {
                mImageView.setImageDrawable(null);
                Downloader.getInstance().addDownload(message.getContent(), mSendingBar, new FileDownloadListener(message));
            }
        }

        // 判断是否为阅后即焚类型的图片，如果是 模糊显示该图片
        if (!isGounp) {
            mImageView.setAlpha(message.getIsReadDel() ? 0.1f : 1f);
        }

        // 上传进度条 我的消息才有进度条
        if (!isMysend || message.isUpload() || message.getUploadSchedule() >= 100) {
            progressPar.setVisibility(View.GONE);
        } else {
            progressPar.setVisibility(View.VISIBLE);
        }
        progressPar.update(message.getUploadSchedule());
    }

    /**
     * 显示密聊提示
     */
    private void showEncryptTip(EncryptMessageHelper.EncryptMessageStatus encryptStatus, ChatMessage message, String content) {
        Log.e("zq", "--ImageViewHolder-----content-----" +content);
        // 隐藏图片容器
        if (mChatImageView != null) {
            mChatImageView.setVisibility(View.GONE);
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
                if (encryptStatus.isSimpleEncryptEnabled) {
                    // 密聊已开启，区分发送者身份处理图片显示
                    if (isMysend) {
                        // 自己发送的图片：直接显示
                        showImageContent(message);
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
                                            showImageContent(message);
                                        } else {
                                            Toast.makeText(mContext, "密码错误，无法查看图片", Toast.LENGTH_SHORT).show();
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

    /**
     * 处理密聊未开启时的点击事件
     */
    protected void handleEncryptNotEnabledClick() {
        android.widget.Toast.makeText(mContext, "请先在设置中开启密聊功能", android.widget.Toast.LENGTH_SHORT).show();
    }

/*
    private void fillImageGif(String filePath) {
        try {
            GifDrawable gifFromFile = new GifDrawable(new File(filePath));
            mImageView.setImageGifDrawable(gifFromFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void fillImage(String filePath) {
        AvatarHelper.getInstance().displayUrl(filePath, mImageView, R.drawable.fez);
    }
*/

    private void fillImage(String filePath) {
        if (filePath.endsWith(".gif")) {
            ImageLoadHelper.showGifWithError(
                    MyApplication.getContext(),
                    filePath,
                    R.drawable.fez,
                    mImageView
            );
        } else {
            ImageLoadHelper.showImageDontAnimateWithError(
                    MyApplication.getContext(),
                    filePath,
                    R.drawable.fez,
                    mImageView
            );
        }
    }
    

    private void changeImageLayoutSize(ChatMessage message) {
        ViewGroup.LayoutParams mLayoutParams = mImageView.getLayoutParams();

        if (TextUtils.isEmpty(message.getLocation_x()) || TextUtils.isEmpty(message.getLocation_y())) {
            mLayoutParams.width = dp2px(IMAGE_MAX_SIZE);
            mLayoutParams.height = dp2px(IMAGE_MAX_SIZE);
            // todo Location_x 与Location_y为空，本地基本上不存在该file，下面会去下载，就不在这里下载了
            // Downloader.getInstance().addDownload(message.getContent(), mSendingBar, new FileDownloadListener(message));
        } else {
            float image_width = Float.parseFloat(message.getLocation_x());
            float image_height = Float.parseFloat(message.getLocation_y());

            if (image_width == 0f || image_height == 0f) {
                // 以防万一，
                mLayoutParams.width = dp2px(IMAGE_MAX_SIZE);
                mLayoutParams.height = dp2px(IMAGE_MAX_SIZE);
            } else {

                // 基于宽度进行缩放,三挡:宽图 55/100,窄图100/55
                float width = image_width / image_height < 0.4 ? IMAGE_MIN_SIZE : IMAGE_MAX_SIZE;
                float height = width == IMAGE_MAX_SIZE ? Math.max(width / image_width * image_height, IMAGE_MIN_SIZE) : IMAGE_MAX_SIZE;

                mLayoutParams.width = dp2px(width);
                mLayoutParams.height = dp2px(height);
            }
        }

        this.width = mLayoutParams.width;
        this.height = mLayoutParams.height;

        mImageView.setLayoutParams(mLayoutParams);
    }

    @Override
    public void onRootClick(View v) {
        // 检查是否为密聊消息
        String content = StringUtils.replaceSpecialChar(mdata.getContent());
        EncryptMessageHelper.EncryptMessageStatus encryptStatus = checkEncryptMessage(mdata, content);
        
        if (encryptStatus.isEncryptedMessage) {
            // 密聊消息，根据密聊状态处理点击事件
            if (encryptStatus.isSimpleEncryptEnabled) {
                // 密聊已开启，显示图片内容
//                showImageContent(mdata);
                Intent intent = new Intent(mContext, SingleImagePreviewActivity.class);
                intent.putExtra(AppConstant.EXTRA_IMAGE_URI, mdata.getContent());
                intent.putExtra("image_path", mdata.getFilePath());
                intent.putExtra("isReadDel", mdata.getIsReadDel());
                if (!isGounp && !isMysend && mdata.getIsReadDel()) {
                    intent.putExtra("DEL_PACKEDID", mdata.getPacketId());
                }
                mContext.startActivity(intent);
            } else {
                // 密聊未开启，提示用户
                handleEncryptNotEnabledClick();
            }
            return;
        }
        
        if (mdata.getIsReadDel()) { // 阅后即焚图片跳转至单张图片预览类
            Intent intent = new Intent(mContext, SingleImagePreviewActivity.class);
            intent.putExtra(AppConstant.EXTRA_IMAGE_URI, mdata.getContent());
            intent.putExtra("image_path", mdata.getFilePath());
            intent.putExtra("isReadDel", mdata.getIsReadDel());
            if (!isGounp && !isMysend && mdata.getIsReadDel()) {
                intent.putExtra("DEL_PACKEDID", mdata.getPacketId());
            }
            mContext.startActivity(intent);
        } else {
            int imageChatMessageList_current_position = 0;
            List<ChatMessage> imageChatMessageList = new ArrayList<>();
            for (int i = 0; i < chatMessages.size(); i++) {
                if (chatMessages.get(i).getType() == XmppMessage.TYPE_IMAGE
                        && !chatMessages.get(i).getIsReadDel()) {
                    if (chatMessages.get(i).getPacketId().equals(mdata.getPacketId())) {
                        imageChatMessageList_current_position = imageChatMessageList.size();
                    }
                    imageChatMessageList.add(chatMessages.get(i));
                }
            }
            Intent intent = new Intent(mContext, ChatOverviewActivity.class);
            ChatOverviewActivity.imageChatMessageListStr = JSON.toJSONString(imageChatMessageList);
            intent.putExtra("imageChatMessageList_current_position", imageChatMessageList_current_position);
            mContext.startActivity(intent);
        }
    }

    @Override
    public boolean enableSendRead() {
        return true;
    }

    // 启用阅后即焚
    @Override
    public boolean enableFire() {
        return true;
    }

    class FileDownloadListener implements DownloadListener {
        private ChatMessage message;

        public FileDownloadListener(ChatMessage message) {
            this.message = message;
        }

        @Override
        public void onStarted(String uri, View view) {
            if (view != null) {
                view.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onFailed(String uri, FailReason failReason, View view) {
            if (view != null) {
                view.setVisibility(View.GONE);
            }
        }

        @Override
        public void onComplete(String uri, String filePath, View view) {
            if (view != null) {
                view.setVisibility(View.GONE);
            }
            message.setFilePath(filePath);
            ChatMessageDao.getInstance().updateMessageDownloadState(mLoginUserId, mToUserId, message.get_id(), true, filePath);
            // 保存图片尺寸到数据库
            saveImageSize(filePath);

/*
            if (filePath.endsWith(".gif")) { // 加载gif
                fillImageGif(filePath);
            } else { // 加载图片
                fillImage(filePath);
            }
*/
            fillImage(filePath);
        }

        @Override
        public void onCancelled(String uri, View view) {
            if (view != null) {
                view.setVisibility(View.GONE);
            }
        }

        /**
         * 获取图片宽高，保存至本地
         *
         * @param filePath
         */
        private void saveImageSize(String filePath) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, options); // 此时返回的bitmap为null

            message.setLocation_x(String.valueOf(options.outWidth));
            message.setLocation_y(String.valueOf(options.outHeight));

            // 重绘图片尺寸
            changeImageLayoutSize(message);
            // 保存下载到数据库
            ChatMessageDao.getInstance().updateMessageLocationXY(message, mLoginUserId);
        }
    }
}
