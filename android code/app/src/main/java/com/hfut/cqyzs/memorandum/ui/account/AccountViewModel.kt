package com.hfut.cqyzs.memorandum.ui.account

import android.content.Context
import android.os.Handler
import androidx.lifecycle.ViewModel;
import com.hfut.cqyzs.memorandum.repository.Reposition

class AccountViewModel : ViewModel() {
    fun account(handler: Handler, context: Context?, user_id: String, telephone: String, email: String) {
        Reposition.instance.accountReposition.accountRetrofit(handler, user_id, telephone, email)
    }

    fun logout(handler:Handler,context:Context){
        Reposition.instance.accountReposition.logout(handler,context)
    }
}