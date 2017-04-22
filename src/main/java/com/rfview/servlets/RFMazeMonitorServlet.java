package com.rfview.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.sql.SQLException;
import java.util.Map;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.rfview.conf.Assignment;
import com.rfview.utils.DbAccess;

public class RFMazeMonitorServlet extends HttpServlet {

    private static final long serialVersionUID = -4332237228315934345L;

    private static final String NAME_RFMAZE1 = "com.rfview.management.server:type=";
    private static final String NAME_RFMAZE2 = ",name=rfmaze";

    private static final Logger debugLogger = Logger.getLogger(RFMazeMonitorServlet.class.getName());
    
    private String user;
    private String hardware;

    @Override
    public void init() throws ServletException {
        super.init();
        debugLogger.info("RFMazeMonitorServlet initialization");
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        PrintWriter out = response.getWriter();

        user = (String) request.getSession().getAttribute("loginId");
        hardware = (String) request.getSession().getAttribute("hardware");

        debugLogger.info("session user name = " + user);
        debugLogger.info("session hardware = " + hardware);

        String command = request.getParameter("command");
        if ((command == null) || command.isEmpty()) {
            out.println("Unsupported Command!");
        }
        
        String user = request.getParameter("user");
        String hardware = request.getParameter("hardware");
        if (hardware == null) {
            out.println("Hardware not specified.");
        }
        if (command.equals("getAssignedRow")) {
            executeCommand(out, true, user, hardware);
        } else if (command.equals("getAssignedColumn")) {
            executeCommand(out, false, user, hardware);
        } else if (command.equals("getAll")) {
            out.println(command +" not supported,");
        } else {
            out.println(command +" not supported,");
        }
    }

    private void executeCommand(PrintWriter out, boolean getrow, String user, String hardware) {
        Assignment assignement;
        try {
            assignement = DbAccess.getInstance().getAssignment(hardware, user);
            if (assignement == null) {
                out.println("no result!");
            }
            
            Map<String, String> resultset = assignement.getAssingedRows();
            if (getrow) {
                resultset = assignement.getAssingedRows();
            }else {
                resultset = assignement.getAssingedColumns();
            }
            
            StringBuilder buffer = new StringBuilder();
            for (String s : resultset.keySet()) {
                buffer.append(s).append(":").append(resultset.get(s)).append("\n");
            }
             
            out.println(buffer.toString());
        } catch (SQLException e) {
            out.println(e.getMessage());
        }
    }

    protected void sendCommand(String command) {
        debugLogger.info("Send command: [" + command + "]");
        String[] cmdOut = new String[1];
        try {
            ObjectName mBeanName = new ObjectName(NAME_RFMAZE1 + hardware + NAME_RFMAZE2);
            String[] signature = { String.class.getName() };
            cmdOut[0] = command;
            ManagementFactory.getPlatformMBeanServer().invoke(mBeanName, "execute", cmdOut, signature);
        } catch (InstanceNotFoundException e) {
            debugLogger.error(e.getMessage());
        } catch (ReflectionException e) {
            debugLogger.error(e.getMessage());
        } catch (MBeanException e) {
            debugLogger.error(e.getMessage());
        } catch (MalformedObjectNameException e) {
            debugLogger.error(e.getMessage());
        }
    }
}
