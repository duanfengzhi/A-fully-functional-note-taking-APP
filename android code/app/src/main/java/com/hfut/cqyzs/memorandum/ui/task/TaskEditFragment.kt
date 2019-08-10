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
import android.widget.*
import androidx.navigation.fragment.findNavController
import com.hfut.cqyzs.memorandum.R
import com.hfut.cqyzs.memorandum.local.entity.Item
import com.hfut.cqyzs.memorandum.local.entity.Task
import com.hfut.cqyzs.memorandum.utils.Upload.Okgo
import com.hfut.cqyzs.memorandum.utils.alarm.CallAlarm
import com.hfut.cqyzs.memorandum.utils.common.ToastUtil
import com.hfut.cqyzs.memorandum.utils.datepicker.CustomDatePicker
import com.hfut.cqyzs.memorandum.utils.datepicker.DateFormatUtils
import kotlinx.android.synthetic.main.task_edit_fragment.*
import java.util.*
import kotlin.collections.ArrayList


class TaskEditFragment : Fragment() {

    companion object {
        fun newInstance() = TaskEditFragment()
    }

    private lateinit var viewModel: TaskEditViewModel
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
    private lateinit var itemAdepter: ArrayAdapter<String>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProviders.of(this).get(TaskEditViewModel::class.java)
        time1Share = this.context!!.getSharedPreferences("save.xml", 0)
        mediaPlayer = MediaPlayer.create(this.context, R.raw.clockmusic2)

        return inflater.inflate(R.layout.task_edit_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(TaskEditViewModel::class.java)

        //获取清单数据
        var itemList: ArrayList<Item> = ArrayList()
        var myCallback: DataCallback = object : DataCallback {
            override fun getData(data: List<String>) {
                //设置配置器
                itemAdepter = ArrayAdapter(context!!, android.R.layout.simple_spinner_item, data)
                //设置适配器样式
                itemAdepter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                //加载适配器
                sp_list.setAdapter(itemAdepter)
                //设置默认选择
                sp_list.setSelection(0)
                sp_list.prompt = "请选择"
                //监听器
                sp_list.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        choose_item_id = itemList.get(position).itemId
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        choose_item_id = itemList.get(0).itemId
                    }
                }
            }
        }
        viewModel.getAllItem(
            itemList,
            myCallback
        )

        //重要性选择
        important_degree.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbtn_important -> {
                    priority = 1
                }
                R.id.rbtn_common -> {
                    priority = 0
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

        //btn_save提交信息到本地数据库
        btn_save.setOnClickListener {
            val edtTitle = edt_title.text.toString()
            val des = edt_des.text.toString()
            val insertTask = Task(0, "", choose_item_id, edtTitle, priority, des, clockTime, deadline, 0, 0, 0, 0, 0)
            viewModel.insertTask(
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
                                intent.putExtra("msg", insertTask.title)
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
                            findNavController().navigate(R.id.action_TaskEditFragment_to_HomeFragment)
                        } else {
                            ToastUtil.instance.showMsg(context!!, "任务插入失败")
                        }
                    }
                }, insertTask
            )
        }

        btn_back.setOnClickListener {
            findNavController().navigate(R.id.action_TaskEditFragment_to_HomeFragment)
        }

        btn_test.setOnClickListener {
            val sus = Environment.getRootDirectory().path
            Okgo.getInstance().saveToInternet(sus + "/storage/emulated/0/Pictures/", "babbabba.png")
//            Okgo.getInstance().downLoad("1658709692.png")
        }
    }

    //设置截止日期
    private fun initDatePicker() {
        val beginTimestamp = System.currentTimeMillis()
        val endTimestamp = DateFormatUtils.str2Long("2100-12-31", false)

        mTvSelectedDate!!.text = DateFormatUtils.long2Str(beginTimestamp, false)

        // 通过时间戳初始化日期，毫秒级别
        mDatePicker = CustomDatePicker(this.context, CustomDatePicker.Callback { timestamp ->
            mTvSelectedDate!!.text = DateFormatUtils.long2Str(timestamp, false)
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
        val show = DateFormatUtils.long2Str(System.currentTimeMillis(), true)
        val showFlag = System.currentTimeMillis()

        mTvSelectedTime!!.text = show

        // 通过日期字符串初始化日期，格式请用：yyyy-MM-dd HH:mm
        mTimerPicker = CustomDatePicker(
            this.context,
            CustomDatePicker.Callback { timestamp ->
                mTvSelectedTime!!.text = DateFormatUtils.long2Str(timestamp, true)
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
