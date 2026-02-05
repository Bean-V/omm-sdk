package com.oort.weichat.view.chatHolder;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

//import com.kedacom.kmedia.player.KPlayer;
//import com.kedacom.kmedia.player.KPlayerResource;
import com.oort.weichat.R;
import com.oort.weichat.bean.message.ChatMessage;
import com.oort.weichat.fragment.dynamic.DynamicActivityDynamicInfo;
import com.oort.weichat.fragment.home.HomeActivityNewsDetail;
import com.oort.weichat.helper.AvatarHelper;
import com.oortcloud.appstore.activity.AppManagerActivity;
import com.oortcloud.appstore.activity.AppManagerService;
import com.oortcloud.appstore.bean.AppInfo;
import com.oortcloud.appstore.db.DataInit;
import com.oortcloud.basemodule.constant.Constant;
import com.oortcloud.basemodule.utils.StringUtil;
import com.sentaroh.android.upantool.FileTool;

import org.json.JSONException;
import org.json.JSONObject;

class TextImgViewHolder extends AChatHolderInterface {

    TextView tvTitle;  // 主标题
    TextView tvText;   // 副标题
    ImageView ivImage; // 图像
    String mLinkUrl;
    String mType;
    String mAppid;
    String mParam;
    private String duuid;

    String mPack_name;

    @Override
    public int itemLayoutId(boolean isMysend) {
        return isMysend ? R.layout.chat_from_item_text_img : R.layout.chat_to_item_text_img;
    }

    @Override
    public void initView(View view) {
        tvTitle = view.findViewById(R.id.chat_title);
        tvText = view.findViewById(R.id.chat_text);
        ivImage = view.findViewById(R.id.chat_img);
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
            if (mType.equals("app")){
                mAppid = json.getString("appid");
                mParam = json.getString("param");
            }else if (mType.equals("share_app")){
                mPack_name = json.getString("package_name");
                AppInfo app = DataInit.getAppinfo(mPack_name);

                tile = app.getApplabel();
                sub = app.getIntro();
                img = app.getIcon_url();
            }else if (mType.equals("share_app_content")){
                mPack_name = json.getString("package_name");
                mLinkUrl = json.getString("path");
            }else if (mType.equals("share_news_content")){
                mParam = json.getString("param");
            }else if(mType.equals("dynamic")){

            }else if(mType.equals("dynamic_android")){

                duuid = json.getString("duuid");
            }else if(mType.equals("video_kd_android")){

                duuid = json.getString("sourceId");
            }else{
                //如果不是应用消息，链接需要转换成网关访问
                url = StringUtil.getUrlRelativePath(mLinkUrl);
                mLinkUrl = Constant.BASE_URL + url;
            }

            tvTitle.setText(tile);
            tvText.setText(sub);
            ivImage.setVisibility(View.VISIBLE);
            ivImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                if(mType.equals("dynamic") || mType.equals("dynamic_android")){

                    String atttype = json.getString("atttype");
                    if(atttype.equals("image") || atttype.equals("video")){
                        AvatarHelper.getInstance().displayUrl(img, ivImage);
                    }else if(atttype.equals("text")){
                        ivImage.setVisibility(View.GONE);
                    }else if(atttype.equals("audio")){
                        ivImage.setImageResource(R.mipmap.icon_dynamic_audio_icon);
                        //ivImage.setScaleType(ImageView.ScaleType.FIT_XY);
                        ViewGroup.LayoutParams lay = ivImage.getLayoutParams();
                    }else if(atttype.equals("attach")){
                        String attachName = json.getString("attachName");
                        ivImage.setImageResource(FileTool.getResIdFromFileNameBig(false,attachName));
                        //ivImage.setScaleType(ImageView.ScaleType.FIT_XY);
                    }else{
                        ivImage.setVisibility(View.GONE);
                    }
                }else {

                    if(img.isEmpty()){
                        ivImage.setVisibility(View.GONE);
                    }else {
                        AvatarHelper.getInstance().displayUrl(img, ivImage);
                    }
                }

        } catch (JSONException e) {
            ivImage.setVisibility(View.GONE);
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

        if(mType.equals("dynamic")){
           // intent = new Intent(appid + ".app.appmanager");
            intent = new Intent(mContext, AppManagerActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            String packagename;
            intent.putExtra("packageName", "com.work_dynamics.oort");
            intent.putExtra("params", mLinkUrl);
            mContext.startActivity(intent);
            return;
        }else if(mType.equals("share_app")){
            // intent = new Intent(appid + ".app.appmanager");
            intent = new Intent(mContext, AppManagerActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            String packagename;
            intent.putExtra("packageName",mPack_name);
            mContext.startActivity(intent);
            return;
        }
        if(mType.equals("share_app_content")){
            // intent = new Intent(appid + ".app.appmanager");
            intent = new Intent(mContext, AppManagerActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            String packagename;
            intent.putExtra("packageName",mPack_name);
            intent.putExtra("params", mLinkUrl);
            mContext.startActivity(intent);
            return;
        }
        if(mType.equals("share_news_content")){
            // intent = new Intent(appid + ".app.appmanager");

            HomeActivityNewsDetail.start(mContext,Integer.parseInt(mParam));
            return;
        }

        if(mType.equals("dynamic_android")){

            DynamicActivityDynamicInfo.start(mContext,duuid);
            return;
        }

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


        if (mType.equals("app")){
           startService(mAppid);

        }else{
            intent = new Intent(appid + ".web.container");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("url", mLinkUrl);
            mContext.startActivity(intent);
        }

    }


    private void  startService(String packageName){

        if(mContext == null){
            return;
        }
        //String packageName = "com.jwb_home.oort";
        String params = "";
        Intent intent = new Intent(mContext , AppManagerService.class);
        intent.putExtra("packageName" , packageName);
        intent.putExtra("params" , params);
        mContext.startService(intent);


    }

    @Override
    public boolean enableSendRead() {
        return true;
    }
}
