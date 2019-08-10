package com.hfut.cqyzs.memorandum.ui.home

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.AlertDialog
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Handler
import android.os.Message
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hfut.cqyzs.memorandum.MemoApp
import com.hfut.cqyzs.memorandum.ui.home.service.*
import com.hfut.cqyzs.memorandum.utils.common.ToastUtil
import com.hfut.cqyzs.memorandum.utils.tool.CircleMenu
import com.hfut.cqyzs.memorandum.utils.tool.OnItemClickListener
import com.hfut.cqyzs.memorandum.R
import com.hfut.cqyzs.memorandum.local.entity.Item
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.android.synthetic.main.layout_home_content.*
import java.lang.Exception

class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private val imageViews = ArrayList<ImageView>()
    private val circleMenu = CircleMenu()

    companion object {
        fun newInstance() = HomeFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)

        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    @SuppressLint("NewApi", "InflateParams")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //主页的记录和任务相关
        val listItem: ArrayList<ListItem> = ArrayList()
        val listItemAdapter = HomeAdapter(context!!, R.layout.layout_home_item, listItem,
            object : OnItemClickListener{
                override fun onItemClick( position: Int) {
                    if (listItem[position].type == 0) {
                        try {
                            val miniTask = listItem[position] as MiniTask
                            findNavController().navigate(R.id.action_HomeFragment_to_TaskModifyFragment, Bundle().apply {
                                putInt("taskId", miniTask.task!!.task_id)
                            })
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    } else {
                        val record = listItem[position] as MiniRecord
                        findNavController().navigate(R.id.action_HomeFragment_to_RecordEditFragment, Bundle().apply {
                            putInt("recordId", record.record!!.recordId)
                        })
                    }
                }

                override fun onTrashClick(position: Int) {
                    viewModel.deleteListItem(
                        @SuppressLint("HandlerLeak")
                        object : Handler() {
                            override fun handleMessage(msg : Message?) {

                                super.handleMessage(msg!!)
                                if (msg.obj != null){
                                    ToastUtil.instance.showMsg(context!!, "删除成功")
                                }
                            }
                        },position
                    )
                }

                override fun onRecover(position: Int) {}
            })
        tv_user_name.text = MemoApp.instance.user!!.userId
        rv_home.layoutManager = LinearLayoutManager(context!!)
        rv_home.adapter = listItemAdapter
        rv_home.background.alpha = 0
        viewModel.getListItemData()

        val listItemObserver = Observer<List<ListItem>> { newListItem ->
            listItem.clear()
            for(i in newListItem){
                listItem.add(i)
            }
            listItemAdapter.notifyDataSetChanged()
        }
        viewModel.listItemLiveData.observe(this, listItemObserver)

        //主页的侧滑栏和条目相关
        val listSlide: ArrayList<Item> = ArrayList()
        val slideAdapter = ItemAdapter(context!!,R.layout.layout_item, listSlide,
            object : OnItemClickListener{
                override fun onRecover(position: Int) {}

                override fun onItemClick(position: Int) {
                    viewModel.getSlideItemData(listSlide[position])
                    tv_title.text = listSlide[position].name
                }

                override fun onTrashClick(position: Int) {
                    viewModel.deleteSlideItemData(
                        @SuppressLint("HandlerLeak")
                        object : Handler() {
                            override fun handleMessage(msg : Message?) {
                                super.handleMessage(msg!!)
                                ToastUtil.instance.showMsg(context!!,msg.obj as String)
                            }
                        },
                        position
                    )
                }
            })
        rv_item.layoutManager = LinearLayoutManager(context!!)
        rv_item.adapter = slideAdapter
        viewModel.getAllSlideItemData()

        val listSlideItemObserver = Observer<List<Item>> { newListItem ->
            listSlide.clear()
            for(i in newListItem){
                listSlide.add(i)
            }
            slideAdapter.notifyDataSetChanged()
        }
        viewModel.listSlideItemLiveData.observe(this, listSlideItemObserver)

        //主页的添加任务和添加记录按钮相关
        imageViews.clear()
        imageViews.add(iv_addTask)
        imageViews.add(iv_addRecord)
        var isShow = false

        //页面的点击函数
        btn_add_item.setOnClickListener{
            val view = LayoutInflater.from(context).inflate(R.layout.layout_add_item, null)
            AlertDialog.Builder(context)
                .setTitle("新建清单")
                .setView(view)
                .setPositiveButton("确定") { _, _ ->
                    val editText: EditText = view.findViewById(R.id.editText)
                    val item = Item(0,"",editText.text.toString(),0,0,0)
                    viewModel.addSlideItemData(
                        @SuppressLint("HandlerLeak")
                        object : Handler() {
                            override fun handleMessage(msg : Message?) {
                                if(msg?.obj as Boolean){
                                    ToastUtil.instance.showMsg(context!!,"清单新建成功！")
                                }
                            }
                        },
                        item
                    )
                }
                .setNeutralButton("取消", null)
                .create()
                .show()
        }

        btn_cloud.setOnClickListener{
            val button = ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
            button.duration = 1000
            btn_cloud.startAnimation(button)
            viewModel.postItemCloudData(this.context!!)
        }

        iv_add.setOnClickListener {
            if (!isShow) {
                iv_addRecord.visibility = View.VISIBLE
                iv_addTask.visibility = View.VISIBLE
                val objectAnimator = ObjectAnimator.ofFloat(iv_add, "rotation", 0f, 0f)
                objectAnimator.duration = 500
                objectAnimator.start()
                isShow= true
                circleMenu.showSectorMenu(imageViews)
            } else {
                isShow = false
                val objectAnimator = ObjectAnimator.ofFloat(iv_add, "rotation", 0f, 0f)
                objectAnimator.duration = 500
                objectAnimator.start()
                objectAnimator.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(p0: Animator?) {}
                    override fun onAnimationCancel(p0: Animator?) {}
                    override fun onAnimationStart(p0: Animator?) {}

                    override fun onAnimationEnd(animation: Animator) {
                        iv_addRecord.visibility = View.GONE
                        iv_addTask.visibility = View.GONE
                    }
                })
                circleMenu.closeSectorMenu(imageViews)
            }
        }

        iv_sort.setOnClickListener {
            val popupMenu = PopupMenu(context, iv_sort)
            popupMenu.menuInflater.inflate(R.menu.home_menu, popupMenu.menu)
            popupMenu.show()

            popupMenu.setOnMenuItemClickListener { item ->
                // 控件每一个item的点击事件
                when (item.itemId) {
                    R.id.popup_record -> {
                        viewModel.getRecordItemData()
                    }
                    R.id.popup_task -> {
                        viewModel.getTaskItemData()
                    }
                    R.id.popup_all_item -> {
                        if(tv_title.text == getString(R.string.app_name)){
                            ToastUtil.instance.showMsg(context!!,"请选择清单")
                        }
                        viewModel.getAllItemData()

                    }
                    R.id.popup_all -> {
                        viewModel.getListItemData()
                        tv_title.text = getString(R.string.app_name)
                    }
                }

                true
            }

        }

        iv_addRecord.setOnClickListener {
            findNavController().navigate(R.id.action_HomeFragment_to_RecordEditFragment)
        }

        iv_addTask.setOnClickListener{
            findNavController().navigate(R.id.action_HomeFragment_to_TaskEditFragment)
        }

        btn_add_item.setOnClickListener {
            val view = LayoutInflater.from(context).inflate(R.layout.layout_add_item, null)
            AlertDialog.Builder(context)
                .setTitle("新建清单")
                .setView(view)
                .setPositiveButton("确定") { _, _ ->
                    val editText: EditText = view.findViewById(R.id.editText)
                    val item = Item(0, "", editText.text.toString(), 0, 0, 0)
                    viewModel.addSlideItemData(
                        @SuppressLint("HandlerLeak")
                        object : Handler() {
                            override fun handleMessage(msg: Message?) {
                                super.handleMessage(msg!!)
                                if (msg.obj as Boolean) {
                                    ToastUtil.instance.showMsg(context!!, "清单新建成功！")
                                }
                            }
                        },
                        item
                    )
                }
                .setNeutralButton("取消", null)
                .create()
                .show()
        }

        account.setOnClickListener {
            findNavController().navigate(R.id.action_HomeFragment_to_AccountFragment)
        }

        iv_trash.setOnClickListener {
            findNavController().navigate(R.id.action_HomeFragment_to_RecycleFragment)
        }

        btn_setting.setOnClickListener {
            findNavController().navigate(R.id.action_HomeFragment_to_SettingFragment)
        }
    }
}
