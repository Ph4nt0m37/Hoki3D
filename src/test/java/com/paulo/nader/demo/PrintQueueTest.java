package com.paulo.nader.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.SQLException;

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
}
