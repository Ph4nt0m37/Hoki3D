package com.paulo.nader.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

class PrintControllerTest {

    private PrintController printController;
    private PrintQueue printQueue;
    private DatabaseController databaseController;
    private SLQueue<PrintJob> queue;

    @BeforeEach
    void setUp() {
        printController = new PrintController();
        printQueue = mock(PrintQueue.class);
        databaseController = mock(DatabaseController.class);
        queue = new SLQueue<>();

        ReflectionTestUtils.setField(printController, "printQueue", printQueue);
        ReflectionTestUtils.setField(
                printController,
                "databaseController",
                databaseController);
        when(printQueue.getPrintQueue()).thenReturn(queue);
    }

    @AfterEach
    void removeTestFiles() throws IOException {
        Files.deleteIfExists(testUploadPath("PrintControllerTest_model.stl"));
        Files.deleteIfExists(testUploadPath("PrintControllerTest_printing.stl"));
    }

    @Test
    void getPrintQueueReturnsAllQueuedJobs() {
        PrintJob first = new PrintJob(1L, "first.stl", "user-1");
        PrintJob second = new PrintJob(2L, "second.stl", "user-1");
        queue.enqueue(first);
        queue.enqueue(second);

        ResponseEntity<List<PrintJob>> response =
                printController.getPrintQueue();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertSame(first, response.getBody().get(0));
        assertSame(second, response.getBody().get(1));
    }

    @Test
    void getNextPrintReturnsNextJob() {
        PrintJob next = new PrintJob(1L, "next.stl", "user-1");
        queue.enqueue(next);

        ResponseEntity<PrintJob> response =
                printController.getNextPrint();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(next, response.getBody());
    }

    @Test
    void getNextPrintReturnsInternalServerErrorForEmptyQueue() {
        ResponseEntity<PrintJob> response =
                printController.getNextPrint();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void addPrintCopiesFileAndAddsJobToQueue() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename())
                .thenReturn("PrintControllerTest_model.stl");
        when(file.getInputStream())
                .thenReturn(new ByteArrayInputStream(new byte[10000]));
        when(file.getSize()).thenReturn(10000L);
        when(printQueue.add(any(PrintJob.class))).thenReturn(true);

        ResponseEntity<PrintJob> response =
                printController.addPrint(file, "user-42");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("PrintControllerTest_model.stl",
                response.getBody().getFilePath());
        assertEquals("user-42", response.getBody().getUserId());
        assertEquals(5L, response.getBody().getEstimatedPrintTime());
        assertEquals(true, Files.exists(testUploadPath(
                "PrintControllerTest_model.stl")));

