package cs209a.finalproject_demo.service;

import cs209a.finalproject_demo.model.Answer;
import cs209a.finalproject_demo.model.StackOverflowThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class SolvableAnalysisService {
    private static final Logger logger = LoggerFactory.getLogger(SolvableAnalysisService.class);
    private final DataLoaderService dataLoaderService;

    public SolvableAnalysisService(DataLoaderService dataLoaderService) {
        this.dataLoaderService = dataLoaderService;
    }

    public Map<String, Object> getAnalysis() {
        logger.info("Analyzing Solvable vs. Hard-to-Solve Questions");
        List<StackOverflowThread> allThreads = dataLoaderService.getAllThreads();

        List<StackOverflowThread> solvable = allThreads.parallelStream()
                .filter(StackOverflowThread::isSolvable)
                .collect(Collectors.toList());

        logger.info("filtered {} solvable threads", solvable.size());

        List<StackOverflowThread> notSolvable = allThreads.parallelStream()
                .filter(StackOverflowThread::notSolvable)
                .collect(Collectors.toList());

        logger.info("filtered {} not-solvable threads", notSolvable.size());

        Map<String, Object> result = new HashMap<>();
        
        result.put("reputationAnalysis", getReputationAnalysis(solvable, notSolvable));
        
        // 2. 问题长度和清晰度分析
        result.put("questionLengthAnalysis", getQuestionLengthAnalysis(solvable, notSolvable));
        
        // 3. 代码片段分析
        result.put("codeSnippetAnalysis", getCodeSnippetAnalysis(solvable, notSolvable));
        
        // 4. 标签数量分析
        result.put("tagCountAnalysis", getTagCountAnalysis(solvable, notSolvable));
        
        // 5. 响应时间分析
        result.put("responseTimeAnalysis", getResponseTimeAnalysis(solvable, notSolvable));
        
        // 6. 问题得分分析
        result.put("questionScoreAnalysis", getQuestionScoreAnalysis(solvable, notSolvable));
        
        // 7. 浏览量分析
        result.put("viewCountAnalysis", getViewCountAnalysis(solvable, notSolvable));
        
        // 8. 标题长度分析
        result.put("titleLengthAnalysis", getTitleLengthAnalysis(solvable, notSolvable));
        
        // 9. 基本统计信息
        result.put("basicStats", getBasicStats(solvable, notSolvable));

        return result;
    }

    private Map<String, Object> getReputationAnalysis(List<StackOverflowThread> solvable,
                                                      List<StackOverflowThread> notSolvable) {
        double solvableAvgReputation = solvable.parallelStream()
                .filter(t -> t.getQuestion() != null
                        && t.getQuestion().getOwner() != null
                        && t.getQuestion().getOwner().getReputation() != null
                )
                .mapToInt(t -> t.getQuestion().getOwner().getReputation())
                .average()
                .orElse(0.0);

        double notSolvableAvgReputation = notSolvable.parallelStream()
                .filter(t -> t.getQuestion() != null
                        && t.getQuestion().getOwner() != null
                        && t.getQuestion().getOwner().getReputation() != null
                )
                .mapToInt(t -> t.getQuestion().getOwner().getReputation())
                .average()
                .orElse(0.0);

        Map<String, Object> map = new HashMap<>();
        map.put("solvableAvg", Math.round(solvableAvgReputation * 100.0) / 100.0);
        map.put("notSolvableAvg", Math.round(notSolvableAvgReputation * 100.0) / 100.0);
        map.put("difference", Math.round((solvableAvgReputation - notSolvableAvgReputation) * 100.0) / 100.0);

        return map;
    }

    private Map<String, Object> getQuestionLengthAnalysis(List<StackOverflowThread> solvable,
                                                          List<StackOverflowThread> notSolvable) {
        double solvableAvgLength = solvable.parallelStream()
                .filter(t -> t.getQuestion() != null && t.getQuestion().getBody() != null)
                .mapToInt(t -> t.getQuestion().getBody().length())
                .average()
                .orElse(0.0);

        double solvableAvgWords = solvable.parallelStream()
                .filter(t -> t.getQuestion() != null && t.getQuestion().getBody() != null)
                .mapToInt(t -> countWords(t.getQuestion().getBody()))
                .average()
                .orElse(0.0);

        // 不可解决问题的平均长度
        double notSolvableAvgLength = notSolvable.parallelStream()
                .filter(t -> t.getQuestion() != null && t.getQuestion().getBody() != null)
                .mapToInt(t -> t.getQuestion().getBody().length())
                .average()
                .orElse(0.0);

        double notSolvableAvgWords = notSolvable.parallelStream()
                .filter(t -> t.getQuestion() != null && t.getQuestion().getBody() != null)
                .mapToInt(t -> countWords(t.getQuestion().getBody()))
                .average()
                .orElse(0.0);

        Map<String, Object> map = new HashMap<>();
        map.put("solvableAvgCharacters", Math.round(solvableAvgLength * 100.0) / 100.0);
        map.put("solvableAvgWords", Math.round(solvableAvgWords * 100.0) / 100.0);
        map.put("notSolvableAvgCharacters", Math.round(notSolvableAvgLength * 100.0) / 100.0);
        map.put("notSolvableAvgWords", Math.round(notSolvableAvgWords * 100.0) / 100.0);
        map.put("characterDifference", Math.round((solvableAvgLength - notSolvableAvgLength) * 100.0) / 100.0);
        map.put("wordDifference", Math.round((solvableAvgWords - notSolvableAvgWords) * 100.0) / 100.0);
        return map;
    }

    /**
     * 分析代码片段的存在和数量
     */
    private Map<String, Object> getCodeSnippetAnalysis(List<StackOverflowThread> solvable,
                                                       List<StackOverflowThread> notSolvable) {
        // 检测代码块的正则表达式（<code>、<pre>、```等）
        Pattern codePattern = Pattern.compile("<code>|<pre>|```", Pattern.CASE_INSENSITIVE);

        // 可解决问题中包含代码的比例
        long solvableWithCode = solvable.parallelStream()
                .filter(t -> t.getQuestion() != null && t.getQuestion().getBody() != null)
                .filter(t -> {
                    Matcher m = codePattern.matcher(t.getQuestion().getBody());
                    return m.find();
                })
                .count();

        double solvableCodeRatio = solvable.isEmpty() ? 0.0 : 
                (double) solvableWithCode / solvable.size() * 100;

        double solvableAvgCodeBlocks = solvable.parallelStream()
                .filter(t -> t.getQuestion() != null && t.getQuestion().getBody() != null)
                .mapToInt(t -> countCodeBlocks(t.getQuestion().getBody()))
                .average()
                .orElse(0.0);

        // 不可解决问题中包含代码的比例
        long notSolvableWithCode = notSolvable.parallelStream()
                .filter(t -> t.getQuestion() != null && t.getQuestion().getBody() != null)
                .filter(t -> {
                    Matcher m = codePattern.matcher(t.getQuestion().getBody());
                    return m.find();
                })
                .count();

        double notSolvableCodeRatio = notSolvable.isEmpty() ? 0.0 : 
                (double) notSolvableWithCode / notSolvable.size() * 100;

        double notSolvableAvgCodeBlocks = notSolvable.parallelStream()
                .filter(t -> t.getQuestion() != null && t.getQuestion().getBody() != null)
                .mapToInt(t -> countCodeBlocks(t.getQuestion().getBody()))
                .average()
                .orElse(0.0);

        Map<String, Object> map = new HashMap<>();
        map.put("solvableWithCodePercentage", Math.round(solvableCodeRatio * 100.0) / 100.0);
        map.put("solvableAvgCodeBlocks", Math.round(solvableAvgCodeBlocks * 100.0) / 100.0);
        map.put("notSolvableWithCodePercentage", Math.round(notSolvableCodeRatio * 100.0) / 100.0);
        map.put("notSolvableAvgCodeBlocks", Math.round(notSolvableAvgCodeBlocks * 100.0) / 100.0);
        map.put("percentageDifference", Math.round((solvableCodeRatio - notSolvableCodeRatio) * 100.0) / 100.0);

        return map;
    }

    /**
     * 分析标签数量
     */
    private Map<String, Object> getTagCountAnalysis(List<StackOverflowThread> solvable,
                                                    List<StackOverflowThread> notSolvable) {
        double solvableAvgTags = solvable.parallelStream()
                .filter(t -> t.getQuestion() != null && t.getQuestion().getTags() != null)
                .mapToInt(t -> t.getQuestion().getTags().size())
                .average()
                .orElse(0.0);

        double notSolvableAvgTags = notSolvable.parallelStream()
                .filter(t -> t.getQuestion() != null && t.getQuestion().getTags() != null)
                .mapToInt(t -> t.getQuestion().getTags().size())
                .average()
                .orElse(0.0);

        Map<String, Object> map = new HashMap<>();
        map.put("solvableAvgTags", Math.round(solvableAvgTags * 100.0) / 100.0);
        map.put("notSolvableAvgTags", Math.round(notSolvableAvgTags * 100.0) / 100.0);
        map.put("difference", Math.round((solvableAvgTags - notSolvableAvgTags) * 100.0) / 100.0);

        return map;
    }

    /**
     * 分析响应时间（从问题创建到第一个回答的时间）
     */
    private Map<String, Object> getResponseTimeAnalysis(List<StackOverflowThread> solvable,
                                                        List<StackOverflowThread> notSolvable) {
        // 可解决问题的平均响应时间（秒转小时）
        double solvableAvgResponseTime = solvable.parallelStream()
                .filter(t -> t.getQuestion() != null && t.getAnswers() != null && !t.getAnswers().isEmpty())
                .filter(t -> t.getQuestion().getCreationDate() != null)
                .mapToLong(t -> {
                    long questionTime = t.getQuestion().getCreationDate();
                    long firstAnswerTime = t.getAnswers().stream()
                            .filter(a -> a.getCreationDate() != null)
                            .mapToLong(Answer::getCreationDate)
                            .min()
                            .orElse(questionTime);
                    return firstAnswerTime - questionTime;
                })
                .average()
                .orElse(0.0) / 3600.0; // 转换为小时

        // 不可解决问题有回答的平均响应时间
        double notSolvableAvgResponseTime = notSolvable.parallelStream()
                .filter(t -> t.getQuestion() != null && t.getAnswers() != null && !t.getAnswers().isEmpty())
                .filter(t -> t.getQuestion().getCreationDate() != null)
                .mapToLong(t -> {
                    long questionTime = t.getQuestion().getCreationDate();
                    long firstAnswerTime = t.getAnswers().stream()
                            .filter(a -> a.getCreationDate() != null)
                            .mapToLong(Answer::getCreationDate)
                            .min()
                            .orElse(questionTime);
                    return firstAnswerTime - questionTime;
                })
                .average()
                .orElse(0.0) / 3600.0;

        // 计算无回答的问题比例
        long notSolvableWithoutAnswers = notSolvable.stream()
                .filter(t -> t.getAnswers() == null || t.getAnswers().isEmpty())
                .count();
        
        double noAnswerPercentage = notSolvable.isEmpty() ? 0.0 : 
                (double) notSolvableWithoutAnswers / notSolvable.size() * 100;

        Map<String, Object> map = new HashMap<>();
        map.put("solvableAvgResponseHours", Math.round(solvableAvgResponseTime * 100.0) / 100.0);
        map.put("notSolvableAvgResponseHours", Math.round(notSolvableAvgResponseTime * 100.0) / 100.0);
        map.put("notSolvableNoAnswerPercentage", Math.round(noAnswerPercentage * 100.0) / 100.0);
        map.put("difference", Math.round((solvableAvgResponseTime - notSolvableAvgResponseTime) * 100.0) / 100.0);

        return map;
    }

    /**
     * 分析问题得分
     */
    private Map<String, Object> getQuestionScoreAnalysis(List<StackOverflowThread> solvable,
                                                         List<StackOverflowThread> notSolvable) {
        double solvableAvgScore = solvable.parallelStream()
                .filter(t -> t.getQuestion() != null && t.getQuestion().getScore() != null)
                .mapToInt(t -> t.getQuestion().getScore())
                .average()
                .orElse(0.0);

        double notSolvableAvgScore = notSolvable.parallelStream()
                .filter(t -> t.getQuestion() != null && t.getQuestion().getScore() != null)
                .mapToInt(t -> t.getQuestion().getScore())
                .average()
                .orElse(0.0);

        Map<String, Object> map = new HashMap<>();
        map.put("solvableAvgScore", Math.round(solvableAvgScore * 100.0) / 100.0);
        map.put("notSolvableAvgScore", Math.round(notSolvableAvgScore * 100.0) / 100.0);
        map.put("difference", Math.round((solvableAvgScore - notSolvableAvgScore) * 100.0) / 100.0);

        return map;
    }

    /**
     * 分析浏览量
     */
    private Map<String, Object> getViewCountAnalysis(List<StackOverflowThread> solvable,
                                                     List<StackOverflowThread> notSolvable) {
        double solvableAvgViews = solvable.parallelStream()
                .filter(t -> t.getQuestion() != null && t.getQuestion().getViewCount() != null)
                .mapToInt(t -> t.getQuestion().getViewCount())
                .average()
                .orElse(0.0);

        double notSolvableAvgViews = notSolvable.parallelStream()
                .filter(t -> t.getQuestion() != null && t.getQuestion().getViewCount() != null)
                .mapToInt(t -> t.getQuestion().getViewCount())
                .average()
                .orElse(0.0);

        Map<String, Object> map = new HashMap<>();
        map.put("solvableAvgViews", Math.round(solvableAvgViews * 100.0) / 100.0);
        map.put("notSolvableAvgViews", Math.round(notSolvableAvgViews * 100.0) / 100.0);
        map.put("difference", Math.round((solvableAvgViews - notSolvableAvgViews) * 100.0) / 100.0);

        return map;
    }

    /**
     * 分析标题长度
     */
    private Map<String, Object> getTitleLengthAnalysis(List<StackOverflowThread> solvable,
                                                       List<StackOverflowThread> notSolvable) {
        double solvableAvgTitleLength = solvable.parallelStream()
                .filter(t -> t.getQuestion() != null && t.getQuestion().getTitle() != null)
                .mapToInt(t -> t.getQuestion().getTitle().length())
                .average()
                .orElse(0.0);

        double notSolvableAvgTitleLength = notSolvable.parallelStream()
                .filter(t -> t.getQuestion() != null && t.getQuestion().getTitle() != null)
                .mapToInt(t -> t.getQuestion().getTitle().length())
                .average()
                .orElse(0.0);

        double solvableAvgTitleWords = solvable.parallelStream()
                .filter(t -> t.getQuestion() != null && t.getQuestion().getTitle() != null)
                .mapToInt(t -> countWords(t.getQuestion().getTitle()))
                .average()
                .orElse(0.0);

        double notSolvableAvgTitleWords = notSolvable.parallelStream()
                .filter(t -> t.getQuestion() != null && t.getQuestion().getTitle() != null)
                .mapToInt(t -> countWords(t.getQuestion().getTitle()))
                .average()
                .orElse(0.0);

        Map<String, Object> map = new HashMap<>();
        map.put("solvableAvgCharacters", Math.round(solvableAvgTitleLength * 100.0) / 100.0);
        map.put("solvableAvgWords", Math.round(solvableAvgTitleWords * 100.0) / 100.0);
        map.put("notSolvableAvgCharacters", Math.round(notSolvableAvgTitleLength * 100.0) / 100.0);
        map.put("notSolvableAvgWords", Math.round(notSolvableAvgTitleWords * 100.0) / 100.0);
        map.put("characterDifference", Math.round((solvableAvgTitleLength - notSolvableAvgTitleLength) * 100.0) / 100.0);

        return map;
    }

    /**
     * 基本统计信息
     */
    private Map<String, Object> getBasicStats(List<StackOverflowThread> solvable,
                                              List<StackOverflowThread> notSolvable) {
        Map<String, Object> map = new HashMap<>();
        map.put("totalSolvable", solvable.size());
        map.put("totalNotSolvable", notSolvable.size());
        map.put("totalQuestions", solvable.size() + notSolvable.size());
        map.put("solvablePercentage", Math.round((double) solvable.size() / 
                (solvable.size() + notSolvable.size()) * 10000.0) / 100.0);
        map.put("notSolvablePercentage", Math.round((double) notSolvable.size() / 
                (solvable.size() + notSolvable.size()) * 10000.0) / 100.0);

        return map;
    }

    /**
     * 统计单词数（简单按空格分割）
     */
    private int countWords(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }
        // 移除HTML标签后统计
        String cleanText = text.replaceAll("<[^>]+>", " ");
        return cleanText.trim().split("\\s+").length;
    }

    /**
     * 统计代码块数量
     */
    private int countCodeBlocks(String text) {
        if (text == null) {
            return 0;
        }
        int count = 0;
        // 统计<code>标签
        count += text.split("<code>", -1).length - 1;
        // 统计<pre>标签
        count += text.split("<pre>", -1).length - 1;
        // 统计```标记（Markdown代码块）
        count += text.split("```", -1).length - 1;
        return count;
    }

}
