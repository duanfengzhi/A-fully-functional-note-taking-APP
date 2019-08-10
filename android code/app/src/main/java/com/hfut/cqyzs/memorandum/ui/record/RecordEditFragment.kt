package com.hfut.cqyzs.memorandum.ui.record

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.*
import android.util.Log
import androidx.lifecycle.ViewModelProviders
import android.view.*
import androidx.fragment.app.Fragment
import com.iflytek.cloud.*
import com.iflytek.cloud.ui.RecognizerDialog
import com.iflytek.cloud.ui.RecognizerDialogListener
import org.json.JSONException
import org.json.JSONObject
import java.util.LinkedHashMap

import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import java.io.FileInputStream
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

import androidx.core.net.toUri
import androidx.navigation.fragment.findNavController
import com.hfut.cqyzs.memorandum.MemoApp
import com.hfut.cqyzs.memorandum.R
import com.hfut.cqyzs.memorandum.local.entity.Item
import com.hfut.cqyzs.memorandum.utils.JsonParser
import com.hfut.cqyzs.memorandum.utils.common.ToastUtil
import com.iflytek.cloud.SpeechConstant
import com.iflytek.cloud.SpeechRecognizer
import com.iflytek.cloud.SpeechUtility
import com.lzy.okgo.utils.HttpUtils.runOnUiThread
import kotlinx.android.synthetic.main.record_edit_fragment.*

class RecordEditFragment : Fragment(){
    private var speechRecognizer: SpeechRecognizer? = null  //语音听写对象
    private var recognizerDialog: RecognizerDialog? = null  //语音听写UI
    private var isShowDialog = true //是否显示听写UI
    private var sharedPreferences: SharedPreferences? = null    //缓存
    private var hashMap = LinkedHashMap<String, String>()   //用HashMap存储听写结果
    private var mEngineType: String? = null  //引擎类型（云端或本地）
    private var ret = 0 //函数返回值
    private var toast: Toast? = null

    private lateinit var titleStr: String
    private var contextPath:String? = null
    private var recorder: MediaRecorder? = null
    private var play: MediaPlayer =MediaPlayer()
    private var filePathList = ArrayList<String>()
    private var imgPathList = ArrayList<String>()
    private var recorderList = ArrayList<String>()
    private var filePath:String? = null
    private var fileName:String? = null
    private var currentItem = -1
    private var currentEvent:EditText? = null
    private lateinit var nowPlay:Button

    private var strMap = mutableMapOf<String,Int>()
    private var recordID = -1
    private var itemId:Int = 0
    private var background:Int = 0

    private val choosePicture = 0
    private lateinit var tempMap:Map<String,Int>
    var itemInfoList = ArrayList<Item>()

    companion object {
        fun newInstance() = RecordEditFragment()
    }

    private lateinit var viewModel: RecordEditViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.record_edit_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(RecordEditViewModel::class.java)
        contextPath =  MemoApp.instance.path

        val itemSpinner = spinnerItem//1
        val bgSpinner = spinnerBG//2
        val itemList = ArrayList<String>()
        val bgList = ArrayList<String>()

        btn_return.setOnClickListener {
            findNavController().navigate(R.id.action_RecordEditFragment_to_HomeFragment)
        }

        val recordHandler = @SuppressLint("HandlerLeak")
        object : Handler(){
            override fun handleMessage(msg : Message?) {
                val result = msg!!.obj as List<*>
                for(item in result){
                    itemInfoList.add(item as Item)
                    itemList.add(item.name)
                }
//                val adItem = ArrayAdapter(context!!,R.layout.spinner_item_layout, itemList)
                val adItem = ArrayAdapter(context!!,android.R.layout.simple_spinner_item, itemList)
                adItem.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                itemSpinner.adapter = adItem

                arguments?.let {
                    recordID = it.getInt("recordId",-1)
                }
                if(recordID != -1){
                    val loadFileHandler = @SuppressLint("HandlerLeak")
                    object : Handler() {
                        override fun handleMessage(msg : Message?) {
                            tempMap = msg?.obj as Map<String,Int>
                            if(tempMap["fail"] == 0){
                                loadFile(tempMap)
                                var itemPosition = -1
                                for((i,item) in itemInfoList.withIndex()){
                                    if(item.itemId == itemId)
                                        itemPosition = i

                                }
                                itemSpinner.setSelection(itemPosition)
                                bgSpinner.setSelection(background)
                            }
                            else{
                                ToastUtil.instance.showMsg(context!!,"加载失败")
                            }
                        }
                    }
                    viewModel.loadFile(loadFileHandler,recordID)
                }
            }
        }

