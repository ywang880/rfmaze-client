package com.rfview;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.rfview.management.MazeserverManagement;
import com.rfview.utils.Constants;

public class FileDownloadsAction extends BaseActionSupport implements ServletRequestAware {

    private static final long serialVersionUID = -6798445163576134855L;
    private MazeserverManagement mgmt = MazeserverManagement.getInstance();
    private Logger logger = Logger.getLogger(FileDownloadsAction.class.getName());
    private InputStream fileInputStream;
    HttpServletRequest request;
    private LogFile[] filelist;
    private String filename;
    
    public FileDownloadsAction() {
    }

    public InputStream getFileInputStream() {
        return fileInputStream;
    }
    
    public LogFile[] getFilelist() {
        return filelist;
    }
    
    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
    
    public String download() {
        String filename = request.getParameter("filename");
        try {
            fileInputStream = new FileInputStream(new File(mgmt.getLogFolder()+File.separator+filename));
            logger.info("ACTION download file = " + mgmt.getLogFolder()+File.separator+filename);
        } catch (FileNotFoundException e) {
            logger.warn(e);
        }
        
        if (sessionMap != null) {
            String uid = (String) sessionMap.get(Constants.KEY_LOGIN_ID);
            if (uid == null) {
                return "login";
            }
            this.username = uid;
        }
        
        filelist = mgmt.getAllLogFiles();
        return "download";
    }

    @Override
    public void setServletRequest(HttpServletRequest request) {
        this.request = request;        
    }
}