package com.rfview;

import com.rfview.utils.Constants;

public class Cell {

    private String name;
    private String hiddenName = "-";
    private String status = Constants.CONST_AVAILABLE;
    private String rowstatus = Constants.CONST_AVAILABLE;
    private String label = "";
    private String description = "";
    private String booked = "no";
    private String bgcolor = "#688DB2";
    private String offset;
    
    public String getBgcolor() {
        return bgcolor;
    }

    public void setBgcolor(String bgcolor) {
        this.bgcolor = bgcolor;
    }

    public Cell(String name) {
        super();
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

    public String getHiddenName() {
        return hiddenName;
    }

    public void setHiddenName(String hiddenName) {
        this.hiddenName = hiddenName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRowstatus() {
        return rowstatus;
    }

    public void setRowstatus(String rowstatus) {
        this.rowstatus = rowstatus;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getBooked() {
        return booked;
    }

    public void setBooked(String booked) {
        this.booked = booked;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    
    @Override
    public String toString() {
        return "Cell [name=" + name + ", hiddenName=" + hiddenName + ", status=" + status
                + ", rowstatus=" + rowstatus + ", label=" + label + ", assignedLabel="
                + description + ", booked=" + booked + "]";
    }
}
