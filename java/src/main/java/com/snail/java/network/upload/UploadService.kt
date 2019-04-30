package com.snail.java.network.upload

import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*

/**
 *
 *
 * date: 2019/2/28 13:48
 * author: zengfansheng
 */
internal interface UploadService {
    @POST
    @Multipart
    fun upload(@Url url: String, @Part file: MultipartBody.Part): Observable<ResponseBody>

    @POST
    @Multipart
    fun upload(@Url url: String, @PartMap args: Map<String, @JvmSuppressWildcards RequestBody>, @Part file: MultipartBody.Part): Observable<ResponseBody>
}