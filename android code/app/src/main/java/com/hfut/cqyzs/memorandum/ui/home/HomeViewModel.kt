package com.hfut.cqyzs.memorandum.ui.home

import android.content.Context
import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hfut.cqyzs.memorandum.local.entity.Item
import com.hfut.cqyzs.memorandum.repository.Reposition
import com.hfut.cqyzs.memorandum.ui.home.service.ListItem

class HomeViewModel : ViewModel() {
    var listItemLiveData: LiveData<List<ListItem>> = Reposition.instance.homeReposition.listItemLiveData
    var listSlideItemLiveData: LiveData<List<Item>> = Reposition.instance.homeReposition.listSlideItemLiveData

    //主页面操作任务和记录相关的方法
    fun getListItemData(){
        Reposition.instance.homeReposition.getListItem()
    }

    fun deleteListItem(handler: Handler, position: Int){
        Reposition.instance.homeReposition.deleteListItem(handler,position)
    }

    fun getRecordItemData(){
        Reposition.instance.homeReposition.getRecordItem()
    }

    fun getTaskItemData(){
        Reposition.instance.homeReposition.getTaskItem()
    }

    fun getAllItemData(){
        Reposition.instance.homeReposition.getAllItem()
    }

    //主页面操作清单相关的方法
    fun getSlideItemData(item: Item){
        Reposition.instance.homeReposition.getSlideListItem(item)
    }

    fun addSlideItemData(handler: Handler,item: Item){
        Reposition.instance.homeReposition.addSlideItem(handler,item)
    }

    fun getAllSlideItemData(){
        Reposition.instance.homeReposition.getAllSlideItem()
    }

    fun deleteSlideItemData(handler: Handler,position: Int){
        Reposition.instance.homeReposition.deleteSlideItem(handler,position)
    }

    fun postItemCloudData(context: Context){
        Reposition.instance.homeReposition.postItemCloud(context)
        Reposition.instance.homeReposition.postRecordCloud(context)
        Reposition.instance.homeReposition.postTaskCloud(context)
        Reposition.instance.homeReposition.postContentCloud(context)
    }
}
