package com.rfview;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import org.apache.log4j.Logger;

import com.rfview.comm.HeartBeatTask;
import com.rfview.comm.MazeServer;
import com.rfview.comm.ProcessInfo;
import com.rfview.comm.RFmazeInfo;
import com.rfview.comm.RfMazeServerConnectionInfo;
import com.rfview.conf.Assignment;
import com.rfview.conf.BroadcastConf;
import com.rfview.conf.MatrixConfig;
import com.rfview.exceptions.InvalidConfigurationException;
import com.rfview.maze.Datagrid;
import com.rfview.maze.Entry;
import com.rfview.utils.ColorMapping;
import com.rfview.utils.CompositeKey;
import com.rfview.utils.Constants;

public class MatrixViewAction extends BaseActionSupport {

    private static final long serialVersionUID = -6798445163576134855L;

    private static final long FIVE_SECONDS = 5000L;
    private static final String SUCCESS1 = SUCCESS + "1";
    private static final String SUCCESS2 = SUCCESS + "2";
    private static final String SUCCESS3 = SUCCESS + "3";

    private Cell[][] matrix;
    private List<THeader> tableHeader;
    private String hardware;
    private Datagrid cache;
    private String action;
    private List<MatrixLabel> inputLabels;
    private List<MatrixLabel> outputLabels;
    private int nummatrix;
    private int numCols;
    private String showDialog = "no";
    private String input;
    private String output;
    private String value;
    private String status;
    private String range1 = "0-10";
    private String range2 = "11-62";
    private String range3 = "63";
    private String color1 = "008800";
    private String color2 = "EE9933";
    private String color3 = "DD0000";

    private List<String> outputAttenuation;
    private final List<String> hardwares = new ArrayList<String>();
    private final RfMazeServerConnectionInfo serverConntionInfo = RfMazeServerConnectionInfo.getInstance();
    private final Logger logger = Logger.getLogger(MatrixViewAction.class.getName());

    public List<MatrixLabel> getInputLabels() {
		return inputLabels;
	}

	public void setInputLabels(List<MatrixLabel> inputLabels) {
		this.inputLabels = inputLabels;
	}

	public int getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(int defaultValue) {
		this.defaultValue = defaultValue;
	}

	public Integer getHandoffOut1() {
		return handoffOut1;
	}

	public void setHandoffOut1(Integer handoffOut1) {
		this.handoffOut1 = handoffOut1;
	}

	public Integer getHandoffIn1() {
		return handoffIn1;
	}

	public void setHandoffIn1(Integer handoffIn1) {
		this.handoffIn1 = handoffIn1;
	}

	public Integer getHandoffOut2() {
		return handoffOut2;
	}

	public void setHandoffOut2(Integer handoffOut2) {
		this.handoffOut2 = handoffOut2;
	}

	public Integer getHandoffIn2() {
		return handoffIn2;
	}

	public void setHandoffIn2(Integer handoffIn2) {
		this.handoffIn2 = handoffIn2;
	}

	public Integer getHandoffOut3() {
		return handoffOut3;
	}

	public void setHandoffOut3(Integer handoffOut3) {
		this.handoffOut3 = handoffOut3;
	}

	public Integer getHandoffIn3() {
		return handoffIn3;
	}

	public void setHandoffIn3(Integer handoffIn3) {
		this.handoffIn3 = handoffIn3;
	}

	public Integer getHandoffOut4() {
		return handoffOut4;
	}

	public void setHandoffOut4(Integer handoffOut4) {
		this.handoffOut4 = handoffOut4;
	}

	public Integer getHandoffIn4() {
		return handoffIn4;
	}

	public void setHandoffIn4(Integer handoffIn4) {
		this.handoffIn4 = handoffIn4;
	}

	public Map<Integer, Integer> getListHandoffOut1() {
		return listHandoffOut1;
	}

	public void setListHandoffOut1(Map<Integer, Integer> listHandoffOut1) {
		this.listHandoffOut1 = listHandoffOut1;
	}

	public Map<Integer, Integer> getListHandoffIn1() {
		return listHandoffIn1;
	}

	public void setListHandoffIn1(Map<Integer, Integer> listHandoffIn1) {
		this.listHandoffIn1 = listHandoffIn1;
	}

	public Map<Integer, Integer> getListHandoffOut2() {
		return listHandoffOut2;
	}

	public void setListHandoffOut2(Map<Integer, Integer> listHandoffOut2) {
		this.listHandoffOut2 = listHandoffOut2;
	}

	public Map<Integer, Integer> getListHandoffIn2() {
		return listHandoffIn2;
	}

	public void setListHandoffIn2(Map<Integer, Integer> listHandoffIn2) {
		this.listHandoffIn2 = listHandoffIn2;
	}

