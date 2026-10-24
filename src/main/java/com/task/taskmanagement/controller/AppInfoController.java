package com.task.taskmanagement.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/info")
public class AppInfoController {

    @Value("${app.name}")
    private String appName;

    @Value("${app.version}")
    private String appVersion;

    @GetMapping
    public Map<String, String> getAppInfo() {
        return Map.of(
                "name", appName,
                "version", appVersion
        );
    }
}