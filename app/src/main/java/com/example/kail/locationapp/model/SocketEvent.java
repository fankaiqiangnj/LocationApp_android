package com.example.kail.locationapp.model;

public class SocketEvent {
    public SocketEvent(boolean canClient) {
        this.canClient = canClient;
    }

    private boolean canClient;

    public boolean isCanClient() {
        return canClient;
    }

    public void setCanClient(boolean canClient) {
        this.canClient = canClient;
    }
}
