package com.rfview.maze;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

import com.rfview.Cell;
import com.rfview.MatrixLabel;
import com.rfview.utils.CompositeKey;

public class Datagrid {
    
    private final Logger logger = Logger.getLogger(Datagrid.class.getName());

    private final ConcurrentHashMap<String, Entry[][]> matrixData = new ConcurrentHashMap<String, Entry[][]>();
    private final ConcurrentHashMap<String, Entry[]> offset = new ConcurrentHashMap<String, Entry[]>();
    private final ConcurrentHashMap<String, String[]> attenuation = new ConcurrentHashMap<String, String[]>();
    private final ConcurrentHashMap<String, MatrixSize> msize = new ConcurrentHashMap<String, MatrixSize>();

    private final ConcurrentHashMap<CompositeKey, Cell[][]> matrixCache = new ConcurrentHashMap<CompositeKey, Cell[][]>();
    private final ConcurrentHashMap<Set<CompositeKey>, Cell[][]> matrixIndex = new ConcurrentHashMap<Set<CompositeKey>, Cell[][]>();
    private final ConcurrentHashMap<String, AtomicBoolean> connectionStatus = new ConcurrentHashMap<String, AtomicBoolean>();
    private String input;
    private String output;
    private String value;

    private final Map<String, List<MatrixLabel>> inputLabels = new ConcurrentHashMap<String, List<MatrixLabel>>();
    private final Map<String, List<MatrixLabel>> outputLabels = new ConcurrentHashMap<String, List<MatrixLabel>>();
    private final Map<String, AtomicBoolean> labelsChanged = new ConcurrentHashMap<String, AtomicBoolean>();

    private final static Map<String, Timer> timers = new HashMap<String, Timer>();
    private final static Map<String, RFMazeServerAgent> agents = new HashMap<String, RFMazeServerAgent>();

    private static final Datagrid instance = new Datagrid();
    public Map<String, Entry[]> getOffset() {
        return offset;
    }

    public List<MatrixLabel> getInputLabels(String hardware) {
        return inputLabels.get(hardware);
    }

    public void setInputLabels(String hardware, List<MatrixLabel> data) {
        inputLabels.put(hardware, data);
    }

    public List<MatrixLabel> getOutputLabels(String hardware) {
        return outputLabels.get(hardware);
    }

    public void setOutputLabels(String hardware, List<MatrixLabel> data) {
        outputLabels.put(hardware, data);
    }

    public Entry[] getOffset(String hardware) {
        return offset.get(hardware);
    }

    public void setConnectionState(String hardware, boolean state) {
        AtomicBoolean currentState = connectionStatus.get(hardware);
        if (currentState == null) {
            connectionStatus.put(hardware, new AtomicBoolean(state));
        } else {
            currentState.set(state);
        }
    }

    public boolean getConnectionState(String hardware) {
        if (connectionStatus == null || hardware == null) {
            return false;
        }
        AtomicBoolean currentState = connectionStatus.get(hardware);
        if (currentState == null) {
            return false;
        }
        return currentState.get();
    }

    public boolean isLabelsChanged(String hardware) {
        AtomicBoolean labelChanged = labelsChanged.get(hardware);
        if (labelChanged == null) {
            return false;
        }
        return labelChanged.get();
    }

    public void setLabelsChanged(String hardware, boolean ischanged) {
        AtomicBoolean labelChanged = labelsChanged.get(hardware);
        if (labelChanged == null) {
            labelsChanged.put(hardware, new AtomicBoolean(ischanged));
        } else {
            labelChanged.set(ischanged);
        }
    }

    public void setMsize(String hardware, MatrixSize size) {
        if (msize.containsKey(hardware)) {
            msize.remove(hardware);
        }
        msize.put(hardware, size);
    }

    public Map<String, MatrixSize> getMsize() {
        return msize;
    }

    public static Datagrid getInstance() {
        return instance;
    }

