package com.rfview;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionSupport;
import com.rfview.comm.ProcessInfo;
import com.rfview.conf.Assignment;
import com.rfview.conf.MatrixConfig;
import com.rfview.exceptions.InvalidConfigurationException;
import com.rfview.management.MazeserverManagement;
import com.rfview.utils.Constants;
import com.rfview.utils.db.DbAccess;

public class BaseActionSupport extends ActionSupport implements SessionAware, ServletRequestAware {

    private static final long serialVersionUID = 2118418847463345983L;
    
    protected static final String CONST_START_SERVER = "There are apparently no running rfmaze processes. Please start process first!";

    protected static final long FIVE_SECONDS = 5000L;
    protected static final String SUCCESS1 = SUCCESS + "1";
    protected static final String SUCCESS2 = SUCCESS + "2";
    protected static final String SUCCESS3 = SUCCESS + "3";
    protected static final String SUCCESS4 = SUCCESS + "4";
    protected static final String SUCCESS5 = SUCCESS + "5";
    
    protected final Logger logger = Logger.getLogger(BaseActionSupport.class);
    protected final DbAccess dbAccess = DbAccess.getInstance();
    protected final MazeserverManagement mgmt = MazeserverManagement.getInstance();
    protected String username;
    protected String menu;
    protected String showcontent = Constants.CONST_YES;
    private String errorMessage = null;
    private String infoMessage = null;
    private String warningMessage = null;
    private String successMessage = null;
    protected Map<String, Object> sessionMap;
    protected HttpServletRequest request;
    
    public String getSuccessMessage() {
        return successMessage;
    }

    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getInfoMessage() {
        return infoMessage;
    }

    public void setInfoMessage(String infoMessage) {
        this.infoMessage = infoMessage;
    }

    public String getWarningMessage() {
        return warningMessage;
    }

    public void setWarningMessage(String warningMessage) {
        this.warningMessage = warningMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }    
    
    @Override
    public void setSession(Map<String, Object> sessionMap) {
      this.sessionMap = sessionMap;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getShowcontent() {
        return showcontent;
    }

    public void setShowcontent(String showcontent) {
        this.showcontent = showcontent;
    }
    
    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    @Override
    public void setServletRequest(HttpServletRequest request) {
        this.request = request;
    }
    
    public HttpServletRequest getServletRequest() {
        return request;        
    }
    
    protected boolean isLTE(String hardware) {
        try {
            return MatrixConfig.getInstance().getServerInfo(hardware).isLTE();
        } catch (InvalidConfigurationException e) {
            logger.error("ERR", e);
        }
        return false;
    }

    protected boolean isRBM(String hardware) {
        try {
            return MatrixConfig.getInstance().getServerInfo(hardware).isRBM();
        } catch (InvalidConfigurationException e) {
            logger.error("ERR", e);
        }
        return false;
    }
    
    protected String getMetrixType(String hardware) {
        try {
            return MatrixConfig.getInstance().getServerInfo(hardware).getType();
        } catch (InvalidConfigurationException e) {
            logger.error("ERR", e);
        }
        return "";
    }

    protected boolean isTurnTable(String hardware) {
        try {
            return MatrixConfig.getInstance().getServerInfo(hardware).isTurnTable();
        } catch (InvalidConfigurationException e) {
            logger.error("ERR", e);
        }
        return false;
    }

    protected boolean isTopYoung(String hardware) {
        try {
            return MatrixConfig.getInstance().getServerInfo(hardware).isTopYoung();
        } catch (InvalidConfigurationException e) {
            logger.error("ERR", e);
        }
        return false;
    }

    protected boolean isQRBTopYoung( String hardware, Assignment assignment ) {
        try {
            if ( !MatrixConfig.getInstance().getServerInfo(hardware).isQRB() || assignment == null ) { 
            	return false;
            }
            String rowTokens[] = assignment.getRows().split(",");
            return rowTokens != null && rowTokens.length == 1;
        } catch (InvalidConfigurationException e) {
            logger.error("ERR", e);
        }
        return false;
    }

    protected boolean isRunning(List<ProcessInfo> pinfo, String pname) {
		for (ProcessInfo info : pinfo) {
			if (info.getConfigFile().endsWith(pname) && info.getStatus().contains("running")) {
				return true;
			}
		}
		return false;
	}
}
