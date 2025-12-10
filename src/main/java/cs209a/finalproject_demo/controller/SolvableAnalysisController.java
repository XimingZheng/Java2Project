package cs209a.finalproject_demo.controller;

import cs209a.finalproject_demo.service.MultithreadingService;
import cs209a.finalproject_demo.service.SolvableAnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class SolvableAnalysisController {
    private final SolvableAnalysisService solvableAnalysisService;

    public SolvableAnalysisController(SolvableAnalysisService solvableAnalysisService) {
        this.solvableAnalysisService = solvableAnalysisService;
    }

    @GetMapping("/solvable")
    public ResponseEntity<Map<String, Object>> getSolvableAnalysis() {
        Map<String, Object> result = solvableAnalysisService.getAnalysis();
        return ResponseEntity.ok(result);
    }
}