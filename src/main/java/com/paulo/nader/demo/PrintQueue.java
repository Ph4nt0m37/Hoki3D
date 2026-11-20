package com.paulo.nader.demo;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Provides static access to the shared queue of print jobs.
 */
@Service
public class PrintQueue {
    private PrintJob currentPrint;
    private SLQueue<PrintJob> queue = new SLQueue<>();

    private DatabaseController databaseController; //Spring will autoinject databaseController

    /**
     * Initializes the queue and prevents instances from being created.
     */
    public PrintQueue(DatabaseController databaseController) {
        this.databaseController = databaseController;
        try {
            queue = databaseController.getQueuedPrints();
            currentPrint = databaseController.getCurrentPrint();
        }catch(SQLException e) {
            System.err.println("Something went wrong loading the print queue from the database. ");
            e.printStackTrace();
        }
    }

    /**
     * Returns the shared print queue.
     *
     * @return the live queue of print jobs
     */
    public SLQueue<PrintJob> getPrintQueue() {
        return queue;
    }

    /**
     * Adds a print job to the end of the shared queue.
     *
     * @param job the print job to add
     * @throws IllegalArgumentException if {@code job} is {@code null}
     */
    public boolean add(PrintJob job) {
        try {
            databaseController.savePrintJob(job);
            queue.enqueue(job);

            return true;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Attempts to remove a print job from the shared queue.
     *
     * @param job the print job to remove
     */
    public boolean remove(PrintJob job) {
        try {
            databaseController.removePrintJob(job.getPrintId());
            queue.remove(job);

            return true;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    public PrintJob startNextPrint() throws NoSuchElementException {
        try {
            // save the current print as completed, then get next current print
            if (currentPrint != null) {
                currentPrint.setStatus(PrintStatus.COMPLETED);
                databaseController.saveCompletedPrint(currentPrint);
            }

            currentPrint = queue.dequeue();
            currentPrint.setStatus(PrintStatus.PRINTING);
            databaseController.saveCompletedPrint(currentPrint);
            databaseController.removePrintJob(currentPrint.getPrintId());

            return currentPrint;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
}
