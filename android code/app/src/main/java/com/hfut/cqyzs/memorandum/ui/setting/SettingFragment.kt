package com.hfut.cqyzs.memorandum.ui.setting

import android.content.SharedPreferences
import android.graphics.Color
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController

import com.hfut.cqyzs.memorandum.R
import com.hfut.cqyzs.memorandum.ui.MainActivity
import kotlinx.android.synthetic.main.setting_fragment.*



class SettingFragment : Fragment() {
    lateinit var sharedPreference: SharedPreferences

    lateinit var fatherActivity:MainActivity
    companion object {
        fun newInstance() = SettingFragment()
    }

    private lateinit var viewModel: SettingViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.setting_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SettingViewModel::class.java)

        fatherActivity = activity as MainActivity

        sharedPreference=this.requireContext().getSharedPreferences("save.xml",0)

        var isNight=sharedPreference.getBoolean("isNight",false)
        if(isNight==true){
            slideButton.setChecked(true)
        }else{
            slideButton.setChecked(false)
        }

        slideButton.setSmallCircleModel(
            Color.parseColor("#cccccc"),
            Color.parseColor("#00000000"),
            Color.parseColor("#FF4040"),
            Color.parseColor("#cccccc")
        )

        slideButton.setOnCheckedListener {

            if(slideButton.isChecked==true){


                fatherActivity.delegate.setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                sharedPreference.edit().putBoolean("isNight",true).commit()
            }
            else
            {
                fatherActivity.delegate.setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                sharedPreference.edit().putBoolean("isNight",false).commit()
            }

            fatherActivity.recreate()
        }
        btn_return.setOnClickListener {
            findNavController().navigate(R.id.action_SettingFragment_to_HomeFragment)
        }
    }

}
