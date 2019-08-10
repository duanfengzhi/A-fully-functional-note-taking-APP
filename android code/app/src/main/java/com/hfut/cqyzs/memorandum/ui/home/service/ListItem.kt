package com.hfut.cqyzs.memorandum.ui.home.service

abstract class ListItem {
    var title:String = ""
    var id:Int = -1
    var type:Int = -1
    var priority:Int? = null
    var createTime: Long = -1
}