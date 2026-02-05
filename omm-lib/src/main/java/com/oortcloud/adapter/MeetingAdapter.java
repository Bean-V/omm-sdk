package com.oortcloud.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.oort.weichat.R;
import com.oort.weichat.util.ToastUtil;
import com.oortcloud.basemodule.user.UserInfoUtils;
import com.oortcloud.bean.meeting.MeetingInfo;
import com.oortcloud.dialog.MeetingCRUDDialog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @filename:
 * @author: zzj/@date: 2020/12/17 14:34
 * @version： v1.0
 * @function：
 */
public class MeetingAdapter extends RecyclerView.Adapter {



    private Context mContext;
    private List<MeetingInfo> mData;
    private String uuid ;
    public MeetingAdapter(Context context , List data){
        this.mContext = context;
        this.mData = data;
        uuid = UserInfoUtils.getInstance(mContext).getLoginUserInfo().getOort_uuid();

    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_meeting_layout, viewGroup , false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        MeetingInfo meetingInfo = mData.get(i);
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.title.setText(meetingInfo.getName());
        viewHolder.content.setText(meetingInfo.getContent());
        viewHolder.emcee.setText("创建人：" + meetingInfo.getCreator());
        Date date = new Date(meetingInfo.getCreated_on() *1000L);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// HH:mm:ss
        viewHolder.time.setText("时间："+simpleDateFormat.format(date));


        int status = meetingInfo.getStatus();
        if (status == 1){
            viewHolder.status.setText("进行中");
        }else if (status == 2){
            viewHolder.status.setText("未开始");
        }else if (status == 3){
            viewHolder.status.setText("已结束");
        }
        viewHolder.numbar.setText(String.valueOf(meetingInfo.getNumber()));

        viewHolder.view.setOnClickListener(v -> {

            if (meetingInfo.getUuid().equals(uuid) || status == 1){
                Intent intent = new Intent(mContext.getApplicationInfo().processName +".Jitsi_connecting_second");
                intent.putExtra("meeting_obj" , meetingInfo);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }else {
                if (status == 2){
                    ToastUtil.showToast(mContext, "会议未开始");
                }else if (status == 3){
                    ToastUtil.showToast(mContext, "会议已结束");
                }
            }

        });

        viewHolder.view.setOnLongClickListener(v -> {
            if (meetingInfo.getUuid().equals(uuid)){
                new MeetingCRUDDialog(mContext , meetingInfo).show();
            }else {
                ToastUtil.showToast(mContext, "抱歉，您不是会议创建人");
            }

            return true;
        });

    }

    public void  setData(List list){
        mData = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder{
        View view ;
        ImageView meting_pic;
        TextView title;
        TextView content;
        TextView emcee;
        TextView time;
        TextView status;
        TextView numbar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            meting_pic = itemView.findViewById(R.id.meeting_default);
            title = itemView.findViewById(R.id.meeting_title);
            content = itemView.findViewById(R.id.meeting_content);
            emcee = itemView.findViewById(R.id.meeting_emcee_name);
            time = itemView.findViewById(R.id.meeting_create_time);
            status = itemView.findViewById(R.id.meeting_status);
            numbar = itemView.findViewById(R.id.meeting_number);
        }
    }
}
