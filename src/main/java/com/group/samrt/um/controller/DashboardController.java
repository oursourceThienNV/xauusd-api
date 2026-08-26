package com.group.samrt.um.controller;

import com.group.samrt.um.client.client.response.DashboardResponse;
import com.group.samrt.um.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;


    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard() {

        return ResponseEntity.ok(
                dashboardService.getDashboard()
        );
    }
}