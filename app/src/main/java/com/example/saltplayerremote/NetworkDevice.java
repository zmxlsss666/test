package com.example.saltplayerremote;

public class NetworkDevice {
    private String ip;
    private String name;

    public NetworkDevice(String ip, String name) {
        this.ip = ip;
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public String getName() {
        return name;
    }
}