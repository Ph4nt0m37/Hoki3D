package com.paulo.nader.demo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PrintQueueTest {

    @BeforeEach
    void setUp() {
        // Remove any leftover jobs before each test.
        SLQueue<PrintJob> queue = PrintQueue.getPrintQueue();

        while (!queue.isEmpty()) {
            queue.dequeue();
        }
    }

    @Test
    void testGetPrintQueueReturnsQueue() {
        SLQueue<PrintJob> queue = PrintQueue.getPrintQueue();

        assertNotNull(queue,
                "Print queue should not be null");
    }

    @Test
    void testSingletonReturnsSameQueueInstance() {
        SLQueue<PrintJob> queue1 = PrintQueue.getPrintQueue();
        SLQueue<PrintJob> queue2 = PrintQueue.getPrintQueue();

        assertSame(queue1, queue2,
                "Both calls should return the same queue instance");
    }

    @Test
    void testAddPrintJob() {
        PrintJob job = new PrintJob("awesome.stl");

        PrintQueue.add(job);

        assertTrue(PrintQueue.getPrintQueue().contains(job),
                "Queue should contain the added print job");
    }

    @Test
    void testAddMultiplePrintJobs() {
        PrintJob job1 = new PrintJob("awesome.stl");
        PrintJob job2 = new PrintJob("awesome.stl");

        PrintQueue.add(job1);
        PrintQueue.add(job2);

        assertTrue(PrintQueue.getPrintQueue().contains(job1));
        assertTrue(PrintQueue.getPrintQueue().contains(job2));
    }

    @Test
    void testRemovePrintJob() {
        PrintJob job = new PrintJob("awesome.stl");

        PrintQueue.add(job);
        PrintQueue.remove(job);

        assertFalse(PrintQueue.getPrintQueue().contains(job),
                "Queue should no longer contain the removed job");
    }

    @Test
    void testRemoveJobDoesNotRemoveOtherJobs() {
        PrintJob job1 = new PrintJob("awesome.stl");
        PrintJob job2 = new PrintJob("awesome.stl");

        PrintQueue.add(job1);
        PrintQueue.add(job2);

        PrintQueue.remove(job1);

        assertFalse(PrintQueue.getPrintQueue().contains(job1));
        assertTrue(PrintQueue.getPrintQueue().contains(job2));
    }

    @Test
    void testAddingSameJobTwice() {
        PrintJob job = new PrintJob("awesome.stl");

        PrintQueue.add(job);
        PrintQueue.add(job);

        SLQueue<PrintJob> queue = PrintQueue.getPrintQueue();

        assertTrue(queue.contains(job));
        assertEquals(2, queue.size());
    }
}
