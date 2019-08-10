package com.hfut.cqyzs.memorandum.ui.task

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.*
import androidx.lifecycle.ViewModelProviders
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController

import com.hfut.cqyzs.memorandum.R
import com.hfut.cqyzs.memorandum.local.entity.Item
import com.hfut.cqyzs.memorandum.local.entity.Task
import com.hfut.cqyzs.memorandum.utils.alarm.CallAlarm
import com.hfut.cqyzs.memorandum.utils.common.ToastUtil
import com.hfut.cqyzs.memorandum.utils.datepicker.CustomDatePicker
import com.hfut.cqyzs.memorandum.utils.datepicker.DateFormatUtils
import kotlinx.android.synthetic.main.task_modify_fragment.*
import java.util.*
import kotlin.collections.ArrayList

class TaskModifyFragment : Fragment() {

    companion object {
        fun newInstance() = TaskModifyFragment()
    }

    lateinit var time1Share: SharedPreferences//数据持久化
    private var priority: Int = 0
    private var mTvSelectedDate: TextView? = null
    private var mTvSelectedTime: TextView? = null
    private var mTimerPicker: CustomDatePicker? = null
    private var mDatePicker: CustomDatePicker? = null
    private var clockTime: Long = 1752316020000
    private var deadline: Long = 1752316020000
    private var mediaPlayer: MediaPlayer? = null
    private var choose_item_id = 0
    private var selectTask: Task = Task(0, "", 0, "", 0, "", 1752316020000, 1752316020000, 0, 0, 0, 0, 0)
    var itemList: ArrayList<Item> = ArrayList()
    private lateinit var viewModel: TaskModifyViewModel

