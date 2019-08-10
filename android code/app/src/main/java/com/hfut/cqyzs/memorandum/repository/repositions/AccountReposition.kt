package com.hfut.cqyzs.memorandum.repository.repositions

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Message
import com.hfut.cqyzs.memorandum.MemoApp
import com.hfut.cqyzs.memorandum.ui.account.service.AccountNetService
import com.hfut.cqyzs.memorandum.utils.JsonData.NetResult
import com.hfut.cqyzs.memorandum.utils.common.ToastUtil
import com.hfut.cqyzs.memorandum.utils.tool.AudioTask
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

class AccountReposition {
    fun accountRetrofit(
        handler: Handler,
        user_id: String,
        telephone: String?,
        email: String?
    ){
        val netService: AccountNetService

        val retrofit = Retrofit.Builder()
            .baseUrl("http://212.64.5.80:443/")        //设置网络请求的Url地址
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build()

        netService = retrofit.create(AccountNetService::class.java)

        val jasonData = JSONObject()
        jasonData.put("user_id",user_id)
        jasonData.put("telephone", telephone)
        jasonData.put("email", email)

        val requestBody = RequestBody.create(
            MediaType.parse("application/json"),
            jasonData.toString())

        netService.getCall(requestBody)
            .enqueue(object: Callback<NetResult> {
                override fun onFailure(call: Call<NetResult>, t: Throwable) {
                }

                override fun onResponse(call: Call<NetResult>, response: Response<NetResult>) {
                    val result = response.body()?.data?.status
                    val thread = @SuppressLint("StaticFieldLeak")
                    object : AudioTask(null) {
                        override fun myExecute() {
                            val user = super.getDataBase().userDao().getAll()[0]
                            if(result== 1 && telephone != null){
                                user.telephone = telephone
                            }else if(result== 1 && email != null){
                                user.email = email
                            }
                            super.getDataBase().userDao().updateUser(user)
                        }
                    }
                    thread.execute()

                    val message = Message()
                    message.obj = result
                    handler.sendMessage(message)
                }
            })
    }

    fun logout(handler:Handler,context:Context){
        val thread = @SuppressLint("StaticFieldLeak")
        object: AudioTask(handler){
            override fun myExecute() {
                try{
                    val user = MemoApp.instance.user
                    val result = super.getDataBase().userDao().delete(user!!)
                    if(result == 1){
                        val message = Message()
                        handler.sendMessage(message)
                        ToastUtil.instance.showMsg(context,"用户退出成功")
                    }else{
                        ToastUtil.instance.showMsg(context,"用户退出失败")
                    }
                }catch (e : Exception){
                    e.printStackTrace()
                }
            }
        }
        thread.execute()
    }
}