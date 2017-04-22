package com.rfview.management;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.log4j.Logger;

import com.rfview.LogFile;
import com.rfview.comm.ProcessInfo;
import com.rfview.maze.Datagrid;

public class MazeserverManagement implements MazeserverManagementMBean {

    private static final String CATALINA_CONF_DIR = System.getenv("CATALINA_HOME");
    private static final String CONF_DIR = System.getProperty("rfmaze.conf.dir");
    private static final String MAZE_HOME = System.getProperty("rfmaze.home.dir");
    private static final String RFMAZE_BIN_DIR=MAZE_HOME + File.separator + "bin" + File.separator;
    private final String catalina_base = System.getProperty("catalina.base");
    private final String catalina_home = System.getProperty("catalina.home");

    private final String configureDir = (CONF_DIR != null) ? CONF_DIR : CATALINA_CONF_DIR + File.separator + "conf";
    private List<ProcessInfo> processes;
    private String processId=null;

    private final Map<String, AtomicBoolean> reload = new HashMap<String, AtomicBoolean>();
    private final Map<String, AtomicBoolean> update = new HashMap<String, AtomicBoolean>();

    private final Format format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
    private Logger logger = Logger.getLogger(MazeserverManagement.class.getName());

    private static MazeserverManagement instance = new MazeserverManagement();
    private static final Map<String, String> pidMapper = new HashMap<String, String>();
  
    private MazeserverManagement() {
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        try {
            server.registerMBean(this, new ObjectName("com.rfview.management:name=server"));
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public static MazeserverManagement getInstance() {
        return instance;
    }

    public String getConfigureDir() {
        return configureDir;
    }

    public String [] getProperties() {
        String [] properties = {
                CATALINA_CONF_DIR,
                CONF_DIR,
                MAZE_HOME,
                RFMAZE_BIN_DIR,
                catalina_base,
                catalina_home
        };

        return properties;
    }

    public String getCatalinaBase() {
        return catalina_base;
    }

    public String getCatalinaHome() {
        return catalina_home;
    }

    public String getProcessId() {
        if ((processId == null) || processId.isEmpty()) {
            processId = runCommand("getpid.sh");
        }
        logger.info("tomcat process ID is " + processId);
        return processId;
    }

    public List<ProcessInfo> getProcesses() {

        processes = new ArrayList<ProcessInfo>();

        File cfgFile = new File(CONF_DIR);
        File[] cfgFiles = cfgFile.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                String fname = pathname.getName();
                return (fname.endsWith("cfg") && !fname.contains("_broadcast"));
            }
        });

        Set<String> fileList = new HashSet<String>();
        for (File f : cfgFiles) {
            fileList.add(f.getName());
        }

