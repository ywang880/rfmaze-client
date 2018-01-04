package com.rfview.conf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.rfview.exceptions.InvalidConfigurationException;
import com.rfview.management.MazeserverManagement;

public class BroadcastConf {

    private static final int DEFAULT_BROADCAST_PORT = 29000;
    private final String name = "RFMAZE_LIST_BROADCAST";
    private String bsIp;
    private int bsPort = DEFAULT_BROADCAST_PORT;
    private Logger logger = Logger.getLogger(BroadcastConf.class.getName());
    private static final String CONF_FILENAME = MazeserverManagement.getInstance().getConfigureDir() + File.separator + "rfmaze_broadcast.cfg";
    private static BroadcastConf instance = new BroadcastConf();

    public static BroadcastConf getInstance() {
        return instance;
    }

    private BroadcastConf() {
    }

    public String getName() {
        return name;
    }

    public String getBsIp() {
        return getLocalHost();
    }

    public void setBsIp(String bsIp) {
        this.bsIp = bsIp;
    }

    public int getBsPort() {
        return bsPort;
    }

    public void setBsPort(int bsPort) {
        this.bsPort = bsPort;
    }

    public boolean exists() {
        File f = new File(CONF_FILENAME);
        return f.exists();
    }

    public List<String> getAllAssignedServers() {
        BufferedReader br=null;
        List<String> result = new ArrayList<String>();
        File f = new File(CONF_FILENAME);
        try {
            br = new BufferedReader(new FileReader(f));
            String line = br.readLine();
            while (line!=null) {
                String data = line.trim();
                if (data.startsWith("#") || data.startsWith("=")) {
                    line = br.readLine();
                    continue;
                }
                if (data.matches("RFMAZE\\s*=\\s*.*\\d$")) {
                    String[] tokens = data.split(" ");
                    if (!tokens[2].trim().equals("RFMAZE_LIST")) {
                        result.add(tokens[2].trim());
                    }
                }
                line = br.readLine();
            }
        } catch (FileNotFoundException e) {
            logger.error("File not found", e);
        } catch (IOException e) {
            logger.error("IOException ", e);
        } finally {
            if (br!=null) {
                try {
                    br.close();
                } catch (IOException e) {
                    logger.error("IOException", e);
                }
            }
        }
        return result;
    }

    public String removeAssignedMazeServer(String name) {
        logger.info("remove RF maze server "+ name);
        BufferedReader br=null;
        StringBuilder buffer = new StringBuilder();
        PrintWriter writer = null;

        File f = new File(CONF_FILENAME);
        try {
            br = new BufferedReader(new FileReader(f));
            String line = br.readLine();
            while (line!=null) {
                String data = line.trim();
                if (data.contains(name)) {
                    line = br.readLine();
                    continue;
                }
                buffer.append(data).append("\n");
                line = br.readLine();
            }
            writer = new PrintWriter(f);
            writer.write(buffer.toString());
            writer.flush();
            writer.close();

            MazeserverManagement.getInstance().deleteLogFiles(name);
            return name + " is deleted";
        } catch (FileNotFoundException e) {
            logger.error("File not found", e);
        } catch (IOException e) {
            logger.error("IOException", e);
        } finally {
            if (br!=null) {
                try {
                    br.close();
                } catch (IOException e) {
                	logger.error("IOException", e);
                }
            }
            if (writer!=null) {
                writer.close();
            }
        }
        return null;
    }


    public String assignMazeServer(String name, String ip, int port) {
        BufferedReader br=null;
        StringBuilder buffer = new StringBuilder();
        PrintWriter writer = null;

        File f = new File(CONF_FILENAME);
        try {
            br = new BufferedReader(new FileReader(f));
            String line = br.readLine();
            while (line!=null) {
                String data = line.trim();
                buffer.append(data).append("\n");
                line = br.readLine();
            }

            if (buffer.indexOf(name) != -1) {
                return name + " is already exits";
            }
            logger.info(buffer.toString());

            int insertPostion = buffer.indexOf("### End of regular maze sever ###");
            buffer.insert(insertPostion, "RFMAZE = " + name + " " + ip + " " + port + "\n");
            writer = new PrintWriter(f);
            writer.write(buffer.toString());
            writer.flush();
            writer.close();
            return name + " is created successfully.";
        } catch (FileNotFoundException e) {
            logger.error("File not found", e);
        } catch (IOException e) {
        	logger.error("IOException", e);
        } finally {
            if (br!=null) {
                try {
                    br.close();
                } catch (IOException e) {
                	logger.error("IOException", e);
                }
            }
            if (writer!=null) {
                writer.close();
            }
        }

        return null;
    }

