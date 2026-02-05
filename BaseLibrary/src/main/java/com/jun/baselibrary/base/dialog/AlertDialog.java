package com.jun.baselibrary.base.dialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.core.app.RemoteInput;

import com.jun.baselibrary.R;

import java.lang.ref.WeakReference;

/**
 * Email: 465571041@qq.com
 * Created by zzj on 2022/12/1 9:15
 * Version 1.0
 * Description：自定义万能Dialog
 */
public class AlertDialog extends Dialog {

    private AlertController mAlert;

    protected AlertDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        mAlert = new AlertController(this, getWindow());
    }
    /**
     * 设置文本内容
     *
     * @param viewId
     * @param text
     * @return
     */
    public void setText(int viewId, CharSequence text) {
        mAlert.setText(viewId, text);
    }

    /**
     * 设置点击事件
     *
     * @param viewId
     * @param listener
     * @return
     */
    public void setOnClickListener(int viewId, View.OnClickListener listener) {
        mAlert.setOnClickListener(viewId,  new WeakReference(listener));
    }

    /**
     * 获取View
     * @param viewId
     * @param <T>
     * @return
     */
    public <T extends View> T getView(int viewId) {

        return (T) mAlert.getView(viewId);
    }

    public static class Builder {

        AlertController.AlertParams P;

        public Builder(Context context) {
            //使用默认dialog样式
            this(context, R.style.dialog);
        }

        public Builder(@NonNull Context context, @StyleRes int themeResId) {
            P = new AlertController.AlertParams(context, themeResId);
        }

        @NonNull
        public Context getContext() {
            return P.context;
        }

        /**
         * Sets whether the dialog is cancelable or not.  Default is true.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setCancelable(boolean cancelable) {
            P.cancelable = cancelable;
            return this;
        }

        /**
         * Sets the callback that will be called if the dialog is canceled.
         *
         * @param onCancelListener
         * @return
         */
        public Builder setOnCancelListener(OnCancelListener onCancelListener) {
            P.onCancelListener = onCancelListener;
            return this;
        }

        /**
         * Sets the callback that will be called when the dialog is dismissed for any reason.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setOnDismissListener(OnDismissListener onDismissListener) {
            P.onDismissListener = onDismissListener;
            return this;
        }

        /**
         * 设置布局
         * Sets a custom view to be the contents of the alert dialog.
         */
        public Builder setContentView(View view) {
            P.view = view;
            P.viewLayoutResId = 0;
            return this;
        }

        /**
         * 设置布局
         * Set a custom view resource to be the contents of the Dialog. The
         */
        public Builder setContentView(int layoutResId) {
            P.view = null;
            P.viewLayoutResId = layoutResId;
            return this;
        }

        /**
         * Sets the callback that will be called if a key is dispatched to the dialog.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setOnKeyListener(OnKeyListener onKeyListener) {
            P.mOnKeyListener = onKeyListener;
            return this;
        }

        /**
         * 设置文本内容
         *
         * @param viewId
         * @param text
         * @return
         */
        public Builder setText(int viewId, CharSequence text) {
            P.textArray.put(viewId, text);
            return this;
        }
        /**
         * 设置图标 图片
         *
         * @param viewId
         * @param resImgId
         * @return
         */
        public Builder setImage(int viewId, @RemoteInput.Source int resImgId) {
            P.imgArray.put(viewId, resImgId);
            return this;
        }

        /**
         * 设置点击事件
         *
         * @param viewId
         * @param listener
         * @return
         */
        public Builder setOnClickListener(int viewId, View.OnClickListener listener) {
            P.clickArray.put(viewId, new WeakReference(listener));
            return this;
        }

        /**
         * 设置点击事件,带文本信息
         *
         * @param viewId
         * @param listener
         * @return
         */
        public Builder setOnClickListener(int viewId, View.OnClickListener listener, int textViewId) {
            P.clickArray.put(viewId, new WeakReference(listener));
            return this;
        }

        /**
         * 设置是否全屏
         *
         * @return
         */
        public Builder fullWidth() {
            P.width = ViewGroup.LayoutParams.MATCH_PARENT;
            return this;
        }

        /**
         * 指定宽高
         *
         * @param width
         * @param height
         * @return
         */
        private Builder setWidthAndHeight(int width, int height) {
            P.width = width;
            P.height = height;
            return this;
        }


        /**
         * 弹出位置
         *
         * @param gravity 位置
         * @return
         */
        public Builder formGravity(int gravity) {
            P.gravity = gravity;
            return this;
        }
        /**
         * 是否使用默认动画
         *
         * @param isAnimator 是否加载动画
         * @return
         */
        public Builder isAnimator(boolean isAnimator) {
            //缩放动画
            if (isAnimator){
                setAnimator(R.style.dialog_from_bottom_anim);

            }
            return this;
        }
        /**
         * 是否使用默认动画
         *
         * @return
         */
        public Builder addDefaultAnimator() {
            //缩放动画
            setAnimator(R.style.dialog_scale_anim);
            return this;
        }

        /**
         * 设置动画
         *
         * @param animator 是否加载动画
         * @return
         */
        public Builder setAnimator(@RemoteInput.Source int animator) {
            P.animator = animator;
            return this;
        }

        //创建Dialog
        @NonNull
        public AlertDialog create() {
            final AlertDialog dialog = new AlertDialog(P.context, P.themeResId);
            P.apply(dialog.mAlert);
            dialog.setCancelable(P.cancelable);
            if (P.cancelable) {
                dialog.setCanceledOnTouchOutside(true);
            }
            dialog.setOnCancelListener(P.onCancelListener);
            dialog.setOnDismissListener(P.onDismissListener);
            if (P.mOnKeyListener != null) {
                dialog.setOnKeyListener(P.mOnKeyListener);
            }
            return dialog;
        }

        /**
         * 显示布局
         *
         * @return
         */
        public AlertDialog show() {
            final AlertDialog dialog = create();
            dialog.show();
            return dialog;
        }

    }
}
