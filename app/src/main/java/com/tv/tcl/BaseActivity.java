package com.tv.tcl;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * 文 件 名: BaseActivity
 * 创 建 人: 何庆
 * 创建日期: 2018/12/31 00:02
 * 修改备注：
 */

public class BaseActivity extends AppCompatActivity {

    private Server server;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        AppExecutors.getInstance().childThread().execute(new BroadCastUdp());
        AppExecutors.getInstance().childThread().execute(() -> {
            server = new Server();
            server.start();
        });
    }

    public void addTcpLisener(TcpListener listener) {
        if (server != null)
            server.addListener(listener);
    }

    public void toast(String msg) {
        AppExecutors.getInstance().mainThread().execute(() -> {
            Toast.makeText(this.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        });
    }

}
