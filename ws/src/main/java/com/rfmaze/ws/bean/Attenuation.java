package com.rfmaze.ws.bean;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Attenuation {

    private int row;
    private int col;
    private int attenuation;

    public Attenuation() {
        super();
    }

    public Attenuation(int row, int col, int attenuation) {
        super();
        this.row = row;
        this.col = col;
        this.attenuation = attenuation;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getAttenuation() {
        return attenuation;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public void setAttenuation(int attenuation) {
        this.attenuation = attenuation;
    }

    @Override
    public String toString() {
        return "Attenuation [row=" + row + ", col=" + col + ", val=" + attenuation + "]";
    }

}
