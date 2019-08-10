package com.hfut.cqyzs.memorandum.local.dao

import androidx.room.*
import com.hfut.cqyzs.memorandum.local.entity.Item

@Dao
interface ItemDao {
    @Insert
    fun insertAll(vararg item: Item)

    @Query("SELECT * FROM item WHERE isTrash == 1 AND status != 1")
    fun getAllInvalid(): List<Item>

    @Delete
    fun delete(record: Item): Int

    @Update
    fun updateItem(item: Item): Int

    @Query("SELECT name FROM item  WHERE isTrash == 0" )
    fun getAllItemName(): List<String>

    @Query("SELECT * FROM item WHERE isTrash == 0")
    fun getAllValid(): List<Item>

    @Update
    fun updateItemTrash(vararg item: Item): Int

    @Query("SELECT * FROM item WHERE status == 0")
    fun getAllInsertCloud(): List<Item>

    @Query("SELECT * FROM item WHERE status == 1")
    fun getAllDeleteCloud(): List<Item>

    @Query("SELECT * FROM item WHERE status == 2")
    fun getAllUpdateCloud(): List<Item>

    @Query("SELECT * FROM item WHERE createTime == (:createTime)")
    fun getItemByCreateTime(createTime: Long): Item

    @Query("SELECT * FROM item WHERE item_id == (:item_id)")
    fun getItemByItemId(item_id: Int): Item

    @Query("SELECT item_id FROM item WHERE user_id = (:user_Id) AND createTime = (:createTime)")
    fun getItemId(user_Id: String, createTime: Long): Int

    @Query("SELECT createTime FROM item WHERE item_id = (:item_id)")
    fun postItemCreateTime(item_id: Int): Long

}