package com.oort.weichat.fragment.dynamic;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.InputFilter;
import android.text.Layout;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.google.gson.reflect.TypeToken;
import com.oort.weichat.AppConstant;
import com.oort.weichat.R;
import com.oort.weichat.Reporter;
import com.oort.weichat.adapter.UserClickableSpan;
import com.oort.weichat.audio.AudioPalyer;
import com.oort.weichat.audio_x.VoiceAnimView;
import com.oort.weichat.audio_x.VoicePlayer;
import com.oort.weichat.bean.Friend;
import com.oort.weichat.bean.circle.Comment;
import com.oort.weichat.bean.circle.PublicMessage;
import com.oort.weichat.db.dao.FriendDao;
import com.oort.weichat.fragment.entity.DynamicBean;
import com.oort.weichat.fragment.entity.OORTDynamic;
import com.oort.weichat.fragment.entity.OORTDynamicReview;
import com.oort.weichat.fragment.entity.Res;
import com.oort.weichat.fragment.entity.ResObj;
import com.oort.weichat.fragment.entity.UserInfoBean;
import com.oort.weichat.helper.AvatarHelper;
import com.oort.weichat.helper.ImageLoadHelper;
import com.oort.weichat.ui.MainActivity;
import com.oort.weichat.ui.base.CoreManager;
import com.oort.weichat.ui.circle.BusinessCircleActivity;
import com.oort.weichat.ui.circle.LongTextShowActivity;
import com.oort.weichat.ui.me.MyCollection;
import com.oort.weichat.ui.mucfile.DownManager;
import com.oort.weichat.ui.mucfile.MucFileDetails;
import com.oort.weichat.ui.mucfile.XfileUtils;
import com.oort.weichat.ui.mucfile.bean.MucFileBean;
import com.oort.weichat.ui.tool.MultiImagePreviewActivity;
import com.oort.weichat.ui.tool.SingleImagePreviewActivity;
import com.oort.weichat.util.HtmlUtils;
import com.oort.weichat.util.LinkMovementClickMethod;
import com.oort.weichat.util.StringUtils;
import com.oort.weichat.util.SystemUtil;
import com.oort.weichat.util.TimeUtils;
import com.oort.weichat.util.ToastUtil;
import com.oort.weichat.util.UiUtils;
import com.oort.weichat.util.filter.EmojiInputFilter;
import com.oort.weichat.util.link.HttpTextView;
import com.oort.weichat.view.CheckableImageView;
import com.oort.weichat.view.MyGridView;
import com.oort.weichat.view.SelectionFrame;
import com.oort.weichat.view.TrillCommentInputDialog;
import com.oortcloud.appstore.http.HttpRequestParam;
import com.oortcloud.basemodule.im.IMUserInfoUtil;
import com.oortcloud.basemodule.user.UserInfoUtils;
import com.oortcloud.basemodule.utils.fastsharedpreferences.FastSharedPreferences;
import com.oortcloud.login.net.utils.RxBus;
import com.xuan.xuanhttplibrary.okhttp.HttpUtils;
import com.xuan.xuanhttplibrary.okhttp.callback.ListCallback;
import com.xuan.xuanhttplibrary.okhttp.result.ArrayResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JVCideoPlayerStandardSecond;
import okhttp3.Call;

/**
 * 1.我的空间
 * 2.我的收藏 adapter
 */
public class DynamicReviewListAdapter extends RecyclerView.Adapter<DynamicReviewListAdapter.ViewHolder> implements BusinessCircleActivity.ListenerAudio {
    private static final int VIEW_TYPE_NORMAL_TEXT = 0;
    private static final int VIEW_TYPE_NORMAL_SINGLE_IMAGE = 2;
    private static final int VIEW_TYPE_NORMAL_MULTI_IMAGE = 4;
    private static final int VIEW_TYPE_NORMAL_VOICE = 6;
    private static final int VIEW_TYPE_NORMAL_VIDEO = 8;
    private static final int VIEW_TYPE_NORMAL_FILE = 10;
    // 分享的链接
    private static final int VIEW_TYPE_NORMAL_LINK = 11;
    private Context mContext;
    private CoreManager coreManager;
    private List<OORTDynamicReview> mMessages;
    private LayoutInflater mInflater;
    private String mLoginUserId;
    private String mLoginNickName;
    private ViewHolder mVoicePlayViewHolder;
    private AudioPalyer mAudioPalyer;
    private String mVoicePlayId = null;
    private Map<String, Boolean> mClickOpenMaps = new HashMap<>();
    private int collectionType;
    private OnItemClickListener onItemClickListener = null;
    /**
     * 缓存getShowName，
     * 每次约两三毫秒，
     */
    private WeakHashMap<String, String> showNameCache = new WeakHashMap<>();
    private CommentReplyCache mCommentReplyCache;

