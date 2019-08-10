package com.hfut.cqyzs.memorandum.repository.repositions

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import android.os.Handler
import android.os.Message
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.hfut.cqyzs.memorandum.MemoApp
import com.hfut.cqyzs.memorandum.local.entity.*
import com.hfut.cqyzs.memorandum.ui.home.service.*
import com.hfut.cqyzs.memorandum.utils.JsonData.NetResult
import com.hfut.cqyzs.memorandum.utils.Upload.Okgo
import com.hfut.cqyzs.memorandum.utils.tool.AudioTask
import com.hfut.cqyzs.memorandum.utils.tool.Uri2PathUtil
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@SuppressLint("SimpleDateFormat")
class HomeReposition{
    val listItem = ArrayList<ListItem>()
    val listItemLiveData: MutableLiveData<List<ListItem>> = MutableLiveData()

    val listSlideItem = ArrayList<Item>()
    val listSlideItemLiveData: MutableLiveData<List<Item>> = MutableLiveData()

    private lateinit var coInsert:Array<Content?>
    private var countInsert = 0

    private val uploadInsertHandler = @SuppressLint("HandlerLeak")
    object : Handler() {
        lateinit var context : Context
        lateinit var listContent : List<Content>
        val myHandler = @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message?) {
                Log.i("YXG","nihadasdasdasdasdasdosdsd")
                val postNetService: ContentPostNetService
                val retrofit = Retrofit.Builder()
                    .baseUrl("http://212.64.5.80:443/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build()

                postNetService = retrofit.create(ContentPostNetService::class.java)

                postNetService.syncContentInsert(msg?.obj as RequestBody)
                    .enqueue(object: retrofit2.Callback<NetResult> {
                        val thread = @SuppressLint("StaticFieldLeak")
                        object: AudioTask(null){
                            override fun myExecute() {
                                try{
                                    for(content in listContent){
                                        val oldContent = super.getDataBase().contentDao().getContentById(content.contentId)
                                        oldContent.status = 9
                                        super.getDataBase().contentDao().updateContent(oldContent)
                                    }
                                }catch (e : Exception){
                                    e.printStackTrace()
                                }
                            }
                        }

                        override fun onFailure(call: Call<NetResult>, t: Throwable) {
                            Toast.makeText(context, "插入网络访问失败！", Toast.LENGTH_SHORT).show()
                        }

                        override fun onResponse(call: Call<NetResult>, response: Response<NetResult>) {
                            try{
                                thread.execute()
                            }catch (e : Exception){
                                e.printStackTrace()
                            }
                        }
                    })
            }
        }

        override fun handleMessage(msg : Message?) {
            contentInsertListToJSON(myHandler,coInsert)
        }

