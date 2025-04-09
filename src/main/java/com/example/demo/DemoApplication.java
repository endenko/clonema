package com.example.demo.controller;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class sdfsdf {

    @PostMapping("/receive")
    public Map<String, Object> receiveData(@RequestBody Map<String, Object> data) {
        System.out.println("Received data: " + data);
        return Map.of("status", "success", "receivedData", data);
    }
}
