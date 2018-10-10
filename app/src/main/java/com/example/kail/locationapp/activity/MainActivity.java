package com.example.kail.locationapp.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.example.kail.locationapp.LocationAppApplication;
import com.example.kail.locationapp.R;
import com.example.kail.locationapp.base.network.NetWorkCallback;
import com.example.kail.locationapp.dialog.AlarmDialog;
import com.example.kail.locationapp.dialog.SocketEditDialog;
import com.example.kail.locationapp.gen.MessageEventDao;
import com.example.kail.locationapp.model.ClearAlarm;
import com.example.kail.locationapp.model.ErrorMessage;
import com.example.kail.locationapp.model.GpsModel;
import com.example.kail.locationapp.model.MessageEvent;
import com.example.kail.locationapp.model.SocketEvent;
import com.example.kail.locationapp.model.SuccessModel;
import com.example.kail.locationapp.model.TimerEventStart;
import com.example.kail.locationapp.model.TimerEventStop;
import com.example.kail.locationapp.service.SocketService;
import com.example.kail.locationapp.util.BaseUtil;
import com.example.kail.locationapp.util.UrlUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SensorEventListener, NetWorkCallback {
    Context mContext;
    private MapView mMapView;
    private TextView mLongitude;
    private TextView mLatitude;
    //    private TextView mAddress;
    private TextView mDownload;
    private TextView mTvSocket;
    private TextView mAddress;
    private String url = "";
    private BaiduMap mBaiduMap;
    BitmapDescriptor mCurrentMarker;
    private MyLocationConfiguration.LocationMode mCurrentMode;
    private static final int accuracyCircleFillColor = 0xAAFFFF88;
    private static final int accuracyCircleStrokeColor = 0xAA00FF00;
    private SensorManager mSensorManager;
    private Sensor mSensor;// 传感器
    private Double lastX = 0.0;
    public LocationClient mLocationClient = null;
    boolean isFirstLoc = true; // 是否首次定位
    private MyLocationData locData;
    private int mCurrentDirection = 0;
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private float mCurrentAccracy;
    private Timer mTimer;
    String ip = "";
    String socketIp = "";
    String socketPort = "";
    private TelephonyManager tm;
    String client = "数据链接异常";
    private SocketService socketService = null;
    public MyLocationListenner myListener = new MyLocationListenner();
    private MessageEventDao messageEventDao = LocationAppApplication.getInstance().getDaoSession().getMessageEventDao();

    //BDAbstractLocationListener为7.2版本新增的Abstract类型的监听接口，原有BDLocationListener接口暂时同步保留。具体介绍请参考后文中的说明
    //建立udp的服务
    DatagramSocket datagramSocket;

    private Handler doActionHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int msgId = msg.what;
            switch (msgId) {
                case 1:
                    mLongitude.setText("经度: " + mCurrentLon);
                    mLatitude.setText("纬度: " + mCurrentLat);
                 /*   if (client.equals("数据连接正常")) {
                        mAddress.setTextColor(Color.GREEN);
                    } else {
                        mAddress.setTextColor(Color.RED);
                    }
                    mAddress.setText("连接: " + client);*/
                    break;
                default:
                    break;
            }
        }
    };

    public ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            SocketService.MyBinder binder = (SocketService.MyBinder) service;
            socketService = binder.getService();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        mContext = this.getBaseContext();
        ip = UrlUtil.getIp(mContext);
        url = UrlUtil.getUrl(ip);
        socketIp = UrlUtil.getSocketIp(mContext);
        socketPort = UrlUtil.getSocketPort(mContext);
        checkSocket();
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);
        mLongitude = (TextView) findViewById(R.id.tv_longitude);
        mLatitude = (TextView) findViewById(R.id.tv_latitude);
        mAddress = (TextView) findViewById(R.id.tv_address);
        mDownload = (TextView) findViewById(R.id.tv_download);
        mTvSocket = findViewById(R.id.tv_socket);
        mTvSocket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<MessageEvent> lists = messageEventDao.queryBuilder().where(MessageEventDao.Properties.Type.between(0, 2)).list();
                if (lists.size() == 0) {
                    return;
                }
                AlarmDialog alarmDialog = new AlarmDialog(MainActivity.this, lists, new AlarmDialog.CallBack() {
                    @Override
                    public void dialogCarllBack(String s) {
                        try {
                            Intent i1 = new Intent();
                            i1.setData(Uri.parse(s));
                            startActivity(i1);
                        } catch (Exception e) {
                            BaseUtil.showToast(mContext, "需要先安装百度地图");
                        }
                    }
                });
                alarmDialog.show();

            }
        });
        mAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              /*  EditDialog editDialog = new EditDialog(MainActivity.this, ip, new EditDialog.CallBack() {
                    @Override
                    public void dialogCarllBack(String s) {
                        url = UrlUtil.http + s + UrlUtil.uri;
                    }
                });
                editDialog.show();*/
                SocketEditDialog socketEditDialog = new SocketEditDialog(MainActivity.this, socketIp, socketPort, new SocketEditDialog.CallBack() {
                    @Override
                    public void dialogCarllBack(String ip, String port) {
                        socketIp = ip;
                        socketPort = port;
                        checkSocket();
                    }
                });
                socketEditDialog.show();
            }
        });
        tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        mDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, OfflineDemo.class);
                startActivity(intent);
            }
        });
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                //申请定位权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE}, 1);
            } else {
                initMyLocation();
            }
        } else {
            initMyLocation();

        }

        Intent intent = new Intent(this, SocketService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        EventBus.getDefault().register(this);
        List<MessageEvent> list = messageEventDao.queryBuilder().where(MessageEventDao.Properties.Type.between(0, 2)).list();
        mTvSocket.setText(String.valueOf(list.size()));


//        ScheduledExecutorService
    }

    @Override
    protected void onDestroy() {
        if (mTimer != null) {
            try {
                mTimer.cancel();
            } catch (Exception e) {

            }
        }
        if (datagramSocket != null) {
            datagramSocket.close();
        }
        // 退出时销毁定位
//        mLocationClient.stop();
        // 关闭定位图层
//        mBaiduMap.setMyLocationEnabled(false);
        mSensorManager.unregisterListener(this);
        mMapView.onDestroy();
        mMapView = null;
        unbindService(serviceConnection);
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }


    public void initMyLocation() {
        mBaiduMap = mMapView.getMap();
        //普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);//获取传感器管理服务
        if (mSensorManager != null) {
            //获得方向传感器
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        }
        if (mSensor != null) {
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_UI);//第三个参数为经度
        }
        mCurrentMode = MyLocationConfiguration.LocationMode.FOLLOWING;

        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                mCurrentMode, true, mCurrentMarker));
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.overlook(0);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        mLocationClient = new LocationClient(getApplicationContext());
        //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);
        //注册监听函数
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("wgs84");
        //可选，默认gcj02，设置返回的定位结果坐标系
        int span = 3000;
        option.setScanSpan(span);
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的

        option.setIsNeedAddress(true);
        //可选，设置是否需要地址信息，默认不需要

        option.setOpenGps(true);
        //可选，默认false,设置是否使用gps

        option.setLocationNotify(true);
        //可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果

        option.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”

        option.setIsNeedLocationPoiList(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到

        option.setIgnoreKillProcess(false);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死

        option.setEnableSimulateGps(false);
        //可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocationClient.setLocOption(option);
        mLocationClient.start();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                initMyLocation();
            } else {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    //申请定位权限
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    initMyLocation();
                }
            }
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        double x = event.values[SensorManager.DATA_X];
        if (Math.abs(x - lastX) > 1.0) {
            mCurrentDirection = (int) x;
            locData = new MyLocationData.Builder()
                    .accuracy(mCurrentAccracy)
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(mCurrentLat)
                    .longitude(mCurrentLon).build();
            mBaiduMap.setMyLocationData(locData);
        }
        lastX = x;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    @Override
    public void error(int code, String s) {
        switch (code) {
            case 0:
                client = "数据连接异常";
                break;
            default:
                break;

        }

    }

    @Override
    public void success(int code, Object Object) {
        switch (code) {
            case 0:
                if (Object instanceof SuccessModel) {
                    SuccessModel model = (SuccessModel) Object;
                    if (model.getCode() == 100) {
                        client = "数据连接正常";
                    } else {
                        client = "数据连接异常";
                    }
                }

                break;
            default:
                break;

        }

    }


    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            mCurrentLat = location.getLatitude();//获取纬度信息
            mCurrentLon = location.getLongitude();//获取经度信息
            mCurrentAccracy = location.getRadius();
            LocationAppApplication.getInstance().setmCurrentLat(location.getLatitude());
            LocationAppApplication.getInstance().setmCurrentLon(location.getLongitude());
