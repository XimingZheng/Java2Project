package cs209a.finalproject_demo.service;

import cs209a.finalproject_demo.model.Question;
import cs209a.finalproject_demo.model.StackOverflowThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
@Service
public class TopicAnalysisService {
    private static final Logger logger = LoggerFactory.getLogger(TopicAnalysisService.class);

    private final DataLoaderService dataLoaderService;

    private static final Map<String, List<String>> TOPIC_KEYWORDS = new HashMap<>();

    static {
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

    public Map<String, Object> getTopicTrends(List<String> topics, String startDate, String endDate) {
        logger.info("分析主题趋势: topics={}, startDate={}, endDate={}", topics, startDate, endDate);

        List<StackOverflowThread> allThreads = dataLoaderService.getAllThreads();

        // 过滤时间范围内的帖子
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        List<StackOverflowThread> filteredThreads = allThreads.stream()
                .filter(thread -> {
                    if (thread.getQuestion() == null || thread.getQuestion().getCreationDate() == null) {
                        return false;
                    }
                    LocalDate creationDate = Instant.ofEpochSecond(thread.getQuestion().getCreationDate())
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                    return !creationDate.isBefore(start) && !creationDate.isAfter(end);
                })
                .collect(Collectors.toList());

        logger.info("时间范围内的帖子数量: {}", filteredThreads.size());

        // 为每个主题统计数据
        Map<String, List<TimeSeriesData>> topicTrendsMap = new LinkedHashMap<>();

        for (String topic : topics) {
            List<TimeSeriesData> trendData = calculateTopicTrend(filteredThreads, topic, start, end);
            topicTrendsMap.put(topic, trendData);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("topicTrends", topicTrendsMap);
        result.put("totalThreads", filteredThreads.size());
        result.put("dateRange", Map.of("start", startDate, "end", endDate));

        return result;
    }

    /**
     * 计算单个主题的时间序列数据
     */
    private List<TimeSeriesData> calculateTopicTrend(List<StackOverflowThread> threads,
                                                     String topic,
                                                     LocalDate start,
                                                     LocalDate end) {
        List<String> keywords = TOPIC_KEYWORDS.getOrDefault(topic.toLowerCase(), Collections.singletonList(topic));

        // 按周分组统计
        Map<String, TimeSeriesData> weeklyData = new TreeMap<>();

        threads.stream()
                .filter(thread -> matchesTopic(thread, keywords))
                .forEach(thread -> {
                    Question question = thread.getQuestion();
                    LocalDate creationDate = Instant.ofEpochSecond(question.getCreationDate())
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();

                    // 获取该周的起始日期(周一)
                    LocalDate weekStart = creationDate.minusDays(creationDate.getDayOfWeek().getValue() - 1);
                    String weekKey = weekStart.toString();

                    TimeSeriesData data = weeklyData.computeIfAbsent(weekKey,
                            k -> new TimeSeriesData(weekStart, 0, 0, 0, 0.0));

                    data.questionCount++;
                    data.answerCount += (thread.getAnswers() != null ? thread.getAnswers().size() : 0);
                    data.viewCount += (question.getViewCount() != null ? question.getViewCount() : 0);
                    data.averageScore += (question.getScore() != null ? question.getScore() : 0);
                });

        // 计算平均分
        weeklyData.values().forEach(data -> {
            if (data.questionCount > 0) {
                data.averageScore = data.averageScore / data.questionCount;
            }
        });

        return new ArrayList<>(weeklyData.values());
    }

    /**
     * 判断帖子是否匹配主题关键词
     */
    private boolean matchesTopic(StackOverflowThread thread, List<String> keywords) {
        if (thread.getQuestion() == null || thread.getQuestion().getTags() == null) {
            return false;
        }

        List<String> tags = thread.getQuestion().getTags();

        return keywords.stream()
                .anyMatch(keyword -> tags.stream()
                        .anyMatch(tag -> tag.toLowerCase().contains(keyword.toLowerCase())));
    }

    /**
     * 获取所有可用的主题列表
     */
    public List<String> getAvailableTopics() {
        return new ArrayList<>(TOPIC_KEYWORDS.keySet());
    }

    /**
     * 获取主题活动度排名
     */
    public List<TopicActivity> getTopicActivityRanking(String startDate, String endDate) {
        List<StackOverflowThread> allThreads = dataLoaderService.getAllThreads();

        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        Map<String, TopicActivity> activityMap = new HashMap<>();

        for (Map.Entry<String, List<String>> entry : TOPIC_KEYWORDS.entrySet()) {
            String topic = entry.getKey();
            List<String> keywords = entry.getValue();

            TopicActivity activity = new TopicActivity(topic);

            allThreads.stream()
                    .filter(thread -> {
                        if (thread.getQuestion() == null || thread.getQuestion().getCreationDate() == null) {
                            return false;
                        }
                        LocalDate creationDate = Instant.ofEpochSecond(thread.getQuestion().getCreationDate())
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate();
                        return !creationDate.isBefore(start) && !creationDate.isAfter(end);
                    })
                    .filter(thread -> matchesTopic(thread, keywords))
                    .forEach(thread -> {
                        activity.questionCount++;
                        activity.answerCount += (thread.getAnswers() != null ? thread.getAnswers().size() : 0);
                        activity.totalViews += (thread.getQuestion().getViewCount() != null ?
                                thread.getQuestion().getViewCount() : 0);
                        activity.totalScore += (thread.getQuestion().getScore() != null ?
                                thread.getQuestion().getScore() : 0);
                    });

            if (activity.questionCount > 0) {
                activity.calculateMetrics();
                activityMap.put(topic, activity);
            }
        }

        return activityMap.values().stream()
                .sorted(Comparator.comparingInt(TopicActivity::getQuestionCount).reversed())
                .collect(Collectors.toList());
    }

    // DTO类
    public static class TimeSeriesData {
        private LocalDate date;
        private int questionCount;
        private int answerCount;
        private int viewCount;
        private double averageScore;

        public TimeSeriesData(LocalDate date, int questionCount, int answerCount,
                              int viewCount, double averageScore) {
            this.date = date;
            this.questionCount = questionCount;
            this.answerCount = answerCount;
            this.viewCount = viewCount;
            this.averageScore = averageScore;
        }

        // Getters
        public String getDate() { return date.toString(); }
        public int getQuestionCount() { return questionCount; }
        public int getAnswerCount() { return answerCount; }
        public int getViewCount() { return viewCount; }
        public double getAverageScore() { return Math.round(averageScore * 100.0) / 100.0; }
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
