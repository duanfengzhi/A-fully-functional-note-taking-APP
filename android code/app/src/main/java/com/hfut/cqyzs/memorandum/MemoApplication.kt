package com.hfut.cqyzs.memorandum

import android.app.Application
import com.hfut.cqyzs.memorandum.local.AppDatabase
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.log.LoggerInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit


class MemoApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        //完成全局初始化
        AppDatabase.initDatabase(applicationContext)
    }
}