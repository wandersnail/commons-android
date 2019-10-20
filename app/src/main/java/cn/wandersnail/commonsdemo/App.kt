package cn.wandersnail.commonsdemo

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import cn.wandersnail.commons.base.AppHolder
import cn.wandersnail.commons.helper.CrashHandler
import cn.wandersnail.commons.observer.Observable
import cn.wandersnail.commons.poster.PosterDispatcher
import cn.wandersnail.commons.poster.ThreadMode
import com.tencent.mmkv.MMKV
import java.util.concurrent.Executors

/**
 * 描述:
 * 时间: 2018/12/5 10:12
 * 作者: zengfansheng
 */
class App : Application() { 
    val observable = Observable(PosterDispatcher(Executors.newCachedThreadPool(), ThreadMode.POSTING), false)
    
    override fun onCreate() {
        super.onCreate()
        instance = this
        AppHolder.initialize(this)
        MMKV.initialize(this)
        val uriString = MMKV.defaultMMKV().decodeString("uri")
        if (uriString != null) {
            val root = DocumentFile.fromTreeUri(this, Uri.parse(uriString))
            var documentFile = root!!.findFile("logs")
            if (documentFile == null) {
                documentFile = root.createDirectory("logs")
            }
            if (documentFile != null) {
                Log.d("App", "logDir = ${documentFile.uri}")
                CrashHandler(this, documentFile,
                    CrashHandler.Callback { detailError, e ->

                        true
                    })
            }            
        }
    }
    
    companion object {
        var instance: App? = null
            private set
    }
}