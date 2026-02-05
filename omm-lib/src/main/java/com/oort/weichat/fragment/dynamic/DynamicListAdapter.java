package com.oort.weichat.fragment.dynamic;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Layout;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.oort.weichat.bean.collection.CollectionEvery;
import com.oort.weichat.db.dao.FriendDao;
import com.oort.weichat.fragment.entity.DynamicBean;
import com.oort.weichat.fragment.entity.DynamicMyinfo;
import com.oort.weichat.fragment.entity.DynamicPlayAudioEvent;
import com.oort.weichat.fragment.entity.DynamicProfile;
import com.oort.weichat.fragment.entity.DynamicSetTopEvent;
import com.oort.weichat.fragment.entity.OORTDynamic;
import com.oort.weichat.fragment.entity.Res;
import com.oort.weichat.fragment.entity.ResObj;
import com.oort.weichat.fragment.entity.UserInfoBean;
import com.oort.weichat.helper.ImageLoadHelper;
import com.oort.weichat.ui.MainActivity;
import com.oort.weichat.ui.base.CoreManager;
import com.oort.weichat.ui.circle.BusinessCircleActivity;
import com.oort.weichat.ui.me.MyCollection;
import com.oort.weichat.ui.mucfile.DownManager;
import com.oort.weichat.ui.mucfile.MucFileDetails;
import com.oort.weichat.ui.mucfile.XfileUtils;
import com.oort.weichat.ui.mucfile.bean.MucFileBean;
import com.oort.weichat.ui.tool.MultiImagePreviewActivity;
import com.oort.weichat.ui.tool.SingleImagePreviewActivity;
import com.oort.weichat.ui.tool.WebViewActivity;
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
import com.oortcloud.appstore.AppStoreInit;
import com.oortcloud.appstore.adapter.BaseRecyclerViewAdapter;
import com.oortcloud.appstore.http.HttpRequestParam;
import com.oortcloud.basemodule.im.IMUserInfoUtil;
import com.oortcloud.basemodule.user.UserInfo;
import com.oortcloud.basemodule.user.UserInfoUtils;
import com.oortcloud.basemodule.utils.AppSetConfig;
import com.oortcloud.basemodule.utils.ImageLoader;
import com.oortcloud.basemodule.utils.OperLogUtil;
import com.oortcloud.basemodule.utils.fastsharedpreferences.FastSharedPreferences;
import com.oortcloud.basemodule.widget.transformerstip.TransformersTip;
import com.oortcloud.basemodule.widget.transformerstip.gravity.ArrowGravity;
import com.oortcloud.basemodule.widget.transformerstip.gravity.TipGravity;
import com.oortcloud.login.net.utils.RxBus;
import com.sentaroh.android.upantool.FileTool;
import com.xuan.xuanhttplibrary.okhttp.HttpUtils;
import com.xuan.xuanhttplibrary.okhttp.callback.ListCallback;
import com.xuan.xuanhttplibrary.okhttp.result.ArrayResult;
import com.xuexiang.xui.widget.dialog.DialogLoader;
import com.xuexiang.xui.widget.flowlayout.FlowTagLayout;
import com.xuexiang.xui.widget.toast.XToast;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

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
public class DynamicListAdapter extends RecyclerView.Adapter<DynamicListAdapter.ViewHolder> implements BusinessCircleActivity.ListenerAudio {
    public static final int VIEW_TYPE_NORMAL_TEXT = 0;
    public static final int VIEW_TYPE_NORMAL_SINGLE_IMAGE = 2;
    public static final int VIEW_TYPE_NORMAL_MULTI_IMAGE = 4;
    public static final int VIEW_TYPE_NORMAL_VOICE = 6;
    public static final int VIEW_TYPE_NORMAL_VIDEO = 8;
    public static final int VIEW_TYPE_NORMAL_FILE = 10;
    // 分享的链接
    public static final int VIEW_TYPE_NORMAL_LINK = 11;
    public static final int VIEW_TYPE_HOME_INFO = 8;

    public static final int VIEW_TYPE_USER_HOME_INFO = 14;

    public static final int VIEW_TYPE_DYNAMIC_SEARCH = 15;
    private Context mContext;
    private CoreManager coreManager;
    private List<OORTDynamic> mMessages;
    private LayoutInflater mInflater;
    private String mLoginUserId;
    private String mLoginNickName;
    private ViewHolder mVoicePlayViewHolder;
    private AudioPalyer mAudioPalyer;
    private String mVoicePlayId = null;
    private Map<String, Boolean> mClickOpenMaps = new HashMap<>();
    private int collectionType;
    private int contentType;

    public interface OnSearchListener {
        void onSearch(String query); // 定义搜索回调方法
    }

    public OnSearchListener getSearchListener() {
        return searchListener;
    }

    public void setSearchListener(OnSearchListener searchListener) {
        this.searchListener = searchListener;
    }

    private OnSearchListener searchListener;

    private OnItemClickListener onItemClickListener = null;

    private OnDongTaiItemClickListener onDongTaiItemClickListener = null;
    /**
     * 缓存getShowName，
     * 每次约两三毫秒，
     */
    private WeakHashMap<String, String> showNameCache = new WeakHashMap<>();
    private CommentReplyCache mCommentReplyCache;
    private PopupWindow mPop;
    private DynamicAtTagAdapter tagAdapter;

    public DynamicMyinfo getMyInfo() {
        return myInfo;
    }

    public void setMyInfo(DynamicMyinfo myInfo) {
        this.myInfo = myInfo;

        notifyDataSetChanged();
    }

