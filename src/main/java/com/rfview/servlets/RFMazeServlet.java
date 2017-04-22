package com.rfview.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.management.AttributeNotFoundException;
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

import org.apache.log4j.Logger;

import com.rfview.conf.Assignment;
import com.rfview.management.MazeserverManagement;
import com.rfview.maze.AssignMapper;
import com.rfview.maze.Datagrid;
import com.rfview.maze.MappingCompositeKey;
import com.rfview.utils.Constants;
import com.rfview.utils.DbAccess;
import com.rfview.utils.MatrixBuilder;
import com.rfview.utils.MatrixMapper;
import com.rfview.utils.Util;

public class RFMazeServlet extends HttpServlet {

    private static final String CMD_TIER_ROAM = "tier_roam ";
	private static final String CMD_HANDOVER = "handover";
	private static final String DELIMITER = " ";
	private static final long serialVersionUID = -4332237228315934345L;
    private static final String CMD_SET_MIMO = "set_mimo";
    private static final int MAX_WAIT_TIME = 10;
    private static final Logger debugLogger = Logger.getLogger(RFMazeServlet.class.getName());
    private static final Logger refreshLogger = Logger.getLogger("rfmaze.refresh.message");
    private static final DbAccess dbAccess = DbAccess.getInstance();

    private String user;
    private String hardware;
    private final Datagrid cache = Datagrid.getInstance();
    private final StringBuffer serverResponse = new StringBuffer();
    private String uuid = "";
    private Map<MappingCompositeKey, AssignMapper> mapper = new ConcurrentHashMap<MappingCompositeKey, AssignMapper>();

    private MazeserverManagement mgmtBean = MazeserverManagement.getInstance();

