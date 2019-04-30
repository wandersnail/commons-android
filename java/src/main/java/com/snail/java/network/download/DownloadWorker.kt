package com.snail.java.network.download

import com.snail.java.network.TaskInfo
import com.snail.java.network.TaskWorker
import com.snail.java.network.callback.MultiTaskListener
import com.snail.java.network.callback.TaskListener
import com.snail.java.network.exception.RetryWhenException
import com.snail.java.network.interceptor.ProgressInterceptor
import com.snail.java.utils.IOUtils
import com.snail.java.network.utils.SchedulerUtils
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile
import java.nio.channels.FileChannel

/**
 * 下载任务
 *
 * date: 2019/2/23 18:13
 * author: zengfansheng
 */
class DownloadWorker<T : DownloadInfo> : TaskWorker<T, T> {
    
    internal constructor(info: T, listener: TaskListener<T>?) : super(info, listener)
    
    internal constructor(infos: List<T>, listener: MultiTaskListener<T>?) : super(infos, listener)

    override fun execute(info: T) {
        //如果listener为空，说明不需要监听，不为空则在本地监听后，再传出去
        val observer = DownloadObserver(info, if (listener == null) null else LocalTaskListener())
        taskMap[info] = observer
        val interceptor = ProgressInterceptor(observer)
        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
        Retrofit.Builder()
            .client(client)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl(info.baseUrl)
            .build()
            .create(DownloadService::class.java)
            //断点续传
            .download("bytes=" + info.completionLength + "-", info.url)
            //失败后的retry配置
            .retryWhen(RetryWhenException())
            //写入文件
            .map { responseBody ->
                writeToDisk(responseBody, File(info.temporaryFilePath), info)
                info
            }
            .compose(SchedulerUtils.applyGeneralObservableSchedulers())
            .subscribe(observer)
    }
    
    /**
     * 暂停所有下载
     */
    fun pause() {
        taskMap.values.forEach { 
            it.dispose(false)
        }
    }

    /**
     * 暂停单个下载
     */
    fun pause(info: T) {
        taskMap[info]?.dispose(false)
    }

    /**
     * 恢复所有下载
     */
    fun resume() {
        taskMap.keys.forEach {
            if (it.state == TaskInfo.State.PAUSE) {
                execute(it)
            }
        }
    }

    /**
     * 恢复单个下载
     */
    fun resume(info: T) {
        if (info.state == TaskInfo.State.PAUSE) {
            execute(info)
        }
    }

    //写入文件在本地
    private fun writeToDisk(responseBody: ResponseBody?, file: File, info: T) {
        if (responseBody == null) {
            return
        }
        if (!file.parentFile.exists()) {
            file.parentFile.mkdirs()
        }
        val inputStream = responseBody.byteStream()
        var randomAccessFile: RandomAccessFile? = null
        var channelOut: FileChannel? = null
        try {
            val allLength: Long = if (info.contentLength == 0L) {
                responseBody.contentLength()
            } else {
                info.contentLength
            }
            randomAccessFile = RandomAccessFile(file, "rwd")
            channelOut = randomAccessFile.channel
            val mappedBuffer = channelOut.map(FileChannel.MapMode.READ_WRITE, info.completionLength, allLength - info.completionLength)
            val buffer = ByteArray(1024 * 8)
            var len = inputStream.read(buffer)
            while (len != -1) {
                mappedBuffer.put(buffer, 0, len)
                len = inputStream.read(buffer)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            IOUtils.closeQuietly(responseBody.byteStream(), channelOut, randomAccessFile)
        }
    }
}