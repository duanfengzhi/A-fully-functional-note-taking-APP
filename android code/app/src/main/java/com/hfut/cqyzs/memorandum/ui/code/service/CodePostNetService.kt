package com.hfut.cqyzs.memorandum.ui.code.service

import com.hfut.cqyzs.memorandum.utils.JsonData.NetResult
import retrofit2.Call
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface CodePostNetService {
    @Headers("Content-Type: application/json", "Accept: */*")
    @POST("notepad/user/getTelephone?")
    fun verifyIdentity(@Query(value = "userId") userId: String): Call<NetResult>
}