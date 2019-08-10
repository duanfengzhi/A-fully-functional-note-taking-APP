package com.hfut.cqyzs.memorandum.ui.login

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Handler
import android.os.Message
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.hfut.cqyzs.memorandum.MemoApp

import com.hfut.cqyzs.memorandum.R
import com.hfut.cqyzs.memorandum.local.entity.User
import kotlinx.android.synthetic.main.login_fragment.*

class LoginFragment : Fragment() {
    companion object {
        fun newInstance() = LoginFragment()
    }

    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)

        //用于接收查询本地数据库用户登录情况的handler
        val loginHandler = @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg : Message?) {
                if(msg != null){
                    MemoApp.setUser(msg.obj as User)
                    findNavController().navigate(R.id.action_LoginFragment_to_HomeFragment)
                }
            }
        }
        viewModel.getLoginStatusData(loginHandler)

        return inflater.inflate(R.layout.login_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val userLoginHandler = @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg : Message?) {
                if(msg != null){
                    val user = msg.obj as User
                    //通过网络请求获得用户的信息
                    MemoApp.setUser(user)

                    //getCloudData方法的handler,获得云端数据后跳转界面
                    val getCloudHandler = @SuppressLint("HandlerLeak")
                    object : Handler() {
                        override fun handleMessage(msg: Message?) {
                            findNavController().navigate(R.id.action_LoginFragment_to_HomeFragment)
                        }
                    }

                    //insertLoginUserData方法的handler,插入用户信息后调用getCloudData方法
                    val insertHandler = @SuppressLint("HandlerLeak")
                    object : Handler() {
                        override fun handleMessage(msg : Message?) {
                            viewModel.getCloudData(getCloudHandler,context!!,user.userId)
                        }
                    }

                    viewModel.insertLoginUserData(insertHandler,user)
                }
             }
        }

        btn_login.setOnClickListener{
            viewModel.getLoginData(userLoginHandler,this.context,et_username.text.toString(),et_password.text.toString())
        }

        btn_register.setOnClickListener{
            findNavController().navigate(R.id.action_LoginFragment_to_RegisterFragment)
        }

        btn_forgetpsw.setOnClickListener{
            findNavController().navigate(R.id.action_LoginFragment_to_GetCodeFragment)
        }
    }
}