        viewModel.getAllItem(recordHandler)
        bgList.add("默认")
        bgList.add("背景1")
        bgList.add("背景2")
        bgList.add("背景3")
        bgList.add("背景4")
        bgList.add("背景5")
        bgList.add("背景6")
        //为下拉列表定义一个适配器
        val adBG = ArrayAdapter(this.requireContext(),R.layout.spinner_item_layout, bgList)
        //设置下拉菜单样式。
        adBG.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        //添加数据
        bgSpinner.adapter = adBG
        itemSpinner.setSelection(itemId)
        bgSpinner.setSelection(background)
        //点击响应事件
        val listenItem = ItemClickListener()
        val listenBG = BGClickListener()
        itemSpinner.onItemSelectedListener = listenItem
        bgSpinner.onItemSelectedListener = listenBG
        itemSpinner.setSelection(itemId)
        bgSpinner.setSelection(background)

        initDate()
        btn_voiceInput.setOnClickListener {
            start()
        }

        recorderStart.setOnClickListener {
            recorderStart()
            currentItem = 3
            recorderStart.visibility = View.GONE
            recorderStop.visibility = View.VISIBLE
        }

        recorderStop.setOnClickListener {
            recorderStop()
            currentItem = 3
            recorderStop.visibility = View.GONE
            recorderStart.visibility = View.VISIBLE
        }

        loadImg.setOnClickListener {
            //图片
            showChoosePicDialog()
            currentItem = 2
        }

        save.setOnClickListener {
            var j = 0
            var t = 0
            for(i in 0 until LL.childCount){
                when (val view:View = LL.getChildAt(i)) {
                    is EditText -> strMap[view.text.toString()] = 1
                    is Button -> {
                        strMap[recorderList[t]] = 2
                        t++

                    }
                    is ImageView -> {
                        strMap[imgPathList[j]] = 3
                        j++
                    }
                }
            }
            titleStr = title.text.toString()
            viewModel.saveFile(@SuppressLint("HandlerLeak")
            object : Handler() {
                override fun handleMessage(msg : Message?) {
                    if(msg?.obj as Boolean){
                        findNavController().navigate(R.id.action_RecordEditFragment_to_HomeFragment)
                    }
                    else{
                        ToastUtil.instance.showMsg(context!!,"保存失败")
                    }
                }
            },strMap,titleStr,itemId,background,recordID)
        }

