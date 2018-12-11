package com.zfs.commons.annotation;

import java.lang.annotation.*;

/**
 * 描述:
 * 时间: 2018/12/11 09:55
 * 作者: zengfansheng
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface RunThread {
    ThreadType value() default ThreadType.POSTING;
}
