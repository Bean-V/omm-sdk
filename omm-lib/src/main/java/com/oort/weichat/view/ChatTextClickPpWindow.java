package com.oort.weichat.view;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.flexbox.FlexboxLayout;
import com.oort.weichat.MyApplication;
import com.oort.weichat.R;
import com.oort.weichat.bean.event.EventNotifyByTag;
import com.oort.weichat.bean.message.ChatMessage;
import com.oort.weichat.bean.message.XmppMessage;
import com.oort.weichat.course.ChatRecordHelper;
import com.oort.weichat.helper.DialogHelper;
import com.oort.weichat.ui.base.CoreManager;
import com.oort.weichat.util.Constants;
import com.oort.weichat.util.PreferenceUtils;
import com.oort.weichat.util.TimeUtils;
import com.oortcloud.basemodule.widget.BasePopupWindow.basepopup.BasePopupSDK;
import com.oortcloud.basemodule.widget.BasePopupWindow.basepopup.BasePopupWindow;
import com.oortcloud.basemodule.widget.BasePopupWindow.util.animation.AlphaConfig;
import com.oortcloud.basemodule.widget.BasePopupWindow.util.animation.AnimationHelper;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;


public class ChatTextClickPpWindow extends BasePopupWindow {
    private final ImageView ivArrow;



    private View mMenuView;
    private TextView tvCopy;
    private TextView tvRelay;
    private TextView tvCollection;// 存表情
    private TextView tvCollectionOther; // 收藏其他类型的消息
    private TextView tvBack;
    private TextView tvReplay;
    private TextView tvDel;
    private TextView tvMoreSelected;
    // 开始 & 停止录制
    private TextView tvRecord;
    private TextView tvSpeaker;
    private TextView tvTimer;

    private boolean isGroup;
    private boolean isDevice;
    private int mRole;
    public ChatTextClickPpWindow(Context context) {
        super(context);

        BasePopupSDK.getInstance().init(context);
        setContentView(R.layout.item_chat_long_click);

        ivArrow = findViewById(R.id.iv_arrow);
        setViewPivotRatio(ivArrow, 0.5f, 0.5f);

        mMenuView = getContentView();


    }

        public ChatTextClickPpWindow(Context context, View.OnClickListener listener,
                                     final ChatMessage type, final String toUserId, boolean course,
                                     boolean group, boolean device, int role) {
        super(context);

            BasePopupSDK.getInstance().init(context);
            setContentView(R.layout.item_chat_long_click);

            ivArrow = findViewById(R.id.iv_arrow);
            setViewPivotRatio(ivArrow, 0.5f, 0.5f);

            mMenuView = getContentView();



        // mMenuView = inflater.inflate(R.layout.item_chat_long_click_list_style, null);

        this.isGroup = group;
        this.isDevice = device;
        this.mRole = role;


        tvCopy = (TextView) mMenuView.findViewById(R.id.item_chat_copy_tv);
        tvRelay = (TextView) mMenuView.findViewById(R.id.item_chat_relay_tv);
        tvCollection = (TextView) mMenuView.findViewById(R.id.item_chat_collection_tv);
        tvCollectionOther = (TextView) mMenuView.findViewById(R.id.collection_other);
        tvBack = (TextView) mMenuView.findViewById(R.id.item_chat_back_tv);
        tvReplay = (TextView) mMenuView.findViewById(R.id.item_chat_replay_tv);
        tvDel = (TextView) mMenuView.findViewById(R.id.item_chat_del_tv);
        tvMoreSelected = (TextView) mMenuView.findViewById(R.id.item_chat_more_select);
        tvRecord = (TextView) mMenuView.findViewById(R.id.item_chat_record);
        tvSpeaker = (TextView) mMenuView.findViewById(R.id.item_chat_speaker);
        tvTimer = (TextView) mMenuView.findViewById(R.id.item_chat_timer);

        if (type.getIsReadDel()) {
            tvRecord.setVisibility(View.GONE);
        }
        // 仅语音显示，扬声器、听筒切换 && 仅限聊天界面
        if (type.getType() == XmppMessage.TYPE_VOICE
                && !TextUtils.equals(MyApplication.IsRingId, "Empty")) {
            tvSpeaker.setVisibility(View.VISIBLE);
        }
        boolean isSpeaker = PreferenceUtils.getBoolean(MyApplication.getContext(),
                Constants.SPEAKER_AUTO_SWITCH + CoreManager.requireSelf(MyApplication.getContext()).getUserId(), true);
        tvSpeaker.setText(isSpeaker ? MyApplication.getContext().getString(R.string.chat_earpiece) : MyApplication.getContext().getString(R.string.chat_speaker));
        tvSpeaker.setOnClickListener(v -> {
            PreferenceUtils.putBoolean(MyApplication.getContext(),
                    Constants.SPEAKER_AUTO_SWITCH + CoreManager.requireSelf(MyApplication.getContext()).getUserId(), !isSpeaker);
            // 通知聊天界面刷新
            EventBus.getDefault().post(new EventNotifyByTag(EventNotifyByTag.Speak));
            dismiss();
        });

        hideButton(type, course);
        ViewGroup fbl = (FlexboxLayout) mMenuView.findViewById(R.id.fbl);


            ViewGroup parent = fbl; // 替换为你的父视图的ID
            int childCount = parent.getChildCount();

            int index = 0;
            ArrayList<View> views = new ArrayList();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);



