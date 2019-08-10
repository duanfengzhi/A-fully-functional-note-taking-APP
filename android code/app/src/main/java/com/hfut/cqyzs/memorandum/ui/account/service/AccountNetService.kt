package com.hfut.cqyzs.memorandum.ui.account.service

import com.hfut.cqyzs.memorandum.utils.JsonData.NetResult
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AccountNetService {
    @Headers("Content-Type: application/json", "Accept: */*")
    @POST("notepad/user/account")
    fun getCall(@Body body: RequestBody): Call<NetResult>
}