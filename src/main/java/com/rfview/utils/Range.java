package com.rfview.utils;

public class Range {

    private int low;
    private int high;
    private String color;
    
    public int getLow() {
        return low;
    }
    public int getHigh() {
        return high;
    }

    public void setLow(int low) {
        this.low = low;
    }
    public void setHigh(int high) {
        this.high = high;
    }
    
    public String getColor(int data) {
        if (low == -1) {
            if (data == high) {
                return color;
            }
        } else {
            if ((data >= low) && (data <= high)) {
                return color;
            }
        }
        return null;
    }
    
    public Range(String data, String color) {

        if (color.trim().startsWith("#")) {
            this.color = color;
        } else {
            this.color = "#"+color.trim();
        }
        
        if (!data.contains("-")) {            
            low = -1;
            high = Integer.parseInt(data);
            return;
        }

        String[] tokens = data.split("\\s*-\\s*");
        low = Integer.parseInt(tokens[0]);
        high = Integer.parseInt(tokens[1]);
    }
    
    @Override
    public boolean equals(Object o) {
        if ( !(o instanceof Range))  {
            return false;
        }
        
        Range r = (Range)o;
        return ((r.getHigh() == this.high) && (r.getLow() == this.low));
    }

    public int hashCode() {
        return (Integer.toString(high) + Integer.toString(low)).hashCode();
    }
    
    @Override
    public String toString() {
        return hashCode() + ", Range [low=" + low + ", high=" + high + ", color=" + color + "]";
    }
}
