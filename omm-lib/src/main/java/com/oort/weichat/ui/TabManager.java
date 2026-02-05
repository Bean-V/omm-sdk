package com.oort.weichat.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.oort.weichat.R;
import com.oort.weichat.fragment.DynamicFragment;
import com.oort.weichat.fragment.HomeFragment;
import com.oort.weichat.fragment.MeFragment;
import com.oort.weichat.fragment.MessageFragment;
import com.oortcloud.basemodule.utils.BottomConfigBean;
import com.oortcloud.revision.fragment.NewFriendFragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tab管理工具类，支持默认Tab和动态配置Tab
 * 移除了皮肤框架依赖，使用系统默认资源管理
 */
public class TabManager {
    // 日志标签（便于过滤日志）
    private static final String TAG = "TabManager";

    // 常量定义
    private static final int TAB_TEXT_SIZE_SP = 12;
    private static final int TAB_DRAWABLE_PADDING_DP = 8;
    private static final int DEFAULT_TAB_ICON = R.drawable.icon_01;
    private static final int TAB_SELECTED_COLOR = R.color.tab_selected_color; // 需要在colors.xml中定义
    private static final int TAB_UNSELECTED_COLOR = R.color.tab_unselected_color; // 需要在colors.xml中定义

    // 上下文与核心组件
    private final Context mContext;
    private final RadioGroup mRadioGroup;
    private final FragmentManager mFragmentManager;
    private final int mContainerId; // Fragment容器ID

    // Tab状态管理
    private int mCurrentTabId = -1;
    private Fragment mCurrentFragment;
    private List<BottomConfigBean> mDynamicTabConfigs;
    private final Map<Integer, Fragment> mFragmentCache = new HashMap<>(); // 缓存Fragment避免重复创建

    // 回调接口（对外暴露事件）
    private OnTabChangeListener mTabChangeListener;

    // 默认Tab的ID（需与xml中定义一致）
    public static final int DEFAULT_TAB_HOME = R.id.rb_tab_0;
    public static final int DEFAULT_TAB_MSG = R.id.rb_tab_1;
    public static final int DEFAULT_TAB_CONTACT = R.id.rb_tab_2;
    public static final int DEFAULT_TAB_DYNAMIC = R.id.rb_tab_3;
    public static final int DEFAULT_TAB_ME = R.id.rb_tab_4;

    // 在TabManager中添加成员变量
    private SimpleImageLoader mImageLoader;

    /**
     * 构造函数
     * @param context 上下文
     * @param radioGroup 底部Tab容器
     * @param fragmentManager Fragment管理器
     * @param containerId Fragment容器布局ID
     */
    public TabManager(Context context, RadioGroup radioGroup,
                      FragmentManager fragmentManager, int containerId) {
        this.mContext = context;
        this.mRadioGroup = radioGroup;
        this.mFragmentManager = fragmentManager;
        this.mContainerId = containerId;
        this.mImageLoader = new SimpleImageLoader(context);
        Log.d(TAG, "初始化TabManager：context=" + context.getClass().getSimpleName()
                + ", containerId=" + containerId + ", radioGroup子项数量=" + radioGroup.getChildCount());
        initRadioGroupListener();
    }

