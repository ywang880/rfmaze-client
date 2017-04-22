package com.rfview.comm;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.net.Socket;
import java.net.SocketException;
import java.util.TimerTask;

import javax.management.Attribute;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.rfview.maze.Datagrid;
import com.rfview.utils.Constants;

public class HeartBeatTask extends TimerTask {
    private Socket clientSocket = null;
    private Logger logger = Logger.getLogger(HeartBeatTask.class.getName());
    private String ip;
    private int port;
    private String hardware;
    private final Datagrid cache = Datagrid.getInstance();
    
    public HeartBeatTask(String hardware, String ip, int port) {
        this.hardware = hardware;
        this.ip = ip;
        this.port = port;
    }
    
    @Override
    public void run() {
        OutputStream os = null;
        if (!createSocketIfNotCreated()) {
            logger.log(Level.ERROR, "failed to create client socket");
            return;
        }
        
        try {
            os = clientSocket.getOutputStream();
            if (os != null) {
                os.write("hello\r\n".getBytes());
                os.flush();
                if ( clientSocket.getInputStream().available() > 0) {
                	notify(true);	
                }
            }
        } catch (SocketException e) {
        	notify(false);
            logger.log(Level.ERROR, e.getMessage());
            try {
                clientSocket.close();
            } catch (IOException e1) {}
            clientSocket = null;
        } catch (IOException e) {
        	notify(false);
            logger.log(Level.ERROR, e.getMessage());
            try {
                clientSocket.close();
            } catch (IOException e1) {}
            clientSocket = null;
        }
    }
   
    private boolean createSocketIfNotCreated() {
        if (clientSocket == null) {
            try {
                clientSocket = new Socket(ip, port);
                clientSocket.setKeepAlive(true);
            } catch (SocketException e) {
                logger.log(Level.ERROR, e.getMessage());
            } catch (IOException e) {
                logger.log(Level.ERROR, e.getMessage());
            }
        }
        
        return (clientSocket!=null);
    }
    
    private void notify(boolean status) {
    	cache.setConnectionState(hardware, status);
    	final MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
    	ObjectName mBeanName;
		try {
			mBeanName = new ObjectName(Constants.OBJECTMAME_PREFIX + hardware + Constants.OBJECTNAME_SUFFIX);
			mbeanServer.setAttribute(mBeanName, new Attribute("Status", Boolean.toString(status)));
		} catch (MalformedObjectNameException | AttributeNotFoundException | MBeanException | ReflectionException | InvalidAttributeValueException | InstanceNotFoundException e) {
			logger.error("notify() failed to set attribute ", e);
		}
    }
}