package com.jun.framelibrary.base.navigaion;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.jun.baselibrary.base.navigation.AbsNavigationBar;
import com.jun.framelibrary.R;

/**
 * Email: 465571041@qq.com
 * Created by zzj on 2022/11/18 22:59
 * Version 1.0
 * Description：默认的NavigationBar满足大部分导航栏需要
 */
public class DefaultNavigationBar extends AbsNavigationBar<DefaultNavigationBar.Builder> {

    protected DefaultNavigationBar(Builder builder) {
        super(builder);
    }

    @Override
    public void attachParams() {
        super.attachParams();
        findViewById(R.id.tv1).setVisibility(getBuilder().mLeftVisible);
    }

    public static class Builder extends AbsNavigationBar.Builder<Builder>{
        private int mLeftVisible = View.VISIBLE;

        public Builder(Context context) {
            super(context, R.layout.default_navigationbar);
        }



        @Override
        public DefaultNavigationBar builder() {
            return new DefaultNavigationBar(this);
        }

        public Builder setText(String text){
            setText(R.id.tv1, text);
            return this;
        }

        public Builder setClickListener(View.OnClickListener listener){
            setClickListener(R.id.tv1, listener);
            return this;
        }

        public Builder hideLeftText() {
            mLeftVisible =  View.INVISIBLE;
            return this;
        }
    }
}
