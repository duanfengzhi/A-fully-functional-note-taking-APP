package com.hfut.cqyzs.memorandum.ui.task

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import androidx.lifecycle.ViewModel
import com.hfut.cqyzs.memorandum.local.entity.Item
import com.hfut.cqyzs.memorandum.local.entity.Task
import com.hfut.cqyzs.memorandum.utils.tool.AudioTask
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TaskEditViewModel : ViewModel() {

    //根据所给id获取item所处位置
    fun getSlecteTask(handler: Handler, taskId: Int) {
        val thread = @SuppressLint("StaticFieldLeak")

        object : AudioTask(handler) {
            var selectTask: Task = Task(0, "", 0, "", 0, "", 0, 0, 0, 0, 0, 0, 0)
            override fun myExecute() {
                try {
                    selectTask = super.getDataBase().taskDao().getTaskById(taskId)
                } catch (e: Exception) {
                } finally {
                    val message = Message()
                    message.obj = selectTask
                    handler.sendMessage(message)
                }
            }
        }
    }


    fun getAllItem(itemList: ArrayList<Item>, callback: DataCallback) {
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

    fun insertTask(handler: Handler, task: Task) {
        val thread = @SuppressLint("StaticFieldLeak")
        object : AudioTask(handler) {
            override fun myExecute() {
                val user = super.getDataBase().userDao().getAll()[0]
                task.user_id = user.userId//获取当前登录用户id
                task.isTrash = 0//默认不在回收站
                task.isItemTrash = 0//默认不在回收站
                task.state = 0 //默认进行中
                val time = Date()
                val longTime: Long = time.time
                task.createTime = longTime//插入当前创建时间
                val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                simpleDateFormat.timeZone = TimeZone.getTimeZone("GMT+08:00")

                // timeString为转换格式后的字符串
                simpleDateFormat.format(Date(longTime))
                /*
                val task = super.getDataBase().taskDao().getAll()
                var timeString = simpleDateFormat.format(Date(longTime));
                var clockString = simpleDateFormat.format(Date(task.clock!!));
                Log.i("MYLOG","---task: ${task}")
                Log.i("MYLOG","---timeString: ${timeString}")
                Log.i("MYLOG","---clockString: ${clockString}")
                */

                var result = true
                try {
                    super.getDataBase().taskDao().insertAll(task)
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

    fun update() {

    }

}
