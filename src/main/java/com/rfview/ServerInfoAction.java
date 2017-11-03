package com.rfview;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rfview.conf.BroadcastConf;
import com.rfview.conf.MatrixConfig;
import com.rfview.exceptions.InvalidConfigurationException;
import com.rfview.maze.Server;

public class ServerInfoAction extends BaseActionSupport {

    private static final long serialVersionUID = -6798445163576134855L;
    private final List<String> configFiles = new ArrayList<String>();
    private static Map<String,String> listType;
    private static Map<String,String> listQuintechType;
    private List<ServerInfo> serverInfo;
    private List<String> mazeServers;
    private String mazeServer;
    private String action;
    private String matrixType;
    private String defaultType;
    private String matrixName;
    private String currentMatrixName;
    private String hwIp;
    private String hwIp2;
    private int numberOfInputs = 32;
    private int numberOfOutputs = 32;
    private int hwPort = 9100;
    private int hwPort2 = 9100;
    private int maxAttn = 63;
    private int minAttn;
    private int stepAttn = 1;
    private boolean invertInputOutput;
    private String selectedRows;
    private String selectCols;
    private StringBuilder buffer;
    private String quintechType;
	private String defaultQuintechType = "X";

	private String actionCmd = "";

	public String getpMatrixName() {
		return pMatrixName;
	}

	private String pMatrixName = "Matrix Name: ";
		
    private final static BroadcastConf bServer = BroadcastConf.getInstance();
    static {
        listType = new HashMap<String, String>();
        listType.put("L", "Quintech LTE3000");
        listType.put("K", "Quintech QRB3000");
        listType.put("R", "Quintech RBM3000");
        listType.put("T", "TurnTable");
        listType.put("Y", "TopYoung");
    }

    static {
    	listQuintechType = new HashMap<String, String>();
        listQuintechType.put("X", "Default");
        listQuintechType.put("N", "NEXUS");
        listQuintechType.put("C", "NEXUS Combined");
    }
    
