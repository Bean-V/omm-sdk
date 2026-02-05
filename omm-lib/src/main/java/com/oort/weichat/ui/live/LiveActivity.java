package com.oort.weichat.ui.live;

import static com.oort.weichat.ui.MainActivity.UpdatePoliceInfo;
import static com.oortcloud.basemodule.constant.Constant.VIDEO_URL;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.oort.weichat.R;
import com.oort.weichat.helper.DialogHelper;
import com.oort.weichat.ui.base.BaseActivity;
import com.oort.weichat.ui.live.bean.LiveRoom;
import com.oort.weichat.ui.live.livelist.LivePlayingFragment;
import com.oort.weichat.util.ToastUtil;
import com.oort.weichat.view.SelectionFrame;
import com.oortcloud.basemodule.im.IMUserInfoUtil;
import com.oortcloud.basemodule.navigationbar.DefaultNavigationBar;
import com.oortcloud.basemodule.utils.SkinUtils;
import com.oortcloud.basemodule.utils.fastsharedpreferences.FastSharedPreferences;
import com.oortcloud.bean.ReportInfo;
import com.oortcloud.dialog.LiveSettingDialog;
import com.xuan.xuanhttplibrary.okhttp.HttpUtils;
import com.xuan.xuanhttplibrary.okhttp.callback.BaseCallback;
import com.xuan.xuanhttplibrary.okhttp.result.ObjectResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import okhttp3.Call;

/**
 * 直播
 */
public class LiveActivity extends BaseActivity implements View.OnClickListener {
    private TabLayout tabLayout;
    private ViewPager mViewPager;

    private SelectionFrame mSelectionFrame;
    private String mLoginUserId;
    private FastSharedPreferences sharedPreferences = FastSharedPreferences.get("LOCATION_SAVE");

