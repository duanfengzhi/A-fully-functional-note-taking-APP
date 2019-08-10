package com.hfut.cqyzs.memorandum.local.dao

import androidx.room.*
import com.hfut.cqyzs.memorandum.local.entity.Record

@Dao
interface RecordDao {
    @Query("SELECT * FROM record")
    fun getAll(): List<Record>

    @Query("SELECT * FROM record WHERE record_id IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<Record>

    @Query("SELECT * FROM record WHERE record_id == (:record_id)")
    fun loadRecordById(record_id: Int): Record

    @Insert
    fun insertAll(vararg records: Record)

    @Delete
    fun delete(record: Record): Int

    @Query("SELECT * FROM record WHERE isTrash == 0 AND isItemTrash == 0 AND status != 1")
    fun getAllValid(): List<Record>

    @Query("SELECT * FROM record WHERE isTrash == 0 AND isItemTrash == 0 AND status != 1 AND item_id = (:item_id)")
    fun getAllValidSlide(item_id: Int): List<Record>

    @Query("SELECT * FROM record WHERE (isTrash == 1 OR isItemTrash = 1) AND status != 1")
    fun getAllInvalid(): List<Record>

    @Update
    fun updateRecord(vararg record: Record): Int

    @Update
    fun updateRecordTrashByItem(vararg record: Record): Int

    @Query("SELECT * FROM record WHERE item_id = (:item_id) AND isTrash == 0")
    fun loadAllByItemIds(item_id: Int): List<Record>

    @Query("SELECT * FROM record WHERE status == 0")
    fun getAllInsertCloud():List<Record>

    @Query("SELECT record_id FROM record WHERE createTime = (:createTime)")
    fun loadBycreateTime(createTime:Long): Int

    @Query("SELECT * FROM record WHERE status == 2")
    fun getAllUpdateCloud():List<Record>

    @Query("SELECT * FROM record WHERE status == 1")
    fun getAllDeleteCloud():List<Record>

    @Query("SELECT * FROM record WHERE createTime == (:create_time)")
    fun getRecordIdByCreateTime(create_time: Long): Record

    @Query("SELECT * FROM record WHERE item_id == (:itemId) AND isItemTrash == (:isItemTrash)")
    fun getRecordIdByIsItemTrash (itemId: Int, isItemTrash: Int): List<Record>

    @Query("SELECT * FROM record WHERE record_id == (:record_id)")
    fun getRecordByRecordId(record_id: Int):Record
}