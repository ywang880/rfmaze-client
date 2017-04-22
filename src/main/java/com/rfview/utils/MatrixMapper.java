package com.rfview.utils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.rfview.conf.Assignment;

public class MatrixMapper {

    private static final Logger logger = Logger.getLogger(MatrixMapper.class.getName());
    private static final Map<CompositeKey, Map<String, String>>reverseRowMapCache = new  HashMap<CompositeKey, Map<String, String>>();
    private static final Map<CompositeKey, Map<String, String>>reverseColumnMapCache = new  HashMap<CompositeKey, Map<String, String>>();

    public static String mapping(String hardware, String user, String input,
            boolean matrix_row) throws SQLException {

        Assignment assignment = DbAccess.getInstance().getAssignment(hardware, user);
        String[] assigned = matrix_row ? assignment.getRows().split(",") : assignment.getCols().split(",");

        logger.debug("input = " + input);
        logger.debug("assigned = " +  (matrix_row ? assignment.getRows() : assignment.getCols()));

        StringBuilder buffer = new StringBuilder();
        String[] tokens = input.split(",");

        for (int i = 0; i < tokens.length; i++) {
            int idx = Integer.parseInt(tokens[i]) - 1;
            if (i == 0) {
                buffer.append(assigned[idx]);
            } else {
                buffer.append(",").append(assigned[idx]);
            }
        }

        return buffer.toString();
    }

    public static String[] mapping(String hardware, String user, String[] input, boolean matrix_row) throws SQLException, IllegalArgumentException {

        Assignment assignment = DbAccess.getInstance().getAssignment(hardware, user);
        String[] assigned = matrix_row ? assignment.getRows().split(",") : assignment.getCols().split(",");

        logger.info("assigned = " +  (matrix_row ? assignment.getRows() : assignment.getCols()));

        if (!matrix_row) {
            String[] output = new String[input.length];
            for (int i = 0; i < input.length; i++) {
                int idx = Integer.parseInt(input[i]) - 1;
                if ( idx < 0 || idx >= assigned.length) {
                	logger.error("mapping() output " + idx + " is not assigned.");
                	throw new IllegalArgumentException("output " + idx + " is not assigned.");
                }
                output[i] = assigned[idx];
            }
            return output;
        } else {
            String[] output = new String[input.length];
            StringBuffer sb;
            for (int i = 0; i < input.length; i++) {
                String tokens[] = input[i].split(",");
                sb = new StringBuffer();
                for (int k = 0; k < tokens.length; k++) {
                    int idx = Integer.parseInt(tokens[k]) - 1;
                    if ( idx < 0 || idx >= assigned.length) {
                    	logger.error("mapping() input " + idx + " is not assigned.");
                    	throw new IllegalArgumentException("input " + idx + " is not assigned.");
                    }
                    if (k==0) {
                        sb.append(assigned[idx]);
                    } else {
                        sb.append(",").append(assigned[idx]);
                    }
                }
                output[i] = sb.toString();
            }
            return output;
        }
    }

    public static synchronized String reverseRowMapping(String hardware, String user, String value) {
        CompositeKey key = CompositeKey.key(user, hardware);
        Map<String, String> reverseRowMapping = reverseRowMapCache.get(CompositeKey.key(user, hardware));
        if (reverseRowMapping == null) {
            Assignment assignement = null;
            try {
                assignement = DbAccess.getInstance().getAssignment(hardware, user);
            } catch (SQLException e) {
                logger.error(e);
            }
            if (assignement == null) {
                return null;
            }

            reverseRowMapping = new HashMap<String, String>();
            int i = 0;
            for (String s : assignement.getRows().split(",")) {
                reverseRowMapping.put(s, Integer.toString(i++));
            }

            reverseRowMapCache.put(key, reverseRowMapping);
        }

        return reverseRowMapping.get(value);
    }

    public static synchronized String reverseColumnMapping(String hardware, String user, String value) {
        CompositeKey key = CompositeKey.key(user, hardware);
        Map<String, String> reverseColumnMapping = reverseColumnMapCache.get(CompositeKey.key(user, hardware));
        if (reverseColumnMapping == null) {
            Assignment assignement = null;
            try {
                assignement = DbAccess.getInstance().getAssignment(hardware, user);
            } catch (SQLException e) {
                logger.error(e);
            }
            if (assignement == null) {
                return null;
            }

            reverseColumnMapping = new HashMap<String, String>();
            int i = 0;
            for (String s : assignement.getCols().split(",")) {
                reverseColumnMapping.put(s, Integer.toString(i++));
            }
            reverseColumnMapCache.put(key, reverseColumnMapping);
        }
        return reverseColumnMapping.get(value);
    }
}
