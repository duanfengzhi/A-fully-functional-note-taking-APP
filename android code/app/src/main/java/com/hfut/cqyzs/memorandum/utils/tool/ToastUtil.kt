package com.hfut.cqyzs.memorandum.utils.common

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast

class ToastUtil {
    private var mToast: Toast? = null
    private constructor()

    object Holder{
        val INSTANCE = ToastUtil()
    }

    companion object{
        val instance = Holder.INSTANCE
    }

    @SuppressLint("ShowToast")
    fun showMsg(context: Context, msg: String) {
        if (mToast == null) {
            mToast = Toast.makeText(context, msg, Toast.LENGTH_SHORT)
        } else {
            mToast!!.setText(msg)
        }
        mToast!!.show()
    }
}