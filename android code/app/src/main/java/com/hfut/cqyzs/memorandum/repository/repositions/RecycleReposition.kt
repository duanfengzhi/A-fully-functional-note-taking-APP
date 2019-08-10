package com.hfut.cqyzs.memorandum.repository.repositions

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import androidx.lifecycle.MutableLiveData
import com.hfut.cqyzs.memorandum.ui.recycle.service.RecycleList

import com.hfut.cqyzs.memorandum.ui.recycle.service.RecycleListItem
import com.hfut.cqyzs.memorandum.ui.recycle.service.RecycleRecord
import com.hfut.cqyzs.memorandum.ui.recycle.service.RecycleTask
import com.hfut.cqyzs.memorandum.utils.tool.AudioTask
import java.lang.Exception

class RecycleReposition {
    val trashListItem = ArrayList<RecycleListItem>()
    val trashListItemLiveData: MutableLiveData<List<RecycleListItem>> = MutableLiveData()

    fun getTrashListItem() {
        trashListItem.clear()
        val thread = @SuppressLint("StaticFieldLeak")
        object : AudioTask(null) {
            override fun myExecute() {
                try{
                    val trashRecordList = super.getDataBase().recordDao().getAllInvalid()
                    val trashTaskList = super.getDataBase().taskDao().getAllInvalid()
                    val trashListList = super.getDataBase().itemDao().getAllInvalid()

                    for (listItem in trashListList) {
                        val trashItem = RecycleList(listItem)
                        trashListItem.add(trashItem)
                    }

                    for (recordItem in trashRecordList) {
                        val trashRecord = RecycleRecord(recordItem)
                        trashListItem.add(trashRecord)
                    }

                    for (taskItem in trashTaskList) {
                        val trashTask = RecycleTask(taskItem)
                        trashListItem.add(trashTask)
                    }

                    trashListItemLiveData.postValue(trashListItem)
                }catch (e : Exception){
                    e.printStackTrace()
                }
            }
        }
        thread.execute()
    }

    fun getTrashRecordItem() {
        val trashListRecord = ArrayList<RecycleListItem>()
        for (i in trashListItem) {
            if (i.type == 1)
                trashListRecord.add(i)
        }

        trashListItemLiveData.postValue(trashListRecord)
    }

    fun getTrashTaskItem() {
        val trashListTask = ArrayList<RecycleListItem>()
        for (i in trashListItem) {
            if (i.type == 0)
                trashListTask.add(i)
        }

        trashListItemLiveData.postValue(trashListTask)
    }

    fun getTrashItemItem() {
        val trashItemItem = ArrayList<RecycleListItem>()
        for (i in trashListItem) {
            if (i.type == 2)
                trashItemItem.add(i)
        }

        trashListItemLiveData.postValue(trashItemItem)
    }

    fun getTrashAllItem(){
        trashListItemLiveData.postValue(trashListItem)
    }

    fun deleteTrashItem(handler: Handler, position: Int){
        val thread = @SuppressLint("StaticFieldLeak")
        object : AudioTask(handler){
            override fun myExecute() {
                try {
                    var result = 0
                    val item = trashListItem[position]

                    if (item.type == 0){
                        val myTrashTask = item as RecycleTask

                        myTrashTask.task!!.status = 1

                        result = super.getDataBase().taskDao().updateTask(myTrashTask.task!!)
                    }
                    else if (item.type == 1){
                        val myTrashRecord = item as RecycleRecord
                        myTrashRecord.record!!.status = 1
                        result = super.getDataBase().recordDao().updateRecord(myTrashRecord.record!!)
                    }
                    else {
                        val myTrashItem = item as RecycleList
                        myTrashItem.item!!.status = 1

                        val listTrashRecord = super.getDataBase().recordDao().getRecordIdByIsItemTrash(myTrashItem.item!!.itemId, 1)
                        val listTrashTask = super.getDataBase().taskDao().getTaskByIsItemTrash(myTrashItem.item!!.itemId, 1)
                        for (record in listTrashRecord) {
                            record.status = 1
                            super.getDataBase().recordDao().updateRecord(record)
                        }
                        for (task in listTrashTask) {
                            task.status = 1
                            super.getDataBase().taskDao().updateTask(task)
                        }

                        result = super.getDataBase().itemDao().updateItemTrash(myTrashItem.item!!)
                    }

                    trashListItem.removeAt(position)
                    trashListItemLiveData.postValue(trashListItem)

                    val message = Message()
                    message.obj = result
                    handler.sendMessage(message)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        thread.execute()
    }

    fun recoverTrashItem(handler: Handler, position: Int){
        val thread = @SuppressLint("StaticFieldLeak")
        object : AudioTask(handler){
            override fun myExecute() {
                try{
                    var result = 0
                    val item = trashListItem[position]

                    if (item.type == 0){
                            val myTrashTask = item as RecycleTask
                            myTrashTask.task!!.isTrash = 0
                            myTrashTask.task!!.status = 2
                            result = super.getDataBase().taskDao().updateTask(myTrashTask.task!!)
                    } else if (item.type == 1){
                            val myTrashRecord = item as RecycleRecord
                            myTrashRecord.record!!.isTrash = 0
                            myTrashRecord.record!!.status = 2
                            result = super.getDataBase().recordDao().updateRecord(myTrashRecord.record!!)
                    } else {
                            val myTrashItem = item as RecycleList
                            myTrashItem.item!!.status = 2
                            myTrashItem.item!!.isTrash = 0
                            val listTrashRecord = super.getDataBase().recordDao().getRecordIdByIsItemTrash(myTrashItem.item!!.itemId, 1)
                            val listTrashTask = super.getDataBase().taskDao().getTaskByIsItemTrash(myTrashItem.item!!.itemId, 1)
                            for (record in listTrashRecord) {
                                record.isItemTrash = 0
                                record.status = 2
                                super.getDataBase().recordDao().updateRecord(record)
                            }
                            for (task in listTrashTask) {
                                task.isItemTrash = 0
                                task.status = 2
                                super.getDataBase().taskDao().updateTask(task)
                            }
                            result = super.getDataBase().itemDao().delete(myTrashItem.item!!)
                    }

                    if(result == 1){
                        trashListItem.removeAt(position)
                        trashListItemLiveData.postValue(trashListItem)
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

}