package com.example.devops.maven.springboot.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/mtls/hello")
    public String sayHello(@RequestParam String name) {
        return "Hi, " + name + "!";
    }
}