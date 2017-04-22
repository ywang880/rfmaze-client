package com.rfview.management;

import java.util.List;

import com.rfview.comm.ProcessInfo;

public interface MazeserverManagementMBean {

    public String getConfigureDir();
    
    public String getProcessId();
    
    public List<ProcessInfo> getProcesses();

    public String[] getPids();

    public String[] getConfigures();

    public void startProcess(String user, String configfile);

    public void stopProcess(String pid);
    
    public boolean isProcessStarted(String configfile);
        
    public void deleteLogFiles(String name);
 
    public String runCommand(String command);
    
    public String [] getProperties();
    
    public String getCatalinaBase();

    public String getCatalinaHome();
}
