package com.hfut.cqyzs.memorandum.local.entity

import androidx.room.*

@Entity(
    tableName = "content",
    foreignKeys = [ForeignKey(entity = Record::class,
        parentColumns = arrayOf("record_id"),
        childColumns = arrayOf("record_id"),
        onDelete = ForeignKey.CASCADE)],
    indices = [Index("record_id")]
)
data class Content(
    //内容id，自增
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="content_id")
    var contentId: Int,
    //记录id
    @ColumnInfo(name="record_id")
    var recordId: Int,
    //记录内容，文本，音频和图片
    @ColumnInfo(name="record_content")
    var recordContent: String,
    //记录内容的类型 0：文本 1：图片 2：音频
    @ColumnInfo(name="record_type")
    var recordType: Int,
    @ColumnInfo(name="createTime")
    var createTime: Long,
    @ColumnInfo(name="order")
    var order:Int,
    @ColumnInfo(name="status")
    var status:Int
)

