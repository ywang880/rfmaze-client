package com.rfview;

public class LogFile {

    private final String name;
    private final long size;
    private final String date;
    private String url;
    
    public LogFile(String name, long size, String date) {
        super();
        this.name = name;
        this.size = size;
        this.date = date;
        this.url = "rfmaze/filedownload.action?filename="+name;
    }

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }

    public String getDate() {
        return date;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return "LogFile [name=" + name + ", size=" + size + ", date=" + date + "]";
    }
}
