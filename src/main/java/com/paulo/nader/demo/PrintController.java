package com.paulo.nader.demo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class PrintController {
    @Autowired
    private PrintQueue printQueue;

    @Autowired
    private DatabaseController databaseController;

    @GetMapping("/api/get-print-queue")
    public ResponseEntity<List<PrintJob>> getPrintQueue() {
        return new ResponseEntity<>(printQueue.getPrintQueue().toList(), HttpStatus.OK);
    }

    @GetMapping("/api/get-next-print")
    public ResponseEntity<PrintJob> getNextPrint() {
        try {
            return new ResponseEntity<>(printQueue.getPrintQueue().peek(), HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/api/add-print")
    public ResponseEntity<PrintJob> addPrint(@RequestParam MultipartFile file, @CookieValue String userId) {
        //validate that the file is an STL
        String[] splitName = file.getOriginalFilename().split("\\.");
        if (!splitName[splitName.length - 1].equalsIgnoreCase("STL")) return new ResponseEntity<>(HttpStatus.UNSUPPORTED_MEDIA_TYPE);

        String uploadedFilePath = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            String uploadedFilesFolderPath = "src\\main\\resources\\uploadedFiles\\".replace("\\", File.separator);
            Files.copy(file.getInputStream(), Path.of(uploadedFilesFolderPath+uploadedFilePath), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        PrintJob printJob = new PrintJob((long) (Math.random() * 9999999999l), uploadedFilePath, userId);
        printJob.setEstimatedPrintTime((long)(file.getSize()*(3/5000.0)));
        if (printQueue.add(printJob)) {
            return new ResponseEntity<>(printJob, HttpStatus.OK);
        }else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/api/remove-print")
    public ResponseEntity<?> removePrint(String printId) {
        if (printId == null || printId.isBlank()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        final long id;
        try {
            id = Long.parseLong(printId);
        } catch (NumberFormatException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        for (PrintJob printJob : printQueue.getPrintQueue().toList()) {
            if (printJob.getPrintId() == id) {
                if (printQueue.remove(printJob)) {
                    return new ResponseEntity<>(printJob, HttpStatus.OK);
                }
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/api/start-next-print")
    public ResponseEntity<PrintJob> startPrint() {
        try {
            PrintJob nextPrint = printQueue.startNextPrint();
            return new ResponseEntity<>(nextPrint, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            //No more prints in the queue. No need to freak out, just return nothing.
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @GetMapping("/api/get-user-id")
    public ResponseEntity<String> getUserId(@CookieValue String userId) {
        return new ResponseEntity<>(userId, HttpStatus.OK);
    }

    @GetMapping("/api/get-completed-prints")
    public ResponseEntity<List<PrintJob>> getCompletedPrints(@CookieValue String userId) {
        try {
            return new ResponseEntity<>(databaseController.getCompletedPrints(userId), HttpStatus.OK);
        } catch (SQLException e) {
            System.err.println("Something went wrong fetching completed prints from database.");
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/api/get-current-print")
    public ResponseEntity<PrintJob> getCurrentPrint() {
        try {
            PrintJob currentPrint = databaseController.getCurrentPrint();
            if (currentPrint != null) {
                String uploadedFilesFolderPath = "src\\main\\resources\\uploadedFiles\\".replace("\\", File.separator);
                File printFile = new File(uploadedFilesFolderPath+currentPrint.getFilePath());
                currentPrint.setEstimatedPrintTime((long)(printFile.length()*(3/5000.0)));
            }
            
            return new ResponseEntity<>(currentPrint, HttpStatus.OK);
        } catch (SQLException e) {
            System.err.println("Something went wrong fetching completed prints from database.");
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
