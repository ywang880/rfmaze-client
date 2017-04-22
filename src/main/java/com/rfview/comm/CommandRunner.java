package com.rfview.comm;

import java.util.List;

public class CommandRunner {

    private static final CommandRunner instance = new CommandRunner();
    
    public static CommandRunner getInstance() {
        return instance;
    }
    
    public List<ProcessInfo> getProcesses() {
        return null;
    }
    
    public void startProcess(String configfile) {
        
    }
    
    public void stopProcess(int pid) {
        
    }
}