    private lateinit var itemAdepter: ArrayAdapter<String>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProviders.of(this).get(TaskModifyViewModel::class.java)
        time1Share = this.context!!.getSharedPreferences("save.xml", 0)
        mediaPlayer = MediaPlayer.create(this.context, R.raw.clockmusic2)
        return inflater.inflate(R.layout.task_modify_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(TaskModifyViewModel::class.java)

        val mainHandler = @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message?) {
                sp_list.adapter = msg!!.obj as ArrayAdapter<String>
                //设置默认选择
                //获取当前所选清单属于哪一个
                for (i in 0..itemList.size - 1) {
                    if (selectTask.item_id == itemList.get(i).itemId) {
                        sp_list.setSelection(i)
                    }
                }
                sp_list.prompt = "请选择"
                //监听器
                sp_list.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        selectTask.item_id = itemList.get(position).itemId
//                        choose_item_id = itemList.get(position).itemId
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
//                        choose_item_id = itemList.get(0).itemId
                    }
                }
            }
        }
        val myCallback = object : DataCallback {
            override fun getData(data: List<String>) {
                //设置配置器
                itemAdepter = ArrayAdapter(context!!, android.R.layout.simple_spinner_item, data)
                //设置适配器样式
                itemAdepter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                //加载适配器
                val message = Message()
                message.obj = itemAdepter
                mainHandler.sendMessage(message)
            }
        }

        val myHandler = @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message?) {
                if (msg != null) {
                    //获取所要编辑的内容
                    selectTask = msg.obj as Task
                    //初始化界面
                    edt_title.setText(selectTask.title, TextView.BufferType.EDITABLE)
                    edt_des.setText(selectTask.description, TextView.BufferType.EDITABLE)
                    //获取清单数据
                    viewModel.getAllItem(itemList, myCallback)
                    //重要性选择
                    if (selectTask.priority == 1) {
                        rbtn_important.isChecked = true
                    } else {
                        rbtn_common.isChecked = true
                    }
                    important_degree.setOnCheckedChangeListener { _, checkedId ->
                        when (checkedId) {
                            R.id.rbtn_important -> {
                                selectTask.priority = 1
                            }
                            R.id.rbtn_common -> {
                                selectTask.priority = 0
                            }
                        }
                    }

                    //提醒时间选择
                    ll_time.setOnClickListener {
                        mTimerPicker!!.show(mTvSelectedTime!!.text.toString())
                    }
                    mTvSelectedTime = tv_selected_time
                    initTimerPicker()

                    //截止日期选择
                    ll_date.setOnClickListener {
                        mDatePicker!!.show(mTvSelectedDate!!.text.toString())
                    }
                    mTvSelectedDate = tv_selected_date
                    initDatePicker()
                }
            }
        }

        arguments.let {
            val selectTaskId = it?.getInt("taskId", -1)
            if (selectTaskId != null) {
                //获取选择的Task内容
                viewModel.getSelectTask(myHandler, selectTaskId)
            }
        }

        //btn_save提交信息到本地数据库
        btn_save.setOnClickListener {
            val edtTitle = edt_title.text.toString()
            val des = edt_des.text.toString()
            selectTask.title = edtTitle
            selectTask.description = des
//            val insertTask = Task(0, "", choose_item_id, edtTitle, priority, des, clockTime, deadline, 0, 0, 0, 0, 0)
            viewModel.updateTask(
                @SuppressLint("HandlerLeak")
                object : Handler() {
                    override fun handleMessage(msg: Message?) {
                        super.handleMessage(msg)
                        if (msg?.obj as Boolean) {
                            //插入提醒
                            //此处添加闹钟事件
                            if (clockTime > 0) {
                                var calendar = Calendar.getInstance()
                                calendar.setTimeInMillis(clockTime)
                                val intent = Intent(context, CallAlarm::class.java)
                                intent.putExtra("msg", selectTask.title)
                                val sender = PendingIntent.getBroadcast(
                                    context, 0, intent, 0
                                )
                                val am = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    am.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, sender)
                                }
                                val tmpS =
                                    String.format(calendar.get(Calendar.HOUR_OF_DAY).toString()) + "：" + String.format(
                                        calendar.get(
                                            Calendar.MINUTE
                                        ).toString()
                                    )
                                //SharedPreferences保存数据，并提交
                                val editor = time1Share.edit()
                                editor.putString("TIME1", tmpS)
                                editor.commit()
                                Toast.makeText(
                                    context, "设置闹钟时间为" + tmpS,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                            findNavController().navigate(R.id.action_TaskModifyFragment_to_HomeFragment)
                        } else {
                            ToastUtil.instance.showMsg(context!!, "任务插入失败")
                        }
                    }
                }, selectTask
            )
        }

        btn_back.setOnClickListener {
            findNavController().navigate(R.id.action_TaskModifyFragment_to_HomeFragment)
        }
    }


    //设置截止日期
    private fun initDatePicker() {
        val beginTimestamp = System.currentTimeMillis()
        val endTimestamp = DateFormatUtils.str2Long("2100-12-31", false)
        var show: Long = selectTask.deadline!!
        mTvSelectedDate!!.text = DateFormatUtils.long2Str(show, false)

        // 通过时间戳初始化日期，毫秒级别
        mDatePicker = CustomDatePicker(this.context, CustomDatePicker.Callback { timestamp ->
            mTvSelectedDate!!.text = DateFormatUtils.long2Str(timestamp, false)
            selectTask.deadline = timestamp
            deadline = timestamp
        }, beginTimestamp, endTimestamp)
        // 不允许点击屏幕或物理返回键关闭
        mDatePicker!!.setCancelable(false)
        // 不显示时和分
        mDatePicker!!.setCanShowPreciseTime(false)
        // 不允许循环滚动
        mDatePicker!!.setScrollLoop(false)
        // 不允许滚动动画
        mDatePicker!!.setCanShowAnim(false)
    }

    //设置提醒时间
    private fun initTimerPicker() {
        val beginTime = "2000-01-01 00:00"
        val endTime = "2100-01-01 00:00"
        // var show = DateFormatUtils.long2Str(System.currentTimeMillis(), true)
        var show = DateFormatUtils.long2Str(selectTask.clock!!, true)
        mTvSelectedTime!!.text = show
        // 通过日期字符串初始化日期，格式请用：yyyy-MM-dd HH:mm
        mTimerPicker = CustomDatePicker(
            this.context,
            CustomDatePicker.Callback { timestamp ->
                mTvSelectedTime!!.text = DateFormatUtils.long2Str(timestamp, true)
                selectTask.clock = timestamp
                clockTime = timestamp
            },
            beginTime,
            endTime
        )
        // 允许点击屏幕或物理返回键关闭
        mTimerPicker!!.setCancelable(true)
        // 显示时和分
        mTimerPicker!!.setCanShowPreciseTime(true)
        // 允许循环滚动
        mTimerPicker!!.setScrollLoop(true)
        // 允许滚动动画
        mTimerPicker!!.setCanShowAnim(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        mDatePicker!!.onDestroy()
    }

}
