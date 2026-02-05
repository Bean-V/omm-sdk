package com.jun.baselibrary.base.dialog;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;

/**
 * Email: 465571041@qq.com
 * Created by zzj on 2022/12/1 9:17
 * Version 1.0
 * Description：Dialog辅助类 处理UI事件
 */
class DialogViewHelper {
    private Context mContext;
    private View mContentView;
    //软引用，防止内存泄露
    private SparseArray<WeakReference<View>> mViews;

    DialogViewHelper(Context context) {
        mContext = context;
        mViews = new SparseArray<>();
    }

    void setContentView(int viewLayoutResId) {
        mContentView = LayoutInflater.from(mContext).inflate(viewLayoutResId, null);
    }

    public void setContentView(View view) {
        mContentView = view;
    }

    public View getContentView() {
        return mContentView;
    }

    /**
     * 设置文本
     *
     * @param viewId
     * @param text
     */
    public void setText(int viewId, CharSequence text) {
        View view = getView(viewId);
        if (view instanceof TextView) {
            TextView tv = (TextView) view;
            tv.setText(text);
        }
    }

    public void setImage(int viewId, Integer resImgId) {
        View view = getView(viewId);
        if (view instanceof ImageView) {
            ImageView img = (ImageView) view;
            img.setImageResource(resImgId);
        }
    }

    /**
     * 设置点击事件
     *
     * @param viewId
     * @param weakReference 使用弱引用
     */
    public void setOnClickListener(int viewId, WeakReference<View.OnClickListener> weakReference) {
        View view = getView(viewId);
        if (view != null) {
            View.OnClickListener listener = weakReference.get();
            //判断是否是DialogOnClickListener
            if (listener instanceof DialogOnClickListener) {
                //强转 绑定
                DialogOnClickListener dialogOnClickListener = (DialogOnClickListener) weakReference.get();
                dialogOnClickListener.setHelper(this);

            }
            view.setOnClickListener(weakReference.get());
        }
    }

    //获取View
    <T extends View> T getView(int viewId) {
        View view = null;
        WeakReference<View> weakReference = mViews.get(viewId);
        if (weakReference == null) {
            view = mContentView.findViewById(viewId);
            if (view != null) {
                mViews.put(viewId, new WeakReference(view));
            }

        } else {
            view = weakReference.get();
        }
        return (T) view;
    }

}
