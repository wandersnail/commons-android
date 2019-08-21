package cn.wandersnail.commons.observer;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cn.wandersnail.commons.poster.ThreadMode;

/**
 * 
 * date: 2019/8/9 12:46
 * author: zengfansheng
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Observe {
    ThreadMode value() default ThreadMode.UNSPECIFIED;
}
