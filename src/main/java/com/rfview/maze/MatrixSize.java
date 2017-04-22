package com.rfview.maze;

public class MatrixSize {

    private final int numInputs;
    private final int numOutputs;
    
    public MatrixSize(int numInputs, int numOutputs) {
        this.numInputs = numInputs;
        this.numOutputs = numOutputs;
    }
    
    public int getNuminputs() {
        return numInputs;
    }
    public int getNumouputs() {
        return numOutputs;
    }
}
