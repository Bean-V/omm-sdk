package com.jun.baselibrary.base.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import java.lang.ref.WeakReference;

/**
 * Email: 465571041@qq.com
 * Created by zzj on 2022/12/1 9:17
 * Version 1.0
 * Description：
 */
class AlertController {
    private AlertDialog mDialog;
    private Window mWindow;

    private DialogViewHelper mViewHelper;

    public AlertController(AlertDialog dialog, Window window) {
        mDialog = dialog;
        mWindow = window;
    }

    //获取Dialog
    public AlertDialog getDialog() {
        return mDialog;
    }

    //获取dialog的Window
    public Window getWindow() {
        return mWindow;
    }

    //获取dialog的Window
    private void setViewHelper(DialogViewHelper viewHelper) {
        mViewHelper = viewHelper;
    }

    /**
     * 设置文本内容
     *
     * @param viewId
     * @param text
     * @return
     */
    public void setText(int viewId, CharSequence text) {
        mViewHelper.setText(viewId, text);
    }

    /**
     * 设置点击事件
     *
     * @param viewId
     * @param listener
     * @return
     */
    public void setOnClickListener(int viewId, WeakReference<View.OnClickListener> listener) {
        mViewHelper.setOnClickListener(viewId, listener);
    }

    /**
     * 获取View
     *
     * @param viewId
     * @param <T>
     * @return
     */
    <T extends View> T getView(int viewId) {

        return (T) mViewHelper.getView(viewId);
    }

    /**
     * 处理参数
     */
    public static class AlertParams {
        //上下文
        Context context;
        //主题 样式
        int themeResId;
        //点击空白处是否取消
        boolean cancelable = true;
        //Dialog Cancel 取消监听
        DialogInterface.OnCancelListener onCancelListener;
        //Dialog 完成后取消监听
        DialogInterface.OnDismissListener onDismissListener;
        //Dialog 按键key监听
        DialogInterface.OnKeyListener mOnKeyListener;
        //布局View
        View view;
        //布局Id
        int viewLayoutResId;
        //存放文本，可能有多个用集合存储  SparseArray比HashMap更高效
        SparseArray<CharSequence> textArray = new SparseArray<>();
        //存放图片，可能有多个用集合存储  SparseArray比HashMap更高效
        SparseArray<Integer> imgArray = new SparseArray<>();
        //存放事件 WeakReference软应用，避免照成内存泄露
        SparseArray<WeakReference<View.OnClickListener>> clickArray = new SparseArray<>();
        //设置宽高，默认WRAP_CONTENT
        int width = ViewGroup.LayoutParams.WRAP_CONTENT;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        //弹出位置
        int gravity = Gravity.CENTER;
        //动画
        int animator = 0;

        public AlertParams(Context context, int themeResId) {
            this.context = context;
            this.themeResId = themeResId;
        }

        /**
         * 封装Dialog 绑定和设置参数
         *
         * @param alert
         */
        public void apply(AlertController alert) {
            DialogViewHelper helper = null;
            //设置布局
            if (viewLayoutResId != 0 || view != null) {
                helper = new DialogViewHelper(context);
                if (viewLayoutResId != 0){
                    helper.setContentView(viewLayoutResId);
                }
                if (view != null) {
                    helper.setContentView(view);
                }
            }

            if (helper == null) {
                throw new IllegalArgumentException("请设置布局setContentView()");
            }
            //帮助类
            alert.setViewHelper(helper);

            //设置文本
            for (int i = 0; i < textArray.size(); i++) {
                helper.setText(textArray.keyAt(i), textArray.valueAt(i));
            }

            //设置图片
            for (int i = 0; i < imgArray.size(); i++) {
                helper.setImage(imgArray.keyAt(i), imgArray.valueAt(i));
            }

            //设置点击事件
            for (int i = 0; i < clickArray.size(); i++) {
                helper.setOnClickListener(clickArray.keyAt(i), clickArray.valueAt(i));
            }

            //Dialog设置布局
            alert.mDialog.setContentView(helper.getContentView());
            //设置位置
            alert.mWindow.setGravity(gravity);
            //设置动画
            if (animator != 0) {
                alert.mWindow.setWindowAnimations(animator);
            }
            //设置宽高
            WindowManager.LayoutParams params = alert.mWindow.getAttributes();
            params.width = width;
            params.height = height;
            alert.mWindow.setAttributes(params);
        }

    }
}
