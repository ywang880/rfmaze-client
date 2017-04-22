package com.rfview.utils;

import java.sql.*;
import java.text.DecimalFormat;

import org.apache.log4j.Logger;

import com.rfview.conf.Assignment;
import com.rfview.maze.Entry;

public class Util {

    public static void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
        }
    }

    public static boolean isOutputsAssigned(String hardware, String user) {
        DbAccess dbAccess = DbAccess.getInstance();
        try {
            Assignment a = dbAccess.getAssignment(hardware, user);
            if (a == null) {
                return false;
            }
            return (a.isRowsEmpty() && a.isColsEmpty());
        } catch (SQLException e) {
        }
        return false;
    }

    public static boolean isInputsAssigned(String hardware, String user) {
        DbAccess dbAccess = DbAccess.getInstance();
        try {

            Assignment a = dbAccess.getAssignment(hardware, user);
            if (a == null) {
                return false;
            }
            return ((a.getCols() != null) && (!a.getCols().isEmpty()));
        } catch (SQLException e) {
        }
        return false;
    }

    public static boolean isBlank(String value) {
        if (value == null) {
            return true;
        }

        return value.isEmpty();
    }

    // Convert string to number
    public static int convertDigit(String character) {
        int value = 0;

        if (character.equals("1")) {
            value = 1;
        } else if (character.equals("2")) {
            value = 2;
        } else if (character.equals("3")) {
            value = 3;
        } else if (character.equals("4")) {
            value = 4;
        } else if (character.equals("5")) {
            value = 5;
        } else if (character.equals("6")) {
            value = 6;
        } else if (character.equals("7")) {
            value = 7;
        } else if (character.equals("8")) {
            value = 8;
        } else if (character.equals("9")) {
            value = 9;
        } else if (character.equals("a")) {
            value = 10;
        } else if (character.equals("b")) {
            value = 11;
        } else if (character.equals("c")) {
            value = 12;
        } else if (character.equals("d")) {
            value = 13;
        } else if (character.equals("e")) {
            value = 14;
        } else if (character.equals("f")) {
            value = 15;
        } else if (character.equals("g")) {
            value = 16;
        } else if (character.equals("h")) {
            value = 17;
        } else if (character.equals("i")) {
            value = 18;
        } else if (character.equals("j")) {
            value = 19;
        } else if (character.equals("k")) {
            value = 20;
        } else if (character.equals("l")) {
            value = 21;
        } else if (character.equals("m")) {
            value = 22;
        } else if (character.equals("n")) {
            value = 23;
        } else if (character.equals("o")) {
            value = 24;
        } else if (character.equals("p")) {
            value = 25;
        } else if (character.equals("q")) {
            value = 26;
        } else if (character.equals("r")) {
            value = 27;
        } else if (character.equals("s")) {
            value = 28;
        } else if (character.equals("t")) {
            value = 29;
        } else if (character.equals("u")) {
            value = 30;
        } else if (character.equals("v")) {
            value = 31;
        } else if (character.equals("w")) {
            value = 32;
        } else if (character.equals("x")) {
            value = 33;
        } else if (character.equals("y")) {
            value = 34;
        } else if (character.equals("z")) {
            value = 35;
        } else if (character.equals("A")) {
            value = 36;
        } else if (character.equals("B")) {
            value = 37;
        } else if (character.equals("C")) {
            value = 38;
        } else if (character.equals("D")) {
            value = 39;
        } else if (character.equals("E")) {
            value = 40;
        } else if (character.equals("F")) {
            value = 41;
        } else if (character.equals("G")) {
            value = 42;
        } else if (character.equals("H")) {
            value = 43;
        } else if (character.equals("I")) {
            value = 44;
        } else if (character.equals("J")) {
            value = 45;
        } else if (character.equals("K")) {
            value = 46;
        } else if (character.equals("L")) {
            value = 47;
        } else if (character.equals("M")) {
            value = 48;
        } else if (character.equals("N")) {
            value = 49;
        } else if (character.equals("O")) {
            value = 50;
        } else if (character.equals("P")) {
            value = 51;
        } else if (character.equals("Q")) {
            value = 52;
        } else if (character.equals("R")) {
            value = 53;
        } else if (character.equals("S")) {
            value = 54;
        } else if (character.equals("T")) {
            value = 55;
        } else if (character.equals("U")) {
            value = 56;
        } else if (character.equals("V")) {
            value = 57;
        } else if (character.equals("W")) {
            value = 58;
        } else if (character.equals("X")) {
            value = 59;
        } else if (character.equals("Y")) {
            value = 60;
        } else if (character.equals("Z")) {
            value = 61;
        } else if (character.equals("!")) {
            value = 62;
        } else if (character.equals("@")) {
            value = 63;
        } else if (character.equals("#")) {
            value = 64;
        } else {
            value = 100;
        }

        return value;
    }

    public static void logMatrix(Logger logger, Entry[][] matrix) {
        StringBuilder buffer = new StringBuilder("\n");
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                buffer.append(matrix[i][j]).append(", ");
            }
            buffer.append("\n");
        }
        logger.info(buffer.toString());
    }

    public void dumpMatrix(Logger logger, Entry[][] entries) {
        StringBuilder sb = new StringBuilder();
        for (Entry[] el : entries) {
            for (Entry e : el) {
                sb.append(new DecimalFormat("00").format(e.getValue())).append(",");
            }
            sb.append("\n");
        }
        logger.info(sb.toString());
    }

    public static void main(String args[]) {

        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:/var/lib/dbs/rfmaze.db");
            // Statement s = c.createStatement();
            // ResultSet rs = s.executeQuery("SELECT * FROM matrix_labels");
            // while (rs.next()) {
            // System.out.println(rs.getString(2));
            // }
            //
            String SQL = "INSERT INTO matrix_labels values (?,?,?,?,?)";
            PreparedStatement stat = null;

            conn = DriverManager.getConnection("jdbc:sqlite:/var/lib/dbs/rfmaze.db");
            stat = conn.prepareStatement(SQL);

            for (int i = 0; i < 32; i++) {
                stat.setInt(1, i + 1);
                stat.setString(2, "testing");
                stat.setString(3, "input");
                stat.setString(4, "BTS" + (i + 1));
                stat.setString(5, "Mobile" + (i + 1));

                stat.addBatch();
            }

            for (int i = 0; i < 32; i++) {
                stat.setInt(1, i + 1);
                stat.setString(2, "input");
                stat.setString(3, "output");
                stat.setString(4, "chamber" + (i + 1));
                stat.setString(5, "chamber" + (i + 1));
                stat.addBatch();

            }

            stat.executeBatch();

            stat.close();
            conn.close();
            // rs.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Opened database successfully");
    }
}
