package com.rfview.maze;

public class BoardcastServer {

    private String name;
    private String type;
    private String ip = "192.168.0.116";
    private int port = 29000;

    private static final BoardcastServer instance = new BoardcastServer();
    
    public static BoardcastServer getInstance() {
        return instance;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
