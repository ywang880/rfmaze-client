package com.rfview;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionSupport;
import com.rfview.management.MazeserverManagement;
import com.rfview.utils.Constants;
import com.rfview.utils.DbAccess;

public class BaseActionSupport extends ActionSupport implements SessionAware, ServletRequestAware {

    private static final long serialVersionUID = 2118418847463345983L;

    protected final Logger logger = Logger.getLogger(BaseActionSupport.class.getName());
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
}
