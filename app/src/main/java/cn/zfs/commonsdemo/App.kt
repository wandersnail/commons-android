package cn.zfs.commonsdemo

import android.app.Application
import com.snail.commons.AppHolder

/**
 * 描述:
 * 时间: 2018/12/5 10:12
 * 作者: zengfansheng
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        AppHolder.initialize(this)
    }
}