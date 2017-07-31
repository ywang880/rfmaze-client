package com.rfview.servlets.listeners;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.rfview.maze.resources.MatrixResources;

public class RFMatrixServletListener implements ServletContextListener {

	private MatrixResources resources;
	
	public MatrixResources getResources() {
		return resources;
	}

	public void setResources(MatrixResources resources) {
		this.resources = resources;
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		Logger.getLogger(this.getClass()).info("contextInitialized()");
		MatrixResources.init();
		Logger.getLogger(this.getClass()).info("Matrix resources \n" + MatrixResources.getResources());
	}
}
