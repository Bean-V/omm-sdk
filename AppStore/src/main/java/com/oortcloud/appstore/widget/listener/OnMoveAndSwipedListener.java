package com.oortcloud.appstore.widget.listener;

import androidx.recyclerview.widget.RecyclerView;

public interface OnMoveAndSwipedListener {
    boolean onItemMove(int fromPosition, int toPosition);
    void onItemDismiss(int position);
    void onItemDismiss_(RecyclerView.ViewHolder vh);
}
