package com.hfut.cqyzs.memorandum.repository.repositions

import android.content.Context
import android.os.Handler
import android.os.Message
import android.widget.Toast
import com.hfut.cqyzs.memorandum.local.entity.User
import com.hfut.cqyzs.memorandum.ui.register.Service.RegisterNetService
import com.hfut.cqyzs.memorandum.utils.JsonData.NetResult
import com.hfut.cqyzs.memorandum.utils.common.ToastUtil
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

class RegisterReposition {
    fun registerRetrofit(handler: Handler, context: Context?, user: User) {
        var netService: RegisterNetService

        val retrofit = Retrofit.Builder()
            .baseUrl("http://212.64.5.80:443/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build()

        netService = retrofit.create(RegisterNetService::class.java)

        val jsonData = JSONObject()
        jsonData.put("user_id", user.userId)
        jsonData.put("user_psw", user.password)
        jsonData.put("telephone", user.telephone)
        jsonData.put("email", user.email)
        jsonData.put("avatar", user.avatar)

        val requestBody = RequestBody.create(
            MediaType.parse("application/json"),
            jsonData.toString()
        )

        netService.getCall(requestBody)
            .enqueue(object : Callback<NetResult> {
                override fun onFailure(call: Call<NetResult>, t: Throwable) {
                    Toast.makeText(context, "网络访问失败！", Toast.LENGTH_SHORT).show()
                }
                override fun onResponse(call: Call<NetResult>, response: Response<NetResult>) {
                    try{
                        val temp = response.body()?.data?.status
                        if(temp == 1){
                            val message = Message()
                            handler.sendMessage(message)

                            ToastUtil.instance.showMsg(context!!,"用户注册成功")
                        }else{
                            ToastUtil.instance.showMsg(context!!,"用户注册失败")
                        }
                    } catch(e : Exception){
                        e.printStackTrace()
                    }
                }
            })
    }
}