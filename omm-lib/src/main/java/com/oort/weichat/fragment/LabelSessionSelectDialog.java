package com.oort.weichat.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.oort.weichat.R;
import com.oort.weichat.bean.Friend;
import com.oort.weichat.helper.AvatarHelper;
import com.oort.weichat.view.HeadView;
import com.oortcloud.basemodule.im.IMUserInfoUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class LabelSessionSelectDialog extends BottomSheetDialog {
    private Context mContext;
    private String mTargetLabelId; // 目标标签ID（要添加会话的标签）
    private List<Friend> mAllSessions; // 所有可选会话
    private List<LabelSessionRelation> mSelectedRelations; // 已选关联关系
    private OnConfirmSelectListener mConfirmListener;

    // 已选会话ID缓存（用于快速判断是否选中，关键：统一使用sessionId）
    private Set<String> mSelectedSessionIds = new HashSet<>();

    // 视图控件
    private LinearLayout mSelectedTagsLl; // 已选会话标签容器
    private RecyclerView mSessionRv;      // 可选会话列表
    private TextView mTvConfirm;          // 确认按钮
    private SessionSelectAdapter mAdapter;

    // 构造方法
    public LabelSessionSelectDialog(Context context, String targetLabelId,
                                    List<Friend> allSessions, List<LabelSessionRelation> existingRelations) {
        super(context);
        this.mContext = context;
        this.mTargetLabelId = targetLabelId;
        this.mAllSessions = filterValidSessions(allSessions); // 过滤无效会话
        this.mSelectedRelations = existingRelations != null ? new ArrayList<>(existingRelations) : new ArrayList<>();
        // 初始化已选会话ID缓存（关键：使用sessionId而非userId）
        for (LabelSessionRelation relation : mSelectedRelations) {
            mSelectedSessionIds.add(relation.getSessionId());
        }
        initView();
    }

    // 过滤无效会话，避免空指针
    private List<Friend> filterValidSessions(List<Friend> sessions) {
        List<Friend> validSessions = new ArrayList<>();
        if (sessions == null) return validSessions;
        for (Friend session : sessions) {
            if (session != null && !TextUtils.isEmpty(session.getSid())) {
                validSessions.add(session);
            }
        }
        return validSessions;
    }

    // 初始化布局和逻辑
    private void initView() {
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.dialog_label_session_select, null);
        setContentView(contentView);
        // 适配底部弹窗高度（占屏幕70%）
        ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
        layoutParams.height = (int) (Resources.getSystem().getDisplayMetrics().heightPixels * 0.7);
        contentView.setLayoutParams(layoutParams);

        // 绑定控件
        mSelectedTagsLl = contentView.findViewById(R.id.ll_selected_tags);
        mSessionRv = contentView.findViewById(R.id.rv_sessions);
        mTvConfirm = contentView.findViewById(R.id.tv_confirm);

        // 初始化已选标签展示
        updateSelectedTags();

        // 初始化可选会话列表
        mSessionRv.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new SessionSelectAdapter(mAllSessions, mSelectedSessionIds);
        mAdapter.setOnSessionSelectListener((friend, isSelected) -> {
            String sessionId = friend.getSid(); // 关键：统一使用sessionId作为标识
            if (isSelected) {
                // 选中：添加到已选列表（确保不会重复添加）
                if (!mSelectedSessionIds.contains(sessionId)) {
                    mSelectedSessionIds.add(sessionId);
                    mSelectedRelations.add(new LabelSessionRelation(
                            mTargetLabelId,
                            sessionId,
                            friend.getRoomFlag() // 0=单聊，1=群聊
                    ));
                }
            } else {
                // 取消选中：从已选列表移除
                if (mSelectedSessionIds.contains(sessionId)) {
                    mSelectedSessionIds.remove(sessionId);
                    Iterator<LabelSessionRelation> iterator = mSelectedRelations.iterator();
                    while (iterator.hasNext()) {
                        LabelSessionRelation relation = iterator.next();
                        if (relation.getSessionId().equals(sessionId)) {
                            iterator.remove();
                            break;
                        }
                    }
                }
            }
            // 更新已选标签展示
            updateSelectedTags();
            updateConfirmButtonState(); // 更新确认按钮状态

            mAdapter.notifyDataSetChanged();
        });
        mSessionRv.setAdapter(mAdapter);

        // 确认按钮逻辑
        mTvConfirm.setOnClickListener(v -> {
            if (mConfirmListener != null) {
                mConfirmListener.onConfirm(new ArrayList<>(mSelectedRelations)); // 返回副本，避免外部修改
            }
            dismiss();
        });

        // 初始化确认按钮状态
        updateConfirmButtonState();
    }

    // 更新确认按钮状态（根据选择数量）
    private void updateConfirmButtonState() {
        if (mSelectedRelations.isEmpty()) {
            mTvConfirm.setEnabled(false);
            mTvConfirm.setTextColor(ContextCompat.getColor(mContext, R.color.gray_40));
        } else {
            mTvConfirm.setEnabled(true);
            mTvConfirm.setTextColor(ContextCompat.getColor(mContext, R.color.main_color));
            mTvConfirm.setText(String.format("确认（%d项）", mSelectedRelations.size()));
        }
    }

    // 更新已选会话标签展示
    private void updateSelectedTags() {
        mSelectedTagsLl.removeAllViews();
        if (mSelectedRelations.isEmpty()) {
            // 无已选时显示提示
            TextView tipTv = new TextView(mContext);
            tipTv.setText("未选择会话");
            tipTv.setTextColor(ContextCompat.getColor(mContext, R.color.gray_40));
            tipTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            mSelectedTagsLl.addView(tipTv);
            return;
        }

        // 遍历已选关系，添加标签View
        for (LabelSessionRelation relation : mSelectedRelations) {
            // 查找对应的会话信息（优化：使用循环查找，确保匹配）
            Friend targetSession = findSessionById(relation.getSessionId());
            if (targetSession == null) continue;

            // 创建标签View
            LinearLayout tagView = (LinearLayout) LayoutInflater.from(mContext)
                    .inflate(R.layout.item_selected_session_tag, mSelectedTagsLl, false);
            TextView tagNameTv = tagView.findViewById(R.id.tv_tag_name);
            ImageView tagCloseIv = tagView.findViewById(R.id.iv_tag_close);

            // 设置标签名称（优先显示备注名）
            String tagName = TextUtils.isEmpty(targetSession.getRemarkName())
                    ? targetSession.getNickName()
                    : targetSession.getRemarkName();
            tagNameTv.setText(tagName);

            // 标签关闭按钮逻辑（点击移除选择）
            tagCloseIv.setOnClickListener(v -> {
                // 渐变动画
                tagView.animate()
                        .alpha(0f)
                        .scaleX(0.8f)
                        .scaleY(0.8f)
                        .setDuration(200)
                        .withEndAction(() -> {
                            // 从缓存和列表中移除
                            String sessionId = relation.getSessionId();
                            mSelectedSessionIds.remove(sessionId);
                            mSelectedRelations.remove(relation);
                            mSelectedTagsLl.removeView(tagView);
                            mAdapter.notifyDataSetChanged(); // 刷新列表选中状态
                            updateConfirmButtonState();
                        })
                        .start();
            });

            mSelectedTagsLl.addView(tagView);
        }
    }

    // 辅助方法：根据sessionId查找会话
    private Friend findSessionById(String sessionId) {
        for (Friend friend : mAllSessions) {
            if (friend != null && sessionId.equals(friend.getSid())) {
                return friend;
            }
        }
        return null;
    }

    // 确认选择的回调接口
    public interface OnConfirmSelectListener {
        void onConfirm(List<LabelSessionRelation> selectedRelations);
    }

    // 设置确认回调
    public void setOnConfirmSelectListener(OnConfirmSelectListener listener) {
        this.mConfirmListener = listener;
    }

    // 可选会话列表适配器
    private static class SessionSelectAdapter extends RecyclerView.Adapter<SessionSelectViewHolder> {
        private List<Friend> mSessions;
        private Set<String> mSelectedIds; // 存储的是sessionId
        private OnSessionSelectListener mSelectListener;

        public SessionSelectAdapter(List<Friend> sessions, Set<String> selectedIds) {
            this.mSessions = sessions;
            this.mSelectedIds = selectedIds;
        }

        @NonNull
        @Override
        public SessionSelectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_label_session_select, parent, false);
            return new SessionSelectViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull SessionSelectViewHolder holder, int position) {
            Friend session = mSessions.get(position);
            if (session == null) return;

            // 关键：使用sessionId判断选中状态，与添加/移除逻辑保持一致
            boolean isSelected = mSelectedIds.contains(session.getSid());

            // 显示会话名称（优先备注名）
            String sessionName = TextUtils.isEmpty(session.getRemarkName())
                    ? session.getNickName()
                    : session.getRemarkName();
            holder.tvName.setText(sessionName);
            AvatarHelper.getInstance().displayAvatar(IMUserInfoUtil.getInstance().getUserId(), session, holder.ivAvatar);

            // 显示会话类型（单聊/群聊）
            holder.tvType.setText(session.getRoomFlag() == 0 ? "单聊" : "群聊");

            // 显示选中状态（使用正确的图标）
            holder.ivCheck.setImageResource(isSelected
                    ? R.drawable.ic_check_selected
                    : R.drawable.ic_check_unselected);

            // 点击item切换选中状态（关键：将正确的状态传递出去）
            holder.itemView.setOnClickListener(v -> {
                if (mSelectListener != null) {
                    mSelectListener.onSessionSelect(session, !isSelected);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mSessions.size();
        }

        // 会话选择回调
        public interface OnSessionSelectListener {
            void onSessionSelect(Friend session, boolean isSelected);
        }

        public void setOnSessionSelectListener(OnSessionSelectListener listener) {
            this.mSelectListener = listener;
        }
    }

    // 可选会话列表ViewHolder
    private static class SessionSelectViewHolder extends RecyclerView.ViewHolder {
        HeadView ivAvatar;
        TextView tvName;
        TextView tvType;
        ImageView ivCheck;

        public SessionSelectViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_session_avatar);
            tvName = itemView.findViewById(R.id.tv_session_name);
            tvType = itemView.findViewById(R.id.tv_session_type);
            ivCheck = itemView.findViewById(R.id.iv_session_check);
        }
    }

    // 标签-会话关联关系实体类（补充完整）
//    public static class LabelSessionRelation {
//        private String labelId;
//        private String sessionId;
//        private int roomFlag;
//
//        public LabelSessionRelation(String labelId, String sessionId, int roomFlag) {
//            this.labelId = labelId;
//            this.sessionId = sessionId;
//            this.roomFlag = roomFlag;
//        }
//
//        public String getLabelId() {
//            return labelId;
//        }
//
//        public String getSessionId() {
//            return sessionId;
//        }
//
//        public int getRoomFlag() {
//            return roomFlag;
//        }
//    }
}
