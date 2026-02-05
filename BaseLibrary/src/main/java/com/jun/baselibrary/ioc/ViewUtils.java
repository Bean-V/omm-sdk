package com.jun.baselibrary.ioc;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import androidx.fragment.app.Fragment;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Email: 465571041@qq.com
 * Created by zzj on 2022/11/24 1:07
 * Version 1.0
 * Description：IOC注入
 */
public class ViewUtils {
    /**
     * IOC注入
     * @param activity 注入对象
     */
    public static void inject(Activity activity){
        inject(activity,new ViewFinder(activity));
    }
    /**
     * IOC注入
     * @param view 注入对象
     */
    public static void inject(View view){
        inject(view, new ViewFinder(view));
    }

    /**
     * 兼容fragment
     * @param object 注入对象
     * @param view 查询控件
     */
    public static void inject(Object object,View view){
        inject(object, new ViewFinder(view));
    }
    //兼容--
    private static void inject(Object object, ViewFinder finder){
        //属性注入
        injectFinder(object, finder);
        //事件注入
        injectEvent(object, finder);
        //检测网络
        CheckNetUtil.injectNet(finder.getContext(), object);

    }


    /**
     * 反射属性注入
     */
    private static void injectFinder( Object object ,ViewFinder finder) {
        //获取类对象
        Class<?> clazz = object.getClass();
        //获取属性
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            //获取Annotation
           ViewById viewById =  field.getAnnotation(ViewById.class);
           if (viewById != null){
               int viewId = viewById.value();
               View view = finder.findViewById(viewId);
               if (view != null){
                   //操作私有
                   field.setAccessible(true);
                   try {
                       //动态注入
                       field.set(object, view);
                   } catch (IllegalAccessException e) {
                       Log.e("TAG", "Error ", e);
                   }
               }

           }

        }
    }

    /**
     * 反射事件注入
     * @param object
     * @param finder
     *
     */
    private static void injectEvent( Object object, ViewFinder finder) {
        Class<?> clazz = object.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            Click click = method.getAnnotation(Click.class);
            if (click != null){
                int[] viewIds = click.value();
                for (int viewId : viewIds) {
                    View view = finder.findViewById(viewId);
                    if (view != null){
                        view.setOnClickListener(new DeclaredOnClickListener(object, method));
                    }
                }

            }
        }
    }
    //仿View onClick
    private static class DeclaredOnClickListener implements View.OnClickListener {
        private Object object;
        private Method method;
        private DeclaredOnClickListener(Object object,Method method){
            this.object = object;
            this.method = method;
        }
        @Override
        public void onClick(View view) {
            //可操作所有类型修饰符
            method.setAccessible(true);
            try {
                method.invoke(object, view);
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    //处理无参
                    method.invoke(object);
                } catch (Exception ex) {
                    e.printStackTrace();

                }
            }
        }
    }

}
