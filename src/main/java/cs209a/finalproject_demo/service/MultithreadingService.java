package cs209a.finalproject_demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import cs209a.finalproject_demo.config.PatternMatchingConfig;
import cs209a.finalproject_demo.config.TopicKeywords;
import cs209a.finalproject_demo.model.Answer;
import cs209a.finalproject_demo.model.Question;
import cs209a.finalproject_demo.model.StackOverflowThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class MultithreadingService {
    private static final Logger logger = LoggerFactory.getLogger(MultithreadingService.class);
    private final PatternMatchingConfig patternMatchingConfig;
    private final DataLoaderService dataLoaderService;
    private final TopicKeywords topicKeywords;
    private final List<String> keywords;

    public MultithreadingService(DataLoaderService dataLoaderService,
                                 TopicKeywords topicKeywords, PatternMatchingConfig patternMatchingConfig) {
        this.dataLoaderService = dataLoaderService;
        this.topicKeywords = topicKeywords;
        this.keywords = topicKeywords.getKeywordsForTopic("multithreading");
        this.patternMatchingConfig = patternMatchingConfig;
    }

    public Map<String, Object> getRecurrenceProblems (int n) {
        logger.info("Analyzing top {} recurring problems in multithreading", n);
        List<StackOverflowThread> allThreads = dataLoaderService.getAllThreads();

        List<StackOverflowThread> filteredThreads = allThreads.parallelStream()
                .filter(t -> {
                    if (t.getQuestion() != null && questionContainsKeywords(t.getQuestion())) return true;
                    if (t.getAnswers() != null && answersContainsKeywords(t.getAnswers())) return true;
                    return false;
                })
                .collect(Collectors.toList());

        logger.info("Filtered {} threads with multithreading keywords", filteredThreads.size());

        // 2. 获取所有的 PitfallPattern
        List<PatternMatchingConfig.PitfallPattern> pitfallPatterns = patternMatchingConfig.concurrencyPatterns();
        
        // 3. 统计每种 pattern 出现的次数
        Map<String, Integer> patternCounts = new HashMap<>();
        Map<String, String> patternCategories = new HashMap<>();
        
        // 初始化计数器
        for (PatternMatchingConfig.PitfallPattern pattern : pitfallPatterns) {
            patternCounts.put(pattern.normalizedName, 0);
            patternCategories.put(pattern.normalizedName, pattern.category);
        }
        
        // 4. 遍历每个筛选出的线程,检查是否匹配任何 pattern
        for (StackOverflowThread thread : filteredThreads) {
            Set<String> matchedPatterns = new HashSet<>();
            
            // 提取线程中的所有文本内容
            List<String> texts = extractAllTexts(thread);
            
            // 对每个文本内容检查是否匹配 pattern
            for (String text : texts) {
                if (text == null || text.isEmpty()) continue;
                
                String lowerText = text.toLowerCase();
                
                for (PatternMatchingConfig.PitfallPattern pattern : pitfallPatterns) {
                    // 检查是否匹配该 pattern 的任何正则表达式
                    for (Pattern regex : pattern.regexes) {
                        if (regex.matcher(lowerText).find()) {
                            matchedPatterns.add(pattern.normalizedName);
                        }
                    }
                }
            }
            
            for (String patternName : matchedPatterns) {
                patternCounts.put(patternName, patternCounts.get(patternName) + 1);
            }
        }
        
        List<Map<String, Object>> topProblems = patternCounts.entrySet().stream()
                .filter(entry -> entry.getValue() > 0) // 只保留出现过的
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue())) // 降序排序
                .limit(n)
                .map(entry -> {
                    Map<String, Object> problem = new HashMap<>();
                    problem.put("patternName", entry.getKey());
                    problem.put("category", patternCategories.get(entry.getKey()));
                    problem.put("count", entry.getValue());

                    return problem;
                })
                .collect(Collectors.toList());
        
        Map<String, Object> result = new HashMap<>();
        result.put("totalThreads", filteredThreads.size());
        result.put("topProblems", topProblems);
        
        logger.info("Found {} recurring problems in {} threads", topProblems.size(), filteredThreads.size());
        
        return result;
    }

    private List<String> extractAllTexts(StackOverflowThread thread) {
        List<String> texts = new ArrayList<>();
        
        // Question 的 title, tag 和 body
        if (thread.getQuestion() != null) {
            Question question = thread.getQuestion();
            if (question.getTags() != null) {
                texts.addAll(question.getTags());
            }
            if (question.getTitle() != null) {
                texts.add(question.getTitle());
            }
            if (question.getBody() != null) {
                texts.add(question.getBody());
            }
        }
        
        // Answers 的 body
        if (thread.getAnswers() != null) {
            thread.getAnswers().forEach(answer -> {
                if (answer.getBody() != null) {
                    texts.add(answer.getBody());
                }
            });
        }
        
        return texts;
    }

    private void saveThreadsToJson(List<StackOverflowThread> filteredThreads) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File("data/filtered_threads.json"), filteredThreads);
            logger.info("Filtered threads have been saved to data/filtered_threads.json");
        } catch (IOException e) {
            logger.error("Error saving filtered threads to JSON file", e);
        }
    }

    private boolean answersContainsKeywords(List<Answer> answers) {
        for (Answer answer : answers) {
            return keywords.stream().anyMatch(keyword -> {
                if (answer.getBody() != null && answer.getBody().toLowerCase().contains(keyword)) {
                    logger.info(keyword);
                    return true;
                }
                return false;
            });
        }
        return false;
    }

    private boolean questionContainsKeywords(Question question) {
        return keywords.stream().anyMatch(keyword -> {
            if (question.getTags() != null && question.getTags().stream().anyMatch(tag -> tag.toLowerCase().contains(keyword.toLowerCase()))) {
                logger.info(keyword);
                return true;
            }
            if (question.getTitle() != null && question.getTitle().toLowerCase().contains(keyword.toLowerCase())) {
                logger.info(keyword);
                return true;
            }
            if (question.getBody() != null && question.getBody().toLowerCase().contains(keyword.toLowerCase())) {
                logger.info(keyword);
                return true;
            }
            return false;
        });
    }

}