    /**
     * 初始化RadioGroup的选中监听
     */
    private void initRadioGroupListener() {
        Log.d(TAG, "初始化RadioGroup选中监听器");
        mRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            Log.d(TAG, "RadioGroup选中变化：新选中ID=" + checkedId + ", 当前选中ID=" + mCurrentTabId);
            if (checkedId == -1 || checkedId == mCurrentTabId) {
                Log.d(TAG, "选中ID无效或与当前一致，忽略切换");
                return;
            }

            // 触发Tab切换逻辑
            handleTabChange(checkedId);
        });
    }

    /**
     * 处理Tab切换
     */
    private void handleTabChange(int checkedId) {
        Log.d(TAG, "开始处理Tab切换：目标TabID=" + checkedId + ", 旧TabID=" + mCurrentTabId);

        // 创建并显示对应的Fragment
        Fragment targetFragment = createFragmentByTabId(checkedId);
        if (targetFragment != null) {
            mCurrentTabId = checkedId;
            updateTabStyles();
            Log.d(TAG, "成功创建目标Fragment：" + targetFragment.getClass().getSimpleName());
            switchFragment(targetFragment);


            // 更新Tab样式（选中状态）


            // 回调通知外部
            if (mTabChangeListener != null) {
                mTabChangeListener.onTabChanged(checkedId, targetFragment);
                Log.d(TAG, "已通知外部Tab切换完成：TabID=" + checkedId + ", Fragment=" + targetFragment.getClass().getSimpleName());
            }
        } else {
            Log.e(TAG, "目标TabID=" + checkedId + "对应的Fragment创建失败！");
        }
    }

    /**
     * 切换到指定Fragment
     */
    private void switchFragment(Fragment targetFragment) {
        if (targetFragment == mCurrentFragment) {
            Log.d(TAG, "目标Fragment与当前Fragment相同，无需切换：" + targetFragment.getClass().getSimpleName());
            return;
        }

        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        String targetTag = getFragmentTag(targetFragment);
        String currentTag = mCurrentFragment != null ? getFragmentTag(mCurrentFragment) : "null";

        Log.d(TAG, "开始切换Fragment：当前Fragment=" + currentTag + ", 目标Fragment=" + targetTag);

        // 缓存Fragment
        mFragmentCache.put(targetFragment.hashCode(), targetFragment);
        Log.d(TAG, "缓存目标Fragment：hashCode=" + targetFragment.hashCode() + ", tag=" + targetTag);

        // 添加或显示目标Fragment
        if (!targetFragment.isAdded()) {
            transaction.add(mContainerId, targetFragment, targetTag);
            Log.d(TAG, "目标Fragment未添加，执行add操作：" + targetTag);
        } else {
            transaction.show(targetFragment);
            Log.d(TAG, "目标Fragment已添加，执行show操作：" + targetTag);
        }

        // 隐藏当前Fragment
        if (mCurrentFragment != null) {
            transaction.hide(mCurrentFragment);
            Log.d(TAG, "隐藏当前Fragment：" + currentTag);
        }

        transaction.commitAllowingStateLoss();
        mCurrentFragment = targetFragment;
        Log.d(TAG, "Fragment切换完成：当前Fragment更新为" + targetTag);
    }

    /**
     * 根据TabId创建对应的Fragment
     */
    private Fragment createFragmentByTabId(int tabId) {
        Log.d(TAG, "根据TabID创建Fragment：tabId=" + tabId);

        // 动态Tab的Fragment创建逻辑
        if (mDynamicTabConfigs != null) {
            int position = findTabPositionById(tabId);
            Log.d(TAG, "动态Tab配置存在，查找位置：tabId=" + tabId + ", 位置=" + position);
            if (position != -1 && position < mDynamicTabConfigs.size()) {
                BottomConfigBean config = mDynamicTabConfigs.get(position);
                Fragment dynamicFragment = createDynamicFragment(config);
                Log.d(TAG, "创建动态Fragment：config.label=" + config.getLabel() + ", fragment=" + (dynamicFragment != null ? dynamicFragment.getClass().getSimpleName() : "null"));
                return dynamicFragment;
            }
        }

        // 默认Tab的Fragment创建逻辑
        Fragment defaultFragment = createDefaultFragment(tabId);
        Log.d(TAG, "创建默认Fragment：tabId=" + tabId + ", fragment=" + (defaultFragment != null ? defaultFragment.getClass().getSimpleName() : "null"));
        return defaultFragment;
    }

    /**
     * 创建默认Tab对应的Fragment
     */
    /**
     * 创建默认Tab对应的Fragment
     */
    private Fragment createDefaultFragment(int tabId) {
        Fragment fragment = null;
        // 用if-else替代switch-case，避免依赖R.id的常量性
        if (tabId == DEFAULT_TAB_HOME) {
            fragment = new HomeFragment();
        } else if (tabId == DEFAULT_TAB_MSG) {
            fragment = new MessageFragment();
        } else if (tabId == DEFAULT_TAB_CONTACT) {
            fragment = new NewFriendFragment();
        } else if (tabId == DEFAULT_TAB_DYNAMIC) {
            fragment = new DynamicFragment();
        } else if (tabId == DEFAULT_TAB_ME) {
            fragment = new MeFragment();
        } else {
            Log.w(TAG, "未知的默认TabID：" + tabId + "，使用EmptyFragment");
            fragment = new EmptyFragment();
        }
        return fragment;
    }

    /**
     * 创建动态配置的Fragment
     */
    private Fragment createDynamicFragment(BottomConfigBean config) {
        if (config == null) {
            Log.e(TAG, "动态Tab配置为null，无法创建Fragment");
            return new EmptyFragment();
        }
        Log.d(TAG, "创建动态Fragment：config.label=" + config.getLabel() + ", appPackage=" + (config.getRelateApp() != null ? config.getRelateApp().getApppackage() : "null"));
        if (config.getRelateApp() != null) {
            return HomeFragment.newInstance(config.getRelateApp().getApppackage());
        }
        return HomeFragment.newInstance(config.getLabel());
    }

    /**
     * 设置默认Tab（使用xml中定义的Tab）
     */
    public void setupDefaultTabs() {
        Log.d(TAG, "开始设置默认Tab");
        // 清除动态配置
        mDynamicTabConfigs = null;
        mFragmentCache.clear();
        Log.d(TAG, "已清除动态配置和Fragment缓存");

        // 初始化Tab样式
        updateTabStyles();
        Log.d(TAG, "默认Tab样式初始化完成");

        // 默认选中首页
        RadioButton homeTab = mRadioGroup.findViewById(DEFAULT_TAB_HOME);
        if (homeTab != null) {
            homeTab.setChecked(true);
            Log.d(TAG, "默认选中首页Tab：id=" + DEFAULT_TAB_HOME);
        } else {
            Log.e(TAG, "未找到首页Tab（id=" + DEFAULT_TAB_HOME + "），请检查xml布局");
        }
    }

    /**
     * 更新所有Tab的样式（选中/未选中状态）
     */
    private void updateTabStyles() {
        int childCount = mRadioGroup.getChildCount();
        Log.d(TAG, "开始更新所有Tab样式：共" + childCount + "个Tab，当前选中TabID=" + mCurrentTabId);
        for (int i = 0; i < childCount; i++) {
            View child = mRadioGroup.getChildAt(i);
            if (child instanceof RadioButton) {
                RadioButton tab = (RadioButton) child;
                boolean isSelected = tab.getId() == mCurrentTabId;
                Log.d(TAG, "更新Tab[" + i + "]样式：id=" + tab.getId() + ", 文本=" + tab.getText() + ", 选中状态=" + isSelected);
                updateSingleTabStyle(tab, isSelected);
            } else {
                Log.w(TAG, "RadioGroup子项不是RadioButton，索引=" + i);
            }
        }
        Log.d(TAG, "所有Tab样式更新完成");
    }

    /**
     * 更新单个Tab的样式
     */
    private void updateSingleTabStyle(RadioButton tab, boolean isSelected) {
        // 文字颜色
        int textColor = isSelected
                ? ContextCompat.getColor(mContext, TAB_SELECTED_COLOR)
                : ContextCompat.getColor(mContext, TAB_UNSELECTED_COLOR);
        tab.setTextColor(textColor);
        Log.d(TAG, "更新Tab文字颜色：id=" + tab.getId() + ", 选中状态=" + isSelected + ", 颜色资源=" + (isSelected ? "TAB_SELECTED_COLOR" : "TAB_UNSELECTED_COLOR"));

        // 图标着色
        Drawable icon = tab.getCompoundDrawables()[1];
        if (icon != null) {
            Drawable wrapped = DrawableCompat.wrap(icon.mutate()); // mutate()避免共享状态
            DrawableCompat.setTint(wrapped, textColor);
            tab.setCompoundDrawablesWithIntrinsicBounds(null, wrapped, null, null);
            Log.d(TAG, "更新Tab图标着色：id=" + tab.getId() + ", 图标资源存在");
        } else {
            Log.w(TAG, "更新Tab图标着色失败：id=" + tab.getId() + ", 图标为null");
        }
    }

    /**
     * 设置动态Tab（从服务器获取配置）
     */
    public void setupDynamicTabs(List<BottomConfigBean> configs) {
        if (configs == null || configs.isEmpty()) {
            Log.w(TAG, "动态Tab配置为空，不执行设置");
            return;
        }

        Log.d(TAG, "开始设置动态Tab：配置数量=" + configs.size());
        mDynamicTabConfigs = configs;
        mFragmentCache.clear();
        mRadioGroup.removeAllViews(); // 清空原有Tab
        Log.d(TAG, "已清除原有Tab和Fragment缓存");

        // 创建动态Tab
        for (int i = 0; i < configs.size(); i++) {
            BottomConfigBean config = configs.get(i);
            RadioButton tab = createDynamicTabButton(config, i);
            mRadioGroup.addView(tab);
            Log.d(TAG, "添加动态Tab[" + i + "]：label=" + config.getLabel() + ", id=" + tab.getId() + ", iconUrl=" + config.getIcon());
        }

        // 默认选中第一个Tab
        if (mRadioGroup.getChildCount() > 0) {
            RadioButton firstTab = (RadioButton) mRadioGroup.getChildAt(0);
            firstTab.setChecked(true);
            Log.d(TAG, "动态Tab设置完成，默认选中第一个Tab：id=" + firstTab.getId() + ", label=" + firstTab.getText());
        } else {
            Log.e(TAG, "动态Tab创建失败，RadioGroup无子项");
        }
    }

    /**
     * 创建动态Tab的RadioButton
     */
    private RadioButton createDynamicTabButton(BottomConfigBean config, int position) {
        Log.d(TAG, "创建动态Tab按钮：position=" + position + ", label=" + config.getLabel());
        RadioButton tab = new RadioButton(mContext);
        RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(
                0, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
        tab.setLayoutParams(params);

        // 基础样式
        tab.setId(View.generateViewId());
        tab.setText(config.getLabel());
        tab.setTextSize(TypedValue.COMPLEX_UNIT_SP, TAB_TEXT_SIZE_SP);
        tab.setButtonDrawable(android.R.color.transparent);
        tab.setCompoundDrawablePadding(dp2px(TAB_DRAWABLE_PADDING_DP));
        Log.d(TAG, "动态Tab按钮基础样式设置完成：id=" + tab.getId() + ", 文字大小=" + TAB_TEXT_SIZE_SP + "sp, 图标间距=" + TAB_DRAWABLE_PADDING_DP + "dp");

        // 加载图标
        loadTabIcon(tab, config.getIcon());

        return tab;
    }

    /**
     * 加载Tab图标并着色
     */


    // 修改loadTabIcon方法，使用SimpleImageLoader
    private void loadTabIcon(RadioButton tab, String iconUrl) {
        Log.d(TAG, "开始加载Tab图标：tabId=" + tab.getId() + ", iconUrl=" + iconUrl);

        // 现在传入RadioButton参数，与SimpleImageLoader的方法匹配
        mImageLoader.loadImage(iconUrl, tab, DEFAULT_TAB_ICON);

        // 初始着色（未选中状态）
        int initialColor = ContextCompat.getColor(mContext, TAB_UNSELECTED_COLOR);
        Drawable icon = tab.getCompoundDrawables()[1]; // 获取顶部图标
        if (icon != null) {
            Drawable wrapped = DrawableCompat.wrap(icon.mutate());
            DrawableCompat.setTint(wrapped, initialColor);
            tab.setCompoundDrawablesWithIntrinsicBounds(null, wrapped, null, null);
        }
    }
    private void loadTabIcon01(RadioButton tab, String iconUrl) {
        Log.d(TAG, "开始加载Tab图标：tabId=" + tab.getId() + ", iconUrl=" + iconUrl + ", 默认图标=" + DEFAULT_TAB_ICON);
        Glide.with(mContext)
                .load(iconUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL) // 强制磁盘缓存
                .error(DEFAULT_TAB_ICON)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.e(TAG, "Tab图标加载失败：tabId=" + tab.getId() + ", url=" + iconUrl + ", 错误信息=" + (e != null ? e.getMessage() : "未知错误"));
                        return false; // 允许Glide处理错误（显示默认图标）
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        // 通过dataSource判断加载来源
                        String source = switch (dataSource) {
                            case REMOTE -> "网络下载";
                            case LOCAL -> "本地文件";
                            case DATA_DISK_CACHE -> "磁盘原始缓存";
                            case RESOURCE_DISK_CACHE -> "磁盘处理后缓存";
                            case MEMORY_CACHE -> "内存缓存";
                            default -> "未知来源";
                        };
                        Log.d(TAG, "Tab图标加载成功：tabId=" + tab.getId() + ", 来源=" + source + ", url=" + iconUrl);
                        return false; // 允许Glide将资源传递给Target
                    }
                })
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource,
                                                @Nullable Transition<? super Drawable> transition) {
                        // 初始着色（默认使用未选中颜色）
                        int initialColor = ContextCompat.getColor(mContext, TAB_UNSELECTED_COLOR);
                        Drawable wrapped = DrawableCompat.wrap(resource.mutate());
                        DrawableCompat.setTint(wrapped, initialColor);
                        tab.setCompoundDrawablesWithIntrinsicBounds(null, wrapped, null, null);
                        Log.d(TAG, "Tab图标设置完成：tabId=" + tab.getId() + ", 初始着色为未选中状态");
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        Log.d(TAG, "Tab图标加载被清除：tabId=" + tab.getId() + ", 占位图=" + (placeholder != null ? placeholder.toString() : "null"));
                    }
                });
    }

    // 工具方法：查找Tab在列表中的位置
    private int findTabPositionById(int tabId) {
        for (int i = 0; i < mRadioGroup.getChildCount(); i++) {
            View child = mRadioGroup.getChildAt(i);
            if (child.getId() == tabId) {
                Log.d(TAG, "查找Tab位置：tabId=" + tabId + ", 找到位置=" + i);
                return i;
            }
        }
        Log.w(TAG, "查找Tab位置失败：未找到tabId=" + tabId + "的Tab");
        return -1;
    }

    // 工具方法：生成Fragment唯一标识
    private String getFragmentTag(Fragment fragment) {
        String tag = fragment.getClass().getSimpleName() + "_" + fragment.hashCode();
        Log.d(TAG, "生成Fragment标签：" + tag);
        return tag;
    }

    // 工具方法：dp转px
    private int dp2px(float dp) {
        int px = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                mContext.getResources().getDisplayMetrics()
        );
        Log.d(TAG, "dp转px：" + dp + "dp = " + px + "px");
        return px;
    }

    // 对外提供的接口：设置Tab切换监听
    public void setOnTabChangeListener(OnTabChangeListener listener) {
        this.mTabChangeListener = listener;
        Log.d(TAG, "设置Tab切换监听器：" + (listener != null ? listener.getClass().getSimpleName() : "null"));
    }

    /**
     * Tab切换监听器
     */
    public interface OnTabChangeListener {
        void onTabChanged(int tabId, Fragment currentFragment);
    }

    // 内部空Fragment（默认占位）
    public static class EmptyFragment extends Fragment {
        private static final String TAG = "EmptyFragment";

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater,
                                 @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            Log.d(TAG, "创建EmptyFragment视图：container=" + (container != null ? container.getId() : "null"));
            return inflater.inflate(R.layout.fragment_empty, container, false);
        }
    }
}