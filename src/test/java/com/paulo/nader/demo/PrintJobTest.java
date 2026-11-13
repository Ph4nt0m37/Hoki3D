package com.paulo.nader.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Tests the behavior of {@link PrintJob}.
 */
class PrintJobTest {

    // PJ-01
    @Test
    void returnsAssignedPrintId() {
        PrintJob job = new PrintJob(2001L, "uploads/model.stl", "user-42");

        assertEquals(2001L, job.getPrintId());
    }

    // PJ-02
    @Test
    void defaultsToPrintIdZero() {
        PrintJob job = new PrintJob("uploads/model.stl");

        assertEquals(0L, job.getPrintId());
    }

    // PJ-03
    @Test
    void returnsAssignedFilePath() {
        PrintJob job = new PrintJob("uploads/model.stl");

        assertEquals("uploads/model.stl", job.getFilePath());
    }

    // PJ-04
    @Test
    void returnsNullForMissingFilePath() {
        PrintJob job = new PrintJob(null);

        assertNull(job.getFilePath());
    }

    // PJ-05
    @Test
    void returnsSubmittingUserId() {
        PrintJob job = new PrintJob(2001L, "uploads/model.stl", "user-42");

        assertEquals("user-42", job.getUserId());
    }

    // PJ-06
    @Test
    void returnsNullForMissingUserId() {
        PrintJob job = new PrintJob("uploads/model.stl");

        assertNull(job.getUserId());
    }

    // PJ-07
    @Test
    void returnsAssignedEstimatedPrintTime() {
        PrintJob job = new PrintJob("uploads/model.stl");
        job.setEstimatedPrintTime(120L);

        assertEquals(120L, job.getEstimatedPrintTime());
    }

    // PJ-08
    @Test
    void defaultsToEstimatedPrintTimeZero() {
        PrintJob job = new PrintJob("uploads/model.stl");

        assertEquals(0L, job.getEstimatedPrintTime());
    }

    // PJ-09
    @Test
    void updatesFilePath() {
        PrintJob job = new PrintJob("uploads/model.stl");
        job.setFilePath("uploads/test.stl");

        assertEquals("uploads/test.stl", job.getFilePath());
    }

    // PJ-10
    @Test
    void storesNullAndEmptyFilePathsAsSupplied() {
        PrintJob nullPathJob = new PrintJob("uploads/model.stl");
        PrintJob emptyPathJob = new PrintJob("uploads/model.stl");

        nullPathJob.setFilePath(null);
        emptyPathJob.setFilePath("");

        assertNull(nullPathJob.getFilePath());
        assertEquals("", emptyPathJob.getFilePath());
    }

    // PJ-11
    @Test
    void updatesEstimatedPrintTime() {
        PrintJob job = new PrintJob("uploads/model.stl");
        job.setEstimatedPrintTime(60L);

        job.setEstimatedPrintTime(120L);

        assertEquals(120L, job.getEstimatedPrintTime());
    }

    // PJ-12
    @Test
    void rejectsNegativeEstimatedPrintTime() {
        PrintJob job = new PrintJob("uploads/model.stl");

        assertThrows(IllegalArgumentException.class,
                () -> job.setEstimatedPrintTime(-10L));
    }
}

