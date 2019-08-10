package com.hfut.cqyzs.memorandum.utils.JsonData

import com.hfut.cqyzs.memorandum.local.entity.User

data class Data(
    val status: Int,
    val user: User,
    val tel: String
)