package com.hfut.cqyzs.memorandum.utils.JsonData.jsonrecept

data class JsonItem(
    val createTime: String,
    val isTrash: Int,
    val itemId: Int,
    val name: String,
    val status: Int,
    val userId: String
)