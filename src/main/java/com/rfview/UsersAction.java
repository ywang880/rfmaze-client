package com.rfview;

import java.lang.management.ManagementFactory;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import com.rfview.comm.ProcessInfo;
import com.rfview.conf.Assignment;
import com.rfview.conf.BroadcastConf;
import com.rfview.conf.MatrixConfig;
import com.rfview.maze.MatrixSize;
import com.rfview.maze.User;
import com.rfview.utils.CommandBuilder;
import com.rfview.utils.Constants;
import com.rfview.utils.DbAccess;
import com.rfview.utils.Util;

public class UsersAction extends BaseActionSupport {

    private static final long serialVersionUID = 1L;
    private static final String[] STR_SIGNATURE = { String.class.getName() };
    private List<User> users = null;
    private User user = null;
    private String username;
    private String assignTo = "";
    private String assignedHardwareToUser;
    private String hardwareAssignment;
    private BroadcastConf bConf = BroadcastConf.getInstance();
    private String action;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAssignTo() {
        return assignTo;
    }

    public void setAssignTo(String assignTo) {
        this.assignTo = assignTo;
    }

    public String getAssignedHardwareToUser() {
        return assignedHardwareToUser;
    }

    public void setAssignedHardwareToUser(String assignedHardwareToUser) {
        this.assignedHardwareToUser = assignedHardwareToUser;
    }

