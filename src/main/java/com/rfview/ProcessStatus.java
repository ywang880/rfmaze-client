package com.rfview;

public class ProcessStatus {

    private int pid;
    private String configuration;
    private String status;
    
    public ProcessStatus(int pid, String configuration, String argument, String status) {
        super();
        this.pid = pid;
        this.configuration = configuration;
        this.status = status;
    }
    
    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getConfiguration() {
        return configuration;
    }

    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isRunning() {
        return status.equalsIgnoreCase("Running");
    }
}
