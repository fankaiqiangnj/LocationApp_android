package com.example.kail.locationapp.adapter;

import android.content.Context;
import android.view.View;

import com.baidu.mapapi.model.LatLng;
import com.example.kail.locationapp.LocationAppApplication;
import com.example.kail.locationapp.R;
import com.example.kail.locationapp.base.adapter.BaseRecyclerAdapter;
import com.example.kail.locationapp.base.adapter.BaseRecylerViewHolder;
import com.example.kail.locationapp.gen.MessageEventDao;
import com.example.kail.locationapp.model.ClearAlarm;
import com.example.kail.locationapp.model.ErrorMessage;
import com.example.kail.locationapp.model.MessageEvent;
import com.example.kail.locationapp.model.SocketEvent;
import com.example.kail.locationapp.model.SocketSendDDXC;
import com.example.kail.locationapp.model.SocketSendFHSD;
import com.example.kail.locationapp.model.SocketSendJDSJ;
import com.example.kail.locationapp.model.TimerEventStart;
import com.example.kail.locationapp.model.TimerEventStop;
import com.example.kail.locationapp.util.BaseUtil;
import com.example.kail.locationapp.util.GPSUtil;
import com.example.kail.locationapp.util.UrlUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Date;
import java.util.List;

public class AlarmRecycleAdapter extends BaseRecyclerAdapter<MessageEvent> {
    Impl impl;
    private MessageEventDao messageEventDao = LocationAppApplication.getInstance().getDaoSession().getMessageEventDao();

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
                        EventBus.getDefault().post(new TimerEventStart());

                        break;
                    case 1:
                        sendMessage(viewHolder, messageEvent);
                        break;
                    case 2:
                        sendMessage(viewHolder, messageEvent);
                        EventBus.getDefault().post(new TimerEventStop());
                        break;
                    default:
                        break;
                }


            }
        });
        viewHolder.getView(R.id.tv_daohang).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LatLng latLng = GPSUtil.gpsConvertToBaidu(messageEvent.getLatitude(),messageEvent.getLongitude());
                String uri = "baidumap://map/marker?location=" + latLng.latitude+","+latLng.longitude + "&title=%s&content=告警位置&traffic=off";
                impl.goToBaiDu(uri);
            }
        });
    }




    public void sendMessage(final BaseRecylerViewHolder viewHolder, final MessageEvent messageEvent) {
        LocationAppApplication.getInstance().getThreadPoolExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Object object=new Object();
                switch (messageEvent.getType()) {
                    case 0:
                        object= new SocketSendJDSJ(messageEvent.getGdbh(), BaseUtil.date2String(new Date()),
                                String.valueOf(LocationAppApplication.getInstance().getmCurrentLon()),
                                String.valueOf(LocationAppApplication.getInstance().getmCurrentLat()));
                        break;
                    case 1:
                        object= new SocketSendDDXC(messageEvent.getGdbh(), BaseUtil.date2String(new Date()),
                                String.valueOf(LocationAppApplication.getInstance().getmCurrentLon()),
                                String.valueOf(LocationAppApplication.getInstance().getmCurrentLat()));
                        break;

                    case 2:
                        object= new SocketSendFHSD(messageEvent.getGdbh(), BaseUtil.date2String(new Date()),
                                String.valueOf(LocationAppApplication.getInstance().getmCurrentLon()),
                                String.valueOf(LocationAppApplication.getInstance().getmCurrentLat()));
                        break;
                    default:
                        break;
                }
                try {
                    Socket s = new Socket();
                    SocketAddress socketAddress = new InetSocketAddress(UrlUtil.getSocketIp(mContext), Integer.valueOf(UrlUtil.getSocketPort(mContext)));
                    s.connect(socketAddress, 1500);
                    PrintWriter pw = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), "UTF-8"), true);
                    pw.println(LocationAppApplication.getInstance().getGson()
                            .toJson(object));
                    pw.flush();
                    s.shutdownOutput();
                    s.close();
                    messageEvent.setType(messageEvent.getType() + 1);
                    messageEventDao.update(messageEvent);
                    if (messageEvent.getType() == 3) {
                        EventBus.getDefault().post(new ClearAlarm());
                    }
                    LocationAppApplication.getInstance().setSocketIsCanClient(true);

                } catch (Exception e) {
                    LocationAppApplication.getInstance().setSocketIsCanClient(false);
                    EventBus.getDefault().post(new ErrorMessage("数据发送失败，请检查网络状态"));
                    EventBus.getDefault().post(new SocketEvent());
                }finally {
                    impl.close();
                }

            }
        });
    }

    public interface Impl {
        void close();

        void goToBaiDu(String uri);
    }
}