                if(child.getVisibility() == View.VISIBLE){
                    index ++;

                    if(!child.equals(tvMoreSelected)){
                        views.add(child);
                    }

                }
                // 在这里处理子视图
            }
            if(views.size() > 4){
                views.add(4,tvMoreSelected);
            }else{
                views.add(tvMoreSelected);
            }
            fbl.removeView(tvMoreSelected);
            for(View v : views){
                fbl.removeView(v);
            }

            for(View v : views){
                fbl.addView(v);
            }


        // 设置按钮监听
        tvCopy.setOnClickListener(listener);
        tvRelay.setOnClickListener(listener);
        tvCollection.setOnClickListener(listener);
        tvCollectionOther.setOnClickListener(listener);
        tvBack.setOnClickListener(listener);
        tvReplay.setOnClickListener(listener);
        tvDel.setOnClickListener(listener);
        tvMoreSelected.setOnClickListener(listener);
        tvTimer.setOnClickListener(listener);
        tvRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ChatRecordHelper.instance().getState() == ChatRecordHelper.STATE_UN_RECORD) {
                    // 未录制 --> 开始录制
                    ChatRecordHelper.instance().start(type);
                    String tip;
                    if (MyApplication.IS_SUPPORT_SECURE_CHAT) {
                        tip = context.getString(R.string.course_support_type)
                                + context.getString(R.string.dont_support_tip, context.getString(R.string.record_course_tip));
                    } else {
                        tip = context.getString(R.string.course_support_type);
                    }
                    DialogHelper.tipDialog(context, tip);
                } else {
                    // 停止录制
                    ChatRecordHelper.instance().stop(type, toUserId);
                }
                dismiss();
            }
        });
    }


    public void showCopy(boolean  res){
        tvCopy.setVisibility(res ? View.VISIBLE : View.GONE);
    }
    public void showCollection(boolean  res){
        tvCollectionOther.setVisibility(res ? View.VISIBLE : View.GONE);
    }
    public void showRelay(boolean  res){
        tvRelay.setVisibility(res ? View.VISIBLE : View.GONE);
    }

    /*
    根据消息类型隐藏部分操作
     */
    private void hideButton(ChatMessage message, boolean course) {
        int type = message.getType();
        // 文本类型可复制
        if (type != XmppMessage.TYPE_TEXT) {
            tvCopy.setVisibility(View.GONE);
        } else {
            tvCopy.setVisibility(View.VISIBLE);
        }


        // 图片类型可存表情
        if (type == XmppMessage.TYPE_IMAGE) {
            tvCollection.setVisibility(View.VISIBLE);
        } else {
            tvCollection.setVisibility(View.GONE);
        }

        // 文本、图片、语音、视频、文件类型可收藏
        if (type == XmppMessage.TYPE_LOCATION ||type == XmppMessage.TYPE_TEXT || type == XmppMessage.TYPE_IMAGE || type == XmppMessage.TYPE_VOICE || type == XmppMessage.TYPE_VIDEO || type == XmppMessage.TYPE_FILE) {
            tvCollectionOther.setVisibility(View.VISIBLE);
        } else {
            tvCollectionOther.setVisibility(View.GONE);
        }

        // 撤回
        if (isGroup) {
            if ((message.isMySend() || mRole == 1 || mRole == 2) && type != XmppMessage.TYPE_RED) {
                tvBack.setVisibility(View.VISIBLE);
            } else {
                tvBack.setVisibility(View.GONE);
            }
        } else {
            if (!message.isMySend()
                    || type == XmppMessage.TYPE_RED
                    || type == XmppMessage.TYPE_TRANSFER
                    || ((type >= XmppMessage.TYPE_IS_CONNECT_VOICE && type <= XmppMessage.TYPE_EXIT_VOICE))
                    || type == XmppMessage.TYPE_SECURE_LOST_KEY) {
                // 该条消息 NotSendByMe || 红包 || 音视频通话 类型不可撤回
                tvBack.setVisibility(View.GONE);
            } else {
                tvBack.setVisibility(View.VISIBLE);
                /*if (judgeTime(message.getTimeSend())) {
                    // 超时不可撤回
                    tvBack.setVisibility(View.GONE);
                } else {
                    tvBack.setVisibility(View.VISIBLE);
                }*/
            }
        }

        // 红包 || 音视频通话 类型不可转发
        if (type == XmppMessage.TYPE_RED
                || type == XmppMessage.TYPE_TRANSFER
                || (type >= XmppMessage.TYPE_IS_CONNECT_VOICE && type <= XmppMessage.TYPE_EXIT_VOICE)
                || type == XmppMessage.TYPE_SECURE_LOST_KEY) {
            tvRelay.setVisibility(View.GONE);
        } else {
            tvRelay.setVisibility(View.VISIBLE);
        }

        // 阅后即焚消息不支持回复
        tvReplay.setVisibility(message.getIsReadDel() ? View.GONE : View.VISIBLE);

        // 当前正在 我的讲课-讲课详情 页面，只保留 复制 与 删除
        if (course) {
            tvRelay.setVisibility(View.GONE);
            tvCollection.setVisibility(View.GONE);
            tvCollectionOther.setVisibility(View.GONE);
            tvBack.setVisibility(View.GONE);
            tvMoreSelected.setVisibility(View.GONE);
            tvReplay.setVisibility(View.GONE);
            tvRecord.setVisibility(View.GONE);
        }

        if (message.getFromUserId().equals(CoreManager.requireSelf(MyApplication.getInstance()).getUserId())) {// 只录制自己的
            ChatRecordHelper.instance().iniText(tvRecord, message);
        } else {
            tvRecord.setVisibility(View.GONE);
        }

        if (isDevice) {// 正在‘我的设备’聊天界面 隐藏讲课
            tvRecord.setVisibility(View.GONE);
        }
        //mMenuView.findViewById(R.id.item_chat_text_ll).setBackgroundResource(R.drawable.bg_chat_text_long_v2);
    }

    /*
    判断当前消息已发送的时间是否超过五分钟
     */
    private boolean judgeTime(long timeSend) {
        return timeSend + 300 < TimeUtils.sk_time_current_time();
    }









    public static void setViewPivotRatio(final View v, final float pvX, final float pvY) {
        if (v == null) return;
        v.post(new Runnable() {
            @Override
            public void run() {
                v.setPivotX(v.getWidth() * pvX);
                v.setPivotY(v.getHeight() * pvY);
            }
        });
    }

    @Override
    protected Animation onCreateShowAnimation(int width, int height) {
        return AnimationHelper.asAnimation()
                .withAlpha(AlphaConfig.IN)
                .toShow();
    }

    @Override
    protected Animation onCreateDismissAnimation(int width, int height) {
        return AnimationHelper.asAnimation()
                .withAlpha(AlphaConfig.OUT)
                .toDismiss();
    }

    @Override
    public void onPopupLayout(@NonNull Rect popupRect, @NonNull Rect anchorRect) {
        int gravity = computeGravity(popupRect, anchorRect);

        boolean verticalCenter = false;
        switch (gravity & Gravity.VERTICAL_GRAVITY_MASK) {
            case Gravity.TOP:
                ivArrow.setVisibility(View.VISIBLE);
               // ivArrow.setTranslationX((popupRect.width() + popupRect.width()/4 - ivArrow.getWidth()) >> 1);
                ivArrow.setTranslationY(popupRect.height() - ivArrow.getHeight());
                ivArrow.setRotation(0f);
                break;
            case Gravity.BOTTOM:
                ivArrow.setVisibility(View.VISIBLE);
                //ivArrow.setTranslationX((popupRect.width() + popupRect.width()/4 - ivArrow.getWidth()) >> 1);
                ivArrow.setTranslationY(0);
                ivArrow.setRotation(180f);
                break;
            case Gravity.CENTER_VERTICAL:
                verticalCenter = true;
                break;
        }
        switch (gravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
            case Gravity.LEFT:
                ivArrow.setVisibility(View.VISIBLE);
                ivArrow.setTranslationX((popupRect.width()*3/4));

/*                ivArrow.setTranslationY((popupRect.height() - ivArrow.getHeight()) >> 1);
                ivArrow.setRotation(270f);*/
                break;
            case Gravity.RIGHT:
                ivArrow.setVisibility(View.VISIBLE);
                ivArrow.setTranslationX(popupRect.width()/4);
//                ivArrow.setTranslationX(0);
//                ivArrow.setTranslationY((popupRect.height() - ivArrow.getHeight()) >> 1);


//                ivArrow.setRotation(90f);
                break;
            case Gravity.CENTER_HORIZONTAL:
                ivArrow.setVisibility(verticalCenter ? View.INVISIBLE : View.VISIBLE);
                break;
        }
    }
}


