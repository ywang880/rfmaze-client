package com.rfview;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import org.apache.commons.lang3.StringUtils;

import com.rfview.comm.HeartBeatTask;
import com.rfview.comm.MazeServer;
import com.rfview.comm.ProcessInfo;
import com.rfview.comm.RfMazeServerConnectionInfo;
import com.rfview.conf.Assignment;
import com.rfview.conf.BroadcastConf;
import com.rfview.maze.Datagrid;
import com.rfview.utils.Constants;
import com.rfview.utils.db.DbAccess;

public class SelectHardwareAction extends BaseActionSupport {

	private static final long serialVersionUID = -7025202904193630399L;
	private final DbAccess dbAccess = DbAccess.getInstance();
	private final List<String> mazeServers = new ArrayList<String>();
	private final List<String> hardwares = new ArrayList<String>();
    private final RfMazeServerConnectionInfo serverConntionInfo = RfMazeServerConnectionInfo.getInstance();
	private String hardware;
	private Datagrid cache;

	public String execute() {

		logger.info("invoke hardware select action");
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

		logger.info("query assigned hardware for user " + username);
		Assignment assignment = null;
		try {
			assignment = dbAccess.getAssignment(username);
		} catch (SQLException e) {
		}
		if ( assignment == null) {
			return ERROR;
		}

		List<String> hwAssigned = assignment.getHardwares();
		logger.info("assigned hardwares " + StringUtils.join(hwAssigned, ", "));
		List<String> availableServers = BroadcastConf.getInstance().getAllAssignedServers(); // getNonSwitchHardwareList();
		logger.info("availableServers=" + StringUtils.join(availableServers, ", "));
		List<ProcessInfo> pinfo = mgmt.getProcesses();
		for (String ss : availableServers) {
			for (String sss : hwAssigned) {
				if (sss.equals(ss) && isRunning(pinfo, sss)) {
					hardwares.add(ss);
				}
			}
		}

		if (hardwares.isEmpty()) {
			setWarningMessage(CONST_START_SERVER);
		}

		String args[] = new String[2];
		if (Datagrid.getInstance().getAgent(hardware) == null) {
			logger.info("no agent started for " + hardware);
			try {
				MazeServer theServer = serverConntionInfo.getServer(hardware);
				Timer timer = Datagrid.getTimer(hardware);
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
				return ERROR;
			}
			Datagrid.getInstance().createAgent(args, hardware);
		}
		return isTurnTable(hardware)? "turntable" : "rfmaze";
	}

	public boolean isRunning(List<ProcessInfo> pinfo, String pname) {
		for (ProcessInfo info : pinfo) {
			if (info.getConfigFile().endsWith(pname) && info.getStatus().contains("running")) {
				return true;
			}
		}
		return false;
	}

	public List<String> getMazeServers() {
		return mazeServers;
	}

	public String getHardware() {
		return hardware;
	}

	public void setHardware(String hardware) {
		this.hardware = hardware;
	}

	public List<String> getHardwares() {
		return hardwares;
	}
}
