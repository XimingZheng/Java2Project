package cs209a.finalproject_demo.service;

import cs209a.finalproject_demo.model.Question;
import cs209a.finalproject_demo.model.StackOverflowThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.IsoFields;
import java.util.*;
import java.util.stream.Collectors;
@Service
public class TopicAnalysisService {
    private static final Logger logger = LoggerFactory.getLogger(TopicAnalysisService.class);

    private final DataLoaderService dataLoaderService;

    private static final Map<String, List<String>> TOPIC_KEYWORDS = new HashMap<>();

    static {
        TOPIC_KEYWORDS.put("java", Arrays.asList("java"));
        TOPIC_KEYWORDS.put("generics", Arrays.asList("generic", "generics", "type-parameter"));
        TOPIC_KEYWORDS.put("collections", Arrays.asList("collection", "list", "map", "set", "arraylist", "hashmap"));
        TOPIC_KEYWORDS.put("io", Arrays.asList("io", "inputstream", "outputstream", "file", "nio"));
        TOPIC_KEYWORDS.put("lambda", Arrays.asList("lambda", "stream", "functional-interface", "method-reference"));
        TOPIC_KEYWORDS.put("multithreading", Arrays.asList("thread", "multithreading", "concurrency", "synchronized", "executor"));
        TOPIC_KEYWORDS.put("socket", Arrays.asList("socket", "serversocket", "network", "tcp", "udp"));
        TOPIC_KEYWORDS.put("reflection", Arrays.asList("reflection", "class.forname", "method.invoke"));
        TOPIC_KEYWORDS.put("spring-boot", Arrays.asList("spring-boot", "spring", "springboot"));
    }

    public TopicAnalysisService(DataLoaderService dataLoaderService) {
        this.dataLoaderService = dataLoaderService;
    }

    public Map<String, Object> getTopicTrends(List<String> topics, String startDate, String endDate, String period) {
        logger.info("Analyzing Topic Trends: topics={}, startDate={}, endDate={}, period={}"
                , topics, startDate, endDate, period);

        List<StackOverflowThread> allThreads = dataLoaderService.getAllThreads();

        List<String> keywords =
                topics.stream()
                        .flatMap(t -> TOPIC_KEYWORDS.getOrDefault(t, List.of()).stream())
                        .distinct()
                        .toList();

        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        List<StackOverflowThread> filteredThreads = allThreads.stream()
                .filter(thread -> {
                    if (thread.getQuestion() == null || thread.getQuestion().getCreationDate() == null) {
                        return false;
                    }
                    // filter keywords (topics)
                    if (keywords.stream().noneMatch(keyword -> thread.getQuestion().getTags().contains(keyword)))
                        return false;

                    LocalDate creationDate = Instant.ofEpochSecond(thread.getQuestion().getCreationDate())
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                    if (!creationDate.isBefore(start) && !creationDate.isAfter(end)) {
                        logger.info(String.valueOf(creationDate));
                        return true;
                    }
                    return false;
                })
                .collect(Collectors.toList());

        logger.info("Threads filtered: {}", filteredThreads.size());


        Map<String, List<Map<String, Object>>> topicTrends = new LinkedHashMap<>();

        for (String topic : topics) {
            // 当前 topic 对应的 keywords
            List<String> topicKeywords = TOPIC_KEYWORDS.getOrDefault(topic, List.of());

            // 3.1 先找到属于这个 topic 的 threads（在 filteredThreads 里再细分一次）
            List<StackOverflowThread> topicThreads = filteredThreads.stream()
                    .filter(thread -> {
                        List<String> tags = thread.getQuestion().getTags();
                        return topicKeywords.stream().anyMatch(tags::contains);
                    })
                    .toList();

            // 3.2 按 period 分桶并计数：bucketKey -> count
            Map<String, Long> bucketCount = topicThreads.stream()
                    .collect(Collectors.groupingBy(
                            thread -> {
                                LocalDate date = Instant.ofEpochSecond(thread.getQuestion().getCreationDate())
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate();
                                return formatTimePeriod(date, period);
                            },
                            // 用 TreeMap 保证时间顺序
                            TreeMap::new,
                            Collectors.counting()
                    ));

            // 3.3 把 Map<String, Long> 转成 List<{"period":..., "count":...}>
            List<Map<String, Object>> series = bucketCount.entrySet().stream()
                    .map(e -> {
                        Map<String, Object> point = new LinkedHashMap<>();
                        point.put("period", e.getKey());
                        point.put("count", e.getValue());
                        return point;
                    })
                    .toList();

            topicTrends.put(topic, series);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("period", period.toLowerCase());
        result.put("dateRange", Map.of("start", startDate, "end", endDate));
        result.put("totalThreads", filteredThreads.size());
        result.put("topicTrends", topicTrends);

        return result;
    }

    private String formatTimePeriod(LocalDate date, String period) {
        if (period == null || period.isBlank()) {
            period = "month";
        }
        switch (period.toLowerCase()) {
            case "day":
                // 2025-12-02
                return date.toString();
            case "year":
                // 2025
                return String.valueOf(date.getYear());
            case "week":
                // ISO 周 = YYYY-Www，例如 2025-W14
                int weekYear = date.get(IsoFields.WEEK_BASED_YEAR);
                int week = date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
                return String.format("%d-W%02d", weekYear, week);
            default:
                // 2025-12
                return date.getYear() + "-" + String.format("%02d", date.getMonthValue());
        }
    }

    public List<String> getAvailableTopics() {
        return new ArrayList<>(TOPIC_KEYWORDS.keySet());
    }

    public List<TopicActivity> getTopicActivityRanking(String startDate, String endDate) {
        List<StackOverflowThread> allThreads = dataLoaderService.getAllThreads();

        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        return null;
    }
    public static class TopicActivity {
        private String topic;
        private int questionCount;
        private int answerCount;
        private int totalViews;
        private int totalScore;
        private double averageAnswersPerQuestion;
        private double averageViewsPerQuestion;
        private double averageScore;

        public TopicActivity(String topic) {
            this.topic = topic;
        }

        public void calculateMetrics() {
            if (questionCount > 0) {
                this.averageAnswersPerQuestion = (double) answerCount / questionCount;
                this.averageViewsPerQuestion = (double) totalViews / questionCount;
                this.averageScore = (double) totalScore / questionCount;
            }
        }

        // Getters
        public String getTopic() { return topic; }
        public int getQuestionCount() { return questionCount; }
        public int getAnswerCount() { return answerCount; }
        public int getTotalViews() { return totalViews; }
        public double getAverageAnswersPerQuestion() {
            return Math.round(averageAnswersPerQuestion * 100.0) / 100.0;
        }
        public double getAverageViewsPerQuestion() {
            return Math.round(averageViewsPerQuestion * 100.0) / 100.0;
        }
        public double getAverageScore() {
            return Math.round(averageScore * 100.0) / 100.0;
        }
    }

}
