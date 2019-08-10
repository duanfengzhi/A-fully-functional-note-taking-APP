package com.hfut.cqyzs.memorandum.local.dao

import androidx.room.*
import com.hfut.cqyzs.memorandum.local.entity.Task

@Dao
interface TaskDao {
    @Query("SELECT * FROM task")
    fun getAll(): List<Task>

    @Query("SELECT * FROM task WHERE task_id IN (:task_ids)")
    fun loadAllByIds(task_ids: IntArray): List<Task>

    @Insert
    fun insertAll(vararg tasks: Task)

    @Delete
    fun delete(task: Task): Int

    @Query("SELECT * FROM task WHERE isTrash == 0 AND isItemTrash == 0 AND status != 1")
    fun getAllValid(): List<Task>

    @Query("SELECT * FROM task WHERE isTrash == 0 AND isItemTrash == 0 AND status != 1 AND item_id = (:item_id)")
    fun getAllValidSlide(item_id: Int): List<Task>

    @Query("SELECT * FROM task WHERE (isTrash == 1 OR isItemTrash = 1) AND status != 1 ")
    fun getAllInvalid(): List<Task>

    @Update
    fun updateTask(vararg task: Task): Int

    @Update
    fun updateRecordTrashByItem(vararg task: Task): Int

    @Query("SELECT * FROM task WHERE item_id = (:item_id) AND isTrash == 1")
    fun loadAllByItemIds(item_id: Int): List<Task>

    @Query("SELECT * FROM task WHERE status == 0")
    fun getAllInsertCloud(): List<Task>

    @Query("SELECT * FROM task WHERE status == 1")
    fun getAllDeleteCloud(): List<Task>

    @Query("SELECT * FROM task WHERE status == 2")
    fun getAllUpdateCloud(): List<Task>

    @Query("SELECT * FROM task WHERE item_id = (:itemId) AND isItemTrash = (:isItemTrash)")
    fun getTaskByIsItemTrash(itemId: Int, isItemTrash: Int): List<Task>

    @Query("SELECT * FROM task WHERE task_id = (:taskId)")
    fun getTaskById(taskId: Int): Task


}