    private DynamicMyinfo myInfo;

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
        notifyDataSetChanged();
    }

    private UserInfo userInfo;


    public DynamicProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(DynamicProfile userProfile) {
        this.userProfile = userProfile;
        notifyDataSetChanged();
    }

    private DynamicProfile userProfile;


    public int getFollowState() {
        return followState;
    }

    public void setFollowState(int followState) {
        this.followState = followState;
        notifyDataSetChanged();
    }

    private int followState = 0;





    public DynamicListAdapter(Context context, CoreManager coreManager, List<OORTDynamic> messages,int type) {
        contentType = type;
        initAdapter(context,coreManager,messages);
    }

    public DynamicListAdapter(Context context, CoreManager coreManager, List<OORTDynamic> messages){
        initAdapter(context,coreManager,messages);
    }


    public void initAdapter(Context context, CoreManager coreManager, List<OORTDynamic> messages) {
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
    public void setOnDongTaiItemClickListener(OnDongTaiItemClickListener onItemClickListener) {
        this.onDongTaiItemClickListener = onItemClickListener;
    }



    @Override
    public long getItemId(int position) {
        if(contentType == 8) {
            if (position == 0) {
                return "dynamicHeaderList".hashCode();
            } else {
                return mMessages.get(position - 1).hashCode();
            }
        }else if(contentType == 14) {
            if (position == 0) {
                return "dynamicUserHeaderList".hashCode();
            } else {
                return mMessages.get(position - 1).hashCode();
            }
        }else if(contentType == 15) {
            if (position == 0) {
                return "dynamicSearchList".hashCode();
            } else {
                return mMessages.get(position - 1).hashCode();
            }
        }else {
            return mMessages.get(position).hashCode();

        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {




        if (viewType == VIEW_TYPE_HOME_INFO) {
            View convertView = mInflater.inflate(R.layout.fragment_dynamic_list_header, viewGroup, false);
            HeadViewHolder viewHolder;

            viewHolder = new HeadViewHolder(convertView);
            return viewHolder;
        }


        if (viewType == VIEW_TYPE_USER_HOME_INFO) {
            View convertView = mInflater.inflate(R.layout.fragment_dynamic_list_header_user_info, viewGroup, false);
            HeadViewHolder viewHolder;

            viewHolder = new HeadViewHolder(convertView);




            return viewHolder;
        }

        if (viewType == VIEW_TYPE_DYNAMIC_SEARCH) {
            View convertView = mInflater.inflate(R.layout.fragment_dynamic_search, viewGroup, false);
            SearchViewHolder viewHolder;

            if(searchListener != null) {
                viewHolder = new SearchViewHolder(convertView, searchListener);
                return viewHolder;
            }





        }


        View convertView = mInflater.inflate(R.layout.p_msg_item_main_dynamic_body, viewGroup, false);
        View innerView = null;
        ViewHolder viewHolder;
        if (viewType == VIEW_TYPE_NORMAL_TEXT) {
            viewHolder = new NormalTextHolder(convertView);
        } else if (viewType == VIEW_TYPE_NORMAL_SINGLE_IMAGE) {
            NormalSingleImageHolder holder = new NormalSingleImageHolder(convertView);
            innerView = mInflater.inflate(R.layout.p_msg_item_normal_single_img, holder.content_fl, false);
            holder.image_view = (ImageView) innerView.findViewById(R.id.image_view);
            holder.icon_play = (ImageView) innerView.findViewById(R.id.icon_paly);
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
            viewHolder.llOperator.setVisibility(View.GONE);
        } else {
            viewHolder.llOperator.setVisibility(View.VISIBLE);
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
        if(contentType == 8){

            if(position == 0){
                HeadViewHolder hvh = (HeadViewHolder) viewHolder;
                if(myInfo != null) {
                    hvh.tv_attend_count.setText(String.valueOf(myInfo.getFollows()));
                    hvh.tv_dongtai_cout.setText(String.valueOf(myInfo.getDynamics()));
                    hvh.tv_jinghua_count.setText(String.valueOf(myInfo.getDynamic_grade1()));
                    hvh.tv_baijin_cout.setText(String.valueOf(myInfo.getDynamic_grade2()));
                    hvh.tv_like_count.setText(String.valueOf(myInfo.getLikes()));
                    hvh.tv_collect_count.setText(String.valueOf(myInfo.getCollects()));
                    hvh.tv_comment_count.setText(String.valueOf(myInfo.getComments()));
                    hvh.tv_fans_count.setText(String.valueOf(myInfo.getFans()));

                    AppSetConfig.getInstance().addCallback(new AppSetConfig.ConfigCallback() {
                        @Override
                        public void callback(AppSetConfig.ConfigData.SolutionBean solutionBean) {
                            ((Activity)mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    if(solutionBean != null) {
                                        OperLogUtil.msg("dynamic_home_header" + solutionBean.toString());
                                    }
                                    ImageLoader.loadImage(hvh.icon_banner,solutionBean.getAppSetting().getBasicConfig().getAppBanner(),R.mipmap.icon_banner);
                                }
                            });

                        }
                    });

                    hvh.ll_attend.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {



                            if(userInfo != null) {
                                OperLogUtil.msg("点击了动态个人中心我的关注");
                                FragmentDynamicUserList fragmentDynamicList = new FragmentDynamicUserList();
                                fragmentDynamicList.setContentType(1);
                                fragmentDynamicList.setOort_userid(userInfo.getOort_uuid());
                                CustomBottomSheetDialogFragment dialog = new CustomBottomSheetDialogFragment(fragmentDynamicList);

                                dialog.show(((AppCompatActivity) mContext).getSupportFragmentManager(), "ll_attend");
                            }



                        }
                    });

                    hvh.ll_dongtai.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {


                            if(myInfo.getDynamics() > 0) {
                                OperLogUtil.msg("点击了动态个人中心我的动态");

                                if (onDongTaiItemClickListener != null) {
                                    onDongTaiItemClickListener.onItemClick();
                                }
                            }

                        }
                    });

                    hvh.ll_jinghua.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if(userInfo != null) {
                                OperLogUtil.msg("点击了动态个人中心我的精华");
                                FragmentDynamicList fragmentDynamicList = new FragmentDynamicList();
                                fragmentDynamicList.setContentType(5);
                                fragmentDynamicList.setOort_uuuid(userInfo.getOort_uuid());
                                CustomBottomSheetDialogFragment dialog = new CustomBottomSheetDialogFragment(fragmentDynamicList);

                                dialog.show(((AppCompatActivity) mContext).getSupportFragmentManager(), "jinhua");
                            }
                        }
                    });

                    hvh.ll_baijin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(userInfo != null) {
                                OperLogUtil.msg("点击了动态个人中心我的白金");
                                FragmentDynamicList fragmentDynamicList = new FragmentDynamicList();
                                fragmentDynamicList.setContentType(4);
                                fragmentDynamicList.setOort_uuuid(userInfo.getOort_uuid());
                                CustomBottomSheetDialogFragment dialog = new CustomBottomSheetDialogFragment(fragmentDynamicList);

                                dialog.show(((AppCompatActivity) mContext).getSupportFragmentManager(), "baijin");
                            }
                        }
                    });

                    hvh.ll_like.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            OperLogUtil.msg("点击了动态个人中心我的点赞");
                            FragmentDynamicReviewList fragmentDynamicList = new FragmentDynamicReviewList();
                            fragmentDynamicList.setContentType(11);
                            CustomBottomSheetDialogFragment dialog = new CustomBottomSheetDialogFragment(fragmentDynamicList);

                            dialog.show(((AppCompatActivity)mContext).getSupportFragmentManager(), "like");
                        }
                    });

                    hvh.ll_collect.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            OperLogUtil.msg("点击了动态个人中心我的收藏");
                            FragmentDynamicList fragmentDynamicList = new FragmentDynamicList();
                            fragmentDynamicList.setContentType(7);
                            CustomBottomSheetDialogFragment dialog = new CustomBottomSheetDialogFragment(fragmentDynamicList);

                            dialog.show(((AppCompatActivity)mContext).getSupportFragmentManager(), "collect");
                        }
                    });

                    hvh.ll_comment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            OperLogUtil.msg("点击了动态个人中心我的评论");
                            FragmentDynamicReviewList fragmentDynamicList = new FragmentDynamicReviewList();
                            fragmentDynamicList.setContentType(12);
                            CustomBottomSheetDialogFragment dialog = new CustomBottomSheetDialogFragment(fragmentDynamicList);

                            dialog.show(((AppCompatActivity)mContext).getSupportFragmentManager(), "like");
                        }
                    });

                    hvh.ll_fans.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if(userInfo != null) {
                                OperLogUtil.msg("点击了动态个人中心我的粉丝");
                                FragmentDynamicUserList fragmentDynamicList = new FragmentDynamicUserList();
                                fragmentDynamicList.setContentType(2);
                                fragmentDynamicList.setOort_userid(userInfo.getOort_uuid());
                                CustomBottomSheetDialogFragment dialog = new CustomBottomSheetDialogFragment(fragmentDynamicList);

                                dialog.show(((AppCompatActivity) mContext).getSupportFragmentManager(), "ll_fans");
                            }
                        }
                    });
                }

                if(userInfo != null) {

                    //ImageLoader.loadImage(user_header,result.getData().getUserInfo().getOort_photo(),com.oortcloud.contacts.R.mipmap.default_head_portrait);
                    ImageLoader.loadImage(hvh.icon_header, userInfo.getOort_photo(),com.oortcloud.contacts.R.mipmap.default_head_portrait);
                    hvh.tv_name.setText(userInfo.getOort_name());
                    hvh.tv_depart.setText(userInfo.getOort_depname());
                }

                return;
            }else {
                position = position - 1;
            }

        }

        if(contentType == 14){

            if(position == 0){
                HeadViewHolder hvh = (HeadViewHolder) viewHolder;

                AppSetConfig.getInstance().addCallback(new AppSetConfig.ConfigCallback() {
                    @Override
                    public void callback(AppSetConfig.ConfigData.SolutionBean solutionBean) {
                        ((Activity)mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ImageLoader.loadImage(hvh.icon_banner,solutionBean.getAppSetting().getBasicConfig().getAppBanner(),R.mipmap.icon_banner);
                            }
                        });

                    }
                });

                if(userProfile != null) {
                    hvh.tv_attend_count.setText(String.valueOf(userProfile.getFollow()));
                    hvh.tv_dongtai_cout.setText(String.valueOf(userProfile.getDynamic()));
                    hvh.tv_fans_count.setText(String.valueOf(userProfile.getFans()));

                    hvh.btn_follow.setVisibility(View.VISIBLE);
                    if(followState == 0){
                        hvh.btn_follow.setVisibility(View.GONE);
                    }

                    if(followState == 1){
                        hvh.btn_follow.setText(mContext.getString(R.string.cancel_follow));
                    }
                    if(followState == 2){
                        hvh.btn_follow.setText(mContext.getString(R.string.follow));
                    }


                    hvh.btn_follow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            TextView tv = (TextView) view;
                            OperLogUtil.msg("点击了动态" + userProfile.getUserInfo().getOort_name() + "的个人主页" + tv.getText().toString() );
                            String mToken = FastSharedPreferences.get("USERINFO_SAVE").getString("token","");

                            HttpRequestParam.dynamic_follow(mToken,userProfile.getUserInfo().getOort_uuid(),followState == 1 ? 1 : 0).subscribe(new RxBus.BusObserver<String>() {
                                @Override
                                public void onNext(String s) {
                                    Res res = JSON.parseObject(s,Res.class);//
                                    if(res.getCode() == 200){

                                        setFollowState(followState == 1 ? 2 : 1);


                                    }

                                }

                                @Override
                                public void onError(Throwable e) {
                                    Log.v("msg" , e.toString());
                                }
                            });
                        }
                    });



                    hvh.ll_attend.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if(userProfile != null) {
                                OperLogUtil.msg("点击了动态" + userProfile.getUserInfo().getOort_name() + "的个人主页关注" );
                                FragmentDynamicUserList fragmentDynamicList = new FragmentDynamicUserList();
                                fragmentDynamicList.setContentType(1);
                                fragmentDynamicList.setOort_userid(userProfile.getUserInfo().getOort_uuid());
                                CustomBottomSheetDialogFragment dialog = new CustomBottomSheetDialogFragment(fragmentDynamicList);

                                dialog.show(((AppCompatActivity) mContext).getSupportFragmentManager(), "ll_attend");
                            }


                        }
                    });

                    hvh.ll_dongtai.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });



                    hvh.ll_fans.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if(userProfile != null) {
                                OperLogUtil.msg("点击了动态" + userProfile.getUserInfo().getOort_name() + "的个人主页粉丝" + userProfile.getUserInfo().getOort_name());
                                FragmentDynamicUserList fragmentDynamicList = new FragmentDynamicUserList();
                                fragmentDynamicList.setContentType(2);
                                fragmentDynamicList.setOort_userid(userProfile.getUserInfo().getOort_uuid());
                                CustomBottomSheetDialogFragment dialog = new CustomBottomSheetDialogFragment(fragmentDynamicList);

                                dialog.show(((AppCompatActivity) mContext).getSupportFragmentManager(), "ll_fans");
                            }
                        }
                    });


                    if(userProfile.getUserInfo() != null) {
                        //ImageLoader.loadImage(user_header,result.getData().getUserInfo().getOort_photo(),com.oortcloud.contacts.R.mipmap.default_head_portrait);
                        ImageLoader.loadImage(hvh.icon_header, userProfile.getUserInfo().getOort_photo(), com.oortcloud.contacts.R.mipmap.default_head_portrait);
                        hvh.tv_name.setText(userProfile.getUserInfo().getOort_name());
                        hvh.tv_depart.setText(userProfile.getUserInfo().getOort_depname());
                    }
                }



                return;
            }else {
                position = position - 1;
            }

        }

        if(contentType == 15){

            if(position == 0){
                SearchViewHolder hvh = (SearchViewHolder) viewHolder;
                hvh.bind();


                return;
            }else {
                position = position - 1;
            }

        }



        // 和ViewHolder一样的，只不过用作匿名内部类里面调用需要final
        final ViewHolder finalHolder = viewHolder;
        // set data
        final OORTDynamic message = mMessages.get(position);
        if (message == null) {
            return;
        }


        UserInfoBean userInfoBean = message.getUserInfo(message.getDynamic().getOort_userid());
        /* 设置头像 */
        //AvatarHelper.getInstance().displayAvatar(userInfoBean.getImuserid(), viewHolder.avatar_img);


        if(contentType != 10){
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DynamicActivityDynamicInfo.start(mContext,message.getDynamic().getOort_duuid());
                    OperLogUtil.msg("进入" +message.getDynamic().getOort_duuid() + "动态详情");
                }
            });
        }

        ImageLoader.loadImage(viewHolder.avatar_img,userInfoBean.getOort_photo(),com.oortcloud.contacts.R.mipmap.default_head_portrait);
        /* 设置昵称 */
        SpannableStringBuilder nickNamebuilder = new SpannableStringBuilder();
        final String userId = userInfoBean.getOort_uuid();
        String showName = getShowName(userId,userInfoBean.getOort_name());
        UserClickableSpan.setClickableSpan(mContext, nickNamebuilder, showName, userInfoBean.getOort_uuid() );//userInfoBean.getImuserid(),
        viewHolder.nick_name_tv.setText(nickNamebuilder);
        viewHolder.nick_name_tv.setLinksClickable(true);
        viewHolder.nick_name_tv.setMovementMethod(LinkMovementClickMethod.getInstance());
        viewHolder.tv_dept.setText(userInfoBean.getOort_depname());


        viewHolder.tv_baijin_flag.setVisibility(message.getDynamic().getOort_grade2() == 1 ? View.VISIBLE : View.GONE);
        viewHolder.tv_jinghua_flag.setVisibility(message.getDynamic().getOort_grade1() == 1 ? View.VISIBLE : View.GONE);
        viewHolder.tv_settop_flag.setVisibility(message.getDynamic().getOort_top() > 0 ? View.VISIBLE : View.GONE);

        List<DynamicBean.AttachBean> atts = new ArrayList<DynamicBean.AttachBean>();
        atts.addAll(message.getDynamic().getAttach_audios());
        atts.addAll(message.getDynamic().getAttach_atts());

        AttAdpter attadp = new AttAdpter(mContext);
        viewHolder.rv_atts.setAdapter(attadp);

        if(atts.size() > 0){
            attadp.setData(atts);
        }

        String [] flags = {mContext.getString(R.string.set_top9),mContext.getString(R.string.set_top8),mContext.getString(R.string.set_top7),mContext.getString(R.string.set_top6),mContext.getString(R.string.set_top5),mContext.getString(R.string.set_top4),mContext.getString(R.string.set_top3),mContext.getString(R.string.set_top2),mContext.getString(R.string.set_top1)};

        if(message.getDynamic().getOort_top() > 0) {
            if(message.getDynamic().getOort_top() < 9) {
                viewHolder.tv_settop_flag.setText(flags[message.getDynamic().getOort_top()]);
            }else {
                viewHolder.tv_settop_flag.setText(mContext.getString(R.string.set_top));
            }
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
            viewHolder.open_tv.setVisibility(View.GONE);
        } else {
            // 支持emoji显示
            viewHolder.body_tv.setFilters(new InputFilter[]{new EmojiInputFilter(mContext)});
            viewHolder.body_tv.setUrlText(body.getContent());
            viewHolder.body_tv.setVisibility(View.VISIBLE);






            String content = body.getContent();

            content = content.replace("\n\n","\n");
            if(body.getOort_tname() != null) {
                int startIndex = content.indexOf("#" + body.getOort_tname() + "#");

                if(startIndex > -1) {


                    SpannableString spannableString = new SpannableString(body.getContent());

                    ForegroundColorSpan foregroundColorSpan = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        foregroundColorSpan = new ForegroundColorSpan(mContext.getColor(R.color.main_color));
                        spannableString.setSpan(foregroundColorSpan, startIndex, startIndex + body.getOort_tname().length() + 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    }


                    viewHolder.body_tv.setUrlText(spannableString);
                }
            }


            viewHolder.body_tv.requestLayout();  // 强制重新布局
            viewHolder.body_tv.post(() -> {
                Layout layout = viewHolder.body_tv.getLayout();
                if (layout != null) {
                    int lines = layout.getLineCount();
                    if (contentType == 10) {
                        viewHolder.body_tv.setMaxLines(100000);
                        viewHolder.open_tv.setVisibility(View.GONE);
                        viewHolder.open_tv.setOnClickListener(null);
                    } else if (lines > 6) {
                        viewHolder.open_tv.setVisibility(View.VISIBLE);

                    } else {
                        viewHolder.open_tv.setVisibility(View.GONE);
                        viewHolder.open_tv.setOnClickListener(null);
                    }
                } else {
                    viewHolder.open_tv.setVisibility(View.GONE);
                }
            });


            // 判断是否超出6行限制，超过则显示"全文"



            //});
        }

        viewHolder.body_tv.setOnLongClickListener(v -> {
            showBodyTextLongClickDialog(body.getContent());
            return false;
        });

        if(contentType != 10) {
            viewHolder.body_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DynamicActivityDynamicInfo.start(mContext, message.getDynamic().getOort_duuid());
                    OperLogUtil.msg("进入" + message.getDynamic().getOort_duuid() + "动态详情");
                }
            });
        }





        if(message.getDynamic().getAt() != null) {
            viewHolder.tagLayout.setVisibility(View.VISIBLE);
            FlowTagLayout ly = viewHolder.tagLayout;
            DynamicAtTagAdapter tagAdapter = new DynamicAtTagAdapter(mContext);
            ly.setAdapter(tagAdapter);
            ly.setOnTagClickListener(new FlowTagLayout.OnTagClickListener() {
                @Override
                public void onItemClick(FlowTagLayout parent, View view, int position) {

                    String userid = message.getDynamic().getAt().get(position);
                    DynamicActivityUserHome.start(mContext, userid);

                }
            });

            ArrayList names = new ArrayList<>();
            for (String userid : message.getDynamic().getAt()) {
                names.add("@" + message.getUserInfo(userid).getOort_name());
            }
            tagAdapter.addTags(names);

        }else{
            viewHolder.tagLayout.setVisibility(View.GONE);

            FlowTagLayout ly = viewHolder.tagLayout;
            DynamicAtTagAdapter tagAdapter = new DynamicAtTagAdapter(mContext);
            ly.setAdapter(tagAdapter);
            ArrayList names = new ArrayList<>();

            tagAdapter.addTags(names);
        }

        int finalPosition = position;
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
                        showDeleteMsgDialog(finalPosition);
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
                            showDeleteMsgDialog(finalPosition);
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
        vh.ivThumb.setChecked(1 == message.getDynamic().getLikes().getIs_like());
        vh.tvThumb.setText(String.valueOf(message.getDynamic().getLikes().getCounts()));
        vh.llThumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(UiUtils.isNormalClick(v)) {
                    // 是否是点过赞，
                    final boolean isPraise = vh.ivThumb.isChecked();
                    // 调接口，旧代码保留，传的是相反的状态，
                    onPraise(finalPosition, !isPraise,vh);
                    // 更新赞数，调接口完成还会刷新，
                    int praiseCount = message.getDynamic().getLikes().getCounts();
                    if (isPraise) {
                        praiseCount--;
                    } else {
                        praiseCount++;
                    }
                    vh.tvThumb.setText(String.valueOf(praiseCount));
                    vh.ivThumb.toggle();
                }
            }
        });
        // 是否点评论过，
        // TODO: 不准了，评论分页加载，
        boolean isComment = false;
        if (message.getDynamic().getComments() != null) {
            for (DynamicBean.CommentsBean.ListBean comment : message.getDynamic().getComments().getList()) {
                if (mLoginUserId.equals(comment.getUserid())) {
                    isComment = true;
                }
            }
        }
        vh.ivComment.setChecked(isComment);
        vh.tvComment.setText(String.valueOf(message.getDynamic().getComments().getCounts()));
        vh.llComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 调接口，旧代码保留，
                onComment(finalPosition, vh.command_listView,vh);
                // 评论数在调接口完成后还会刷新，
            }
        });
        vh.ivCollection.setChecked(1 == message.getDynamic().getCollects().getIs_collect());
        vh.tvCollection.setText(String.valueOf(message.getDynamic().getCollects().getCounts()));
        vh.llCollection.setOnClickListener(v -> {
            onCollection(finalPosition,vh);
        });

        vh.ivReport.setImageResource(R.mipmap.icon_dynamic_list_item_more);
        vh.llReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onReport(finalPosition,v,vh);
            }
        });
        if(UserInfoUtils.getInstance(mContext).getLoginUserInfo().getOort_isadmin() == 1 || (message.getDynamic().getHad_auth() == 1)){// && !message.getDynamic().getOort_userid().equals(UserInfoUtils.getInstance(mContext).getUserId()


        }else{


            if(!message.getDynamic().getOort_userid().equals(UserInfoUtils.getInstance(mContext).getUserId())) {
                vh.ivReport.setImageResource(R.mipmap.icon_dynamic_list_item_share);
                vh.llReport.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        JSONObject obj = new JSONObject();


                        OORTDynamic dynamic = message;
                        try {

                            obj.put("type", "dynamic_android");
                            obj.put("title", dynamic.getUserInfo(dynamic.getDynamic().getOort_userid()).getOort_name() + mContext.getString(R.string.moment_of));
                            obj.put("sub", dynamic.getDynamic().getContent());
                            obj.put("url", "");

                            obj.put("duuid", dynamic.getDynamic().getOort_duuid());

                            if (dynamic.getDynamic().getAttach_images().size() > 0) {

                                DynamicBean.AttachBean b = dynamic.getDynamic().getAttach_images().get(0);
                                obj.put("atttype", b.getType());

                                if (b.getType().equals("video")) {
                                    obj.put("img", b.getThumb());
                                } else {
                                    obj.put("img", b.getUrl());
                                }
                            } else if (dynamic.getDynamic().getAttach_audios().size() > 0) {
                                DynamicBean.AttachBean b = dynamic.getDynamic().getAttach_audios().get(0);
                                obj.put("atttype", b.getType());
                                obj.put("img", b.getUrl());
                                obj.put("attachName", b.getName());

                            } else if (dynamic.getDynamic().getAttach_atts().size() > 0) {
                                DynamicBean.AttachBean b = dynamic.getDynamic().getAttach_atts().get(0);
                                obj.put("atttype", b.getType());
                                obj.put("img", b.getUrl());
                                obj.put("attachName", b.getName());
                            } else {
                                obj.put("atttype", "text");
                            }


                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }


//ShareNearChatFriend

                        Context mContext = AppStoreInit.getInstance().getApplication();
                        String appid = mContext.getApplicationInfo().processName;
                        Intent in = new Intent(appid + ".shareFriend");
                        in.putExtra("action", "shareFriend");
                        in.putExtra("content", obj.toString());
                        in.addFlags(FLAG_ACTIVITY_NEW_TASK);

                        mContext.startActivity(in);
                    }
                });
            }
        }


        /* 显示多少人赞过 */
        List<DynamicBean.LikesBean.ListBeanX> praises = message.getDynamic().getLikes().getList();


        if (praises != null && praises.size() > 0) {

//            for(int i = 0;i<10;i++){
//                praises.addAll(praises);
//                if(praises.size() > 1000){
//                    break;
//                }
//            }

            int count = praises.size();
            if(contentType != 10){
                if(count > 20) {
                    count = 20;
                }
            }
            viewHolder.multi_praise_tv.setVisibility(View.VISIBLE);
            viewHolder.iv_prise.setVisibility(View.VISIBLE);
            SpannableStringBuilder builder = new SpannableStringBuilder();
            for (int i = 0; i < count; i++) {

                DynamicBean.LikesBean.ListBeanX praise = praises.get(i);
                UserInfoBean userInfo = message.getUserInfo(praise.getUserid());
                String praiseName = getShowName(userInfo.getOort_uuid(), userInfo.getOort_name());
                UserClickableSpan.setClickableSpan(mContext, builder, praiseName, userInfo.getOort_uuid());
                if (i < count - 1)
                    builder.append(",");
            }
            if (praises.size() > count) {
                builder.append(mContext.getString(R.string.praise_ending_place_holder, message.getDynamic().getLikes().getCounts()));
            }
            viewHolder.multi_praise_tv.setText(builder);
        } else {
            viewHolder.iv_prise.setVisibility(View.GONE);
            viewHolder.multi_praise_tv.setVisibility(View.GONE);
            viewHolder.multi_praise_tv.setText("");
        }
        viewHolder.multi_praise_tv.setLinksClickable(true);
        viewHolder.multi_praise_tv.setMovementMethod(LinkMovementClickMethod.getInstance());
        viewHolder.multi_praise_tv.setOnClickListener(v -> {
            //PraiseListActivity.start(mContext, message.getMessageId());
        });

        /* 设置回复 */
        final List<DynamicBean.CommentsBean.ListBean> comments = message.getDynamic().getComments().getList();
        viewHolder.command_listView.setVisibility(View.VISIBLE);
        CommentAdapter adapter = new CommentAdapter(position, comments,message,vh);
        viewHolder.command_listView.setAdapter(adapter);
        viewHolder.tvLoadMore.setVisibility(View.GONE);
        if (comments != null && comments.size() > 0) {
            if (message.getDynamic().getCollects().getCounts() > comments.size()) {
//                // 需要分页加载，
//                viewHolder.tvLoadMore.setVisibility(View.VISIBLE);
//                viewHolder.tvLoadMore.setOnClickListener(v -> {
//                    loadCommentsNextPage(vh.tvLoadMore, message.getDynamic().get, adapter);
//                });
            }
        }

        // 赞与评论之间的横线，两者都有才显示
        if (praises != null && praises.size() > 0 && comments != null && comments.size() > 0) {
            viewHolder.line_v.setVisibility(View.VISIBLE);
        } else {
            viewHolder.line_v.setVisibility(View.INVISIBLE);
        }