    @Override
    public void init() throws ServletException {
        super.init();
        uuid = UUID.randomUUID().toString();
        debugLogger.info("RFMazeServlet initialization, uuid=" + uuid);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();

        try {
            user = (String) request.getSession().getAttribute("loginId");
            hardware = (String) request.getSession().getAttribute("hardware");
            if ((user == null) || (hardware == null)) {
                out.println("ERROR: invalid user session parameters.");
            }

            debugLogger.debug("session user name = " + user + ", session hardware = " + hardware);

            String command = request.getParameter("command");
            if ((command == null) || command.isEmpty()) {
                out.println("Unsupported Command!");
            }

            if ("hello".equals(command)) {
                out.println("<PID>" + uuid + "</PID>");
                return;
            }
            response.addHeader("Cache-Control","no-cache,no-store");

            debugLogger.debug("Client command = " + command);
            if ("handoff_start".equalsIgnoreCase(command)) {
                onHandoverStart(request, out);
            } else if ("handoff_stop".equalsIgnoreCase(command)) {
                onHandoverStop(request, out);
            } else if ("tierroam_start".equalsIgnoreCase(command)) {
            	onTierRoamStart(request, out);
            } else if ("tierroam_stop".equalsIgnoreCase(command)) {
            	onTierRoamStop(request, out);
            } else if ("refresh".equalsIgnoreCase(command)) {
                onRefresh(out);
            } else if (command.equalsIgnoreCase("set_attenuation") || command.equalsIgnoreCase(CMD_SET_MIMO)) {
                onSetAttenuration(request, out, command);
            } else if ("isconnected".equalsIgnoreCase(command)) {
                StringBuilder ret_buffer = onCheckConnection();
                out.println(ret_buffer);
            } else {
                debugLogger.warn("Unknown command [" + command + "]");
            }
        } catch (Exception e) {
            debugLogger.warn("doGet() failed to process request. ", e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            PrintWriter out = response.getWriter();
            user = (String) request.getSession().getAttribute("loginId");
            hardware = (String) request.getSession().getAttribute("hardware");
            if ((user == null) || (hardware == null)) {
                out.println("ERROR: invalid user session parameters.");
            }

            String command = request.getParameter("command");
            if ((command == null) || command.isEmpty()) {
                out.println("Unsupported Command!");
            } else if ("restart".equals(command)) {
                execute("restart.sh");
            } else {
                doGet(request, response);
            }
        } catch (Exception e) {
            debugLogger.warn("doPost() failed to process request. ", e);
        }
    }

    private StringBuilder onCheckConnection() {
        StringBuilder ret_buffer = new StringBuilder();
        if (cache == null) {
            return ret_buffer;
        }

        ret_buffer.append("<response>");
        ret_buffer.append("<state>");
        if (cache.getConnectionState(hardware)) {
            ret_buffer.append("connected.png");
        } else {
            ret_buffer.append("disconnected.png");
        }
        ret_buffer.append("</state>");
        String client_act = mgmtBean.isUpdate(user) ? Constants.RELOAD : Constants.OK;
        ret_buffer.append("<server_state>").append(client_act).append("</server_state>");

        MappingCompositeKey key = new MappingCompositeKey(user, hardware);
        AssignMapper assignMapper = mapper.get(key);

        if (assignMapper == null) {
            try {
                Assignment theAssignment = dbAccess.getAssignment(hardware, user);
                if (theAssignment.getRows() == null || theAssignment.getCols() == null) {
                    return ret_buffer;
                }
                assignMapper = new AssignMapper(theAssignment);
            } catch (SQLException e) {
                debugLogger.error(e);
            }
            mapper.put(key, assignMapper);
        }
        String offsettable = MatrixBuilder.offsetToXml(user, hardware, mgmtBean.isUpdate(user));
        ret_buffer.append(offsettable);
        ret_buffer.append("</response>");

        if (mgmtBean.isUpdate(user)) {
            mgmtBean.setUpdate(user, false);
        }
        return ret_buffer;
    }

    private void onHandoverStart(HttpServletRequest request, PrintWriter out) {
        debugLogger.info("Query String [" + request.getQueryString() + "]");
        String mimo = request.getParameter("mimo");

        String handoff_out1 = request.getParameter("handoff_out1");
        String handoff_out2 = request.getParameter("handoff_out2");
        String handoff_out3 = request.getParameter("handoff_out3");
        String handoff_out4 = request.getParameter("handoff_out4");

        String handoff_in1 = request.getParameter("handoff_in1");
        String handoff_in2 = request.getParameter("handoff_in2");
        String handoff_in3 = request.getParameter("handoff_in3");
        String handoff_in4 = request.getParameter("handoff_in4");

        String handoff_step = request.getParameter("handoff_step");
        String handoff_start = request.getParameter("handoff_start");
        String handoff_speed = request.getParameter("handoff_speed");
        String handoff_target = request.getParameter("handoff_target");

        String mm_pause = request.getParameter("mm_pause");
        String target_fadein = request.getParameter("target_fadein");

        StringBuffer buffer = new StringBuffer("\n====== HANDOFF START ======\n");
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
        buffer.append("mm_pause=" + mm_pause).append("\n");
        buffer.append("target_fadein=" + target_fadein).append("\n");

        debugLogger.info(buffer.toString());

        String[] handoff_out_list;
        String[] handoff_in_list;

        if (mimo.equalsIgnoreCase("No")) {
            handoff_out_list = new String[] { handoff_out1 };
            handoff_in_list = new String[] { handoff_in1 };
        } else if (mimo.contains("2X2")) {
            handoff_out_list = new String[] { handoff_out1, handoff_out2 };
            handoff_in_list = new String[] { handoff_in1, handoff_in2 };
        } else if (mimo.contains("4X4")) {
            handoff_out_list = new String[] { handoff_out1, handoff_out2, handoff_out3, handoff_out4 };
            handoff_in_list = new String[] { handoff_in1, handoff_in2, handoff_in3, handoff_in4 };
        } else {
            debugLogger.warn("Unsupported miom");
            return;
        }

        try {
            String[] mapped_handoff_out = MatrixMapper.mapping(hardware, user, handoff_out_list, false);
            String[] mapped_handoff_in = MatrixMapper.mapping(hardware, user, handoff_in_list, true);

            startHandoff(mimo, user, mapped_handoff_out, mapped_handoff_in, handoff_step,
                    handoff_speed, handoff_start, handoff_target, mm_pause);

            debugLogger.info("Handover commands sent out");
            out.println("Commands sent successfully. Handover started!");
        } catch (SQLException e) {
            debugLogger.warn(e);
            out.println("ERROR: " + e.getMessage());
        } catch (IllegalArgumentException e) {
        	debugLogger.error("failed to map intput/outout " + e.getMessage(), e);
            out.println("ERROR: " + e.getMessage());
        }  catch (Exception e) {
            debugLogger.warn(e);
            out.println("ERROR: " + e.getMessage());
        }
    }

    private void onHandoverStop(HttpServletRequest request, PrintWriter out) {
        String mimo = request.getParameter("mimo");

        String handoff_out1 = request.getParameter("handoff_out1");
        String handoff_out2 = request.getParameter("handoff_out2");
        String handoff_out3 = request.getParameter("handoff_out3");
        String handoff_out4 = request.getParameter("handoff_out4");

        StringBuffer buffer = new StringBuffer("====== HANDOFF STOP ======\n");
        buffer.append("mimo=" + mimo).append("\n");
        buffer.append("handoff_out1=" + handoff_out1).append("\n");
        buffer.append("handoff_out2=" + handoff_out2).append("\n");
        buffer.append("handoff_out3=" + handoff_out3).append("\n");
        buffer.append("handoff_out4=" + handoff_out4).append("\n");
        debugLogger.info(buffer.toString());

        String[] handoff_out_list;
        if (mimo.equalsIgnoreCase("No")) {
            handoff_out_list = new String[] { handoff_out1 };
        } else if (mimo.contains("2X2")) {
            handoff_out_list = new String[] { handoff_out1, handoff_out2 };
        } else if (mimo.contains("4X4")) {
            handoff_out_list = new String[] { handoff_out1, handoff_out2, handoff_out3, handoff_out4 };
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

    private void onTierRoamStart(HttpServletRequest request, PrintWriter out) {
        debugLogger.info("onTierRoamStart() Query String [" + request.getQueryString() + "]");

        String tierroam_out = request.getParameter("tierroam_out");
        String tierroam_in = request.getParameter("tierroam_in");

        String tierroam_step = request.getParameter("tierroam_step");
        String tierroam_start = request.getParameter("tierroam_start");
        String tierroam_speed = request.getParameter("tierroam_speed");
        String tierroam_end = request.getParameter("tierroam_end");
        String mm_pause = request.getParameter("mm_pause");

        StringBuffer buffer = new StringBuffer("====== Start Tier Roam ======\n");
        buffer.append("tierroam_out=" + tierroam_out).append("\n");
        buffer.append("tierroam_in=" + tierroam_in).append("\n");

        buffer.append("tierroam_step=" + tierroam_step).append("\n");
        buffer.append("tierroam_start=" + tierroam_start).append("\n");
        buffer.append("tierroam_speed=" + tierroam_speed).append("\n");
        buffer.append("tierroam_end=" + tierroam_end).append("\n");
        buffer.append("mm_pause=" + mm_pause).append("\n");

        debugLogger.info(buffer.toString());
        try {
            String mapped_tierroam_out = MatrixMapper.mapping(hardware, user, tierroam_out, false);
            String mapped_tierroam_in = MatrixMapper.mapping(hardware, user, tierroam_in, true);
            String command = buildCommand(CMD_TIER_ROAM, user, mapped_tierroam_out, mapped_tierroam_in, tierroam_step,
            		tierroam_speed, tierroam_start, tierroam_end, mm_pause);
            sendCommand(command);

            debugLogger.info("TierRoam command sent out");
            out.println("Command [" + command + "] sent successfully.");
        } catch (SQLException e) {
            debugLogger.warn(e);
            out.println("ERROR: " + e.getMessage());
        } catch (Exception e) {
            debugLogger.warn(e);
        }
    }

    private void onTierRoamStop(HttpServletRequest request, PrintWriter out) {
        debugLogger.info("onTierRoamStop() Query String [" + request.getQueryString() + "]");

        String tierroam_out = request.getParameter("tierroam_out");
        StringBuffer buffer = new StringBuffer("====== Stop Tier Roam ======\n");
        buffer.append("tierroam_out=" + tierroam_out).append("\n");
        debugLogger.info(buffer.toString());
        try {
            String mapped_tierroam_out = MatrixMapper.mapping(hardware, user, tierroam_out, false);
            String commandToServer = "stop_mobile " + mapped_tierroam_out + " : " + user;
            sendCommand(commandToServer);

            debugLogger.info("TierRoam command sent out");
            out.println("Command [" + commandToServer + "] sent successfully.");
        } catch (SQLException e) {
            debugLogger.warn(e);
            out.println("ERROR: " + e.getMessage());
        } catch (Exception e) {
            debugLogger.warn(e);
        }
    }

    private void onSetAttenuration(HttpServletRequest request, PrintWriter out, String command) {
        String outputs = request.getParameter("outputs");
        String inputs = request.getParameter("inputs");
        String value = request.getParameter("value");

        StringBuilder builder = new StringBuilder();
        builder.append("ACTION:: ").append(command).append(DELIMITER).append(outputs).append(", ")
                .append(inputs).append(", ").append(value);

        debugLogger.info(builder.toString());
        String c1 = "";
        String c2 = "";

        try {
            c1 = MatrixMapper.mapping(hardware, user, outputs, false);
            c2 = inputs.equals("0")? "0" : MatrixMapper.mapping(hardware, user, inputs, true);
        } catch (SQLException e1) {
            debugLogger.error(e1);
        }

        String[] cmdOut = new String[1];
        try {
            ObjectName mBeanName = new ObjectName(Constants.OBJECTMAME_PREFIX + hardware + Constants.OBJECTNAME_SUFFIX);
            String[] signature = { String.class.getName() };
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

            if (command.equalsIgnoreCase("set_attenuation")) {
                cmdOut[0] = "set " + c1 + DELIMITER + c2 + DELIMITER + value;
            } else {
                cmdOut[0] = CMD_SET_MIMO + DELIMITER + c1 + DELIMITER + c2 + DELIMITER + value;
            }
            mbs.invoke(mBeanName, "execute", cmdOut, signature);
            debugLogger.info("command " + cmdOut[0] + " send to " + mBeanName);
            // wait response
            int count = 0;
            Util.sleep(200);
            String serverResp = "";

            serverResponse.delete(0, serverResponse.length());
            while (count < MAX_WAIT_TIME) {
                serverResp = (String) mbs.getAttribute(mBeanName, "Data");
                if ((serverResponse != null) && serverResp.contains("OK")) {
                    debugLogger.debug(">>> response OK <<<");
                    break;
                }
                Util.sleep(200);
                count++;
            }

            debugLogger.debug("rfmaze server response = " + serverResp);
        } catch (InstanceNotFoundException e) {
            debugLogger.error(e.getMessage(), e);
        } catch (ReflectionException e) {
            debugLogger.error(e.getMessage(), e);
        } catch (MBeanException e) {
            debugLogger.error(e.getMessage(), e);
        } catch (MalformedObjectNameException e) {
            debugLogger.error(e.getMessage(), e);
        } catch (AttributeNotFoundException e) {
            debugLogger.error(e.getMessage(), e);
        }
    }

    private void onRefresh(PrintWriter out) {
    	debugLogger.debug("onRefresh()");
    	if (mgmtBean.isUpdate(user)) {
    		 out.println("<table class='matrix'></table>");
    		 debugLogger.warn("onRefresh() waiting for reload complete.");
    		 return;
    	}

        MappingCompositeKey key = new MappingCompositeKey(user, hardware);
        if (mapper == null || user == null || hardware == null) {
            return;
        }
        AssignMapper assignMapper = mapper.get(key);
        if (assignMapper == null) {
            try {
                assignMapper = new AssignMapper(dbAccess.getAssignment(hardware, user));
            } catch (SQLException e) {
                debugLogger.error(e);
            }
            mapper.put(key, assignMapper);
        }

        String matrixtable = MatrixBuilder.toXml(cache.getMatrix(hardware), user, hardware);
        out.println(matrixtable);
        if (matrixtable==null)
        {
        	debugLogger.debug("onRefresh() done. return 0 bytes.");
        }
        else
        {
        	debugLogger.debug("onRefresh() done. return " + matrixtable.length());
        }
        refreshLogger.debug("\n" + matrixtable);
    }

    private void startHandoff(String mimo, String userid, String[] outputs, String[] inputs,
            String stepDb, String stepTime, String startDb, String targetDb,
            String mmPause) {
        debugLogger.info("mimo selection is [" + mimo + "]");
        if ((!mimo.equalsIgnoreCase("No")) && (outputs[0].indexOf(',') != -1)) {
            debugLogger.warn("Invalid output - " + outputs[0]);
            return;
        }

        String mobiles = outputs[0];
        String cellsites = inputs[0];
        String command = buildCommand(CMD_HANDOVER, userid, mobiles, cellsites, stepDb, stepTime, startDb, targetDb, mmPause);
        debugLogger.debug("command=" + command);
        sendCommand(command);

        if (mimo.contains("2X2")) {
            if (outputs[1].indexOf(',') != -1) {
                debugLogger.warn("Invalid output for mimo 2x2 - " + outputs[1]);
                return;
            }

            if (outputs.length > 1) {
            	mobiles = outputs[1];
            	cellsites = inputs[1];
            	command = buildCommand(CMD_HANDOVER, userid, mobiles, cellsites, stepDb, stepTime, startDb, targetDb, mmPause);
            	sendCommand(command);
            }
        } else if (mimo.contains("4X4")) {
            if ((outputs[1].indexOf(',') != -1) || (outputs[2].indexOf(',') != -1)
                    || (outputs[3].indexOf(',') != -1)) {
                debugLogger.warn("Invalid outputs for mimo 4x4 - " + outputs[1] + " - "
                        + outputs[2] + " - " + outputs[3]);
                return;
            }

            if (outputs.length > 1) {
	            mobiles = outputs[1];
	            cellsites = inputs[1];
	            command = buildCommand(CMD_HANDOVER, userid, mobiles, cellsites, stepDb, stepTime, startDb, targetDb, mmPause);
	            debugLogger.debug("4x4-2, command=" + command);
	            sendCommand(command);
            }

            if (outputs.length > 2) {
	            mobiles = outputs[2];
	            cellsites = inputs[2];
	            command = buildCommand(CMD_HANDOVER, userid, mobiles, cellsites, stepDb, stepTime, startDb, targetDb, mmPause);
	            debugLogger.debug("4x4-3, command=" + command);
	            sendCommand(command);
            }

            if (outputs.length > 3) {
	            mobiles = outputs[3];
	            cellsites = inputs[3];
	            command = buildCommand(CMD_HANDOVER, userid, mobiles, cellsites, stepDb, stepTime, startDb, targetDb, mmPause);
	            debugLogger.debug("4x4-4, command=" + command);
	            sendCommand(command);
            }
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

    //handover [mobs] [cells] [step_db] [step_time] [start_db] [target_db] [min-max-pause]:userid
	private String buildCommand(String cmdString, String userid, String mobiles, String cellsites,
			String stepDb, String stepTime, String startDb, String targetDb, String mmPause) {
		StringBuilder command = new StringBuilder();
        command.append(cmdString).append(DELIMITER);
        command.append(mobiles).append(DELIMITER);
        command.append(cellsites).append(DELIMITER);
        command.append(stepDb).append(DELIMITER);
        command.append(stepTime).append(DELIMITER);
        command.append(startDb).append(DELIMITER);
        command.append(targetDb).append(DELIMITER);
        command.append(mmPause).append(DELIMITER);
        command.append(":").append(DELIMITER);
        command.append(userid);
		return command.toString();
	}

    private void sendCommand(String command) {
        debugLogger.info("RFMazeServlet::sendCommand() The command = [" + command + "]");
        String[] cmdOut = new String[1];
        try {
            ObjectName mBeanName = new ObjectName(Constants.OBJECTMAME_PREFIX + hardware + Constants.OBJECTNAME_SUFFIX);
            String[] signature = { String.class.getName() };
            cmdOut[0] = command;
            ManagementFactory.getPlatformMBeanServer().invoke(mBeanName, "execute", cmdOut, signature);
        } catch (Exception e) {
            debugLogger.error(e.getMessage());
        }
    }

    private void execute(String command) {
        debugLogger.info("Send command: [" + command + "]");
        MazeserverManagement.getInstance().runCommand(command);
    }
}
