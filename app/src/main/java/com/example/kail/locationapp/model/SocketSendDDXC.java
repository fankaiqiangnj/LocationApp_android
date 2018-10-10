package com.example.kail.locationapp.model;

public class SocketSendDDXC {
    String gdbh;
    String ddxc;
    private String longitude;
    private String latitude;

    public SocketSendDDXC() {
    }

    public SocketSendDDXC(String gdbh, String ddxc, String longitude, String latitude) {
        this.gdbh = gdbh;
        this.ddxc = ddxc;
        this.longitude = longitude;
        this.latitude = latitude;
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

    public String getGdbh() {
        return gdbh;
    }

    public void setGdbh(String gdbh) {
        this.gdbh = gdbh;
    }

    public String getDdxc() {
        return ddxc;
    }

    public void setDdxc(String jdsj) {
        this.ddxc = jdsj;
    }
}
