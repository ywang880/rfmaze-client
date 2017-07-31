package com.rfview.maze.resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class MatrixResources {

	private static final Properties resources = new Properties();
	private static final Logger LOG = Logger.getLogger(MatrixResources.class);

	public static void init() {
		try {
			InputStream is = MatrixResources.class.getClassLoader().getResourceAsStream("MatrixResources.properties");
			resources.load(is);
		} catch (IOException e) {
			LOG.error("Failed to load MatrixResources.properties file");
		}
	}

	public static Properties getResources() {
		return resources;
	}
}
