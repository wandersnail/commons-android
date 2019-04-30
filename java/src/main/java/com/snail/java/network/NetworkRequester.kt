package com.snail.java.network

import com.snail.java.network.callback.MultiTaskListener
import com.snail.java.network.callback.TaskListener
import com.snail.java.network.converter.ResponseConverter
import com.snail.java.network.download.DownloadInfo
import com.snail.java.network.download.DownloadWorker
import com.snail.java.network.upload.UploadInfo
import com.snail.java.network.upload.UploadWorker
import com.snail.network.utils.HttpUtils
import com.snail.java.network.utils.SchedulerUtils
import io.reactivex.Observable
import io.reactivex.Observer
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.concurrent.Executors


/**
 * http网络请求，包含普通的get和post、上传、下载
 *
 * date: 2019/2/23 16:37
 * author: zengfansheng
 */
object NetworkRequester {
    internal val executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
    
    private fun applyConfiguration(baseUrl: String, configuration: Configuration?): Configuration {
        val url = HttpUtils.getBaseUrl(baseUrl)
        val config = configuration ?: Configuration()
        if (config.retrofit == null) {
            config.retrofit = Retrofit.Builder().baseUrl(url)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        }
        config.service = config.retrofit!!.create(HttpService::class.java)
        return config
    }
    
    /**
     * 单个下载
     *
     * @param info 下载信息
     * @param listener 下载监听
     */
    @JvmStatic
    fun <T : DownloadInfo> download(info: T, listener: TaskListener<T>?): DownloadWorker<T> {
        return DownloadWorker(info, listener)
    }

    /**
     * 多个同时下载
     *
     * @param infos 下载信息
     * @param listener 下载监听
     */
    @JvmStatic
    fun <T : DownloadInfo> download(infos: List<T>, listener: MultiTaskListener<T>?): DownloadWorker<T> {
        return DownloadWorker(infos, listener)
    }

    /**
     * 上传单个文件
     */
    @JvmStatic
    fun <R, T : UploadInfo<R>> upload(info: T, listener: TaskListener<T>?): UploadWorker<R, T> {
        return UploadWorker(info, listener)
    }

    /**
     * 批量上传
     */
    @JvmStatic
    fun <R, T : UploadInfo<R>> upload(infos: List<T>, listener: MultiTaskListener<T>?): UploadWorker<R, T> {
        return UploadWorker(infos, listener)
    }

    private fun <T> subscribe(observable: Observable<T>, configuration: Configuration, observer: Observer<T>? = null) {
        observable.compose(SchedulerUtils.applyGeneralObservableSchedulers()).subscribe(GeneralRequestTask(configuration, observer))
    }
    
    /**
     * 普通GET请求
     */
    @JvmStatic
    @JvmOverloads 
    fun get(url: String, observer: Observer<ResponseBody>? = null) {
        val config = applyConfiguration(url, null)        
        subscribe(config.service!!.get(url), config, observer)
    }

    /**
     * 普通GET请求
     */
    @JvmStatic
    @JvmOverloads 
    fun get(configuration: Configuration, url: String, observer: Observer<ResponseBody>? = null) {
        val config = applyConfiguration(url, configuration)
        subscribe(config.service!!.get(url), config, observer)
    }

    /**
     * 普通GET请求
     *
     * @param converter 响应体转换器
     * @param T 转到成的对象类
     */
    @JvmStatic
    fun <T> get(url: String, converter: ResponseConverter<T>, observer: Observer<T>) {
        val config = applyConfiguration(url, null)
        subscribe(HttpUtils.convertObservable(config.service!!.get(url), converter), config, observer)
    }

    /**
     * 普通GET请求
     *
     * @param converter 响应体转换器
     * @param T 转到成的对象类
     */
    @JvmStatic
    fun <T> get(configuration: Configuration, url: String, converter: ResponseConverter<T>, observer: Observer<T>) {
        val config = applyConfiguration(url, configuration)
        subscribe(HttpUtils.convertObservable(config.service!!.get(url), converter), config, observer)
    }

