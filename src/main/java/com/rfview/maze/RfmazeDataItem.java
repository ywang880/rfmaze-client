package com.rfview.maze;

import java.io.Serializable;

public class RfmazeDataItem implements Serializable {

    private static final long serialVersionUID = -3934382765138529741L;

	private String row;
    private String column;
    private String value;
    private boolean isOffset;
    private boolean voidData = false;
    public static RfmazeDataItem VOID = new RfmazeDataItem();
    
    private RfmazeDataItem() {
        voidData = true;
    }
        
    public RfmazeDataItem(String row, String column, String value, boolean isOffset) {
        super();
        this.row = row;
        this.column = column;
        this.value = value;
        this.isOffset = isOffset;
    }
    
    public String getRow() {
        return row;
    }

    public void setRow(String row) {
        this.row = row;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isOffset() {
        return isOffset;
    }

    public void setOffset(boolean isOffset) {
        this.isOffset = isOffset;
    }

    public boolean isVoidData() {
        return voidData;
    }

    @Override
	public String toString() {
		return "RfmazeDataItem [row=" + row + ", column=" + column + ", value=" + value + ", isOffset="
				+ isOffset + "]";
	}
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((column == null) ? 0 : column.hashCode());
        result = prime * result + ((row == null) ? 0 : row.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RfmazeDataItem other = (RfmazeDataItem) obj;
        if (column == null) {
            if (other.column != null)
                return false;
        } else if (!column.equals(other.column))
            return false;
        if (row == null) {
            if (other.row != null)
                return false;
        } else if (!row.equals(other.row))
            return false;
        return true;
    }    
}
