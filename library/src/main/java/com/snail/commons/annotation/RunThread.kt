package com.snail.commons.annotation

import java.lang.annotation.Inherited

/**
 * 描述:
 * 时间: 2018/12/11 09:55
 * 作者: zengfansheng
 */
@Inherited
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
annotation class RunThread(val value: ThreadType = ThreadType.POSTING)
