package com.snail.java.network

import io.reactivex.Observable
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*

/**
 * 
 *
 * date: 2019/2/25 10:03
 * author: zengfansheng
 */
internal interface HttpService {
    @GET
    fun get(@Url url: String): Observable<ResponseBody>

    @POST
    @FormUrlEncoded
    fun postForm(@Url url: String, @FieldMap map: Map<String, @JvmSuppressWildcards Any>): Observable<ResponseBody>

    @POST
    @Headers("Content-Type:application/json;charset=utf-8", "Accept:application/json;")
    fun postJson(@Url url: String, @Body body: RequestBody): Observable<ResponseBody>

    @POST
    fun post(@Url url: String, @Body body: RequestBody): Observable<ResponseBody>
}