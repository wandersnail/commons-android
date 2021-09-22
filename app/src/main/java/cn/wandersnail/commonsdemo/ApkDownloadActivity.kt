package cn.wandersnail.commonsdemo

import android.os.Bundle
import android.os.Environment
import cn.wandersnail.commons.helper.ApkInstaller
import cn.wandersnail.http.EasyHttp
import cn.wandersnail.http.TaskInfo
import cn.wandersnail.http.download.DownloadInfo
import cn.wandersnail.http.download.DownloadListener
import kotlinx.android.synthetic.main.activity_apk_download.*
import java.io.File

/**
 * 调用系统下载APK
 *
 * date: 2019/5/2 19:16
 * author: zengfansheng
 */
class ApkDownloadActivity : BaseActivity() {
    private val url = "https://outexp-beta.cdn.qq.com/outbeta/2021/09/16/cnzfsbledebugger_3.0.3_2c50549c-d5ea-57e1-9fb0-ff99cc658092.apk"
    private var apkInstaller: ApkInstaller? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apk_download)
        val dir = externalCacheDir ?: File(Environment.getExternalStorageDirectory(), "Android/data/$packageName/files")
        if (!dir.exists()) {
            dir.mkdir()
        }
        val apkFile = File(dir, "bleutility.apk")        
        apkInstaller = ApkInstaller(this, apkFile)
        btnDownload.setOnClickListener {
            EasyHttp.singleDownloadWorkerBuilder()
                .setFileInfo(url, apkFile.absolutePath)
                .setListener(object : DownloadListener<DownloadInfo> {
                    override fun onStateChange(info: DownloadInfo, t: Throwable?) {
                        when (info.state) {
                            TaskInfo.State.START -> tvStatus.text = "下载开始"
                            TaskInfo.State.ONGOING -> tvStatus.text = "下载中..."
                            TaskInfo.State.COMPLETED -> {
                                tvStatus.text = "下载成功"
                                apkInstaller?.install()
                            }
                            TaskInfo.State.CANCEL -> tvStatus.text = "下载取消"
                            TaskInfo.State.ERROR -> tvStatus.text = "下载失败"
                            TaskInfo.State.PAUSE -> tvStatus.text = "下载暂停"
                            else -> tvStatus.text = "未知状态..."
                        }
                    }

                    override fun onProgress(info: DownloadInfo) {
                        progressBar.max = info.contentLength.toInt()
                        progressBar.progress = info.completionLength.toInt()
                    }
                })
                .build()
        }
    }
}