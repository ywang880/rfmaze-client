package com.rfview;

public class Option {

    private String name;
    private String bgcolor = "#688DB2";

    public Option(String name) {
        super();
        this.name = name;
    }
    
    
    public String getBgcolor() {
        return bgcolor;
    }

    public void setBgcolor(String bgcolor) {
        this.bgcolor = bgcolor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
