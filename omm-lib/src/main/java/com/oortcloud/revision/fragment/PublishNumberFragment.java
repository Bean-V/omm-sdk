package com.oortcloud.revision.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.oort.weichat.R;
import com.oort.weichat.bean.Friend;
import com.oort.weichat.bean.message.ChatMessage;
import com.oort.weichat.bean.message.NewFriendMessage;
import com.oort.weichat.bean.message.XmppMessage;
import com.oort.weichat.broadcast.CardcastUiUpdateUtil;
import com.oort.weichat.db.dao.FriendDao;
import com.oort.weichat.db.dao.NewFriendDao;
import com.oort.weichat.helper.AvatarHelper;
import com.oort.weichat.helper.DialogHelper;
import com.oort.weichat.helper.FriendHelper;
import com.oort.weichat.pay.sk.SKPayActivity;
import com.oort.weichat.ui.base.EasyFragment;
import com.oort.weichat.ui.message.ChatActivity;
import com.oort.weichat.ui.nearby.PublicNumberSearchActivity;
import com.oort.weichat.util.TimeUtils;
import com.oort.weichat.util.ToastUtil;
import com.oort.weichat.util.ViewHolder;
import com.oort.weichat.view.HeadView;
import com.oort.weichat.view.SelectionFrame;
import com.oort.weichat.xmpp.ListenerManager;
import com.oortcloud.basemodule.im.IMUserInfoUtil;
import com.oortcloud.basemodule.utils.OperLogUtil;
import com.roamer.slidelistview.SlideBaseAdapter;
import com.roamer.slidelistview.SlideListView;
import com.xuan.xuanhttplibrary.okhttp.HttpUtils;
import com.xuan.xuanhttplibrary.okhttp.callback.BaseCallback;
import com.xuan.xuanhttplibrary.okhttp.result.ObjectResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * @filename:
 * @author: zzj/@date: 2021/3/8 16:45
 * @version： v1.0
 * @function： 公众号Fragment
 */
 public class PublishNumberFragment extends EasyFragment {

    private SlideListView mNoticeAccountList;
    private NoticeAdapter mNoticeAdapter;
    private List<Friend> mNoticeFriendList;
    private long mOldTime;
    private long mDelayMilliseconds = 1000;
    private Context mContext;

    private FloatingActionButton mFab;
    @Override
    protected int inflateLayoutId() {
        return R.layout.activity_notice;
    }

    @Override
    protected void onActivityCreated(Bundle savedInstanceState, boolean createView) {
        initActionBar();
        initView();
        mContext = getActivity();
    }

    private void initActionBar() {

        findViewById(R.id.iv_title_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        TextView tvTitle = (TextView) findViewById(R.id.tv_title_center);
        tvTitle.setText(R.string.public_number);
        if (coreManager.getConfig().enableMpModule) {
            ImageView ivRight = (ImageView) findViewById(R.id.iv_title_right);
            ivRight.setImageResource(R.mipmap.search_icon);
            ivRight.setOnClickListener(v -> {
                PublicNumberSearchActivity.start(getActivity());
                OperLogUtil.msg("点击搜索应用号");
            });
        }
        findViewById(R.id.search_edit).setOnClickListener(v -> {
            PublicNumberSearchActivity.start(mContext);
            OperLogUtil.msg("点击搜索应用号");
        });
    }

    private void initView() {

        mNoticeFriendList = FriendDao.getInstance().getAllSystems(coreManager.getSelf().getUserId());
        if (mNoticeFriendList == null) {
            mNoticeFriendList = new ArrayList<>();
        }
        // 关闭支付功能，移除支付公众号
        if (!coreManager.getConfig().enablePayModule) {
            for (int i = 0; i < mNoticeFriendList.size(); i++) {
                if (mNoticeFriendList.get(i).getUserId().equals(Friend.ID_SK_PAY)) {
                    mNoticeFriendList.remove(i);
                    break;
                }
            }
        }
        mNoticeAccountList = findViewById(R.id.notice_account_lv);
        mNoticeAdapter = new NoticeAdapter(getActivity());
        mNoticeAccountList.setAdapter(mNoticeAdapter);
        mNoticeAccountList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                long nowTime = SystemClock.elapsedRealtime();
                long intervalTime = nowTime - mOldTime;
                if (mOldTime == 0 || intervalTime >= mDelayMilliseconds) {
                    mOldTime = nowTime;

                    Friend mFriend = mNoticeFriendList.get(position);
                    if (mFriend != null) {
                        if (mFriend.getUserId().equals(Friend.ID_SK_PAY)) {
                            startActivity(new Intent(mContext, SKPayActivity.class));
                        } else {
                            Intent intent = new Intent(mContext, ChatActivity.class);
                            intent.putExtra(ChatActivity.FRIEND, mFriend);
                            startActivity(intent);
                        }
                        OperLogUtil.msg("公众号列表进入" + mFriend.getShowName());
                    }

                }
            }
        });


    }

    // 删除公众号，
    private void showDeleteAllDialog(final int position) {
        Friend friend = mNoticeFriendList.get(position);
        if (friend.getStatus() == Friend.STATUS_UNKNOW) {// 陌生人
            return;
        }
        if (friend.getUserId().equals(Friend.ID_SYSTEM_MESSAGE)
                || friend.getUserId().equals(Friend.ID_SK_PAY)) {// 10000 与1100 号不能删除，
            Toast.makeText(mContext, getString(R.string.tip_not_allow_delete), Toast.LENGTH_SHORT).show();
            OperLogUtil.msg("提示应用号不可删除");
            return;
        }
        SelectionFrame mSF = new SelectionFrame(mContext);
        mSF.setSomething(getString(R.string.delete_public_number), getString(R.string.ask_delete_public_number), new SelectionFrame.OnSelectionFrameClickListener() {
            @Override
            public void cancelClick() {

            }

            @Override
            public void confirmClick() {
                deleteFriend(position, 1);
            }
        });
        mSF.show();
    }

    private void deleteFriend(final int position, final int type) {
        Friend friend = mNoticeFriendList.get(position);
        Map<String, String> params = new HashMap<>();
        params.put("access_token", IMUserInfoUtil.getInstance().getToken());
        params.put("toUserId", friend.getUserId());
        DialogHelper.showDefaulteMessageProgressDialog(mContext);

        HttpUtils.get().url(coreManager.getConfig().FRIENDS_ATTENTION_DELETE)
                .params(params)
                .build()
                .execute(new BaseCallback<Void>(Void.class) {

                    @Override
                    public void onResponse(ObjectResult<Void> result) {
                        DialogHelper.dismissProgressDialog();
                        if (result.getResultCode() == 1) {
                            NewFriendMessage message = NewFriendMessage.createWillSendMessage(
                                    coreManager.getSelf(), XmppMessage.TYPE_DELALL, null, friend);
                            coreManager.sendNewFriendMessage(coreManager.getSelf().getUserId(), message); // 删除好友
                            FriendHelper.removeAttentionOrFriend(coreManager.getSelf().getUserId(), message.getUserId());

                            ChatMessage deleteChatMessage = new ChatMessage();
                            deleteChatMessage.setContent(getString(R.string.has_delete_public_number_place_holder, coreManager.getSelf().getNickName()));
                            deleteChatMessage.setDoubleTimeSend(TimeUtils.sk_time_current_time_double());
                            FriendDao.getInstance().updateLastChatMessage(coreManager.getSelf().getUserId(), Friend.ID_NEW_FRIEND_MESSAGE, deleteChatMessage);

                            message.setContent(getString(R.string.delete_firend_public) + friend.getNickName());
                            NewFriendDao.getInstance().createOrUpdateNewFriend(message);
                            NewFriendDao.getInstance().changeNewFriendState(coreManager.getSelf().getUserId(), Friend.STATUS_16);
                            ListenerManager.getInstance().notifyNewFriend(coreManager.getSelf().getUserId(), message, true);

                            CardcastUiUpdateUtil.broadcastUpdateUi(mContext);
                            mNoticeFriendList.remove(position);
                            mNoticeAdapter.notifyDataSetChanged();
                        } else if (!TextUtils.isEmpty(result.getResultMsg())) {
                            ToastUtil.showToast(mContext, result.getResultMsg());
                        } else {
                            ToastUtil.showToast(mContext, R.string.tip_server_error);
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        DialogHelper.dismissProgressDialog();
                        ToastUtil.showNetError(mContext);
                    }
                });
    }

    class NoticeAdapter extends SlideBaseAdapter {
        NoticeAdapter(Context context) {
            super(context);
        }

        @Override
        public int getFrontViewId(int position) {
            return R.layout.item_notice_account;
        }

        @Override
        public int getLeftBackViewId(int position) {
            return 0;
        }

        @Override
        public int getRightBackViewId(int position) {
            return R.layout.item_notice_right;
        }

        @Override
        public int getCount() {
            return mNoticeFriendList.size();
        }

        @Override
        public Friend getItem(int position) {
            return mNoticeFriendList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = createConvertView(position);
            }
            HeadView mNoticeAccountIv = ViewHolder.get(convertView, R.id.notice_iv);

            mNoticeAccountIv.setRound(false);
            TextView mNoticeAccountTv = ViewHolder.get(convertView, R.id.notice_tv);

            Friend friend = getItem(position);
            if (friend != null) {
                AvatarHelper.getInstance().displayAvatar(friend.getUserId(), mNoticeAccountIv);
                mNoticeAccountTv.setText(!TextUtils.isEmpty(friend.getRemarkName()) ? friend.getRemarkName() : friend.getNickName());
            }

            TextView delete_tv = ViewHolder.get(convertView, R.id.delete_tv);

            delete_tv.setOnClickListener(v -> {
                showDeleteAllDialog(position);
                OperLogUtil.msg("弹出删除公众号弹窗");
            });
            return convertView;
        }
    }
}
