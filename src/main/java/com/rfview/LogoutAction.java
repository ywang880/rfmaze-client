package com.rfview;

public class LogoutAction extends BaseActionSupport {

    private static final long serialVersionUID = -7025202904193630399L;
   
    public LogoutAction() {
    }

    public String execute() {
        if (sessionMap!=null) {       
            sessionMap.remove("loginId");
        }        
        return SUCCESS;
    }

}
