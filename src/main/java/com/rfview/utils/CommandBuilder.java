package com.rfview.utils;

public class CommandBuilder {

    private static final String CMD_DELIMITER = " ";
    private static final String DEFAULT_ATTENUATION_VALUE = "63";
    private static final String CMD_SET = "set";

    public static String buildSetCommand(String row, String cols) {
        StringBuilder builder = new StringBuilder(CMD_SET);
        builder.append(CMD_DELIMITER);
        builder.append(cols);
        builder.append(CMD_DELIMITER);
        builder.append(row);
        builder.append(CMD_DELIMITER);
        builder.append(DEFAULT_ATTENUATION_VALUE);
        return builder.toString();
    }
}
