package com.rfview;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.rfview.conf.Assignment;
import com.rfview.conf.BroadcastConf;
import com.rfview.conf.MatrixConfig;
import com.rfview.maze.User;
import com.rfview.utils.db.DbAccess;

public class CellAssignmentAction extends BaseActionSupport {

    private static final long serialVersionUID = -6798445163576134855L;
    BroadcastConf bConf = BroadcastConf.getInstance();
    MatrixConfig mConfg = MatrixConfig.getInstance();
    
    private String action;
    private List<String> assignedusers;
    private String assigntouser;
    private final DbAccess dbAccess = DbAccess.getInstance();
    private String selectedrows;
    private String selectedcols;
    private String hardware;
    private String actionCmd="";
    private String params="";
    private int numRows;
    private int numCols;
    private Cell[][] matrix;

    public CellAssignmentAction() {
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
        sessionMap.put("hardware", hardware);
    }
    
    public List<String> getTableHeader() {
        List<String> result = new ArrayList<String>();
        for (int i = 0; i < numCols; i++) {
            result.add("Output_" + (i+1));
        }
        return result;
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

    public String execute() {
        if (sessionMap!=null) {
            String uid = (String)sessionMap.get("loginId");
            if (uid == null) {
                return "login";
            }
            this.username = uid;
            
            hardware = (String)sessionMap.get("hardware");
            assigntouser = (String)sessionMap.get("assigntouser");
        }
        logger.info("Action = " + action);
        if ((action!=null) && !action.trim().isEmpty()) {
            String tokens[] = action.split("\\s+");
            actionCmd = tokens[0];
            if (tokens.length > 1) {
                for (int i = 1; i < tokens.length; i++) {
                    if (params.isEmpty()) {
                        params=tokens[i];
                    } else {
                        params=params + " " + tokens[i];
                    }
                }
            }
        }

        // process browser action
        if (actionCmd.equals("assignment")) {
            logger.info("Params = [" + params + "]");
            String[] rowsAndcols = params.trim().split(" ");
            logger.info("Parameter tokens " + rowsAndcols.length);
            try {
                dbAccess.updateAssignment(getHardware(), getAssigntouser(), new Assignment(getAssigntouser(), rowsAndcols[0],  rowsAndcols[1], true, true));
            } catch (SQLException e) {
                setErrorMessage("Failed to assign selected rows and cols to user " + getAssigntouser());
                logger.error(e);
            }
        }
        assignedusers = new ArrayList<String>();        
        try {
            for (User u : dbAccess.queryUsers()) {
                assignedusers.add(u.getId());
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        
        if ((hardware == null) || hardware.trim().isEmpty()) {
            setWarningMessage("NOTE: Select hardware, user and input rows and columns.");
        } else {
            Properties props;
            try {
                props = mConfg.loadConfigureFile(hardware);
                numRows = Integer.parseInt(props.getProperty("matrix_inputs"));
                numCols = Integer.parseInt(props.getProperty("matrix_outputs"));

                matrix = new Cell[numRows][numCols];
                for (int i =0 ; i < numRows; i++) {
                    for (int j =0 ; j < numCols; j++) {
                        matrix[i][j] = new Cell("-");
                    }
                }
            } catch (FileNotFoundException e) {
                setErrorMessage("File " +hardware + ".cfg not found!");
            } catch (IOException e) {
                setErrorMessage("Failed to read configure file.");
            } 
        }
        
        return "success";
    }

    public List<String> getHardwarelist() {
        return bConf.getHardwareList();
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
}
