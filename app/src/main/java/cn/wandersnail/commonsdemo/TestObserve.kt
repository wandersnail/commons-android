package cn.wandersnail.commonsdemo

import android.os.Looper
import cn.wandersnail.commons.observer.Observe
import cn.wandersnail.commons.poster.RunOn
import cn.wandersnail.commons.poster.Tag
import cn.wandersnail.commons.poster.ThreadMode
import cn.wandersnail.commons.util.ToastUtils

/**
 *
 *
 * date: 2019/8/29 17:18
 * author: zengfansheng
 */
class TestObserve : MyObserver{
    override fun onChanged(o: Any?) {
        ToastUtils.showShort("$o, 主线程: ${Looper.getMainLooper() == Looper.myLooper()}")
    }

    @Observe
    @RunOn(ThreadMode.MAIN)
    override fun coming() {
        ToastUtils.showShort("coming, 主线程: ${Looper.getMainLooper() == Looper.myLooper()}")
    }

    @Tag("test_tag")
    fun test() {
        ToastUtils.showShort("test, 主线程: ${Looper.getMainLooper() == Looper.myLooper()}")
    }
}