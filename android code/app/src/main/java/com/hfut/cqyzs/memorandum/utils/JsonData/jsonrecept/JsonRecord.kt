package com.hfut.cqyzs.memorandum.utils.JsonData.jsonrecept

data class JsonRecord (
    var recordId: Int,
    var userId: String,
    var itemId: Int,
    var title: String,
    var isTrash: Int,
    var isItemTrash: Int,
    var createTime: String,
    var background: Int,
    var status:  Int
)

