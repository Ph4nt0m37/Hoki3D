package com.paulo.nader.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PrintQueueTest {
    private PrintQueue printQueue;
    private DatabaseController databaseController;
    private SLQueue<PrintJob> queue;

    @BeforeEach
    void setUp() throws SQLException {
        databaseController = mock(DatabaseController.class);
        queue = new SLQueue<>();
        when(databaseController.getQueuedPrints()).thenReturn(queue);
        printQueue = new PrintQueue(databaseController);
    }

    @Test
    void testGetPrintQueueReturnsQueue() {
        assertNotNull(queue,
                "Print queue should not be null");
    }

    @Test
    void testGetPrintQueueReturnsSameQueueInstance() {
        SLQueue<PrintJob> queue1 = printQueue.getPrintQueue();
        SLQueue<PrintJob> queue2 = printQueue.getPrintQueue();

        assertSame(queue1, queue2,
                "Both calls should return the same queue instance");
    }

    @Test
    void testAddPrintJob() {
        PrintJob job = new PrintJob("awesome.stl");

        assertTrue(printQueue.add(job));

        assertTrue(printQueue.getPrintQueue().contains(job),
                "Queue should contain the added print job");
    }

    @Test
    void testAddMultiplePrintJobs() {
        PrintJob job1 = new PrintJob("awesome.stl");
        PrintJob job2 = new PrintJob("awesome.stl");

        printQueue.add(job1);
        printQueue.add(job2);

        assertTrue(printQueue.getPrintQueue().contains(job1));
        assertTrue(printQueue.getPrintQueue().contains(job2));
    }

    @Test
    void testRemovePrintJob() {
        PrintJob job = new PrintJob("awesome.stl");

        printQueue.add(job);
        printQueue.remove(job);

        assertFalse(printQueue.getPrintQueue().contains(job),
                "Queue should no longer contain the removed job");
    }

    @Test
    void testRemoveJobDoesNotRemoveOtherJobs() {
        PrintJob job1 = new PrintJob("awesome.stl");
        PrintJob job2 = new PrintJob("awesome.stl");

        printQueue.add(job1);
        printQueue.add(job2);

        printQueue.remove(job1);

        assertFalse(printQueue.getPrintQueue().contains(job1));
        assertTrue(printQueue.getPrintQueue().contains(job2));
    }

    @Test
    void testAddingSameJobTwice() {
        PrintJob job = new PrintJob("awesome.stl");

        printQueue.add(job);
        printQueue.add(job);

        SLQueue<PrintJob> printQueueContents = printQueue.getPrintQueue();

        assertTrue(printQueueContents.contains(job));
        assertEquals(2, printQueueContents.size());
    }

    @Test
    void testConstructorLoadsQueuedPrintsFromDatabase() throws SQLException {
        SLQueue<PrintJob> loadedQueue = new SLQueue<>();
        loadedQueue.enqueue(new PrintJob(
                1001L,
                "loaded.stl",
                "user-42"));
        when(databaseController.getQueuedPrints()).thenReturn(loadedQueue);

        PrintQueue loadedPrintQueue = new PrintQueue(databaseController);

        assertSame(loadedQueue, loadedPrintQueue.getPrintQueue());
        assertEquals(1, loadedPrintQueue.getPrintQueue().size());
    }

    @Test
    void testConstructorUsesEmptyQueueWhenLoadingFails() throws SQLException {
        when(databaseController.getQueuedPrints())
                .thenThrow(new SQLException("Database unavailable"));

        PrintQueue failedPrintQueue = new PrintQueue(databaseController);

        assertTrue(failedPrintQueue.getPrintQueue().isEmpty());
    }

    @Test
    void testAddPrintSavesJobBeforeEnqueuing() throws SQLException {
        PrintJob job = new PrintJob(
                1001L,
                "new-job.stl",
                "user-42");

        assertTrue(printQueue.add(job));

        verify(databaseController).savePrintJob(job);
        assertSame(job, printQueue.getPrintQueue().peek());
    }

    @Test
    void testAddPrintReturnsFalseAndDoesNotEnqueueWhenDatabaseSaveFails()
            throws SQLException {
        PrintJob job = new PrintJob(
                1001L,
                "new-job.stl",
                "user-42");
        doThrow(new SQLException("Database unavailable"))
                .when(databaseController)
                .savePrintJob(job);

        assertFalse(printQueue.add(job));
        assertTrue(printQueue.getPrintQueue().isEmpty());
    }

    @Test
    void testRemovePrintRemovesDatabaseAndQueueEntry() throws SQLException {
        PrintJob job = new PrintJob(
                1001L,
                "remove-job.stl",
                "user-42");
        printQueue.add(job);

        assertTrue(printQueue.remove(job));

        verify(databaseController).removePrintJob(1001L);
        assertFalse(printQueue.getPrintQueue().contains(job));
    }

    @Test
    void testRemovePrintReturnsFalseAndKeepsQueueWhenDatabaseFails()
            throws SQLException {
        PrintJob job = new PrintJob(
                1001L,
                "remove-job.stl",
                "user-42");
        printQueue.getPrintQueue().enqueue(job);
        doThrow(new SQLException("Database unavailable"))
                .when(databaseController)
                .removePrintJob(1001L);

        assertFalse(printQueue.remove(job));
        assertTrue(printQueue.getPrintQueue().contains(job));
    }

    @Test
    void testStartNextPrintMovesFrontJobToPrinting() throws SQLException {
        PrintJob nextJob = new PrintJob(
                1001L,
                "next-job.stl",
                "user-42");
        queue.enqueue(nextJob);

        PrintJob startedJob = printQueue.startNextPrint();

        assertSame(nextJob, startedJob);
        assertEquals(PrintStatus.PRINTING, startedJob.getStatus());
        assertTrue(queue.isEmpty());
        verify(databaseController).saveCompletedPrint(nextJob);
        verify(databaseController).removePrintJob(1001L);
    }

    @Test
    void testStartNextPrintCompletesCurrentPrintAndStartsNext()
            throws SQLException {
        PrintJob currentJob = new PrintJob(
                1001L,
                "current-job.stl",
                PrintStatus.PRINTING,
                "user-42");
        PrintJob nextJob = new PrintJob(
                1002L,
                "next-job.stl",
                "user-42");
        queue.enqueue(nextJob);
        when(databaseController.getCurrentPrint()).thenReturn(currentJob);

        PrintQueue queueWithCurrent = new PrintQueue(databaseController);
        queueWithCurrent.getPrintQueue().enqueue(nextJob);
        PrintJob startedJob = queueWithCurrent.startNextPrint();

        assertEquals(PrintStatus.COMPLETED, currentJob.getStatus());
        verify(databaseController).saveCompletedPrint(currentJob);
        assertSame(nextJob, startedJob);
        assertEquals(PrintStatus.PRINTING, nextJob.getStatus());
        verify(databaseController).saveCompletedPrint(nextJob);
        verify(databaseController).removePrintJob(1002L);
    }

    @Test
    void testStartNextPrintThrowsWhenQueueIsEmpty() {
        assertThrows(
                NoSuchElementException.class,
                () -> printQueue.startNextPrint());
    }

    @Test
    void testStartNextPrintReturnsNullWhenDatabaseSaveFails()
            throws SQLException {
        PrintJob nextJob = new PrintJob(
                1001L,
                "next-job.stl",
                "user-42");
        queue.enqueue(nextJob);
        doThrow(new SQLException("Database unavailable"))
                .when(databaseController)
                .saveCompletedPrint(nextJob);

        assertNull(printQueue.startNextPrint());
    }
}
