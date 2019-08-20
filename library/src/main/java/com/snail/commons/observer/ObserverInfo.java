package com.snail.commons.observer;

import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * date: 2019/8/9 16:19
 * author: zengfansheng
 */
class ObserverInfo {
    final WeakReference<Observer> weakObserver;
    final Map<String, ObserverMethod> methodMap;

    ObserverInfo(Observer observer, Map<String, ObserverMethod> methodMap) {
        weakObserver = new WeakReference<>(observer);
        this.methodMap = methodMap;
    }
}
