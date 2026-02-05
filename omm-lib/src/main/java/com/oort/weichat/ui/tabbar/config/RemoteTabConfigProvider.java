package com.oort.weichat.ui.tabbar.config;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oort.weichat.R;
import com.oort.weichat.bean.redpacket.TabConfig;
import com.oort.weichat.ui.tabbar.model.TabModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 后台配置提供器：从接口获取配置，支持原生/Web Fragment混合
 */
public class RemoteTabConfigProvider implements TabConfigProvider {
    private final Context mContext;
    private final String mBackendUrl;
    private List<TabModel> mRemoteTabs;

    public RemoteTabConfigProvider(Context context, String backendUrl) {
        this.mContext = context;
        this.mBackendUrl = backendUrl;
    }

    public RemoteTabConfigProvider(Context context, List<TabConfig.DataBean.InnerData.AppSetting.BottomConfig> listConfig) {
        this.mContext = context;
        mBackendUrl = "";
        mRemoteTabs = new ArrayList<>();
        mRemoteTabs.clear(); // 先清空旧数据
        for (TabConfig.DataBean.InnerData.AppSetting.BottomConfig bc : listConfig) {
            // TabId: 用 app_id 或 label 唯一标识
            String tabId = bc.getRelateApp() != null ? bc.getRelateApp().getApp_id() : bc.getLabel();

            // 标签文字
            String label = bc.getLabel();

            // 默认图标，本地占位资源，可根据实际替换
            int defaultIconRes = R.drawable.tab_home_normal_bg; // 替换成你项目的占位图标

            // 图标URL
            String iconUrl = bc.getIcon(); // 普通图标 URL
            String selectIconUrl = bc.getHighlightIcon(); // 选中图标 URL（可选择使用）

            // 目标APK
            String targetApk = bc.getRelateApp() != null ? bc.getRelateApp().getApk_url() : null;

            // 是否显示
            boolean isVisible = true; // 或者根据 bc.isDefine() 判断

            // Fragment 类型，假设全部为 Web 类型
            TabModel.FragmentType fragmentType = TabModel.FragmentType.WEB;
            String webUrl = targetApk; // 如果要打开 Web 页面，则填 URL，否则 null

            // Icon 类型
            TabModel.IconType iconType = TabModel.IconType.COLOR_IMAGE;

            String fragmentName = "";
            if(bc.getRelateApp().getApppackage().equals("com.oort.tab.home__void_apply")){
                fragmentName = "Fragment_home_parent";
                fragmentType = TabModel.FragmentType.NATIVE;
                defaultIconRes = R.drawable.tab_home_normal_bg;
            }else if(bc.getRelateApp().getApppackage().equals("com.oort.tab.message_void_apply")){
                fragmentName = "NewMessageFragment";
                fragmentType = TabModel.FragmentType.NATIVE;
                defaultIconRes = R.drawable.tab_chat_normal_bg;
            }else if(bc.getRelateApp().getApppackage().equals("com.oort.tab.contact_void_apply")){
                fragmentName = "NewFriendFragment";
                fragmentType = TabModel.FragmentType.NATIVE;
                defaultIconRes = R.drawable.tab_group_normal_bg;
            }else if(bc.getRelateApp().getApppackage().equals("com.oort.tab.dynamic_void_apply")){
                fragmentName = "DynamicFragment_tab";
                fragmentType = TabModel.FragmentType.NATIVE;
                defaultIconRes = R.drawable.tab_find_normal_bg;
            }else if(bc.getRelateApp().getApppackage().equals("com.oort.tab.me_void_apply")){
                fragmentName = "MeFragment";
                fragmentType = TabModel.FragmentType.NATIVE;
                defaultIconRes = R.drawable.tab_my_normal_bg;


            }
            // 构建 TabModel
            TabModel tabModel = new TabModel(
                    tabId,
                    label,
                    iconUrl,
                    selectIconUrl,
                    defaultIconRes,
                    targetApk,
                    0,          // unreadCount 初始值
                    iconType,
                    isVisible,
                    fragmentType,
                    fragmentName,       // nativeFragmentClassName, Web 不用填
                    webUrl,
                    bc.getRelateApp().getApppackage()
            );
            mRemoteTabs.add(tabModel);
        }

    }

    /**
     * 从后台获取配置（子线程调用）
     */
    public void fetchRemoteConfig() throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                .build();
        
        Request request = new Request.Builder().url(mBackendUrl).build();
        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) {
            throw new IOException("配置请求失败: " + response.code());
        }

        String configJson = response.body().string();
        mRemoteTabs = new Gson().fromJson(
                configJson,
                new TypeToken<List<TabModel>>() {}.getType()
        );
    }

    @Override
    public List<TabModel> getTabConfigs() {
        if (mRemoteTabs == null) {
            throw new IllegalStateException("请先调用fetchRemoteConfig()");
        }
        return mRemoteTabs;
    }

    @Override
    public TabConfigType getConfigType() {
        return TabConfigType.REMOTE_BACKEND;
    }
}
