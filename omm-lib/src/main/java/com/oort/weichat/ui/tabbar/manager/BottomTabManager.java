package com.oort.weichat.ui.tabbar.manager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.util.LruCache;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.ObjectKey;
import com.oort.weichat.R;
import com.oort.weichat.fragment.Fragment_home_parent;
import com.oort.weichat.fragment.HomeFragment;
import com.oort.weichat.fragment.MeFragment;
import com.oort.weichat.fragment.dynamic.DynamicFragment_tab;
import com.oort.weichat.ui.tabbar.config.TabConfigProvider;
import com.oort.weichat.ui.tabbar.config.TabConfigType;
import com.oort.weichat.ui.tabbar.model.TabModel;
import com.oort.weichat.ui.tabbar.utils.DensityUtils;
import com.oort.weichat.ui.tabbar.utils.SkinUtils;
import com.oortcloud.revision.fragment.NewFriendFragment;
import com.oortcloud.revision.fragment.NewMessageFragment;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BottomTabManager {
    private static final String TAG = "TabIconDebug";
    private static final int MAX_VISIBLE_TABS = 5;
    private final LruCache<String, Bitmap> mBitmapCache; // 内存缓存核心

    // 上下文与视图容器
    private final FragmentActivity mActivity;
    private final LinearLayout mTabContainer;
    private final int mFragmentContainerId;
    private final FragmentManager mFragmentManager;
    private final SkinUtils mSkinUtils;

    // 数据存储与状态
    private TabConfigProvider mCurrentConfigProvider;
    private final Map<String, FrameLayout> mTabIdToView = new HashMap<>();
    private final Map<String, TabModel> mTabIdToModel = new HashMap<>();
    private final Map<String, Fragment> mFragmentCache = new HashMap<>();
    private String mCurrentSelectedTabId;
    private final List<TabModel> mMoreTabs = new ArrayList<>();
    private final List<TabModel> mAllTabsForIcon = new ArrayList<>();
    private List<TabModel> mVisibleTabs;

    // 图标加载统计
    private int mTotalIconsToLoad;
    private int mLoadedIconsCount;
    private long mIconLoadStartTime;
    private OnAllIconsLoadedListener mAllIconsLoadedListener;
    private OnTabSelectedListener mSelectedListener;

    // 构造方法（初始化内存缓存）
    public BottomTabManager(@NonNull FragmentActivity activity,
                            @NonNull LinearLayout tabContainer,
                            int fragmentContainerId,
                            SkinUtils skinUtils) {
        this.mActivity = activity;
        this.mTabContainer = tabContainer;
        this.mFragmentContainerId = fragmentContainerId;
        this.mFragmentManager = activity.getSupportFragmentManager();
        this.mSkinUtils = skinUtils != null ? skinUtils : SkinUtils.getSkin(activity);

        // 初始化Bitmap内存缓存（占总内存的1/8）
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        int cacheSize = maxMemory / 8;
        mBitmapCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount() / 1024; // 按KB计算
            }
        };
        Log.d(TAG, "【初始化】内存缓存初始化完成，大小: " + cacheSize + "KB");
    }

    // 回调接口
    public interface OnAllIconsLoadedListener {
        void onAllIconsLoaded(long totalTimeMs);
    }

    public interface OnTabSelectedListener {
        void onTabSelected(TabModel tabModel, Fragment fragment);
    }

    public void setOnAllIconsLoadedListener(OnAllIconsLoadedListener listener) {
        this.mAllIconsLoadedListener = listener;
    }

    public void setOnTabSelectedListener(OnTabSelectedListener listener) {
        this.mSelectedListener = listener;
    }

    // 核心初始化方法
    public void initTabs(@NonNull TabConfigProvider configProvider) {
        Log.d(TAG, "【初始化】开始，配置提供者: " + configProvider.getClass().getSimpleName());
        long initStart = System.currentTimeMillis();

        clearOldTabs();
        mCurrentConfigProvider = configProvider;
        List<TabModel> tabModels = configProvider.getTabConfigs();
        Log.d(TAG, "【初始化】总标签数量: " + (tabModels != null ? tabModels.size() : 0));

        mVisibleTabs = prepareVisibleTabs(tabModels);
        Log.d(TAG, "【初始化】可见标签: " + mVisibleTabs.size() + "，更多标签: " + mMoreTabs.size());

        mAllTabsForIcon.addAll(mVisibleTabs);
        mAllTabsForIcon.addAll(mMoreTabs);
        Log.d(TAG, "【初始化】需处理图标标签总数: " + mAllTabsForIcon.size());

        countTotalIconsToLoad();
        preCreateFragments();

        if (mTotalIconsToLoad <= 0) {
            Log.d(TAG, "【初始化】无图标需加载，直接设置UI");
            createTabViews(mVisibleTabs);
            setAllTabIconsAfterLoad();
            selectFirstVisibleTab(mVisibleTabs);
            notifyLoadComplete(0);
            Log.d(TAG, "【初始化】完成，耗时: " + (System.currentTimeMillis() - initStart) + "ms");
            return;
        }

        Log.d(TAG, "【初始化】需预加载图标总数: " + mTotalIconsToLoad);
        createTabViews(mVisibleTabs);
        mIconLoadStartTime = System.currentTimeMillis();
        preloadAllIcons();

        Log.d(TAG, "【初始化】预加载启动，耗时: " + (System.currentTimeMillis() - initStart) + "ms");
    }

    // 预创建Fragment（子线程执行）
    private void preCreateFragments() {
        if (mVisibleTabs == null || mVisibleTabs.isEmpty()) {
            Log.w(TAG, "【预创建Fragment】可见标签为空");
            return;
        }

        new Thread(() -> {
            Log.d(TAG, "【预创建Fragment】开始，数量: " + mVisibleTabs.size());
            for (TabModel tab : mVisibleTabs) {
                String tabId = tab.getUid();
                if ("more".equals(tabId)) continue;
                getOrCreateFragment(tab);
                Log.v(TAG, "【预创建Fragment】完成Tab=" + tabId);
            }
            Log.d(TAG, "【预创建Fragment】全部完成");
        }).start();
    }

    // 统计需预加载的图标数量
    private void countTotalIconsToLoad() {
        mTotalIconsToLoad = 0;
        mLoadedIconsCount = 0;

        for (TabModel tab : mAllTabsForIcon) {
            String tabId = tab.getUid();
            if (mCurrentConfigProvider.getConfigType() == TabConfigType.LOCAL_DEFAULT) {
                Log.v(TAG, "【计数】Tab=" + tabId + " 本地图标，无需加载");
                continue;
            }

            String normalUrl = tab.getIconUrl();
            String selectUrl = tab.getSelectIconUrl();

            if (isValidIconUrl(normalUrl)) {
                mTotalIconsToLoad++;
                Log.v(TAG, "【计数】Tab=" + tabId + " 需加载正常图标");
            }
            if (isValidIconUrl(selectUrl) && !selectUrl.equals(normalUrl)) {
                mTotalIconsToLoad++;
                Log.v(TAG, "【计数】Tab=" + tabId + " 需加载选中图标");
            }
        }
    }

    // 分批预加载所有图标（核心优化）
    private void preloadAllIcons() {
        Log.d(TAG, "【预加载】开始，总数: " + mTotalIconsToLoad);
        // 分批加载（每批2个，间隔100ms，避免CPU峰值）
        for (int i = 0; i < mAllTabsForIcon.size(); i++) {
            int finalI = i;
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                TabModel tab = mAllTabsForIcon.get(finalI);
                if (mCurrentConfigProvider.getConfigType() == TabConfigType.LOCAL_DEFAULT) return;
                preloadTabIcons(tab);
            }, i / 2 * 100);
        }
    }

    // 预加载单个Tab的图标
    private void preloadTabIcons(TabModel tab) {
        String tabId = tab.getUid();
        Log.d(TAG, "【预加载】处理Tab=" + tabId);

        String normalUrl = tab.getIconUrl();
        if (isValidIconUrl(normalUrl)) {
            preloadSingleIcon(tab, normalUrl, false);
        } else {
            Log.v(TAG, "【预加载】Tab=" + tabId + " 正常图标URL无效");
            if (isValidIconUrl(tab.getSelectIconUrl())) mLoadedIconsCount++;
        }

        String selectUrl = tab.getSelectIconUrl();
        if (isValidIconUrl(selectUrl) && !selectUrl.equals(normalUrl)) {
            preloadSingleIcon(tab, selectUrl, true);
        } else {
            Log.v(TAG, "【预加载】Tab=" + tabId + " 选中图标无需加载");
        }
    }

    // 预加载单个图标（Base64解码优化）
    private void preloadSingleIcon(TabModel tab, String url, boolean isSelected) {
        String tabId = tab.getUid();
        String state = isSelected ? "选中" : "正常";
        Log.v(TAG, "【预加载】启动" + state + "图标: Tab=" + tabId);

        if (isBase64Icon(url)) {
            // Base64图标：子线程解码并缓存
            new Thread(() -> {
                try {
                    // 提取Base64数据（去除前缀）
                    int prefixIndex = url.indexOf("base64,");
                    if (prefixIndex == -1) throw new IllegalArgumentException("无效Base64格式");
                    String base64Data = url.substring(prefixIndex + 7);

                    // 解码为字节数组
                    byte[] imageBytes = Base64.decode(base64Data, Base64.DEFAULT);

                    // 按目标尺寸解码Bitmap（24dp）
                    int targetSize = DensityUtils.dp2px(mActivity, 24);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, options);
                    int scale = Math.max(options.outWidth / targetSize, options.outHeight / targetSize);
                    if (scale > 1) options.inSampleSize = scale;
                    options.inJustDecodeBounds = false;
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, options);

                    // 存入内存缓存
                    String cacheKey = getIconCacheKey(tab, url, isSelected);
                    mBitmapCache.put(cacheKey, bitmap);
                    Log.d(TAG, "【预加载】" + state + "图标缓存成功: Tab=" + tabId + "，尺寸=" + bitmap.getWidth() + "x" + bitmap.getHeight());
                    onIconLoadFinished(tabId, state, true);
                } catch (Exception e) {
                    Log.e(TAG, "【预加载】" + state + "图标解码失败: Tab=" + tabId, e);
                    onIconLoadFinished(tabId, state, false);
                }
            }).start();
            return;
        }

        // 网络图标：Glide预加载
        int iconSize = DensityUtils.dp2px(mActivity, 24);
        ImageView tempView = new ImageView(mActivity);
        String cacheKey = getIconCacheKey(tab, url, isSelected);
        Glide.with(tempView.getContext())
                .asDrawable()
                .load(url)
                .override(iconSize, iconSize)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .signature(new ObjectKey(cacheKey))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.e(TAG, "【预加载】网络图标失败: Tab=" + tabId, e);
                        onIconLoadFinished(tabId, state, false);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Log.v(TAG, "【预加载】网络图标成功: Tab=" + tabId + "，来源=" + getSourceDesc(dataSource));
                        onIconLoadFinished(tabId, state, true);
                        return false;
                    }
                })
                .preload(iconSize, iconSize);
    }

    // 图标加载完成回调
    private void onIconLoadFinished(String tabId, String state, boolean success) {
        mLoadedIconsCount++;
        Log.d(TAG, "【进度】" + mLoadedIconsCount + "/" + mTotalIconsToLoad + "（Tab=" + tabId + "，" + state + "）");

        if (mLoadedIconsCount >= mTotalIconsToLoad) {
            long totalTime = System.currentTimeMillis() - mIconLoadStartTime;
            Log.d(TAG, "【预加载完成】总耗时: " + totalTime + "ms");
            mActivity.runOnUiThread(() -> {
                setAllTabIconsAfterLoad();
                selectFirstVisibleTab(mVisibleTabs);
                notifyLoadComplete(totalTime);
            });
        }
    }

    // 统一设置所有图标到UI（优先内存缓存）
    private void setAllTabIconsAfterLoad() {
        Log.d(TAG, "【设置UI】开始，可见Tab数量: " + mTabIdToView.size());
        for (Map.Entry<String, FrameLayout> entry : mTabIdToView.entrySet()) {
            String tabId = entry.getKey();
            FrameLayout tabView = entry.getValue();
            TabModel tabModel = mTabIdToModel.get(tabId);
            if (tabModel == null) {
                Log.w(TAG, "【设置UI】Tab=" + tabId + " 模型不存在");
                continue;
            }
            setTabIcon(tabView, tabModel, false);
        }
        Log.d(TAG, "【设置UI】完成");
    }

    // 设置单个Tab图标（核心优化：内存缓存优先）
    private void setTabIcon(FrameLayout tabView, TabModel tabModel, boolean isSelected) {
        String tabId = tabModel.getUid();
        ImageView iconIv = (ImageView) tabView.getTag(R.id.tab_icon_tag);
        String state = isSelected ? "选中" : "非选中";

        if (iconIv == null) {
            Log.e(TAG, "【设置图标】Tab=" + tabId + " ImageView为null");
            return;
        }

        setIconSize(iconIv);

        // 本地图标处理
        if (mCurrentConfigProvider.getConfigType() == TabConfigType.LOCAL_DEFAULT) {
            loadLocalIcon(iconIv, tabModel, isSelected);
            return;
        }

        // 优先从内存缓存获取
        String targetUrl = isSelected && isValidIconUrl(tabModel.getSelectIconUrl())
                ? tabModel.getSelectIconUrl() : tabModel.getIconUrl();
        String cacheKey = getIconCacheKey(tabModel, targetUrl, isSelected);
        Bitmap cachedBitmap = mBitmapCache.get(cacheKey);

        if (cachedBitmap != null && !cachedBitmap.isRecycled()) {
            iconIv.setImageBitmap(cachedBitmap);
            Log.v(TAG, "【设置图标】内存缓存命中: Tab=" + tabId + " " + state);
            return;
        }

        // 缓存未命中：Glide兜底
        Log.v(TAG, "【设置图标】内存缓存未命中，使用Glide: Tab=" + tabId + " " + state);
        if (!isValidIconUrl(targetUrl)) {
            iconIv.setImageResource(tabModel.getDefaultIconRes());
            return;
        }

        Glide.with(iconIv.getContext())
                .asDrawable()
                .load(targetUrl)
                .override(DensityUtils.dp2px(mActivity, 24), DensityUtils.dp2px(mActivity, 24))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .signature(new ObjectKey(cacheKey))
                .error(tabModel.getDefaultIconRes())
                .apply(new RequestOptions().priority(com.bumptech.glide.Priority.HIGH))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.e(TAG, "【设置图标】Glide失败: Tab=" + tabId, e);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Log.v(TAG, "【设置图标】Glide成功: Tab=" + tabId + "，来源=" + getSourceDesc(dataSource));
                        return false;
                    }
                })
                .into(iconIv);
    }

    // 加载本地图标
    private void loadLocalIcon(ImageView iconIv, TabModel tabModel, boolean isSelected) {
        String tabId = tabModel.getUid();
        try {
            int resId = tabModel.getDefaultIconRes();
            Drawable icon = ContextCompat.getDrawable(mActivity, resId);
            if (icon == null) {
                Log.e(TAG, "【本地图标】Tab=" + tabId + " 资源不存在: " + resId);
                return;
            }
            if (tabModel.getIconType() == TabModel.IconType.OUTLINE_ICON) {
                Drawable wrapped = DrawableCompat.wrap(icon.mutate());
                DrawableCompat.setTintList(wrapped, mSkinUtils.getMainTabColorState());
                iconIv.setImageDrawable(wrapped);
            } else {
                iconIv.setImageDrawable(icon);
            }
            Log.v(TAG, "【本地图标】Tab=" + tabId + " 加载成功");
        } catch (Exception e) {
            Log.e(TAG, "【本地图标】Tab=" + tabId + " 加载失败", e);
        }
    }

    // 设置图标尺寸
    private void setIconSize(ImageView iconIv) {
        int size = DensityUtils.dp2px(mActivity, 24);
        ViewParent parent = iconIv.getParent();
        ViewGroup.LayoutParams params;
        if (parent instanceof LinearLayout) {
            params = new LinearLayout.LayoutParams(size, size);
        } else {
            params = new FrameLayout.LayoutParams(size, size);
        }
        // 根据布局类型设置gravity（LinearLayout或FrameLayout）
        if (params instanceof LinearLayout.LayoutParams) {
            ((LinearLayout.LayoutParams) params).gravity = Gravity.CENTER;
        } else if (params instanceof FrameLayout.LayoutParams) {
            ((FrameLayout.LayoutParams) params).gravity = Gravity.CENTER;
        }
        iconIv.setLayoutParams(params);
        iconIv.setScaleType(ImageView.ScaleType.FIT_CENTER);
    }

    // 生成图标缓存键
    private String getIconCacheKey(TabModel tab, String url, boolean isSelected) {
        String tabId = tab.getUid();
        String state = isSelected ? "selected" : "normal";
        if (isBase64Icon(url)) {
            String base64Data = url.substring(url.indexOf("base64,") + 7);
            return "base64_" + tabId + "_" + state + "_" + generateMD5(base64Data);
        }
        return "url_" + tabId + "_" + state + "_" + generateMD5(url);
    }

    // 生成MD5
    private String generateMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "【MD5】生成失败", e);
            return String.valueOf(input.hashCode());
        }
    }

    // 辅助方法：URL有效性检查
    private boolean isValidIconUrl(String url) {
        return url != null && !url.trim().isEmpty();
    }

    // 辅助方法：Base64图标检查
    private boolean isBase64Icon(String url) {
        return isValidIconUrl(url) && url.startsWith("data:image/") && url.contains("base64,");
    }

    // 辅助方法：数据源描述
    private String getSourceDesc(DataSource dataSource) {
        if (dataSource == null) return "未知";
        switch (dataSource) {
            case MEMORY_CACHE: return "内存缓存";
            case DATA_DISK_CACHE: return "磁盘缓存";
            case REMOTE: return "网络";
            default: return dataSource.name();
        }
    }

    // 辅助方法：截断URL日志
    private String truncateUrl(String url) {
        return url == null ? "null" : (url.length() > 50 ? url.substring(0, 50) + "..." : url);
    }

    // 通知加载完成
    private void notifyLoadComplete(long totalTime) {
        if (mAllIconsLoadedListener != null) {
            mAllIconsLoadedListener.onAllIconsLoaded(totalTime);
            Log.d(TAG, "【通知】加载完成回调，耗时=" + totalTime + "ms");
        }
    }

    // 准备可见标签和更多标签
    private List<TabModel> prepareVisibleTabs(List<TabModel> tabModels) {
        List<TabModel> visibleTabs = new ArrayList<>();
        mMoreTabs.clear();
        if (tabModels == null) return visibleTabs;

        List<TabModel> allVisible = new ArrayList<>();
        for (TabModel tab : tabModels) {
            if (tab.isVisible()) {
                allVisible.add(tab);
                Log.v(TAG, "【可见标签】添加: " + tab.getUid() + "（" + tab.getLabel() + "）");
            }
        }

        if (allVisible.size() > MAX_VISIBLE_TABS) {
            visibleTabs.addAll(allVisible.subList(0, MAX_VISIBLE_TABS - 1));
            mMoreTabs.addAll(allVisible.subList(MAX_VISIBLE_TABS - 1, allVisible.size()));
            Collections.reverse(mMoreTabs);
            visibleTabs.add(new TabModel("more", "更多", R.drawable.ic_tab_more, HomeFragment.class, TabModel.IconType.COLOR_IMAGE));
            Log.d(TAG, "【可见标签】拆分完成，可见=" + visibleTabs.size() + "，更多=" + mMoreTabs.size());
        } else {
            visibleTabs.addAll(allVisible);
            Log.d(TAG, "【可见标签】无需拆分，数量=" + visibleTabs.size());
        }

        mTabIdToModel.clear();
        for (TabModel tab : visibleTabs) {
            String tabId = tab.getUid();
            if (tabId != null && !tabId.isEmpty()) {
                mTabIdToModel.put(tabId, tab);
            }
        }
        return visibleTabs;
    }

    // 创建标签视图
    private void createTabViews(List<TabModel> visibleTabs) {
        Log.d(TAG, "【创建视图】开始，数量: " + visibleTabs.size());
        mTabContainer.removeAllViews();

        for (TabModel tab : visibleTabs) {
            String tabId = tab.getUid();
            FrameLayout tabView = (FrameLayout) LayoutInflater.from(mActivity)
                    .inflate(R.layout.tab_item, mTabContainer, false);
            if (tabView == null) {
                Log.e(TAG, "【创建视图】Tab=" + tabId + " 布局加载失败");
                continue;
            }

            ImageView iconIv = tabView.findViewById(R.id.iv_tab_icon);
            TextView labelTv = tabView.findViewById(R.id.tv_tab_label);
            TextView badgeTv = tabView.findViewById(R.id.tv_badge);

            if (labelTv != null) {
                labelTv.setText(tab.getLabel());
                labelTv.setTextColor(mSkinUtils.getMainTabColorState());
            }

            tabView.setTag(R.id.tab_icon_tag, iconIv);
            tabView.setTag(R.id.tab_badge_tag, badgeTv);
            tabView.setTag(R.id.tab_id_tag, tabId);

            tabView.setOnClickListener(v -> {
                Log.d(TAG, "【点击】Tab=" + tabId);
                if ("more".equals(tabId)) {
                    showMoreMenu();
                } else {
                    selectTabByTabId(tabId);
                }
            });

            mTabContainer.addView(tabView);
            mTabIdToView.put(tabId, tabView);
            updateTabUnreadCount(tabId, tab.getUnreadCount());
            Log.v(TAG, "【创建视图】Tab=" + tabId + " 完成");
        }
        Log.d(TAG, "【创建视图】全部完成");
    }

    // 选择标签
    public void selectTabByTabId(String tabId) {
        Log.d(TAG, "【选择标签】开始，当前选中=" + mCurrentSelectedTabId);
        if (tabId.equals(mCurrentSelectedTabId)) return;

        TabModel tabModel = mTabIdToModel.get(tabId);
        if (tabModel == null) {
            Log.e(TAG, "【选择标签】Tab=" + tabId + " 模型不存在");
            return;
        }

        // 取消上一个选中
        if (mCurrentSelectedTabId != null) {
            FrameLayout oldTab = mTabIdToView.get(mCurrentSelectedTabId);
            TabModel oldModel = mTabIdToModel.get(mCurrentSelectedTabId);
            if (oldTab != null && oldModel != null) {
                setTabSelected(oldTab, oldModel, false);
            }
        }

        // 选中新标签
        mCurrentSelectedTabId = tabId;
        FrameLayout newTab = mTabIdToView.get(tabId);
        if (newTab != null) {
            setTabSelected(newTab, tabModel, true);
            Log.v(TAG, "【选择标签】UI更新完成: " + tabId);
        }

        // 切换Fragment
        mActivity.runOnUiThread(() -> {
            Fragment fragment = getOrCreateFragment(tabModel);
            if (mSelectedListener != null) {
                mSelectedListener.onTabSelected(tabModel, fragment);
            }
            switchFragment(fragment, tabId);
        });
    }

    // 设置标签选中状态
    private void setTabSelected(FrameLayout tabView, TabModel tabModel, boolean selected) {
        String tabId = tabModel.getUid();
        Log.v(TAG, "【设置选中】Tab=" + tabId + "，状态=" + selected);

        TextView labelTv = tabView.findViewById(R.id.tv_tab_label);
        if (labelTv != null) {
            labelTv.setSelected(selected);
            labelTv.setTextColor(mSkinUtils.getMainTabColorState());
        }

        setTabIcon(tabView, tabModel, selected);
    }

    // 获取或创建Fragment
    private Fragment getOrCreateFragment(TabModel tabModel) {
        String tabId = tabModel.getUid();
        if (mFragmentCache.containsKey(tabId)) {
            Fragment fragment = mFragmentCache.get(tabId);
            Log.v(TAG, "【Fragment】缓存获取: " + tabId);
            return fragment;
        }

        Fragment fragment = createFragmentByPackage(tabModel.getAppPackage());
        mFragmentCache.put(tabId, fragment);
        Log.v(TAG, "【Fragment】创建: " + tabId);
        return fragment;
    }

    // 创建Fragment
    private Fragment createFragmentByPackage(String appPackage) {
        Log.v(TAG, "【创建Fragment】包名: " + appPackage);
        if (appPackage == null) return new HomeFragment();

        switch (appPackage) {
            case "com.oort.tab.home__void_apply":
                return new Fragment_home_parent();
            case "com.oort.tab.message_void_apply":
                return new NewMessageFragment();
            case "com.oort.tab.contact_void_apply":
                return new NewFriendFragment();
            case "com.oort.tab.dynamic_void_apply":
                return new DynamicFragment_tab();
            case "com.oort.tab.me_void_apply":
                return new MeFragment();
            default:
                return HomeFragment.newInstance(appPackage);
        }
    }

    // 切换Fragment
    private void switchFragment(Fragment fragment, String tag) {
        if ("more".equals(tag)) return;
        Log.d(TAG, "【切换Fragment】tag=" + tag);

        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        for (Fragment f : mFragmentManager.getFragments()) {
            if (f != null && f.isVisible()) transaction.hide(f);
        }

        if (mFragmentManager.findFragmentByTag(tag) == null) {
            transaction.add(mFragmentContainerId, fragment, tag);
        } else {
            transaction.show(fragment);
        }
        transaction.commitAllowingStateLoss();
    }

    // 更新未读角标
    public void updateTabUnreadCount(@NonNull String tabId, int unreadCount) {
        FrameLayout tabView = mTabIdToView.get(tabId);
        if (tabView == null) {
            Log.w(TAG, "【角标更新】Tab=" + tabId + " 视图不存在");
            return;
        }

        TextView badgeTv = (TextView) tabView.getTag(R.id.tab_badge_tag);
        if (badgeTv == null) return;

        if (unreadCount <= 0) {
            badgeTv.setVisibility(View.GONE);
        } else {
            badgeTv.setVisibility(View.VISIBLE);
            badgeTv.setText(unreadCount > 9 ? "9+" : String.valueOf(unreadCount));
        }
        Log.v(TAG, "【角标更新】Tab=" + tabId + "，数量=" + unreadCount);
    }

    // 选择第一个可见标签
    private void selectFirstVisibleTab(List<TabModel> visibleTabs) {
        Log.d(TAG, "【选择首个标签】数量=" + visibleTabs.size());
        for (TabModel tab : visibleTabs) {
            String tabId = tab.getUid();
            if (tabId == null || "more".equals(tabId)) continue;
            if (mTabIdToModel.containsKey(tabId) && mTabIdToView.containsKey(tabId)) {
                Log.d(TAG, "【选择首个标签】选中: " + tabId);
                selectTabByTabId(tabId);
                return;
            }
        }
        Log.w(TAG, "【选择首个标签】无有效标签");
    }

    // 显示更多菜单
    PopupWindow popup = null;
    private void showMoreMenu() {
        Log.d(TAG, "【更多菜单】显示，数量=" + mMoreTabs.size());
        FrameLayout moreTabView = mTabIdToView.get("more");
        if (mMoreTabs.isEmpty() || moreTabView == null) return;

        View contentView = LayoutInflater.from(mActivity).inflate(R.layout.popup_more_tabs, null);
        LinearLayout container = contentView.findViewById(R.id.ll_more_container);
        if (container == null) return;
        container.removeAllViews();

        for (TabModel tab : mMoreTabs) {
            View item = LayoutInflater.from(mActivity).inflate(R.layout.item_more_tab, container, false);
            ImageView ivIcon = item.findViewById(R.id.iv_more_icon);
            TextView tvLabel = item.findViewById(R.id.tv_more_label);
            if (ivIcon == null || tvLabel == null) continue;

            tvLabel.setText(tab.getLabel());
            String url = tab.getIconUrl();
            if (isValidIconUrl(url)) {
                String cacheKey = getIconCacheKey(tab, url, false);
                Bitmap cachedBitmap = mBitmapCache.get(cacheKey);
                if (cachedBitmap != null && !cachedBitmap.isRecycled()) {
                    ivIcon.setImageBitmap(cachedBitmap);
                } else {
                    Glide.with(ivIcon.getContext())
                            .load(url)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .signature(new ObjectKey(cacheKey))
                            .error(tab.getDefaultIconRes())
                            .into(ivIcon);
                }
            } else {
                ivIcon.setImageResource(tab.getDefaultIconRes());
            }

            item.setOnClickListener(v -> {
                selectTabByTabId(tab.getUid());
                if (popup != null) popup.dismiss();
            });
            container.addView(item);
        }

        popup = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popup.setElevation(DensityUtils.dp2px(mActivity, 4));
        }
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);

        int[] tabBarLoc = new int[2];
        mTabContainer.getLocationOnScreen(tabBarLoc);
        int[] moreLoc = new int[2];
        moreTabView.getLocationOnScreen(moreLoc);
        int x = moreLoc[0] + moreTabView.getWidth() / 2 - contentView.getMeasuredWidth() / 2;
        int y = tabBarLoc[1] - contentView.getMeasuredHeight();
        popup.showAtLocation(mTabContainer, Gravity.NO_GRAVITY, x, y);
    }

    // 清除旧数据
    private void clearOldTabs() {
        Log.d(TAG, "【清除旧数据】开始");
        mBitmapCache.evictAll(); // 清空内存缓存
        mTabContainer.removeAllViews();
        mTabIdToView.clear();
        mTabIdToModel.clear();
        mFragmentCache.clear();
        mCurrentSelectedTabId = null;
        mMoreTabs.clear();
        mAllTabsForIcon.clear();
        mVisibleTabs = null;
        Log.d(TAG, "【清除旧数据】完成");
    }
}