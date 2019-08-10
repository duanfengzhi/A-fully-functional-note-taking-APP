package com.hfut.cqyzs.memorandum.ui.account

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Handler
import android.os.Message
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import cn.smssdk.EventHandler
import cn.smssdk.SMSSDK
import com.hfut.cqyzs.memorandum.R
import com.hfut.cqyzs.memorandum.utils.code.VerifyCode
import com.hfut.cqyzs.memorandum.utils.common.ToastUtil
//import kotlinx.android.synthetic.main.register_fragment.*
import kotlinx.android.synthetic.main.set_tel_fragment.*

class SetTelFragment : Fragment() {

    companion object {
        fun newInstance() = SetTelFragment()
    }

    private lateinit var viewModel: SetTelViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.set_tel_fragment, container, false)
    }

    var i = 30
    @SuppressLint("Handlerleak")
    internal var handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            if (msg.what == -9) {
                code_btn?.text = "重新发送($i)"
            } else if (msg.what == -8) {
                code_btn?.text = "获取验证码"
                code_btn?.isClickable = true
                i = 30
            } else {
                val event = msg.arg1
                val result = msg.arg2
                val data = msg.obj

                // 短信注册成功后，返回MainActivity,然后提示
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// 提交验证码成功
                    if (result == SMSSDK.RESULT_COMPLETE) {
                        Toast.makeText(
                            context, "提交验证码成功",
                            Toast.LENGTH_SHORT
                        ).show()
                        viewModel.setTel(
                            @SuppressLint("HandlerLeak")
                            object : Handler() {
                                override fun handleMessage(msg : Message?) {
                                    super.handleMessage(msg)
                                    if(msg?.obj as Int == 1){
                                        findNavController().navigate(R.id.action_SetTelFragment_to_AccountFragment)
                                    }else{
                                        ToastUtil.instance.showMsg(context!!,"修改失败")
                                    }
                                }
                            },requireContext(),edt_newTel.text.toString())
                    }else{
                        Toast.makeText(
                            context, "验证码错误",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    Toast.makeText(
                        context, "验证码已经发送",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    (data as Throwable).printStackTrace()
                }

            }
        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val codeVerify  =  VerifyCode()
        init()
        viewModel = ViewModelProviders.of(this).get(SetTelViewModel::class.java)

        btn_return.setOnClickListener {
            findNavController().navigate(R.id.action_SetTelFragment_to_AccountFragment)
        }
        code_btn.setOnClickListener {

            val telephone = old_tel.text.toString()
            if (!codeVerify.judgePhoneNums(telephone)) {
                //return
                Toast.makeText(context, "手机号码格式错误",
                    Toast.LENGTH_SHORT).show()

            } // 2. 通过sdk发送短信验证
            SMSSDK.getVerificationCode("86", telephone)
            // 3. 把按钮变成不可点击，并且显示倒计时（正在获取）
            code_btn.isClickable = true
            code_btn.text = "重新发送($i)"
            object :Thread() {
                override fun run() {
                    while (i > 0) {
                        handler.sendEmptyMessage(-9)
                        if (i <= 0) {
                            break
                        }
                        try {
                            sleep(1000)
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        }
                        i--
                    }
                    handler.sendEmptyMessage(-8)
                }
            }.start()
        }


        btn_ensureTel.setOnClickListener{

            val telephone = old_tel.text.toString()
            SMSSDK.submitVerificationCode("86", telephone, code_input.text.toString())
        }
    }
    private fun init() {
        val eventHandler = object : EventHandler() {
            override fun afterEvent(p0: Int, p1: Int, p2: Any?) {
                super.afterEvent(p0, p1, p2)
                val message = Message()
                message.arg1 = p0
                message.arg2 = p1
                message.obj = p2
                handler.sendMessage(message)
            }
        }
        //注册回调监听接口
        SMSSDK.registerEventHandler(eventHandler)
    }

    override fun onDestroy() {

        SMSSDK.unregisterAllEventHandler()
        handler.removeCallbacksAndMessages(null)

        super.onDestroy()
    }

}
