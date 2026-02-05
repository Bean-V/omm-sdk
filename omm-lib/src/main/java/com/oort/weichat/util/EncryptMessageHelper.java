package com.oort.weichat.util;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.oort.weichat.R;
import com.oort.weichat.bean.message.ChatMessage;
import com.oort.weichat.bean.message.XmppMessage;
import com.oort.weichat.util.secure.SimpleEncryptUtil;
import com.oortcloud.basemodule.dialog.inputpsw.dialog.PswInputDialog;

/**
 * 密聊消息处理工具类
 * 提供统一的密聊消息处理逻辑
 */
public class EncryptMessageHelper {
    
    /**
     * 密聊消息状态
     */
    public static class EncryptMessageStatus {
        public boolean isEncryptedMessage;      // 是否为加密消息
        public boolean isSimpleEncryptEnabled;  // 密聊是否开启
        public String displayText;             // 显示文本
        public int textColor;                  // 文字颜色
        public boolean isClickable;            // 是否可点击
        
        public EncryptMessageStatus(boolean isEncryptedMessage, boolean isSimpleEncryptEnabled, 
                                  String displayText, int textColor, boolean isClickable) {
            this.isEncryptedMessage = isEncryptedMessage;
            this.isSimpleEncryptEnabled = isSimpleEncryptEnabled;
            this.displayText = displayText;
            this.textColor = textColor;
            this.isClickable = isClickable;
        }
    }
    
    /**
     * 检查并处理密聊消息
     * @param context 上下文
     * @param message 消息对象
     * @param content 消息内容
     * @param isMysend 是否自己发送
     * @param mToUserId 对方用户ID
     * @param mLoginUserId 当前用户ID
     * @return 密聊消息状态
     */
    public static EncryptMessageStatus checkAndProcessEncryptMessage(Context context, 
                                                                   ChatMessage message, 
                                                                   String content, 
                                                                   boolean isMysend, 
                                                                   String mToUserId, 
                                                                   String mLoginUserId) {
        // 检查是否为密聊消息
        boolean isEncryptedMessage = message.getIsEncrypt() == 1;
        
        // 检查密聊是否开启
//        String encryptKey = Constants.generateSimpleEncryptKey(mToUserId, mLoginUserId);
        boolean isSimpleEncryptEnabled =  isEncryptedMessage;//PreferenceUtils.getBoolean(context, encryptKey, false);

        // 特殊处理图片消息：根据密聊状态显示相应提示
        if (message.getType() == XmppMessage.TYPE_IMAGE) {
            if (isSimpleEncryptEnabled) {
                // 密聊已开启
                String displayText = isMysend ? "🔒 发送密聊图片 - 点击查看" : "🔒 收到密聊图片 - 点击查看";
                return new EncryptMessageStatus(true, true, 
                    displayText, 
                    isMysend ? context.getResources().getColor(R.color.white) : context.getResources().getColor(R.color.black), 
                    true);
            } else {
//                // 密聊未开启
//                String displayText = isMysend ? "🔒 发送密聊图片 - 对方可能未开启密聊" : "🔒 收到密聊图片 - 请开启密聊功能";
//                return new EncryptMessageStatus(true, false,
//                    displayText,
//                    isMysend ? context.getResources().getColor(R.color.white) : context.getResources().getColor(R.color.black),
//                    true);
            }
        }

        if (isEncryptedMessage && isSimpleEncryptEnabled) {
            // 密聊开启时的消息显示
            String displayText;
            if (message.getType() == XmppMessage.TYPE_CARD) {
                // 名片消息的特殊提示
                displayText = isMysend ? "🔒 发送密聊名片 - 点击查看" : "🔒 收到密聊名片 - 点击查看";
            } else if (message.getType() == XmppMessage.TYPE_FILE) {
                // 文件消息的特殊提示
                displayText = isMysend ? "🔒 发送密聊文件 - 点击查看" : "🔒 收到密聊文件 - 点击查看";
            }  else if (message.getType() == XmppMessage.TYPE_VIDEO) {
                // 文件消息的特殊提示
                displayText = isMysend ? "🔒 发送密聊视频 - 点击查看" : "🔒 收到密聊文件 - 点击查看";
            }else {
                // 其他消息的通用提示
                displayText = isMysend ? "🔒 发送密聊消息 - 点击查看" : "🔒 收到密聊消息 - 点击查看";
            }
            
            return new EncryptMessageStatus(true, true, 
                displayText, 
                isMysend ? context.getResources().getColor(R.color.white) : context.getResources().getColor(R.color.black), 
                true);
        } else if (isEncryptedMessage && !isSimpleEncryptEnabled) {
            // 密聊未开启时的消息显示
            String displayText;
            if (message.getType() == XmppMessage.TYPE_CARD) {
                // 名片消息的特殊提示
                displayText = isMysend ? "🔒 发送密聊名片 - 对方可能未开启密聊" : "🔒 收到密聊名片 - 请开启密聊功能";
            } else if (message.getType() == XmppMessage.TYPE_IMAGE) {
                // 图片消息的特殊提示
                displayText = isMysend ? "🔒 发送密聊图片 - 对方可能未开启密聊" : "🔒 收到密聊图片 - 请开启密聊功能";
            } else if (message.getType() == XmppMessage.TYPE_FILE) {
                // 文件消息的特殊提示
                displayText = isMysend ? "🔒 发送密聊文件 - 对方可能未开启密聊" : "🔒 收到密聊文件 - 请开启密聊功能";
            } else {
                // 其他消息的通用提示
                displayText = isMysend ? "🔒 发送密聊消息 - 对方可能未开启密聊" : "🔒 收到密聊消息 - 请开启密聊功能";
            }
            
            return new EncryptMessageStatus(true, false, 
                displayText, 
                isMysend ? context.getResources().getColor(R.color.white) : context.getResources().getColor(R.color.black), 
                true);
        }
        
        // 不是密聊消息
        return new EncryptMessageStatus(false, false, content, 0, false);
    }
    
