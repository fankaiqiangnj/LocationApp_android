package com.example.kail.locationapp.model;

public class SocketSendFHSD {
    String gdbh;
    String fhsd;
    private String longitude;
    private String latitude;

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

    public SocketSendFHSD(String gdbh, String fhsd, String longitude, String latitude) {

        this.gdbh = gdbh;
        this.fhsd = fhsd;
        this.longitude = longitude;
        this.latitude = latitude;
    }



    public String getGdbh() {
        return gdbh;
    }

    public void setGdbh(String gdbh) {
        this.gdbh = gdbh;
    }

    public String getFhsd() {
        return fhsd;
    }

    public void setFhsd(String jdsj) {
        this.fhsd = jdsj;
    }
}
