package com.hfut.cqyzs.memorandum.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.hfut.cqyzs.memorandum.MemoApp
import com.hfut.cqyzs.memorandum.R
import com.hfut.cqyzs.memorandum.ui.login.LoginFragment
import okhttp3.OkHttpClient
import com.lzy.okgo.OkGo
import com.lzy.okgo.interceptor.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import java.util.logging.Level
import com.lzy.okgo.cache.CacheEntity
import com.lzy.okgo.cache.CacheMode
import com.lzy.okgo.model.HttpHeaders
import com.lzy.okgo.model.HttpParams
import java.io.File

class MainActivity : AppCompatActivity() {
    var fragment = LoginFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initOkGo()
        permission()
        createFile()
    }

    private fun createFile(){
        val path = getExternalFilesDir(null)!!.absolutePath + "/Memo/data"
        val file = File(path)

        if (!file.exists()) {
            file.mkdirs()
        }
        MemoApp.setPath(path)
    }

    private fun permission(){
        //检查权限（NEED_PERMISSION）是否被授权 PackageManager.PERMISSION_GRANTED表示同意授权
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            //用户已经拒绝过一次，再次弹出权限申请对话框需要给用户一个解释
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission
                    .WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "请开通相关权限，否则无法正常使用本应用！", Toast.LENGTH_SHORT).show()
            }

            val unApplyedList = ArrayList<String>()
            unApplyedList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            unApplyedList.add(Manifest.permission.READ_PHONE_STATE)
            unApplyedList.add(Manifest.permission.RECORD_AUDIO)
            unApplyedList.add(Manifest.permission.READ_CONTACTS)
            unApplyedList.add(Manifest.permission.ACCESS_FINE_LOCATION)

            val unAppliedArray:Array<String> = unApplyedList.toTypedArray()

            //申请权限
            ActivityCompat.requestPermissions(this,unAppliedArray,0)
        } else {
            Toast.makeText(this, "授权成功！", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initOkGo(){
        //配置okgo
        OkGo.getInstance().init(this.application)
        val builder = OkHttpClient.Builder()
        val loggingInterceptor = HttpLoggingInterceptor("OkGo")
        //log打印级别，决定了log显示的详细程度
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY)
        //log颜色级别，决定了log在控制台显示的颜色
        loggingInterceptor.setColorLevel(Level.INFO)
        builder.addInterceptor(loggingInterceptor)

        //配置超时时间
        //全局的读取超时时间
        builder.readTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS)
        //全局的写入超时时间
        builder.writeTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS)
        //全局的连接超时时间
        builder.connectTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS)

        //---------这里给出的是示例代码,告诉你可以这么传,实际使用的时候,根据需要传,不需要就不传-------------//
        val headers = HttpHeaders()
        headers.put("commonHeaderKey1", "commonHeaderValue1")    //header不支持中文，不允许有特殊字符
        headers.put("commonHeaderKey2", "commonHeaderValue2")
        val params = HttpParams()
        params.put("commonParamsKey1", "commonParamsValue1")     //param支持中文,直接传,不要自己编码
        params.put("commonParamsKey2", "这里支持中文参数")

        OkGo.getInstance().init(this.application)                       //必须调用初始化
            .setOkHttpClient(builder.build())               //建议设置OkHttpClient，不设置将使用默认的
            .setCacheMode(CacheMode.NO_CACHE)               //全局统一缓存模式，默认不使用缓存，可以不传
            .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)   //全局统一缓存时间，默认永不过期，可以不传
            .setRetryCount(3)                               //全局统一超时重连次数，默认为三次，那么最差的情况会请求4次(一次原始请求，三次重连请求)，不需要可以设置为0
            .addCommonHeaders(headers)                      //全局公共头
            .addCommonParams(params)                       //全局公共参数
    }
}
