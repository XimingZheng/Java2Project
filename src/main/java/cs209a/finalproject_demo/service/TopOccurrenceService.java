package cs209a.finalproject_demo.service;

import cs209a.finalproject_demo.model.StackOverflowThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TopOccurrenceService {
    private static final Logger logger = LoggerFactory.getLogger(TopOccurrenceService.class);

    private final DataLoaderService dataLoaderService;

    public TopOccurrenceService(DataLoaderService dataLoaderService) {
        this.dataLoaderService = dataLoaderService;
    }

    public Map<String, Object> getTopOccurrence(int n) {
        logger.info("Analyzing top {} co-occurrence pairs", n);

        List<StackOverflowThread> allThreads = dataLoaderService.getAllThreads();

        Map<String, Integer> coOccurrenceMap = new HashMap<>();

        for (StackOverflowThread thread : allThreads) {
            if (thread.getQuestion() == null || thread.getQuestion().getTags() == null) {
                continue;
            }
            
            List<String> tags = thread.getQuestion().getTags().stream()
                    .distinct()
                    .collect(Collectors.toList());

            for (int i = 0; i < tags.size(); i++) {
                for (int j = i + 1; j < tags.size(); j++) {
                    String tag1 = tags.get(i);
                    String tag2 = tags.get(j);
                    if (tag1.equals(tag2) || tag1.equals("java") || tag2.equals("java")) continue;

                    String key = tag1.compareTo(tag2) < 0 ? tag1 + "," + tag2 : tag2 + "," + tag1;

                    coOccurrenceMap.put(key, coOccurrenceMap.getOrDefault(key, 0) + 1);
                }
            }
        }

        List<CoOccurrencePair> topPairs = coOccurrenceMap.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(n)
                .map(entry -> {
                    String[] tags = entry.getKey().split(",");
                    return new CoOccurrencePair(tags[0], tags[1], entry.getValue());
                })
                .collect(Collectors.toList());

        logger.info("Found {} co-occurrence pairs", topPairs.size());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalPairs", coOccurrenceMap.size());
        result.put("topN", n);
        result.put("coOccurrences", topPairs);
        
        return result;
    }

    // DTO 类用于JSON序列化
    public static class CoOccurrencePair {
        private String tag1;
        private String tag2;
        private int count;

        public CoOccurrencePair(String tag1, String tag2, int count) {
            this.tag1 = tag1;
            this.tag2 = tag2;
            this.count = count;
        }

        public String getTag1() { return tag1; }
        public void setTag1(String tag1) { this.tag1 = tag1; }

        public String getTag2() { return tag2; }
        public void setTag2(String tag2) { this.tag2 = tag2; }

        public int getCount() { return count; }
        public void setCount(int count) { this.count = count; }
    }
}
