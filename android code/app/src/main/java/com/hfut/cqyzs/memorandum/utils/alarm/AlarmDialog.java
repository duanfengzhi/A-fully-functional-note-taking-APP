package com.hfut.cqyzs.memorandum.utils.alarm;

import android.app.Dialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import com.hfut.cqyzs.memorandum.R;

/**
 * 项目名称：android code
 * 日期:2019/7/14 13:01
 * 包名:com.hfut.cqyzs.memorandum.utils.alarm
 * 类描述:
 *
 * @author: 王佳敏
 */
public class AlarmDialog extends Dialog {
    private Button clockBtn;
    private TextView clockTitle;
    private MediaPlayer mediaPlayer;

    public AlarmDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.clock_layout);
        mediaPlayer = MediaPlayer.create(this.getContext(), R.raw.clockmusic2);
        mediaPlayer.start();

        clockTitle = findViewById(R.id.clock_title);
        clockTitle.setText("测试");
        clockBtn = findViewById(R.id.clock_btn);
        clockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                dismiss();
            }
        });
    }

    public void setClockTitle(String setTitle) {
        clockTitle.setText(setTitle);
    }
}
