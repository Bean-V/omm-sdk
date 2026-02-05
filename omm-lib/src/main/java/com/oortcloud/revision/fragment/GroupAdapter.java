package com.oortcloud.revision.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.oort.weichat.R;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {
    private Context mContext;
    private List<MsgGroup> mGroupList;
    private OnGroupClickListener mListener;
    private OnGroupLongClickListener mLongClickListener;
    // 新增：标签分组是否展开
    private boolean isLabelExpanded = true;
    // 标签分组在列表中的位置（用于快速定位）
    private int labelGroupPosition = -1;

    // 构造方法中初始化标签分组位置
    public GroupAdapter(Context context, List<MsgGroup> groupList) {
        this.mContext = context;
        this.mGroupList = groupList;
        // 找到“label”分组的位置
        for (int i = 0; i < groupList.size(); i++) {
            if (groupList.get(i).getGroupId().equals("label")) {
                labelGroupPosition = i;
                break;
            }
        }
    }

    // 切换标签分组展开/收起状态
    public void toggleLabelExpand() {
        isLabelExpanded = !isLabelExpanded;
        notifyDataSetChanged(); // 刷新列表
    }

    // 重写 getItemViewType 区分不同类型的item
    @Override
    public int getItemViewType(int position) {
        // 如果是标签分组，返回标签类型
        if (position == labelGroupPosition) {
            return 0;
        }
        // 如果是展开状态下的二级标签
        if (isLabelExpanded && isSubLabelPosition(position)) {
            return 1;
        }
        // 其他一级分组
        return 2;
    }

    // 判断是否是二级标签的位置
    private boolean isSubLabelPosition(int position) {
        if (labelGroupPosition == -1) return false;
        // 二级标签在标签分组的下一位开始
        int subLabelStart = labelGroupPosition + 1;
        int subLabelEnd = subLabelStart + getSubLabelCount() - 1;
        return position >= subLabelStart && position <= subLabelEnd;
    }

    // 获取二级标签数量（包含“新增标签”按钮）
    private int getSubLabelCount() {
        MsgGroup labelGroup = getLabelGroup();
        return labelGroup == null ? 0 : labelGroup.getSubLabels().size() + 1; // +1 是新增按钮
    }

    // 获取标签分组
    private MsgGroup getLabelGroup() {
        if (labelGroupPosition == -1) return null;
        return mGroupList.get(labelGroupPosition);
    }

    // 重写 getItemCount：展开时需要加上二级标签数量
    @Override
    public int getItemCount() {
        int baseCount = mGroupList.size();
        return isLabelExpanded ? baseCount + getSubLabelCount() : baseCount;
    }

    @Override
    public GroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId;
        if (viewType == 0) {
            // 标签分组item（带箭头指示展开/收起）
            layoutId = R.layout.item_label_group;
        } else if (viewType == 1) {
            // 二级标签item（包含新增按钮）
            layoutId = R.layout.item_sub_label;
        } else {
            // 普通一级分组item
            layoutId = R.layout.item_msg_group;
        }
        View view = LayoutInflater.from(mContext).inflate(layoutId, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GroupViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        MsgGroup labelGroup = getLabelGroup();

        if (viewType == 0) {
            // 标签分组item：显示名称+箭头
            holder.tvGroupName.setText(labelGroup.getName());
            holder.ivGroupIcon.setImageResource(labelGroup.getIconRes());
            // 箭头方向：展开时朝上，收起时朝下
            holder.ivArrow.setImageResource(isLabelExpanded ?
                    R.drawable.ic_arrow_down : R.drawable.ic_arrow_right);
            holder.itemView.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onGroupClick(labelGroup);
                }
            });
        } else if (viewType == 1) {
            // 二级标签item：计算当前二级标签的索引
            int subLabelIndex = position - (labelGroupPosition + 1);
            // 最后一个是“新增标签”按钮
            if (subLabelIndex == labelGroup.getSubLabels().size()) {
                // 新增标签按钮
                holder.ivGroupIcon.setVisibility(View.GONE);
                holder.tvSubLabelName.setText("+ 新增标签");
                holder.tvSubLabelName.setTextColor(mContext.getResources().getColor(R.color.blue));
                holder.itemView.setOnClickListener(v -> {
                    // 触发新增二级标签
                    if (mListener != null) {
                        mListener.onGroupClick(new MsgGroup("add_label", "新增标签",R.drawable.ic_app_add));
                    }
                });
            } else {
                // 普通二级标签

                holder.ivGroupIcon.setVisibility(View.VISIBLE);
                MsgGroup subLabel = labelGroup.getSubLabels().get(subLabelIndex);
                holder.ivGroupIcon.setImageResource(subLabel.getIconRes());
                holder.tvSubLabelName.setText(subLabel.getName());
                holder.tvSubLabelName.setTextColor(mContext.getResources().getColor(R.color.black));
                holder.itemView.setOnClickListener(v -> {
                    if (mListener != null) {
                        mListener.onGroupClick(subLabel);
                    }
                });
                // 长按删除二级标签
                holder.itemView.setOnLongClickListener(v -> {
                    if (mLongClickListener != null) {
                        mLongClickListener.onGroupLongClick(subLabel);
                        return true;
                    }
                    return false;
                });
            }
        } else {
            // 普通一级分组（全部/未读/单聊/群聊）
            MsgGroup group = mGroupList.get(position);
            holder.tvGroupName.setText(group.getName());
            holder.ivGroupIcon.setImageResource(group.getIconRes());
            // 未读数量显示（原有逻辑）
            if (group.getUnreadCount() > 0) {
                holder.tvUnreadCount.setVisibility(View.VISIBLE);
                holder.tvUnreadCount.setText(String.valueOf(group.getUnreadCount()));
            } else {
                holder.tvUnreadCount.setVisibility(View.GONE);
            }
            // 点击事件
            holder.itemView.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onGroupClick(group);
                }
            });
        }
    }



    // 新增：二级标签的ViewHolder
    public static class GroupViewHolder extends RecyclerView.ViewHolder {
        // 一级分组控件
        ImageView ivGroupIcon;
        TextView tvGroupName;
        TextView tvUnreadCount;
        // 标签分组专用：箭头图标
        ImageView ivArrow;
        // 二级标签专用：标签名称
        TextView tvSubLabelName;

        public GroupViewHolder(View itemView) {
            super(itemView);
            // 初始化共用控件
            ivGroupIcon = itemView.findViewById(R.id.iv_group_icon);
            tvGroupName = itemView.findViewById(R.id.tv_group_name);
            tvUnreadCount = itemView.findViewById(R.id.tv_unread_count);
            // 初始化标签分组专用控件
            ivArrow = itemView.findViewById(R.id.iv_arrow);
            // 初始化二级标签专用控件
            tvSubLabelName = itemView.findViewById(R.id.tv_sub_label_name);
        }
    }

    // 原有接口（保持不变）
    public interface OnGroupClickListener {
        void onGroupClick(MsgGroup group);
    }

    public interface OnGroupLongClickListener {
        void onGroupLongClick(MsgGroup group);
    }

    // setter方法（保持不变）
    public void setOnGroupClickListener(OnGroupClickListener listener) {
        this.mListener = listener;
    }

    public void setOnGroupLongClickListener(OnGroupLongClickListener listener) {
        this.mLongClickListener = listener;
    }
}