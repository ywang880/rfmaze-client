package com.rfview;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import com.rfview.conf.Assignment;
import com.rfview.conf.BroadcastConf;
import com.rfview.conf.MatrixConfig;
import com.rfview.maze.User;
import com.rfview.utils.CommandBuilder;
import com.rfview.utils.Constants;
import com.rfview.utils.DbAccess;
import com.rfview.utils.Util;

public class AssignOutputsAction extends BaseActionSupport {

    private static final long serialVersionUID = -6798445163576134855L;
    private static final String[] STR_SIGNATURE = { String.class.getName() };

    private BroadcastConf bConf = BroadcastConf.getInstance();
    private MatrixConfig mConfg = MatrixConfig.getInstance();

    private String action;
    private List<String> assignedusers;
    private String assigntouser;
    private final DbAccess dbAccess = DbAccess.getInstance();
    private String selectedrows;
    private String selectedcols;
    private String hardware;
    private String actionCmd="";
    private List<String> params;
    private int numRows;
    private int numCols;
    private Cell[] matrix;

    private String matrixRowLabel;
    private String matrixRowDescripton;

    private Assignment assignment;
    private List<THeader> tableHeader;
    private List<String> users;

    public AssignOutputsAction() {
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int numberOfRows() {
        return numRows;
    }

    public String getHardware() {
        return hardware;
    }

    public void setHardware(String hardware) {
        this.hardware = hardware;
        sessionMap.put(Constants.KEY_HARDWARE, hardware);
    }

    public List<THeader> getTableHeader() {
        return tableHeader;
    }

    public String getAssigntouser() {
        return assigntouser;
    }

    public String getMatrixRowLabel() {
        return matrixRowLabel;
    }

    public void setMatrixRowLabel(String matrixRowLabel) {
        this.matrixRowLabel = matrixRowLabel;
    }

    public String getMatrixRowDescripton() {
        return matrixRowDescripton;
    }

    public void setMatrixRowDescripton(String matrixRowDescripton) {
        this.matrixRowDescripton = matrixRowDescripton;
    }

    public void setAssigntouser(String assigntouser) {
        logger.info("Assign rows and columns to user [" + assigntouser + "]");
        this.assigntouser = assigntouser;
        if (sessionMap.containsKey(Constants.KEY_ASSIGNTOUSER)) {
            sessionMap.remove(Constants.KEY_ASSIGNTOUSER);
        }
        sessionMap.put(Constants.KEY_ASSIGNTOUSER, assigntouser);
    }

    public int numberOfCols() {
        return numCols;
    }

    public Cell[] getMatrix() {
        return matrix;
    }

    public List<String> getUsers() {
        return users;
    }

    public String execute() {
        if (sessionMap!=null) {
            String uid = (String)sessionMap.get(Constants.KEY_LOGIN_ID);
            if (uid == null) {
                return "login";
            }
            this.username = uid;

            hardware = (String)sessionMap.get(Constants.KEY_HARDWARE);
            assigntouser = (String)sessionMap.get(Constants.KEY_ASSIGNTOUSER);
        }

        logger.info("User=" + username + ", Action = " + action);
        if ((action!=null) && !action.trim().isEmpty()) {
            String tokens[] = action.split("\\s+");
            actionCmd = tokens[0];
            if (tokens.length > 1) {
                params = new ArrayList<String>();
                for (int i = 1; i < tokens.length; i++) {
                    params.add(tokens[i].trim());
                }
            }
        }

        // process browser action
        if (actionCmd.equals(Constants.KEY_ASSIGNMENT)) {
            for (int i = 0; i < params.size(); i++) {
                logger.info("Output param " +  params.get(i));
            }
            try {
                assignment = new Assignment(getAssigntouser(), null, params.get(0), false, true);
                dbAccess.updateAssignment(getHardware(), getAssigntouser(), assignment);
            } catch (SQLException e) {
                setErrorMessage("Failed to assign selected rows and cols to user " + getAssigntouser());
                logger.error(e);
            }

            forceReload(getAssigntouser());
        } else if (actionCmd.equals("reassignment")) {
            //reassignment 5 from chamber2 to chamber3
            // command     0   1      2     3     4
            if (params.size() < 3) {
                logger.warn("Invalid command arguments");
            } else {
                String columnToReassign = params.get(0);
                String fromUser = params.get(2);
                String toUser = params.get(4);
                logger.debug("Reassign column " + columnToReassign + " from " + fromUser + " to user " + toUser);
                try {
                    Assignment userAssignment = dbAccess.getAssignment(getHardware(), fromUser);
                    String ss1 = userAssignment.getCols();
                    String ss2 = userAssignment.getRows();
                    if ((ss1!=null) && (columnToReassign.split(",").length == ss1.split(",").length) && (ss2!=null) && !ss2.isEmpty()) {
                        setWarningMessage("Cannot reassign all outputs to different users as the user " + " has inputs assigned. Please reassign inputs first!");
                    } else {
                        dbAccess.reAssignColumn(columnToReassign, getHardware(), fromUser, toUser);
                        dbAccess.deleteAssignmentIfEmpty(fromUser, getHardware());
                    }
                } catch (SQLException e) {
                    logger.warn(e);
                }

                try {
                    Assignment userAssignment = dbAccess.getAssignment(getHardware(), fromUser);
                    String userrows = userAssignment.getRows();
                    if (!Util.isBlank(userrows)) {
                        onSetAttenuration(CommandBuilder.buildSetCommand(userrows, columnToReassign));
                    }
                } catch (SQLException e) {
                    logger.warn(e);
                }

                forceReload(fromUser);
                forceReload(toUser);
            }
        } else if (actionCmd.equals("free")) {
            //reassignment 5 from chamber2 to chamber3
            // command     0   1      2     3     4
            if (params.size() < 3) {
                logger.warn("Invalid command arguments");
            } else {
                String columnToReassign = params.get(0);
                String fromUser = params.get(2);
                logger.debug("free column " + columnToReassign + " from " + fromUser);

                try {
                    Assignment userAssignment = dbAccess.getAssignment(getHardware(), fromUser);
                    String ss1 = userAssignment.getCols();
                    String ss2 = userAssignment.getRows();
                    if ((ss1!=null) && (columnToReassign.split(",").length == ss1.split(",").length) && (ss2!=null) && !ss2.isEmpty()) {
                        setWarningMessage("Cannot free the last assigned output as the user " + " has inputs assigned. Please free inputs first!");
                    } else {
                        dbAccess.freeColumn(columnToReassign, getHardware(), fromUser);
                        dbAccess.deleteAssignmentIfEmpty(fromUser, getHardware());
                    }
                } catch (SQLException e) {
                    logger.warn(e);
                }

                try {
                    Assignment userAssignment = dbAccess.getAssignment(getHardware(), fromUser);
                    String userrows = userAssignment.getRows();
                    if (!Util.isBlank(userrows)) {
                        onSetAttenuration(CommandBuilder.buildSetCommand(userrows, columnToReassign));
                    }
                } catch (SQLException e) {
                    logger.warn(e);
                }
                forceReload(fromUser);
            }
        }

        users = new ArrayList<String>();
        try {
            for (User u : dbAccess.queryUsers()) {
                if (u.getId().equals("admin")) {
                    continue;
                }
                users.add(u.getId());
            }
        } catch (SQLException e1) {
            logger.error(e1);
        }

        assignedusers = new ArrayList<String>();
        try {
            for (User u : dbAccess.queryUsers()) {
                if (!"admin".equals(u.getId())) {
                    assignedusers.add(u.getId());
                }
            }
        } catch (SQLException e) {
            logger.error(e);
        }

        // build matrix and return
        if ((hardware == null) || hardware.trim().isEmpty()) {
            setWarningMessage("NOTE: Select hardware and user.");
        } else {
            Properties props;
            try {
                props = mConfg.loadConfigureFile(hardware);
                numRows = Integer.parseInt(props.getProperty("matrix_inputs"));
                numCols = Integer.parseInt(props.getProperty("matrix_outputs"));
                try {
                    assignment = dbAccess.getAssignment(getHardware(), null);
                    logger.debug(assignment.toString());
                } catch (SQLException e) {
                    logger.error(e);
                }
                matrix = new Cell[numCols];
                tableHeader = new ArrayList<THeader>();

                logger.debug("Get labels for hardware " + getHardware());
                List<MatrixLabel> inputLabels = dbAccess.queryInputLabels(getHardware());
                List<MatrixLabel> outputLabels = dbAccess.queryOutputLabels(getHardware());

                boolean useDefault = false;
                if ((inputLabels.size() < numRows) || (outputLabels.size() < numCols)) {
                    useDefault = true;
                }

                if (useDefault) {
                    for (int k = 0 ; k < numCols; k++) {
                        tableHeader.add(new THeader(null, "[" + (k+1) + "] Output" + (k+1), "Output" + (k+1)));
                    }
                } else {
                    int k = 1;
                    for (MatrixLabel l : outputLabels) {
                        tableHeader.add(new THeader(assignment.getUserByReservedCol(k), "[" + k + "] " + l.getLabel(), l.getDescription()));
                        k++;
                    }
                }

                for (int j = 0; j < numCols; j++) {
                    String cUser = assignment.getUserByReservedCol(j+1);
                    if (cUser!=null) {
                        matrix[j] = new Cell(cUser);
                    } else {
                        matrix[j] = new Cell("-");
                    }
                }

                if (useDefault) {
                    setMatrixRowLabel("Input");
                    setMatrixRowDescripton("Input 1");
                } else {
                    setMatrixRowLabel(inputLabels.get(0).getLabel());
                    setMatrixRowDescripton(inputLabels.get(0).getDescription());
                }

            } catch (FileNotFoundException e) {
                setErrorMessage("File " +hardware + ".cfg not found!");
            } catch (IOException e) {
                setErrorMessage("Failed to read configure file.");
            }
        }
        return SUCCESS;
    }

    public List<String> getHardwarelist() {
    	return bConf.getNonSwitchHardwareList();
    }

    public List<String> getAssignedusers() {
        return assignedusers;
    }

    public void setAssignedusers(List<String> assignedusers) {
        this.assignedusers = assignedusers;
    }

    public String getSelectedrows() {
        return selectedrows;
    }

    public void setSelectedrows(String selectedrows) {
        this.selectedrows = selectedrows;
    }

    public String getSelectedcols() {
        return selectedcols;
    }

    public void setSelectedcols(String selectedcols) {
        this.selectedcols = selectedcols;
    }

    private void forceReload(String user) {
        mgmt.setReload(user, true);
        mgmt.setUpdate(user, true);
    }

    private void onSetAttenuration(String command) {
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
}
