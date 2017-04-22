package com.rfview;

import java.io.InputStream;
import com.rfview.management.MazeserverManagement;
import com.rfview.utils.Constants;

public class FileDownloadAction extends BaseActionSupport {

    private static final long serialVersionUID = -6798445163576134855L;
    private MazeserverManagement mgmt = MazeserverManagement.getInstance();
    private InputStream fileInputStream;
    private LogFile[] filelist;
    private String action;
    private String filename;

    public FileDownloadAction() {
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public InputStream getFileInputStream() {
        return fileInputStream;
    }
    
    public String execute() {
        filelist = mgmt.getAllLogFiles();
        if (sessionMap != null) {
            String uid = (String) sessionMap.get(Constants.KEY_LOGIN_ID);
            if (uid == null) {
                return "login";
            }
            this.username = uid;
        }
        return SUCCESS;
    }

    public LogFile[] getFilelist() {
        return filelist;
    }
}
