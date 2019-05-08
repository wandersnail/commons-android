package com.snail.commons.helper

import android.app.DownloadManager
import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import com.snail.commons.utils.StringUtils
import com.snail.commons.utils.moveTo
import java.io.File


/**
 * 调用系统下载管理下载文件
 *
 * date: 2019/5/2 17:04
 * author: zengfansheng
 *
 * @param url 下载地址
 * @param savePath 保存路径
 * @param listener 下载监听
 */
class FileDownloadHelper(context: Context, private val mimeType: String, private val url: String, private val title: String, private val savePath: String, private val listener: DownloadListener?) {
    private var appContext = context.applicationContext
    private val downloadManager = appContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    private val downloadMgrPro = DownloadManagerPro(downloadManager)
    private var downloadId = -1L
    private var tempFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "${StringUtils.randomUuid()}.apk")
    private var downloading = false
    private val observer = DownloadChangeObserver()
    private var status = -1

    /**
     * 开始下载
     */
    fun start() {
        if (downloading) {
            synchronized(this) {
                if (downloading) {
                    return
                }
            }
        }
        downloadId = -1
        downloading = true
        //注册监听
        appContext.contentResolver.registerContentObserver(DownloadManagerPro.CONTENT_URI, true, observer)
        //如果文件存在，先删除
        File(savePath).delete()
        val request = DownloadManager.Request(Uri.parse(url))
        //7.0以上的系统适配
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            request.setRequiresDeviceIdle(false)
            request.setRequiresCharging(false)
        }
        request.setTitle(title)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, tempFile.name)
        request.setVisibleInDownloadsUi(true)
        request.allowScanningByMediaScanner()
        request.setMimeType(mimeType)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        downloadId = downloadManager.enqueue(request) //开始下载
    }

    @Synchronized
    private fun unregisterListener() {
        appContext.contentResolver.unregisterContentObserver(observer)
        downloading = false        
    }

    /**
     * 取消下载
     */
    fun cancel() {
        unregisterListener()
        downloadManager.remove(downloadId)
    }

    private inner class DownloadChangeObserver : ContentObserver(Handler(Looper.getMainLooper())) {

        override fun onChange(selfChange: Boolean) {
            if (downloadId == -1L) {
                return
            }
            var status = downloadMgrPro.getStatusById(downloadId)
            if (status == -1) {
                status = DownloadManager.STATUS_FAILED
            }            
            when (status) {
                DownloadManager.STATUS_RUNNING -> {
                    //下载进度，回调
                    val downloadBytes = downloadMgrPro.getDownloadBytes(downloadId)
                    listener?.onProgress(downloadBytes[0], downloadBytes[1])
                }
                DownloadManager.STATUS_FAILED -> unregisterListener()
                DownloadManager.STATUS_SUCCESSFUL -> {
                    unregisterListener()
                    //从下载临时路径移动到目标路径
                    if (tempFile.exists() && tempFile.moveTo(File(savePath), true)) {
                        //更新最新进度
                        val downloadBytes = downloadMgrPro.getDownloadBytes(downloadId)
                        listener?.onProgress(downloadBytes[0], downloadBytes[1])
                    } else {
                        status = DownloadManager.STATUS_FAILED
                    }
                }
            }
            if (this@FileDownloadHelper.status != status) {
                this@FileDownloadHelper.status = status
                listener?.onStateChange(status)
            }
        }
    }

    interface DownloadListener {
        /**
         * 下载进度
         *
         * @param downloaded 已下载
         * @param total 总大小
         */
        fun onProgress(downloaded: Int, total: Int)

        /**
         * 下载完成
         *
         * @param status 下载状态。[DownloadManager.STATUS_RUNNING]等
         */
        fun onStateChange(status: Int)
    }

    companion object {        
        const val MIME_TYPE_BINARY = "application/octet-stream"
        const val MIME_TYPE_APK = "application/vnd.android.package-archive"
    }
}