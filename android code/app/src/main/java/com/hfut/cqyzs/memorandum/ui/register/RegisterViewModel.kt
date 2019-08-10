package com.hfut.cqyzs.memorandum.ui.register

import android.content.Context
import android.os.Handler
import androidx.lifecycle.ViewModel;
import com.hfut.cqyzs.memorandum.local.entity.User
import com.hfut.cqyzs.memorandum.repository.Reposition

class RegisterViewModel : ViewModel() {
    fun register(handler: Handler, context: Context?, user: User){
        Reposition.instance.registerReposition.registerRetrofit(handler,context,user)
    }
}
