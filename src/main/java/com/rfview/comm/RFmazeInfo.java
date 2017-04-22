package com.rfview.comm;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.rfview.maze.Datagrid;
import com.rfview.utils.Util;

public class RFmazeInfo {

    private static final int HALF_SECOND = 500;
    private static final int MAX_RETRIES = 30;
    private static final int ONE_SECOND = 1000;
    private static final Charset charset = Charset.forName("UTF-8");
    private static final CharsetDecoder decoder = charset.newDecoder();
    private static SocketChannel socketChannel = null;
    private static final ByteBuffer sendBuffer = ByteBuffer.allocate(64 * 1024);
    private static final ByteBuffer receiveBuffer = ByteBuffer.allocate(128 * 1024);
    private static Selector selector;
    private boolean isSend = false;
    private boolean done = false;
    private int send = 0;
    static int compteBuf = 0;
    private String type = "";
    private String title = "";
    private int cellNumber = 0;
    private int mobNumber = 0;
    private String[] cellN1;
    private String[] cellN2;
    private String[] mobN1;
    private String[] mobN2;
    private String[] offV;
    private String[] offU;
    private String[][] attV;
    private String[][] attU;
    private String data = "";
    private String[] information;
    private static final int delay = 250000;
    private double maxattn = 127.0;
    private double minattn = 0.0;

    private static final Logger logger = Logger.getLogger(RFmazeInfo.class.getName());
    private final Datagrid cache = Datagrid.getInstance();
    
    public RFmazeInfo() {
    }
    
    public RFmazeInfo(Selector select, SocketChannel sock) throws IOException {
        socketChannel = sock;
        selector = select;
    }

    // Create a non-blocking socket channel
    public static SocketChannel createSocketChannel(String hostName, int port)
            throws IOException {
        SocketChannel client = SocketChannel.open();
        client.configureBlocking(false);
        client.connect(new InetSocketAddress(hostName, port));
        return client;
    }

    // Detect comment section for a cellName line
    public String endCellName(String str) {
        int end = 0;
        int nbCnt = 0;
        String name = "";

        for (int i = 0; i < str.length(); i++) {
            char n = str.charAt(i);
            if (Character.toString(n).equals("#")) {
                end = i;
                i = str.length();
                nbCnt++;
            }

            if (i == str.length() - 1 && nbCnt == 0) {
                end = str.length();
            }
        }
        name = str.substring(0, end);

        return name;
    }

    // return cellName Label1
    public String[] getCellLabel1(String[] infoResponse, int cellNumber) {
        String[] cellLabel1 = new String[cellNumber];

        for (String info : infoResponse) {
            if ((info == null) || info.trim().isEmpty()) {
                continue;
            }

            if (info.substring(0, 3).equals("EOF")) {
                break;
            }

            int pos = info.indexOf("_");
            if ((info.length() > 9)
                    && ((info.substring(0, 5).equals("INPUT")) && ((pos < 0) || (pos > 8)))) {
                int pos1 = info.indexOf(" =");
                String cellNo = info.substring(5, pos1);
                try {
                    if (Integer.parseInt(cellNo) > cellNumber) {
                        break;
                    }
                } catch (NumberFormatException e) {
                    logger.debug("Invalid cell number " + cellNo);
                    continue;
                }

                String cellN = info.substring(9, info.length());
                String realCellN = endCellName(cellN);
                realCellN = realCellN.trim();
                realCellN = realCellN.replace("\n", "");
                if (Integer.parseInt(cellNo) - 1 < cellNumber) {
                    cellLabel1[Integer.parseInt(cellNo) - 1] = realCellN;
                }
            }
        }
        return cellLabel1;
    }

