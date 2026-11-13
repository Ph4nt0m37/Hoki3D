package com.paulo.nader.demo;

//Singleton design pattern
/**
 * Provides static access to the shared queue of print jobs.
 */
public class PrintQueue {
    private static SLQueue<PrintJob> queue = new SLQueue<>();

    /**
     * Initializes the queue and prevents instances from being created.
     */
    private PrintQueue() {
        queue = new SLQueue<>();
    }

    /**
     * Returns the shared print queue.
     *
     * @return the live queue of print jobs
     */
    public static SLQueue<PrintJob> getPrintQueue() {
        return queue;
    }

    /**
     * Adds a print job to the end of the shared queue.
     *
     * @param job the print job to add
     * @throws IllegalArgumentException if {@code job} is {@code null}
     */
    public static void add(PrintJob job) {
        queue.enqueue(job);
    }

    /**
     * Attempts to remove a print job from the shared queue.
     *
     * @param job the print job to remove
     */
    public static void remove(PrintJob job) {
        queue.remove(job);
    }
}
