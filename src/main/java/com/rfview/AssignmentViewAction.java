package com.rfview;

import java.util.ArrayList;
import java.util.List;

import com.rfview.maze.Entry;

public class AssignmentViewAction extends BaseActionSupport {
  private static final long serialVersionUID = -6798445163576134855L;
    
    private int nummatrix = 6;
    private int numCols = 6+1;
    private Cell[][] matrix = new Cell[nummatrix][numCols];
    private List<String> hardwares;
    private String hardware;
    private String action;
    
    List<MatrixLabel> inputLabels;
    List<MatrixLabel> outputLabels;
    
    public AssignmentViewAction() {
    }

    public int numberOfmatrix() {
        return nummatrix;
    }

    public List<THeader> getTableHeader() {        
        List<THeader> result = new ArrayList<THeader>();
        result.add(new THeader(null, "Offset", ""));
        
        for (int i = 1; i < numCols; i++) {
            result.add(new THeader(null, "Output_" + i, "H"+i));
        }
        return result;
    }

    public String[] getFirstColumn() {
        String[] result = new String[nummatrix];
        for (int i = 1; i < nummatrix; i++) {
            result[i] = "Input_" + i;
        }
        return result;
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
        }
        logger.info("Action = " + action + "hardware="+getHardware());
        
        inputLabels = dbAccess.queryInputLabels(getHardware());
        outputLabels = dbAccess.queryOutputLabels(getHardware());
        
        getData();
        return SUCCESS;
    }

    public List<String> getHardwares() {
        return hardwares;
    }

    public void setHardwares(List<String> hardwares) {
        this.hardwares = hardwares;
    }

    public String getHardware() {
        return hardware;
    }

    public void setHardware(String hardware) {        
        this.hardware = hardware;
    }
    
    private void getData() {
        
        Entry[][] matrix_data = buildMatrix(getHardware(), getUsername());
        Entry[] offset_data = buildOffsetData(getHardware(), getUsername());
      
        for (int i = 0; i < nummatrix; i++) {
            for (int j = 0; j < numCols; j++) {
                if (j == 0) {
                    matrix[i][j] = new Cell(offset_data[i].getValue()+"dBm");
                } else {
                    matrix[i][j] = new Cell(matrix_data[i][j].getValue()+"availabe");
                }
            }
        }       
    }
    
    public Entry[][] buildMatrix(String hardware, String user) {
        
        Entry[][] data = new Entry[nummatrix][numCols];
        for (int i = 0; i < nummatrix; i++) {
            for (int j = 0; j < numCols; j++) {
                data[i][j] = new Entry(i, j, "chamber1", 20, true);
            }
        }
        return data;
    }
    
    public Entry[] buildOffsetData(String hardware, String user) {
        Entry [] e = new Entry[nummatrix];
        e[0] = new Entry(0, 0, "ch", 10, true);
        e[1] = new Entry(1, 0, "ch", 10, true);
        e[2] = new Entry(2, 0, "ch", 10, true);
        e[3] = new Entry(3, 0, "ch", 10, true);
        e[4] = new Entry(4, 0, "ch", 10, true);
        e[5] = new Entry(5, 0, "ch", 10, true);
        return e;        
    }
    
    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
     
}