    public DynamicReviewListAdapter(Context context, CoreManager coreManager, List<OORTDynamicReview> messages) {
        setHasStableIds(true);
        mContext = context;
        this.coreManager = coreManager;
        mMessages = messages;
        setHasStableIds(true);
        mInflater = LayoutInflater.from(mContext);
        mLoginUserId = UserInfoUtils.getInstance(mContext).getUserId();// coreManager.getSelf().getUserId();
        mLoginNickName = coreManager.getSelf().getNickName();
        mAudioPalyer = new AudioPalyer();
        mAudioPalyer.setAudioPlayListener(new AudioPalyer.AudioPlayListener() {
            @Override
            public void onSeekComplete() {
            }

            @Override
            public void onPrepared() {
            }

            @Override
            public void onError() {
                mVoicePlayId = null;
                if (mVoicePlayViewHolder != null) {
                    updateVoiceViewHolderIconStatus(false, mVoicePlayViewHolder);
                }
                mVoicePlayViewHolder = null;
            }

            @Override
            public void onCompletion() {
                mVoicePlayId = null;
                if (mVoicePlayViewHolder != null) {
                    updateVoiceViewHolderIconStatus(false, mVoicePlayViewHolder);
                }
                mVoicePlayViewHolder = null;
            }

            @Override
            public void onBufferingUpdate(int percent) {
            }

            @Override
            public void onPreparing() {
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public long getItemId(int position) {
        return mMessages.get(position).hashCode();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View convertView = mInflater.inflate(R.layout.p_msg_item_main_body_review, viewGroup, false);
        View innerView = null;
        ViewHolder viewHolder;
        if (viewType == VIEW_TYPE_NORMAL_TEXT) {
            viewHolder = new NormalTextHolder(convertView);
        } else if (viewType == VIEW_TYPE_NORMAL_SINGLE_IMAGE) {
            NormalSingleImageHolder holder = new NormalSingleImageHolder(convertView);
            innerView = mInflater.inflate(R.layout.p_msg_item_normal_single_img, holder.content_fl, false);
            holder.image_view = (ImageView) innerView.findViewById(R.id.image_view);
            viewHolder = holder;
        } else if (viewType == VIEW_TYPE_NORMAL_MULTI_IMAGE) {
            NormalMultiImageHolder holder = new NormalMultiImageHolder(convertView);
            innerView = mInflater.inflate(R.layout.p_msg_item_normal_multi_img, holder.content_fl, false);
            holder.grid_view = (MyGridView) innerView.findViewById(R.id.grid_view);
            viewHolder = holder;
        } else if (viewType == VIEW_TYPE_NORMAL_VOICE) {
            NormalVoiceHolder holder = new NormalVoiceHolder(convertView);
            innerView = mInflater.inflate(R.layout.p_msg_item_normal_voice, holder.content_fl, false);
            holder.img_view = (ImageView) innerView.findViewById(R.id.img_view);
            holder.voice_action_img = (ImageView) innerView.findViewById(R.id.voice_action_img);
            holder.voice_desc_tv = (TextView) innerView.findViewById(R.id.voice_desc_tv);
            holder.chat_to_voice = (VoiceAnimView) innerView.findViewById(R.id.chat_to_voice);
            viewHolder = holder;
        } else if (viewType == VIEW_TYPE_NORMAL_VIDEO) {
            NormalVideoHolder holder = new NormalVideoHolder(convertView);
            innerView = mInflater.inflate(R.layout.p_msg_item_normal_video, holder.content_fl, false);
            holder.gridViewVideoPlayer = (JVCideoPlayerStandardSecond) innerView.findViewById(R.id.preview_video);
            viewHolder = holder;
        } else if (viewType == VIEW_TYPE_NORMAL_FILE) {
            NormalFileHolder holder = new NormalFileHolder(convertView);
            innerView = mInflater.inflate(R.layout.p_msg_item_normal_file, holder.content_fl, false);
            holder.file_click = (RelativeLayout) innerView.findViewById(R.id.collection_file);
            holder.file_image = (ImageView) innerView.findViewById(R.id.file_img);
            holder.text_tv = (TextView) innerView.findViewById(R.id.file_name);
            viewHolder = holder;
        } else if (viewType == VIEW_TYPE_NORMAL_LINK) {
            NormalLinkHolder holder = new NormalLinkHolder(convertView);
            innerView = mInflater.inflate(R.layout.p_msg_item_normal_link, holder.content_fl, false);
            holder.link_click = (LinearLayout) innerView.findViewById(R.id.link_ll);
            holder.link_image = (ImageView) innerView.findViewById(R.id.link_iv);
            holder.link_tv = (TextView) innerView.findViewById(R.id.link_text_tv);
            viewHolder = holder;
        } else {
            viewHolder = new NormalTextHolder(convertView);
           // throw new IllegalStateException("unkown viewType: " + viewType);
        }

        if (collectionType == 1 || collectionType == 2) {
            // 当前适配器用于我的收藏列表，隐藏评论 && 赞功能
            //viewHolder.llOperator.setVisibility(View.GONE);
        } else {
           // viewHolder.llOperator.setVisibility(View.VISIBLE);
        }
        viewHolder.iv_prise = convertView.findViewById(R.id.iv_prise);
        viewHolder.multi_praise_tv = (TextView) convertView.findViewById(R.id.multi_praise_tv);
        viewHolder.tvLoadMore = (TextView) convertView.findViewById(R.id.tvLoadMore);
        viewHolder.line_v = convertView.findViewById(R.id.line_v);
        viewHolder.command_listView = (ListView) convertView.findViewById(R.id.command_listView);
        viewHolder.location_tv = (TextView) convertView.findViewById(R.id.location_tv);
        if (innerView != null) {
            viewHolder.content_fl.addView(innerView);
        }
        viewHolder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(viewHolder);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
        int viewType = getItemViewType(position);
        // 和ViewHolder一样的，只不过用作匿名内部类里面调用需要final
        final ViewHolder finalHolder = viewHolder;
        // set data
        final OORTDynamicReview message = mMessages.get(position);
        if (message == null) {
            return;
        }


        UserInfoBean userInfoBean = message.getUserInfo(message.getOort_userid());

        UserInfoBean userInfoBean01 = message.getUserInfo(message.getDynamic().getOort_userid());
        /* 设置头像 */
        AvatarHelper.getInstance().displayAvatar(userInfoBean.getImuserid(), viewHolder.avatar_img);
        /* 设置昵称 */
        SpannableStringBuilder nickNamebuilder = new SpannableStringBuilder();
        final String userId = userInfoBean.getOort_uuid();
        String showName = getShowName(userId,userInfoBean.getOort_name());
        UserClickableSpan.setClickableSpan(mContext, nickNamebuilder, showName,  userInfoBean.getOort_uuid());
        viewHolder.nick_name_tv.setText(nickNamebuilder);
        viewHolder.nick_name_tv.setLinksClickable(true);
        viewHolder.nick_name_tv.setMovementMethod(LinkMovementClickMethod.getInstance());
        viewHolder.tv_dept.setText(userInfoBean.getOort_depname());

        if(message.getType().equals("like")){
            viewHolder.tv_oper_flag.setText(mContext.getString(R.string.like_moments));
            viewHolder.tv_comment_content.setVisibility(View.GONE);
        }
        if(message.getType().equals("comment")){
            viewHolder.tv_oper_flag.setText(mContext.getString(R.string.comment_moments));
            viewHolder.tv_comment_content.setVisibility(View.VISIBLE);


            String content = message.getContent().replace("\n\n","\n");
            viewHolder.tv_comment_content.setText(content);
        }
        if(message.getType().equals("collect")){
            viewHolder.tv_oper_flag.setText(mContext.getString(R.string.collect_moments));
            viewHolder.tv_comment_content.setVisibility(View.GONE);
        }

        // 设置头像的点击事件
        viewHolder.avatar_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!UiUtils.isNormalClick(v)) {
                    return;
                }
                //BasicInfoActivity.start(mContext, userInfoBean.getImuserid());

                DynamicActivityUserHome.start(mContext,userInfoBean.getOort_uuid());
            }
        });

        // 获取消息本身的内容
        DynamicBean body = message.getDynamic();
        if (body == null) {
            return;
        }

        // 是否是转载的
        //boolean isForwarding = message.getSource() == PublicMessage.SOURCE_FORWARDING;

        // 设置body_tv
        // todo 注释掉的代码为 展开/全文的方式显示文本，文本长度过长时会卡顿，先隐藏这种方式，换种方式(最多显示6行，操作的跳转显示)
        if (TextUtils.isEmpty(body.getContent())) {
            viewHolder.body_tv.setVisibility(View.GONE);
        } else {
            // 支持emoji显示
            viewHolder.body_tv.setFilters(new InputFilter[]{new EmojiInputFilter(mContext)});


            String content = body.getContent().replace("\n\n","\n");
            SpannableString spannableString = new SpannableString(    "@" + userInfoBean01.getOort_name() + ":" + content);

            ForegroundColorSpan foregroundColorSpan = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                foregroundColorSpan = new ForegroundColorSpan(mContext.getColor(R.color.main_color));
                spannableString.setSpan(foregroundColorSpan, 0, userInfoBean01.getOort_name().length() + 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            }


            viewHolder.body_tv.setUrlText(spannableString);
            viewHolder.body_tv.setVisibility(View.VISIBLE);
        }
        // 判断是否超出6行限制，超过则显示"全文"
        viewHolder.body_tv.post(() -> {
            Layout layout = viewHolder.body_tv.getLayout();
            if (layout != null) {
                int lines = layout.getLineCount();
                // setText换成setUrlText之后，layout.getEllipsisCount有点问题，换一个判断方法
/*
                if (lines > 0) {
                    if (layout.getEllipsisCount(lines - 1) > 0) {
                        viewHolder.open_tv.setVisibility(View.VISIBLE);
                        viewHolder.open_tv.setOnClickListener(v -> LongTextShowActivity.start(mContext, body.getText()));
                    } else {
                        viewHolder.open_tv.setVisibility(View.GONE);
                        viewHolder.open_tv.setOnClickListener(null);
                    }
                }
*/
                if (lines > 6) {
                    viewHolder.open_tv.setVisibility(View.VISIBLE);
                    viewHolder.open_tv.setOnClickListener(v -> LongTextShowActivity.start(mContext, body.getContent()));
                } else {
                    viewHolder.open_tv.setVisibility(View.GONE);
                    viewHolder.open_tv.setOnClickListener(null);
                }
            }
        });

        viewHolder.body_tv.setOnLongClickListener(v -> {
            showBodyTextLongClickDialog(body.getContent());
            return false;
        });

        viewHolder.body_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DynamicActivityDynamicInfo.start(mContext,message.getDynamic().getOort_duuid());
            }
        });

        // 设置发布时间 MyCollection
        viewHolder.time_tv.setText(TimeUtils.getFriendlyTimeDesc(mContext, (int) message.getDynamic().getCreated_at()));
        if (MyCollection.class.toString().contains(mContext.getClass().toString())) {
            // 设置取消收藏按钮
            viewHolder.delete_tv.setText(mContext.getString(R.string.cancel_collection));
            viewHolder.llReport.setVisibility(View.GONE);
        } else {
            viewHolder.llReport.setVisibility(View.VISIBLE);
            viewHolder.delete_tv.setText(mContext.getString(R.string.delete));
        }
        if (collectionType == 1) {
            viewHolder.delete_tv.setVisibility(View.VISIBLE);
            viewHolder.delete_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(UiUtils.isNormalClick(v)) {
                        showDeleteMsgDialog(position);
                    }
                }
            });
        } else if (collectionType == 2) {
            viewHolder.delete_tv.setVisibility(View.GONE);
        } else {
            if (userId.equals(mLoginUserId)) {
                // 是我发的消息
                viewHolder.delete_tv.setVisibility(View.VISIBLE);
                viewHolder.delete_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (UiUtils.isNormalClick(v)) {
                            showDeleteMsgDialog(position);
                        }
                    }
                });
            } else {
                viewHolder.delete_tv.setVisibility(View.GONE);
                viewHolder.delete_tv.setOnClickListener(null);
            }
        }
        viewHolder.delete_tv.setVisibility(View.GONE);
        final ViewHolder vh = viewHolder;


        // //////////////////上面是公用的部分，下面是每个Type不同的部分/////////////////////////////////////////
        // 转载的消息会有一个转载人和text
        SpannableStringBuilder forwardingBuilder = null;
