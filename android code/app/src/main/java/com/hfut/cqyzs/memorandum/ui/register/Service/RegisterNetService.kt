package com.hfut.cqyzs.memorandum.ui.register.Service

import com.hfut.cqyzs.memorandum.utils.JsonData.NetResult
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface RegisterNetService {
    /**
     * getCall(): 接收网络请求数据的方法
     * 返回类型为Call<*>，*是接收数据的类
     */
    @Headers("Content-Type: application/json", "Accept: */*")
    @POST("notepad/user/register")
    fun getCall(@Body body: RequestBody): Call<NetResult>
}