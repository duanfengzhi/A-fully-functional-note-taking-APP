package com.hfut.cqyzs.memorandum.local.entity

import androidx.room.*
import java.sql.Timestamp
import java.util.*

@Entity(
    tableName = "task",
    foreignKeys = [ForeignKey(
        entity = Item::class,
        parentColumns = kotlin.arrayOf("item_id"),
        childColumns = kotlin.arrayOf("item_id"),
        onDelete = ForeignKey.CASCADE
    ),
        ForeignKey(
            entity = User::class,
            parentColumns = kotlin.arrayOf("user_id"),
            childColumns = kotlin.arrayOf("user_id"),
            onDelete = ForeignKey.CASCADE
        )],
    indices = [Index("item_id")]
)
data class Task(
    //任务id
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "task_id")
    var task_id: Int,
    //用户名
    @ColumnInfo(name = "user_id")
    var user_id: String,
    //所属清单的id
    @ColumnInfo(name = "item_id")
    var item_id: Int,
    //标题
    @ColumnInfo(name = "title")
    var title: String,
    //优先级  默认0：普通  1：重要
    @ColumnInfo(name = "priority")
    var priority: Int?,
    //描述
    @ColumnInfo(name = "description")
    var description: String?,
    //提醒时间
    @ColumnInfo(name = "clock")
    var clock: Long?,
    //截止时间
    @ColumnInfo(name = "deadline")
    var deadline: Long?,
    //自己是否在回收站  默认0（不在回收站）  1（myRecord在回收站）
    @ColumnInfo(name = "isTrash")
    var isTrash: Int,
    //所属清单是否在回收站  默认0（不在回收站）  1（在回收站）
    @ColumnInfo(name = "isItemTrash")
    var isItemTrash: Int,
    //默认0（进行中）  1（过期）
    @ColumnInfo(name = "state")
    var state: Int,
    //创建的日期
    @ColumnInfo(name = "createTime")
    var createTime: Long,
    //同步状态
    @ColumnInfo(name = "status")
    var status: Int
)