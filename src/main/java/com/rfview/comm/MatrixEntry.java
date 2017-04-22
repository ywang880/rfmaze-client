package com.rfview.comm;

public class MatrixEntry {

    private final int value;
    private final String status;
    
    public static final MatrixEntry EMPTY = new MatrixEntry(0, "unknown");
    
    public MatrixEntry( int value,  String status) {
        super();
        this.value = value;
        this.status = status;
    }

    public int getValue() {
        return value;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "MatrixEntry [value=" + value + ", status=" + status + "]";
    }
}
