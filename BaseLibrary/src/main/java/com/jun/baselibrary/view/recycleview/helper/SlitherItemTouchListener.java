package com.jun.baselibrary.view.recycleview.helper;

import java.util.List;

/**
 * Email: 465571041@qq.com
 * Created by zzj on 2023/3/7 0:22
 * Version 1.0
 * Description：滑动、拖拽事件回调, 按需求复写
 */
public abstract class SlitherItemTouchListener {
    //拖拽
    public void onMove(int fromPosition, int targetPosition){}

    public List onMove(){
        return null;
    }
    //侧滑
    public void onSwiped(int position){}
}
