package com.hfut.cqyzs.memorandum.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


@Entity(tableName = "user")
data class User (
    //用户名
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    @SerializedName("user_id")
    var userId: String,
    //密码
    @ColumnInfo(name = "password")
    @SerializedName("user_psw")
    var password: String,
    //手机号
    @ColumnInfo(name = "telephone")
    var telephone: String,
    //邮箱
    @ColumnInfo(name = "email")
    var email: String,
    //头像
    @ColumnInfo(name = "avatar")
    var avatar: String
)