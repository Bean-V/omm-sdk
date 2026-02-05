package com.oortcloud.contacts.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.AppCompatCheckBox;

import com.oortcloud.contacts.R;

/**
 * Email: 465571041@qq.com
 * Created by zzj on 2022/4/23
 * Version 1.0
 * Description：
 */
public class ThreeStateCheckbox extends AppCompatCheckBox {
    private Boolean isChecked;
    private int[] states = new int[]{R.attr.state_middle};
    private Boolean isMiddle = false;
    private Boolean mBroadcasting = false;
    private OnStateChangeListener onStateChangeListener;

    private Drawable checkDrawable = getResources().getDrawable(R.mipmap.square_select_focus, null);
    private Drawable unCheckDrawable =   getResources().getDrawable(R.mipmap.square_select_default, null);
    private Drawable middleDrawable = getResources().getDrawable(R.mipmap.square_select_delete, null);
    public ThreeStateCheckbox(Context context) {
        this(context, null);
    }

    public ThreeStateCheckbox(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ThreeStateCheckbox(Context context, AttributeSet attrs, int defStyleAttr) {

        super(context, attrs, defStyleAttr);
        initView();
    }

    public void initView(){
        StateListDrawable stateListDrawable =new  StateListDrawable();
        stateListDrawable.addState(states, middleDrawable);
        stateListDrawable.addState(new int[]{android.R.attr.state_checked}, checkDrawable);
        stateListDrawable.addState(new int[]{}, unCheckDrawable);
        stateListDrawable.setBounds(0, 0, stateListDrawable.getMinimumWidth(), stateListDrawable.getMinimumHeight());
        setCompoundDrawables(stateListDrawable, null, null, null);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
//        buttonDrawable = null

        StateListDrawable buttonDrawable = new StateListDrawable();   //有些低版本手机还是会显示默认的框，用这个方式去掉
    }


    @Override
    public void toggle() {
        if (isMiddle) {
            isChecked = true;
        } else {
            super.toggle();
        }
    }

    @Override
    public void setChecked(boolean checked) {
        boolean checkedChanged = isChecked != checked;
        super.setChecked(checked);
        boolean wasMiddle = isMiddle;
        setMiddleState(false, false);
        if (wasMiddle || checkedChanged) {
            notifyStateListener();
        }
    }

    void setMiddleState(Boolean indeterminate) {
        setMiddleState(indeterminate, true);
    }

    private Boolean getState() {
         if (isMiddle) {
             return null;
        } else{
             return isChecked;
         }
    }
    /**
     * 设置状态，null表示中间状态
     */
    public void setState(Boolean state) {
        if (state != null) {
            isChecked = state;
        } else {
            setMiddleState(true);
        }
    }

    private void setMiddleState(Boolean isMiddle, Boolean notify) {
        if (this.isMiddle != isMiddle) {
            this.isMiddle = isMiddle;
            refreshDrawableState();
            if (notify) {
                notifyStateListener();
            }
        }
    }
    private void notifyStateListener() {
        if (mBroadcasting) {
            return;
        }
        mBroadcasting = true;
        if (onStateChangeListener != null) {
            onStateChangeListener.onStateChanged(this, getState());
        }
        mBroadcasting = false;
    }
    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (getState() == null) {
            View.mergeDrawableStates(drawableState, states);
        }
        return drawableState;
    }

    public void setOnStateChangeListener(OnStateChangeListener listener) {
        this.onStateChangeListener = listener;
    }
    interface OnStateChangeListener {
        void onStateChanged(ThreeStateCheckbox checkbox, Boolean newState);
    }
}