//        if (isForwarding) {// 转载的那个人和说的话
//            forwardingBuilder = new SpannableStringBuilder();
//            String forwardName = getShowName(message.getFowardUserId(), message.getFowardNickname());
//            UserClickableSpan.setClickableSpan(mContext, forwardingBuilder, forwardName, message.getFowardUserId());
//            if (!TextUtils.isEmpty(message.getFowardText())) {
//                forwardingBuilder.append(" : ");
//                forwardingBuilder.append(message.getFowardText());
//            }
//        }
        if (viewType == VIEW_TYPE_NORMAL_TEXT) {
            viewHolder.content_fl.setVisibility(View.GONE);
        } else if (viewType == VIEW_TYPE_NORMAL_SINGLE_IMAGE) {
            ImageView image_view = ((NormalSingleImageHolder) viewHolder).image_view;
            String url = message.getDynamic().getAttach().get(0).getUrl();
            if (!TextUtils.isEmpty(url)) {
                if (url.endsWith(".gif")) {
                    ImageLoadHelper.showGifWithPlaceHolder(
                            mContext,
                            url,
                            R.drawable.default_gray,
                            R.drawable.image_download_fail_icon,
                            image_view
                    );
                } else {
                    ImageLoadHelper.showImageCenterCrop(
                            mContext,
                            url,
                            R.drawable.default_gray,
                            R.drawable.image_download_fail_icon,
                            image_view
                    );
                }
                image_view.setOnClickListener(new SingleImageClickListener(url));
                image_view.setVisibility(View.VISIBLE);
            } else {
                image_view.setImageBitmap(null);
                image_view.setVisibility(View.GONE);
            }
        } else if (viewType == VIEW_TYPE_NORMAL_MULTI_IMAGE) {
            MyGridView grid_view = ((NormalMultiImageHolder) viewHolder).grid_view;
            if (body.getAttach() != null) {
                grid_view.setAdapter(new DynamicImageGridViewAdapter(mContext, body.getAttach()));
                grid_view.setOnItemClickListener(new MultipleImagesClickListener(body.getAttach()));
            } else {
                grid_view.setAdapter(null);
            }
        } else if (viewType == VIEW_TYPE_NORMAL_VOICE) {
            // 相关代码相当混乱且有大量废弃代码，
            // 修改语音消息外观时尽少修改代码，
//            final NormalVoiceHolder holder = (NormalVoiceHolder) viewHolder;
//            holder.chat_to_voice.fillData(message);
//            holder.chat_to_voice.setOnClickListener(v -> {
//                VoicePlayer.instance().playVoice(holder.chat_to_voice);
//            });
        } else if (viewType == VIEW_TYPE_NORMAL_VIDEO) {
//            NormalVideoHolder holder = (NormalVideoHolder) viewHolder;
//            String imageUrl = message.getFirstImageOriginal();
//            // 判断是自己处理就直接使用本地文件，
//            String videoUrl = UploadCacheUtils.get(mContext, message.getFirstVideo());
//            if (!TextUtils.isEmpty(videoUrl)) {
//                if (videoUrl.equals(message.getFirstVideo())) {
//                    // 如果不是自己上传的，就使用视频缓存库统一缓存，
//                    videoUrl = MyApplication.getProxy(mContext).getProxyUrl(message.getFirstVideo());
//                }
//                holder.gridViewVideoPlayer.setUp(videoUrl,
//                        JVCideoPlayerStandardSecond.SCREEN_LAYOUT_NORMAL, "");
//            }
//            if (TextUtils.isEmpty(imageUrl)) {
//                AvatarHelper.getInstance().asyncDisplayOnlineVideoThumb(videoUrl, holder.gridViewVideoPlayer.thumbImageView);
//            } else {
//                ImageLoadHelper.showImageWithPlaceHolder(
//                        mContext,
//                        imageUrl,
//                        R.drawable.default_gray,
//                        R.drawable.default_gray,
//                        holder.gridViewVideoPlayer.thumbImageView
//                );
//            }
        } else if (viewType == VIEW_TYPE_NORMAL_FILE) {
            // 文件
//            NormalFileHolder holder = (NormalFileHolder) viewHolder;
//            final String mFileUrl = message.getFirstFile();
//
//            if (TextUtils.isEmpty(mFileUrl)) {
//                return;
//            }
//            // 朋友圈的接口数据没有fileName,
//            if (!TextUtils.isEmpty(message.getFileName())) {
//                holder.text_tv.setText(mContext.getString(R.string.msg_file) + message.getFileName());
//            } else {
//                try {
//                    message.setFileName(mFileUrl.substring(mFileUrl.lastIndexOf('/') + 1));
//                    holder.text_tv.setText(mContext.getString(R.string.msg_file) + message.getFileName());
//                } catch (Exception ignored) {
//                    // 万一url有问题，没有斜杠/直接抛异常下来显示url,
//                    holder.text_tv.setText(mContext.getString(R.string.msg_file) + mFileUrl);
//                }
//            }
//
//            String suffix = "";
//            int index = mFileUrl.lastIndexOf(".");
//            if (index != -1) {
//                suffix = mFileUrl.substring(index + 1).toLowerCase();
//                if (suffix.equals("png") || suffix.equals("jpg")) {
//                    ImageLoadHelper.showImageWithSize(
//                            mContext,
//                            mFileUrl,
//                            100, 100,
//                            holder.file_image
//                    );
//                } else {
//                    AvatarHelper.getInstance().fillFileView(suffix, holder.file_image);
//                }
//            }
//
//            final long size = message.getBody().getFiles().get(0).getSize();
//            Log.e("xuan", "setOnClickListener: " + size);
//
//            holder.file_click.setOnClickListener(v -> intentPreviewFile(mFileUrl, message.getFileName(), message.getNickName(), size));
        } else if (viewType == VIEW_TYPE_NORMAL_LINK) {
//            NormalLinkHolder holder = (NormalLinkHolder) viewHolder;
//            if (TextUtils.isEmpty(message.getBody().getSdkIcon())) {
//                holder.link_image.setImageResource(R.drawable.browser);
//            } else {
//                AvatarHelper.getInstance().displayUrl(message.getBody().getSdkIcon(), holder.link_image);
//            }
//            holder.link_tv.setText(message.getBody().getSdkTitle());
//
//            holder.link_click.setOnClickListener(v -> {
//                Intent intent = new Intent(mContext, WebViewActivity.class);
//                intent.putExtra(WebViewActivity.EXTRA_URL, message.getBody().getSdkUrl());
//                mContext.startActivity(intent);
//            });
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DynamicActivityDynamicInfo.start(mContext,message.getDynamic().getOort_duuid());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    /**
     * @see PublicMessage#getType() <br/>
     * 1=文字消息；2=图文消息；3=语音消息； 4=视频消息；5、转载<br/>
     * 分的视图类型有： <br/>
     * {@link #VIEW_TYPE_NORMAL_TEXT}0、普通文字消息视图<br/>
     * {@link #VIEW_TYPE_NORMAL_SINGLE_IMAGE} 2、普通单张图片的视图<br/>
     * {@link #VIEW_TYPE_NORMAL_MULTI_IMAGE}4、普通多张图片的视图<br/>
     * {@link #VIEW_TYPE_NORMAL_VOICE} 6、普通音频视图<br/>
     * {@link #VIEW_TYPE_NORMAL_VIDEO}8、普通视频视图<br/>
     */

    @Override
    public int getItemViewType(int position) {
        OORTDynamicReview message = mMessages.get(position);
        // boolean fromSelf = message.getSource() == PublicMessage.SOURCE_SELF;
        if (message == null || message.getDynamic() == null) {
            // 如果为空，那么可能是数据错误，直接返回一个普通的文本视图
            return VIEW_TYPE_NORMAL_TEXT;
        }
        DynamicBean body = message.getDynamic();
//        if (message.getIsAllowComment() == 1) {
//            message.setIsAllowComment(1);
//        } else {
//            message.setIsAllowComment(0);
//        }
        if (body.getAttach() == null || body.getAttach().size() == 0) {
            // 文本视图
            return VIEW_TYPE_NORMAL_TEXT;
        } else if (body.getAttach().size() > 0) {
            if (body.getAttach().size() <= 1) {
                // 普通的单张图片的视图
                return VIEW_TYPE_NORMAL_SINGLE_IMAGE;
            } else {// 普通的多张图片视图
                return VIEW_TYPE_NORMAL_MULTI_IMAGE;
            }
        }
//        else if (body.getType() == PublicMessage.TYPE_VOICE) {// 普通音频
//            return VIEW_TYPE_NORMAL_VOICE;
//        } else if (body.getType() == PublicMessage.TYPE_VIDEO) {// 普通视频
//            return VIEW_TYPE_NORMAL_VIDEO;
//        } else if (body.getType() == PublicMessage.TYPE_FILE) {
//            // 文件
//            return VIEW_TYPE_NORMAL_FILE;
//        } else if (body.getType() == PublicMessage.TYPE_LINK) {
//            // 链接
//            return VIEW_TYPE_NORMAL_LINK;
//        } else {
//            // 其他，数据错误
//            return VIEW_TYPE_NORMAL_TEXT;
//        }

        return VIEW_TYPE_NORMAL_TEXT;
    }

    /**
     * 跳转到文件预览
     *
     * @param filePath
     */
    private void intentPreviewFile(String filePath, String fileName, String fromName, long size) {
        MucFileBean data = new MucFileBean();

        // 取出文件后缀
        int start = filePath.lastIndexOf(".");
        String suffix = start > -1 ? filePath.substring(start + 1).toLowerCase() : "";

        int fileType = XfileUtils.getFileType(suffix);
        data.setNickname(fromName);
        data.setUrl(filePath);
        data.setName(fileName);
        data.setSize(size);
        data.setState(DownManager.STATE_UNDOWNLOAD);
        data.setType(fileType);
        Intent intent = new Intent(mContext, MucFileDetails.class);
        intent.putExtra("data", data);
        mContext.startActivity(intent);
    }

    /**
     * 第一次调用时就已经是阅读完第一页，加载第二页了，
     */
    private void loadCommentsNextPage(TextView view, String messageId, CommentAdapter adapter) {
        // isLoading同时有noMore效果，只有加载出新数据时才设置isLoading为false,
        if (adapter.isLoading()) {
            return;
        }
        adapter.setLoading(true);
        // 只能是20， 因为朋友圈消息列表接口直接返回了的是第一页20条，
        int pageSize = 20;
        // 有20个就加载第二页也就是index==1, 21个是加载第三页，得到空列表，就能停止了，
        int index = (adapter.getCount() + (pageSize - 1)) / pageSize;
        Map<String, String> params = new HashMap<>();
        params.put("access_token", IMUserInfoUtil.getInstance().getToken());
        params.put("pageIndex", String.valueOf(index));
        params.put("pageSize", String.valueOf(pageSize));
        params.put("messageId", messageId);

        String url = coreManager.getConfig().MSG_COMMENT_LIST;

        view.setTag(messageId);
        HttpUtils.get().url(url)
                .params(params)
                .build()
                .execute(new ListCallback<Comment>(Comment.class) {
                    @Override
                    public void onResponse(ArrayResult<Comment> result) {
                        List<Comment> data = result.getData();
                        if (data.size() > 0) {
//                            adapter.addAll(data);
//                            adapter.setLoading(false);
                        } else {
                            ToastUtil.showToast(mContext, R.string.tip_no_more);
                            if (view.getTag() == messageId) {
                                // 隐藏加载按钮，
                                view.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        Reporter.post("评论分页加载失败，", e);
                        ToastUtil.showToast(mContext, mContext.getString(R.string.tip_comment_load_error));
                    }
                });

    }

    private String getShowName(String userId, String defaultName) {
        String cache = showNameCache.get(userId);
        if (!TextUtils.isEmpty(cache)) {
            return cache;
        }
        String showName = "";

        if (userId.equals(mLoginUserId)) {
            showName = coreManager.getSelf().getNickName();
        } else {
            Friend friend = FriendDao.getInstance().getFriend(mLoginUserId, userId);
            if (friend != null) {
                showName = TextUtils.isEmpty(friend.getRemarkName()) ? friend.getNickName() : friend.getRemarkName();
            }
        }

        if (TextUtils.isEmpty(showName)) {
            showName = defaultName;
        }
        showNameCache.put(userId, showName);
        return showName;
    }

    /* 操作事件 */
    private void showDeleteMsgDialog(final int position) {
        SelectionFrame selectionFrame = new SelectionFrame(mContext);
        int tip;
        if (mContext instanceof MyCollection) {
            tip = R.string.sure_cancel_collection;
        } else {
            tip = R.string.delete_prompt;
        }
        selectionFrame.setSomething(null, mContext.getString(tip), new SelectionFrame.OnSelectionFrameClickListener() {
            @Override
            public void cancelClick() {

            }

            @Override
            public void confirmClick() {
                if (collectionType == 1 || collectionType == 2) {
                    // 删除收藏
                    deleteCollection(position);
                } else {
                    // 删除评论
                    deleteMsg(position);
                }
            }
        });
        selectionFrame.show();
    }

    private void deleteMsg(final int position) {
        final OORTDynamicReview message = mMessages.get(position);
        if (message == null) {
            return;
        }

    }

    public void deleteCollection(final int position) {

    }

    private void showBodyTextLongClickDialog(final String text) {
        CharSequence[] items = new CharSequence[]{mContext.getString(R.string.copy)};
        new AlertDialog.Builder(mContext).setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        // 复制文字
                        SystemUtil.copyText(mContext, text);
                        break;
                }
            }
        }).setCancelable(true).create().show();
    }

    private void showCommentLongClickDialog(final int messagePosition, final int commentPosition,
                                            final CommentAdapter adapter) {
        if (messagePosition < 0 || messagePosition >= mMessages.size()) {
            return;
        }
        final OORTDynamicReview message = mMessages.get(messagePosition);
        if (message == null) {
            return;
        }
        final List<DynamicBean.CommentsBean.ListBean> comments =  message.getDynamic().getComments().getList();
        if (comments == null) {
            return;
        }
        if (commentPosition < 0 || commentPosition >= comments.size()) {
            return;
        }
        final DynamicBean.CommentsBean.ListBean comment = comments.get(commentPosition);

        CharSequence[] items;
        if (comment.getUserid().equals(mLoginUserId) || message.getDynamic().getOort_userid().equals(mLoginUserId)) {
            // 我的评论 || 我的消息，那么我就可以删除
            items = new CharSequence[]{mContext.getString(R.string.copy), mContext.getString(R.string.delete)};
        } else {
            items = new CharSequence[]{mContext.getString(R.string.copy)};
        }
        new AlertDialog.Builder(mContext).setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:// 复制文字
                        if (TextUtils.isEmpty(comment.getContent())) {
                            return;
                        }
                        SystemUtil.copyText(mContext, comment.getContent());
                        break;
                    case 1:
                        //deleteComment(message, messagePosition, comment.getUuid(), comments, commentPosition, adapter);
                        break;
                }
            }
        }).setCancelable(true).create().show();
    }

    /**
     * 删除一条回复
     */



    private <T> T firstOrNull(List<T> list) {
        if (list == null) {
            return null;
        }
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }






    private void refreshPostion(int pos){

        OORTDynamicReview message = mMessages.get(pos);
        String mToken = FastSharedPreferences.get("USERINFO_SAVE").getString("token","");
        HttpRequestParam.dynamic_info(mToken,message.getDynamic().getOort_duuid()).subscribe(new RxBus.BusObserver<String>() {
            @Override
            public void onNext(String s) {
                ResObj<OORTDynamic> res = JSON.parseObject(s,new TypeToken<ResObj<OORTDynamic>>() {}.getType());//
                if(res.getCode() == 200 && res.getData() != null){
                    notifyDataSetChanged();
                }

            }

            @Override
            public void onError(Throwable e) {
                Log.v("msg" , e.toString());
            }
        });
    }



    /**
     * 停止播放声音
     */
    public void stopVoice() {
        if (mAudioPalyer != null) {
            mAudioPalyer.stop();
        }
        VoicePlayer.instance().stop();
    }

    /**
     * @param viewHolder
     */
    private void play(ViewHolder viewHolder, PublicMessage message) {
        JCVideoPlayer.releaseAllVideos();

        String voiceUrl = message.getFirstAudio();
        if (mVoicePlayId == null) {
            // 没有在播放
            try {
                mAudioPalyer.play(voiceUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mVoicePlayId = message.getMessageId();
            updateVoiceViewHolderIconStatus(true, viewHolder);
            mVoicePlayViewHolder = viewHolder;
        } else {
            if (mVoicePlayId == message.getMessageId()) {
                mAudioPalyer.stop();
                mVoicePlayId = null;
                updateVoiceViewHolderIconStatus(false, viewHolder);
                mVoicePlayViewHolder = null;
            } else {
                // 正在播放别的， 在播放这个
                mAudioPalyer.stop();
                mVoicePlayId = null;
                if (mVoicePlayViewHolder != null) {
                    updateVoiceViewHolderIconStatus(false, mVoicePlayViewHolder);
                }
                try {
                    mAudioPalyer.play(voiceUrl);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mVoicePlayId = message.getMessageId();
                updateVoiceViewHolderIconStatus(true, viewHolder);
                mVoicePlayViewHolder = viewHolder;
            }
        }
    }

    private void updateVoiceViewHolderIconStatus(boolean play, ViewHolder viewHolder) {
        if (viewHolder instanceof NormalVoiceHolder) {
            // 普通音频
            if (play) {
                ((NormalVoiceHolder) viewHolder).voice_action_img.setImageResource(R.drawable.feed_main_player_pause);
            } else {
                ((NormalVoiceHolder) viewHolder).voice_action_img.setImageResource(R.drawable.feed_main_player_play);
            }
        } else {
            // 转载音频
            if (play) {
                ((FwVoiceHolder) viewHolder).voice_action_img.setImageResource(R.drawable.feed_main_player_pause);
            } else {
                ((FwVoiceHolder) viewHolder).voice_action_img.setImageResource(R.drawable.feed_main_player_play);
            }
        }
    }

    /**
     * 节口回调的方法
     */
    @Override
    public void ideChange() {
        stopVoice();
    }

    /**
     * 刷新适配器
     **/
    public void setData(List<OORTDynamicReview> mMessages) {
        this.mMessages = mMessages;
        this.notifyDataSetChanged();
    }

    /**
     * 0:正常 default==0
     * 1:查看我的收藏 隐藏赞与评论功能 删除按钮一直显示
     * 2.发送我的收藏 隐藏赞与评论功能 删除按钮一直隐藏
     */
    public void setCollectionType(int collectionType) {
        this.collectionType = collectionType;
    }

    public void refresh(ArrayList arr) {
        mMessages.clear();;
        mMessages.addAll(arr);
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(DynamicReviewListAdapter.ViewHolder vh);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar_img;
        TextView nick_name_tv;
        TextView time_tv;
        HttpTextView body_tv;
        //  HttpTextView body_tvS;
        TextView open_tv;
        FrameLayout content_fl;
        TextView delete_tv;
        TextView multi_praise_tv;
        View line_v;
        ListView command_listView;
        TextView tvLoadMore;
        TextView location_tv;

        View llOperator;
        View llThumb;
        CheckableImageView ivThumb;
        TextView tvThumb;
        View llComment;
        CheckableImageView ivComment;
        TextView tvComment;
        View llCollection;
        CheckableImageView ivCollection;
        View llReport;
        ImageView iv_prise;

        TextView tv_dept;
        TextView tv_baijin_flag;
        TextView tv_jinghua_flag;
        TextView tv_settop_flag;
        TextView tv_oper_flag;

        TextView tv_comment_content;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar_img = (ImageView) itemView.findViewById(R.id.avatar_img);
            nick_name_tv = (TextView) itemView.findViewById(R.id.nick_name_tv);

            tv_dept = (TextView) itemView.findViewById(R.id.tv_dept);
            tv_baijin_flag = (TextView) itemView.findViewById(R.id.tv_baijing_flag);
            tv_jinghua_flag = (TextView) itemView.findViewById(R.id.tv_jinghua_flag);
            tv_settop_flag = (TextView) itemView.findViewById(R.id.tv_settop_flag);

            tv_oper_flag = (TextView) itemView.findViewById(R.id.tv_oper_flag);

            tv_comment_content = (TextView) itemView.findViewById(R.id.tv_comment_content);
            time_tv = (TextView) itemView.findViewById(R.id.time_tv);
            body_tv = itemView.findViewById(R.id.body_tv);
            // body_tvS = itemView.findViewById(R.id.body_tvS);
            open_tv = (TextView) itemView.findViewById(R.id.open_tv);
            content_fl = (FrameLayout) itemView.findViewById(R.id.content_fl);
            delete_tv = (TextView) itemView.findViewById(R.id.delete_tv);

            llOperator = itemView.findViewById(R.id.llOperator);
            llThumb = itemView.findViewById(R.id.llThumb);
            ivThumb = itemView.findViewById(R.id.ivThumb);
            tvThumb = itemView.findViewById(R.id.tvThumb);
            llComment = itemView.findViewById(R.id.llComment);
            ivComment = itemView.findViewById(R.id.ivComment);
            tvComment = itemView.findViewById(R.id.tvComment);
            llCollection = itemView.findViewById(R.id.llCollection);
            ivCollection = itemView.findViewById(R.id.ivCollection);
            llReport = itemView.findViewById(R.id.llReport);
        }
    }

    /* 普通的Text */
    static class NormalTextHolder extends ViewHolder {

        public NormalTextHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    /* 转载的Text */
    static class FwTextHolder extends ViewHolder {
        TextView text_tv;

        public FwTextHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    /* 普通的单张图片 */
    static class NormalSingleImageHolder extends ViewHolder {
        ImageView image_view;

        public NormalSingleImageHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    /* 转载的单张图片 */
    static class FwSingleImageHolder extends ViewHolder {
        TextView text_tv;
        ImageView image_view;

        public FwSingleImageHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    /* 普通的多张图片 */
    static class NormalMultiImageHolder extends ViewHolder {
        MyGridView grid_view;

        public NormalMultiImageHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    /* 转载的多张图片 */
    static class FwMultiImageHolder extends ViewHolder {
        TextView text_tv;
        MyGridView grid_view;

        public FwMultiImageHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    /* 普通的音频 */
    static class NormalVoiceHolder extends ViewHolder {
        ImageView img_view;
        ImageView voice_action_img;
        TextView voice_desc_tv;
        VoiceAnimView chat_to_voice;

        public NormalVoiceHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    /* 转载的音频 */
    static class FwVoiceHolder extends ViewHolder {
        TextView text_tv;
        ImageView img_view;
        ImageView voice_action_img;
        TextView voice_desc_tv;

        public FwVoiceHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    /* 普通的视频 */
    static class NormalVideoHolder extends ViewHolder {
        JVCideoPlayerStandardSecond gridViewVideoPlayer;

        public NormalVideoHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    /* 转载的视频 */
    static class FwVideoHolder extends ViewHolder {
        TextView text_tv;
        ImageView video_thumb_img;
        TextView video_desc_tv;

        public FwVideoHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    /************************* 播放声音 ******************************/

    /* 普通的文件 */
    static class NormalFileHolder extends ViewHolder {
        RelativeLayout file_click;
        ImageView file_image;
        TextView text_tv;

        public NormalFileHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    /* 普通的链接 */
    static class NormalLinkHolder extends ViewHolder {
        LinearLayout link_click;
        ImageView link_image;
        TextView link_tv;

        public NormalLinkHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    static class CommentViewHolder {
        TextView text_view;
    }

    public class CommentAdapter extends BaseAdapter {
        private int messagePosition;
        private boolean loading;

        private OORTDynamic mDynamic;
        private List<DynamicBean.CommentsBean.ListBean> datas;

        CommentAdapter(int messagePosition, List<DynamicBean.CommentsBean.ListBean> data,OORTDynamic dynamic) {
            this.messagePosition = messagePosition;
            mDynamic = dynamic;
            if (data == null) {
                datas = new ArrayList<>();
            } else {
                this.datas = data;
            }
        }

/*
        public void setData(int messagePosition, List<Comment> data) {
            this.messagePosition = messagePosition;
            this.datas = data;
            notifyDataSetChanged();
        }
*/

        public void addAll(List<DynamicBean.CommentsBean.ListBean> data) {
            this.datas.addAll(data);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            CommentViewHolder holder;
            if (convertView == null) {
                holder = new CommentViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.p_msg_comment_list_item, null);
                holder.text_view = (TextView) convertView.findViewById(R.id.text_view);
                convertView.setTag(holder);
            } else {
                holder = (CommentViewHolder) convertView.getTag();
            }
            final DynamicBean.CommentsBean.ListBean comment = datas.get(position);


            UserInfoBean userInfo = mDynamic.getUserInfo(comment.getUserid());
            UserInfoBean replyUserInfo = mDynamic.getUserInfo(comment.getOort_reply_userid());
            SpannableStringBuilder builder = new SpannableStringBuilder();
            String showName = getShowName(comment.getUserid(), userInfo.getOort_name());
            UserClickableSpan.setClickableSpan(mContext, builder, showName, comment.getUserid());            // 设置评论者的ClickSpanned
            if (!TextUtils.isEmpty(comment.getUserid()) && !TextUtils.isEmpty(comment.getOort_reply_userid())) {
                builder.append(mContext.getString(R.string.replay_infix_comment));
                String toShowName = getShowName(replyUserInfo.getOort_uuid(), replyUserInfo.getOort_name());
                UserClickableSpan.setClickableSpan(mContext, builder, toShowName, replyUserInfo.getOort_uuid());// 设置被评论者的ClickSpanned
            }
            builder.append(":");
            // 设置评论内容
            String commentBody = comment.getContent();
            if (!TextUtils.isEmpty(commentBody)) {
                commentBody = StringUtils.replaceSpecialChar(comment.getContent());
                CharSequence charSequence = HtmlUtils.transform200SpanString(commentBody, true);
                builder.append(charSequence);
            }
            holder.text_view.setText(builder);
            holder.text_view.setLinksClickable(true);
            holder.text_view.setMovementMethod(LinkMovementClickMethod.getInstance());

            holder.text_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (comment.getUserid().equals(mLoginUserId)) {
                        // 如果消息是我发的，那么就弹出删除和复制的对话框
                        showCommentLongClickDialog(messagePosition, position, CommentAdapter.this);
                    } else {
                        // 弹出回复的框
                        String toShowName = getShowName(comment.getUserid(), userInfo.getOort_name());
                        if (mContext instanceof MainActivity) {
                            showCommentEnterView(messagePosition, comment.getUserid(), userInfo.getOort_name(), toShowName);
                        } else {
//                            EventBus.getDefault().post(new MessageEventReply("Reply", comment, messagePosition, toShowName,
//                                    (ListView) parent));
                        }
                    }
                }
            });

            holder.text_view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showCommentLongClickDialog(messagePosition, position, CommentAdapter.this);
                    return true;
                }
            });

            return convertView;
        }

        public boolean isLoading() {
            return loading;
        }

        public void setLoading(boolean loading) {
            this.loading = loading;
        }

        public void addComment(DynamicBean.CommentsBean.ListBean comment) {
            this.datas.add(0, comment);
            notifyDataSetChanged();
        }
    }

    public void showCommentEnterView(int messagePosition, String toUserId, String toNickname, String toShowName) {

        mCommentReplyCache = new CommentReplyCache();
        mCommentReplyCache.messagePosition = messagePosition;
        mCommentReplyCache.toUserId = toUserId;
        mCommentReplyCache.toNickname = toNickname;
        String hint;
        Boolean isReply = false;
        if (TextUtils.isEmpty(toUserId) || TextUtils.isEmpty(toNickname) || TextUtils.isEmpty(toShowName)) {
            // mPMsgBottomView.setHintText("");
            hint = mContext.getString(R.string.enter_pinlunt);

        } else {
            // mPMsgBottomView.setHintText(getString(R.string.replay_text, toShowName));
            hint = mContext.getString(R.string.replay_text, toShowName);
            isReply = true;
        }
        // mPMsgBottomView.show();
        Boolean finalIsReply = isReply;
        TrillCommentInputDialog trillCommentInputDialog = new TrillCommentInputDialog(mContext, hint, str -> {
            if (mCommentReplyCache != null) {
                mCommentReplyCache.text = str;
                //addComment(mCommentReplyCache);



                final OORTDynamicReview message = mMessages.get(messagePosition);
                if (message == null) {
                    return;
                }

                String mToken = FastSharedPreferences.get("USERINFO_SAVE").getString("token","");
                if(finalIsReply){
                    HttpRequestParam.dynamic_comment_push_reply(mToken,str,message.getDynamic().getOort_duuid(),toUserId).subscribe(new RxBus.BusObserver<String>() {
                        @Override
                        public void onNext(String s) {
                            Res res = JSON.parseObject(s,Res.class);//
                            if(res.getCode() == 200){
                                refreshPostion(messagePosition);
                            }

                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.v("msg" , e.toString());
                        }
                    });

                    return;

                }
                HttpRequestParam.dynamic_comment_push(mToken,str,message.getDynamic().getOort_duuid()).subscribe(new RxBus.BusObserver<String>() {
                    @Override
                    public void onNext(String s) {
                        Res res = JSON.parseObject(s,Res.class);//
                        if(res.getCode() == 200){
                            refreshPostion(messagePosition);
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.v("msg" , e.toString());
                    }
                });


            }
        });
        Window window = trillCommentInputDialog.getWindow();
        if (window != null) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);// 软键盘弹起
            trillCommentInputDialog.show();
        }
    }

    class CommentReplyCache {
        int messagePosition;// 消息的Position
        String toUserId;
        String toNickname;
        String text;
    }
    private class SingleImageClickListener implements View.OnClickListener {
        private String url;

        SingleImageClickListener(String url) {
            this.url = url;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, SingleImagePreviewActivity.class);
            intent.putExtra(AppConstant.EXTRA_IMAGE_URI, url);
            mContext.startActivity(intent);
        }
    }

    private class MultipleImagesClickListener implements AdapterView.OnItemClickListener {
        private List<DynamicBean.AttachBean> images;

        MultipleImagesClickListener(List<DynamicBean.AttachBean> images) {
            this.images = images;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (images == null || images.size() <= 0) {
                return;
            }
            ArrayList<String> lists = new ArrayList<String>();
            for (int i = 0; i < images.size(); i++) {
                lists.add(images.get(i).getUrl());
            }
            Intent intent = new Intent(mContext, MultiImagePreviewActivity.class);
            intent.putExtra(AppConstant.EXTRA_IMAGES, lists);
            intent.putExtra(AppConstant.EXTRA_POSITION, position);
            intent.putExtra(AppConstant.EXTRA_CHANGE_SELECTED, false);
            mContext.startActivity(intent);
        }
    }
}
