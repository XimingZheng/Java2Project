package cs209a.finalproject_demo.config;

import java.util.*;

public class TopicKeywords {

    public static final Map<String, List<String>> TOPIC_KEYWORDS = new LinkedHashMap<>();

    static {
        // Generics
        TOPIC_KEYWORDS.put("generics", Arrays.asList(
                "generic", "type-parameter", "wildcard", "bounded-types",
                "type-erasure", "parameterized-type", "covariance", "contravariance"
        ));

        // Collections
        TOPIC_KEYWORDS.put("collections", Arrays.asList(
                "collection", "list", "arraylist",
                "map", "hashmap", "concurrenthashmap",
                "set", "hashset", "queue", "iterator"
        ));

        // I/O
        TOPIC_KEYWORDS.put("io", Arrays.asList(
                "java-io", "inputstream", "outputstream", "reader", "writer",
                "file-io", "nio", "serialization", "serializable", "path", "files"
        ));

        // Lambda
        TOPIC_KEYWORDS.put("lambda", Arrays.asList(
                "lambda", "stream-api", "functional-interface", "functional-programming",
                "method-reference", "optional", "collectors", "java-8", "completable-future"
        ));

        // Multithreading & Concurrency
        TOPIC_KEYWORDS.put("multithreading", Arrays.asList("thread", "multithreading", "concurrency",
                "synchronized", "executor", "parallel-processing",
                "race condition", "deadlock", "thread-safe"));

        // Socket
        TOPIC_KEYWORDS.put("socket", Arrays.asList(
                "socket", "sockets", "serversocket", "networking",
                "network", "tcp", "udp", "http", "https",
                "websocket", "port",
                "client-server"
        ));

        // Reflection
        TOPIC_KEYWORDS.put("reflection", Arrays.asList(
                "reflection", "reflect", "class.forname",
                "method.invoke", "method", "field",
                "constructor", "proxy", "annotation"
        ));

        // Spring Boot
        TOPIC_KEYWORDS.put("spring-boot", Arrays.asList(
                "spring-boot", "springboot",
                "spring-mvc", "spring-data", "spring-jpa",
                "spring-security", "spring-rest", "restcontroller",
                "spring-web", "thymeleaf", "hibernate",
                "jpa", "bean", "autowired", "dependency-injection",
                "application.properties", "application.yml"
        ));

        // Maven
        TOPIC_KEYWORDS.put("maven", Arrays.asList(
                "maven", "pom.xml", "mvn", "build-tool",
                "dependency-management", "plugins", "repository", "artifact"
        ));

        // JUnit & Testing
        TOPIC_KEYWORDS.put("testing", Arrays.asList(
                "junit", "unit-testing", "integration-testing",
                "mockito", "mock", "test-driven-development",
                "assertion", "test-coverage"
        ));

        // Exception Handling
        TOPIC_KEYWORDS.put("exceptions", Arrays.asList(
                "exception", "try-catch", "throw", "throws",
                "stack-trace", "runtimeexception", "checked-exception",
                "error-handling", "custom-exception"
        ));

        // JDBC & Database
        TOPIC_KEYWORDS.put("database", Arrays.asList(
                "jdbc", "sql", "database", "mysql", "postgresql", "oracle",
                "connection", "preparedstatement", "resultset",
                "transaction", "datasource"
        ));
    }

    public static Map<String, List<String>> getTopicKeywords() {
        return new LinkedHashMap<>(TOPIC_KEYWORDS);
    }

    public static List<String> getAllTopics() {
        return new ArrayList<>(TOPIC_KEYWORDS.keySet());
    }

    public static List<String> getKeywordsForTopic(String topic) {
        return TOPIC_KEYWORDS.getOrDefault(topic, Collections.emptyList());
    }

    /**
     * 将标签映射到对应的主题
     * @param tag Stack Overflow 标签
     * @return 对应的主题名称，如果不属于任何主题则返回 null
     */
    public static String mapTagToTopic(String tag) {
        if (tag == null || tag.isEmpty()) {
            return null;
        }

        String lowerTag = tag.toLowerCase();
        for (Map.Entry<String, List<String>> entry : TOPIC_KEYWORDS.entrySet()) {
            if (entry.getValue().stream()
                    .anyMatch(keyword -> lowerTag.contains(keyword.toLowerCase()))) {
                return entry.getKey();
            }
        }
        return null;
    }
}