    /**
     * 处理密聊消息点击事件
     * @param context 上下文
     * @param message 消息对象
     * @param encryptedContent 加密内容
     * @param textView 要更新的TextView
     * @param isMysend 是否自己发送
     * @return 是否处理成功
     */
    public static boolean handleEncryptedMessageClick(Context context,
                                                      ChatMessage message,
                                                      String encryptedContent,
                                                      TextView textView,
                                                      boolean isMysend) {
        try {
            // 自己发送的消息：直接解密显示（使用自己的用户ID作为密钥，无需密码验证）
            if (isMysend) {
                String senderUserId = message.getFromUserId(); // 自己的用户ID
                String decryptedContent = SimpleEncryptUtil.decrypt(encryptedContent, senderUserId);

                // 显示解密后的内容
                textView.setText(decryptedContent);
                textView.setTextColor(context.getResources().getColor(R.color.white));
                textView.setClickable(false);

                Toast.makeText(context, "密聊消息已解密", Toast.LENGTH_SHORT).show();
                return true;
            }
            // 对方发送的消息：先输入密码验证，再解密
            else {
                // 创建密码输入对话框

                PswInputDialog pswInputDialog = new PswInputDialog(context);
                //pswInputDialog.setTitle("请输入管理员密码");
                //showPswDialog()一定要在最前面执行
                pswInputDialog.showPswDialog();

                //隐藏忘记密码的入口
                pswInputDialog.hideForgetPswClickListener();

                //设置忘记密码的点击事件
                pswInputDialog.setOnForgetPswClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, context.getText(R.string.forget_password), Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(context, "请输入密码", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if (!inputPassword.equals(message.getSignature())) {
                                Toast.makeText(context, "密码有误", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            try {
                                // 使用输入的密码解密
                                String decryptedContent = SimpleEncryptUtil.decrypt(encryptedContent, inputPassword);

                                // 显示解密内容
                                textView.setText(decryptedContent);
                                textView.setTextColor(context.getResources().getColor(R.color.black));
                                textView.setClickable(false);

                                Toast.makeText(context, "密聊消息已解密", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                // 解密失败处理
                                textView.setText("❌ 密聊消息解密失败");
                                textView.setTextColor(context.getResources().getColor(R.color.redpacket_bg));
                                Toast.makeText(context, "密码错误或解密失败", Toast.LENGTH_SHORT).show();
                            }
                        }
//                            Toast.makeText(MainActivity.this, "你输入的密码是：" + psw, Toast.LENGTH_SHORT).show();
                    }
                });

                return true; // 表示已触发解密流程
            }
        } catch (Exception e) {
            // 自己发送的消息解密失败处理
            textView.setText("❌ 密聊消息解密失败");
            textView.setTextColor(context.getResources().getColor(R.color.redpacket_bg));
            Toast.makeText(context, "解密失败，请检查密钥", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
    
    /**
     * 处理密聊未开启时的点击事件
     * @param context 上下文
     */
    public static void handleEncryptNotEnabledClick(Context context) {
        android.widget.Toast.makeText(context, "请先在设置中开启密聊功能", android.widget.Toast.LENGTH_SHORT).show();
    }
}
