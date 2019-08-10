package com.hfut.cqyzs.memorandum.ui.home.service

import com.hfut.cqyzs.memorandum.utils.JsonData.NetResult
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface ContentPostNetService {
    @Headers("Content-Type: application/json", "Accept: */*")
    @POST("notepad/content/delete")
    fun syncRecordDelete(@Body body: RequestBody): Call<NetResult>

    @Headers("Content-Type: application/json", "Accept: */*")
    @POST("notepad/content/insert")
    fun syncContentInsert(@Body body: RequestBody): Call<NetResult>
}