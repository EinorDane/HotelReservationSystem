package com.hotelres.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api") // ✅ Ensures the base path "/api" is mapped correctly
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "Welcome to the Hotel Reservation System!";
    }

    @GetMapping("/status")
    public String checkStatus() {
        return "Backend is running!";
    }
}