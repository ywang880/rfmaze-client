package com.rfview;

public class ColorSchemeAction extends BaseActionSupport {

    private static final long serialVersionUID = -6798445163576134855L;

    private String range1="0-10";
    private String range2="11-62";
    private String range3="63";
    private String color1="008800";
    private String color2="EE9933";
    private String color3="DD0000";

    public ColorSchemeAction() {
    }

    public String getRange1() {
        return range1;
    }

    public void setRange1(String range1) {
        this.range1 = range1;
    }

    public String getRange2() {
        return range2;
    }

    public void setRange2(String range2) {
        this.range2 = range2;
    }

    public String getRange3() {
        return range3;
    }

    public void setRange3(String range3) {
        this.range3 = range3;
    }

    public String getColor1() {
        return color1;
    }

    public void setColor1(String color1) {
        this.color1 = color1;
    }

    public String getColor2() {
        return color2;
    }

    public void setColor2(String color2) {
        this.color2 = color2;
    }

    public String getColor3() {
        return color3;
    }

    public void setColor3(String color3) {
        this.color3 = color3;
    }

    public String execute() {
        if (sessionMap != null) {
            String uid = (String) sessionMap.get("loginId");
            if (uid == null) {
                return "login";
            }
            this.username = uid;

        }

        logger.debug(getRange1());
        logger.debug(getRange2());
        logger.debug(getRange3());
        logger.debug(getColor1());
        logger.debug(getColor2());
        logger.debug(getColor3());
        return SUCCESS;
    }
}
