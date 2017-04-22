package com.rfview.maze;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

import org.apache.log4j.Logger;

import com.rfview.maze.client.RFMazeConnector;
import com.rfview.maze.mbeans.RFMazeData;
import com.rfview.utils.Constants;

public class RFMazeServerAgent extends Thread {

	private BlockingQueue<RfmazeDataItem> dataQueue = new LinkedBlockingDeque<RfmazeDataItem>();

	private final Logger logger = Logger.getLogger(RFMazeServerAgent.class.getName());
	private final MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
	private final RFMazeData rfmazeData;
	private final String[] args;
	private RFMazeConnector connector;
	private final MatrixDataProcessor processor;
	private final String hardware;

	public RFMazeServerAgent(String args[], String hardware) {
		this.args = args;
		this.hardware = hardware;
		processor = new MatrixDataProcessor(hardware, dataQueue);
		connector = new RFMazeConnector(hardware, args[0], Integer.parseInt(args[1]));
		this.rfmazeData = new RFMazeData(connector, dataQueue);
		this.rfmazeData.setHardware(hardware);
		super.setName("RFMazeServerAgent: " + hardware);
	}

	public void shutdown() {
		logger.info("RFMazeServerAgent() shutdown " + hardware);
		if (processor != null) {
			processor.stopProcessor();
			processor.interrupt();
		}

		connector.stop();
		connector = null;

		Collection<RfmazeDataItem> c = new ArrayList<RfmazeDataItem>();
		dataQueue.drainTo(c);
		dataQueue = null;
		
		try {
			ObjectName mBeanName = new ObjectName(Constants.OBJECTMAME_PREFIX + hardware + Constants.OBJECTNAME_SUFFIX);
			mbeanServer.unregisterMBean(mBeanName);
		} catch (Exception e) {
        	logger.error("shutdown() ", e);
		}
		
	}

	public String[] getArgs() {
		return args;
	}

	public String getHardware() {
		return hardware;
	}

	public void run() {
		logger.info("RFMazeServerAgent() start");
		try {
			registerMBeans();

			processor.start();

			connector.start();

			synchronized ( this) {
				wait();
			}
			unregisterIfExist();
		}  catch (InterruptedException e) {
			logger.warn(e.getMessage());
		} catch (Exception e) {
			logger.error("Failed to run thread ", e);
		}
		logger.info("RFMazeServerAgent() exit");
	}

	private void registerMBeans() throws MalformedObjectNameException, MBeanRegistrationException,
			InstanceAlreadyExistsException, NotCompliantMBeanException, InstanceNotFoundException {

		ObjectName mBeanName = new ObjectName(Constants.OBJECTMAME_PREFIX + hardware + Constants.OBJECTNAME_SUFFIX);

		Set<ObjectInstance> mbeans = mbeanServer.queryMBeans(mBeanName, null);
		if (mbeans != null && !mbeans.isEmpty()) {
			mbeanServer.unregisterMBean(mBeanName);
		}
		logger.info("Register mbean " + mBeanName + ", hardware = " + hardware);
		mbeanServer.registerMBean(rfmazeData, mBeanName);
	}

	private void unregisterIfExist() throws MBeanRegistrationException {
		ObjectName mBeanName = null;
		try {
			mBeanName = new ObjectName(Constants.OBJECTMAME_PREFIX + hardware + Constants.OBJECTNAME_SUFFIX);
		} catch (Exception e) {			
		}

		try {
			if (mbeanServer.isRegistered(mBeanName)) {
				mbeanServer.unregisterMBean(mBeanName);
			}
		} catch (Exception e) {
			logger.error("Failed tounregister mbean " + mBeanName);
		}
		return;
	}
}
