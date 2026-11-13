package com.paulo.nader.demo;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Represents a print job submitted to the 3D printer.
 */
public class PrintJob {
    private long printId;
    private String filePath;
    private String userId;
    private long estimatedPrintTime;

    /**
     * Creates a print job with its identifier, file path, and submitting user.
     *
     * @param printId the identifier of this print job
     * @param filePath the path to the file to print
     * @param userId the identifier of the user who submitted the job
     */
    public PrintJob(long printId, String filePath, String userId) {
        this.printId = printId;
        this.filePath = filePath;
        this.userId = userId;
    }

    //constructor without User parameter or printId for use in API calls since backend will handle that
    /**
     * Creates a print job from an API request containing only a file path.
     *
     * @param filePath the path to the file to print
     */
    @JsonCreator
    public PrintJob(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Returns this print job's identifier.
     *
     * @return the print job identifier
     */
    public long getPrintId() {
        return printId;
    }

    /**
     * Sets this print job's identifier.
     *
     * @param printId the print job identifier
     */
    public void setPrintId(long printId) {
        this.printId = printId;
    }

    /**
     * Returns the path to the file to print.
     *
     * @return the file path
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Sets the path to the file to print.
     *
     * @param filePath the file path
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Returns the identifier of the user who submitted this job.
     *
     * @return the user identifier
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the identifier of the user who submitted this job.
     *
     * @param userId the user identifier
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Returns the estimated duration of this print job.
     *
     * @return the estimated print duration
     */
    public long getEstimatedPrintTime() {
        return estimatedPrintTime;
    }

    /**
     * Sets the estimated duration of this print job.
     *
     * @param estimatedPrintTime the estimated print duration
     */
    public void setEstimatedPrintTime(long estimatedPrintTime) {
        this.estimatedPrintTime = estimatedPrintTime;
    }
}