	public Map<Integer, Integer> getListHandoffOut3() {
		return listHandoffOut3;
	}

	public void setListHandoffOut3(Map<Integer, Integer> listHandoffOut3) {
		this.listHandoffOut3 = listHandoffOut3;
	}

	public Map<Integer, Integer> getListHandoffIn3() {
		return listHandoffIn3;
	}

	public void setListHandoffIn3(Map<Integer, Integer> listHandoffIn3) {
		this.listHandoffIn3 = listHandoffIn3;
	}

	public Map<Integer, Integer> getListHandoffOut4() {
		return listHandoffOut4;
	}

	public void setListHandoffOut4(Map<Integer, Integer> listHandoffOut4) {
		this.listHandoffOut4 = listHandoffOut4;
	}

	public Map<Integer, Integer> getListHandoffIn4() {
		return listHandoffIn4;
	}

	public void setListHandoffIn4(Map<Integer, Integer> listHandoffIn4) {
		this.listHandoffIn4 = listHandoffIn4;
	}

	private int defaultValue = 1;
    
    private Integer handoffOut1;
    private Integer handoffIn1;

    private Integer handoffOut2;
    private Integer handoffIn2;

    private Integer handoffOut3;
    private Integer handoffIn3;
    
    private Integer handoffOut4;
    private Integer handoffIn4;
    
    private Map<Integer, Integer>listHandoffOut1 = new HashMap<>();
    private Map<Integer, Integer>listHandoffIn1 = new HashMap<>();

    private Map<Integer, Integer>listHandoffOut2 = new HashMap<>();
    private Map<Integer, Integer>listHandoffIn2 = new HashMap<>();

    private Map<Integer, Integer>listHandoffOut3 = new HashMap<>();
    private Map<Integer, Integer>listHandoffIn3 = new HashMap<>();
    
    private Map<Integer, Integer>listHandoffOut4 = new HashMap<>();
    private Map<Integer, Integer>listHandoffIn4 = new HashMap<>();
      
    public String getShowDialog() {
        return showDialog;
    }