    // Return a table that containt position and String information
    // for all cell that have RFMAZE to launch (used for q type)
    public String[] getCellLaunch(String[] information) {
        String[] cellL = new String[2];
        try {
            int compteL = 0;
            for (int i = 0; i < information.length - 5; i++) {
                if (information[i].length() >= 21
                        && information[i].substring(0, 5).equals("INPUT")
                        && information[i].substring(5, 19).equals("_RFMAZE_LAUNCH")) {
                    compteL++;
                    String cellNo = information[i].substring(4, 5);
                    if (cellNo.equals("1") || cellNo.equals("2")
                            || cellNo.equals("3") || cellNo.equals("4")
                            || cellNo.equals("5") || cellNo.equals("6")
                            || cellNo.equals("7") || cellNo.equals("8")
                            || cellNo.equals("9")) {
                    } else {
                        int cell_no = Util.convertDigit(cellNo);
                        information[cell_no - 1] = information[i].substring(21,
                                information[i].length());
                    }
                }
            }

            cellL = new String[2 * compteL];
            int compte = 0;

            for (int i = 0; i < information.length - 5; i++) {
                if (information[i].length() >= 21
                        && information[i].substring(0, 4).equals("CELL")
                        && information[i].substring(5, 19).equals(
                                "_RFMAZE_LAUNCH")) {

                    String cellNo = information[i].substring(4, 5);
                    if (cellNo.equals("1") || cellNo.equals("2")
                            || cellNo.equals("3") || cellNo.equals("4")
                            || cellNo.equals("5") || cellNo.equals("6")
                            || cellNo.equals("7") || cellNo.equals("8")
                            || cellNo.equals("9")) {
                        String cellN = information[i].substring(21,
                                information[i].length());
                        cellL[compte] = cellNo;
                        cellL[compte + 1] = cellN;
                    } else {
                        int cell_no = Util.convertDigit(cellNo);
                        String cellN = information[i].substring(21,
                                information[i].length());
                        cellL[compte] = cellNo;
                        cellL[compte + 1] = cellN;
                        information[cell_no - 1] = information[i].substring(21,
                                information[i].length());
                    }
                    compte = compte + 2;
                }
            }
            return cellL;
        } catch (NullPointerException e) {
        }
        return cellL;
    }

    public String[] getMobLabel1(String[] infoResponse, int mobNumber) {
        String[] mobLabel1 = new String[mobNumber];

        for (String info : infoResponse) {

            if ((info == null) || info.trim().isEmpty()) {
                continue;
            }
            if (info.substring(0, 3).equals("EOF")) {
                break;
            }

            int pos = info.indexOf("_");
            if ((info.length() > 12)
                    && ((info.substring(0, 6).equals("OUTPUT")) && ((pos > 10) || (pos < 0)))) {
                int pos1 = info.indexOf(" =");
                String mobNo = info.substring(6, pos1);

                if (Integer.parseInt(mobNo) > mobNumber) {
                    break;
                }

                pos1 = info.indexOf("=") + 1;
                String mobN = info.substring(pos1 + 1, info.length());
                String realMobN = endCellName(mobN);
                realMobN = realMobN.trim();
                realMobN = realMobN.replace("\n", "");

                logger.debug("MOB_Label1: mobNo=" + mobNo
                        + ", real mobNo=" + realMobN);
                if (Integer.parseInt(mobNo) - 1 < mobNumber) {
                    mobLabel1[Integer.parseInt(mobNo) - 1] = realMobN;
                }
            }
        }
        return mobLabel1;
    }

    // Return mobileName label2

    public String[] getMobLabel2(String[] infoResponse, int mobNumber) {
        String[] mobLabel2 = new String[mobNumber];

        for (String info : infoResponse) {

            if ((info == null) || info.trim().isEmpty()) {
                continue;
            }

            if (info.substring(0, 3).equals("EOF")) {
                break;
            }

            int pos = info.indexOf("_INFO");
            if ((info.length() > 16)
                    && (info.substring(0, 6).equals("OUTPUT"))
                    && ((info.substring(7, 12).equals("_INFO")) || (info
                            .substring(8, 13).equals("_INFO")))) {
                String mobNo = info.substring(6, pos);
                if (Integer.parseInt(mobNo) > mobNumber) {
                    break;
                }

                int pos1 = info.indexOf("=") + 1;
                String mobN = info.substring(pos1 + 1, info.length());
                String realMobN = endCellName(mobN);
                realMobN = realMobN.trim();
                realMobN = realMobN.replace("\n", "");

                logger.debug("MOB_Label2: mobNo=" + mobNo
                        + ", real mobNo=" + realMobN);

                if (Integer.parseInt(mobNo) - 1 < mobNumber) {
                    mobLabel2[Integer.parseInt(mobNo) - 1] = realMobN;
                }
            }
        }
        return mobLabel2;
    }

