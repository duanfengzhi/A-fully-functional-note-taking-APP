package com.hfut.cqyzs.memorandum.ui.home.service

import com.hfut.cqyzs.memorandum.utils.JsonData.NetResult
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ItemPostNetService {
    @Headers("Content-Type: application/json", "Accept: */*")
    @PUT("notepad/item/update")
    fun syncItemUpdate(@Body body: RequestBody): Call<NetResult>

    @POST("notepad/item/delete?")
    fun syncItemDelete(@Body body: RequestBody): Call<NetResult>

    @Headers("Content-Type: application/json", "Accept: */*")
    @POST("notepad/item/insert")
    fun syncItemInsert(@Body body: RequestBody): Call<NetResult>
}