package com.rfview;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.rfview.comm.ProcessInfo;
import com.rfview.conf.Assignment;
import com.rfview.conf.BroadcastConf;
import com.rfview.utils.Constants;
import com.rfview.utils.DbAccess;

public class SwitchMatrixAction extends BaseActionSupport {

    private static final long serialVersionUID = -7025202904193630399L;
    private final DbAccess dbAccess = DbAccess.getInstance();
    private final List<String> hardwares = new ArrayList<String>();

    public SwitchMatrixAction() {
    }

    public String execute() throws Exception {

        try {

            logger.info("invoke matrix view action");
            if (sessionMap != null) {
                String uid = (String) sessionMap.get(Constants.KEY_LOGIN_ID);
                if (uid == null) {
                    return "login";
                }
                this.username = uid;
            }

            Assignment assignment = dbAccess.getAssignment(username);
            List<String> hwAssigned = assignment.getHardwares();
            List<String> availableServers = BroadcastConf.getInstance().getAllAssignedServers();
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

            return SUCCESS;
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
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getHardwares() {
        return hardwares;
    }

}
