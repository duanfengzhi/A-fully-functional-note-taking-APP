package com.hfut.cqyzs.memorandum.repository.repositions

import android.annotation.SuppressLint
import android.content.Context
import android.os.*
import android.widget.Toast
import com.hfut.cqyzs.memorandum.MemoApp
import com.hfut.cqyzs.memorandum.local.entity.*
import com.hfut.cqyzs.memorandum.ui.login.service.GetNetService
import com.hfut.cqyzs.memorandum.ui.login.service.LoginNetService
import com.hfut.cqyzs.memorandum.utils.JsonData.*
import com.hfut.cqyzs.memorandum.utils.JsonData.jsonrecept.JsonContent
import com.hfut.cqyzs.memorandum.utils.Upload.Okgo
import com.hfut.cqyzs.memorandum.utils.tool.AudioTask
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.*
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class LoginReposition {
    private lateinit var co:Array<JsonContent?>
    private var count = 0

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://212.64.5.80:443/")
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        .build()
    private val getNetService = retrofit.create(GetNetService::class.java)

    fun loginRetrofit(handler: Handler, context: Context?, user_id:String, user_psw:String){
        val netService: LoginNetService

        val retrofit = Retrofit.Builder()
            .baseUrl("http://212.64.5.80:443/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build()

        netService = retrofit.create(LoginNetService::class.java)

        val jsonData = JSONObject()
        jsonData.put("user_id",user_id)
        jsonData.put("user_psw", user_psw)
        val requestBody = RequestBody.create(MediaType.parse("application/json"), jsonData.toString())

        netService.getCall(requestBody)
            .enqueue(object: Callback<NetResult> {
                override fun onFailure(call: Call<NetResult>, t: Throwable) {
                    Toast.makeText(context, "网络访问失败！", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call<NetResult>, response: Response<NetResult>) {

                    val result = response.body()?.data!!
                    try{
                        if(result.status == 1){
                            Toast.makeText(context, "用户登录成功！", Toast.LENGTH_SHORT).show()
                            val message = Message()
                            message.obj = result.user
                            handler.sendMessage(message)
                        }else{
                            Toast.makeText(context, "用户名或密码错误！", Toast.LENGTH_SHORT).show()
                        }
                    }catch(e: java.lang.Exception){
                        e.printStackTrace()
                    }
                }
            })
    }

    fun getLoginStatus(handler: Handler){
        val thread = @SuppressLint("StaticFieldLeak")
        object: AudioTask(handler){
            override fun myExecute() {
                try{
                    val userList = super.getDataBase().userDao().getAll()
                    if(userList.size == 1){
                        val message = Message()
                        message.obj = userList[0]
                        handler.sendMessage(message)
                    }
                }catch(e : java.lang.Exception){
                    e.printStackTrace()
                }
            }
        }
        thread.execute()
    }

    fun insertLoginUser(handler:Handler,user: User) {
        val thread = @SuppressLint("StaticFieldLeak")
        object: AudioTask(null){
            override fun myExecute() {
                try{
                    super.getDataBase().userDao().insertAll(user)
                    val message = Message()
                    handler.sendMessage(message)
                }catch (e :Exception){
                    e.printStackTrace()
                }
            }
        }
        thread.execute()
    }

    fun getCloudItem(handler:Handler,context:Context, userId: String) {
        getNetService.syncGetItem(userId)
            .enqueue(object: Callback<ItemResult> {
                override fun onFailure(call: Call<ItemResult>, t: Throwable) {
                    Toast.makeText(context, "网络访问失败！", Toast.LENGTH_SHORT).show()
                }
                @SuppressLint("SimpleDateFormat")
                override fun onResponse(call: Call<ItemResult>, response: Response<ItemResult>) {
                    val temp = response.body()?.data
                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

                    val thread = @SuppressLint("StaticFieldLeak")
                    object: AudioTask(null){
                        override fun myExecute() {
                            try{
                                for(jsonItem in temp!!){
                                    val createTime = sdf.parse(jsonItem.createTime)!!.time
                                    val item = Item(jsonItem.itemId, jsonItem.userId, jsonItem.name,
                                        createTime, jsonItem.isTrash, jsonItem.status)
                                    super.getDataBase().itemDao().insertAll(item)
                                }
                                getCloudRecord(handler,context,userId)
                                getCloudTask(handler,context,userId)
                            }catch (e : Exception){
                                e.printStackTrace()
                            }
                        }
                    }
                    thread.execute()
                }
            })
    }

    fun getCloudRecord(handler: Handler, context: Context,userId:String) {
        getNetService.syncGetRecord(userId)
            .enqueue(object: Callback<RecordResult> {
                override fun onFailure(call: Call<RecordResult>, t: Throwable) {
                    Toast.makeText(context, "网络访问失败！", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call<RecordResult>, response: Response<RecordResult>) {
                    val recordDataList = response.body()?.data
                    val thread = @SuppressLint("StaticFieldLeak")
                    object: AudioTask(null){
                        override fun myExecute() {
                            try{
                                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                sdf.timeZone = TimeZone.getTimeZone("GMT+08:00")
                                for(recordData in recordDataList!!){
                                    val createTime = sdf.parse(recordData.time)!!.time
                                    val recordTime = sdf.parse(recordData.record.createTime)!!.time
                                    val recordJson = recordData.record
                                    val itemId = super.getDataBase().itemDao().getItemByCreateTime(createTime).itemId

                                    val  record = Record(recordJson.recordId,recordJson.userId, itemId,recordJson.title,recordJson.isTrash,
                                        recordJson.isItemTrash,recordTime,recordJson.background,recordJson.status)
                                    super.getDataBase().recordDao().insertAll(record)
                                }
                                getCloudContent(handler,context,userId)
                            }catch (e : Exception){
                                e.printStackTrace()
                            }
                        }
                    }
                    thread.execute()
                }
            })
    }

    fun getCloudTask(handler:Handler,context:Context,userId: String) {
        getNetService.syncGetTask(userId)
            .enqueue(object: Callback<TaskResult> {
                override fun onFailure(call: Call<TaskResult>, t: Throwable) {
                    Toast.makeText(context, "网络访问失败！", Toast.LENGTH_SHORT).show()
                }

                @SuppressLint("SimpleDateFormat")
                override fun onResponse(call: Call<TaskResult>, response: Response<TaskResult>) {
                    val temp = response.body()?.data

                    //每个任务同步后的操作
                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    sdf.timeZone = TimeZone.getTimeZone("GMT+08:00")
                    var itemCreateTime: Long

                    val thread = @SuppressLint("StaticFieldLeak")
                    object: AudioTask(null){
                        override fun myExecute() {
                            try{
                                for(taskData in temp!!){
                                    val createTime = sdf.parse(taskData.task.createTime)!!.time
                                    val clock = sdf.parse(taskData.task.clock).time
                                    val deadline = sdf.parse(taskData.task.deadline).time
                                    itemCreateTime = sdf.parse(taskData.time)!!.time
                                    taskData.task.itemId = super.getDataBase().itemDao().getItemId(taskData.task.userId, itemCreateTime)

                                    val task = Task(taskData.task.taskId, taskData.task.userId, taskData.task.itemId, taskData.task.title, taskData.task.priority, taskData.task.description, clock, deadline, taskData.task.isTrash, taskData.task.isItemTrash, taskData.task.state, createTime, taskData.task.status)
                                    super.getDataBase().taskDao().insertAll(task)
                                }
                                val message = Message()
                                handler.sendMessage(message)
                            }catch (e : Exception){
                                e.printStackTrace()
                            }
                        }
                    }
                    thread.execute()
                }
            })
    }

    fun getCloudContent(handler:Handler?,context: Context, userId: String) {
        getNetService.syncGetContent(userId)
            .enqueue(object: Callback<ContentResult> {
                override fun onFailure(call: Call<ContentResult>, t: Throwable) {
                    Toast.makeText(context, "网络访问失败！", Toast.LENGTH_SHORT).show()
                }

                @SuppressLint("SimpleDateFormat")
                override fun onResponse(call: Call<ContentResult>, response: Response<ContentResult>) {
                    val jsonContentList = response.body()?.data
                    if(jsonContentList.isNullOrEmpty()){return}

                    val thread = @SuppressLint("StaticFieldLeak")
                    object: AudioTask(handler){
                        override fun myExecute() {
                            try{
                                for(jsonContent in co){
                                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                    sdf.timeZone = TimeZone.getTimeZone("GMT+08:00")
                                    val recordCreateTime = sdf.parse(jsonContent!!.recordCreateTime)!!.time
                                    val recordId = super.getDataBase().recordDao().getRecordIdByCreateTime(recordCreateTime).recordId

                                    val content = Content(jsonContent.contentId,recordId,jsonContent.recordContent,jsonContent.contentType,recordCreateTime,jsonContent.contentOrder,9)
                                    super.getDataBase().contentDao().insertAll(content)
                                }
                            }catch (e : Exception){
                                e.printStackTrace()
                            }
                        }
                    }

                    val downloadHandler = @SuppressLint("HandlerLeak")
                    object : Handler() {
                        override fun handleMessage(msg : Message?) {
                            thread.execute()
                        }
                    }

                    co= arrayOfNulls(size = jsonContentList.size)
                    for((position,jsonContent) in jsonContentList.withIndex()){
                        DownloadTask(downloadHandler).execute(Pair(position,jsonContent))
                    }
                }
            })
    }

    @SuppressLint("StaticFieldLeak")
    private inner class DownloadTask(handler: Handler?):AsyncTask<Pair<Int,JsonContent>,Void,Void>(){
        val myHandler = handler

        @SuppressLint("SimpleDateFormat")
        override fun doInBackground(vararg p0: Pair<Int, JsonContent>?): Void? {
            val (position,jsonContent) = p0[0]!!

            if(jsonContent.contentType == 1){
                co[position] = jsonContent
            }else{
                val str = jsonContent.recordContent
                val pathList = str.split("/")
                val fileName = pathList[pathList.lastIndex]
                jsonContent.recordContent = "${MemoApp.instance.path}/$fileName"
                co[position] = jsonContent
                Okgo.getInstance().downLoad(fileName)
            }

            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            if (++count < co.size){}
            else{
                val message = Message()
                myHandler!!.sendMessage(message)
            }
        }
    }
}
