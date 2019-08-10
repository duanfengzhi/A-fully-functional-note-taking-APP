package com.hfut.cqyzs.memorandum.local.entity

import androidx.room.*
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "item",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = arrayOf("user_id"),
        childColumns = arrayOf("user_id"),
        onDelete = ForeignKey.CASCADE)],
    indices = [Index("user_id")]
)
data class Item (
    //用户名
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "item_id")
    @SerializedName("item_id")
    var itemId: Int,
    //用户名
    @ColumnInfo(name="user_id")
    @SerializedName("user_id")
    var userId:  String,
    //清单名称

    @ColumnInfo(name="name")
    var name:  String,

    @ColumnInfo(name="createTime")
    @SerializedName("create_time")
    var createTime: Long,
    //默认0（不在回收站） 1（在回收站）
    @ColumnInfo(name="isTrash")
    @SerializedName("is_trash")
    var isTrash:  Int,
    //同步状态
    @ColumnInfo(name="status")
    var status:  Int
)