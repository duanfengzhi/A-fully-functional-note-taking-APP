package com.hfut.cqyzs.memorandum.ui.code

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

import com.hfut.cqyzs.memorandum.R
import cn.smssdk.EventHandler
import cn.smssdk.SMSSDK
import com.hfut.cqyzs.memorandum.utils.code.VerifyCode
import kotlinx.android.synthetic.main.get_code_fragment.*

class GetCodeFragment : Fragment(){

    companion object {
        fun newInstance() = GetCodeFragment()
    }

    private lateinit var viewModel: GetCodeViewModel
    var i = 30

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProviders.of(this).get(GetCodeViewModel::class.java)
        return inflater.inflate(R.layout.get_code_fragment, container, false)
    }


    @SuppressLint("Handlerleak")
    internal var handler: Handler = object : Handler() {
        @SuppressLint("SetTextI18n")
        override fun handleMessage(msg: Message) {
            if (msg.what == -9) {
                getCode?.text = "重新发送($i)"
            } else if (msg.what == -8) {
                getCode?.text = "获取验证码"
                getCode?.isClickable = false
                i = 30
            } else {
                val event = msg.arg1
                val result = msg.arg2
                val data = msg.obj
                if (result == SMSSDK.RESULT_COMPLETE) {
                    // 短信注册成功后，返回MainActivity,然后提示
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// 提交验证码成功
                        Toast.makeText(context, "提交验证码成功", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_GetCodeFragment_to_ResetPswFragment)
                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        Toast.makeText(context, "验证码已经发送", Toast.LENGTH_SHORT).show()
                    } else {
                        (data as Throwable).printStackTrace()
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        val codeVerify  =  VerifyCode()
        super.onActivityCreated(savedInstanceState)

        init()

        getCode.setOnClickListener {

            val telephone = input_telephone.text.toString()
            if (!codeVerify.judgePhoneNums(telephone)) {
                Toast.makeText(context, "手机号码格式错误", Toast.LENGTH_SHORT).show()
            }
            // 通过sdk发送短信验证
            SMSSDK.getVerificationCode("86", telephone)
            //把按钮变成不可点击，并且显示倒计时（正在获取）
            getCode.isClickable = true
            getCode.text = "重新发送($i)"
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

        btn_next.setOnClickListener {
            val telephone = input_telephone.text.toString()
            val vihandler = @SuppressLint("HandlerLeak")
            object : Handler() {
                override fun handleMessage(msg : Message?) {
                    super.handleMessage(msg!!)
                    if(msg.obj as String == input_telephone.text.toString()){
                        SMSSDK.submitVerificationCode("86", telephone, input_code.text.toString())
                    }else{
                        Toast.makeText(context, "用户ID与手机号码不匹配！",
                            Toast.LENGTH_SHORT).show()
                    }
                }
            }
            viewModel.verifyIdentity(vihandler,context!!,input_userId.text.toString())
        }

        btn_return.setOnClickListener {
            findNavController().navigate(R.id.action_GetCodeFragment_to_LoginFragment)
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
