package com.paulo.nader.demo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;

@Repository
public class DatabaseController {
    @Autowired
    private DataSource dataSource;

    @PostConstruct
    public void init() {
        
    }

    public SLQueue<PrintJob> getQueuedPrints() throws SQLException{
        SLQueue<PrintJob> queuedPrints = new SLQueue<>();

        //Loading queue from database
        Connection conn = dataSource.getConnection();
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
        return queuedPrints;
    }

    public void savePrintJob(PrintJob printJob) throws SQLException {
        try (Connection conn = dataSource.getConnection();) {
            String sqlQuery = "INSERT INTO queued_prints (id, queue_order, file_path, user_id, est_print_time) VALUES (?, (SELECT IFNULL(MAX(queue_order), 0) + 1 FROM queued_prints), ?, ?, ?);";
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
}
