package com.rfview.comm;

import java.io.Serializable;

public class ProcessInfo implements Serializable {

    private static final long serialVersionUID = 3836727812735608377L;
    private String pid;
    private String configFile;
    private String status;

    public ProcessInfo(String pid, String configFile, String status) {
        this.pid = pid;
        this.configFile = configFile;
        this.status = status;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getConfigFile() {
        return configFile.replace(".cfg", "");
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