    public String execute() {

        if (sessionMap != null) {
            String uid = (String) sessionMap.get("loginId");
            if (uid == null) {
                return "login";
            }
            this.username = uid;
        }
        logger.info("ServerInfoAction::Action = " + action);
        if (action != null) {
            String tokens[] = action.split("\\s+");
            actionCmd = tokens[0];
        }
        setMenu("server_info");

        // process browser action
        if (actionCmd.equals("create_server")) {
            int thePort = getMatrixControlPort();
            buildRegularMazeServerInfo(true, thePort);
            String message = bServer.assignMazeServer(getMatrixName(), getLocalhost(), thePort);
            if (message != null) {
                setSuccessMessage(message);
            }
        } else if (actionCmd.equals("modify_server")) {
            String newName = getMatrixName();
            String oldName = getCurrentMatrixName();
            if (mgmt.isProcessStarted(oldName)) {
                setWarningMessage("Can not modify matrix name as it is in use.");
            } else if ((newName!=null) && (oldName!=null) && (!newName.trim().equals(oldName.trim()))) {

                //delete old server
                logger.warn(oldName + " is removed");
                try {
                    dbAccess.renameAssignment(newName, oldName);
                } catch (SQLException e) {
                    logger.error(e);
                }

                String message = bServer.removeAssignedMazeServer(oldName);
                if (message != null) {
                    setSuccessMessage(message);
                }
                File f = new File(mgmt.getConfigureDir() + File.separator + oldName + ".cfg");
                f.delete();
                logger.warn("Configuration file " + f.getName() + " is deleted");
                dbAccess.reclaimPort(oldName);

                // create new server
                int thePort = getMatrixControlPort();
                buildRegularMazeServerInfo(true, thePort);
                message = bServer.assignMazeServer(newName, getLocalhost(), thePort);
                if (message != null) {
                    setSuccessMessage(message);
                }
            } else {
                try {
                    Server server = MatrixConfig.getInstance().getServerInfo(getMatrixName());
                    buildRegularMazeServerInfo(false, server.getPort());
                } catch (InvalidConfigurationException e) {
                    logger.error(e);
                }

                dbAccess.updateLabels(getMatrixName(), getNumberOfInputs(), getNumberOfOutputs());
            }
        } else if (actionCmd.equals("delete_server")) {
            if (mgmt.isProcessStarted(getMatrixName())) {
                setWarningMessage("Can not delete matrix configuration file as it is in use.");
            } else {
                String message = bServer.removeAssignedMazeServer(getMazeServer());
                if (message != null) {
                    setSuccessMessage(message);
                }
            }
        } else if (actionCmd.equals("delete_server_and_config")) {
            if (mgmt.isProcessStarted(getMatrixName())) {
                setWarningMessage("Can not delete matrix configuration file as it is in use.");
            } else {
                logger.warn(getMazeServer() + " is removed");
                String message = bServer.removeAssignedMazeServer(getMazeServer());
                if (message != null) {
                    setSuccessMessage(message);
                }
                File f = new File(mgmt.getConfigureDir() + File.separator + getMazeServer() + ".cfg");
                f.delete();
                logger.warn("Configuration file " + f.getName() + " is deleted");
                dbAccess.reclaimPort(getMazeServer());
            }
        } else if (actionCmd.equals("change_port")) {
            try {
                logger.info("new process control port = " + getBsPort());
                bServer.changePort(getBsPort());
            } catch (FileNotFoundException e) {
                logger.error(e);
                setErrorMessage("Configure file not found!");
            } catch (IOException e) {
                logger.error(e);
                setErrorMessage("Failed to update configure file!");
            }
        } else if (action != null && action.startsWith("browse")) {
            BufferedReader br;
            try {
                br = new BufferedReader(new FileReader(mgmt.getConfigureDir() + File.separator + getMazeServer() + ".cfg"));
                buffer = new StringBuilder();
                String line = br.readLine();
                while (line != null) {
                    buffer.append(line).append("\n");
                    if (line.trim().startsWith("#") || line.trim().startsWith("=")) {
                        line = br.readLine();
                        continue;
                    }
                    String[] tokens = line.split("=");
                    if (tokens.length < 2) {
                        line = br.readLine();
                        continue;
                    }
                    String token1 = tokens[0].trim();
                    String token2 = tokens[1].trim();

                    if (token1.equalsIgnoreCase("MATRIX_NAME")) {
                        setMatrixName(token2);
                        setCurrentMatrixName(token2);
                    } else if (token1.equalsIgnoreCase("matrix_socket_port")) {
                        setHwPort(Integer.parseInt(token2));
                    } else if (token1.equalsIgnoreCase("matrix_inputs")) {
                        setNumberOfInputs(Integer.parseInt(token2));
                    } else if (token1.equalsIgnoreCase("matrix_outputs")) {
                        setNumberOfOutputs(Integer.parseInt(token2));
                    } else if (token1.equalsIgnoreCase("matrix_ip")) {
                        setHwIp(token2);
                    } else if (token1.equalsIgnoreCase("max_attn")) {
                        setMaxAttn(Integer.parseInt(token2));
                    } else if (token1.equalsIgnoreCase("min_attn")) {
                        setMinAttn(Integer.parseInt(token2));
                    } else if (token1.equalsIgnoreCase("step_db")) {
                        setStepAttn((int)Float.parseFloat(token2));
                    } else if (token1.equalsIgnoreCase("invert_input_output")) {
                        setInvertInputOutput(token2.equalsIgnoreCase("yes"));
                    } else if (token1.equalsIgnoreCase("matrix_type")) {
                        defaultType = token2;
                        setMatrixType(token2);
                    } else if (token1.equalsIgnoreCase("matrix_ip2")) {
                    	setHwIp2(token2);
                    } else if (token1.equalsIgnoreCase("matrix_socket_port2")) {
                    	setHwPort2(Integer.parseInt(token2));
                    } else if (token1.equalsIgnoreCase("quintech_type")) {
                    	setQuintechType(token2);
                    	setDefaultQuintechType(token2);
                    }
                    line = br.readLine();
                }
            } catch (FileNotFoundException e) {
                logger.warn(e);
            } catch (IOException e) {
                logger.warn(e);
            }
            
            if ( "T".equals(getQuintechType()) ) {
            	pMatrixName = "Hardware Name: ";
            }
        }

        if (!bServer.exists()) {
            buildBroadcastServerInfo();
        }

        if (mazeServers == null) {
            mazeServers = bServer.getAllAssignedServers();
        }
        return "success";
    }

