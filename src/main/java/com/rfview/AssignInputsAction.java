package com.rfview;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
import com.rfview.utils.CommandBuilder;
import com.rfview.utils.Constants;
import com.rfview.utils.DbAccess;
import com.rfview.utils.Util;

public class AssignInputsAction extends BaseActionSupport {

    private static final String CONST_COMMA = ",";

    private static final long serialVersionUID = -6798445163576134855L;

    private static final String CMD_SHARE = "share";
    private static final String CMD_FREE = "free";
    private static final String CMD_REASSIGN = "reassign";
    private static final String CMD_ASSIGN = "assign";
    private static final String DEFAULT_NAME = "-";
    private static final String[] STR_SIGNATURE = { String.class.getName() };

    private BroadcastConf bConf = BroadcastConf.getInstance();
    private MatrixConfig mConfg = MatrixConfig.getInstance();

    private String action;
    private String assigntouser;
    private final DbAccess dbAccess = DbAccess.getInstance();
    private String selectedrows;
    private String selectedcols;
    private String hardware;
    private String actionCmd = "";
    private List<String> users;

    private int numRows;
    private int numCols;
    private Cell[][] matrix;
    private Assignment assignment;
    private List<THeader> tableHeader;

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
        sessionMap.put("hardware", hardware);
    }

    public List<THeader> getTableHeader() {
        return tableHeader;
    }

    public String getAssigntouser() {
        return assigntouser;
    }

    public void setAssigntouser(String assigntouser) {
        logger.info("assign rows and columns to user " + assigntouser);
        this.assigntouser = assigntouser;
        sessionMap.put("assigntouser", assigntouser);
    }

    public int numberOfCols() {
        return numCols;
    }

    public Cell[][] getMatrix() {
        return matrix;
    }

    public Cell[] getCols(int index) {
        return matrix[index];
    }

    public List<String> getUsers() {
        return users;
    }

    public String execute() {
        if (sessionMap != null) {
            String uid = (String) sessionMap.get("loginId");
            if (uid == null) {
                return "login";
            }
            this.username = uid;
            hardware = (String) sessionMap.get("hardware");
            assigntouser = (String) sessionMap.get("assigntouser");
        }

        try {
            users = dbAccess.getAssignedOutputUsers(hardware);
            if (users.isEmpty()) {
                setWarningMessage("No output assigned. Please assign output first!");
                return SUCCESS;
            }
        } catch (SQLException e1) {
            logger.error( e1);
        }

        if ((hardware == null) || (hardware.isEmpty())) {
            setWarningMessage("Hardware not selected. Please choose assign output first!");
            return SUCCESS;
        }

        if (!hasInputAssigned(getHardware())) {
            setWarningMessage("Outputs is not assigned. Please assign outputs first!");
            return SUCCESS;
        }

        logger.info("AssignInputs:execute() Action = " + action);
        List<String> cmd_parameters = new ArrayList<String>();
        if ((action != null) && !action.trim().isEmpty()) {
            String tokens[] = action.split("\\s+");
            actionCmd = tokens[0];
            if (tokens.length > 1) {
                for (int i = 1; i < tokens.length; i++) {
                    cmd_parameters.add(tokens[i].trim());
                }
            }
        }

        // process command
        if (actionCmd.equals(CMD_ASSIGN)) {
            assignInputToUsers(cmd_parameters);
        } else if (actionCmd.equals(CMD_REASSIGN)) {
            reassignInputToUser(cmd_parameters);
        } else if (actionCmd.equals(CMD_FREE)) {
            freeInputFromUser(cmd_parameters);
        } else if (actionCmd.equals(CMD_SHARE)) {
            shareInputWithUser(cmd_parameters);
        }

        if ((hardware == null) || hardware.trim().isEmpty()) {
            setWarningMessage("NOTE: Please select hardware and user to show matrix!");
            return SUCCESS;
        }

        Properties props = null;
        try {
            props = mConfg.loadConfigureFile(hardware);
        } catch (FileNotFoundException e) {
            setErrorMessage("File " + hardware + ".cfg not found!");
        } catch (IOException e) {
            setErrorMessage("Failed to read configure file.");
        }
        if (props == null) {
            setErrorMessage("ERROR: Cannot find configuration file for selected hardware!");
            return SUCCESS;
        }

        numRows = Integer.parseInt(props.getProperty("matrix_inputs"));
        numCols = Integer.parseInt(props.getProperty("matrix_outputs"));

        try {
            assignment = dbAccess.getAssignment(getHardware(), null);
        } catch (SQLException e) {
            logger.error(e);
            setErrorMessage("ERROR: Failed to query user assignment!");
            return SUCCESS;
        }

        if (assignment == null) {
            setErrorMessage("ERROR: Cannot find hardware assignment information!");
            return SUCCESS;
        }

        tableHeader = new ArrayList<THeader>();
        List<MatrixLabel> inputLabels = dbAccess.queryInputLabels(getHardware());
        List<MatrixLabel> outputLabels = dbAccess.queryOutputLabels(getHardware());
        if ((inputLabels.size() < numRows) || (outputLabels.size() < numCols)) {
            setErrorMessage("ERROR: Matrix configuration labels are not assigned. Please assign label first!");
            return SUCCESS;
        }

        Map<String, String> assignedRows = assignment.getAssingedRows();
        Map<String, String> assignedColumns = assignment.getAssingedColumns();
        if (assignedColumns.isEmpty()) {
            setWarningMessage("WARN: The outputs has not yet assigned please assign output first!");
            return SUCCESS;
        }
                
        int numAssignedCols = assignedColumns.size();
        Cell[][] viewmatrix = new Cell[numRows][numAssignedCols];
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numAssignedCols; j++) {
                viewmatrix[i][j] = new Cell(DEFAULT_NAME);
            }
        }

        String[] assignedUsers = new String[numAssignedCols];
        assignedColumns.keySet().toArray(assignedUsers);
        Arrays.sort(assignedUsers);

        logger.info("AssignInputs:execute() build table. Number of assigned users is " + assignedUsers.length);
        for (int i = 0; i < assignedUsers.length; i++) {
            String assignedUser = assignedUsers[i];

            String therows = assignedRows.get(assignedUser);
            if (Util.isBlank(therows)) {
                continue;
            }
            String[] tokens = therows.split(CONST_COMMA);
            for (String token : tokens) {
                int cIdx = Integer.parseInt(token);
                viewmatrix[cIdx-1][i].setName(assignedUser);
                viewmatrix[cIdx-1][0].setRowstatus(Constants.CONST_RESERVED);
            }
        }
        matrix = viewmatrix;

        for (int i = 0; i < assignedUsers.length; i++) {
            tableHeader.add(new THeader(assignedUsers[i], assignedUsers[i], outputLabels.get(i)
                    .getDescription()));
        }

        for (int i = 0; i < numRows; i++) {
            viewmatrix[i][0].setLabel(inputLabels.get(i).getLabel());
            viewmatrix[i][0].setDescription(inputLabels.get(i).getDescription());
        }
        return SUCCESS;
    }

    // Action = share 6 with cage2
    private void shareInputWithUser(List<String> params) {
        if (params.size() < 3) {
            logger.warn("Invalid command arguments");
        } else {
            String rowToShare = params.get(0);
            String toUser = params.get(2);
            logger.debug("Share column " + rowToShare + " with user " + toUser);
            dbAccess.share(rowToShare, getHardware(), toUser);
            
            if (toUser != null) {
                forceReload(toUser);
            } else {
                logger.warn("Invalid user = " + toUser);
            }
        }
    }

    // Action = free 2 from cage1
    private void freeInputFromUser(List<String> params) {
        String fromUser = null;
        String rowToDelete;
        if (params.size() < 3) {
            logger.warn("Invalid command arguments");
        } else {
            rowToDelete = params.get(0);
            fromUser = params.get(2);
            logger.debug("free row " + rowToDelete + " from " + fromUser);
            dbAccess.freeRow(rowToDelete, getHardware(), fromUser);
            dbAccess.deleteAssignmentIfEmpty(fromUser, getHardware());

            try {
                Assignment userAssignment = dbAccess.getAssignment(getHardware(), fromUser);
                String usercols = userAssignment.getCols();
                if (!Util.isBlank(usercols)) {
                    onSetAttenuration(CommandBuilder.buildSetCommand(rowToDelete, usercols));
                }
            } catch (SQLException e) {
                logger.warn(e);
            }
        }

        if (fromUser != null) {
            forceReload(fromUser);
        } else {
            logger.warn("Invalid user = " + fromUser);
        }
    }

    // Action = reassign 3 from chamber2 to chamber3
    private void reassignInputToUser(List<String> params) {

        String rowToReassign;
        String fromUser;
        String toUser;
        if (params.size() < 3) {
            logger.warn("Invalid command arguments");
        } else {
            rowToReassign = params.get(0);
            fromUser = params.get(2);
            toUser = params.get(4);
            if (logger.isDebugEnabled()) {
                logger.debug("Reassign column " + rowToReassign + " from " + fromUser + " to user "
                        + toUser);
            }
            dbAccess.reAssignRow(rowToReassign, getHardware(), fromUser, toUser);
            dbAccess.deleteAssignmentIfEmpty(fromUser, getHardware());

            try {
                Assignment userAssignment = dbAccess.getAssignment(getHardware(), fromUser);
                String usercols = userAssignment.getCols();
                if (!Util.isBlank(usercols)) {
                    onSetAttenuration(CommandBuilder.buildSetCommand(rowToReassign, usercols));
                }
            } catch (SQLException e) {
                logger.warn(e);
            }

            if (fromUser != null) {
                forceReload(fromUser);
            } else {
                logger.warn("Invalid user = " + fromUser);
            }

            if (toUser != null) {
                forceReload(toUser);
            } else {
                logger.warn("Invalid user = " + toUser);
            }
        }
    }

    // Action = assign 9 to chamber1,chamber2,chamber3
    private void assignInputToUsers(List<String> params) {
        String[] users = params.get(2).split(CONST_COMMA);
        String input = params.get(0);
        for (String toUser : users) {
            boolean isUpdate = false;
            assignment = new Assignment(toUser, input, null, true, false);
            try {
                Assignment currentAssignment = dbAccess.getAssignment(getHardware(), toUser);
                if ((currentAssignment != null) && (currentAssignment.getRows() != null)
                        && (currentAssignment.getRows().trim().length() > 1)) {
                    isUpdate = true;
                }
            } catch (SQLException e1) {
                logger.warn(e1);
            }

            try {
                if (logger.isDebugEnabled()) {
                    logger.debug("Assign ports, " + assignment + " " + getHardware() + " " + toUser);
                }
                dbAccess.updateAssignment(getHardware(), toUser, assignment);
            } catch (SQLException e) {
                setErrorMessage("Failed to assign selected rows and cols to user "
                        + getAssigntouser());
                logger.error(e);
            }
            if (isUpdate) {
                forceReload(toUser);
            }
        }
    }

    private void forceReload(String user) {
        mgmt.setReload(user, true);
        mgmt.setUpdate(user, true);
    }

    public List<String> getHardwarelist() {
        return bConf.getNonSwitchHardwareList();
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

    private boolean hasInputAssigned(String hardwarename) {
        try {
            return dbAccess.hasAssignedInputs(hardwarename);
        } catch (SQLException e1) {
            logger.error(e1);
            setWarningMessage("Failed to connect to database, Contact administrator for assistant!");
        }
        return false;
    }

    private void onSetAttenuration(String command) {
        String[] cmdOut = { command };
        try {
            ObjectName mBeanName = new ObjectName(Constants.OBJECTMAME_PREFIX + hardware
                    + Constants.OBJECTNAME_SUFFIX);
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