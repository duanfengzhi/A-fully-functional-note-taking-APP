package com.hfut.cqyzs.memorandum.ui.recycle

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hfut.cqyzs.memorandum.repository.Reposition
import com.hfut.cqyzs.memorandum.ui.recycle.service.RecycleListItem

class RecycleViewModel : ViewModel() {

    var trashListLiveData: LiveData<List<RecycleListItem>> = Reposition.instance.recycleReposition.trashListItemLiveData

    fun getTrashItemListData() {
        Reposition.instance.recycleReposition.getTrashListItem()
    }

    fun getTrashTaskItemData() {
        Reposition.instance.recycleReposition.getTrashTaskItem()
    }

    fun getTrashRecodItemData() {
        Reposition.instance.recycleReposition.getTrashRecordItem()
    }

    fun getTrashItemItemData() {
        Reposition.instance.recycleReposition.getTrashItemItem()
    }

    fun getTrashAllData() {
        Reposition.instance.recycleReposition.getTrashAllItem()
    }

    fun deleteListItem(handler: Handler, position: Int) {
        Reposition.instance.recycleReposition.deleteTrashItem(handler, position)
    }

    fun recoverListItem(handler: Handler, position: Int) {
        Reposition.instance.recycleReposition.recoverTrashItem(handler, position)
    }

}