    private EventBus eventBus;
    private LivePlayingFragment mLivePlayingFragment;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_viewpager);
        initActionBar();
        initView();
        if (eventBus == null){
            eventBus = EventBus.getDefault();
        }
        eventBus.register(this);
        Log.v("msg" ,  "this"+sharedPreferences.getString("share_screen", ""));
    }

    private void initActionBar() {
        getSupportActionBar().hide();

        new DefaultNavigationBar.Builder(this).setTitle(getString(R.string.live_push)).setMoreClickListener(view ->  {
            new LiveSettingDialog(LiveActivity.this)
                    .setConfirmListener((String theme, String notice, boolean check) -> {
                        sharedPreferences.edit().putString("liveTheme" , theme);
                        sharedPreferences.edit().putString("liveNotice" , notice);
                        sharedPreferences.edit().putBoolean("liveCheck" , check);
                    })
                    .show();
        }).builder();

    }

    private void initView() {
        mLoginUserId = coreManager.getSelf().getUserId();

        mViewPager = (ViewPager) findViewById(R.id.tab1_vp);
        List<Fragment> fragments = new ArrayList<>();
        // TODO 隐藏全部直播
        // fragments.add(new LiveFragment());
        mLivePlayingFragment = new LivePlayingFragment();

        fragments.add(mLivePlayingFragment);
        mViewPager.setAdapter(new MyTabAdapter(getSupportFragmentManager(), fragments));

        tabLayout = (TabLayout) findViewById(R.id.tab1_layout);
        tabLayout.setTabTextColors(getResources().getColor(R.color.text_black), SkinUtils.getSkin(this).getAccentColor());
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setVisibility(View.GONE);


        findViewById(R.id.live_tv).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        isExistLiveRoom();
    }

    private void isExistLiveRoom() {
        Map<String, String> params = new HashMap<>();
        params.put("access_token", IMUserInfoUtil.getInstance().getToken());
        params.put("userId", mLoginUserId);

        HttpUtils.get().url(coreManager.getConfig().LIVE_GET_LIVEROOM)
                .params(params)
                .build()
                .execute(new BaseCallback<LiveRoom>(LiveRoom.class) {
                    @Override
                    public void onResponse(final ObjectResult<LiveRoom> result) {
                        DialogHelper.dismissProgressDialog();
                        if (isFinishing()) {
                            return;
                        }
                        if (result.getResultCode() == 1) {

                            if (result.getData() != null) {
                                LiveRoom liveRoom = result.getData();
                                if (liveRoom.getCurrentState() == 1) {
                                    DialogHelper.tip(LiveActivity.this, getString(R.string.tip_live_locking));
                                } /*else if (liveRoom.getStatus() != 0) {
                                    DialogHelper.tip(LiveActivity.this, getString(R.string.tip_live_room_online));
                                } */else {
                                    SelectionFrame selectionFrame = new SelectionFrame(LiveActivity.this);
                                    selectionFrame.setSomething(null, getString(R.string.you_have_one_live_room) + "，" +
                                            getString(R.string.start_live) + "？", new SelectionFrame.OnSelectionFrameClickListener() {
                                        @Override
                                        public void cancelClick() {

                                        }

                                        @Override
                                        public void confirmClick() {  // 进入直播间
                                            LiveRoom liveRoom = result.getData();

                                            if((!liveRoom.getUrl().startsWith("http") && !liveRoom.getUrl().startsWith("rtmp"))){

                                                DialogHelper.tipDialog(LiveActivity.this,getString(R.string.unset_url));
                                                return ;
                                            }

                                            openLive(liveRoom.getUrl(), liveRoom.getRoomId(), liveRoom.getJid(), liveRoom.getName(), liveRoom.getNotice());
                                            //记录直播链接
                                            String strurl = liveRoom.getUrl();
                                            String roomNum = strurl.substring(strurl.indexOf("live"));
                                            ReportInfo.video_num = VIDEO_URL + roomNum + ".flv" ;
                                            ReportInfo.screen_num = VIDEO_URL + roomNum + ".flv" ;
//                                            ReportInfo.video_num = liveRoom.getUrl();
//                                            ReportInfo.screen_num = liveRoom.getUrl();
                                            //存储共享链接
//                                            sharedPreferences.edit().putString("share_screen", ReportInfo.screen_num).apply();

                                            sharedPreferences.edit().putString("share_screen", liveRoom.getUrl()).apply();
                                            updatePoliceInfo();
                                        }
                                    });
                                    selectionFrame.show();
                                }
                            } else { // 创建直播间
//                                Intent intent = new Intent(LiveActivity.this, CreateLiveActivity.class);
//                                startActivity(intent);
                                /*new LiveSettingDialog(LiveActivity.this)
                                        .setTitle("新建直播")
                                        .setConfirmListener((String roomName, String roomDesc, boolean check) -> {
                                            String roomJid = coreManager.createMucRoom(roomDesc);
                                            roomName = sharedPreferences.getString("liveTheme","我的直播");
                                            roomDesc = sharedPreferences.getString("liveNotice","欢迎观看");
                                            createLive(roomJid, roomName, roomDesc);
                                        })
                                        .show();*/

                                String roomName = sharedPreferences.getString("liveTheme",getString(R.string.my_live_push));
                                String roomDesc = sharedPreferences.getString("liveNotice",getString(R.string.live_push));
                                String roomJid = coreManager.createMucRoom(roomDesc);
                                createLive(roomJid, roomName, roomDesc);
                            }
                        } else {
                            Toast.makeText(LiveActivity.this, result.getResultMsg(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        DialogHelper.dismissProgressDialog();
                        ToastUtil.showErrorNet(LiveActivity.this);
                    }
                });
    }

    private void openLive(final String url, final String roomId, final String roomJid, final String roomName, final String roomNotice) {
        DialogHelper.showDefaulteMessageProgressDialog(this);
        Map<String, String> params = new HashMap<>();
        params.put("access_token", IMUserInfoUtil.getInstance().getToken());
        params.put("roomId", roomId);
        params.put("userId", mLoginUserId);

        HttpUtils.get().url(coreManager.getConfig().JOIN_LIVE_ROOM)
                .params(params)
                .build()
                .execute(new BaseCallback<Void>(Void.class) {
                    @Override
                    public void onResponse(ObjectResult<Void> result) {
                        DialogHelper.dismissProgressDialog();
                        if (result.getResultCode() == 1) {
                            Intent intent = new Intent(LiveActivity.this, PushFlowActivity.class);
                            intent.putExtra(LiveConstants.LIVE_PUSH_FLOW_URL, url);
                            intent.putExtra(LiveConstants.LIVE_ROOM_ID, roomId);
                            intent.putExtra(LiveConstants.LIVE_CHAT_ROOM_ID, roomJid);
                            intent.putExtra(LiveConstants.LIVE_ROOM_NAME, roomName);
                            intent.putExtra(LiveConstants.LIVE_ROOM_PERSON_ID, mLoginUserId);
                            intent.putExtra(LiveConstants.LIVE_ROOM_NOTICE, roomNotice);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        DialogHelper.dismissProgressDialog();
                        ToastUtil.showErrorNet(LiveActivity.this);
                    }
                });
    }


    class MyTabAdapter extends FragmentPagerAdapter {
        List<String> listTitle = new ArrayList<>();
        private List<Fragment> mFragments;

        MyTabAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            mFragments = fragments;

            listTitle.add(getString(R.string.all_live));
            listTitle.add(getString(R.string.all_liveing));
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            if (mFragments != null) {
                return mFragments.size();
            }
            return 0;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (listTitle != null) {
                return listTitle.get(position);
            }
            return "";
        }
    }

    private void updatePoliceInfo(){
        UpdatePoliceInfo();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String message) {
        if ("Live_Activity".equals(message)){

        if (mLivePlayingFragment.getSize() !=  0){
            findViewById(R.id.live_img).setVisibility(View.GONE);
        }else {
            findViewById(R.id.live_img).setVisibility(View.VISIBLE);
        }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (eventBus != null){
            eventBus.unregister(this);
        }
    }

    //创建直播间
    private void createLive(String roomJid, String roomName, String roomDesc) {
        Map<String, String> params = new HashMap<>();
        params.put("access_token", IMUserInfoUtil.getInstance().getToken());
        params.put("userId", mLoginUserId);
        params.put("nickName",  coreManager.getSelf().getNickName());
        params.put("jid", roomJid);
        params.put("name", roomName);
        params.put("notice", roomDesc);
        HttpUtils.get().url(coreManager.getConfig().CREATE_LIVE_ROOM)
                .params(params)
                .build()
                .execute(new BaseCallback<LiveRoom>(LiveRoom.class) { // 创建直播间成功，进入推流界面
                    @Override
                    public void onResponse(ObjectResult<LiveRoom> result) {
                        DialogHelper.dismissProgressDialog();
                        if (result.getResultCode() == 1) {
                            LiveRoom room = result.getData();
                            Intent intent = new Intent(LiveActivity.this, PushFlowActivity.class);
                            intent.putExtra(LiveConstants.LIVE_PUSH_FLOW_URL, room.getUrl());
                            intent.putExtra(LiveConstants.LIVE_ROOM_ID, room.getRoomId());
                            intent.putExtra(LiveConstants.LIVE_CHAT_ROOM_ID, room.getJid());
                            intent.putExtra(LiveConstants.LIVE_ROOM_NAME, room.getName());
                            intent.putExtra(LiveConstants.LIVE_ROOM_PERSON_ID, String.valueOf(room.getUserId()));
                            intent.putExtra(LiveConstants.LIVE_ROOM_NOTICE, String.valueOf(room.getNotice()));

                            //记录直播链接
                            String strurl = room.getUrl();
                            String roomNum = strurl.substring(strurl.indexOf("live"));
                            ReportInfo.video_num = VIDEO_URL + roomNum + ".flv" ;
                            ReportInfo.screen_num = VIDEO_URL + roomNum + ".flv" ;
//                            ReportInfo.video_num = room.getUrl();
//                            ReportInfo.screen_num = room.getUrl();
                            //存储共享链接
                            sharedPreferences.edit().putString("share_screen", room.getUrl()).apply();

//                                            sharedPreferences.edit().putString("push_screen", liveRoom.getUrl()).apply();
                            updatePoliceInfo();
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        DialogHelper.dismissProgressDialog();
                        ToastUtil.showNetError(LiveActivity.this);
                    }
                });
    }

}
