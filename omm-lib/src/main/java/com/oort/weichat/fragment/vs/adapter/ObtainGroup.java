package com.oort.weichat.fragment.vs.adapter;


import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.oort.weichat.Reporter;
import com.oort.weichat.bean.Friend;
import com.oort.weichat.bean.RoomMember;
import com.oort.weichat.bean.message.MucRoom;
import com.oort.weichat.db.dao.FriendDao;
import com.oort.weichat.db.dao.OnCompleteListener2;
import com.oort.weichat.db.dao.RoomMemberDao;
import com.oort.weichat.sortlist.BaseSortModel;
import com.oort.weichat.sortlist.SortHelper;
import com.oort.weichat.ui.base.CoreManager;
import com.oort.weichat.util.AsyncUtils;
import com.oortcloud.basemodule.im.IMUserInfoUtil;
import com.oortcloud.basemodule.user.v2.UserInfoUtils;
import com.xuan.xuanhttplibrary.okhttp.HttpUtils;
import com.xuan.xuanhttplibrary.okhttp.callback.ListCallback;
import com.xuan.xuanhttplibrary.okhttp.result.ArrayResult;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Email 465571041@qq.com
 * Created by zhang-zhi-jun on 2025/8/20-11:14.
 * Version 1.0
 * Description:获取消息群组
 */
public class ObtainGroup {
    private Context mContext;
    private String mLoginUserId;

    private RoomListListener mRoomListListener;
    /**
     * 下载我的群组
     */
    public ObtainGroup(Context context){
        this.mContext = context;
        mLoginUserId = UserInfoUtils.getInstance(mContext).getImUserId();
    }

    public void setRoomListListener(RoomListListener roomListListener) {
        mRoomListListener = roomListListener;
    }
    public void updateRoom(CoreManager coreManager) {
        Log.e("zq", "ObtainGroup-群组: -updateRoom->" );
        HashMap<String, String> params = new HashMap<>();
        params.put("access_token", IMUserInfoUtil.getInstance().getToken());
        params.put("type", "0");
        params.put("pageIndex", "0");
        params.put("pageSize", "1000");// 给一个尽量大的值
        HttpUtils.get().url(coreManager.getConfig().ROOM_LIST_HIS)
                .params(params)
                .build()
                .execute(new ListCallback<MucRoom>(MucRoom.class) {
                    @Override
                    public void onResponse(ArrayResult<MucRoom> result) {
                        if (result.getResultCode() == 1) {
                            FriendDao.getInstance().addRooms( new Handler(), mLoginUserId, result.getData(),
                                    new OnCompleteListener2() {
                                        @Override
                                        public void onLoading(int progressRate, int sum) {
                                        }

                                        @Override
                                        public void onCompleted() {// 下载完成
                                            if (coreManager.isLogin()) {
                                                // 1.调用smack内join方法加入群组
                                                List<Friend> mFriends = FriendDao.getInstance().getAllRooms(mLoginUserId);
                                                for (int i = 0; i < mFriends.size(); i++) {// 已加入的群组不会重复加入，方法内已去重
                                                    coreManager.joinMucChat(mFriends.get(i).getUserId(), 0);
                                                }

                                            }
                                            // 2.更新我的群组列表
                                            loadData(mLoginUserId);
                                        }
                                    });
                        }
                    }
                    @Override
                    public void onError(Call call, Exception e) {
                    }
                });
    }
    private void loadData(String mLoginUserId) {
        String searchKey = "";
        AsyncUtils.doAsync(this, e -> {
            Reporter.post("加载数据失败，", e);
        }, c -> {
            long startTime = System.currentTimeMillis();
            final List<Friend> friends;
            friends = FriendDao.getInstance().getAllRooms(mLoginUserId);
            if (!TextUtils.isEmpty(searchKey)) {
                Iterator<Friend> iterator = friends.iterator();
                while (iterator.hasNext()) {
                    Friend friend = iterator.next();
                    if (friend.getNickName().contains(searchKey)) {
                        continue;
                    }
                    RoomMember roomMember = RoomMemberDao.getInstance().searchMemberContains(friend, searchKey);
                    if (roomMember != null) {
                        continue;
                    }
                    iterator.remove();
                }
            }
            Map<String, Integer> existMap = new HashMap<>();
            List<BaseSortModel<Friend>> sortedList = SortHelper.toSortedModelList(friends, existMap, Friend::getShowName);

            long delayTime = 200 - (startTime - System.currentTimeMillis());// 保证至少200ms的刷新过程
            if (delayTime < 0) {
                delayTime = 0;
            }

            c.postDelayed(r -> {
//                mSideBar.setExistMap(existMap);
//                mSortFriends = sortedList;
//                mAdapter.setData(sortedList);
                if (mRoomListListener != null){
                    mRoomListListener.getRoomList(sortedList);
                }

            }, delayTime);


        });
    }

    public interface RoomListListener{
        void getRoomList(List<BaseSortModel<Friend>> mucRooms);
    }
}
