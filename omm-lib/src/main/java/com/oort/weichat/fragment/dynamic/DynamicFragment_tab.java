package com.oort.weichat.fragment.dynamic;

import static com.google.android.material.tabs.TabLayout.MODE_SCROLLABLE;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.oort.weichat.R;
import com.oort.weichat.adapter.PublicMessageRecyclerAdapter;
import com.oort.weichat.bean.MyZan;
import com.oort.weichat.bean.circle.PublicMessage;
import com.oort.weichat.bean.event.MessageEventHongdian;
import com.oort.weichat.db.dao.MyZanDao;
import com.oort.weichat.fragment.dynamic.views.MusicButton;
import com.oort.weichat.fragment.entity.DynamicPlayAudioEvent;
import com.oort.weichat.fragment.entity.DynamicSetTopEvent;
import com.oort.weichat.helper.AvatarHelper;
import com.oort.weichat.ui.base.EasyFragment;
import com.oort.weichat.ui.circle.SelectPicPopupWindow;
import com.oort.weichat.util.StringUtils;
import com.oort.weichat.view.MergerStatus;
import com.oortcloud.basemodule.utils.OperLogUtil;
import com.oortcloud.basemodule.utils.StringUtil;
import com.oortcloud.basemodule.views.swiperecyclerview.SwipeRecyclerView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.xuexiang.xui.widget.layout.ExpandableLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * 朋友圈的Fragment
 */
public class DynamicFragment_tab extends EasyFragment implements TabLayout.OnTabSelectedListener {
    private static final int REQUEST_CODE_SEND_MSG = 1;
    private static final int REQUEST_CODE_PICK_PHOTO = 2;
    private static int PAGER_SIZE = 10;
    private String mUserId;
    private String mUserName;
    private TextView mTvTitle;
    private ImageView mIvTitleRight;
    private SelectPicPopupWindow menuWindow;
    private View mHeadView;
    private ImageView ivHeadBg, ivHead;
    private LinearLayout mTipLl;
    private ImageView mTipIv;
    private TextView mTipTv;
    private ExpandableLayout expandable_layout01;
    private EditText editText;
    private int tabIndex;
    private SmartRefreshLayout mRefreshLayout;
    private SwipeRecyclerView mListView;
    private PublicMessageRecyclerAdapter mAdapter;
    private List<PublicMessage> mMessages = new ArrayList<>();
    private boolean more;
    private String messageId;
    private boolean showTitle = true;
    private MergerStatus mergerStatus;
    private RelativeLayout rl_title;
    private TabLayout tabLayout;
    private FragmentCacheAdapter mAdapter1;
    private ViewPager viewPager;
    private MusicButton cv_auido;
    private int mTop;
    private ExpandableLayout expandable_layout;
    private DynamicSetTopEvent mTopevent;
    private RadioButton rb01;
    private RadioButton rb02;
    private RadioButton rb03;
    private RadioButton rb04;
    private RadioButton rb05;

    // 新增：蒙版与加载动画相关
    private FrameLayout mLoadingMask;
    private ProgressBar mLoadingProgress;
    private Handler mainHandler;

    // 新增：异步操作状态监控
    private boolean isMediaReady = false;
    private boolean isFragmentReady = false;
    private long initDataStartTime;
    private MediaPlayer mPlayer;

    // 新增：Fragment数据加载完成回调接口
    public interface OnFragmentDataLoadedListener {
        void onLoaded();
    }

    @Override
    protected int inflateLayoutId() {
        return R.layout.fragment_dynamic_tab;
    }

    @Override
    protected void onActivityCreated(Bundle savedInstanceState, boolean createView) {
    }

    boolean isload = false;

