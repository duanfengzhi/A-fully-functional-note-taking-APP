package com.hfut.cqyzs.memorandum.repository.repositions

import android.content.Context
import android.os.Handler
import android.os.Message
import android.widget.Toast
import com.hfut.cqyzs.memorandum.ui.register.Service.ResetpswNetService
import com.hfut.cqyzs.memorandum.utils.JsonData.NetResult
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class ResetPswReposition {
    fun resetPswRetrofit(handler: Handler,context: Context?, user_id: String, user_psw:String){
        val netService: ResetpswNetService

        val retrofit = Retrofit.Builder()
            .baseUrl("http://212.64.5.80:443/")        //设置网络请求的Url地址
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build()

        netService = retrofit.create(ResetpswNetService::class.java)

        val jasonData = JSONObject()
        jasonData.put("user_id",user_id)
        jasonData.put("user_psw", user_psw)

        val requestBody = RequestBody.create(
            MediaType.parse("application/json"),
            jasonData.toString())

        netService.getCall(requestBody)
            .enqueue(object: Callback<NetResult> {
                override fun onFailure(call: Call<NetResult>, t: Throwable) {
                    Toast.makeText(context, "网络访问失败！", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call<NetResult>, response: Response<NetResult>) {
                    val temp = response.body()?.data?.status
                    try{
                        if(temp == 1){
                            val message = Message()
                            message.obj = temp
                            handler.sendMessage(message)
                        }
                    }catch (e : Exception){
                        e.printStackTrace()
                    }
                }
            })
    }
}