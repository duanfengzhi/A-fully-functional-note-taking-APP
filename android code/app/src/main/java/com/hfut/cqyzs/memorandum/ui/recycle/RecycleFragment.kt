package com.hfut.cqyzs.memorandum.ui.recycle

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Handler
import android.os.Message
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager

import com.hfut.cqyzs.memorandum.R
import com.hfut.cqyzs.memorandum.ui.recycle.service.RecycleAdapter
import com.hfut.cqyzs.memorandum.ui.recycle.service.RecycleListItem
import com.hfut.cqyzs.memorandum.utils.common.ToastUtil
import com.hfut.cqyzs.memorandum.utils.tool.OnItemClickListener
import kotlinx.android.synthetic.main.recycle_fragment.*

class RecycleFragment : Fragment() {

    companion object {
        fun newInstance() = RecycleFragment()
    }

    private lateinit var viewModel: RecycleViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.recycle_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(RecycleViewModel::class.java)
        rv_trash.layoutManager = LinearLayoutManager(context!!)
        viewModel.getTrashItemListData()

        val trashListItem = ArrayList<RecycleListItem>()

        val adapter =RecycleAdapter(context!!, R.layout.layout_recycle_item, trashListItem,
                object : OnItemClickListener{
                    override fun onItemClick(position: Int) {}

                    override fun onTrashClick(position: Int) {
                        viewModel.deleteListItem(@SuppressLint("HandlerLeak")
                        object : Handler(){
                            override fun handleMessage(msg : Message?) {
                                if (msg?.obj != null){
                                    ToastUtil.instance.showMsg(context!!, "删除成功")
                                }
                            }
                        }, position)
                    }

                    override fun onRecover(position: Int) {
                        viewModel.recoverListItem(@SuppressLint("HandlerLeak")
                        object : Handler(){
                            override fun handleMessage(msg : Message?) {
                                if (msg?.obj != null){
                                    ToastUtil.instance.showMsg(context!!, "已恢复")
                                }
                            }
                        }, position)
                    }
                })
        rv_trash.adapter = adapter

        val trashListItemObserve = Observer<List<RecycleListItem>> { newTrashListItem ->
            trashListItem.clear()
            for (i in newTrashListItem) {
                trashListItem.add(i)
            }
            adapter.notifyDataSetChanged()
        }
        viewModel.trashListLiveData.observe(this, trashListItemObserve)

        iv_sort.setOnClickListener {
            val popupMenu = PopupMenu(context, iv_sort)
            popupMenu.menuInflater.inflate(R.menu.recycle_menu, popupMenu.menu)
            popupMenu.show()

            popupMenu.setOnMenuItemClickListener {item ->
                when(item.itemId) {
                    R.id.popup_task -> {
                        viewModel.getTrashTaskItemData()
                        adapter.notifyDataSetChanged()
                    }
                    R.id.popup_record -> {
                        viewModel.getTrashRecodItemData()
                        adapter.notifyDataSetChanged()
                    }
                    R.id.popup_list -> {
                        viewModel.getTrashItemItemData()
                        adapter.notifyDataSetChanged()
                    }
                    R.id.popup_all -> {
                        viewModel.getTrashAllData()
                        adapter.notifyDataSetChanged()
                    }
                }

                true
            }

        }

        iv_back.setOnClickListener {
            findNavController().navigate(R.id.action_RecycleFragment_to_HomeFragment)
        }
    }

}
