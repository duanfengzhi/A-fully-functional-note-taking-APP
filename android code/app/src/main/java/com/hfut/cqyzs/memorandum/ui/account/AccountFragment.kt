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
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.hfut.cqyzs.memorandum.MemoApp

import com.hfut.cqyzs.memorandum.R
import com.hfut.cqyzs.memorandum.local.entity.User
import kotlinx.android.synthetic.main.account_fragment.*

class AccountFragment : Fragment() {

    companion object {
        fun newInstance() = AccountFragment()
    }

    private lateinit var viewModel: AccountViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.account_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(AccountViewModel::class.java)

        val user = MemoApp.instance.user!!
        tv_name2.text = user.userId
        tv_tel2.text = user.telephone
        tv_email2.text = user.email

        btn_logout.setOnClickListener{
            findNavController().navigate(R.id.action_AccountFragment_to_LoginFragment)
        }

        btn_tel.setOnClickListener {
            findNavController().navigate(R.id.action_AccountFragment_to_SetTelFragment)
        }

        btn_email.setOnClickListener {
            findNavController().navigate(R.id.action_AccountFragment_to_SetEmailFragment)
        }

        btn_logout.setOnClickListener{
            val logoutHandler = @SuppressLint("HandlerLeak")
            object : Handler() {
                override fun handleMessage(msg : Message?) {
                    findNavController().navigate(R.id.action_AccountFragment_to_LoginFragment)
                }
            }
            viewModel.logout(logoutHandler,context!!)
        }
        btn_back.setOnClickListener {
            findNavController().navigate(R.id.action_AccountFragment_to_HomeFragment)
        }


    }
}
