package com.rfview;

public class THeader {


    private final String user;
    private final String label;
    private final String description;

    public THeader(String user, String label, String description) {
        super();
        this.user = user;
        this.label = label;
        this.description = description;
    }

    public String getUser() {
        return user;
    }
    
    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }
}
