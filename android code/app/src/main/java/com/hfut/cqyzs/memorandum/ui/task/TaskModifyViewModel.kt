package com.hfut.cqyzs.memorandum.ui.task

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import androidx.lifecycle.ViewModel
import com.hfut.cqyzs.memorandum.local.entity.Item
import com.hfut.cqyzs.memorandum.local.entity.Task
import com.hfut.cqyzs.memorandum.utils.tool.AudioTask
import java.lang.Exception
import kotlin.collections.ArrayList

class TaskModifyViewModel : ViewModel() {
    //根据所给id获取item所处位置
    fun getSelectTask(handler: Handler, taskId:Int){
        val thread = @SuppressLint("StaticFieldLeak")
        object : AudioTask(handler) {
            var selectTask: Task = Task(0,"",0,"",0,"",0,0,0,0,0,0,0)
            override fun myExecute() {
                try {
                    selectTask =super.getDataBase().taskDao().getTaskById(taskId)
                } catch (e: Exception) {
                } finally {
                    val message = Message()
                    message.obj = selectTask
                    handler.sendMessage(message)
                }
            }
        }
        thread.execute()
    }


    fun getAllItem( itemList: ArrayList<Item>, callback: DataCallback) {
        val thread = @SuppressLint("StaticFieldLeak")
        object : AudioTask(null) {
            override fun myExecute() {

                try {

                    var list = super.getDataBase().itemDao().getAllValid()
                    var nameList = super.getDataBase().itemDao().getAllItemName()
                    itemList.clear()
                    itemList.addAll(list)
                    callback.getData(nameList)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        thread.execute()
    }

    fun updateTask(handler: Handler, task: Task) {
        val thread = @SuppressLint("StaticFieldLeak")
        object : AudioTask(handler) {
            override fun myExecute() {
                val user = super.getDataBase().userDao().getAll()[0]
                task.user_id = user.userId//获取当前登录用户id
                var result = true
                try {
                    super.getDataBase().taskDao().updateTask(task)
                } catch (e: Exception) {
                    result = false
                } finally {
                    val message = Message()
                    message.obj = result
                    handler.sendMessage(message)
                }
            }
        }
        thread.execute()
    }
}
