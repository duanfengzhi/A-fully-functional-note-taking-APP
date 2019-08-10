package com.hfut.cqyzs.memorandum.ui.account

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import androidx.lifecycle.ViewModel
import com.hfut.cqyzs.memorandum.repository.Reposition
import com.hfut.cqyzs.memorandum.utils.tool.AudioTask

class SetEmailViewModel : ViewModel() {
    fun setEmail(handler:Handler,context: Context?, email: String) {
        val thread = @SuppressLint("StaticFieldLeak")
        object : AudioTask(handler) {
            override fun myExecute() {
                val user = super.getDataBase().userDao().getAll()[0]
                Reposition.instance.accountReposition.accountRetrofit(handler, user.userId, null, email)
            }
        }
        thread.execute()
    }

}
