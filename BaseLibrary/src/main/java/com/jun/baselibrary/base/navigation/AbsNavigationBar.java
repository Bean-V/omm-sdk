package com.jun.baselibrary.base.navigation;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.RemoteInput;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Email: 465571041@qq.com
 * Created by zzj on 2022/11/18 17:51
 * Version 1.0
 * Description：导航栏基类
 */
public abstract class AbsNavigationBar<T extends AbsNavigationBar.Builder> implements INavigation {

    private Builder mBuilder;

    private View mNavigationBar;

    protected AbsNavigationBar(T builder){
        mBuilder = builder;
        create();
    }

    /**
     * 创建NavigationBar
     */
    @Override
    public void create() {
        if (mBuilder.mLayoutId == 0){
            throw new IllegalArgumentException("请设置NavigationBar布局--> setLayoutView(int layoutId)");
        }
        mNavigationBar = LayoutInflater.from(mBuilder.mContext).
                inflate(mBuilder.mLayoutId,mBuilder.mParent,false);
        //添加到布局
        attachParent(mNavigationBar, mBuilder.mParent);
        //绑定参数
        attachParams();
    }

    /**
     * 将navigationBar添加到父布局
     * @param navigationBar
     * @param parent
     */
    @Override
    public void attachParent(View navigationBar, ViewGroup parent) {
        if (parent instanceof FrameLayout){
            parent.addView(navigationBar);
        }else {
            parent.addView(navigationBar ,0);
        }

    }

    /**
     * 绑定基本参数
     * 事件、图片、文本
     */
    @Override
    public void attachParams() {
        //设置文本 title等
        Set<Map.Entry<Integer,CharSequence>> TextEntries = mBuilder.mTextMap.entrySet();
        for (Map.Entry<Integer,CharSequence> entry : TextEntries) {
            TextView textView =  findViewById(entry.getKey());
            textView.setText(entry.getValue());
        }
        //设置图片 图标
        Set<Map.Entry<Integer,Integer>> imgEntries = mBuilder.mImgMap.entrySet();
        for (Map.Entry<Integer,Integer> entry : imgEntries) {
            ImageView imgView =  findViewById(entry.getKey());
            imgView.setImageResource(entry.getValue());
        }

        //设置点击事件
        Set<Map.Entry<Integer,View.OnClickListener>> mListenerEntries = mBuilder.mListenerMap.entrySet();
        for (Map.Entry<Integer, View.OnClickListener> entry : mListenerEntries) {
            findViewById(entry.getKey()).setOnClickListener(entry.getValue());
        }
    }

    /**
     * 获取View
     * @param viewId
     * @param <T>
     * @return
     */
    protected  <T extends View> T findViewById(Integer viewId) {

        return  mNavigationBar.findViewById(viewId);
    }

    /**
     * 获取builder
     * @return
     */
    public T getBuilder() {
        return (T) mBuilder;
    }

    /**
     * builder基类
     */
    public abstract static class Builder<T extends Builder>{
        //上下文
        private Context mContext;
        //导航栏布局Id
        private int mLayoutId;
        //需要添加的父布局
        private ViewGroup mParent;
        //存储 文本
        private HashMap<Integer,CharSequence> mTextMap;
        //存储 图片
        private HashMap<Integer,Integer> mImgMap;
        //存储 OnClickListener
        private Map<Integer, View.OnClickListener> mListenerMap;

        public Builder(Context context){
           this(context, 0);
        }

        protected Builder(Context context, int layoutId){
            this(context, layoutId,
                    //获取Activity根布局
                    (ViewGroup) ((ViewGroup) ((Activity) context).getWindow().getDecorView()).getChildAt(0));
        }

        protected Builder(Context context, int layoutId, ViewGroup parent){
            mContext = context;
            mLayoutId = layoutId;
            mParent = parent;
            mTextMap = new HashMap<>();
            mImgMap = new HashMap<>();
            mListenerMap = new HashMap<>();
        }

        /**
         * 设置导航栏布局文件id
         * @param layoutId
         * @return
         */
        protected T setLayoutView(int layoutId){
            mLayoutId = layoutId;
            return (T) this;
        }

        /**
         * 导航栏需要添加到父布局
         * @param parent
         * @return
         */
        protected T setParentLayout(ViewGroup parent){
            mParent = parent;
            return (T) this;
        }
//        public Builder setTextView(int viewId, String text){
//            id = viewId;
//            mText = text;
//            return  this;
//        }
//        public Builder setClick(int viewId, View.OnClickListener listener){
//
//            return  this;
//        }

        //使用泛型避免强制
        protected T setText(int viewId, String text){
            mTextMap.put(viewId, text);
            return (T) this;
        }
        //使用泛型避免强制
        protected T setImg(int viewId, @RemoteInput.Source int resImgId){
            mImgMap.put(viewId, resImgId);
            return (T) this;
        }

        protected T setClickListener(int viewId, View.OnClickListener listener){
            mListenerMap.put(viewId, listener);
            return (T) this;
        }

        /**
         * 创建NavigationBar
         *  @return
         *
         * 在Java 5.0之前，当你重写方法时，参数和返回类型必须完全匹配。
         * 在Java 5.0中，它引入了一个名为协变返回类型的新工具。
         * 可以使用相同的签名覆盖方法，但返回返回的对象的子类。
         * 简单说，子类中的方法可以返回一个对象，该对象的类型是由超类中具有相同签名的方法返回的类型的子类
         * @return
         */
        protected abstract AbsNavigationBar builder();
    }
}
