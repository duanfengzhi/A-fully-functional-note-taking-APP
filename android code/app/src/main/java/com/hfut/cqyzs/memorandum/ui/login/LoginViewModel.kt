package com.hfut.cqyzs.memorandum.ui.login

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Message
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hfut.cqyzs.memorandum.local.entity.User
import com.hfut.cqyzs.memorandum.repository.Reposition
import com.hfut.cqyzs.memorandum.utils.tool.AudioTask


class LoginViewModel : ViewModel() {
    fun getLoginData(handler: Handler, context: Context?, user_id:String, user_psw:String){
        Reposition.instance.loginReposition.loginRetrofit(handler, context, user_id, user_psw)
    }

    fun getLoginStatusData(handler:Handler){
        Reposition.instance.loginReposition.getLoginStatus(handler)
    }

    fun insertLoginUserData(handler:Handler,user: User) {
        Reposition.instance.loginReposition.insertLoginUser(handler,user)
    }

    fun getCloudData(handler:Handler,context:Context,userId:String) {
        Reposition.instance.loginReposition.getCloudItem(handler,context,userId)
    }
}
