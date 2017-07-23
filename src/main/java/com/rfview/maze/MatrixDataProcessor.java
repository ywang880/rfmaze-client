package com.rfview.maze;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

import com.rfview.conf.MatrixConfig;
import com.rfview.utils.Util;

public class MatrixDataProcessor extends Thread {

    private final String hardware;
    private BlockingQueue<RfmazeDataItem> dataQueue;
    private final Logger logger = Logger.getLogger(MatrixDataProcessor.class.getName());
    private final Datagrid cache = Datagrid.getInstance();
    private final AtomicBoolean stopProcessing = new AtomicBoolean(false);
    private final String type;
    private final boolean isLTE;
    private final boolean isRBM;
 
    public MatrixDataProcessor(String hardware, BlockingQueue<RfmazeDataItem> dataQueue) {
        this.hardware = hardware;
        this.dataQueue = dataQueue;
        super.setName("MatrixDataProcessor : " + hardware);
        this.type = MatrixConfig.getInstance().getType(hardware);
        isLTE = this.type.equalsIgnoreCase("L"); 
        isRBM = this.type.equalsIgnoreCase("R");
    }

    public void stopProcessor() {
		logger.info("MatrixDataProcessor() stopProcessor " + hardware);
        stopProcessing.set(true);
        dataQueue.offer(RfmazeDataItem.VOID);    }

    @Override
    public void run() {
        Util.sleep(3000L);
        logger.info("MatrixDataProcessor start data processor for" +  hardware);
        while (!stopProcessing.get() && !Thread.currentThread().isInterrupted()) {
            try {
                RfmazeDataItem data = dataQueue.take();
                if (data.isVoidData()) {
                	logger.info("MatrixDataProcessor exit loop");
                    Collection<RfmazeDataItem> c = new ArrayList<RfmazeDataItem>();
                    dataQueue.drainTo(c);
                    dataQueue = null;
                    return;
                }
                process(data);
            } catch (InterruptedException e) {
                logger.error(e);
                return;
            }
        }
        logger.info("MatrixDataProcessor run() exit thread");
    }

    private void process(RfmazeDataItem data) {
    	logger.info("update hardware (" + hardware + ") matrix " + data);
    	if (data.isOffset()) {
    		Entry[] items = cache.getOffset(hardware);
    		float fValue = 0.0f;
    		try {
    			fValue = Float.parseFloat(data.getValue());
    		} catch (Exception e) {
    			logger.error("invalid number " + data.getValue());
    			return;
    		}
    		int index = Integer.parseInt(data.getRow()) - 1;
    		if (items == null) {
    			logger.error("process() no cached offset data: hardware[" + hardware + "], incoming data[" + data + "]");
    			return;
    		}
    		items[index].setValue((int) fValue);       
    	} else {
    		Entry[][] entries = cache.getMatrix(hardware);
    		if (entries == null) {
    			logger.error("process() no cached matrix data: hardware[" + hardware + "], incoming data[" + data + "]");
    			return;
    		}
                
    		int erow = Integer.parseInt(data.getRow());
    		int ecol = Integer.parseInt(data.getColumn());
    		if (erow > 0 && ecol > 0) {
    			logger.debug("update matrix [ " + data.getRow() + "," + data.getColumn() +  "]=" + data.getValue());
    			int value = Integer.parseInt(data.getValue());
    			if (isRBM) {
    				if (value == 1) {
    					for (int n = 0; n < entries.length; n++) {
    						entries[n][ecol - 1].setValue(0);
    					}
    					for (int m = 0; m < entries[erow - 1].length; m++) {
    						entries[erow - 1][m].setValue(0);
    					}
    					entries[erow - 1][ecol - 1].setValue(1);
    				}
    			} else if (isLTE) {
    				logger.info("process() set (" + (erow-1) + "," + (ecol-1) + ")="+value);                    	
    				entries[erow-1][ecol-1].setValue(value);
    			} else {
    				logger.info("process() set (" + (erow-1) + "," + (ecol-1) + ")="+value);                    	
    				entries[erow-1][ecol-1].setValue(value);
    			}
    		} else if (erow == 0 && ecol > 0) {
    			// must be LTE output attenuation
    			String[] attens = cache.getAttenuation(hardware);
    			attens[ecol - 1] = data.getValue();
    		}
    	}        
    }
}
