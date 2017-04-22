package com.rfview;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.rfview.conf.BroadcastConf;
import com.rfview.maze.User;
import com.rfview.utils.Constants;
import com.rfview.utils.DbAccess;

public class AssignMatrixAction extends BaseActionSupport {

	private static final long serialVersionUID = -6798445163576134855L;

	private BroadcastConf bConf = BroadcastConf.getInstance();

	private String action;
	private List<String> assignedusers;
	private String assigntouser;
	private final DbAccess dbAccess = DbAccess.getInstance();
	private String hardware;
	private String actionCmd = "";
	private List<String> params;
	private List<String> users;
	private String returnMessage="";

	public String getReturnMessage() {
		return returnMessage;
	}

	public AssignMatrixAction() {
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getHardware() {
		return hardware;
	}

	public void setHardware(String hardware) {
		this.hardware = hardware;
		sessionMap.put(Constants.KEY_HARDWARE, hardware);
	}

	public String getAssigntouser() {
		return assigntouser;
	}

	public void setAssigntouser(String assigntouser) {
		this.assigntouser = assigntouser;
		if (sessionMap.containsKey(Constants.KEY_ASSIGNTOUSER)) {
			sessionMap.remove(Constants.KEY_ASSIGNTOUSER);
		}
		sessionMap.put(Constants.KEY_ASSIGNTOUSER, assigntouser);
	}

	public List<String> getUsers() {
		return users;
	}

	public String execute() {
		if (sessionMap != null) {
			String uid = (String) sessionMap.get(Constants.KEY_LOGIN_ID);
			if (uid == null) {
				return "login";
			}
			this.username = uid;
			hardware = (String) sessionMap.get(Constants.KEY_HARDWARE);
			assigntouser = (String) sessionMap.get(Constants.KEY_ASSIGNTOUSER);
		}

		if ((action != null) && !action.trim().isEmpty()) {
			String tokens[] = action.split("\\s+");
			actionCmd = tokens[0];
			if (tokens.length > 1) {
				params = new ArrayList<String>();
				for (int i = 1; i < tokens.length; i++) {
					params.add(tokens[i].trim());
				}
			}
		}

		// process browser action
		if (actionCmd.equals("assign")) {
			String retMessage = dbAccess.assignAlltoUser(getAssigntouser(), getHardware());
			if (StringUtils.isBlank(retMessage)) {
				returnMessage = "All inputs and outputs of " + getHardware() + " is successfully assigned to " + getAssigntouser();
			} else {

			}
			forceReload(getAssigntouser());
		} else if (actionCmd.equals("free")) {
			String retMessage = dbAccess.freeAllFromUser(getAssigntouser(), getHardware());
			if (StringUtils.isBlank(retMessage)) {
				returnMessage = "All inputs and outputs of " + getHardware() + " assigned to " + getAssigntouser() + " is freed successfully.";
			} else {
				returnMessage = "ERROR: " + retMessage;
			}
			forceReload(getAssigntouser());
		}

		users = new ArrayList<String>();
		try {
			for (User u : dbAccess.queryUsers()) {
				String uid = u.getId();
				if (uid.equals("admin")) {
					continue;
				}
				users.add(uid);
			}
		} catch (SQLException e1) {
			logger.error(e1);
		}

		assignedusers = new ArrayList<String>();
		try {
			for (User u : dbAccess.queryUsers()) {
				if (!"admin".equals(u.getId())) {
					assignedusers.add(u.getId());
				}
			}
		} catch (SQLException e) {
			logger.error(e);
		}

		// build matrix and return
		if ((hardware == null) || hardware.trim().isEmpty()) {
			setWarningMessage("NOTE: Select hardware and user.");
		}

		return SUCCESS;
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

	private void forceReload(String user) {
		mgmt.setReload(user, true);
		mgmt.setUpdate(user, true);
	}
}
