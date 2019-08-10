package com.hfut.cqyzs.memorandum

import com.hfut.cqyzs.memorandum.local.entity.User

class MemoApp private constructor() {
    var user:User? = null
    var path:String? = null

    object Holder{
        val INSTANCE = MemoApp()
    }

    companion object{
        val instance = Holder.INSTANCE

        fun setUser(user: User){
            instance.user = user
        }

        fun setPath(path:String){
            instance.path = path
        }
    }
}