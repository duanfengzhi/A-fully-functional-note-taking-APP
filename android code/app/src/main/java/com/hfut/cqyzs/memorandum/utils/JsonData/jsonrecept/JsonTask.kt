package com.hfut.cqyzs.memorandum.utils.JsonData.jsonrecept

data class JsonTask (
    var taskId:Int,
    var userId:String,
    var itemId:Int,
    var title:String,
    var priority:Int?,
    var description:String?,
    var clock:String?,
    var deadline:String?,
    var isTrash:Int,
    var isItemTrash:Int,
    var state:Int,
    var createTime:String,
    var status: Int
)
