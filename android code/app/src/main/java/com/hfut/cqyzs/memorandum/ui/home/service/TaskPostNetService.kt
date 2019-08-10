package com.hfut.cqyzs.memorandum.ui.home.service

import com.hfut.cqyzs.memorandum.utils.JsonData.NetResult
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface TaskPostNetService {
    @Headers("Content-Type: application/json", "Accept: */*")
    @POST("notepad/task/insert")
    fun syncTaskInsert(@Body body: RequestBody): Call<NetResult>

    @POST("notepad/task/delete")
    fun syncTaskDelete(@Body body: RequestBody): Call<NetResult>

    @Headers("Content-Type: application/json", "Accept: */*")
    @PUT("notepad/task/update")
    fun syncTaskUpdate(@Body body: RequestBody): Call<NetResult>

}