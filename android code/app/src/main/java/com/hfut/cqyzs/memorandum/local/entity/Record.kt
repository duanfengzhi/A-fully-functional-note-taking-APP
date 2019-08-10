package com.hfut.cqyzs.memorandum.local.entity

import androidx.room.*

@Entity(
    tableName = "record",
    foreignKeys = [ForeignKey(entity = Item::class,
        parentColumns = arrayOf("item_id"),
        childColumns = arrayOf("item_id"),
        onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = User::class,
        parentColumns = arrayOf("user_id"),
        childColumns = arrayOf("user_id"),
        onDelete = ForeignKey.CASCADE)],
    indices = [Index("item_id")]
)
data class Record (
    //记录id，自增
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="record_id")
    var recordId: Int,
    //用户名
    @ColumnInfo(name = "user_id")
    var userId: String,
    //所属清单id
    @ColumnInfo(name="item_id")
    var itemId: Int,
    //标题
    @ColumnInfo(name="title")
    var title: String,
    //此记录是否在回收站 默认0（不在回收站） 1（在回收站）
    @ColumnInfo(name="isTrash")
    var isTrash: Int,
    //所属清单是否在回收站 默认0（不在回收站） 1（在回收站）
    @ColumnInfo(name="isItemTrash")
    var isItemTrash: Int,
    //创建日期
    @ColumnInfo(name="createTime")
    var createTime: Long,
    //背景
    @ColumnInfo(name="background")
    var background: Int,
    //同步状态
    @ColumnInfo(name="status")
    var status:  Int

)