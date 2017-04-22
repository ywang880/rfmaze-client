package com.rfview.comm;

public class MazeServer {

    // RFMAZE1_32x32 192.168.0.116 29010
    private final String name;
    private final String ip;
    private final int port;

    public MazeServer(String name, String ip, int port) {
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
