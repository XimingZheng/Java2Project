package cs209a.finalproject_demo.controller;

import cs209a.finalproject_demo.service.TopicAnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/topics")
public class TopicAnalysisController {
    private final TopicAnalysisService topicAnalysisService;

    public TopicAnalysisController(TopicAnalysisService topicAnalysisService) {
        this.topicAnalysisService = topicAnalysisService;
    }

    @GetMapping("/trend")
    public ResponseEntity<Map<String, Object>> getTopicTrends(
            @RequestParam List<String> topics,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam (required = false, defaultValue = "month") String period) {

        Map<String, Object> result = topicAnalysisService.getTopicTrends(topics, startDate,
                endDate, period.toLowerCase());
        return ResponseEntity.ok(result);
    }

    /**
     * 获取可用主题列表
     */
    @GetMapping("/list")
    public ResponseEntity<List<String>> getAvailableTopics() {
        return ResponseEntity.ok(topicAnalysisService.getAvailableTopics());
    }

    /**
     * 获取主题活动度排名
     */
    @GetMapping("/activity")
    public ResponseEntity<Map<String, Object>> getTopicActivity(
            @RequestParam List<String> topics,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam (required = false, defaultValue = "month") String period) {

        Map<String, Object> result = topicAnalysisService.getTopicActivityScore(topics, startDate,
                endDate, period.toLowerCase());
        return ResponseEntity.ok(result);
    }
}