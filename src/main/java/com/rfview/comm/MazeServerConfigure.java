package com.rfview.comm;

public class MazeServerConfigure {

    private String matrixName;
    private String matrixType;
    private int serverPort;
    private int numInputs;
    private int numOutputs;
    private int maxCells;
    private int maxMobiles;
    private String serverHostname;
    private String matrixIp;
    private int matrixServerPort;
    private int maxAttn;
    private int minAttn;
    private int stepDb;
    private boolean invertInputOutput;
    private boolean debug;
    private boolean debug2;

    public String getMatrixName() {
        return matrixName;
    }

    public void setMatrixName(String matrixName) {
        this.matrixName = matrixName;
    }

    public String getMatrixType() {
        return matrixType;
    }

    public void setMatrixType(String matrixType) {
        this.matrixType = matrixType;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public int getNumInputs() {
        return numInputs;
    }

    public void setNumInputs(int numInputs) {
        this.numInputs = numInputs;
    }

    public int getNumOutputs() {
        return numOutputs;
    }

    public void setNumOutputs(int numOutputs) {
        this.numOutputs = numOutputs;
    }

    public int getMaxCells() {
        return maxCells;
    }

    public void setMaxCells(int maxCells) {
        this.maxCells = maxCells;
    }

    public int getMaxMobiles() {
        return maxMobiles;
    }

    public void setMaxMobiles(int maxMobiles) {
        this.maxMobiles = maxMobiles;
    }

    public String getServerHostname() {
        return serverHostname;
    }

    public void setServerHostname(String serverHostname) {
        this.serverHostname = serverHostname;
    }

    public String getMatrixIp() {
        return matrixIp;
    }

    public void setMatrixIp(String matrixIp) {
        this.matrixIp = matrixIp;
    }

    public int getMatrixServerPort() {
        return matrixServerPort;
    }

    public void setMatrixServerPort(int matrixServerPort) {
        this.matrixServerPort = matrixServerPort;
    }

    public int getMaxAttn() {
        return maxAttn;
    }

    public void setMaxAttn(int maxAttn) {
        this.maxAttn = maxAttn;
    }

    public int getMinAttn() {
        return minAttn;
    }

    public void setMinAttn(int minAttn) {
        this.minAttn = minAttn;
    }

    public int getStepDb() {
        return stepDb;
    }

    public void setStepDb(int stepDb) {
        this.stepDb = stepDb;
    }

    public boolean isInvertInputOutput() {
        return invertInputOutput;
    }

    public void setInvertInputOutput(boolean invertInputOutput) {
        this.invertInputOutput = invertInputOutput;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isDebug2() {
        return debug2;
    }

    public void setDebug2(boolean debug2) {
        this.debug2 = debug2;
    }

}
