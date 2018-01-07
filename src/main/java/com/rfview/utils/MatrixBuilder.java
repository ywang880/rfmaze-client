package com.rfview.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

import com.rfview.Cell;
import com.rfview.conf.Assignment;
import com.rfview.conf.MatrixConfig;
import com.rfview.exceptions.InvalidConfigurationException;
import com.rfview.maze.Datagrid;
import com.rfview.maze.Entry;
import com.rfview.utils.db.DbAccess;

final public class MatrixBuilder {

    private static final int DEFAULT_OFFSET = -99;
    private static final Logger debugLogger = Logger.getLogger(MatrixBuilder.class.getName());
    private static final Datagrid cache = Datagrid.getInstance();
    private static final DbAccess dbAccess = DbAccess.getInstance();
    private static final Map<String, Boolean> types = new HashMap<String, Boolean>();
    private static final Set<String> topYoung = new HashSet<>();
    private static final Set<String> trueQRB = new HashSet<>();
    private static final Map<CompositeKey, Assignment> assignments = new HashMap<CompositeKey, Assignment>();

    public static String toXml(Cell[][] matrix) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<table>");
        for (Cell[] cc : matrix) {
            buffer.append("  <tr>");
            for (Cell c : cc) {
                buffer.append(" <td>");
                buffer.append("   <v>").append(c.getName()).append("</v>");
                buffer.append("   <c>").append(c.getBgcolor()).append("</c>");
                buffer.append(" </td>");
            }
            buffer.append("  </tr>");
        }
        buffer.append("</table>");
        return buffer.toString();
    }

    public static String toXml(Entry[][] matrix, String user, String hardware) {
        final StringBuffer resp = new StringBuffer();
        resp.append("<table class='matrix'>");

        final boolean isQRB = isTypeOfQRB(hardware);
        Assignment assignment;
        try {
            CompositeKey key = CompositeKey.key(user, hardware);
            assignment = assignments.get(key);
            if (assignment == null) {
                assignment = dbAccess.getAssignment(hardware, user);
                assignments.put(key, assignment);
            }

            String[] assignedColumns = assignment.getCols().split(",");
            if (!isQRB) {
                resp.append("<thead>");
                String [] attens = cache.getAttenuation(hardware);
                for (String c : assignedColumns) {
                    resp.append("<th>");
                    if (attens != null && attens.length > 0) {
                        try {
                            int cc = Integer.parseInt(c);
                            int attenVal = Integer.parseInt(attens[cc-1]);
                            resp.append("<v>").append(attens[cc-1]).append("</v>");
                            String color = ColorMapping.mapping(user, attenVal);
                            resp.append("<c>").append(color).append("</c>");
                        } catch (Exception e) {}
                    }
                    resp.append("</th>");
                }
                resp.append("</thead>");
            }

            Entry[] items = cache.getOffset(hardware);
            for (String r : assignment.getRows().split(",")) {
                resp.append("<tr>");
                int rr = Integer.parseInt(r);
                resp.append("<td>");
                int offsetVal=DEFAULT_OFFSET;
                if (items!=null && items.length>rr-1 && items[(rr-1)] != null) {
                    offsetVal = items[rr-1].getValue();
                }
                resp.append("<v>").append(offsetVal+"dBm").append("</v>");
                if (isQRB) {
                    resp.append("<c>#688DB2</c>");
                }
                resp.append("</td>");

                for (String c : assignment.getCols().split(",")) {
                    int cc = Integer.parseInt(c);
                    int matrixVal = matrix[rr-1][cc-1].getValue();
                    String color = isTopYoungLikeQRB(hardware)? ColorMapping.mapping(user, true, matrixVal) : ColorMapping.mapping(user, matrixVal);
                    resp.append("<td>");
                    resp.append("<v>").append(matrixVal).append("</v>");
                    if (isQRB) {
                        resp.append("<c>").append(color).append("</c>");
                    }
                    resp.append("</td>");
                }
                resp.append("</tr>");
            }
        } catch (Exception e) {
            debugLogger.error("Caught exception when build xml file", e);
        }
        resp.append("</table>");
        return resp.toString();
    }

    private static boolean isTypeOfQRB(String hardware) {
        if (!types.containsKey(hardware)) {
            try {
                types.put(hardware, Boolean.valueOf(MatrixConfig.getInstance().getServerInfo(hardware).isQRB()));
            } catch (InvalidConfigurationException e) {
                debugLogger.error("Invalid configuration");
            }
        }
        return Boolean.TRUE.equals(types.get(hardware));
    }

    private static boolean isTopYoungLikeQRB(String hardware) {

    	if ( !isTypeOfQRB(hardware) || trueQRB.contains(hardware) ) {
    		return Boolean.FALSE.booleanValue();
    	}

        if ( !topYoung.contains(hardware) && !trueQRB.contains(hardware) ) {
            try {
                Properties props = MatrixConfig.getInstance().getConfiguration(hardware);
            	int inputs = Integer.parseInt(props.getProperty("matrix_inputs", "0"));
            	int outputs = Integer.parseInt(props.getProperty("matrix_outputs", "0"));
            	if  ( inputs == 1 && outputs == 8 ) {
            		topYoung.add(hardware);

            	} else {
            		trueQRB.add(hardware);
            	}
            } catch (InvalidConfigurationException e) {
                debugLogger.error("Invalid configuration");
            }
        }

        if ( topYoung.contains(hardware) ) {
        	return Boolean.TRUE.booleanValue();
        }

        return false;
    }

    public static String toFullXml(Entry[][] matrix, String user, String hardware) {
        final StringBuffer resp = new StringBuffer();
        final boolean isQRB = isTypeOfQRB(hardware);
        resp.append("<table class='matrix'>");

    	if ( isLTE(hardware) ) {
    		String[] atten = cache.getAttenuation(hardware);
    		resp.append("<tr>");
    		for (String attenValue : atten) {
    			resp.append("<td>");
    			resp.append("<v>").append(attenValue).append("</v>");
    			int val = Integer.parseInt(attenValue);
    			String color = (val == 31)? "#DD0000" : ColorMapping.mapping(user, val);
    			resp.append("<c>").append(color).append("</c>");
    			resp.append("</td>");
    		}
    		resp.append("</tr>");
    	}

        try {
            Entry[] items = cache.getOffset(hardware);
            for (int i = 0; i < matrix.length; i++) {
                resp.append("<tr>");
                resp.append("<td>");
                int offsetVal=DEFAULT_OFFSET;
                if ((items != null) && (items.length > matrix.length - 1) && (items[i] != null)) {
                    offsetVal = items[i].getValue();
                }
                resp.append("<v>").append(offsetVal+"dBm").append("</v>");
                if (isQRB) {
                    resp.append("<c>#688DB2</c>");
                }
                resp.append("</td>");

                for (int j = 0; j < matrix[0].length; j++) {
                    int matrixVal = matrix[i][j].getValue();
                    String color = ColorMapping.mapping(user, matrixVal);
                    resp.append("<td>");
                    resp.append("<v>").append(matrixVal).append("</v>");
                    if (isQRB) {
                        resp.append("<c>").append(color).append("</c>");
                    }
                    resp.append("</td>");
                }
                resp.append("</tr>");
            }
        } catch (Exception e) {
            debugLogger.error("Caught exception when build xml file", e);
        }
        resp.append("</table>");
        return resp.toString();
    }

    public static String offsetToXml(String user, String hardware, boolean reload) {
        StringBuffer resp = new StringBuffer();
        resp.append("<table>");
        Assignment assignment = null;
        try {
            CompositeKey key = CompositeKey.key(user, hardware);
            if (reload) {
                assignments.remove(key);
            } else {
                assignment = assignments.get(key);
            }
            if (assignment == null) {
                assignment = dbAccess.getAssignment(hardware, user);
                assignments.put(key, assignment);
            }

            Entry[] items = cache.getOffset(hardware);
            for (String r : assignment.getRows().split(",")) {
                resp.append("<tr>");
                int rr = Integer.parseInt(r);
                resp.append("<td>");
                int offsetVal=DEFAULT_OFFSET;
                if (items!=null && items.length>rr-1 && items[(rr-1)] != null) {
                    offsetVal = items[rr-1].getValue();
                }
                resp.append("<v>").append(offsetVal+"dBm").append("</v>");
                resp.append("</td>");
                resp.append("</tr>");
            }
        } catch (Exception e) {
            debugLogger.error("Caught exception when build xml file", e);
        }
        resp.append("</table>");
        return resp.toString();
    }

    public static String offsetToXml(String hardware, boolean reload) {
        StringBuffer resp = new StringBuffer();
        resp.append("<table>");
        try {
            Entry[] items = cache.getOffset(hardware);
            if (items!=null) {
                for (Entry item : items) {
                    resp.append("<tr>");
                    resp.append("<td>");
                    int offsetVal=DEFAULT_OFFSET;
                    if (item!=null) {
                        offsetVal = item.getValue();
                    }
                    resp.append("<v>").append(offsetVal+"dBm").append("</v>");
                    resp.append("</td>");
                    resp.append("</tr>");
                }
            }
        } catch (Exception e) {
            debugLogger.error("Caught exception when build xml file", e);
        }
        resp.append("</table>");
        return resp.toString();
    }

    private static boolean isLTE(String hardware) {
        try {
            return MatrixConfig.getInstance().getServerInfo(hardware).isLTE();
        } catch (InvalidConfigurationException e) {
        }
        return false;
    }
}
