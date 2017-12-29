package com.rfview.conf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import java.util.Set;

public class Assignment {

    private String user;
    private String hardware;
    private List<String> hardwares = new ArrayList<String>();
    private String rows;
    private String cols;
    private boolean rowupdate;
    private boolean colupdate;
    private boolean fullAssignment = false;

    private final Map<Integer, Set<String>> rowsForUser = new HashMap<Integer, Set<String>>();
    private final Map<Integer, String> colsForUser = new HashMap<Integer, String>();

    private final Map<String, String> assingedRows = new HashMap<String, String>();
    private final Map<String, String> assingedColumns = new HashMap<String, String>();

    public Assignment() {
    }

    public Assignment(String user, String rows,  String cols, boolean rowupdate, boolean colupdate) {
        super();
        this.rows = (rows==null)? "" : rows.trim();
        this.cols = (cols==null)? "" : cols.trim();
        this.rowupdate = rowupdate;
        this.colupdate = colupdate;

        Set<String> users = new HashSet<String>();
        users.add(user);
        if ((rows!=null) && !rows.isEmpty() && !rows.startsWith("null"))  {
            String[] tokens = rows.split(",");
            for (String t : tokens) {
                rowsForUser.put(Integer.valueOf(t), users);
            }
        }

        if ((cols!=null) && !cols.isEmpty() && !cols.startsWith("null")) {
            String[] tokens = cols.split(",");
            for (String t : tokens) {
                colsForUser.put(Integer.valueOf(t), user);
            }
        }
    }

    public void addReserved(String user, String rows, String cols) {
        this.rows = rows;
        this.cols = cols;
        if ((rows!=null) && !rows.isEmpty() && !rows.startsWith("null"))  {
            String[] tokens = rows.split(",");
            for (String t : tokens) {
                Integer key = Integer.valueOf(t);
                Set<String> users = rowsForUser.get(key);
                if (users == null) {
                    users = new HashSet<String>();
                    users.add(user);
                    rowsForUser.put(key, users);
                } else if (!users.contains(user)) {
                    users.add(user);
                    rowsForUser.put(key, users);
                }
            }
        }

        if ((cols!=null) && !cols.isEmpty() && !cols.startsWith("null")) {
            String[] tokens = cols.split(",");
            for (String t : tokens) {
                colsForUser.put(Integer.valueOf(t), user);
            }
        }

        assingedRows.put(user, rows);
        assingedColumns.put(user, cols);
    }

    public boolean isFullAssignment() {
        return fullAssignment;
    }

    public void setFullAssignment(boolean fullAssignment) {
        this.fullAssignment = fullAssignment;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getHardware() {
        return hardware;
    }

    public void setHardware(String hardware) {
        this.hardware = hardware;
    }

    public Map<String, String> getAssingedRows() {
        return assingedRows;
    }

    public Map<String, String> getAssingedColumns() {
        return assingedColumns;
    }

    public void setRows(String rows) {
        this.rows = rows;
    }

    public void setCols(String cols) {
        this.cols = cols;
    }

    public String getRows() {
        return rows;
    }

    public String getCols() {
        return cols;
    }

    public boolean isRowupdate() {
        return rowupdate;
    }

    public boolean isColupdate() {
        return colupdate;
    }

    public Set<Integer> getAllAssignedColumns() {
        return colsForUser.keySet();
    }

    public Map<Integer, String> getColsForUser() {
        return colsForUser;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Row Assignment: ");
        for (Entry<Integer,Set<String>> e : rowsForUser.entrySet()) {
            sb.append(e.getKey()+"=" + e.getValue()).append(",");
        }

        sb.append("\nColumn Assignment: ");
        for (Entry<Integer,String> e : colsForUser.entrySet()) {
            sb.append(e.getKey()+"=" + e.getValue()).append(",");
        }
        return sb.toString();
    }

    public boolean containsRow(int row) {
        return rowsForUser.containsKey(Integer.valueOf(row));
    }

    public boolean containsColumn(int col) {
        return colsForUser.containsKey(Integer.valueOf(col));
    }

    public Set<String> getUserByReservedRow(int row) {
        return rowsForUser.get(Integer.valueOf(row));
    }

    public String getUserByReservedCol(int col) {
        return colsForUser.get(Integer.valueOf(col));
    }

    public boolean isRowsEmpty() {
        return rowsForUser.isEmpty();
    }

    public boolean isColsEmpty() {
        return colsForUser.isEmpty();
    }

    public List<String> getHardwares() {
        return hardwares;
    }

    public List<String> getFullAssignmentHardwares(String theuser) {
        List<String> result = new LinkedList<>();
        for (String h : hardwares) {
            Properties props;
            try {
                props = MatrixConfig.getInstance().getConfiguration(h);
                int inputs = Integer.parseInt(props.getProperty("matrix_inputs", "-1"));
                int outputs = Integer.parseInt(props.getProperty("matrix_outputs", "-1"));
                if (inputs == -1 || outputs == -1) {
                    continue;
                }

                String r = assingedRows.get(theuser);
                String c = assingedColumns.get(theuser);
                if (r.split(",").length == inputs && c.split(",").length == outputs) {
                    result.add(h);
                }
            } catch (Exception e) {
                continue;
            }
        }
        return result;
    }

    public void addHardware(String hardware) {
        this.hardwares.add(hardware);
    }
}
