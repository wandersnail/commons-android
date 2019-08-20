package cn.zfs.commonsdemo

import android.app.Application
import com.snail.commons.AppHolder
import com.snail.commons.methodpost.PosterDispatcher
import com.snail.commons.methodpost.ThreadMode
import com.snail.commons.observer.Observable
import java.util.concurrent.Executors

/**
 * 描述:
 * 时间: 2018/12/5 10:12
 * 作者: zengfansheng
 */
class App : Application() { 
    val observable = Observable(PosterDispatcher(Executors.newCachedThreadPool(), ThreadMode.POSTING), true)
    
    override fun onCreate() {
        super.onCreate()
        instance = this
        AppHolder.initialize(this)
    }
    
    companion object {
        var instance: App? = null
            private set
    }
}