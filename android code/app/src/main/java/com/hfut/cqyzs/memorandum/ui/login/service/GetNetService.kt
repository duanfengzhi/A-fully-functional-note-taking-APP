package com.hfut.cqyzs.memorandum.ui.login.service

import com.hfut.cqyzs.memorandum.utils.JsonData.*
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface GetNetService {
    @GET("notepad/item/select?")
    fun syncGetItem(@Query("userId")userId: String): Call<ItemResult>

    @GET("notepad/task/select?")
    fun syncGetTask(@Query("userId") userId: String): Call<TaskResult>

    @GET("notepad/record/select?")
    fun syncGetRecord(@Query("userId")userId: String): Call<RecordResult>

    @GET("notepad/content/select?")
    fun syncGetContent(@Query("userId")userId: String): Call<ContentResult>
}