    // Return cellNameL Label2
    public String[] getCellLabel2(String[] infoResponse, int cellNumber) {
        String[] cellLabel2 = new String[cellNumber];
        for (String info : infoResponse) {
            if ((info == null) || info.trim().isEmpty()) {
                continue;
            }

            if (info.substring(0, 3).equals("EOF")) {
                break;
            }

            int pos = info.indexOf("_INFO");
            if ((info.length() > 13) && (info.substring(0, 5).equals("INPUT"))
                    && (!info.substring(7, 13).equals("RFMAZE")) && (pos > -1)) {
                String cellNo = info.substring(5, pos);
                try {
                    if (Integer.parseInt(cellNo) > cellNumber) {
                        break;
                    }
                } catch (NumberFormatException e) {
                    logger.debug("Invalid cell number " + cellNo);
                    continue;
                }

                String cellN = info.substring(14, info.length());
                String realCellN = endCellName(cellN);
                realCellN = realCellN.trim();
                realCellN = realCellN.replace("\n", "");
                if (Integer.parseInt(cellNo) - 1 < cellNumber) {
                    cellLabel2[Integer.parseInt(cellNo) - 1] = realCellN;
                }
            }
        }
        return cellLabel2;
    }

    // Set variable by readinf the information table
    public void setParameters(String[] information) {
        for (int i = 0; i < information.length; i++) {
            try {
                // Read RFMAZE type
                if (information[i].indexOf("matrix_type") != -1) {
                    int begin = information[i].indexOf("matrix_type");
                    type = information[i].substring(begin + 14, begin + 15);
                }
                // Read RFMAZE Name
                if (information[i].indexOf("MATRIX_NAME") != -1) {
                    int begin = information[i].indexOf("MATRIX_NAME");
                    title = information[i].substring(begin + 14,
                            information[i].length());
                }

                // Read RFMAZE CellNumber
                if (information[i].indexOf("MAX_CELLS") != -1) {
                    try {
                        cellNumber = Integer.parseInt(information[i].substring(
                                12, 14));
                    } catch (NumberFormatException e) {
                        cellNumber = Integer.parseInt(information[i].substring(
                                12, 13));
                    }
                    cellN1 = new String[cellNumber];
                    cellN2 = new String[cellNumber];
                }

                // Read RFMAZE MobNumber
                if (information[i].indexOf("MAX_MOBILES") != -1) {
                    int begin = information[i].indexOf("MAX_MOBILES");
                    try {
                        mobNumber = Integer.parseInt(information[i].substring(
                                begin + 14, begin + 16));
                    } catch (NumberFormatException e) {
                        mobNumber = Integer.parseInt(information[i].substring(
                                begin + 14, begin + 15));
                    }
                    mobN1 = new String[mobNumber];
                    mobN2 = new String[mobNumber];
                }

                // get the max/min attenuation value
                if (information[i].indexOf("max_attn") != -1) {
                    try {
                        maxattn = Double.parseDouble(information[i].substring(
                                11, 13));
                    } catch (NumberFormatException e) {
                        maxattn = Integer.parseInt(information[i].substring(11,
                                14));
                    }
                }

                if (information[i].indexOf("min_attn") != -1) {
                    try {
                        minattn = Double.parseDouble(information[i].substring(
                                11, 13));
                    } catch (NumberFormatException e) {
                        minattn = Integer.parseInt(information[i].substring(11,
                                14));
                    }
                }

                // Read RFMAZE data
                if (i > 15 && information[i].indexOf(":") != -1
                        && information[i].indexOf("#") == -1) {
                    data = information[i];
                }

            } catch (NullPointerException e) {
            }
        }
    }

    // Convert bytebuffer to string
    public static String bb_to_str(ByteBuffer buffer) {
        String data;
        try {
            int old_position = buffer.position();
            data = decoder.decode(buffer).toString();
            // reset buffer's position to its original so it is not altered
            buffer.position(old_position);
        } catch (CharacterCodingException e) {
            logger.error("bb_to_str()", e);
            return "";
        }
        return data;
    }

