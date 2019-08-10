package com.hfut.cqyzs.memorandum.ui.resetpsw

import android.content.Context
import android.os.Handler
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel;
import com.hfut.cqyzs.memorandum.repository.Reposition

class ResetPswViewModel : ViewModel() {
    fun reSetPsw(handler:Handler, context: Context?, user_id: String, user_psw:String) {
        Reposition.instance.resetPswReposition.resetPswRetrofit(handler,context,user_id,user_psw)
    }
}
