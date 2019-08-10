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
import androidx.navigation.fragment.findNavController
import com.hfut.cqyzs.memorandum.R
import com.hfut.cqyzs.memorandum.utils.common.ToastUtil
import kotlinx.android.synthetic.main.set_email_fragment.*

class SetEmailFragment : Fragment() {
    companion object {
        fun newInstance() = SetEmailFragment()
    }

    private lateinit var viewModel: SetEmailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.set_email_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SetEmailViewModel::class.java)

        btn_return.setOnClickListener {
            findNavController().navigate(R.id.action_SetEmailFragment_to_AccountFragment)
        }

        btn_ensureEmail.setOnClickListener{
            viewModel.setEmail(
                @SuppressLint("HandlerLeak")
                object : Handler() {
                    override fun handleMessage(msg : Message?) {
                        super.handleMessage(msg)
                        if(msg?.obj as Int == 1){
                            findNavController().navigate(R.id.action_SetEmailFragment_to_AccountFragment)
                        }else{
                            ToastUtil.instance.showMsg(context!!,"修改失败")
                        }
                    }
                },this.requireContext(),edt_newEmail.text.toString())
        }
    }

}
