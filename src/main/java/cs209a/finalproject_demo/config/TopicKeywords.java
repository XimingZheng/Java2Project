package cs209a.finalproject_demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class TopicKeywords {

    private final Map<String, List<String>> topicKeywords = new LinkedHashMap<>();

    public TopicKeywords() {
        // Generics
        topicKeywords.put("generics", Arrays.asList(
                "generic", "type-parameter", "wildcard", "bounded-types",
                "type-erasure", "parameterized-type", "covariance", "contravariance"
        ));

        // Collections
        topicKeywords.put("collections", Arrays.asList(
                "collection", "list", "arraylist",
                "map", "hashmap", "concurrenthashmap",
                "set", "hashset", "queue", "iterator"
        ));

        // I/O
        topicKeywords.put("io", Arrays.asList(
                "java-io", "inputstream", "outputstream", "reader", "writer",
                "file-io", "nio", "serialization", "serializable", "path", "files"
        ));

        // Lambda
        topicKeywords.put("lambda", Arrays.asList(
                "lambda", "stream-api", "functional-interface", "functional-programming",
                "method-reference", "optional", "collectors", "java-8", "completable-future"
        ));

        // Multithreading & Concurrency
        topicKeywords.put("multithreading", Arrays.asList("thread", "multithreading", "concurrency",
                "synchronized", "executor", "parallel-processing",
                "race condition", "deadlock", "thread-safe"));

        // Socket
        topicKeywords.put("socket", Arrays.asList(
                "socket", "sockets", "serversocket", "networking",
                "network", "tcp", "udp", "http", "https",
                "websocket", "port",
                "client-server"
        ));

        // Reflection
        topicKeywords.put("reflection", Arrays.asList(
                "reflection", "reflect", "class.forname",
                "method.invoke", "method", "field",
                "constructor", "proxy", "annotation"
        ));

        // Spring Boot
        topicKeywords.put("spring-boot", Arrays.asList(
                "spring-boot", "springboot",
                "spring-mvc", "spring-data", "spring-jpa",
                "spring-security", "spring-rest", "restcontroller",
                "spring-web", "thymeleaf", "hibernate",
                "jpa", "bean", "autowired", "dependency-injection",
                "application.properties", "application.yml"
        ));

        // Maven
        topicKeywords.put("maven", Arrays.asList(
                "maven", "pom.xml", "mvn", "build-tool",
                "dependency-management", "plugins", "repository", "artifact"
        ));

        // JUnit & Testing
        topicKeywords.put("testing", Arrays.asList(
                "junit", "unit-testing", "integration-testing",
                "mockito", "mock", "test-driven-development",
                "assertion", "test-coverage"
        ));

        // Exception Handling
        topicKeywords.put("exceptions", Arrays.asList(
                "exception", "try-catch", "throw", "throws",
                "stack-trace", "runtimeexception", "checked-exception",
                "error-handling", "custom-exception"
        ));

        // JDBC & Database
        topicKeywords.put("database", Arrays.asList(
                "jdbc", "sql", "database", "mysql", "postgresql", "oracle",
                "connection", "preparedstatement", "resultset",
                "transaction", "datasource"
        ));
    }

    public Map<String, List<String>> getTopicKeywords() {
        return new LinkedHashMap<>(topicKeywords);
    }

    public List<String> getAllTopics() {
        return new ArrayList<>(topicKeywords.keySet());
    }

    public List<String> getKeywordsForTopic(String topic) {
        return topicKeywords.getOrDefault(topic, Collections.emptyList());
    }

    /**
     * 将标签映射到对应的主题
     * @param tag Stack Overflow 标签
     * @return 对应的主题名称，如果不属于任何主题则返回 null
     */
    public String mapTagToTopic(String tag) {
        if (tag == null || tag.isEmpty()) {
            return null;
        }

        String lowerTag = tag.toLowerCase();
        for (Map.Entry<String, List<String>> entry : topicKeywords.entrySet()) {
            if (entry.getValue().stream()
                    .anyMatch(keyword -> lowerTag.contains(keyword.toLowerCase()))) {
                return entry.getKey();
            }
        }
        return null;
    }
}
