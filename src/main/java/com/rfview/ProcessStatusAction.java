package com.rfview;

import java.util.List;

import com.rfview.comm.ProcessInfo;
import com.rfview.maze.Datagrid;
import com.rfview.maze.RFMazeServerAgent;
import com.rfview.utils.Util;

public class ProcessStatusAction extends BaseActionSupport {
	private static final String CFG_EXTENSION = ".cfg";
	private static final long serialVersionUID = 3257905400827766452L;
	private String action;

	public List<ProcessInfo> getProcessesInfo() {
		return mgmt.getProcesses();
	}

	public String execute() {
		if (sessionMap != null) {
			String uid = (String) sessionMap.get("loginId");
			if (uid == null) {
				return "login";
			}
			this.username = uid;
		}

		logger.info("Action = " + action);
		if ((action != null) && !action.isEmpty()) {
			String[] tokens = action.split(" ");
			String command = tokens[0].trim();

			if (command.equalsIgnoreCase("start")) {
				String configfile = tokens[1] + CFG_EXTENSION;
				mgmt.startProcess(username, configfile);
			} else if (command.equalsIgnoreCase("stop")) {
				disconnect(tokens[1]);
				mgmt.stopProcess(tokens[1] + CFG_EXTENSION);
				mgmt.removePid(tokens[1] + CFG_EXTENSION);
			} else {
				logger.warn("Unsupported command " + action);
			}
		}
		return "success";
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	private void disconnect(String hardware) {
		try {
			RFMazeServerAgent agent = Datagrid.getInstance().getAgent(hardware);
			if (agent != null) {
				agent.shutdown();
				agent.interrupt();
			}			
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		Util.sleep(5000L);
		Datagrid.getInstance().removeAgent(hardware);		
		Datagrid.getInstance().removeMatrix(hardware);
		Datagrid.getInstance().removeOffsetData(hardware);
	}
}