    private Datagrid() {
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public synchronized Entry[][] getMatrix(String server) {
        Entry[][] data = matrixData.get(server);
        return data!=null? data : testData();
    }

    public synchronized void removeMatrix(String server) {
        matrixData.remove(server);
    }

    public synchronized boolean isDataReady(String server) {
        return matrixData.get(server) != null;
    }

    public synchronized Entry[][] setMatrix(String server, Entry[][] matrix) {
        if (matrixData.containsKey(server)) {
            matrixData.remove(server);
        }
        return matrixData.put(server, matrix);
    }

    public synchronized Entry[] getOffsetData(String hardware) {
        return offset.get(hardware);
    }

    public void removeOffsetData(String hardware) {
        offset.remove(hardware);
    }

    public void fetchFromServer(Server server) {
    }

    private Entry[][] testData() {
        Entry[][] data = new Entry[32][32];
        for (int i = 0; i < 32; i++) {
            for (int j = 0; j < 32; j++) {
                data[i][j] = new Entry(i, j, "chamber1", i*32+j, true);
            }
        }
        return data;
    }

    public synchronized void updateMatrix(String hardware, String user, int numRows, int numCol, String[][] data) {

        if (matrixData.containsKey(hardware)) {
            matrixData.remove(hardware);
        }

        Entry[][] newData = new Entry[numRows][numCol];
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCol; j++) {
                try {
                    newData[i][j] = new Entry(i, j, user, getDataValue(data[i][j]), true);
                } catch (NumberFormatException e) {
                    logger.error(e.getMessage() + "  " + data[i][j]);
                }
            }
        }
        matrixData.put(hardware, newData);
    }

    public synchronized void update(String hardware, String c1, String c2, String value) {

        if (!matrixData.containsKey(hardware)) {
            logger.warn("not data found for " + hardware);
        }

        if (c1.isEmpty() || c2.isEmpty()) {
            logger.warn("Invalid rows and columns number.");
        }

        Entry[][] theData = matrixData.get(hardware);
        String[] strRow = c1.split(",");
        String[] strCol = c2.split(",");
        for (String r : strRow) {
            for (String c : strCol) {
                int rn = Integer.parseInt(r)-1;
                int cn = Integer.parseInt(c)-1;
                theData[rn][cn].setValue(Integer.parseInt(value));
            }
        }
    }

    public void updateAttenuation(String hardware, String[] data) {
        if (attenuation.containsKey(hardware)) {
            attenuation.remove(hardware);
        }
        attenuation.put(hardware, data);
    }

    public String[] getAttenuation(String hardware) {
        return attenuation.get(hardware);
    }

    public void updateOffset(String hardware, String user, String[] data) {
        if (offset.containsKey(hardware)) {
            offset.remove(hardware);
        }

        Entry[] newData = new Entry[data.length];
        for (int i = 0; i < data.length; i++) {
            try {
                Float f = Float.parseFloat(data[i].trim());
                newData[i] = new Entry(i, 0, user, f.intValue(), true);
            } catch (NumberFormatException e) {
                logger.error(e.getMessage() + "  " + data[i]);
            }
        }

        offset.put(hardware, newData);
    }

    private int getDataValue(String var) {
        if ((var==null) || var.trim().isEmpty()) {
            return 0;
        }
        String[] tokens = var.split(":");
        return Integer.parseInt(tokens[0].trim());
    }

    public Cell[][] getMatrixCache(CompositeKey key) {
        return matrixCache.get(key);
    }

    public void removeMatrixCache(CompositeKey key) {
        matrixCache.remove(key);
    }

    public void putMatrixCache(CompositeKey key, Cell[][] matrix) {
        matrixCache.remove(key);
        matrixCache.put(key, matrix);
    }

    public Cell[][] getMatrixIndex(Set<CompositeKey> key) {
        return matrixIndex.get(key);
    }

    public static Map<String, Timer> getTimers() {
        return timers;
    }

    public static Timer getTimer(String name) {
        if (!timers.containsKey(name)) {
            timers.put(name, new Timer(name));
        }
        return timers.get(name);
    }

    public void createAgent(String[] args, String hardware) {    	
        RFMazeServerAgent agent = getAgent(hardware);
        if (agent == null) {
        	agent = new RFMazeServerAgent(args, hardware);
        	addAgent(hardware, agent);
            agent.start();
        }
    }
    
    public Map<String, RFMazeServerAgent> getAgents() {
        return agents;
    }
    
    public synchronized RFMazeServerAgent getAgent(String name) {
        return agents.get(name);
    }

    public synchronized void addAgent(String hardware, RFMazeServerAgent agent) {
        agents.put(hardware, agent);
    }

    public synchronized void removeAgent(String hardware) {
       agents.remove(hardware);
    }
    
    public void removeAgents() {
        agents.clear();
     }
}
