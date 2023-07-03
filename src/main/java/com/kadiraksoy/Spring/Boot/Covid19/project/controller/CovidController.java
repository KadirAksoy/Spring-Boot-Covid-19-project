package com.kadiraksoy.Spring.Boot.Covid19.project.controller;

import com.kadiraksoy.Spring.Boot.Covid19.project.model.LocationStats;
import com.kadiraksoy.Spring.Boot.Covid19.project.service.CovidService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;

import java.util.List;

@Controller
@RequestMapping("/api/")
public class CovidController {


    private final CovidService covidService;

    public CovidController(CovidService covidService) {
        this.covidService = covidService;
    }

    @GetMapping("covid")
    public String home(Model model) {
        List<LocationStats> allStats = covidService.getAllStats();
        int totalReportedCases = allStats.stream().mapToInt(stat -> stat.getLatestTotalCases()).sum();
        int totalNewCases = allStats.stream().mapToInt(stat -> stat.getDiffFromPrevDay()).sum();
        model.addAttribute("locationStats", allStats);
        model.addAttribute("totalReportedCases", totalReportedCases);
        model.addAttribute("totalNewCases", totalNewCases);

        return "home";
    }
}
