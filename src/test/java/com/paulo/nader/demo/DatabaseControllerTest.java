package com.paulo.nader.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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
                "REPLACE INTO queued_prints (id, queue_order, file_path, user_id, est_print_time) "
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

    @Test
    void saveCompletedPrintBindsAllValuesAndExecutesUpdate() throws SQLException {
        PreparedStatement statement = mock(PreparedStatement.class);
        PrintJob printJob = new PrintJob(
                3001L,
                "uploads/completed.stl",
                PrintStatus.COMPLETED,
                "user-42");

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(
                "REPLACE INTO completed_prints (id, file_path, status, user_id) "
                        + "VALUES (?, ?, ?, ?);"))
                .thenReturn(statement);

        databaseController.saveCompletedPrint(printJob);

        verify(statement).setLong(1, 3001L);
        verify(statement).setString(2, "uploads/completed.stl");
        verify(statement).setString(3, "COMPLETED");
        verify(statement).setString(4, "user-42");
        verify(statement).executeUpdate();
    }

    @Test
    void getCompletedPrintsLoadsMatchingUserRows() throws SQLException {
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet results = mock(ResultSet.class);

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(
                "SELECT * FROM completed_prints WHERE user_id=?;"))
                .thenReturn(statement);
        when(statement.executeQuery()).thenReturn(results);
        when(results.next()).thenReturn(true, true, false);
        when(results.getLong("id")).thenReturn(3001L, 3002L);
        when(results.getString("file_path"))
                .thenReturn("uploads/first.stl", "uploads/second.stl");
        when(results.getString("status"))
                .thenReturn("COMPLETED", "CANCELLED");

        List<PrintJob> completedPrints =
                databaseController.getCompletedPrints("user-42");

        verify(statement).setString(1, "user-42");
        assertEquals(2, completedPrints.size());
        assertEquals(3001L, completedPrints.get(0).getPrintId());
        assertEquals("uploads/first.stl", completedPrints.get(0).getFilePath());
        assertEquals(PrintStatus.COMPLETED, completedPrints.get(0).getStatus());
        assertEquals("user-42", completedPrints.get(0).getUserId());
        assertEquals(3002L, completedPrints.get(1).getPrintId());
        assertEquals("uploads/second.stl", completedPrints.get(1).getFilePath());
        assertEquals(PrintStatus.CANCELLED, completedPrints.get(1).getStatus());
        assertEquals("user-42", completedPrints.get(1).getUserId());
    }

    @Test
    void getCompletedPrintsReturnsEmptyListWhenUserHasNoRows() throws SQLException {
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet results = mock(ResultSet.class);

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(
                "SELECT * FROM completed_prints WHERE user_id=?;"))
                .thenReturn(statement);
        when(statement.executeQuery()).thenReturn(results);
        when(results.next()).thenReturn(false);

        List<PrintJob> completedPrints =
                databaseController.getCompletedPrints("unknown-user");

        verify(statement).setString(1, "unknown-user");
        assertTrue(completedPrints.isEmpty());
    }

    @Test
    void getCurrentPrintLoadsPrintingRow() throws SQLException {
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet results = mock(ResultSet.class);

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(
                "SELECT * FROM completed_prints WHERE status='PRINTING' LIMIT 1;"))
                .thenReturn(statement);
        when(statement.executeQuery()).thenReturn(results);
        when(results.next()).thenReturn(true);
        when(results.getLong("id")).thenReturn(4001L);
        when(results.getString("file_path")).thenReturn("uploads/printing.stl");
        when(results.getString("user_id")).thenReturn("user-42");

        PrintJob currentPrint = databaseController.getCurrentPrint();

        assertEquals(4001L, currentPrint.getPrintId());
        assertEquals("uploads/printing.stl", currentPrint.getFilePath());
        assertEquals(PrintStatus.PRINTING, currentPrint.getStatus());
        assertEquals("user-42", currentPrint.getUserId());
    }

    @Test
    void getCurrentPrintReturnsNullWhenNoPrintingRowExists() throws SQLException {
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet results = mock(ResultSet.class);

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(
                "SELECT * FROM completed_prints WHERE status='PRINTING' LIMIT 1;"))
                .thenReturn(statement);
        when(statement.executeQuery()).thenReturn(results);
        when(results.next()).thenReturn(false);

        assertNull(databaseController.getCurrentPrint());
    }

    @Test
    void saveCompletedPrintPropagatesDatabaseException() throws SQLException {
        SQLException databaseFailure = new SQLException("Database unavailable");
        when(dataSource.getConnection()).thenThrow(databaseFailure);

        SQLException thrown = assertThrows(
                SQLException.class,
                () -> databaseController.saveCompletedPrint(
                        new PrintJob(
                                3001L,
                                "uploads/completed.stl",
                                PrintStatus.COMPLETED,
                                "user-42")));

        assertEquals("Database unavailable", thrown.getMessage());
    }

    @Test
    void getCompletedPrintsPropagatesDatabaseException() throws SQLException {
        SQLException databaseFailure = new SQLException("Database unavailable");
        when(dataSource.getConnection()).thenThrow(databaseFailure);

        SQLException thrown = assertThrows(
                SQLException.class,
                () -> databaseController.getCompletedPrints("user-42"));

        assertEquals("Database unavailable", thrown.getMessage());
    }

    @Test
    void getCurrentPrintPropagatesDatabaseException() throws SQLException {
        SQLException databaseFailure = new SQLException("Database unavailable");
        when(dataSource.getConnection()).thenThrow(databaseFailure);

        SQLException thrown = assertThrows(
                SQLException.class,
                () -> databaseController.getCurrentPrint());

        assertEquals("Database unavailable", thrown.getMessage());
    }
}
