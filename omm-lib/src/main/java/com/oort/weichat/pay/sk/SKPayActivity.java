package com.oort.weichat.pay.sk;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.oort.weichat.bean.Friend;
import com.oort.weichat.bean.message.ChatMessage;
import com.oort.weichat.R;
import com.oort.weichat.db.dao.ChatMessageDao;
import com.oort.weichat.ui.base.BaseActivity;
import com.oort.weichat.util.TimeUtils;
import com.oort.weichat.xmpp.ListenerManager;
import com.oort.weichat.xmpp.listener.ChatMessageListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SKPayActivity extends BaseActivity implements ChatMessageListener {

    private RecyclerView mSKPayRcy;
    private SKPayAdapter mSKPayAdapter;
    private List<ChatMessage> mChatMessageSource = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sk_pay);
        initActionBar();
        initView();
        ListenerManager.getInstance().addChatMessageListener(this);
    }

    private void initActionBar() {
        getSupportActionBar().hide();
        findViewById(R.id.iv_title_left).setOnClickListener(view -> finish());
        TextView titleTv = findViewById(R.id.tv_title_center);
        titleTv.setText(getString(R.string.sk_pay));
    }

    private void initView() {
        mChatMessageSource = new ArrayList<>();
        ChatMessageDao.getInstance().getSingleChatMessages(coreManager.getSelf().getUserId(),
                Friend.ID_SK_PAY, TimeUtils.sk_time_current_time(), 100, new ChatMessageDao.OnOperationResultListener<List<ChatMessage>>() {
                    @Override
                    public void onResult(List<ChatMessage> result) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        List<ChatMessage> messages = result;
                                        Collections.reverse(messages);// 将集合倒序
                                        mChatMessageSource.addAll(messages);
                                        mSKPayRcy = findViewById(R.id.sk_pay_rcy);
                                        mSKPayAdapter = new SKPayAdapter(mChatMessageSource);
                                        mSKPayRcy.setLayoutManager(new LinearLayoutManager(SKPayActivity.this));
                                        mSKPayRcy.setAdapter(mSKPayAdapter);
                                        mSKPayRcy.setItemAnimator(new DefaultItemAnimator());

                                        mSKPayRcy.scrollToPosition(mSKPayAdapter.getItemCount() - 1);

                                        mSKPayRcy.setVisibility(mChatMessageSource.size() > 0 ? View.VISIBLE : View.GONE);
                                    }
                                });

                            }
                        });
                    }
                });

    }

    @Override
    public void onMessageSendStateChange(int messageState, String msgId) {

    }

    @Override
    public boolean onNewMessage(String fromUserId, ChatMessage message, boolean isGroupMsg) {
        if (fromUserId.equals(Friend.ID_SK_PAY)) {
            mSKPayRcy.setVisibility(View.VISIBLE);
            mChatMessageSource.add(message);
            mSKPayAdapter.notifyItemInserted(mChatMessageSource.size());
            mSKPayRcy.scrollToPosition(mSKPayAdapter.getItemCount() - 1);
        }
        return false;
    }
}
