package com.hfut.cqyzs.memorandum.utils.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * 项目名称：android code
 * 日期:2019/7/9 16:37
 * 包名:com.hfut.cqyzs.memorandum.utils.alarm
 * 类描述:
 *
 * @author: 王佳敏
 */
public class CallAlarm extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String msg = intent.getStringExtra("msg");
        Intent intent1 = new Intent(context,AlarmAlert.class);
        Bundle bundle = new Bundle();
        bundle.putString("STR_CALLER","");
        intent1.putExtras(bundle);
        intent1.putExtra("msg", msg);
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent1);
    }
}
