package com.hfut.cqyzs.memorandum.utils.tool

import android.os.AsyncTask
import android.os.Handler
import android.os.Message
import com.hfut.cqyzs.memorandum.local.AppDatabase

abstract class AudioTask(handler: Handler?): AsyncTask<Void, Void, Any?>() {
    private lateinit var db: AppDatabase

    override fun doInBackground(vararg p0: Void?): Any? {
        db = AppDatabase.getDatabase()!!
        return myExecute()
    }

    protected fun getDataBase(): AppDatabase {
        return db
    }

    abstract fun myExecute():Any?
}