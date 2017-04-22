package com.rfview.maze;

public class Entry {
    private final int row;
    private final int col;
    private final String name;
    private int value;
    private final boolean booked;

    public static final Entry EMPTY = new Entry(0, 0, null, 0, false);
    
    public Entry(int row, int col, String name, int value, boolean booked) {
        this.row = row;
        this.col = col;
        this.name = name;
        this.value = value;
        this.booked = booked;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public boolean isBooked() {
        return booked;
    }

    public void setValue(int v) {
        this.value = v;
    }
}
