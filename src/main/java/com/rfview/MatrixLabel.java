package com.rfview;

public class MatrixLabel {

    private final int id;
    private final String label;
    private final String description;

    public MatrixLabel(int id, String label, String description) {
        super();
        this.id = id;
        this.label = label;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }
    
    @Override
    public String toString() {
        return "MatrixLabel [id=" + id + ", label=" + label + ", description="
                + description + "]";
    }
}
