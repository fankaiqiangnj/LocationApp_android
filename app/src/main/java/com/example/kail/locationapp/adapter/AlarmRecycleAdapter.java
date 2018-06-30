package com.example.kail.locationapp.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.View;

import com.example.kail.locationapp.LocationAppApplication;
import com.example.kail.locationapp.R;
import com.example.kail.locationapp.base.adapter.BaseRecyclerAdapter;
import com.example.kail.locationapp.base.adapter.BaseRecylerViewHolder;
import com.example.kail.locationapp.model.ClearAlarm;
import com.example.kail.locationapp.model.ErrorMessage;
import com.example.kail.locationapp.model.MessageEvent;
import com.example.kail.locationapp.model.SocketSendJDSJ;
import com.example.kail.locationapp.util.BaseUtil;
import com.example.kail.locationapp.util.SPUtils;
import com.example.kail.locationapp.util.UrlUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Date;
import java.util.List;

public class AlarmRecycleAdapter extends BaseRecyclerAdapter<MessageEvent> {
    Impl impl;

    public AlarmRecycleAdapter(Context context, int resource, List<MessageEvent> list, Impl impl) {
        super(context, resource, list);
        this.impl = impl;
    }

    @Override
    public void setConvert(final BaseRecylerViewHolder viewHolder, final MessageEvent messageEvent) {

        viewHolder.setTextView(R.id.check_id, "工单编号：" + messageEvent.getGdbh());
        viewHolder.setTextView(R.id.check_name, messageEvent.getQxnr().replaceAll(";", ";\n").trim());

        switch (messageEvent.getType()) {
            case 0:
                viewHolder.setTextView(R.id.tv_alarm, "接受处理");
                break;
            case 1:
                viewHolder.setTextView(R.id.tv_alarm, "到达现场");
                break;
            case 2:
                viewHolder.setTextView(R.id.tv_alarm, "处理完成");
                break;

            default:
                break;
        }

        viewHolder.getView(R.id.tv_alarm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (messageEvent.getType()) {
                    case 0:
                        sendMessage(viewHolder, messageEvent);

                        break;
                    case 1:
                        sendMessage(viewHolder,messageEvent );
                        break;
                    case 2:
                        sendMessage(viewHolder,messageEvent );
                        break;
                    default:
                        break;
                }


            }
        });
        viewHolder.getView(R.id.tv_daohang).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseUtil.showToast(mContext, "功能正在开发中");
            }
        });
    }

    public void sendMessage(final BaseRecylerViewHolder viewHolder, final MessageEvent messageEvent) {
        LocationAppApplication.getInstance().getThreadPoolExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket s = new Socket();
                    SocketAddress socketAddress = new InetSocketAddress(UrlUtil.getSocketIp(mContext), Integer.valueOf(UrlUtil.getSocketPort(mContext)));
                    s.connect(socketAddress, 1500);
                    PrintWriter pw = new PrintWriter(s.getOutputStream());
                    pw.println(LocationAppApplication.getInstance().getGson()
                            .toJson(new SocketSendJDSJ(messageEvent.getGdbh(), BaseUtil.date2String(new Date()))));
                    pw.flush();
                    s.shutdownOutput();
                    s.close();
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            switch (messageEvent.getType()) {
                                case 0:
                                    viewHolder.setTextView(R.id.tv_alarm, "到达现场");
                                    break;
                                case 1:
                                    viewHolder.setTextView(R.id.tv_alarm, "处理完成");
                                    break;
                                default:
                                    break;
                            }
                        }
                    });
                    messageEvent.setType(messageEvent.getType()+1);
                    SPUtils.put(mContext, "alarm", LocationAppApplication.getInstance().getGson().toJson(messageEvent));
                    if (messageEvent.getType() == 3) {
                        EventBus.getDefault().post(new ClearAlarm());
                    }
                    impl.close();
                } catch (Exception e) {
                    EventBus.getDefault().post(new ErrorMessage("消息接口连接失败"));
                }

            }
        });
    }

    public interface Impl {
        void close();
    }
}
