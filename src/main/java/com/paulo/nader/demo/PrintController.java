package com.paulo.nader.demo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

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
        return new ResponseEntity<>(printQueue.peek(), HttpStatus.OK);
    }

    @PostMapping("/api/add-print")
    public ResponseEntity<PrintJob> getNextPrint(PrintJob submittedPrint, @CookieValue("userId") String userId) {
        submittedPrint.setUserId(userId);
        return new ResponseEntity<>(printQueue.enqueue(submittedPrint), HttpStatus.OK);
    }
}
