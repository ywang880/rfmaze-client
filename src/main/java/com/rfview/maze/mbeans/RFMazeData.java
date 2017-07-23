package com.rfview.maze.mbeans;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.log4j.Logger;

import com.rfview.events.Event;
import com.rfview.events.EventType;
import com.rfview.events.MatrixDataEvent;
import com.rfview.listeners.EventListener;
import com.rfview.maze.Datagrid;
import com.rfview.maze.Entry;
import com.rfview.maze.RfmazeDataItem;
import com.rfview.maze.client.RFMazeConnector;
import com.rfview.utils.Constants;
import com.rfview.utils.Util;

public class RFMazeData implements RFMazeDataMBean, MBeanRegistration, EventListener, Serializable {

    private static final long serialVersionUID = -2173204238498046913L;
    private static final String MATRIX_DATA_PATTERN = "StT[0-9]{1,2}\\s*+[0-9]{1,2}.*EnD|set\\s+[0-9]{1,2}\\s+[0-9]{1,2}\\s+[0-9]{1,3}\\s+OK";
    private static final String SWITCH_DATA_PATTERN = "\\s*set\\s+[0-9]{1,2}\\s+[0-9]{1,2}\\s+(ON|OFF)\\s+OK\\s*";
    
    private volatile String data;
    private Datagrid cache = Datagrid.getInstance();
    private RFMazeConnector client;
    private BlockingQueue<RfmazeDataItem> dataQueue;
    private volatile String command;
    private boolean connected;
    private final transient Logger msgLogger = Logger.getLogger(Constants.MESSAGE_LOGGER);
    private final transient Logger logger = Logger.getLogger(RFMazeData.class.getName());
    private volatile String status = "UNKNOWN";
    private int count = 0;
    private String etag;
	private String hardware;
    
    public RFMazeData(RFMazeConnector client, BlockingQueue<RfmazeDataItem> dataQ) {
    	this.client = client;
        this.dataQueue = dataQ;
        this.client.addListener(this);
    }

    public void destroy() {
    	client.getSession().closeNow();
    	client.stop();
    	client = null;
    }

    public String getHardware() {
		return hardware;
	}

	public void setHardware(String hardware) {
		this.hardware = hardware;
	}
	
    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public synchronized void setData(String data) {
        this.data = data;
    }

    @Override
    public synchronized String getData() {
        return data;
    }