/*
        mAdapter = (CommentAdapter) viewHolder.command_listView.getAdapter();
        if (mAdapter == null) {
            mAdapter = new CommentAdapter();
            viewHolder.command_listView.setAdapter(mAdapter);
        }

        if (comments != null && comments.size() > 0) {
            viewHolder.line_v.setVisibility(View.VISIBLE);
            viewHolder.command_listView.setVisibility(View.VISIBLE);
        } else {
            viewHolder.line_v.setVisibility(View.GONE);
            viewHolder.command_listView.setVisibility(View.GONE);
        }
        mAdapter.setData(position, comments);
*/

//        if (!TextUtils.isEmpty(message.getLocation())) {
//            viewHolder.location_tv.setText(message.getLocation());
//            viewHolder.location_tv.setVisibility(View.VISIBLE);
//        } else {
//            viewHolder.location_tv.setVisibility(View.GONE);
//        }

        viewHolder.location_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(mContext, MapActivity.class);
//                intent.putExtra("latitude", message.getLatitude());
//                intent.putExtra("longitude", message.getLongitude());
//                intent.putExtra("userName", message.getLocation());
//                mContext.startActivity(intent);
            }
        });

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
            ImageView icon_play = ((NormalSingleImageHolder) viewHolder).icon_play;
            DynamicBean.AttachBean att = message.getDynamic().getAttach_images().get(0);

            String url = message.getDynamic().getAttach().get(0).getUrl();

            icon_play.setVisibility(View.GONE);
            if(att.getType().equals("video")){
                url = att.getThumb();
                icon_play.setVisibility(View.VISIBLE);

            }

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
                image_view.setOnClickListener(new SingleImageClickListener(att));
                image_view.setVisibility(View.VISIBLE);
            } else {
                image_view.setImageBitmap(null);
                image_view.setVisibility(View.GONE);
            }
        } else if (viewType == VIEW_TYPE_NORMAL_MULTI_IMAGE) {
            MyGridView grid_view = ((NormalMultiImageHolder) viewHolder).grid_view;


            if (body.getAttach() != null) {

                if(body.getAttach_images().size() == 2 || body.getAttach_images().size() == 4){
                    grid_view.setNumColumns(2);
                }else{
                    grid_view.setNumColumns(3);
                }
                grid_view.setAdapter(new DynamicImageGridViewAdapter(mContext, body.getAttach_images()));
                grid_view.setOnItemClickListener(new MultipleImagesClickListener(body.getAttach_images()));
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
            NormalVideoHolder holder = (NormalVideoHolder) viewHolder;
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
    }

    @Override
    public int getItemCount() {
        return (contentType == 8 || contentType == 14 || contentType == 15) ? mMessages.size() + 1 : mMessages.size();
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


        if(contentType == 8){

            if(position == 0){
                return VIEW_TYPE_HOME_INFO;
            }else {
                position = position - 1;
            }

        }

        if(contentType == 14){

            if(position == 0){
                return VIEW_TYPE_USER_HOME_INFO;
            }else {
                position = position - 1;
            }

        }

        if(contentType == 15){

            if(position == 0){
                return VIEW_TYPE_DYNAMIC_SEARCH;
            }else {
                position = position - 1;
            }

        }
        OORTDynamic message = mMessages.get(position);
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
        if (body.getAttach_images() == null && body.getAttach_images().size() == 0) {
            // 文本视图
            return VIEW_TYPE_NORMAL_TEXT;
        } else if (body.getAttach_images().size() > 0) {
            if (body.getAttach_images().size() <= 1) {
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
        final OORTDynamic message = mMessages.get(position);
        if (message == null) {
            return;
        }
//        HashMap<String, String> params = new HashMap<String, String>();
//        params.put("access_token", IMUserInfoUtil.getInstance().getToken());
//        params.put("messageId", message.getDynamic().getOort_duuid());
//        DialogHelper.showDefaulteMessageProgressDialog((Activity) mContext);
//
//        HttpUtils.get().url(CoreManager.requireConfig(MyApplication.getInstance()).CIRCLE_MSG_DELETE)
//                .params(params)
//                .build()
//                .execute(new BaseCallback<Void>(Void.class) {
//
//                    @Override
//                    public void onResponse(ObjectResult<Void> result) {
//                        DialogHelper.dismissProgressDialog();
//                        if (Result.checkSuccess(mContext, result)) {
//                            CircleMessageDao.getInstance().deleteMessage(message.getMessageId());// 删除数据库的记录（如果存在的话）
//                            mMessages.remove(position);
//                            notifyDataSetChanged();
//
//                            // 删除成功，停止正在播放的视频、音频
//                            JCVideoPlayer.releaseAllVideos();
//                            stopVoice();
//                        }
//                    }
//
//                    @Override
//                    public void onError(Call call, Exception e) {
//                        DialogHelper.dismissProgressDialog();
//                        ToastUtil.showErrorNet(mContext);
//                    }
//                });
    }

    public void deleteCollection(final int position) {
//        final PublicMessage message = mMessages.get(position);
//        if (message == null) {
//            return;
//        }
//        HashMap<String, String> params = new HashMap<String, String>();
//        params.put("access_token", IMUserInfoUtil.getInstance().getToken());
//        params.put("emojiId", message.getEmojiId());
//        DialogHelper.showDefaulteMessageProgressDialog((Activity) mContext);
//
//        HttpUtils.get().url(CoreManager.requireConfig(MyApplication.getInstance()).Collection_REMOVE)
//                .params(params)
//                .build()
//                .execute(new BaseCallback<Collectiion>(Collectiion.class) {
//
//                    @Override
//                    public void onResponse(ObjectResult<Collectiion> result) {
//                        DialogHelper.dismissProgressDialog();
//                        if (Result.checkSuccess(mContext, result)) {
//                            mMessages.remove(position);
//                            notifyDataSetChanged();
//                        }
//                    }
//
//                    @Override
//                    public void onError(Call call, Exception e) {
//                        DialogHelper.dismissProgressDialog();
//                        ToastUtil.showErrorNet(mContext);
//                    }
//                });
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
        final OORTDynamic message = mMessages.get(messagePosition);
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
                        deleteComment(message, messagePosition, comment.getUuid(), comments, commentPosition, adapter);
                        break;
                }
            }
        }).setCancelable(true).create().show();
    }

    /**
     * 删除一条回复
     */
    private void deleteComment(OORTDynamic message, int messagePosition, String commentId, final List<DynamicBean.CommentsBean.ListBean> comments,
                               final int commentPosition, final CommentAdapter adapter) {



        String mToken = FastSharedPreferences.get("USERINFO_SAVE").getString("token","");

        HttpRequestParam.dynamic_del_comment(mToken,commentId).subscribe(new RxBus.BusObserver<String>() {
            @Override
            public void onNext(String s) {
                Res res = JSON.parseObject(s,Res.class);//
                if(res.getCode() == 200){
                    refreshPostionForOper(messagePosition,adapter.getmVh());
                }

            }

            @Override
            public void onError(Throwable e) {
                Log.v("msg" , e.toString());
            }
        });



//        String messageId = message.getDynamic().getOort_duuid();
//        Map<String, String> params = new HashMap<>();
//        params.put("access_token", IMUserInfoUtil.getInstance().getToken());
//        params.put("messageId", messageId);
//        params.put("commentId", commentId);
//        HttpUtils.get().url(CoreManager.requireConfig(MyApplication.getInstance()).MSG_COMMENT_DELETE)
//                .params(params)
//                .build()
//                .execute(new BaseCallback<Void>(Void.class) {
//
//                    @Override
//                    public void onResponse(ObjectResult<Void> result) {
//                        if (Result.checkSuccess(mContext, result)) {
////                            message.getDynamic().(message.getCommnet() - 1);
////                            comments.remove(commentPosition);
////                            notifyItemChanged(messagePosition);
//                        }
//                    }
//
//                    @Override
//                    public void onError(Call call, Exception e) {
//                        ToastUtil.showErrorNet(mContext);
//                    }
//                });
    }

    public void onPraise(int messagePosition, boolean isPraise,ViewHolder vh) {
        praiseOrCancel(messagePosition, isPraise,vh);
    }

    public void onComment(int messagePosition, ListView view,ViewHolder vh) {
        OORTDynamic message = mMessages.get(messagePosition);
        showCommentEnterView(messagePosition, null, null, null,vh);

        //        if (message != null
//                && message.getIsAllowComment() == 1
//                && !TextUtils.equals(message.getUserId(), mLoginUserId)) {
//            Toast.makeText(mContext, MyApplication.getContext().getString(R.string.ban_comment), Toast.LENGTH_SHORT).show();
//            return;
//        }
//        if (mContext instanceof BusinessCircleActivity) {
//            ((BusinessCircleActivity) mContext).showCommentEnterView(messagePosition, null, null, null);
//        } else {
//            String path = "";
//            if (message.getType() == 3) {
//                //语音
//                path = message.getFirstAudio();
//            } else if (message.getType() == 2) {
//                //图片
//                path = message.getFirstImageOriginal();
//            } else if (message.getType() == 6) {
//                //视频
//                path = message.getFirstVideo();
//            }
//            view.setTag(message);
//            EventBus.getDefault().post(new MessageEventComment("Comment", message.getMessageId(), message.getIsAllowComment(),
//                    message.getType(), path, message, view));
//        }
    }

    private <T> T firstOrNull(List<T> list) {
        if (list == null) {
            return null;
        }
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    private String collectionParam(PublicMessage message) {
        com.alibaba.fastjson.JSONArray array = new com.alibaba.fastjson.JSONArray();
        com.alibaba.fastjson.JSONObject json = new com.alibaba.fastjson.JSONObject();
        int type = message.getCollectionType();
        String msg = "";
        String collectContent = "";
        String fileName = "";
        long fileLength = 0;
        long fileSize = 0;
        String id = message.getMessageId();
        PublicMessage.Resource res = null;
        if (message.getBody() != null) {
            collectContent = message.getBody().getText();
            switch (type) {
                case CollectionEvery.TYPE_TEXT:
                    msg = message.getBody().getText();
                    break;
                case CollectionEvery.TYPE_IMAGE:
                    List<PublicMessage.Resource> images = message.getBody().getImages();
                    // 莫名出现类型为图片，但是没有图片的朋友圈消息，略做兼容，
                    if (images == null || images.isEmpty()) {
                        type = CollectionEvery.TYPE_TEXT;
                        msg = message.getBody().getText();
                        break;
                    }
                    StringBuilder sb = new StringBuilder();
                    boolean firstTime = true;
                    for (PublicMessage.Resource token : images) {
                        String url = token.getOriginalUrl();
                        if (TextUtils.isEmpty(url)) {
                            continue;
                        }
                        if (firstTime) {
                            firstTime = false;
                        } else {
                            sb.append(',');
                        }
                        sb.append(url);
                    }
                    msg = sb.toString();
                    break;
                case CollectionEvery.TYPE_FILE:
                    res = firstOrNull(message.getBody().getFiles());
                    break;
                case CollectionEvery.TYPE_VIDEO:
                    res = firstOrNull(message.getBody().getVideos());
                    break;
                case CollectionEvery.TYPE_VOICE:
                    res = firstOrNull(message.getBody().getAudios());
                    break;
                case CollectionEvery.TYPE_LINK:
                    firstOrNull(message.getBody().getSdkUrls());
                    break;
                default:
                    throw new IllegalStateException("类型<" + type + ">不存在，");
            }
        }

        if (res != null) {
            if (!TextUtils.isEmpty(res.getOriginalUrl())) {
                msg = res.getOriginalUrl();
            }
            fileLength = res.getLength();
            fileSize = res.getSize();
        }
        if (!TextUtils.isEmpty(message.getFileName())) {
            fileName = message.getFileName();
        }

        json.put("type", String.valueOf(type));
        json.put("msg", msg);
        json.put("fileName", fileName);
        json.put("fileSize", fileSize);
        json.put("fileLength", fileLength);
        json.put("collectContent", collectContent);
        json.put("collectType", 1);
        json.put("collectMsgId", id);
        array.add(json);
        return JSON.toJSONString(array);
    }

    private void onCollection(final int messagePosition,ViewHolder vh) {
        OORTDynamic message = mMessages.get(messagePosition);

        String mToken = FastSharedPreferences.get("USERINFO_SAVE").getString("token","");

        HttpRequestParam.dynamic_collect(mToken,message.getDynamic().getCollects().getIs_collect() == 1 ? 1 : 0,message.getDynamic().getOort_duuid()).subscribe(new RxBus.BusObserver<String>() {
            @Override
            public void onNext(String s) {
                Res res = JSON.parseObject(s,Res.class);//
                if(res.getCode() == 200){
                    refreshPostionForOper(messagePosition,vh);
                }

            }

            @Override
            public void onError(Throwable e) {
                Log.v("msg" , e.toString());
            }
        });


    }

    public void onReport(final int messagePosition,View v,ViewHolder vh) {

        OORTDynamic dynamic = mMessages.get(messagePosition);

        String mToken = FastSharedPreferences.get("USERINFO_SAVE").getString("token","");

        View popView = ((Activity)(mContext)).getLayoutInflater().inflate(R.layout.popup_dynamic_list_item_oper, null);


        int height = 205;

        if(dynamic.getDynamic().getOort_userid().equals(UserInfoUtils.getInstance(mContext).getUserId())){

        }else{
            height = height - 41;
            ((FrameLayout)(popView.findViewById(R.id.btn_del).getParent())).setVisibility(View.GONE);
        }
        if(UserInfoUtils.getInstance(mContext).getLoginUserInfo().getOort_isadmin() == 1 || (dynamic.getDynamic().getHad_auth() == 1)){//  && !dynamic.getDynamic().getOort_userid().equals(UserInfoUtils.getInstance(mContext).getUserId())
            TextView tv_jinhua = popView.findViewById(R.id.tv_jinhua);
            TextView tv_baijin =  popView.findViewById(R.id.tv_baijin);
            TextView tv_top = popView.findViewById(R.id.tv_top);

            String title_bajin = mContext.getString(R.string.dyn_set_platinum);
            if(dynamic.getDynamic().getOort_grade2() == 1){
                title_bajin = mContext.getString(R.string.dyn_cancel_platinum);
            }
            tv_baijin.setText(title_bajin);

            String title_jinhua = mContext.getString(R.string.dyn_set_cream);
            if(dynamic.getDynamic().getOort_grade1() == 1){
                title_jinhua = mContext.getString(R.string.dyn_cancel_cream);
            }
            tv_jinhua.setText(title_jinhua);

            String title_top = mContext.getString(R.string.dyn_set_top);
            if(dynamic.getDynamic().getOort_top() > 0){
                title_top = mContext.getString(R.string.dyn_cancel_top);
            }
            tv_top.setText(title_top);
        }else{
            ((FrameLayout)(popView.findViewById(R.id.btn_jinhua).getParent())).setVisibility(View.GONE);
            ((FrameLayout)(popView.findViewById(R.id.btn_baijin).getParent())).setVisibility(View.GONE);
            ((FrameLayout)(popView.findViewById(R.id.btn_top).getParent())).setVisibility(View.GONE);
            height = height - 3*41;

        }



        new TransformersTip(v, popView) {
            @Override
            protected void initView(View contentView) {
                popView.findViewById(R.id.btn_share).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //mPop.dismiss();
                        dismissTip();


                        //oortSharePlugin  oort_message   [type,args...]
//  title 标题
//  url 链接， 'oort_duuid=' + this.item.oort_duuid,
//  text 内容
//  path 图片链接或者视频链接或者音频 或者附件
//  type 代表path的类型  image:图片 video:视频 audio:音频 attach:附件
                        JSONObject obj = new JSONObject();


                        try {

                            obj.put("type","dynamic_android");
                            obj.put("title",dynamic.getUserInfo(dynamic.getDynamic().getOort_userid()).getOort_name() + mContext.getString(R.string.moment_of));
                            obj.put("sub",dynamic.getDynamic().getContent());
                            obj.put("url","");

                            obj.put("duuid",dynamic.getDynamic().getOort_duuid());

                            if(dynamic.getDynamic().getAttach_images().size() > 0){

                                DynamicBean.AttachBean b = dynamic.getDynamic().getAttach_images().get(0);
                                obj.put("atttype",b.getType());

                                if(b.getType().equals("video")){
                                    obj.put("img",b.getThumb());
                                }else{
                                    obj.put("img",b.getUrl());
                                }
                            }else if(dynamic.getDynamic().getAttach_audios().size() > 0){
                                DynamicBean.AttachBean b = dynamic.getDynamic().getAttach_audios().get(0);
                                obj.put("atttype",b.getType());
                                obj.put("img",b.getUrl());
                                obj.put("attachName",b.getName());

                            }else if(dynamic.getDynamic().getAttach_atts().size() > 0){
                                DynamicBean.AttachBean b = dynamic.getDynamic().getAttach_atts().get(0);
                                obj.put("atttype",b.getType());
                                obj.put("img",b.getUrl());
                                obj.put("attachName",b.getName());
                            }else{
                                obj.put("atttype","text");
                            }


                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }




//ShareNearChatFriend

                        Context mContext = AppStoreInit.getInstance().getApplication();
                        String appid = mContext.getApplicationInfo().processName;
                        Intent in = new Intent(appid + ".shareFriend");
                        in.putExtra("action","shareFriend");
                        in.putExtra("content",obj.toString());
                        in.addFlags(FLAG_ACTIVITY_NEW_TASK);

                        mContext.startActivity(in);

                    }
                });
                popView.findViewById(R.id.btn_jinhua).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //mPop.dismiss();
                        dismissTip();

                        boolean isgrade2 = dynamic.getDynamic().getOort_grade1() == 1;
                        HttpRequestParam.dynamic_grade1(mToken,dynamic.getDynamic().getOort_duuid(),isgrade2 ? 2 : 1).subscribe(new RxBus.BusObserver<String>() {
                            @Override
                            public void onNext(String s) {
                                Res res = JSON.parseObject(s,Res.class);//
                                if(res.getCode() == 200){
                                    if(contentType == 5){
                                        mMessages.remove(messagePosition);
                                        notifyDataSetChanged();
                                    }else {
                                        //refreshPostion(messagePosition);

                                        refreshPostionForOper(messagePosition,vh);
                                    }
                                }

                                XToast.success(mContext,res.getMsg());

                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.v("msg" , e.toString());

                                XToast.success(mContext,e.toString());
                            }
                        });


                    }
                });
                popView.findViewById(R.id.btn_baijin).setOnClickListener(new View.OnClickListener() {
                                                                             @Override
                                                                             public void onClick(View v) {
                                                                                 //mPop.dismiss();
                                                                                 dismissTip();
                                                                                 boolean isgrade1 = dynamic.getDynamic().getOort_grade2() == 1;
                                                                                 HttpRequestParam.dynamic_grade2(mToken,dynamic.getDynamic().getOort_duuid(),isgrade1 ? 2 : 1).subscribe(new RxBus.BusObserver<String>() {
                                                                                     @Override
                                                                                     public void onNext(String s) {
                                                                                         Res res = JSON.parseObject(s,Res.class);//
                                                                                         if(res.getCode() == 200){

                                                                                             if(contentType == 4){
                                                                                                 mMessages.remove(messagePosition);
                                                                                                 notifyDataSetChanged();
                                                                                             }else {
                                                                                                 refreshPostionForOper(messagePosition,vh);
                                                                                             }
                                                                                         }

                                                                                         XToast.success(mContext,res.getMsg());

                                                                                     }

                                                                                     @Override
                                                                                     public void onError(Throwable e) {
                                                                                         Log.v("msg" , e.toString());

                                                                                         XToast.success(mContext,e.toString());
                                                                                     }
                                                                                 });


                                                                             }
                                                                         }


                );

                popView.findViewById(R.id.btn_top).setOnClickListener(new View.OnClickListener() {
                                                                          @Override
                                                                          public void onClick(View v) {
                                                                              //mPop.dismiss();
                                                                              dismissTip();


                                                                              if(dynamic.getDynamic().getOort_top()  ==  0){

                                                                                  DynamicSetTopEvent ev = new DynamicSetTopEvent();
                                                                                  ev.type = contentType;
                                                                                  ev.show = true;
                                                                                  ev.callback = new DynamicSetTopEvent.SetCallback() {
                                                                                      @Override
                                                                                      public void setCallback(int top) {
                                                                                          HttpRequestParam.dynamic_top(mToken,dynamic.getDynamic().getOort_duuid(),top).subscribe(new RxBus.BusObserver<String>() {
                                                                                              @Override
                                                                                              public void onNext(String s) {
                                                                                                  Res res = JSON.parseObject(s,Res.class);//
                                                                                                  if(res.getCode() == 200){
                                                                                                      refreshPostionForOper(messagePosition,vh);
                                                                                                  }

                                                                                                  XToast.success(mContext,res.getMsg());

                                                                                              }

                                                                                              @Override
                                                                                              public void onError(Throwable e) {
                                                                                                  Log.v("msg" , e.toString());

                                                                                                  XToast.success(mContext,e.toString());
                                                                                              }
                                                                                          });

                                                                                      }
                                                                                  };
                                                                                  EventBus.getDefault().post(ev);

                                                                                  return;
                                                                              }
                                                                              HttpRequestParam.dynamic_top(mToken,dynamic.getDynamic().getOort_duuid(),0).subscribe(new RxBus.BusObserver<String>() {
                                                                                  @Override
                                                                                  public void onNext(String s) {
                                                                                      Res res = JSON.parseObject(s,Res.class);//
                                                                                      if(res.getCode() == 200){
                                                                                          refreshPostionForOper(messagePosition,vh);
                                                                                      }

                                                                                      XToast.success(mContext,res.getMsg());

                                                                                  }

                                                                                  @Override
                                                                                  public void onError(Throwable e) {
                                                                                      Log.v("msg" , e.toString());

                                                                                      XToast.success(mContext,e.toString());
                                                                                  }
                                                                              });

                                                                          }
                                                                      }


                );


                popView.findViewById(R.id.btn_del).setOnClickListener(new View.OnClickListener() {
                                                                          @Override
                                                                          public void onClick(View v) {
                                                                              //mPop.dismiss();
                                                                              dismissTip();

                                                                              DialogLoader.getInstance().showConfirmDialog(
                                                                                      mContext,
                                                                                      mContext.getString(com.oortcloud.appstore.R.string.del_or_not)+ mContext.getString(R.string.moments),
                                                                                      mContext.getString(com.oortcloud.appstore.R.string.dialog_ok),
                                                                                      (dialog, which) -> {
                                                                                          HttpRequestParam.dynamic_del(mToken,dynamic.getDynamic().getOort_duuid()).subscribe(new RxBus.BusObserver<String>() {
                                                                                              @Override
                                                                                              public void onNext(String s) {
                                                                                                  Res res = JSON.parseObject(s,Res.class);//
                                                                                                  if(res.getCode() == 200){
                                                                                                      mMessages.remove(messagePosition);
                                                                                                      notifyDataSetChanged();

                                                                                                      EventBus.getDefault().post(new DyamicListChangeEvent());

                                                                                                      if(contentType == 10){
                                                                                                          ((Activity)mContext).finish();
                                                                                                      }
                                                                                                  }
                                                                                                  XToast.success(mContext,res.getMsg());
                                                                                              }
                                                                                              @Override
                                                                                              public void onError(Throwable e) {
                                                                                                  Log.v("msg" , e.toString());
                                                                                                  XToast.success(mContext,e.toString());
                                                                                              }
                                                                                          });
                                                                                          dialog.dismiss();
                                                                                      },
                                                                                      mContext.getString(com.oortcloud.appstore.R.string.base_cancel),
                                                                                      (dialog, which) -> {
                                                                                          dialog.dismiss();
                                                                                      }
                                                                              );



                                                                          }
                                                                      }


                );
            }
        }
                .setArrowGravity(ArrowGravity.TO_TOP_CENTER) // 设置箭头相对于浮窗的位置
                .setBgColor(Color.WHITE) // 设置背景色
                .setShadowColor(Color.parseColor("#33000000")) // 设置阴影色
                .setArrowHeightDp(6) // 设置箭头高度
                .setRadiusDp(4) // 设置浮窗圆角半径
                .setArrowOffsetXDp(40) // 设置箭头在 x 轴的偏移量
                .setArrowOffsetYDp(0) // 设置箭头在 y 轴的偏移量
                .setShadowSizeDp(6) // 设置阴影宽度

                .setTipGravity(TipGravity.TO_BOTTOM_CENTER) // 设置浮窗相对于锚点控件展示的位置
                .setTipOffsetXDp(20) // 设置浮窗在 x 轴的偏移量
                .setTipOffsetYDp(6) // 设置浮窗在 y 轴的偏移量

                .setBackgroundDimEnabled(false) // 设置是否允许浮窗的背景变暗
                .setDismissOnTouchOutside(true) // 设置点击浮窗外部时是否自动关闭浮窗

                .show(); // 显示浮窗


