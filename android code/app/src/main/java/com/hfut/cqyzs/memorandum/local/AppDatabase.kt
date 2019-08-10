package com.hfut.cqyzs.memorandum.local

import android.content.Context
import androidx.room.*
import com.hfut.cqyzs.memorandum.local.dao.*
import com.hfut.cqyzs.memorandum.local.entity.*

@Database(entities = [Task::class, User::class,Record::class,Item::class,Content::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun taskDao(): TaskDao
    abstract fun recordDao(): RecordDao
    abstract fun itemDao(): ItemDao
    abstract fun contentDao(): ContentDao

    companion object{
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private const val DB_NAME = "app_database"

        fun getDatabase(): AppDatabase? {
            return INSTANCE
        }

        fun initDatabase(context: Context){
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                ).build()

                INSTANCE = instance
            }
        }
    }
}
