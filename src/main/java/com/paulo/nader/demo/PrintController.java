package com.paulo.nader.demo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.NoSuchElementException;

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
    private SLQueue<PrintJob> printQueue = new SLQueue<>();
    private PrintJob currentPrint;

    @GetMapping("/api/get-print-queue")
    public ResponseEntity<SLQueue> getPrintQueue() {
        return new ResponseEntity<>(printQueue, HttpStatus.OK);
    }

    @GetMapping("/api/get-next-print")
    public ResponseEntity<PrintJob> getNextPrint() {
        try {
            return new ResponseEntity<>(printQueue.peek(), HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/api/add-print")
    public ResponseEntity<PrintJob> getNextPrint(@RequestParam MultipartFile file, @CookieValue String userId) {
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
        printQueue.enqueue(printJob);
        return new ResponseEntity<>(printJob, HttpStatus.OK);
    }
}