//        mPop = new PopupWindow(popView, WRAP_CONTENT, ViewTool.dp2px(mContext, height));//ViewTool.dp2px(mContext, 110)
//        mPop.setOutsideTouchable(false);
//        mPop.setFocusable(true);
//        mPop.showAsDropDown(v,-30,0);
//        ReportDialog mReportDialog = new ReportDialog(mContext, false, new ReportDialog.OnReportListItemClickListener() {
//            @Override
//            public void onReportItemClick(Report report) {
//                report(messagePosition, report);
//            }
//        });
//        mReportDialog.show();

        //new DiscoverDialog(mContext ,  mMessages.get(messagePosition)).show();
    }

    /**
     * 赞 || 取消赞
     */
    private void praiseOrCancel(final int position, final boolean isPraise,ViewHolder vh) {
        final OORTDynamic message = mMessages.get(position);
        if (message == null) {
            return;
        }



        String mToken = FastSharedPreferences.get("USERINFO_SAVE").getString("token","");
        HttpRequestParam.dynamic_like(mToken,isPraise ? 0 : 1,message.getDynamic().getOort_duuid()).subscribe(new RxBus.BusObserver<String>() {
            @Override
            public void onNext(String s) {
                Res res = JSON.parseObject(s,Res.class);//
                if(res.getCode() == 200){
                    refreshPostionForOper(position,vh);
                }

            }

            @Override
            public void onError(Throwable e) {
                Log.v("msg" , e.toString());
            }
        });


    }

    private void refreshPostionForOper(int pos, ViewHolder vh){

        OORTDynamic message0 = mMessages.get(pos);
        String mToken = FastSharedPreferences.get("USERINFO_SAVE").getString("token","");
        HttpRequestParam.dynamic_info(mToken,message0.getDynamic().getOort_duuid()).subscribe(new RxBus.BusObserver<String>() {
            @Override
            public void onNext(String s) {
                ResObj<OORTDynamic> res = JSON.parseObject(s,new TypeToken<ResObj<OORTDynamic>>() {}.getType());//
                if(res.getCode() == 200 && res.getData() != null){

                    mMessages.set(pos,res.getData());
                    //notifyDataSetChanged();
                    OORTDynamic message = res.getData();

                    vh.ivThumb.setChecked(1 == message.getDynamic().getLikes().getIs_like());
                    vh.tvThumb.setText(String.valueOf(message.getDynamic().getLikes().getCounts()));


                    boolean isComment = false;
                    if (message.getDynamic().getComments() != null) {
                        for (DynamicBean.CommentsBean.ListBean comment : message.getDynamic().getComments().getList()) {
                            if (mLoginUserId.equals(comment.getUserid())) {
                                isComment = true;
                            }
                        }
                    }
                    vh.ivComment.setChecked(isComment);
                    vh.tvComment.setText(String.valueOf(message.getDynamic().getComments().getCounts()));

                    vh.ivCollection.setChecked(1 == message.getDynamic().getCollects().getIs_collect());
                    vh.tvCollection.setText(String.valueOf(message.getDynamic().getCollects().getCounts()));


                    List<DynamicBean.LikesBean.ListBeanX> praises = message.getDynamic().getLikes().getList();
                    if (praises != null && praises.size() > 0) {
                        vh.multi_praise_tv.setVisibility(View.VISIBLE);
                        vh.iv_prise.setVisibility(View.VISIBLE);
                        SpannableStringBuilder builder = new SpannableStringBuilder();
                        for (int i = 0; i < praises.size(); i++) {

                            DynamicBean.LikesBean.ListBeanX praise = praises.get(i);
                            UserInfoBean userInfo = message.getUserInfo(praise.getUserid());
                            String praiseName = getShowName(userInfo.getOort_uuid(), userInfo.getOort_name());
                            UserClickableSpan.setClickableSpan(mContext, builder, praiseName, userInfo.getOort_uuid());
                            if (i < praises.size() - 1)
                                builder.append(",");
                        }
                        if (message.getDynamic().getLikes().getCounts() > praises.size()) {
                            builder.append(mContext.getString(R.string.praise_ending_place_holder, message.getDynamic().getLikes().getCounts()));
                        }
                        vh.multi_praise_tv.setText(builder);
                    } else {
                        vh.iv_prise.setVisibility(View.GONE);
                        vh.multi_praise_tv.setVisibility(View.GONE);
                        vh.multi_praise_tv.setText("");
                    }

                    final List<DynamicBean.CommentsBean.ListBean> comments = message.getDynamic().getComments().getList();
                    vh.command_listView.setVisibility(View.VISIBLE);
                    CommentAdapter adapter = new CommentAdapter(pos, comments,message,vh);
                    vh.command_listView.setAdapter(adapter);




                    vh.tv_baijin_flag.setVisibility(message.getDynamic().getOort_grade2() == 1 ? View.VISIBLE : View.GONE);
                    vh.tv_jinghua_flag.setVisibility(message.getDynamic().getOort_grade1() == 1 ? View.VISIBLE : View.GONE);
                    vh.tv_settop_flag.setVisibility(message.getDynamic().getOort_top() > 0 ? View.VISIBLE : View.GONE);

                    List<DynamicBean.AttachBean> atts = new ArrayList<DynamicBean.AttachBean>();
                    atts.addAll(message.getDynamic().getAttach_audios());
                    atts.addAll(message.getDynamic().getAttach_atts());

                    AttAdpter attadp = new AttAdpter(mContext);
                    vh.rv_atts.setAdapter(attadp);

                    if(atts.size() > 0){
                        attadp.setData(atts);
                    }



                    String [] flags = {mContext.getString(R.string.set_top9),mContext.getString(R.string.set_top8),mContext.getString(R.string.set_top7),mContext.getString(R.string.set_top6),mContext.getString(R.string.set_top5),mContext.getString(R.string.set_top4),mContext.getString(R.string.set_top3),mContext.getString(R.string.set_top2),mContext.getString(R.string.set_top1)};

                    if(message.getDynamic().getOort_top() > 0) {
                        if(message.getDynamic().getOort_top() < 9) {
                            vh.tv_settop_flag.setText(flags[message.getDynamic().getOort_top()]);
                        }else {
                            vh.tv_settop_flag.setText(mContext.getString(R.string.set_top));
                        }
                    }

                }

            }

            @Override
            public void onError(Throwable e) {
                Log.v("msg" , e.toString());
            }
        });

    }


    private void refreshPostion(int pos){

        OORTDynamic message = mMessages.get(pos);
        String mToken = FastSharedPreferences.get("USERINFO_SAVE").getString("token","");
        HttpRequestParam.dynamic_info(mToken,message.getDynamic().getOort_duuid()).subscribe(new RxBus.BusObserver<String>() {
            @Override
            public void onNext(String s) {
                ResObj<OORTDynamic> res = JSON.parseObject(s,new TypeToken<ResObj<OORTDynamic>>() {}.getType());//
                if(res.getCode() == 200 && res.getData() != null){

                    mMessages.set(pos,res.getData());
                    notifyDataSetChanged();
                }

            }

            @Override
            public void onError(Throwable e) {
                Log.v("msg" , e.toString());
            }
        });
    }


    private void getDetail(int pos){

        final OORTDynamic message = mMessages.get(pos);
        if (message == null) {
            return;
        }

        String mToken = FastSharedPreferences.get("USERINFO_SAVE").getString("token","");
//        HttpRequestParam.GZDTDetail(mToken,message.getDynamic().getOort_duuid()).subscribe(new RxBus.BusObserver<String>() {
//            @Override
//            public void onNext(String s) {
//                ResObj<OORTDynamic> res = JSON.parseObject(s,Res.class);//
//                if(res.getCode() == 200 && res.getData() != null && res.getData().getList() != null){
//
//                }
//
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                Log.v("msg" , e.toString());
//            }
//        });
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
    public void setData(List<OORTDynamic> mMessages) {
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
        void onItemClick(DynamicListAdapter.ViewHolder vh);
    }

    public interface OnDongTaiItemClickListener {
        void onItemClick();
    }

    public static class SearchViewHolder extends ViewHolder {
        EditText etSearchInput;
        TextView tvSearchButton;
        OnSearchListener mSearchListener;



        SearchViewHolder(@NonNull View itemView, OnSearchListener searchListener) {
            super(itemView);
            etSearchInput = itemView.findViewById(R.id.et_search_input);
            tvSearchButton = itemView.findViewById(R.id.tv_search_button);
            mSearchListener = searchListener;
        }

        void bind() {
            etSearchInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // filter(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            tvSearchButton.setOnClickListener(v -> {
                String query = etSearchInput.getText().toString().trim();

                if(mSearchListener != null){
                    mSearchListener.onSearch(query);
                }

                //filter(query);
            });
        }
    }


    public static class HeadViewHolder extends ViewHolder {

        ImageView icon_banner;
        ImageView icon_header;
        TextView tv_name;
        TextView tv_depart;
        TextView tv_fans_count;
        TextView tv_attend_count;
        TextView tv_dongtai_cout;
        TextView tv_jinghua_count;
        TextView tv_baijin_cout;
        TextView tv_like_count;
        TextView tv_comment_count;
        TextView tv_collect_count;



        LinearLayout ll_fans;
        LinearLayout ll_attend;
        LinearLayout ll_dongtai;
        LinearLayout ll_jinghua;
        LinearLayout ll_baijin;
        LinearLayout ll_like;
        LinearLayout ll_comment;
        LinearLayout ll_collect;


        Button btn_follow;


        public HeadViewHolder(@NonNull View itemView) {
            super(itemView);
            icon_banner = itemView.findViewById(R.id.iv_banner);
            icon_header = itemView.findViewById(R.id.iv_user_header);
            tv_name = itemView.findViewById(R.id.tv_user_name);
            tv_depart = itemView.findViewById(R.id.tv_user_depart);
            tv_fans_count = itemView.findViewById(R.id.tv_fans_count);
            tv_attend_count = itemView.findViewById(R.id.tv_attend_count);
            tv_dongtai_cout = itemView.findViewById(R.id.tv_dongtai_count);
            tv_jinghua_count = itemView.findViewById(R.id.tv_jinghua_count);
            tv_baijin_cout = itemView.findViewById(R.id.tv_baijin_count);
            tv_like_count = itemView.findViewById(R.id.tv_like_count);
            tv_comment_count = itemView.findViewById(R.id.tv_comment_count);
            tv_collect_count = itemView.findViewById(R.id.tv_collect_count);

            ll_fans = itemView.findViewById(R.id.ll_fans);
            ll_attend = itemView.findViewById(R.id.ll_attend);
            ll_dongtai = itemView.findViewById(R.id.ll_dongtai);
            ll_jinghua = itemView.findViewById(R.id.ll_jinghua);
            ll_baijin = itemView.findViewById(R.id.ll_baijin);
            ll_like = itemView.findViewById(R.id.ll_like);
            ll_comment = itemView.findViewById(R.id.ll_comment);
            ll_collect = itemView.findViewById(R.id.ll_collect);


            btn_follow = itemView.findViewById(R.id.btn_follow);
        }
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar_img;
        TextView nick_name_tv;
        TextView time_tv;
        HttpTextView body_tv;
        //  HttpTextView body_tvS;
        TextView open_tv;
        FrameLayout content_fl;

        FlowTagLayout tagLayout;
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
        TextView tvCollection;
        CheckableImageView ivCollection;
        View llReport;
        CheckableImageView ivReport;
        ImageView iv_prise;

        TextView tv_dept;
        TextView tv_baijin_flag;
        TextView tv_jinghua_flag;
        TextView tv_settop_flag;

        RecyclerView rv_atts;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar_img = (ImageView) itemView.findViewById(R.id.avatar_img);
            nick_name_tv = (TextView) itemView.findViewById(R.id.nick_name_tv);

            tv_dept = (TextView) itemView.findViewById(R.id.tv_dept);
            tv_baijin_flag = (TextView) itemView.findViewById(R.id.tv_baijing_flag);
            tv_jinghua_flag = (TextView) itemView.findViewById(R.id.tv_jinghua_flag);
            tv_settop_flag = (TextView) itemView.findViewById(R.id.tv_settop_flag);
            time_tv = (TextView) itemView.findViewById(R.id.time_tv);
            body_tv = itemView.findViewById(R.id.body_tv);
            // body_tvS = itemView.findViewById(R.id.body_tvS);

            tagLayout = itemView.findViewById(R.id.tagLayout);
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
            tvCollection = itemView.findViewById(R.id.tvCollect);
            llReport = itemView.findViewById(R.id.llReport);
            ivReport = itemView.findViewById(R.id.ivReport);
            rv_atts = itemView.findViewById(R.id.rv_att);
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
        ImageView icon_play;

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

        public ViewHolder getmVh() {
            return mVh;
        }

        private ViewHolder mVh;
        private List<DynamicBean.CommentsBean.ListBean> datas;

        CommentAdapter(int messagePosition, List<DynamicBean.CommentsBean.ListBean> data,OORTDynamic dynamic,ViewHolder vh) {
            this.messagePosition = messagePosition;
            mDynamic = dynamic;
            mVh = vh;
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
            commentBody = commentBody.replace("\n\n","\n");
            if(commentBody.endsWith("\n\n")){
                commentBody = commentBody.replace("\n\n","");
            }
            if (!TextUtils.isEmpty(commentBody)) {
                commentBody = StringUtils.replaceSpecialChar(commentBody);
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
                            showCommentEnterView(messagePosition, comment.getUserid(), userInfo.getOort_name(), toShowName,mVh);
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

    public void showCommentEnterView(int messagePosition, String toUserId, String toNickname, String toShowName,ViewHolder vh) {

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



                final OORTDynamic message = mMessages.get(messagePosition);
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
                                // refreshPostion(messagePosition);

                                refreshPostionForOper(messagePosition,vh);
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
                            //refreshPostion(messagePosition,vh);
                            refreshPostionForOper(messagePosition,vh);
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

    public static class CommentReplyCache {
        public int messagePosition;// 消息的Position
        public String toUserId;
        public String toNickname;
        public String text;
    }
    private class SingleImageClickListener implements View.OnClickListener {
        private String url;
        DynamicBean.AttachBean mAtt;

        SingleImageClickListener(String url) {
            this.url = url;
        }

        SingleImageClickListener(DynamicBean.AttachBean att) {
            this.mAtt = att;
            this.url = att.getUrl();
        }

        @Override
        public void onClick(View v) {

            if(mAtt != null) {
                if(mAtt.getType().equals("video")){
                    DynamicActivityPlayVideo.start(mContext, mAtt.getUrl());
//                    Intent intent = new Intent(mContext, ChatVideoPreviewActivity.class);
//                    intent.putExtra(AppConstant.EXTRA_VIDEO_FILE_URI, url);
//
//                    mContext.startActivity(intent);
                    return;
                }

            }
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

            DynamicBean.AttachBean att = images.get(position);
            if(att.getType().equals("video")){
                DynamicActivityPlayVideo.start(mContext,att.getUrl());
                return;
            }
            ArrayList<String> lists = new ArrayList<String>();
            for (int i = 0; i < images.size(); i++) {

                DynamicBean.AttachBean att0 = images.get(i);
                if(att0.getType().equals("video")){
                    continue;
                }

                lists.add(images.get(i).getUrl());
            }
            Intent intent = new Intent(mContext, MultiImagePreviewActivity.class);
            intent.putExtra(AppConstant.EXTRA_IMAGES, lists);
            intent.putExtra(AppConstant.EXTRA_POSITION, position);
            intent.putExtra(AppConstant.EXTRA_CHANGE_SELECTED, false);
            mContext.startActivity(intent);
        }
    }



    public class AttAdpter extends BaseRecyclerViewAdapter<DynamicBean.AttachBean> {

        public AttAdpter(Context context) {
            super(context);
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = inflater.inflate(R.layout.item_dymaic_att_list_item_layout, parent , false);
            AttAdpter.ViewHolder viewHolder = new AttAdpter.ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

            final ViewHolder holder = (ViewHolder) viewHolder;

            final DynamicBean.AttachBean info = lists.get(position);
            holder.cancel.setVisibility(View.VISIBLE);
            holder.name.setText(info.getName());



            if(FileTool.isAudio(info.getUrl())){
                holder.fileIcon.setImageResource(R.mipmap.icon_dynamic_audio_icon);
                holder.cancel.setVisibility(View.VISIBLE);
            }else {
                holder.cancel.setVisibility(View.GONE);
                holder.fileIcon.setImageResource(FileTool.getResIdFromFileNameBig(false, info.getUrl()));
            }


            holder.itemView.setOnClickListener(view ->  {


                if(info != null) {
                    if(info.getType().equals("audio")){


                        DynamicPlayAudioEvent event = new DynamicPlayAudioEvent();
                        event.statu = 1;
                        event.url = info.getUrl();
                        EventBus.getDefault().post(event);
                        //DynamicActivityPlayVideo.start(mContext, info.getUrl());
                    }else{
                        if(FileTool.isVideo(info.getUrl())){
                            DynamicActivityPlayVideo.start(mContext,info.getUrl());
                            return;
                        }
                        if(FileTool.isAudio(info.getUrl())){
                            DynamicActivityPlayVideo.start(mContext,info.getUrl());
                            return;
                        }

                        if(FileTool.isPicture(info.getUrl())){
                            ArrayList<String> lists = new ArrayList<String>();
                            lists.add(info.getUrl());
                            Intent intent = new Intent(mContext, MultiImagePreviewActivity.class);
                            intent.putExtra(AppConstant.EXTRA_IMAGES, lists);
                            intent.putExtra(AppConstant.EXTRA_POSITION, position);
                            intent.putExtra(AppConstant.EXTRA_CHANGE_SELECTED, false);
                            mContext.startActivity(intent);
                            return;
                        }

                        WebViewActivity.start(mContext,info.getUrl());


                    }

                }

            });
            holder.cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    lists.remove(position);
//
//
//                    notifyDataSetChanged();

                    if(info != null) {
                        if(info.getType().equals("audio")){
//                            DynamicActivityPlayVideo.start(mContext, info.getUrl());
                            DynamicPlayAudioEvent event = new DynamicPlayAudioEvent();
                            event.statu = 1;
                            event.url = info.getUrl();
                            EventBus.getDefault().post(event);
                        }

                    }


                }
            });
        }

        @Override
        public int getItemCount() {
            return lists.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder{
            ImageView fileIcon;
            TextView name;
            TextView cancel;
            public ViewHolder( View itemView) {
                super(itemView);
                fileIcon = itemView.findViewById(R.id.iv_file_icon);
                name = itemView.findViewById(R.id.tv_name);
                cancel = itemView.findViewById(R.id.tv_cancel);

            }
        }

    }
}
