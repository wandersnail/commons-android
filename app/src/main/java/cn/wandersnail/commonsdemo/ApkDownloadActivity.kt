package cn.wandersnail.commonsdemo

import android.Manifest
import android.app.DownloadManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import cn.wandersnail.commons.helper.ApkInstallHelper
import cn.wandersnail.commons.helper.FileDownloadHelper
import cn.wandersnail.commons.helper.PermissionsRequester
import kotlinx.android.synthetic.main.activity_apk_download.*
import java.io.File

/**
 * 调用系统下载APK
 *
 * date: 2019/5/2 19:16
 * author: zengfansheng
 */
class ApkDownloadActivity : BaseActivity() {
    private val url = "http://gdown.baidu.com/data/wisegame/55dc62995fe9ba82/jinritoutiao_448.apk"
    private var downloader: FileDownloadHelper? = null
    private val apkInstallHelper = ApkInstallHelper(this)

    private val permissionsRequester: PermissionsRequester? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apk_download)
        val dir = externalCacheDir ?: File(Environment.getExternalStorageDirectory(), "Android/data/$packageName/files")
        if (!dir.exists()) {
            dir.mkdir()
        }
        val apkFile = File(dir, "toutiao.apk")
        val savePath = apkFile.absolutePath
        val builder = FileDownloadHelper.Builder(this, url)
        builder.setMimeType(FileDownloadHelper.MIME_TYPE_APK)
        builder.setTitle("今日头条")
        builder.setDescription("下载中...嗒嗒嗒")
        builder.setSavePath(savePath)
        downloader = builder.build()
        downloader!!.setCallback(object : FileDownloadHelper.Callback {
            override fun onProgress(downloaded: Int, total: Int) {
                progressBar.max = total
                progressBar.progress = downloaded
            }

            override fun onStateChange(status: Int) {
                when (status) {
                    DownloadManager.STATUS_FAILED -> tvStatus.text = "下载失败"
                    DownloadManager.STATUS_RUNNING -> tvStatus.text = "下载中..."
                    DownloadManager.STATUS_PAUSED -> tvStatus.text = "下载暂停"
                    DownloadManager.STATUS_PENDING -> tvStatus.text = "等待下载..."
                    else -> tvStatus.text = "未知状态..."
                }
            }

            override fun onCompleted(file: File) {
                tvStatus.text = "下载成功"
                apkInstallHelper.install(file)
            }
        })
        btnDownload.setOnClickListener { 
            downloader!!.start()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PermissionsRequester(this)
            permissionsRequester?.checkAndRequest(listOf(Manifest.permission.REQUEST_INSTALL_PACKAGES))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        apkInstallHelper.onActivityResult(requestCode)
        permissionsRequester?.onActivityResult(requestCode)
    }

    override fun onDestroy() {
        super.onDestroy()
        downloader?.cancel()
    }
}