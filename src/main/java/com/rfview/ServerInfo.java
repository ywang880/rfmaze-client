package com.rfview;

public class ServerInfo {

    private final String name;
    private final String ip;
    private final int port;

    public ServerInfo(String name, String ip, int port) {
        super();
        this.name = name;
        this.ip = ip;
        this.port = port;
    }

    public String getName() {
        return name;
    }
    
    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }
}