/**
 * 聊天消息长按事件
 */
//public class ChatTextClickPpWindow extends BasePopupWindow {
//    private View mMenuView;
//    private TextView tvCopy;
//    private TextView tvRelay;
//    private TextView tvCollection;// 存表情
//    private TextView tvCollectionOther; // 收藏其他类型的消息
//    private TextView tvBack;
//    private TextView tvReplay;
//    private TextView tvDel;
//    private TextView tvMoreSelected;
//    // 开始 & 停止录制
//    private TextView tvRecord;
//    private TextView tvSpeaker;
//    private TextView tvTimer;
//
//    private int mWidth, mHeight;
//
//    private boolean isGroup;
//    private boolean isDevice;
//    private int mRole;
//
//    public ChatTextClickPpWindow(Context context, View.OnClickListener listener,
//                                 final ChatMessage type, final String toUserId, boolean course,
//                                 boolean group, boolean device, int role) {
//        super(context);
//
//        BasePopupSDK.getInstance().init(context);
//        LayoutInflater inflater = LayoutInflater.from(context);
//        mMenuView = inflater.inflate(R.layout.item_chat_long_click, null);
//        // mMenuView = inflater.inflate(R.layout.item_chat_long_click_list_style, null);
//
//        this.isGroup = group;
//        this.isDevice = device;
//        this.mRole = role;
//
//        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
//        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
//        mMenuView.measure(w, h);
//        // 获取PopWindow宽和高
//        mHeight = mMenuView.getMeasuredHeight();
//        mWidth = mMenuView.getMeasuredWidth();
//
//        tvCopy = (TextView) mMenuView.findViewById(R.id.item_chat_copy_tv);
//        tvRelay = (TextView) mMenuView.findViewById(R.id.item_chat_relay_tv);
//        tvCollection = (TextView) mMenuView.findViewById(R.id.item_chat_collection_tv);
//        tvCollectionOther = (TextView) mMenuView.findViewById(R.id.collection_other);
//        tvBack = (TextView) mMenuView.findViewById(R.id.item_chat_back_tv);
//        tvReplay = (TextView) mMenuView.findViewById(R.id.item_chat_replay_tv);
//        tvDel = (TextView) mMenuView.findViewById(R.id.item_chat_del_tv);
//        tvMoreSelected = (TextView) mMenuView.findViewById(R.id.item_chat_more_select);
//        tvRecord = (TextView) mMenuView.findViewById(R.id.item_chat_record);
//        tvSpeaker = (TextView) mMenuView.findViewById(R.id.item_chat_speaker);
//        tvTimer = (TextView) mMenuView.findViewById(R.id.item_chat_timer);
//
//        if (type.getIsReadDel()) {
//            tvRecord.setVisibility(View.GONE);
//        }
//        // 仅语音显示，扬声器、听筒切换 && 仅限聊天界面
//        if (type.getType() == XmppMessage.TYPE_VOICE
//                && !TextUtils.equals(MyApplication.IsRingId, "Empty")) {
//            tvSpeaker.setVisibility(View.VISIBLE);
//        }
//        boolean isSpeaker = PreferenceUtils.getBoolean(MyApplication.getContext(),
//                Constants.SPEAKER_AUTO_SWITCH + CoreManager.requireSelf(MyApplication.getContext()).getUserId(), true);
//        tvSpeaker.setText(isSpeaker ? MyApplication.getContext().getString(R.string.chat_earpiece) : MyApplication.getContext().getString(R.string.chat_speaker));
//        tvSpeaker.setOnClickListener(v -> {
//            PreferenceUtils.putBoolean(MyApplication.getContext(),
//                    Constants.SPEAKER_AUTO_SWITCH + CoreManager.requireSelf(MyApplication.getContext()).getUserId(), !isSpeaker);
//            // 通知聊天界面刷新
//            EventBus.getDefault().post(new EventNotifyByTag(EventNotifyByTag.Speak));
//            dismiss();
//        });
//
//        //设置SelectPicPopupWindow的View
//        this.setContentView(mMenuView);
//
//        //设置SelectPicPopupWindow弹出窗体的宽
//        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//
//        //之前是0.9 现在改成1 大雄说要居中好看一些
//        //mWidth = (int) (manager.getDefaultDisplay().getWidth() * 1);
//        //this.setWidth(mWidth);
//        //	 this.setWidth(ViewPiexlUtil.dp2px(context,200));
//        //设置SelectPicPopupWindow弹出窗体的高
//        //this.setHeight(LayoutParams.WRAP_CONTENT);
//        //设置SelectPicPopupWindow弹出窗体可点击
//        //this.setFocusable(true);
//        //设置SelectPicPopupWindow弹出窗体动画效果
//        //this.setAnimationStyle(R.style.Buttom_Popwindow);
//        //实例化一个ColorDrawable颜色为半透明
//        ColorDrawable dw = new ColorDrawable(0000000000);
//        //设置SelectPicPopupWindow弹出窗体的背景
//        //this.setBackgroundDrawable(dw);
//        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
//        /*mMenuView.setOnTouchListener(new View.OnTouchListener() {
//
//            public boolean onTouch(View v, MotionEvent event) {
//                int height = mMenuView.findViewById(R.id.pop_layout).getTop();
//                int bottom = mMenuView.findViewById(R.id.pop_layout).getBottom();
//                int y = (int) event.getY();
//                if (event.getAction() == MotionEvent.ACTION_UP) {
//                    if (y < height) {
//                        dismiss();
//                    } else if (y > bottom) {
//                        dismiss();
//                    }
//                }
//                return true;
//            }
//        });*/
//
//
////        if(type.isMySend()){
////            this.setLayoutDirection(LayoutDirection.LTR);
////        }else {
////            this.setLayoutDirection(LayoutDirection.RTL);
////        }
//        hideButton(type, course);
//        // 设置按钮监听
//        tvCopy.setOnClickListener(listener);
//        tvRelay.setOnClickListener(listener);
//        tvCollection.setOnClickListener(listener);
//        tvCollectionOther.setOnClickListener(listener);
//        tvBack.setOnClickListener(listener);
//        tvReplay.setOnClickListener(listener);
//        tvDel.setOnClickListener(listener);
//        tvMoreSelected.setOnClickListener(listener);
//        tvTimer.setOnClickListener(listener);
//        tvRecord.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (ChatRecordHelper.instance().getState() == ChatRecordHelper.STATE_UN_RECORD) {
//                    // 未录制 --> 开始录制
//                    ChatRecordHelper.instance().start(type);
//                    String tip;
//                    if (MyApplication.IS_SUPPORT_SECURE_CHAT) {
//                        tip = context.getString(R.string.course_support_type)
//                                + context.getString(R.string.dont_support_tip, context.getString(R.string.record_course_tip));
//                    } else {
//                        tip = context.getString(R.string.course_support_type);
//                    }
//                    DialogHelper.tipDialog(context, tip);
//                } else {
//                    // 停止录制
//                    ChatRecordHelper.instance().stop(type, toUserId);
//                }
//                dismiss();
//            }
//        });
//    }
//
//    public int getWidth() {
//        return mWidth;
//    }
//
//    public int getHeight() {
//        return mHeight;
//    }
//
//    /*
//    根据消息类型隐藏部分操作
//     */
//    private void hideButton(ChatMessage message, boolean course) {
//        int type = message.getType();
//        // 文本类型可复制
//        if (type != XmppMessage.TYPE_TEXT) {
//            tvCopy.setVisibility(View.GONE);
//        } else {
//            tvCopy.setVisibility(View.VISIBLE);
//        }
//
//        // 图片类型可存表情
//        if (type == XmppMessage.TYPE_IMAGE) {
//            tvCollection.setVisibility(View.VISIBLE);
//        } else {
//            tvCollection.setVisibility(View.GONE);
//        }
//
//        // 文本、图片、语音、视频、文件类型可收藏
//        if (type == XmppMessage.TYPE_LOCATION ||type == XmppMessage.TYPE_TEXT || type == XmppMessage.TYPE_IMAGE || type == XmppMessage.TYPE_VOICE || type == XmppMessage.TYPE_VIDEO || type == XmppMessage.TYPE_FILE) {
//            tvCollectionOther.setVisibility(View.VISIBLE);
//        } else {
//            tvCollectionOther.setVisibility(View.GONE);
//        }
//
//        // 撤回
//        if (isGroup) {
//            if ((message.isMySend() || mRole == 1 || mRole == 2) && type != XmppMessage.TYPE_RED) {
//                tvBack.setVisibility(View.VISIBLE);
//            } else {
//                tvBack.setVisibility(View.GONE);
//            }
//        } else {
//            if (!message.isMySend()
//                    || type == XmppMessage.TYPE_RED
//                    || type == XmppMessage.TYPE_TRANSFER
//                    || ((type >= XmppMessage.TYPE_IS_CONNECT_VOICE && type <= XmppMessage.TYPE_EXIT_VOICE))
//                    || type == XmppMessage.TYPE_SECURE_LOST_KEY) {
//                // 该条消息 NotSendByMe || 红包 || 音视频通话 类型不可撤回
//                tvBack.setVisibility(View.GONE);
//            } else {
//                tvBack.setVisibility(View.VISIBLE);
//                /*if (judgeTime(message.getTimeSend())) {
//                    // 超时不可撤回
//                    tvBack.setVisibility(View.GONE);
//                } else {
//                    tvBack.setVisibility(View.VISIBLE);
//                }*/
//            }
//        }
//
//        // 红包 || 音视频通话 类型不可转发
//        if (type == XmppMessage.TYPE_RED
//                || type == XmppMessage.TYPE_TRANSFER
//                || (type >= XmppMessage.TYPE_IS_CONNECT_VOICE && type <= XmppMessage.TYPE_EXIT_VOICE)
//                || type == XmppMessage.TYPE_SECURE_LOST_KEY) {
//            tvRelay.setVisibility(View.GONE);
//        } else {
//            tvRelay.setVisibility(View.VISIBLE);
//        }
//
//        // 阅后即焚消息不支持回复
//        tvReplay.setVisibility(message.getIsReadDel() ? View.GONE : View.VISIBLE);
//
//        // 当前正在 我的讲课-讲课详情 页面，只保留 复制 与 删除
//        if (course) {
//            tvRelay.setVisibility(View.GONE);
//            tvCollection.setVisibility(View.GONE);
//            tvCollectionOther.setVisibility(View.GONE);
//            tvBack.setVisibility(View.GONE);
//            tvMoreSelected.setVisibility(View.GONE);
//            tvReplay.setVisibility(View.GONE);
//            tvRecord.setVisibility(View.GONE);
//        }
//
//        if (message.getFromUserId().equals(CoreManager.requireSelf(MyApplication.getInstance()).getUserId())) {// 只录制自己的
//            ChatRecordHelper.instance().iniText(tvRecord, message);
//        } else {
//            tvRecord.setVisibility(View.GONE);
//        }
//
//        if (isDevice) {// 正在‘我的设备’聊天界面 隐藏讲课
//            tvRecord.setVisibility(View.GONE);
//        }
//        mMenuView.findViewById(R.id.item_chat_text_ll).setBackgroundResource(R.drawable.bg_chat_text_long_v2);
//    }
//
//    /*
//    判断当前消息已发送的时间是否超过五分钟
//     */
//    private boolean judgeTime(long timeSend) {
//        return timeSend + 300 < TimeUtils.sk_time_current_time();
//    }
//}