        sv.setOnTouchListener { _, motionEvent ->
            if(motionEvent.action == MotionEvent.ACTION_DOWN && currentItem != 1){
                addEditText()
            }
            false
        }
    }

    @SuppressLint("ShowToast")
    private fun initDate() {
        SpeechUtility.createUtility(this.requireContext(), SpeechConstant.APPID +"=5d23f1b0")
        speechRecognizer = SpeechRecognizer.createRecognizer(this.requireContext(), initListener)
        recognizerDialog = RecognizerDialog(this.requireContext(), initListener)
        sharedPreferences = this.requireContext().getSharedPreferences(this.requireContext().packageName, Context.MODE_PRIVATE)
        toast = Toast.makeText(this.requireContext(), "", Toast.LENGTH_SHORT)
        mEngineType = SpeechConstant.TYPE_CLOUD
    }

    //开始听写
    private fun start() {
        hashMap.clear()
        setParams()

        if (isShowDialog) {
            recognizerDialog?.setListener(dialogListener)
            recognizerDialog?.show()
        } else {
            ret = speechRecognizer!!.startListening(recognizerListener)
            if (ret != ErrorCode.SUCCESS) {
            }
        }

    }

    //结束听写
    private fun stop() {
        Toast.makeText(this.requireContext(), "停止听写", Toast.LENGTH_SHORT).show()
        if (isShowDialog) {
            recognizerDialog?.dismiss()
        } else {
            speechRecognizer?.stopListening()
        }
    }

    //初始化监听器
    private val initListener = InitListener { i ->
        if (i != ErrorCode.SUCCESS) {
        }
    }

    //无UI监听器
    private val recognizerListener = object : RecognizerListener {
        override fun onBeginOfSpeech() {}
        override fun onEndOfSpeech() {}
        override fun onError(speechError: SpeechError) {}
        override fun onEvent(i: Int, i1: Int, i2: Int, bundle: Bundle) {}

        override fun onVolumeChanged(i: Int, bytes: ByteArray) {
            runOnUiThread { toast!!.setText("当前音量$i") }
        }

        override fun onResult(recognizerResult: RecognizerResult?, b: Boolean) {
            if (recognizerResult != null) {
                printResult(recognizerResult)
            }
        }
    }

    //有UI监听器
    private val dialogListener = object : RecognizerDialogListener {
        override fun onResult(recognizerResult: RecognizerResult?, b: Boolean) {
            if (recognizerResult != null) {
                printResult(recognizerResult)
            }
        }

        override fun onError(speechError: SpeechError) {}
    }

    //输出结果，将返回的json字段解析并在textVie中显示
    private fun printResult(results: RecognizerResult) {
        val text = JsonParser.parseIatResult(results.resultString)

        lateinit var sn: String
        // 读取json结果中的sn字段
        try {
            val resultJson = JSONObject(results.resultString)
            sn = resultJson.optString("sn")
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        hashMap[sn] = text

        val resultBuffer = StringBuffer()
        for (key in hashMap.keys) {
            resultBuffer.append(hashMap[key])
        }

        if(currentItem != 1){
            val tv = EditText(this@RecordEditFragment.context)
            tv.textSize = 20f
            tv.setBackgroundColor(resources.getColor(android.R.color.transparent))// = R.color.mtrl_btn_transparent_bg_color//?android:attr/panelBackgroun
            @SuppressLint("NewApi")
            tv.background = null
            tv.setOnKeyListener { _, i, _ ->
                if(i == KeyEvent.KEYCODE_DEL){
                    if(currentEvent?.length() == 0){
                        LL.removeView(currentEvent)
                        if(LL.getChildAt(LL.childCount - 1) is TextView){
                            //删除音频文件
                            deleteFile(filePathList[filePathList.size - 1])
                        }else if(LL.getChildAt(LL.childCount - 1) is ImageView){
                            //删除图片文件
                        }
                        LL.removeView(LL.getChildAt(LL.childCount - 1))
                        isEditText()
                    }
                }
                false
            }
            LL.addView(tv)
            tv.requestFocus()
            currentItem = 1
            currentEvent = tv
        }
        var str = currentEvent!!.text.toString()
        str += resultBuffer.toString()
        currentEvent!!.setText(str)

        stop()
    }

    private fun setParams() {
        //清空参数
        speechRecognizer?.setParameter(SpeechConstant.PARAMS, null)
        //设置引擎
        speechRecognizer?.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType)
        //设置返回数据类型
        speechRecognizer?.setParameter(SpeechConstant.RESULT_TYPE, "json")
        //设置中文 普通话
        speechRecognizer?.setParameter(SpeechConstant.LANGUAGE, "zh_cn")
        speechRecognizer?.setParameter(SpeechConstant.ACCENT, "mandarin")

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        speechRecognizer?.setParameter(
            SpeechConstant.VAD_BOS,
            sharedPreferences!!.getString("iat_vadbos_preference", "1000")
        )

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        speechRecognizer?.setParameter(
            SpeechConstant.VAD_EOS,
            sharedPreferences!!.getString("iat_vadeos_preference", "1000")
        )

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        speechRecognizer?.setParameter(
            SpeechConstant.ASR_PTT,
            sharedPreferences?.getString("iat_punc_preference", "0")
        )

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        speechRecognizer?.setParameter(SpeechConstant.AUDIO_FORMAT, "wav")
        speechRecognizer?.setParameter(
            SpeechConstant.ASR_AUDIO_PATH,
            Environment.getExternalStorageDirectory().toString() + "/msc/iat.wav"
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        play.stop() //停止播放音乐
        play.release()  //释放占用的资源
    }

    private fun recorderStart() {
        fileName = getNow()
        fileName = fileName!!.replace(".","-")
        fileName = fileName!!.replace(":","")+ ".amr"

        if(recorder == null){
            filePath= "$contextPath/$fileName"
            val soundFile = File(filePath!!)
            if(!soundFile.exists()){
                try {
                    soundFile.createNewFile()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            recorder = MediaRecorder()
            recorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
            recorder?.setOutputFormat(MediaRecorder.OutputFormat.AMR_WB)
            recorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB)
            recorder?.setOutputFile(soundFile.absolutePath)
            try {
                recorder?.prepare()
                recorder?.start()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun recorderStop(){
        if(recorder != null){
            recorder?.stop()
            recorder?.release()
            recorder = null
            val tempPath = filePath
            if(tempPath != null){
                filePathList.add(tempPath)
                addRecorder()
            }
        }
    }

    private fun playStart(filePath:String?){
        try {
            play.reset()
            val file = File(filePath!!)
            val fis = FileInputStream(file)
            //加载多媒体文件
            play.setDataSource(fis.fd)
            //准备播放音乐
            play.prepare()
            //播放音乐
            play.start()

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun playStop(){
        play.stop()
    }

    @SuppressLint("SimpleDateFormat")
    private fun getNow(): String {
        return if (Build.VERSION.SDK_INT >= 24){
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())
        }else{
            val tms = Calendar.getInstance()
            tms.get(Calendar.YEAR).toString() + "-" + tms.get(Calendar.MONTH).toString() + "-" + tms.get(Calendar.DAY_OF_MONTH).toString() + " " + tms.get(Calendar.HOUR_OF_DAY).toString() + ":" + tms.get(Calendar.MINUTE).toString() +":" + tms.get(Calendar.SECOND).toString() +"." + tms.get(Calendar.MILLISECOND).toString()
        }
    }

    private fun deleteFile(str:String?){
        val soundFile = File(str!!)
        if(soundFile.exists()){
            try {
                soundFile.delete()
                filePathList.remove(str)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun isEditText(){
        val view = LL.getChildAt(LL.childCount - 1)
        if(view is EditText){
            currentEvent = view
            currentItem = 1
        }else{
            currentItem = -1
        }
    }

    @SuppressLint("InlinedApi")
    private fun showChoosePicDialog() {
        val builder = android.app.AlertDialog.Builder(this.requireContext())
        builder.setTitle("选择图片")
        val items = arrayOf("选择本地照片")
        builder.setNegativeButton("取消", null)
        builder.setItems(items) { _, which ->
            when (which) {
                choosePicture // 选择本地照片
                -> {
                    val openAlbumIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                    openAlbumIntent.type = "image/*"
                    startActivityForResult(openAlbumIntent, choosePicture)
                }
            }
        }
        builder.create().show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK && null != data) {
            addImg(data)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray)
    {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        } else {
            // 没有获取 到权限，从新请求，或者关闭app
            Toast.makeText(this.requireContext(), "需要存储权限", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addEditText(){
        val tv = EditText(this@RecordEditFragment.context)
        tv.textSize = 20f
//        tv.background.alpha = 0
        tv.setBackgroundColor(resources.getColor(android.R.color.transparent))// = R.color.mtrl_btn_transparent_bg_color//?android:attr/panelBackground
        tv.setOnKeyListener { _, i, _ ->
            if(i == KeyEvent.KEYCODE_DEL){
                if(currentEvent?.length() == 0){
                    LL.removeView(currentEvent)
                    if(LL.getChildAt(LL.childCount - 1) is Button){
                        //删除音频文件
                        deleteFile(filePathList[filePathList.size - 1])
                    }else if(LL.getChildAt(LL.childCount - 1) is ImageView){
                        //删除图片文件
                    }
                    LL.removeView(LL.getChildAt(LL.childCount - 1))
                    isEditText()
                }
            }
            false
        }
        LL.addView(tv)
        tv.requestFocus()
        currentItem = 1
        currentEvent = tv
    }

    private fun addRecorder(){
        if(currentEvent?.text?.length == 0){
            LL.removeView(currentEvent)
        }
        val btn = Button(this@RecordEditFragment.context)
        btn.layoutParams = LinearLayout.LayoutParams(200,200)
        val tempPath = filePath
        if(tempPath != null){
            recorderList.add(tempPath)
        }
        btn.setBackgroundResource(R.drawable.recorder)
        LL.addView(btn)
        btn.setOnClickListener {
            if(!play.isPlaying){
                playStart(filePath)
                play.setOnCompletionListener {
                    btn.setBackgroundResource(R.drawable.recorder)
                }
                nowPlay = btn
                btn.setBackgroundResource(R.drawable.recorderstart)
            }else{
                playStop()
                nowPlay.setBackgroundResource(R.drawable.recorder)
                if(btn != nowPlay){
                    playStart(filePath)
                    play.setOnCompletionListener {
                        btn.setBackgroundResource(R.drawable.recorder)
                    }
                    nowPlay = btn
                    btn.setBackgroundResource(R.drawable.recorderstart)
                }
            }
        }
        btn.setOnLongClickListener {
            AlertDialog.Builder(this.requireContext())
                .setTitle("提示")
                .setMessage("是否删除")
                .setPositiveButton("删除") { _, _->
                    deleteFile(btn.text.toString())
                    isEditText()
                    LL.removeView(btn)
                }.setNegativeButton("取消") { _, _ ->
                }.create().show()
            false
        }
        addEditText()
    }

    private fun addImg(data: Intent?){
        if(currentEvent?.text?.length == 0){
            LL.removeView(currentEvent)
        }
        val img = ImageView(this@RecordEditFragment.context)
        img.scaleType = ImageView.ScaleType.CENTER_CROP
        img.setImageURI(data?.data)
        LL.addView(img)
        val height = (1036.toFloat() / img.drawable.minimumWidth.toFloat() * img.drawable.minimumHeight).toInt()
        img.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,height)

        imgPathList.add(data?.data.toString())
        img.setOnLongClickListener {
            AlertDialog.Builder(this.requireContext())
                .setTitle("提示")
                .setMessage("是否删除")
                .setPositiveButton("删除") { _, _->
                    isEditText()
                    LL.removeView(img)
                }.setNegativeButton("取消") { _, _ ->
                }.create().show()
            false
        }
        addEditText()
    }

    fun loadFile(strMap: Map<String,Int>){
        for ((key,value) in strMap){
            when (value) {
                1 -> {
                    val tv = EditText(this@RecordEditFragment.context)
                    tv.textSize = 20f

                    tv.setBackgroundColor(resources.getColor(android.R.color.transparent))// = R.color.mtrl_btn_transparent_bg_color//?android:attr/panelBackground
//                    tv.background.alpha = 0
                    tv.setText(key)
                    tv.setOnKeyListener { _, i, _ ->
                        if(i == KeyEvent.KEYCODE_DEL){
                            if(currentEvent?.length() == 0){
                                LL.removeView(currentEvent)
                                if(LL.getChildAt(LL.childCount - 1) is TextView){
                                    //删除音频文件
                                    deleteFile(filePathList[filePathList.size - 1])
                                }else if(LL.getChildAt(LL.childCount - 1) is ImageView){
                                    //删除图片文件
                                }
                                LL.removeView(LL.getChildAt(LL.childCount - 1))
                                isEditText()
                            }
                        }
                        false
                    }
                    LL.addView(tv)
                    tv.requestFocus()
                    currentItem = 1
                    currentEvent = tv
                }
                2 -> {
                    filePathList.add(key)
                    filePath = key
                    if(currentEvent?.text?.length == 0){
                        LL.removeView(currentEvent)
                    }
                    val btn = Button(this@RecordEditFragment.context)
                    btn.layoutParams = LinearLayout.LayoutParams(200,200)
                    val tempPath = filePath
                    if(tempPath != null){
                        recorderList.add(tempPath)
                    }
                    btn.setBackgroundResource(R.drawable.recorder)
                    LL.addView(btn)
                    filePathList.add(key)
                    btn.setOnClickListener {
                        if(!play.isPlaying){
                            playStart(filePath)
                            play.setOnCompletionListener {
                                btn.setBackgroundResource(R.drawable.recorder)
                            }
                            nowPlay = btn
                            btn.setBackgroundResource(R.drawable.recorderstart)
                        }else{
                            playStop()
                            nowPlay.setBackgroundResource(R.drawable.recorder)
                            if(btn != nowPlay){
                                playStart(filePath)
                                play.setOnCompletionListener {
                                    btn.setBackgroundResource(R.drawable.recorder)
                                }
                                nowPlay = btn
                                btn.setBackgroundResource(R.drawable.recorderstart)
                            }
                        }
                    }
                    btn.setOnLongClickListener {
                        AlertDialog.Builder(this.requireContext())
                            .setTitle("提示")
                            .setMessage("是否删除")
                            .setPositiveButton("删除") { _, _->
                                deleteFile(btn.text.toString())
                                isEditText()
                                LL.removeView(btn)
                            }.setNegativeButton("取消") { _, _ ->
                            }.create().show()
                        false
                    }
                    currentItem = 2
                }
                3 -> {
                    if(currentEvent?.text?.length == 0){
                        LL.removeView(currentEvent)
                    }
                    val img = ImageView(this@RecordEditFragment.context)
                    img.scaleType = ImageView.ScaleType.CENTER_CROP
                    img.setImageURI(key.toUri())
                    val height = (1036.toFloat() / img.drawable.minimumWidth.toFloat() * img.drawable.minimumHeight).toInt()
                    img.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,height)
                    img.setOnLongClickListener {
                        AlertDialog.Builder(this.requireContext())
                            .setTitle("提示")
                            .setMessage("是否删除")
                            .setPositiveButton("删除") { _, _->
                                isEditText()
                                LL.removeView(img)
                            }.setNegativeButton("取消") { _, _ ->
                            }.create().show()
                        false
                    }
                    LL.addView(img)
                    imgPathList.add(key)
                    currentItem = 3
                }
                4 -> title.setText(key)
                5 -> itemId = key.toInt()
                6 -> background = key.toInt()
            }
        }
    }

    internal inner class ItemClickListener : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {

        }
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            itemId = itemInfoList[position].itemId
        }
    }

    internal inner class BGClickListener : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {

        }
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            background = position
            when (background){
                0 ->{
                    @SuppressLint("NewApi")
                    frameLayoutRecord.background = null
                }
                1 ->
                    frameLayoutRecord.setBackgroundResource(R.drawable.background1)
                2 ->
                    frameLayoutRecord.setBackgroundResource(R.drawable.bg2)
                3 ->
                    frameLayoutRecord.setBackgroundResource(R.drawable.bg3)
                4 ->
                    frameLayoutRecord.setBackgroundResource(R.drawable.bg4)
                5 ->
                    frameLayoutRecord.setBackgroundResource(R.drawable.bg5)
                6 ->
                    frameLayoutRecord.setBackgroundResource(R.drawable.bg6)
            }
        }
    }
}
