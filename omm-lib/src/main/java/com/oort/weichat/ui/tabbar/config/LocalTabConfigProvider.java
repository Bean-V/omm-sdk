package com.oort.weichat.ui.tabbar.config;

import android.content.Context;

import com.oort.weichat.R;
import com.oort.weichat.fragment.Fragment_home_parent;
import com.oort.weichat.fragment.MeFragment;
import com.oort.weichat.fragment.dynamic.DynamicFragment_tab;
import com.oort.weichat.ui.tabbar.model.TabModel;
import com.oortcloud.revision.fragment.NewFriendFragment;
import com.oortcloud.revision.fragment.NewMessageFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 本地默认配置：仅加载原生Fragment（Home/Message/Contact/Mine）
 */
public class LocalTabConfigProvider implements TabConfigProvider {
    private final Context mContext;

    public LocalTabConfigProvider(Context context) {
        this.mContext = context;
    }

    @Override
    public List<TabModel> getTabConfigs() {
        List<TabModel> localTabs = new ArrayList<>();

        // 首页（原生Fragment+轮廓图标）
        localTabs.add(new TabModel(
                "tab_home",
                mContext.getString(R.string.tab_home),
                R.drawable.tab_home_normal_bg,
                Fragment_home_parent.class,
                TabModel.IconType.OUTLINE_ICON
        ));

        // 消息（原生Fragment+轮廓图标）
        localTabs.add(new TabModel(
                "tab_message",
                mContext.getString(R.string.tab_message),
                R.drawable.tab_chat_normal_bg,
                NewMessageFragment.class,
                TabModel.IconType.OUTLINE_ICON
        ));

        // 联系人（原生Fragment+轮廓图标）
        localTabs.add(new TabModel(
                "tab_contact",
                mContext.getString(R.string.tab_contact),
                R.drawable.tab_group_normal_bg,
                NewFriendFragment.class,
                TabModel.IconType.OUTLINE_ICON
        ));
        localTabs.add(new TabModel(
                "tab_dynamic",
                mContext.getString(com.sentaroh.android.upantool.R.string.tab_find),
                R.drawable.tab_find_normal_bg,
                DynamicFragment_tab.class,
                TabModel.IconType.OUTLINE_ICON
        ));

        // 我的（原生Fragment+轮廓图标）
        localTabs.add(new TabModel(
                "tab_mine",
                mContext.getString(R.string.tab_mine),
                R.drawable.tab_my_normal_bg,
                MeFragment.class,
                TabModel.IconType.OUTLINE_ICON
        ));

        return localTabs;
    }

    @Override
    public TabConfigType getConfigType() {
        return TabConfigType.LOCAL_DEFAULT;
    }
}
