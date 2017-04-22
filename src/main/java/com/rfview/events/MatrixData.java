package com.rfview.events;

import java.io.Serializable;
import java.util.Properties;

import com.rfview.comm.MatrixEntry;

public class MatrixData implements Serializable {

    private static final long serialVersionUID = -1242821016362043241L;

    private final int numberInputs;
    private final int numberOutputs;
    private final MatrixEntry[][] matrix;
    private final Properties matrixConf;
    private final int[] offset;

    public MatrixData(int numberInputs, int numberOutputs, MatrixEntry[][] matrix,
            Properties matrixConf, int[] offset) {
        super();
        this.numberInputs = numberInputs;
        this.numberOutputs = numberOutputs;
        this.matrix = matrix;
        this.matrixConf = matrixConf;
        this.offset = offset;
    }

    public int getNumberInputs() {
        return numberInputs;
    }

    public int getNumberOutputs() {
        return numberOutputs;
    }

    public MatrixEntry[][] getMatrix() {
        return matrix;
    }

    public Properties getMatrixConf() {
        return matrixConf;
    }

    public int[] getOffset() {
        return offset;
    }
}
