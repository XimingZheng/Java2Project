package cs209a.finalproject_demo.controller;

import cs209a.finalproject_demo.service.MultithreadingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/multithreading")
public class MultithreadingController {
    private final MultithreadingService multithreadingService;

    public MultithreadingController(MultithreadingService multithreadingService) {
        this.multithreadingService = multithreadingService;
    }
    @GetMapping("/top")
    public ResponseEntity<Map<String, Object>> getTopCoOccurrence(
            @RequestParam(defaultValue = "5") int n) {

        Map<String, Object> result = multithreadingService.getRecurrenceProblems(n);
        return ResponseEntity.ok(result);
    }
}
