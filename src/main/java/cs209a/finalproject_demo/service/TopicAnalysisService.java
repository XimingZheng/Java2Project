package cs209a.finalproject_demo.service;

import cs209a.finalproject_demo.config.TopicKeywords;
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

import static cs209a.finalproject_demo.config.TopicKeywords.TOPIC_KEYWORDS;

@Service
public class TopicAnalysisService {
    private static final Logger logger = LoggerFactory.getLogger(TopicAnalysisService.class);

    private final DataLoaderService dataLoaderService;

    public TopicAnalysisService(DataLoaderService dataLoaderService) {
        this.dataLoaderService = dataLoaderService;
    }

    public Map<String, Object> getTopicTrends(List<String> topics, String startDate, String endDate, String period) {
        logger.info("Analyzing Topic Trends: topics={}, startDate={}, endDate={}, period={}"
                , topics, startDate, endDate, period);

        List<StackOverflowThread> allThreads = dataLoaderService.getAllThreads();

        List<StackOverflowThread> filteredThreads = filterTopicAndDate(
                allThreads, topics, startDate, endDate
        );

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

    private List<StackOverflowThread> filterTopicAndDate(
            List<StackOverflowThread> allThreads,
            List<String> topics, String startDate, String endDate){
        List<String> keywords =
                topics.stream()
                        .flatMap(t -> TopicKeywords.getKeywordsForTopic(t).stream())
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
        return filteredThreads;
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
        return TopicKeywords.getAllTopics();
    }

    public Map<String, Object> getTopicActivityScore(
            List<String> topics, String startDate, String endDate, String period) {

        List<StackOverflowThread> allThreads = dataLoaderService.getAllThreads();

        List<StackOverflowThread> filteredThreads = filterTopicAndDate(
                allThreads, topics, startDate, endDate
        );
        
        Map<String, List<Map<String, Object>>> topicActivityScore = new LinkedHashMap<>();

        for (String topic : topics) {
            // 当前 topic 对应的 keywords
            List<String> topicKeywords = TopicKeywords.getKeywordsForTopic(topic);

            // 找到属于这个 topic 的 threads
            List<StackOverflowThread> topicThreads = filteredThreads.stream()
                    .filter(thread -> {
                        List<String> tags = thread.getQuestion().getTags();
                        return topicKeywords.stream().anyMatch(tags::contains);
                    })
                    .toList();

            // 按 period 分桶计算活跃度分数：bucketKey -> activityScore
            Map<String, Double> bucketActivityScore = new TreeMap<>();

            for (StackOverflowThread thread : topicThreads) {
                Question question = thread.getQuestion();

                // 1. 处理 Question 的活跃度（权重 1.0）
                if (question != null && question.getCreationDate() != null) {
                    LocalDate qDate = Instant.ofEpochSecond(question.getCreationDate())
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                    String qPeriod = formatTimePeriod(qDate, period);
                    int qScore = question.getScore() != null ? question.getScore() : 0;
                    
                    bucketActivityScore.merge(qPeriod, 1.0 * ReLU(qScore), Double::sum);
                }

                // 2. 处理 Answers 的活跃度（权重 0.8）
                if (thread.getAnswers() != null) {
                    for (var answer : thread.getAnswers()) {
                        if (answer.getCreationDate() != null) {
                            LocalDate aDate = Instant.ofEpochSecond(answer.getCreationDate())
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate();
                            String aPeriod = formatTimePeriod(aDate, period);
                            int aScore = answer.getScore() != null ? answer.getScore() : 0;
                            
                            bucketActivityScore.merge(aPeriod, 0.8 * ReLU(aScore), Double::sum);
                        }
                    }
                }

                // 3. 处理 Question Comments 的活跃度（权重 0.5）
                if (thread.getQuestionComments() != null) {
                    for (var comment : thread.getQuestionComments()) {
                        if (comment.getCreationDate() != null) {
                            LocalDate cDate = Instant.ofEpochSecond(comment.getCreationDate())
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate();
                            String cPeriod = formatTimePeriod(cDate, period);
                            int cScore = comment.getScore() != null ? comment.getScore() : 0;
                            
                            bucketActivityScore.merge(cPeriod, 0.5 * ReLU(cScore), Double::sum);
                        }
                    }
                }

                // 4. 处理 Answer Comments 的活跃度（权重 0.5）
                if (thread.getAnswerComments() != null) {
                    for (var commentList : thread.getAnswerComments().values()) {
                        if (commentList != null) {
                            for (var comment : commentList) {
                                if (comment.getCreationDate() != null) {
                                    LocalDate cDate = Instant.ofEpochSecond(comment.getCreationDate())
                                            .atZone(ZoneId.systemDefault())
                                            .toLocalDate();
                                    String cPeriod = formatTimePeriod(cDate, period);
                                    int cScore = comment.getScore() != null ? comment.getScore() : 0;
                                    
                                    bucketActivityScore.merge(cPeriod, 0.5 * ReLU(cScore), Double::sum);
                                }
                            }
                        }
                    }
                }
            }

            // 把 Map<String, Double> 转成 List<{"period":..., "activityScore":...}>
            List<Map<String, Object>> series = bucketActivityScore.entrySet().stream()
                    .map(e -> {
                        Map<String, Object> point = new LinkedHashMap<>();
                        point.put("period", e.getKey());
                        point.put("activityScore", Math.round(e.getValue() * 100.0) / 100.0); // 保留两位小数
                        return point;
                    })
                    .toList();

            topicActivityScore.put(topic, series);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("period", period.toLowerCase());
        result.put("dateRange", Map.of("start", startDate, "end", endDate));
        result.put("totalThreads", filteredThreads.size());
        result.put("topicActivityScore", topicActivityScore);

        return result;
    }

    private int ReLU(int score) {
        return Math.max(0, score);
    }

}