    public void writeToFile() {
        File f = new File(CONF_FILENAME);
        if (!f.exists()) {
            PrintWriter writer;
            try {
                writer = new PrintWriter(f);
                bsIp = getLocalHost();
                bsPort = DEFAULT_BROADCAST_PORT;
                writer.write("### This is an RFMAZE configuration file. Format should not be changed ###\n");
                writer.write("MATRIX_NAME = RFMAZE_LIST_BROADCAST \n");
                writer.write("RFMAZE = RFMAZE_LIST " + bsIp + " " + bsPort + "\n");
                writer.write("matrix_type = V\n");
                writer.write("### Begin of regular maze sever ###\n");
                writer.write("### End of regular maze sever ###\n");
                writer.write("server_socket_port = " + bsPort +"\n");
                writer.write("EOF = Mandatory\n");
                writer.flush();
                writer.close();
            } catch (FileNotFoundException e) {
                logger.error("File not found", e);
            }
        } else {
            BufferedReader br=null;
            try {
                br = new BufferedReader(new FileReader(f));
                String line = br.readLine();
                while (line!=null) {
                    String data = line.trim();
                    if (data.startsWith("#") || data.startsWith("=")) {
                        line = br.readLine();
                        continue;
                    }

                    if (data.startsWith("RFMAZE")) {
                        String value = data.split("=")[1].trim();
                        if (value.startsWith("RFMAZE_LIST")) {
                            String[] tokens = value.split(" ");
                            bsIp = tokens[1];
                            bsPort = Integer.parseInt(tokens[2]);
                        }
                    }
                    line = br.readLine();
                }
            } catch (FileNotFoundException e) {
                logger.error("File not found", e);
            } catch (IOException e) {
            	logger.error("IOException", e);
            } finally {
                if (br!=null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                    	logger.error("IOException", e);
                    }
                }
            }
        }
    }

    public void changePort(int newport) throws FileNotFoundException, IOException {
        StringBuilder buffer = new StringBuilder();
        File f = new File(CONF_FILENAME);
        BufferedReader br = new BufferedReader(new FileReader(f));
        String line = br.readLine();
        while (line!=null) {
            String data = line.trim();
            if (data.matches("RFMAZE\\s*=\\s*RFMAZE_LIST.*")) {
                buffer.append("RFMAZE = RFMAZE_LIST " + getLocalHost() + " " + newport).append("\n");
            } else if (data.matches("server_socket_port\\s*=.*")) {
                buffer.append("server_socket_port = " + getLocalHost() + " " + newport).append("\n");
            } else {
                buffer.append(data).append("\n");
            }
            line = br.readLine();
        }
        writeToFile(buffer);
        br.close();
    }

    public List<String> getHardwareList() {
        return getAllAssignedServers();
    }

    public List<String> getNonSwitchHardwareList() {
        List<String> all = getAllAssignedServers();
        if (all == null) {
            return new ArrayList<String>();
        }

        List<String> theList = new ArrayList<String>();
        MatrixConfig mc = MatrixConfig.getInstance();
        for (String s : all) {
            try {
                if (!mc.getServerInfo(s).isQRB() && !mc.getServerInfo(s).isTopYoung() && !mc.getServerInfo(s).isTurnTable()) {
                    continue;
                }
            } catch (InvalidConfigurationException e) {}
            theList.add(s);
        }
        return theList;
    }

    private void writeToFile(StringBuilder data) throws FileNotFoundException {
        File f = new File(CONF_FILENAME);
        PrintWriter writer = new PrintWriter(f);
        writer.write(data.toString());
        writer.flush();
        writer.close();
    }

    public String getLocalHost() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (IOException e) {
            return "127.0.0.1";
        }
    }
}
