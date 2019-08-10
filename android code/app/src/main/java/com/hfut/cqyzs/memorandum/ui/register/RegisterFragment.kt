package com.hfut.cqyzs.memorandum.ui.register

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import cn.smssdk.EventHandler
import cn.smssdk.SMSSDK

import com.hfut.cqyzs.memorandum.R
import com.hfut.cqyzs.memorandum.local.entity.User
import com.hfut.cqyzs.memorandum.utils.code.VerifyCode
import kotlinx.android.synthetic.main.register_fragment.*

class RegisterFragment : Fragment() {
    companion object {
        fun newInstance() = RegisterFragment()
    }

    private lateinit var viewModel: RegisterViewModel
    var i = 30

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProviders.of(this).get(RegisterViewModel::class.java)
        return inflater.inflate(R.layout.register_fragment, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val codeVerify  =  VerifyCode()
        init()

        btn_identify.setOnClickListener {
            val telephone = edt_phone.text.toString()
            if (!codeVerify.judgePhoneNums(telephone)) {
                Toast.makeText(context, "手机号码格式错误", Toast.LENGTH_SHORT).show()
            }
            //通过sdk发送短信验证
            SMSSDK.getVerificationCode("86", telephone)

            //把按钮变成不可点击，并且显示倒计时（正在获取）
            btn_identify.isClickable = false
            btn_identify.text = "重新发送($i)"

            object :Thread() {
                override fun run() {
                    while (i > 0) {
                        handler.sendEmptyMessage(-9)
                        if (i <= 0) { break }
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

        btn_register.setOnClickListener{
            val telephone = edt_phone.text.toString()
            SMSSDK.submitVerificationCode("86", telephone, edt_identify.text.toString())
        }
        btn_return.setOnClickListener {
            findNavController().navigate(R.id.action_RegisterFragment_to_LoginFragment)
        }
    }

    var registerHandler: Handler = @SuppressLint("HandlerLeak")
    object : Handler(){
        override fun handleMessage(msg: Message) {
            findNavController().navigate(R.id.action_RegisterFragment_to_LoginFragment)
        }
    }

    @SuppressLint("Handlerleak")
    internal var handler: Handler = object : Handler() {
        @SuppressLint("SetTextI18n")
        override fun handleMessage(msg: Message) {
            if (msg.what == -9) {
                btn_identify?.text = "重新发送($i)"
            } else if (msg.what == -8) {
                btn_identify?.text = "获取验证码"
                btn_identify?.isClickable = true
                i = 30
            } else {
                val event = msg.arg1
                val result = msg.arg2
                val data = msg.obj

                // 短信注册成功后，返回MainActivity,然后提示
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// 提交验证码成功
                    if (result == SMSSDK.RESULT_COMPLETE) {
                        Toast.makeText(context, "提交验证码成功", Toast.LENGTH_SHORT).show()
                        if (edt_password.text.toString() == editText4.text.toString()) {
                            val user = User(edt_name.text.toString(), edt_password.text.toString(),
                                edt_phone.text.toString(), edt_email.text.toString(), "s")
                            viewModel.register(registerHandler,context, user)
                        } else {
                            Toast.makeText(context, "两次密码不一致", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(context, "验证码错误", Toast.LENGTH_SHORT).show()
                    }
                }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    Toast.makeText(context, "验证码已经发送", Toast.LENGTH_SHORT).show()
                } else {
                    (data as Throwable).printStackTrace()
                }
            }
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
