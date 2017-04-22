package com.rfview;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import com.rfview.conf.Assignment;
import com.rfview.conf.BroadcastConf;
import com.rfview.conf.MatrixConfig;
import com.rfview.maze.Datagrid;
import com.rfview.maze.Entry;
import com.rfview.utils.DbAccess;

public class MatrixMonitorAction extends BaseActionSupport {

    private static final long serialVersionUID = -6798445163576134855L;
    BroadcastConf bConf = BroadcastConf.getInstance();
    MatrixConfig mConfg = MatrixConfig.getInstance();
    
    private String action;
    private final DbAccess dbAccess = DbAccess.getInstance();
    private String hardware;
    private List<String> params;
    private int numRows;
    private int numCols;
    private Cell[][] matrix;
    private Assignment assignment;
    private List<THeader> tableHeader;
    
    public MatrixMonitorAction() {
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
    
    public List<THeader> getTableHeader() {
        return tableHeader;
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
        }
       
        if ((hardware==null) || (hardware.isEmpty())) {
            setWarningMessage("Please choose hardware to show overall assignment view!");
            return SUCCESS;
        }      
        
        logger.info("Action = " + action);
        if ((action!=null) && !action.trim().isEmpty()) {
            String tokens[] = action.split("\\s+");
            if (tokens.length > 1) {
                params = new ArrayList<String>();
                for (int i = 1; i < tokens.length; i++) {
                    params.add(tokens[i].trim());
                }
            }
        }

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
                                
            logger.debug("Get labels for hardware " + getHardware());
            tableHeader = new ArrayList<THeader>();
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
                    tableHeader.add(new THeader(assignment.getUserByReservedCol(k), "[" + k + "] "+ l.getLabel(), l.getDescription()));
                    k++;
                }
            }
            
            Entry[] offset = Datagrid.getInstance().getOffset(hardware);
            if ((offset == null) || (offset.length < numCols)) {
                // build default
                offset = new Entry[numCols];
                for (int i = 0; i < numCols; i++) {
                    offset[i] = new Entry(i, i, "", 0, false);
                }
            } else {
                Arrays.sort(offset, new Comparator<Entry>() {
    
                    @Override
                    public int compare(Entry o1, Entry o2) {
                        if (o1.getRow() == o2.getRow()) {
                            return 0;
                        } else if (o1.getRow() > o2.getRow()) {
                            return 1;
                        } else {
                            return -1;
                        }
                    }
                });
            }
            
            matrix = new Cell[numRows][numCols];
            for (int i = 0; i < numRows; i++) {
                for (int j = 0; j < numCols; j++) {
                    int rDisplyNumber = i+1;
                    int cDisplyNumber = j+1;
                    String name = "-";
                    String cUser = assignment.getUserByReservedCol(cDisplyNumber);
                    Set<String> rUser = assignment.getUserByReservedRow(rDisplyNumber);
                    if ((rUser!=null) && (cUser!=null) && rUser.contains(cUser)) {
                        name = cUser;
                    }

                    Cell cell = new Cell(name);
                    if (j==0) {
                        if (useDefault) {
                            cell.setLabel("[" + (i+1) + "] Input" + (i+1));
                            cell.setDescription("Input" + (i+1));
                        } else {
                            cell.setLabel("[" + (i+1) + "] "+inputLabels.get(i).getLabel());
                            cell.setDescription(inputLabels.get(i).getDescription());
                        }
                        cell.setOffset(Integer.toString(offset[i].getValue())+"dBm");
                    }
                    matrix[i][j] = cell;
                }
            }
        } catch (FileNotFoundException e) {
            setErrorMessage("File " +hardware + ".cfg not found!");
        } catch (IOException e) {
            setErrorMessage("Failed to read configure file.");
        }
        return "success";
    }

    public List<String> getHardwarelist() {
        return bConf.getHardwareList();
    }
}