    @Override
    public synchronized String execute(String command) {
    	if (client.getSession() == null || !client.getSession().isConnected()) {
    		logger.info("execute() session closed. reconnect and wait for 5 seconds ... ");
    		client.doConnection();
    		Util.sleep(5000L);
    	}
    	
    	StringBuilder builder = new StringBuilder();
    	builder.append("execute() write to session host = ");
    	builder.append(client.getHost());
    	builder.append(" : " );
    	builder.append(client.getPort());
    	builder.append(" >>> ");
    	builder.append(client.getSession().getId());
    	
    	logger.info(builder.toString());
    	client.getSession().write(command);
    	return null;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void generateEvent(String data) {
        MatrixDataEvent e = new MatrixDataEvent("simulator", data);
        handleEvent(e);
    }

    public int getMatrix(String hardware, int x, int y) {
        Entry[][] entries = cache.getMatrix(hardware);
        if ((x < entries.length) && (y < entries[0].length)) {
            return entries[x][y].getValue();
        } else {
            return -9;
        }
    }

    public void dumpPower(String hardware) {
    	try {
	        logger.info("dumpPower() [" + hardware+"]");
	        Entry[] dataset = cache.getOffset(hardware.trim());
	        StringBuilder sb = new StringBuilder("\n");
	        for (Entry e : dataset) {
	        	sb.append(new DecimalFormat("00").format(e.getValue())).append(",");
	        }
	        logger.info(sb.toString());
    	} catch (Exception e) {
    		logger.error(e);
    	}
    }

    public void dumpMatrix(String hardware) {
        Entry[][] entries = cache.getMatrix(hardware);
        StringBuilder sb = new StringBuilder("\n");
        for (Entry[] el : entries) {
            for (Entry e : el) {
                sb.append(new DecimalFormat("00").format(e.getValue())).append(",");
            }
            sb.append("\n");
        }
        logger.info(sb.toString());
    }

    @Override
    public void handleEvent(Event e) {
    			  
        Object eData = e.getEventData();        
        if ((eData==null) || !(eData instanceof String)) {
            logger.warn("unsupported event " + e.getType().name());
            return;
        }

        if (EventType.COMMUNICATION.equals(e.getType())) {
            this.connected = e.getEventData().equals("connected");
        } else if (EventType.MATRIX_DATA.equals(e.getType())) {
            String eventData = eData.toString();
            if (eventData != null && eventData.matches("(?i)hello\\s*OK\\s*[\r\n]?.*")) {
                count--;
            }

            String eventTokens[] = eventData.split("\r");
            for (String token : eventTokens) {
            	String incomingData = token.trim();
                boolean swichingMode = incomingData.matches(SWITCH_DATA_PATTERN);
                if (!incomingData.matches(MATRIX_DATA_PATTERN) && !incomingData.matches(SWITCH_DATA_PATTERN) )  {
                    logger.warn("handleEvent() ignored event data [ " + incomingData + " ] does not match " + MATRIX_DATA_PATTERN  + " or " + SWITCH_DATA_PATTERN);
                    continue;
                }

                if (incomingData != null) {
                    onProcess(incomingData, swichingMode);
                }
                setData(incomingData);
            }
        }
    }

    // StT2 29 63:availableEnD
    // StT0 7 63:-9.0dBmEnD
    // set 6 8 15 OK
    private void onProcess(String value, boolean isSwichingMode) {
        RfmazeDataItem data = null;
        if (isSwichingMode) {
            String[] tokens = value.trim().split("\\s+");
            if (tokens.length < 4) {
                return;
            }
            String val = tokens[3].matches("ON")? "1" : "0";
            data = new RfmazeDataItem(tokens[1], tokens[2], val, false);
        } else {        
	        String tmpString = value.replace("StT", "").replace("EnD", "");
	        if (tmpString.contains("dBm")) {
	            String[] tokens = tmpString.replace("dBm", "").split("\\s+");
	            if (tokens.length < 3) {
	                return;
	            }
	            String[] subtokens = tokens[2].split("\\s*:\\s*");
	            if (subtokens.length < 2) {
	                return;
	            }
	            data = new RfmazeDataItem(tokens[1], "0", subtokens[1], true);
	        } else if (tmpString.matches("set\\s+[0-9]{1,2}\\s+[0-9]{1,2}\\s+[0-9]{1,3}\\s+OK")) {
	            String[] tokens = tmpString.split("\\s+");
	            if (tokens.length < 5) {
	                return;
	            }
	            data = new RfmazeDataItem(tokens[2], tokens[1], tokens[3], false);
	            msgLogger.info("Update metrix " + data);
	        } else {
	            String[] tokens = tmpString.split("\\s+");
	            if (tokens.length < 3) {
	                return;
	            }
	
	            String[] subtokens = tokens[2].split("\\s*:\\s*");
	            data = new RfmazeDataItem(tokens[1], tokens[0], subtokens[0], false);
	        }
        }
        logger.debug("Add to queue let matrix updater to update matrix, " + data);
        
        if ( data!=null ) {
        	logger.info("Write [ " + data + " ] to Queue " + getHardware());
            dataQueue.offer(data);
            etag = UUID.randomUUID().toString();
        }
    }

    public int getCounter() {
        return count;
    }

    public void setCounter(int count) {
        this.count = count;
    }

    @Override
    public ObjectName preRegister(MBeanServer server, ObjectName name) throws Exception {
        return null;
    }

    @Override
    public void postRegister(Boolean registrationDone) {
    }

    @Override
    public void preDeregister() throws Exception {
    	client.getSession().closeNow();
    	client.stop();
    	client = null;    	
    	dataQueue = null;
    }

    @Override
    public void postDeregister() {
    }

	@Override
	public String getEtag() {
		return etag;
	}

	@Override
	public void shutdown(String hardware) {
		if (hardware == null || hardware.isEmpty()) {
			logger.warn("shutdown() invalid hardware name");
			return;
		}
		logger.info("shutdown() disconnect rfmazer server " + hardware);
		connected = false;
		client.getSession().closeNow();
    	client.stop();
    	client = null;
	}
}
