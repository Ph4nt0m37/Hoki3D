package com.paulo.nader.demo;

import com.fasterxml.jackson.annotation.JsonCreator;

public class PrintJob {
    private long printId;
    private String filePath;
    private String userId;
    private long estimatedPrintTime;

    public PrintJob(long printId, String filePath, String userId) {
        this.printId = printId;
        this.filePath = filePath;
        this.userId = userId;
    }

    //constructor without User parameter for use in API calls
    @JsonCreator
    public PrintJob(long printId, String filePath) {
        this.printId = printId;
        this.filePath = filePath;
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
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public long getEstimatedPrintTime() {
        return estimatedPrintTime;
    }
    public void setEstimatedPrintTime(long estimatedPrintTime) {
        this.estimatedPrintTime = estimatedPrintTime;
    }
}