        ArgumentCaptor<PrintJob> jobCaptor =
                ArgumentCaptor.forClass(PrintJob.class);
        verify(printQueue).add(jobCaptor.capture());
        assertEquals(response.getBody(), jobCaptor.getValue());
    }

    @Test
    void addPrintRejectsNonStlFile() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("model.obj");

        ResponseEntity<PrintJob> response =
                printController.addPrint(file, "user-42");

        assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void addPrintReturnsInternalServerErrorWhenFileCopyFails()
            throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename())
                .thenReturn("PrintControllerTest_model.stl");
        when(file.getInputStream())
                .thenThrow(new IOException("Copy failed"));

        ResponseEntity<PrintJob> response =
                printController.addPrint(file, "user-42");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void addPrintReturnsInternalServerErrorWhenQueueRejectsJob()
            throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename())
                .thenReturn("PrintControllerTest_model.stl");
        when(file.getInputStream())
                .thenReturn(new ByteArrayInputStream(new byte[1]));
        when(file.getSize()).thenReturn(1L);
        when(printQueue.add(any(PrintJob.class))).thenReturn(false);

        ResponseEntity<PrintJob> response =
                printController.addPrint(file, "user-42");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void removePrintRejectsNullId() {
        assertEquals(
                HttpStatus.BAD_REQUEST,
                printController.removePrint(null).getStatusCode());
    }

    @Test
    void removePrintRejectsBlankId() {
        assertEquals(
                HttpStatus.BAD_REQUEST,
                printController.removePrint("  ").getStatusCode());
    }

    @Test
    void removePrintRejectsNonNumericId() {
        assertEquals(
                HttpStatus.BAD_REQUEST,
                printController.removePrint("not-a-number").getStatusCode());
    }

    @Test
    void removePrintReturnsNotFoundWhenJobIsNotQueued() {
        queue.enqueue(new PrintJob(1L, "other.stl", "user-1"));

        assertEquals(
                HttpStatus.NOT_FOUND,
                printController.removePrint("999").getStatusCode());
    }

    @Test
    void removePrintReturnsRemovedJobWhenQueueAcceptsRemoval() {
        PrintJob printJob = new PrintJob(1L, "model.stl", "user-1");
        queue.enqueue(printJob);
        when(printQueue.remove(printJob)).thenReturn(true);

        ResponseEntity<?> response =
                printController.removePrint("1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(printJob, response.getBody());
        verify(printQueue).remove(printJob);
    }

    @Test
    void removePrintReturnsInternalServerErrorWhenQueueRejectsRemoval() {
        PrintJob printJob = new PrintJob(1L, "model.stl", "user-1");
        queue.enqueue(printJob);
        when(printQueue.remove(printJob)).thenReturn(false);

        assertEquals(
                HttpStatus.INTERNAL_SERVER_ERROR,
                printController.removePrint("1").getStatusCode());
    }

    @Test
    void startPrintReturnsNextJob() throws SQLException {
        PrintJob next = new PrintJob(1L, "next.stl", "user-1");
        when(printQueue.startNextPrint()).thenReturn(next);

        ResponseEntity<PrintJob> response =
                printController.startPrint();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(next, response.getBody());
    }

    @Test
    void startPrintReturnsOkWithNoBodyForEmptyQueue() {
        when(printQueue.startNextPrint())
                .thenThrow(new NoSuchElementException());

        ResponseEntity<PrintJob> response =
                printController.startPrint();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getUserIdReturnsCookieValue() {
        ResponseEntity<String> response =
                printController.getUserId("user-42");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("user-42", response.getBody());
    }

    @Test
    void getCompletedPrintsReturnsDatabaseRows() throws SQLException {
        List<PrintJob> completed = List.of(
                new PrintJob(
                        1L,
                        "first.stl",
                        PrintStatus.COMPLETED,
                        "user-42"),
                new PrintJob(
                        2L,
                        "second.stl",
                        PrintStatus.CANCELLED,
                        "user-42"));
        when(databaseController.getCompletedPrints("user-42"))
                .thenReturn(completed);

        ResponseEntity<List<PrintJob>> response =
                printController.getCompletedPrints("user-42");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(completed, response.getBody());
    }

    @Test
    void getCompletedPrintsReturnsInternalServerErrorOnDatabaseFailure()
            throws SQLException {
        when(databaseController.getCompletedPrints("user-42"))
                .thenThrow(new SQLException("Database unavailable"));

        ResponseEntity<List<PrintJob>> response =
                printController.getCompletedPrints("user-42");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getCurrentPrintReturnsJobWithCalculatedEstimate()
            throws IOException, SQLException {
        Files.write(testUploadPath("PrintControllerTest_printing.stl"),
                new byte[10000]);
        PrintJob current = new PrintJob(
                1L,
                "PrintControllerTest_printing.stl",
                PrintStatus.PRINTING,
                "user-42");
        when(databaseController.getCurrentPrint()).thenReturn(current);

        ResponseEntity<PrintJob> response =
                printController.getCurrentPrint();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(current, response.getBody());
        assertEquals(5L, current.getEstimatedPrintTime());
    }

    @Test
    void getCurrentPrintReturnsNullWhenNoPrintIsPrinting()
            throws SQLException {
        when(databaseController.getCurrentPrint()).thenReturn(null);

        ResponseEntity<PrintJob> response =
                printController.getCurrentPrint();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getCurrentPrintReturnsInternalServerErrorOnDatabaseFailure()
            throws SQLException {
        when(databaseController.getCurrentPrint())
                .thenThrow(new SQLException("Database unavailable"));

        ResponseEntity<PrintJob> response =
                printController.getCurrentPrint();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    private static Path testUploadPath(String fileName) {
        return Path.of(
                "src",
                "main",
                "resources",
                "uploadedFiles",
                fileName);
    }
}
