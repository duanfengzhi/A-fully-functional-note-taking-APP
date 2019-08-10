package com.hfut.cqyzs.memorandum.ui.login.service

import com.hfut.cqyzs.memorandum.utils.JsonData.NetResult
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface LoginNetService {
    @Headers("Content-Type: application/json", "Accept: */*")
    @POST("notepad/user/login")
    fun getCall(@Body body: RequestBody): Call<NetResult>
}