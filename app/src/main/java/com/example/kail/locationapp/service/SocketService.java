package com.example.kail.locationapp.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.example.kail.locationapp.model.MessageEvent;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SocketService extends Service {
    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2, 6, 30, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
    Gson gson = new Gson();

    public SocketService() {
    }

    public class MyBinder extends Binder {

        public SocketService getService() {
            return SocketService.this;
        }
    }

    private MyBinder binder = new MyBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    ServerSocket serverSocket = new ServerSocket(8888);
                    while (true) {
                        Socket socket = serverSocket.accept();
                        // 实例化输入流，并获取网页代码
                        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                        String s; // 依次循环，至到读的值为空
                        StringBuilder sb = new StringBuilder();
                        while ((s = reader.readLine()) != null) {
                            sb.append(s);
                        }
                        reader.close();
                        EventBus.getDefault().post(gson.fromJson(sb.toString(),MessageEvent.class));
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }

}
