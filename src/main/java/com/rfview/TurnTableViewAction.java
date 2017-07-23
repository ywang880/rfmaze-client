package com.rfview;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import org.apache.log4j.Logger;

import com.rfview.comm.HeartBeatTask;
import com.rfview.comm.MazeServer;
import com.rfview.comm.ProcessInfo;
import com.rfview.comm.RfMazeServerConnectionInfo;
import com.rfview.conf.Assignment;
import com.rfview.conf.BroadcastConf;
import com.rfview.maze.Datagrid;
import com.rfview.maze.RFMazeServerAgent;
import com.rfview.utils.Constants;

public class TurnTableViewAction extends BaseActionSupport {

	private static final long serialVersionUID = -6798445163576134855L;

	private String hardware;
	private String action;
	private String status;

	private final List<String> hardwares = new ArrayList<String>();
	private final RfMazeServerConnectionInfo serverConntionInfo = RfMazeServerConnectionInfo.getInstance();
	private final Logger logger = Logger.getLogger(TurnTableViewAction.class.getName());

    private static final String SUCCESS1 = SUCCESS + "1";
    private static final String SUCCESS2 = SUCCESS + "2";
    private static final String SUCCESS3 = SUCCESS + "3";

    
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public String execute() {			
	     logger.info("invoke turn table view action");
	        if (sessionMap != null) {
	            String uid = (String) sessionMap.get(Constants.KEY_LOGIN_ID);
	            if (uid == null) {
	                return "login";
	            }
	            this.username = uid;

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
	            setWarningMessage(CONST_START_SERVER);
	        }

	        if (hardware == null || hardware.isEmpty()) {
	            setWarningMessage("Hardware not selected. Please select hardware!");
	            return SUCCESS1;
	        }

	        String returnCode;
	        if ( isLTE( hardware )) {
	            returnCode = SUCCESS2;
	        } else if ( isRBM( hardware)) {
	            returnCode = SUCCESS3;
	        } else {
	            returnCode = SUCCESS1;
	        }
	      
	        if (username.equals("admin")) {
	            setErrorMessage("invalid user name. please login as non-admin user!");
	            return returnCode;
	        }
	        
		if (sessionMap != null) {
			String uid = (String) sessionMap.get(Constants.KEY_LOGIN_ID);
			if (uid == null) {
				return "login";
			}
			this.username = uid;

			String cachedHardware = (String) sessionMap.get(Constants.KEY_HARDWARE);
			if ((cachedHardware != null) && !cachedHardware.isEmpty()) {
				hardware = cachedHardware;
			}
		}
		
		RFMazeServerAgent agent = Datagrid.getInstance().getAgent(hardware);
		if (agent == null) {
			logger.info("no cached data, fetch the data from maze server");
			String args[] = new String[2];
			try {
				MazeServer theServer = serverConntionInfo.getServer(hardware);
					    
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
				return SUCCESS;
			}

			logger.info("Create agent for " + hardware);
			agent = new RFMazeServerAgent(args, hardware);
			Datagrid.getInstance().addAgent(hardware, agent);
			agent.start();
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
			setWarningMessage(CONST_START_SERVER);
		}

		if (hardware == null || hardware.isEmpty()) {
			setWarningMessage("Hardware not selected. Please select hardware!");
			return SUCCESS;
		}

		if (username.equals("admin")) {
			setErrorMessage("invalid user name. please login as non-admin user!");
			return SUCCESS;
		}

		logger.info("Action = [" + action + "], hardware=" + hardware);
		return SUCCESS;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
}