    /**
     * POST请求，body是json
     *
     * @param url 请求的url
     */
    @JvmStatic
    @JvmOverloads 
    fun postJson(url: String, json: String, observer: Observer<ResponseBody>? = null) {
        val requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), json)
        val config = applyConfiguration(url, null)
        subscribe(config.service!!.postJson(url, requestBody), config, observer)
    }

    /**
     * POST请求，body是json
     *
     * @param url 请求的url
     */
    @JvmOverloads 
    fun postJson(configuration: Configuration, url: String, json: String, observer: Observer<ResponseBody>? = null) {
        val requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), json)
        val config = applyConfiguration(url, configuration)
        subscribe(config.service!!.postJson(url, requestBody), config, observer)
    }

    /**
     * POST请求，body是json
     *
     * @param converter 响应体转换器
     * @param T 转到成的对象类
     */
    @JvmStatic
    fun <T> postJson(url: String, json: String, converter: ResponseConverter<T>, observer: Observer<T>) {
        val requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), json)
        val config = applyConfiguration(url, null)
        val observable = config.service!!.postJson(url, requestBody)
        subscribe(HttpUtils.convertObservable(observable, converter), config, observer)
    }

    /**
     * POST请求，body是json
     *
     * @param converter 响应体转换器
     * @param T 转到成的对象类
     */
    @JvmStatic
    fun <T> postJson(configuration: Configuration, url: String, json: String, converter: ResponseConverter<T>, observer: Observer<T>) {
        val requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), json)
        val config = applyConfiguration(url, configuration)
        val observable = config.service!!.postJson(url, requestBody)
        subscribe(HttpUtils.convertObservable(observable, converter), config, observer)
    }

    /**
     * POST请求，body是字符串
     */
    @JvmStatic
    @JvmOverloads 
    fun postText(url: String, text: String, observer: Observer<ResponseBody>? = null) {
        val requestBody = RequestBody.create(MediaType.parse("text/plain;charset=utf-8"), text)
        val config = applyConfiguration(url, null)
        subscribe(config.service!!.post(url, requestBody), config, observer)
    }

    /**
     * POST请求，body是字符串
     */
    @JvmStatic
    @JvmOverloads 
    fun postText(configuration: Configuration, url: String, text: String, observer: Observer<ResponseBody>? = null) {
        val requestBody = RequestBody.create(MediaType.parse("text/plain;charset=utf-8"), text)
        val config = applyConfiguration(url, configuration)
        subscribe(config.service!!.post(url, requestBody), config, observer)
    }

    /**
     * POST请求，body是字符串
     *
     * @param converter 响应体转换器
     * @param T 转到成的对象类
     */
    @JvmStatic
    fun <T> postText(url: String, text: String, converter: ResponseConverter<T>, observer: Observer<T>) {
        val requestBody = RequestBody.create(MediaType.parse("text/plain;charset=utf-8"), text)
        val config = applyConfiguration(url, null)
        val observable = config.service!!.post(url, requestBody)
        subscribe(HttpUtils.convertObservable(observable, converter), config, observer)
    }

    /**
     * POST请求，body是字符串
     *
     * @param converter 响应体转换器
     * @param T 转到成的对象类
     */
    @JvmStatic
    fun <T> postText(configuration: Configuration, url: String, text: String, converter: ResponseConverter<T>, observer: Observer<T>) {
        val requestBody = RequestBody.create(MediaType.parse("text/plain;charset=utf-8"), text)
        val config = applyConfiguration(url, configuration)
        val observable = config.service!!.post(url, requestBody)
        subscribe(HttpUtils.convertObservable(observable, converter), config, observer)
    }

    /**
     * POST提交表单
     *
     * @param map 参数集合
     */
    @JvmStatic
    @JvmOverloads 
    fun postForm(url: String, map: Map<String, Any>, observer: Observer<ResponseBody>? = null) {
        val config = applyConfiguration(url, null)
        subscribe(config.service!!.postForm(url, map), config, observer)
    }

    /**
     * POST提交表单
     *
     * @param map 参数集合
     */
    @JvmStatic
    @JvmOverloads 
    fun postForm(configuration: Configuration, url: String, map: Map<String, Any>, observer: Observer<ResponseBody>? = null) {
        val config = applyConfiguration(url, configuration)
        subscribe(config.service!!.postForm(url, map), config, observer)
    }

    /**
     * POST提交表单
     *
     * @param converter 响应体转换器
     * @param T 转到成的对象类
     */
    @JvmStatic
    fun <T> postForm(url: String, map: Map<String, Any>, converter: ResponseConverter<T>, observer: Observer<T>) {
        val config = applyConfiguration(url, null)
        val observable = config.service!!.postForm(url, map)
        subscribe(HttpUtils.convertObservable(observable, converter), config, observer)
    }

    /**
     * POST提交表单
     *
     * @param converter 响应体转换器
     * @param T 转到成的对象类
     */
    @JvmStatic
    fun <T> postForm(configuration: Configuration, url: String, map: Map<String, Any>, converter: ResponseConverter<T>, observer: Observer<T>) {
        val config = applyConfiguration(url, configuration)
        val observable = config.service!!.postForm(url, map)
        subscribe(HttpUtils.convertObservable(observable, converter), config, observer)
    }
}