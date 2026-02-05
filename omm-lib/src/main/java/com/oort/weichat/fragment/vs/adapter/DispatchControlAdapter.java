package com.oort.weichat.fragment.vs.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.oort.imagepicksdk.ImagePickSdk;
import com.oort.imagepicksdk.PictureRecording;
import com.oort.imagepicksdk.model.ImagePickConfig;
import com.oort.weichat.R;
import com.oort.weichat.fragment.vs.file.up.FileUtils;
import com.oortcloud.oort_zhifayi.ActivityTasks;

import java.util.List;

import timber.log.Timber;

public class DispatchControlAdapter extends RecyclerView.Adapter<DispatchControlAdapter.ViewHolder> {
    private Context mContext;
    private String[] controlTexts = {"立即录音", "立即录像", "拍传  ", "实时图传"};
    private final int[] controlIcons = {
        R.mipmap.ic_voice_input,
        R.mipmap.ic_video_camera,
        R.mipmap.ic_camera,
        R.mipmap.ic_image_transmission
    };
    private final int[] controlBackgrounds = {
        R.drawable.gradient_blue_purple,
        R.drawable.gradient_green,
        R.drawable.gradient_purple_blue,
        R.drawable.gradient_orange
    };

    public DispatchControlAdapter(Context context){
        mContext = context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dispatch_control, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.ivDispatchIcon.setImageResource(controlIcons[position]);
        holder.tvDispatchText.setText(controlTexts[position]);
        holder.llDispatchButton.setBackgroundResource(controlBackgrounds[position]);
        holder.itemView.setOnClickListener(v -> {
            if (position ==0){
                recorder();
            }else if (position == 1){
                videoCamera();
            }else if (position == 2){
                photograph();
            }else{
                //实时图传
                mContext.startActivity(new Intent(mContext, ActivityTasks.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return controlTexts.length;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivDispatchIcon;
        TextView tvDispatchText;
        View llDispatchButton;

        ViewHolder(View itemView) {
            super(itemView);
            ivDispatchIcon = itemView.findViewById(R.id.iv_dispatch_icon);
            tvDispatchText = itemView.findViewById(R.id.tv_dispatch_text);
            llDispatchButton = itemView.findViewById(R.id.ll_dispatch_button);
        }
    }
    void recorder(){
        PictureRecording.recorder(mContext, new ImagePickConfig(), new ImagePickSdk.ImagePickFinish() {
            @Override
            public void imagePickFinsh(int code, List uris, List<String> paths) {
                upLoadFile(paths);
//                for(String urlPath : paths){
////                    Log.e("zq", "urlPath---" +urlPath);
//                    //Uri uri = Uri.parse(uristr);
//                    File file = new File(urlPath);//FileTool.getFileByUri(uri,DynamicSendActivity.this);
//
////                    FileUtils.uploadFile(urlPath, mContext);
//                }
            }
        });
    }
    void videoCamera(){
        PictureRecording.videotape(mContext, new ImagePickConfig(), new ImagePickSdk.ImagePickFinish() {
            @Override
            public void imagePickFinsh(int code, List uris, List<String> paths) {
                upLoadFile(paths);

//                for(String urlPath : paths){
//                    Log.e("zq", "urlPath---" +urlPath);
//                    //Uri uri = Uri.parse(uristr);
//                    File file = new File(urlPath);//FileTool.getFileByUri(uri,DynamicSendActivity.this);
//                    new Thread(()->{
//                        FileUtils.uploadFile(mContext, urlPath);
//                    }).start();
//
//                }
            }
        });
    }
    void photograph(){
        PictureRecording.image(mContext, new ImagePickConfig(), new ImagePickSdk.ImagePickFinish() {
            @Override
            public void imagePickFinsh(int code, List uris, List<String> paths) {
                upLoadFile(paths);

            }
        });
    }

    public void upLoadFile(List<String> paths){
        if (paths == null || paths.isEmpty())
            return;
        Timber.tag("zq").e("urlPath---%s", paths);
        new Thread(()->{
            FileUtils.uploadFiles(mContext, paths.toArray(new String[0]));
        }).start();
    }


} 