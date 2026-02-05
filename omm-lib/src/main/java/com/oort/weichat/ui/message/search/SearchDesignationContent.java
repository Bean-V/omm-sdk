package com.oort.weichat.ui.message.search;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.oort.weichat.bean.Friend;
import com.oort.weichat.bean.message.ChatMessage;
import com.oort.weichat.bean.message.XmppMessage;
import com.oort.weichat.ui.mucfile.DownManager;
import com.oort.weichat.ui.mucfile.MucFileDetails;
import com.oort.weichat.ui.mucfile.XfileUtils;
import com.oort.weichat.ui.mucfile.bean.MucFileBean;
import com.oort.weichat.AppConstant;
import com.oort.weichat.R;
import com.oort.weichat.db.dao.ChatMessageDao;
import com.oort.weichat.db.dao.FriendDao;
import com.oort.weichat.helper.AvatarHelper;
import com.oort.weichat.helper.ImageLoadHelper;
import com.oort.weichat.ui.base.BaseActivity;
import com.oort.weichat.ui.message.ChatActivity;
import com.oort.weichat.ui.message.MucChatActivity;
import com.oort.weichat.ui.tool.WebViewActivity;
import com.oort.weichat.util.TimeUtils;
import com.oort.weichat.view.CircleImageView;
import com.oortcloud.contacts.activity.PersonDetailActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.oort.weichat.ui.tool.WebViewActivity.EXTRA_URL;

/**
 * 查找指定内容
 * 文件、链接、音乐、交易
 */
public class SearchDesignationContent extends BaseActivity {

