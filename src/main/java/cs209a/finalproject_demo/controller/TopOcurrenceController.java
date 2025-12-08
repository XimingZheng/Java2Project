package cs209a.finalproject_demo.controller;

import cs209a.finalproject_demo.service.TopOccurrenceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/occurrence")
public class TopOcurrenceController {
    private final TopOccurrenceService topOccurrenceService;

    public TopOcurrenceController(TopOccurrenceService topOccurrenceService) {
        this.topOccurrenceService = topOccurrenceService;
    }
    
    /**
     * 获取标签共现频率最高的 N 对
     * 示例: GET /api/occurrence/top?n=10
     */
    @GetMapping("/top")
    public ResponseEntity<Map<String, Object>> getTopCoOccurrence(
            @RequestParam(defaultValue = "10") int n) {

        Map<String, Object> result = topOccurrenceService.getTopOccurrence(n);
        return ResponseEntity.ok(result);
    }
}
