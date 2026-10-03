package com.smartbus.smartbusapi.controller;

import com.smartbus.smartbusapi.dto.DashboardDto;
import com.smartbus.smartbusapi.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Panel de indicadores: GET /api/dashboard
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public DashboardDto obtener() {
        return dashboardService.obtener();
    }
}
