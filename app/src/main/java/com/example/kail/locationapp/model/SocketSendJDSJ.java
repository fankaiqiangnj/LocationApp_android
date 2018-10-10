package com.example.kail.locationapp.model;

public class SocketSendJDSJ {
    String gdbh;
    String jdsj;
    private String longitude;
    private String latitude;



    public SocketSendJDSJ(String gdbh, String jdsj, String longitude, String latitude) {
        this.gdbh = gdbh;
        this.jdsj = jdsj;
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

    public String getJdsj() {
        return jdsj;
    }

    public void setJdsj(String jdsj) {
        this.jdsj = jdsj;
    }
}
