package com.bmsp.bmsp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    
    @GetMapping("/")
    public String home() {
        return "Welcome to BMSP";
    }
    
    @GetMapping("/test")
    public String test() {
        return "Test endpoint";
    }
}