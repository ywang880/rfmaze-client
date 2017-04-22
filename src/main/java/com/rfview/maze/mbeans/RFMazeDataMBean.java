package com.rfview.maze.mbeans;

public interface RFMazeDataMBean {

    int getCounter();
    
    String getEtag();
    
    void setCounter(int count);
        
    boolean isConnected();

    void shutdown(String hardware);

    String getStatus();

    void setStatus(String status);

    String getData();

    void setData(String data);

    public String getCommand();

    String execute(String command);
    
    void generateEvent(String e);
    
    int getMatrix(String hardware, int x, int y);
    
    void dumpPower(String hardware);
    
    void dumpMatrix(String hardware);
}