    public MatrixViewAction() {
    	for ( int i = 0; i < 10; i++ ) {
    		listHandoffOut1.put(i, i);
    		listHandoffIn1.put(i, i);
    		
    		listHandoffOut2.put(i, i);
    		listHandoffIn2.put(i, i);

    		listHandoffOut3.put(i, i);
    		listHandoffIn3.put(i, i);
    		
    		listHandoffOut4.put(i, i);
    		listHandoffIn4.put(i, i);

    	}
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int numberOfmatrix() {
        return nummatrix;
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

    public String getRange1() {
        return range1;
    }

    public void setRange1(String range1) {
        this.range1 = range1;
    }

    public String getRange2() {
        return range2;
    }

    public void setRange2(String range2) {
        this.range2 = range2;
    }

    public String getRange3() {
        return range3;
    }

    public void setRange3(String range3) {
        this.range3 = range3;
    }

    public String getColor1() {
        return color1;
    }

    public void setColor1(String color1) {
        this.color1 = color1;
    }

    public String getColor2() {
        return color2;
    }

    public void setColor2(String color2) {
        this.color2 = color2;
    }

    public String getColor3() {
        return color3;
    }

    public void setColor3(String color3) {
        this.color3 = color3;
    }

    public String getServer() {
        try {
            MazeServer theServer = serverConntionInfo.getServer(hardware);
            sessionMap.put(Constants.KEY_SERVER, theServer.getIp() + ":" + theServer.getPort());
            return theServer.getIp() + ":" + theServer.getPort();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return "127.0.0.1:29020";
    }

    public List<String> getOutputAttenuation() {
        return outputAttenuation;
    }
    
    public String execute() {
        logger.info("invoke matrix view action");
        if (sessionMap != null) {
            String uid = (String) sessionMap.get(Constants.KEY_LOGIN_ID);
            if (uid == null) {
                return "login";
            }
            this.username = uid;
            cache = (Datagrid) sessionMap.get(Constants.KEY_DATAGRID);
            if (cache == null) {
                cache = Datagrid.getInstance();
                sessionMap.put(Constants.KEY_DATAGRID, cache);
            }

            String cachedHardware = (String) sessionMap.get(Constants.KEY_HARDWARE);
            if ((cachedHardware != null) && !cachedHardware.isEmpty()) {
                hardware = cachedHardware;
            }
        }

        try {
            Assignment userAssignment = dbAccess.getAssignment(username);
            List<String> hwAssigned = userAssignment.getHardwares();
            List<String> availableServers = BroadcastConf.getInstance().getNonSwitchHardwareList();
            List<ProcessInfo> pinfo = mgmt.getProcesses();
            for (String ss : availableServers) {
                for (String sss : hwAssigned) {
                    if (sss.equals(ss) && isRunning(pinfo, sss)) {
                        hardwares.add(ss);
                    }
                }
            }
        } catch (SQLException e2) {
            logger.warn(e2.getMessage());
        }

        if (hardwares.isEmpty()) {
            setWarningMessage("There are apparently no running rfmaze processes. Please start process first!");
        }

        if (hardware == null || hardware.isEmpty()) {
            setWarningMessage("Hardware not selected. Please select hardware!");
            return SUCCESS1;
        }

        String returnCode;
        if ( isLTE()) {
            returnCode = SUCCESS2;
        } else if ( isRBM()) {
            returnCode = SUCCESS3;
        } else {
            returnCode = SUCCESS1;
        }
        logger.info("server type = " + getMetrixType()+ ", return core " + returnCode);

        if (username.equals("admin")) {
            setErrorMessage("invalid user name. please login as non-admin user!");
            return returnCode;
        }

        logger.info("Action = [" + action + "], hardware=" + hardware);
        RFmazeInfo rfmazeInfo = new RFmazeInfo();

        if ("color_scheme".equals(action)) {
            updateColorScheme();
        }

        String args[] = new String[2];
        if (!hasCachedData(hardware)) {
            logger.info("no cached data, fetch the data from maze server");
            try {
                MazeServer theServer = serverConntionInfo.getServer(hardware);
                rfmazeInfo.initQuery(hardware, username, theServer.getIp(), theServer.getPort());

                Timer timer = Datagrid.getTimer(hardware);
                sessionMap.put("heartbeat_timer", timer);
                HeartBeatTask heartbeatTask = new HeartBeatTask(hardware, theServer.getIp(),
                        theServer.getPort());
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

            Datagrid.getInstance().createAgent(args, hardware);
        }
        
        Assignment assignment = null;
        try {
            logger.info("Get user assignment information user = " + username + " hardware = "
                    + getHardware());
            assignment = dbAccess.getAssignment(getHardware(), username);
            logger.info("===== USER ASSIGNMENT ===== " + this.hashCode() + ",  " + assignment);
        } catch (SQLException e) {
            logger.error(e);
            setErrorMessage("There is no inputs and output assigned for user " + username + ".");
            return returnCode;
        }

        if (assignment == null) {
            logger.debug("There is no inputs and output assigned for user " + username + ".");
            setErrorMessage("There is no inputs and output assigned for user " + username + ".");
            return returnCode;
        }

        if (!validateAssignment(assignment.getRows())) {
            logger.debug("There is no inputs assigned for user " + username + ".");
            setErrorMessage("There is no inputs assigned for user " + username + ".");
            return returnCode;
        }

        if (!validateAssignment(assignment.getCols())) {
            logger.debug("There is no output assigned for user " + username + ".");
            setErrorMessage("There is no output assigned for user " + username + ".");
            return returnCode;
        }

        String rowTokens[] = assignment.getRows().split(",");
        String colTokens[] = assignment.getCols().split(",");

        int assignedRows[] = new int[rowTokens.length];
        int assignedCols[] = new int[colTokens.length];
        for (int i = 0; i < rowTokens.length; i++) {
            assignedRows[i] = Integer.parseInt(rowTokens[i]);
        }

        for (int i = 0; i < colTokens.length; i++) {
            assignedCols[i] = Integer.parseInt(colTokens[i]);
        }

        Arrays.sort(assignedRows);
        Arrays.sort(assignedCols);

        nummatrix = assignedRows.length;
        numCols = assignedCols.length;

        logger.info("Get cached matrix data hardware = " + getHardware() + " username = " + username);
        if (mgmt.isReload(username)) {
            matrix = null;
            cache.removeMatrixCache(CompositeKey.key(username, hardware));
        } else {
            cache.getMatrixCache(CompositeKey.key(username, hardware));
        }
        if ((matrix == null) || cache.isLabelsChanged(hardware)) {
            inputLabels = dbAccess.queryInputLabels(getHardware());
            outputLabels = dbAccess.queryOutputLabels(getHardware());

            cache.setInputLabels(hardware, inputLabels);
            cache.setOutputLabels(hardware, outputLabels);
            cache.setLabelsChanged(hardware, false);
            matrix = new Cell[assignedRows.length][assignedCols.length + 1];
            getData(assignedRows, assignedCols);
        }

        mgmt.setReload(username, false);
        tableHeader = new ArrayList<THeader>();
        if (!isRBM()) {
            tableHeader.add(new THeader(null, "Power", "Power"));
        }
        boolean useDefault = false;

        inputLabels = cache.getInputLabels(getHardware());
        outputLabels = cache.getOutputLabels(getHardware());
        if ((inputLabels.size() < nummatrix) || (outputLabels.size() < numCols)) {
            useDefault = true;
        }

        if (useDefault) {
            for (int k = 0; k < numCols; k++) {
                tableHeader.add(new THeader(null, "[" + (k + 1) + "] Output" + (k + 1), "Output"
                        + (k + 1)));
            }
        } else {
            int labelIndex = 1;
            for (int cIndex : assignedCols) {
                int cIdx = cIndex - 1;
                tableHeader.add(new THeader(assignment.getUserByReservedCol(cIdx), "["
                        + labelIndex++ + "] " + outputLabels.get(cIdx).getLabel(), outputLabels
                        .get(cIdx).getDescription()));
            }
        }

        this.input = cache.getInput();
        this.output = cache.getOutput();
        this.value = cache.getValue();

        outputAttenuation = new ArrayList<String>();
        String[] atten = cache.getAttenuation(hardware);
        if (atten!=null) {
            for (int i = 0; i < numCols; i++) {
                outputAttenuation.add(atten[i]);
            }
        } else {
            logger.warn("no outout attenuation data" );
        }        
        return returnCode;
    }

    private String getMetrixType() {
        try {
            return MatrixConfig.getInstance().getServerInfo(hardware).getType();
        } catch (InvalidConfigurationException e) {
            logger.error(e);
        }
        return "";
    }

    private boolean isLTE() {
        try {
            return MatrixConfig.getInstance().getServerInfo(hardware).isLTE();
        } catch (InvalidConfigurationException e) {
            logger.error(e);
        }
        return false;
    }

    private boolean isRBM() {
        try {
            return MatrixConfig.getInstance().getServerInfo(hardware).isRBM();
        } catch (InvalidConfigurationException e) {
            logger.error(e);
        }
        return false;
    }

    private boolean validateAssignment(String data) {
        return ((data != null) && !data.trim().isEmpty() && !data.equalsIgnoreCase("null"));
    }

    public List<String> getHardwares() {
        return hardwares;
    }

    public String getHardware() {
        return hardware;
    }

    public void setHardware(String hardware) {
        this.hardware = hardware;
        if ((hardware != null) && !hardware.isEmpty()) {
            sessionMap.put("hardware", hardware);
        }
    }

    private void getData(int[] assignedRows, int[] assignedCols) {
        Entry[][] matrix_data = cache.getMatrix(getHardware());
        Entry[] offset_data = cache.getOffsetData(getHardware());
        boolean validOffset = isValid(offset_data, assignedRows.length);
        for (int i = 0; i < assignedRows.length; i++) {
            Cell c = (validOffset) ? new Cell(offset_data[i].getValue() + "dBm") : new Cell("0dBm");
            int rIndex = assignedRows[i];
            c.setLabel("[" + (i + 1) + "] " + inputLabels.get(rIndex - 1).getLabel());
            c.setDescription(inputLabels.get(rIndex - 1).getDescription());
            matrix[i][0] = c;
            for (int j = 0; j < assignedCols.length; j++) {
                int cIndex = assignedCols[j];
                int value = matrix_data[rIndex - 1][cIndex - 1].getValue();
                Cell tCell = new Cell(Integer.toString(matrix_data[rIndex - 1][cIndex - 1]
                        .getValue()));
                tCell.setBgcolor(ColorMapping.mapping(username, value));
                matrix[i][j + 1] = tCell;
            }
        }
        CompositeKey mKey = CompositeKey.key(username, hardware);
        cache.putMatrixCache(mKey, matrix);
    }

    private void updateColorScheme() {
        ColorMapping.update(username, getRange1(), getRange2(), getRange3(), getColor1(),
                getColor2(), getColor3());
        matrix = cache.getMatrixCache(CompositeKey.key(username, hardware));
        for (Cell[] cc : matrix) {
            for (Cell ccc : cc) {
                if (!ccc.getName().contains("dBm")) {
                    int v = Integer.parseInt(ccc.getName());
                    ccc.setBgcolor(ColorMapping.mapping(username, v));
                }
            }
        }
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
        return ((matrix_data != null) && (offset_data != null));
    }

    public boolean isRunning(List<ProcessInfo> pinfo, String pname) {
        for (ProcessInfo info : pinfo) {
            if (info.getConfigFile().endsWith(pname) && info.getStatus().contains("running")) {
                return true;
            }
        }
        return false;
    }

    private boolean isValid(Entry[] offset_data, int size) {
        return offset_data != null && offset_data.length == size;
    }
}
