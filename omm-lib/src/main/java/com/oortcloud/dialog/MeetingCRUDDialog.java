package com.oortcloud.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oort.weichat.R;
import com.oort.weichat.bean.circle.PublicMessage;
import com.oort.weichat.util.ScreenUtil;
import com.oort.weichat.util.ToastUtil;
import com.oortcloud.activity.MeetingActivity;
import com.oortcloud.activity.RepeatActivity;
import com.oortcloud.activity.ReportActivity;
import com.oortcloud.bean.Result;
import com.oortcloud.bean.meeting.MeetingInfo;
import com.oortcloud.login.net.RequesManager;
import com.oortcloud.login.net.utils.RxBus;

/**
 * @filename:
 * @author: zzj/@date: 2020/12/17 18:22
 * @version： v1.0
 * @function： 会议修改 删除 选项框
 */
public class MeetingCRUDDialog  extends Dialog implements View.OnClickListener {

    private Context mContext;
    private View view;
    private MeetingInfo mMeetingInfo;
    public MeetingCRUDDialog(@NonNull Context context , MeetingInfo meetingInfo) {
        super(context, R.style.BottomDialog);
        mContext = context;
        mMeetingInfo = meetingInfo;
        initDialogStyle();

        Window o = getWindow();
        WindowManager.LayoutParams lp = o.getAttributes();
        lp.width = (int) (ScreenUtil.getScreenWidth(getContext()) *1);
        lp.gravity = Gravity.BOTTOM;
        o.setAttributes(lp);

    }
    private void initDialogStyle(){
        view=  LayoutInflater.from(getContext()).inflate(R.layout.dialog_meeting_crud_layout, null);
        setContentView(view);
        view.findViewById(R.id.meeting_c).setOnClickListener(this);
        view.findViewById(R.id.meeting_r).setOnClickListener(this);
        view.findViewById(R.id.meeting_u).setOnClickListener(this);
        view.findViewById(R.id.meeting_d).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.meeting_c) {
        } else if (id == R.id.meeting_r) {
        } else if (id == R.id.meeting_u) {
            editMeeting();
            dismiss();
        } else if (id == R.id.meeting_d) {
            deleteMeeting();
        }
        dismiss();
    }

    public MeetingCRUDDialog setTitle(String title){

        return this;
    }
    public MeetingCRUDDialog setConfirmListener(LiveSettingDialog.ConfirmListener confirmListener){

        if (confirmListener != null){
            view.findViewById(R.id.btn_enter).setOnClickListener(v -> {


                dismiss();
            });
        }
        return this;
    }

    public interface ConfirmListener {
        void onConfirmListener(String roomName , String roomDesc , boolean check);
    }
    private void deleteMeeting(){
        RequesManager.deleteMeeting(mMeetingInfo.getUid()).subscribe(new RxBus.BusObserver<String>() {
            @Override
            public void onNext(String s) {

                Result result = new Gson().fromJson(s, new TypeToken<Result>() {}.getType());

                if (result.isOk()) {
                    if (mContext instanceof MeetingActivity){
                        ((MeetingActivity)mContext).initData();
                    }
                    ToastUtil.showToast(mContext , "会议删除成功");
                    dismiss();
                }
            }

            @Override
            public void onError(Throwable e) {

                ToastUtil.showToast(mContext , "会议删除失败");
                dismiss();
            }
        });

    }
    private void editMeeting(){
        new MeetingDialog(mContext).setTitle("编辑会议").setTheme(mMeetingInfo.getName())
                .setNotice(mMeetingInfo.getContent()).setConfirmListener((String theme , String notice) ->{

            RequesManager.editMeeting(mMeetingInfo.getUid() , theme , notice ).subscribe(new RxBus.BusObserver<String>() {
                @Override
                public void onNext(String s) {

                    Result result = new Gson().fromJson(s, new TypeToken<Result>() {}.getType());

                    if (result.isOk()) {
                        if (mContext instanceof MeetingActivity){
                            ((MeetingActivity)mContext).initData();
                            ToastUtil.showToast(mContext , "会议修改成功");
                        }
                    }
                }

                @Override
                public void onError(Throwable e) {
                    ToastUtil.showToast(mContext , "会议修改失败");

                }
            });

        }).show();
    }
}
