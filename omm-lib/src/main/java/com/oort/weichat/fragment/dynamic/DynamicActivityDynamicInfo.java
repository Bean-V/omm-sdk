package com.oort.weichat.fragment.dynamic;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.fragment.app.FragmentTransaction;

import com.oort.weichat.R;
import com.oort.weichat.fragment.entity.DynamicSetTopEvent;
import com.oort.weichat.fragment.entity.OORTDynamic;
import com.oort.weichat.ui.base.BaseActivity;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.xuexiang.xui.XUI;
import com.xuexiang.xui.widget.layout.ExpandableLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class DynamicActivityDynamicInfo extends BaseActivity {

    private SmartRefreshLayout mRefreshLayout;
    private String mTid;

    private OORTDynamic mDynamic;
    private DynamicListAdapter.ViewHolder viewHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        XUI.initTheme(this);
        setContentView(R.layout.activity_dynamic_info);

       // getSupportActionBar().hide();

        TextView tvTitle = (TextView) findViewById(R.id.tv_title_center);
        tvTitle.setText(getString(R.string.detail));
        ImageView iv_left = (ImageView) findViewById(R.id.iv_title_left);
        iv_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mTid = getIntent().getStringExtra("tid");



        if(mTid != null) {

            FragmentDynamicList fragmentDynamicList = new FragmentDynamicList();
            fragmentDynamicList.setContentType(10);
            fragmentDynamicList.setOort_duuid(mTid);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.ll_container, fragmentDynamicList);
            transaction.commitNow();
        }

