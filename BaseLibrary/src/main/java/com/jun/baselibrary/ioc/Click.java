package com.jun.baselibrary.ioc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Email: 465571041@qq.com
 * Created by zzj on 2022/11/24 14:59
 * Version 1.0
 * Description：View Event Annotation
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Click {
    int[] value();
}
