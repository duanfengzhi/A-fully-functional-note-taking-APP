package com.hfut.cqyzs.memorandum.repository

import com.hfut.cqyzs.memorandum.repository.repositions.*

class Reposition {
    private constructor()

    private object Holder {
        val holder= Reposition()
    }
    
    companion object {
        val instance = Holder.holder
    }

    var accountReposition = AccountReposition()
    var loginReposition = LoginReposition()
    var registerReposition = RegisterReposition()
    var resetPswReposition = ResetPswReposition()
    var homeReposition = HomeReposition()
    var recycleReposition = RecycleReposition()
}