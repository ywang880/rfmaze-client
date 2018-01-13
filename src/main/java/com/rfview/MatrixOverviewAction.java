package com.rfview;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import com.rfview.comm.HeartBeatTask;
import com.rfview.comm.MazeServer;
import com.rfview.comm.ProcessInfo;
import com.rfview.comm.RFmazeInfo;
import com.rfview.comm.RfMazeServerConnectionInfo;
import com.rfview.conf.Assignment;
import com.rfview.conf.MatrixConfig;
import com.rfview.exceptions.InvalidConfigurationException;
import com.rfview.maze.Datagrid;
import com.rfview.maze.Entry;
import com.rfview.maze.RFMazeServerAgent;
import com.rfview.utils.ColorMapping;
import com.rfview.utils.CompositeKey;
import com.rfview.utils.Constants;

public class MatrixOverviewAction extends BaseActionSupport {

    private static final String USER_ADMIN = "admin";
	private static final String USER_SWADMIN = "swadmin";
	private static final String ERROR_MESSAGE = "There are no rfmaze processes started with this configuration data. Please start process first!";
    private static final long serialVersionUID = -6798445163576134855L;
    private static final long FIVE_SECONDS = 5000L;
    private static final String SELECT_HARDWARE="select_hardware";

    private Cell[][] matrix;
    private List<String> hardwares;
    private List<THeader> tableHeader;
    private String hardware;

    private Datagrid cache = Datagrid.getInstance();
    private String action;
    private List<MatrixLabel> inputLabels;
    private List<MatrixLabel> outputLabels;
    private int numRows;
    private int numCols;

    private List<Cell> outputAttenuation;

    private String showDialog="no";
    private String input;
    private String output;
    private String value;
    private String status;
    private List<String> assignedUsers;
    private boolean isQRB = true;

    private final RfMazeServerConnectionInfo serverConntionInfo = RfMazeServerConnectionInfo.getInstance();