    // Method that receive a command line and send it on the socketChannel
    public void receiveFromUser(String command) {
        try {
            InputStream is = new ByteArrayInputStream(command.getBytes("UTF-8"));
            BufferedReader localReader = new BufferedReader(new InputStreamReader(is));
            String msg;

            while ((msg = localReader.readLine()) != null) {
                isSend = true;
                synchronized (sendBuffer) {
                    sendBuffer.put(encode(msg + "\r\n"));
                }
            }
        } catch (IOException e) {
            logger.error("receiveFromUser()" , e);
        }
    }

    // Method to read or write on the socketChannel
    public void talk() throws IOException {
        try {
            socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
            while (selector.select() > 0) {
                if (done == true) {
                    break;
                }

                Set<?> readyKeys = selector.selectedKeys();
                Iterator<?> it = readyKeys.iterator();
                while (it.hasNext()) {
                    if (done == true) {
                        break;
                    }
                    SelectionKey key = null;
                    try {
                        key = (SelectionKey) it.next();
                        it.remove();
                        if (key.isWritable()) {
                            send++;
                            send(key);
                            Util.sleep(1000);
                        }

                        if (key.isReadable()) {
                            receive(key);
                        }

                    } catch (IOException e) {
                        logger.error("talk()", e);
                        try {
                            if (key != null) {
                                key.cancel();
                                key.channel().close();
                            }
                        } catch (IOException ex) {
                            logger.error("talk()", ex);
                        }
                    }
                }
            }
        } catch (Exception e) {
            done = true;
        }
    }