        fun setHandler(context: Context, listContent:List<Content>){
            this.context = context
            this.listContent = listContent
        }
    }

    private val uploadDeleteHandler = @SuppressLint("HandlerLeak")
    object : Handler() {
        lateinit var context : Context
        lateinit var listContent : List<Content>
        val myHandler = @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message?) {
                val listContentInsertCloud = msg!!.obj as List<Content>
                if(listContentInsertCloud.isNotEmpty()){
                    coInsert = arrayOfNulls(listContentInsertCloud.size)
                    contentInsertRetrofit(context,listContentInsertCloud)
                }
            }
        }

        override fun handleMessage(msg : Message?) {
            val postNetService: ContentPostNetService
            val retrofit = Retrofit.Builder()
                .baseUrl("http://212.64.5.80:443/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()

            postNetService = retrofit.create(ContentPostNetService::class.java)

            postNetService.syncRecordDelete(msg?.obj as RequestBody)
                .enqueue(object: retrofit2.Callback<NetResult> {
                    val thread = @SuppressLint("StaticFieldLeak")
                    object: AudioTask(null){
                        override fun myExecute() {
                            try{
                                for(content in listContent){
                                    super.getDataBase().contentDao().delete(content)
                                }
                                val listContentInsertCloud = super.getDataBase().contentDao().getAllInsertCloud()
                                val message = Message()
                                message.obj = listContentInsertCloud
                                myHandler.sendMessage(message)
                            }catch (e : Exception){
                                e.printStackTrace()
                            }
                        }
                    }

                    override fun onFailure(call: Call<NetResult>, t: Throwable) {
                        Toast.makeText(context, "插入网络访问失败！", Toast.LENGTH_SHORT).show()
                    }

                    override fun onResponse(call: Call<NetResult>, response: Response<NetResult>) {
                        try{
                            thread.execute()
                        }catch (e : Exception){
                            e.printStackTrace()
                        }
                    }
                })

        }

        fun setHandler(context: Context, listContent:List<Content>){
            this.context = context
            this.listContent = listContent
        }
    }

    fun getSlideListItem(item : Item){
        listItem.clear()
        val thread = @SuppressLint("StaticFieldLeak")
        object: AudioTask(null){
            override fun myExecute() {
                try{
                    val recordList = super.getDataBase().recordDao().getAllValidSlide(item.itemId)
                    val taskList = super.getDataBase().taskDao().getAllValidSlide(item.itemId)
                    for(recordItem in recordList){
                        val miniRecord = MiniRecord(recordItem)
                        listItem.add(miniRecord)
                    }

                    for(taskItem in taskList){
                        val miniTask = MiniTask(taskItem)
                        listItem.add(miniTask)
                    }

                    listItemLiveData.postValue(listItem)
                }catch (e : Exception){
                    e.printStackTrace()
                }
            }
        }
        thread.execute()
    }

    fun getListItem(){
        listItem.clear()
        val thread = @SuppressLint("StaticFieldLeak")
        object: AudioTask(null){
            override fun myExecute() {
                try{
                    val recordList = super.getDataBase().recordDao().getAllValid()
                    val taskList = super.getDataBase().taskDao().getAllValid()
                    for(recordItem in recordList){
                        val miniRecord = MiniRecord(recordItem)
                        listItem.add(miniRecord)
                    }

                    for(taskItem in taskList){
                        val miniTask = MiniTask(taskItem)
                        listItem.add(miniTask)
                    }

                    listItemLiveData.postValue(listItem)
                }catch (e : Exception){
                    e.printStackTrace()
                }
            }
        }
        thread.execute()
    }

    fun deleteListItem(handler: Handler, position: Int){
        val thread = @SuppressLint("StaticFieldLeak")
        object : AudioTask(handler){
            override fun myExecute() {
                try{
                    val result: Int
                    val item = listItem[position]

                    if (item.type == 0){
                        val myTask = item as MiniTask
                        myTask.task?.isTrash  = 1
                        myTask.task?.status = 2
                        result = super.getDataBase().taskDao().updateTask(myTask.task!!)
                    } else {
                        val myRecord = item as MiniRecord
                        myRecord.record?.isTrash  = 1
                        myRecord.record?.status = 2
                        result = super.getDataBase().recordDao().updateRecord(myRecord.record!!)
                    }

                    if(result == 1){
                        listItem.removeAt(position)
                        listItemLiveData.postValue(listItem)
                    }

                    val message = Message()
                    message.obj = result
                    handler.sendMessage(message)
                }catch (e : Exception){
                    e.printStackTrace()
                }
            }
        }
        thread.execute()
    }

    fun getRecordItem(){
        val listRecord = ArrayList<ListItem>()
        for(i in listItem){
            if(i.type == 1)
                listRecord.add(i)
        }

        listItemLiveData.postValue(listRecord)
    }

    fun getTaskItem(){
        val listTask = ArrayList<ListItem>()
        for(i in listItem){
            if(i.type == 0)
                listTask.add(i)
        }

        listItemLiveData.postValue(listTask)
    }

    fun getAllItem() {
        listItemLiveData.postValue(listItem)
    }

    fun getAllSlideItem() {
        listSlideItem.clear()
        val thread = @SuppressLint("StaticFieldLeak")
        object: AudioTask(null){
            override fun myExecute() {
                try{
                    val itemList = super.getDataBase().itemDao().getAllValid()
                    for(item in itemList){
                        listSlideItem.add(item)
                    }

                    listSlideItemLiveData.postValue(listSlideItem)
                }catch (e : Exception){
                    e.printStackTrace()
                }
            }
        }
        thread.execute()
    }

    fun addSlideItem(handler: Handler,item: Item) {
        val thread = @SuppressLint("StaticFieldLeak")
        object: AudioTask(handler){
            override fun myExecute() {
                val user = MemoApp.instance.user!!
                item.userId = user.userId
                item.createTime = Date().time

                var result = true
                try{
                    listSlideItem.clear()
                    super.getDataBase().itemDao().insertAll(item)
                    val itemList = super.getDataBase().itemDao().getAllValid()
                    for(i in itemList){
                        listSlideItem.add(i)
                    }
                    listSlideItemLiveData.postValue(listSlideItem)
                }catch(e: Exception){
                    result = false
                }finally {
                    val message = Message()
                    message.obj = result
                    handler.sendMessage(message)
                }
            }
        }
        thread.execute()
    }

    fun deleteSlideItem(handler: Handler,position: Int) {
        val thread = @SuppressLint("StaticFieldLeak")
        object: AudioTask(handler){
            override fun myExecute() {
                val item = listSlideItem[position]
                item.isTrash = 1
                item.status = 2

                val result = StringBuilder()
                try{
                    val p1 = super.getDataBase().itemDao().updateItemTrash(item)
                    val taskList = super.getDataBase().taskDao().loadAllByItemIds(item.itemId)
                    val recordList = super.getDataBase().recordDao().loadAllByItemIds(item.itemId)

                    for(task in taskList){
                        task.isItemTrash = 1
                        task.status = 2
                        super.getDataBase().taskDao().updateRecordTrashByItem(task)
                    }

                    for(record in recordList){
                        record.isItemTrash = 1
                        record.status = 2
                        super.getDataBase().recordDao().updateRecordTrashByItem(record)
                    }

                    result.append("删除成功，共删除${p1}条清单、${taskList.size}条任务、${recordList.size}条记录")
                    listSlideItem.removeAt(position)
                    listSlideItemLiveData.postValue(listSlideItem)
                }catch(e: Exception){
                    result.append("删除失败")
                }finally {
                    val message = Message()
                    message.obj = result.toString()
                    handler.sendMessage(message)
                }

            }
        }
        thread.execute()
    }

    fun postItemCloud(context: Context) {
        val thread = @SuppressLint("StaticFieldLeak")
        object: AudioTask(null){
            override fun myExecute() {
                try{
                    val listItemInsertCloud = super.getDataBase().itemDao().getAllInsertCloud()
                    if(listItemInsertCloud.isNotEmpty()){
                        itemInsertRetrofit(context,listItemInsertCloud)
                    }

                    val listItemUpdateCloud = super.getDataBase().itemDao().getAllUpdateCloud()
                    if(listItemUpdateCloud.isNotEmpty()){
                        itemUpdateRetrofit(context,listItemUpdateCloud)
                    }

                    val listItemDeleteCloud = super.getDataBase().itemDao().getAllDeleteCloud()

                    if(listItemDeleteCloud.isNotEmpty()){
                        itemDeleteRetrofit(context,listItemDeleteCloud)

                    }
                }catch(e: Exception){
                    e.printStackTrace()
                }
            }
        }
        thread.execute()
    }

    fun itemInsertRetrofit(context: Context, listItem:List<Item>){
        val postNetService: ItemPostNetService

        val retrofit = Retrofit.Builder()
            .baseUrl("http://212.64.5.80:443/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build()

        postNetService = retrofit.create(ItemPostNetService::class.java)

        postNetService.syncItemInsert(itemListToJSON(listItem))
            .enqueue(object: Callback<NetResult> {
                override fun onFailure(call: Call<NetResult>, t: Throwable) {
                    Toast.makeText(context, "Item插入网络访问失败！", Toast.LENGTH_SHORT).show()
                }
                override fun onResponse(call: Call<NetResult>, response: Response<NetResult>) {
                    val result = response.body()?.data?.status
                    try{
                        if(result == 1){
                            Toast.makeText(context, "Item插入网络访问成功！", Toast.LENGTH_SHORT).show()
                            val thread = @SuppressLint("StaticFieldLeak")
                            object: AudioTask(null){
                                override fun myExecute() {
                                    for(item in listItem){
                                        item.status = 9
                                        super.getDataBase().itemDao().updateItem(item)
                                    }
                                }
                            }
                            thread.execute()
                        }
                    }catch (e : Exception){
                        e.printStackTrace()
                    }
                }
            })
    }

    fun itemUpdateRetrofit(context: Context, listItem:List<Item>){
        val postNetService: ItemPostNetService

        val retrofit = Retrofit.Builder()
            .baseUrl("http://212.64.5.80:443/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build()

        postNetService = retrofit.create(ItemPostNetService::class.java)

        postNetService.syncItemUpdate(itemListToJSON(listItem))
            .enqueue(object: Callback<NetResult> {
                override fun onFailure(call: Call<NetResult>, t: Throwable) {
                    Toast.makeText(context, "Item更新网络访问失败！", Toast.LENGTH_SHORT).show()
                }
                override fun onResponse(call: Call<NetResult>, response: Response<NetResult>) {
                    val result = response.body()?.data?.status
                    try{
                        if(result == 1){
                            Toast.makeText(context, "Item更新网络访问成功！", Toast.LENGTH_SHORT).show()
                            val thread = @SuppressLint("StaticFieldLeak")
                            object: AudioTask(null){
                                override fun myExecute() {
                                    for(item in listItem){
                                        item.status = 9
                                        super.getDataBase().itemDao().updateItem(item)
                                    }
                                }
                            }
                            thread.execute()
                        }
                    }catch (e : Exception){
                        e.printStackTrace()
                    }
                }
            })
    }

    fun itemDeleteRetrofit(context: Context, listItem:List<Item>){
        val postNetService: ItemPostNetService

        val retrofit = Retrofit.Builder()
            .baseUrl("http://212.64.5.80:443/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build()

        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        simpleDateFormat.timeZone = TimeZone.getTimeZone("GMT+08:00")
        val jsonObject = JSONObject()
        val jsonArray = JSONArray()
        for (item in listItem){
            val jsonTaskObject = JSONObject()
            jsonTaskObject.put("userId", item.userId)
            jsonTaskObject.put("createTime", simpleDateFormat.format(item.createTime))
            jsonArray.put(jsonTaskObject)
        }
        jsonObject.put("deleteAllList", jsonArray)

        val body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString())

        postNetService = retrofit.create(ItemPostNetService::class.java)

        postNetService.syncItemDelete(body)
            .enqueue(object: Callback<NetResult> {
                override fun onFailure(call: Call<NetResult>, t: Throwable) {
                    Toast.makeText(context, "Item删除网络访问失败！", Toast.LENGTH_SHORT).show()
                }
                override fun onResponse(call: Call<NetResult>, response: Response<NetResult>) {
                    val result = response.body()?.data?.status
                    try{
                        if(result == 1){
                            Toast.makeText(context, "Item删除网络访问成功！", Toast.LENGTH_SHORT).show()
                            val thread = @SuppressLint("StaticFieldLeak")
                            object: AudioTask(null){
                                override fun myExecute() {
                                    for(item in listItem){
                                        super.getDataBase().itemDao().delete(item)
                                    }
                                }
                            }
                            thread.execute()
                        }
                    }catch (e : Exception){
                        e.printStackTrace()
                    }
                }
            })
    }

    fun postTaskCloud(context: Context) {
        val thread = @SuppressLint("StaticFieldLeak")
        object: AudioTask(null){
            override fun myExecute() {
                try{
                    val listTaskInsertCloud = super.getDataBase().taskDao().getAllInsertCloud()
                    val listTaskDeleteCloud = super.getDataBase().taskDao().getAllDeleteCloud()
                    val listTaskUpdateCloud = super.getDataBase().taskDao().getAllUpdateCloud()

                    if(listTaskInsertCloud.isNotEmpty()){
                        taskInsertRetrofit(context,listTaskInsertCloud)
                    }
                    if(listTaskDeleteCloud.isNotEmpty()){
                        taskDeleteRetrofit(context,listTaskDeleteCloud)
                    }
                    if(listTaskUpdateCloud.isNotEmpty()){
                        taskUpdateRetrofit(context,listTaskUpdateCloud)
                    }

                }catch(e: Exception){
                    e.printStackTrace()
                }
            }
        }
        thread.execute()
    }

    fun taskInsertRetrofit(context: Context, listTask:List<Task>){
        val taskHandler = @SuppressLint("HandlerLeak")
        object : Handler(context.mainLooper){
            override fun handleMessage(msg: Message?) {
                val postNetService: TaskPostNetService

                val retrofit = Retrofit.Builder()
                    .baseUrl("http://212.64.5.80:443/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build()
                postNetService = retrofit.create(TaskPostNetService::class.java)

                postNetService.syncTaskInsert(msg?.obj as RequestBody)
                    .enqueue(object: retrofit2.Callback<NetResult> {
                        override fun onFailure(call: Call<NetResult>, t: Throwable) {
                            Toast.makeText(context, "Task插入网络访问失败！", Toast.LENGTH_SHORT).show()
                        }
                        override fun onResponse(call: Call<NetResult>, response: Response<NetResult>) {
                            val result = response.body()?.data?.status
                            try{
                                if(result == 1){
                                    Toast.makeText(context, "Task插入网络访问成功！", Toast.LENGTH_SHORT).show()
                                    val thread = @SuppressLint("StaticFieldLeak")
                                    object: AudioTask(null){
                                        override fun myExecute() {
                                            for(task in listTask){
                                                task.status = 9
                                                super.getDataBase().taskDao().updateTask(task)
                                            }
                                        }
                                    }
                                    thread.execute()
                                }
                            }catch (e : Exception){
                                e.printStackTrace()
                            }
                        }
                    })
            }
        }

        taskListToJSON(taskHandler, listTask)
    }

    fun taskUpdateRetrofit(context: Context, listTask: List<Task>){
        val taskHandler = @SuppressLint("HandlerLeak")
        object : Handler(context.mainLooper){
            override fun handleMessage(msg: Message?) {
                val postNetService: TaskPostNetService

                val retrofit = Retrofit.Builder()
                    .baseUrl("http://212.64.5.80:443/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build()

                postNetService = retrofit.create(TaskPostNetService::class.java)

                postNetService.syncTaskUpdate(msg?.obj as RequestBody)
                    .enqueue(object: retrofit2.Callback<NetResult> {
                        override fun onFailure(call: Call<NetResult>, t: Throwable) {
                            Toast.makeText(context, "Task更新网络访问失败！", Toast.LENGTH_SHORT).show()
                        }
                        override fun onResponse(call: Call<NetResult>, response: Response<NetResult>) {
                            val result = response.body()?.data?.status
                            try{
                                if(result == 1){
                                    Toast.makeText(context, "Task更新网络访问成功！", Toast.LENGTH_SHORT).show()
                                    val thread = @SuppressLint("StaticFieldLeak")
                                    object: AudioTask(null){
                                        override fun myExecute() {
                                            for(task in listTask){
                                                task.status = 9
                                                super.getDataBase().taskDao().updateTask(task)
                                            }
                                        }
                                    }
                                    thread.execute()
                                }
                            }catch (e : Exception){
                                e.printStackTrace()
                            }

                        }
                    })
            }
        }

        taskListToJSON(taskHandler, listTask)

    }

    fun taskDeleteRetrofit(context: Context, listTask:List<Task>){
        val postNetService: TaskPostNetService
        val retrofit = Retrofit.Builder()
            .baseUrl("http://212.64.5.80:443/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build()

        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        simpleDateFormat.timeZone = TimeZone.getTimeZone("GMT+08:00")
        val jsonObject = JSONObject()
        val jsonArray = JSONArray()
        for (task in listTask){
            val jsonTaskObject = JSONObject()
            jsonTaskObject.put("userId", task.user_id)
            jsonTaskObject.put("createTime", simpleDateFormat.format(task.createTime))
            jsonArray.put(jsonTaskObject)
        }
        jsonObject.put("deleteAllList", jsonArray)

        val body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString())

        postNetService = retrofit.create(TaskPostNetService::class.java)

        postNetService.syncTaskDelete(body)
            .enqueue(object: Callback<NetResult> {
                override fun onFailure(call: Call<NetResult>, t: Throwable) {
                    Toast.makeText(context, "Task删除网络访问失败！", Toast.LENGTH_SHORT).show()
                }
                override fun onResponse(call: Call<NetResult>, response: Response<NetResult>) {
                    val result = response.body()?.data?.status
                    try{
                        if(result == 1){
                            Toast.makeText(context, "Task删除网络访问成功！", Toast.LENGTH_SHORT).show()
                            val thread = @SuppressLint("StaticFieldLeak")
                            object: AudioTask(null){
                                override fun myExecute() {
                                    for (task in listTask) {
                                        task.status = 9
                                        super.getDataBase().taskDao().delete(task)
                                    }
                                }
                            }
                            thread.execute()
                        }
                    }catch (e : Exception){
                        e.printStackTrace()
                    }
                }
            })

    }

    fun postRecordCloud(context: Context) {
        val thread = @SuppressLint("StaticFieldLeak")
        object: AudioTask(null){
            override fun myExecute() {
                try{
                    val listRecordInsertCloud = super.getDataBase().recordDao().getAllInsertCloud()
                    if(listRecordInsertCloud.isNotEmpty()){
                        recordInsertRetrofit(context,listRecordInsertCloud)
                    }

                    val listRecordUpdateCloud = super.getDataBase().recordDao().getAllUpdateCloud()
                    if(listRecordUpdateCloud.isNotEmpty()){
                        recordUpdateRetrofit(context,listRecordUpdateCloud)
                    }

                    val listRecordDeleteCloud = super.getDataBase().recordDao().getAllDeleteCloud()
                    if(listRecordDeleteCloud.isNotEmpty()){
                        recordDeleteRetrofit(context,listRecordDeleteCloud)
                    }
                }catch(e: Exception){
                    e.printStackTrace()
                }
            }
        }
        thread.execute()
    }

    fun recordInsertRetrofit(context: Context, listRecord:List<Record>){
        val myHandler = @SuppressLint("HandlerLeak")
        object : Handler(context.mainLooper) {
            override fun handleMessage(msg: Message?) {
                val postNetService: RecordPostNetService
                val retrofit = Retrofit.Builder()
                    .baseUrl("http://212.64.5.80:443/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build()

                postNetService = retrofit.create(RecordPostNetService::class.java)

                postNetService.syncRecordInsert(msg?.obj as RequestBody)
                    .enqueue(object: retrofit2.Callback<NetResult> {
                        override fun onFailure(call: Call<NetResult>, t: Throwable) {
                            Toast.makeText(context, "Record插入网络访问失败！", Toast.LENGTH_SHORT).show()
                        }
                        override fun onResponse(call: Call<NetResult>, response: Response<NetResult>) {
                            val result = response.body()?.data?.status
                            try{
                                if(result == 1){
                                    Toast.makeText(context, "Record插入网络访问成功！", Toast.LENGTH_SHORT).show()
                                    val thread = @SuppressLint("StaticFieldLeak")
                                    object: AudioTask(null){
                                        override fun myExecute() {
                                            for(record in listRecord){
                                                record.status = 9
                                                super.getDataBase().recordDao().updateRecord(record)
                                            }
                                        }
                                    }
                                    thread.execute()
                                }
                            }catch (e : Exception){
                                e.printStackTrace()
                            }
                        }
                    })
            }
        }
        recordListToJSON(myHandler,listRecord)
    }

    fun recordUpdateRetrofit(context: Context, listRecord:List<Record>){
        val myHandler = @SuppressLint("HandlerLeak")
        object : Handler(context.mainLooper) {
            override fun handleMessage(msg: Message?) {
                val postNetService: RecordPostNetService
                val retrofit = Retrofit.Builder()
                    .baseUrl("http://212.64.5.80:443/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build()

                postNetService = retrofit.create(RecordPostNetService::class.java)

                postNetService.syncRecordUpdate(msg?.obj as RequestBody)
                    .enqueue(object: retrofit2.Callback<NetResult> {
                        override fun onFailure(call: Call<NetResult>, t: Throwable) {
                            Toast.makeText(context, "Record更新网络访问失败！", Toast.LENGTH_SHORT).show()
                        }
                        override fun onResponse(call: Call<NetResult>, response: Response<NetResult>) {
                            val result = response.body()?.data?.status
                            if(result == 1){
                                Toast.makeText(context, "Record更新网络访问成功！", Toast.LENGTH_SHORT).show()
                                val thread = @SuppressLint("StaticFieldLeak")
                                object: AudioTask(null){
                                    override fun myExecute() {
                                        for(record in listRecord){
                                            record.status = 9
                                            super.getDataBase().recordDao().updateRecord(record)
                                        }
                                    }
                                }
                                thread.execute()
                            }
                        }
                    })
            }
        }
        recordListToJSON(myHandler,listRecord)
    }

    fun recordDeleteRetrofit(context: Context, listRecord: List<Record>){
        val postNetService: RecordPostNetService

        val retrofit = Retrofit.Builder()
            .baseUrl("http://212.64.5.80:443/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build()

        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        simpleDateFormat.timeZone = TimeZone.getTimeZone("GMT+08:00")
        val jsonObject = JSONObject()
        val jsonArray = JSONArray()
        for (record in listRecord){
            val jsonTaskObject = JSONObject()
            jsonTaskObject.put("userId", record.userId)
            jsonTaskObject.put("createTime", simpleDateFormat.format(record.createTime))
            jsonArray.put(jsonTaskObject)
        }
        jsonObject.put("deleteAllList", jsonArray)

        val body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString())

        postNetService = retrofit.create(RecordPostNetService::class.java)

        for(record in listRecord){
            postNetService.syncRecordDelete(body)
                .enqueue(object: Callback<NetResult> {
                    override fun onFailure(call: Call<NetResult>, t: Throwable) {
                        Toast.makeText(context, "Record删除网络访问失败！", Toast.LENGTH_SHORT).show()
                    }
                    override fun onResponse(call: Call<NetResult>, response: Response<NetResult>) {
                        val result = response.body()?.data?.status
                        try{
                            if(result == 1){
                                Toast.makeText(context, "Record删除网络访问成功！", Toast.LENGTH_SHORT).show()
                                val thread = @SuppressLint("StaticFieldLeak")
                                object: AudioTask(null){
                                    override fun myExecute() {
                                        super.getDataBase().recordDao().delete(record)
                                    }
                                }
                                thread.execute()
                            }
                        }catch (e : Exception){
                            e.printStackTrace()
                        }
                    }
                })
        }
    }

    fun postContentCloud(context: Context){
        val thread = @SuppressLint("StaticFieldLeak")
        object: AudioTask(null){
            override fun myExecute() {
                try{
                    val listContentDeleteCloud = super.getDataBase().contentDao().getAllDeleteCloud()
                    val listRecordId = ArrayList<Int>()
                    for(contentDeleteCloud in listContentDeleteCloud){
                        var flag = false
                        for(recordId in listRecordId){
                            if(recordId == contentDeleteCloud.recordId){
                                flag = true
                            }
                        }
                        if(flag){
                            break
                        }
                        listRecordId.add(contentDeleteCloud.recordId)
                    }

                    if(listRecordId.isNotEmpty()){
                        contentDeleteRetrofit(context,listContentDeleteCloud,listRecordId)
                    }else{
                        val listContentInsertCloud = super.getDataBase().contentDao().getAllInsertCloud()
                        if(listContentInsertCloud.isNotEmpty()){
                            coInsert = arrayOfNulls(listContentInsertCloud.size)
                            contentInsertRetrofit(context,listContentInsertCloud)
                        }
                    }
                }catch(e: Exception){
                    e.printStackTrace()
                }
            }
        }
        thread.execute()
    }

    fun contentInsertRetrofit(context: Context, listContent:List<Content>){
        uploadInsertHandler.setHandler(context,listContent)
        for((i,content) in listContent.withIndex()){
            UploadTask(context,uploadInsertHandler).execute(Pair(i,content))
        }
    }

    fun contentDeleteRetrofit(context: Context,listContent:List<Content>,listRidContent:ArrayList<Int>){
        uploadDeleteHandler.setHandler(context,listContent)
        contentDeleteListToJSON(uploadDeleteHandler,listRidContent)
    }

    private fun itemListToJSON(listItem: List<Item>): RequestBody{
        val jsonArray = JSONArray()
        for(item in listItem){
            val jsonData = JSONObject()
            jsonData.put("userId",item.userId)
            jsonData.put("name", item.name)
            jsonData.put("isTrash", item.isTrash)
            jsonData.put("status", 9)
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            simpleDateFormat.timeZone = TimeZone.getTimeZone("GMT+08:00")
            jsonData.put("createTime",simpleDateFormat.format(Date(item.createTime)))

            jsonArray.put(jsonData)
        }

        val jsonObject = JSONObject()
        jsonObject.put("items",jsonArray)

        return RequestBody.create(MediaType.parse("application/json"), jsonObject.toString())
    }

    private fun taskListToJSON(handler: Handler, listTask:List<Task>){
        val thread = @SuppressLint("StaticFieldLeak")
        object: AudioTask(handler){
            override fun myExecute() {
                try{
                    val jsonArray = JSONArray()
                    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    simpleDateFormat.timeZone = TimeZone.getTimeZone("GMT+08:00")
                    for(task in listTask){
                        val jsonData = JSONObject()
                        val jsonTaskData = JSONObject()

                        jsonTaskData.put("taskId",task.task_id)
                        jsonTaskData.put("userId",task.user_id)
                        jsonTaskData.put("itemId",task.item_id.toString())
                        jsonTaskData.put("title", task.title)
                        jsonTaskData.put("priority", task.priority)
                        jsonTaskData.put("description", task.description)
                        jsonTaskData.put("clock", simpleDateFormat.format(task.clock!!))
                        jsonTaskData.put("deadline", simpleDateFormat.format(task.deadline!!))
                        jsonTaskData.put("isTrash", task.isTrash)
                        jsonTaskData.put("isItemTrash", task.isItemTrash)
                        jsonTaskData.put("state", task.state)
                        jsonTaskData.put("createTime", simpleDateFormat.format(Date(task.createTime)))
                        jsonTaskData.put("status", 9)
                        val itemCreateTime = super.getDataBase().itemDao().postItemCreateTime(task.item_id)

                        jsonData.put("task",jsonTaskData)
                        jsonData.put("time", simpleDateFormat.format(itemCreateTime))
                        jsonArray.put(jsonData)
                    }

                    val jsonObject = JSONObject()
                    jsonObject.put("tasks",jsonArray)

                    val message = Message()
                    message.obj = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString())
                    handler.sendMessage(message)
                }catch(e: Exception){
                    e.printStackTrace()
                }
            }
        }
        thread.execute()
    }

    private fun recordListToJSON(handler: Handler,listRecord: List<Record>){
        val thread = @SuppressLint("StaticFieldLeak")
        object: AudioTask(handler){
            override fun myExecute() {
                try{
                    val jsonArray = JSONArray()
                    for(record in listRecord){
                        val item = super.getDataBase().itemDao().getItemByItemId(record.itemId)
                        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        simpleDateFormat.timeZone = TimeZone.getTimeZone("GMT+08:00")
                        val reCreateTime = (simpleDateFormat.format(Date(record.createTime)))
                        val createTime = (simpleDateFormat.format(Date(item.createTime)))

                        val jsonRecordData = JSONObject()
                        jsonRecordData.put("recordId",record.recordId)
                        jsonRecordData.put("userId",record.userId)
                        jsonRecordData.put("itemId",0)
                        jsonRecordData.put("title",record.title)
                        jsonRecordData.put("isTrash",record.isTrash)
                        jsonRecordData.put("isItemTrash",record.isItemTrash)
                        jsonRecordData.put("createTime",reCreateTime)
                        jsonRecordData.put("background",record.background)
                        jsonRecordData.put("status",9)

                        val jsonData = JSONObject()
                        jsonData.put("record",jsonRecordData)
                        jsonData.put("time",createTime)

                        jsonArray.put(jsonData)

                    }
                    val message = Message()
                    message.obj = RequestBody.create(MediaType.parse("application/json"), JSONObject().put("records",jsonArray).toString())
                    handler.sendMessage(message)
                }catch (e : Exception){
                    e.printStackTrace()
                }
            }
        }
        thread.execute()
    }

    private fun contentInsertListToJSON(handler: Handler, listContent: Array<Content?>){
        val thread = @SuppressLint("StaticFieldLeak")
        object: AudioTask(handler){
            override fun myExecute() {
                try{
                    val jsonArray = JSONArray()
                    for(content in listContent){
                        val record = super.getDataBase().recordDao().getRecordByRecordId(content!!.recordId)
                        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        simpleDateFormat.timeZone = TimeZone.getTimeZone("GMT+08:00")
                        val createTime = (simpleDateFormat.format(Date(record.createTime)))

                        val jsonRecordData = JSONObject()
                        jsonRecordData.put("recordId",content.recordId)
                        jsonRecordData.put("recordContent",content.recordContent)
                        jsonRecordData.put("contentType",content.recordType)
                        jsonRecordData.put("contentOrder",content.order)

                        val jsonData = JSONObject()
                        jsonData.put("content",jsonRecordData)
                        jsonData.put("time",createTime)

                        jsonArray.put(jsonData)
                    }

                    val message = Message()
                    message.obj = RequestBody.create(MediaType.parse("application/json"), JSONObject().put("contents",jsonArray).toString())
                    handler.sendMessage(message)
                }catch (e : Exception){
                    e.printStackTrace()
                }
            }
        }
        thread.execute()
    }

    private fun contentDeleteListToJSON(handler: Handler, listContent: ArrayList<Int>){
        val thread = @SuppressLint("StaticFieldLeak")
        object: AudioTask(handler){
            override fun myExecute() {
                try{
                    val jsonArray = JSONArray()
                    for(content in listContent){
                        val record = super.getDataBase().recordDao().getRecordByRecordId(content)
                        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        simpleDateFormat.timeZone = TimeZone.getTimeZone("GMT+08:00")
                        val createTime = (simpleDateFormat.format(Date(record.createTime)))

                        val jsonData = JSONObject()
                        jsonData.put("userId", MemoApp.instance.user!!.userId)
                        jsonData.put("createTime",createTime)

                        jsonArray.put(jsonData)
                    }

                    val message = Message()
                    message.obj = RequestBody.create(MediaType.parse("application/json"), JSONObject().put("deleteAllList",jsonArray).toString())
                    handler.sendMessage(message)
                }catch (e : Exception){
                    e.printStackTrace()
                }
            }
        }
        thread.execute()
    }

    @SuppressLint("StaticFieldLeak")
    private inner class UploadTask(val context: Context, handler: Handler?): AsyncTask<Pair<Int, Content>, Void, Void>(){
        val myHandler = handler

        @SuppressLint("SimpleDateFormat")
        override fun doInBackground(vararg p0: Pair<Int, Content>?): Void? {
            val (position,content) = p0[0]!!
            when {
                content.recordType == 1 -> coInsert[position] = content
                content.recordType == 2 -> {
                    val str = content.recordContent
                    val pathList = str.split("/")
                    val fileName = pathList[pathList.lastIndex]
                    val filePath = str.substring(0,str.lastIndexOf('/'))

                    Okgo.getInstance().saveToInternet("$filePath/",fileName)
                    content.recordContent = "http://tryandstudy.cn:443/notepadres/$fileName"
                    coInsert[position] = content
                }
                else -> {
                    var str = Uri2PathUtil.getRealPathFromUri(context,Uri.parse(content.recordContent))
                    if(str == null){
                        str = content.recordContent
                    }
                    val pathList = str.split("/")
                    val fileName = pathList[pathList.lastIndex]
                    val filePath = str.substring(0,str.lastIndexOf('/'))

                    Okgo.getInstance().saveToInternet("$filePath/",fileName)
                    content.recordContent = "http://tryandstudy.cn:443/notepadres/$fileName"
                    coInsert[position] = content
                }
            }

            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            if (++countInsert < coInsert.size){}
            else{
                val message = Message()
                myHandler!!.sendMessage(message)
            }
        }
    }
}