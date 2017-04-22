package com.rfview.utils.db;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import com.rfview.BaseActionSupport;
import com.rfview.conf.Assignment;
import com.rfview.maze.Server;
import com.rfview.maze.User;
import com.rfview.utils.DbAccess;
import com.rfview.utils.Util;

public class DatabaseViewAction extends BaseActionSupport {

    private static final String STRING_NA = " - ";
    private static final long serialVersionUID = -7025202904193630399L;
    private static final String[] userDef = new String[] { "ID", "Password", "First name",
            "Last name", "Organization", "Email", "Phone", "Expiration" };

    private static final String[] serverDef = new String[] { "Name", "Type", "IP", "Port" };

    private static final String[] assignmentDef = new String[] { "Hardware", "User",
            "Assigned Rows", "Assigned Columns" };

    private final DbAccess dbAccess = DbAccess.getInstance();

    private String[][] udata;
    private String[][] sdata;
    private String[][] adata;

    private String command;
    private String result;
    
    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public List<String> getUserTableHeader() {
        return Arrays.asList(userDef);
    }

    public List<String> getServerTableHeader() {
        return Arrays.asList(serverDef);
    }

    public List<String> getAssignmentsTableHeader() {
        return Arrays.asList(assignmentDef);
    }

    public DatabaseViewAction() {
    }

    public String[][] getUdata() {
        return udata;
    }

    public void setUdata(String[][] udata) {
        this.udata = udata;
    }

    public String[][] getSdata() {
        return sdata;
    }

    public void setSdata(String[][] sdata) {
        this.sdata = sdata;
    }

    public String[][] getAdata() {
        return adata;
    }

    public String query() {
        if (!Util.isBlank(command)) {
            setResult(executeCommand(command));
        }
        try {
            List<User> users = dbAccess.queryUsers();
            udata = new String[users.size()][userDef.length];
            for (int i = 0; i < users.size(); i++) {
                udata[i][0] = users.get(i).getId();
                udata[i][1] = users.get(i).getPassword();
                udata[i][2] = getAndUseDefaultIfNotExist(users.get(i).getFirstname());
                udata[i][3] = getAndUseDefaultIfNotExist(users.get(i).getLastname());
                udata[i][4] = getAndUseDefaultIfNotExist(users.get(i).getOrganization());
                udata[i][5] = getAndUseDefaultIfNotExist(users.get(i).getPhone());
                udata[i][6] = getAndUseDefaultIfNotExist(users.get(i).getEmail());
                udata[i][7] = getAndUseDefaultIfNotExist(users.get(i).getExpiration());
            }
        } catch (SQLException e) {
            logger.error("query()", e);
        }

        // server query
        try {
            List<Server> servers = dbAccess.queryServers();
            sdata = new String[servers.size()][serverDef.length];
            for (int i = 0; i < servers.size(); i++) {
                Server s = servers.get(i);
                sdata[i][0] = getAndUseDefaultIfNotExist(s.getName());
                sdata[i][1] = getAndUseDefaultIfNotExist(s.getType());
                sdata[i][2] = getAndUseDefaultIfNotExist(s.getIp());
                sdata[i][3] = Integer.toString(s.getPort());
            }
        } catch (SQLException e) {
            logger.error("query()", e);
        }

        // assignment query
        try {
            List<Assignment> assignements = dbAccess.queryAssignments();
            adata = new String[assignements.size()][assignmentDef.length];
            for (int i = 0; i < assignements.size(); i++) {
                Assignment a = assignements.get(i);
                adata[i][0] = getAndUseDefaultIfNotExist(a.getHardware());
                adata[i][1] = getAndUseDefaultIfNotExist(a.getUser());
                adata[i][2] = getAndUseDefaultIfNotExist(a.getRows());
                adata[i][3] = getAndUseDefaultIfNotExist(a.getCols());
            }
        } catch (SQLException e) {
            logger.error("query()", e);
        }
        return SUCCESS;
    }

    private String getAndUseDefaultIfNotExist(String value) {
        return (value == null) ? STRING_NA : value;
    }

    private String executeCommand(String sqlCmd) {
        return dbAccess.execute(sqlCmd);
    }
}
