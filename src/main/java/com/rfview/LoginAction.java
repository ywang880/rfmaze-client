package com.rfview;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.rfview.comm.ProcessInfo;
import com.rfview.conf.Assignment;
import com.rfview.conf.BroadcastConf;
import com.rfview.maze.Server;
import com.rfview.maze.User;
import com.rfview.utils.Constants;
import com.rfview.utils.db.DbAccess;

public class LoginAction extends BaseActionSupport {

    private static final long serialVersionUID = -7025202904193630399L;
    private final DbAccess dbAccess = DbAccess.getInstance();
    private final List<String> mazeServers = new ArrayList<String>();
    private final List<String> hardwares = new ArrayList<String>();

    private String userName;
    private String password;

    public LoginAction() {
    }

    public String authenticate() {
        try {
            User dbUser  = dbAccess.queryUser(userName);
            if (dbUser==null) {
                setErrorMessage("Authentication failure: invalid user or password!");
                return ERROR;
            }

            if ((userName==null) || (password == null)) {
                setErrorMessage("Authentication failure: invalid user or password!");
                return ERROR;
            }

            if (!password.equals(dbUser.getPassword())) {
                setErrorMessage("Authentication failure: invalid user or password!");
                return ERROR;
            }

            logger.info(dbUser.getId()+ ", " + dbUser.getPassword() + ", " + dbUser.getAssigned_rows() + ", " + dbUser.getAssigned_col());
            if ( dbUser.equals(userName) && dbUser.getPassword().equals(password) ) {
                mazeServers.clear();
                List<Server> servers = dbAccess.queryServers();
                for (Server s : servers) {
                    if (s.getType().equalsIgnoreCase("maze")) {
                        mazeServers.add(s.getIp()+":"+s.getPort());
                    }
                }
            }

            if (sessionMap!=null) {
                logger.info("user id set to session " + userName);
                if (sessionMap.containsKey(Constants.KEY_LOGIN_ID)) {
                    sessionMap.remove(Constants.KEY_LOGIN_ID);
                }
                sessionMap.put(Constants.KEY_LOGIN_ID, userName);
            }

            if ("admin".equalsIgnoreCase(userName) || "swadmin".equalsIgnoreCase(userName)) {
                return SUCCESS;
            }

            Assignment assignment = dbAccess.getAssignment(userName);
            List<String> hwAssigned = assignment.getHardwares();
            List<String> availableServers = BroadcastConf.getInstance().getNonSwitchHardwareList();
            logger.info("availableServers=" + StringUtils.join(availableServers, ", "));
            List<ProcessInfo> pinfo = mgmt.getProcesses();
            for (String ss : availableServers) {
                for (String sss : hwAssigned) {
                    if (sss.equals(ss) && isRunning(pinfo, sss)) {
                        hardwares.add(ss);
                    }
                }
            }

            if (hardwares.isEmpty()) {
                setWarningMessage("There are apparently no running rfmaze processes. Please start process first!");
            }

            return "rfmaze";
        } catch (SQLException e) {
            logger.error("DB access failure", e);
        }
        return ERROR;
    }

    public boolean isRunning(List<ProcessInfo> pinfo, String pname) {
        for (ProcessInfo info : pinfo) {
            if (info.getConfigFile().endsWith(pname) && info.getStatus().contains("running")) {
                return true;
            }
        }
        return false;
    }

    public String getUsername() {
        return userName;
    }

    public void setUsername(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getMazeServers() {
        return mazeServers;
    }

    public List<String> getHardwares() {
        return hardwares;
    }

}