        InputStream is = null;
        try {
            String command = RFMAZE_BIN_DIR + "rfmaze_mgmt.sh status";
            Process p = Runtime.getRuntime().exec(command);
            is = p.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = br.readLine();
            while (line != null) {
                if (line.trim().isEmpty()) {
                    line = br.readLine();
                    continue;
                }
                String[] tokens = line.split("\\s+");
                ProcessInfo info = new ProcessInfo(tokens[0], tokens[1], "running");
                pidMapper.put(tokens[1], tokens[0]);
                processes.add(info);
                if (fileList.contains(tokens[1])) {
                    fileList.remove(tokens[1]);
                }
                line = br.readLine();
            }
        } catch (Exception e) {
            logger.error(e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    logger.error(e);
                }
            }
        }

        for (String name : fileList) {
            ProcessInfo info = new ProcessInfo("-", name, "stopped");
            processes.add(info);
        }
        return processes;
    }

    public boolean isProcessStarted(String configfile) {
        InputStream is = null;
        BufferedReader br = null;
        try {
            String command = RFMAZE_BIN_DIR + "rfmaze_mgmt.sh status";
            Process p = Runtime.getRuntime().exec(command);
            is = p.getInputStream();
            br = new BufferedReader(new InputStreamReader(is));
            String line = br.readLine();
            while (line != null) {
                if (line.contains(configfile)) {
                    return true;
                }
                line = br.readLine();
            }
            return false;
        } catch (Exception e) {
            logger.error(e);
        } finally {
            if (is!=null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
            if (br!=null) {
                try {
                    br.close();
                } catch (IOException e) {
                }
            }
        }
        return false;
    }

    public void startProcess(String user, String configfile) {
        try {
            String command = RFMAZE_BIN_DIR + "rfmaze_mgmt.sh start " + configfile;
            logger.info("Invoke command " + command + " to start process");
            Runtime.getRuntime().exec(command);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public String[] getPids() {
        if (processes == null) {
            getProcesses();
        }

        String[] result = new String[processes.size()];
        for (int i = 0; i < processes.size(); i++) {
            result[i] = processes.get(i).getPid();
        }
        return result;
    }

    public String getPid(String name) {
        String pid = pidMapper.get(name);
        return pid;
    }

    public void removePid(String name) {
        pidMapper.remove(name);
    }

    public String[] getConfigures() {
        if (processes == null) {
            getProcesses();
        }

        String[] result = new String[processes.size()];
        for (int i = 0; i < processes.size(); i++) {
            result[i] = processes.get(i).getConfigFile();
        }
        return result;
    }

    public LogFile[] getAllLogFiles() {
        File file = new File(getLogFolder());
        String[] files = file.list(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.matches(".*\\.cfg.log[0-9]{0,2}");
            }
        });

        LogFile[] resultset = new LogFile[files.length];
        int i = 0;
        String logFileDir = getLogFolder();
        for (String f : files) {
            File ff = new File(logFileDir + File.separator + f);
            resultset[i++] = new LogFile(ff.getName(), ff.length(), convertTime(ff.lastModified()));
        }

        return resultset;
    }

    public String getLogFolder() {
        return getConfigureDir()+File.separator +"log";
    }

    public void stopProcess(String confFileName) {
        String pid = getPid(confFileName);
        if (pid==null) {
            logger.info("Process " + confFileName + " not found.");
        }

        try {
            String command = RFMAZE_BIN_DIR + "rfmaze_mgmt.sh stop " + pid;
            logger.info("Invoke command " + command + " to stop process");
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            logger.error(e);
        }
        removePid(pid);

        String server = confFileName.replace(".cfg", "").trim();
        Datagrid.getInstance().removeMatrix(server);
        Datagrid.getInstance().removeOffsetData(server);
    }

    public void deleteLogFiles(String name) {
        // remove log files
        LogFile[] files = MazeserverManagement.getInstance().getAllLogFiles();
        for (LogFile lf : files) {
            String lfname = lf.getName();
            int pos = lfname.indexOf('.');
            String namenoextension = lfname.substring(0,pos);
            if (namenoextension.equals(name)) {
                File ff = new File(MazeserverManagement.getInstance().getLogFolder()+File.separator+lfname);
                ff.delete();
            }
        }
    }

    private String convertTime(long time){
        Date date = new Date(time);
        return format.format(date);
    }

    public String runCommand(String command_string) {
        String cmdString= catalina_base+File.separator+"bin"+File.separator+command_string;
        logger.info("Execute command " + cmdString);

        String command[] = cmdString.split(",");
        ProcessBuilder builder = new ProcessBuilder(command);
        Process process = null;
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        StringBuilder retResult = new StringBuilder();
        try {
            process = builder.start();
            is = process.getInputStream();
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                retResult.append(line);
            }
        } catch (IOException e) {
        } finally {
            if (is!=null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }

            if (isr!=null) {
                try {
                    isr.close();
                } catch (IOException e) {
                }
            }

            if (br!=null) {
                try {
                    br.close();
                } catch (IOException e) {
                }
            }

            if (process!=null) {
                process.destroy();
            }
        }
        return retResult.toString();
    }

    public synchronized boolean isReload(String user) {
        if (reload.get(user) == null) {
            logger.info(" no flag set for user " + user);
            return false;
        }
        return reload.get(user).get();
    }

    public synchronized void setReload(String user, boolean flag) {
        AtomicBoolean reloadFlag = reload.get(user);
        if (reloadFlag == null) {
            reloadFlag = new AtomicBoolean();
        }
        reloadFlag.set(flag);
        reload.put(user, reloadFlag);
    }

    public synchronized boolean  isUpdate(String user) {
        if (update.get(user) == null) {
            return false;
        }

        return update.get(user).get();
    }

    public synchronized void setUpdate(String user, boolean flag) {
        AtomicBoolean updateFlag = update.get(user);
        if (updateFlag == null) {
            updateFlag = new AtomicBoolean();
        }
        updateFlag.set(flag);
        update.put(user, updateFlag);
    }
}
