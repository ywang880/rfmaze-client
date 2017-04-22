package com.rfview.maze;

public class Server {

	private String name;
    private String type;
    private String ip;
    private int port;

    public Server(String name, String type, String ip, int port) {
        super();
        this.name = name;
        this.type = type;
        this.ip = ip;
        this.port = port;
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

    public boolean isQRB() {
        return type != null && type.matches("(?i)\\s*K\\s*");
    }
    
    public boolean isLTE() {
        return type != null && type.matches("(?i)\\s*L\\s*");
    }    

    public boolean isRBM() {
        return type != null && type.matches("(?i)\\s*R\\s*");
    }

    public boolean isSwitchingType() {
        return isLTE() || isRBM();
    }

    @Override
	public String toString() {
		return "Server [name=" + name + ", type=" + type + ", ip=" + ip + ", port=" + port + "]";
	}
}
