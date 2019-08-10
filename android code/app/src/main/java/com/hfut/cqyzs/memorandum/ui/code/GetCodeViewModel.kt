package com.hfut.cqyzs.memorandum.ui.code

import android.content.Context
import android.os.Handler
import android.os.Message
import android.widget.Toast
import androidx.lifecycle.ViewModel;
import com.hfut.cqyzs.memorandum.ui.code.service.CodePostNetService
import com.hfut.cqyzs.memorandum.utils.JsonData.NetResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

class GetCodeViewModel : ViewModel() {
    fun verifyIdentity(handler: Handler, context: Context, userId : String){
        val postNetService: CodePostNetService

        val retrofit = Retrofit.Builder()
            .baseUrl("http://212.64.5.80:443/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build()

        postNetService = retrofit.create(CodePostNetService::class.java)

        postNetService.verifyIdentity(userId)
            .enqueue(object: Callback<NetResult> {
                override fun onFailure(call: Call<NetResult>, t: Throwable) {
                    Toast.makeText(context, "网络访问失败！", Toast.LENGTH_SHORT).show()
                }
                override fun onResponse(call: Call<NetResult>, response: Response<NetResult>) {
                    var temp = response.body()?.data?.tel!!
                    try{
                        if(temp != null){
                            val message = Message()
                            message.obj = temp
                            handler.sendMessage(message)
                        }

                    }catch (e :Exception){
                        e.printStackTrace()

                    }

                }
            })
    }
}
