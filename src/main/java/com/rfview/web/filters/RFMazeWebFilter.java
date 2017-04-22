package com.rfview.web.filters;

import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.Set;
import java.util.Timer;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.struts2.dispatcher.ng.filter.StrutsPrepareAndExecuteFilter;

import com.rfview.maze.Datagrid;
import com.rfview.maze.RFMazeServerAgent;
import com.rfview.utils.Util;

public class RFMazeWebFilter extends StrutsPrepareAndExecuteFilter {

    private Logger logger = Logger.getLogger(RFMazeWebFilter.class.getName());
    
    @Override
    public void destroy() {
        super.destroy();

        logger.info("stop timer");
        Map<String, Timer> timers = Datagrid.getTimers();
        for (String timerName : timers.keySet()) {
            logger.info("cancel timer " + timerName);
            timers.get(timerName).cancel();
        }        
        Datagrid.getTimers().clear();

        logger.info("shutdown agents");
        Map<String, RFMazeServerAgent> agents = Datagrid.getInstance().getAgents();
        logger.info("number registered agents " + agents.size());

        for (Map.Entry<String, RFMazeServerAgent> entry :agents.entrySet()) {
            logger.info("shutdown connection agent " + entry.getKey());
            entry.getValue().shutdown();
            Util.sleep(1000L);
            entry.getValue().interrupt();
        }
        
        Datagrid.getInstance().removeAgents();
        
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();        
        Set<ObjectInstance> mbeans = ManagementFactory.getPlatformMBeanServer().queryMBeans(null, null);
        logger.info("unregister all mbeans");

        for (ObjectInstance mbean : mbeans) {
            ObjectName objectName = mbean.getObjectName();
            if (objectName.getCanonicalName().startsWith("com.rfview")) {
                try {
                    logger.info("unregister mbean " + objectName.getCanonicalName());
                    server.unregisterMBean(objectName);
                } catch (MBeanRegistrationException e) {
                    logger.warn(e);
                } catch (InstanceNotFoundException e) {
                    logger.warn(e);
                }
            }
        }
        logger.info("Shutdown log4j");
        LogManager.shutdown();
    }    
}
