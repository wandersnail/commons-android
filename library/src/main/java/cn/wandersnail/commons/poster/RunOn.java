package cn.wandersnail.commons.poster;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记方法执行线程
 * <p>
 * date: 2019/8/2 23:53
 * author: zengfansheng
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface RunOn {
    /**
     * 运行线程
     */
    ThreadMode value() default ThreadMode.UNSPECIFIED;
}