    public static int TYPE_FILE = 0;
    public static int TYPE_LINK = 1;
    public static int TYPE_PAY = 2;
    public static int TYPE_GROUP_MEMBER = 3;
    public static int TYPE_DATE = 4;
    public static int TYPE_CARD = 5;
    private int mSearchType;
    private String mSearchObject;
    private String mSearchMemberUserid;
    private double mSearchDate;
    private RecyclerView mRecyclerView;
    private DesignationContentAdapter mDesignationContentAdapter;
    private List<ChatMessage> mChatMessage = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_designation_content);
        mSearchType = getIntent().getIntExtra("search_type", TYPE_FILE);
        mSearchObject = getIntent().getStringExtra("search_objectId");
        mSearchMemberUserid = getIntent().getStringExtra("member_userid");
        mSearchDate = getIntent().getDoubleExtra("search_date",0);

        if (mSearchType == TYPE_FILE) {
            List<ChatMessage> fileList = ChatMessageDao.getInstance().queryChatMessageByType(coreManager.getSelf().getUserId(), mSearchObject, XmppMessage.TYPE_FILE);
            mChatMessage.addAll(fileList);
        } else if (mSearchType == TYPE_LINK) {
            List<ChatMessage> linkList = ChatMessageDao.getInstance().queryChatMessageByType(coreManager.getSelf().getUserId(), mSearchObject, XmppMessage.TYPE_LINK);
            List<ChatMessage> shareLinkList = ChatMessageDao.getInstance().queryChatMessageByType(coreManager.getSelf().getUserId(), mSearchObject, XmppMessage.TYPE_SHARE_LINK);
            mChatMessage.addAll(linkList);
            mChatMessage.addAll(shareLinkList);
        } else if (mSearchType == TYPE_PAY) {
            List<ChatMessage> redList = ChatMessageDao.getInstance().queryChatMessageByType(coreManager.getSelf().getUserId(), mSearchObject, XmppMessage.TYPE_RED);
            List<ChatMessage> transferList = ChatMessageDao.getInstance().queryChatMessageByType(coreManager.getSelf().getUserId(), mSearchObject, XmppMessage.TYPE_TRANSFER);
            mChatMessage.addAll(redList);
            mChatMessage.addAll(transferList);
        }else if (mSearchType == TYPE_GROUP_MEMBER) {
            if (!TextUtils.isEmpty(mSearchMemberUserid)) {
                List<ChatMessage> fileList = ChatMessageDao.getInstance().getMemberAllMessage(coreManager.getSelf().getUserId(), mSearchObject, mSearchMemberUserid);
                if(fileList != null) {
                    mChatMessage.addAll(fileList);
                }
            }
        }else if (mSearchType == TYPE_DATE) {
            if (mSearchDate > 0 ) {
                List<ChatMessage> fileList = ChatMessageDao.getInstance().searchMessagesByDate(coreManager.getSelf().getUserId(), mSearchObject, mSearchDate);
                mChatMessage.addAll(fileList);
            }
        }else if (mSearchType == TYPE_CARD) {
            List<ChatMessage> cardList = ChatMessageDao.getInstance().queryChatMessageByType(coreManager.getSelf().getUserId(), mSearchObject, XmppMessage.TYPE_CARD);
            mChatMessage.addAll(cardList);
        }
        // 根据timeSend排序
        Comparator<ChatMessage> comparator = (o1, o2) -> (int) (o2.getDoubleTimeSend() - o1.getDoubleTimeSend());
        Collections.sort(mChatMessage, comparator);

        initActionBar();
        initView();
    }

    private void initActionBar() {
        getSupportActionBar().hide();
        findViewById(R.id.iv_title_left).setOnClickListener(v -> finish());
        TextView tvTitle = findViewById(R.id.tv_title_center);
        if (mSearchType == TYPE_FILE) {
            tvTitle.setText(getString(R.string.s_file));
        } else if (mSearchType == TYPE_LINK) {
            tvTitle.setText(getString(R.string.s_link));
        } else if (mSearchType == TYPE_PAY) {
            tvTitle.setText(getString(R.string.s_pay));
        }else if (mSearchType == TYPE_GROUP_MEMBER) {
            tvTitle.setText("群成员");
        }else if (mSearchType == TYPE_DATE) {
            tvTitle.setText("日期");
        }else if (mSearchType == TYPE_CARD) {
            tvTitle.setText("名片");
        }
    }

    private void initView() {
        mRecyclerView = findViewById(R.id.s_dest_content_rcy);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mDesignationContentAdapter = new DesignationContentAdapter(mChatMessage);
        mRecyclerView.setAdapter(mDesignationContentAdapter);
    }

    class DesignationContentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<ChatMessage> mChatMessageSource;

        public DesignationContentAdapter(List<ChatMessage> chatMessages) {
            this.mChatMessageSource = chatMessages;
            if (mChatMessageSource == null) {
                mChatMessageSource = new ArrayList<>();
            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new DesignationContentHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_designation, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            ChatMessage chatMessage = mChatMessageSource.get(i);
            Friend friend = FriendDao.getInstance().getFriend(coreManager.getSelf().getUserId(), chatMessage.getFromUserId());
            String name = chatMessage.getFromUserName();
            if (friend != null && !TextUtils.isEmpty(friend.getRemarkName())) {
                name = friend.getRemarkName();
            }

            AvatarHelper.getInstance().displayAvatar(name, chatMessage.getFromUserId(),
                    ((DesignationContentHolder) viewHolder).mAvatarIv, true);
            if (chatMessage.getType() == XmppMessage.TYPE_TRANSFER) {
                ((DesignationContentHolder) viewHolder).mNameTv.setText(getString(R.string.start_transfer, name));
            } else {
                ((DesignationContentHolder) viewHolder).mNameTv.setText(name);
            }
            ((DesignationContentHolder) viewHolder).mDateTv.setText(TimeUtils.getFriendlyTimeDesc(mContext,
                    chatMessage.getTimeSend()));
            if (chatMessage.getType() == XmppMessage.TYPE_FILE) {
                // 文件
                fillFileData(chatMessage, ((DesignationContentHolder) viewHolder).mAbstractLl, ((DesignationContentHolder) viewHolder).mAbstractLeftIv,
                        ((DesignationContentHolder) viewHolder).mAbstractTopTv, ((DesignationContentHolder) viewHolder).mAbstractBottomTv);
            } else if (chatMessage.getType() == XmppMessage.TYPE_LINK || chatMessage.getType() == XmppMessage.TYPE_SHARE_LINK) {
                // 链接
                fillLinkData(chatMessage, ((DesignationContentHolder) viewHolder).mAbstractLl, ((DesignationContentHolder) viewHolder).mAbstractLeftIv,
                        ((DesignationContentHolder) viewHolder).mAbstractTopTv, ((DesignationContentHolder) viewHolder).mAbstractBottomTv);
            } else if (chatMessage.getType() == XmppMessage.TYPE_RED || chatMessage.getType() == XmppMessage.TYPE_TRANSFER) {
                // 红包与转账
                fillRedTransferData(chatMessage, ((DesignationContentHolder) viewHolder).mAbstractLl, ((DesignationContentHolder) viewHolder).mAbstractLeftIv,
                        ((DesignationContentHolder) viewHolder).mAbstractTopTv, ((DesignationContentHolder) viewHolder).mAbstractBottomTv);
            }else if (chatMessage.getType() == XmppMessage.TYPE_CARD ) {
                // 卡片
                fillCardData(chatMessage, ((DesignationContentHolder) viewHolder).mAbstractLl, ((DesignationContentHolder) viewHolder).mAbstractLeftIv,
                        ((DesignationContentHolder) viewHolder).mAbstractTopTv, ((DesignationContentHolder) viewHolder).mAbstractBottomTv);
            }else {
                fillData(chatMessage, ((DesignationContentHolder) viewHolder).mAbstractLl, ((DesignationContentHolder) viewHolder).mAbstractLeftIv,
                        ((DesignationContentHolder) viewHolder).mAbstractTopTv, ((DesignationContentHolder) viewHolder).mAbstractBottomTv);
            }
        }

        @Override
        public int getItemCount() {
            return mChatMessageSource.size();
        }

        private void fillFileData(ChatMessage chatMessage, LinearLayout ll, ImageView iv, TextView topTv, TextView bottomTv) {
            String filePath = TextUtils.isEmpty(chatMessage.getFilePath()) ? chatMessage.getContent() : chatMessage.getFilePath();
            int index = filePath.lastIndexOf(".");
            String type = filePath.substring(index + 1).toLowerCase();
            int start = filePath.lastIndexOf("/");
            String fileName = filePath.substring(start + 1).toLowerCase();

            if (type.equals("png") || type.equals("jpg")) {
                ImageLoadHelper.showImageWithError(
                        mContext,
                        filePath,
                        R.drawable.image_download_fail_icon,
                        iv
                );
            } else {
                AvatarHelper.getInstance().fillFileView(type, iv);
            }
            topTv.setText(fileName);
            bottomTv.setText(XfileUtils.fromatSize(chatMessage.getFileSize()));

            ll.setOnClickListener(v -> {
                MucFileBean mucFileBean = new MucFileBean();
                mucFileBean.setName(fileName);
                mucFileBean.setNickname(fileName);
                mucFileBean.setUrl(chatMessage.getContent());
                mucFileBean.setSize(chatMessage.getFileSize());
                mucFileBean.setState(DownManager.STATE_UNDOWNLOAD);
                mucFileBean.setType(XfileUtils.getFileType(type));
                Intent intent = new Intent(mContext, MucFileDetails.class);
                intent.putExtra("data", mucFileBean);
                startActivity(intent);
            });
        }

        private void fillLinkData(ChatMessage chatMessage, LinearLayout ll, ImageView iv, TextView topTv, TextView bottomTv) {
            if (chatMessage.getType() == XmppMessage.TYPE_LINK) {
                // 普通链接
                try {
                    JSONObject json = new JSONObject(chatMessage.getContent());
                    String linkTitle = json.getString("title");
                    String linkImage = json.getString("img");
                    String linkAddress = json.getString("url");

                    ImageLoadHelper.showImageWithError(
                            mContext,
                            linkImage,
                            R.drawable.browser,
                            iv
                    );
                    topTv.setText(linkTitle);

                    ll.setOnClickListener(v -> {
                        Intent intent = new Intent(mContext, WebViewActivity.class);
                        intent.putExtra(EXTRA_URL, linkAddress);
                        mContext.startActivity(intent);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                // SK ShareSDK 分享进来的链接
                try {
                    JSONObject json = new JSONObject(chatMessage.getObjectId());
                    String appName = json.getString("appName");
                    String appIcon = json.getString("appIcon");
                    String title = json.getString("title");
                    String subTitle = json.getString("subTitle");
                    String imageUrl = json.getString("imageUrl");
                    String linkShareAddress = json.getString("url");
                    String linkShareDownAppAddress = json.getString("downloadUrl");

                    if (TextUtils.isEmpty(appIcon) && TextUtils.isEmpty(imageUrl)) {
                        iv.setImageResource(R.drawable.browser);
                    } else if (TextUtils.isEmpty(imageUrl)) {
                        AvatarHelper.getInstance().displayUrl(appIcon, iv);
                    } else {
                        AvatarHelper.getInstance().displayUrl(imageUrl, iv);
                    }
                    topTv.setText(title);
                    bottomTv.setText(subTitle);

                    ll.setOnClickListener(v -> {
                        Intent intent = new Intent(mContext, WebViewActivity.class);
                        intent.putExtra(WebViewActivity.EXTRA_URL, linkShareAddress);
                        intent.putExtra(WebViewActivity.EXTRA_DOWNLOAD_URL, linkShareDownAppAddress);
                        mContext.startActivity(intent);
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        private void fillRedTransferData(ChatMessage chatMessage, LinearLayout ll, ImageView iv, TextView topTv, TextView bottomTv) {
            if (chatMessage.getType() == XmppMessage.TYPE_RED) {
                // 红包
                iv.setImageResource(R.drawable.ic_chat_hongbao);
                topTv.setText(chatMessage.getContent());
            } else {
                // 转账
                iv.setImageResource(R.drawable.ic_tip_transfer_money);
                topTv.setText("￥ " + chatMessage.getContent());
                bottomTv.setText(chatMessage.getFilePath());
            }

            ll.setOnClickListener(v -> {
                Friend friend = FriendDao.getInstance().getFriend(coreManager.getSelf().getUserId(), mSearchObject);
                if (friend != null) {
                    Intent intent = new Intent();
                    if (friend.getRoomFlag() == 0) { // 个人
                        intent.setClass(mContext, ChatActivity.class);
                        intent.putExtra(ChatActivity.FRIEND, friend);
                    } else {
                        intent.setClass(mContext, MucChatActivity.class);
                        intent.putExtra(AppConstant.EXTRA_USER_ID, friend.getUserId());
                        intent.putExtra(AppConstant.EXTRA_NICK_NAME, friend.getNickName());
                    }

                    intent.putExtra("isserch", true);
                    intent.putExtra("jilu_id", chatMessage.getDoubleTimeSend());
                    startActivity(intent);
                }
            });
        }

        private void fillCardData(ChatMessage chatMessage, LinearLayout ll, ImageView iv, TextView topTv, TextView bottomTv) {
            if (chatMessage.getType() == XmppMessage.TYPE_CARD) {
                topTv.setText(chatMessage.getContent());
                bottomTv.setText("个人名片");
            }
            ll.setOnClickListener(v -> {
                String userid = chatMessage.getObjectId();
                PersonDetailActivity.actionStart(SearchDesignationContent.this,userid);
            });
        }

        private void fillData(ChatMessage chatMessage, LinearLayout ll, ImageView iv, TextView topTv, TextView bottomTv) {

            iv.setVisibility(View.GONE);
            bottomTv.setVisibility(View.GONE);
            topTv.setText(chatMessage.getContent());

            ll.setOnClickListener(v -> {
                String userid = chatMessage.getObjectId();
                PersonDetailActivity.actionStart(SearchDesignationContent.this,userid);
            });
        }
    }

    class DesignationContentHolder extends RecyclerView.ViewHolder {

        private ImageView mAvatarIv;
        private TextView mNameTv, mDateTv;

        private LinearLayout mAbstractLl;
        private ImageView mAbstractLeftIv;
        private TextView mAbstractTopTv, mAbstractBottomTv;

        public DesignationContentHolder(@NonNull View itemView) {
            super(itemView);
            mAvatarIv = itemView.findViewById(R.id.avatar_iv);
            mNameTv = itemView.findViewById(R.id.name_tv);
            mDateTv = itemView.findViewById(R.id.date_tv);
            mAbstractLl = itemView.findViewById(R.id.abstract_ll);
            mAbstractLeftIv = itemView.findViewById(R.id.abstract_left_iv);
            mAbstractTopTv = itemView.findViewById(R.id.abstract_top_tv);
            mAbstractBottomTv = itemView.findViewById(R.id.abstract_bottom_tv);
        }
    }
}
