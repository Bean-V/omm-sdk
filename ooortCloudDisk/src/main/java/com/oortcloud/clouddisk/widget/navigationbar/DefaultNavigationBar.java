package com.oortcloud.clouddisk.widget.navigationbar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.oortcloud.clouddisk.R;
import com.oortcloud.clouddisk.activity.more.MoreActivity;
import com.oortcloud.clouddisk.observer.Observer;

/**
 * @FileName: DefaultNavigationBar.java
 * @Author: ZZJun / @CreateDate: 2020/7/12 19:35
 * @Version: 1.0
 * @Function:
 */
public class DefaultNavigationBar<D extends
        DefaultNavigationBar.Builder.DefaultNavigationParams> extends
        AbsNavigationBar<DefaultNavigationBar.Builder.DefaultNavigationParams>  implements Observer {
    public DefaultNavigationBar(Builder.DefaultNavigationParams params) {
        super(params);
    }

    @Override
    public int bindLayoutId() {
        return  R.layout.title_bar;
    }

    @Override
    public void applyView() {
        // 绑定效果

        setText(R.id.title_tv, getParams().mTitle);

        if (getParams().flag){
            findViewById(R.id.home_rl).setVisibility(View.VISIBLE);
            setOnClickListener(R.id.home_tv , getParams().mLeftClickListener);
            setOnClickListener(R.id.more_tv , getParams().mMoreClickListener);
        }else {
            findViewById(R.id.back).setVisibility(View.VISIBLE);
            setOnClickListener(R.id.back , getParams().mLeftClickListener);

        }
        setParams(R.id.right_text,getParams().mRightText ,0 , getParams().mRightClickListener);
        setParams(R.id.right_img,"" , getParams().mRightRes , getParams().mRightClickListener);
        setParams(R.id.right_setting_text,getParams().mSettingText ,0 , getParams().mSettingClickListener);
        setParams(R.id.right_setting_img,"" , getParams().mSettingRes , getParams().mSettingClickListener);

    }

    private void setParams(int viewId , String text , int res , View.OnClickListener listener){
        if (!TextUtils.isEmpty(text)){
            setText(viewId, text);
        }else if (res != 0 ){
            setRes(viewId, res);
        }

        setOnClickListener(viewId, listener);
    }

    /**
     * 处理上传 下载数量显示
     * @param counts
     */
    public void setTransfersCounts(int counts){
        if (counts > 0){
            setParams(R.id.count_tv,String.valueOf(counts) ,0 , null);
        }else {
            findViewById(R.id.count_tv).setVisibility(View.GONE);
        }
    }

    @Override
    public void notifyMsg(int msg) {
        setTransfersCounts(msg);
    }

    public static class Builder extends AbsNavigationBar.Builder{
        DefaultNavigationParams P ;
        public Builder(Context context, ViewGroup parent) {
            super(context , parent);
             P = new DefaultNavigationParams(context , parent);

        }
        public Builder(Context context) {
            this(context , null);
        }

        @Override
        public DefaultNavigationBar builder() {
            DefaultNavigationBar defaultNavigationBar = new DefaultNavigationBar(P);
            return defaultNavigationBar;
        }
        // 1. 设置所有效果
        public Builder setTitle(String title) {
            P.mTitle = title;
            return this;
        }
        public Builder setRightText(String rightText) {
            P.mRightText = rightText;
            return this;
        }

        public Builder setRightRes(int rightRes) {
            P.mRightRes = rightRes;
            return this;
        }
        public Builder setSettingRes(int settingRes) {
            P.mSettingRes = settingRes;
            return this;
        }
        /**
         * 设置更多点击事件
         */
        public Builder
        setMoreClickListener(View.OnClickListener moreListener) {
            P.mMoreClickListener = moreListener;
            return this;
        }
        /**
         * 设置black点击事件
         */
        public Builder
        setLeftClickListener(View.OnClickListener moreListener) {

            P.mMoreClickListener = moreListener;
            return this;
        }
         /**
         * 设置右边的点击事件
         */
        public Builder
        setRightClickListener(View.OnClickListener rightListener) {

            P.mRightClickListener = rightListener;
            return this;
        }
        /**
         * 设置Setting的点击事件
         */
        public Builder
        setSettingClickListener(View.OnClickListener settingListener) {

            P.mSettingClickListener = settingListener;
            return this;
        }
        /**
         * 设置Setting的点击事件
         */
        public Builder
        setStyle(boolean flag) {

            P.flag = flag;
            return this;
        }


        protected static class DefaultNavigationParams
                extends AbsNavigationBar.Builder.AbsNavigationParams{
            protected String mTitle;

            protected String mLeftText;

            protected int mLeftRes;

            protected String mSearchText;

            protected int mSearchRes;

            protected String mDownloadText;

            protected int mDownloadRes;

            protected String mRightText;

            protected int mRightRes;

            protected String mSettingText;

            protected int mSettingRes;

            private boolean flag;

            // 后面还有一些通用的
            protected View.OnClickListener mRightClickListener;

            protected View.OnClickListener mSearchClickListener;

            protected View.OnClickListener mDownloadClickListener;

            protected View.OnClickListener mMoreClickListener= v -> {

                mContext.startActivity(new Intent(mContext, MoreActivity.class));

//                new MorePopupWindow((Activity) mContext  ,v);


            };;

            protected View.OnClickListener mSettingClickListener;

            protected View.OnClickListener mLeftClickListener = v -> {

                    // 关闭当前Activity
                    ((Activity) mContext).finish();

            };
            protected DefaultNavigationParams(Context context, ViewGroup parent) {
                super(context, parent);
            }
        }

    }


}
