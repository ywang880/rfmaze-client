package com.rfview.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.sql.SQLException;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.rfview.management.MazeserverManagement;
import com.rfview.maze.Datagrid;
import com.rfview.utils.Constants;
import com.rfview.utils.MatrixBuilder;
import com.rfview.utils.MatrixMapper;
import com.rfview.utils.Util;

public class RFMazeAdminServlet extends HttpServlet {

    private static final String CMD_SET_MIMO = "set_mimo";
    private static final long serialVersionUID = -4332237228315934345L;
    private static final int MAX_WAIT_TIME = 10;
    private static final Logger debugLogger = Logger.getLogger(RFMazeAdminServlet.class.getName());
    private static final Logger refreshLogger = Logger.getLogger("rfmaze.refresh.message");

    private String user;
    private String hardware;
    private final Datagrid cache = Datagrid.getInstance();
    private final StringBuffer serverResponse = new StringBuffer();
    private final MazeserverManagement mgmtBean = MazeserverManagement.getInstance();

    @Override
    public void init() throws ServletException {
        super.init();
        debugLogger.info("RFMazeServlet initialization");
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();

        user = (String)request.getSession().getAttribute("loginId");
        hardware = (String)request.getSession().getAttribute(Constants.KEY_HARDWARE);
        if ((user==null) || (hardware == null)) {
            out.println("ERROR: invalid user session parameters.");
        }

        String command = request.getParameter("command");
        hardware = request.getParameter("hardware");
        request.getSession().setAttribute(Constants.KEY_HARDWARE, hardware);

        if ((command == null) || command.isEmpty()) {
            out.println("Unsupported Command!");
        }

        if ("handoff_start".equalsIgnoreCase(command)) {
            onHandoverStart(request, out);
        } else if ("handoff_stop".equalsIgnoreCase(command)) {
            onHandoverStop(request, out);
        } else if ("refresh".equalsIgnoreCase(command)) {
            onRefresh(out);
        } else if (command.equalsIgnoreCase("set_attenuation") || command.equalsIgnoreCase(CMD_SET_MIMO)) {
            onSetAttenuration(request, out, command);
        } else if ("isconnected".equalsIgnoreCase(command)) {
            final StringBuilder ret_buffer = onCheckConnection();
            out.println(ret_buffer);
        } else {
            debugLogger.warn("Unknown command [" + command+"]");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        PrintWriter out = response.getWriter();
        user = (String)request.getSession().getAttribute("loginId");
        hardware = (String)request.getSession().getAttribute("hardware");
        if ((user ==null) || (hardware == null)) {
            out.println("ERROR: invalid user session parameters.");
        }

        String command = request.getParameter("command");
        if ((command == null) || command.isEmpty()) {
            out.println("Unsupported Command!");
        } else if ("restart".equals(command)) {
            execute("restart.sh");
        } else if ("setallmax".equals(command)) {
        	sendCommand("set 1 1 allmax");
        }
        else {
            doGet(request, response);
        }
    }

    private StringBuilder onCheckConnection() {
        StringBuilder ret_buffer = new StringBuilder();
        ret_buffer.append("<response>");
        ret_buffer.append("<state>");
        if (cache.getConnectionState(hardware)) {
            ret_buffer.append("connected.png");
        } else {
            ret_buffer.append("disconnected.png");
        }
        ret_buffer.append("</state>");
        ret_buffer.append("<server_state>OK</server_state>");

        String offsettable = MatrixBuilder.offsetToXml(hardware, false);
        ret_buffer.append(offsettable);
        ret_buffer.append("</response>");

        mgmtBean.setUpdate(user, false);
        return ret_buffer;
    }

    private void onHandoverStart(HttpServletRequest request, PrintWriter out) {
        debugLogger.info("Query String [" + request.getQueryString()+"]");
        String mimo=request.getParameter("mimo");

        String handoff_out1=request.getParameter("handoff_out1");
        String handoff_out2=request.getParameter("handoff_out2");
        String handoff_out3=request.getParameter("handoff_out3");
        String handoff_out4=request.getParameter("handoff_out4");

        String handoff_in1=request.getParameter("handoff_in1");
        String handoff_in2=request.getParameter("handoff_in2");
        String handoff_in3=request.getParameter("handoff_in3");
        String handoff_in4=request.getParameter("handoff_in4");

        String handoff_step=request.getParameter("handoff_step");
        String handoff_start=request.getParameter("handoff_start");
        String handoff_speed=request.getParameter("handoff_speed");
        String handoff_target=request.getParameter("handoff_target");

        StringBuffer buffer = new StringBuffer("====== HANDOFF START ======\n");
        buffer.append("mimo=" + mimo).append("\n");
        buffer.append("handoff_out1=" + handoff_out1).append("\n");
        buffer.append("handoff_out2=" + handoff_out2).append("\n");
        buffer.append("handoff_out3=" + handoff_out3).append("\n");
        buffer.append("handoff_out4=" + handoff_out4).append("\n");

        buffer.append("handoff_in1=" + handoff_in1).append("\n");
        buffer.append("handoff_in2=" + handoff_in2).append("\n");
        buffer.append("handoff_in3=" + handoff_in3).append("\n");
        buffer.append("handoff_in4=" + handoff_in4).append("\n");

        buffer.append("handoff_step=" + handoff_step).append("\n");
        buffer.append("handoff_start=" + handoff_start).append("\n");
        buffer.append("handoff_speed=" + handoff_speed).append("\n");
        buffer.append("handoff_target=" + handoff_target).append("\n");
        refreshLogger.info(buffer.toString());

        String[] handoff_out_list;
        String[] handoff_in_list;

        if (mimo.equalsIgnoreCase("No")) {
            handoff_out_list = new String[] {handoff_out1};
            handoff_in_list = new String[] {handoff_in1};
        } else if (mimo.contains("2X2")) {
            handoff_out_list = new String[] {handoff_out1, handoff_out2};
            handoff_in_list = new String[] {handoff_in1, handoff_in2};
        } else if (mimo.contains("4X4")) {
            handoff_out_list = new String[] {handoff_out1, handoff_out2, handoff_out3, handoff_out4};
            handoff_in_list = new String[] {handoff_in1, handoff_in2, handoff_in3, handoff_in4 };
        } else {
            debugLogger.warn("Unsupported miom");
            return;
        }

        try {

            String[] mapped_handoff_out = MatrixMapper.mapping(hardware, user, handoff_out_list, false);
            String[] mapped_handoff_in = MatrixMapper.mapping(hardware, user, handoff_in_list, true);

            startHandoff(mimo,
                        user,
                        mapped_handoff_out,
                        mapped_handoff_in,
                        handoff_step,
                        handoff_speed,
                        handoff_start,
                        handoff_target);

            debugLogger.info("Handover commands sent out");
            out.println("Commands sent successfully. Handover started!");
        } catch (SQLException e) {
            debugLogger.log(Level.WARN, e);
            out.println("ERROR: " + e.getMessage());
        } catch (Exception e) {
            debugLogger.log(Level.WARN, e);
        }
    }

    private void onHandoverStop(HttpServletRequest request, PrintWriter out) {
        String mimo=request.getParameter("mimo");

        String handoff_out1=request.getParameter("handoff_out1");
        String handoff_out2=request.getParameter("handoff_out2");
        String handoff_out3=request.getParameter("handoff_out3");
        String handoff_out4=request.getParameter("handoff_out4");

        StringBuffer buffer = new StringBuffer("====== HANDOFF STOP ======\n");
        buffer.append("mimo=" + mimo).append("\n");
        buffer.append("handoff_out1=" + handoff_out1).append("\n");
        buffer.append("handoff_out2=" + handoff_out2).append("\n");
        buffer.append("handoff_out3=" + handoff_out3).append("\n");
        buffer.append("handoff_out4=" + handoff_out4).append("\n");
        debugLogger.info(buffer.toString());

        String[] handoff_out_list;
        if (mimo.equalsIgnoreCase("No")) {
            handoff_out_list = new String[] {handoff_out1};
        } else if (mimo.contains("2X2")) {
            handoff_out_list = new String[] {handoff_out1, handoff_out2};
        } else if (mimo.contains("4X4")) {
            handoff_out_list = new String[] {handoff_out1, handoff_out2, handoff_out3, handoff_out4};
        } else {
            debugLogger.warn("Unsupported miom");
            return;
        }

        String[] mapped_handoff_out;
        try {
            mapped_handoff_out = MatrixMapper.mapping(hardware, user, handoff_out_list, false);
            stopHandleOff(mimo, user, mapped_handoff_out);

            out.println("Commands sent successfully. Handover stopped!");
        } catch (Exception e) {
            debugLogger.warn(e);
            out.println("ERROR: " + e.getMessage());
        }
    }

    private void onSetAttenuration(HttpServletRequest request, PrintWriter out, String command) {
        String outputs = request.getParameter("outputs");
        String inputs = request.getParameter("inputs");
        String value =  request.getParameter("value");

        StringBuilder builder = new StringBuilder();
        builder.append("ACTION:: ").append(command).append(" ").append(outputs).append(", ").append(inputs).append(", ").append(value);

        debugLogger.info(builder.toString());
        String[] cmdOut=new String[1];
        try {
            ObjectName mBeanName = new ObjectName(Constants.OBJECTMAME_PREFIX + hardware + Constants.OBJECTNAME_SUFFIX);
            String[] signature = {String.class.getName()};
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

            if (command.equalsIgnoreCase("set_attenuation")) {
                cmdOut[0] = "set " + outputs + " " + inputs + " " + value;
                mbs.invoke(mBeanName, "execute", cmdOut, signature);
            } else {
                cmdOut[0] = CMD_SET_MIMO + " " + outputs + " " + inputs + " " + value;
                mbs.invoke(mBeanName, "execute", cmdOut, signature);
            }

            debugLogger.info("command to server = " + cmdOut[0]);
            // wait response
            int count = 0;
            Util.sleep(200);
            String serverResp="";

            serverResponse.delete(0, serverResponse.length());
            while (count < MAX_WAIT_TIME) {
                serverResp = (String)mbs.getAttribute(mBeanName, "Data");
                if ((serverResponse!=null) && serverResp.contains("OK")) {
                    debugLogger.info(">>> response OK <<<");
                    break;
                }
                Util.sleep(200);
                count++;
            }

            debugLogger.debug("rfmaze server response = " + serverResp);
        } catch (Exception e) {
            debugLogger.error(e.getMessage(), e);
        }
    }

    private void onRefresh(PrintWriter out) {
    	debugLogger.info("onRefresh() hardware " + hardware + ", user " + user);
    	String matrixtable = MatrixBuilder.toFullXml(cache.getMatrix(hardware), user, hardware);
    	out.println(matrixtable);
    	debugLogger.debug("onRefresh() done. return 0 bytes.");
    	if (matrixtable==null) {
    		debugLogger.debug("onRefresh() done. return 0 bytes.");
    	} else {
    		debugLogger.debug("onRefresh() done. return " + matrixtable.length());
    	}
    }

    private void startHandoff(String mimo, String userid, String[] outputs,
                              String[] inputs, String changeText, String speedText,
                              String homeText, String targetText) {

        String change = changeText;
        String speed = speedText;
        String s4 = homeText;
        String s3 = targetText;
        debugLogger.info("mimo selection is [" + mimo + "]");

        if ((!mimo.equalsIgnoreCase("No")) && (outputs[0].indexOf(',') != -1)) {
            debugLogger.warn("Invalid output - " + outputs[0]);
            return;
        }

        String s1 = outputs[0];
        String s2 = inputs[0];
        int pos = 0;
        if (s2.indexOf('|') != -1) {
            pos = s2.indexOf('|');
        } else {
            pos = s2.indexOf(',');
        }

        String command = "set " + s1 + " " + s2.substring(pos + 1, s2.length()) +
                         " " + s3 + " : " + userid;
        debugLogger.debug("command=" + command);
        sendCommand(command);

        command = "set " + outputs[0] + " " + s2.substring(0, pos) + " " + s4 + " : " + userid;
        debugLogger.debug("command=" + command);
        sendCommand(command);

        String mobiles = outputs[0];
        String cellsites = inputs[0];
        command = "handover " + mobiles + " " + cellsites + " " + change +
                  " " + speed + " : " + userid;
        debugLogger.debug("command=" + command);
        sendCommand(command);

        if (mimo.contains("2X2")) {
            if (outputs[1].indexOf(',') != -1) {
                debugLogger.warn("Invalid output for mimo 2x2 - " + outputs[1]);
                return;
            }

            s1 = outputs[1];
            s2 = inputs[1];
            if (s2.indexOf('|') != -1) {
                pos = s2.indexOf('|');
            } else {
                pos = s2.indexOf(',');
            }

            command = "set " + s1 + " " + s2.substring(pos + 1, s2.length()) +
                      " " + s3 + " : " + userid;
            debugLogger.debug("2X2-2, command=" + command);
            sendCommand(command);

            command = "set " + outputs[1] + " " + s2.substring(0, pos) + " "
                    + s4 + " : " + userid;
            debugLogger.debug("2X2-2, command=" + command);
            sendCommand(command);

            mobiles = outputs[1];
            cellsites = inputs[1];
            command = "handover " + mobiles + " " + cellsites + " "
                    + change + " " + speed + " : " + userid;
            debugLogger.debug("2X2-2, command=" + command);
            sendCommand(command);
        } else if (mimo.contains("4X4")) {
            if ((outputs[1].indexOf(',') != -1)
                    || (outputs[2].indexOf(',') != -1)
                    || (outputs[3].indexOf(',') != -1)) {
                debugLogger.warn("Invalid outputs for mimo 4x4 - " + outputs[1]
                        + " - " + outputs[2] + " - " + outputs[3]);
                return;
            }

            s1 = outputs[1];
            s2 = inputs[1];
            if (s2.indexOf('|') != -1) {
                pos = s2.indexOf('|');
            } else {
                pos = s2.indexOf(',');
            }

            command = "set " + s1 + " " + s2.substring(pos + 1, s2.length()) +
                      " " + s3 + " : " + userid;
            debugLogger.debug("4x4-2, command=" + command);
            sendCommand(command);

            command = "set " + outputs[1] + " " + s2.substring(0, pos) + " " + s4 + " : " + userid;
            debugLogger.debug("4x4-2, command=" + command);
            sendCommand(command);

            mobiles = outputs[1];
            cellsites = inputs[1];
            command = "handover " + mobiles + " " + cellsites + " " +
                      change + " " + speed + " : " + userid;
            debugLogger.debug("4x4-2, command=" + command);
            sendCommand(command);

            s1 = outputs[2];
            s2 = inputs[2];
            if (s2.indexOf('|') != -1) {
                pos = s2.indexOf('|');
            } else {
                pos = s2.indexOf(',');
            }

            command = "set " + s1 + " " + s2.substring(pos + 1, s2.length()) +
                      " " + s3 + " : " + userid;
            debugLogger.debug("4x4-3, command=" + command);
            sendCommand(command);

            command = "set " + outputs[2] + " " + s2.substring(0, pos) +
                      " " + s4 + " : " + userid;
            debugLogger.debug("4x4-3, command=" + command);
            sendCommand(command);

            mobiles = outputs[2];
            cellsites = inputs[2];
            command = "handover " + mobiles + " " + cellsites +
                      " " + change + " " + speed + " : " + userid;
            debugLogger.debug("4x4-3, command=" + command);
            sendCommand(command);

            s1 = outputs[3];
            s2 = inputs[3];
            if (s2.indexOf('|') != -1) {
                pos = s2.indexOf('|');
            } else {
                pos = s2.indexOf(',');
            }

            command = "set " + s1 + " " + s2.substring(pos + 1, s2.length()) +
                      " " + s3 + " : " + userid;
            debugLogger.debug("4x4-4, command=" + command);
            sendCommand(command);

            command = "set " + outputs[3] + " " + s2.substring(0, pos) +
                      " " + s4 + " : " + userid;
            debugLogger.debug("4x4-4, command=" + command);
            sendCommand(command);

            mobiles = outputs[3];
            cellsites = inputs[3];
            command = "handover " + mobiles + " " + cellsites +
                      " " + change + " " + speed + " : " + userid;
            debugLogger.debug("4x4-4, command=" + command);
            sendCommand(command);
        }
    }

    private void stopHandleOff(String mimo, String username, String[] outputs) {
        String mobiles = "";

        if (mimo.equalsIgnoreCase("No")) {
            mobiles = outputs[0];
        } else if (mimo.equalsIgnoreCase("2X2")) {
            mobiles = outputs[0] + "," + outputs[1];
        } else if (mimo.equalsIgnoreCase("4X4")) {
            mobiles = outputs[0] + "," + outputs[1] + "," + outputs[2] + "," + outputs[3];
        }

        String command = "stop_mobile " + mobiles + " : " + username;
        sendCommand(command);
    }

    private void sendCommand(String command) {
        debugLogger.info("Send command: [" + command + "]");
        String[] cmdOut = new String[1];
        try {
            ObjectName mBeanName = new ObjectName(Constants.OBJECTMAME_PREFIX + hardware + Constants.OBJECTNAME_SUFFIX);
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

    private void execute(String command) {
        debugLogger.info("Send command: [" + command + "]");
        MazeserverManagement.getInstance().runCommand(command);
    }
}
