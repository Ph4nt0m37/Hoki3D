package com.paulo.nader.demo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;

@Repository
public class DatabaseController {
    @Autowired
    private DataSource dataSource;

    public SLQueue<PrintJob> getQueuedPrints() throws SQLException{
        SLQueue<PrintJob> queuedPrints = new SLQueue<>();

        //Loading queue from database
        try (Connection conn = dataSource.getConnection();) {
            String sqlQuery = "SELECT * FROM queued_prints ORDER BY queue_order;";
            PreparedStatement statement = conn.prepareStatement(sqlQuery);

            ResultSet results = statement.executeQuery();
            
            while (results.next()) {
                long printId = results.getLong("id");
                String filePath = results.getString("file_path");
                String userId = results.getString("user_id");
                long estPrintTime = results.getLong("est_print_time");
                PrintJob printJob = new PrintJob(printId, filePath, userId);
                printJob.setEstimatedPrintTime(estPrintTime);
                queuedPrints.enqueue(printJob);
            }
        }
        return queuedPrints;
    }

    public void savePrintJob(PrintJob printJob) throws SQLException {
        try (Connection conn = dataSource.getConnection();) {
            String sqlQuery = "REPLACE INTO queued_prints (id, queue_order, file_path, user_id, est_print_time) VALUES (?, (SELECT IFNULL(MAX(queue_order), 0) + 1 FROM queued_prints), ?, ?, ?);";
            PreparedStatement statement = conn.prepareStatement(sqlQuery);
            statement.setLong(1, printJob.getPrintId());
            statement.setString(2, printJob.getFilePath());
            statement.setString(3, printJob.getUserId());
            statement.setLong(4, printJob.getEstimatedPrintTime());

            statement.executeUpdate();
        }
    }

    public void removePrintJob(long printId) throws SQLException {
        try (Connection conn = dataSource.getConnection();) {
            String sqlQuery = "DELETE FROM queued_prints WHERE id=?;";
            PreparedStatement statement = conn.prepareStatement(sqlQuery);
            statement.setLong(1, printId);

            statement.executeUpdate();
        }
    }

    public void saveCompletedPrint(PrintJob printJob) throws SQLException {
        try (Connection conn = dataSource.getConnection();) {
            String sqlQuery = "REPLACE INTO completed_prints (id, file_path, status, user_id) VALUES (?, ?, ?, ?);";
            PreparedStatement statement = conn.prepareStatement(sqlQuery);
            statement.setLong(1, printJob.getPrintId());
            statement.setString(2, printJob.getFilePath());
            statement.setString(3, printJob.getStatus().toString());
            statement.setString(4, printJob.getUserId());

            statement.executeUpdate();
        }
    }

    public List<PrintJob> getCompletedPrints(String userId) throws SQLException{
        List<PrintJob> completedPrints = new ArrayList<>();

        //Loading list from database
        try (Connection conn = dataSource.getConnection();) {
            String sqlQuery = "SELECT * FROM completed_prints WHERE user_id=?;";
            PreparedStatement statement = conn.prepareStatement(sqlQuery);
            statement.setString(1, userId);

            ResultSet results = statement.executeQuery();
            
            while (results.next()) {
                long printId = results.getLong("id");
                String filePath = results.getString("file_path");
                String status = results.getString("status");
                PrintJob printJob = new PrintJob(printId, filePath, PrintStatus.valueOf(status), userId);
                completedPrints.add(printJob);
            }
        }
        return completedPrints;
    }

    public PrintJob getCurrentPrint() throws SQLException{
        try (Connection conn = dataSource.getConnection();) {
            //Loading current print
            String sqlQuery = "SELECT * FROM completed_prints WHERE status='PRINTING' LIMIT 1;";
            PreparedStatement statement = conn.prepareStatement(sqlQuery);

            ResultSet results = statement.executeQuery();
            
            if (results.next()) {
                long printId = results.getLong("id");
                String filePath = results.getString("file_path");
                String userId = results.getString("user_id");
                PrintJob printJob = new PrintJob(printId, filePath, PrintStatus.PRINTING, userId);
                return printJob;
            }
        }
        return null;
    }
}
