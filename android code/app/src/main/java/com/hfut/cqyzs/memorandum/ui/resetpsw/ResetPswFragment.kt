package com.hfut.cqyzs.memorandum.ui.resetpsw

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
import com.hfut.cqyzs.memorandum.MemoApp

import com.hfut.cqyzs.memorandum.R
import kotlinx.android.synthetic.main.reset_psw_fragment.*

class ResetPswFragment : Fragment() {

    companion object {
        fun newInstance() = ResetPswFragment()
    }

    private lateinit var viewModel: ResetPswViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.reset_psw_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ResetPswViewModel::class.java)

        resetpswBtn.setOnClickListener {
            val firPsw = new_psw_edt.text.toString()
            val secPsw = new_psw_edt2.text.toString()
            if(firPsw == secPsw){
                val resetPswHandler = @SuppressLint("HandlerLeak")
                object : Handler() {
                    override fun handleMessage(msg : Message?) {
                        if(msg?.obj != null){
                            findNavController().navigate(R.id.action_ResetPswFragment_to_LoginFragment)
                        }else {
                            Toast.makeText(context, "密码修改失败！", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                viewModel.reSetPsw(resetPswHandler,this.context, MemoApp.instance.user!!.userId,firPsw)
            }else{
                new_psw_edt2.setText("")
                new_psw_edt.setText("")
                Toast.makeText(context, "两次输入的密码不一致，请重新输入！", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