    public String commit() {
        return SUCCESS;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getHardwareAssignment() {
        return hardwareAssignment;
    }

    public void setHardwareAssignment(String hardwareAssignment) {
        this.hardwareAssignment = hardwareAssignment;
    }

    public List<String> getAssignedHardwares() {
        System.out.println("getAssignedHardwares() ");
        return new ArrayList<>();
    }

    public List<String> getHardwarelist() {
        return bConf.getHardwareList();
    }

    public String queryUserFromDB() {

        if (sessionMap != null) {
            String uid = (String) sessionMap.get(Constants.KEY_LOGIN_ID);
            if (uid == null) {
                return "login";
            }
            this.username = uid;
        }

        logger.info("action and path params = " + action);
        if (isDelete()) {
            String uid = action.split(" ")[1];
            try {
                dbAccess.deleteUser(uid);
                resetAttenuration(uid);
                setWarningMessage(
                        "WARN: The attenuation value set by deleted user has been reset to default value! If process is not running the attenuation values will not be reset.");
            } catch (SQLException e) {
                logger.error("SQL Exception", e);
            }

            buildUserList();
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).getId().equals(uid)) {
                    users.remove(i);
                    break;
                }
            }
            return SUCCESS;
        } else if (user != null) {
            String[] listOfHardwares = getAssignedHardwares(action);
            updateAssignment(username, listOfHardwares);

            try {

                dbAccess.updateUser(user);
                buildUserList();
                return SUCCESS;
            } catch (SQLException e) {
                logger.error("SQL Exception", e);
            }
        }

        buildUserList();
        return SUCCESS;
    }

    private boolean isDelete() {
        return action != null && action.startsWith("delete");
    }

    private void buildUserList() {
        if (users == null) {
            try {
                users = dbAccess.queryUsers();
            } catch (SQLException e) {
                logger.error("SQL Exception", e);
            }
        }
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public String getId() {
        return (user != null) ? user.getId() : "";
    }

    public void setId(String id) {
        isUserCreated();
        user.setId(id);
    }

    public String getPassword() {
        return (user != null) ? user.getPassword() : "";
    }

    public void setPassword(String password) {
        isUserCreated();
        user.setPassword(password);
    }

    public String getFirstname() {
        return (user != null) ? user.getFirstname() : "";
    }

    public void setFirstname(String firstname) {
        isUserCreated();
        user.setFirstname(firstname);
    }

    public String getLastname() {
        return (user != null) ? user.getLastname() : "";
    }

    public void setLastname(String lastname) {
        isUserCreated();
        user.setLastname(lastname);
    }

    public String getOrganization() {
        return (user != null) ? user.getOrganization() : "";
    }

    public void setOrganization(String organization) {
        isUserCreated();
        user.setOrganization(organization);
    }

    public String getEmail() {
        return (user != null) ? user.getEmail() : "";
    }

    public void setEmail(String email) {
        isUserCreated();
        user.setEmail(email);
    }

    public String getPhone() {
        return (user != null) ? user.getPhone() : "";
    }

    public void setPhone(String phone) {
        isUserCreated();
        user.setPhone(phone);
    }

    public String getAssigned_rows() {
        return user.getAssigned_rows();
    }

    public void setAssigned_rows(String assigned_rows) {
        isUserCreated();
        user.setAssigned_rows(assigned_rows);
    }

    public String getAssigned_col() {
        return user.getAssigned_col();
    }

    public void setAssigned_col(String assigned_col) {
        isUserCreated();
        user.setAssigned_col(assigned_col);
    }

    public String getExpiration() {
        return user.getExpiration();
    }

    public void setExpiration(String expiration) {
        isUserCreated();
        user.setExpiration(expiration);
    }

    private void isUserCreated() {
        if (user == null) {
            user = new User();
        }
    }

    private void resetAttenuration(String user) {
        try {
            Assignment assignment = dbAccess.getAssignment(user);
            List<String> hwAssigned = assignment.getHardwares();
            List<ProcessInfo> pinfo = mgmt.getProcesses();

            for (String hardware : hwAssigned) {
                if (isProcessRunning(hardware, pinfo)) {
                    Assignment as = dbAccess.getAssignment(hardware, user);
                    String cols = as.getCols();
                    String rows = as.getRows();
                    if (!Util.isBlank(cols) && !Util.isBlank(rows)) {
                        String cmd = CommandBuilder.buildSetCommand(rows, cols);
                        setAttenuation(cmd, hardware);
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("SQL Exception", e);
        }
    }

    private void setAttenuation(String command, String hardware) {
        String[] cmdOut = { command };
        try {
            ObjectName mBeanName = new ObjectName(Constants.OBJECTMAME_PREFIX + hardware + Constants.OBJECTNAME_SUFFIX);
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            mbs.invoke(mBeanName, "execute", cmdOut, STR_SIGNATURE);
            logger.info("command to server = " + cmdOut[0]);
        } catch (MalformedObjectNameException e) {
            logger.error(e.getMessage(), e);
        } catch (InstanceNotFoundException e) {
            logger.error(e.getMessage(), e);
        } catch (ReflectionException e) {
            logger.error(e.getMessage(), e);
        } catch (MBeanException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private boolean isProcessRunning(String hardware, List<ProcessInfo> info) {
        for (ProcessInfo p : info) {
            if (p.getStatus().equals("running") && hardware.equals(p.getConfigFile())) {
                return true;
            }
        }
        return false;
    }

    private String[] getAssignedHardwares(String params) {
        if (params == null || params.isEmpty()) {
            return new String[0];
        }
        return params.replaceAll("commit\\?hardwares=", "").split(",");
    }

    private void updateAssignment(String userName, String[] selectedHardwares) {

        Set<String> currentList = getAssigned(user.getId());
        Map<String, MatrixSize> insertList = new HashMap<>();

        for (String s : selectedHardwares) {
            if ( s == null || s.isEmpty() || s.matches("\\s*(?i)Undefined\\s*" )) {
                continue;
            }

            if (currentList.contains(s)) {
                currentList.remove(s);
                continue; // exists
            }

            Properties props;
            try {
                props = MatrixConfig.getInstance().getConfiguration(s);
            } catch (Exception e) {
                continue;
            }

            int inputs = Integer.parseInt(props.getProperty("matrix_inputs", "-1"));
            int outputs = Integer.parseInt(props.getProperty("matrix_outputs", "-1"));

            if (inputs == -1 || outputs == -1) {
                continue;
            }
            insertList.put(s, new MatrixSize(inputs, outputs));
        }

        for ( String h : currentList ) {
            logger.info("Delete assignment matrix " + h + " from user " + user.getId() );
            DbAccess.getInstance().freeAllFromUser( user.getId(), h );
        }

        for ( Map.Entry<String, MatrixSize> e : insertList.entrySet() ) {
            logger.info("Assign matrix " + e.getKey() + ", " +  e.getValue().getNumInputs() +  " X " + e.getValue().getNumOuputs() + " to user " + user.getId());
            DbAccess.getInstance().assignAlltoUser(user.getId(),  e.getKey(), e.getValue().getNumInputs(), e.getValue().getNumOuputs());
        }
    }

    private Set<String> getAssigned(String name) {
        Set<String> hardwares = new HashSet<>();
        try {
            Assignment userAssignment = dbAccess.getAssignment(name);
            List<String> hwAssigned = userAssignment.getHardwares();
            for (String sss : hwAssigned) {
                hardwares.add(sss);
            }
        } catch (SQLException e2) {
            logger.warn(e2.getMessage());
        }
        return hardwares;
    }
}
