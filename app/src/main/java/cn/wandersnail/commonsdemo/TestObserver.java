package cn.wandersnail.commonsdemo;

import cn.wandersnail.commons.observer.Observer;

/**
 * date: 2019/9/2 15:59
 * author: zengfansheng
 */
public interface TestObserver extends Observer {
    void test(int i, float f, double d, byte b, boolean b1, char c, long l, short s);
}
