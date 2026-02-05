package com.jun.baselibrary.base.navigation;

import android.content.Context;
import android.view.ViewGroup;

/**
 * Email: 465571041@qq.com
 * Created by zzj on 2022/11/18 18:01
 * Version 1.0
 * Description：导航栏 实现类（可直接使用）
 */
public class NavigationBar extends AbsNavigationBar {
    protected NavigationBar(Builder builder) {
        super(builder);
    }

    /**
     * 导航栏Builder实现类
     */
    public static class Builder extends AbsNavigationBar.Builder<Builder>{

        public Builder(Context context, int layoutId, ViewGroup parent) {
            super(context, layoutId, parent);
        }

        /**
         * 在Java 5.0之前，当你重写方法时，参数和返回类型必须完全匹配。
         * 在Java 5.0中，它引入了一个名为协变返回类型的新工具。
         * 可以使用相同的签名覆盖方法，但返回返回的对象的子类。
         * 简单说，子类中的方法可以返回一个对象，该对象的类型是由超类中具有相同签名的方法返回的类型的子类
         * @return
         */
        @Override
        public NavigationBar builder() {

            return new NavigationBar(this);
        }

    }
}
