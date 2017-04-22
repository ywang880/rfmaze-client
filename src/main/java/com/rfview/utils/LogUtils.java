package com.rfview.utils;

import java.util.Collection;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;


import com.rfview.Cell;
import com.rfview.maze.Entry;

public class LogUtils {

    public static void log(Logger logger, Level level, Collection<?> messages) {
        StringBuilder buffer = new StringBuilder();
        for (Object obj : messages) {
            buffer.append(obj).append("\n");
        }
        logger.log(level, buffer.toString());
    }

    public static void log(Logger logger, Level level, String name, Entry[][] matrix, int i, int j) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("\n============================  Entry[][] " + name + "  ================================\n");
        for (int x = 0; x < i; x++) {
            buffer.append("\n");
            for (int y = 0; y < j; y++) {
                String z = matrix[x][y] != null? String.format("%02d",  Integer.toString(matrix[x][y].getValue())) : "xx";
                buffer.append(z).append(", ");
            }
        }
        buffer.append("\n===============================================================\n");
        logger.log(level, buffer.toString());
    }
    
    public static void log(Logger logger, Level level, Cell[][] matrix, int i, int j) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("\n=============================  Cell[][]  ==================================\n");
        for (int x = 0; x < i; x++) {
            buffer.append("\n");
            for (int y = 0; y < j; y++) {
                String z = matrix[x][y] != null? String.format("%02d", matrix[x][y].getName()) : "xx";
                buffer.append(z).append(", ");
            }
        }
        buffer.append("\n===============================================================\n");
        logger.log(level, buffer.toString());
    }
    
    public static void log(Logger logger, Level level, List<?> listData) {
        StringBuilder buffer = new StringBuilder("\n");
        for (Object data : listData) {
            buffer.append(data).append(", ");
        }
        logger.log(level, buffer.toString());
    }
}
