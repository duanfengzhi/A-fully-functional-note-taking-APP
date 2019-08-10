package com.hfut.cqyzs.memorandum.utils.alarm;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import com.hfut.cqyzs.memorandum.R;

/**
 * 项目名称：android code
 * 日期:2019/7/9 16:34
 * 包名:com.hfut.cqyzs.memorandum.ui
 * 类描述:
 *
 * @author: 王佳敏
 */
public class AlarmAlert extends Activity {
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        String title  = savedInstanceState.getString("TITLE");
        String title  = "测试吧";
        mediaPlayer = MediaPlayer.create(this,R.raw.clockmusic2);
        mediaPlayer.start();
        Intent intent = getIntent();
        title = intent.getStringExtra("msg");
        new AlertDialog.Builder(AlarmAlert.this)
                .setTitle("山人自有妙计提醒您")
                .setMessage(title)//"时间到了！" +
                .setPositiveButton("关掉"
                        , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AlarmAlert.this.finish();
                                mediaPlayer.stop();
                            }
                        }).show();
    }
}



