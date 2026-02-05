package com.oort.weichat.view.chatHolder;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.oort.weichat.adapter.TextImgManyAdapter;
import com.oort.weichat.bean.TextImgBean;
import com.oort.weichat.bean.message.ChatMessage;
import com.oort.weichat.helper.AvatarHelper;
import com.oort.weichat.ui.tool.WebViewActivity;
import com.oort.weichat.R;
import com.oortcloud.basemodule.constant.Constant;
import com.oortcloud.basemodule.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.oort.weichat.luo.camfilter.GPUCamImgOperator.context;

class TextImgManyHolder extends AChatHolderInterface {

    ListView lvList;
    TextView tvTitle;
    ImageView ivImage;

    String mLinkUrl;

    @Override
    public int itemLayoutId(boolean isMysend) {
        return isMysend ? R.layout.chat_from_item_text_img_many : R.layout.chat_to_item_text_img_many;
    }

    @Override
    public void initView(View view) {
        lvList = view.findViewById(R.id.chat_item_content);
        tvTitle = view.findViewById(R.id.chat_title);
        ivImage = view.findViewById(R.id.chat_img);
        mRootView = view.findViewById(R.id.chat_warp_view);
    }

    @Override
    public void fillData(ChatMessage message) {
        try {
            JSONArray jsonArray = new JSONArray(message.getContent());
            if (jsonArray.length() > 0) {
                List<TextImgBean> datas = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject json = jsonArray.getJSONObject(i);
                    TextImgBean info = new TextImgBean();
                    info.title = json.getString("title");
                    info.img = json.getString("img");
                    //链接转换成通过网关可以访问的链接
                    String url = StringUtil.getUrlRelativePath(info.img);
                    info.img = Constant.BASE_URL + url;
                    info.url = json.getString("url");
                    url = StringUtil.getUrlRelativePath(info.url);
                    info.url = Constant.BASE_URL + url;

                    if (i > 0) {
                        datas.add(info);
                    } else {
                        tvTitle.setText(info.title);
                        AvatarHelper.getInstance().displayUrl(info.img, ivImage);
                        mLinkUrl = info.url;
                    }
                }

                lvList.setAdapter(new TextImgManyAdapter(mContext, datas));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onRootClick(View v) {
//        Intent intent = new Intent(mContext, WebViewActivity.class);
//        intent.putExtra(WebViewActivity.EXTRA_URL, mLinkUrl);
        String appid = mContext.getApplicationInfo().processName;
        Intent intent = new Intent(appid+ ".web.container");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("url", mLinkUrl);
        mContext.startActivity(intent);
    }

    @Override
    public boolean enableSendRead() {
        return true;
    }
}
