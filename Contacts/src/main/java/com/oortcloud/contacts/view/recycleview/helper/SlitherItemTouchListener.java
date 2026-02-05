package com.oortcloud.contacts.view.recycleview.helper;

/**
 * Email: 465571041@qq.com
 * Created by zzj on 2022/4/21
 * Version 1.0
 * Description：拖拽-侧滑-回调接口
 */
public abstract class SlitherItemTouchListener {

   public abstract void move(int fromPosition, int toPosition);

    public void onSwiped(int position){

//      mChildDeptList.remove(position);
//     mAdapter.notifyItemRemoved(position);

    }

    public abstract  void stop();
}
