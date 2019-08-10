package com.hfut.cqyzs.memorandum.ui.record

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import androidx.lifecycle.ViewModel
import com.hfut.cqyzs.memorandum.local.entity.Content
import com.hfut.cqyzs.memorandum.local.entity.Record
import com.hfut.cqyzs.memorandum.utils.tool.AudioTask
import java.lang.Exception
import java.util.*

class RecordEditViewModel : ViewModel() {

    fun getAllItem(handler: Handler){
        val thread = @SuppressLint("StaticFieldLeak")
        object: AudioTask(null){
            override fun myExecute() {
                try{
                    val listItem = super.getDataBase().itemDao().getAllValid()
                    val message = Message()
                    message.obj = listItem
                    handler.sendMessage(message)
                }catch (e :Exception){
                    e.printStackTrace()
                }
            }
        }
        thread.execute()
    }

    fun loadFile(handler: Handler,recordID:Int){
        val strMap = mutableMapOf<String,Int>()
        val thread = @SuppressLint("StaticFieldLeak")
        object : AudioTask(handler) {
            override fun myExecute() {
                strMap["fail"] = 0
                try {
                    val record = super.getDataBase().recordDao().loadRecordById(recordID)
                    strMap[record.title] = 4
                    strMap[record.itemId.toString()] = 5
                    strMap[record.background.toString()] = 6
                    val contents = super.getDataBase().contentDao().loadAllById(recordID)
                    for (content:Content in contents){
                        when {
                            content.recordType == 1 -> strMap[content.recordContent] = 1
                            content.recordType == 2 -> strMap[content.recordContent] = 2
                            content.recordType == 3 -> strMap[content.recordContent] = 3
                        }
                    }
                }catch(e: Exception){
                    strMap["fail"] = 1
                }finally {
                    val message = Message()
                    message.obj = strMap
                    handler.sendMessage(message)
                }
            }
        }
        thread.execute()
    }

    fun saveFile(handler: Handler, strMap: Map<String,Int>, title:String,itemId:Int,background:Int,recordID:Int) {
        val thread = @SuppressLint("StaticFieldLeak")
        object : AudioTask(handler) {
            override fun myExecute() {
                var result = true
                val time = Date()
                val longTime: Long = time.time
                try{
                    var i = 1
                    var recordId = recordID
                    if(recordID == -1){
                        val user = super.getDataBase().userDao().getAll()[0]
                        val record = Record(0,user.userId,itemId,title,0,0,longTime,background,0)
                        super.getDataBase().recordDao().insertAll(record)
                        recordId = super.getDataBase().recordDao().loadBycreateTime(longTime)
                    }else{
                        val record = super.getDataBase().recordDao().loadRecordById(recordID)
                        record.itemId = itemId
                        record.background = background
                        record.title = title
                        super.getDataBase().recordDao().updateRecord(record)
                        val contents = super.getDataBase().contentDao().loadAllById(recordID)
                        for (content:Content in contents){
                            content.status = 1
                            super.getDataBase().contentDao().updateContent(content)
                        }
                    }
                    val content = Content(0,recordId,"",1,longTime,0,0)
                    for ((key,value) in strMap){
                        content.recordContent = key
                        content.order = i
                        when (value) {
                            1 -> content.recordType = 1
                            2 -> content.recordType = 2
                            3 -> content.recordType = 3
                        }
                        i++
                        super.getDataBase().contentDao().insertAll(content)
                    }
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
}
