package com.hfut.cqyzs.memorandum.local.dao

import androidx.room.*
import com.hfut.cqyzs.memorandum.local.entity.Content
import com.hfut.cqyzs.memorandum.local.entity.Item

@Dao
interface ContentDao {
    @Insert
    fun insertAll(vararg contents: Content)


    @Query("SELECT * FROM content WHERE status == 0")
    fun getAllInsertCloud(): List<Content>

    @Query("SELECT * FROM content WHERE status == 1")
    fun getAllDeleteCloud(): List<Content>

    @Update
    fun updateContent(vararg content: Content): Int

    @Query("SELECT * FROM content WHERE record_id == (:record_id) AND status != 1 ORDER BY `order`")
    fun loadAllById(record_id: Int): List<Content>

    @Delete
    fun delete(content: Content): Int

    @Query("SELECT * FROM content WHERE  content_id == (:contentId)")
    fun getContentById(contentId: Int): Content
}