    @Override
    public void onResume() {
        super.onResume();
        if (!isload) {
            isload = true;
            long startTime = System.currentTimeMillis();
            Log.d("DynamicMask", "【流程开始】时间戳: " + startTime + ", 准备显示蒙版");

            // 显示蒙版
            if (mLoadingMask != null) {
                mLoadingMask.setVisibility(View.VISIBLE);
                Log.d("DynamicMask", "【蒙版显示】时间戳: " + System.currentTimeMillis() + ", 耗时: " + (System.currentTimeMillis() - startTime) + "ms");
            }

            // 延迟30ms执行初始化，避免阻塞onResume
            mainHandler.postDelayed(() -> {
                long initStartTime = System.currentTimeMillis();
                long delayActual = initStartTime - startTime;
                Log.d("DynamicMask", "【延迟结束】实际延迟: " + delayActual + "ms (预期30ms), 开始初始化");

                initActionBar();
                initViews();
                initDataStartTime = System.currentTimeMillis();
                initData();

                // 启动5秒超时保护
                mainHandler.postDelayed(this::forceHideMask, 5000);
            }, 30);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // 暂停音频播放
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
            cv_auido.stopMusic();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        mainHandler = new Handler(Looper.getMainLooper());
        initLoadingViews(rootView); // 动态创建蒙版
        return rootView;
    }

    /**
     * 动态创建白色蒙版和加载动画
     */
    private void initLoadingViews(View rootView) {
        // 1. 创建蒙版容器
        mLoadingMask = new FrameLayout(requireContext());
        FrameLayout.LayoutParams maskParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        );
        mLoadingMask.setLayoutParams(maskParams);
        mLoadingMask.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.white));
        mLoadingMask.setVisibility(View.GONE);

        // 2. 创建加载动画
        mLoadingProgress = new ProgressBar(requireContext());
        FrameLayout.LayoutParams progressParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        progressParams.gravity = Gravity.CENTER;
        mLoadingProgress.setLayoutParams(progressParams);
        mLoadingProgress.setIndeterminate(true);
        // 可选：设置自定义加载图标（需在res/drawable创建progress_rotate.xml）
        // mLoadingProgress.setIndeterminateDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.progress_rotate));

        // 3. 添加到根布局
        mLoadingMask.addView(mLoadingProgress);
        if (rootView instanceof ViewGroup) {
            ((ViewGroup) rootView).addView(mLoadingMask);
            mLoadingMask.bringToFront();
        }
    }

    public void initData() {
        Log.d("DynamicMask", "【initData开始】时间戳: " + System.currentTimeMillis());
        EventBus.getDefault().register(this);

        // 初始化MediaPlayer（异步监控）
        initMediaPlayer();

        // 初始化ViewPager和Fragment（监控数据加载）
        initViewPagerWithListener();

        Log.d("DynamicMask", "【initData同步代码完成】时间戳: " + System.currentTimeMillis());
    }

    /**
     * 初始化MediaPlayer并监控准备状态
     */
    private void initMediaPlayer() {
        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mPlayer.setOnPreparedListener(mp -> {
            long prepareTime = System.currentTimeMillis();
            isMediaReady = true;
            Log.d("DynamicMask", "【MediaPlayer准备完成】耗时: " + (prepareTime - initDataStartTime) + "ms");
            checkAllReadyAndHideMask();
        });

        mPlayer.setOnErrorListener((mp, what, extra) -> {
            Log.e("DynamicMask", "【MediaPlayer错误】what=" + what + ", extra=" + extra);
            isMediaReady = true; // 错误状态也标记为完成，避免阻塞
            checkAllReadyAndHideMask();
            return false;
        });

        mPlayer.setOnCompletionListener(mp -> {
            mPlayer.stop();
            cv_auido.stopMusic();
            cv_auido.setVisibility(View.GONE);
        });
    }

    /**
     * 初始化ViewPager并监听Fragment数据加载
     */
    private void initViewPagerWithListener() {
        mAdapter1 = new FragmentCacheAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                Fragment fragment = super.getItem(position);
                if (fragment instanceof FragmentDynamicList) {
//                    ((FragmentDynamicList) fragment).setOnDataLoadedListener(() -> {
//                        long loadTime = System.currentTimeMillis();
//                        isFragmentReady = true;
//                        Log.d("DynamicMask", "【Fragment数据加载完成】位置: " + position + ", 耗时: " + (loadTime - initDataStartTime) + "ms");
//                        checkAllReadyAndHideMask();
//                    });
                    checkAllReadyAndHideMask();
                }
                return fragment;
            }
        };

        tabLayout.setTabMode(MODE_SCROLLABLE);
        tabLayout.addOnTabSelectedListener(this);
        viewPager.setAdapter(mAdapter1);
        viewPager.setOffscreenPageLimit(2); // 减少预加载
        tabLayout.setupWithViewPager(viewPager);
        refreshAdapter(true);
    }

    /**
     * 检查所有异步操作是否完成，完成则隐藏蒙版
     */
    private void checkAllReadyAndHideMask() {
        if (isMediaReady && isFragmentReady) {
            long allReadyTime = System.currentTimeMillis();
            Log.d("DynamicMask", "【所有异步完成】总耗时: " + (allReadyTime - initDataStartTime) + "ms, 隐藏蒙版");
            hideMask();
        } else {
            Log.d("DynamicMask", "【等待异步】MediaReady=" + isMediaReady + ", FragmentReady=" + isFragmentReady);
        }
    }

    /**
     * 隐藏蒙版
     */
    private void hideMask() {
        if (mLoadingMask != null && isAdded()) {
            mainHandler.post(() -> {
                mLoadingMask.setVisibility(View.GONE);
                Log.d("DynamicMask", "【蒙版隐藏】时间戳: " + System.currentTimeMillis());
            });
        }
    }

    /**
     * 超时保护：5秒后强制隐藏
     */
    private void forceHideMask() {
        if (mLoadingMask != null && mLoadingMask.getVisibility() == View.VISIBLE) {
            Log.w("DynamicMask", "【超时保护】5秒未完成，强制隐藏蒙版");
            hideMask();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void helloEventBus(DynamicPlayAudioEvent evnet) {
        if (evnet.statu == 0) {
            cv_auido.clearAnimation();
            cv_auido.setVisibility(View.GONE);
        }
        if (evnet.statu == 1) {
            cv_auido.setVisibility(View.VISIBLE);
            curMusic = evnet.url;
            playMusic();
        }
        if (evnet.statu == 2) {
            cv_auido.playMusic();
            cv_auido.setVisibility(View.VISIBLE);
            if (mPlayer != null) {
                if (mPlayer.isPlaying()) {
                    mPlayer.pause();
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void helloEventBus(DynamicSetTopEvent evnet) {
        if (evnet.show && evnet.type != 10) {
            mTopevent = evnet;
            expandable_layout.expand(true);
            expandable_layout.setVisibility(View.VISIBLE);
            rb01.setChecked(true);
            rb02.setChecked(false);
            rb03.setChecked(false);
            rb04.setChecked(false);
            rb05.setChecked(false);
            mTop = 9;
        }
    }

    private String curMusic = "";

    private void playMusic() {
        try {
            if (mPlayer == null) {
                mPlayer = new MediaPlayer();
                mPlayer.setDataSource(curMusic);
                mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mPlayer.prepareAsync(); // 异步准备，避免阻塞
                mPlayer.setOnPreparedListener(mp -> {
                    mp.start();
                    cv_auido.playMusic();
                });
                mPlayer.setOnErrorListener((mp, what, extra) -> {
                    curMusic = "";
                    return false;
                });
                mPlayer.setOnCompletionListener(mp -> {
                    stopMusic();
                });
            } else if (mPlayer.isPlaying()) {
                mPlayer.stop();
                mainHandler.postDelayed(() -> {
                    try {
                        mPlayer.reset();
                        mPlayer.setDataSource(curMusic);
                        mPlayer.prepareAsync();
                    } catch (Exception e) {
                        curMusic = "";
                        e.printStackTrace();
                    }
                }, 1000);
            } else {
                mPlayer.reset();
                mPlayer.setDataSource(curMusic);
                mPlayer.prepareAsync();
                mPlayer.setOnPreparedListener(mp -> {
                    mp.start();
                    cv_auido.playMusic();
                });
            }
        } catch (Exception e) {
            curMusic = "";
            e.printStackTrace();
        }
    }

    private void stopMusic() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
        cv_auido.stopMusic();
        cv_auido.setVisibility(View.GONE);
    }

    private void initActionBar() {
    }

    public void initViews() {
        more = true;

        expandable_layout = findViewById(R.id.expandable_layout);
        expandable_layout.expand(false);
        expandable_layout.setVisibility(View.GONE);
        expandable_layout.setOnClickListener(v -> {
            expandable_layout.setVisibility(View.GONE);
            expandable_layout.expand(false);
        });

        expandable_layout01 = findViewById(R.id.expandable_layout01);
        expandable_layout01.expand(false);
        expandable_layout01.setVisibility(View.GONE);
        expandable_layout01.setOnClickListener(v -> {
            expandable_layout01.setVisibility(View.GONE);
            expandable_layout01.expand(false);
        });

        rb01 = findViewById(R.id.rb01);
        rb02 = findViewById(R.id.rb02);
        rb03 = findViewById(R.id.rb03);
        rb04 = findViewById(R.id.rb04);
        rb05 = findViewById(R.id.rb05);

        Button cancel = findViewById(R.id.btn_cancel);
        Button ok = findViewById(R.id.btn_ok);

        rb01.setOnClickListener(v -> {
            rb01.setChecked(true);
            rb02.setChecked(false);
            rb03.setChecked(false);
            rb04.setChecked(false);
            rb05.setChecked(false);
            mTop = 9;
        });
        rb02.setOnClickListener(v -> {
            rb01.setChecked(false);
            rb02.setChecked(true);
            rb03.setChecked(false);
            rb04.setChecked(false);
            rb05.setChecked(false);
            mTop = 8;
        });
        rb03.setOnClickListener(v -> {
            rb01.setChecked(false);
            rb02.setChecked(false);
            rb03.setChecked(true);
            rb04.setChecked(false);
            rb05.setChecked(false);
            mTop = 7;
        });
        rb04.setOnClickListener(v -> {
            rb01.setChecked(false);
            rb02.setChecked(false);
            rb03.setChecked(false);
            rb04.setChecked(true);
            rb05.setChecked(false);
            mTop = 6;
        });
        rb05.setOnClickListener(v -> {
            rb01.setChecked(false);
            rb02.setChecked(false);
            rb03.setChecked(false);
            rb04.setChecked(false);
            rb05.setChecked(true);
            mTop = 5;
        });

        cancel.setOnClickListener(v -> {
            expandable_layout.expand(false);
            expandable_layout.setVisibility(View.GONE);
        });

        ok.setOnClickListener(v -> {
            expandable_layout.expand(false);
            expandable_layout.setVisibility(View.GONE);
            if (mTopevent.callback != null) {
                mTopevent.callback.setCallback(mTop);
            }
        });

        findViewById(R.id.btn_cancel_01).setOnClickListener(v -> {
            expandable_layout01.expand(false);
            expandable_layout01.setVisibility(View.GONE);
        });
        findViewById(R.id.btn_ok__01).setOnClickListener(v -> {
            expandable_layout01.expand(false);
            expandable_layout01.setVisibility(View.GONE);
            TextView tv = findViewById(R.id.et_search);
            if (!StringUtil.isBlank(tv.getText().toString())) {
                DynamicActivitySearchResList.start(mContext, tv.getText().toString());
            }
        });

        editText = findViewById(R.id.et_search);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    return;
                }
                if (s.length() < 2) {
                    return;
                }
            }
        });

        mUserId = coreManager.getSelf().getUserId();
        mUserName = coreManager.getSelf().getNickName();
        mergerStatus = findViewById(R.id.mergerStatus);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        cv_auido = findViewById(R.id.cv_audio);

        findViewById(R.id.iv_dynamic_push).setOnClickListener(v -> {
            Intent in = new Intent(getContext(), DynamicSendActivity.class);
            startActivity(in);
        });

        findViewById(R.id.iv_my).setOnClickListener(v -> {
            viewPager.setCurrentItem(8, false);
        });

        findViewById(R.id.iv_search).setOnClickListener(v -> {
            int initialHeight = (int) (getResources().getDisplayMetrics().heightPixels * 1);
            OperLogUtil.msg("点击了动态搜索");
            FragmentDynamicList fragmentDynamicList = new FragmentDynamicList();
            fragmentDynamicList.setContentType(15);
            CustomBottomSheetDialogFragment dialog = new CustomBottomSheetDialogFragment(fragmentDynamicList, initialHeight);
            dialog.show(((AppCompatActivity) mContext).getSupportFragmentManager(), "搜索");
        });
    }

    private void initTabLayout() {
        mAdapter1 = new FragmentCacheAdapter(getChildFragmentManager());
        tabLayout.setTabMode(MODE_SCROLLABLE);
        tabLayout.addOnTabSelectedListener(this);
        viewPager.setAdapter(mAdapter1);
        viewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(viewPager);
        refreshAdapter(true);
    }

    private void refreshAdapter(boolean isShow) {
        if (isShow) {
            String[] types = {getString(R.string.leader_comment), getString(R.string.follows), getString(R.string.place_work), getString(R.string.place_all), getString(R.string.place_platinum), getString(R.string.place_cream), getString(R.string.place_topic), getString(R.string.place_fav), getString(R.string.place_user_center)};
            int i = 0;
            for (String page : types) {
                if (i == 0) {
                    mAdapter1.addFragment(new FragmentDynamicReviewList(), page);
                } else if (i == 6) {
                    mAdapter1.addFragment(new FragmentDynamicTopic(), page);
                } else {
                    FragmentDynamicList fragment = new FragmentDynamicList();
                    fragment.setContentType(i);
                    mAdapter1.addFragment(fragment, page);
                }
                i++;
            }
            mAdapter1.notifyDataSetChanged();
            viewPager.setCurrentItem(3, false);
        } else {
            mAdapter1.clear();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void refreshComplete() {
        mListView.postDelayed(() -> {
            mRefreshLayout.finishRefresh();
            mRefreshLayout.finishLoadMore();
        }, 200);
    }

    public void updateTip() {
        int tipCount = MyZanDao.getInstance().getZanSize(coreManager.getSelf().getUserId());
        if (tipCount == 0) {
            mTipLl.setVisibility(View.GONE);
            EventBus.getDefault().post(new MessageEventHongdian(0));
        } else {
            List<MyZan> zanList = MyZanDao.getInstance().queryZan(coreManager.getSelf().getUserId());
            if (zanList == null || zanList.size() == 0) {
                return;
            }
            MyZan zan = zanList.get(zanList.size() - 1);
            AvatarHelper.getInstance().displayAvatar(zan.getFromUsername(), zan.getFromUserId(), mTipIv, true);
            mTipTv.setText(tipCount + getString(R.string.piece_new_message));
            mTipLl.setVisibility(View.VISIBLE);
            EventBus.getDefault().post(new MessageEventHongdian(tipCount));
        }
    }

    public void showToCurrent(String mCommentId) {
        int pos = -1;
        for (int i = 0; i < mMessages.size(); i++) {
            if (StringUtils.strEquals(mCommentId, mMessages.get(i).getMessageId())) {
                pos = i + 2;
                break;
            }
        }
        if (pos != -1) {
            mListView.scrollToPosition(pos);
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 清理资源
        if (mLoadingMask != null && mLoadingMask.getParent() instanceof ViewGroup) {
            ((ViewGroup) mLoadingMask.getParent()).removeView(mLoadingMask);
        }
        mLoadingMask = null;
        mLoadingProgress = null;
        if (mainHandler != null) {
            mainHandler.removeCallbacksAndMessages(null);
        }
    }

    public int getTabIndex() {
        return tabIndex;
    }

    public void setTabIndex(int tabIndex) {
        this.tabIndex = tabIndex;
        tabLayout.selectTab(tabLayout.getTabAt(tabIndex));
    }
}