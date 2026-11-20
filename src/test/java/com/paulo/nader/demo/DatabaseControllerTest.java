package com.paulo.nader.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class DatabaseControllerTest {

    private DatabaseController databaseController;
    private DataSource dataSource;
    private Connection connection;

    @BeforeEach
    void setUp() {
        databaseController = new DatabaseController();
        dataSource = mock(DataSource.class);
        connection = mock(Connection.class);
        ReflectionTestUtils.setField(databaseController, "dataSource", dataSource);
    }

    @Test
    void getQueuedPrintsLoadsRowsInDatabaseOrder() throws SQLException {
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet results = mock(ResultSet.class);

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(
                "SELECT * FROM queued_prints ORDER BY queue_order;"))
                .thenReturn(statement);
        when(statement.executeQuery()).thenReturn(results);
        when(results.next()).thenReturn(true, true, false);
        when(results.getLong("id")).thenReturn(2001L, 2002L);
        when(results.getString("file_path"))
                .thenReturn("uploads/model.stl", "uploads/second.stl");
        when(results.getString("user_id"))
                .thenReturn("user-42", "user-99");
        when(results.getLong("est_print_time")).thenReturn(120L, 240L);

        SLQueue<PrintJob> queuedPrints = databaseController.getQueuedPrints();

        assertEquals(2, queuedPrints.size());
        assertEquals(2001L, queuedPrints.peek().getPrintId());
        assertEquals("uploads/model.stl", queuedPrints.peek().getFilePath());
        assertEquals("user-42", queuedPrints.peek().getUserId());
        assertEquals(120L, queuedPrints.peek().getEstimatedPrintTime());

        PrintJob secondJob = queuedPrints.toList().get(1);
        assertEquals(2002L, secondJob.getPrintId());
        assertEquals("uploads/second.stl", secondJob.getFilePath());
        assertEquals("user-99", secondJob.getUserId());
        assertEquals(240L, secondJob.getEstimatedPrintTime());
    }

    @Test
    void getQueuedPrintsReturnsEmptyQueueWhenDatabaseHasNoRows() throws SQLException {
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet results = mock(ResultSet.class);

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(
                "SELECT * FROM queued_prints ORDER BY queue_order;"))
                .thenReturn(statement);
        when(statement.executeQuery()).thenReturn(results);
        when(results.next()).thenReturn(false);

        SLQueue<PrintJob> queuedPrints = databaseController.getQueuedPrints();

        assertTrue(queuedPrints.isEmpty());
    }

    @Test
    void savePrintJobBindsAllValuesAndExecutesUpdate() throws SQLException {
        PreparedStatement statement = mock(PreparedStatement.class);
        PrintJob printJob = new PrintJob(
                2001L, "uploads/model.stl", "user-42");
        printJob.setEstimatedPrintTime(120L);

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(
                "INSERT INTO queued_prints (id, queue_order, file_path, user_id, est_print_time) "
                        + "VALUES (?, (SELECT IFNULL(MAX(queue_order), 0) + 1 FROM queued_prints), ?, ?, ?);"))
                .thenReturn(statement);

        databaseController.savePrintJob(printJob);

        verify(statement).setLong(1, 2001L);
        verify(statement).setString(2, "uploads/model.stl");
        verify(statement).setString(3, "user-42");
        verify(statement).setLong(4, 120L);
        verify(statement).executeUpdate();
    }

    @Test
    void removePrintJobBindsPrintIdAndExecutesUpdate() throws SQLException {
        PreparedStatement statement = mock(PreparedStatement.class);

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(
                "DELETE FROM queued_prints WHERE id=?;"))
                .thenReturn(statement);

        databaseController.removePrintJob(2001L);

        verify(statement).setLong(1, 2001L);
        verify(statement).executeUpdate();
    }

    @Test
    void getQueuedPrintsPropagatesDatabaseException() throws SQLException {
        SQLException databaseFailure = new SQLException("Database unavailable");
        when(dataSource.getConnection()).thenThrow(databaseFailure);

        SQLException thrown = assertThrows(
                SQLException.class,
                () -> databaseController.getQueuedPrints());

        assertEquals("Database unavailable", thrown.getMessage());
    }

    @Test
    void savePrintJobPropagatesDatabaseException() throws SQLException {
        SQLException databaseFailure = new SQLException("Database unavailable");
        when(dataSource.getConnection()).thenThrow(databaseFailure);

        SQLException thrown = assertThrows(
                SQLException.class,
                () -> databaseController.savePrintJob(
                        new PrintJob("uploads/model.stl")));

        assertEquals("Database unavailable", thrown.getMessage());
    }

    @Test
    void removePrintJobPropagatesDatabaseException() throws SQLException {
        SQLException databaseFailure = new SQLException("Database unavailable");
        when(dataSource.getConnection()).thenThrow(databaseFailure);

        SQLException thrown = assertThrows(
                SQLException.class,
                () -> databaseController.removePrintJob(2001L));

        assertEquals("Database unavailable", thrown.getMessage());
    }
}
