package com.hfut.cqyzs.memorandum.local.dao

import androidx.room.*
import com.hfut.cqyzs.memorandum.local.entity.*

@Dao
interface UserDao {
    @Insert
    fun insertAll(vararg tasks: User)

    @Query("SELECT * FROM user")
    fun getAll(): List<User>

    @Update
    fun updateUser(vararg user: User): Int

    @Delete
    fun delete(user: User): Int
}