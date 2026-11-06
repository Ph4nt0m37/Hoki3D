package com.paulo.nader.demo;

public class PrintJob {
    private long printId;
    private String filePath;
    private User user;
    private long estimatedPrintTime;

    public PrintJob(long printId, String filePath, User user) {
        this.printId = printId;
        this.filePath = filePath;
        this.user = user;
    }
    
    public long getPrintId() {
        return printId;
    }
    public void setPrintId(long printId) {
        this.printId = printId;
    }
    public String getFilePath() {
        return filePath;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public long getEstimatedPrintTime() {
        return estimatedPrintTime;
    }
    public void setEstimatedPrintTime(long estimatedPrintTime) {
        this.estimatedPrintTime = estimatedPrintTime;
    }
}
