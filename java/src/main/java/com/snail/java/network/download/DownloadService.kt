package com.snail.java.network.download

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Streaming
import retrofit2.http.Url

/**
 * 下载接口
 *
 * date: 2019/2/23 18:05
 * author: zengfansheng
 */
internal interface DownloadService {
    @Streaming
    @GET
    fun download(@Header("RANGE") offset: String, @Url url: String): Observable<ResponseBody>
}