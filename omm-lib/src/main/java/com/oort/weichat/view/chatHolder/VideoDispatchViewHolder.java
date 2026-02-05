package com.oort.weichat.view.chatHolder;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

//import com.kedacom.kmedia.player.KPlayer;
//import com.kedacom.kmedia.player.KPlayerResource;
import com.oort.weichat.R;
import com.oort.weichat.bean.message.ChatMessage;
import com.oortcloud.basemodule.constant.Constant;
import com.oortcloud.basemodule.utils.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

class VideoDispatchViewHolder extends AChatHolderInterface {

    TextView tvTitle;  // 主标题
    TextView tvText;   // 副标题
//    ImageView ivImage; // 图像
    String mLinkUrl;
    String mType;
    String mAppid;
    String mParam;
    private String duuid;

    String mPack_name;

    @Override
    public int itemLayoutId(boolean isMysend) {
        return isMysend ? R.layout.chat_from_item_video_dispatch : R.layout.chat_to_item_video_dispatch;
    }

    @Override
    public void initView(View view) {
        tvTitle = view.findViewById(R.id.chat_title);
        tvText = view.findViewById(R.id.chat_text);
//        ivImage = view.findViewById(R.id.chat_img);
        mRootView = view.findViewById(R.id.chat_warp_view);
    }

    @Override
    public void fillData(ChatMessage message) {
        try {
            JSONObject json = new JSONObject(message.getContent());


            String tile = json.optString("title") == null ? "" :  json.optString("title");
            String sub = json.optString("sub") == null ? "" :  json.optString("sub");
            String img = json.optString("img") == null ? "" :  json.optString("img");

            String url = "";
            if(!img.isEmpty()) {
               url = StringUtil.getUrlRelativePath(img);

                img = Constant.BASE_URL + url;

                mLinkUrl = json.optString("url");
            }

            mType = json.getString("type");
           if(mType.equals("video_kd_android")){

                duuid = json.getString("sourceId");
            }else{
                //如果不是应用消息，链接需要转换成网关访问
                url = StringUtil.getUrlRelativePath(mLinkUrl);
                mLinkUrl = Constant.BASE_URL + url;
            }

            tvTitle.setText(tile);
            tvText.setText(sub);


        } catch (JSONException e) {

            e.printStackTrace();
        }
    }

    @Override
    protected void onRootClick(View v) {
        /*Intent intent = new Intent(mContext, WebViewActivity.class);
        intent.putExtra(WebViewActivity.EXTRA_URL, mLinkUrl);
        mContext.startActivity(intent);*/
        String appid = mContext.getApplicationInfo().processName;
        Intent intent;
        if(mType.equals("video_kd_android")){

//            KPlayer.play(new KPlayerResource() {
//                @NonNull
//                @Override
//                public String debugString() {
//                    return "";
//                }
//
//                @Override
//                public String getResourceId() {
//                    return duuid;
//                }//62020000021323000048
//
//                @Override
//                public Integer[] getResourceMediaIds() {
//                    return new Integer[]{72};
//                }
//
//                @Override
//                public String getResourceName() {
//                    return tvText.getText().toString();
//                }
//
//                @Override
//                public boolean getResourceStarred() {
//                    return false;
//                }
//
//                @Override
//                public void setResourceStarred(boolean value) {
//                    // 收藏功能回调，不需要可做空实现
//                }
//            });
            return;
        }
    }
    @Override
    public boolean enableSendRead() {
        return true;
    }
}
