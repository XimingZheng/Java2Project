package cs209a.finalproject_demo.service;

import cs209a.finalproject_demo.config.TopicKeywords;
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
            if (thread.getQuestion() == null) {
                continue;
            }
            
            Set<String> topics = new HashSet<>();
            
            if (thread.getQuestion().getTags() != null) {
                List<String> tags = thread.getQuestion().getTags().stream()
                        .distinct()
                        .collect(Collectors.toList());
                
                tags.stream()
                        .map(TopicKeywords::mapTagToTopic)
                        .filter(Objects::nonNull)
                        .forEach(topics::add);
            }
            
            if (thread.getQuestion().getTitle() != null) {
                String title = thread.getQuestion().getTitle().toLowerCase();
                for (String topic : TopicKeywords.getAllTopics()) {
                    List<String> keywords = TopicKeywords.getKeywordsForTopic(topic);
                    if (keywords.stream().anyMatch(keyword -> title.contains(keyword.toLowerCase()))) {
                        topics.add(topic);
                    }
                }
            }
            
            List<String> topicList = new ArrayList<>(topics);

            for (int i = 0; i < topicList.size(); i++) {
                for (int j = i + 1; j < topicList.size(); j++) {
                    String topic1 = topicList.get(i);
                    String topic2 = topicList.get(j);
                    
                    String key = topic1.compareTo(topic2) < 0 ? topic1 + "," + topic2 : topic2 + "," + topic1;

                    coOccurrenceMap.put(key, coOccurrenceMap.getOrDefault(key, 0) + 1);
                }
            }
        }

        List<CoOccurrencePair> topPairs = coOccurrenceMap.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(n)
                .map(entry -> {
                    String[] topics = entry.getKey().split(",");
                    return new CoOccurrencePair(topics[0], topics[1], entry.getValue());
                })
                .collect(Collectors.toList());

        logger.info("Found {} topic co-occurrence pairs", topPairs.size());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalPairs", coOccurrenceMap.size());
        result.put("topN", n);
        result.put("coOccurrences", topPairs);
        
        return result;
    }

    // DTO 类用于JSON序列化
    public static class CoOccurrencePair {
        private String topic1;
        private String topic2;
        private int count;

        public CoOccurrencePair(String topic1, String topic2, int count) {
            this.topic1 = topic1;
            this.topic2 = topic2;
            this.count = count;
        }

        public String getTopic1() { return topic1; }
        public void setTopic1(String topic1) { this.topic1 = topic1; }

        public String getTopic2() { return topic2; }
        public void setTopic2(String topic2) { this.topic2 = topic2; }

        public int getCount() { return count; }
        public void setCount(int count) { this.count = count; }
    }
}