//        mRefreshLayout = findViewById(R.id.refreshLayout);
//
//        mRefreshLayout.setOnRefreshListener(refreshLayout -> {
//            requestData();
//        });
//
//        requestData();

        initViews();
        EventBus.getDefault().register(this);

    }
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }



    private int mTop;
    private ExpandableLayout expandable_layout;
    private DynamicSetTopEvent mTopevent;
    private RadioButton rb01;
    private RadioButton rb02;
    private RadioButton rb03;
    private RadioButton rb04;
    private RadioButton rb05;

    public void initViews() {


        expandable_layout = findViewById(R.id.expandable_layout);
        expandable_layout.expand(false);
        expandable_layout.setVisibility(View.GONE);
        expandable_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandable_layout.setVisibility(View.GONE);
                expandable_layout.expand(false);
            }
        });


        rb01 = findViewById(R.id.rb01);
        rb02 = findViewById(R.id.rb02);
        rb03 = findViewById(R.id.rb03);
        rb04 = findViewById(R.id.rb04);
        rb05 = findViewById(R.id.rb05);

        Button cancel = findViewById(R.id.btn_cancel);
        Button ok = findViewById(R.id.btn_ok);


        rb01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rb01.setChecked(true);
                rb02.setChecked(false);
                rb03.setChecked(false);
                rb04.setChecked(false);
                rb05.setChecked(false);
                mTop = 9;
            }
        });
        rb02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rb01.setChecked(false);
                rb02.setChecked(true);
                rb03.setChecked(false);
                rb04.setChecked(false);
                rb05.setChecked(false);
                mTop = 8;
            }
        });
        rb03.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rb01.setChecked(false);
                rb02.setChecked(false);
                rb03.setChecked(true);
                rb04.setChecked(false);
                rb05.setChecked(false);
                mTop = 7;
            }
        });
        rb04.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rb01.setChecked(false);
                rb02.setChecked(false);
                rb03.setChecked(false);
                rb04.setChecked(true);
                rb05.setChecked(false);
                mTop = 6;
            }
        });
        rb05.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rb01.setChecked(false);
                rb02.setChecked(false);
                rb03.setChecked(false);
                rb04.setChecked(false);
                rb05.setChecked(true);
                mTop = 5;
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandable_layout.expand(false);
                expandable_layout.setVisibility(View.GONE);
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandable_layout.expand(false);

                expandable_layout.setVisibility(View.GONE);


                if (mTopevent.callback != null) {
                    mTopevent.callback.setCallback(mTop);
                }

            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void helloEventBus(DynamicSetTopEvent evnet) {


        if(evnet.show){
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





//    private void requestData() {
//
//
//        String uuid= UserInfoUtils.getInstance(this).getUserId();
//        String mToken = FastSharedPreferences.get("USERINFO_SAVE").getString("token","");
//        HttpRequestParam.dynamic_info(mToken,mTid).subscribe(new RxBus.BusObserver<String>() {
//            @Override
//            public void onNext(String s) {
//                ResObj<OORTDynamic> res = JSON.parseObject(s,new TypeToken<ResObj<OORTDynamic>>() {}.getType());//
//                if(res.getCode() == 200 && res.getData() != null){
//                    mDynamic = res.getData();
//                    reloadData();
//                }
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                Log.v("msg" , e.toString());
//                mRefreshLayout.closeHeaderOrFooter();
//            }
//        });
//    }
//
//    private void reloadData() {
//
//
//        buildHolder();
//        bindHolder();
//    }
//
//    public int getItemViewType() {
//
//
//
//        OORTDynamic message = mDynamic;
//        // boolean fromSelf = message.getSource() == PublicMessage.SOURCE_SELF;
//        if (message == null || message.getDynamic() == null) {
//            // 如果为空，那么可能是数据错误，直接返回一个普通的文本视图
//            return VIEW_TYPE_NORMAL_TEXT;
//        }
//        DynamicBean body = message.getDynamic();
////        if (message.getIsAllowComment() == 1) {
////            message.setIsAllowComment(1);
////        } else {
////            message.setIsAllowComment(0);
////        }
//        if (body.getAttach_images() == null && body.getAttach_images().size() == 0) {
//            // 文本视图
//            return VIEW_TYPE_NORMAL_TEXT;
//        } else if (body.getAttach_images().size() > 0) {
//            if (body.getAttach_images().size() <= 1) {
//                // 普通的单张图片的视图
//                return VIEW_TYPE_NORMAL_SINGLE_IMAGE;
//            } else {// 普通的多张图片视图
//                return VIEW_TYPE_NORMAL_MULTI_IMAGE;
//            }
//        }
////        else if (body.getType() == PublicMessage.TYPE_VOICE) {// 普通音频
////            return VIEW_TYPE_NORMAL_VOICE;
////        } else if (body.getType() == PublicMessage.TYPE_VIDEO) {// 普通视频
////            return VIEW_TYPE_NORMAL_VIDEO;
////        } else if (body.getType() == PublicMessage.TYPE_FILE) {
////            // 文件
////            return VIEW_TYPE_NORMAL_FILE;
////        } else if (body.getType() == PublicMessage.TYPE_LINK) {
////            // 链接
////            return VIEW_TYPE_NORMAL_LINK;
////        } else {
////            // 其他，数据错误
////            return VIEW_TYPE_NORMAL_TEXT;
////        }
//
//        return VIEW_TYPE_NORMAL_TEXT;
//    }
//
//    private void buildHolder() {
//
//        View convertView = findViewById(R.id.layout_dynamic_item);
//        View innerView = null;
//        LayoutInflater mInflater = getLayoutInflater();
//        int viewType = getItemViewType();
//        if (viewType == VIEW_TYPE_NORMAL_TEXT) {
//            viewHolder = new DynamicListAdapter.NormalTextHolder(convertView);
//        } else if (viewType == VIEW_TYPE_NORMAL_SINGLE_IMAGE) {
//            DynamicListAdapter.NormalSingleImageHolder holder = new DynamicListAdapter.NormalSingleImageHolder(convertView);
//            innerView = mInflater.inflate(R.layout.p_msg_item_normal_single_img, holder.content_fl, false);
//            holder.image_view = (ImageView) innerView.findViewById(R.id.image_view);
//            holder.icon_play = (ImageView) innerView.findViewById(R.id.icon_paly);
//            viewHolder = holder;
//        } else if (viewType == VIEW_TYPE_NORMAL_MULTI_IMAGE) {
//            DynamicListAdapter.NormalMultiImageHolder holder = new DynamicListAdapter.NormalMultiImageHolder(convertView);
//            innerView = mInflater.inflate(R.layout.p_msg_item_normal_multi_img, holder.content_fl, false);
//            holder.grid_view = (MyGridView) innerView.findViewById(R.id.grid_view);
//            viewHolder = holder;
//        } else if (viewType == VIEW_TYPE_NORMAL_VOICE) {
//            DynamicListAdapter.NormalVoiceHolder holder = new DynamicListAdapter.NormalVoiceHolder(convertView);
//            innerView = mInflater.inflate(R.layout.p_msg_item_normal_voice, holder.content_fl, false);
//            holder.img_view = (ImageView) innerView.findViewById(R.id.img_view);
//            holder.voice_action_img = (ImageView) innerView.findViewById(R.id.voice_action_img);
//            holder.voice_desc_tv = (TextView) innerView.findViewById(R.id.voice_desc_tv);
//            holder.chat_to_voice = (VoiceAnimView) innerView.findViewById(R.id.chat_to_voice);
//            viewHolder = holder;
//        } else if (viewType == VIEW_TYPE_NORMAL_VIDEO) {
//            DynamicListAdapter.NormalVideoHolder holder = new DynamicListAdapter.NormalVideoHolder(convertView);
//            innerView = mInflater.inflate(R.layout.p_msg_item_normal_video, holder.content_fl, false);
//            holder.gridViewVideoPlayer = (JVCideoPlayerStandardSecond) innerView.findViewById(R.id.preview_video);
//            viewHolder = holder;
//        } else if (viewType == VIEW_TYPE_NORMAL_FILE) {
//            DynamicListAdapter.NormalFileHolder holder = new DynamicListAdapter.NormalFileHolder(convertView);
//            innerView = mInflater.inflate(R.layout.p_msg_item_normal_file, holder.content_fl, false);
//            holder.file_click = (RelativeLayout) innerView.findViewById(R.id.collection_file);
//            holder.file_image = (ImageView) innerView.findViewById(R.id.file_img);
//            holder.text_tv = (TextView) innerView.findViewById(R.id.file_name);
//            viewHolder = holder;
//        } else if (viewType == VIEW_TYPE_NORMAL_LINK) {
//            DynamicListAdapter.NormalLinkHolder holder = new DynamicListAdapter.NormalLinkHolder(convertView);
//            innerView = mInflater.inflate(R.layout.p_msg_item_normal_link, holder.content_fl, false);
//            holder.link_click = (LinearLayout) innerView.findViewById(R.id.link_ll);
//            holder.link_image = (ImageView) innerView.findViewById(R.id.link_iv);
//            holder.link_tv = (TextView) innerView.findViewById(R.id.link_text_tv);
//            viewHolder = holder;
//        } else {
//            viewHolder = new DynamicListAdapter.NormalTextHolder(convertView);
//            // throw new IllegalStateException("unkown viewType: " + viewType);
//        }
//        viewHolder.llOperator.setVisibility(View.VISIBLE);
//
//        viewHolder.iv_prise = convertView.findViewById(R.id.iv_prise);
//        viewHolder.multi_praise_tv = (TextView) convertView.findViewById(R.id.multi_praise_tv);
//        viewHolder.tvLoadMore = (TextView) convertView.findViewById(R.id.tvLoadMore);
//        viewHolder.line_v = convertView.findViewById(R.id.line_v);
//        viewHolder.command_listView = (ListView) convertView.findViewById(R.id.command_listView);
//        viewHolder.location_tv = (TextView) convertView.findViewById(R.id.location_tv);
//        if (innerView != null) {
//            viewHolder.content_fl.addView(innerView);
//        }
//        viewHolder.itemView.setOnClickListener(v -> {
//        });
//
//    }
//
//    private void bindHolder()  {
//
//        int viewType = getItemViewType();
//
//
//
//
//        // 和ViewHolder一样的，只不过用作匿名内部类里面调用需要final
//        final DynamicListAdapter.ViewHolder finalHolder = viewHolder;
//        // set data
//        final OORTDynamic message = mDynamic;
//        if (message == null) {
//            return;
//        }
//
//
//        UserInfoBean userInfoBean = message.getUserInfo(message.getDynamic().getOort_userid());
//        /* 设置头像 */
//        AvatarHelper.getInstance().displayAvatar(userInfoBean.getImuserid(), viewHolder.avatar_img);
//        /* 设置昵称 */
//        SpannableStringBuilder nickNamebuilder = new SpannableStringBuilder();
//        final String userId = userInfoBean.getOort_uuid();
//        String showName = userInfoBean.getOort_name()getShowName(userId,userInfoBean.getOort_name());
//        UserClickableSpan.setClickableSpan(mContext, nickNamebuilder, showName,  userInfoBean.getImuserid());
//        viewHolder.nick_name_tv.setText(nickNamebuilder);
//        viewHolder.nick_name_tv.setLinksClickable(true);
//        viewHolder.nick_name_tv.setMovementMethod(LinkMovementClickMethod.getInstance());
//        viewHolder.tv_dept.setText(userInfoBean.getOort_depname());
//
//
//        viewHolder.tv_baijin_flag.setVisibility(message.getDynamic().getOort_grade1() == 1 ? View.VISIBLE : View.GONE);
//        viewHolder.tv_jinghua_flag.setVisibility(message.getDynamic().getOort_grade2() == 1 ? View.VISIBLE : View.GONE);
//        viewHolder.tv_settop_flag.setVisibility(message.getDynamic().getOort_top() > 0 ? View.VISIBLE : View.GONE);
//
//        List<DynamicBean.AttachBean> atts = new ArrayList<DynamicBean.AttachBean>();
//        atts.addAll(message.getDynamic().getAttach_audios());
//        atts.addAll(message.getDynamic().getAttach_atts());
//
//        DynamicListAdapter.AttAdpter attadp = new DynamicListAdapter.AttAdpter(mContext);
//        viewHolder.rv_atts.setAdapter(attadp);
//
//        if(atts.size() > 0){
//            attadp.setData(atts);
//        }
//
//        String [] flags = {"置顶十","置顶九","置顶八","置顶七","置顶六","置顶五","置顶四","置顶三","置顶二","置顶一"};
//
//        if(message.getDynamic().getOort_top() > 0) {
//            if(message.getDynamic().getOort_top() < 10) {
//                viewHolder.tv_settop_flag.setText(flags[message.getDynamic().getOort_top()]);
//            }else {
//                viewHolder.tv_settop_flag.setText("置顶");
//            }
//        }
//
//
//        // 设置头像的点击事件
//        viewHolder.avatar_img.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!UiUtils.isNormalClick(v)) {
//                    return;
//                }
//                BasicInfoActivity.start(mContext, userInfoBean.getImuserid());
//            }
//        });
//
//        // 获取消息本身的内容
//        DynamicBean body = message.getDynamic();
//        if (body == null) {
//            return;
//        }
//
//        // 是否是转载的
//        //boolean isForwarding = message.getSource() == PublicMessage.SOURCE_FORWARDING;
//
//        // 设置body_tv
//        // todo 注释掉的代码为 展开/全文的方式显示文本，文本长度过长时会卡顿，先隐藏这种方式，换种方式(最多显示6行，操作的跳转显示)
//        if (TextUtils.isEmpty(body.getContent())) {
//            viewHolder.body_tv.setVisibility(View.GONE);
//        } else {
//            // 支持emoji显示
//            viewHolder.body_tv.setFilters(new InputFilter[]{new EmojiInputFilter(mContext)});
//            viewHolder.body_tv.setUrlText(body.getContent());
//            viewHolder.body_tv.setVisibility(View.VISIBLE);
//        }
//        // 判断是否超出6行限制，超过则显示"全文"
//        viewHolder.body_tv.post(() -> {
//            Layout layout = viewHolder.body_tv.getLayout();
//            if (layout != null) {
//                int lines = layout.getLineCount();
//                // setText换成setUrlText之后，layout.getEllipsisCount有点问题，换一个判断方法
///*
//                if (lines > 0) {
//                    if (layout.getEllipsisCount(lines - 1) > 0) {
//                        viewHolder.open_tv.setVisibility(View.VISIBLE);
//                        viewHolder.open_tv.setOnClickListener(v -> LongTextShowActivity.start(mContext, body.getText()));
//                    } else {
//                        viewHolder.open_tv.setVisibility(View.GONE);
//                        viewHolder.open_tv.setOnClickListener(null);
//                    }
//                }
//*/
//                if (lines > 6) {
//                    viewHolder.open_tv.setVisibility(View.VISIBLE);
//                    viewHolder.open_tv.setOnClickListener(v -> LongTextShowActivity.start(mContext, body.getContent()));
//                } else {
//                    viewHolder.open_tv.setVisibility(View.GONE);
//                    viewHolder.open_tv.setOnClickListener(null);
//                }
//            }
//        });
//
//        viewHolder.body_tv.setOnLongClickListener(v -> {
//            showBodyTextLongClickDialog(body.getContent());
//            return false;
//        });
//
//        int finalPosition = position;
//        // 设置发布时间 MyCollection
//        viewHolder.time_tv.setText(TimeUtils.getFriendlyTimeDesc(mContext, (int) message.getDynamic().getCreated_at()));
//        if (MyCollection.class.toString().contains(mContext.getClass().toString())) {
//            // 设置取消收藏按钮
//            viewHolder.delete_tv.setText(mContext.getString(R.string.cancel_collection));
//            viewHolder.llReport.setVisibility(View.GONE);
//        } else {
//            viewHolder.llReport.setVisibility(View.VISIBLE);
//            viewHolder.delete_tv.setText(mContext.getString(R.string.delete));
//        }
//        if (collectionType == 1) {
//            viewHolder.delete_tv.setVisibility(View.VISIBLE);
//
//            viewHolder.delete_tv.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if(UiUtils.isNormalClick(v)) {
//                        showDeleteMsgDialog(finalPosition);
//                    }
//                }
//            });
//        } else if (collectionType == 2) {
//            viewHolder.delete_tv.setVisibility(View.GONE);
//        } else {
//            if (userId.equals(mLoginUserId)) {
//                // 是我发的消息
//                viewHolder.delete_tv.setVisibility(View.VISIBLE);
//                viewHolder.delete_tv.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (UiUtils.isNormalClick(v)) {
//                            showDeleteMsgDialog(finalPosition);
//                        }
//                    }
//                });
//            } else {
//                viewHolder.delete_tv.setVisibility(View.GONE);
//                viewHolder.delete_tv.setOnClickListener(null);
//            }
//        }
//
//        final DynamicListAdapter.ViewHolder vh = viewHolder;
//        vh.ivThumb.setChecked(1 == message.getDynamic().getLikes().getIs_like());
//        vh.tvThumb.setText(String.valueOf(message.getDynamic().getLikes().getCounts()));
//        vh.llThumb.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(UiUtils.isNormalClick(v)) {
//                    // 是否是点过赞，
//                    final boolean isPraise = vh.ivThumb.isChecked();
//                    // 调接口，旧代码保留，传的是相反的状态，
//                    onPraise(finalPosition, !isPraise);
//                    // 更新赞数，调接口完成还会刷新，
//                    int praiseCount = message.getDynamic().getLikes().getCounts();
//                    if (isPraise) {
//                        praiseCount--;
//                    } else {
//                        praiseCount++;
//                    }
//                    vh.tvThumb.setText(String.valueOf(praiseCount));
//                    vh.ivThumb.toggle();
//                }
//            }
//        });
//        // 是否点评论过，
//        // TODO: 不准了，评论分页加载，
//        boolean isComment = false;
//        if (message.getDynamic().getComments() != null) {
//            for (DynamicBean.CommentsBean.ListBean comment : message.getDynamic().getComments().getList()) {
//                if (mLoginUserId.equals(comment.getUserid())) {
//                    isComment = true;
//                }
//            }
//        }
//        vh.ivComment.setChecked(isComment);
//        vh.tvComment.setText(String.valueOf(message.getDynamic().getComments().getCounts()));
//        vh.llComment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 调接口，旧代码保留，
//                onComment(finalPosition, vh.command_listView);
//                // 评论数在调接口完成后还会刷新，
//            }
//        });
//        vh.ivCollection.setChecked(1 == message.getDynamic().getCollects().getIs_collect());
//        vh.tvCollection.setText(String.valueOf(message.getDynamic().getCollects().getCounts()));
//        vh.llCollection.setOnClickListener(v -> {
//            onCollection(finalPosition);
//        });
//        vh.llReport.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onReport(finalPosition,v);
//            }
//        });
//
//        /* 显示多少人赞过 */
//        List<DynamicBean.LikesBean.ListBeanX> praises = message.getDynamic().getLikes().getList();
//        if (praises != null && praises.size() > 0) {
//            viewHolder.multi_praise_tv.setVisibility(View.VISIBLE);
//            viewHolder.iv_prise.setVisibility(View.VISIBLE);
//            SpannableStringBuilder builder = new SpannableStringBuilder();
//            for (int i = 0; i < praises.size(); i++) {
//
//                DynamicBean.LikesBean.ListBeanX praise = praises.get(i);
//                UserInfoBean userInfo = message.getUserInfo(praise.getUserid());
//                String praiseName = getShowName(userInfo.getOort_uuid(), userInfo.getOort_name());
//                UserClickableSpan.setClickableSpan(mContext, builder, praiseName, userInfo.getOort_uuid());
//                if (i < praises.size() - 1)
//                    builder.append(",");
//            }
//            if (message.getDynamic().getLikes().getCounts() > praises.size()) {
//                builder.append(mContext.getString(R.string.praise_ending_place_holder, message.getDynamic().getLikes().getCounts()));
//            }
//            viewHolder.multi_praise_tv.setText(builder);
//        } else {
//            viewHolder.iv_prise.setVisibility(View.GONE);
//            viewHolder.multi_praise_tv.setVisibility(View.GONE);
//            viewHolder.multi_praise_tv.setText("");
//        }
//        viewHolder.multi_praise_tv.setLinksClickable(true);
//        viewHolder.multi_praise_tv.setMovementMethod(LinkMovementClickMethod.getInstance());
//        viewHolder.multi_praise_tv.setOnClickListener(v -> {
//            //PraiseListActivity.start(mContext, message.getMessageId());
//        });
//
//        /* 设置回复 */
//        final List<DynamicBean.CommentsBean.ListBean> comments = message.getDynamic().getComments().getList();
//        viewHolder.command_listView.setVisibility(View.VISIBLE);
//        DynamicListAdapter.CommentAdapter adapter = new DynamicListAdapter.CommentAdapter(position, comments,message);
//        viewHolder.command_listView.setAdapter(adapter);
//        viewHolder.tvLoadMore.setVisibility(View.GONE);
//        if (comments != null && comments.size() > 0) {
//            if (message.getDynamic().getCollects().getCounts() > comments.size()) {
////                // 需要分页加载，
////                viewHolder.tvLoadMore.setVisibility(View.VISIBLE);
////                viewHolder.tvLoadMore.setOnClickListener(v -> {
////                    loadCommentsNextPage(vh.tvLoadMore, message.getDynamic().get, adapter);
////                });
//            }
//        }
//
//        // 赞与评论之间的横线，两者都有才显示
//        if (praises != null && praises.size() > 0 && comments != null && comments.size() > 0) {
//            viewHolder.line_v.setVisibility(View.VISIBLE);
//        } else {
//            viewHolder.line_v.setVisibility(View.INVISIBLE);
//        }
//
///*
//        mAdapter = (CommentAdapter) viewHolder.command_listView.getAdapter();
//        if (mAdapter == null) {
//            mAdapter = new CommentAdapter();
//            viewHolder.command_listView.setAdapter(mAdapter);
//        }
//
//        if (comments != null && comments.size() > 0) {
//            viewHolder.line_v.setVisibility(View.VISIBLE);
//            viewHolder.command_listView.setVisibility(View.VISIBLE);
//        } else {
//            viewHolder.line_v.setVisibility(View.GONE);
//            viewHolder.command_listView.setVisibility(View.GONE);
//        }
//        mAdapter.setData(position, comments);
//*/
//
////        if (!TextUtils.isEmpty(message.getLocation())) {
////            viewHolder.location_tv.setText(message.getLocation());
////            viewHolder.location_tv.setVisibility(View.VISIBLE);
////        } else {
////            viewHolder.location_tv.setVisibility(View.GONE);
////        }
//
//        viewHolder.location_tv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Intent intent = new Intent(mContext, MapActivity.class);
////                intent.putExtra("latitude", message.getLatitude());
////                intent.putExtra("longitude", message.getLongitude());
////                intent.putExtra("userName", message.getLocation());
////                mContext.startActivity(intent);
//            }
//        });
//
//        // //////////////////上面是公用的部分，下面是每个Type不同的部分/////////////////////////////////////////
//        // 转载的消息会有一个转载人和text
//        SpannableStringBuilder forwardingBuilder = null;
////        if (isForwarding) {// 转载的那个人和说的话
////            forwardingBuilder = new SpannableStringBuilder();
////            String forwardName = getShowName(message.getFowardUserId(), message.getFowardNickname());
////            UserClickableSpan.setClickableSpan(mContext, forwardingBuilder, forwardName, message.getFowardUserId());
////            if (!TextUtils.isEmpty(message.getFowardText())) {
////                forwardingBuilder.append(" : ");
////                forwardingBuilder.append(message.getFowardText());
////            }
////        }
//        if (viewType == VIEW_TYPE_NORMAL_TEXT) {
//            viewHolder.content_fl.setVisibility(View.GONE);
//        } else if (viewType == VIEW_TYPE_NORMAL_SINGLE_IMAGE) {
//            ImageView image_view = ((DynamicListAdapter.NormalSingleImageHolder) viewHolder).image_view;
//            ImageView icon_play = ((DynamicListAdapter.NormalSingleImageHolder) viewHolder).icon_play;
//            DynamicBean.AttachBean att = message.getDynamic().getAttach_images().get(0);
//
//            String url = message.getDynamic().getAttach().get(0).getUrl();
//
//            icon_play.setVisibility(View.GONE);
//            if(att.getType().equals("video")){
//                url = att.getThumb();
//                icon_play.setVisibility(View.VISIBLE);
//
//            }
//
//            if (!TextUtils.isEmpty(url)) {
//                if (url.endsWith(".gif")) {
//                    ImageLoadHelper.showGifWithPlaceHolder(
//                            mContext,
//                            url,
//                            R.drawable.default_gray,
//                            R.drawable.image_download_fail_icon,
//                            image_view
//                    );
//                } else {
//                    ImageLoadHelper.showImageCenterCrop(
//                            mContext,
//                            url,
//                            R.drawable.default_gray,
//                            R.drawable.image_download_fail_icon,
//                            image_view
//                    );
//                }
//                image_view.setOnClickListener(new DynamicListAdapter.SingleImageClickListener(att));
//                image_view.setVisibility(View.VISIBLE);
//            } else {
//                image_view.setImageBitmap(null);
//                image_view.setVisibility(View.GONE);
//            }
//        } else if (viewType == VIEW_TYPE_NORMAL_MULTI_IMAGE) {
//            MyGridView grid_view = ((DynamicListAdapter.NormalMultiImageHolder) viewHolder).grid_view;
//            if (body.getAttach() != null) {
//                grid_view.setAdapter(new DynamicImageGridViewAdapter(mContext, body.getAttach_images()));
//                grid_view.setOnItemClickListener(new DynamicListAdapter.MultipleImagesClickListener(body.getAttach_images()));
//            } else {
//                grid_view.setAdapter(null);
//            }
//        } else if (viewType == VIEW_TYPE_NORMAL_VOICE) {
//            // 相关代码相当混乱且有大量废弃代码，
//            // 修改语音消息外观时尽少修改代码，
////            final NormalVoiceHolder holder = (NormalVoiceHolder) viewHolder;
////            holder.chat_to_voice.fillData(message);
////            holder.chat_to_voice.setOnClickListener(v -> {
////                VoicePlayer.instance().playVoice(holder.chat_to_voice);
////            });
//        } else if (viewType == VIEW_TYPE_NORMAL_VIDEO) {
//            DynamicListAdapter.NormalVideoHolder holder = (DynamicListAdapter.NormalVideoHolder) viewHolder;
////            String imageUrl = message.getFirstImageOriginal();
////            // 判断是自己处理就直接使用本地文件，
////            String videoUrl = UploadCacheUtils.get(mContext, message.getFirstVideo());
////            if (!TextUtils.isEmpty(videoUrl)) {
////                if (videoUrl.equals(message.getFirstVideo())) {
////                    // 如果不是自己上传的，就使用视频缓存库统一缓存，
////                    videoUrl = MyApplication.getProxy(mContext).getProxyUrl(message.getFirstVideo());
////                }
////                holder.gridViewVideoPlayer.setUp(videoUrl,
////                        JVCideoPlayerStandardSecond.SCREEN_LAYOUT_NORMAL, "");
////            }
////            if (TextUtils.isEmpty(imageUrl)) {
////                AvatarHelper.getInstance().asyncDisplayOnlineVideoThumb(videoUrl, holder.gridViewVideoPlayer.thumbImageView);
////            } else {
////                ImageLoadHelper.showImageWithPlaceHolder(
////                        mContext,
////                        imageUrl,
////                        R.drawable.default_gray,
////                        R.drawable.default_gray,
////                        holder.gridViewVideoPlayer.thumbImageView
////                );
////            }
//        } else if (viewType == VIEW_TYPE_NORMAL_FILE) {
//            // 文件
////            NormalFileHolder holder = (NormalFileHolder) viewHolder;
////            final String mFileUrl = message.getFirstFile();
////
////            if (TextUtils.isEmpty(mFileUrl)) {
////                return;
////            }
////            // 朋友圈的接口数据没有fileName,
////            if (!TextUtils.isEmpty(message.getFileName())) {
////                holder.text_tv.setText(mContext.getString(R.string.msg_file) + message.getFileName());
////            } else {
////                try {
////                    message.setFileName(mFileUrl.substring(mFileUrl.lastIndexOf('/') + 1));
////                    holder.text_tv.setText(mContext.getString(R.string.msg_file) + message.getFileName());
////                } catch (Exception ignored) {
////                    // 万一url有问题，没有斜杠/直接抛异常下来显示url,
////                    holder.text_tv.setText(mContext.getString(R.string.msg_file) + mFileUrl);
////                }
////            }
////
////            String suffix = "";
////            int index = mFileUrl.lastIndexOf(".");
////            if (index != -1) {
////                suffix = mFileUrl.substring(index + 1).toLowerCase();
////                if (suffix.equals("png") || suffix.equals("jpg")) {
////                    ImageLoadHelper.showImageWithSize(
////                            mContext,
////                            mFileUrl,
////                            100, 100,
////                            holder.file_image
////                    );
////                } else {
////                    AvatarHelper.getInstance().fillFileView(suffix, holder.file_image);
////                }
////            }
////
////            final long size = message.getBody().getFiles().get(0).getSize();
////            Log.e("xuan", "setOnClickListener: " + size);
////
////            holder.file_click.setOnClickListener(v -> intentPreviewFile(mFileUrl, message.getFileName(), message.getNickName(), size));
//        } else if (viewType == VIEW_TYPE_NORMAL_LINK) {
////            NormalLinkHolder holder = (NormalLinkHolder) viewHolder;
////            if (TextUtils.isEmpty(message.getBody().getSdkIcon())) {
////                holder.link_image.setImageResource(R.drawable.browser);
////            } else {
////                AvatarHelper.getInstance().displayUrl(message.getBody().getSdkIcon(), holder.link_image);
////            }
////            holder.link_tv.setText(message.getBody().getSdkTitle());
////
////            holder.link_click.setOnClickListener(v -> {
////                Intent intent = new Intent(mContext, WebViewActivity.class);
////                intent.putExtra(WebViewActivity.EXTRA_URL, message.getBody().getSdkUrl());
////                mContext.startActivity(intent);
////            });
//        }
//    }

    public static void start(Context context,String tid) {
        Intent starter = new Intent(context, DynamicActivityDynamicInfo.class);
        starter.putExtra("tid",tid);
        context.startActivity(starter);
    }
}