    // Same than talk but with another exit condition
    public void talkLong() throws IOException {
        try {
            socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
            while (selector.select() > 0) {

                if (done == true) {
                    break;
                }
                @SuppressWarnings("rawtypes")
                Set readyKeys = selector.selectedKeys();
                @SuppressWarnings("rawtypes")
                Iterator it = readyKeys.iterator();
                while (it.hasNext()) {
                    send++;
                    if (send > delay) {
                        done = true;
                    }
                    if (done == true) {
                        break;
                    }
                    SelectionKey key = null;
                    try {
                        key = (SelectionKey) it.next();
                        it.remove();
                        if (key.isReadable()) {
                            receiveLong(key);
                        }
                        if (key.isWritable()) {
                            send(key);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        try {
                            if (key != null) {
                                key.cancel();
                                key.channel().close();
                            }
                        } catch (Exception ex) {
                            logger.error("talkLong()", e);
                        }
                    }
                }
            }
        } catch (NullPointerException e) {
            done = true;
            logger.error("talkLong()", e);
        } catch (ClosedChannelException e1) {
            done = true;
            logger.error("talkLong()", e1);
        } catch (CancelledKeyException e2) {
            done = true;
            logger.error("talkLong()", e2);
        } catch (ClosedSelectorException e3) {
            done = true;
            logger.error("talkLong()", e3);
        }
    }

    // Method to send data
    public void send(SelectionKey key) throws IOException {
        if (!isSend) {
            send++;
            return;
        }
        SocketChannel socketChannel = (SocketChannel) key.channel();

        synchronized (sendBuffer) {
            sendBuffer.flip();
            socketChannel.write(sendBuffer);
            sendBuffer.compact();
        }
        isSend = false;
    }

    // Method that receive data from the server
    public void receive(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        int loop = 0;
        String receiveData = "";
        while ((socketChannel.read(receiveBuffer) > 0) && (loop < 100)) {
            socketChannel.read(receiveBuffer);
            receiveBuffer.flip();
            receiveData += decode(receiveBuffer);
            if (receiveData.indexOf("\r\n") == -1) {
                Util.sleep(100);
                loop++;
            }
        }

        String outputData = receiveData.substring(0, receiveData.indexOf("\n") + 1);
        String[] info = receiveData.split("\r");
        List<String>infoString = new ArrayList<String>();
        for (String s : info) {
            if ((s != null) && !s.trim().isEmpty()) {
               infoString.add(s.replaceAll("[\r\n]", ""));
            }
        }
        String[] information = new String[infoString.size()];
        infoString.toArray(information);
        setInformation(information);
        done = true;

        ByteBuffer temp = encode(outputData);
        receiveBuffer.position(temp.limit());
        receiveBuffer.compact();
    }

    // Method that receive data from the server(for RFMAZE bigger than 32x32)
    public void receiveLong(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        socketChannel.read(receiveBuffer);
        receiveBuffer.flip();
        String receiveData = decode(receiveBuffer);
        String outputData = receiveData.substring(0, receiveData.indexOf("\n") + 1);

        String[] info = receiveData.split("\r");
        String[] information = new String[info.length];

        for (int i = 0; i < info.length; i++) {
            if (!info[i].equals("")) {
                if (info[i].indexOf(":") != -1 && info[i].indexOf("#") == -1) {
                    information[i] = info[i].substring(0, info[i].length());
                } else {
                    information[i] = info[i].substring(0, info[i].length() - 1);
                }
            }
        }
        setInformation(information);
        ByteBuffer temp = encode(outputData);
        receiveBuffer.position(temp.limit());
        receiveBuffer.compact();
    }

    // Get attenuators tables
    public String[][] getTable(String data) {
        String[][] attV = new String[cellNumber][mobNumber];
        String n = "";
        String val = "";
        boolean debut = false;
        int compte = 0;
        int j = 0;
        int k = 0;

        for (int i = 0; i < data.length(); i++) {
            if (compte <= cellNumber * mobNumber - 1) {
                n = Character.toString(data.charAt(i));
                if (data.charAt(i) == ' ') {
                    if (k % mobNumber == 0 && k != 0) {
                        k = 0;
                        j++;
                    }
                    if (debut == false) {
                        val = "";
                    } else if (debut == true) {
                        attV[j][k] = val;
                        val = "";
                        compte++;
                        debut = false;
                        k++;
                    }
                } else {
                    debut = true;
                    val = val + n;
                }
            }
        }
        return attV;
    }

    // Get offset tables
    public String[] getTableOff(String data) {
        String[] offV = new String[cellNumber];
        String n = "";
        String val = "";
        boolean debut = false;
        int compte = 0;

        for (int i = 0; i < data.length(); i++) {
            if (compte <= cellNumber - 1) {
                n = Character.toString(data.charAt(i));
                if (data.charAt(i) == ' ') {
                    if (debut == false) {
                        val = "";
                    } else if (debut == true) {
                        offV[compte] = val;
                        val = "";
                        compte++;
                        debut = false;
                    }
                } else {
                    debut = true;
                    val = val + n;
                }
            }
        }
        return offV;
    }

    public ByteBuffer encode(String str) {
        return charset.encode(str);
    }

    public String decode(ByteBuffer buffer) {
        CharBuffer charBuffer = charset.decode(buffer);
        return charBuffer.toString();
    }

    // Setters
    public void setInformation(String[] message) {
        information = message;
    }

    public void setCellN1(String[] cn) {
        cellN1 = cn;
    }

    public void setCellN2(String[] cn) {
        cellN2 = cn;
    }

    // Getters

    public String[] getInformation() {
        return information;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public int getCellNumber() {
        return cellNumber;
    }

    public int getMobNumber() {
        return mobNumber;
    }

    public String[] getCellN1() {
        return cellN1;
    }

    public String[] getCellN2() {
        return cellN2;
    }

    public String[] getMobN1() {
        return mobN1;
    }

    public String[] getMobN2() {
        return mobN2;
    }

    public String[] getOffUser() {
        return offU;
    }

    public String[] getOffValue() {
        return offV;
    }

    public String[][] getAttUser() {
        return attU;
    }

    public String[][] getAttValue() {
        return attV;
    }

    public String getData() {
        return data;
    }

    public void setType(String typ) {
        this.type = typ;
    }

    public double getMaxAttn() {
        return maxattn;
    }

    public double getMinAttn() {
        return minattn;
    }

    public void initQuery(String hardware, String user, String server, int port) {
    	int retry = 0;
    	while (retry++ < 60) { // maximum retry 5 min.
	        try {
	            SocketChannel socket = SocketChannel.open();
	            socket.configureBlocking(false);
	            logger.info("Connect to " + server + ":" + port);
	            socket.connect(new InetSocketAddress(server, port));
	    
	            Selector selector = Selector.open();
	    
	            int loop = 0;
	            while ((!socket.finishConnect()) && (loop < 30)) {
	                logger.info("waiting connection to finish...trial " + loop);
	                Util.sleep(HALF_SECOND);
	                loop++;
	            }
	            
	            if (loop >= MAX_RETRIES) {
	                logger.warn("retried " + MAX_RETRIES + " times.");
	            }
	            Util.sleep(ONE_SECOND);
	    
	            final RFmazeInfo clientt;
	            clientt = new RFmazeInfo(selector, socket);
	            clientt.receiveFromUser("\r\n");
	            clientt.talk();
	            Util.sleep(1000);
	            String[] tokens = clientt.getInformation();
	            
	            socket.finishConnect();
	            socket.close();
	            
	            final SortedMap<Integer, String> offsetMapper = new TreeMap<Integer, String>();
	            List<String> list2 = new ArrayList<String>();
	            
	            Properties props = new Properties();
	            StringBuilder data_buffer = new StringBuilder();
	            for (String t : tokens) {
	                if (t==null || t.isEmpty()) {
	                    continue;
	                }
	                data_buffer.append(t).append("\n");
	                
	                String s = t.trim();
	                if (s.endsWith("dBmEnD")) {
	                    String ss = s.replaceAll("(StT)|(dBmEnD)", "");
	                    String sss = ss.replaceAll(":", " ");
	                    String[] tokens1 = sss.split("\\s+");
	                    if (tokens1.length != 4) {
	                        continue;
	                    }
	                    offsetMapper.put(Integer.valueOf(tokens1[1].trim()), tokens1[3].trim());
	                    continue;
	                } else if (s.matches("StT.*EnD")) {
	                    String ss = s.replaceAll("(StT)|(EnD)", "");
	                    list2.add(ss);
	                    continue;
	                } else if (s.contains("=")) {
	                    String[] tokens1 = s.split("=");
	                    if (tokens1.length == 2) {
	                        props.setProperty(tokens1[0].trim(), tokens1[1].trim());
	                    }
	                }
	            }
	            
	            String inputs = props.getProperty("matrix_inputs");
	            String outputs = props.getProperty("matrix_outputs");
	            logger.info("Received matrix size = " + inputs + "X" + outputs);
	            logger.info("\n\n"+data_buffer);
	            
	            if ((inputs!=null) && (outputs!=null)) {
	                int numRows = Integer.parseInt(inputs);
	                int numCols = Integer.parseInt(outputs);
	                String[][] matrix = new String[numRows][numCols];
	                String[] attenuation = new String[numRows];
	                
	                for (String ss : list2) {
	                    String[] tokens1 = ss.split(" ");
	                    int output = Integer.parseInt(tokens1[0].trim());
	                    int input = Integer.parseInt(tokens1[1].trim());
	                    String value = tokens1[2].trim();
	                    if (input > numRows || output > numCols) {
	                        continue;
	                    }
	                    
	                    if (input == 0) {
	                        attenuation[output-1] = value.replaceAll(":.*", "");
	                    } else {
	                        matrix[input-1][output-1] = value;
	                    }
	                }
	                
	                cache.updateMatrix(hardware, user, numRows, numCols, matrix);
	                cache.updateAttenuation(hardware, attenuation);
	                
	                String[] offset = new String[offsetMapper.size()];
	                int i = 0;
	                for (Map.Entry<Integer, String> en : offsetMapper.entrySet()) { 
	                	offset[i++] = en.getValue();
	                }                
	                cache.updateOffset(hardware, user, offset);
	            } else {
	                logger.info("TODO");
	            }
	            
	            clientt.receiveFromUser("exit\r\n");
	            clientt.talk();
	            Util.sleep(2000);
	            break;
	        } catch (IOException e) {
	            logger.warn("Connection not establised. Retry in 5 second. ");
	            Util.sleep(5000L);
	        } catch (Exception e) {
	            logger.error("initQuery()", e);
	        }
    	}
    }
}

