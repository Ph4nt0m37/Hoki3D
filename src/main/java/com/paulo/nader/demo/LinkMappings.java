package com.paulo.nader.demo;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller 
public class LinkMappings {
    @GetMapping("/")
    public ResponseEntity<String> getMainPage(HttpServletRequest request, HttpServletResponse response) {
        //generate user cookie (if no cookie exists)
        Cookie[] userCookies = request.getCookies();
        if (userCookies!=null) {
            for (Cookie cookie:userCookies) {
                if (cookie.getName().equals("userId")) {
                    return new ResponseEntity<>("index.html", HttpStatus.OK);
                }
            }
        }

        Cookie userCookie = new Cookie("userId",UUID.randomUUID().toString());
        userCookie.setSecure(true);
        userCookie.setHttpOnly(true);
        response.addCookie(userCookie);

        return new ResponseEntity<>("index.html", HttpStatus.OK);
    }
}
