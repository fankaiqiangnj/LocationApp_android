package com.example.kail.locationapp.model;

public class MessageEvent {
    public MessageEvent() {
    }

    public MessageEvent(String gdbh, String qxnr, String gps) {
        this.gdbh = gdbh;
        this.qxnr = qxnr;
        this.gps = gps;
    }

    /**
     * gdbh : 工单号
     * qxnr : 抢修内容
     * gps : 坐标,坐标
     */

    private String gdbh;
    private String qxnr;
    private String gps;
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

    public String getGps() {
        return gps;
    }

    public void setGps(String gps) {
        this.gps = gps;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
