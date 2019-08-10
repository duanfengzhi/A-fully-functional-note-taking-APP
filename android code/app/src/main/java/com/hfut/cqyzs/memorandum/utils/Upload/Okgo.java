package com.hfut.cqyzs.memorandum.utils.Upload;

import android.util.Log;
import com.hfut.cqyzs.memorandum.MemoApp;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.io.File;
import java.io.IOException;

/**
 * 项目名称：android code
 * 日期:2019/7/10 15:26
 * 包名:com.hfut.cqyzs.memorandum.utils.OkGo
 * 类描述:
 *
 * @author: 王佳敏
 */
public class Okgo {

    private static Okgo instance = new Okgo();
    private Okgo(){}
    public  static Okgo getInstance(){
        return instance;
    }
    private String downUrl = "http://www.tryandstudy.cn:443/notepadres/";
    private String url = MemoApp.Companion.getInstance().getPath() + "/";

    public static void getFileSize1(File file) {
        if (file.exists() && file.isFile()) {
            String fileName = file.getName();
            System.out.println("文件"+fileName+"的大小是："+file.length());
        }
    }

    public void saveToInternet(String filesDir,String fileName) {
        //上传单个文件
        File file = new File(filesDir, fileName);
        String url = "http://www.tryandstudy.cn:443/notepad/icon";
        getFileSize1(file);
        OkGo.<String>post(url)
                .tag(this)
                .params("icon", file)
                .isMultipart(true)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.i("YXG","成功：" + response);
                    }
                    @Override
                    public void onError(Response<String> response) {
                        Log.i("YXG","失败：" + response.message());
                    }
                });

    }

    public void downLoad(final String fileName) {
        OkGo.<File>get(downUrl + fileName)
            .execute(new FileCallback(url,fileName) {   //指定下载的路径  下载文件名
                @Override
                public void onSuccess(Response<File> response) {
                    File fileDir = new File(url +fileName);
                    if (!fileDir.exists() || !fileDir.isDirectory()) {
                        try {
                            fileDir.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onError(Response<File> response) {
                    super.onError(response);
                }
            });
    }

}