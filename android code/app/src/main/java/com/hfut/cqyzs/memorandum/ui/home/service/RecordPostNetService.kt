package com.hfut.cqyzs.memorandum.ui.home.service

import com.hfut.cqyzs.memorandum.utils.JsonData.NetResult
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface RecordPostNetService {
    @Headers("Content-Type: application/json", "Accept: */*")
    @PUT("notepad/record/update")
    fun syncRecordUpdate(@Body body: RequestBody): Call<NetResult>

    @Headers("Content-Type: application/json", "Accept: */*")
    @POST("notepad/record/delete")
    fun syncRecordDelete(@Body body: RequestBody): Call<NetResult>

    @Headers("Content-Type: application/json", "Accept: */*")
    @POST("notepad/record/insert")
    fun syncRecordInsert(@Body body: RequestBody): Call<NetResult>
}