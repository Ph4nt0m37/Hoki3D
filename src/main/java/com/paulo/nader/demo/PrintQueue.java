package com.paulo.nader.demo;

//Singleton design pattern
public class PrintQueue {
    private static SLQueue<PrintJob> queue = new SLQueue<>();

    private PrintQueue() {
        queue = new SLQueue<>();
    }

    public static SLQueue<PrintJob> getPrintQueue() {
        return queue;
    }

    public static void add(PrintJob job) {
        queue.enqueue(job);
    }

    public static void remove(PrintJob job) {
        queue.remove(job);
    }
}