//            mAddressStr = location.getAddrStr();    //获取当前位置描述信息
            locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }

            Message message = new Message();
            message.what = 1;
            doActionHandler.sendMessage(message);
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(MessageEvent messageEvent) {
        if (messageEventDao.queryBuilder().where(MessageEventDao.Properties.Gdbh.eq(messageEvent.getGdbh())).unique() != null) {
            messageEventDao.update(messageEvent);
        } else {
            messageEventDao.insert(messageEvent);
        }
        mTvSocket.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_radius_yell));
        List<MessageEvent> list = messageEventDao.queryBuilder().where(MessageEventDao.Properties.Type.between(0, 2)).list();
        mTvSocket.setText(String.valueOf(list.size()));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(ClearAlarm clearAlarm) {
        mTvSocket.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_radius_yell));
        List<MessageEvent> list = messageEventDao.queryBuilder().where(MessageEventDao.Properties.Type.between(0, 2)).list();
        mTvSocket.setText(String.valueOf(list.size()));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(ErrorMessage errorMessage) {
        BaseUtil.showToast(mContext, errorMessage.getError());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(TimerEventStart eventStart) {
        startTimer();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(TimerEventStop eventStop) {
        stopTimer();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void SocketEvent(SocketEvent socketEvent) {
        if (LocationAppApplication.getInstance().isSocketIsCanClient()) {
            mAddress.setText("连接：正常");
            mAddress.setTextColor(Color.GREEN);
            mTvSocket.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_radius_yell));
        } else {
            mAddress.setText("连接：异常");
            mAddress.setTextColor(Color.RED);
            mTvSocket.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_radius_red));
        }
    }

    public void checkSocket() {
        LocationAppApplication.getInstance().getThreadPoolExecutor().execute(new Runnable() {
            @Override
            public void run() {
                clientServer();
            }

            public void clientServer() {
                try {
                    Socket s = new Socket();
                    SocketAddress socketAddress = new InetSocketAddress(socketIp, Integer.valueOf(socketPort));
                    s.connect(socketAddress, 1500);
                    LocationAppApplication.getInstance().setSocketIsCanClient(true);
                    s.close();
                    EventBus.getDefault().post(new SocketEvent());
                } catch (Exception e) {
                    LocationAppApplication.getInstance().setSocketIsCanClient(false);
                    EventBus.getDefault().post(new SocketEvent());
                    SystemClock.sleep(15000);
                    clientServer();
                }

            }
        });
    }

    public void startTimer() {
        if (mTimer != null) {
            try {
                mTimer.cancel();
            } catch (Exception e) {

            }
        }

        mTimer = new Timer();
        setTimerTask();
    }

    public void stopTimer() {
        mTimer.cancel();
        if (datagramSocket !=null&& !datagramSocket.isClosed()) {
            datagramSocket.close();
        }
    }

    private void setTimerTask() {
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if ((String.valueOf(MainActivity.this.mCurrentLon).equals("4.9E-324")) || (String.valueOf(MainActivity.this.mCurrentLat).equals("4.9E-324"))) {
                    return;
                }
                //http版本
         /*       HashMap localHashMap = new HashMap();
                localHashMap.put("time", BaseUtil.date2String(new Date()));
                localHashMap.put("east", String.valueOf(MainActivity.this.mCurrentLon));
                localHashMap.put("north", String.valueOf(MainActivity.this.mCurrentLat));
                localHashMap.put("userId", tm.getDeviceId());
                OkHttpUtil.post(url + "CoreServlet", localHashMap, 0, SuccessModel.class, MainActivity.this);
          */

         //tcp版本
                try {
                    GpsModel model = new GpsModel();
                    model.setUserid(tm.getDeviceId());
                    model.setLatitude(String.valueOf(mCurrentLat));
                    model.setLongitude(String.valueOf(mCurrentLon));
                    Socket s = new Socket();
                    SocketAddress socketAddress = new InetSocketAddress(UrlUtil.getSocketIp(mContext), Integer.valueOf(UrlUtil.getSocketPort(mContext)));
                    s.connect(socketAddress, 1500);
                    PrintWriter pw = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), "UTF-8"), true);
                    pw.println(LocationAppApplication.getInstance().getGson()
                            .toJson(model));
                    pw.flush();
                    s.shutdownOutput();
                    s.close();
                    LocationAppApplication.getInstance().setSocketIsCanClient(true);
                    EventBus.getDefault().post(new SocketEvent());
                } catch (Exception e) {
                    LocationAppApplication.getInstance().setSocketIsCanClient(false);
                    EventBus.getDefault().post(new SocketEvent());
                }

               //udp版本
       /*         try {
                    if (datagramSocket == null|| datagramSocket.isClosed()) {
                        datagramSocket = new DatagramSocket();
                    }
                        GpsModel model = new GpsModel();
                        model.setUserid(tm.getDeviceId());
                        model.setLatitude(String.valueOf(mCurrentLat));
                        model.setLongitude(String.valueOf(mCurrentLon));

                        //准备数据，把数据封装到数据包中。
                        String data = LocationAppApplication.getInstance().getGson()
                                .toJson(model);
                        //创建了一个数据包

                        DatagramPacket packet = new DatagramPacket(data.getBytes(), data.getBytes().length, InetAddress.getByName(socketIp), Integer.valueOf(socketPort));
                        //调用udp的服务发送数据包
                        datagramSocket.send(packet);
                        //关闭资源 ---实际上就是释放占用的端口号

                } catch (Exception e) {
                    EventBus.getDefault().post(new ErrorMessage("udp发送失败"));
                }*/


            }
        }, 1000L, 7000L);
    }

}

