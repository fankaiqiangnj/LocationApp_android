package com.example.kail.locationapp.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class MessageEvent {
    public MessageEvent() {
    }

    public MessageEvent(String gdbh, String qxnr, String longitude, String latitude) {
        this.gdbh = gdbh;
        this.qxnr = qxnr;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    @Generated(hash = 104803499)
    public MessageEvent(String gdbh, String qxnr, String longitude, String latitude,
            int type) {
        this.gdbh = gdbh;
        this.qxnr = qxnr;
        this.longitude = longitude;
        this.latitude = latitude;
        this.type = type;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    /**
     * gdbh : 工单号
     * qxnr : 抢修内容
     * gps : 坐标,坐标
     */
    @Id
    private String gdbh;
    private String qxnr;
    private String longitude;
    private String latitude;

    private int type;

    public String getGdbh() {
        return gdbh;
    }

    public void setGdbh(String gdbh) {
        this.gdbh = gdbh;
    }

    public String getQxnr() {
        return qxnr;
    }

    public void setQxnr(String qxnr) {
        this.qxnr = qxnr;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