    public String getShowDialog() {
        return showDialog;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setShowDialog(String showDialog) {
        this.showDialog = showDialog;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public List<Cell> getOutputAttenuation() {
        return outputAttenuation;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int numberOfmatrix() {
        return numRows;
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

    public List<String> getAssignedUsers() {
        return assignedUsers;
    }

    public String getServer() {
        try {
            MazeServer theServer = serverConntionInfo.getServer(hardware);
            sessionMap.put(Constants.KEY_SERVER, theServer.getIp() +":" + theServer.getPort());
            return theServer.getIp() +":" + theServer.getPort();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return "127.0.0.1:29020";
    }

    public String execute() {
        if (sessionMap!=null) {
            String uid = (String)sessionMap.get(Constants.KEY_LOGIN_ID);
            if (uid == null) {
                return "login";
            }
            this.username = uid;
        }

        if (!USER_ADMIN.equals(username) && !USER_SWADMIN.equals(username) ) {
            setErrorMessage("User " + username + " does not have privilege to access this page!");
            return shouldFullview(hardware)? SUCCESS1f : SUCCESS1;
        }

        logger.debug("Action = [" + action + "], hardware="+hardware);
        List<ProcessInfo> pinfo = mgmt.getProcesses();
        hardwares = new ArrayList<String>();
        for (ProcessInfo info : pinfo) {
            if (info.getStatus().contains("running")) {
            	if (USER_SWADMIN.equals(username)) {
            		String hwName = info.getConfigFile();
            		try {
						if (MatrixConfig.getInstance().getServerInfo(hwName).isSwitchingType()) {
							hardwares.add(info.getConfigFile());
						}
					} catch (InvalidConfigurationException e) {
					}
            	} else {
            		hardwares.add(info.getConfigFile());
            	}
            }
        }
        if (hardwares.isEmpty()) {
            setWarningMessage("There are apparently no running rfmaze processes. Please start process first!");
        }

        if ((hardware == null) || (!"list_hardware".equals(action))) {
            return SELECT_HARDWARE;
        }

        logger.debug("Action = [" + action + "], hardware="+hardware);
        String returnCode;
        try {
            isQRB = MatrixConfig.getInstance().getServerInfo(hardware).isQRB();
            if (MatrixConfig.getInstance().getServerInfo(hardware).isQRB()) {
                returnCode = shouldFullview(hardware)? SUCCESS1f : SUCCESS1;
            } else if (MatrixConfig.getInstance().getServerInfo(hardware).isLTE()) {
                returnCode = shouldFullview(hardware)? SUCCESS2f : SUCCESS2;
            } else {
                returnCode = shouldFullview(hardware)? SUCCESS3f : SUCCESS3;
            }
            logger.info("server type = " + MatrixConfig.getInstance().getServerInfo(hardware).getType()
                    + ", return core " + returnCode);
        } catch (InvalidConfigurationException e2) {
            returnCode = shouldFullview(hardware)? SUCCESS1f : SUCCESS1;
            logger.error(e2.getMessage());
        }

        if (!hasCachedData(hardware)) {
            logger.info("no cached data, fetch the data from maze server");
            String args[] = new String[2];
            try {
                RFmazeInfo rfmazeInfo = new RFmazeInfo();
                MazeServer theServer = serverConntionInfo.getServer(hardware);
                logger.info("Connect to server " +theServer.getIp() + ":" + theServer.getPort());
                rfmazeInfo.initQuery(hardware, username, theServer.getIp(), theServer.getPort());

                Timer timer = new Timer();
                sessionMap.put("heartbeat_timer", timer);
                HeartBeatTask heartbeatTask = new HeartBeatTask(hardware, theServer.getIp(), theServer.getPort());
                timer.scheduleAtFixedRate(heartbeatTask, FIVE_SECONDS, FIVE_SECONDS);
                sessionMap.put(Constants.KEY_SERVER, theServer.getIp());
                sessionMap.put(Constants.KEY_PORT, theServer.getPort());
                args[0] = theServer.getIp();
                args[1] = Integer.toString(theServer.getPort());

            } catch (IOException e1) {
                logger.error(e1);
                setWarningMessage("WARN: Connection cannot be established. Please go to matrix control page to check server status!");
                return returnCode;
            }

            RFMazeServerAgent agent = Datagrid.getInstance().getAgent(hardware);
            if (agent == null) {
            	logger.info("Create agent for " + hardware);
            	agent = new RFMazeServerAgent(args, hardware);
            	Datagrid.getInstance().addAgent(hardware, agent);
            	agent.start();
            }
        }

        inputLabels = dbAccess.queryInputLabels(getHardware());
        outputLabels = dbAccess.queryOutputLabels(getHardware());

        numRows = inputLabels.size();
        numCols = outputLabels.size();

        cache.setInputLabels(hardware, inputLabels);
        cache.setOutputLabels(hardware, outputLabels);
        cache.setLabelsChanged(hardware, false);

        outputAttenuation = new ArrayList<Cell>();
        if ( isLTE() ) {
	        String[] atten = cache.getAttenuation(hardware);
	        if (atten!=null) {
	            for (int i = 0; i < numCols; i++) {
	                try {
	                Cell tCell = new Cell(atten[i]);
	                tCell.setBgcolor(ColorMapping.mapping(username, Integer.parseInt(atten[i])));
	                outputAttenuation.add(tCell);
	                } catch (Exception e) {}
	            }
	        }
        }

        if (isQRB) {
            matrix = new Cell[numRows][numCols+1];
            if (!getQRBData(numRows, numCols)) {
                setWarningMessage(ERROR_MESSAGE);
                return returnCode;
            }
        } else {
            matrix = new Cell[numRows][numCols];
            if (!getRBMData(numRows, numCols)) {
                setWarningMessage(ERROR_MESSAGE);
                return returnCode;
            }
        }

        buildAssignmentTable(numCols);

        tableHeader = new ArrayList<THeader>();
        if (isQRB) {
            tableHeader.add(new THeader("", "Power", "Power"));
        }
        inputLabels = cache.getInputLabels(getHardware());
        outputLabels = cache.getOutputLabels(getHardware());

        for (int k = 0; k < numCols; k++) {
            MatrixLabel ml = outputLabels.get(k);
            THeader headDesc;
            if (ml!=null) {
                headDesc = new THeader(assignedUsers.get(k), "["+(k+1) +"]" + ml.getLabel(), "assigned to " + ml.getDescription());
            } else {
                headDesc = new THeader("", "["+(k+1) +"] Output" + (k+1), "Output" + (k+1));
            }
            tableHeader.add(headDesc);
        }

        this.input = cache.getInput();
        this.output = cache.getOutput();
        this.value = cache.getValue();

        return returnCode;
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

    private boolean getRBMData(int numrows, int numcols) {
        final Entry[][] matrix_data = cache.getMatrix(getHardware());
        if (matrix_data==null) {
            return false;
        }
        for (int i = 0; i < numrows; i++) {
            for (int j = 1; j <= numcols; j++) {
                try {
                int value = matrix_data[i][j-1].getValue();
                Cell tCell = new Cell(Integer.toString(value));
                if (j == 1) {
                    tCell.setLabel("[" + (i+1) + "] " +  inputLabels.get(i).getLabel());
                    tCell.setDescription(inputLabels.get(i).getDescription());
                }
                matrix[i][j-1] = tCell;
                } catch (Exception e) {}
            }
        }
        if (username==null) {
            username=USER_ADMIN;
        }
        CompositeKey mKey = CompositeKey.key(username, hardware);
        cache.putMatrixCache(mKey, matrix);
        return true;
    }

    private boolean getQRBData(int numrows, int numcols) {

        Entry[][] matrix_data = cache.getMatrix(getHardware());
        Entry[] offset_data = cache.getOffsetData(getHardware());

        if ((matrix_data==null) || (offset_data == null)) {
            return false;
        }
        for (int i = 0; i < numrows; i++) {
            Cell c = ((offset_data == null) || (offset_data.length < numrows-1))? new Cell("-99dBm") : new Cell(offset_data[i].getValue()+"dBm");
            c.setLabel("[" + (i+1) + "] " +  inputLabels.get(i).getLabel());
            c.setDescription(inputLabels.get(i).getDescription());
            matrix[i][0] = c;
            for (int j = 0; j < numcols; j++) {
                int value = matrix_data[i][j].getValue();
                Cell tCell = new Cell(Integer.toString(matrix_data[i][j].getValue()));
                tCell.setBgcolor(ColorMapping.mapping(username, value));
                matrix[i][j+1] = tCell;
            }
        }
        if (username==null) {
            username=USER_ADMIN;
        }
        CompositeKey mKey = CompositeKey.key(username, hardware);
        cache.putMatrixCache(mKey, matrix);

        return true;
    }


    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    private boolean hasCachedData(String hardware) {
        Entry[][] matrix_data = cache.getMatrix(getHardware());
        Entry[] offset_data = cache.getOffsetData(getHardware());
        return ((matrix_data!=null) && (offset_data!=null));
    }

    private void buildAssignmentTable(int numcols) {
        assignedUsers = new ArrayList<String>();
        try {
            Assignment assignment = dbAccess.getAssignment(getHardware(), null);
            for (int i = 0; i < numcols; i++) {
                String u = assignment.getUserByReservedCol(i+1);
                if (u == null) {
                    assignedUsers.add("-");
                } else {
                    assignedUsers.add(u);
                }
            }
        } catch (SQLException e) {
            for (int i = 0; i < numcols; i++) {
                assignedUsers.add("-");
            }
        }
    }

    private boolean isLTE() {
    	try {
			return MatrixConfig.getInstance().getServerInfo(hardware).isLTE();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
    	return false;
    }

    private boolean shouldFullview(String hardware) {
        return MatrixConfig.getInstance().fullView(hardware);
    }
}