    public int getMatrixControlPort() {
        return nextMatrixControlPort();
    }

    public int getNumServers() {
        if (mazeServers == null) {
            return 0;
        }
        return mazeServers.size();
    }

    public void setMazeServer(String mazeServer) {
        this.mazeServer = mazeServer;
    }

    public String getMazeServer() {
        return mazeServer;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public List<String> getConfigureFiles() {
        return configFiles;
    }

    public void addConfigFile(String configFile) {
        this.configFiles.add(configFile);
    }

    public Map<String,String> getListType() {
        return listType;
    }

    public Map<String,String> getListQuintechType() {
        return listQuintechType;
    }

    public List<ServerInfo> getServerInfo() {
        return serverInfo;
    }

    public void setServerInfo(List<ServerInfo> serverInfo) {
        this.serverInfo = serverInfo;
    }

    public String getMatrixName() {
        return matrixName;
    }

    public void setMatrixName(String matrixName) {
        this.matrixName = matrixName;
    }

    public String getCurrentMatrixName() {
        return currentMatrixName;
    }

    public void setCurrentMatrixName(String currentMatrixName) {
        this.currentMatrixName = currentMatrixName;
    }

    public String getMatrixType() {
        return matrixType;
    }

    public void setMatrixType(String matrixType) {
        this.matrixType = matrixType;
    }

    public String getDefaultType() {
        return defaultType;
    }

    public void setDefaultType(String defaultType) {
        this.defaultType = defaultType;
    }

    public String getHwIp() {
        return hwIp;
    }

    public void setHwIp(String hwIp) {
        this.hwIp = hwIp;
    }

    public int getNumberOfInputs() {
        return numberOfInputs;
    }

    public void setNumberOfInputs(int numberOfInputs) {
        this.numberOfInputs = numberOfInputs;
    }

    public int getNumberOfOutputs() {
        return numberOfOutputs;
    }

    public void setNumberOfOutputs(int numberOfOutputs) {
        this.numberOfOutputs = numberOfOutputs;
    }

    public int getHwPort() {
        return hwPort;
    }

    public void setHwPort(int hwPort) {
        this.hwPort = hwPort;
    }

    public int getMaxAttn() {
        return maxAttn;
    }

    public void setMaxAttn(int maxAttn) {
        this.maxAttn = maxAttn;
    }

    public int getMinAttn() {
        return minAttn;
    }

    public void setMinAttn(int minAttn) {
        this.minAttn = minAttn;
    }

    public int getStepAttn() {
        return stepAttn;
    }

    public void setStepAttn(int stepAttn) {
        this.stepAttn = stepAttn;
    }

    public boolean isInvertInputOutput() {
        return invertInputOutput;
    }

    public void setInvertInputOutput(boolean invertInputOutput) {
        this.invertInputOutput = invertInputOutput;
    }

    public List<String> getMazeServers() {
        return mazeServers;
    }

    public void setMazeServers(List<String> mazeServers) {
        this.mazeServers = mazeServers;
    }

    public String getBsName() {
        return bServer.getName();
    }

    public void setBsPort(int bsPort) {
        bServer.setBsPort(bsPort);
    }

    public String getHwIp2() {
		return hwIp2;
	}

	public void setHwIp2(String hwIp2) {
		this.hwIp2 = hwIp2;
	}

	public int getHwPort2() {
		return hwPort2;
	}

	public void setHwPort2(int hwPort2) {
		this.hwPort2 = hwPort2;
	}

	public String getQuintechType() {
		return quintechType;
	}

	public void setQuintechType(String quintechType) {
		this.quintechType = quintechType;
	}

    public String getDefaultQuintechType() {
		return defaultQuintechType;
	}

	public void setDefaultQuintechType(String defaultQuintechType) {
		this.defaultQuintechType = defaultQuintechType;
	}

    public int getBsPort() {
        return bServer.getBsPort();
    }

    public String getSelectedRows() {
        return selectedRows;
    }

    public void setSelectedRows(String selectedRows) {
        this.selectedRows = selectedRows;
    }

    public String getSelectCols() {
        return selectCols;
    }

    public void setSelectCols(String selectCols) {
        this.selectCols = selectCols;
    }

    public String getConfigureFile() {
        return (buffer==null)? "" : buffer.toString();
    }

    public boolean isQRB3000() {
    	return "K".equals(getMatrixType()) && ("C".equals(getQuintechType()) || "N".equals(getQuintechType()));
    }
    
    public boolean isQRB3000C() {
    	return "K".equals(getMatrixType()) && "C".equals(getQuintechType());
    }

    public boolean isQRB3000N() {
        return "K".equals(getMatrixType()) && "N".equals(getQuintechType());
    }
    
    private void buildRegularMazeServerInfo(boolean create_new, int thePort) {

        MatrixConfig mConf = MatrixConfig.getInstance();
        int matrixControlPort = create_new? thePort : dbAccess.getPort(getMatrixName().trim());
        if (matrixControlPort == 0) {
            String strPort;
            try {
                strPort = (String)mConf.getConfiguration(getMatrixName().trim()).get("server_socket_port");
                matrixControlPort = Integer.parseInt(strPort);
            } catch (InvalidConfigurationException e) {
                logger.error(e);
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("matrix name = " + getMatrixName()).append("\n");
        sb.append("matrix type = " + getMatrixType()).append("\n");
        sb.append("HW ip = " + getHwIp()).append("\n");
        sb.append("server port = " + matrixControlPort).append("\n");
        sb.append("HW port = " + getHwPort()).append("\n");
        sb.append("Number inputs = " + getNumberOfInputs()).append("\n");
        sb.append("Number outputs = " + getNumberOfOutputs()).append("\n");
        sb.append("Max attn = " + getMaxAttn()).append("\n");
        sb.append("Min attn = " + getMinAttn()).append("\n");
        sb.append("Step attn = " + getStepAttn()).append("\n");
        sb.append("Invert input output = " + isInvertInputOutput()).append("\n");
        logger.info(sb.toString());

        mConf.setMatrixName(getMatrixName());
        if ( "Y".equals(getMatrixType()) ) {
        	mConf.setMatrixType("K");
        } else {
        	mConf.setMatrixType(getMatrixType());
        }
        mConf.setMatrixControlPort(matrixControlPort);
        mConf.setMatrixIp(getHwIp());
        mConf.setServerIp(getLocalhost());
        mConf.setHwPort(getHwPort());
        mConf.setMatrixSocketPort(getHwPort());
        mConf.setMatrixInputs(getNumberOfInputs());
        mConf.setMatrixOutputs(getNumberOfOutputs());
        mConf.setMaxAttn(getMaxAttn());
        mConf.setMinAttn(getMinAttn());
        mConf.setStepDb(getStepAttn());
        mConf.setInvertInputOutput(isInvertInputOutput());

        // only save quintech type a for QRB mode
        if ( isQRB3000N() || isQRB3000C()  ) {
        	mConf.setQuintechType(quintechType);
        }
        
        // only save address and port 2 for QRB combined mode
        if ( isQRB3000C() ) {
        	mConf.setHwPort2(getHwPort2());
        	mConf.setMatrixIp2(getHwIp2());
        }
        mConf.generate();
        if (create_new) {
            dbAccess.insertDefaultLabels(getMatrixName(), getNumberOfInputs(), getNumberOfOutputs());
        }

        logger.info("update database add port " + matrixControlPort);
        if (create_new) {
            dbAccess.updatePort(matrixControlPort, getMatrixName());
        }
        
         
        setDefaultType(getMatrixType());
        setMatrixType(getMatrixType());
        defaultQuintechType = getQuintechType();
        setSuccessMessage("Matrix configuration file modified successfully!");
        logger.info(" set default type = " + getDefaultType());
    }

    private void buildBroadcastServerInfo() {
        bServer.setBsIp(getLocalhost());
        bServer.writeToFile();
    }

    private String getLocalhost() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "127.0.0.1";
        }
    }

    private int nextMatrixControlPort() {
        return dbAccess.getNextAvailablePort();
    }
}
